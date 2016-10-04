package com.nowellpoint.api.service;

import java.math.BigDecimal;

import org.jboss.logging.Logger;

import com.braintreegateway.SubscriptionRequest;
import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.Customer;
import com.braintreegateway.Subscription;
import com.braintreegateway.Environment;
import com.braintreegateway.Result;
import com.braintreegateway.Transaction;
import com.braintreegateway.TransactionRequest;
import com.braintreegateway.exceptions.NotFoundException;
import com.nowellpoint.aws.model.admin.Properties;

public class PaymentGatewayService {
	
	private static final Logger LOGGER = Logger.getLogger(PaymentGatewayService.class);
	
	private static BraintreeGateway gateway = new BraintreeGateway(
			Environment.parseEnvironment(System.getProperty(Properties.BRAINTREE_ENVIRONMENT)),
			System.getProperty(Properties.BRAINTREE_MERCHANT_ID),
			System.getProperty(Properties.BRAINTREE_PUBLIC_KEY),
			System.getProperty(Properties.BRAINTREE_PRIVATE_KEY)
	);
	
	static {
		gateway.clientToken().generate();
	}
	
	public Customer addCreditCard(String id) {
		Customer customer = null;
		try {
			customer = gateway.customer().find(id);
		} catch (NotFoundException e) {
			LOGGER.warn(e.getMessage());
		}
		return customer;
	}
	
	public void updateCreditCard() {
		
	}
	
	public void deleteCreditCard() {
		
	}
	
	public void submitTransaction(String paymentMethodToken, BigDecimal amount) {
		
		TransactionRequest request = new TransactionRequest()
				.amount(amount)
				.paymentMethodToken(paymentMethodToken)
				.options()
				.submitForSettlement(true)
				.done();
		
		Result<Transaction> result = gateway.transaction().sale(request);
		
		System.out.println(result.getMessage());
	}
	
	public void addMonthlyRecurringPlan(String paymentMethodToken, BigDecimal price) {
		SubscriptionRequest request = new SubscriptionRequest()
			    .paymentMethodToken(paymentMethodToken)
			    .planId("RECURRING_MONTHLY_PLAN")
			    .price(price);

		Result<Subscription> result = gateway.subscription().create(request);
		
		System.out.println(result.getMessage());
	}
}