package com.nowellpoint.console.model;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable
@Value.Modifiable
@Value.Style(typeImmutable = "*", jdkOnly = true, create = "new")
@JsonSerialize(as = Limits.class)
@JsonDeserialize(as = Limits.class)
public abstract class AbstractLimits {
	public abstract Limit getConcurrentAsyncGetReportInstances();
	public abstract Limit getConcurrentSyncReportRuns();
	public abstract Limit getDailyAnalyticsDataflowJobExecutions();
	public abstract Limit getDailyAsyncApexExecutions();
	public abstract Limit getDailyDurableGenericStreamingApiEvents();
	public abstract Limit getDailyDurableStreamingApiEvents();
	public abstract Limit getDailyWorkflowEmails();
	public abstract Limit getDataStorageMB();
	public abstract Limit getDurableStreamingApiConcurrentClients();
	public abstract Limit getFileStorageMB();
	public abstract Limit getHourlyAsyncReportRuns();
	public abstract Limit getHourlyDashboardRefreshes();
	public abstract Limit getHourlyDashboardResults();
	public abstract Limit getHourlyDashboardStatuses();
	public abstract Limit getHourlyODataCallout();
	public abstract Limit getHourlySyncReportRuns();
	public abstract Limit getHourlyTimeBasedWorkflow();
	public abstract Limit getMassEmail();
	public abstract Limit getMonthlyPlatformEvents();
	public abstract Limit getPackage2VersionCreates();
	public abstract Limit getSingleEmail();
	public abstract Limit getStreamingApiConcurrentClients();
	
	public static Limits of(com.nowellpoint.client.sforce.model.Limits source) {
		return source == null ? Limits.builder().build() : Limits.builder()
				.concurrentAsyncGetReportInstances(Limit.of(source.getConcurrentAsyncGetReportInstances()))
				.concurrentSyncReportRuns(Limit.of(source.getConcurrentSyncReportRuns()))
				.dailyAnalyticsDataflowJobExecutions(Limit.of(source.getDailyAnalyticsDataflowJobExecutions()))
				.dailyAsyncApexExecutions(Limit.of(source.getDailyAsyncApexExecutions()))
				.dailyDurableGenericStreamingApiEvents(Limit.of(source.getDailyDurableGenericStreamingApiEvents()))
				.dailyDurableStreamingApiEvents(Limit.of(source.getDailyDurableStreamingApiEvents()))
				.dailyWorkflowEmails(Limit.of(source.getDailyWorkflowEmails()))
				.dataStorageMB(Limit.of(source.getDataStorageMB()))
				.durableStreamingApiConcurrentClients(Limit.of(source.getDurableStreamingApiConcurrentClients()))
				.fileStorageMB(Limit.of(source.getFileStorageMB()))
				.hourlyAsyncReportRuns(Limit.of(source.getHourlyAsyncReportRuns()))
				.hourlyDashboardRefreshes(Limit.of(source.getHourlyDashboardRefreshes()))
				.hourlyDashboardResults(Limit.of(source.getHourlyDashboardResults()))
				.hourlyDashboardStatuses(Limit.of(source.getHourlyDashboardStatuses()))
				.hourlyODataCallout(Limit.of(source.getHourlyODataCallout()))
				.hourlySyncReportRuns(Limit.of(source.getHourlySyncReportRuns()))
				.hourlyTimeBasedWorkflow(Limit.of(source.getHourlyTimeBasedWorkflow()))
				.massEmail(Limit.of(source.getMassEmail()))
				.monthlyPlatformEvents(Limit.of(source.getMonthlyPlatformEvents()))
				.package2VersionCreates(Limit.of(source.getPackage2VersionCreates()))
				.singleEmail(Limit.of(source.getSingleEmail()))
				.streamingApiConcurrentClients(Limit.of(source.getStreamingApiConcurrentClients()))
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
				.dailyWorkflowEmails(Limit.of(source.getDailyWorkflowEmails()))
				.dataStorageMB(Limit.of(source.getDataStorageMB()))
				.durableStreamingApiConcurrentClients(Limit.of(source.getDurableStreamingApiConcurrentClients()))
				.fileStorageMB(Limit.of(source.getFileStorageMB()))
				.hourlyAsyncReportRuns(Limit.of(source.getHourlyAsyncReportRuns()))
				.hourlyDashboardRefreshes(Limit.of(source.getHourlyDashboardRefreshes()))
				.hourlyDashboardResults(Limit.of(source.getHourlyDashboardResults()))
				.hourlyDashboardStatuses(Limit.of(source.getHourlyDashboardStatuses()))
				.hourlyODataCallout(Limit.of(source.getHourlyODataCallout()))
				.hourlySyncReportRuns(Limit.of(source.getHourlySyncReportRuns()))
				.hourlyTimeBasedWorkflow(Limit.of(source.getHourlyTimeBasedWorkflow()))
				.massEmail(Limit.of(source.getMassEmail()))
				.monthlyPlatformEvents(Limit.of(source.getMonthlyPlatformEvents()))
				.package2VersionCreates(Limit.of(source.getPackage2VersionCreates()))
				.singleEmail(Limit.of(source.getSingleEmail()))
				.streamingApiConcurrentClients(Limit.of(source.getStreamingApiConcurrentClients()))
				.build();
	}
}