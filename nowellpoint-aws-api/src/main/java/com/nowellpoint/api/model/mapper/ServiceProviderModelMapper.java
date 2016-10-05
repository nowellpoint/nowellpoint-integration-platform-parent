package com.nowellpoint.api.model.mapper;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

import java.util.HashSet;
import java.util.Set;

import org.modelmapper.TypeToken;

import com.nowellpoint.api.model.dto.ServiceProvider;

/**
 * 
 * 
 * @author jherson
 *
 *
 */

public class ServiceProviderModelMapper extends AbstractModelMapper<com.nowellpoint.api.model.document.ServiceProvider> {
	
	/**
	 * 
	 * 
	 * 
	 */
	
	public ServiceProviderModelMapper() {
		super(com.nowellpoint.api.model.document.ServiceProvider.class);
	}
	
	/**
	 * 
	 * 
	 * @param id
	 * @return
	 * 
	 * 
	 */
	
	protected ServiceProvider findServiceProvider(String id) {
		com.nowellpoint.api.model.document.ServiceProvider document = findById(id.toString());
		ServiceProvider serviceProvider = modelMapper.map(document, ServiceProvider.class);
		return serviceProvider;
	}	
	
	/**
	 * 
	 * 
	 * @param serviceProvider
	 * 
	 * 
	 */
	
	protected void createServiceProvider(ServiceProvider serviceProvider) {
		com.nowellpoint.api.model.document.ServiceProvider document = modelMapper.map(serviceProvider, com.nowellpoint.api.model.document.ServiceProvider.class);
		create(getSubject(), document);
		modelMapper.map(document, serviceProvider);
	}
	
	/**
	 * 
	 * 
	 * @param serviceProvider
	 * 
	 * 
	 */
	
	protected void updateServiceProvider(ServiceProvider serviceProvider) {
		com.nowellpoint.api.model.document.ServiceProvider document = modelMapper.map(serviceProvider, com.nowellpoint.api.model.document.ServiceProvider.class);
		replace(getSubject(), document);
		modelMapper.map(document, serviceProvider);
	}
	
	/**
	 * 
	 * 
	 * @param key
	 * @return
	 * 
	 * 
	 */
	
	protected ServiceProvider findByServiceKey(String key) {
		com.nowellpoint.api.model.document.ServiceProvider document = super.findOne( eq ( "services.key", key ) );
		ServiceProvider serviceProvider = modelMapper.map( document, ServiceProvider.class );
		return serviceProvider;
	}
	
	/**
	 * 
	 * 
	 * @return
	 * 
	 * 
	 */
	
	protected Set<ServiceProvider> getAllActive(String localeSidKey, String languageLocaleKey) {
		Set<com.nowellpoint.api.model.document.ServiceProvider> documents = super.find( and ( 
							eq ( "isActive", Boolean.TRUE ), 
							eq ( "localeSidKey", localeSidKey ), 
							eq ( "languageLocaleKey", languageLocaleKey ) ) );
		
		Set<ServiceProvider> serviceProviders = null;
		
		if (! documents.isEmpty()) {
			serviceProviders = modelMapper.map(documents, new TypeToken<HashSet<ServiceProvider>>() {}.getType());
		}
		
		return serviceProviders;
	}
	
	/**
	 * 
	 * 
	 * @param serviceProvider
	 * 
	 * 
	 */
	
	protected void deleteServiceProvider(ServiceProvider serviceProvider) {
		com.nowellpoint.api.model.document.ServiceProvider document = modelMapper.map(serviceProvider, com.nowellpoint.api.model.document.ServiceProvider.class);
		delete(getSubject(), document);
	}
}