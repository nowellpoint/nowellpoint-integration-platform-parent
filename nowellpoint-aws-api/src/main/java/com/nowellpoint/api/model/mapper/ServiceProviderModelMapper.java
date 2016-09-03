package com.nowellpoint.api.model.mapper;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

import java.util.Optional;
import java.util.Set;

import org.modelmapper.TypeToken;

import com.nowellpoint.api.model.document.ServiceProviderDocument;
import com.nowellpoint.api.model.dto.Id;
import com.nowellpoint.api.model.dto.ServiceProvider;

/**
 * 
 * 
 * @author jherson
 *
 *
 */

public class ServiceProviderModelMapper extends AbstractModelMapper<ServiceProviderDocument> {
	
	/**
	 * 
	 * 
	 * 
	 */
	
	public ServiceProviderModelMapper() {
		super(ServiceProviderDocument.class);
	}
	
	/**
	 * 
	 * 
	 * @param id
	 * @return
	 * 
	 * 
	 */
	
	public ServiceProvider findServiceProvider(Id id) {
		Optional<ServiceProviderDocument> document = Optional.ofNullable(findById(id.getValue()));
		ServiceProvider serviceProvider = null;
		if (document.isPresent()) {
			serviceProvider = modelMapper.map(document, ServiceProvider.class);
		}
		return serviceProvider;
	}	
	
	/**
	 * 
	 * 
	 * @param serviceProvider
	 * 
	 * 
	 */
	
	public void createServiceProvider(ServiceProvider serviceProvider) {
		ServiceProviderDocument document = modelMapper.map(serviceProvider, ServiceProviderDocument.class);
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
	
	public void updateServiceProvider(ServiceProvider serviceProvider) {
		ServiceProviderDocument document = modelMapper.map(serviceProvider, ServiceProviderDocument.class);
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
	
	public ServiceProvider findByServiceKey(String key) {
		ServiceProviderDocument document = super.findOne( eq ( "services.key", key ) );
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
	
	public Set<ServiceProvider> getAllActive(String localeSidKey, String languageLocaleKey) {
		Optional<Set<ServiceProviderDocument>> documents = Optional.ofNullable( super.find( and ( 
							eq ( "isActive", Boolean.TRUE ), 
							eq ( "localeSidKey", localeSidKey ), 
							eq ( "languageLocaleKey", languageLocaleKey ) ) ) );
		
		Set<ServiceProvider> serviceProviders = null;
		
		if (documents.isPresent()) {
			serviceProviders = modelMapper.map(documents, new TypeToken<Set<ServiceProvider>>() {}.getType());
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
	
	public void deleteServiceProvider(ServiceProvider serviceProvider) {
		ServiceProviderDocument document = modelMapper.map(serviceProvider, ServiceProviderDocument.class);
		delete(getSubject(), document);
	}
}