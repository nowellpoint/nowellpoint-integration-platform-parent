package com.nowellpoint.console.service.impl;

import org.mongodb.morphia.query.Query;

import com.nowellpoint.console.entity.LeadDAO;
import com.nowellpoint.console.entity.UserProfile;
import com.nowellpoint.console.model.Lead;
import com.nowellpoint.console.model.LeadRequest;
import com.nowellpoint.console.service.AbstractService;
import com.nowellpoint.console.service.LeadService;

public class LeadServiceImpl extends AbstractService implements LeadService {
	
	private LeadDAO leadDAO;

	public LeadServiceImpl() {
		leadDAO = new LeadDAO(com.nowellpoint.console.entity.Lead.class, datastore);
	}
	
	@Override
	public Lead create(LeadRequest request) {
		
		Lead lead = Lead.builder()
				.email(request.getEmail())
				.firstName(request.getFirstName())
				.lastName(request.getLastName())
				.message(request.getMessage())
				.build();
		
		com.nowellpoint.console.entity.Lead entity = modelMapper.map(lead, com.nowellpoint.console.entity.Lead.class);
		entity.setCreatedBy(getSystemAdmin());
		entity.setCreatedOn(getCurrentDateTime());
		entity.setLastUpdatedOn(entity.getCreatedOn());
		entity.setLastUpdatedBy(entity.getCreatedBy());
		leadDAO.save(entity);
		return Lead.of(entity);
	}
	
	private UserProfile getSystemAdmin() {
		Query<UserProfile> query = datastore.createQuery(UserProfile.class)
				.field("username")
				.equal("system.administrator@nowellpoint.com");
				 
		UserProfile userProfile = query.get();
		
		return userProfile;
	}
}