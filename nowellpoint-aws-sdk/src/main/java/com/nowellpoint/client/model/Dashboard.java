package com.nowellpoint.client.model;

import java.util.Date;
import java.util.List;

public class Dashboard extends AbstractResource {
	
	private Integer connectors;
	
	private Integer jobs;
	
	private List<JobStatusAggregation> jobStatusSummary;
	
	private List<JobExecution> recentJobExecutions;
	
	private String data;
	
	private Date lastRefreshedOn;
	
	private Dashboard() {
		
	}

	public Integer getConnectors() {
		return connectors;
	}

	public Integer getJobs() {
		return jobs;
	}

	public List<JobStatusAggregation> getJobStatusSummary() {
		return jobStatusSummary;
	}

	public List<JobExecution> getRecentJobExecutions() {
		return recentJobExecutions;
	}

	public String getData() {
		return data;
	}

	public Date getLastRefreshedOn() {
		return lastRefreshedOn;
	}
}