package com.nowellpoint.api.service;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.nowellpoint.util.Assert.isEmpty;
import static com.nowellpoint.util.Assert.isNull;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.net.ssl.HttpsURLConnection;
import javax.validation.ValidationException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response.Status;

import org.bson.types.ObjectId;

import javax.ws.rs.core.UriBuilder;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.DeleteObjectsRequest.KeyVersion;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.mongodb.DBRef;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.nowellpoint.api.model.document.UserRef;
import com.nowellpoint.api.model.domain.Environment;
import com.nowellpoint.api.model.domain.SalesforceConnector;
import com.nowellpoint.api.model.domain.UserInfo;
import com.nowellpoint.api.model.dynamodb.UserProperties;
import com.nowellpoint.api.model.dynamodb.UserProperty;
import com.nowellpoint.api.model.mapper.SalesforceConnectorModelMapper;
import com.nowellpoint.util.Properties;
import com.nowellpoint.client.sforce.Client;
import com.nowellpoint.client.sforce.CountRequest;
import com.nowellpoint.client.sforce.DescribeGlobalSobjectsRequest;
import com.nowellpoint.client.sforce.DescribeSobjectRequest;
import com.nowellpoint.client.sforce.GetIdentityRequest;
import com.nowellpoint.client.sforce.GetOrganizationRequest;
import com.nowellpoint.client.sforce.OauthAuthenticationResponse;
import com.nowellpoint.client.sforce.OauthException;
import com.nowellpoint.client.sforce.ThemeRequest;
import com.nowellpoint.client.sforce.model.Count;
import com.nowellpoint.client.sforce.model.Identity;
import com.nowellpoint.client.sforce.model.LoginResult;
import com.nowellpoint.client.sforce.model.Organization;
import com.nowellpoint.client.sforce.model.Photos;
import com.nowellpoint.client.sforce.model.Theme;
import com.nowellpoint.client.sforce.model.Token;
import com.nowellpoint.client.sforce.model.sobject.DescribeGlobalSobjectsResult;
import com.nowellpoint.client.sforce.model.sobject.DescribeSobjectResult;
import com.nowellpoint.client.sforce.model.sobject.Sobject;
import com.nowellpoint.mongodb.document.DocumentNotFoundException;
import com.nowellpoint.mongodb.document.MongoDatastore;

/**
 * 
 * 
 * @author jherson
 *
 * 
 */

public class SalesforceConnectorService extends SalesforceConnectorModelMapper {
	
	@Inject
	private SalesforceService salesforceService;
	
	private static final String IS_ACTIVE = "isActive";
	private static final String API_VERSION = "apiVersion";
	private static final String AUTH_ENDPOINT = "authEndpoint";
	private static final String USERNAME = "username";
	private static final String SECURITY_TOKEN_PARAM = "securityToken";
	private static final String PASSWORD = "password";
	private static final String SECURITY_TOKEN_PROPERTY = "security.token";
	private static final String REFRESH_TOKEN_PROPERTY = "refresh.token";
	
	private static final AmazonS3 s3Client = new AmazonS3Client();
	
	/**
	 * 
	 */
	
	public SalesforceConnectorService() {
		
	}
	
	/**
	 * @return
	 */
	
	public Set<SalesforceConnector> findAllByOwner() {
		return super.findAllByOwner();
	}
	
	/**
	 * 
	 * @param token
	 * @return
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
		
		UserProperties.saveSalesforceTokens(getSubject(), environment.getKey(), token);
		
		return resource;
	}
	
	/**
	 * 
	 * @param id
	 * @param salesforceConnector
	 */
	
	public void updateSalesforceConnector(String id, SalesforceConnector salesforceConnector) {		
		SalesforceConnector original = findSalesforceConnector(id);
		
		salesforceConnector.setId(original.getId());
		salesforceConnector.setCreatedDate(original.getCreatedDate());
		salesforceConnector.setSystemCreatedDate(original.getSystemCreatedDate());
		salesforceConnector.setSystemModifiedDate(original.getSystemModifiedDate());
		
		if (isNull(salesforceConnector.getTag())) {
			salesforceConnector.setTag(original.getTag());
		} else if (isEmpty(salesforceConnector.getTag())) {
			salesforceConnector.setTag(null);
		}
		
		salesforceConnector.setLastModifiedBy(new UserInfo(getSubject()));
		
		super.updateSalesforceConnector(salesforceConnector);
	}
	
	/**
	 * 
	 * @param id
	 * 
	 */
	
