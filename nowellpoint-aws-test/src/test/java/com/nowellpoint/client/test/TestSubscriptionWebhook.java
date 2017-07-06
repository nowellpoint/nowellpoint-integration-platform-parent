package com.nowellpoint.client.test;

import java.math.BigDecimal;
import java.util.logging.Logger;

import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.junit.Ignore;
import org.junit.Test;

import com.amazon.sqs.javamessaging.SQSConnection;
import com.amazon.sqs.javamessaging.SQSConnectionFactory;
import com.braintreegateway.AddressRequest;
import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.CreditCardRequest;
import com.braintreegateway.CustomerRequest;
import com.braintreegateway.Environment;
import com.braintreegateway.Result;
import com.braintreegateway.SubscriptionRequest;
import com.braintreegateway.Transaction;
import com.braintreegateway.WebhookNotification;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nowellpoint.util.Properties;

public class TestSubscriptionWebhook {
	
	private static Logger LOG = Logger.getLogger(TestSubscriptionWebhook.class.getName());
	
	@Test
	@Ignore
	public void testCreateSubscription() {
		
		Properties.loadProperties("sandbox");
		
		BraintreeGateway gateway = new BraintreeGateway(
				Environment.parseEnvironment(System.getProperty(Properties.BRAINTREE_ENVIRONMENT)),
				System.getProperty(Properties.BRAINTREE_MERCHANT_ID),
				System.getProperty(Properties.BRAINTREE_PUBLIC_KEY),
				System.getProperty(Properties.BRAINTREE_PRIVATE_KEY)
		);
		
		gateway.clientToken().generate();
		
		CustomerRequest customerRequest = new CustomerRequest()
				.company("Test Company")
				.email("john.d.herson@gmail.com")
				.firstName("Test")
				.lastName("Herson")
				.phone("000-000-0000");
		
		Result<com.braintreegateway.Customer> customerResult = gateway.customer().create(customerRequest);
		
		AddressRequest addressRequest = new AddressRequest()
				.locality("Raleigh")
				.postalCode("27601")
				.region("NC")
				.streetAddress("129 S. Bloodworth Street")
				.countryCodeAlpha2("US");
		
		Result<com.braintreegateway.Address> addressResult = gateway.address().create(customerResult.getTarget().getId(), addressRequest);
		
		CreditCardRequest creditCardRequest = new CreditCardRequest()
				.cardholderName( "John Herson" )
				.expirationMonth( "12" )
				.expirationYear( "2017" )
				.number( "4111111111111111" )
				.customerId( customerResult.getTarget().getId() )
				.billingAddressId( addressResult.getTarget().getId() );
		
		Result<com.braintreegateway.CreditCard> creditCardResult = gateway.creditCard().create(creditCardRequest);
		
		SubscriptionRequest subscriptionRequest = new SubscriptionRequest()
				.paymentMethodToken( creditCardResult.getTarget().getToken() )
				.planId( "STANDARD" )
				.price(new BigDecimal( 25.00 ));
		
		Result<com.braintreegateway.Subscription> subscriptionResult = gateway.subscription().create( subscriptionRequest );
		
		LOG.info(subscriptionResult.getTarget().getId());
		
		//gateway.creditCard().delete(creditCardResult.getTarget().getToken());
		
		//gateway.subscription().delete(customerResult.getTarget().getId(), subscriptionResult.getTarget().getId());
		
		//gateway.address().delete(customerResult.getTarget().getId(), addressResult.getTarget().getId());
		
		//gateway.customer().delete(customerResult.getTarget().getId());

	}
	
