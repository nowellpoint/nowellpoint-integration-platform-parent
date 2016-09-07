package com.nowellpoint.api.service;

import java.io.IOException;
import java.util.Date;
import java.util.Base64;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import javax.inject.Inject;

import org.jboss.logging.Logger;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nowellpoint.api.dto.idp.Token;
import com.nowellpoint.api.model.dto.AccountProfile;
import com.nowellpoint.api.model.dto.ErrorDTO;
import com.nowellpoint.api.util.UserContext;
import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.MediaType;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.http.Status;
import com.nowellpoint.aws.idp.model.Account;
import com.nowellpoint.aws.idp.model.Group;
import com.nowellpoint.aws.idp.model.Groups;
import com.nowellpoint.aws.idp.model.SearchResult;
import com.nowellpoint.aws.model.admin.Properties;
import com.stormpath.sdk.api.ApiKey;
import com.stormpath.sdk.api.ApiKeys;
import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.client.Clients;
import com.stormpath.sdk.oauth.AccessToken;
import com.stormpath.sdk.oauth.Authenticators;
import com.stormpath.sdk.oauth.OAuthBearerRequestAuthentication;
import com.stormpath.sdk.oauth.OAuthBearerRequestAuthenticationResult;
import com.stormpath.sdk.oauth.OAuthGrantRequestAuthenticationResult;
import com.stormpath.sdk.oauth.OAuthPasswordGrantRequestAuthentication;
import com.stormpath.sdk.oauth.OAuthRefreshTokenRequestAuthentication;
import com.stormpath.sdk.oauth.OAuthRequests;
import com.stormpath.sdk.resource.ResourceException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class IdentityProviderService extends AbstractCacheService {
	
	private static final Logger LOGGER = Logger.getLogger(IdentityProviderService.class);
	
	@Inject
	private AccountProfileService accountProfileService;
	
	private static ApiKey apiKey;
	private static Client client;
	private static Application application;
	
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
	}
	
	/**
	 * 
	 * @param username
	 * @param password
	 * @return the Authentication token
	 */
	
	public Token authenticate(String username, String password) throws ResourceException {			
		OAuthPasswordGrantRequestAuthentication request = OAuthRequests.OAUTH_PASSWORD_GRANT_REQUEST
				.builder()
				.setLogin(username)
                .setPassword(password)
                .build();

        OAuthGrantRequestAuthenticationResult result = Authenticators.OAUTH_PASSWORD_GRANT_REQUEST_AUTHENTICATOR
        		.forApplication(application)
        		.authenticate(request);
        
        AccessToken accessToken = result.getAccessToken();
        
        Account account = new Account();
        account.setEmail(accessToken.getAccount().getEmail());
        account.setFullName(accessToken.getAccount().getFullName());
        account.setGivenName(accessToken.getAccount().getGivenName());
        account.setHref(accessToken.getAccount().getHref());
        account.setMiddleName(accessToken.getAccount().getMiddleName());
        account.setSurname(accessToken.getAccount().getSurname());
        account.setStatus(accessToken.getAccount().getStatus().name());
        account.setUsername(accessToken.getAccount().getUsername());
        
        Groups groups = new Groups();
        groups.setHref(accessToken.getAccount().getGroups().getHref());
        groups.setLimit(accessToken.getAccount().getGroups().getLimit());
        groups.setOffset(accessToken.getAccount().getGroups().getOffset());
        groups.setSize(accessToken.getAccount().getGroups().getSize());
        
        account.setGroups(groups);
        
        if (accessToken.getAccount().getGroups().getSize() > 0) {
        	Set<Group> items = new HashSet<Group>();
            accessToken.getAccount().getGroups().forEach(g -> {
            	Group group = new Group();
            	group.setHref(g.getHref());
            	group.setName(g.getName());
            	items.add(group);
            });
            account.getGroups().setItems(items);
        }
        
        AccountProfile accountProfile = accountProfileService.findAccountProfileByHref(account.getHref());
        
        String id = UserContext.parseClaims(result.getAccessTokenString()).getBody().getId();
        Date expiration = UserContext.parseClaims(result.getAccessTokenString()).getBody().getExpiration();
        
        String jwt = createJwt(id, account.getHref().concat("-").concat(accountProfile.getId()), expiration);
        
        Token token = new Token();
		token.setAccessToken(jwt);
		token.setExpiresIn(result.getExpiresIn());
		token.setRefreshToken(result.getRefreshTokenString());
		token.setTokenType(result.getTokenType());
        
        return token;
	}
	
	/**
	 * 
	 * 
	 * @param username
	 * @return
	 * 
	 * 
	 */
	
	public Boolean isEnabledAccount(String username) {
		HttpResponse httpResponse = RestResource.get(System.getProperty(Properties.STORMPATH_API_ENDPOINT))
				.basicAuthorization(apiKey.getId(), apiKey.getSecret())
				.accept(MediaType.APPLICATION_JSON)
				.path("directories")
				.path(System.getProperty(Properties.STORMPATH_DIRECTORY_ID))
				.path("accounts")
				.path("?username=".concat(username))
				.execute();
		
		SearchResult searchResult = httpResponse.getEntity(SearchResult.class);
		
		if (searchResult.getSize() == 0) {
			return false;
		} else {
			Account account = searchResult.getItems().get(0);
			if ("ENABLED".equals(account.getStatus())) {
				return true;
			}
			return false;
		}
	}
	
	public Account getAccount(String id) {
		
		Account account = null;
		
		HttpResponse httpResponse = RestResource.get(String.format("%s/accounts/%s", System.getProperty(Properties.STORMPATH_API_ENDPOINT), id))
				.basicAuthorization(apiKey.getId(), apiKey.getSecret())
				.queryParameter("expand","groups")
				.execute();
			
		LOGGER.info("Status Code: " + httpResponse.getStatusCode() + " Target: " + httpResponse.getURL());
		
		if (httpResponse.getStatusCode() == 200) {
			account = httpResponse.getEntity(Account.class);
		} else {
			LOGGER.error(httpResponse.getAsString());
		}
		
		return account;
	}
	
	public Account getAccountByHref(String href) {
		
		Account account = null;
		
		HttpResponse httpResponse = RestResource.get(href)
				.basicAuthorization(apiKey.getId(), apiKey.getSecret())
				.execute();
			
		LOGGER.info("Status Code: " + httpResponse.getStatusCode() + " Target: " + httpResponse.getURL());
		
		if (httpResponse.getStatusCode() == 200) {
			account = httpResponse.getEntity(Account.class);
		} else {
			LOGGER.error(httpResponse.getAsString());
		}
		
		return account;
	}
	
	/**
	 * 
	 * @param resource
	 * @throws IOException 
	 */
	
	public Account createAccount(Account account) {	
		HttpResponse httpResponse = RestResource.post(System.getProperty(Properties.STORMPATH_API_ENDPOINT))
				.contentType(MediaType.APPLICATION_JSON)
				.path("directories")
				.path(System.getProperty(Properties.STORMPATH_DIRECTORY_ID))
				.path("accounts")
				.basicAuthorization(apiKey.getId(), apiKey.getSecret())
				.body(account)
				.execute();

		LOGGER.info("Status Code: " + httpResponse.getStatusCode() + " Target: " + httpResponse.getURL());
		
		if (httpResponse.getStatusCode() == 201) {
			account = httpResponse.getEntity(Account.class);
		} else {
			LOGGER.error(httpResponse.getAsString());
		}
		
		return account;
	}
	
	/**
	 * 
	 * @param resource
	 */
	
	public Account updateAccount(Account account) {	
		HttpResponse httpResponse = RestResource.post(account.getHref())
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.basicAuthorization(apiKey.getId(), apiKey.getSecret())
				.body(account)
				.execute();
		
		LOGGER.info("Status Code: " + httpResponse.getStatusCode() + " Target: " + httpResponse.getURL());
		
		if (httpResponse.getStatusCode() == 200) {
			account = httpResponse.getEntity(Account.class);
		} else {
			LOGGER.error(httpResponse.getAsString());
		}
		
		if (hexists(account.getHref(), Account.class.getName())) {
			hset(account.getHref(), Account.class.getName(), account);
		}
		
		return account;
	}
	
	/**
	 * 
	 * @param href
	 */
	
	public void disableAccount(String href) {
		Account account = new Account();
		account.setStatus("DISABLED");
		
		HttpResponse httpResponse = RestResource.post(href)
				.contentType(MediaType.APPLICATION_JSON)
				.basicAuthorization(apiKey.getId(), apiKey.getSecret())
				.body(account)
				.execute();
		
		LOGGER.info("Status Code: " + httpResponse.getStatusCode() + " Target: " + httpResponse.getURL());
		
		if (httpResponse.getStatusCode() != 200) {
			throw new ServiceException(httpResponse.getAsString());
		}
		
		if (hexists(href, Account.class.getName())) {
			hdel(href, Account.class.getName());
		}
	}
	
	/**
	 * 
	 * @param subject
	 * @return
	 * @throws IOException 
	 */
	
	public Account getAccountBySubject(String subject) throws IOException {
		Account account = hget(Account.class, subject, Account.class.getName());
		
		if (account == null) {			
			HttpResponse httpResponse = RestResource.get(subject)
					.basicAuthorization(apiKey.getId(), apiKey.getSecret())
					.queryParameter("expand","groups")
					.execute();
				
			LOGGER.info("Status Code: " + httpResponse.getStatusCode() + " Target: " + httpResponse.getURL());
			
			if (httpResponse.getStatusCode() == 200) {
				account = httpResponse.getEntity(Account.class);
			} else {
				LOGGER.error(httpResponse.getAsString());
			}
		}
		
		return account;
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
		
		String id = UserContext.parseClaims(result.getAccessTokenString()).getBody().getId();
		Date expiration = UserContext.parseClaims(result.getAccessTokenString()).getBody().getExpiration();
		
		String jwt = createJwt(id, result.getAccessToken().getAccount().getHref().concat("-").concat(accountProfile.getId()), expiration);
		
		Token token = new Token();
		token.setAccessToken(jwt);
		token.setExpiresIn(result.getExpiresIn());
		token.setRefreshToken(result.getRefreshTokenString());
		token.setTokenType(result.getTokenType());
        
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
	 * @return the Account associated with the @param username
	 */
	
	public Account findAccountByUsername(String username) {
		
		HttpResponse httpResponse = RestResource.get(System.getProperty(Properties.STORMPATH_API_ENDPOINT))
				.basicAuthorization(apiKey.getId(), apiKey.getSecret())
				.accept(MediaType.APPLICATION_JSON)
				.path("directories")
				.path(System.getProperty(Properties.STORMPATH_DIRECTORY_ID))
				.path("accounts")
				.path("?username=".concat(username))
				.execute();
		
		Account account = null;
		
		SearchResult searchResult = httpResponse.getEntity(SearchResult.class);
		
		if (searchResult.getSize() == 1) {
			account = searchResult.getItems().get(0);
		}
		
		return account;
	}
	
	/**
	 * 
	 * @param bearerToken
	 */
	
	public void revoke(String bearerToken) {		
		Optional.ofNullable(get(Token.class, bearerToken)).ifPresent(token -> {
			del(bearerToken);
			
			Jws<Claims> claims = Jwts.parser()
					.setSigningKey(Base64.getUrlEncoder().encodeToString(apiKey.getSecret().getBytes()))
					.parseClaimsJws(bearerToken); 
			
			System.out.println("revoke: " + UserContext.getSecurityContext().getAuthenticationScheme());
			
			HttpResponse httpResponse = RestResource.delete(System.getProperty(Properties.STORMPATH_API_ENDPOINT))
					.basicAuthorization(apiKey.getId(), apiKey.getSecret())
					.path("accessTokens")
					.path(UserContext.getSecurityContext().getAuthenticationScheme())
					.execute();
			
			if (httpResponse.getStatusCode() != Status.NO_CONTENT) {
				ObjectNode response = httpResponse.getEntity(ObjectNode.class);
				LOGGER.warn(response.toString()); 
			}
		});
	}
	
	public String verifyEmail(String emailVerificationToken) {	
		
		HttpResponse httpResponse = RestResource.post(System.getProperty(Properties.STORMPATH_API_ENDPOINT))
				.basicAuthorization(apiKey.getId(), apiKey.getSecret())
				.path("accounts")
				.path("emailVerificationTokens")
				.path(emailVerificationToken)
				.execute();
		
		ObjectNode response = httpResponse.getEntity(ObjectNode.class);

		if (httpResponse.getStatusCode() != Status.OK) {
			ErrorDTO error = new ErrorDTO(response.get("code").asInt(), response.get("developerMessage").asText());
			throw new ServiceException(error); 
		}
		
		return response.get("href").asText();
	}
	
	private String createJwt(String id, String subject, Date expiration) {
		return Jwts.builder()
        		.setId(id)
        		.setHeaderParam("typ", "JWT")
        		.setIssuer("nowellpoint.com")
        		.setSubject(subject)
        		.setIssuedAt(new Date(System.currentTimeMillis()))
        		.setExpiration(expiration)
        		.signWith(SignatureAlgorithm.HS256, Base64.getUrlEncoder().encodeToString(System.getProperty(Properties.STORMPATH_API_KEY_SECRET).getBytes()))
        		.compact();
		
	}
}