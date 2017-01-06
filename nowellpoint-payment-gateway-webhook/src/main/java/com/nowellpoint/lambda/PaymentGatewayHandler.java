package com.nowellpoint.lambda;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.Environment;
import com.braintreegateway.WebhookNotification;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nowellpoint.util.Properties;

public class PaymentGatewayHandler implements RequestStreamHandler {
	
	private static ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
		
		JsonNode node = objectMapper.readTree(inputStream);
		
		List<NameValuePair> params = URLEncodedUtils.parse(node.get("body").asText(), Charset.forName("UTF-8"));
		
		Properties.loadProperties("production");
		
		BraintreeGateway gateway = new BraintreeGateway(
				Environment.parseEnvironment(System.getProperty(Properties.BRAINTREE_ENVIRONMENT)),
				System.getProperty(Properties.BRAINTREE_MERCHANT_ID),
				System.getProperty(Properties.BRAINTREE_PUBLIC_KEY),
				System.getProperty(Properties.BRAINTREE_PRIVATE_KEY)
		);
		
		gateway.clientToken().generate();
		
		String signature = null;
		String payload = null;
		
		for (NameValuePair param : params) {
			if ("bt_signature".equals(param.getName())) {
				signature = param.getValue();
			}
			if ("bt_payload".equals(param.getName())) {
				payload = param.getValue();
			}
		}
		
		Map<String, String> sampleNotification = gateway.webhookTesting().sampleNotification(
				WebhookNotification.Kind.SUBSCRIPTION_CHARGED_SUCCESSFULLY, "4hnmnr"
		);
		
		WebhookNotification webhookNotification = gateway.webhookNotification().parse(
				signature,
				payload
				//sampleNotification.get("bt_signature"),
				//sampleNotification.get("bt_payload")
		);
		
		System.out.println("[Webhook Received " + webhookNotification.getTimestamp().getTime() + "] | Kind: " + webhookNotification.getKind());
		
		System.out.println("[Subscription Id Received " + webhookNotification.getSubscription().getId());
		
	}
}