package com.nowellpoint.aws.api.service;

import com.stormpath.sdk.oauth.JwtAuthenticationResult;
import com.nowellpoint.aws.idp.model.Token;
import com.nowellpoint.aws.model.admin.Properties;
import com.stormpath.sdk.api.ApiKey;
import com.stormpath.sdk.api.ApiKeys;
import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.client.Clients;
import com.stormpath.sdk.oauth.Authenticators;
import com.stormpath.sdk.oauth.JwtAuthenticationRequest;
import com.stormpath.sdk.oauth.Oauth2Requests;
import com.stormpath.sdk.oauth.OauthGrantAuthenticationResult;
import com.stormpath.sdk.oauth.PasswordGrantRequest;
import com.stormpath.sdk.oauth.RefreshGrantRequest;

public class IdentityProviderService {
	
	private static Application application;
	
	static {
		ApiKey apiKey = ApiKeys.builder()
				.setId(System.getProperty(Properties.STORMPATH_API_KEY_ID))
				.setSecret(System.getProperty(Properties.STORMPATH_API_KEY_SECRET))
				.build();
		
		Client client = Clients.builder()
				.setApiKey(apiKey)
				.build();
		
		application = client.getResource(System.getProperty(Properties.STORMPATH_API_ENDPOINT).concat("/applications/")
				.concat(System.getProperty(Properties.STORMPATH_APPLICATION_ID)), Application.class);
	}
	
	public Token authenticate(String username, String password) {	
		
		PasswordGrantRequest request = Oauth2Requests.PASSWORD_GRANT_REQUEST
				.builder()
				.setLogin(username)
                .setPassword(password)
                .build();

        OauthGrantAuthenticationResult result = Authenticators.PASSWORD_GRANT_AUTHENTICATOR
        		.forApplication(application)
        		.authenticate(request);
        
        Token token = Token.builder()
        		.setAccessToken(result.getAccessTokenString())
        		.setExpiresIn(result.getExpiresIn())
        		.setRefreshToken(result.getRefreshTokenString())
        		.setStormpathAccessTokenHref(result.getAccessTokenHref())
        		.setTokenType(result.getTokenType())
        		.build();
        
        return token;
		
	}
	
	public Token refresh(String accessToken) {
		
		RefreshGrantRequest refreshRequest = Oauth2Requests.REFRESH_GRANT_REQUEST.builder()
				  .setRefreshToken(accessToken)
				  .build();
		
		OauthGrantAuthenticationResult result = Authenticators.REFRESH_GRANT_AUTHENTICATOR
				  .forApplication(application)
				  .authenticate(refreshRequest);
		
		Token token = Token.builder()
        		.setAccessToken(result.getAccessTokenString())
        		.setExpiresIn(result.getExpiresIn())
        		.setRefreshToken(result.getRefreshTokenString())
        		.setStormpathAccessTokenHref(result.getAccessTokenHref())
        		.setTokenType(result.getTokenType())
        		.build();
        
        return token;
	}
	
	public String verify(String accessToken) {
		
		JwtAuthenticationRequest request = Oauth2Requests.JWT_AUTHENTICATION_REQUEST.builder()
				.setJwt(accessToken)
				.build();
		
		JwtAuthenticationResult result = Authenticators.JWT_AUTHENTICATOR
				.forApplication(application)
				.withLocalValidation()
				.authenticate(request);
		
		return result.getJwt();
	}
	
	public void revoke(String accessToken) {
		//AccessToken token = new AccessToken();
	}
}