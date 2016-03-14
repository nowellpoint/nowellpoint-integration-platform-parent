package com.nowellpoint.aws.api.service;

import java.net.URI;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import com.nowellpoint.aws.api.dto.ServiceProviderDTO;
import com.nowellpoint.aws.data.mongodb.ServiceProvider;

public class ServiceProviderService extends AbstractDataService<ServiceProviderDTO, ServiceProvider> {
	
	public ServiceProviderService() {
		super(ServiceProviderDTO.class, ServiceProvider.class);
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
		
		Set<ServiceProviderDTO> resources = getAll(subject);
		
		Optional<ServiceProviderDTO> query = resources.stream().filter(exists(resource.getType(), resource.getAccount(), resource.getKey())).findFirst();
		
		if (query.isPresent()) {
			resource.setId(query.get().getId());
			return updateServiceProvider(subject, resource, eventSource);
		}
		
		create(subject, resource, eventSource);

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
		
		update(subject, resource, eventSource);
		
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
	
	private static Predicate<ServiceProviderDTO> exists(String type, String account, String key) {
		return p -> p.getType().equals(type) && 
				p.getAccount().equals(account) && 
				p.getKey().equals(key);
	}
}