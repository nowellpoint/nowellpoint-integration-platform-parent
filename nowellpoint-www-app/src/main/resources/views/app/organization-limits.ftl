<div class="tab-pane" id="limits">
    <div class="container-fluid pr-5 pl-5 mb-3">
        <div class="row">
            <div class="col-6">
                <span>${labels['limit']}</span>
            </div>
            <div class="col-3 text-right">
                ${labels['maximum']}
            </div>
            <div class="col-3 text-right">
                ${labels['remainder']}
            </div>
        </div>
        <hr>
        <div class="row">
            <div class="col-6">
                <span class="text-muted">${labels['concurrent.async.get.report.instances']}</span>
            </div>
            <div class="col-3 text-right">
                ${(organization.limits.concurrentAsyncGetReportInstances.max)!}
            </div>
            <div class="col-3 text-right">
                ${(organization.limits.concurrentAsyncGetReportInstances.remaining)!}
            </div>
        </div>
        <hr>
        <div class="row">
            <div class="col-6">
                <span class="text-muted">${labels['concurrent.sync.report.runs']}</span>
            </div>
            <div class="col-3 text-right">
                ${(organization.limits.concurrentSyncReportRuns.max)!}
            </div>
            <div class="col-3 text-right">
                ${(organization.limits.concurrentSyncReportRuns.remaining)!}
            </div>
        </div>
        <hr>
        <div class="row">
            <div class="col-6">
                <span class="text-muted">${labels['daily.analytics.dataflow.job.executions']}</span>
            </div>
            <div class="col-3 text-right">
                ${(organization.limits.dailyAnalyticsDataflowJobExecutions.max)!}
            </div>
            <div class="col-3 text-right">
                ${(organization.limits.dailyAnalyticsDataflowJobExecutions.remaining)!}
            </div>
        </div>
        <hr>
        <div class="row">
            <div class="col-6">
                <span class="text-muted">${labels['daily.async.apex.executions']}</span>
            </div>
            <div class="col-3 text-right">
                ${(organization.limits.dailyAsyncApexExecutions.max)!}
            </div>
            <div class="col-3 text-right">
                ${(organization.limits.dailyAsyncApexExecutions.remaining)!}
            </div>
        </div>
        <hr>
        <div class="row">
            <div class="col-6">
                <span class="text-muted">${labels['daily.durable.generic.streaming.api.events']}</span>
            </div>
            <div class="col-3 text-right">
                ${(organization.limits.dailyDurableGenericStreamingApiEvents.max)!}
            </div>
            <div class="col-3 text-right">
                ${(organization.limits.dailyDurableGenericStreamingApiEvents.remaining)!}
            </div>
        </div>
        <hr>
        <div class="row">
            <div class="col-6">
                <span class="text-muted">${labels['daily.durable.streaming.api.events']}</span>
            </div>
            <div class="col-3 text-right">
                ${(organization.limits.dailyDurableStreamingApiEvents.max)!}
            </div>
            <div class="col-3 text-right">
                ${(organization.limits.dailyDurableStreamingApiEvents.remaining)!}
            </div>
        </div>
        <hr>
        <div class="row">
            <div class="col-6">
                <span class="text-muted">${labels['daily.workflow.emails']}</span>
            </div>
            <div class="col-3 text-right">
                ${(organization.limits.dailyWorkflowEmails.max)!}
            </div>
            <div class="col-3 text-right">
                ${(organization.limits.dailyWorkflowEmails.remaining)!}
            </div>
        </div>
        <hr>
        <div class="row">
            <div class="col-6">
                <span class="text-muted">${labels['data.storage.mb']}</span>
            </div>
            <div class="col-3 text-right">
                ${(organization.limits.dataStorageMB.max)!}
            </div>
            <div class="col-3 text-right">
                ${(organization.limits.dataStorageMB.remaining)!}
            </div>
        </div>
        <hr>
        
        <div class="row">
            <div class="col-6">
                <span class="text-muted">${labels['durable.streaming.api.concurrent.clients']}</span>
            </div>
            <div class="col-3 text-right">
                ${(organization.limits.durableStreamingApiConcurrentClients.max)!}
            </div>
            <div class="col-3 text-right">
                ${(organization.limits.durableStreamingApiConcurrentClients.remaining)!}
            </div>
        </div>
        <hr>
        
        <div class="row">
            <div class="col-6">
                <span class="text-muted">${labels['file.storage.mb']}</span>
            </div>
            <div class="col-3 text-right">
                ${(organization.limits.fileStorageMB.max)!}
            </div>
            <div class="col-3 text-right">
                ${(organization.limits.fileStorageMB.remaining)!}
            </div>
        </div>
        <hr>
        
        <div class="row">
            <div class="col-6">
                <span class="text-muted">${labels['hourly.async.report.runs']}</span>
            </div>
            <div class="col-3 text-right">
                ${(organization.limits.hourlyAsyncReportRuns.max)!}
            </div>
            <div class="col-3 text-right">
                ${(organization.limits.hourlyAsyncReportRuns.remaining)!}
            </div>
        </div>
        <hr>
        <div class="row">
            <div class="col-6">
                <span class="text-muted">${labels['hourly.dashboard.refreshes']}</span>
            </div>
            <div class="col-3 text-right">
                ${(organization.limits.hourlyDashboardRefreshes.max)!}
            </div>
            <div class="col-3 text-right">
                ${(organization.limits.hourlyDashboardRefreshes.remaining)!}
            </div>
        </div>
        <hr>
        <div class="row">
            <div class="col-6">
                <span class="text-muted">${labels['hourly.dashboard.results']}</span>
            </div>
            <div class="col-3 text-right">
                ${(organization.limits.hourlyDashboardResults.max)!}
            </div>
            <div class="col-3 text-right">
                ${(organization.limits.hourlyDashboardResults.remaining)!}
            </div>
        </div>
        <hr>
        <div class="row">
            <div class="col-6">
                <span class="text-muted">${labels['hourly.dashboard.statuses']}</span>
            </div>
            <div class="col-3 text-right">
                ${(organization.limits.hourlyDashboardStatuses.max)!}
            </div>
            <div class="col-3 text-right">
                ${(organization.limits.hourlyDashboardStatuses.remaining)!}
            </div>
        </div>
        <hr>
        <div class="row">
            <div class="col-6">
                <span class="text-muted">${labels['hourly.odata.callout']}</span>
            </div>
            <div class="col-3 text-right">
                ${(organization.limits.hourlyODataCallout.max)!}
            </div>
            <div class="col-3 text-right">
                ${(organization.limits.hourlyODataCallout.remaining)!}
            </div>
        </div>
        <hr>
        <div class="row">
            <div class="col-6">
                <span class="text-muted">${labels['hourly.sync.report.runs']}</span>
            </div>
            <div class="col-3 text-right">
                ${(organization.limits.hourlySyncReportRuns.max!)}
            </div>
            <div class="col-3 text-right">
                ${(organization.limits.hourlySyncReportRuns.remaining!)}
            </div>
        </div>
        <hr>
        <div class="row">
            <div class="col-6">
                <span class="text-muted">${labels['hourly.time.based.workflow']}</span>
            </div>
            <div class="col-3 text-right">
                ${(organization.limits.hourlyTimeBasedWorkflow.max)!}
            </div>
            <div class="col-3 text-right">
                ${(organization.limits.hourlyTimeBasedWorkflow.remaining)!}
            </div>
        </div>
        <hr>
        <div class="row">
            <div class="col-6">
                <span class="text-muted">${labels['mass.email']}</span>
            </div>
            <div class="col-3 text-right">
                ${(organization.limits.massEmail.max)!}
            </div>
            <div class="col-3 text-right">
                ${(organization.limits.massEmail.remaining)!}
            </div>
        </div>
        <hr>
        <div class="row">
            <div class="col-6">
                <span class="text-muted">${labels['monthly.platform.events']}</span>
            </div>
            <div class="col-3 text-right">
                ${(organization.limits.monthlyPlatformEvents.max)!}
            </div>
            <div class="col-3 text-right">
                ${(organization.limits.monthlyPlatformEvents.remaining)!}
            </div>
        </div>
        <hr>
        <div class="row">
            <div class="col-6">
                <span class="text-muted">${labels['package2.version.creates']}</span>
            </div>
            <div class="col-3 text-right">
                ${(organization.limits.package2VersionCreates.max)!}
            </div>
            <div class="col-3 text-right">
                ${(organization.limits.package2VersionCreates.remaining)!}
            </div>
        </div>
        <hr>
        <div class="row">
            <div class="col-6">
                <span class="text-muted">${labels['single.email']}</span>
            </div>
            <div class="col-3 text-right">
                ${(organization.limits.singleEmail.max)!}
            </div>
            <div class="col-3 text-right">
                ${(organization.limits.singleEmail.remaining)!}
            </div>
        </div>
        <hr>
        <div class="row">
            <div class="col-6">
                <span class="text-muted">${labels['streaming.api.concurrent.clients']}</span>
            </div>
            <div class="col-3 text-right">
                ${(organization.limits.streamingApiConcurrentClients.max)!}
            </div>
            <div class="col-3 text-right">
                ${(organization.limits.streamingApiConcurrentClients.remaining)!}
            </div>
        </div>
        <hr>
    </div>
</div>