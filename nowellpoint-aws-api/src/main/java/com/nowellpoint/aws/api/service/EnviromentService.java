package com.nowellpoint.aws.api.service;

import java.sql.Date;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.ws.rs.core.MultivaluedMap;

import com.nowellpoint.aws.api.dto.EnvironmentDTO;
import com.nowellpoint.aws.api.model.dynamodb.UserProperties;
import com.nowellpoint.aws.api.model.dynamodb.UserProperty;
import com.nowellpoint.aws.model.admin.Properties;
import com.nowellpoint.client.sforce.Authenticators;
import com.nowellpoint.client.sforce.Client;
import com.nowellpoint.client.sforce.GetOrganizationRequest;
import com.nowellpoint.client.sforce.OauthAuthenticationResponse;
import com.nowellpoint.client.sforce.OauthException;
import com.nowellpoint.client.sforce.OauthRequests;
import com.nowellpoint.client.sforce.RefreshTokenGrantRequest;
import com.nowellpoint.client.sforce.model.Identity;
import com.nowellpoint.client.sforce.model.LoginResult;
import com.nowellpoint.client.sforce.model.Organization;
import com.nowellpoint.client.sforce.model.Token;

public class EnviromentService {
	
	@Inject
	private SalesforceService salesforceService;
	
	private static final String IS_ACTIVE = "isActive";
	private static final String API_VERSION = "apiVersion";
	private static final String AUTH_ENDPOINT = "authEndpoint";
	private static final String USERNAME = "username";
	private static final String PASSWORD = "password";
	private static final String SECURITY_TOKEN_PROPERTY = "security.token";
	private static final String SECURITY_TOKEN_PARAM = "securityToken";
	private static final String REFRESH_TOKEN_PROPERTY = "refresh.token";
	
	public void updateEnvironment(EnvironmentDTO environment, MultivaluedMap<String, String> parameters) {
		
		if (parameters.containsKey(IS_ACTIVE)) {
			environment.setIsActive(Boolean.valueOf(parameters.getFirst(IS_ACTIVE)));
		}
		
		if (parameters.containsKey(API_VERSION)) {
			environment.setApiVersion(parameters.getFirst(API_VERSION));
		}
		
		if (parameters.containsKey(AUTH_ENDPOINT)) {
			environment.setAuthEndpoint(parameters.getFirst(AUTH_ENDPOINT));
		}
		
		if (parameters.containsKey(PASSWORD)) {
			environment.setPassword(parameters.getFirst(PASSWORD));
		}
		
		if (parameters.containsKey(USERNAME)) {
			environment.setUsername(parameters.getFirst(USERNAME));
		}
		
		if (parameters.containsKey(SECURITY_TOKEN_PARAM)) {
			environment.setSecurityToken(parameters.getFirst(SECURITY_TOKEN_PARAM));
		}
	}
	
	public void testConnection(EnvironmentDTO environment, MultivaluedMap<String, String> parameters) {
		
		try {
			UserProperty userProperty = new UserProperty();
			userProperty.setSubject(environment.getKey());
			
			Map<String, UserProperty> properties = UserProperties.query(userProperty)
					.stream()
					.collect(Collectors.toMap(UserProperty::getKey, p -> p));

			if (environment.getIsSandbox()) {
				
				String authEndpoint = parameters.containsKey(AUTH_ENDPOINT) ? parameters.getFirst(AUTH_ENDPOINT) : environment.getAuthEndpoint();
				String username = parameters.containsKey(USERNAME) ? parameters.getFirst(USERNAME) : environment.getUsername();
				String password = parameters.containsKey(PASSWORD) ? parameters.getFirst(PASSWORD) : properties.get(PASSWORD).getValue();
				String securityToken = parameters.containsKey(SECURITY_TOKEN_PARAM) ? parameters.getFirst(SECURITY_TOKEN_PARAM) : properties.get(SECURITY_TOKEN_PROPERTY).getValue();
				
				LoginResult loginResult = salesforceService.login(authEndpoint, username, password, securityToken);		
				environment.setUserId(loginResult.getUserId());
				environment.setOrganizationId(loginResult.getOrganizationId());
				environment.setOrganizationName(loginResult.getOrganizationName());
				environment.setServiceEndpoint(loginResult.getServiceEndpoint());
				
			} else {
				
				String refreshToken = properties.containsKey(REFRESH_TOKEN_PROPERTY) ? properties.get(REFRESH_TOKEN_PROPERTY).getValue() : null;

				RefreshTokenGrantRequest request = OauthRequests.REFRESH_TOKEN_GRANT_REQUEST.builder()
						.setClientId(System.getProperty(Properties.SALESFORCE_CLIENT_ID))
						.setClientSecret(System.getProperty(Properties.SALESFORCE_CLIENT_SECRET))
						.setRefreshToken(refreshToken)
						.build();
				
				OauthAuthenticationResponse authenticationResponse = Authenticators.REFRESH_TOKEN_GRANT_AUTHENTICATOR
						.authenticate(request);
				
				Token token = authenticationResponse.getToken();
				Identity identity = authenticationResponse.getIdentity();
				
				Client client = new Client();

				GetOrganizationRequest getOrganizationRequest = new GetOrganizationRequest()
						.setAccessToken(token.getAccessToken())
						.setOrganizationId(identity.getOrganizationId())
						.setSobjectUrl(identity.getUrls().getSobjects());
				
				Organization organization = client.getOrganization(getOrganizationRequest);
				
				environment.setUserId(identity.getUserId());
				environment.setOrganizationId(identity.getOrganizationId());
				environment.setOrganizationName(organization.getName());
				environment.setServiceEndpoint(token.getInstanceUrl());
				environment.setIsValid(Boolean.TRUE);
				
			}
			environment.setIsValid(Boolean.TRUE);
		} catch (OauthException e) {
			environment.setIsValid(Boolean.FALSE);
			environment.setTestMessage(e.getErrorDescription());
		} catch (ServiceException e) {
			environment.setIsValid(Boolean.FALSE);
			environment.setTestMessage(e.getError().getMessage());
		} catch (Exception e) {
			environment.setIsValid(Boolean.FALSE);
			environment.setTestMessage(e.getMessage());
		}
		
	}

	/**************************************************************************************************************************
	 * 
	 * 
	 * @param environment
	 * @return
	 * 
	 * 
	 *************************************************************************************************************************/
	
	public List<UserProperty> getEnvironmentUserProperties(String subject, EnvironmentDTO environment) {
		List<UserProperty> properties = new ArrayList<UserProperty>();
		
		UserProperty accessTokenProperty = new UserProperty()
				.withSubject(environment.getKey())
				.withKey(PASSWORD)
				.withValue(environment.getPassword())
				.withLastModifiedBy(subject)
				.withLastModifiedDate(Date.from(Instant.now()));
		
		properties.add(accessTokenProperty);
		
		UserProperty refreshTokenProperty = new UserProperty()
				.withSubject(environment.getKey())
				.withKey(SECURITY_TOKEN_PROPERTY)
				.withValue(environment.getSecurityToken())
				.withLastModifiedBy(subject)
				.withLastModifiedDate(Date.from(Instant.now()));
		
		properties.add(refreshTokenProperty);
		
		return properties;
	}
}