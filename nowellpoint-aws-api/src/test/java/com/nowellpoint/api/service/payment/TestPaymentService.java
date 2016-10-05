package com.nowellpoint.api.service.payment;

import java.math.BigDecimal;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.braintreegateway.AddModificationRequest;
import com.braintreegateway.Address;
import com.braintreegateway.AddressRequest;
import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.CreditCard;
import com.braintreegateway.CreditCardRequest;
import com.braintreegateway.Customer;
import com.braintreegateway.CustomerRequest;
import com.braintreegateway.Environment;
import com.braintreegateway.ModificationRequest;
import com.braintreegateway.ModificationsRequest;
import com.braintreegateway.exceptions.NotFoundException;
import com.braintreegateway.Result;
import com.braintreegateway.Subscription;
import com.braintreegateway.SubscriptionRequest;
import com.nowellpoint.aws.model.admin.Properties;

public class TestPaymentService {
	
	private static BraintreeGateway gateway;
	
	@BeforeClass
	public static void beforeClass() {
		
		Properties.setSystemProperties(System.getenv("NCS_PROPERTY_STORE"));
		
		gateway = new BraintreeGateway(Environment.parseEnvironment(System.getProperty(Properties.BRAINTREE_ENVIRONMENT)),
				System.getProperty(Properties.BRAINTREE_MERCHANT_ID),
				System.getProperty(Properties.BRAINTREE_PUBLIC_KEY),
				System.getProperty(Properties.BRAINTREE_PRIVATE_KEY)
		);
		
		gateway.clientToken().generate();
	}
	
	@Test
	public void addCreditCard() {
		
		Customer customer = null;
		
		try {		
			customer = gateway.customer().find("56c7f2b33004ea79702df8d4");
			//gateway.customer().delete("56c7f2b33004ea79702df8d4");
		} catch (NotFoundException e) {
			
		}
		
		CustomerRequest customerRequest = new CustomerRequest()
				.id("56c7f2b33004ea79702df8d4")
				.company("Nowellpoint")
				.email("john.d.herson@gmail.com")
				.firstName("John")
				.lastName("Herson")
				.phone("6787730798");
		
		AddressRequest addressRequest = new AddressRequest()
				.countryCodeAlpha2("US")
				.firstName("John")
				.lastName("Herson")
				.locality("Raleigh")
				.region("North Carolina")
				.postalCode("27601")
				.streetAddress("300 W. Hargett Unit 415");
		
		if (customer == null) {
			Result<Customer> customerResult = gateway.customer().create(customerRequest);
			customer = customerResult.getTarget();
		} else {
			Result<Customer> customerResult = gateway.customer().update(customer.getId(), customerRequest);
			customer = customerResult.getTarget();
		}
		
		Result<Address> addressResult = gateway.address().create(customer.getId(), addressRequest);
		
		System.out.println(addressResult.getTarget().getId());
		
		CreditCardRequest creditCardRequest = new CreditCardRequest()
				.cardholderName("John Herson")
				.expirationMonth("12")
				.expirationYear("2018")
				.number("4111111111111111")
				.customerId(customer.getId())
				.billingAddressId(addressResult.getTarget().getId());
		
		Result<CreditCard> creditCardResult = gateway.creditCard().create(creditCardRequest);
		
		System.out.println(creditCardResult.getTarget().getToken());
		
		gateway.address().update(customer.getId(), addressResult.getTarget().getId(), addressRequest);
		
		gateway.creditCard().update(creditCardResult.getTarget().getToken(), creditCardRequest);
		
		customer = gateway.customer().find("56c7f2b33004ea79702df8d4");
		
		SubscriptionRequest subscriptionRequest = new SubscriptionRequest()
			    .paymentMethodToken(customer.getDefaultPaymentMethod().getToken())
			    .planId("RECURRING_MONTHLY_PLAN")
			    .price(new BigDecimal("0.00"));

		Result<Subscription> subscriptionResult = gateway.subscription().create(subscriptionRequest);
		
		System.out.println(subscriptionResult.getTarget().getId());		
	}
	
	@AfterClass
	public static void afterClass() {
		//gateway.customer().delete("56c7f2b33004ea79702df8d4");
	}
}