package com.nowellpoint.api.service;

import java.io.IOException;

import javax.enterprise.event.Observes;

import com.nowellpoint.api.rest.domain.AccountProfile;
import com.nowellpoint.api.rest.domain.Deactivate;
import com.nowellpoint.api.rest.domain.RunHistory;
import com.nowellpoint.api.rest.domain.ScheduledJob;
import com.nowellpoint.api.rest.domain.ScheduledJobList;

public interface ScheduledJobService {
	
	/**
	 * 
	 * 
	 * 
	 */
	
	public ScheduledJobList findByOwner(String ownerId);
	
	/**
	 * 
	 * 
	 * 
	 */
	
	public ScheduledJobList findScheduled();
	
	/**
	 * 
	 * 
	 * 
	 */
	
	public void createScheduledJob(ScheduledJob scheduledJob);
	
	/**
	 * 
	 * 
	 * 
	 */
	
	public void updateScheduledJob(String id, ScheduledJob scheduledJob);
	
	/**
	 * 
	 * 
	 * 
	 */
	
	public ScheduledJob terminateScheduledJob(String id);
	
	/**
	 * 
	 * 
	 * 
	 */
	
	public ScheduledJob startScheduledJob(String id);
	
	/**
	 * 
	 * 
	 * 
	 */
	
	public ScheduledJob stopScheduledJob(String id);
	
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

	public ScheduledJob findById(String id);
	
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