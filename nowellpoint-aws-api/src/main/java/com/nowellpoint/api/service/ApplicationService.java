package com.nowellpoint.api.service;

import java.util.Date;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

import javax.inject.Inject;
import javax.validation.ValidationException;
import javax.ws.rs.core.MultivaluedMap;

import com.nowellpoint.api.model.domain.AccountProfile;
import com.nowellpoint.api.model.domain.Application;
import com.nowellpoint.api.model.domain.Environment;
import com.nowellpoint.api.model.domain.SalesforceConnector;
import com.nowellpoint.api.model.dynamodb.UserProperties;
import com.nowellpoint.api.model.mapper.ApplicationModelMapper;
import com.nowellpoint.util.Properties;
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
	
	/**
	 * 
	 */
	
	public ApplicationService() {
		
	}
	
	/**
	 * @return 
	 */
	
	public Set<Application> findAllByOwner() {
		return super.findAllByOwner();
	}
	
	/**
	 * 
	 * @param application
	 * @param connectorId
	 * @param importSandboxes
	 * @param importServices
	 */
	
	public void createApplication(Application application, String connectorId, Boolean importSandboxes, Boolean importServices) {
		
		if (application.getOwner() == null) {
			AccountProfile owner = new AccountProfile(getSubject());
			application.setOwner(owner);
		}
		
		application.setStatus("WORK_IN_PROGRESS");
		
		SalesforceConnector connector = salesforceConnectorService.findSalesforceConnector(connectorId);
		
		if (importSandboxes) {
			application.setEnvironments(connector.getEnvironments());
		} else {
			application.addEnvironment(connector.getEnvironments().stream().filter(e -> ! e.getIsSandbox()).findFirst().get());
		}
		
		super.createApplication(application);
	}
	
	/**
	 * 
	 * @param id
	 * @param application
	 */
	
	public void updateApplication(String id, Application application) {
		Application original = findApplication( id );
		
		application.setId(id);
		application.setCreatedDate(original.getCreatedDate());
		application.setSystemCreatedDate(original.getSystemCreatedDate());
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
	 * @param id
	 */
	
	public void deleteApplication(String id) {		
		Application resource = findApplication(id);
		super.deleteApplication(resource);
	}
	
	/**
	 * 
	 */
	
	public Application findApplication(String id) {
		return super.findApplication(id);
	}	
	
	/**
	 * 
	 * @param id
	 * @param key
	 * @param environment
	 */
	
	public void updateEnvironment(String id, String key, Environment environment) {
		Application application = findApplication( id );
		
		environment.setKey(key);
		
		updateEnvironment(application, environment);
	} 
	
	/**
	 * 
	 * @param id
	 * @param key
	 * @param parameters
	 * @return
	 */
	
	public Environment updateEnvironment(String id, String key, MultivaluedMap<String, String> parameters) {
		
		Application application = findApplication( id );
		
		Environment environment = application.getEnvironments()
				.stream()
				.filter(e -> key.equals(e.getKey()))
				.findFirst()
				.get();
		
		if (parameters.containsKey("test") || Boolean.valueOf(parameters.getFirst("test"))) {
			
			application.getEnvironments().removeIf(e -> key.equals(e.getKey()));
			
			//environmentService.testConnection(environment, parameters);
			
			application.addEnvironment(environment);
			
			updateApplication( id, application );
			
		} else {
			
			//environmentService.updateEnvironment(environment, parameters);
			
			updateEnvironment(application, environment);
		}
		
		return environment;
	}
	
	/**
	 * 
	 * @param id
	 * @param key
	 * @return
	 */
	
	public Environment getEnvironment(String id, String key) {
		Application resource = findApplication(id);
		
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

		Application resource = findApplication(id);
		
		if (resource.getEnvironments() != null && resource.getEnvironments().stream().filter(e -> e.getOrganizationId().equals(loginResult.getOrganizationId())).findFirst().isPresent()) {
			throw new ValidationException(String.format("Unable to add new environment. Conflict with existing organization: %s with Id: ", loginResult.getOrganizationName(), loginResult.getOrganizationId()));
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
		
		UserProperties.saveSalesforceCredentials(getSubject(), environment.getKey(), environment.getPassword(), environment.getSecurityToken());
		
		environment.setPassword(null);
		environment.setSecurityToken(null);
		
		resource.addEnvironment(environment);
		
		updateApplication(id, resource);
	}
	
	/**
	 * 
	 * @param resource
	 * @param environment
	 */
	
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
				
				throw new ValidationException(String.format("Unable to update environment. Conflict with existing organization: %s", loginResult.getOrganizationId()));
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
		
		UserProperties.saveSalesforceCredentials(getSubject(), environment.getKey(), environment.getPassword(), environment.getSecurityToken());
		
		environment.setPassword(null);
		environment.setSecurityToken(null);
		
		resource.addEnvironment(environment);
		
		updateApplication(resource.getId(), resource);
	}
	
	/**
	 * 
	 * @param id
	 * @param key
	 */
	
	public void removeEnvironment(String id, String key) {
		Application resource = findApplication(id);
		
		Environment environment = resource.getEnvironments()
				.stream()
				.filter(e -> key.equals(e.getKey()))
				.findFirst()
				.get();
		
		UserProperties.saveSalesforceCredentials(getSubject(), environment.getKey(), environment.getPassword(), environment.getSecurityToken());
		
		resource.getEnvironments().removeIf(e -> key.equals(e.getKey()));
		
		updateApplication(id, resource);
	}
}