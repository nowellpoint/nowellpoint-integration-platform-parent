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
	public Organization createOrganization(String domain);
	public Organization updateOrganization(String id, String domain);
}