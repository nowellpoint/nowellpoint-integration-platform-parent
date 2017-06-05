package com.nowellpoint.api.rest.impl;

import javax.inject.Inject;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

import com.nowellpoint.api.rest.IdentityResource;
import com.nowellpoint.api.rest.domain.AccountProfile;
import com.nowellpoint.api.rest.domain.Identity;
import com.nowellpoint.api.service.AccountProfileService;
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
		
		Identity identity = Identity.of(accountProfile, uriInfo);
				
		return Response.ok(identity)
				.build();
	}
}