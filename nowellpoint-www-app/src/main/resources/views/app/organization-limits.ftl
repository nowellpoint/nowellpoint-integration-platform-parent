<div class="container-fluid pr-5 pl-5 mb-3">
    <div class="card w-100">
        <div class="card-body">
            <h5 class="card-title">${labels['limits']}</h5>
            <div class="row">
                <div class="col-6">
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
            </div>
            <hr>
            <div class="row">
                <div class="col-6">
                    <span class="text-muted">${labels['daily.analytics.dataflow.job.executions']}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.limits.dailyAnalyticsDataflowJobExecutions.used}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.limits.dailyAnalyticsDataflowJobExecutions.remaining}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.limits.dailyAnalyticsDataflowJobExecutions.max}</span>
                </div>
            </div>
            <hr>
            <div class="row">
                <div class="col-6">
                    <span class="text-muted">${labels['daily.api.requests']}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${(organization.limits.dailyApiRequests.used)!}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${(organization.limits.dailyApiRequests.remaining)!}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${(organization.limits.dailyApiRequests.max)!}</span>
                </div>
            </div>
            <hr>
            <div class="row">
                <div class="col-6">
                    <span class="text-muted">${labels['daily.async.apex.executions']}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${(organization.limits.dailyAsyncApexExecutions.used)!}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${(organization.limits.dailyAsyncApexExecutions.remaining)!}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${(organization.limits.dailyAsyncApexExecutions.max)!}</span>
                </div>
            </div>
            <hr>
            <div class="row">
                <div class="col-6">
                    <span class="text-muted">${labels['daily.bulk.api.requests']}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${(organization.limits.dailyBulkApiRequests.used)!}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${(organization.limits.dailyBulkApiRequests.remaining)!}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${(organization.limits.dailyBulkApiRequests.max)!}</span>
                </div>
            </div>
            <hr>
            <div class="row">
                <div class="col-6">
                    <span class="text-muted">${labels['daily.generic.streaming.api.events']}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${(organization.limits.dailyGenericStreamingApiEvents.used)!}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${(organization.limits.dailyGenericStreamingApiEvents.remaining)!}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${(organization.limits.dailyGenericStreamingApiEvents.max)!}</span>
                </div>
            </div>
            <hr>
            <div class="row">
                <div class="col-6">
                    <span class="text-muted">${labels['daily.streaming.api.events']}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${(organization.limits.dailyStreamingApiEvents.used)!}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${(organization.limits.dailyStreamingApiEvents.remaining)!}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${(organization.limits.dailyStreamingApiEvents.max)!}</span>
                </div>
            </div>
            <hr>
            <div class="row">
                <div class="col-6">
                    <span class="text-muted">${labels['concurrent.async.get.report.instances']}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${(organization.limits.concurrentAsyncGetReportInstances.used)!}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${(organization.limits.concurrentAsyncGetReportInstances.remaining)!}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${(organization.limits.concurrentAsyncGetReportInstances.max)!}</span>
                </div>
            </div>
            <hr>
            <div class="row">
                <div class="col-6">
                    <span class="text-muted">${labels['concurrent.sync.report.runs']}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${(organization.limits.concurrentSyncReportRuns.used)!}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${(organization.limits.concurrentSyncReportRuns.remaining)!}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${(organization.limits.concurrentSyncReportRuns.max)!}</span>
                </div>
            </div>

            <hr>
            <div class="row">
                <div class="col-6">
                    <span class="text-muted">${labels['daily.durable.generic.streaming.api.events']}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${(organization.limits.dailyDurableGenericStreamingApiEvents.used)!}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${(organization.limits.dailyDurableGenericStreamingApiEvents.remaining)!}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${(organization.limits.dailyDurableGenericStreamingApiEvents.max)!}</span>
                </div>
            </div>
            <hr>
            <div class="row">
                <div class="col-6">
                    <span class="text-muted">${labels['daily.durable.streaming.api.events']}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${(organization.limits.dailyDurableStreamingApiEvents.used)!}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${(organization.limits.dailyDurableStreamingApiEvents.remaining)!}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${(organization.limits.dailyDurableStreamingApiEvents.max)!}</span>
                </div>
            </div>
            <hr>
            <div class="row">
                <div class="col-6">
                    <span class="text-muted">${labels['daily.workflow.emails']}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${(organization.limits.dailyWorkflowEmails.used)!}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${(organization.limits.dailyWorkflowEmails.remaining)!}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${(organization.limits.dailyWorkflowEmails.max)!}</span>
                </div>
            </div>
            <hr>
            <div class="row">
                <div class="col-6">
                    <span class="text-muted">${labels['data.storage.mb']}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${(organization.limits.dataStorageMB.used)!}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${(organization.limits.dataStorageMB.remaining)!}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${(organization.limits.dataStorageMB.max)!}</span>
                </div>
            </div>
            <hr>

            <div class="row">
                <div class="col-6">
                    <span class="text-muted">${labels['durable.streaming.api.concurrent.clients']}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${(organization.limits.durableStreamingApiConcurrentClients.used)!}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${(organization.limits.durableStreamingApiConcurrentClients.remaining)!}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${(organization.limits.durableStreamingApiConcurrentClients.max)!}</span>
                </div>
            </div>
            <hr>

            <div class="row">
                <div class="col-6">
                    <span class="text-muted">${labels['file.storage.mb']}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${(organization.limits.fileStorageMB.used)!}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${(organization.limits.fileStorageMB.remaining)!}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${(organization.limits.fileStorageMB.max)!}</span>
                </div>
            </div>
            <hr>

            <div class="row">
                <div class="col-6">
                    <span class="text-muted">${labels['hourly.async.report.runs']}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${(organization.limits.hourlyAsyncReportRuns.used)!}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${(organization.limits.hourlyAsyncReportRuns.remaining)!}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${(organization.limits.hourlyAsyncReportRuns.max)!}</span>
                </div>
            </div>
            <hr>
            <div class="row">
                <div class="col-6">
                    <span class="text-muted">${labels['hourly.dashboard.refreshes']}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${(organization.limits.hourlyDashboardRefreshes.used)!}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${(organization.limits.hourlyDashboardRefreshes.remaining)!}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${(organization.limits.hourlyDashboardRefreshes.max)!}</span>
                </div>
            </div>
            <hr>
            <div class="row">
                <div class="col-6">
                    <span class="text-muted">${labels['hourly.dashboard.results']}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${(organization.limits.hourlyDashboardResults.used)!}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${(organization.limits.hourlyDashboardResults.remaining)!}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${(organization.limits.hourlyDashboardResults.max)!}</span>
                </div>
            </div>
            <hr>
            <div class="row">
                <div class="col-6">
                    <span class="text-muted">${labels['hourly.dashboard.statuses']}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${(organization.limits.hourlyDashboardStatuses.used)!}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${(organization.limits.hourlyDashboardStatuses.remaining)!}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${(organization.limits.hourlyDashboardStatuses.max)!}</span>
                </div>
            </div>
            <hr>
            <div class="row">
                <div class="col-6">
                    <span class="text-muted">${labels['hourly.odata.callout']}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${(organization.limits.hourlyODataCallout.used)!}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${(organization.limits.hourlyODataCallout.remaining)!}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${(organization.limits.hourlyODataCallout.max)!}</span>
                </div>
            </div>
            <hr>
            <div class="row">
                <div class="col-6">
                    <span class="text-muted">${labels['hourly.sync.report.runs']}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${(organization.limits.hourlySyncReportRuns.used!)}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${(organization.limits.hourlySyncReportRuns.remaining!)}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${(organization.limits.hourlySyncReportRuns.max!)}</span>
                </div>
            </div>
            <hr>
            <div class="row">
                <div class="col-6">
                    <span class="text-muted">${labels['hourly.time.based.workflow']}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${(organization.limits.hourlyTimeBasedWorkflow.used)!}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${(organization.limits.hourlyTimeBasedWorkflow.remaining)!}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${(organization.limits.hourlyTimeBasedWorkflow.max)!}</span>
                </div>
            </div>
            <hr>
            <div class="row">
                <div class="col-6">
                    <span class="text-muted">${labels['mass.email']}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${(organization.limits.massEmail.used)!}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${(organization.limits.massEmail.remaining)!}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${(organization.limits.massEmail.max)!}</span>
                </div>
            </div>
            <hr>
            <div class="row">
                <div class="col-6">
                    <span class="text-muted">${labels['monthly.platform.events']}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${(organization.limits.monthlyPlatformEvents.used)!}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${(organization.limits.monthlyPlatformEvents.remaining)!}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${(organization.limits.monthlyPlatformEvents.max)!}</span>
                </div>
            </div>
            <hr>
            <div class="row">
                <div class="col-6">
                    <span class="text-muted">${labels['package2.version.creates']}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${(organization.limits.package2VersionCreates.used)!}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${(organization.limits.package2VersionCreates.remaining)!}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${(organization.limits.package2VersionCreates.max)!}</span>
                </div>
            </div>
            <hr>
            <div class="row">
                <div class="col-6">
                    <span class="text-muted">${labels['single.email']}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${(organization.limits.singleEmail.used)!}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${(organization.limits.singleEmail.remaining)!}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${(organization.limits.singleEmail.max)!}</span>
                </div>
            </div>
            <hr>
            <div class="row">
                <div class="col-6">
                    <span class="text-muted">${labels['streaming.api.concurrent.clients']}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${(organization.limits.streamingApiConcurrentClients.used)!}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${(organization.limits.streamingApiConcurrentClients.remaining)!}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${(organization.limits.streamingApiConcurrentClients.max)!}</span>
                </div>
            </div>
            <hr>
        </div>
    </div>
</div>    