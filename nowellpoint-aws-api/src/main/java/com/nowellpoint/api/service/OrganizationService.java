package com.nowellpoint.api.service;

import com.nowellpoint.api.rest.domain.OrganizationOld;

/**
 * 
 * 
 * @author jherson
 *
 * 
 */

public interface OrganizationService {

	public OrganizationOld findById(String id);
	
	public OrganizationOld findByDomain(String domain);
	
	public OrganizationOld createOrganization(String domain);
	
	public OrganizationOld updateOrganization(OrganizationOld organizationOld);
	
}