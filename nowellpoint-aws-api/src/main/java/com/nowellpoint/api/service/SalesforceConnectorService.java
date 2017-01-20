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
import com.mongodb.client.FindIterable;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.nowellpoint.api.model.document.UserRef;
import com.nowellpoint.api.model.domain.Instance;
import com.nowellpoint.api.model.domain.SalesforceConnector;
import com.nowellpoint.api.model.domain.SalesforceConnectorList;
import com.nowellpoint.api.model.domain.UserInfo;
import com.nowellpoint.api.model.dynamodb.UserProperties;
import com.nowellpoint.api.model.dynamodb.UserProperty;
import com.nowellpoint.api.util.UserContext;
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
import com.nowellpoint.mongodb.document.MongoDocumentService;

/**
 * 
 * 
 * @author jherson
 *
 * 
 */

public class SalesforceConnectorService {
	
	private MongoDocumentService mongoDocumentService = new MongoDocumentService();
	
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
	
	public SalesforceConnectorList findAllByOwner(String ownerId) {
		
		FindIterable<com.nowellpoint.api.model.document.SalesforceConnector> documents = mongoDocumentService.find(com.nowellpoint.api.model.document.SalesforceConnector.class,
				eq ( "owner.identity", new DBRef( MongoDatastore.getCollectionName( com.nowellpoint.api.model.document.AccountProfile.class ), 
						new ObjectId( ownerId ) ) ) );
		
		SalesforceConnectorList resources = new SalesforceConnectorList(documents);
		
		return resources;
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
		
		UserInfo userInfo = new UserInfo(UserContext.getPrincipal().getName());
		
		if (resource.getOwner() == null) {
			resource.setOwner(userInfo);
		}
		
		Instance instance = new Instance();
		instance.setKey(UUID.randomUUID().toString().replaceAll("-", ""));
		instance.setIdentityId(identity.getId());
		instance.setEmail(identity.getEmail());
		instance.setGrantType("token");
		instance.setIsActive(Boolean.TRUE);
		instance.setEnvironmentName("Production");
		instance.setIsReadOnly(Boolean.TRUE);
		instance.setIsSandbox(Boolean.FALSE);
		instance.setIsValid(Boolean.TRUE);
		instance.setAddedOn(Date.from(Instant.now()));
		instance.setUpdatedOn(Date.from(Instant.now()));
		instance.setUserId(identity.getUserId());
		instance.setUsername(identity.getUsername());
		instance.setOrganizationId(organization.getId());
		instance.setOrganizationName(organization.getName());
		instance.setServiceEndpoint(token.getInstanceUrl());
		instance.setAuthEndpoint("https://login.salesforce.com");
		instance.setApiVersion(System.getProperty(Properties.SALESFORCE_API_VERSION));
		
		resource.addEnvironment(instance);
		
		Date now = Date.from(Instant.now());
		
		resource.setCreatedDate(now);
		resource.setCreatedBy(userInfo);
		resource.setLastModifiedDate(now);
		resource.setLastModifiedBy(userInfo);
		resource.setSystemCreatedDate(now);
		resource.setSystemModifiedDate(now);
		
		mongoDocumentService.create(resource.toDocument());
		
		UserProperties.saveSalesforceTokens(UserContext.getPrincipal().getName(), instance.getKey(), token);
		
		return resource;
	}
	
	/**
	 * 
	 * @param id
	 * @param salesforceConnector
	 */
	
	public void updateSalesforceConnector(String id, SalesforceConnector salesforceConnector) {		
		SalesforceConnector original = findById(id);
		
		salesforceConnector.setId(original.getId());
		salesforceConnector.setCreatedDate(original.getCreatedDate());
		salesforceConnector.setSystemCreatedDate(original.getSystemCreatedDate());
		salesforceConnector.setSystemModifiedDate(original.getSystemModifiedDate());
		
		if (isNull(salesforceConnector.getTag())) {
			salesforceConnector.setTag(original.getTag());
		} else if (isEmpty(salesforceConnector.getTag())) {
			salesforceConnector.setTag(null);
		}
		
		UserInfo userInfo = new UserInfo(UserContext.getPrincipal().getName());
		
		Date now = Date.from(Instant.now());
		
		salesforceConnector.setLastModifiedDate(now);
		salesforceConnector.setLastModifiedBy(userInfo);
		salesforceConnector.setSystemModifiedDate(now);
		
		mongoDocumentService.replace(salesforceConnector.toDocument());
	}
	
	/**
	 * 
	 * @param id
	 * 
	 */
	
