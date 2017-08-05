package com.nowellpoint.api.rest.service;

import java.time.Instant;
import java.util.Date;

import com.mongodb.client.model.Filters;
import com.nowellpoint.api.rest.domain.JobType;
import com.nowellpoint.api.rest.domain.JobTypeList;
import com.nowellpoint.api.rest.domain.AbstractUserInfo;
import com.nowellpoint.api.service.JobTypeService;
import com.nowellpoint.api.util.UserContext;

public class JobTypeServiceImpl extends AbstractJobTypeService implements JobTypeService {

	public JobTypeServiceImpl() {
		
	}
	
	@Override
	public JobType findById(String id) {
		return super.findById( id );
	}
	
	@Override
	public JobType findByCode(String code) {
		return super.findOne( Filters.eq ( "code", code ) ); 
	}
	
	@Override
	public JobTypeList findByLanguage(String languageSidKey) {
		return super.query( Filters.eq ( "languageSidKey", languageSidKey ) ); 
	}
	
	@Override
	public void createScheduledJobType(JobType jobType) {
		AbstractUserInfo abstractUserInfo = AbstractUserInfo.of(UserContext.getPrincipal().getName());
		
		Date now = Date.from(Instant.now());
		
		jobType.setCreatedOn(now);
		jobType.setCreatedBy(abstractUserInfo);
		jobType.setLastUpdatedOn(now);
		jobType.setLastUpdatedBy(abstractUserInfo);
		
		create(jobType);
	}
	
	@Override
	public void updateScheduledJobType(String id, JobType jobType) {
		jobType.setId(id);
		
		AbstractUserInfo abstractUserInfo = AbstractUserInfo.of(UserContext.getPrincipal().getName());
		
		Date now = Date.from(Instant.now());
				
		jobType.setLastUpdatedOn(now);
		jobType.setLastUpdatedBy(abstractUserInfo);
		
		update(jobType);
	}
	
	@Override
	public void deleteScheduledJobType(String id) {
		JobType jobType = findById(id);
		delete(jobType);
	}
}