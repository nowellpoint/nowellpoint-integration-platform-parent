package com.nowellpoint.api.service;

import com.nowellpoint.api.model.document.Address;
import com.nowellpoint.api.rest.domain.AccountProfile;
import com.nowellpoint.api.rest.domain.CreditCard;
import com.nowellpoint.api.rest.domain.Subscription;

public interface AccountProfileService {
	
	public void createAccountProfile(AccountProfile accountProfile);
	
	public void deactivateAccountProfile(String id);
	
	public void updateAccountProfile(AccountProfile accountProfile);
	
	public void updateAddress(String id, Address address);
	
	public Subscription getSubscription(String id);
	
	public void setSubscription(String accountProfileId, String paymentMethodToken, Subscription subscription);
	
	public Address getAddress(String id);
	
	public AccountProfile findById(String id);
	
	public AccountProfile findByIdpId(String accountHref);
	
	public AccountProfile findByUsername(String username);
	
	public void addSalesforceProfilePicture(String userId, String profileHref);
	
	public CreditCard getCreditCard(String id, String token);
		
	public CreditCard setPrimary(String id, String token);
		
	public void addCreditCard(String id, CreditCard creditCard);
	
	public void updateCreditCard(String id, String token, CreditCard creditCard);

	public void removeCreditCard(String id, String token);
	
	public byte[] getInvoice(String id, String invoiceNumber);
}