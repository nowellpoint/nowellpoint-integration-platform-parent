package com.nowellpoint.console.service;

import com.nowellpoint.console.model.Identity;
import com.nowellpoint.console.model.SignUpRequest;
import com.nowellpoint.console.model.Token;

public interface ConsoleService {
	public Identity signUp(SignUpRequest request);
	public void revoke(String accessToken);
	public Token authenticate(String username, String password);
}