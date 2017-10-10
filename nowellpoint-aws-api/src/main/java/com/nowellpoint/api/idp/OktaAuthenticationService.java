package com.nowellpoint.api.idp;

import org.jboss.logging.Logger;

import com.nowellpoint.api.idp.model.AuthenticationException;
import com.nowellpoint.api.idp.model.Error;
import com.nowellpoint.api.idp.model.Keys;
import com.nowellpoint.api.idp.model.TokenResponse;
import com.nowellpoint.api.idp.model.TokenVerificationResponse;
import com.nowellpoint.api.service.AuthenticationService;
import com.nowellpoint.http.HttpResponse;
import com.nowellpoint.http.MediaType;
import com.nowellpoint.http.RestResource;
import com.nowellpoint.http.Status;
import com.nowellpoint.util.Properties;

public class OktaAuthenticationService implements AuthenticationService {
	
	/**
	 * 
	 */
	
	private static final Logger LOG = Logger.getLogger(OktaAuthenticationService.class);
	
	private static final String VERSION = "v1";
	private static final String TOKEN = "token";
	private static final String REVOKE = "revoke";
	private static final String INTROSPECT = "introspect";
	private static final String KEYS = "keys";
	private static final String GRANT_TYPE = "grant_type";
	//private static final String CLIENT_CREDENTIALS = "client_credentials";
	private static final String TOKEN_TYPE_HINT = "token_type_hint";
	private static final String ACCESS_TOKEN = "access_token";
	private static final String REFRESH_TOKEN = "refresh_token";
	private static final String SCOPE = "scope";
	private static final String OFFLINE_ACCESS = "offline_access";
	private static final String USERNAME = "username";
	private static final String PASSWORD = "password";
	
	/**
	 * 
	 * @param apiKey
	 * @return
	 */
	
	@Override
	public TokenResponse authenticate(String apiKey) {
		throw new AuthenticationException("invalid_grant", "Invalid Grant Type: client_credentials is not supported.");
	}
	
	/**
	 * 
	 * @param username
	 * @param password
	 * @return
	 */
	
	@Override
	public TokenResponse authenticate(String username, String password) {	
		HttpResponse httpResponse = RestResource.post(System.getProperty(Properties.OKTA_AUTHORIZATION_SERVER))
				.basicAuthorization(System.getProperty(Properties.OKTA_CLIENT_ID), System.getProperty(Properties.OKTA_CLIENT_SECRET))
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.path(VERSION)
				.path(TOKEN)
				.parameter(GRANT_TYPE, PASSWORD)
				.parameter(USERNAME, username)
				.parameter(PASSWORD, password)
				.parameter(SCOPE, OFFLINE_ACCESS)
				.execute();
		
		TokenResponse response = null;
		
		if (httpResponse.getStatusCode() == Status.OK) {
			response = httpResponse.getEntity(TokenResponse.class);
		} else {
			Error error = httpResponse.getEntity(Error.class);	
			LOG.debug(error.getError());
			LOG.debug(error.getErrorDescription());
			throw new AuthenticationException(error);
		}
		
		return response;
	}
	
	@Override
	public Keys getKeys() {
		HttpResponse httpResponse = RestResource.get(System.getProperty(Properties.OKTA_AUTHORIZATION_SERVER))
				.basicAuthorization(System.getProperty(Properties.OKTA_CLIENT_ID), System.getProperty(Properties.OKTA_CLIENT_SECRET))
				.accept(MediaType.APPLICATION_JSON)
				.path(VERSION)
				.path(KEYS)
				.execute();
		
		Keys keys = null;
		
		if (httpResponse.getStatusCode() == Status.OK) {
			keys = httpResponse.getEntity(Keys.class);
		} else {
			Error error = httpResponse.getEntity(Error.class);	
			throw new AuthenticationException(error);
		}
		
		return keys;
	}
	
	/**
	 * 
	 * @param refreshToken
	 * @return
	 */
	
	@Override
	public TokenResponse refreshToken(String refreshToken) {		
		HttpResponse httpResponse = RestResource.post(System.getenv("OKTA_AUTHORIZATION_SERVER"))
				.basicAuthorization(System.getenv("OKTA_CLIENT_ID"), System.getenv("OKTA_CLIENT_SECRET"))
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.path(VERSION)
				.path(TOKEN)
				.parameter(GRANT_TYPE, REFRESH_TOKEN)
				.parameter(REFRESH_TOKEN, refreshToken)
				.parameter(SCOPE, OFFLINE_ACCESS)
				.execute();
		
		TokenResponse response = null;
		
		if (httpResponse.getStatusCode() == Status.OK) {
			response = httpResponse.getEntity(TokenResponse.class);
		} else {
			Error error = httpResponse.getEntity(Error.class);	
			throw new AuthenticationException(error);
		}
		
		return response;
	}
	
	/**
	 * 
	 * @param accessToken
	 * @return TokenVerificationResponse
	 */
	
	@Override
	public TokenVerificationResponse verifyToken(String accessToken) {		
		HttpResponse httpResponse = RestResource.post(System.getenv("OKTA_AUTHORIZATION_SERVER"))
				.basicAuthorization(System.getenv("OKTA_CLIENT_ID"), System.getenv("OKTA_CLIENT_SECRET"))
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.path(VERSION)
				.path(INTROSPECT)
				.parameter(TOKEN, accessToken)
				.parameter(TOKEN_TYPE_HINT, ACCESS_TOKEN)
				.execute();
		
		return httpResponse.getEntity(TokenVerificationResponse.class);
	}
	
	/**
	 * 
	 * @param accessToken
	 */
	
	@Override
	public void revokeToken(String accessToken) {
		HttpResponse httpResponse = RestResource.post(System.getProperty(Properties.OKTA_AUTHORIZATION_SERVER))
				.basicAuthorization(System.getProperty(Properties.OKTA_CLIENT_ID), System.getProperty(Properties.OKTA_CLIENT_SECRET))
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.path(VERSION)
				.path(REVOKE)
				.parameter(TOKEN, accessToken)
				.parameter(TOKEN_TYPE_HINT, ACCESS_TOKEN)
				.execute();
		
		if (httpResponse.getStatusCode() != Status.OK) {
			LOG.error("Revoke Token Error: " + httpResponse.getAsString());
		}
	}
}