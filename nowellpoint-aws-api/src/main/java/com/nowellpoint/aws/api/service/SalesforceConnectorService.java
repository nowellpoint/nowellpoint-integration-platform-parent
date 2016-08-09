package com.nowellpoint.aws.api.service;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.sql.Date;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.net.ssl.HttpsURLConnection;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriBuilder;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.DeleteObjectsRequest.KeyVersion;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.nowellpoint.aws.api.dto.AccountProfileDTO;
import com.nowellpoint.aws.api.dto.EnvironmentDTO;
import com.nowellpoint.aws.api.dto.EventListenerDTO;
import com.nowellpoint.aws.api.dto.Id;
import com.nowellpoint.aws.api.dto.SalesforceConnectorDTO;
import com.nowellpoint.aws.api.dto.ServiceInstanceDTO;
import com.nowellpoint.aws.api.dto.ServiceProviderDTO;
import com.nowellpoint.aws.api.model.EventListener;
import com.nowellpoint.aws.api.model.Plan;
import com.nowellpoint.aws.api.model.SalesforceConnector;
import com.nowellpoint.aws.api.model.Service;
import com.nowellpoint.aws.api.model.Targets;
import com.nowellpoint.aws.api.model.dynamodb.UserProperty;
import com.nowellpoint.aws.model.admin.Properties;
import com.nowellpoint.aws.model.admin.Property;
import com.nowellpoint.aws.provider.DynamoDBMapperProvider;
import com.nowellpoint.client.sforce.Client;
import com.nowellpoint.client.sforce.GetIdentityRequest;
import com.nowellpoint.client.sforce.GetOrganizationRequest;
import com.nowellpoint.client.sforce.model.Identity;
import com.nowellpoint.client.sforce.model.LoginResult;
import com.nowellpoint.client.sforce.model.Organization;
import com.nowellpoint.client.sforce.model.Token;

public class SalesforceConnectorService extends AbstractDocumentService<SalesforceConnectorDTO, SalesforceConnector> {
	
	@Inject
	private OutboundMessageService outboundMessageService;
	
	@Inject
	private ServiceProviderService serviceProviderService;
	
	@Inject
	private SalesforceService salesforceService;
	
	private static final AmazonS3 s3Client = new AmazonS3Client();
	
	private static final DynamoDBMapper dynamoDBMapper = DynamoDBMapperProvider.getDynamoDBMapper();
	
	private static final String API_VERSION = "37.0";
	
	public SalesforceConnectorService() {
		super(SalesforceConnectorDTO.class, SalesforceConnector.class);
	}
	
	public Set<SalesforceConnectorDTO> getAll(String subject) {
		Set<SalesforceConnectorDTO> resources = hscan( subject, SalesforceConnectorDTO.class );		
		if (resources.isEmpty()) {
			resources = findAllByOwner(subject);
			hset( subject, resources );
		}
		return resources;
	}
	
	public SalesforceConnectorDTO createSalesforceConnector(Token token) {
		
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
		
		AccountProfileDTO owner = new AccountProfileDTO();
		owner.setHref(getSubject());
		
		SalesforceConnectorDTO resource = new SalesforceConnectorDTO();
		resource.setOrganization(organization);
		resource.setIdentity(identity);
		resource.setOwner(owner);
		
		EnvironmentDTO environment = new EnvironmentDTO();
		environment.setKey(UUID.randomUUID().toString().replaceAll("-", ""));
		environment.setIsActive(Boolean.TRUE);
		environment.setEnvironmentName("Production");
		environment.setIsReadOnly(Boolean.TRUE);
		environment.setIsSandbox(Boolean.FALSE);
		environment.setIsValid(Boolean.TRUE);
		environment.setAddedOn(Date.from(Instant.now()));
		environment.setUpdatedOn(Date.from(Instant.now()));
		environment.setUsername(identity.getUsername());
		environment.setOrganizationName(organization.getName());
		environment.setServiceEndpoint(token.getInstanceUrl());
		environment.setAuthEndpoint("https://login.salesforce.com");
		environment.setApiVersion(API_VERSION);
		
		resource.addEnvironment(environment);
		
		create( resource );
		hset( getSubject(), SalesforceConnectorDTO.class.getName().concat( resource.getId() ), resource );
		hset( resource.getId(), getSubject(), resource );
		
		List<UserProperty> properties = new ArrayList<UserProperty>();
		
		UserProperty accessTokenProperty = new UserProperty();
		accessTokenProperty.setSubject(resource.getId());
		accessTokenProperty.setKey("access.token");
		accessTokenProperty.setValue(token.getAccessToken());
		accessTokenProperty.setLastModifiedBy(getSubject());
		accessTokenProperty.setLastModifiedDate(Date.from(Instant.now()));
		
		properties.add(accessTokenProperty);
		
		UserProperty refreshTokenProperty = new UserProperty();
		refreshTokenProperty.setSubject(resource.getId());
		refreshTokenProperty.setKey("refresh.token");
		refreshTokenProperty.setValue(token.getRefreshToken());
		refreshTokenProperty.setLastModifiedBy(getSubject());
		refreshTokenProperty.setLastModifiedDate(Date.from(Instant.now()));
		
		properties.add(refreshTokenProperty);
		
		dynamoDBMapper.batchSave(properties);
		
		return resource;
	}
	
