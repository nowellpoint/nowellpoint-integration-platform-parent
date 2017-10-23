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

package com.nowellpoint.client;

public class Environment {
	
    public static final Environment PRODUCTION = new Environment("https://api.nowellpoint.com/rest", "production");

    public static final Environment SANDBOX = new Environment("https://localhost:5100/rest", "sandbox");
    
    private String environmentUrl;
    
    private String environmentName;
    
	public Environment(String environmentUrl, String environmentName) {
		this.environmentUrl = environmentUrl;
		this.environmentName = environmentName;
	}

	public static Environment parseEnvironment(String environment) {
		if (environment.equalsIgnoreCase("sandbox")) {
			return SANDBOX;
		} else if (environment.equalsIgnoreCase("production")) {
			return PRODUCTION;
		} else {
			throw new IllegalArgumentException("Unknown environment: " + environment);
		}
	}

	public String getEnvironmentUrl() {
		return environmentUrl;
	}

	public String getEnvironmentName() {
		return environmentName;
	}
}