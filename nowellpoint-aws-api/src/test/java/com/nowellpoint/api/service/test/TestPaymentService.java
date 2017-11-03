package com.nowellpoint.api.service.test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.braintreegateway.Address;
import com.braintreegateway.AddressRequest;
import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.CreditCard;
import com.braintreegateway.CreditCardAddressRequest;
import com.braintreegateway.CreditCardRequest;
import com.braintreegateway.Customer;
import com.braintreegateway.CustomerRequest;
import com.braintreegateway.Environment;
import com.braintreegateway.MerchantAccount;
import com.braintreegateway.PaginatedCollection;
import com.braintreegateway.Plan;
import com.braintreegateway.Result;
import com.braintreegateway.Subscription;
import com.braintreegateway.SubscriptionRequest;
import com.braintreegateway.exceptions.NotFoundException;
import com.nowellpoint.util.Properties;

public class TestPaymentService {
	
	private static BraintreeGateway gateway;
	
	@BeforeClass
	public static void beforeClass() {
		
		Properties.loadProperties(System.getenv("NOWELLPOINT_PROPERTY_STORE"));
		
		gateway = new BraintreeGateway(Environment.parseEnvironment(System.getProperty(Properties.BRAINTREE_ENVIRONMENT)),
				System.getProperty(Properties.BRAINTREE_MERCHANT_ID),
				System.getProperty(Properties.BRAINTREE_PUBLIC_KEY),
				System.getProperty(Properties.BRAINTREE_PRIVATE_KEY)
		);
		
		gateway.clientToken().generate();
	}
	
	@Test
	public void getPlans() {
		List<Plan> list = gateway.plan().all();
		list.forEach( a -> {
			System.out.println("*****");
			System.out.println(a.getCurrencyIsoCode());
			System.out.println(a.getDescription());
			System.out.println(a.getId());
			System.out.println(a.getName());
			System.out.println(a.getPrice());
		});
	}
}