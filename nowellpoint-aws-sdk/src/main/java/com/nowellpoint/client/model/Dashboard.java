package com.nowellpoint.client.model;

import java.util.Date;
import java.util.List;

public class Dashboard extends AbstractResource {
	
	private Integer connectors;
	
	private Integer jobs;
	
	private List<JobStatusAggregation> jobStatusSummary;
	
	private List<JobExecution> recentJobExecutions;
	
	private Date lastRefreshedOn;
	
	private Dashboard() {
		
	}

	public Integer getConnectors() {
		return connectors;
	}

	public void setConnectors(Integer connectors) {
		this.connectors = connectors;
	}

	public Integer getJobs() {
		return jobs;
	}

	public void setJobs(Integer jobs) {
		this.jobs = jobs;
	}

	public List<JobStatusAggregation> getJobStatusSummary() {
		return jobStatusSummary;
	}

	public void setJobStatusSummary(List<JobStatusAggregation> jobStatusSummary) {
		this.jobStatusSummary = jobStatusSummary;
	}

	public List<JobExecution> getRecentJobExecutions() {
		return recentJobExecutions;
	}

	public void setRecentJobExecutions(List<JobExecution> recentJobExecutions) {
		this.recentJobExecutions = recentJobExecutions;
	}

	public Date getLastRefreshedOn() {
		return lastRefreshedOn;
	}

	public void setLastRefreshedOn(Date lastRefreshedOn) {
		this.lastRefreshedOn = lastRefreshedOn;
	}
}