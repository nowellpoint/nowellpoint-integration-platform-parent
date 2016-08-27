package com.nowellpoint.aws.api.service;

import java.net.URI;
import java.util.Set;

import javax.inject.Inject;

import com.nowellpoint.aws.api.dto.AccountProfileDTO;
import com.nowellpoint.aws.api.dto.ApplicationDTO;
import com.nowellpoint.aws.api.dto.Id;
import com.nowellpoint.aws.api.dto.SalesforceConnectorDTO;
import com.nowellpoint.aws.api.model.Application;

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
	
	public ApplicationDTO updateApplication(ApplicationDTO resource) {
		/// need to fix this to accept id as a paramter
		ApplicationDTO original = findApplication( new Id(resource.getId()) );
		resource.setCreatedById(original.getCreatedById());
		resource.setCreatedDate(original.getCreatedDate());
		
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
	
	public void deleteApplication(String applicationId, String subject, URI eventSource) {		
		ApplicationDTO resource = new ApplicationDTO(applicationId);
		
		delete(resource);
		
		hdel( subject, ApplicationDTO.class.getName().concat(applicationId) );
		hdel( applicationId, subject );
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
}