package com.nowellpoint.console.service;

import com.nowellpoint.client.model.Token;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;

public interface TokenService {
	
	/**
     * Returns the token that should be used on every request for authentication and authorization
     *
     * @param TokenResponse received from the Identity Provider (Okta)
     * @return the token that should be used on every request for authentication and authorization.
     */
	
	public Token createToken(TokenResponse tokenResponse);
	
	/**
	 * 
	 * @param accessToken
	 * @return
	 */
	
	public Jws<Claims> verifyToken(String accessToken);
}