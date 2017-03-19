package com.nowellpoint.api.rest.impl;

import javax.inject.Inject;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

import com.nowellpoint.api.rest.JobResource;
import com.nowellpoint.api.rest.domain.JobList;
import com.nowellpoint.api.service.JobService;

public class JobResourceImpl implements JobResource {
	
	@Inject
	private JobService jobService;
	
	@Context
	private SecurityContext securityContext;
	
	@Context
	private UriInfo uriInfo;

	@Override
	public Response findAllByOwner() {
		JobList jobList = jobService.findAllByOwner(securityContext.getUserPrincipal().getName());
		return Response.ok(jobList).build();
	}

	@Override
	public Response getJob(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Response invokeAction(String id, String action) {
		// TODO Auto-generated method stub
		return null;
	}

}