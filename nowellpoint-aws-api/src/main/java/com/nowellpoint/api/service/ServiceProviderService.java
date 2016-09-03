package com.nowellpoint.api.service;

import java.util.Set;

import com.nowellpoint.api.document.service.ServiceProviderDocumentService;
import com.nowellpoint.api.dto.Id;
import com.nowellpoint.api.dto.ServiceProviderDTO;

/**
 * 
 * 
 * @author jherson
 * 
 *
 */

public class ServiceProviderService extends ServiceProviderDocumentService {
	
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
	
	public Set<ServiceProviderDTO> getAllActive(String localeSidKey, String languageLocaleKey) {
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
	
	public ServiceProviderDTO findByServiceKey(String key) {
		return super.findByServiceKey(key);
	}
	
	/**
	 * 
	 * 
	 * 
	 * 
	 */
	
	public void createServiceProvider(ServiceProviderDTO serviceProvider) {
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
	
	public void updateServiceProvider(Id id, ServiceProviderDTO serviceProvider) {
		ServiceProviderDTO original = getServiceProvider( id );
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
		ServiceProviderDTO serviceProvider = getServiceProvider( id );
		super.deleteServiceProvider(serviceProvider);
	}
	
	/**
	 * 
	 * @param id
	 * @param subject
	 * @return
	 */
	
	public ServiceProviderDTO getServiceProvider(Id id) {
		return super.findServiceProvider(id);
	}
}