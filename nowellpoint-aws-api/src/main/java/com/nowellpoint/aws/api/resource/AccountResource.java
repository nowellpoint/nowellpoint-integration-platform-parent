package com.nowellpoint.aws.api.resource;

import java.net.URI;
import java.time.Instant;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nowellpoint.aws.api.util.HttpServletRequestUtil;
import com.nowellpoint.aws.client.IdentityProviderClient;
import com.nowellpoint.aws.model.Configuration;
import com.nowellpoint.aws.model.DynamoDBMapperProvider;
import com.nowellpoint.aws.model.Event;
import com.nowellpoint.aws.model.idp.Account;
import com.nowellpoint.aws.model.idp.GetAccountRequest;
import com.nowellpoint.aws.model.idp.GetAccountResponse;

@Path("/account")
public class AccountResource {
	
	@Context
	private UriInfo uriInfo;
	
	@Context
	private HttpServletRequest servletRequest;
	
	private static final IdentityProviderClient identityProviderClient = new IdentityProviderClient();

	@GET
    @Produces(MediaType.APPLICATION_JSON)
	public Response getAccount() {
		
		//
		// get the bearer token from the HttpServletRequest
		//
		
		String bearerToken = HttpServletRequestUtil.getBearerToken(servletRequest);
		
		//
		// throw exception if the bearerToken is null or missing 
		//
		
		if (bearerToken == null || bearerToken.trim().isEmpty()) {
			throw new BadRequestException("Missing bearer token");
		}
		
		//
		// build the GetAccountRequest
		//
		
		GetAccountRequest getAccountRequest = new GetAccountRequest().withAccessToken(bearerToken);
		
		//
		// excute the GetAccountRequest
		//
		
		GetAccountResponse getAccountResponse = identityProviderClient.account(getAccountRequest);
		
		//
		// build and return the response
		//
		
		return Response.status(getAccountResponse.getStatusCode())
				.entity((getAccountResponse.getStatusCode() != 200 ? getAccountResponse.getErrorMessage() : getAccountResponse.getAccount()))
				.type(MediaType.APPLICATION_JSON)
				.build();
	}
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response create(Account account) {
		
		//
		//
		//
				
		String payload = null;
		try {			
			payload = new ObjectMapper().writeValueAsString(account);
		} catch (JsonProcessingException e) {
			throw new WebApplicationException(e);
		}
		
		//
		//
		//
		
		Event event = new Event().withEventDate(Date.from(Instant.now()))
				.withEventStatus(Event.EventStatus.NEW)
				.withType(Account.class.getName())
				.withOrganizationId(Configuration.getDefaultOrganizationId())
				.withUserId(Configuration.getDefaultUserId())
				.withPayload(payload);
		
		DynamoDBMapperProvider.getDynamoDBMapper().save(event);
		
		//
		//
		//
		
		URI uri = UriBuilder.fromUri(uriInfo.getBaseUri())
				.path(AccountResource.class)
				.path("/{id}")
				.build(event.getId());
		
		//
		//
		//
		
		return Response.created(uri).build();	
	}
}