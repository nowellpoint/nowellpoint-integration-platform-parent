package com.nowellpoint.console.service;

import org.bson.types.ObjectId;

import com.nowellpoint.console.entity.OrganizationDAO;
import com.nowellpoint.console.model.ModifiableOrganization;
import com.nowellpoint.console.model.Organization;

public class OrganizationService extends AbstractService {
	
	private OrganizationDAO organizationDAO;
	
	public OrganizationService() {
		organizationDAO = new OrganizationDAO(com.nowellpoint.console.entity.Organization.class, datastore);
	}

	public Organization get(String id) {
		com.nowellpoint.console.entity.Organization entity = organizationDAO.get(new ObjectId(id));
		ModifiableOrganization organization = modelMapper.map(entity, ModifiableOrganization.class);
		return organization.toImmutable();
	}
}