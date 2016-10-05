package com.nowellpoint.api.service;

import static com.nowellpoint.util.Assert.isEmpty;
import static com.nowellpoint.util.Assert.isNull;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.sql.Date;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import javax.inject.Inject;
import javax.net.ssl.HttpsURLConnection;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.DeleteObjectsRequest.KeyVersion;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.nowellpoint.api.model.document.SimpleStorageService;
import com.nowellpoint.api.model.document.Targets;
import com.nowellpoint.api.model.dto.Environment;
import com.nowellpoint.api.model.dto.SalesforceConnector;
import com.nowellpoint.api.model.dto.ServiceInstanceDTO;
import com.nowellpoint.api.model.dto.UserInfo;
import com.nowellpoint.api.model.dynamodb.UserProperties;
import com.nowellpoint.api.model.dynamodb.UserProperty;
import com.nowellpoint.api.model.mapper.SalesforceConnectorModelMapper;
import com.nowellpoint.aws.model.admin.Properties;
import com.nowellpoint.client.sforce.Client;
import com.nowellpoint.client.sforce.GetIdentityRequest;
import com.nowellpoint.client.sforce.GetOrganizationRequest;
import com.nowellpoint.client.sforce.model.Identity;
import com.nowellpoint.client.sforce.model.LoginResult;
import com.nowellpoint.client.sforce.model.Organization;
import com.nowellpoint.client.sforce.model.Token;

/**************************************************************************************************************************
 * 
 * 
 * @author jherson
 *
 * 
 *************************************************************************************************************************/

public class SalesforceConnectorService extends SalesforceConnectorModelMapper {
	
	@Inject
	private SalesforceService salesforceService;
	
	@Inject
	private CommonFunctions commonFunctions;
	
	private static final String PASSWORD = "password";
	private static final String SECURITY_TOKEN_PROPERTY = "security.token";
	private static final String ACCESS_TOKEN_PROPERTY = "access.token";
	private static final String REFRESH_TOKEN_PROPERTY = "refresh.token";
	
	private static final AmazonS3 s3Client = new AmazonS3Client();
	
	/**
	 * 
	 * 
	 * constructor
	 * 
	 * 
	 */
	
	public SalesforceConnectorService() {
		
	}
	
	/**
	 *
	 * 
	 * @param subject
	 * @return all SalesforceConnectorDTO owned by @param subject
	 * 
	 * 
	 */
	
	public Set<SalesforceConnector> findAllByOwner() {
		return super.findAllByOwner();
	}
	
	/**
	 *
	 * 
	 * @param token
	 * @return the created SalesforceConnectorDTO
	 * 
	 *
	 */
	
