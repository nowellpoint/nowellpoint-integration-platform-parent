package com.nowellpoint.console.service.impl;

import com.nowellpoint.console.entity.LeadDAO;
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
		
		return create(lead);
	}
	
	private Lead create(Lead lead) {
		com.nowellpoint.console.entity.Lead entity = modelMapper.map(lead, com.nowellpoint.console.entity.Lead.class);
		entity.setCreatedBy(getSystemAdmin());
		entity.setCreatedOn(getCurrentDateTime());
		entity.setLastUpdatedBy(entity.getCreatedBy());
		entity.setLastUpdatedOn(entity.getCreatedOn());
		leadDAO.save(entity);
		return Lead.of(entity);
	}
}