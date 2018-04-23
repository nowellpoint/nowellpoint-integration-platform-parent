package com.nowellpoint.console.service;

import org.mongodb.morphia.query.Query;

import com.nowellpoint.console.entity.Lead;
import com.nowellpoint.console.entity.LeadDAO;
import com.nowellpoint.console.entity.UserProfile;
import com.nowellpoint.console.model.LeadRequest;

public class LeadService extends AbstractService {
	
	private LeadDAO leadDAO;

	public LeadService() {
		leadDAO = new LeadDAO(com.nowellpoint.console.entity.Lead.class, datastore);
	}
	
	public Lead createLead(LeadRequest request) {
		UserProfile who = getSystemAdmin();
		com.nowellpoint.console.entity.Lead entity = modelMapper.map(request, Lead.class);
		leadDAO.save(entity);
		return entity;
	}
	
	private UserProfile getSystemAdmin() {
		Query<UserProfile> query = datastore.createQuery(UserProfile.class)
				.field("username")
				.equal("system.administrator@nowellpoint.com");
				 
		UserProfile userProfile = query.get();
		
		return userProfile;
	}
}