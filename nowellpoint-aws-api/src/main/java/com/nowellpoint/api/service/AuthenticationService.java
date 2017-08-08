package com.nowellpoint.api.service;

import com.nowellpoint.api.idp.model.Keys;
import com.nowellpoint.api.idp.model.TokenResponse;
import com.nowellpoint.api.idp.model.TokenVerificationResponse;

public interface AuthenticationService {
	
	/**
	 * 
	 * @return
	 */
	
	public Keys getKeys();
	
	/**
	 * 
	 * @param username
	 * @param password
	 * @return
	 */
	
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
	
	public TokenVerificationResponse verifyToken(String accessToken);
	
	/**
	 * 
	 * @param bearerToken
	 */
	
	public void revokeToken(String bearerToken);

}