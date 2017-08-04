package com.nowellpoint.api.rest.impl;

import javax.inject.Inject;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriBuilder;

import com.nowellpoint.api.rest.IdentityResource;
import com.nowellpoint.api.rest.JobResource;
import com.nowellpoint.api.rest.OrganizationResource;
import com.nowellpoint.api.rest.SalesforceConnectorResource;
import com.nowellpoint.api.rest.domain.UserProfile;
import com.nowellpoint.api.rest.domain.Identity;
import com.nowellpoint.api.rest.domain.AbstractOrganization;
import com.nowellpoint.api.rest.domain.Resources;
import com.nowellpoint.api.service.OrganizationService;
import com.nowellpoint.api.service.UserProfileService;
import com.nowellpoint.util.Assert;
import com.nowellpoint.util.Properties;

public class IdentityResourceImpl implements IdentityResource {
	
	@Inject
	private UserProfileService userProfileService;
	
	@Inject
	private OrganizationService organizationService;
	
	@Context 
	private SecurityContext securityContext;
	
	@Override
	public Response getIdentity(String organizationId, String userId) {
		
		String subject = securityContext.getUserPrincipal().getName();
		
		if (Assert.isNotEqual(subject, userId)) {
			throw new ForbiddenException("You are not authorized to access this resource");
		}
		
		UserProfile userProfile = findUser(userId);
		
		AbstractOrganization organization = findOrganization(organizationId);
		
		String organizationHref = UriBuilder.fromUri(System.getProperty(Properties.API_HOSTNAME))
				.path(OrganizationResource.class)
				.build()
				.toString();
		
		String salesforceHref = UriBuilder.fromUri(System.getProperty(Properties.API_HOSTNAME))
				.path(SalesforceConnectorResource.class)
				.build()
				.toString();
		
		String jobsHref = UriBuilder.fromUri(System.getProperty(Properties.API_HOSTNAME))
				.path(JobResource.class)
				.build()
				.toString();
		
		Resources resources = Resources.builder()
				.organization(organizationHref)
				.salesforce(salesforceHref)
				.jobs(jobsHref)
				.build();
		
		Identity identity = Identity.builder()
				.id(userProfile.getId())
				.firstName(userProfile.getFirstName())
				.lastName(userProfile.getLastName())
				.name(userProfile.getName())
				.locale(userProfile.getLocale().getDisplayName())
				.timeZone(userProfile.getTimeZone().getID())
				.resources(resources)
				.meta(userProfile.getMeta())
				.address(userProfile.getAddress())
				.organization(organization)
				.build();
				
		return Response.ok(identity)
				.build();
	}
	
	private UserProfile findUser(String userId) {
		return userProfileService.findById(userId);
	}
	
	private AbstractOrganization findOrganization(String organizationId) {
		return organizationService.findById(organizationId);
	}
}