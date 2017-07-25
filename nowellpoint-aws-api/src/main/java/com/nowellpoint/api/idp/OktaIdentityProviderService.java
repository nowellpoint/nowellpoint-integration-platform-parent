package com.nowellpoint.api.idp;

import org.jboss.logging.Logger;

import com.nowellpoint.api.idp.model.AuthenticationException;
import com.nowellpoint.api.idp.model.Error;
import com.nowellpoint.api.idp.model.TokenResponse;
import com.nowellpoint.api.idp.model.TokenVerificationResponse;
import com.nowellpoint.api.service.IdentityProviderService;
import com.nowellpoint.http.HttpResponse;
import com.nowellpoint.http.MediaType;
import com.nowellpoint.http.RestResource;
import com.nowellpoint.http.Status;
import com.nowellpoint.util.Properties;
import com.okta.sdk.authc.credentials.TokenClientCredentials;
import com.okta.sdk.client.Client;
import com.okta.sdk.client.Clients;
import com.okta.sdk.resource.user.PasswordCredential;
import com.okta.sdk.resource.user.User;
import com.okta.sdk.resource.user.UserCredentials;
import com.okta.sdk.resource.user.UserProfile;
import com.okta.sdk.resource.user.UserStatus;

public class OktaIdentityProviderService implements IdentityProviderService {
	
	private static final Logger LOG = Logger.getLogger(IdentityProviderService.class);
	
	private static Client client;
	
	static {
		
		client = Clients.builder()
				.setClientCredentials(new TokenClientCredentials(System.getProperty(Properties.OKTA_API_KEY)))
				.setOrgUrl(System.getProperty(Properties.OKTA_ORG_URL))
				.build();
	}
	
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
				.path("v1")
				.path("token")
				.parameter("grant_type", "password")
				.parameter("username", username)
				.parameter("password", password)
				.parameter("scope", "offline_access")
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
	 * @param refreshToken
	 * @return
	 */
	
	@Override
	public TokenResponse refreshToken(String refreshToken) {		
		HttpResponse httpResponse = RestResource.post(System.getenv("OKTA_AUTHORIZATION_SERVER"))
				.basicAuthorization(System.getenv("OKTA_CLIENT_ID"), System.getenv("OKTA_CLIENT_SECRET"))
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.path("v1")
				.path("token")
				.parameter("grant_type", "refresh_token")
				.parameter("refresh_token", refreshToken)
				.parameter("scope", "offline_access")
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
	public TokenVerificationResponse verify(String accessToken) {		
		HttpResponse httpResponse = RestResource.post(System.getenv("OKTA_AUTHORIZATION_SERVER"))
				.basicAuthorization(System.getenv("OKTA_CLIENT_ID"), System.getenv("OKTA_CLIENT_SECRET"))
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.path("v1")
				.path("introspect")
				.parameter("token", accessToken)
				.parameter("token_type_hint", "access_token")
				.execute();
		
		return httpResponse.getEntity(TokenVerificationResponse.class);
	}
	
	/**
	 * 
	 * @param href
	 * @return
	 */
	
	@Override
	public User getUser(String id) {
		return client.getUser(id);
	}
	
	/**
	 * 
	 * @param email
	 * @param firstName
	 * @param lastName
	 * @param password
	 */
	
	@Override
	public User createUser(String email, String firstName, String lastName, String password) {	
		
		UserProfile userProfile = client.instantiate(UserProfile.class)
			    .setEmail(email)
			    .setLogin(email)
			    .setFirstName(firstName)
			    .setLastName(lastName);
		
		PasswordCredential passwordCredential = client.instantiate(PasswordCredential.class)
				.setValue(password);
		
		UserCredentials userCredentials = client.instantiate(UserCredentials.class)
				.setPassword(passwordCredential);
		
		User user = client.instantiate(User.class)
				.setProfile(userProfile)
				.setStatus(UserStatus.ACTIVE)
				.setCredentials(userCredentials);
		
		user = client.createUser(user);
		
		client.getUser(user.getId()).addToGroup(System.getProperty("okta.group.id"));
		
		return user;
	}
	
	/**
	 * 
	 */
	
	@Override
	public User updateUser(String id, String email, String firstName, String lastName) {
		
		User user = client.getUser(id);
		
		UserProfile userProfile = user.getProfile()
				.setLogin(email)
			    .setEmail(email)
			    .setFirstName(firstName)
			    .setLastName(lastName);
		
		user.setProfile(userProfile).update();
		
		return user;
	}
	
	/**
	 * 
	 * @param href
	 */
	
	@Override
	public void deactivateUser(String id) {
		client.getUser(id).deactivate();
	}
	
	/**
	 * 
	 * @param href
	 * @param password
	 */
	
	@Override
	public void changePassword(String id, String password) {
		PasswordCredential passwordCredential = client.instantiate(PasswordCredential.class)
				.setValue(password);
		
		UserCredentials userCredentials = client.instantiate(UserCredentials.class)
				.setPassword(passwordCredential);
		
		client.getUser(id).setCredentials(userCredentials).update();
	}
	
	/**
	 * 
	 */
	
	@Override
	public void deleteUser(String id) {
		client.getUser(id).delete();
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
				.path("v1")
				.path("revoke")
				.parameter("token", accessToken)
				.parameter("token_type_hint", "access_token")
				.execute();
		
		if (httpResponse.getStatusCode() != Status.OK) {
			LOG.error("Revoke Token Error: " + httpResponse.getAsString());
		}
	}
}