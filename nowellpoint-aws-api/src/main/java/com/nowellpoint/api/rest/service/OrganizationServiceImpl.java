package com.nowellpoint.api.rest.service;

import com.mongodb.client.model.Filters;
import com.nowellpoint.api.rest.domain.OrganizationOld;
import com.nowellpoint.api.service.OrganizationService;

public class OrganizationServiceImpl extends AbstractOrganizationService implements OrganizationService {
	
	@Override
	public OrganizationOld findById(String id) {
		return super.findById(id);
	}
	
	@Override
	public OrganizationOld findByDomain(String domain) {
		return super.query( Filters.eq( "domain", domain ));
	}

	@Override
	public OrganizationOld createOrganization(String domain) {
		OrganizationOld organizationOld = OrganizationOld.createOrganization(domain);
		super.create(organizationOld);
		return organizationOld;
	}

	@Override
	public OrganizationOld updateOrganization(OrganizationOld organizationOld) {
		super.update(organizationOld);
		return organizationOld;
	}
}