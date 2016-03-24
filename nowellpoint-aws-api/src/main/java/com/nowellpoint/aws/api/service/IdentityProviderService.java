package com.nowellpoint.aws.api.service;

import com.stormpath.sdk.oauth.JwtAuthenticationResult;

import java.util.Optional;

import com.nowellpoint.aws.api.dto.idp.AccountDTO;
import com.nowellpoint.aws.api.dto.idp.Token;
import com.nowellpoint.aws.model.admin.Properties;
import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.account.AccountStatus;
import com.stormpath.sdk.api.ApiKey;
import com.stormpath.sdk.api.ApiKeys;
import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.client.Clients;
import com.stormpath.sdk.oauth.AccessToken;
import com.stormpath.sdk.oauth.Authenticators;
import com.stormpath.sdk.oauth.JwtAuthenticationRequest;
import com.stormpath.sdk.oauth.Oauth2Requests;
import com.stormpath.sdk.oauth.OauthGrantAuthenticationResult;
import com.stormpath.sdk.oauth.PasswordGrantRequest;
import com.stormpath.sdk.oauth.RefreshGrantRequest;

public class IdentityProviderService extends AbstractCacheService {
	
	private static Client client;
	private static Application application;
	
	static {
		ApiKey apiKey = ApiKeys.builder()
				.setId(System.getProperty(Properties.STORMPATH_API_KEY_ID))
				.setSecret(System.getProperty(Properties.STORMPATH_API_KEY_SECRET))
				.build();
		
		client = Clients.builder()
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
        
        AccountDTO resource = getResource(result.getAccessToken().getAccount());
        
        hset(resource.getHref(), AccountDTO.class.getName(), resource);
        
        Token token = new Token();
		token.setAccessToken(result.getAccessTokenString());
		token.setExpiresIn(result.getExpiresIn());
		token.setRefreshToken(result.getRefreshTokenString());
		token.setStormpathAccessTokenHref(result.getAccessTokenHref());
		token.setTokenType(result.getTokenType());
		
		setex(token.getAccessToken(), token.getExpiresIn().intValue(), token);
        
        return token;
	}
	
	public void createAccount(AccountDTO resource) {
		Account account = client.instantiate(Account.class);
		account.setGivenName(resource.getGivenName());
		account.setMiddleName(resource.getMiddleName());
		account.setSurname(resource.getSurname());
		account.setEmail(resource.getEmail());
		account.setUsername(resource.getUsername());
		account.setPassword(resource.getPassword());
		account.setStatus(AccountStatus.UNVERIFIED);
		application.createAccount(account);
	}
	
	public void createAccount(String givenName, String middleName, String surname, String email, String username, String password) {
		Account account = client.instantiate(Account.class);
		account.setGivenName(givenName);
		account.setMiddleName(middleName);
		account.setSurname(surname);
		account.setEmail(email);
		account.setUsername(email);
		account.setPassword(password);
		account.setStatus(AccountStatus.UNVERIFIED);
		application.createAccount(account);
	}
	
	public void updateAccount(String givenName, String middleName, String surname, String email, String username, String href) {
		Account account = client.instantiate(Account.class);
		account.setGivenName(givenName);
		account.setMiddleName(middleName);
		account.setSurname(surname);
		account.setEmail(email);
		account.setUsername(email);
		account.save();
	}
	
	public AccountDTO getAccountBySubject(String subject) {
		
		AccountDTO resource = hget(AccountDTO.class, subject, AccountDTO.class.getName());
		
		if (resource == null) {
			Account account = client.getResource(subject, com.stormpath.sdk.account.Account.class);
			resource = getResource(account);
		}
		
		return resource;
	}
	
	public Token refresh(String bearerToken) {
		
		RefreshGrantRequest refreshRequest = Oauth2Requests.REFRESH_GRANT_REQUEST.builder()
				  .setRefreshToken(bearerToken)
				  .build();
		
		OauthGrantAuthenticationResult result = Authenticators.REFRESH_GRANT_AUTHENTICATOR
				  .forApplication(application)
				  .authenticate(refreshRequest);
		
		Token token = new Token();
		token.setAccessToken(result.getAccessTokenString());
		token.setExpiresIn(result.getExpiresIn());
		token.setRefreshToken(result.getRefreshTokenString());
		token.setStormpathAccessTokenHref(result.getAccessTokenHref());
		token.setTokenType(result.getTokenType());
		
		setex(token.getAccessToken(), token.getExpiresIn().intValue(), token);
        
        return token;
	}
	
	public String verify(String bearerToken) {
		
		JwtAuthenticationRequest request = Oauth2Requests.JWT_AUTHENTICATION_REQUEST.builder()
				.setJwt(bearerToken)
				.build();
		
		JwtAuthenticationResult result = Authenticators.JWT_AUTHENTICATOR
				.forApplication(application)
				.withLocalValidation()
				.authenticate(request);
		
		return result.getJwt();
	}
	
	public void revoke(String bearerToken) {		
		Optional.ofNullable(get(Token.class, bearerToken)).ifPresent(token -> {
			del(bearerToken);
			AccessToken accessToken = client.getResource(token.getStormpathAccessTokenHref(), AccessToken.class);
			accessToken.delete();
		});
	}
	
	private AccountDTO getResource(Account account) {
		AccountDTO resource = new AccountDTO();
		resource.setEmail(account.getEmail());
		resource.setFullName(account.getFullName());
		resource.setGivenName(account.getGivenName());
		resource.setHref(account.getHref());
		resource.setMiddleName(account.getMiddleName());
		resource.setSurname(account.getSurname());
		resource.setStatus(account.getStatus().name());
		resource.setUsername(account.getUsername());
		return resource;
	}
}