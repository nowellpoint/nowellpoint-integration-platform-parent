package com.nowellpoint.api.service;

import java.io.IOException;

import javax.enterprise.event.Observes;

import com.nowellpoint.annotation.Deactivate;
import com.nowellpoint.api.rest.domain.AccountProfile;
import com.nowellpoint.api.rest.domain.RunHistory;
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
	
	public JobSpecificationList findScheduled();
	
	/**
	 * 
	 * 
	 * 
	 */
	
	public JobSpecification createJobSchedule(
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
	
	public JobSpecification updateScheduledJob(
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
	
	public JobSpecification terminateScheduledJob(String id);
	
	/**
	 * 
	 * 
	 * 
	 */
	
	public JobSpecification startScheduledJob(String id);
	
	/**
	 * 
	 * 
	 * 
	 */
	
	public JobSpecification stopScheduledJob(String id);
	
	/**
	 * 
	 * 
	 * 
	 */
	
	public void deleteScheduledJob(String id);
	
	/**
	 * 
	 * 
	 * 
	 */

	public JobSpecification findById(String id);
	
	/**
	 * 
	 * 
	 * 
	 */
	
	public RunHistory findRunHistory(String id, String fireInstanceId);
	
	/**
	 * 
	 * 
	 * 
	 */
	
	public String getFile(String id, String fireInstanceId, String filename) throws IOException;
	
	/**
	 * 
	 * 
	 * 
	 */
	
	public void terminateAllJobs(@Observes @Deactivate AccountProfile accountProfile);
}