	public void deleteSalesforceConnector(String id) {
		SalesforceConnector salesforceConnector = findById( id );
		
		Photos photos = salesforceConnector.getIdentity().getPhotos();

		List<KeyVersion> keys = new ArrayList<KeyVersion>();
		keys.add(new KeyVersion(photos.getPicture().substring(photos.getPicture().lastIndexOf("/") + 1)));
		keys.add(new KeyVersion(photos.getThumbnail().substring(photos.getThumbnail().lastIndexOf("/") + 1)));

		DeleteObjectsRequest deleteObjectsRequest = new DeleteObjectsRequest("nowellpoint-profile-photos").withKeys(keys);

		s3Client.deleteObjects(deleteObjectsRequest);
		
		salesforceConnector.getInstances().stream().forEach(e -> {
			removeInstance(e);
		});
		
		mongoDocumentService.delete(salesforceConnector.toDocument());
	}
	
	/**
	 * 
	 *  @param id
	 *  
	 */
	
	public SalesforceConnector findById(String id) {		
		com.nowellpoint.api.model.document.SalesforceConnector document = mongoDocumentService.find(com.nowellpoint.api.model.document.SalesforceConnector.class, new ObjectId( id ) );
		SalesforceConnector resource = new SalesforceConnector( document );
		return resource;
	}
	
	/**
	 * 
	 * @param id
	 * @return
	 */
	
	public Set<Instance> getInstances(String id) {
		SalesforceConnector salesforceConnector = findById(id);
		return salesforceConnector.getInstances();
	}
	
	/**
	 * 
	 * @param id
	 * @param key
	 * @return
	 */
	
	public Instance getInstance(String id, String key) {
		SalesforceConnector resource = findById(id);
		
		Instance instance = resource.getInstances()
				.stream()
				.filter(p -> p.getKey().equals(key))
				.findFirst()
				.get();
		
		return instance;
	}
	
	/**
	 * 
	 * @param id
	 * @param instance
	 */
	
	public void addInstance(String id, Instance instance) {
		LoginResult loginResult = salesforceService.login(instance.getAuthEndpoint(), instance.getUsername(), instance.getPassword(), instance.getSecurityToken());

		SalesforceConnector resource = findById(id);
		
		if (resource.getInstances() != null && resource.getInstances().stream().filter(e -> e.getOrganizationId().equals(loginResult.getOrganizationId())).findFirst().isPresent()) {
			throw new ValidationException(String.format("Unable to add new environment. Conflict with existing organization: %s with Id: %s", loginResult.getOrganizationName(), loginResult.getOrganizationId()));
		}
		
		instance.setKey(UUID.randomUUID().toString().replace("-", ""));
		instance.setEmail(loginResult.getEmail());
		instance.setIdentityId(loginResult.getId());
		instance.setGrantType("password");
		instance.setIsActive(Boolean.TRUE);
		instance.setIsReadOnly(Boolean.FALSE);
		instance.setIsValid(Boolean.TRUE);
		instance.setAddedOn(Date.from(Instant.now()));
		instance.setUpdatedOn(Date.from(Instant.now()));
		instance.setApiVersion(System.getProperty(Properties.SALESFORCE_API_VERSION));
		instance.setIsSandbox(Boolean.TRUE);
		instance.setUserId(loginResult.getUserId());
		instance.setUsername(loginResult.getUserName());
		instance.setOrganizationId(loginResult.getOrganizationId());
		instance.setOrganizationName(loginResult.getOrganizationName());
		instance.setServiceEndpoint(loginResult.getServiceEndpoint());
		
		UserProperties.saveSalesforceCredentials(UserContext.getPrincipal().getName(), instance.getKey(), instance.getPassword(), instance.getSecurityToken());
		
		instance.setPassword(null);
		instance.setSecurityToken(null);
		
		resource.addEnvironment(instance);
		
		updateSalesforceConnector(id, resource);
	}
	
	/**
	 * 
	 * @param id
	 * @param key
	 * @param instance
	 * 
	 */
	
	public void updateInstance(String id, String key, Instance instance) {
		SalesforceConnector salesforceConnector = findById( id );
		
		instance.setKey(key);
		
		updateInstance(salesforceConnector, instance);

	} 

	/**
	 * 
	 * @param resource
	 * @param instance
	 * 
	 */
	
