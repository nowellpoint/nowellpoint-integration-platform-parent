package com.nowellpoint.api.service;

import java.util.Set;

import com.nowellpoint.api.model.dto.Id;
import com.nowellpoint.api.model.dto.ServiceProvider;
import com.nowellpoint.api.model.mapper.ServiceProviderModelMapper;

/**
 * 
 * 
 * @author jherson
 * 
 *
 */

public class ServiceProviderService extends ServiceProviderModelMapper {
	
	/**
	 * 
	 * 
	 * 
	 * 
	 */
	
	public ServiceProviderService() {

	}
	
	/**
	 * 
	 * 
	 * 
	 * 
	 */
	
	public Set<ServiceProvider> getAllActive(String localeSidKey, String languageLocaleKey) {
		return super.getAllActive(localeSidKey, languageLocaleKey);
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
		return super.findByServiceKey(key);
	}
	
	/**
	 * 
	 * 
	 * 
	 * 
	 */
	
	public void createServiceProvider(ServiceProvider serviceProvider) {
		super.createServiceProvider(serviceProvider);
	}
	
	/**
	 * 
	 * 
	 * @param subject
	 * @param resource
	 * @param eventSource
	 * @return
	 * 
	 * 
	 */
	
	public void updateServiceProvider(Id id, ServiceProvider serviceProvider) {
		ServiceProvider original = getServiceProvider( id );
		serviceProvider.setCreatedById(original.getCreatedById());
		serviceProvider.setCreatedDate(original.getCreatedDate());
		
		super.updateServiceProvider(serviceProvider);
	}
	
	/**
	 * 
	 * 
	 * @param id
	 * 
	 * 
	 */
	
	public void deleteServiceProvider(Id id) {		
		ServiceProvider serviceProvider = getServiceProvider( id );
		super.deleteServiceProvider(serviceProvider);
	}
	
	/**
	 * 
	 * @param id
	 * @param subject
	 * @return
	 */
	
	public ServiceProvider getServiceProvider(Id id) {
		return super.findServiceProvider(id);
	}
}