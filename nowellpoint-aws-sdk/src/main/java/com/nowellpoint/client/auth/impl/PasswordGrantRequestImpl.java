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
import com.nowellpoint.client.auth.PasswordGrantRequest;

public class PasswordGrantRequestImpl implements PasswordGrantRequest {
	
	private String username;
	
	private String password;
	
	private Environment environment;
	
	public PasswordGrantRequestImpl(Environment environment, String username, String password) {
		this.environment = environment;
		this.username = username;
		this.password = password;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public String getPassword() {
		return password;
	}
	
	@Override
	public Environment getEnvironment() {
		return environment;
	}
}