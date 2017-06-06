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
import com.nowellpoint.client.auth.ClientCredentialsGrantAuthorizerBuilder;
import com.nowellpoint.client.auth.ClientCredentialsGrantRequest;
import com.nowellpoint.client.util.MessageProvider;
import com.nowellpoint.util.Assert;

public class ClientCredentialsGrantAuthorizerBuilderImpl implements ClientCredentialsGrantAuthorizerBuilder {

	private String apiKeyId;
	
	private String apiKeySecret;
	
	private Environment environment;

	@Override
	public ClientCredentialsGrantAuthorizerBuilder setApiKeyId(String apiKeyId) {
		this.apiKeyId = apiKeyId;
		return this;
	}

	@Override
	public ClientCredentialsGrantAuthorizerBuilder setApiKeySecret(String apiKeySecret) {
		this.apiKeySecret = apiKeySecret;
		return this;
	}
	
	@Override
	public ClientCredentialsGrantAuthorizerBuilder setEnvironment(Environment environment) {
		this.environment = environment;
		return this;
	}
	
	@Override
	public ClientCredentialsGrantRequest build() {
		if (Assert.isNullOrEmpty(apiKeyId) || Assert.isNullOrEmpty(apiKeySecret) || Assert.isNull(environment)) {
			
			List<String> errors = new ArrayList<>();
			if (Assert.isNullOrEmpty(apiKeyId)) {
				errors.add(MessageProvider.getMessage(Locale.US, "api.key.id.required"));
			}
			if (Assert.isNullOrEmpty(apiKeySecret)) {
				errors.add(MessageProvider.getMessage(Locale.US, "api.key.secret.required"));
			}
			if (Assert.isNull(environment)) {
				errors.add(MessageProvider.getMessage(Locale.US, "environment.required"));
			}
			
			String error = errors.stream().collect(Collectors.joining(" / "));
			
			throw new IllegalArgumentException(error);
		}
		
		return new ClientCredentialsGrantRequestImpl(environment, apiKeyId, apiKeySecret);
	}
}