	public SalesforceConnectorDTO updateSalesforceConnector(Id id, SalesforceConnectorDTO resource) {		
		SalesforceConnectorDTO original = findSalesforceConnector(id);
		resource.setId(original.getId());
		resource.setCreatedById(original.getCreatedById());
		resource.setCreatedDate(original.getCreatedDate());
		
		replace( resource );
		
		hset( getSubject(), SalesforceConnectorDTO.class.getName().concat( resource.getId() ), resource );
		hset( resource.getId(), getSubject(), resource );
		
		return resource;
	}
	
	public void deleteSalesforceConnector(Id id) {
		SalesforceConnectorDTO resource = findSalesforceConnector( id );
		
		delete(resource);
		
		hdel( getSubject(), SalesforceConnectorDTO.class.getName().concat(id.getValue()) );
		hdel( id.getValue(), getSubject() );

		List<KeyVersion> keys = new ArrayList<KeyVersion>();
		keys.add(new KeyVersion(resource.getIdentity().getPhotos().getPicture().substring(resource.getIdentity().getPhotos().getPicture().lastIndexOf("/") + 1)));
		keys.add(new KeyVersion(resource.getIdentity().getPhotos().getThumbnail().substring(resource.getIdentity().getPhotos().getThumbnail().lastIndexOf("/") + 1)));

		DeleteObjectsRequest deleteObjectsRequest = new DeleteObjectsRequest("nowellpoint-profile-photos").withKeys(keys);

		s3Client.deleteObjects(deleteObjectsRequest);
		
		List<UserProperty> properties = new ArrayList<UserProperty>();
		
		UserProperty accessTokenProperty = new UserProperty();
		accessTokenProperty.setSubject(resource.getId());
		accessTokenProperty.setKey("access.token");
		
		properties.add(accessTokenProperty);
		
		UserProperty refreshTokenProperty = new UserProperty();
		refreshTokenProperty.setSubject(resource.getId());
		refreshTokenProperty.setKey("refresh.token");
		
		properties.add(refreshTokenProperty);
		
		resource.getEnvironments().stream().forEach(e -> {
			
			UserProperty passwordProperty = new UserProperty();
			passwordProperty.setSubject(e.getKey());
			passwordProperty.setKey("password");
			passwordProperty.setValue(e.getPassword());
			passwordProperty.setLastModifiedBy(getSubject());
			passwordProperty.setLastModifiedDate(Date.from(Instant.now()));
			
			properties.add(passwordProperty);
			
			UserProperty securityTokenProperty = new UserProperty();
			securityTokenProperty.setSubject(e.getKey());
			securityTokenProperty.setKey("security.token");
			securityTokenProperty.setValue(e.getSecurityToken());
			securityTokenProperty.setLastModifiedBy(getSubject());
			securityTokenProperty.setLastModifiedDate(Date.from(Instant.now()));
			
			properties.add(securityTokenProperty);
			
		});
		
		dynamoDBMapper.batchDelete(properties);
	}
	
	public SalesforceConnectorDTO findSalesforceConnector(Id id) {		
		SalesforceConnectorDTO resource = hget( SalesforceConnectorDTO.class, id.getValue(), getSubject() );
		if ( resource == null ) {		
			resource = find(id.getValue());
			hset( id.getValue(), getSubject(), resource );
		}
		return resource;
	}
	
	public EnvironmentDTO getEnvironment(Id id, String key) {
		SalesforceConnectorDTO resource = findSalesforceConnector(id);
		
		EnvironmentDTO environment = resource.getEnvironments()
				.stream()
				.filter(p -> p.getKey().equals(key))
				.findFirst()
				.get();
		
		return environment;
	}
	
