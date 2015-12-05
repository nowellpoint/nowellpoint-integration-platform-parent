package com.nowellpoint.aws.app.api;

import java.util.Base64;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.nowellpoint.aws.client.IdentityProviderClient;
import com.nowellpoint.aws.model.idp.GetTokenRequest;
import com.nowellpoint.aws.model.idp.GetTokenResponse;

@Path("/oauth/token")
public class TokenResource {
	
	@Context
	private HttpServletRequest servletRequest;
	
	private static IdentityProviderClient identityProviderClient = new IdentityProviderClient();
	
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response authenticate() {
		
		//
		// ensure the request has an Authorization header parameter
		//
		
		Optional<String> authorization = Optional.ofNullable(servletRequest.getHeader("Authorization"));
		
		if (! authorization.isPresent()) {
			return Response.status(Status.BAD_REQUEST)
					.entity("Invalid Request - Missing Authorization Header")
					.build();
		}
		
		//
		// parset the authorization token to get the base64 basic token
		//
		
		String basicToken = new String(Base64.getDecoder().decode(authorization.get().replace("Basic ", "")));
		
		//
		// ensure that the token has both a username and password parameter
		// 
		
		String[] params = basicToken.split(":");
		
		if (params.length != 2) {
			return Response.status(Status.BAD_REQUEST)
					.entity("Invalid Request - Missing username and/or password")
					.build();
		}
		
		//
		// execute the get token request for username and password
		//
		
		GetTokenRequest tokenRequest = new GetTokenRequest().withUsername(params[0])
				.withPassword(params[1]);
		
		params = null;
		
		GetTokenResponse tokenResponse = identityProviderClient.authenticate(tokenRequest);
		
		//
		// parse the returned entity
		//
		
		Object entity;
		if (tokenResponse.getStatusCode() == Status.OK.getStatusCode()) {
			entity = tokenResponse.getToken();
		} else {
			entity = tokenResponse.getErrorMessage();
		}
		
		//
		// build and return the response
		//
		
		return Response.status(tokenResponse.getStatusCode())
				.entity(entity)
				.type(MediaType.APPLICATION_JSON)
				.build();
	}
}