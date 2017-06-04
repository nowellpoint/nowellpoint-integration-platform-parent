package com.nowellpoint.api.service;

import java.io.IOException;

import com.nowellpoint.api.rest.domain.CreateJobRequest;
import com.nowellpoint.api.rest.domain.Job;
import com.nowellpoint.api.rest.domain.JobExecution;
import com.nowellpoint.api.rest.domain.JobList;

public interface JobService {
	
	public JobList findAllByOwner(String ownerId);
	
	public JobList findAllScheduled();
	
	public Job findById(String id);
	
	public JobList queryBySource(String sourceId);
	
	public Job createJob(CreateJobRequest createJobRequest);
	
	public void updateJob(Job job);
	
	public void runJob(Job job);
	
	public JobExecution findByFireInstanceId(String id, String fireInstanceId);
	
	public String getOutputFile(String id, String filename) throws IOException;
}