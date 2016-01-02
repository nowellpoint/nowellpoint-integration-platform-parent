package com.nowellpoint.aws.api.resource;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.nowellpoint.aws.client.SalesforceClient;
import com.nowellpoint.aws.model.sforce.GetAuthorizationRequest;
import com.nowellpoint.aws.model.sforce.GetAuthorizationResponse;
import com.nowellpoint.aws.model.sforce.GetIdentityRequest;
import com.nowellpoint.aws.model.sforce.GetIdentityResponse;

@Path("/salesforce")
public class SalesforceResource {
	
	@Context
	private HttpServletRequest servletRequest;
	
	private static SalesforceClient salesforceClient = new SalesforceClient();

	@GET
	@Path("/token/{code}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getToken(@PathParam(value="code") String code) {
		
		GetAuthorizationRequest authorizationRequest = new GetAuthorizationRequest().withCode(code);
		GetAuthorizationResponse authorizationResponse = salesforceClient.authorize(authorizationRequest);
		
		return Response.status(authorizationResponse.getStatusCode())
				.entity(authorizationResponse.getToken())
				.type(MediaType.APPLICATION_JSON)
				.build();
	}	
	
	@GET
	@Path("/identity")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getIdentity(@QueryParam("id") String id, @QueryParam("access_token") String accessToken) {
		
		GetIdentityRequest identityRequest = new GetIdentityRequest().withAccessToken(accessToken).withId(id);
		GetIdentityResponse identityResponse = salesforceClient.getIdentity(identityRequest);
		
		System.out.println("response: " + identityResponse.getStatusCode());
		
		return Response.status(identityResponse.getStatusCode())
				.entity(identityResponse.getIdentity())
				.type(MediaType.APPLICATION_JSON)
				.build();
	}
}