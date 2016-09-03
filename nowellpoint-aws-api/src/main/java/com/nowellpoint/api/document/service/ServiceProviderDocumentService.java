package com.nowellpoint.api.document.service;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

import java.util.Optional;
import java.util.Set;

import org.modelmapper.TypeToken;

import com.nowellpoint.api.dto.Id;
import com.nowellpoint.api.dto.ServiceProviderDTO;
import com.nowellpoint.api.model.ServiceProvider;
import com.nowellpoint.api.service.AbstractModelMapper;

/**
 * 
 * 
 * @author jherson
 *
 *
 */

public class ServiceProviderDocumentService extends AbstractModelMapper<ServiceProvider> {
	
	/**
	 * 
	 * 
	 * 
	 */
	
	public ServiceProviderDocumentService() {
		super(ServiceProvider.class);
	}
	
	/**
	 * 
	 * 
	 * @param id
	 * @return
	 * 
	 * 
	 */
	
	public ServiceProviderDTO findServiceProvider(Id id) {
		Optional<ServiceProvider> document = Optional.ofNullable(findById(id.getValue()));
		ServiceProviderDTO serviceProvider = null;
		if (document.isPresent()) {
			serviceProvider = modelMapper.map(document, ServiceProviderDTO.class);
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
	
	public void createServiceProvider(ServiceProviderDTO serviceProvider) {
		ServiceProvider document = modelMapper.map(serviceProvider, ServiceProvider.class);
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
	
	public void updateServiceProvider(ServiceProviderDTO serviceProvider) {
		ServiceProvider document = modelMapper.map(serviceProvider, ServiceProvider.class);
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
	
	public ServiceProviderDTO findByServiceKey(String key) {
		ServiceProvider document = super.findOne( eq ( "services.key", key ) );
		ServiceProviderDTO serviceProvider = modelMapper.map( document, ServiceProviderDTO.class );
		return serviceProvider;
	}
	
	/**
	 * 
	 * 
	 * @return
	 * 
	 * 
	 */
	
	public Set<ServiceProviderDTO> getAllActive(String localeSidKey, String languageLocaleKey) {
		Optional<Set<ServiceProvider>> documents = Optional.ofNullable( super.find( and ( 
							eq ( "isActive", Boolean.TRUE ), 
							eq ( "localeSidKey", localeSidKey ), 
							eq ( "languageLocaleKey", languageLocaleKey ) ) ) );
		
		Set<ServiceProviderDTO> serviceProviders = null;
		
		if (documents.isPresent()) {
			serviceProviders = modelMapper.map(documents, new TypeToken<Set<ServiceProviderDTO>>() {}.getType());
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
	
	public void deleteServiceProvider(ServiceProviderDTO serviceProvider) {
		ServiceProvider document = modelMapper.map(serviceProvider, ServiceProvider.class);
		delete(getSubject(), document);
	}
}