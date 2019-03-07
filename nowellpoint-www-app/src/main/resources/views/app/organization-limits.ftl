<div class="container-fluid pr-5 pl-5 mb-3">
    <div class="card w-100">
        <div class="card-body">
            <h5 class="card-title">${labels['limits']}</h5>
            <div class="row">
                <div class="col-4">
                    <span></span>
                </div>
                <div class="col-2 text-right">
                    ${labels['used']}
                </div>
                <div class="col-2 text-right">
                    ${labels['available']}
                </div>
                <div class="col-2 text-right">
                    ${labels['max']}
                </div>
                <div class="col-2 text-right">
                    ${labels['percent']}
                </div>
            </div>
            <hr>
            <div class="row">
                <div class="col-4">
                    <span class="text-muted">${labels['daily.analytics.dataflow.job.executions']}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.limits.dailyAnalyticsDataflowJobExecutions.used}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.limits.dailyAnalyticsDataflowJobExecutions.available}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.limits.dailyAnalyticsDataflowJobExecutions.max}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.limits.dailyAnalyticsDataflowJobExecutions.percentAvailable}&#37;</span>
                </div>
            </div>
            <hr>
            <div class="row">
                <div class="col-4">
                    <span class="text-muted">${labels['daily.api.requests']}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.limits.dailyApiRequests.used}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.limits.dailyApiRequests.available}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.limits.dailyApiRequests.max}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.limits.dailyApiRequests.percentAvailable}&#37;</span>
                </div>
            </div>
            <hr>
            <div class="row">
                <div class="col-4">
                    <span class="text-muted">${labels['daily.async.apex.executions']}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.limits.dailyAsyncApexExecutions.used}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.limits.dailyAsyncApexExecutions.available}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.limits.dailyAsyncApexExecutions.max}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.limits.dailyAsyncApexExecutions.percentAvailable}&#37;</span>
                </div>                
            </div>
            <hr>
            <div class="row">
                <div class="col-4">
                    <span class="text-muted">${labels['daily.bulk.api.requests']}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.limits.dailyBulkApiRequests.used}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.limits.dailyBulkApiRequests.available}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.limits.dailyBulkApiRequests.max}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.limits.dailyBulkApiRequests.percentAvailable}&#37;</span>
                </div>                
            </div>
            <hr>
            <div class="row">
                <div class="col-4">
                    <span class="text-muted">${labels['daily.generic.streaming.api.events']}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.limits.dailyGenericStreamingApiEvents.used}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.limits.dailyGenericStreamingApiEvents.available}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.limits.dailyGenericStreamingApiEvents.max}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.limits.dailyGenericStreamingApiEvents.percentAvailable}&#37;</span>
                </div>            
            </div>
            <hr>
            <div class="row">
                <div class="col-4">
                    <span class="text-muted">${labels['daily.streaming.api.events']}</span>
                </div>
                
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.limits.dailyStreamingApiEvents.used}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.limits.dailyStreamingApiEvents.available}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.limits.dailyStreamingApiEvents.max}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.limits.dailyStreamingApiEvents.percentAvailable}&#37;</span>
                </div>
            </div>
            <hr>
            <div class="row">
                <div class="col-4">
                    <span class="text-muted">${labels['concurrent.async.get.report.instances']}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.limits.concurrentAsyncGetReportInstances.used}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.limits.concurrentAsyncGetReportInstances.available}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.limits.concurrentAsyncGetReportInstances.max}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.limits.concurrentAsyncGetReportInstances.percentAvailable}&#37;</span>
                </div>
            </div>
            <hr>
            <div class="row">
                <div class="col-4">
                    <span class="text-muted">${labels['concurrent.sync.report.runs']}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.limits.concurrentSyncReportRuns.used}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.limits.concurrentSyncReportRuns.available}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.limits.concurrentSyncReportRuns.max}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.limits.concurrentSyncReportRuns.percentAvailable}&#37;</span>
                </div>
            </div>

            <hr>
            <div class="row">
                <div class="col-4">
                    <span class="text-muted">${labels['daily.durable.generic.streaming.api.events']}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.limits.dailyDurableGenericStreamingApiEvents.used}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.limits.dailyDurableGenericStreamingApiEvents.available}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.limits.dailyDurableGenericStreamingApiEvents.max}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.limits.dailyDurableGenericStreamingApiEvents.percentAvailable}&#37;</span>
                </div>
            </div>
            <hr>
            <div class="row">
                <div class="col-4">
                    <span class="text-muted">${labels['daily.durable.streaming.api.events']}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.limits.dailyDurableStreamingApiEvents.used}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.limits.dailyDurableStreamingApiEvents.available}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.limits.dailyDurableStreamingApiEvents.max}</span>
                </div>
                                <div class="col-2 text-right">
                    <span class="text-muted">${organization.limits.dailyDurableStreamingApiEvents.percentAvailable}&#37;</span>
                </div>
            </div>
            <hr>
            <div class="row">
                <div class="col-4">
                    <span class="text-muted">${labels['daily.workflow.emails']}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.limits.dailyWorkflowEmails.used}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.limits.dailyWorkflowEmails.available}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.limits.dailyWorkflowEmails.max}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.limits.dailyWorkflowEmails.percentAvailable}&#37;</span>
                </div>
            </div>
            <hr>
            <div class="row">
                <div class="col-4">
                    <span class="text-muted">${labels['data.storage.mb']}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.limits.dataStorageMB.used}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.limits.dataStorageMB.available}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.limits.dataStorageMB.max}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.limits.dataStorageMB.percentAvailable}&#37;</span>
                </div>
            </div>
            <hr>

            <div class="row">
                <div class="col-4">
                    <span class="text-muted">${labels['durable.streaming.api.concurrent.clients']}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.limits.durableStreamingApiConcurrentClients.used}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.limits.durableStreamingApiConcurrentClients.available}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.limits.durableStreamingApiConcurrentClients.max}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.limits.durableStreamingApiConcurrentClients.percentAvailable}&#37;</span>
                </div>
            </div>
            <hr>

            <div class="row">
                <div class="col-4">
                    <span class="text-muted">${labels['file.storage.mb']}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.limits.fileStorageMB.used}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.limits.fileStorageMB.available}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.limits.fileStorageMB.max}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.limits.fileStorageMB.percentAvailable}&#37;</span>
                </div>
            </div>
            <hr>

            <div class="row">
                <div class="col-4">
                    <span class="text-muted">${labels['hourly.async.report.runs']}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.limits.hourlyAsyncReportRuns.used}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.limits.hourlyAsyncReportRuns.available}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.limits.hourlyAsyncReportRuns.max}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.limits.hourlyAsyncReportRuns.percentAvailable}&#37;</span>
                </div>
            </div>
            <hr>
            <div class="row">
                <div class="col-4">
                    <span class="text-muted">${labels['hourly.dashboard.refreshes']}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.limits.hourlyDashboardRefreshes.used}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.limits.hourlyDashboardRefreshes.available}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.limits.hourlyDashboardRefreshes.max}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.limits.hourlyDashboardRefreshes.percentAvailable}&#37;</span>
                </div>
            </div>
            <hr>
            <div class="row">
                <div class="col-4">
                    <span class="text-muted">${labels['hourly.dashboard.results']}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.limits.hourlyDashboardResults.used}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.limits.hourlyDashboardResults.available}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.limits.hourlyDashboardResults.max}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.limits.hourlyDashboardResults.percentAvailable}&#37;</span>
                </div>
            </div>
            <hr>
            <div class="row">
                <div class="col-4">
                    <span class="text-muted">${labels['hourly.dashboard.statuses']}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.limits.hourlyDashboardStatuses.used}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.limits.hourlyDashboardStatuses.available}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.limits.hourlyDashboardStatuses.max}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.limits.hourlyDashboardStatuses.percentAvailable}&#37;</span>
                </div>
            </div>
            <hr>
            <div class="row">
                <div class="col-4">
                    <span class="text-muted">${labels['hourly.odata.callout']}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.limits.hourlyODataCallout.used}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.limits.hourlyODataCallout.available}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.limits.hourlyODataCallout.max}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.limits.hourlyODataCallout.percentAvailable}&#37;</span>
                </div>
            </div>
            <hr>
            <div class="row">
                <div class="col-4">
                    <span class="text-muted">${labels['hourly.sync.report.runs']}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.limits.hourlySyncReportRuns.used}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.limits.hourlySyncReportRuns.available}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.limits.hourlySyncReportRuns.max}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.limits.hourlySyncReportRuns.percentAvailable}&#37;</span>
                </div>
            </div>
            <hr>
            <div class="row">
                <div class="col-4">
                    <span class="text-muted">${labels['hourly.time.based.workflow']}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.limits.hourlyTimeBasedWorkflow.used}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.limits.hourlyTimeBasedWorkflow.available}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.limits.hourlyTimeBasedWorkflow.max}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.limits.hourlyTimeBasedWorkflow.percentAvailable}&#37;</span>
                </div>
            </div>
            <hr>
            <div class="row">
                <div class="col-4">
                    <span class="text-muted">${labels['mass.email']}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.limits.massEmail.used}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.limits.massEmail.available}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.limits.massEmail.max}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.limits.massEmail.percentAvailable}&#37;</span>
                </div>
            </div>
            <hr>
            <div class="row">
                <div class="col-4">
                    <span class="text-muted">${labels['monthly.platform.events']}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.limits.monthlyPlatformEvents.used}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.limits.monthlyPlatformEvents.available}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.limits.monthlyPlatformEvents.max}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.limits.monthlyPlatformEvents.percentAvailable}&#37;</span>
                </div>
            </div>
            <hr>
            <div class="row">
                <div class="col-4">
                    <span class="text-muted">${labels['package2.version.creates']}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.limits.package2VersionCreates.used}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.limits.package2VersionCreates.available}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.limits.package2VersionCreates.max}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.limits.package2VersionCreates.percentAvailable}&#37;</span>
                </div>
            </div>
            <hr>
            <div class="row">
                <div class="col-4">
                    <span class="text-muted">${labels['single.email']}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.limits.singleEmail.used}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.limits.singleEmail.available}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.limits.singleEmail.max}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.limits.singleEmail.percentAvailable}&#37;</span>
                </div>
            </div>
            <hr>
            <div class="row">
                <div class="col-4">
                    <span class="text-muted">${labels['streaming.api.concurrent.clients']}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.limits.streamingApiConcurrentClients.used}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.limits.streamingApiConcurrentClients.available}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.limits.streamingApiConcurrentClients.max}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.limits.streamingApiConcurrentClients.percentAvailable}&#37;</span>
                </div>
            </div>
            <hr>
        </div>
    </div>
</div>    