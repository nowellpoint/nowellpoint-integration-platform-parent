package com.nowellpoint.api.service;

import com.nowellpoint.api.idp.model.TokenResponse;
import com.nowellpoint.api.rest.domain.Token;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;

public interface TokenService {
	
	public Token createToken(TokenResponse tokenResponse);
	public Jws<Claims> verifyToken(String accessToken);
}