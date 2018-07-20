package com.nowellpoint.console.service;

import java.io.UnsupportedEncodingException;

import com.nowellpoint.console.model.Token;

public interface AuthenticationService {
	
	public void revoke(String accessToken);

	public Token authenticate(String username, String password) throws UnsupportedEncodingException;
}