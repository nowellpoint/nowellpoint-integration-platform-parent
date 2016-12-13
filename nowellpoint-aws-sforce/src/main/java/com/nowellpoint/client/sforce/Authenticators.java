package com.nowellpoint.client.sforce;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.MediaType;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.http.Status;
import com.nowellpoint.client.sforce.impl.OauthAuthenticationResponseImpl;
import com.nowellpoint.client.sforce.model.Error;
import com.nowellpoint.client.sforce.model.Identity;
import com.nowellpoint.client.sforce.model.Token;

public class Authenticators {
	
	private static final String OAUTH_TOKEN_URI = "https://login.salesforce.com/services/oauth2/token";
	
	public static final AuthorizationGrantResponseFactory AUTHORIZATION_GRANT_AUTHENTICATOR = new AuthorizationGrantResponseFactory();
	public static final PasswordGrantResponseFactory PASSWORD_GRANT_AUTHENTICATOR = new PasswordGrantResponseFactory();
	public static final RefreshTokenGrantResponseFactory REFRESH_TOKEN_GRANT_AUTHENTICATOR = new RefreshTokenGrantResponseFactory();
	
	public static class AuthorizationGrantResponseFactory {
		public OauthAuthenticationResponse authenticate(AuthorizationGrantRequest authorizationGrantRequest) {
			
			Optional.of(authorizationGrantRequest.getClientId()).orElseThrow(() -> new IllegalArgumentException("missing clientId"));
			Optional.of(authorizationGrantRequest.getClientSecret()).orElseThrow(() -> new IllegalArgumentException("missing clientSecret"));
			Optional.of(authorizationGrantRequest.getCode()).orElseThrow(() -> new IllegalArgumentException("missing auth code"));
			Optional.of(authorizationGrantRequest.getCallbackUri()).orElseThrow(() -> new IllegalArgumentException("missing callback uri"));
			
			HttpResponse httpResponse = RestResource.post(OAUTH_TOKEN_URI)
					.acceptCharset(StandardCharsets.UTF_8)
					.accept(MediaType.APPLICATION_JSON)
					.contentType(MediaType.APPLICATION_FORM_URLENCODED)
					.parameter(OauthConstants.GRANT_TYPE_PARAMETER, OauthConstants.AUTHORIZATION_GRANT_TYPE)
					.parameter(OauthConstants.CODE_PARAMETER, authorizationGrantRequest.getCode())
					.parameter(OauthConstants.CLIENT_ID_PARAMETER, authorizationGrantRequest.getClientId())
					.parameter(OauthConstants.CLIENT_SECRET_PARAMETER, authorizationGrantRequest.getClientSecret())
					.parameter(OauthConstants.REDIRECT_URI_PARAMETER, authorizationGrantRequest.getCallbackUri())
					.execute();
			
			Token token = null;
			
			if (httpResponse.getStatusCode() == Status.OK) {
	    		token = httpResponse.getEntity(Token.class);
			} else {
				throw new OauthException(httpResponse.getStatusCode(), httpResponse.getEntity(Error.class));
			}
			
			Identity identity = getIdentity(token.getId(), token.getAccessToken());
	    	
			OauthAuthenticationResponse response = new OauthAuthenticationResponseImpl(token, identity);
			return response;
		}
	}
	
