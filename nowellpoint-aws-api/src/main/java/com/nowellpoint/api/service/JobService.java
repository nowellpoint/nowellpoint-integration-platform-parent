package com.nowellpoint.api.service;

import java.io.IOException;

import com.nowellpoint.api.rest.domain.CreateJobRequest;
import com.nowellpoint.api.rest.domain.Job;
import com.nowellpoint.api.rest.domain.JobExecution;
import com.nowellpoint.api.rest.domain.JobList;
import com.nowellpoint.api.rest.domain.UpdateJobRequest;

public interface JobService {
	
	public JobList findAllByOwner(String ownerId);
	
	public JobList findAllScheduled();
	
	public Job findById(String id);
	
	public JobList queryBySource(String sourceId);
	
	public Job createJob(CreateJobRequest jobRequest);
	
	public Job updateJob(UpdateJobRequest jobRequest);
	
	public void updateJob(Job job);
	
	public void submitJob(Job job);
	
	public void stopJob(Job job);
	
	public void terminateJob(Job job);
	
	public JobExecution findByFireInstanceId(String id, String fireInstanceId);
	
	public String getOutputFile(String id, String filename) throws IOException;
	
	public void loadScheduledJobs();
}