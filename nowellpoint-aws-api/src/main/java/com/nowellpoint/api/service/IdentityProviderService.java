package com.nowellpoint.api.service;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.api.ApiKey;
import com.stormpath.sdk.oauth.OAuthGrantRequestAuthenticationResult;

public interface IdentityProviderService {
	
	/**
	 * 
	 * @param apiKey
	 * @return
	 */
	
	public OAuthGrantRequestAuthenticationResult authenticate(ApiKey apiKey);
	
	/**
	 * 
	 * @param username
	 * @param password
	 * @return OAuthGrantRequestAuthenticationResult
	 */
	
	public OAuthGrantRequestAuthenticationResult authenticate(String username, String password);
	
	/**
	 * 
	 * @param refreshToken
	 * @return
	 */
	
	public OAuthGrantRequestAuthenticationResult refreshToken(String refreshToken);
	
	/**
	 * 
	 * @param bearerToken
	 * @return
	 */
	
	public String verify(String bearerToken);
	
	/**
	 * 
	 * @param href
	 * @return
	 */
	
	public Account getAccountByHref(String href);
	
	/**
	 * 
	 * @param email
	 * @param firstName
	 * @param lastName
	 * @param password
	 */
	
	public Account createAccount(String email, String firstName, String lastName, String password);
	
	/**
	 * 
	 * @param href
	 * @param email
	 * @param firstName
	 * @param lastName
	 */
	
	public Account updateAccount(String href, String email, String firstName, String lastName);
	
	/**
	 * 
	 * @param href
	 * @param username
	 */
	
	public void updateUsername(String href, String username);
	
	/**
	 * 
	 * @param href
	 * @param email
	 */
	
	public void updateEmail(String href, String email);
	
	/**
	 * 
	 * @param href
	 */
	
	public void deactivateAccount(String href);
	
	/**
	 * 
	 * @param href
	 * @param password
	 */
	
	public void changePassword(String href, String password);
	
	/**
	 * 
	 * @param username
	 * @return
	 */
	
	public Account findByUsername(String username);
	
	/**
	 * 
	 * @param bearerToken
	 */
	
	public void revokeToken(String bearerToken);
	
	/**
	 * 
	 * @param emailVerificationToken
	 * @return
	 */
	
	public Account verifyEmail(String emailVerificationToken);
}