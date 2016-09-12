package com.nowellpoint.api.service;

import java.sql.Date;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import javax.inject.Inject;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import com.nowellpoint.api.model.document.SimpleStorageService;
import com.nowellpoint.api.model.document.Targets;
import com.nowellpoint.api.model.dto.AccountProfile;
import com.nowellpoint.api.model.dto.Application;
import com.nowellpoint.api.model.dto.ScheduledJob;
import com.nowellpoint.api.model.dto.Environment;
import com.nowellpoint.api.model.dto.Id;
import com.nowellpoint.api.model.dto.SalesforceConnector;
import com.nowellpoint.api.model.dto.ServiceInstanceDTO;
import com.nowellpoint.api.model.dynamodb.UserProperties;
import com.nowellpoint.api.model.dynamodb.UserProperty;
import com.nowellpoint.api.model.mapper.ApplicationModelMapper;
import com.nowellpoint.aws.model.admin.Properties;
import com.nowellpoint.client.sforce.model.LoginResult;

/**
 * 
 * 
 * @author jherson
 *
 * 
 */

public class ApplicationService extends ApplicationModelMapper {
	
	@Inject
	private SalesforceConnectorService salesforceConnectorService;
	
	@Inject
	private SalesforceService salesforceService;
	
	@Inject
	private CommonFunctions commonFunctions;
	
	/**
	 * 
	 * 
	 * 
	 * 
	 * 
	 */
	
	public ApplicationService() {
		
	}
	
	/**
	 * 
	 * @param subject
	 * @return
	 */
	
	public Set<Application> findAllByOwner() {
		return super.findAllByOwner();
	}
	
	/**
	 * 
	 * @param subject
	 * @param resource
	 * @param eventSource
	 * @return
	 * 
	 */
	
	public void createApplication(Application application, String connectorId, Boolean importSandboxes, Boolean importServices) {
		
		if (application.getOwner() == null) {
			AccountProfile owner = new AccountProfile(getSubject());
			application.setOwner(owner);
		}
		
		application.setStatus("WORK_IN_PROGRESS");
		
		SalesforceConnector connector = salesforceConnectorService.findSalesforceConnector(new Id(connectorId));
		
		if (importSandboxes) {
			application.setEnvironments(connector.getEnvironments());
		} else {
			application.addEnvironment(connector.getEnvironments().stream().filter(e -> ! e.getIsSandbox()).findFirst().get());
		}
		
		if (importServices) {
			application.setServiceInstances(connector.getServiceInstances());
		}
		
		super.createApplication(application);
	}
	
	/**
	 * 
	 * @param subject
	 * @param resource
	 * @param eventSource
	 * @return
	 */
	
	public void updateApplication(Id id, Application application) {
		Application original = findApplication( id );
		
		application.setId(id);
		application.setCreatedById(original.getCreatedById());
		application.setCreatedDate(original.getCreatedDate());
		application.setSystemCreationDate(original.getSystemCreationDate());
		application.setSystemModifiedDate(original.getSystemModifiedDate());
		
		if (application.getName() == null) {
			application.setName(original.getName());
		}
		
		if (application.getDescription() == null) {
			application.setDescription(original.getDescription());
		}
		
		if (application.getEnvironments() == null || application.getEnvironments().isEmpty()) {
			application.setEnvironments(original.getEnvironments());
		}
		
		if (application.getServiceInstances() == null || application.getServiceInstances().isEmpty()) {
			application.setServiceInstances(original.getServiceInstances());
		}
		
		if (application.getStatus() == null) {
			application.setStatus(original.getStatus());
		}
		
		if (application.getOwner() == null) {
			application.setOwner(original.getOwner());
		}
		
		super.updateApplication(application);
	}
	
	/**
	 * 
	 * @param applicationId
	 * @param subject
	 * @param eventSource
	 */
	
	public void deleteApplication(Id id) {		
		Application resource = findApplication(id);
		super.deleteApplication(resource);
	}
	
	/**
	 * 
	 * @param id
	 * @param subject
	 * @return
	 */
	
