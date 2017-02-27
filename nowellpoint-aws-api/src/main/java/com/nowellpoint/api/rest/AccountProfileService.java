package com.nowellpoint.api.rest;

import com.nowellpoint.api.model.document.Address;
import com.nowellpoint.api.rest.domain.AccountProfile;
import com.nowellpoint.api.rest.domain.CreditCard;
import com.nowellpoint.api.rest.domain.Subscription;

public interface AccountProfileService {
	
	/**
	 * 
	 * 
	 * 
	 * 
	 */
	
	void createAccountProfile(AccountProfile accountProfile);
	
	/**
	 * 
	 * 
	 * 
	 * 
	 */
	
	void deactivateAccountProfile(String id);
	
	/**
	 * 
	 * 
	 * 
	 * 
	 */
	
	void updateAccountProfile(AccountProfile accountProfile);
	
	/**
	 * 
	 * 
	 * 
	 * 
	 */
	
	void updateAddress(String id, Address address);
	
	/**
	 * 
	 * 
	 * 
	 * 
	 */
	
	Subscription getSubscription(String id);
	
	/**
	 * 
	 * 
	 * 
	 * 
	 */
	
	void setSubscription(String accountProfileId, String paymentMethodToken, Subscription subscription);
	
	/**
	 * 
	 * 
	 * 
	 * 
	 */
	
	Address getAddress(String id);
	
	/**
	 * 
	 * 
	 * 
	 * 
	 */
	
	AccountProfile findById(String id);
	
	/**
	 * 
	 * 
	 * 
	 * 
	 */
	
	AccountProfile findByAccountHref(String accountHref);
	
	/**
	 * 
	 * 
	 * 
	 * 
	 */
	
	AccountProfile findByUsername(String username);
	
	/**
	 * 
	 * 
	 * 
	 * 
	 */
	
	void addSalesforceProfilePicture(String userId, String profileHref);
	
	/**
	 * 
	 * 
	 * 
	 * 
	 */
	
	CreditCard getCreditCard(String id, String token);
	
	/**
	 * 
	 * 
	 * 
	 * 
	 */
	
	CreditCard setPrimary(String id, String token);
	
	/**
	 * 
	 * 
	 * 
	 * 
	 */
	
	void addCreditCard(String id, CreditCard creditCard);
	
	/**
	 * 
	 * 
	 * 
	 * 
	 */
	
	void updateCreditCard(String id, String token, CreditCard creditCard);

	/**
	 * 
	 * 
	 * 
	 * 
	 */
	
	void removeCreditCard(String id, String token);
	
	/**
	 * 
	 * 
	 * 
	 * 
	 */
	
	AccountProfile findBySubscriptionId(String subscriptionId);
}