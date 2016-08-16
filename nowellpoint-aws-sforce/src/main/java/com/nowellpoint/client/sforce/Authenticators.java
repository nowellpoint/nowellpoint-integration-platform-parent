package com.nowellpoint.client.sforce;

import java.nio.charset.StandardCharsets;

import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.MediaType;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.http.Status;
import com.nowellpoint.client.sforce.impl.OauthAuthenticationResponseImpl;
import com.nowellpoint.client.sforce.OauthConstants;
import com.nowellpoint.client.sforce.model.Error;
import com.nowellpoint.client.sforce.model.Identity;
import com.nowellpoint.client.sforce.model.Token;

public class Authenticators {
	
	private static final String OAUTH_TOKEN_URI = "https://login.salesforce.com/services/oauth2/token";
	
	public static final AuthorizationGrantResponseFactory AUTHORIZATION_GRANT_AUTHENTICATOR = new AuthorizationGrantResponseFactory();
	public static final UsernamePasswordGrantResponseFactory USERNAME_PASSWORD_GRANT_AUTHENTICATOR = new UsernamePasswordGrantResponseFactory();
	public static final RefreshTokenGrantResponseFactory REFRESH_TOKEN_GRANT_AUTHENTICATOR = new RefreshTokenGrantResponseFactory();
	
	public static class AuthorizationGrantResponseFactory {
		public OauthAuthenticationResponse authenticate(AuthorizationGrantRequest authorizationGrantRequest) {
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
	
	public static class UsernamePasswordGrantResponseFactory {
		public OauthAuthenticationResponse authenticate(UsernamePasswordGrantRequest usernamePasswordGrantRequest) {
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