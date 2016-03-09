package com.nowellpoint.aws.api.resource;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import com.nowellpoint.aws.api.event.LoggedInEvent;
import com.nowellpoint.aws.api.service.IdentityProviderService;
import com.nowellpoint.aws.api.util.HttpServletRequestUtil;
import com.nowellpoint.aws.data.CacheManager;
import com.nowellpoint.aws.idp.client.IdentityProviderClient;
import com.nowellpoint.aws.idp.model.RevokeTokenRequest;
import com.nowellpoint.aws.idp.model.RevokeTokenResponse;
import com.nowellpoint.aws.idp.model.Token;
import com.nowellpoint.aws.idp.model.VerifyTokenRequest;
import com.nowellpoint.aws.idp.model.VerifyTokenResponse;
import com.nowellpoint.aws.model.admin.Properties;

@Path("/oauth")
public class TokenResource {
	
	@Inject
	private CacheManager cacheManager;
	
	@Inject
	private IdentityProviderService identityProviderService;
	
	@Inject
	private Event<LoggedInEvent> loggedInEvent;
	
	@Context
	private UriInfo uriInfo;
	
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
			throw new WebApplicationException("Invalid Request - Missing username and/or password", Status.BAD_REQUEST);
		}
		
		//
		// execute the get token request for username and password
		//
		
		Token token = identityProviderService.authenticate(params[0], params[1]);
		
		//
		//
		//
		
		params = null;
			
		//
		// fire the logged in event
		//

		loggedInEvent.fire(new LoggedInEvent(token, uriInfo.getBaseUri()));
		
		//
		// build and return the response
		//
		
		return Response.ok()
				.entity(token)
				.type(MediaType.APPLICATION_JSON)
				.build();
	
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
		
		VerifyTokenRequest verifyTokenRequest = new VerifyTokenRequest()
				.withApiEndpoint(System.getProperty(Properties.STORMPATH_API_ENDPOINT))
				.withApiKeyId(System.getProperty(Properties.STORMPATH_API_KEY_ID))
				.withApiKeySecret(System.getProperty(Properties.STORMPATH_API_KEY_SECRET))
				.withApplicationId(System.getProperty(Properties.STORMPATH_APPLICATION_ID))
				.withAccessToken(bearerToken);
		
		//
		// execute the token verification request
		
		VerifyTokenResponse verifyTokenResponse = identityProviderClient.token(verifyTokenRequest);
		
		//
		// build and return the response
		//
		
		if (verifyTokenResponse.getStatusCode() != 200) {
			throw new WebApplicationException( verifyTokenResponse.getErrorMessage(), Status.BAD_REQUEST );
		}
		
		return Response.status(verifyTokenResponse.getStatusCode())
					.entity(verifyTokenResponse.getAuthToken())
					.type(MediaType.APPLICATION_JSON)
					.build();
	}
	
	@DELETE
	@Path("/token")
	public Response revoke() {
		
		//
		// get the bearer token from the header
		//
		
		String bearerToken = HttpServletRequestUtil.getBearerToken(servletRequest);
		
		//
		// build the revoke token request
		//
		
		RevokeTokenRequest revokeTokenRequest = new RevokeTokenRequest()
				.withApiEndpoint(System.getProperty(Properties.STORMPATH_API_ENDPOINT))
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
		
		cacheManager.del(bearerToken);
		
		//
		// build and return the response
		//

		if (revokeTokenResponse.getStatusCode() != 204) {
			throw new WebApplicationException( revokeTokenResponse.getErrorMessage(), Status.BAD_REQUEST );
		}
		
		return Response.noContent().build();
	}
}