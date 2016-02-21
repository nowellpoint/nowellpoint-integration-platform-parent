package com.nowellpoint.aws.api.service;

import java.net.URI;
import java.util.Set;

import com.nowellpoint.aws.api.dto.ApplicationDTO;
import com.nowellpoint.aws.model.data.Application;

public class ApplicationService extends AbstractDataService<ApplicationDTO, Application> {
	
	public ApplicationService() {
		super(ApplicationDTO.class, Application.class);
	}
	
	public Set<ApplicationDTO> getAll(String subject) {
		
		//
		//
		//
		
		Set<ApplicationDTO> resources = hscan( subject, ApplicationDTO.class );
		
		//
		//
		//
		
		if (resources.isEmpty()) {
			resources = findAllByOwner(subject);
			hset( subject, resources );
		}
		
		//
		//
		//
		
		return resources;
		
	}
	
	public ApplicationDTO createApplication(String subject, ApplicationDTO resource, URI eventSource) {
		
		//
		//
		//
		
		create(subject, resource, eventSource);
		
		//
		//
		//

		hset( subject, ApplicationDTO.class.getName().concat(resource.getId()), resource );
		hset( resource.getId(), subject, resource );
		
		//
		//
		//
		
		return resource;
	}
	
	public ApplicationDTO update(String subject, ApplicationDTO resource, URI eventSource) {
		
		//
		//
		//
		
		ApplicationDTO original = getApplication( resource.getId(), subject );
		resource.setCreatedById(original.getCreatedById());
		resource.setCreatedDate(original.getCreatedDate());
		
		//
		//
		//
		
		update(subject, resource, eventSource);
		
		//
		//
		//

		hset( subject, ApplicationDTO.class.getName().concat(resource.getId()), resource );
		hset( resource.getId(), subject, resource );

		//
		//
		//
		
		return resource;
	}
	
	public void deleteApplication(String applicationId, String subject, URI eventSource) {
		
		//
		
		ApplicationDTO resource = new ApplicationDTO(applicationId);
		
		//
		
		delete(subject, resource, eventSource);
		
		//
		
		hdel( subject, ApplicationDTO.class.getName().concat(applicationId) );
		hdel( applicationId, subject );
	}
	
	public ApplicationDTO getApplication(String id, String subject) {
		
		//

		ApplicationDTO resource = hget( id, subject );
		
		//
		
		if ( resource == null ) {
			resource = find(subject, id);
			hset( id, subject, resource );
		}

		//
		
		return resource;
	}
	
}