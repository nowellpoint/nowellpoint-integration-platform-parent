package com.nowellpoint.api.rest.impl;

import javax.inject.Inject;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import com.nowellpoint.api.rest.AccountProfileService;
import com.nowellpoint.api.rest.IdentityResource;
import com.nowellpoint.api.rest.JobSpecificationResource;
import com.nowellpoint.api.rest.SalesforceConnectorResource;
import com.nowellpoint.api.rest.domain.AccountProfile;
import com.nowellpoint.api.rest.domain.Identity;
import com.nowellpoint.api.rest.domain.Resources;
import com.nowellpoint.util.Assert;

public class IdentityResourceImpl implements IdentityResource {
	
	@Inject
	private AccountProfileService accountProfileService;
	
	@Context 
	private SecurityContext securityContext;
	
	@Context
	private UriInfo uriInfo;
	
	@Override
	public Response getId(String id) {
		
		String subject = securityContext.getUserPrincipal().getName();
		
		if (Assert.isNotEqual(subject, id)) {
			throw new ForbiddenException("You are not authorized to access this resource");
		}
		
		AccountProfile accountProfile = accountProfileService.findById( id );
		
		Resources resources = new Resources();
		resources.setSalesforce(UriBuilder.fromUri(uriInfo.getBaseUri()).path(SalesforceConnectorResource.class).build().toString());
		resources.setScheduledJobs(UriBuilder.fromUri(uriInfo.getBaseUri()).path(JobSpecificationResource.class).build().toString());
		
		Identity identity = new Identity();
		identity.setId(accountProfile.getId());
		identity.setFirstName(accountProfile.getFirstName());
		identity.setLastName(accountProfile.getLastName());
		identity.setName(accountProfile.getName());
		identity.setPlanName(accountProfile.getSubscription().getPlanName());
		identity.setAddress(accountProfile.getAddress());
		identity.setLanguageSidKey(accountProfile.getLanguageSidKey());
		identity.setLocaleSidKey(accountProfile.getLocaleSidKey());
		identity.setTimeZoneSidKey(accountProfile.getTimeZoneSidKey());
		identity.setMeta(accountProfile.getMeta());
		identity.setResources(resources);
				
		return Response.ok(identity)
				.build();
	}
}