	public SalesforceConnector createSalesforceConnector(Token token) {
		
		Client client = new Client();
		
		GetIdentityRequest getIdentityRequest = new GetIdentityRequest()
				.setAccessToken(token.getAccessToken())
				.setId(token.getId());
		
		Identity identity = client.getIdentity( getIdentityRequest );
		identity.getPhotos().setPicture( putImage( token.getAccessToken(), identity.getPhotos().getPicture() ) );
		identity.getPhotos().setThumbnail( putImage( token.getAccessToken(), identity.getPhotos().getThumbnail() ) );
		
		GetOrganizationRequest getOrganizationRequest = new GetOrganizationRequest()
				.setAccessToken(token.getAccessToken())
				.setOrganizationId(identity.getOrganizationId())
				.setSobjectUrl(identity.getUrls().getSobjects());
		
		Organization organization = client.getOrganization(getOrganizationRequest);
		
		SalesforceConnector resource = new SalesforceConnector();
		resource.setOrganization(organization);
		resource.setIdentity(identity);
		
		if (resource.getOwner() == null) {
			UserInfo owner = new UserInfo(getSubject());
			resource.setOwner(owner);
		}
		
		Environment environment = new Environment();
		environment.setKey(UUID.randomUUID().toString().replaceAll("-", ""));
		environment.setIdentityId(identity.getId());
		environment.setEmail(identity.getEmail());
		environment.setGrantType("token");
		environment.setIsActive(Boolean.TRUE);
		environment.setEnvironmentName("Production");
		environment.setIsReadOnly(Boolean.TRUE);
		environment.setIsSandbox(Boolean.FALSE);
		environment.setIsValid(Boolean.TRUE);
		environment.setAddedOn(Date.from(Instant.now()));
		environment.setUpdatedOn(Date.from(Instant.now()));
		environment.setUserId(identity.getUserId());
		environment.setUsername(identity.getUsername());
		environment.setOrganizationId(organization.getId());
		environment.setOrganizationName(organization.getName());
		environment.setServiceEndpoint(token.getInstanceUrl());
		environment.setAuthEndpoint("https://login.salesforce.com");
		environment.setApiVersion(System.getProperty(Properties.SALESFORCE_API_VERSION));
		
		resource.addEnvironment(environment);
		
		UserInfo userInfo = new UserInfo(getSubject());
		
		resource.setCreatedBy(userInfo);
		resource.setLastModifiedBy(userInfo);
		
		super.createSalesforceConnector( resource );
		
		List<UserProperty> properties = new ArrayList<UserProperty>();
		
		UserProperty accessTokenProperty = new UserProperty()
				.withSubject(environment.getKey())
				.withKey(ACCESS_TOKEN_PROPERTY)
				.withValue(token.getAccessToken())
				.withLastModifiedBy(getSubject())
				.withLastModifiedDate(Date.from(Instant.now()));
		
		properties.add(accessTokenProperty);
		
		UserProperty refreshTokenProperty = new UserProperty()
				.withSubject(environment.getKey())
				.withKey(REFRESH_TOKEN_PROPERTY)
				.withValue(token.getRefreshToken())
				.withLastModifiedBy(getSubject())
				.withLastModifiedDate(Date.from(Instant.now()));
		
		properties.add(refreshTokenProperty);
		
		UserProperties.batchSave(properties);
		
		return resource;
	}
	
	/**************************************************************************************************************************
	 *
	 * 
	 * @param id
	 * @param resource
	 * @return the modified SalesforceConnectorDTO
	 * 
	 *
	 *************************************************************************************************************************/
	
	public void updateSalesforceConnector(String id, SalesforceConnector salesforceConnector) {		
		SalesforceConnector original = findSalesforceConnector(id);
		
		salesforceConnector.setId(original.getId());
		salesforceConnector.setCreatedById(original.getCreatedById());
		salesforceConnector.setCreatedDate(original.getCreatedDate());
		salesforceConnector.setSystemCreationDate(original.getSystemCreationDate());
		salesforceConnector.setSystemModifiedDate(original.getSystemModifiedDate());
		
		if (isNull(salesforceConnector.getTag())) {
			salesforceConnector.setTag(original.getTag());
		} else if (isEmpty(salesforceConnector.getTag())) {
			salesforceConnector.setTag(null);
		}
		
		salesforceConnector.setLastModifiedBy(new UserInfo(getSubject()));
		
		super.updateSalesforceConnector(salesforceConnector);
	}
	
	/**************************************************************************************************************************
	 * 
	 * 
	 * @param id
	 * 
	 * 
	 *************************************************************************************************************************/
	
