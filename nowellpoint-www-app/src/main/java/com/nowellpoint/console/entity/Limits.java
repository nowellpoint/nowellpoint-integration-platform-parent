package com.nowellpoint.console.entity;

import java.io.Serializable;

public class Limits implements Serializable {
	private static final long serialVersionUID = 3293652666490236458L;
	private Limit concurrentAsyncGetReportInstances;
	private Limit concurrentSyncReportRuns;
	private Limit dailyAnalyticsDataflowJobExecutions;
	private Limit dailyAsyncApexExecutions;
	private Limit dailyDurableGenericStreamingApiEvents;
	private Limit dailyWorkflowEmails;
	private Limit dataStorageMB;
	private Limit durableStreamingApiConcurrentClients;
	private Limit fileStorageMB;
	private Limit hourlyAsyncReportRuns;
	private Limit hourlyDashboardRefreshes;
	private Limit hourlyDashboardResults;
	private Limit hourlyDashboardStatuses;
	private Limit hourlyODataCallout;
	private Limit hourlySyncReportRuns;
	private Limit hourlyTimeBasedWorkflow;
	private Limit massEmail;
	private Limit monthlyPlatformEvents;
	private Limit package2VersionCreates;
	private Limit singleEmail;
	private Limit streamingApiConcurrentClients; 
	private Limit dailyDurableStreamingApiEvents;
	private Limit dailyApiRequests;
	private Limit dailyBulkApiRequests;
	private Limit dailyGenericStreamingApiEvents;
	private Limit dailyStreamingApiEvents;
	
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

	public void setConcurrentAsyncGetReportInstances(Limit concurrentAsyncGetReportInstances) {
		this.concurrentAsyncGetReportInstances = concurrentAsyncGetReportInstances;
	}

	public void setConcurrentSyncReportRuns(Limit concurrentSyncReportRuns) {
		this.concurrentSyncReportRuns = concurrentSyncReportRuns;
	}

	public void setDailyAnalyticsDataflowJobExecutions(Limit dailyAnalyticsDataflowJobExecutions) {
		this.dailyAnalyticsDataflowJobExecutions = dailyAnalyticsDataflowJobExecutions;
	}

	public void setDailyAsyncApexExecutions(Limit dailyAsyncApexExecutions) {
		this.dailyAsyncApexExecutions = dailyAsyncApexExecutions;
	}

	public void setDailyDurableGenericStreamingApiEvents(Limit dailyDurableGenericStreamingApiEvents) {
		this.dailyDurableGenericStreamingApiEvents = dailyDurableGenericStreamingApiEvents;
	}

	public void setDailyDurableStreamingApiEvents(Limit dailyDurableStreamingApiEvents) {
		this.dailyDurableStreamingApiEvents = dailyDurableStreamingApiEvents;
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

	public void setDailyWorkflowEmails(Limit dailyWorkflowEmails) {
		this.dailyWorkflowEmails = dailyWorkflowEmails;
	}

	public void setDataStorageMB(Limit dataStorageMB) {
		this.dataStorageMB = dataStorageMB;
	}

	public void setDurableStreamingApiConcurrentClients(Limit durableStreamingApiConcurrentClients) {
		this.durableStreamingApiConcurrentClients = durableStreamingApiConcurrentClients;
	}

	public void setFileStorageMB(Limit fileStorageMB) {
		this.fileStorageMB = fileStorageMB;
	}

	public void setHourlyAsyncReportRuns(Limit hourlyAsyncReportRuns) {
		this.hourlyAsyncReportRuns = hourlyAsyncReportRuns;
	}

	public void setHourlyDashboardRefreshes(Limit hourlyDashboardRefreshes) {
		this.hourlyDashboardRefreshes = hourlyDashboardRefreshes;
	}

	public void setHourlyDashboardResults(Limit hourlyDashboardResults) {
		this.hourlyDashboardResults = hourlyDashboardResults;
	}

	public void setHourlyDashboardStatuses(Limit hourlyDashboardStatuses) {
		this.hourlyDashboardStatuses = hourlyDashboardStatuses;
	}

	public void setHourlyODataCallout(Limit hourlyODataCallout) {
		this.hourlyODataCallout = hourlyODataCallout;
	}

	public void setHourlySyncReportRuns(Limit hourlySyncReportRuns) {
		this.hourlySyncReportRuns = hourlySyncReportRuns;
	}

	public void setHourlyTimeBasedWorkflow(Limit hourlyTimeBasedWorkflow) {
		this.hourlyTimeBasedWorkflow = hourlyTimeBasedWorkflow;
	}

	public void setMassEmail(Limit massEmail) {
		this.massEmail = massEmail;
	}

	public void setMonthlyPlatformEvents(Limit monthlyPlatformEvents) {
		this.monthlyPlatformEvents = monthlyPlatformEvents;
	}

	public void setPackage2VersionCreates(Limit package2VersionCreates) {
		this.package2VersionCreates = package2VersionCreates;
	}

	public void setSingleEmail(Limit singleEmail) {
		this.singleEmail = singleEmail;
	}

	public void setStreamingApiConcurrentClients(Limit streamingApiConcurrentClients) {
		this.streamingApiConcurrentClients = streamingApiConcurrentClients;
	}

	public Limit getDailyApiRequests() {
		return dailyApiRequests;
	}

	public Limit getDailyBulkApiRequests() {
		return dailyBulkApiRequests;
	}

	public Limit getDailyGenericStreamingApiEvents() {
		return dailyGenericStreamingApiEvents;
	}

	public Limit getDailyStreamingApiEvents() {
		return dailyStreamingApiEvents;
	}

	public void setDailyApiRequests(Limit dailyApiRequests) {
		this.dailyApiRequests = dailyApiRequests;
	}

	public void setDailyBulkApiRequests(Limit dailyBulkApiRequests) {
		this.dailyBulkApiRequests = dailyBulkApiRequests;
	}

	public void setDailyGenericStreamingApiEvents(Limit dailyGenericStreamingApiEvents) {
		this.dailyGenericStreamingApiEvents = dailyGenericStreamingApiEvents;
	}

	public void setDailyStreamingApiEvents(Limit dailyStreamingApiEvents) {
		this.dailyStreamingApiEvents = dailyStreamingApiEvents;
	}
}