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

package com.nowellpoint.api.model.document;

import java.io.Serializable;

import com.nowellpoint.util.Assert;

public class ConnectString implements Serializable {
	
	private static final long serialVersionUID = -2211099796628211955L;
	
	private String uri;
	
	public ConnectString() {
		
	}
	
	private ConnectString(String uri) {
		this.uri = uri;
	}
	
	public static ConnectString of(String uri) {
		Assert.assertNotNullOrEmpty(uri, "Missing uri parameter for ConnectString");
		return new ConnectString(uri);
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}
}