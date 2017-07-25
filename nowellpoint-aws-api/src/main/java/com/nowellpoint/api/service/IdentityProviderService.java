package com.nowellpoint.api.service;

import com.nowellpoint.api.idp.model.TokenResponse;
import com.nowellpoint.api.idp.model.TokenVerificationResponse;
import com.okta.sdk.resource.user.User;

public interface IdentityProviderService {
	
	
	public TokenResponse authenticate(String username, String password);
	
	/**
	 * 
	 * @param apiKey
	 * @return
	 */
	
	public TokenResponse authenticate(String apiKey);
	
	/**
	 * 
	 * @param refreshToken
	 * @return
	 */
	
	public TokenResponse refreshToken(String refreshToken);
	
	/**
	 * 
	 * @param accessToken
	 * @return
	 */
	
	public TokenVerificationResponse verify(String accessToken);
	
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
	 * @param bearerToken
	 */
	
	public void revokeToken(String bearerToken);
}