	public Application findApplication(Id id) {
		return super.findApplication(id);
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
	
	public void updateEnvironment(Id id, String key, Environment environment) {
		Application application = findApplication( id );
		
		environment.setKey(key);
		
		updateEnvironment(application, environment);
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
	
	public Environment updateEnvironment(Id id, String key, MultivaluedMap<String, String> parameters) {
		
		Application application = findApplication( id );
		
		Environment environment = application.getEnvironments()
				.stream()
				.filter(e -> key.equals(e.getKey()))
				.findFirst()
				.get();
		
		if (parameters.containsKey("test") || Boolean.valueOf(parameters.getFirst("test"))) {
			
			application.getEnvironments().removeIf(e -> key.equals(e.getKey()));
			
			commonFunctions.testConnection(environment, parameters);
			
			application.addEnvironment(environment);
			
			updateApplication( id, application );
			
		} else {
			
			commonFunctions.updateEnvironment(environment, parameters);
			
			updateEnvironment(application, environment);
		}
		
		return environment;
	}
	
	/**************************************************************************************************************************
	 * 
	 * 
	 * @param id
	 * @param key
	 * @return
	 * 
	 * 
	 *************************************************************************************************************************/
	
	public Environment getEnvironment(Id id, String key) {
		Application resource = findApplication(id);
		
		Environment environment = resource.getEnvironments()
				.stream()
				.filter(p -> p.getKey().equals(key))
				.findFirst()
				.get();
		
		return environment;
	}
	
	/**************************************************************************************************************************
	 * 
	 * 
	 * @param id
	 * @param environment
	 * @throws ServiceException
	 * 
	 * 
	 *************************************************************************************************************************/
	
	public void addEnvironment(Id id, Environment environment) throws ServiceException {
		LoginResult loginResult = salesforceService.login(environment.getAuthEndpoint(), environment.getUsername(), environment.getPassword(), environment.getSecurityToken());

		Application resource = findApplication(id);
		
		if (resource.getEnvironments() != null && resource.getEnvironments().stream().filter(e -> e.getOrganizationId().equals(loginResult.getOrganizationId())).findFirst().isPresent()) {
			throw new ServiceException(Response.Status.CONFLICT, String.format("Unable to add new environment. Conflict with existing organization: %s with Id: ", loginResult.getOrganizationName(), loginResult.getOrganizationId()));
		}
		
		environment.setKey(UUID.randomUUID().toString().replace("-", ""));
		environment.setIsActive(Boolean.TRUE);
		environment.setIsReadOnly(Boolean.FALSE);
		environment.setIsValid(Boolean.TRUE);
		environment.setAddedOn(Date.from(Instant.now()));
		environment.setUpdatedOn(Date.from(Instant.now()));
		environment.setApiVersion(System.getProperty(Properties.SALESFORCE_API_VERSION));
		environment.setIsSandbox(Boolean.TRUE);
		environment.setUserId(loginResult.getUserId());
		environment.setOrganizationId(loginResult.getOrganizationId());
		environment.setOrganizationName(loginResult.getOrganizationName());
		environment.setServiceEndpoint(loginResult.getServiceEndpoint());
		
		List<UserProperty> properties = commonFunctions.getEnvironmentUserProperties(getSubject(), environment);
		
		UserProperties.batchSave(properties);
		
		environment.setPassword(null);
		environment.setSecurityToken(null);
		
		resource.addEnvironment(environment);
		
		updateApplication(id, resource);
	}
	
	public void updateEnvironment(Application resource, Environment environment) {
		
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
			environment.setIsValid(Boolean.TRUE);
		} else {
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
		
		updateApplication(resource.getId(), resource);
	}
	
	/**************************************************************************************************************************
	 * 
	 * 
	 * @param id
	 * @param serviceProviderId
	 * @param serviceType
	 * @param code
	 * @return
	 * 
	 * 
	 *************************************************************************************************************************/
	
	public ServiceInstanceDTO addServiceInstance(Id id, String key) {		
		Application resource = findApplication(id);
		
		if (resource.getServiceInstances() == null) {
			resource.setServiceInstances(Collections.emptySet());
		}
		
		ServiceInstanceDTO serviceInstance = commonFunctions.buildServiceInstance(key);
		
		resource.getServiceInstances().stream().filter(s -> s.getServiceType().equals(serviceInstance.getServiceType())).findFirst().ifPresent( s-> {
			throw new ServiceException(String.format("Unable to add new environment. Service has already been added with type: %s", s.getServiceName()));
		});
		
		resource.addServiceInstance(serviceInstance);
		
		updateApplication(id, resource);
		
		return serviceInstance;
	}
	
	/**************************************************************************************************************************
	 * 
	 * 
	 * @param id
	 * @param key
	 * 
	 * 
	 *************************************************************************************************************************/
	
	public void removeEnvironment(Id id, String key) {
		Application resource = findApplication(id);
		
		Environment environment = resource.getEnvironments()
				.stream()
				.filter(e -> key.equals(e.getKey()))
				.findFirst()
				.get();
		
		List<UserProperty> properties = commonFunctions.getEnvironmentUserProperties(getSubject(), environment);
		
		UserProperties.batchDelete(properties);
		
		resource.getEnvironments().removeIf(e -> key.equals(e.getKey()));
		
		updateApplication(id, resource);
	}

	/***************************************************************************************************************************
	 * 
	 * 
	 * @param id
	 * @param key
	 * @return
	 * 
	 * 
	 **************************************************************************************************************************/
	
	public ServiceInstanceDTO getServiceInstance(Id id, String key) {
		Application resource = findApplication(id);
		
		ServiceInstanceDTO serviceInstance = resource.getServiceInstances()
				.stream()
				.filter(s -> key.equals(s.getKey()))
				.findFirst()
				.get();
		
		return serviceInstance;
		
	}
	
	/**************************************************************************************************************************
	 * 
	 * 
	 * @param id
	 * @param key
	 * @param serviceInstance
	 * @return
	 * 
	 * 
	 *************************************************************************************************************************/
	
	public void updateServiceInstance(Id id, String key, ServiceInstanceDTO serviceInstance) {		
		Application resource = findApplication(id);

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

		Optional<SimpleStorageService> simpleStoreageService = Optional.of(serviceInstance)
				.map(ServiceInstanceDTO::getTargets)
				.map(Targets::getSimpleStorageService);
		
		if (simpleStoreageService.isPresent()) {
			
			commonFunctions.saveAwsCredentials(getSubject(), key, simpleStoreageService.get());
			
			serviceInstance.getTargets().getSimpleStorageService().setAwsAccessKey(null);
			serviceInstance.getTargets().getSimpleStorageService().setAwsSecretAccessKey(null);
		}

		resource.addServiceInstance(serviceInstance);

		updateApplication(id, resource);
	}
	
	/**************************************************************************************************************************
	 * 
	 * 
	 * @param id
	 * @param key
	 * @param parameters
	 * @return ServiceInstanceDTO
	 * 
	 * 
	 *************************************************************************************************************************/
	
	public ServiceInstanceDTO updateServiceInstance(Id id, String key, MultivaluedMap<String, String> parameters) {		
		Application resource = findApplication(id);
		
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
	 * @param id
	 * @return
	 */
	
	public Set<ScheduledJob> getBatchJobs(Id id) {
		return null;
	}
}