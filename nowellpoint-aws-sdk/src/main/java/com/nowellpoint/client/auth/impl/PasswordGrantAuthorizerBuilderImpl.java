package com.nowellpoint.client.auth.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import com.nowellpoint.client.Environment;
import com.nowellpoint.client.auth.PasswordGrantAuthorizerBuilder;
import com.nowellpoint.client.auth.PasswordGrantRequest;
import com.nowellpoint.client.util.MessageProvider;
import com.nowellpoint.util.Assert;

public class PasswordGrantAuthorizerBuilderImpl implements PasswordGrantAuthorizerBuilder {

	private String username;
	
	private String password;
	
	private Environment environment;

	@Override
	public PasswordGrantAuthorizerBuilder setUsername(String username) {
		this.username = username;
		return this;
	}

	@Override
	public PasswordGrantAuthorizerBuilder setPassword(String password) {
		this.password = password;
		return this;
	}
	
	@Override
	public PasswordGrantAuthorizerBuilder setEnvironment(Environment environment) {
		this.environment = environment;
		return this;
	}
	
	@Override
	public PasswordGrantRequest build() {
		if (Assert.isNullOrEmpty(username) || Assert.isNullOrEmpty(password) || Assert.isNull(environment)) {
			
			List<String> errors = new ArrayList<>();
			if (Assert.isNullOrEmpty(username)) {
				errors.add(MessageProvider.getMessage(Locale.US, "username.required"));
			}
			if (Assert.isNullOrEmpty(password)) {
				errors.add(MessageProvider.getMessage(Locale.US, "password.required"));
			}
			if (Assert.isNull(environment)) {
				errors.add(MessageProvider.getMessage(Locale.US, "environment.required"));
			}
			
			String error = errors.stream().collect(Collectors.joining(" / "));
			
			throw new IllegalArgumentException(error);
		}
		return new PasswordGrantRequestImpl(environment, username, password);
	}
}