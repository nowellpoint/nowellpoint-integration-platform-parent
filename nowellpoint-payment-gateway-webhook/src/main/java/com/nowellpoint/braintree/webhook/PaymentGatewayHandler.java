package com.nowellpoint.braintree.webhook;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.Environment;
import com.braintreegateway.WebhookNotification;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PaymentGatewayHandler implements RequestStreamHandler {

	@Override
	public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
		LambdaLogger logger = context.getLogger();
		
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode node = objectMapper.readTree(inputStream);
		
		logger.log(node.toString());
		
		List<NameValuePair> params = URLEncodedUtils.parse(node.get("body").asText(), Charset.forName("UTF-8"));
		
		BraintreeGateway gateway = new BraintreeGateway(
				Environment.parseEnvironment(System.getenv("BRAINTREE_ENVIRONMENT")),
				System.getenv("BRAINTREE_MERCHANT_ID"),
				System.getenv("BRAINTREE_PUBLIC_KEY"),
				System.getenv("BRAINTREE_PRIVATE_KEY")
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