package com.nowellpoint.api.service;

import com.nowellpoint.api.idp.model.TokenResponse;
import com.nowellpoint.api.rest.domain.Token;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;

public interface TokenService {
	
	/**
	 * 
	 * @param tokenResponse
	 * @return
	 */
	
	public Token createToken(TokenResponse tokenResponse);
	
	/**
	 * 
	 * @param accessToken
	 * @return
	 */
	
	public Jws<Claims> verifyToken(String accessToken);
}