	public void updateInstance(SalesforceConnector resource, Instance instance) {
		
		Instance original = resource.getInstances()
				.stream()
				.filter(e -> instance.getKey().equals(e.getKey()))
				.findFirst()
				.get();
		
		resource.getInstances().removeIf(e -> instance.getKey().equals(e.getKey()));
		
		instance.setAddedOn(original.getAddedOn());
		instance.setUpdatedOn(Date.from(Instant.now()));
		instance.setIsReadOnly(original.getIsReadOnly());
		instance.setIsSandbox(original.getIsSandbox());
		instance.setApiVersion(original.getApiVersion());
		instance.setTestMessage(original.getTestMessage());
		instance.setGrantType(original.getGrantType());
		instance.setTheme(original.getTheme());
		instance.setSobjects(original.getSobjects());
		
		if (instance.getIsActive()) {
			LoginResult loginResult = salesforceService.login(instance.getAuthEndpoint(), instance.getUsername(), instance.getPassword(), instance.getSecurityToken());
			
			if (! loginResult.getOrganizationId().equals(original.getOrganizationId()) 
					&& resource.getInstances().stream().filter(e -> e.getOrganizationId().equals(loginResult.getOrganizationId())).findFirst().isPresent()) {
				
				throw new ValidationException(String.format("Unable to update environment. Conflict with existing organization: %s", loginResult.getOrganizationId()));
			}
			
			instance.setIdentityId(loginResult.getId());
			instance.setEmail(loginResult.getEmail());
			instance.setUserId(loginResult.getUserId());
			instance.setOrganizationId(loginResult.getOrganizationId());
			instance.setOrganizationName(loginResult.getOrganizationName());
			instance.setServiceEndpoint(loginResult.getServiceEndpoint());
			instance.setIsValid(Boolean.TRUE);
		} else {
			instance.setIdentityId(original.getIdentityId());
			instance.setEmail(original.getEmail());
			instance.setUserId(original.getUserId());
			instance.setOrganizationId(original.getOrganizationId());
			instance.setOrganizationName(original.getOrganizationName());
			instance.setServiceEndpoint(original.getServiceEndpoint());
			instance.setIsValid(Boolean.FALSE);
		}
		
		UserProperties.saveSalesforceCredentials(UserContext.getPrincipal().getName(), instance.getKey(), instance.getPassword(), instance.getSecurityToken());
		
		instance.setPassword(null);
		instance.setSecurityToken(null);
		
		resource.addEnvironment(instance);
		
		updateSalesforceConnector(resource.getId(), resource);
	}
	
	/**
	 * 
	 * @param id
	 * @param key
	 * @param parameters
	 * @return
	 */
	
	public Instance updateInstance(String id, String key, MultivaluedMap<String, String> parameters) {
		
		SalesforceConnector resource = findById( id );
		
		Instance instance = resource.getInstances()
				.stream()
				.filter(e -> key.equals(e.getKey()))
				.findFirst()
				.get();
		
		if (parameters.containsKey(IS_ACTIVE)) {
			instance.setIsActive(Boolean.valueOf(parameters.getFirst(IS_ACTIVE)));
		}
		
		if (parameters.containsKey(API_VERSION)) {
			instance.setApiVersion(parameters.getFirst(API_VERSION));
		}
		
		if (parameters.containsKey(AUTH_ENDPOINT)) {
			instance.setAuthEndpoint(parameters.getFirst(AUTH_ENDPOINT));
		}
		
		if (parameters.containsKey(PASSWORD)) {
			instance.setPassword(parameters.getFirst(PASSWORD));
		}
		
		if (parameters.containsKey(USERNAME)) {
			instance.setUsername(parameters.getFirst(USERNAME));
		}
		
		if (parameters.containsKey(SECURITY_TOKEN_PARAM)) {
			instance.setSecurityToken(parameters.getFirst(SECURITY_TOKEN_PARAM));
		}
		
		updateInstance(resource, instance);
		
		return instance;
	}
	
	/**
	 * 
	 * @param id
	 * @param key
	 * @return
	 */
	
	public Instance testConnection(String id, String key) {		
		SalesforceConnector resource = findById( id );
		
		Instance instance = resource.getInstances()
				.stream()
				.filter(e -> key.equals(e.getKey()))
				.findFirst()
				.get();
		
		Map<String, UserProperty> properties = UserProperties.queryBySubject(instance.getKey())
				.stream()
				.collect(Collectors.toMap(UserProperty::getKey, p -> p));
		
		if (instance.getIsSandbox()) {
			instance.setPassword(properties.get(PASSWORD).getValue());
			instance.setSecurityToken(properties.get(SECURITY_TOKEN_PROPERTY).getValue());
		} else {
			instance.setRefreshToken(properties.get(REFRESH_TOKEN_PROPERTY).getValue());
		}
		
		testConnection( instance );
		
		instance.setPassword(null);
		instance.setSecurityToken(null);
		instance.setRefreshToken(null);
		
		updateSalesforceConnector( id, resource );
		
		return instance;
	}
	
