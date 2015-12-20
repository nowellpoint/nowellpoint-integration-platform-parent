package com.nowellpoint.aws.api.resource;

import java.util.Base64;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
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
import com.nowellpoint.aws.model.idp.RevokeTokenRequest;
import com.nowellpoint.aws.model.idp.RevokeTokenResponse;
import com.nowellpoint.aws.model.idp.VerifyTokenRequest;
import com.nowellpoint.aws.model.idp.VerifyTokenResponse;

@Path("/oauth")
public class TokenResource {
	
	@Context
	private HttpServletRequest servletRequest;
	
	private static IdentityProviderClient identityProviderClient = new IdentityProviderClient();
	
	@POST
	@Path("/token")
	@Produces(MediaType.APPLICATION_JSON)
	public Response authenticate() {
		
		Response response;
		
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
		// parse the authorization token to get the base64 basic token
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
		// build and return the response
		//
		
		if (tokenResponse.getStatusCode() != 200) {
			response = Response.status(tokenResponse.getStatusCode())
					.entity(tokenResponse.getErrorMessage())
					.type(MediaType.APPLICATION_JSON)
					.build();
		} else {
			response = Response.status(tokenResponse.getStatusCode())
					.entity(tokenResponse.getToken())
					.type(MediaType.APPLICATION_JSON)
					.build();
		}
		
		return response;
	}
	
	@GET
	@Path("/token")
	@Produces(MediaType.APPLICATION_JSON)
	public Response verify() {
		
		Response response;
		
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
		// parse the authorization header to get the bearer token
		//
		
		String bearerToken = new String(authorization.get().replace("Bearer ", ""));
		
		VerifyTokenRequest verifyTokenRequest = new VerifyTokenRequest().withAccessToken(bearerToken);
		
		VerifyTokenResponse verifyTokenResponse = identityProviderClient.verify(verifyTokenRequest);
		
		//
		// build and return the response
		//
		
		if (verifyTokenResponse.getStatusCode() != 200) {
			response = Response.status(verifyTokenResponse.getStatusCode())
					.entity(verifyTokenResponse.getErrorMessage())
					.type(MediaType.APPLICATION_JSON)
					.build();
		} else {
			response = Response.status(verifyTokenResponse.getStatusCode())
					.entity(verifyTokenResponse.getAuthToken())
					.type(MediaType.APPLICATION_JSON)
					.build();
		}
		
		return response;
	}
	
	
	@DELETE
	@Path("/token")
	@Produces(MediaType.APPLICATION_JSON)
	public Response revoke() {
		
		Response response;
		
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
		// parse the authorization header to get the bearer token
		//
		
		String bearerToken = new String(authorization.get().replace("Bearer ", ""));
		
		RevokeTokenRequest revokeTokenRequest = new RevokeTokenRequest().withAccessToken(bearerToken);
		
		RevokeTokenResponse revokeTokenResponse = identityProviderClient.revoke(revokeTokenRequest);
		
		//
		// build and return the response
		//
		
		if (revokeTokenResponse.getStatusCode() != 200) {
			response = Response.status(revokeTokenResponse.getStatusCode())
					.entity(revokeTokenResponse.getErrorMessage())
					.type(MediaType.APPLICATION_JSON)
					.build();
		} else {
			response = Response.status(revokeTokenResponse.getStatusCode())
					.type(MediaType.APPLICATION_JSON)
					.build();
		}
		
		return response;
	}
}