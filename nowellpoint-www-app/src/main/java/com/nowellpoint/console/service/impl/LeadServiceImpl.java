package com.nowellpoint.console.service.impl;

import org.mongodb.morphia.query.Query;

import com.nowellpoint.console.entity.Lead;
import com.nowellpoint.console.entity.LeadDAO;
import com.nowellpoint.console.entity.UserProfile;
import com.nowellpoint.console.model.LeadRequest;
import com.nowellpoint.console.service.AbstractService;
import com.nowellpoint.console.service.LeadService;

public class LeadServiceImpl extends AbstractService implements LeadService {
	
	private static final UserProfile who = getSystemAdmin();
	
	private LeadDAO leadDAO;

	public LeadServiceImpl() {
		leadDAO = new LeadDAO(com.nowellpoint.console.entity.Lead.class, datastore);
	}
	
	@Override
	public Lead createLead(LeadRequest request) {
		com.nowellpoint.console.entity.Lead entity = modelMapper.map(request, Lead.class);
		entity.setCreatedBy(who);
		entity.setCreatedOn(getCurrentDateTime());
		entity.setLastUpdatedBy(who);
		entity.setLastUpdatedOn(getCurrentDateTime());
		leadDAO.save(entity);
		return entity;
	}
	
	private static UserProfile getSystemAdmin() {
		Query<UserProfile> query = datastore.createQuery(UserProfile.class)
				.field("username")
				.equal("system.administrator@nowellpoint.com");
				 
		UserProfile userProfile = query.get();
		
		return userProfile;
	}
}