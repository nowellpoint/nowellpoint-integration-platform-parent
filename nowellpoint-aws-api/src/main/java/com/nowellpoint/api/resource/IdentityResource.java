package com.nowellpoint.api.resource;

import javax.inject.Inject;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import com.nowellpoint.api.model.domain.AccountProfile;
import com.nowellpoint.api.service.AccountProfileService;
import com.nowellpoint.util.Assert;

@Path("identity")
public class IdentityResource {
	
	@Inject
	private AccountProfileService accountProfileService;
	
	@Context 
	private SecurityContext securityContext;
	
	@GET
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getId(@PathParam("id") String id) {
		
		String subject = securityContext.getUserPrincipal().getName();
		
		if (Assert.isNotEqual(subject, id)) {
			throw new ForbiddenException("You are not authorized to access this resource");
		}
		
		AccountProfile accountProfile = accountProfileService.findById( id );
				
		return Response.ok(accountProfile)
				.build();
	}
}