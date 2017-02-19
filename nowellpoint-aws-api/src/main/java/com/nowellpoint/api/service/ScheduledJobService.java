package com.nowellpoint.api.service;

import java.io.IOException;

import javax.enterprise.event.Observes;

import com.nowellpoint.annotation.Deactivate;
import com.nowellpoint.api.rest.domain.AccountProfile;
import com.nowellpoint.api.rest.domain.RunHistory;
import com.nowellpoint.api.rest.domain.JobSchedule;
import com.nowellpoint.api.rest.domain.JobScheduleList;

public interface ScheduledJobService {
	
	/**
	 * 
	 * 
	 * 
	 */
	
	public JobScheduleList findByOwner(String ownerId);
	
	/**
	 * 
	 * 
	 * 
	 */
	
	public JobScheduleList findScheduled();
	
	/**
	 * 
	 * 
	 * 
	 */
	
	public JobSchedule createScheduledJob(String scheduledJobTypeId);
	
	/**
	 * 
	 * 
	 * 
	 */
	
	public void updateScheduledJob(String id, JobSchedule jobSchedule);
	
	/**
	 * 
	 * 
	 * 
	 */
	
	public JobSchedule terminateScheduledJob(String id);
	
	/**
	 * 
	 * 
	 * 
	 */
	
	public JobSchedule startScheduledJob(String id);
	
	/**
	 * 
	 * 
	 * 
	 */
	
	public JobSchedule stopScheduledJob(String id);
	
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

	public JobSchedule findById(String id);
	
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