package com.nowellpoint.console.entity;

import java.io.Serializable;

public class Limits implements Serializable {
	private static final long serialVersionUID = 3293652666490236458L;
	private Limit concurrentAsyncGetReportInstances;
	private Limit concurrentSyncReportRuns;
	private Limit dailyAnalyticsDataflowJobExecutions;
	private Limit dailyAsyncApexExecutions;
	private Limit dailyDurableGenericStreamingApiEvents;
	private Limit dailyDurableStreamingApiEvents;
	
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
}