	public void deleteSalesforceConnector(String id) {
		SalesforceConnector resource = findSalesforceConnector( id );
		
		super.deleteSalesforceConnector(resource);

		List<KeyVersion> keys = new ArrayList<KeyVersion>();
		keys.add(new KeyVersion(resource.getIdentity().getPhotos().getPicture().substring(resource.getIdentity().getPhotos().getPicture().lastIndexOf("/") + 1)));
		keys.add(new KeyVersion(resource.getIdentity().getPhotos().getThumbnail().substring(resource.getIdentity().getPhotos().getThumbnail().lastIndexOf("/") + 1)));

		DeleteObjectsRequest deleteObjectsRequest = new DeleteObjectsRequest("nowellpoint-profile-photos").withKeys(keys);

		s3Client.deleteObjects(deleteObjectsRequest);
		
		List<UserProperty> properties = new ArrayList<UserProperty>();
		
		resource.getEnvironments().stream().forEach(e -> {
			
			if (e.getIsSandbox()) {
				UserProperty passwordProperty = new UserProperty()
						.withSubject(e.getKey())
						.withKey(PASSWORD);
				
				properties.add(passwordProperty);
				
				UserProperty securityTokenProperty = new UserProperty()
						.withSubject(e.getKey())
						.withKey(SECURITY_TOKEN_PROPERTY);
				
				properties.add(securityTokenProperty);
				
			} else {
				UserProperty accessTokenProperty = new UserProperty()
						.withSubject(resource.getId().toString())
						.withKey(ACCESS_TOKEN_PROPERTY);
				
				properties.add(accessTokenProperty);
				
				UserProperty refreshTokenProperty = new UserProperty()
						.withSubject(resource.getId().toString())
						.withKey(REFRESH_TOKEN_PROPERTY);
				
				properties.add(refreshTokenProperty);
			}
			
		});
		
		UserProperties.batchDelete(properties);
	}
	
	/**
	 * 
	 * 
	 * @param id
	 * @return
	 * 
	 * 
	 */
	
	public SalesforceConnector findSalesforceConnector(String id) {		
		return super.findSalesforceConnector(id);
	}
	
	/**
	 * 
	 * 
	 * @param id
	 * @return
	 * 
	 * 
	 */
	
	public Set<Environment> getEnvironments(String id) {
		SalesforceConnector salesforceConnector = findSalesforceConnector(id);
		return salesforceConnector.getEnvironments();
	}
	
	/**
	 * 
	 * 
	 * @param id
	 * @param key
	 * @return
	 * 
	 * 
	 */
	
	public Environment getEnvironment(String id, String key) {
		SalesforceConnector resource = findSalesforceConnector(id);
		
		Environment environment = resource.getEnvironments()
				.stream()
				.filter(p -> p.getKey().equals(key))
				.findFirst()
				.get();
		
		return environment;
	}
	
	/**
	 * 
	 * 
	 * @param id
	 * @param environment
	 * @return
	 * @throws ServiceException
	 * 
	 * 
	 */
	
	public void addEnvironment(String id, Environment environment) throws ServiceException {
		LoginResult loginResult = salesforceService.login(environment.getAuthEndpoint(), environment.getUsername(), environment.getPassword(), environment.getSecurityToken());

		SalesforceConnector resource = findSalesforceConnector(id);
		
		if (resource.getEnvironments() != null && resource.getEnvironments().stream().filter(e -> e.getOrganizationId().equals(loginResult.getOrganizationId())).findFirst().isPresent()) {
			throw new ServiceException(Response.Status.CONFLICT, String.format("Unable to add new environment. Conflict with existing organization: %s with Id: ", loginResult.getOrganizationName(), loginResult.getOrganizationId()));
		}
		
		environment.setKey(UUID.randomUUID().toString().replace("-", ""));
		environment.setEmail(loginResult.getEmail());
		environment.setIdentityId(loginResult.getId());
		environment.setGrantType("password");
		environment.setIsActive(Boolean.TRUE);
		environment.setIsReadOnly(Boolean.FALSE);
		environment.setIsValid(Boolean.TRUE);
		environment.setAddedOn(Date.from(Instant.now()));
		environment.setUpdatedOn(Date.from(Instant.now()));
		environment.setApiVersion(System.getProperty(Properties.SALESFORCE_API_VERSION));
		environment.setIsSandbox(Boolean.TRUE);
		environment.setUserId(loginResult.getUserId());
		environment.setUsername(loginResult.getUserName());
		environment.setOrganizationId(loginResult.getOrganizationId());
		environment.setOrganizationName(loginResult.getOrganizationName());
		environment.setServiceEndpoint(loginResult.getServiceEndpoint());
		
		List<UserProperty> properties = commonFunctions.getEnvironmentUserProperties(getSubject(), environment);
		
		UserProperties.batchSave(properties);
		
		environment.setPassword(null);
		environment.setSecurityToken(null);
		
		resource.addEnvironment(environment);
		
		updateSalesforceConnector(id, resource);
	}
	
