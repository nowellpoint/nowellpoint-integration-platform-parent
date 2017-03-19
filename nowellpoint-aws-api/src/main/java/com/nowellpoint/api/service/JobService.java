package com.nowellpoint.api.service;

import com.nowellpoint.api.rest.domain.Job;
import com.nowellpoint.api.rest.domain.JobList;

public interface JobService {
	
	JobList findAllByOwner(String ownerId);
	
	Job findById(String id);
	
	Job createJob();
	
	Job updateJob();
}
