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

import com.nowellpoint.client.Environment;
import com.nowellpoint.client.auth.ClientCredentialsGrantRequest;

public class ClientCredentialsGrantRequestImpl implements ClientCredentialsGrantRequest {
	
	private String apiKeyId;
	
	private String apiKeySecret;
	
	private Environment environment;
	
	public ClientCredentialsGrantRequestImpl(Environment environment, String apiKeyId, String apiKeySecret) {
		this.environment = environment;
		this.apiKeyId = apiKeyId;
		this.apiKeySecret = apiKeySecret;
	}

	@Override
	public String getApiKeyId() {
		return apiKeyId;
	}

	@Override
	public String getApiKeySecret() {
		return apiKeySecret;
	}
	
	@Override
	public Environment getEnvironment() {
		return environment;
	}
}