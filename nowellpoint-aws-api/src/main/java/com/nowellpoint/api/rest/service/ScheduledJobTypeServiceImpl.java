package com.nowellpoint.api.rest.service;

import java.time.Instant;
import java.util.Date;

import com.mongodb.client.model.Filters;
import com.nowellpoint.api.rest.domain.ScheduledJobType;
import com.nowellpoint.api.rest.domain.ScheduledJobTypeList;
import com.nowellpoint.api.rest.domain.UserInfo;
import com.nowellpoint.api.service.ScheduledJobTypeService;
import com.nowellpoint.api.util.UserContext;

public class ScheduledJobTypeServiceImpl extends AbstractScheduledJobTypeService implements ScheduledJobTypeService {

	public ScheduledJobTypeServiceImpl() {
		
	}
	
	@Override
	public ScheduledJobType findById(String id) {
		return super.findById( id );
	}
	
	@Override
	public ScheduledJobTypeList findByLanguage(String languageSidKey) {
		return super.query( Filters.eq ( "languageSidKey", languageSidKey ) ); 
	}
	
	@Override
	public void createScheduledJobType(ScheduledJobType scheduledJobType) {
		UserInfo userInfo = new UserInfo(UserContext.getPrincipal().getName());
		
		Date now = Date.from(Instant.now());
		
		scheduledJobType.setCreatedOn(now);
		scheduledJobType.setCreatedBy(userInfo);
		scheduledJobType.setLastUpdatedOn(now);
		scheduledJobType.setLastUpdatedBy(userInfo);
		
		create(scheduledJobType);
	}
	
	@Override
	public void updateScheduledJobType(String id, ScheduledJobType scheduledJobType) {
		scheduledJobType.setId(id);
		
		UserInfo userInfo = new UserInfo(UserContext.getPrincipal().getName());
		
		Date now = Date.from(Instant.now());
				
		scheduledJobType.setLastUpdatedOn(now);
		scheduledJobType.setLastUpdatedBy(userInfo);
		
		update(scheduledJobType);
	}
	
	@Override
	public void deleteScheduledJobType(String id) {
		ScheduledJobType scheduledJobType = findById(id);
		delete(scheduledJobType);
	}
}