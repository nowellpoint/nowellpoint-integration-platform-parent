package com.nowellpoint.api.service.impl;

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
import com.nowellpoint.api.service.PaymentGatewayService;
import com.nowellpoint.util.Properties;

public class PaymentGatewayServiceImpl implements PaymentGatewayService {
	
	private static BraintreeGateway gateway = new BraintreeGateway(
			Environment.parseEnvironment(System.getProperty(Properties.BRAINTREE_ENVIRONMENT)),
			System.getProperty(Properties.BRAINTREE_MERCHANT_ID),
			System.getProperty(Properties.BRAINTREE_PUBLIC_KEY),
			System.getProperty(Properties.BRAINTREE_PRIVATE_KEY)
	);
	
	static {
		gateway.clientToken().generate();
	}
	
	@Override
	public Result<CreditCard> addCreditCard(CreditCardRequest creditCardRequest) {
		Result<CreditCard> result = gateway.creditCard().create(creditCardRequest);
		return result;
	}
	
	public Result<CreditCard> updateCreditCard(String token, CreditCardRequest creditCardRequest) {
		Result<CreditCard> result = gateway.creditCard().update(token, creditCardRequest);
		return result;
	}
	
	
//	@Override
//	public Result<CreditCard> updateCreditCard(String customerId, String billingAddressId, String token, String cardholderName, String number, String expirationMonth, String expirationYear) {
//		
//		CreditCardRequest creditCardRequest = new CreditCardRequest()
//				.cardholderName(cardholderName)
//				.expirationMonth(expirationMonth)
//				.expirationYear(expirationYear)
//				.number(number)
//				.token(token)
//				.customerId(customerId)
//				.billingAddressId(billingAddressId);
//		
//		Result<CreditCard> creditCardResult = gateway.creditCard().update(token, creditCardRequest);
//		return creditCardResult;
//	}
	
	@Override
	public Result<Customer> addCustomer(String company, String email, String firstName, String lastName, String phone) {
		
		CustomerRequest customerRequest = new CustomerRequest()
				.company(company)
				.email(email)
				.firstName(firstName)
				.lastName(lastName)
				.phone(phone);
		
		Result<Customer> customerResult = gateway.customer().create(customerRequest);
		return customerResult;
	}
	
	@Override
	public Result<Customer> updateCustomer(String id, String company, String email, String firstName, String lastName, String phone) {
		
		CustomerRequest customerRequest = new CustomerRequest()
				.id(id)
				.company(company)
				.email(email)
				.firstName(firstName)
				.lastName(lastName)
				.phone(phone);
		
		Result<Customer> customerResult = gateway.customer().create(customerRequest);
		return customerResult;
	}
	
	public Result<Customer> addOrUpdateCustomer(CustomerRequest customerRequest) {
		
		/**
		 * 
		 */
		
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
	
	public com.braintreegateway.CreditCard findCreditCard(String token) {
		return gateway.creditCard().find(token);
	}
	
	public Result<com.braintreegateway.CreditCard> createCreditCard(CreditCardRequest creditCardRequest) {
		Result<com.braintreegateway.CreditCard> creditCardResult = gateway.creditCard().create(creditCardRequest);
		return creditCardResult;
	}
	
	public Result<com.braintreegateway.CreditCard> deleteCreditCard(String token) {
		Result<com.braintreegateway.CreditCard> creditCardResult = gateway.creditCard().delete(token);
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