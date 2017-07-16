package com.nowellpoint.api.rest.impl;

import java.net.URI;
import java.util.Base64;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import com.nowellpoint.api.idp.TokenResponse;
import com.nowellpoint.api.idp.TokenVerificationResponse;
import com.nowellpoint.api.rest.IdentityResource;
import com.nowellpoint.api.rest.TokenResource;
import com.nowellpoint.api.rest.domain.AccountProfile;
import com.nowellpoint.api.rest.domain.AuthenticationException;
import com.nowellpoint.api.rest.domain.Token;
import com.nowellpoint.api.service.AccountProfileService;
import com.nowellpoint.api.service.IdentityProviderService;

public class TokenResourceImpl implements TokenResource {
	
	private static final String CLIENT_CREDENTIALS = "client_credentials";
	private static final String PASSWORD = "password";
	
	@Inject
	private IdentityProviderService identityProviderService;
	
	@Inject
	private AccountProfileService accountProfileService;
	
	@Inject
	private Event<Token> loggedInEvent;
	
	@Context
	private UriInfo uriInfo;
	
	@Override
	public Response authenticate(String authorization, String grantType) {

		//
		// ensure that the authorization header has a basic token
		//
		
		if (! authorization.startsWith("Basic ")) {
			throw new AuthenticationException("invalid_request", "Invalid header Authorization header must be of type Basic scheme");
		}
		
		//
		// parse the authorization token to get the base64 basic token
		//
		
		String basicToken = new String(Base64.getDecoder().decode(authorization.replace("Basic ", "")));
		
		//
		// return the basic token
		//

		String[] params = basicToken.split(":");
		
		//
		// throw exception if params are not equal to 2
		//
		
		if (params.length != 2) {
			throw new AuthenticationException("invalid_request", "Parameters missing from the request, valid parameters are Base64 encoded: username:password or client_id:client_secret");
		}
		
		TokenResponse result = null;
		
		//
		// call the identity service provider to authenticate
		//
		
		if (CLIENT_CREDENTIALS.equals(grantType)) {
			throw new AuthenticationException("invalid_grant", "Invalid Grant Type: client_credentials is not supported.");
		} else if (PASSWORD.equals(grantType)) {
			result = identityProviderService.authenticate(params[0], params[1]);
		} else {
			throw new AuthenticationException("invalid_grant", String.format("Invalid Grant Type: %d. Please provide a valid grant_type, supported types are : client_credentials, password, refresh_token.", grantType));
		}

		//
		// clear params
		//
		
		params = null;
		
		//
		// 
		//
		
		TokenVerificationResponse verification = identityProviderService.verify(result.getAccessToken());
		
		//
		// lookup account profile
		//
		
		AccountProfile accountProfile = accountProfileService.findByIdpId(verification.getUserId());

		//
		// create the token
		//
		
        Token token = createToken(result, accountProfile.getId());

		//
		// fire event for handling login functions
		//
		
		loggedInEvent.fire(token);

		//
		// build cache control
		//
		
		CacheControl cacheControl = new CacheControl();
		cacheControl.setNoStore(Boolean.TRUE);
		
		//
		// return the Response with the token
		//
		
		return Response.ok()
				.cacheControl(cacheControl)
				.header("Pragma", "no-cache")
				.entity(token)
				.type(MediaType.APPLICATION_JSON)
				.build();	
	}
	
	@Override
	public Response revokeToken(String authorization) {
		
		//
		// ensure that the authorization header has a bearer token
		//
		
		if (! authorization.startsWith("Bearer ")) {
			throw new BadRequestException("Invalid authorization. Should be of type Bearer");
		}
		
		//
		// parse the authorization header to get the bearer token
		//
		
		String bearerToken = new String(authorization.replace("Bearer ", ""));
		
		//
		// call the identity provider service to revoke the token
		//
		
		identityProviderService.revokeToken(bearerToken);
		
		//
		// return Response
		//
		
		return Response.noContent().build();
	}
	
	/**
	 * 
	 * @param result
	 * @param issuer
	 * @param subject
	 * @return authentication token
	 */
	
	private Token createToken(TokenResponse tokenResponse, String subject) {
				
		URI uri = UriBuilder.fromUri(uriInfo.getBaseUri())
				.path(IdentityResource.class)
				.path("/{id}")
				.build(subject);
		
		Token token = new Token();
		token.setEnvironmentUrl(uriInfo.getBaseUri().toString());
		token.setId(uri.toString());
		token.setAccessToken(tokenResponse.getAccessToken());
		token.setExpiresIn(tokenResponse.getExpiresIn());
		token.setRefreshToken(tokenResponse.getRefreshToken());
		token.setTokenType(tokenResponse.getTokenType());
        
        return token;
	}
}