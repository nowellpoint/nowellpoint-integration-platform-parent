package com.nowellpoint.api.service;

import com.okta.sdk.models.auth.AuthResult;
import com.okta.sdk.resource.user.User;

public interface IdentityProviderService {
	
	
	public AuthResult authenticate(String username, String password);
	
	/**
	 * 
	 * @param apiKey
	 * @return
	 */
	
	//public OAuthGrantRequestAuthenticationResult authenticate(ApiKey apiKey);
	
	/**
	 * 
	 * @param refreshToken
	 * @return
	 */
	
	//public OAuthGrantRequestAuthenticationResult refreshToken(String refreshToken);
	
	/**
	 * 
	 * @param bearerToken
	 * @return
	 */
	
	//public String verify(String bearerToken);
	
	/**
	 * 
	 * @param href
	 * @return
	 */
	
	public User getUser(String id);
	
	/**
	 * 
	 * @param email
	 * @param firstName
	 * @param lastName
	 * @param password
	 */
	
	public User createUser(String email, String firstName, String lastName, String password);
	
	/**
	 * 
	 * @param href
	 * @param email
	 * @param firstName
	 * @param lastName
	 */
	
	public User updateUser(String id, String email, String firstName, String lastName);
	
	/**
	 * 
	 * @param href
	 */
	
	public void deactivateUser(String id);
	
	/**
	 * 
	 * @param href
	 * @param password
	 */
	
	public void changePassword(String id, String password);
	
	/**
	 * 
	 * @param id
	 */
	
	public void deleteUser(String id);
	
	/**
	 * 
	 * @param username
	 * @return
	 */
	
	public User findByUsername(String username);
	
	/**
	 * 
	 * @param bearerToken
	 */
	
	public void revokeToken(String bearerToken);
}