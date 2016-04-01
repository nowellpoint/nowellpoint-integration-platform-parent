package com.nowellpoint.aws.api.resource;

import java.io.IOException;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.Status;

import com.nowellpoint.aws.api.service.IdentityProviderService;
import com.nowellpoint.aws.idp.model.Account;

@Path("/account")
public class AccountResource {
	
	@Inject
	private IdentityProviderService identityProviderService;
	
	@Context
	private UriInfo uriInfo;
	
	@Context 
	private SecurityContext securityContext;
	
	/**
	 * 
	 * @return
	 */
	
	@GET
    @Produces(MediaType.APPLICATION_JSON)
	public Response getAccount() {		
		String subject = securityContext.getUserPrincipal().getName();
		
		Account resource;
		try {
			resource = identityProviderService.getAccountBySubject(subject);
		} catch (IOException e) {
			throw new WebApplicationException(e, Status.INTERNAL_SERVER_ERROR);
		}
		
		return Response.ok(resource)
				.type(MediaType.APPLICATION_JSON)
				.build();
	}
}