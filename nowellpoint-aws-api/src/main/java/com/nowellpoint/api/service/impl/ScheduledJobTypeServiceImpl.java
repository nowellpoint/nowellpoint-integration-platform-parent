package com.nowellpoint.api.service.impl;

import java.time.Instant;
import java.util.Date;

import com.mongodb.client.model.Filters;
import com.nowellpoint.api.model.domain.ScheduledJobType;
import com.nowellpoint.api.model.domain.ScheduledJobTypeList;
import com.nowellpoint.api.service.ScheduledJobTypeService;

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
		//UserInfo userInfo = new UserInfo(UserContext.getPrincipal().getName());
		
		Date now = Date.from(Instant.now());
		
		scheduledJobType.setCreatedDate(now);
		//scheduledJobType.setCreatedBy(userInfo);
		scheduledJobType.setLastModifiedDate(now);
		//scheduledJobType.setLastModifiedBy(userInfo);
		scheduledJobType.setSystemCreatedDate(now);
		scheduledJobType.setSystemModifiedDate(now);
		
		create(scheduledJobType);
	}
	
	@Override
	public void updateScheduledJobType(String id, ScheduledJobType scheduledJobType) {
		scheduledJobType.setId(id);
		
		//UserInfo userInfo = new UserInfo(UserContext.getPrincipal().getName());
		
		Date now = Date.from(Instant.now());
				
		scheduledJobType.setLastModifiedDate(now);
		//scheduledJobType.setLastModifiedBy(userInfo);
		scheduledJobType.setSystemModifiedDate(now);
		
		update(scheduledJobType);
	}
	
	@Override
	public void deleteScheduledJobType(String id) {
		ScheduledJobType scheduledJobType = findById(id);
		delete(scheduledJobType);
	}
}