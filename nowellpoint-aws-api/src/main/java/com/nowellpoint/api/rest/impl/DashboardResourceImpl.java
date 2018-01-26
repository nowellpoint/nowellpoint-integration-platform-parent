package com.nowellpoint.api.rest.impl;

import javax.inject.Inject;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import com.nowellpoint.api.rest.DashboardResource;
import com.nowellpoint.api.rest.domain.Dashboard;
import com.nowellpoint.api.rest.domain.JobList;
import com.nowellpoint.api.rest.domain.ConnectorList;
import com.nowellpoint.api.service.JobService;
import com.nowellpoint.api.service.ConnectorService;

public class DashboardResourceImpl implements DashboardResource {
	
	@Context
	private SecurityContext securityContext;
	
	@Inject
	private ConnectorService connectorService;
	
	@Inject
	private JobService jobService;

	@Override
	public Response getDashboard() {
		String owner = securityContext.getUserPrincipal().getName();
		
		ConnectorList connectorList = connectorService.getConnectors();
		JobList jobList = jobService.findAllByOwner(owner);
		
		Dashboard dashboard = Dashboard.of(connectorList, jobList);
		
		return Response.ok(dashboard).build();
	}
}