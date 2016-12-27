package com.nowellpoint.api.service;

import java.util.Base64;

import org.jboss.logging.Logger;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.http.Status;
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
import com.stormpath.sdk.directory.Directory;
import com.stormpath.sdk.oauth.Authenticators;
import com.stormpath.sdk.oauth.OAuthBearerRequestAuthentication;
import com.stormpath.sdk.oauth.OAuthBearerRequestAuthenticationResult;
import com.stormpath.sdk.oauth.OAuthClientCredentialsGrantRequestAuthentication;
import com.stormpath.sdk.oauth.OAuthGrantRequestAuthenticationResult;
import com.stormpath.sdk.oauth.OAuthPasswordGrantRequestAuthentication;
import com.stormpath.sdk.oauth.OAuthRefreshTokenRequestAuthentication;
import com.stormpath.sdk.oauth.OAuthRequests;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;

public class IdentityProviderService {
	
	private static final Logger LOGGER = Logger.getLogger(IdentityProviderService.class);
	
	private static ApiKey apiKey;
	private static Client client;
	private static Application application;
	private static Directory directory;
	
	static {
		apiKey = ApiKeys.builder()
				.setId(System.getProperty(Properties.STORMPATH_API_KEY_ID))
				.setSecret(System.getProperty(Properties.STORMPATH_API_KEY_SECRET))
				.build();
		
		client = Clients.builder()
				.setApiKey(apiKey)
				.build();
		
		application = client.getResource(System.getProperty(Properties.STORMPATH_API_ENDPOINT).concat("/applications/")
				.concat(System.getProperty(Properties.STORMPATH_APPLICATION_ID)), Application.class);
		
		directory = client.getResource(System.getProperty(Properties.STORMPATH_API_ENDPOINT).concat("/directories/")
				.concat(System.getProperty(Properties.STORMPATH_DIRECTORY_ID)), Directory.class);
	}
	
	/**
	 * 
	 * @param apiKey
	 * @return
	 */
	
	public OAuthGrantRequestAuthenticationResult authenticate(ApiKey apiKey) {
		OAuthClientCredentialsGrantRequestAuthentication request = OAuthRequests.OAUTH_CLIENT_CREDENTIALS_GRANT_REQUEST
				.builder()
				.setApiKeyId(apiKey.getId())
				.setApiKeySecret(apiKey.getSecret())
				.build();
		
		OAuthGrantRequestAuthenticationResult result = Authenticators.OAUTH_CLIENT_CREDENTIALS_GRANT_REQUEST_AUTHENTICATOR
				.forApplication(application)
				.authenticate(request);
		
		return result;
	}
	
	/**
	 * 
	 * @param username
	 * @param password
	 * @return
	 */
	
	public OAuthGrantRequestAuthenticationResult authenticate(String username, String password) {			
		OAuthPasswordGrantRequestAuthentication request = OAuthRequests.OAUTH_PASSWORD_GRANT_REQUEST
				.builder()
				.setLogin(username)
                .setPassword(password)
                .build();

        OAuthGrantRequestAuthenticationResult result = Authenticators.OAUTH_PASSWORD_GRANT_REQUEST_AUTHENTICATOR
        		.forApplication(application)
        		.authenticate(request);
        
        return result;
        
        //AccountProfile accountProfile = accountProfileService.findAccountProfileByHref(accessToken.getAccount().getHref());
        
        //Token token = createToken(result, accountProfile.getId());
        
        //return token;
	}
	
	/**
	 * 
	 * @param refreshToken
	 * @return
	 */
	
	public OAuthGrantRequestAuthenticationResult refreshToken(String refreshToken) {		
		OAuthRefreshTokenRequestAuthentication refreshRequest = OAuthRequests.OAUTH_REFRESH_TOKEN_REQUEST.builder()
				  .setRefreshToken(refreshToken)
				  .build();
		
		OAuthGrantRequestAuthenticationResult result = Authenticators.OAUTH_REFRESH_TOKEN_REQUEST_AUTHENTICATOR
				  .forApplication(application)
				  .authenticate(refreshRequest);
		
		return result;
	}
	
