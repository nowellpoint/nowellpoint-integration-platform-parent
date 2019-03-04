package com.nowellpoint.console.model;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable
@Value.Modifiable
@Value.Style(typeImmutable = "*", jdkOnly = true, create = "new")
@JsonSerialize(as = Limits.class)
@JsonDeserialize(as = Limits.class)
public abstract class AbstractLimits {
	public abstract @Nullable Limit getConcurrentAsyncGetReportInstances();
	public abstract @Nullable Limit getConcurrentSyncReportRuns();
	public abstract @Nullable Limit getDailyAnalyticsDataflowJobExecutions();
	public abstract @Nullable Limit getDailyAsyncApexExecutions();
	public abstract @Nullable Limit getDailyDurableGenericStreamingApiEvents();
	public abstract @Nullable Limit getDailyDurableStreamingApiEvents();
	public abstract @Nullable Limit dailyWorkflowEmails();
	public abstract @Nullable Limit dataStorageMB();
	public abstract @Nullable Limit durableStreamingApiConcurrentClients();
	public abstract @Nullable Limit fileStorageMB();
	public abstract @Nullable Limit hourlyAsyncReportRuns();
	public abstract @Nullable Limit hourlyDashboardRefreshes();
	public abstract @Nullable Limit hourlyDashboardResults();
	public abstract @Nullable Limit hourlyDashboardStatuses();
	public abstract @Nullable Limit hourlyODataCallout();
	public abstract @Nullable Limit hourlySyncReportRuns();
	public abstract @Nullable Limit hourlyTimeBasedWorkflow();
	public abstract @Nullable Limit massEmail();
	public abstract @Nullable Limit monthlyPlatformEvents();
	public abstract @Nullable Limit package2VersionCreates();
	public abstract @Nullable Limit singleEmail();
	public abstract @Nullable Limit streamingApiConcurrentClients();
	
	public static Limits of(com.nowellpoint.client.sforce.model.Limits source) {
		return source == null ? Limits.builder().build() : Limits.builder()
				.concurrentAsyncGetReportInstances(Limit.of(source.getConcurrentAsyncGetReportInstances()))
				.concurrentSyncReportRuns(Limit.of(source.getConcurrentSyncReportRuns()))
				.dailyAnalyticsDataflowJobExecutions(Limit.of(source.getDailyAnalyticsDataflowJobExecutions()))
				.dailyAsyncApexExecutions(Limit.of(source.getDailyAsyncApexExecutions()))
				.dailyDurableGenericStreamingApiEvents(Limit.of(source.getDailyDurableGenericStreamingApiEvents()))
				.dailyDurableStreamingApiEvents(Limit.of(source.getDailyDurableStreamingApiEvents()))
				.build();
	}
	
	public static Limits of(com.nowellpoint.console.entity.Limits source) {
		return source == null ? Limits.builder().build() : Limits.builder()
				.concurrentAsyncGetReportInstances(Limit.of(source.getConcurrentAsyncGetReportInstances()))
				.concurrentSyncReportRuns(Limit.of(source.getConcurrentSyncReportRuns()))
				.dailyAnalyticsDataflowJobExecutions(Limit.of(source.getDailyAnalyticsDataflowJobExecutions()))
				.dailyAsyncApexExecutions(Limit.of(source.getDailyAsyncApexExecutions()))
				.dailyDurableGenericStreamingApiEvents(Limit.of(source.getDailyDurableGenericStreamingApiEvents()))
				.dailyDurableStreamingApiEvents(Limit.of(source.getDailyDurableStreamingApiEvents()))
				.build();
	}
}