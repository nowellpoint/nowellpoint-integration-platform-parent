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

package com.nowellpoint.client.auth;

import com.nowellpoint.client.auth.impl.ClientCredentialsGrantAuthorizerBuilderImpl;
import com.nowellpoint.client.auth.impl.PasswordGrantAuthorizerBuilderImpl;
import com.nowellpoint.client.auth.impl.RevokeTokenInvalidatorBuilderImpl;

public class OauthRequests {
	
	public static final PasswordGrantRequestFactory PASSWORD_GRANT_REQUEST = new PasswordGrantRequestFactory();
	public static final ClientCredentialsGrantRequestFactory CLIENT_CREDENTIALS_GRANT_REQUEST = new ClientCredentialsGrantRequestFactory();
	public static final RevokeTokenRequestFactory REVOKE_TOKEN_REQUEST = new RevokeTokenRequestFactory();
	
	public static class PasswordGrantRequestFactory {
		public PasswordGrantAuthorizerBuilder builder() {
			PasswordGrantAuthorizerBuilder builder = new PasswordGrantAuthorizerBuilderImpl();
			return builder;
		}
	}
	
	public static class ClientCredentialsGrantRequestFactory {
		public ClientCredentialsGrantAuthorizerBuilder builder() {
			ClientCredentialsGrantAuthorizerBuilder builder = new ClientCredentialsGrantAuthorizerBuilderImpl();
			return builder;
		}
	}
	
	public static class RevokeTokenRequestFactory {
		public RevokeTokenInvalidatorBuilder builder() {
			RevokeTokenInvalidatorBuilder builder = new RevokeTokenInvalidatorBuilderImpl();
			return builder;
		}
	}
}