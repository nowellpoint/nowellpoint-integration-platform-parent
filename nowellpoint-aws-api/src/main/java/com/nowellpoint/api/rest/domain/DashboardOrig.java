package com.nowellpoint.api.rest.domain;

import static com.nowellpoint.util.NumberFormatter.formatFileSize;

import java.time.Instant;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nowellpoint.mongodb.document.MongoDocument;

public class DashboardOrig extends AbstractResource {

	private AbstractUserInfo createdBy;
	
	private AbstractUserInfo lastUpdatedBy;

	private Integer connectors;
	
	private Integer jobs;
	
	private String data;
	
	private Set<JobStatusAggregation> jobStatusSummary = new HashSet<>();
	
	private Set<JobExecution> recentJobExecutions = new HashSet<>();
	
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
	private Date lastRefreshedOn;
	
	private DashboardOrig() {
		
	}
	
	private DashboardOrig(SalesforceConnectorList salesforceConnectorList, JobList jobList) {
		this.connectors = salesforceConnectorList.getSize();
		this.jobs = jobList.getSize();
		this.data = "0 Bytes";
		
		if (jobList.getSize() > 0) {
			
			this.jobStatusSummary = jobList.getItems()
					.stream()
					.collect(Collectors.groupingBy(
							Job::getStatus,
							Collectors.counting()))
					.entrySet()
					.stream()
					.sorted(Comparator.comparing(e -> e.getKey()))
					.map(e -> JobStatusAggregation.of(e.getKey(), e.getValue()))
					.collect(Collectors.toSet());
			
			this.recentJobExecutions = jobList.getItems()
					.stream()
					.map(e -> e.getJobExecutions())
					.flatMap(Set::stream)
					.collect(Collectors.toSet())
					.stream()
					.sorted( (e1,e2) -> e2.getFireTime().compareTo(e1.getFireTime()))
				    .limit(10)
				    .collect(Collectors.toSet());	
			
			long space = jobList.getItems()
					.stream()
					.map(e -> e.getJobOutputs())
					.flatMap(Set::stream)
					.collect(Collectors.toSet())
					.stream()
					.mapToLong(o -> o.getFilesize())
					.sum();
			
			this.data = formatFileSize(space);
		}
		
		this.lastRefreshedOn = Date.from(Instant.now());
	}
	
	private DashboardOrig(String id) {
		setId(id);
	}
	
	private <T> DashboardOrig(T document) {
		modelMapper.map(document, this);
	}
	
	public static DashboardOrig of(String id) {
		return new DashboardOrig(id);
	}
	
	public static DashboardOrig of(MongoDocument document) {
		return new DashboardOrig(document);
	}
	
	public static DashboardOrig of(SalesforceConnectorList salesforceConnectorList, JobList jobList) {
		return new DashboardOrig(salesforceConnectorList, jobList);
	}

	public AbstractUserInfo getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(AbstractUserInfo createdBy) {
		this.createdBy = createdBy;
	}

	public AbstractUserInfo getLastUpdatedBy() {
		return lastUpdatedBy;
	}

	public void setLastUpdatedBy(AbstractUserInfo lastUpdatedBy) {
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

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public Date getLastRefreshedOn() {
		return lastRefreshedOn;
	}

	public void setLastRefreshedOn(Date lastRefreshedOn) {
		this.lastRefreshedOn = lastRefreshedOn;
	}

	@Override
	public MongoDocument toDocument() {
		return modelMapper.map(this, com.nowellpoint.api.model.document.Dashboard.class);
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}