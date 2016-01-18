package com.nowellpoint.aws.api.resource;

import static com.nowellpoint.aws.api.data.CacheManager.getCache;

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

import com.nowellpoint.aws.api.util.HttpServletRequestUtil;
import com.nowellpoint.aws.idp.client.IdentityProviderClient;
import com.nowellpoint.aws.idp.model.GetTokenRequest;
import com.nowellpoint.aws.idp.model.GetTokenResponse;
import com.nowellpoint.aws.idp.model.RevokeTokenRequest;
import com.nowellpoint.aws.idp.model.RevokeTokenResponse;
import com.nowellpoint.aws.idp.model.VerifyTokenRequest;
import com.nowellpoint.aws.idp.model.VerifyTokenResponse;
import com.nowellpoint.aws.model.admin.Properties;

@Path("/oauth")
public class TokenResource {
	
	@Context
	private HttpServletRequest servletRequest;
	
	private static final IdentityProviderClient identityProviderClient = new IdentityProviderClient();
	
	@POST
	@Path("/token")
	@Produces(MediaType.APPLICATION_JSON)
	public Response authenticate() {
		
		//
		// get basic token from the header
		//
		
		String basicToken = HttpServletRequestUtil.getBasicToken(servletRequest);
		
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
		
		GetTokenRequest tokenRequest = new GetTokenRequest().withApiEndpoint(System.getProperty(Properties.STORMPATH_API_ENDPOINT))
				.withApiKeyId(System.getProperty(Properties.STORMPATH_API_KEY_ID))
				.withApiKeySecret(System.getProperty(Properties.STORMPATH_API_KEY_SECRET))
				.withApplicationId(System.getProperty(Properties.STORMPATH_APPLICATION_ID))
				.withUsername(params[0])
				.withPassword(params[1]);
		
		params = null;
		
		GetTokenResponse tokenResponse = identityProviderClient.token(tokenRequest);
		
		//
		// build and return the response
		//
		
		Response response;
		
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
		
		//
		// get the bearer token from the header
		//
		
		String bearerToken = HttpServletRequestUtil.getBearerToken(servletRequest);
		
		//
		// build the token verification request
		//
		
		VerifyTokenRequest verifyTokenRequest = new VerifyTokenRequest().withAccessToken(bearerToken);
		
		//
		// execute the token verification request
		
		VerifyTokenResponse verifyTokenResponse = identityProviderClient.token(verifyTokenRequest);
		
		//
		// build and return the response
		//
		
		Response response;
		
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
		
		//
		// get the bearer token from the header
		//
		
		String bearerToken = HttpServletRequestUtil.getBearerToken(servletRequest);
		
		//
		// build the revoke token request
		//
		
		RevokeTokenRequest revokeTokenRequest = new RevokeTokenRequest().withApiEndpoint(System.getProperty(Properties.STORMPATH_API_ENDPOINT))
				.withApiKeyId(System.getProperty(Properties.STORMPATH_API_KEY_ID))
				.withApiKeySecret(System.getProperty(Properties.STORMPATH_API_KEY_SECRET))
				.withAccessToken(bearerToken);
		
		//
		// execute the revoke token request
		//
		
		RevokeTokenResponse revokeTokenResponse = identityProviderClient.token(revokeTokenRequest);
		
		//
		// remove the account from the cache
		//
		
		getCache().del(bearerToken.getBytes());
		
		//
		// build and return the response
		//
		
		Response response;
		
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