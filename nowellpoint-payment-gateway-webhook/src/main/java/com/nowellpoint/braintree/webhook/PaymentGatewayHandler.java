package com.nowellpoint.braintree.webhook;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.sql.Date;
import java.time.Instant;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.Environment;
import com.braintreegateway.WebhookNotification;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nowellpoint.dynamodb.DynamoDBMapperProvider;
import com.nowellpoint.dynamodb.model.PaymentGatewayNotification;
import com.nowellpoint.util.Properties;

public class PaymentGatewayHandler implements RequestStreamHandler {
	
	private static final DynamoDBMapper mapper = DynamoDBMapperProvider.getDynamoDBMapper();

	@Override
	public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
		LambdaLogger logger = context.getLogger();
		
		ObjectMapper objectMapper = new ObjectMapper();
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
			
			PaymentGatewayNotification notification = initialize(webhookNotification);      
			
			mapper.save(notification);
		}	
	}
	
	private PaymentGatewayNotification initialize(WebhookNotification webhookNotification) {
		return new PaymentGatewayNotification().environment(System.getProperty(Properties.BRAINTREE_ENVIRONMENT))
				.merchantId(System.getProperty(Properties.BRAINTREE_MERCHANT_ID))
				.privateKey(System.getProperty(Properties.BRAINTREE_PRIVATE_KEY))
				.publicKey(System.getProperty(Properties.BRAINTREE_PUBLIC_KEY))
				.subscriptionId(webhookNotification.getSubscription().getId())
				.status(PaymentGatewayNotification.Status.RECEIVED.name())
				.emailApiKey(System.getProperty(Properties.SENDGRID_API_KEY))
				.webhookNotificationKind(webhookNotification.getKind().name())
				.applicationHostname(System.getProperty(Properties.APPLICATION_HOSTNAME))
				.receivedOn(Date.from(Instant.now()));
	}
	
	private String getValue(List<NameValuePair> params, String name) {
		return params.stream()
				.filter(param -> param.getName().equals(name))
				.findFirst()
				.get()
				.getValue();
	}
}