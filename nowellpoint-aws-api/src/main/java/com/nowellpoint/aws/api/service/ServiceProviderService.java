package com.nowellpoint.aws.api.service;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.and;

import java.net.URI;
import java.util.Set;

import com.mongodb.Block;
import com.mongodb.client.FindIterable;
import com.nowellpoint.aws.api.dto.ServiceProviderDTO;
import com.nowellpoint.aws.data.MongoDBDatastore;
import com.nowellpoint.aws.data.annotation.MessageHandler;
import com.nowellpoint.aws.data.mongodb.ServiceProvider;

public class ServiceProviderService extends AbstractDataService<ServiceProviderDTO, ServiceProvider> {
	
	public ServiceProviderService() {
		super(ServiceProviderDTO.class, ServiceProvider.class);
	}
	
	public Set<ServiceProviderDTO> getAllActive(String localeSidKey, String languageLocaleKey) {
		Set<ServiceProviderDTO> resources = hscan( ServiceProviderDTO.class.getName().concat(localeSidKey).concat(languageLocaleKey), ServiceProviderDTO.class );
		
		if (resources.isEmpty()) {
			
			String collectionName = ServiceProvider.class.getAnnotation(MessageHandler.class).collectionName();
			
			FindIterable<ServiceProvider> documents = MongoDBDatastore.getDatabase()
					.getCollection( collectionName )
					.withDocumentClass( ServiceProvider.class )
					.find( and ( 
							eq ( "isActive", Boolean.TRUE ), 
							eq ( "localeSidKey", localeSidKey ), 
							eq ( "languageLocaleKey", languageLocaleKey ) ) );
			
			documents.forEach(new Block<ServiceProvider>() {
				@Override
				public void apply(final ServiceProvider document) {
			        resources.add(modelMapper.map( document, ServiceProviderDTO.class ));
			    }
			});

			hset( ServiceProviderDTO.class.getName().concat(localeSidKey).concat(languageLocaleKey), resources );
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
}