	/**
	 * 
	 * 
	 * @param id
	 * @param key
	 * @param environment
	 * @return
	 * 
	 * 
	 */
	
	public void updateEnvironment(String id, String key, Environment environment) {
		SalesforceConnector salesforceConnector = findSalesforceConnector( id );
		
		environment.setKey(key);
		
		updateEnvironment(salesforceConnector, environment);

	} 

	/**
	 * 
	 * 
	 * @param resource
	 * @param environment
	 * 
	 * 
	 */
	
	public void updateEnvironment(SalesforceConnector resource, Environment environment) {
		
		Environment original = resource.getEnvironments()
				.stream()
				.filter(e -> environment.getKey().equals(e.getKey()))
				.findFirst()
				.get();
		
		resource.getEnvironments().removeIf(e -> environment.getKey().equals(e.getKey()));
		
		environment.setAddedOn(original.getAddedOn());
		environment.setUpdatedOn(Date.from(Instant.now()));
		environment.setIsReadOnly(original.getIsReadOnly());
		environment.setIsSandbox(original.getIsSandbox());
		environment.setApiVersion(original.getApiVersion());
		environment.setTestMessage(original.getTestMessage());
		environment.setGrantType(original.getGrantType());
		
		if (environment.getIsActive()) {
			LoginResult loginResult = salesforceService.login(environment.getAuthEndpoint(), environment.getUsername(), environment.getPassword(), environment.getSecurityToken());
			
			if (! loginResult.getOrganizationId().equals(original.getOrganizationId()) 
					&& resource.getEnvironments().stream().filter(e -> e.getOrganizationId().equals(loginResult.getOrganizationId())).findFirst().isPresent()) {
				
				throw new ServiceException(String.format("Unable to update environment. Conflict with existing organization: %s", loginResult.getOrganizationId()));
			}
			
			environment.setIdentityId(loginResult.getId());
			environment.setEmail(loginResult.getEmail());
			environment.setUserId(loginResult.getUserId());
			environment.setOrganizationId(loginResult.getOrganizationId());
			environment.setOrganizationName(loginResult.getOrganizationName());
			environment.setServiceEndpoint(loginResult.getServiceEndpoint());
			environment.setIsValid(Boolean.TRUE);
		} else {
			environment.setIdentityId(original.getIdentityId());
			environment.setEmail(original.getEmail());
			environment.setUserId(original.getUserId());
			environment.setOrganizationId(original.getOrganizationId());
			environment.setOrganizationName(original.getOrganizationName());
			environment.setServiceEndpoint(original.getServiceEndpoint());
			environment.setIsValid(Boolean.FALSE);
		}
		
		List<UserProperty> properties = commonFunctions.getEnvironmentUserProperties(getSubject(), environment);
		
		UserProperties.batchSave(properties);
		
		environment.setPassword(null);
		environment.setSecurityToken(null);
		
		resource.addEnvironment(environment);
		
		updateSalesforceConnector(resource.getId(), resource);
	}
	
	/**
	 * 
	 * 
	 * @param id
	 * @param key
	 * @param parameters
	 * @return updated EnvironmentDTO
	 * 
	 * 
	 */
	