	public Instance buildEnvironment(String id, String key) {
		SalesforceConnector resource = findById( id );
		
		Instance instance = resource.getInstances()
				.stream()
				.filter(e -> key.equals(e.getKey()))
				.findFirst()
				.get();
		
		Map<String, UserProperty> properties = UserProperties.queryBySubject(instance.getKey())
				.stream()
				.collect(Collectors.toMap(UserProperty::getKey, p -> p));
		
		if (instance.getIsSandbox()) {
			instance.setPassword(properties.get(PASSWORD).getValue());
			instance.setSecurityToken(properties.get(SECURITY_TOKEN_PROPERTY).getValue());
		} else {
			instance.setRefreshToken(properties.get(REFRESH_TOKEN_PROPERTY).getValue());
		}
		
		buildInstance( instance );
		
		instance.setPassword(null);
		instance.setSecurityToken(null);
		instance.setRefreshToken(null);
		
		updateSalesforceConnector( id, resource );
		
		return instance;
	}
	
	/**
	 * 
	 * @param id
	 * @param key
	 */
	
	public void removeInstance(String id, String key) {
		SalesforceConnector resource = findById(id);
		
		Instance instance = resource.getInstances()
				.stream()
				.filter(e -> key.equals(e.getKey()))
				.findFirst()
				.get();
		
		removeInstance(instance);
		
		resource.getInstances().removeIf(e -> key.equals(e.getKey()));
		
		updateSalesforceConnector(id, resource);
	} 
	
	/**
	 * 
	 * @param instance
	 */
	
	private void removeInstance(Instance instance) {
		UserProperties.clear(instance.getKey());
		mongoDocumentService.deleteMany(com.nowellpoint.api.model.document.SObjectDetail.class, eq ( "environmentKey", instance.getKey() ));
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
	
	private void buildInstance(Instance instance) {

		try {
			
			if (instance.getIsSandbox()) {
				
				String authEndpoint = instance.getAuthEndpoint();
				String username = instance.getUsername();
				String password = instance.getPassword();
				String securityToken = instance.getSecurityToken();
				
				LoginResult loginResult = salesforceService.login(authEndpoint, username, password, securityToken);		
				instance.setUserId(loginResult.getUserId());
				instance.setOrganizationId(loginResult.getOrganizationId());
				instance.setOrganizationName(loginResult.getOrganizationName());
				instance.setServiceEndpoint(loginResult.getServiceEndpoint());
				
				Client client = new Client();
				
				GetIdentityRequest getIdentityRequest = new GetIdentityRequest()
						.setAccessToken(loginResult.getSessionId())
						.setId(loginResult.getId());
				
				Identity identity = client.getIdentity(getIdentityRequest);
				
				DescribeGlobalSobjectsRequest describeGlobalSobjectsRequest = new DescribeGlobalSobjectsRequest()
						.setAccessToken(loginResult.getSessionId())
						.setSobjectsUrl(identity.getUrls().getSobjects());
				
				DescribeGlobalSobjectsResult describeGlobalSobjectsResult = client.describeGlobal(describeGlobalSobjectsRequest);
				
				instance.setSobjects(describeGlobalSobjectsResult.getSobjects().stream().collect(Collectors.toSet()));
				
				ThemeRequest themeRequest = new ThemeRequest()
						.withAccessToken(loginResult.getSessionId())
						.withRestEndpoint(identity.getUrls().getRest());
				
				Theme theme = client.getTheme(themeRequest);
				
				instance.setTheme(theme);
				
				describeSobjects(loginResult.getSessionId(), identity.getUrls().getSobjects(), identity.getUrls().getQuery(), describeGlobalSobjectsResult, instance.getKey());
				
			} else {
				
				String refreshToken = instance.getRefreshToken();
				
				OauthAuthenticationResponse authenticationResponse = salesforceService.refreshToken(refreshToken);
				
				Token token = authenticationResponse.getToken();
				Identity identity = authenticationResponse.getIdentity();
				
				Client client = new Client();

				GetOrganizationRequest getOrganizationRequest = new GetOrganizationRequest()
						.setAccessToken(token.getAccessToken())
						.setOrganizationId(identity.getOrganizationId())
						.setSobjectUrl(identity.getUrls().getSobjects());
				
				Organization organization = client.getOrganization(getOrganizationRequest);
				
				instance.setUserId(identity.getUserId());
				instance.setOrganizationId(identity.getOrganizationId());
				instance.setOrganizationName(organization.getName());
				instance.setServiceEndpoint(token.getInstanceUrl());
				instance.setIsValid(Boolean.TRUE);
				
				DescribeGlobalSobjectsRequest describeGlobalSobjectsRequest = new DescribeGlobalSobjectsRequest()
						.setAccessToken(token.getAccessToken())
						.setSobjectsUrl(identity.getUrls().getSobjects());
				
				DescribeGlobalSobjectsResult describeGlobalSobjectsResult = client.describeGlobal(describeGlobalSobjectsRequest);
				
				instance.setSobjects(describeGlobalSobjectsResult.getSobjects().stream().collect(Collectors.toSet()));
				
				ThemeRequest themeRequest = new ThemeRequest()
						.withAccessToken(token.getAccessToken())
						.withRestEndpoint(identity.getUrls().getRest());
				
				Theme theme = client.getTheme(themeRequest);
				
				instance.setTheme(theme);
				
				describeSobjects(token.getAccessToken(), identity.getUrls().getSobjects(), identity.getUrls().getQuery(), describeGlobalSobjectsResult, instance.getKey());
			}
			instance.setIsValid(Boolean.TRUE);
		} catch (OauthException e) {
			instance.setIsValid(Boolean.FALSE);
			instance.setTestMessage(e.getErrorDescription());
		} catch (ValidationException e) {
			instance.setIsValid(Boolean.FALSE);
			instance.setTestMessage(e.getMessage());
		} catch (Exception e) {
			instance.setIsValid(Boolean.FALSE);
			instance.setTestMessage(e.getMessage());
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
				ObjectId id = new ObjectId( UserContext.getPrincipal().getName() );

				DBRef dbref = new DBRef( collectionName, id );
				user.setIdentity(dbref);

				com.nowellpoint.api.model.document.SObjectDetail sobjectDetail = null;
				try {
					sobjectDetail = mongoDocumentService.findOne(com.nowellpoint.api.model.document.SObjectDetail.class, and ( eq ( "name", sobject.getName() ), eq ( "environmentKey", environmentKey ) ) );
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
					mongoDocumentService.create(sobjectDetail);
				} else {
					mongoDocumentService.replace(sobjectDetail);
				}
			});
		}
		
