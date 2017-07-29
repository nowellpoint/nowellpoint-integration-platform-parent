package com.nowellpoint.api.rest.service;

import java.time.Instant;
import java.util.Date;

import com.mongodb.client.model.Filters;
import com.nowellpoint.api.rest.domain.Organization;
import com.nowellpoint.api.rest.domain.UserInfo;
import com.nowellpoint.api.service.OrganizationService;
import com.nowellpoint.api.util.UserContext;

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
		
		UserInfo userInfo = UserInfo.of(UserContext.getPrincipal().getName());
		
		Date now = Date.from(Instant.now());

		Organization organization = Organization.builder()
				.domain(domain)
				.createdBy(userInfo)
				.createdOn(now)
				.lastUpdatedBy(userInfo)
				.lastUpdatedOn(now)
				.build();
		
		super.create(organization);
		
		return organization;
	}

	@Override
	public Organization updateOrganization(String id, String domain) {
		Organization original = findById(id);
		
		Organization organization = Organization.builder()
				.from(original)
				.domain(domain)
				.build();
		
		super.update(organization);
		
		return organization;
	}
}