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
import com.nowellpoint.api.model.domain.Instance;
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
	
	public ApplicationService() {
		
	}
	
	public Set<Application> findAllByOwner() {
		return null;
	}
	
	public void createApplication(Application application, String connectorId, Boolean importSandboxes, Boolean importServices) {
		
		if (application.getOwner() == null) {
			AccountProfile owner = new AccountProfile(getSubject());
			application.setOwner(owner);
		}
		
		application.setStatus("WORK_IN_PROGRESS");
		
		SalesforceConnector connector = salesforceConnectorService.findSalesforceConnector(connectorId);
		
		if (importSandboxes) {
			application.setEnvironments(connector.getInstances());
		} else {
			application.addEnvironment(connector.getInstances().stream().filter(e -> ! e.getIsSandbox()).findFirst().get());
		}
		
		super.createApplication(application);
	}
	
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
	
	public void deleteApplication(String id) {		
		Application resource = findApplication(id);
		super.deleteApplication(resource);
	}
	
	public Application findApplication(String id) {
		return super.findApplication(id);
	}	
	
	public void updateEnvironment(String id, String key, Instance instance) {
		Application application = findApplication( id );
		
		instance.setKey(key);
		
		updateEnvironment(application, instance);
	} 
	
	public Instance updateEnvironment(String id, String key, MultivaluedMap<String, String> parameters) {
		
		Application application = findApplication( id );
		
		Instance instance = application.getEnvironments()
				.stream()
				.filter(e -> key.equals(e.getKey()))
				.findFirst()
				.get();
		
		if (parameters.containsKey("test") || Boolean.valueOf(parameters.getFirst("test"))) {
			
			application.getEnvironments().removeIf(e -> key.equals(e.getKey()));
			
			application.addEnvironment(instance);
			
			updateApplication( id, application );
			
		} else {
			
			updateEnvironment(application, instance);
		}
		
		return instance;
	}
	
	public Instance getEnvironment(String id, String key) {
		Application resource = findApplication(id);
		
		Instance instance = resource.getEnvironments()
				.stream()
				.filter(p -> p.getKey().equals(key))
				.findFirst()
				.get();
		
		return instance;
	}
	
	public void addEnvironment(String id, Instance instance) {
		LoginResult loginResult = salesforceService.login(instance.getAuthEndpoint(), instance.getUsername(), instance.getPassword(), instance.getSecurityToken());

		Application resource = findApplication(id);
		
		if (resource.getEnvironments() != null && resource.getEnvironments().stream().filter(e -> e.getOrganizationId().equals(loginResult.getOrganizationId())).findFirst().isPresent()) {
			throw new ValidationException(String.format("Unable to add new environment. Conflict with existing organization: %s with Id: %s", loginResult.getOrganizationName(), loginResult.getOrganizationId()));
		}
		
		instance.setKey(UUID.randomUUID().toString().replace("-", ""));
		instance.setIsActive(Boolean.TRUE);
		instance.setIsReadOnly(Boolean.FALSE);
		instance.setIsValid(Boolean.TRUE);
		instance.setAddedOn(Date.from(Instant.now()));
		instance.setUpdatedOn(Date.from(Instant.now()));
		instance.setApiVersion(System.getProperty(Properties.SALESFORCE_API_VERSION));
		instance.setIsSandbox(Boolean.TRUE);
		instance.setUserId(loginResult.getUserId());
		instance.setOrganizationId(loginResult.getOrganizationId());
		instance.setOrganizationName(loginResult.getOrganizationName());
		instance.setServiceEndpoint(loginResult.getServiceEndpoint());
		
		UserProperties.saveSalesforceCredentials(getSubject(), instance.getKey(), instance.getPassword(), instance.getSecurityToken());
		
		instance.setPassword(null);
		instance.setSecurityToken(null);
		
		resource.addEnvironment(instance);
		
		updateApplication(id, resource);
	}
	
	public void updateEnvironment(Application resource, Instance instance) {
		
		Instance original = resource.getEnvironments()
				.stream()
				.filter(e -> instance.getKey().equals(e.getKey()))
				.findFirst()
				.get();
		
		resource.getEnvironments().removeIf(e -> instance.getKey().equals(e.getKey()));
		
		instance.setAddedOn(original.getAddedOn());
		instance.setUpdatedOn(Date.from(Instant.now()));
		instance.setIsReadOnly(original.getIsReadOnly());
		instance.setIsSandbox(original.getIsSandbox());
		instance.setApiVersion(original.getApiVersion());
		instance.setTestMessage(original.getTestMessage());
		
		if (instance.getIsActive()) {
			LoginResult loginResult = salesforceService.login(instance.getAuthEndpoint(), instance.getUsername(), instance.getPassword(), instance.getSecurityToken());
			
			if (! loginResult.getOrganizationId().equals(original.getOrganizationId()) 
					&& resource.getEnvironments().stream().filter(e -> e.getOrganizationId().equals(loginResult.getOrganizationId())).findFirst().isPresent()) {
				
				throw new ValidationException(String.format("Unable to update environment. Conflict with existing organization: %s", loginResult.getOrganizationId()));
			}
			
			instance.setUserId(loginResult.getUserId());
			instance.setOrganizationId(loginResult.getOrganizationId());
			instance.setOrganizationName(loginResult.getOrganizationName());
			instance.setServiceEndpoint(loginResult.getServiceEndpoint());
			instance.setIsValid(Boolean.TRUE);
		} else {
			instance.setUserId(original.getUserId());
			instance.setOrganizationId(original.getOrganizationId());
			instance.setOrganizationName(original.getOrganizationName());
			instance.setServiceEndpoint(original.getServiceEndpoint());
			instance.setIsValid(Boolean.FALSE);
		}
		
		UserProperties.saveSalesforceCredentials(getSubject(), instance.getKey(), instance.getPassword(), instance.getSecurityToken());
		
		instance.setPassword(null);
		instance.setSecurityToken(null);
		
		resource.addEnvironment(instance);
		
		updateApplication(resource.getId(), resource);
	}
	
	public void removeEnvironment(String id, String key) {
		Application resource = findApplication(id);
		
		Instance instance = resource.getEnvironments()
				.stream()
				.filter(e -> key.equals(e.getKey()))
				.findFirst()
				.get();
		
		UserProperties.saveSalesforceCredentials(getSubject(), instance.getKey(), instance.getPassword(), instance.getSecurityToken());
		
		resource.getEnvironments().removeIf(e -> key.equals(e.getKey()));
		
		updateApplication(id, resource);
	}
}