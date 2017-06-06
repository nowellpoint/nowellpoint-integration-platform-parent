/**
 * 
 * Copyright 2015-2016 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

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