package com.nowellpoint.api.service;

import com.nowellpoint.api.rest.domain.Organization;
import com.nowellpoint.api.rest.domain.Subscription;

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
	public Organization createOrganization(String domain, Subscription subscription);
	public Organization updateOrganization(String id, String domain);
	public Organization updateSubscription(String id, String planId, String cardholderName, String cardType, String expirationMonth, String expirationYear, String number, Boolean primary);
	public Organization addCreditCard(String id, String cardholderName, String cardType, String expirationMonth, String expirationYear, String number, Boolean primary);
}