	public static class PasswordGrantResponseFactory {
		public OauthAuthenticationResponse authenticate(UsernamePasswordGrantRequest usernamePasswordGrantRequest) {
			
			Optional.of(usernamePasswordGrantRequest.getClientId()).orElseThrow(() -> new IllegalArgumentException("missing clientId"));
			Optional.of(usernamePasswordGrantRequest.getClientSecret()).orElseThrow(() -> new IllegalArgumentException("missing clientSecret"));
			Optional.of(usernamePasswordGrantRequest.getUsername()).orElseThrow(() -> new IllegalArgumentException("missing username"));
			Optional.of(usernamePasswordGrantRequest.getPassword()).orElseThrow(() -> new IllegalArgumentException("missing password"));
			
			HttpResponse httpResponse = RestResource.post(OAUTH_TOKEN_URI)
					.contentType(MediaType.APPLICATION_FORM_URLENCODED)
					.accept(MediaType.APPLICATION_JSON)
					.acceptCharset(StandardCharsets.UTF_8)
					.parameter(OauthConstants.GRANT_TYPE_PARAMETER, OauthConstants.PASSWORD_GRANT_TYPE)
					.parameter(OauthConstants.CLIENT_ID_PARAMETER, usernamePasswordGrantRequest.getClientId())
					.parameter(OauthConstants.CLIENT_SECRET_PARAMETER, usernamePasswordGrantRequest.getClientSecret())
					.parameter(OauthConstants.USERNAME_PARAMETER, usernamePasswordGrantRequest.getUsername())
					.parameter(OauthConstants.PASSWORD_PARAMETER, usernamePasswordGrantRequest.getPassword()
							.concat(usernamePasswordGrantRequest.getSecurityToken() != null ? usernamePasswordGrantRequest.getSecurityToken() : ""))
					.execute();
			
			Token token = null;
			
			if (httpResponse.getStatusCode() == Status.OK) {
	    		token = httpResponse.getEntity(Token.class);
			} else {
				throw new OauthException(httpResponse.getStatusCode(), httpResponse.getEntity(Error.class));
			}
			
			Identity identity = getIdentity(token.getId(), token.getAccessToken());
			
			OauthAuthenticationResponse response = new OauthAuthenticationResponseImpl(token, identity);
			return response;
		}
	}
	
	public static class RefreshTokenGrantResponseFactory {
		public OauthAuthenticationResponse authenticate(RefreshTokenGrantRequest refreshTokenGrantRequest) {
			
			Optional.of(refreshTokenGrantRequest.getClientId()).orElseThrow(() -> new IllegalArgumentException("missing clientId"));
			Optional.of(refreshTokenGrantRequest.getClientSecret()).orElseThrow(() -> new IllegalArgumentException("missing clientSecret"));
			Optional.of(refreshTokenGrantRequest.getRefreshToken()).orElseThrow(() -> new IllegalArgumentException("missing refreshToken"));
			
			HttpResponse httpResponse = RestResource.post(OAUTH_TOKEN_URI)
					.acceptCharset(StandardCharsets.UTF_8)
					.accept(MediaType.APPLICATION_JSON)
					.contentType(MediaType.APPLICATION_FORM_URLENCODED)
					.parameter(OauthConstants.GRANT_TYPE_PARAMETER, OauthConstants.REFRESH_GRANT_TYPE)
					.parameter(OauthConstants.CLIENT_ID_PARAMETER, refreshTokenGrantRequest.getClientId())
					.parameter(OauthConstants.CLIENT_SECRET_PARAMETER, refreshTokenGrantRequest.getClientSecret())
					.parameter(OauthConstants.REFRESH_TOKEN_PARAMETER, refreshTokenGrantRequest.getRefreshToken())
					.execute();
			
			Token token = null;
			
			if (httpResponse.getStatusCode() == Status.OK) {
	    		token = httpResponse.getEntity(Token.class);
			} else {
				throw new OauthException(httpResponse.getStatusCode(), httpResponse.getEntity(Error.class));
			}
			
			Identity identity = getIdentity(token.getId(), token.getAccessToken());
	    	
			OauthAuthenticationResponse response = new OauthAuthenticationResponseImpl(token, identity);
			return response;
		}
	}
	
	private static Identity getIdentity(String id, String accessToken) {
		HttpResponse httpResponse = RestResource.get(id)
				.acceptCharset(StandardCharsets.UTF_8)
				.bearerAuthorization(accessToken)
				.accept(MediaType.APPLICATION_JSON)
				.queryParameter("version", "latest")
				.execute();
    	
		Identity identity = null;
    	
    	if (httpResponse.getStatusCode() == Status.OK) {
    		identity = httpResponse.getEntity(Identity.class);
		} else {
			throw new OauthException(httpResponse.getStatusCode(), httpResponse.getEntity(Error.class));
		}
    	
    	return identity;
	}
}