package com.nowellpoint.api.rest.domain;

import java.time.Instant;
import java.util.Comparator;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nowellpoint.mongodb.document.MongoDocument;

public class Dashboard extends AbstractResource {

	private UserInfo createdBy;
	
	private UserInfo lastUpdatedBy;

	private Integer connectors;
	
	private Integer jobs;
	
	private Set<JobStatusAggregation> jobStatusSummary;
	
	private Set<JobExecution> recentJobExecutions;
	
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
	private Date lastRefreshedOn;
	
	private Dashboard() {
		
	}
	
	private Dashboard(SalesforceConnectorList salesforceConnectorList, JobList jobList) {
		this.connectors = salesforceConnectorList.getSize();
		this.jobs = jobList.getSize();
		this.jobStatusSummary = jobList.getItems()
				.stream()
				.collect(Collectors.groupingBy(
						Job::getStatus,
						Collectors.counting()))
				.entrySet()
				.stream()
				.sorted(Comparator.comparing(e -> e.getKey()))
				.map(e -> JobStatusAggregation.of(e.getKey(), e.getValue())).collect(Collectors.toSet());
		
		this.recentJobExecutions = jobList.getItems().stream()
			     .map(result -> result.getJobExecutions())
			     .flatMap(Set::stream)
			     .collect(Collectors.toSet())
			     .stream()
			     .sorted( (e1,e2) -> e2.getFireTime().compareTo(e1.getFireTime()))
			     .limit(10)
			     .collect(Collectors.toSet());
		
		this.lastRefreshedOn = Date.from(Instant.now());
	}
	
	private Dashboard(String id) {
		setId(id);
	}
	
	private <T> Dashboard(T document) {
		modelMapper.map(document, this);
	}
	
	public static Dashboard of(String id) {
		return new Dashboard(id);
	}
	
	public static Dashboard of(MongoDocument document) {
		return new Dashboard(document);
	}
	
	public static Dashboard of(SalesforceConnectorList salesforceConnectorList, JobList jobList) {
		return new Dashboard(salesforceConnectorList, jobList);
	}

	public UserInfo getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(UserInfo createdBy) {
		this.createdBy = createdBy;
	}

	public UserInfo getLastUpdatedBy() {
		return lastUpdatedBy;
	}

	public void setLastUpdatedBy(UserInfo lastUpdatedBy) {
		this.lastUpdatedBy = lastUpdatedBy;
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

	public Set<JobStatusAggregation> getJobStatusSummary() {
		return jobStatusSummary;
	}

	public void setJobStatusSummary(Set<JobStatusAggregation> jobStatusSummary) {
		this.jobStatusSummary = jobStatusSummary;
	}

	public Set<JobExecution> getRecentJobExecutions() {
		return recentJobExecutions;
	}

	public void setRecentJobExecutions(Set<JobExecution> recentJobExecutions) {
		this.recentJobExecutions = recentJobExecutions;
	}

	public Date getLastRefreshedOn() {
		return lastRefreshedOn;
	}

	public void setLastRefreshedOn(Date lastRefreshedOn) {
		this.lastRefreshedOn = lastRefreshedOn;
	}

	@Override
	public MongoDocument toDocument() {
		return modelMapper.map(this, com.nowellpoint.api.model.document.AccountProfile.class);
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}