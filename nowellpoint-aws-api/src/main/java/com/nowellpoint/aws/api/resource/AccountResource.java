package com.nowellpoint.aws.api.resource;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.nowellpoint.aws.api.util.HttpServletRequestUtil;
import com.nowellpoint.aws.client.IdentityProviderClient;
import com.nowellpoint.aws.model.idp.GetAccountRequest;
import com.nowellpoint.aws.model.idp.GetAccountResponse;

@Path("/account")
public class AccountResource {
	
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
}