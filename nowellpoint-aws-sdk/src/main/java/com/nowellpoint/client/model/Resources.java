package com.nowellpoint.client.model;

public class Resources {
	
	private String connectors;
	
	private String jobs;
	
	private String organization;

	public Resources() {
		
	}

	public String getConnectors() {
		return connectors;
	}

	public String getJobs() {
		return jobs;
	}

	public void setJobs(String jobs) {
		this.jobs = jobs;
	}
	
	public String getOrganization() {
		return organization;
	}
	
	public void setOrganization(String organization) {
		this.organization = organization;
	}
}