	public EnvironmentDTO addEnvironment(Id id, EnvironmentDTO environment) throws ServiceException {
		LoginResult loginResult = salesforceService.login(environment.getAuthEndpoint(), environment.getUsername(), environment.getPassword(), environment.getSecurityToken());

		SalesforceConnectorDTO resource = findSalesforceConnector(id);
		
		if (resource.getEnvironments().stream().filter(e -> e.getOrganizationId().equals(loginResult.getOrganizationId())).findFirst().isPresent()) {
			throw new ServiceException(String.format("Unable to add new environment. Conflict with existing organization: %s", loginResult.getOrganizationId()));
		}
		
		environment.setKey(UUID.randomUUID().toString().replace("-", ""));
		environment.setIsActive(Boolean.TRUE);
		environment.setIsReadOnly(Boolean.FALSE);
		environment.setIsValid(Boolean.TRUE);
		environment.setAddedOn(Date.from(Instant.now()));
		environment.setUpdatedOn(Date.from(Instant.now()));
		environment.setApiVersion(API_VERSION);
		environment.setIsSandbox(Boolean.TRUE);
		environment.setUserId(loginResult.getUserId());
		environment.setOrganizationId(loginResult.getOrganizationId());
		environment.setOrganizationName(loginResult.getOrganizationName());
		environment.setServiceEndpoint(loginResult.getServiceEndpoint());
		
		List<UserProperty> properties = getEnvironmentUserProperties(environment);
		
		dynamoDBMapper.batchSave(properties);
		
		environment.setPassword(null);
		environment.setSecurityToken(null);
		
		resource.addEnvironment(environment);
		
		updateSalesforceConnector(id, resource);
		
		return environment;
	}
	
	public EnvironmentDTO updateEnvironment(Id id, String key, EnvironmentDTO environment) {
		SalesforceConnectorDTO resource = findSalesforceConnector( id );
		
		EnvironmentDTO original = resource.getEnvironments()
				.stream()
				.filter(e -> key.equals(e.getKey()))
				.findFirst()
				.get();
		
		resource.getEnvironments().removeIf(e -> key.equals(e.getKey()));
		
		environment.setKey(key);
		environment.setAddedOn(original.getAddedOn());
		environment.setUpdatedOn(Date.from(Instant.now()));
		environment.setIsReadOnly(original.getIsReadOnly());
		environment.setIsSandbox(original.getIsSandbox());
		environment.setIsValid(original.getIsValid());
		environment.setApiVersion(original.getApiVersion());
		environment.setTestMessage(original.getTestMessage());
		
		if (environment.getIsActive()) {
			LoginResult loginResult = salesforceService.login(environment.getAuthEndpoint(), environment.getUsername(), environment.getPassword(), environment.getSecurityToken());
			
			if (! loginResult.getOrganizationId().equals(original.getOrganizationId()) 
					&& resource.getEnvironments().stream().filter(e -> e.getOrganizationId().equals(loginResult.getOrganizationId())).findFirst().isPresent()) {
				
				throw new ServiceException(String.format("Unable to update environment. Conflict with existing organization: %s", loginResult.getOrganizationId()));
			}
			
			environment.setUserId(loginResult.getUserId());
			environment.setOrganizationId(loginResult.getOrganizationId());
			environment.setOrganizationName(loginResult.getOrganizationName());
			environment.setServiceEndpoint(loginResult.getServiceEndpoint());
		} else {
			environment.setUserId(original.getUserId());
			environment.setOrganizationId(original.getOrganizationId());
			environment.setOrganizationName(original.getOrganizationName());
			environment.setServiceEndpoint(original.getServiceEndpoint());
		}
		
		List<UserProperty> properties = getEnvironmentUserProperties(environment);
		
		dynamoDBMapper.batchSave(properties);
		
		environment.setPassword(null);
		environment.setSecurityToken(null);
		
		resource.addEnvironment(environment);
		
		updateSalesforceConnector(id, resource);
		
		return environment;
	} 
	
