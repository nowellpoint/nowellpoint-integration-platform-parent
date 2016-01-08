package com.nowellpoint.aws.api.resource;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.nowellpoint.aws.api.util.HttpServletRequestUtil;
import com.nowellpoint.aws.client.SalesforceClient;
import com.nowellpoint.aws.model.sforce.GetAuthorizationRequest;
import com.nowellpoint.aws.model.sforce.GetAuthorizationResponse;
import com.nowellpoint.aws.model.sforce.GetIdentityRequest;
import com.nowellpoint.aws.model.sforce.GetIdentityResponse;
import com.nowellpoint.aws.provider.ConfigurationProvider;

@Path("/salesforce")
public class SalesforceResource {
	
	@Context
	private HttpServletRequest servletRequest;
	
	private static SalesforceClient salesforceClient = new SalesforceClient();

	@GET
	@Path("/token")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getToken(@QueryParam(value="code") String code) {
		
		//
		// build the get authorization request
		//
		
		GetAuthorizationRequest authorizationRequest = new GetAuthorizationRequest().withTokenUri(ConfigurationProvider.getSalesforceTokenUri())
				.withClientId(ConfigurationProvider.getSalesforceClientId())
				.withClientSecret(ConfigurationProvider.getSalesforceClientSecret())
				.withRedirectUri(ConfigurationProvider.getRedirectUri())
				.withCode(code);
		
		//
		// execute the get authorization request
		//
		
		GetAuthorizationResponse authorizationResponse = salesforceClient.authorize(authorizationRequest);
		
		//
		// throw WebApplicationException if the response is not ok
		//
		
		if (authorizationResponse.getStatusCode() >= 400) {
			throw new WebApplicationException(authorizationResponse.getErrorMessage(), authorizationResponse.getStatusCode());
		}
		
		//
		// return the result
		//
		
		return Response.status(authorizationResponse.getStatusCode())
				.entity(authorizationResponse.getToken())
				.type(MediaType.APPLICATION_JSON)
				.build();
	}	
	
	@GET
	@Path("/identity")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getIdentity(@QueryParam("id") String id) {
		
		//
		// get the bearer token from the header
		//
		
		String bearerToken = HttpServletRequestUtil.getBearerToken(servletRequest);
		
		//
		// build the get identity request
		//
		
		GetIdentityRequest identityRequest = new GetIdentityRequest().withAccessToken(bearerToken).withId(id);
		
		//
		// execute the identity request
		//
		
		GetIdentityResponse identityResponse = salesforceClient.getIdentity(identityRequest);
		
		//
		// throw WebApplicationException if the response is not ok
		//
		
		if (identityResponse.getStatusCode() >= 400) {
			throw new WebApplicationException(identityResponse.getErrorMessage(), identityResponse.getStatusCode());
		}
		
		//
		// return the result
		//
		
		return Response.status(identityResponse.getStatusCode())
				.entity(identityResponse.getIdentity())
				.type(MediaType.APPLICATION_JSON)
				.build();
	}
}