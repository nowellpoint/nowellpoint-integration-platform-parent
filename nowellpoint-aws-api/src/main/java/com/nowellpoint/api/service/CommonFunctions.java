package com.nowellpoint.api.service;

import java.sql.Date;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.ws.rs.core.MultivaluedMap;

import com.nowellpoint.api.dto.EnvironmentDTO;
import com.nowellpoint.api.dto.ServiceInstanceDTO;
import com.nowellpoint.api.dto.ServiceProviderDTO;
import com.nowellpoint.api.model.Service;
import com.nowellpoint.api.model.SimpleStorageService;
import com.nowellpoint.api.model.Targets;
import com.nowellpoint.api.model.dynamodb.UserProperties;
import com.nowellpoint.api.model.dynamodb.UserProperty;
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

public class CommonFunctions {
	
	@Inject
	private SalesforceService salesforceService;
	
	@Inject
	private ServiceProviderService serviceProviderService;
	
	private static final String IS_ACTIVE = "isActive";
	private static final String API_VERSION = "apiVersion";
	private static final String AUTH_ENDPOINT = "authEndpoint";
	private static final String USERNAME = "username";
	private static final String PASSWORD = "password";
	private static final String SECURITY_TOKEN_PROPERTY = "security.token";
	private static final String SECURITY_TOKEN_PARAM = "securityToken";
	private static final String REFRESH_TOKEN_PROPERTY = "refresh.token";
	private static final String NAME_PARAM = "name";
	private static final String TAG_PARAM = "tag";
	private static final String BUCKET_NAME_PARAM = "bucketName";
	private static final String AWS_ACCESS_KEY_PARAM = "awsAccessKey";
	private static final String AWS_SECRET_ACCESS_KEY_PARAM = "awsSecretAccessKey";
	private static final String AWS_ACCESS_KEY_PROPERTY = "aws.access.key";
	private static final String AWS_SECRET_ACCESS_KEY_PROPERTY = "aws.secret.access.key";
	
	/**************************************************************************************************************************
	 * 
	 * 
	 * @param environment
	 * @param parameters
	 * 
	 * 
	 **************************************************************************************************************************/
	
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
	
	/**************************************************************************************************************************
	 * 
	 * 
	 * @param environment
	 * @param parameters
	 * 
	 * 
	 **************************************************************************************************************************/
	
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
	
	/**************************************************************************************************************************
	 * 
	 * 
	 * @param key
	 * @param parameters
	 * @return
	 * 
	 * 
	 **************************************************************************************************************************/
	
	public void buildServiceInstance(String key, ServiceInstanceDTO serviceInstance, MultivaluedMap<String, String> parameters) {
		serviceInstance.setKey(key);
		
		if (parameters.containsKey(NAME_PARAM)) {
			serviceInstance.setName(parameters.getFirst(NAME_PARAM));
		}
		
		if (parameters.containsKey(TAG_PARAM)) {
			serviceInstance.setTag(parameters.getFirst(TAG_PARAM));
		}
		
		if (parameters.containsKey(BUCKET_NAME_PARAM) || parameters.containsKey(AWS_ACCESS_KEY_PARAM) || parameters.containsKey(AWS_SECRET_ACCESS_KEY_PARAM)) {
			if (serviceInstance.getTargets() == null) {
				serviceInstance.setTargets(new Targets());
			}
			if (serviceInstance.getTargets().getSimpleStorageService() == null) {
				serviceInstance.getTargets().setSimpleStorageService(new SimpleStorageService());
			}
		}
		
		if (parameters.containsKey(BUCKET_NAME_PARAM)) {
			serviceInstance.getTargets().getSimpleStorageService().setBucketName(parameters.getFirst(BUCKET_NAME_PARAM));
		}
		
		if (parameters.containsKey(AWS_ACCESS_KEY_PARAM)) {
			serviceInstance.getTargets().getSimpleStorageService().setAwsAccessKey(parameters.getFirst(AWS_ACCESS_KEY_PARAM));
		}
		
		if (parameters.containsKey(AWS_SECRET_ACCESS_KEY_PARAM)) {
			serviceInstance.getTargets().getSimpleStorageService().setAwsSecretAccessKey(parameters.getFirst(AWS_SECRET_ACCESS_KEY_PARAM));
		}
	}
	
	/**************************************************************************************************************************
	 * 
	 * @param subject
	 * @param key
	 * @param simpleStoreageService
	 * 
	 * 
	 **************************************************************************************************************************/
	
	public void saveAwsCredentials(String subject, String key, SimpleStorageService simpleStoreageService) {
		List<UserProperty> properties = new ArrayList<UserProperty>();
		
		UserProperty awsAccessKey = new UserProperty()
				.withSubject(key)
				.withKey(AWS_ACCESS_KEY_PROPERTY)
				.withValue(simpleStoreageService.getAwsAccessKey())
				.withLastModifiedBy(subject)
				.withLastModifiedDate(Date.from(Instant.now()));
		
		properties.add(awsAccessKey);
		
		UserProperty awsSecretAccessKey = new UserProperty()
				.withSubject(key)
				.withKey(AWS_SECRET_ACCESS_KEY_PROPERTY)
				.withValue(simpleStoreageService.getAwsSecretAccessKey())
				.withLastModifiedBy(subject)
				.withLastModifiedDate(Date.from(Instant.now()));
		
		properties.add(awsSecretAccessKey);
		
		UserProperties.batchSave(properties);
	}
	
	public ServiceInstanceDTO buildServiceInstance(String key) {
		ServiceProviderDTO serviceProvider = serviceProviderService.findByServiceKey(key);
		
		Service service = serviceProvider.getServices()
				.stream()
				.filter(s -> key.equals(s.getKey()))
				.findFirst()
				.get();
		
		ServiceInstanceDTO serviceInstance = new ServiceInstanceDTO();
		serviceInstance.setKey(UUID.randomUUID().toString().replace("-", ""));
		serviceInstance.setServiceType(service.getType());
		serviceInstance.setConfigurationPage(service.getConfigurationPage());
		serviceInstance.setProviderName(serviceProvider.getName());
		serviceInstance.setServiceName(service.getServiceName());
		serviceInstance.setProviderType(serviceProvider.getType());
		serviceInstance.setIsActive(Boolean.FALSE);
		serviceInstance.setAddedOn(Date.from(Instant.now()));
		serviceInstance.setUpdatedOn(Date.from(Instant.now()));
		//serviceInstance.setPlan(plan);
		
		return serviceInstance;
	}
}