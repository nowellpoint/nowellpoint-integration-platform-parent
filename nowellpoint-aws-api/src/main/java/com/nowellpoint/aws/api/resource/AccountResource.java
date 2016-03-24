package com.nowellpoint.aws.api.resource;

import java.net.URI;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import com.nowellpoint.aws.api.dto.idp.AccountDTO;
import com.nowellpoint.aws.api.service.IdentityProviderService;

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
		
		AccountDTO resource = identityProviderService.getAccountBySubject(subject);
		
		return Response.ok(resource)
				.type(MediaType.APPLICATION_JSON)
				.build();
	}
	
	/**
	 * 
	 * @param resource
	 * @return
	 */
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response create(AccountDTO resource) {
				
		identityProviderService.createAccount(resource);
		
		URI uri = UriBuilder.fromUri(uriInfo.getBaseUri())
				.path(AccountResource.class)
				.path("/{id}")
				.build(resource);
		
		return Response.created(uri).build();	
	}
}