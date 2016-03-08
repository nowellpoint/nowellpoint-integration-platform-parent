package com.nowellpoint.aws.api.service;

import java.net.URI;
import java.util.Set;

import com.nowellpoint.aws.api.dto.ServiceProviderDTO;
import com.nowellpoint.aws.data.mongodb.Application;

public class ServiceProviderService extends AbstractDataService<ServiceProviderDTO, Application> {
	
	public ServiceProviderService() {
		super(ServiceProviderDTO.class, Application.class);
	}
	
	/**
	 * 
	 * @param subject
	 * @return
	 */
	
	public Set<ServiceProviderDTO> getAll(String subject) {
		Set<ServiceProviderDTO> resources = hscan( subject, ServiceProviderDTO.class );
		
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
	
	public ServiceProviderDTO createServiceProvider(String subject, ServiceProviderDTO resource, URI eventSource) {
		createIdentity(subject, resource, eventSource);

		hset( subject, ServiceProviderDTO.class.getName().concat(resource.getId()), resource );
		hset( resource.getId(), subject, resource );
		
		return resource;
	}
	
	/**
	 * 
	 * @param subject
	 * @param resource
	 * @param eventSource
	 * @return
	 */
	
	public ServiceProviderDTO updateServiceProvider(String subject, ServiceProviderDTO resource, URI eventSource) {
		ServiceProviderDTO original = getServiceProvider( resource.getId(), subject );
		resource.setCreatedById(original.getCreatedById());
		resource.setCreatedDate(original.getCreatedDate());
		
		updateApplication(subject, resource, eventSource);
		
		hset( subject, ServiceProviderDTO.class.getName().concat(resource.getId()), resource );
		hset( resource.getId(), subject, resource );

		return resource;
	}
	
	/**
	 * 
	 * @param applicationId
	 * @param subject
	 * @param eventSource
	 */
	
	public void deleteServiceProvider(String serviceProviderId, String subject, URI eventSource) {		
		ServiceProviderDTO resource = new ServiceProviderDTO(serviceProviderId);
		
		delete(subject, resource, eventSource);
		
		hdel( subject, ServiceProviderDTO.class.getName().concat(serviceProviderId) );
		hdel( serviceProviderId, subject );
	}
	
	/**
	 * 
	 * @param id
	 * @param subject
	 * @return
	 */
	
	public ServiceProviderDTO getServiceProvider(String id, String subject) {
		ServiceProviderDTO resource = hget( ServiceProviderDTO.class, id, subject );
		
		if ( resource == null ) {
			resource = find(id);
			hset( id, subject, resource );
		}
		
		return resource;
	}	
}