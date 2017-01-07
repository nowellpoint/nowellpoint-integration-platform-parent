package com.nowellpoint.client.test;

import java.math.BigDecimal;
import java.util.logging.Logger;

import org.junit.Test;

import com.braintreegateway.AddressRequest;
import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.CreditCardRequest;
import com.braintreegateway.CustomerRequest;
import com.braintreegateway.Environment;
import com.braintreegateway.Result;
import com.braintreegateway.SubscriptionRequest;
import com.nowellpoint.util.Properties;

public class TestSubscriptionWebhook {
	
	private static Logger LOG = Logger.getLogger(TestSubscriptionWebhook.class.getName());
	
	@Test
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
				.email("test.nowellpoint@mailinator.com")
				.firstName("Test")
				.lastName("Herson")
				.phone("000-000-0000");
		
		Result<com.braintreegateway.Customer> customerResult = gateway.customer().create(customerRequest);
		
		AddressRequest addressRequest = new AddressRequest()
				.countryCodeAlpha2("US");
		
		Result<com.braintreegateway.Address> addressResult = gateway.address().create(customerResult.getTarget().getId(), addressRequest);
		
		CreditCardRequest creditCardRequest = new CreditCardRequest()
				.cardholderName( "Test User" )
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

	}
}
