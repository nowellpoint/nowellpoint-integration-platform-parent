package com.nowellpoint.aws.api.service;

import java.util.Set;

import com.nowellpoint.aws.api.dto.SalesforceInstanceDTO;
import com.nowellpoint.aws.data.mongodb.SalesforceInstance;

public class SalesforceInstanceService extends AbstractDocumentService<SalesforceInstanceDTO, SalesforceInstance> {
	
	public SalesforceInstanceService() {
		super(SalesforceInstanceDTO.class, SalesforceInstance.class);
	}
	
	public Set<SalesforceInstanceDTO> getAll(String subject) {
		Set<SalesforceInstanceDTO> resources = hscan( subject, SalesforceInstanceDTO.class );
		
		if (resources.isEmpty()) {
			resources = findAllByOwner(subject);
			hset( subject, resources );
		}
		
		return resources;
	}

	public SalesforceInstanceDTO createSalesforceInstance(SalesforceInstanceDTO resource) {
		create( resource );
		
		hset( resource.getSubject(), SalesforceInstanceDTO.class.getName().concat( resource.getId()), resource );
		hset( resource.getId(), resource.getSubject(), resource );
		
		return resource;
	}
}