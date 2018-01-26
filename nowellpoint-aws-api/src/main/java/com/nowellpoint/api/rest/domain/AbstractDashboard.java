package com.nowellpoint.api.rest.domain;

import static com.nowellpoint.util.NumberFormatter.formatFileSize;

import java.time.Instant;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.nowellpoint.api.rest.DashboardResource;
import com.nowellpoint.mongodb.document.MongoDocument;

@Value.Immutable
@Value.Modifiable
@Value.Style(typeImmutable = "*", jdkOnly=true, depluralize = true, depluralizeDictionary = {"feature:features"})
@JsonSerialize(as = Dashboard.class)
@JsonDeserialize(as = Dashboard.class)
public abstract class AbstractDashboard extends AbstractImmutableResource {
	public abstract Integer getConnectors();
	public abstract Integer getJobs();
	public abstract String getData();
	public abstract Set<JobStatusAggregation> getJobStatusSummary();
	public abstract Set<JobExecution> getRecentJobExecutions();
	public abstract @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'") Date getLastRefreshedOn();
	
	public static Dashboard of(ConnectorList connectorList, JobList jobList) {
		
		Set<JobStatusAggregation> jobStatusSummary = Collections.emptySet();
		Set<JobExecution> recentJobExecutions = Collections.emptySet();
		String data = "0 Bytes";
		
		if (jobList.getSize() > 0) {
			
			jobStatusSummary = jobList.getItems()
					.stream()
					.collect(Collectors.groupingBy(
							JobOrig::getStatus,
							Collectors.counting()))
					.entrySet()
					.stream()
					.sorted(Comparator.comparing(e -> e.getKey()))
					.map(e -> JobStatusAggregation.of(e.getKey(), e.getValue()))
					.collect(Collectors.toSet());
			
			recentJobExecutions = jobList.getItems()
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
			
			data = formatFileSize(space);
			
		}
		
		Dashboard dashboard = ModifiableDashboard.create()
				.setConnectors(connectorList.getSize())
				.setJobs(jobList.getSize())
				.setLastRefreshedOn(Date.from(Instant.now()))
				.setData(data)
				.setJobStatusSummary(jobStatusSummary)
				.setRecentJobExecutions(recentJobExecutions)
				.setCreatedOn(Date.from(Instant.now()))
				.setLastUpdatedOn(Date.from(Instant.now()))
				.toImmutable();
		
		return dashboard;
	}
	
	@Value.Derived
	public Meta getMeta() {
		return Meta.builder()
				.id(getId())
				.resourceClass(DashboardResource.class)
				.build();
	}
	
	@Override
	public void fromDocument(MongoDocument document) {
		modelMapper.map(document, this);
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(Dashboard.class);
	}
	
	@Override
	public MongoDocument toDocument() {
		return modelMapper.map(this, com.nowellpoint.api.model.document.Dashboard.class);
	}
}