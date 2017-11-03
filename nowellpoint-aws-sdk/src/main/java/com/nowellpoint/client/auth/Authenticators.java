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

import com.nowellpoint.client.auth.impl.OauthAuthenticationResponseImpl;
import com.nowellpoint.client.auth.impl.OauthException;
import com.nowellpoint.client.model.Token;
import com.nowellpoint.client.model.exception.ServiceUnavailableException;
import com.nowellpoint.http.HttpResponse;
import com.nowellpoint.http.MediaType;
import com.nowellpoint.http.RestResource;
import com.nowellpoint.http.Status;
import com.nowellpoint.client.model.Error;

public class Authenticators {
	
	public static final PasswordGrantResponseFactory PASSWORD_GRANT_AUTHENTICATOR = new PasswordGrantResponseFactory();
	public static final ClientCredentialsGrantResponseFactory CLIENT_CREDENTIALS_GRANT_AUTHENTICATOR = new ClientCredentialsGrantResponseFactory();
	public static final RevokeTokenResponseFactory REVOKE_TOKEN_INVALIDATOR = new RevokeTokenResponseFactory();
	
	public static class PasswordGrantResponseFactory {
		public OauthAuthenticationResponse authenticate(PasswordGrantRequest grantRequest) {

			HttpResponse httpResponse = RestResource.post(grantRequest.getEnvironment().getEnvironmentUrl())
					.accept(MediaType.APPLICATION_JSON)
					.contentType(MediaType.APPLICATION_FORM_URLENCODED)
					.path("oauth")
					.path("token")
					.basicAuthorization(grantRequest.getUsername(), grantRequest.getPassword())
					.parameter("grant_type", "password")
					.execute();

			int statusCode = httpResponse.getStatusCode();

			if (statusCode == Status.OK) {

				Token token = httpResponse.getEntity(Token.class);
				OauthAuthenticationResponse response = new OauthAuthenticationResponseImpl(token);
				return response;

			} else if (statusCode == Status.NOT_AUTHORIZED) {
				Error error = httpResponse.getEntity(Error.class);
				throw new OauthException(error.getCode(), error.getErrorMessage());
			} else {
				throw new ServiceUnavailableException(httpResponse.getAsString());
			}
		}
	}
	
	public static class ClientCredentialsGrantResponseFactory {
		public OauthAuthenticationResponse authenticate(ClientCredentialsGrantRequest grantRequest) {

			HttpResponse httpResponse = RestResource.post(grantRequest.getEnvironment().getEnvironmentUrl())
					.accept(MediaType.APPLICATION_JSON)
					.contentType(MediaType.APPLICATION_FORM_URLENCODED)
					.path("oauth")
					.path("token")
					.basicAuthorization(grantRequest.getApiKeyId(), grantRequest.getApiKeySecret())
					.parameter("grant_type", "client_credentials")
					.execute();

			int statusCode = httpResponse.getStatusCode();

			if (statusCode == Status.OK) {

				Token token = httpResponse.getEntity(Token.class);
				OauthAuthenticationResponse response = new OauthAuthenticationResponseImpl(token);
				return response;

			} else if (statusCode == Status.NOT_AUTHORIZED) {
				Error error = httpResponse.getEntity(Error.class);
				throw new OauthException(error.getCode(), error.getErrorMessage());
			} else {
				throw new ServiceUnavailableException(httpResponse.getAsString());
			}
		}
	}
	
	public static class RevokeTokenResponseFactory {
		public void revoke(RevokeTokenRequest revokeTokenRequest) {
			HttpResponse httpResponse = RestResource.delete(revokeTokenRequest.getToken().getEnvironmentUrl())
					.bearerAuthorization(revokeTokenRequest.getToken().getAccessToken())
					.path("oauth")
					.path("token")
					.execute();

			int statusCode = httpResponse.getStatusCode();

			if (statusCode != Status.NO_CONTENT) {
				Error error = httpResponse.getEntity(Error.class);
				throw new OauthException(error.getCode(), error.getErrorMessage());
			}
		}
	}
}