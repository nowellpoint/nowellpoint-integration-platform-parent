package com.nowellpoint.api.rest.domain;

import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import com.nowellpoint.api.rest.JobResource;
import com.nowellpoint.api.rest.SalesforceConnectorResource;

public class Resources {
	
	private String salesforce;
	
	private String jobs;
	
	private Resources(UriInfo uriInfo) {
		this.salesforce = UriBuilder.fromUri(uriInfo.getBaseUri()).path(SalesforceConnectorResource.class).build().toString();
		this.jobs = UriBuilder.fromUri(uriInfo.getBaseUri()).path(JobResource.class).build().toString();
	}
	
	public static Resources of(UriInfo uriInfo) {
		return new Resources(uriInfo);
	}

	public String getSalesforce() {
		return salesforce;
	}

	public String getJobs() {
		return jobs;
	}
}