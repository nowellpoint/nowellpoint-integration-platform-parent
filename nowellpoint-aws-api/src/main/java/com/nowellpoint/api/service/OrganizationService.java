package com.nowellpoint.api.service;

import com.nowellpoint.api.rest.domain.Organization;
import com.nowellpoint.api.rest.domain.Registration;

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
	public Organization createOrganization(Registration registration);
	public Organization updateOrganization(String id, String domain);
	public Organization addCreditCard(String id, String cardholderName, String cardType, String expirationMonth, String expirationYear, String number, Boolean primary);
}