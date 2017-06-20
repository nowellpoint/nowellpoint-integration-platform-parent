package com.nowellpoint.api.rest.domain;

import java.text.DecimalFormat;
import java.time.Instant;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
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
	
	private String data;
	
	private Set<JobStatusAggregation> jobStatusSummary = new HashSet<>();
	
	private Set<JobExecution> recentJobExecutions = new HashSet<>();
	
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
	private Date lastRefreshedOn;
	
	private Dashboard() {
		
	}
	
	private Dashboard(SalesforceConnectorList salesforceConnectorList, JobList jobList) {
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
		return modelMapper.map(this, com.nowellpoint.api.model.document.AccountProfile.class);
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
	private static String formatFileSize(long size) {
	    String hrSize = null;

	    double b = size;
	    double k = size/1024.0;
	    double m = ((size/1024.0)/1024.0);
	    double g = (((size/1024.0)/1024.0)/1024.0);
	    double t = ((((size/1024.0)/1024.0)/1024.0)/1024.0);

	    DecimalFormat format = new DecimalFormat("0.00");

	    if ( t > 1 ) {
	        hrSize = format.format(t).concat(" TB");
	    } else if ( g > 1 ) {
	        hrSize = format.format(g).concat(" GB");
	    } else if ( m > 1 ) {
	        hrSize = format.format(m).concat(" MB");
	    } else if ( k > 1 ) {
	        hrSize = format.format(k).concat(" KB");
	    } else {
	        hrSize = format.format(b).concat(" Bytes");
	    }

	    return hrSize;
	}
}