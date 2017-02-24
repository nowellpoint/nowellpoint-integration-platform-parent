package com.nowellpoint.api.service;

import com.nowellpoint.api.rest.domain.JobSpecification;
import com.nowellpoint.api.rest.domain.JobSpecificationList;

public interface JobSpecificationService {
	
	/**
	 * 
	 * 
	 * 
	 */
	
	public JobSpecificationList findByOwner(String ownerId);
	
	/**
	 * 
	 * 
	 * 
	 */
	
	public JobSpecification createJobSpecification(
			String jobTypeId, 
			String connectorId, 
			String instanceKey, 
			String start, 
			String end,
			String timeZone,
			String seconds,
			String minutes,
			String hours,
			String dayOfMonth,
			String month,
			String dayOfWeek,
			String year,
			String notificationEmail,
			String description);
	
	/**
	 * 
	 * 
	 * 
	 */
	
	public JobSpecification updateJobSpecification(
			String id, 
			String start, 
			String end,
			String timeZone, 
			String seconds,
			String minutes,
			String hours,
			String dayOfMonth,
			String month,
			String dayOfWeek,
			String year,
			String notificationEmail,
			String description);
	
	/**
	 * 
	 * 
	 * 
	 */
	
	public void deleteJobSpecification(String id);
	
	/**
	 * 
	 * 
	 * 
	 */

	public JobSpecification findById(String id);
}