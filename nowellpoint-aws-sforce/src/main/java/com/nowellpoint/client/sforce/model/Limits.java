package com.nowellpoint.client.sforce.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Limits {
	
	@JsonProperty("ConcurrentAsyncGetReportInstances")
	private Limit concurrentAsyncGetReportInstances;
	
	@JsonProperty("ConcurrentSyncReportRuns")
	private Limit concurrentSyncReportRuns;
	
	@JsonProperty("DailyAnalyticsDataflowJobExecutions")
	private Limit dailyAnalyticsDataflowJobExecutions;
	
	@JsonProperty("DailyAsyncApexExecutions")
	private Limit dailyAsyncApexExecutions;
	
	@JsonProperty("DailyDurableGenericStreamingApiEvents")
	private Limit dailyDurableGenericStreamingApiEvents;
	
	@JsonProperty("DailyDurableStreamingApiEvents")
	private Limit dailyDurableStreamingApiEvents;
	
	@JsonProperty("DailyApiRequests")
	private ApiLimits dailyApiRequests;
	
	@JsonProperty("DailyBulkApiRequests")
	private ApiLimits dailyBulkApiRequests;
	
	@JsonProperty("DailyGenericStreamingApiEvents")
	private ApiLimits dailyGenericStreamingApiEvents;
	
	@JsonProperty("DailyStreamingApiEvents")
	private ApiLimits dailyStreamingApiEvents;
	
	@JsonProperty("DailyWorkflowEmails")
	private Limit dailyWorkflowEmails;
	
	@JsonProperty("DataStorageMB")
	private Limit dataStorageMB;
	
	@JsonProperty("DurableStreamingApiConcurrentClients")
	private Limit durableStreamingApiConcurrentClients;
	
	@JsonProperty("FileStorageMB")
	private Limit fileStorageMB;
	
	@JsonProperty("HourlyAsyncReportRuns")
	private Limit hourlyAsyncReportRuns;
	
	@JsonProperty("HourlyDashboardRefreshes")
	private Limit hourlyDashboardRefreshes;
	
	@JsonProperty("HourlyDashboardResults")
	private Limit hourlyDashboardResults;
	
	@JsonProperty("HourlyDashboardStatuses")
	private Limit hourlyDashboardStatuses;
	
	@JsonProperty("HourlyODataCallout")
	private Limit hourlyODataCallout;
	
	@JsonProperty("HourlySyncReportRuns")
	private Limit hourlySyncReportRuns;
	
	@JsonProperty("HourlyTimeBasedWorkflow")
	private Limit hourlyTimeBasedWorkflow;
	
	@JsonProperty("MassEmail")
	private Limit massEmail;
	
	@JsonProperty("MonthlyPlatformEvents")
	private Limit monthlyPlatformEvents;
	
	@JsonProperty("Package2VersionCreates")
	private Limit package2VersionCreates;
	
	@JsonProperty("SingleEmail")
	private Limit singleEmail;
	
	@JsonProperty("StreamingApiConcurrentClients")
	private Limit streamingApiConcurrentClients;
	
	public Limits() {
		
	}

	public Limit getConcurrentAsyncGetReportInstances() {
		return concurrentAsyncGetReportInstances;
	}

	public Limit getConcurrentSyncReportRuns() {
		return concurrentSyncReportRuns;
	}

	public Limit getDailyAnalyticsDataflowJobExecutions() {
		return dailyAnalyticsDataflowJobExecutions;
	}

	public Limit getDailyAsyncApexExecutions() {
		return dailyAsyncApexExecutions;
	}

	public Limit getDailyDurableGenericStreamingApiEvents() {
		return dailyDurableGenericStreamingApiEvents;
	}

	public Limit getDailyDurableStreamingApiEvents() {
		return dailyDurableStreamingApiEvents;
	}

	public ApiLimits getDailyApiRequests() {
		return dailyApiRequests;
	}

	public ApiLimits getDailyBulkApiRequests() {
		return dailyBulkApiRequests;
	}

	public ApiLimits getDailyGenericStreamingApiEvents() {
		return dailyGenericStreamingApiEvents;
	}

	public ApiLimits getDailyStreamingApiEvents() {
		return dailyStreamingApiEvents;
	}

	public Limit getDailyWorkflowEmails() {
		return dailyWorkflowEmails;
	}

	public Limit getDataStorageMB() {
		return dataStorageMB;
	}

	public Limit getDurableStreamingApiConcurrentClients() {
		return durableStreamingApiConcurrentClients;
	}

	public Limit getFileStorageMB() {
		return fileStorageMB;
	}

	public Limit getHourlyAsyncReportRuns() {
		return hourlyAsyncReportRuns;
	}

	public Limit getHourlyDashboardRefreshes() {
		return hourlyDashboardRefreshes;
	}

	public Limit getHourlyDashboardResults() {
		return hourlyDashboardResults;
	}

	public Limit getHourlyDashboardStatuses() {
		return hourlyDashboardStatuses;
	}

	public Limit getHourlyODataCallout() {
		return hourlyODataCallout;
	}

	public Limit getHourlySyncReportRuns() {
		return hourlySyncReportRuns;
	}

	public Limit getHourlyTimeBasedWorkflow() {
		return hourlyTimeBasedWorkflow;
	}

	public Limit getMassEmail() {
		return massEmail;
	}

	public Limit getMonthlyPlatformEvents() {
		return monthlyPlatformEvents;
	}

	public Limit getPackage2VersionCreates() {
		return package2VersionCreates;
	}

	public Limit getSingleEmail() {
		return singleEmail;
	}

	public Limit getStreamingApiConcurrentClients() {
		return streamingApiConcurrentClients;
	}
}