		executor.shutdown();
		executor.awaitTermination(30, TimeUnit.SECONDS);
	}
	
	private void testConnection(Instance instance) {

		try {
			
			if (instance.getIsSandbox()) {
				
				String authEndpoint = instance.getAuthEndpoint();
				String username = instance.getUsername();
				String password = instance.getPassword();
				String securityToken = instance.getSecurityToken();
				
				LoginResult loginResult = salesforceService.login(authEndpoint, username, password, securityToken);		
				
				instance.setUserId(loginResult.getUserId());
				instance.setOrganizationId(loginResult.getOrganizationId());
				instance.setOrganizationName(loginResult.getOrganizationName());
				instance.setServiceEndpoint(loginResult.getServiceEndpoint());
				
			} else {
				
				String refreshToken = instance.getRefreshToken();
				
				OauthAuthenticationResponse authenticationResponse = salesforceService.refreshToken(refreshToken);
				
				Token token = authenticationResponse.getToken();
				Identity identity = authenticationResponse.getIdentity();
				
				Client client = new Client();

				GetOrganizationRequest getOrganizationRequest = new GetOrganizationRequest()
						.setAccessToken(token.getAccessToken())
						.setOrganizationId(identity.getOrganizationId())
						.setSobjectUrl(identity.getUrls().getSobjects());
				
				Organization organization = client.getOrganization(getOrganizationRequest);
				
				instance.setUserId(identity.getUserId());
				instance.setOrganizationId(identity.getOrganizationId());
				instance.setOrganizationName(organization.getName());
				instance.setServiceEndpoint(token.getInstanceUrl());
			}
			instance.setIsValid(Boolean.TRUE);
		} catch (OauthException e) {
			instance.setIsValid(Boolean.FALSE);
			instance.setTestMessage(e.getErrorDescription());
		} catch (ValidationException e) {
			instance.setIsValid(Boolean.FALSE);
			instance.setTestMessage(e.getMessage());
		} catch (Exception e) {
			instance.setIsValid(Boolean.FALSE);
			instance.setTestMessage(e.getMessage());
		}			
	}
}