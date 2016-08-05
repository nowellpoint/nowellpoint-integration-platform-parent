package com.nowellpoint.aws.api.service;

import java.net.URI;
import java.util.Set;

import com.nowellpoint.aws.api.dto.ApplicationDTO;
import com.nowellpoint.aws.api.model.Application;

public class ApplicationService extends AbstractDocumentService<ApplicationDTO, Application> {
	
	private static final String CONFIGURATION_PHASE = "Configuration";
	
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
	
	public ApplicationDTO createApplication(ApplicationDTO resource) {
		resource.setPhase(CONFIGURATION_PHASE);
		
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
		ApplicationDTO original = getApplication( resource.getId(), getSubject() );
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
	
	public ApplicationDTO getApplication(String id, String subject) {
		ApplicationDTO resource = hget( ApplicationDTO.class, id, subject );
		
		if ( resource == null ) {
			resource = find(id);
			hset( id, subject, resource );
		}
		
		return resource;
	}	
}