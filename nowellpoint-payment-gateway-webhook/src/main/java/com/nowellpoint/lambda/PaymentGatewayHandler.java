package com.nowellpoint.lambda;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.MessageAttributeValue;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.Environment;
import com.braintreegateway.Subscription;
import com.braintreegateway.WebhookNotification;
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
			
			Map<String, MessageAttributeValue> messageAttributes = new HashMap<String, MessageAttributeValue>();
			messageAttributes.put("WEBHOOK_NOTIFICATION_INSTANCE", new MessageAttributeValue().withDataType("String").withStringValue(node.get("instance").asText()));
	        messageAttributes.put("WEBHOOK_NOTIFICATION_KIND", new MessageAttributeValue().withDataType("String").withStringValue(webhookNotification.getKind().name()));
			
			AmazonSQS sqs = new AmazonSQSClient();
			
			SendMessageRequest sendMessageRequest = new SendMessageRequest().withQueueUrl("https://sqs.us-east-1.amazonaws.com/600862814314/PAYMENT_GATEWAY_INBOUND")
	        		.withMessageBody(objectMapper.writeValueAsString(subscription))
	        		.withMessageAttributes(messageAttributes);
	        
	        sqs.sendMessage(sendMessageRequest);
			
			
			/**
			 * WebhookNotification.Kind.SUBSCRIPTION_CANCELED
		WebhookNotification.Kind.SUBSCRIPTION_CHARGED_SUCCESSFULLY
		WebhookNotification.Kind.SUBSCRIPTION_CHARGED_UNSUCCESSFULLY
		WebhookNotification.Kind.SUBSCRIPTION_EXPIRED
		WebhookNotification.Kind.SUBSCRIPTION_TRIAL_ENDED
		WebhookNotification.Kind.SUBSCRIPTION_WENT_ACTIVE
		WebhookNotification.Kind.SUBSCRIPTION_WENT_PAST_DUE
		
			 */
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