package com.nowellpoint.api.rest.impl;

import javax.inject.Inject;
import javax.ws.rs.core.Response;

import com.nowellpoint.api.rest.OrganizationResource;
import com.nowellpoint.api.rest.domain.AbstractOrganization;
import com.nowellpoint.api.service.OrganizationService;

public class OrganizationResourceImpl implements OrganizationResource {
	
	@Inject
	private OrganizationService organizationService;

	@Override
	public Response getOrganization(String id) {
		AbstractOrganization organization = organizationService.findById(id);
		
		return Response.ok(organization)
				.build();
	}
}