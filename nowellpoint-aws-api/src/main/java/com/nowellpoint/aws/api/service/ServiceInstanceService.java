package com.nowellpoint.aws.api.service;

import java.util.Set;

import com.nowellpoint.aws.api.dto.ServiceInstanceDTO;
import com.nowellpoint.aws.data.mongodb.ServiceInstance;

public class ServiceInstanceService extends AbstractDataService<ServiceInstanceDTO, ServiceInstance> {

	public ServiceInstanceService() {
		super(ServiceInstanceDTO.class, ServiceInstance.class);
	}
	
	public Set<ServiceInstanceDTO> getAll(String subject) {
		Set<ServiceInstanceDTO> resources = hscan( subject, ServiceInstanceDTO.class );
		
		if (resources.isEmpty()) {
			resources = findAllByOwner(subject);
			hset( subject, resources );
		}
		
		return resources;
	} 

	public ServiceInstanceDTO createServiceInstance(ServiceInstanceDTO resource) {
		create( resource );

		hset( resource.getSubject(), ServiceInstanceDTO.class.getName().concat(resource.getId()), resource );
		hset( resource.getId(), resource.getSubject(), resource );
		
		return resource;
	}
}