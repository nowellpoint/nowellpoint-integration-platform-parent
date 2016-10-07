package com.nowellpoint.api.service;

import java.math.BigDecimal;

import org.jboss.logging.Logger;

import com.braintreegateway.SubscriptionRequest;
import com.braintreegateway.Address;
import com.braintreegateway.AddressRequest;
import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.CreditCard;
import com.braintreegateway.CreditCardRequest;
import com.braintreegateway.Customer;
import com.braintreegateway.CustomerRequest;
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
	
	public Result<Customer> addOrUpdateCustomer(CustomerRequest customerRequest) {
		
		Customer customer = null;
		
		try {
			customer = gateway.customer().find(customerRequest.getId());
		} catch (NotFoundException e) {
			LOGGER.warn(e.getMessage());
		}
		
		Result<Customer> customerResult = null;
		
		if (customer == null) {
			customerResult = gateway.customer().create(customerRequest);
		} else {
			customerResult = gateway.customer().update(customer.getId(), customerRequest);
		}
		
		return customerResult;
	}
	
	public Result<Address> createAddress(String customerId, AddressRequest addressRequest) {
		Result<Address> addressResult = gateway.address().create(customerId, addressRequest);
		return addressResult;
	}
	
	public Result<Address> updateAddress(String customerId, String billingAddressId, AddressRequest addressRequest) {
		Result<Address> addressResult = gateway.address().update(customerId, billingAddressId, addressRequest);
		return addressResult;
	}
	
	public Result<Address> deleteAddress(String customerId, String billingAddressId) {
		Result<Address> addressResult = gateway.address().delete(customerId, billingAddressId);
		return addressResult;
	}
	
	public CreditCard findCreditCard(String token) {
		return gateway.creditCard().find(token);
	}
	
	public Result<CreditCard> createCreditCard(CreditCardRequest creditCardRequest) {
		Result<CreditCard> creditCardResult = gateway.creditCard().create(creditCardRequest);
		return creditCardResult;
	}
	
	public Result<CreditCard> updateCreditCard(String token, CreditCardRequest creditCardRequest) {
		Result<CreditCard> creditCardResult = gateway.creditCard().update(token, creditCardRequest);
		return creditCardResult;
	}
	
	public Result<CreditCard> deleteCreditCard(String token) {
		Result<CreditCard> creditCardResult = gateway.creditCard().delete(token);
		return creditCardResult;
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
	
	public Result<Subscription> addMonthlyRecurringPlan(String paymentMethodToken, BigDecimal price) {
		SubscriptionRequest request = new SubscriptionRequest()
			    .paymentMethodToken(paymentMethodToken)
			    .planId("RECURRING_MONTHLY_PLAN")
			    .price(price);

		Result<Subscription> result = gateway.subscription().create(request);
		
		return result;
	}
}