	public void deleteSalesforceConnector(String id) {
		SalesforceConnector salesforceConnector = findSalesforceConnector( id );
		
		Photos photos = salesforceConnector.getIdentity().getPhotos();

		List<KeyVersion> keys = new ArrayList<KeyVersion>();
		keys.add(new KeyVersion(photos.getPicture().substring(photos.getPicture().lastIndexOf("/") + 1)));
		keys.add(new KeyVersion(photos.getThumbnail().substring(photos.getThumbnail().lastIndexOf("/") + 1)));

		DeleteObjectsRequest deleteObjectsRequest = new DeleteObjectsRequest("nowellpoint-profile-photos").withKeys(keys);

		s3Client.deleteObjects(deleteObjectsRequest);
		
		salesforceConnector.getEnvironments().stream().forEach(e -> {
			removeEnvironment(e);
		});
		
		super.deleteSalesforceConnector( salesforceConnector );
	}
	
	/**
	 * 
	 *  @param id
	 *  
	 */
	
	public SalesforceConnector findSalesforceConnector(String id) {		
		return super.findSalesforceConnector(id);
	}
	
	/**
	 * 
	 * @param id
	 * @return
	 */
	
	public Set<Environment> getEnvironments(String id) {
		SalesforceConnector salesforceConnector = findSalesforceConnector(id);
		return salesforceConnector.getEnvironments();
	}
	
	/**
	 * 
	 * @param id
	 * @param key
	 * @return
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
	 * @param id
	 * @param environment
	 */
	
