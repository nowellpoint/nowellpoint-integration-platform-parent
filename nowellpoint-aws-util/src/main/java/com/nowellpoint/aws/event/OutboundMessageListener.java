package com.nowellpoint.aws.event;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.kms.AWSKMS;
import com.amazonaws.services.kms.AWSKMSClient;
import com.amazonaws.services.kms.model.EncryptRequest;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.amazonaws.util.Base64;
import com.amazonaws.util.IOUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nowellpoint.aws.model.Transaction;

public class OutboundMessageListener implements RequestStreamHandler {
	
	private static LambdaLogger logger;
	private static DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	private static DynamoDBMapper mapper = new DynamoDBMapper(new AmazonDynamoDBClient());
	
	private static String ACK_RESPONSE = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">"
			+ "<soapenv:Body>"
			+ "<notificationsResponse xmlns=\"http://soap.sforce.com/2005/09/outbound\">"
			+ "<Ack>#ack#</Ack>"
			+ "</notificationsResponse>"
			+ "</soapenv:Body>" 
			+ "</soapenv:Envelope>";

	@Override
	public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
		
		//
		//
		//
		
		logger = context.getLogger();
		
		/**
		 * 
		 */
		
		logger.log("received OutboundMessage");
		
		/**
		 * 
		 */
		
		JsonNode request = new ObjectMapper().readValue(IOUtils.toString(inputStream), JsonNode.class);
		
		String xml = request.get("body").asText().replace("\\", "");	
		
		String response;
		
		JsonNode outboundMessage = null;
		try {
			outboundMessage = convertToJson(xml);
			saveOutboundMessage(outboundMessage);
			response = getAckMessage(Boolean.TRUE);
		} catch (ParserConfigurationException | SAXException | AmazonClientException e) {
			e.printStackTrace();
			response = getAckMessage(Boolean.FALSE);
		}
		
		outputStream.write(response.getBytes());
	}
	
	private JsonNode convertToJson(String xml) throws ParserConfigurationException, SAXException, IOException {
		
		ObjectNode outboundMessage = JsonNodeFactory.instance.objectNode();
		
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
		document.getDocumentElement().normalize();
		
		NodeList nodes = document.getElementsByTagName("notifications");
		
		for (int i = 0; i < nodes.getLength(); i++) {
			Node node = nodes.item(i);
			
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				
				Element element = (Element) node;
				
				outboundMessage.put("organizationId", element.getElementsByTagName("OrganizationId").item(0).getTextContent());
				outboundMessage.put("actionId", element.getElementsByTagName("ActionId").item(0).getTextContent());
				outboundMessage.put("sessionId", element.getElementsByTagName("SessionId").item(0).getTextContent());
				outboundMessage.put("enterpriseUrl", element.getElementsByTagName("EnterpriseUrl").item(0).getTextContent());
				outboundMessage.put("partnerUrl", element.getElementsByTagName("PartnerUrl").item(0).getTextContent());
				
				ArrayNode arrayNode = outboundMessage.putArray("notifications");
				
				arrayNode.add(addNotifications(element));
			}
		}
		
		return outboundMessage;
	}
	
	private ObjectNode addNotifications(Element element) {
		
		ObjectNode notificationNode = JsonNodeFactory.instance.objectNode();
		
		NodeList notifications = element.getElementsByTagName("Notification");
		
		for (int i = 0; i < notifications.getLength(); i++) {
			Node notification = notifications.item(i);
			
			if (notification.getNodeType() == Node.ELEMENT_NODE) {
				notificationNode.put("id", ((Element) notification).getElementsByTagName("Id").item(0).getTextContent());
				notificationNode.set("sobject", addSObject(element));
			}
		}
		
		return notificationNode;
	}
	
	private ObjectNode addSObject(Element element) {
		
		ObjectNode sobjectNode = JsonNodeFactory.instance.objectNode();
		
		NodeList sobjects = element.getElementsByTagName("sObject");
		
		for (int i = 0; i < sobjects.getLength(); i++) {
			Node sobject = sobjects.item(i);
			
			if (sobject.getNodeType() == Node.ELEMENT_NODE) {						
				sobjectNode.put("type", ((Element) sobject).getAttribute("xsi:type").replace("sf:", ""));
				sobjectNode.put("id", ((Element) sobject).getElementsByTagName("sf:Id").item(0).getTextContent());
			}
		}
		
		return sobjectNode;
	}
	
	public void saveOutboundMessage(JsonNode outboundMessage) {
		String keyId = "arn:aws:kms:us-east-1:600862814314:key/534e1894-56e5-413b-97fc-a3d6bbc0c51b";
		
		AWSKMS kms = new AWSKMSClient();
		
		ByteBuffer plaintext = ByteBuffer.wrap(outboundMessage.toString().getBytes(Charset.forName(StandardCharsets.UTF_8.displayName())));
		
		EncryptRequest encryptRequest = new EncryptRequest().withKeyId(keyId).withPlaintext(plaintext);
		ByteBuffer ciphertext = kms.encrypt(encryptRequest).getCiphertextBlob();
		
		String payload = Base64.encodeAsString(ciphertext.array());
		
		Transaction transaction = new Transaction();
		transaction.setType(Transaction.TransactionType.OUTBOUND_MESSAGE.name());
		transaction.setStatus(Transaction.TransactionStatus.NEW.name());
		transaction.setTransactionDate(Date.from(Instant.now()));
		transaction.setPayload(payload);
		
		mapper.save(transaction);	
	}
	
	private String getAckMessage(Boolean result) {
		return JsonNodeFactory.instance.objectNode().put("body", ACK_RESPONSE.replace("#ack", String.valueOf(result)).replace("\\", "")).toString();
	}
}