	@Test
	public void testChargeSubscriptionMock() {
		Properties.loadProperties("sandbox");
		
		BraintreeGateway gateway = new BraintreeGateway(
				Environment.parseEnvironment(System.getProperty(Properties.BRAINTREE_ENVIRONMENT)),
				System.getProperty(Properties.BRAINTREE_MERCHANT_ID),
				System.getProperty(Properties.BRAINTREE_PUBLIC_KEY),
				System.getProperty(Properties.BRAINTREE_PRIVATE_KEY)
		);
		
		gateway.clientToken().generate();
		
		com.braintreegateway.Subscription subscription = gateway.subscription().find("6q6tr2");
		
		Transaction transaction = subscription.getTransactions()
				.stream().sorted((t1,t2) -> t2.getCreatedAt().compareTo(t1.getCreatedAt()))
				.findFirst()
				.get();
		
		ObjectMapper objectMapper = new ObjectMapper();
		
		ObjectNode node = objectMapper.createObjectNode()
				.put("id", subscription.getId())
				.put("status", subscription.getStatus().name())
				.put("nextBillingDate", subscription.getNextBillingDate().getTimeInMillis());
		
		ObjectNode transactionNode = objectMapper.createObjectNode()
				.put("id", transaction.getId())
				.put("amount", transaction.getAmount())
				.put("status", transaction.getStatus().name())
				.put("currencyIsoCode", transaction.getCurrencyIsoCode())
				.put("planId", transaction.getPlanId())
				.put("createdAt", transaction.getCreatedAt().getTimeInMillis())
				.put("updatedAt", transaction.getUpdatedAt().getTimeInMillis());
		
		node.set("transaction", transactionNode);
		
		ObjectNode creditCardNode = objectMapper.createObjectNode()
				.put("token", transaction.getCreditCard().getToken())
				.put("cardholderName", transaction.getCreditCard().getCardholderName())
				.put("cardType", transaction.getCreditCard().getCardType())
				.put("last4", transaction.getCreditCard().getLast4())
				.put("expirationDate", transaction.getCreditCard().getExpirationDate())
				.put("expirationMonth", transaction.getCreditCard().getExpirationMonth())
				.put("expirationYear", transaction.getCreditCard().getExpirationYear())
				.put("imageUrl", transaction.getCreditCard().getImageUrl());
		
		transactionNode.set("creditCard", creditCardNode);
		
		LOG.info(node.toString());
		
		SQSConnectionFactory connectionFactory = SQSConnectionFactory.builder().build();
		try {
			SQSConnection connection = connectionFactory.createConnection();
			Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
			Queue queue = session.createQueue("PAYMENT_GATEWAY_INBOUND");
			MessageProducer producer = session.createProducer(queue);
			
			TextMessage message = session.createTextMessage(node.toString());
			message.setStringProperty("WEBHOOK_NOTIFICATION_INSTANCE", "sandbox");
			message.setStringProperty("WEBHOOK_NOTIFICATION_KIND", WebhookNotification.Kind.SUBSCRIPTION_CHARGED_SUCCESSFULLY.name());
			
			producer.send(message);
			
			connection.close();
			
		} catch (JMSException e) {
			e.printStackTrace();
		}		
	}
	
	@Test
	@Ignore
	public void testCancelSubscriptionMock() {
		
		Properties.loadProperties("sandbox");
		
		BraintreeGateway gateway = new BraintreeGateway(
				Environment.parseEnvironment(System.getProperty(Properties.BRAINTREE_ENVIRONMENT)),
				System.getProperty(Properties.BRAINTREE_MERCHANT_ID),
				System.getProperty(Properties.BRAINTREE_PUBLIC_KEY),
				System.getProperty(Properties.BRAINTREE_PRIVATE_KEY)
		);
		
		gateway.clientToken().generate();
		
		com.braintreegateway.Subscription subscription = gateway.subscription().find("123456789");
		
		SQSConnectionFactory connectionFactory = SQSConnectionFactory.builder().build();
		try {
			SQSConnection connection = connectionFactory.createConnection();
			Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
			Queue queue = session.createQueue("PAYMENT_GATEWAY_INBOUND");
			MessageProducer producer = session.createProducer(queue);
			
			TextMessage message = session.createTextMessage(new ObjectMapper().writeValueAsString(subscription));
			message.setStringProperty("WEBHOOK_NOTIFICATION_INSTANCE", "sandbox");
			message.setStringProperty("WEBHOOK_NOTIFICATION_KIND", "SUBSCRIPTION_CANCELED");
			
			producer.send(message);
			
			connection.close();
			
		} catch (JMSException | JsonProcessingException e) {
			e.printStackTrace();
		}		
	}
}
