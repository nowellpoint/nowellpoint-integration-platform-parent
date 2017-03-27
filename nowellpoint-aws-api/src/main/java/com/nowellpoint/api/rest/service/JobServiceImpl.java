package com.nowellpoint.api.rest.service;

import com.nowellpoint.api.rest.domain.Job;
import com.nowellpoint.api.rest.domain.JobList;
import com.nowellpoint.api.service.JobService;

public class JobServiceImpl extends AbstractJobService implements JobService {

	@Override
	public JobList findAllByOwner(String ownerId) {
		return super.findAllByOwner(ownerId);
	}

	@Override
	public Job findById(String id) {
		return super.findById(id);
	}

	@Override
	public void createJob(Job job) {
		job.setStatus(Job.SCHEDULED);
		super.create(job);
	}

	@Override
	public void updateJob(Job job) {
		super.update(job);
	}

}
