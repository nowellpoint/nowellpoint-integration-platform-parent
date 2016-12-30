package com.nowellpoint.api.service;

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
import com.braintreegateway.exceptions.NotFoundException;
import com.nowellpoint.util.Properties;

public class PaymentGatewayService {
	
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
		
		Result<Customer> customerResult = null;
		
		try {
			Customer customer = gateway.customer().find(customerRequest.getId());
			customerResult = gateway.customer().update(customer.getId(), customerRequest);
		} catch (NotFoundException e) {
			customerResult = gateway.customer().create(customerRequest);
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
	
	public Result<Subscription> createSubscription(SubscriptionRequest subscriptionRequest) {
		Result<Subscription> result = gateway.subscription().create(subscriptionRequest);
		return result;
	}
	
	public Result<Subscription> updateSubscription(String id, SubscriptionRequest subscriptionRequest) {
		Result<Subscription> result = gateway.subscription().update(id, subscriptionRequest);
		return result;
	}
	
	public Result<Subscription> cancelSubscription(String id) {
		Result<Subscription> result = gateway.subscription().cancel(id);
		return result;
	}
}