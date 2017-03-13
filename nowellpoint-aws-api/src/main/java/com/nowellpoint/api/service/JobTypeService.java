package com.nowellpoint.api.service;

import com.nowellpoint.api.rest.domain.JobType;
import com.nowellpoint.api.rest.domain.JobTypeList;

public interface JobTypeService {
	
	/**
	 * 
	 * 
	 * 
	 */
	
	JobType findById(String id);
	
	/**
	 * 
	 * 
	 * 
	 */
	
	JobTypeList findByLanguage(String languageSidKey);
	
	/**
	 * 
	 * 
	 * 
	 */
	
	void createScheduledJobType(JobType jobType);
	
	/**
	 * 
	 * 
	 * 
	 */
	
	void updateScheduledJobType(String id, JobType jobType);
	
	/**
	 * 
	 * 
	 * 
	 */
	
	void deleteScheduledJobType(String id);
}