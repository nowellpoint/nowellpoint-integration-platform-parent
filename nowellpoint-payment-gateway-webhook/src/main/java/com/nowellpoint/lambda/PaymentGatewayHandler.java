package com.nowellpoint.lambda;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.List;

import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import com.amazon.sqs.javamessaging.SQSConnection;
import com.amazon.sqs.javamessaging.SQSConnectionFactory;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.Environment;
import com.braintreegateway.Subscription;
import com.braintreegateway.WebhookNotification;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nowellpoint.util.Properties;

public class PaymentGatewayHandler implements RequestStreamHandler {
	
	private static ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
		LambdaLogger logger = context.getLogger();
		
		JsonNode node = objectMapper.readTree(inputStream);
		
		logger.log(node.toString());
		
		Properties.loadProperties(node.get("instance").asText());
		
		List<NameValuePair> params = URLEncodedUtils.parse(node.get("body").asText(), Charset.forName("UTF-8"));
		
		BraintreeGateway gateway = new BraintreeGateway(
				Environment.parseEnvironment(System.getProperty(Properties.BRAINTREE_ENVIRONMENT)),
				System.getProperty(Properties.BRAINTREE_MERCHANT_ID),
				System.getProperty(Properties.BRAINTREE_PUBLIC_KEY),
				System.getProperty(Properties.BRAINTREE_PRIVATE_KEY)
		);
		
		gateway.clientToken().generate();
		
		String signature = getValue(params, "bt_signature");
		String payload = getValue(params, "bt_payload");
		
		WebhookNotification webhookNotification = gateway.webhookNotification().parse(
				signature,
				payload
		);
		
		logger.log("[Webhook Received " + webhookNotification.getTimestamp().getTime() + "] | Kind: " + webhookNotification.getKind());
		
		if (! webhookNotification.getKind().equals(WebhookNotification.Kind.CHECK)) {

			logger.log("[Subscription Id Received " + webhookNotification.getSubscription().getId());
			
			Subscription subscription = webhookNotification.getSubscription();
			
			SQSConnectionFactory connectionFactory = SQSConnectionFactory.builder().build();
			SQSConnection connection = null;
			try {
				connection = connectionFactory.createConnection();
				Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
				Queue queue = session.createQueue("PAYMENT_GATEWAY_INBOUND");
				MessageProducer producer = session.createProducer(queue);
				
				TextMessage message = session.createTextMessage(new ObjectMapper().writeValueAsString(subscription));
				message.setStringProperty("WEBHOOK_NOTIFICATION_INSTANCE", node.get("instance").asText());
				message.setStringProperty("WEBHOOK_NOTIFICATION_KIND", webhookNotification.getKind().name());
				
				producer.send(message);
				
			} catch (JMSException | JsonProcessingException e) {
				e.printStackTrace();
			} finally {
				try {
					connection.close();
				} catch (JMSException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private String getValue(List<NameValuePair> params, String name) {
		return params.stream()
				.filter(param -> param.getName().equals(name))
				.findFirst()
				.get()
				.getValue();
	}
}