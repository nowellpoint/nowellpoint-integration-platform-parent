package com.nowellpoint.api.resource;

import java.io.IOException;
import java.net.URI;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.Status;

import com.nowellpoint.api.service.IdentityProviderService;
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
	@Path("me")
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
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createAccount(Account account) {
		
		Account resource = identityProviderService.createAccount(account);
		
		URI uri = UriBuilder.fromUri(uriInfo.getBaseUri())
				.path(AccountResource.class)
				.path("{id}")
				.build(resource.getId());
		
		return Response.created(uri)
				.entity(resource)
				.build();
	}
	
	@GET
	@Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
	public Response getAccount(@PathParam("id") String id) {		
		
		Account resource = identityProviderService.getAccount(id);
		
		return Response.ok(resource)
				.type(MediaType.APPLICATION_JSON)
				.build();
	}
	
	@DELETE
	@Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
	public Response disableAccount(@PathParam("id") String id) {
		
		identityProviderService.disableAccount(id);
		
		return Response.noContent()
				.build();
	}
}