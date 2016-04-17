package com.nowellpoint.aws.api.service;

import java.net.URI;
import java.util.Optional;
import java.util.Set;

import javax.ws.rs.core.UriBuilder;

import com.nowellpoint.aws.api.dto.ParentDTO;
import com.nowellpoint.aws.api.dto.SalesforceConnectorDTO;
import com.nowellpoint.aws.api.dto.ServiceInstanceDTO;
import com.nowellpoint.aws.api.resource.SalesforceConnectorResource;
import com.nowellpoint.aws.data.mongodb.SalesforceConnector;
import com.nowellpoint.aws.data.mongodb.ServiceInstance;

public class SalesforceConnectorService extends AbstractDocumentService<SalesforceConnectorDTO, SalesforceConnector> {
	
	public SalesforceConnectorService() {
		super(SalesforceConnectorDTO.class, SalesforceConnector.class);
	}
	
	public Set<SalesforceConnectorDTO> getAll(String subject) {
		Set<SalesforceConnectorDTO> resources = hscan( subject, SalesforceConnectorDTO.class );		
		if (resources.isEmpty()) {
			resources = findAllByOwner(subject);
			hset( subject, resources );
		}
		return resources;
	}

	public SalesforceConnectorDTO createSalesforceConnector(SalesforceConnectorDTO resource) {
		create( resource );
		hset( resource.getSubject(), SalesforceConnectorDTO.class.getName().concat( resource.getId()), resource );
		hset( resource.getId(), resource.getSubject(), resource );
		return resource;
	}
	
	public SalesforceConnectorDTO updateSalesforceConnector(SalesforceConnectorDTO resource) {
		replace( resource );
		hset( resource.getSubject(), SalesforceConnectorDTO.class.getName().concat( resource.getId()), resource );
		hset( resource.getId(), resource.getSubject(), resource );
		return resource;
	}
	
	public void deleteSalesforceConnector(String id, String subject) {
		SalesforceConnectorDTO resource = new SalesforceConnectorDTO(id);
		delete(resource);
		hdel( subject, SalesforceConnectorDTO.class.getName().concat(id) );
		hdel( id, subject );
	}
	
	public SalesforceConnectorDTO findSalesforceConnector(String subject, String id) {
		SalesforceConnectorDTO resource = hget( SalesforceConnectorDTO.class, id, subject );
		if ( resource == null ) {		
			resource = find(id);
			hset( id, subject, resource );
		}
		return resource;
	}
	
	public ServiceInstanceDTO getServiceInstance(String subject, String id, String key) {
		SalesforceConnectorDTO salesforceConnector = findSalesforceConnector(subject, id);
		
		Optional<ServiceInstance> serviceInstance = salesforceConnector.getServiceInstances().stream().filter(p -> p.getKey().equals(key)).findFirst();
		
		ServiceInstanceDTO resource = null;
		
		if (serviceInstance.isPresent()) {
			URI uri = UriBuilder.fromResource(SalesforceConnectorResource.class)
					.path("/{id}")
					.build(id);
			
			resource = modelMapper.map(serviceInstance.get(), ServiceInstanceDTO.class);
			resource.setParent(new ParentDTO(SalesforceConnector.class.getSimpleName(), id, uri.toString()));
		}
		
		return resource;
	}
}