	public Environment updateEnvironment(String id, String key, MultivaluedMap<String, String> parameters) {
		
		SalesforceConnector resource = findSalesforceConnector( id );
		
		Environment environment = resource.getEnvironments()
				.stream()
				.filter(e -> key.equals(e.getKey()))
				.findFirst()
				.get();
		
		if (parameters.containsKey("test") || Boolean.valueOf(parameters.getFirst("test"))) {
			
			resource.getEnvironments().removeIf(e -> key.equals(e.getKey()));
			
			commonFunctions.testConnection(environment, parameters);
			
			resource.addEnvironment(environment);
			
			updateSalesforceConnector( id, resource );
			
		} else {
			
			commonFunctions.updateEnvironment(environment, parameters);
			
			updateEnvironment(resource, environment);
		}
		
		return environment;
	}
	
	/**
	 * 
	 * 
	 * @param id
	 * @param key
	 * 
	 * 
	 */
	
	public void removeEnvironment(String id, String key) {
		SalesforceConnector resource = findSalesforceConnector(id);
		
		Environment environment = resource.getEnvironments()
				.stream()
				.filter(e -> key.equals(e.getKey()))
				.findFirst()
				.get();
		
		List<UserProperty> properties = commonFunctions.getEnvironmentUserProperties(getSubject(), environment);
		
		UserProperties.batchDelete(properties);
		
		resource.getEnvironments().removeIf(e -> key.equals(e.getKey()));
		
		updateSalesforceConnector(id, resource);
	} 
	
	/**
	 * 
	 * 
	 * @param id
	 * @param key
	 * @return
	 * 
	 * 
	 */
	
	public ServiceInstanceDTO getServiceInstance(String id, String key) {
		SalesforceConnector resource = findSalesforceConnector(id);
		
		ServiceInstanceDTO serviceInstance = resource.getServiceInstances()
				.stream()
				.filter(s -> key.equals(s.getKey()))
				.findFirst()
				.get();
		
		return serviceInstance;
		
	}
	
	/**
	 * 
	 * 
	 * @param id
	 * @param serviceProviderId
	 * @param serviceType
	 * @param code
	 * @return
	 * 
	 * 
	 */
	
	public ServiceInstanceDTO addServiceInstance(String id, String key) {		
		SalesforceConnector resource = findSalesforceConnector(id);
		
		if (resource.getServiceInstances() == null) {
			resource.setServiceInstances(Collections.emptySet());
		}
		
		ServiceInstanceDTO serviceInstance = commonFunctions.buildServiceInstance(key);
		
		resource.getServiceInstances().stream().filter(s -> s.getServiceType().equals(serviceInstance.getServiceType())).findFirst().ifPresent( s-> {
			throw new ServiceException(String.format("Unable to add new environment. Service has already been added with type: %s", s.getServiceName()));
		});
		
		resource.addServiceInstance(serviceInstance);
		
		updateSalesforceConnector(id, resource);
		
		return serviceInstance;
	}
	
	/**
	 * 
	 * 
	 * @param id
	 * @param key
	 * @param serviceInstance
	 * @return
	 * 
	 * 
	 */
	
