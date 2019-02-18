package com.nowellpoint.api.service.test;

import org.junit.BeforeClass;
import org.junit.Test;

import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.CreditCardRequest;
import com.braintreegateway.Environment;
import com.braintreegateway.Result;

public class TestPaymentService {
	
	private static BraintreeGateway gateway;
	
	@BeforeClass
	public static void beforeClass() {
		
		gateway = new BraintreeGateway(Environment.parseEnvironment(System.getProperty("BRAINTREE_ENVIRONMENT")),
				System.getProperty("BRAINTREE_MERCHANT_ID"),
				System.getProperty("BRAINTREE_PUBLIC_KEY"),
				System.getProperty("Properties.BRAINTREE_PRIVATE_KEY")
		);
		
		gateway.clientToken().generate();
	}
	
	@Test
	public void updateCreditCard() {
		CreditCardRequest creditCardRequest = new CreditCardRequest()
				.cardholderName("Cardholder Name")
				.expirationMonth("12")
				.expirationYear("2020")
				.number(null)
				.cvv("1112")
				.customerId("641228216")
				.billingAddressId("27");
		
		Result<com.braintreegateway.CreditCard> result = gateway.creditCard().update("dss8tj", creditCardRequest);
		
		System.out.println(result.getMessage());
	}
}