	public EnvironmentDTO updateEnvironment(Id id, String key, MultivaluedMap<String, String> parameters) {
		
		EnvironmentDTO environment = getEnvironment(id, key);
		
		if (parameters.containsKey("test")) {
			if (Boolean.valueOf(parameters.getFirst("test"))) {
				try {
					UserProperty userProperty = new UserProperty();
					userProperty.setSubject(environment.getKey());
					
					DynamoDBQueryExpression<UserProperty> queryExpression = new DynamoDBQueryExpression<UserProperty>()
							.withHashKeyValues(userProperty);
					
					Map<String, UserProperty> properties = dynamoDBMapper.query(UserProperty.class, queryExpression).stream().collect(Collectors.toMap(UserProperty::getKey, p -> p));
					
					System.out.println(properties.get("password").getValue());
					System.out.println(properties.get("security.token").getValue());
					
					LoginResult loginResult = salesforceService.login(environment.getAuthEndpoint(), environment.getUsername(), properties.get("password").getValue(), properties.get("security.token").getValue());
					environment.setIsValid(Boolean.TRUE);
					environment.setTestMessage("Success: " + loginResult.getServerTimestamp().toString());
				} catch (Exception e) {
					environment.setIsValid(Boolean.FALSE);
					environment.setTestMessage(e.getMessage());
				}
			}
		} else {
			if (parameters.containsKey("isActive")) {
				environment.setIsActive(Boolean.valueOf(parameters.getFirst("isActive")));
			}
			
			if (parameters.containsKey("apiVersion")) {
				environment.setApiVersion(parameters.getFirst("apiVersion"));
			}
			
			if (parameters.containsKey("authEndpoint")) {
				environment.setAuthEndpoint(parameters.getFirst("authEndpoint"));
			}
			
			if (parameters.containsKey("password")) {
				environment.setPassword(parameters.getFirst("password"));
			}
			
			if (parameters.containsKey("username")) {
				environment.setUsername(parameters.getFirst("username"));
			}
			
			if (parameters.containsKey("securityToken")) {
				environment.setSecurityToken(parameters.getFirst("securityToken"));
			}
		}
		
		updateEnvironment(id, key, environment);
		
		return environment;
	}
	
	public void removeEnvironment(Id id, String key) {
		SalesforceConnectorDTO resource = findSalesforceConnector(id);
		
		EnvironmentDTO environment = resource.getEnvironments()
				.stream()
				.filter(e -> key.equals(e.getKey()))
				.findFirst()
				.get();
		
		List<UserProperty> properties = getEnvironmentUserProperties(environment);
		
		dynamoDBMapper.batchDelete(properties);
		
		resource.getEnvironments().removeIf(e -> key.equals(e.getKey()));
		
		updateSalesforceConnector(id, resource);
	} 
	
	public SalesforceConnectorDTO addServiceInstance(Id id, String serviceProviderId, String serviceType, String code) {		
		SalesforceConnectorDTO resource = findSalesforceConnector(id);
		
		ServiceProviderDTO serviceProvider = serviceProviderService.find(serviceProviderId);
		
		Service service = serviceProvider.getServices()
				.stream()
				.filter(s -> s.getType().equals(serviceType))
				.findFirst()
				.get();
		
		Plan plan = service.getPlans()
				.stream()
				.filter(p -> p.getCode().equals(code))
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
		serviceInstance.setPlan(plan);
		
		resource.addServiceInstance(serviceInstance);
		
		updateSalesforceConnector(id, resource);
		
		return resource;
	}
	
	public SalesforceConnectorDTO updateServiceInstance(Id id, String key, ServiceInstanceDTO serviceInstance) {		
		SalesforceConnectorDTO resource = findSalesforceConnector(id);
		
		if (resource.getServiceInstances() == null) {
			resource.setServiceInstances(Collections.emptySet());
		}
		
		Map<String,ServiceInstanceDTO> map = resource.getServiceInstances().stream().collect(Collectors.toMap(p -> p.getKey(), (p) -> p));
		
		resource.getServiceInstances().clear();
		
		if (map.containsKey(key)) {
			ServiceInstanceDTO original = map.get(key);
			original.setSourceEnvironment(serviceInstance.getSourceEnvironment());
			original.setName(serviceInstance.getName());
			map.put(key, original);
		}
		
		resource.setServiceInstances(new HashSet<ServiceInstanceDTO>(map.values()));

		updateSalesforceConnector(id, resource);
		
		return resource;
	}
	
