package com.nowellpoint.api.rest.service;

import com.mongodb.client.model.Filters;
import com.nowellpoint.api.rest.domain.Organization;
import com.nowellpoint.api.service.OrganizationService;

public class OrganizationServiceImpl extends AbstractOrganizationService implements OrganizationService {
	
	@Override
	public Organization findById(String id) {
		return super.findById(id);
	}
	
	@Override
	public Organization findByDomain(String domain) {
		return super.query( Filters.eq( "domain", domain ));
	}

	@Override
	public Organization createOrganization(String domain) {
		Organization organization = Organization.createOrganization(domain);
		super.create(organization);
		return organization;
	}

	@Override
	public Organization updateOrganization(Organization organization) {
		super.update(organization);
		return organization;
	}
}