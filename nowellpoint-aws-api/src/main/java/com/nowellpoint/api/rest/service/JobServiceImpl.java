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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Job createJob() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Job updateJob() {
		// TODO Auto-generated method stub
		return null;
	}

}
