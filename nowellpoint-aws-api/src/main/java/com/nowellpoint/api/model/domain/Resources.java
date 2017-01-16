package com.nowellpoint.api.model.domain;

public class Resources {
	
	private String salesforce;
	
	private String scheduledJobs;
	
	public Resources() {
		
	}

	public String getSalesforce() {
		return salesforce;
	}

	public void setSalesforce(String salesforce) {
		this.salesforce = salesforce;
	}

	public String getScheduledJobs() {
		return scheduledJobs;
	}

	public void setScheduledJobs(String scheduledJobs) {
		this.scheduledJobs = scheduledJobs;
	}
}