package com.nowellpoint.aws.api.service;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.and;

import java.util.Optional;
import java.util.Set;

import com.mongodb.Block;
import com.mongodb.client.FindIterable;
import com.nowellpoint.aws.api.dto.ServiceProviderDTO;
import com.nowellpoint.aws.api.model.ServiceProvider;
import com.nowellpoint.aws.data.MongoDBDatastore;
import com.nowellpoint.aws.data.annotation.Document;

public class ServiceProviderService extends AbstractDocumentService<ServiceProviderDTO, ServiceProvider> {
	
	public ServiceProviderService() {
		super(ServiceProviderDTO.class, ServiceProvider.class);
	}
	
	public Set<ServiceProviderDTO> getAllActive(String localeSidKey, String languageLocaleKey) {
		Set<ServiceProviderDTO> resources = hscan( ServiceProviderDTO.class.getName().concat(localeSidKey).concat(languageLocaleKey), ServiceProviderDTO.class );
		
		if (resources.isEmpty()) {
			
			String collectionName = ServiceProvider.class.getAnnotation(Document.class).collectionName();
			
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
	
	public ServiceProviderDTO findByServiceKey(String key) {
		return Optional.ofNullable(get(ServiceProviderDTO.class, key)).orElseGet(() -> {
			
			String collectionName = ServiceProvider.class.getAnnotation(Document.class).collectionName();
			
			ServiceProvider document = MongoDBDatastore.getDatabase()
					.getCollection( collectionName )
					.withDocumentClass( ServiceProvider.class )
					.find(eq ( "services.key", key ) )
					.first();
					
			if (document == null) {
				return null;
			}
					
			ServiceProviderDTO resource = modelMapper.map( document, ServiceProviderDTO.class );
					
			set( key, resource );
					
			return resource;
		});	
	}
	
	/**
	 * 
	 * @param subject
	 * @param resource
	 * @param eventSource
	 * @return
	 */
	
	public ServiceProviderDTO createServiceProvider(ServiceProviderDTO resource) {
		create(resource);
		set(resource.getId(), resource);
		return resource;
	}
	
	/**
	 * 
	 * @param subject
	 * @param resource
	 * @param eventSource
	 * @return
	 */
	
	public ServiceProviderDTO updateServiceProvider(ServiceProviderDTO resource) {
		ServiceProviderDTO original = getServiceProvider( resource.getId() );
		resource.setCreatedById(original.getCreatedById());
		resource.setCreatedDate(original.getCreatedDate());
		
		replace(resource);
		set(resource.getId(), resource);
		return resource;
	}
	
	/**
	 * 
	 * @param applicationId
	 * @param subject
	 * @param eventSource
	 */
	
	public void deleteServiceProvider(String serviceProviderId) {		
		ServiceProviderDTO resource = new ServiceProviderDTO(serviceProviderId);
		delete(resource);
		del(serviceProviderId);
	}
	
	/**
	 * 
	 * @param id
	 * @param subject
	 * @return
	 */
	
	public ServiceProviderDTO getServiceProvider(String id) {
		
		ServiceProviderDTO resource = get( ServiceProviderDTO.class, id );
		
		if ( resource == null ) {
			resource = find(id);
			set(resource.getId(), resource);
		}
		
		return resource;
	}
}