package com.nowellpoint.api.rest.service;

import static com.sforce.soap.partner.Connector.newConnection;

import java.util.Optional;

import javax.ws.rs.ForbiddenException;

import com.nowellpoint.api.service.SalesforceService;
import com.nowellpoint.aws.data.AbstractCacheService;
import com.nowellpoint.util.DigitalSignature;
import com.nowellpoint.util.Properties;
import com.nowellpoint.client.sforce.Authenticators;
import com.nowellpoint.client.sforce.AuthorizationGrantRequest;
import com.nowellpoint.client.sforce.Client;
import com.nowellpoint.client.sforce.DescribeGlobalSobjectsRequest;
import com.nowellpoint.client.sforce.GetIdentityRequest;
import com.nowellpoint.client.sforce.GetOrganizationRequest;
import com.nowellpoint.client.sforce.GetUserRequest;
import com.nowellpoint.client.sforce.OauthAuthenticationResponse;
import com.nowellpoint.client.sforce.OauthRequests;
import com.nowellpoint.client.sforce.RefreshTokenGrantRequest;
import com.nowellpoint.client.sforce.model.Identity;
import com.nowellpoint.client.sforce.model.Organization;
import com.nowellpoint.client.sforce.model.Token;
import com.nowellpoint.client.sforce.model.User;
import com.nowellpoint.client.sforce.model.sobject.DescribeGlobalSobjectsResult;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.fault.LoginFault;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;

public class SalesforceServiceImpl extends AbstractCacheService implements SalesforceService {
	
	public SalesforceServiceImpl() {

	}
	
	/**
	 * 
	 * @param authEndpoint
	 * @param username
	 * @param password
	 * @param securityToken
	 * @return
	 */
	
	public Token login(String authEndpoint, String username, String password, String securityToken) throws ConnectionException {
		Optional.of(authEndpoint).orElseThrow(() -> new IllegalArgumentException("missing authEndpoint"));
		Optional.of(username).orElseThrow(() -> new IllegalArgumentException("missing username")); 
		Optional.of(password).orElseThrow(() -> new IllegalArgumentException("missing password")); 
		Optional.of(securityToken).orElseThrow(() -> new IllegalArgumentException("missing securityToken")); 
		
		ConnectorConfig config = new ConnectorConfig();
		config.setAuthEndpoint(String.format("%s/services/Soap/u/%s", authEndpoint, System.getProperty(Properties.SALESFORCE_API_VERSION)));
		config.setUsername(username);
		config.setPassword(password.concat(securityToken));
		
		try {
			PartnerConnection connection = newConnection(config);
			
			String id = String.format("%s/id/%s/%s", authEndpoint, connection.getUserInfo().getOrganizationId(), connection.getUserInfo().getUserId());
			String accessToken = connection.getConfig().getSessionId();
			String instanceUrl = connection.getConfig().getServiceEndpoint().substring(0, connection.getConfig().getServiceEndpoint().indexOf("/services"));
			String issuedAt = String.valueOf(connection.getServerTimestamp().getTimestamp().getTimeInMillis());
			String signature = DigitalSignature.sign(System.getenv("SALESFORCE_CLIENT_SECRET"), id.concat(issuedAt));
			
			Token token = new Token();
			token.setId(id);
			token.setAccessToken(accessToken);
			token.setInstanceUrl(instanceUrl);
			token.setIssuedAt(issuedAt);
			token.setTokenType("Bearer");
			token.setSignature(signature);
			
			return token;
			
		} catch (ConnectionException e) {
			if (e instanceof LoginFault) {
				LoginFault loginFault = (LoginFault) e;
				throw new ConnectionException(loginFault.getExceptionCode().name().concat(": ").concat(loginFault.getExceptionMessage()));
			} else {
				throw e;
			}
		}
	}
	
	public Token login(String encryptedConnectionString) {
		return null;
	}
	
	/**
	 * 
	 * @param id
	 * @return
	 */
	
	public DescribeGlobalSobjectsResult describe(String id) {
		
		Token result = get(Token.class, id);
		
		if (result == null) {
			throw new ForbiddenException("Invalid id or Session has expired");
		}
			
		GetIdentityRequest request = new GetIdentityRequest()
				.setAccessToken(result.getAccessToken())
				.setId(id);
			
		Client client = new Client();
			
		Identity identity = client.getIdentity(request);
			
		return describe(result.getAccessToken(), identity.getUrls().getSobjects());
	}
	
	/**
	 * 
	 * @param code
	 * @return
	 */
	
	public OauthAuthenticationResponse authenticate(String code) {		
		AuthorizationGrantRequest request = OauthRequests.AUTHORIZATION_GRANT_REQUEST.builder()
				.setClientId(System.getProperty(Properties.SALESFORCE_CLIENT_ID))
				.setClientSecret(System.getProperty(Properties.SALESFORCE_CLIENT_SECRET))
				.setCallbackUri(System.getProperty(Properties.SALESFORCE_REDIRECT_URI))
				.setCode(code)
				.build();
		
		OauthAuthenticationResponse response = Authenticators.AUTHORIZATION_GRANT_AUTHENTICATOR
				.authenticate(request);
			
		return response;
	}
	
	/**
	 * 
	 * @param accessToken
	 * @param userId
	 * @param sobjectUrl
	 * @return
	 */
	
	public User getUser(String accessToken, String userId, String sobjectUrl) {	
		GetUserRequest request = new GetUserRequest()
				.setAccessToken(accessToken)
				.setSobjectUrl(sobjectUrl)
				.setUserId(userId);
		
		Client client = new Client();
		
		User user = client.getUser(request);

		return user;
	}
	
	/**
	 * 
	 * @param accessToken
	 * @param organizationId
	 * @param sobjectUrl
	 * @return
	 */
	
	public Organization getOrganization(String accessToken, String organizationId, String sobjectUrl) {		
		GetOrganizationRequest request = new GetOrganizationRequest()
				.setAccessToken(accessToken)
				.setOrganizationId(organizationId)
				.setSobjectUrl(sobjectUrl);
		
		Client client = new Client();
		
		Organization organization = client.getOrganization(request);
		
		return organization;
	}
	
	/**
	 * 
	 * @param refreshToken
	 * @return
	 */
	
	public OauthAuthenticationResponse refreshToken(String refreshToken) {
		RefreshTokenGrantRequest request = OauthRequests.REFRESH_TOKEN_GRANT_REQUEST.builder()
				.setClientId(System.getProperty(Properties.SALESFORCE_CLIENT_ID))
				.setClientSecret(System.getProperty(Properties.SALESFORCE_CLIENT_SECRET))
				.setRefreshToken(refreshToken)
				.build();
		
		OauthAuthenticationResponse authenticationResponse = Authenticators.REFRESH_TOKEN_GRANT_AUTHENTICATOR
				.authenticate(request);
		
		return authenticationResponse;
	}
		
	/**
	 * 
	 * @param accessToken
	 * @param sobjectUrl
	 * @return
	 */
	
	private DescribeGlobalSobjectsResult describe(String accessToken, String sobjectUrl) {
		DescribeGlobalSobjectsRequest request = new DescribeGlobalSobjectsRequest()
				.setAccessToken(accessToken)
				.setSobjectsUrl(sobjectUrl);
		
		Client client = new Client();
		
		DescribeGlobalSobjectsResult result = client.describeGlobal(request);
		
		return result;
	}
}