package com.nowellpoint.aws.event;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

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
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.nowellpoint.aws.event.model.Notification;
import com.nowellpoint.aws.event.model.OutboundMessage;
import com.nowellpoint.aws.event.model.Sobject;

public class OutboundMessageListener implements RequestStreamHandler {
	
	private static LambdaLogger logger;
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
		
		/**
		 * 
		 */
		
		logger = context.getLogger();
		
		/**
		 * 
		 */
		
		logger.log("received OutboundMessage");
		
		/**
		 * 
		 */
		
		JsonNode request = new ObjectMapper().readValue(inputStream, JsonNode.class);
		
		String xml = request.get("body").asText().replace("\\", "");	
		
		String response;
		
		try {
			OutboundMessage outboundMessage = xmlToOutboundMessage(xml);
			mapper.save(outboundMessage);	
			response = getAckMessage(Boolean.TRUE);
		} catch (ParserConfigurationException | SAXException | AmazonClientException e) {
			e.printStackTrace();
			response = getAckMessage(Boolean.FALSE);
		}
		
		outputStream.write(response.getBytes());
	}
	
	private OutboundMessage xmlToOutboundMessage(String xml) throws ParserConfigurationException, SAXException, IOException {
		
		OutboundMessage outboundMessage = new OutboundMessage();
		
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document document = builder.parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
		document.getDocumentElement().normalize();
		
		NodeList nodes = document.getElementsByTagName("notifications");
		
		for (int i = 0; i < nodes.getLength(); i++) {
			Node node = nodes.item(i);
			
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				
				Element element = (Element) node;
				
				outboundMessage.setOrganizationId(element.getElementsByTagName("OrganizationId").item(0).getTextContent());
				outboundMessage.setActionId(element.getElementsByTagName("ActionId").item(0).getTextContent());
				outboundMessage.setSessionId(element.getElementsByTagName("SessionId").item(0).getTextContent());
				outboundMessage.setEnterpriseUrl(element.getElementsByTagName("EnterpriseUrl").item(0).getTextContent());
				outboundMessage.setPartnerUrl(element.getElementsByTagName("PartnerUrl").item(0).getTextContent());
				outboundMessage.setNotifications(addNotifications(element));
			}
		}
		
		return outboundMessage;
	}
	
    private List<Notification> addNotifications(Element element) {
    	
    	List<Notification> notifications = new ArrayList<Notification>();
    	
		NodeList nodeList = element.getElementsByTagName("Notification");
		
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Notification notification = new Notification();
				notification.setId(((Element) node).getElementsByTagName("Id").item(0).getTextContent());
				notification.setSobject(addSObject(element));
				notifications.add(notification);
			}
		}
		
		return notifications;
	}
    
    private Sobject addSObject(Element element) {
    	
    	Sobject sobject = new Sobject();
		
		NodeList sobjects = element.getElementsByTagName("sObject");
		
		for (int i = 0; i < sobjects.getLength(); i++) {
			Node node = sobjects.item(i);
			
			if (node.getNodeType() == Node.ELEMENT_NODE) {		
				sobject.setType(((Element) node).getAttribute("xsi:type").replace("sf:", ""));
				sobject.setId(((Element) node).getElementsByTagName("sf:Id").item(0).getTextContent());
			}
		}
		
		return sobject;
	}
	
	private String getAckMessage(Boolean result) {
		return JsonNodeFactory.instance.objectNode().put("body", ACK_RESPONSE.replace("#ack#", String.valueOf(result)).replace("\\", "")).toString();
	}
}