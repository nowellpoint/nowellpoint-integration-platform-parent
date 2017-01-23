package com.nowellpoint.api.service;

import com.nowellpoint.api.model.domain.ScheduledJobType;
import com.nowellpoint.api.model.domain.ScheduledJobTypeList;

public interface ScheduledJobTypeService {
	
	/**
	 * 
	 * 
	 * 
	 */
	
	ScheduledJobType findById(String id);
	
	/**
	 * 
	 * 
	 * 
	 */
	
	ScheduledJobTypeList findByLanguage(String languageSidKey);
	
	/**
	 * 
	 * 
	 * 
	 */
	
	void createScheduledJobType(ScheduledJobType scheduledJobType);
	
	/**
	 * 
	 * 
	 * 
	 */
	
	void updateScheduledJobType(String id, ScheduledJobType scheduledJobType);
	
	/**
	 * 
	 * 
	 * 
	 */
	
	void deleteScheduledJobType(String id);
}