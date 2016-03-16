package com.nowellpoint.aws.api.service;

import java.net.URI;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import com.nowellpoint.aws.api.dto.ServiceProviderInstanceDTO;
import com.nowellpoint.aws.data.mongodb.ServiceProviderInstance;

public class ServiceProviderService extends AbstractDataService<ServiceProviderInstanceDTO, ServiceProviderInstance> {
	
	public ServiceProviderService() {
		super(ServiceProviderInstanceDTO.class, ServiceProviderInstance.class);
	}
	
	/**
	 * 
	 * @param subject
	 * @return
	 */
	
	public Set<ServiceProviderInstanceDTO> getAll(String subject) {
		Set<ServiceProviderInstanceDTO> resources = hscan( subject, ServiceProviderInstanceDTO.class );
		
		if (resources.isEmpty()) {
			resources = findAllByOwner(subject);
			hset( subject, resources );
		}
		
		return resources;
	}
	
	/**
	 * 
	 * @param subject
	 * @param type
	 * @param account
	 * @return
	 */
	
	public ServiceProviderInstanceDTO queryServiceProvider(String subject, String type, String account) {
		Set<ServiceProviderInstanceDTO> resources = getAll(subject);
		
		Predicate<ServiceProviderInstanceDTO> predicate = p -> p.getType().equals(type) && p.getAccount().equals(account);
		
		Optional<ServiceProviderInstanceDTO> query = resources.stream().filter(predicate).findFirst();
		
		if (query.isPresent()) {
			return query.get();
		}
		
		return null;
	}
	
	/**
	 * 
	 * @param subject
	 * @param resource
	 * @param eventSource
	 * @return
	 */
	
	public ServiceProviderInstanceDTO createServiceProvider(String subject, ServiceProviderInstanceDTO resource, URI eventSource) {
		if (queryServiceProvider(subject, resource.getType(), resource.getAccount()) != null) {
			throw new WebApplicationException(
					String.format("Resource of type ServiceProvider already exists for the following values...Subject: %s, Type: %s, Account: %s", 
							subject, resource.getType(), resource.getAccount()), Status.FORBIDDEN);
		}
		
		create(subject, resource, eventSource);

		hset( subject, ServiceProviderInstanceDTO.class.getName().concat(resource.getId()), resource );
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
	
	public ServiceProviderInstanceDTO updateServiceProvider(String subject, ServiceProviderInstanceDTO resource, URI eventSource) {
		ServiceProviderInstanceDTO original = getServiceProvider( resource.getId(), subject );
		resource.setCreatedById(original.getCreatedById());
		resource.setCreatedDate(original.getCreatedDate());
		
		update(subject, resource, eventSource);
		
		hset( subject, ServiceProviderInstanceDTO.class.getName().concat(resource.getId()), resource );
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
		ServiceProviderInstanceDTO resource = new ServiceProviderInstanceDTO(serviceProviderId);
		
		delete(subject, resource, eventSource);
		
		hdel( subject, ServiceProviderInstanceDTO.class.getName().concat(serviceProviderId) );
		hdel( serviceProviderId, subject );
	}
	
	/**
	 * 
	 * @param id
	 * @param subject
	 * @return
	 */
	
	public ServiceProviderInstanceDTO getServiceProvider(String id, String subject) {
		ServiceProviderInstanceDTO resource = hget( ServiceProviderInstanceDTO.class, id, subject );
		
		if ( resource == null ) {
			resource = find(id);
			hset( id, subject, resource );
		}
		
		return resource;
	}	
}