	public SalesforceConnectorDTO removeServiceInstance(Id id, String key) {		
		SalesforceConnectorDTO resource = findSalesforceConnector(id);
		
		resource.getServiceInstances().removeIf(p -> p.getKey().equals(key));

		updateSalesforceConnector(id, resource);
		
		return resource;
	}
	
//	public SalesforceConnectorDTO addEnvironments(String subject, String id, String key, Set<EnvironmentDTO> environments) {
//		SalesforceConnectorDTO resource = findSalesforceConnector(subject, id);
//		resource.setSubject(subject);
//		
//		Optional<ServiceInstanceDTO> serviceInstance = resource.getServiceInstances()
//				.stream()
//				.filter(p -> p.getKey().equals(key))
//				.findFirst();
//		
//		if (serviceInstance.isPresent()) {
//			Map<Integer,Environment> map = serviceInstance.get().getEnvironments().stream().collect(Collectors.toMap(p -> p.getIndex(), (p) -> p));
//			serviceInstance.get().getEnvironments().clear();
//			serviceInstance.get().setActiveEnvironments(new Long(0));
//			AtomicInteger index = new AtomicInteger();
//			environments.stream().sorted((p1,p2) -> p2.getIndex().compareTo(p1.getIndex())).forEach(e -> {
//				Environment environment = null;
//				if (map.containsKey(e.getIndex())) {
//					environment = map.get(e.getIndex());
//				} else {
//				    environment = new Environment();
//				    environment.setLocked(Boolean.FALSE);
//				    environment.setIndex(index.get());
//				}
//				environment.setName(e.getName());
//				environment.setActive(e.getActive());
//				environment.setLabel(e.getLabel());
//				AtomicLong activeEnvironments = new AtomicLong(serviceInstance.get().getActiveEnvironments());
//				if (e.getActive()) {
//					activeEnvironments.incrementAndGet();
//				}
//				serviceInstance.get().setActiveEnvironments(activeEnvironments.get());
//				serviceInstance.get().getEnvironments().add(environment);
//				index.incrementAndGet();
//			});
//		}
//		
//		updateSalesforceConnector(resource);
//		
//		return resource;
//		
//	}
//	
//	public SalesforceConnectorDTO addEnvironmentVariables(String subject, String id, String key, String environmentName, Set<EnvironmentVariableDTO> environmentVariables) {
//		SalesforceConnectorDTO resource = findSalesforceConnector(subject, id);
//		resource.setSubject(subject);
//		
//		Set<String> variables = new HashSet<String>();
//		environmentVariables.stream().forEach(variable -> {
//			if (variables.contains(variable.getVariable())) {
//				throw new UnsupportedOperationException("Duplicate variable names: " + variable.getVariable());
//			}
//			variables.add(variable.getVariable());
//			if (variable.getVariable().contains(" ")) {
//				throw new IllegalArgumentException("Environment variables must not contain spaces: " + variable.getVariable());
//			}
//		});
//		
//		Optional<ServiceInstanceDTO> serviceInstance = resource.getServiceInstances()
//				.stream()
//				.filter(p -> p.getKey().equals(key))
//				.findFirst();
//		
//		if (serviceInstance.isPresent()) {
//			Optional<Environment> environment = serviceInstance.get().getEnvironments().stream().filter(p -> p.getName().equals(environmentName)).findFirst();
//			if (environment.isPresent()) {
//				Map<String,EnvironmentVariable> map = environment.get().getEnvironmentVariables().stream().collect(Collectors.toMap(p -> p.getVariable(), (p) -> p));
//				environment.get().getEnvironmentVariables().clear();
//				environmentVariables.stream().forEach(e -> {
//					EnvironmentVariable environmentVariable = null;
//					if (map.containsKey(e.getVariable())) {
//						environmentVariable = map.get(e.getVariable());
//					} else {
//						environmentVariable = new EnvironmentVariable();
//						environmentVariable.setVariable(e.getVariable());
//						environmentVariable.setLocked(Boolean.FALSE);
//					}
//					environmentVariable.setValue(e.getValue());
//					environmentVariable.setEncrypted(e.getEncrypted());
//					environment.get().getEnvironmentVariables().add(environmentVariable);
//				});
//			}
//			
//			updateSalesforceConnector(resource);
//		}
//		
//		return resource;
//	}
	
	public SalesforceConnectorDTO addEventListeners(Id id, String key, Set<EventListenerDTO> eventListeners) {
		SalesforceConnectorDTO resource = findSalesforceConnector(id);
		
		Optional<ServiceInstanceDTO> serviceInstance = resource.getServiceInstances()
				.stream()
				.filter(p -> p.getKey().equals(key))
				.findFirst();
		
		if (serviceInstance.isPresent()) {
			
			if (serviceInstance.get().getEventListeners() == null) {
				serviceInstance.get().setEventListeners(Collections.emptySet());
			}
			
			Map<String,EventListener> map = serviceInstance.get().getEventListeners().stream().collect(Collectors.toMap(p -> p.getName(), (p) -> p));
			
			serviceInstance.get().getEventListeners().clear();
			
			eventListeners.stream().forEach(p -> {
				EventListener eventListener =  map.get(p.getName());
				eventListener.setCreate(p.getCreate());
				eventListener.setUpdate(p.getUpdate());
				eventListener.setDelete(p.getDelete());
				eventListener.setCallback(p.getCallback());
				map.put(p.getName(), eventListener);
			});
			
			serviceInstance.get().setEventListeners(new HashSet<EventListener>(map.values()));
			
			updateSalesforceConnector(id, resource);
		}
		
		return resource;
	}
	