	public void updateServiceInstance(String id, String key, ServiceInstanceDTO serviceInstance) {		
		SalesforceConnector resource = findSalesforceConnector(id);
		
		if (resource.getServiceInstances() == null) {
			resource.setServiceInstances(Collections.emptySet());
		}
		
		Optional<ServiceInstanceDTO> query = resource.getServiceInstances()
				.stream()
				.filter(e -> key.equals(e.getKey()))
				.findFirst();
		
		if (! query.isPresent()) {
			return;
		}
		
		ServiceInstanceDTO original = query.get();
		
		resource.getServiceInstances().removeIf(e -> key.equals(e.getKey()));
		
		serviceInstance.setKey(key);
		serviceInstance.setAddedOn(original.getAddedOn());
		serviceInstance.setUpdatedOn(Date.from(Instant.now()));
		
		/**
		serviceInstance.setConfigurationPage(original.getConfigurationPage());
		serviceInstance.setProviderName(original.getProviderName());
		serviceInstance.setProviderType(original.getProviderType());
		serviceInstance.setServiceName(original.getServiceName());
		serviceInstance.setServiceType(original.getServiceType());
		
		if (serviceInstance.getIsActive() == null) {
			serviceInstance.setIsActive(original.getIsActive());
		}
		
		if (serviceInstance.getName() == null || serviceInstance.getName().trim().isEmpty()) {
			serviceInstance.setName(original.getName());
		}
		
		if (serviceInstance.getStatus() == null) {
			serviceInstance.setStatus(original.getStatus());
		}
		
		if (serviceInstance.getTag() == null || serviceInstance.getTag().) */
		
		Optional<SimpleStorageService> simpleStoreageService = Optional.of(serviceInstance)
				.map(ServiceInstanceDTO::getTargets)
				.map(Targets::getSimpleStorageService);
		
		if (simpleStoreageService.isPresent()) {
			
			commonFunctions.saveAwsCredentials(getSubject(), key, simpleStoreageService.get());
			
			serviceInstance.getTargets().getSimpleStorageService().setAwsAccessKey(null);
			serviceInstance.getTargets().getSimpleStorageService().setAwsSecretAccessKey(null);
		}
		
		resource.addServiceInstance(serviceInstance);
		
		updateSalesforceConnector(id, resource);
	}
	
	/**
	 * 
	 * 
	 * @param id
	 * @param key
	 * @param parameters
	 * @return ServiceInstanceDTO
	 * 
	 * 
	 */
	
	public ServiceInstanceDTO updateServiceInstance(String id, String key, MultivaluedMap<String, String> parameters) {		
		SalesforceConnector resource = findSalesforceConnector(id);
		
		if (resource.getServiceInstances() == null) {
			resource.setServiceInstances(Collections.emptySet());
		}
		
		Optional<ServiceInstanceDTO> query = resource.getServiceInstances()
				.stream()
				.filter(e -> key.equals(e.getKey()))
				.findFirst();
		
		if (! query.isPresent()) {
			return null;
		}
		
		ServiceInstanceDTO serviceInstance = query.get();
		
		commonFunctions.buildServiceInstance(key, serviceInstance, parameters);
		
		updateServiceInstance(id, key, serviceInstance);
		
		return serviceInstance;
	}
	
	/**
	 * 
	 * 
	 * @param id
	 * @param key
	 * @return
	 * 
	 * 
	 */
	
	public SalesforceConnector removeServiceInstance(String id, String key) {		
		SalesforceConnector resource = findSalesforceConnector(id);
		
		resource.getServiceInstances().removeIf(p -> p.getKey().equals(key));

		updateSalesforceConnector(id, resource);
		
		return resource;
	}
	
	/**
	 * 
	 * 
	 * @param accessToken
	 * @param imageUrl
	 * @return
	 * 
	 * 
	 */
	
	private String putImage(String accessToken, String imageUrl) {
		
		try {
			URL url = new URL( imageUrl + "?oauth_token=" + accessToken );
			
			HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
			String contentType = connection.getHeaderField("Content-Type");
			
			ObjectMetadata objectMetadata = new ObjectMetadata();
	    	objectMetadata.setContentLength(connection.getContentLength());
	    	objectMetadata.setContentType(contentType);
	    	
	    	String key = UUID.randomUUID().toString().replace("-", "");
			
	    	PutObjectRequest putObjectRequest = new PutObjectRequest("nowellpoint-profile-photos", key, connection.getInputStream(), objectMetadata);
	    	
	    	s3Client.putObject(putObjectRequest);
	    	
	    	URI uri = UriBuilder.fromUri(System.getProperty(Properties.CLOUDFRONT_HOSTNAME))
					.path("{id}")
					.build(key);
			
			return uri.toString();
			
		} catch (IOException e) {
			throw new WebApplicationException(e, Status.INTERNAL_SERVER_ERROR);
		}
	}
}