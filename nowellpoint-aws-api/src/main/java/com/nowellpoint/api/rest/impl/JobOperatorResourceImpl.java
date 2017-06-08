package com.nowellpoint.api.rest.impl;

import javax.inject.Inject;
import javax.ws.rs.core.Response;

import com.nowellpoint.api.rest.JobOperatorResource;
import com.nowellpoint.api.service.JobService;

public class JobOperatorResourceImpl implements JobOperatorResource {
	
	@Inject
	private JobService jobService;
	
	public Response invokeAction(String action) {
		jobService.loadScheduledJobs();
		return Response.ok().build();
	}
}