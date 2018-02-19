package com.nowellpoint.api.service;

import java.io.IOException;

import com.nowellpoint.api.rest.domain.CreateJobRequest;
import com.nowellpoint.api.rest.domain.JobOrig;
import com.nowellpoint.api.rest.domain.JobExecution;
import com.nowellpoint.api.rest.domain.JobList;
import com.nowellpoint.api.rest.domain.UpdateJobRequest;

public interface JobService {
	
	public JobList findAllByOwner(String ownerId);
	
	public JobList findAllScheduled();
	
	public JobOrig findById(String id);
	
	public JobList queryBySource(String sourceId);
	
	public JobOrig createJob(CreateJobRequest jobRequest);
	
	public JobOrig updateJob(UpdateJobRequest jobRequest);
	
	public void updateJob(JobOrig jobOrig);
	
	public void runJob(JobOrig jobOrig);
	
	public void submitJob(JobOrig jobOrig);
	
	public void stopJob(JobOrig jobOrig);
	
	public void terminateJob(JobOrig jobOrig);
	
	public JobExecution findByFireInstanceId(String id, String fireInstanceId);
	
	public String getOutputFile(String id, String filename) throws IOException;
	
	public void loadScheduledJobs();
	
	public void sendSlackTestMessage(JobOrig jobOrig);
}