package com.nowellpoint.api.service;

import java.io.IOException;

import com.nowellpoint.api.rest.domain.Job;
import com.nowellpoint.api.rest.domain.JobExecution;
import com.nowellpoint.api.rest.domain.JobList;

public interface JobService {
	
	JobList findAllByOwner(String ownerId);
	
	JobList findAllScheduled();
	
	Job findById(String id);
	
	void createJob(Job job);
	
	void updateJob(Job job);
	
	void runJob(Job job);
	
	JobExecution findByFireInstanceId(String id, String fireInstanceId);
	
	String getOutputFile(String id, String filename) throws IOException;
}