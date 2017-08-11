package com.nowellpoint.api.service;

import com.nowellpoint.api.rest.domain.Organization;

/**
 * 
 * 
 * @author jherson
 *
 * 
 */

public interface OrganizationService {
	public Organization findById(String id);
	
	public Organization findByDomain(String domain);
	
	public Organization createOrganization(
			String domain, 
			String planId, 
			String firstName,
			String lastName,
			String email,
			String phone,
			String countryCode,
			String cardholderName, 
			String expirationMonth, 
			String expirationYear,
			String number, 
			String cvv);
	
	public Organization updateOrganization(String id, String domain);
	
	public Organization changePlan(String id, String planId);
	
	public Organization updateCreditCard(
			String id, 
			String cardholderName, 
			String expirationMonth, 
			String expirationYear, 
			String number, 
			String cvv);
	
	public Organization updateBillingAddress(String id, String street, String city, String stateCode, String postalCode, String countryCode);
	
	public Organization updateBillingContact(String id, String firstName, String lastName, String email, String phone);
}