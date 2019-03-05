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
	
	@Value.Default
	public Limit getConcurrentAsyncGetReportInstances() {
		return Limit.builder().build();
	}
	
	@Value.Default
	public Limit getConcurrentSyncReportRuns() {
		return Limit.builder().build();
	}
	
	@Value.Default
	public Limit getDailyAnalyticsDataflowJobExecutions() {
		return Limit.builder().build();
	}
	
	@Value.Default
	public Limit getDailyAsyncApexExecutions() {
		return Limit.builder().build();
	}
	
	@Value.Default
	public Limit getDailyDurableGenericStreamingApiEvents() {
		return Limit.builder().build();
	}
	
	@Value.Default
	public Limit getDailyDurableStreamingApiEvents() {
		return Limit.builder().build();
	}
	
	@Value.Default
	public Limit getDailyWorkflowEmails() {
		return Limit.builder().build();
	}
	
	@Value.Default
	public Limit getDataStorageMB() {
		return Limit.builder().build();
	}
	
	@Value.Default
	public Limit getDurableStreamingApiConcurrentClients() {
		return Limit.builder().build();
	}
	
	@Value.Default
	public Limit getFileStorageMB() {
		return Limit.builder().build();
	}
	
	@Value.Default
	public Limit getHourlyAsyncReportRuns() {
		return Limit.builder().build();
	}
	
	@Value.Default
	public Limit getHourlyDashboardRefreshes() {
		return Limit.builder().build();
	}
	
	@Value.Default
	public Limit getHourlyDashboardResults() {
		return Limit.builder().build();
	}
	
	@Value.Default
	public Limit getHourlyDashboardStatuses() {
		return Limit.builder().build();
	}
	
	@Value.Default
	public Limit getHourlyODataCallout() {
		return Limit.builder().build();
	}
	
	@Value.Default
	public Limit getHourlySyncReportRuns() {
		return Limit.builder().build();
	}
	
	@Value.Default
	public Limit getHourlyTimeBasedWorkflow() {
		return Limit.builder().build();
	}
	
	@Value.Default
	public Limit getMassEmail() {
		return Limit.builder().build();
	}
	
	@Value.Default
	public Limit getMonthlyPlatformEvents() {
		return Limit.builder().build();
	}
	
	@Value.Default
	public Limit getPackage2VersionCreates() {
		return Limit.builder().build();
	}
	
	@Value.Default
	public Limit getSingleEmail() {
		return Limit.builder().build();
	}
	
	@Value.Default
	public Limit getStreamingApiConcurrentClients() {
		return Limit.builder().build();
	}
	
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