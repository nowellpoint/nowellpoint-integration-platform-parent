package com.nowellpoint.lambda;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.Environment;
import com.braintreegateway.WebhookNotification;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PaymentGatewayHandler implements RequestStreamHandler {
	
	private static ObjectMapper objectMapper = new ObjectMapper();
	
	private static BraintreeGateway gateway = new BraintreeGateway(
			Environment.parseEnvironment(System.getenv("BRAINTREE_ENVIRONMENT")),
			System.getenv("BRAINTREE_MERCHANT_ID"),
			System.getenv("BRAINTREE_PUBLIC_KEY"),
			System.getenv("BRAINTREE_PRIVATE_KEY")
	);
	
	static {
		gateway.clientToken().generate();
	}

	@Override
	public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
		
		JsonNode node = objectMapper.readTree(inputStream);
		
		WebhookNotification webhookNotification = gateway.webhookNotification().parse(
				node.get("bt_signature").asText(),
				node.get("bt_payload").asText()
		);
		
		System.out.println("[Webhook Received " + webhookNotification.getTimestamp().getTime() + "] | Kind: " + webhookNotification.getKind());
		
	}
}