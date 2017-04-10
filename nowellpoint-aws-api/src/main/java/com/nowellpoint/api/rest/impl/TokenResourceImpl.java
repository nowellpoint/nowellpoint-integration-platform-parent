package com.nowellpoint.api.rest.impl;

import java.net.URI;
import java.util.Base64;
import java.util.HashSet;
import java.util.Set;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import com.nowellpoint.api.exception.AuthenticationException;
import com.nowellpoint.api.rest.IdentityResource;
import com.nowellpoint.api.rest.TokenResource;
import com.nowellpoint.api.rest.domain.AccountProfile;
import com.nowellpoint.api.rest.domain.Token;
import com.nowellpoint.api.rest.service.AccountProfileServiceImpl;
import com.nowellpoint.api.service.IdentityProviderService;
import com.nowellpoint.util.Properties;
import com.stormpath.sdk.api.ApiKey;
import com.stormpath.sdk.api.ApiKeys;
import com.stormpath.sdk.oauth.OAuthGrantRequestAuthenticationResult;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class TokenResourceImpl implements TokenResource {
	
	private static final String CLIENT_CREDENTIALS = "client_credentials";
	private static final String PASSWORD = "password";
	
	@Inject
	private IdentityProviderService identityProviderService;
	
	@Inject
	private AccountProfileServiceImpl accountProfileServiceImpl;
	
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
		
		OAuthGrantRequestAuthenticationResult result = null;
		
		//
		// call the identity service provider to authenticate
		//
		
		if (CLIENT_CREDENTIALS.equals(grantType)) {
			ApiKey apiKey = ApiKeys.builder()
					.setId(params[0])
					.setSecret(params[1])
					.build();
			
			result = identityProviderService.authenticate(apiKey);
		} else if (PASSWORD.equals(grantType)) {
			result = identityProviderService.authenticate(params[0], params[1]);
		} else {
			throw new AuthenticationException("invalid_grant", "Please provide a valid grant_type, supported types are : client_credentials, password, refresh_token.");
		}

		//
		// clear params
		//
		
		params = null;
		
		//
		// lookup account profile
		//
		
		AccountProfile accountProfile = accountProfileServiceImpl.findByAccountHref(result.getAccessToken().getAccount().getHref());

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
	
	private Token createToken(OAuthGrantRequestAuthenticationResult result, String subject) {
		
		Jws<Claims> claims = Jwts.parser()
				.setSigningKey(Base64.getUrlEncoder().encodeToString(System.getProperty(Properties.STORMPATH_API_KEY_SECRET).getBytes()))
				.parseClaimsJws(result.getAccessToken().getJwt()); 
		
		Set<String> groups = new HashSet<String>();
		result.getAccessToken().getAccount().getGroups().forEach(g -> 
			groups.add(g.getName())
        );
		
		String jwt = Jwts.builder()
        		.setId(claims.getBody().getId())
        		.setHeaderParam("typ", "JWT")
        		.setIssuer(claims.getBody().getIssuer())
        		.setAudience(uriInfo.getBaseUri().toString())
        		.setSubject(subject)
        		.setIssuedAt(claims.getBody().getIssuedAt())
        		.setExpiration(claims.getBody().getExpiration())
        		.signWith(SignatureAlgorithm.HS512, Base64.getUrlEncoder().encodeToString(System.getProperty(Properties.STORMPATH_API_KEY_SECRET).getBytes()))
        		.claim("scope", groups.toArray(new String[groups.size()]))
        		.compact();	 
				
		URI uri = UriBuilder.fromUri(uriInfo.getBaseUri())
				.path(IdentityResource.class)
				.path("/{id}")
				.build(subject);
		
		Token token = new Token();
		token.setEnvironmentUrl(uriInfo.getBaseUri().toString());
		token.setId(uri.toString());
		token.setAccessToken(jwt);
		token.setExpiresIn(result.getExpiresIn());
		token.setRefreshToken(result.getRefreshTokenString());
		token.setTokenType(result.getTokenType());
        
        return token;
	}
}