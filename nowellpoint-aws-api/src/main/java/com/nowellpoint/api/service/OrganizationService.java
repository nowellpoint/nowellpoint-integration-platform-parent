package com.nowellpoint.api.service;

import com.nowellpoint.api.rest.domain.Organization;
import com.nowellpoint.api.rest.domain.Plan;

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
			Plan plan,
			String domain,  
			String firstName,
			String lastName,
			String email,
			String phone,
			String countryCode);
	
	public Organization createOrganization(
			Plan plan,
			String domain,  
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
	
	public Organization changePlan(
			String id, 
			String planId,
			String cardholderName, 
			String expirationMonth, 
			String expirationYear,
			String number, 
			String cvv);
	
	public Organization updateCreditCard(
			String id, 
			String cardholderName, 
			String expirationMonth, 
			String expirationYear, 
			String number, 
			String cvv);
	
	public Organization updateBillingAddress(String id, String street, String city, String stateCode, String postalCode, String countryCode);
	
	public Organization updateBillingContact(String id, String firstName, String lastName, String email, String phone);
	
	public void deleteOrganization(String id);
	
	public byte[] getInvoice(String id, String invoiceNumber);
}