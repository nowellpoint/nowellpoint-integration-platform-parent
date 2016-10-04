package com.nowellpoint.api.service.payment;

import org.junit.BeforeClass;
import org.junit.Test;

import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.Customer;
import com.braintreegateway.CustomerRequest;
import com.braintreegateway.Environment;
import com.braintreegateway.Result;
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
	public void testCreateCustomer() {
		
		CustomerRequest customerRequest = new CustomerRequest()
				.id("56c7f2b33004ea79702df8d4")
				.company("Nowellpoint")
				.email("john.d.herson@gmail.com")
				.firstName("John")
				.lastName("Herson")
				.phone("6787730798");
		
		//Result<Customer> customerResult = gateway.customer().create(customerRequest);
		
		gateway.customer().delete("56c7f2b33004ea79702df8d4");
		
		//System.out.println(customerResult.getMessage());
	}
}