	public SalesforceConnectorDTO addTargets(Id id, String key, Targets targets) {		
		SalesforceConnectorDTO resource = findSalesforceConnector(id);
		
		Optional<ServiceInstanceDTO> serviceInstance = resource.getServiceInstances()
				.stream()
				.filter(p -> p.getKey().equals(key))
				.findFirst();
		
		if (serviceInstance.isPresent()) {
			
			serviceInstance.get().setTargets(targets);
			
			updateSalesforceConnector(id, resource);
		}
		
		return resource;
	}
	
//	public SalesforceConnectorDTO testConnection(String subject, String id, String key, String environmentName) {
//		SalesforceConnectorDTO resource = findSalesforceConnector(subject, id);
//		resource.setSubject(subject);
//
//		Optional<ServiceInstanceDTO> serviceInstance = resource.getServiceInstances()
//				.stream()
//				.filter(p -> p.getKey().equals(key))
//				.findFirst();
//
//		if (serviceInstance.isPresent()) {
//			
//			Optional<Environment> environment = serviceInstance.get()
//					.getEnvironments()
//					.stream()
//					.filter(p -> p.getName().equals(environmentName))
//					.findFirst();
//			
//			if (environment.isPresent()) {
//				
//				try {
//				
//					PartnerConnection connection = login(environment.get());
//					
//					environment.get().setEndpoint(connection.getConfig().getServiceEndpoint());
//					environment.get().setOrganization(connection.getUserInfo().getOrganizationId());
//					environment.get().setTest(Boolean.TRUE);
//					environment.get().setTestMessage("Success!");
//					
//				} catch (ConnectionException e) {
//					if (e instanceof LoginFault) {
//						LoginFault loginFault = (LoginFault) e;
//						environment.get().setTest(Boolean.FALSE);
//						environment.get().setTestMessage(loginFault.getExceptionCode().name().concat(": ").concat(loginFault.getExceptionMessage()));
//					} else {
//						throw new InternalServerErrorException(e.getMessage());
//					}
//				} catch (IllegalArgumentException e) {
//					environment.get().setTest(Boolean.FALSE);
//					environment.get().setTestMessage("Missing connection enviroment variables");
//				} finally {
//					updateSalesforceConnector(resource);
//				}
//			}
//		}
//		
//		return resource;
//	}
	
//	public SalesforceConnectorDTO describeGlobal(String subject, String id, String key) {
//		SalesforceConnectorDTO resource = findSalesforceConnector(subject, id);
//		resource.setSubject(subject);
//
//		Optional<ServiceInstanceDTO> serviceInstance = resource.getServiceInstances()
//				.stream()
//				.filter(p -> p.getKey().equals(key))
//				.findFirst();
//
//		if (serviceInstance.isPresent()) {
//			
//			Optional<Environment> environment = serviceInstance.get()
//					.getEnvironments()
//					.stream()
//					.filter(p -> p.getName().equals(serviceInstance.get().getSourceEnvironment()))
//					.findFirst();
//			
//			if (environment.isPresent()) {
//				
//				try {
//					
//					PartnerConnection connection = login(environment.get());
//					
//					DescribeGlobalResult result = connection.describeGlobal();
//					
//					DescribeGlobalSObjectResult[] sobjects = result.getSobjects();
//					
//					if (serviceInstance.get().getEventListeners() == null) {
//						serviceInstance.get().setEventListeners(new HashSet<EventListener>());
//					}
//					
//					Map<String,EventListener> map = serviceInstance.get().getEventListeners().stream().collect(Collectors.toMap(p -> p.getName(), (p) -> p));
//					
//					serviceInstance.get().getEventListeners().clear();
//					
//					Arrays.asList(sobjects).stream().forEach(p -> {
//						
//						EventListener eventListener = new EventListener();
//						eventListener.setName(p.getName());
//						eventListener.setLabel(p.getLabel());
//						eventListener.setTriggerable(p.getTriggerable());
//						eventListener.setCreateable(p.getCreateable());
//						eventListener.setDeleteable(p.getDeletable());
//						eventListener.setUpdateable(p.getUpdateable());
//						eventListener.setReplicateable(p.getReplicateable());
//						eventListener.setQueryable(p.getQueryable());
//						
//						if (map.containsKey(p.getName())) {
//							eventListener.setCreate(map.get(p.getName()).getCreate());
//							eventListener.setDelete(map.get(p.getName()).getDelete());
//							eventListener.setUpdate(map.get(p.getName()).getUpdate());
//							eventListener.setCallback(map.get(p.getName()).getCallback());
//						} else {
//							eventListener.setCreate(Boolean.FALSE);
//							eventListener.setDelete(Boolean.FALSE);
//							eventListener.setUpdate(Boolean.FALSE);
//						}
//						
//						serviceInstance.get().getEventListeners().add(eventListener);
//						
//					});
//					
//					updateSalesforceConnector(resource);
//					
//				} catch (ConnectionException e) {
//					if (e instanceof LoginFault) {
//						LoginFault loginFault = (LoginFault) e;
//						throw new BadRequestException(loginFault.getExceptionCode().name().concat(": ").concat(loginFault.getExceptionMessage()));
//					} else {
//						throw new InternalServerErrorException(e.getMessage());
//					}
//				}
//			}
//		}
//		
//		return resource;
//	}
	
//	public Field[] describeSobject(String subject, String id, String key, String sobject) {
//		SalesforceConnectorDTO resource = findSalesforceConnector(subject, id);
//		resource.setSubject(subject);
//		
//		Field[] fields = null;
//
//		Optional<ServiceInstanceDTO> serviceInstance = resource.getServiceInstances()
//				.stream()
//				.filter(p -> p.getKey().equals(key))
//				.findFirst();
//
//		if (serviceInstance.isPresent()) {
//			
//			Optional<Environment> environment = serviceInstance.get()
//					.getEnvironments()
//					.stream()
//					.filter(p -> p.getName().equals(serviceInstance.get().getSourceEnvironment()))
//					.findFirst();
//			
//			if (environment.isPresent()) {
//				
//				try {
//					
//					PartnerConnection connection = login(environment.get());
//					
//					DescribeSObjectResult result = connection.describeSObject(sobject);
//					
//					fields = result.getFields();
//					
//				} catch (ConnectionException e) {
//					if (e instanceof LoginFault) {
//						LoginFault loginFault = (LoginFault) e;
//						throw new BadRequestException(loginFault.getExceptionCode().name().concat(": ").concat(loginFault.getExceptionMessage()));
//					} else if (e instanceof InvalidSObjectFault) {
//						InvalidSObjectFault fault = (InvalidSObjectFault) e;
//						throw new BadRequestException(fault.getExceptionCode().name().concat(": ").concat(fault.getExceptionMessage()));
//					} else {
//						throw new InternalServerErrorException(e.getMessage());
//					}
//				}
//			}
//		}
//		
//		return fields;
//	}
	
//	public SObject[] query(String subject, String id, String key, String queryString) {
//		if (subject == null) {
//			throw new IllegalArgumentException("Missing parameter: subject");
//		}
//		
//		if (id == null) {
//			throw new IllegalArgumentException("Missing parameter: id");
//		}
//		
//		if (key == null) {
//			throw new IllegalArgumentException("Missing parameter: id");
//		}
//		
//		if (queryString == null) {
//			throw new IllegalArgumentException("Missing parameter: queryString");
//		}
//		
//		SalesforceConnectorDTO resource = findSalesforceConnector(subject, id);
//		resource.setSubject(subject);
//		
//		SObject[] sobjects = null;
//
//		Optional<ServiceInstanceDTO> serviceInstance = resource.getServiceInstances()
//				.stream()
//				.filter(p -> p.getKey().equals(key))
//				.findFirst();
//
//		if (serviceInstance.isPresent()) {
//
//			Optional<Environment> environment = serviceInstance.get()
//					.getEnvironments()
//					.stream()
//					.filter(p -> p.getName().equals(serviceInstance.get().getSourceEnvironment()))
//					.findFirst();
//
//			if (environment.isPresent()) {
//				
//				try {
//
//					PartnerConnection connection = login(environment.get());
//
//					QueryResult result = connection.query(queryString);
//
//					sobjects = result.getRecords();
//
//				} catch (ConnectionException e) {
//					if (e instanceof LoginFault) {
//						LoginFault loginFault = (LoginFault) e;
//						throw new BadRequestException(loginFault.getExceptionCode().name().concat(": ").concat(loginFault.getExceptionMessage()));
//					} else if (e instanceof ApiQueryFault) {
//						ApiQueryFault fault = (ApiQueryFault) e;
//						throw new BadRequestException(fault.getExceptionCode().name().concat(": ").concat(fault.getExceptionMessage()));
//					} else {
//						throw new InternalServerErrorException(e.getMessage());
//					}
//				}
//			}
//		}
//		
//		return sobjects;
//		
//	}
	
//	public SalesforceConnectorDTO deploy(String subject, String id, String key, String environmentName) throws JAXBException, IOException, IllegalArgumentException, ConnectionException, InterruptedException, ExecutionException {
//		SalesforceConnectorDTO resource = findSalesforceConnector(subject, id);
//		resource.setSubject(subject);
//
//		Optional<ServiceInstanceDTO> serviceInstance = resource.getServiceInstances()
//				.stream()
//				.filter(p -> p.getKey().equals(key))
//				.findFirst();
//
//		if (serviceInstance.isPresent()) {
//			
//			Optional<Environment> environment = serviceInstance.get().getEnvironments()
//					.stream()
//					.filter(p -> p.getName().equals(environmentName))
//					.findFirst();
//			
//			if (environment.isPresent()) {
//				
//				PartnerConnection connection = login(environment.get());
//				
//				List<BuildDefaultCallback> tasks = serviceInstance.get()
//						.getEventListeners()
//						.stream()
//						.filter(p -> p.getCreate() || p.getUpdate() || p.getDelete())
//						.map(p -> new BuildDefaultCallback(connection, p))
//						.collect(Collectors.toCollection(ArrayList::new));
//				
//				ExecutorService executor = Executors.newFixedThreadPool(tasks.size());
//				
//				List<Future<Callback>> futures = executor.invokeAll(tasks);
//				executor.shutdown();
//				executor.awaitTermination(30, TimeUnit.SECONDS);
//				
//				List<Callback> queries = new ArrayList<Callback>();
//				
//				for (Future<Callback> future : futures) {
//					queries.add(future.get());
//				}
//				
//				OutboundMessageHandlerConfiguration configuration = new OutboundMessageHandlerConfiguration();
//				configuration.setOrganizationId(environment.get().getOrganization());
//				configuration.setAwsAccessKey(serviceInstance.get().getTargets().getSimpleStorageService().getAwsAccessKey());
//				configuration.setAwsSecretAccessKey(serviceInstance.get().getTargets().getSimpleStorageService().getAwsSecretAccessKey());
//				configuration.setBucketName(serviceInstance.get().getTargets().getSimpleStorageService().getBucketName());
//				configuration.setEnvironmentName(environment.get().getName());
//				configuration.setServiceInstanceKey(serviceInstance.get().getKey());
//				configuration.setQueries(queries);
//				configuration.setDeploymentDate(new Date());
//				configuration.setDeployedBy(subject);
//				configuration.setIntegrationUser(connection.getConfig().getUsername());
//				
//				mapper.save(configuration);
//				
//				String packageKey = outboundMessageService.buildPackage(configuration);
//				
//				outboundMessageService.deployPackage(connection, packageKey);
//			}
//		}
//		
//		return resource;
//	}
	
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
	
	private List<UserProperty> getEnvironmentUserProperties(EnvironmentDTO environment) {
		List<UserProperty> properties = new ArrayList<UserProperty>();
		
		UserProperty accessTokenProperty = new UserProperty();
		accessTokenProperty.setSubject(environment.getKey());
		accessTokenProperty.setKey("password");
		accessTokenProperty.setValue(environment.getPassword());
		accessTokenProperty.setLastModifiedBy(getSubject());
		accessTokenProperty.setLastModifiedDate(Date.from(Instant.now()));
		
		properties.add(accessTokenProperty);
		
		UserProperty refreshTokenProperty = new UserProperty();
		refreshTokenProperty.setSubject(environment.getKey());
		refreshTokenProperty.setKey("security.token");
		refreshTokenProperty.setValue(environment.getSecurityToken());
		refreshTokenProperty.setLastModifiedBy(getSubject());
		refreshTokenProperty.setLastModifiedDate(Date.from(Instant.now()));
		
		properties.add(refreshTokenProperty);
		
		return properties;
	}
}