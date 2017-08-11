package com.nowellpoint.api.service;

import com.braintreegateway.CreditCardRequest;
import com.braintreegateway.CreditCard;
import com.braintreegateway.Customer;
import com.braintreegateway.Result;

public interface PaymentGatewayService {
	public Result<Customer> addCustomer(
			String company, 
			String email, 
			String firstName, 
			String lastName, 
			String phone);
	
	public Result<Customer> updateCustomer(
			String id, 
			String company, 
			String email, 
			String firstName, 
			String lastName, 
			String phone);
	
	public Result<CreditCard> addCreditCard(CreditCardRequest creditCardRequest);
	
	public Result<CreditCard> updateCreditCard(String token, CreditCardRequest creditCardRequest);
	
//	public Result<CreditCard> addCreditCard(
//			String customerId, 
//			String billingAddressId, 
//			String cardholderName, 
//			String number, 
//			String expirationMonth, 
//			String expirationYear);
//	
//	public Result<CreditCard> updateCreditCard(
//			String customerId, 
//			String billingAddressId, 
//			String token, 
//			String cardholderName, 
//			String number, 
//			String expirationMonth, 
//			String expirationYear);
	
}