package com.nowellpoint.aws.api.service;

import java.sql.Date;
import java.time.Instant;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.ws.rs.core.MultivaluedMap;

import com.nowellpoint.aws.api.dto.AccountProfileDTO;
import com.nowellpoint.aws.api.dto.ApplicationDTO;
import com.nowellpoint.aws.api.dto.EnvironmentDTO;
import com.nowellpoint.aws.api.dto.Id;
import com.nowellpoint.aws.api.dto.SalesforceConnectorDTO;
import com.nowellpoint.aws.api.dto.ServiceInstanceDTO;
import com.nowellpoint.aws.api.model.Application;
import com.nowellpoint.aws.api.model.dynamodb.UserProperties;
import com.nowellpoint.aws.api.model.dynamodb.UserProperty;
import com.nowellpoint.client.sforce.model.LoginResult;

/**************************************************************************************************************************
 * 
 * 
 * @author jherson
 *
 * 
 *************************************************************************************************************************/

public class ApplicationService extends AbstractDocumentService<ApplicationDTO, Application> {
	
	@Inject
	private SalesforceConnectorService salesforceConnectorService;
	
	@Inject
	private SalesforceService salesforceService;
	
	@Inject
	private EnviromentService environmentService;
	
	/**************************************************************************************************************************
	 * 
	 * 
	 * constructor
	 * 
	 * 
	 *************************************************************************************************************************/
	
	public ApplicationService() {
		super(ApplicationDTO.class, Application.class);
	}
	
	/**
	 * 
	 * @param subject
	 * @return
	 */
	
	public Set<ApplicationDTO> getAll(String subject) {
		Set<ApplicationDTO> resources = hscan( subject, ApplicationDTO.class );
		if (resources.isEmpty()) {
			resources = findAllByOwner(subject);
			hset( subject, resources );
		}
		return resources;
	}
	
	/**
	 * 
	 * @param subject
	 * @param resource
	 * @param eventSource
	 * @return
	 */
	
	public ApplicationDTO createApplication(ApplicationDTO resource, String connectorId, Boolean importSandboxes, Boolean importServices) {
		
		if (resource.getOwner() == null) {
			AccountProfileDTO owner = new AccountProfileDTO();
			owner.setHref(getSubject());
			resource.setOwner(owner);
		}
		
		resource.setStatus("WORK_IN_PROGRESS");
		
		SalesforceConnectorDTO connector = salesforceConnectorService.find(connectorId);
		
		if (importSandboxes) {
			resource.setEnvironments(connector.getEnvironments());
		} else {
			resource.addEnvironment(connector.getEnvironments().stream().filter(e -> ! e.getIsSandbox()).findFirst().get());
		}
		
		if (importServices) {
			resource.setServiceInstances(connector.getServiceInstances());
		}
		
		create(resource);

		hset( getSubject(), ApplicationDTO.class.getName().concat(resource.getId()), resource );
		hset( resource.getId(), getSubject(), resource );
		
		return resource;
	}
	
	/**
	 * 
	 * @param subject
	 * @param resource
	 * @param eventSource
	 * @return
	 */
	
	public ApplicationDTO updateApplication(Id id, ApplicationDTO resource) {
		ApplicationDTO original = findApplication( id );
		resource.setId(id.getValue());
		resource.setCreatedById(original.getCreatedById());
		resource.setCreatedDate(original.getCreatedDate());
		resource.setEnvironments(original.getEnvironments());
		resource.setServiceInstances(original.getServiceInstances());
		resource.setSystemCreationDate(original.getSystemCreationDate());
		resource.setSystemModifiedDate(original.getSystemModifiedDate());
		
		if (resource.getStatus() == null) {
			resource.setStatus(original.getStatus());
		}
		
		if (resource.getOwner() == null) {
			resource.setOwner(original.getOwner());
		}
		
		replace(resource);
		
		hset( getSubject(), ApplicationDTO.class.getName().concat(resource.getId()), resource );
		hset( resource.getId(), getSubject(), resource );

		return resource;
	}
	
	/**
	 * 
	 * @param applicationId
	 * @param subject
	 * @param eventSource
	 */
	
	public void deleteApplication(Id id) {		
		ApplicationDTO resource = findApplication(id);
		
		delete(resource);
		
		hdel( getSubject(), ApplicationDTO.class.getName().concat(id.getValue()) );
		hdel( id.getValue(), getSubject() );
	}
	
	/**
	 * 
	 * @param id
	 * @param subject
	 * @return
	 */
	
	public ApplicationDTO findApplication(Id id) {
		ApplicationDTO resource = hget( ApplicationDTO.class, id.getValue(), getSubject() );
		if ( resource == null ) {		
			resource = find(id.getValue());
			if (resource != null) {
				hset( id.getValue(), getSubject(), resource );
			}
		}
		return resource;
	}	
	
	/**************************************************************************************************************************
	 * 
	 * 
	 * @param id
	 * @param key
	 * @param parameters
	 * @return updated EnvironmentDTO
	 * 
	 * 
	 *************************************************************************************************************************/
	
	public EnvironmentDTO updateEnvironment(Id id, String key, MultivaluedMap<String, String> parameters) {
		
		ApplicationDTO resource = findApplication( id );
		
		EnvironmentDTO environment = resource.getEnvironments()
				.stream()
				.filter(e -> key.equals(e.getKey()))
				.findFirst()
				.get();
		
		if (parameters.containsKey("test") || Boolean.valueOf(parameters.getFirst("test"))) {
			
			resource.getEnvironments().removeIf(e -> key.equals(e.getKey()));
			
			environmentService.testConnection(environment, parameters);
			
			resource.addEnvironment(environment);
			
			updateApplication( id, resource );
			
		} else {
			
			environmentService.updateEnvironment(environment, parameters);
			
			updateEnvironment(resource, environment);
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
	
	public EnvironmentDTO getEnvironment(Id id, String key) {
		ApplicationDTO resource = findApplication(id);
		
		EnvironmentDTO environment = resource.getEnvironments()
				.stream()
				.filter(p -> p.getKey().equals(key))
				.findFirst()
				.get();
		
		return environment;
	}
	
	public void updateEnvironment(ApplicationDTO resource, EnvironmentDTO environment) {
		
		EnvironmentDTO original = resource.getEnvironments()
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
		
		List<UserProperty> properties = environmentService.getEnvironmentUserProperties(getSubject(), environment);
		
		UserProperties.batchSave(properties);
		
		environment.setPassword(null);
		environment.setSecurityToken(null);
		
		resource.addEnvironment(environment);
		
		updateApplication(new Id(resource.getId()), resource);
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
		ApplicationDTO resource = findApplication(id);
		
		EnvironmentDTO environment = resource.getEnvironments()
				.stream()
				.filter(e -> key.equals(e.getKey()))
				.findFirst()
				.get();
		
		List<UserProperty> properties = environmentService.getEnvironmentUserProperties(getSubject(), environment);
		
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
		ApplicationDTO resource = findApplication(id);
		
		ServiceInstanceDTO serviceInstance = resource.getServiceInstances()
				.stream()
				.filter(s -> key.equals(s.getKey()))
				.findFirst()
				.get();
		
		return serviceInstance;
		
	}
}