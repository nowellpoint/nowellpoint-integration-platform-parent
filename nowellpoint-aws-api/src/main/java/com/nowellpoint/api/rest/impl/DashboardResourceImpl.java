package com.nowellpoint.api.rest.impl;

import javax.inject.Inject;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import com.nowellpoint.api.rest.DashboardResource;
import com.nowellpoint.api.rest.domain.DashboardOrig;
import com.nowellpoint.api.rest.domain.JobList;
import com.nowellpoint.api.rest.domain.SalesforceConnectorList;
import com.nowellpoint.api.service.JobService;
import com.nowellpoint.api.service.SalesforceConnectorService;

public class DashboardResourceImpl implements DashboardResource {
	
	@Context
	private SecurityContext securityContext;
	
	@Inject
	private SalesforceConnectorService salesforceConnectorService;
	
	@Inject
	private JobService jobService;

	@Override
	public Response getDashboard() {
		String owner = securityContext.getUserPrincipal().getName();
		
		SalesforceConnectorList salesforceConnectorList = salesforceConnectorService.findAllByOwner(owner);
		JobList jobList = jobService.findAllByOwner(owner);
		
		DashboardOrig dashboardOrig = DashboardOrig.of(salesforceConnectorList, jobList);
		
		return Response.ok(dashboardOrig).build();
	}
}