	/**
	 * 
	 * @param bearerToken
	 * @return
	 */
	
	public String verify(String bearerToken) {		
		OAuthBearerRequestAuthentication request = OAuthRequests.OAUTH_BEARER_REQUEST.builder()
				.setJwt(bearerToken)
				.build();
		
		OAuthBearerRequestAuthenticationResult result = Authenticators.OAUTH_BEARER_REQUEST_AUTHENTICATOR
				.forApplication(application)
				.withLocalValidation()
				.authenticate(request);
		
		return result.getJwt();
	}
	
	/**
	 * 
	 * @param href
	 * @return
	 */
	
	public Account getAccountByHref(String href) {
		return client.getResource(href, Account.class);
	}
	
	/**
	 * 
	 * @param user
	 */
	
	public void createAccount(Account account) {	
		directory.createAccount(account);
	}
	
	/**
	 * 
	 * @param href
	 * @param email
	 * @param firstName
	 * @param lastName
	 */
	
	public void updateAccount(String href, String email, String firstName, String lastName) {	
		
		Account account = client.getResource(href, Account.class)
				.setUsername(email)
			    .setEmail(email)
			    .setGivenName(firstName)
			    .setSurname(lastName);
		
		account.save();
	}
	
	/**
	 * 
	 * @param account
	 */
	
	public void updateAccount(Account account) {
		account.save();
	}
	
	/**
	 * 
	 * @param href
	 * @param username
	 */
	
	public void updateUsername(String href, String username) {
		
		Account account = client.getResource(href, Account.class)
				.setUsername(username);
		
		account.save();
	}
	
	/**
	 * 
	 * @param href
	 * @param email
	 */
	
	public void updateEmail(String href, String email) {
		
		Account account = client.getResource(href, Account.class)
				.setEmail(email);
		
		account.save();
		
	}
	
	/**
	 * 
	 * @param href
	 */
	
	public void deactivateUser(String href) {
		
		Account account = client.getResource(href, Account.class)
				.setStatus(AccountStatus.DISABLED);
		
		account.save();
	}
	
	/**
	 * 
	 * @param href
	 * @param password
	 */
	
	public void changePassword(String href, String password) {
		
		Account account = client.getResource(href, Account.class)
				.setPassword(password);
		
		account.save();
	}
	
	/**
	 * 
	 * @param username
	 * @return
	 */
	
	public Account findByUsername(String username) {
		AccountList accounts = application.getAccounts(Accounts.where(Accounts.username().eqIgnoreCase(username)));
		
		if (accounts.getSize() == 1) {
			return accounts.single();
		}
		
		return client.instantiate(Account.class);
	}
	
	/**
	 * 
	 * @param bearerToken
	 */
	
	public void revoke(String bearerToken) {		
		Jws<Claims> claims = Jwts.parser()
				.setSigningKey(Base64.getUrlEncoder().encodeToString(apiKey.getSecret().getBytes()))
				.parseClaimsJws(bearerToken); 
		
		HttpResponse httpResponse = RestResource.delete(System.getProperty(Properties.STORMPATH_API_ENDPOINT))
				.basicAuthorization(apiKey.getId(), apiKey.getSecret())
				.path("accessTokens")
				.path(claims.getBody().getId())
				.execute();
		
		/**
		 * AccessToken accessToken = oAuthGrantRequestAuthenticationResult.getAccessToken();
accessToken.delete();
		 */
		
		if (httpResponse.getStatusCode() != Status.NO_CONTENT) {
			ObjectNode response = httpResponse.getEntity(ObjectNode.class);
			LOGGER.warn(response.toString()); 
		}
	}
	
	/**
	 * 
	 * @param emailVerificationToken
	 * @return
	 */
	
	public Account verifyEmail(String emailVerificationToken) {	
		Account account = client.verifyAccountEmail(emailVerificationToken);
	    return account;
	}
}