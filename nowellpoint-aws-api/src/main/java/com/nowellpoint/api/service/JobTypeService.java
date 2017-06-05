package com.nowellpoint.api.service;

import com.nowellpoint.api.rest.domain.JobType;
import com.nowellpoint.api.rest.domain.JobTypeList;

public interface JobTypeService {
	
	public JobType findById(String id);
	
	public JobType findByCode(String code);
	
	public JobTypeList findByLanguage(String languageSidKey);
	
	public void createScheduledJobType(JobType jobType);
	
	public void updateScheduledJobType(String id, JobType jobType);
	
	public void deleteScheduledJobType(String id);
}