	public void addEnvironment(String id, Environment environment) {
		LoginResult loginResult = salesforceService.login(environment.getAuthEndpoint(), environment.getUsername(), environment.getPassword(), environment.getSecurityToken());

		SalesforceConnector resource = findSalesforceConnector(id);
		
		if (resource.getEnvironments() != null && resource.getEnvironments().stream().filter(e -> e.getOrganizationId().equals(loginResult.getOrganizationId())).findFirst().isPresent()) {
			throw new ValidationException(String.format("Unable to add new environment. Conflict with existing organization: %s with Id: %s", loginResult.getOrganizationName(), loginResult.getOrganizationId()));
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
		
		UserProperties.saveSalesforceCredentials(getSubject(), environment.getKey(), environment.getPassword(), environment.getSecurityToken());
		
		environment.setPassword(null);
		environment.setSecurityToken(null);
		
		resource.addEnvironment(environment);
		
		updateSalesforceConnector(id, resource);
	}
	
	/**
	 * 
	 * @param id
	 * @param key
	 * @param environment
	 * 
	 */
	
	public void updateEnvironment(String id, String key, Environment environment) {
		SalesforceConnector salesforceConnector = findSalesforceConnector( id );
		
		environment.setKey(key);
		
		updateEnvironment(salesforceConnector, environment);

	} 

	/**
	 * 
	 * @param resource
	 * @param environment
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
				
				throw new ValidationException(String.format("Unable to update environment. Conflict with existing organization: %s", loginResult.getOrganizationId()));
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
		
		UserProperties.saveSalesforceCredentials(getSubject(), environment.getKey(), environment.getPassword(), environment.getSecurityToken());
		
		environment.setPassword(null);
		environment.setSecurityToken(null);
		
		resource.addEnvironment(environment);
		
		updateSalesforceConnector(resource.getId(), resource);
	}
	
	/**
	 * 
	 * @param id
	 * @param key
	 * @param parameters
	 * @return
	 */
	
	public Environment updateEnvironment(String id, String key, MultivaluedMap<String, String> parameters) {
		
		SalesforceConnector resource = findSalesforceConnector( id );
		
		Environment environment = resource.getEnvironments()
				.stream()
				.filter(e -> key.equals(e.getKey()))
				.findFirst()
				.get();
		
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
		
		updateEnvironment(resource, environment);
		
		return environment;
	}
	
	/**
	 * 
	 * @param id
	 * @param key
	 * @return
	 */
	
	public Environment testConnection(String id, String key) {		
		SalesforceConnector resource = findSalesforceConnector( id );
		
		Environment environment = resource.getEnvironments()
				.stream()
				.filter(e -> key.equals(e.getKey()))
				.findFirst()
				.get();
		
		Map<String, UserProperty> properties = UserProperties.queryBySubject(environment.getKey())
				.stream()
				.collect(Collectors.toMap(UserProperty::getKey, p -> p));
		
		if (environment.getIsSandbox()) {
			environment.setPassword(properties.get(PASSWORD).getValue());
			environment.setSecurityToken(properties.get(SECURITY_TOKEN_PROPERTY).getValue());
		} else {
			environment.setRefreshToken(properties.get(REFRESH_TOKEN_PROPERTY).getValue());
		}
		
		testConnection( environment );
		
		environment.setPassword(null);
		environment.setSecurityToken(null);
		environment.setRefreshToken(null);
		
		updateSalesforceConnector( id, resource );
		
		return environment;
	}
	
	public Environment buildEnvironment(String id, String key) {
		SalesforceConnector resource = findSalesforceConnector( id );
		
		Environment environment = resource.getEnvironments()
				.stream()
				.filter(e -> key.equals(e.getKey()))
				.findFirst()
				.get();
		
		Map<String, UserProperty> properties = UserProperties.queryBySubject(environment.getKey())
				.stream()
				.collect(Collectors.toMap(UserProperty::getKey, p -> p));
		
		if (environment.getIsSandbox()) {
			environment.setPassword(properties.get(PASSWORD).getValue());
			environment.setSecurityToken(properties.get(SECURITY_TOKEN_PROPERTY).getValue());
		} else {
			environment.setRefreshToken(properties.get(REFRESH_TOKEN_PROPERTY).getValue());
		}
		
		buildEnvironment( environment );
		
		environment.setPassword(null);
		environment.setSecurityToken(null);
		environment.setRefreshToken(null);
		
		updateSalesforceConnector( id, resource );
		
		return environment;
	}
	
	/**
	 * 
	 * @param id
	 * @param key
	 */
	
	public void removeEnvironment(String id, String key) {
		SalesforceConnector resource = findSalesforceConnector(id);
		
		Environment environment = resource.getEnvironments()
				.stream()
				.filter(e -> key.equals(e.getKey()))
				.findFirst()
				.get();
		
		removeEnvironment(environment);
		
		resource.getEnvironments().removeIf(e -> key.equals(e.getKey()));
		
		updateSalesforceConnector(id, resource);
	} 
	
	/**
	 * 
	 * @param environment
	 */
	
	private void removeEnvironment(Environment environment) {
		UserProperties.clear(environment.getKey());
		MongoDatastore.deleteMany( MongoDatastore.getCollectionName( com.nowellpoint.api.model.document.SObjectDetail.class ), eq ( "environmentKey", environment.getKey() ) );
	}
	
	/**
	 * 
	 * @param accessToken
	 * @param imageUrl
	 * @return
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
	
	private void buildEnvironment(Environment environment) {

		try {
			
			if (environment.getIsSandbox()) {
				
				String authEndpoint = environment.getAuthEndpoint();
				String username = environment.getUsername();
				String password = environment.getPassword();
				String securityToken = environment.getSecurityToken();
				
				LoginResult loginResult = salesforceService.login(authEndpoint, username, password, securityToken);		
				environment.setUserId(loginResult.getUserId());
				environment.setOrganizationId(loginResult.getOrganizationId());
				environment.setOrganizationName(loginResult.getOrganizationName());
				environment.setServiceEndpoint(loginResult.getServiceEndpoint());
				
				Client client = new Client();
				
				GetIdentityRequest getIdentityRequest = new GetIdentityRequest()
						.setAccessToken(loginResult.getSessionId())
						.setId(loginResult.getId());
				
				Identity identity = client.getIdentity(getIdentityRequest);
				
				DescribeGlobalSobjectsRequest describeGlobalSobjectsRequest = new DescribeGlobalSobjectsRequest()
						.setAccessToken(loginResult.getSessionId())
						.setSobjectsUrl(identity.getUrls().getSobjects());
				
				DescribeGlobalSobjectsResult describeGlobalSobjectsResult = client.describeGlobal(describeGlobalSobjectsRequest);
				
				environment.setSobjects(describeGlobalSobjectsResult.getSobjects().stream().collect(Collectors.toSet()));
				
				ThemeRequest themeRequest = new ThemeRequest()
						.withAccessToken(loginResult.getSessionId())
						.withRestEndpoint(identity.getUrls().getRest());
				
				Theme theme = client.getTheme(themeRequest);
				
				environment.setTheme(theme);
				
				describeSobjects(loginResult.getSessionId(), identity.getUrls().getSobjects(), identity.getUrls().getQuery(), describeGlobalSobjectsResult, environment.getKey());
				
			} else {
				
				String refreshToken = environment.getRefreshToken();
				
				OauthAuthenticationResponse authenticationResponse = salesforceService.refreshToken(refreshToken);
				
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
				
				DescribeGlobalSobjectsRequest describeGlobalSobjectsRequest = new DescribeGlobalSobjectsRequest()
						.setAccessToken(token.getAccessToken())
						.setSobjectsUrl(identity.getUrls().getSobjects());
				
				DescribeGlobalSobjectsResult describeGlobalSobjectsResult = client.describeGlobal(describeGlobalSobjectsRequest);
				
				environment.setSobjects(describeGlobalSobjectsResult.getSobjects().stream().collect(Collectors.toSet()));
				
				ThemeRequest themeRequest = new ThemeRequest()
						.withAccessToken(token.getAccessToken())
						.withRestEndpoint(identity.getUrls().getRest());
				
				Theme theme = client.getTheme(themeRequest);
				
				environment.setTheme(theme);
				
				describeSobjects(token.getAccessToken(), identity.getUrls().getSobjects(), identity.getUrls().getQuery(), describeGlobalSobjectsResult, environment.getKey());
			}
			environment.setIsValid(Boolean.TRUE);
		} catch (OauthException e) {
			environment.setIsValid(Boolean.FALSE);
			environment.setTestMessage(e.getErrorDescription());
		} catch (ValidationException e) {
			environment.setIsValid(Boolean.FALSE);
			environment.setTestMessage(e.getMessage());
		} catch (Exception e) {
			environment.setIsValid(Boolean.FALSE);
			environment.setTestMessage(e.getMessage());
		}	
	}
	
	private void describeSobjects(String accessToken, String sobjectsUrl, String queryUrl, DescribeGlobalSobjectsResult describeGlobalSobjectsResult, String environmentKey) throws InterruptedException, ExecutionException, JsonProcessingException {
		ExecutorService executor = Executors.newFixedThreadPool(describeGlobalSobjectsResult.getSobjects().size());
		
		final Client client = new Client();
		
		for (Sobject sobject : describeGlobalSobjectsResult.getSobjects()) {
			executor.submit(() -> {
				DescribeSobjectRequest describeSobjectRequest = new DescribeSobjectRequest()
						.withAccessToken(accessToken)
						.withSobjectsUrl(sobjectsUrl)
						.withSobject(sobject.getName());

				DescribeSobjectResult describeSobjectResult = client.describeSobject(describeSobjectRequest);
				
				CountRequest countRequest = new CountRequest()
						.withAccessToken(accessToken)
						.withQueryUrl(queryUrl)
						.withSobject(sobject.getName());
				
				Count count = client.getCount(countRequest);

				Date now = Date.from(Instant.now());
				
				UserRef user = new UserRef();
				String collectionName = MongoDatastore.getCollectionName( com.nowellpoint.api.model.document.AccountProfile.class );
				ObjectId id = new ObjectId( getSubject() );

				DBRef dbref = new DBRef( collectionName, id );
				user.setIdentity(dbref);

				com.nowellpoint.api.model.document.SObjectDetail sobjectDetail = null;
				try {
					sobjectDetail = MongoDatastore.findOne(com.nowellpoint.api.model.document.SObjectDetail.class, and ( eq ( "name", sobject.getName() ), eq ( "environmentKey", environmentKey ) ) );
				} catch (DocumentNotFoundException e) {
					sobjectDetail = new com.nowellpoint.api.model.document.SObjectDetail();
					sobjectDetail.setEnvironmentKey(environmentKey);
					sobjectDetail.setName(describeSobjectResult.getName());
					sobjectDetail.setCreatedDate(now);
					sobjectDetail.setSystemCreatedDate(now);
					sobjectDetail.setCreatedBy(user);
				}
				sobjectDetail.setTotalSize(count.getRecords().get(0).getExpr0());
				sobjectDetail.setLastModifiedBy(user);
				sobjectDetail.setLastModifiedDate(now);
				sobjectDetail.setSystemModifiedDate(now);
				sobjectDetail.setResult(describeSobjectResult);
				if (isNull(sobjectDetail.getId())) {
					MongoDatastore.insertOne(sobjectDetail);
				} else {
					MongoDatastore.replaceOne(sobjectDetail);
				}
			});
		}
		
		executor.shutdown();
		executor.awaitTermination(30, TimeUnit.SECONDS);
	}
	
	private void testConnection(Environment environment) {

		try {
			
			if (environment.getIsSandbox()) {
				
				String authEndpoint = environment.getAuthEndpoint();
				String username = environment.getUsername();
				String password = environment.getPassword();
				String securityToken = environment.getSecurityToken();
				
				LoginResult loginResult = salesforceService.login(authEndpoint, username, password, securityToken);		
				
				environment.setUserId(loginResult.getUserId());
				environment.setOrganizationId(loginResult.getOrganizationId());
				environment.setOrganizationName(loginResult.getOrganizationName());
				environment.setServiceEndpoint(loginResult.getServiceEndpoint());
				
			} else {
				
				String refreshToken = environment.getRefreshToken();
				
				OauthAuthenticationResponse authenticationResponse = salesforceService.refreshToken(refreshToken);
				
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
			}
			environment.setIsValid(Boolean.TRUE);
		} catch (OauthException e) {
			environment.setIsValid(Boolean.FALSE);
			environment.setTestMessage(e.getErrorDescription());
		} catch (ValidationException e) {
			environment.setIsValid(Boolean.FALSE);
			environment.setTestMessage(e.getMessage());
		} catch (Exception e) {
			environment.setIsValid(Boolean.FALSE);
			environment.setTestMessage(e.getMessage());
		}			
	}
}