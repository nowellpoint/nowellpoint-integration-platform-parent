package com.nowellpoint.api.service.test;

import static org.junit.Assert.assertNotNull;

import java.util.Base64;

import org.junit.BeforeClass;
import org.junit.Test;

import com.nowellpoint.util.Properties;
import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.account.AccountList;
import com.stormpath.sdk.account.AccountStatus;
import com.stormpath.sdk.account.Accounts;
import com.stormpath.sdk.api.ApiKey;
import com.stormpath.sdk.api.ApiKeys;
import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.client.Clients;
import com.stormpath.sdk.oauth.AccessToken;
import com.stormpath.sdk.oauth.Authenticators;
import com.stormpath.sdk.oauth.OAuthClientCredentialsGrantRequestAuthentication;
import com.stormpath.sdk.oauth.OAuthGrantRequestAuthenticationResult;
import com.stormpath.sdk.oauth.OAuthRequests;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;

public class TestIdentityProviderService {
	
	private static Client client;
	private static ApiKey apiKey;
	private static Application application;
	
	@BeforeClass
	public static void beforeClass() {
		Properties.loadProperties(System.getenv("NOWELLPOINT_PROPERTY_STORE"));
		
		apiKey = ApiKeys.builder()
				.setId(System.getProperty(Properties.STORMPATH_API_KEY_ID))
				.setSecret(System.getProperty(Properties.STORMPATH_API_KEY_SECRET))
				.build();
		
		client = Clients.builder()
				.setApiKey(apiKey)
				.build();
		
		application = client.getResource(System.getProperty(Properties.STORMPATH_API_ENDPOINT).concat("/applications/")
				.concat(System.getProperty(Properties.STORMPATH_APPLICATION_ID)), Application.class);
	}
	
	@Test
	public void testEmailVerificationToken() {
		
		AccountList accounts = application.getAccounts(
				Accounts.where(
						Accounts.username().eqIgnoreCase("test.nowellpoint@mailinator.com")));
		
		Account account = accounts.single();
			
		System.out.println(account.getFullName());
		
		System.out.println(account.getEmailVerificationStatus());
		System.out.println(account.getEmailVerificationToken().getHref());
		System.out.println(account.getEmailVerificationToken().getValue());
		
		account.setPassword("ksieki81;ski");
		account.setUsername(account.getUsername());
		account.setGivenName(account.getGivenName());
		account.setMiddleName(null);
		account.setSurname(account.getSurname());
		account.setEmail("administrator@nowellpoint.com");
		account.setStatus(AccountStatus.UNVERIFIED);
		
		account.save();
		
		assertNotNull(account.getEmailVerificationToken().getHref());
	}
	
	@Test
	public void testAuthentication() {
		OAuthClientCredentialsGrantRequestAuthentication request = OAuthRequests.OAUTH_CLIENT_CREDENTIALS_GRANT_REQUEST
				.builder()
				.setApiKeyId(System.getenv("STORMPATH_API_KEY_ID"))
				.setApiKeySecret(System.getenv("STORMPATH_API_KEY_SECRET"))
				.build();
		
		OAuthGrantRequestAuthenticationResult authenticationResult = Authenticators.OAUTH_CLIENT_CREDENTIALS_GRANT_REQUEST_AUTHENTICATOR
				.forApplication(application)
				.authenticate(request);
		
		assertNotNull(authenticationResult.getAccessToken().getAccount().getFullName());
		assertNotNull(authenticationResult.getAccessToken().getJwt());
		assertNotNull(authenticationResult.getAccessToken().getHref());
		
		Jws<Claims> claims = Jwts.parser()
				.setSigningKey(Base64.getUrlEncoder().encodeToString(System.getProperty(Properties.STORMPATH_API_KEY_SECRET).getBytes()))
				.parseClaimsJws(authenticationResult.getAccessToken().getJwt());
		
		assertNotNull(claims.getBody().getId());
		assertNotNull(claims.getBody().getIssuer());
		assertNotNull(claims.getBody().getSubject());
		
		Account account = client.getResource(authenticationResult.getAccessToken().getAccount().getHref(), Account.class);
		
		System.out.println(account.getHref());
		System.out.println(account.getGroups().single().getName());
		
		AccessToken accessToken = client.getResource(authenticationResult.getAccessToken().getHref(), AccessToken.class);
		accessToken.delete();
		
		System.out.println(accessToken.getHref());
	}
}