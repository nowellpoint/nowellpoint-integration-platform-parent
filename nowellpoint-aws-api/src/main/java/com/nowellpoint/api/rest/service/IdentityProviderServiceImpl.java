package com.nowellpoint.api.rest.service;

import java.io.IOException;
import java.util.Base64;
import java.util.Locale;

import com.nowellpoint.api.rest.domain.AuthenticationException;
import com.nowellpoint.api.service.IdentityProviderService;
import com.nowellpoint.api.util.MessageProvider;
import com.nowellpoint.util.Properties;
import com.okta.sdk.authc.credentials.ClientCredentials;
import com.okta.sdk.authc.credentials.TokenClientCredentials;
import com.okta.sdk.client.Client;
import com.okta.sdk.client.ClientBuilder;
import com.okta.sdk.client.Clients;
import com.okta.sdk.clients.AuthApiClient;
import com.okta.sdk.framework.ApiClientConfiguration;
import com.okta.sdk.models.auth.AuthResult;
import com.okta.sdk.resource.ResourceException;
import com.okta.sdk.resource.user.PasswordCredential;
import com.okta.sdk.resource.user.User;
import com.okta.sdk.resource.user.UserCredentials;
import com.okta.sdk.resource.user.UserList;
import com.okta.sdk.resource.user.UserProfile;
import com.okta.sdk.resource.user.UserStatus;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;

public class IdentityProviderServiceImpl implements IdentityProviderService {
	
	private static Client client;
	
	static {
		
		ClientBuilder builder = Clients.builder();
		
		ClientCredentials<String> credentials = new TokenClientCredentials(System.getProperty(Properties.OKTA_API_KEY));
		
		client = builder.setClientCredentials(credentials)
				.setOrgUrl(System.getProperty(Properties.OKTA_ORG_URL))
				.build();
	}
	
	/**
	 * 
	 * @param apiKey
	 * @return
	 */
	
//	@Override
//	public OAuthGrantRequestAuthenticationResult authenticate(ApiKey apiKey) {
//		
//		OAuthClientCredentialsGrantRequestAuthentication request = OAuthRequests.OAUTH_CLIENT_CREDENTIALS_GRANT_REQUEST
//				.builder()
//				.setApiKeyId(apiKey.getId())
//				.setApiKeySecret(apiKey.getSecret())
//				.build();
//		
//		try {
//			
//			OAuthGrantRequestAuthenticationResult result = Authenticators.OAUTH_CLIENT_CREDENTIALS_GRANT_REQUEST_AUTHENTICATOR
//					.forApplication(application)
//					.authenticate(request);
//			
//			return result;
//			
//		} catch (ResourceException e) {
//			String errorMessage = MessageProvider.getMessage(Locale.US, "login.error");		
//			throw new AuthenticationException("invalid_credentials", errorMessage);
//		}
//	}
	
	/**
	 * 
	 * @param username
	 * @param password
	 * @return
	 */
	
	@Override
	public AuthResult authenticate(String username, String password) {	
		
		ApiClientConfiguration config = new ApiClientConfiguration(System.getProperty(Properties.OKTA_ORG_URL), System.getProperty(Properties.OKTA_API_KEY));
		AuthApiClient authApiClient = new AuthApiClient(config);
		
		try {
			AuthResult result = authApiClient.authenticate(username, password, null);
			return result;
		} catch (ResourceException | IOException e) {
			throw new AuthenticationException("invalid_credentials", MessageProvider.getMessage(Locale.US, "login.error"));
		}
	}
	
	/**
	 * 
	 * @param refreshToken
	 * @return
	 */
	
//	@Override
//	public OAuthGrantRequestAuthenticationResult refreshToken(String refreshToken) {		
//		OAuthRefreshTokenRequestAuthentication refreshRequest = OAuthRequests.OAUTH_REFRESH_TOKEN_REQUEST.builder()
//				  .setRefreshToken(refreshToken)
//				  .build();
//		
//		OAuthGrantRequestAuthenticationResult result = Authenticators.OAUTH_REFRESH_TOKEN_REQUEST_AUTHENTICATOR
//				  .forApplication(application)
//				  .authenticate(refreshRequest);
//		
//		return result;
//	}
	
	/**
	 * 
	 * @param bearerToken
	 * @return
	 */
	
//	@Override
//	public String verify(String bearerToken) {		
//		OAuthBearerRequestAuthentication request = OAuthRequests.OAUTH_BEARER_REQUEST.builder()
//				.setJwt(bearerToken)
//				.build();
//		
//		OAuthBearerRequestAuthenticationResult result = Authenticators.OAUTH_BEARER_REQUEST_AUTHENTICATOR
//				.forApplication(application)
//				.withLocalValidation()
//				.authenticate(request);
//		
//		return result.getJwt();
//	}
	
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
		
		return client.createUser(user);
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
	 * @param username
	 * @return
	 */
	
	@Override
	public User findByUsername(String username) {
		UserList users = client.listUsers();
				
		

		
		return null;
	}
	
	/**
	 * 
	 * @param bearerToken
	 */
	
	@Override
	public void revokeToken(String bearerToken) {		
		
	}
}