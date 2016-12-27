package com.nowellpoint.api.service;

import java.util.Date;
import java.util.Base64;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;
import javax.validation.ValidationException;

import org.jboss.logging.Logger;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nowellpoint.api.model.domain.AccountProfile;
import com.nowellpoint.api.model.domain.Error;
import com.nowellpoint.api.model.domain.idp.Token;
import com.nowellpoint.api.model.domain.idp.User;
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
import com.stormpath.sdk.oauth.AccessToken;
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
import io.jsonwebtoken.SignatureAlgorithm;

public class IdentityProviderService {
	
	private static final Logger LOGGER = Logger.getLogger(IdentityProviderService.class);
	
	@Inject
	private AccountProfileService accountProfileService;
	
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
	
	public Token authenticate(ApiKey apiKey) {
		OAuthClientCredentialsGrantRequestAuthentication request = OAuthRequests.OAUTH_CLIENT_CREDENTIALS_GRANT_REQUEST
				.builder()
				.setApiKeyId(apiKey.getId())
				.setApiKeySecret(apiKey.getSecret())
				.build();
		
		OAuthGrantRequestAuthenticationResult result = Authenticators.OAUTH_CLIENT_CREDENTIALS_GRANT_REQUEST_AUTHENTICATOR
				.forApplication(application)
				.authenticate(request);
		
		AccessToken accessToken = result.getAccessToken();
        
        AccountProfile accountProfile = accountProfileService.findAccountProfileByHref(accessToken.getAccount().getHref());
        
        Token token = createToken(result, accountProfile.getId());
        
        return token;
	}
	
	/**
	 * 
	 * @param username
	 * @param password
	 * @return
	 */
	
	public Token authenticate(String username, String password) {			
		OAuthPasswordGrantRequestAuthentication request = OAuthRequests.OAUTH_PASSWORD_GRANT_REQUEST
				.builder()
				.setLogin(username)
                .setPassword(password)
                .build();

        OAuthGrantRequestAuthenticationResult result = Authenticators.OAUTH_PASSWORD_GRANT_REQUEST_AUTHENTICATOR
        		.forApplication(application)
        		.authenticate(request);
        
        AccessToken accessToken = result.getAccessToken();
        
        AccountProfile accountProfile = accountProfileService.findAccountProfileByHref(accessToken.getAccount().getHref());
        
        Token token = createToken(result, accountProfile.getId());
        
        return token;
	}
	
	/**
	 * 
	 * @param id
	 * @return
	 */
	
	public User getAccount(String id) {
		
		User user = null;
		
		HttpResponse httpResponse = RestResource.get(String.format("%s/accounts/%s", System.getProperty(Properties.STORMPATH_API_ENDPOINT), id))
				.basicAuthorization(apiKey.getId(), apiKey.getSecret())
				.queryParameter("expand","groups")
				.execute();
			
		LOGGER.info("Status Code: " + httpResponse.getStatusCode() + " Target: " + httpResponse.getURL());
		
		if (httpResponse.getStatusCode() == 200) {
			user = httpResponse.getEntity(User.class);
		} else {
			LOGGER.error(httpResponse.getAsString());
		}
		
		return user;
	}
	
	/**
	 * 
	 * @param href
	 * @return
	 */
	
	public User getAccountByHref(String href) {
		
		User user = null;
		
		HttpResponse httpResponse = RestResource.get(href)
				.basicAuthorization(apiKey.getId(), apiKey.getSecret())
				.execute();
			
		LOGGER.info("Status Code: " + httpResponse.getStatusCode() + " Target: " + httpResponse.getURL());
		
		if (httpResponse.getStatusCode() == 200) {
			user = httpResponse.getEntity(User.class);
		} else {
			LOGGER.error(httpResponse.getAsString());
		}
		
		return user;
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
	 * @param user
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
	 * @param subject
	 * @return
	 */
	
	public User getAccountBySubject(String subject) {		
		HttpResponse httpResponse = RestResource.get(subject)
				.basicAuthorization(apiKey.getId(), apiKey.getSecret())
				.queryParameter("expand","groups")
				.execute();
			
		LOGGER.info("Status Code: " + httpResponse.getStatusCode() + " Target: " + httpResponse.getURL());
		
		User user = null;
		
		if (httpResponse.getStatusCode() == 200) {
			user = httpResponse.getEntity(User.class);
		} else {
			throw new ValidationException(httpResponse.getAsString());
		}
		
		return user;
	}
	
	/**
	 * 
	 * @param bearerToken
	 * @return
	 */
	
	public Token refresh(String bearerToken) {		
		OAuthRefreshTokenRequestAuthentication refreshRequest = OAuthRequests.OAUTH_REFRESH_TOKEN_REQUEST.builder()
				  .setRefreshToken(bearerToken)
				  .build();
		
		OAuthGrantRequestAuthenticationResult result = Authenticators.OAUTH_REFRESH_TOKEN_REQUEST_AUTHENTICATOR
				  .forApplication(application)
				  .authenticate(refreshRequest);
		
		AccountProfile accountProfile = accountProfileService.findAccountProfileByHref(result.getAccessToken().getAccount().getHref());
		
		Token token = createToken(result, accountProfile.getId().toString());
        
        return token;
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
	
	public String verifyEmail(String emailVerificationToken) {	
		
		HttpResponse httpResponse = RestResource.post(System.getProperty(Properties.STORMPATH_API_ENDPOINT))
				.basicAuthorization(apiKey.getId(), apiKey.getSecret())
				.path("accounts")
				.path("emailVerificationTokens")
				.path(emailVerificationToken)
				.execute();
		
		ObjectNode response = httpResponse.getEntity(ObjectNode.class);

		if (httpResponse.getStatusCode() != Status.OK) {
			Error error = new Error(response.get("code").asInt(), response.get("developerMessage").asText());
			throw new ValidationException(error.getMessage()); 
		}
		
		return response.get("href").asText();
	}
	
	/**
	 * 
	 * @param result
	 * @param subject
	 * @return
	 */
	
	private Token createToken(OAuthGrantRequestAuthenticationResult result, String subject) {
		
		Jws<Claims> claims = Jwts.parser()
				.setSigningKey(Base64.getUrlEncoder().encodeToString(System.getProperty(Properties.STORMPATH_API_KEY_SECRET).getBytes()))
				.parseClaimsJws(result.getAccessTokenString()); 
		
		String id = claims.getBody().getId();
		Date expiration = claims.getBody().getExpiration();
		
		Set<String> groups = new HashSet<String>();
		result.getAccessToken().getAccount().getGroups().forEach(g -> 
			groups.add(g.getName())
        );
		
		String jwt = Jwts.builder()
        		.setId(id)
        		.setHeaderParam("typ", "JWT")
        		.setIssuer("nowellpoint.com")
        		.setSubject(subject)
        		.setIssuedAt(new Date(System.currentTimeMillis()))
        		.setExpiration(expiration)
        		.signWith(SignatureAlgorithm.HS512, Base64.getUrlEncoder().encodeToString(System.getProperty(Properties.STORMPATH_API_KEY_SECRET).getBytes()))
        		.claim("groups", groups.toArray(new String[groups.size()]))
        		.compact();	 
		
		Token token = new Token();
		token.setAccessToken(jwt);
		token.setExpiresIn(result.getExpiresIn());
		token.setRefreshToken(result.getRefreshTokenString());
		token.setTokenType(result.getTokenType());
        
        return token;
	}
}