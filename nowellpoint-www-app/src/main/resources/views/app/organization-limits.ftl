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
                    &nbsp;
                </div>
            </div>
            <hr>
            <@limitsView limit=organization.dashboard.limits.dailyAnalyticsDataflowJobExecutions
                         label=labels['daily.analytics.dataflow.job.executions']/>
            <@limitsView limit=organization.dashboard.limits.dailyApiRequests
                         label=labels['daily.api.requests']/>
            <@limitsView limit=organization.dashboard.limits.dailyAsyncApexExecutions
                         label=labels['daily.async.apex.executions']/>
            <@limitsView limit=organization.dashboard.limits.dailyBulkApiRequests
                         label=labels['daily.bulk.api.requests']/>
            <@limitsView limit=organization.dashboard.limits.dailyGenericStreamingApiEvents
                         label=labels['daily.generic.streaming.api.events']/>
            <@limitsView limit=organization.dashboard.limits.dailyStreamingApiEvents
                         label=labels['daily.streaming.api.events']/>
            <@limitsView limit=organization.dashboard.limits.concurrentAsyncGetReportInstances
                         label=labels['concurrent.async.get.report.instances']/>
            <@limitsView limit=organization.dashboard.limits.concurrentSyncReportRuns
                         label=labels['concurrent.sync.report.runs']/>
            <@limitsView limit=organization.dashboard.limits.dailyDurableGenericStreamingApiEvents
                         label=labels['daily.durable.generic.streaming.api.events']/>
            <@limitsView limit=organization.dashboard.limits.dailyDurableStreamingApiEvents
                         label=labels['daily.durable.streaming.api.events']/>
            <@limitsView limit=organization.dashboard.limits.dailyWorkflowEmails
                         label=labels['daily.workflow.emails']/>
            <@limitsView limit=organization.dashboard.limits.dataStorageMB
                         label=labels['data.storage.mb']/>
            <@limitsView limit=organization.dashboard.limits.durableStreamingApiConcurrentClients
                         label=labels['durable.streaming.api.concurrent.clients']/>
            <@limitsView limit=organization.dashboard.limits.fileStorageMB
                         label=labels['file.storage.mb']/>
            <@limitsView limit=organization.dashboard.limits.hourlyAsyncReportRuns
                         label=labels['hourly.async.report.runs']/>
            <@limitsView limit=organization.dashboard.limits.hourlyDashboardRefreshes
                         label=labels['hourly.dashboard.refreshes']/>
            <@limitsView limit=organization.dashboard.limits.hourlyDashboardResults
                         label=labels['hourly.dashboard.results']/>
            <@limitsView limit=organization.dashboard.limits.hourlyDashboardStatuses
                         label=labels['hourly.dashboard.statuses']/>
            <@limitsView limit=organization.dashboard.limits.hourlyODataCallout
                         label=labels['hourly.odata.callout']/>
            <@limitsView limit=organization.dashboard.limits.hourlySyncReportRuns
                         label=labels['hourly.sync.report.runs']/>
            <@limitsView limit=organization.dashboard.limits.hourlyTimeBasedWorkflow
                         label=labels['hourly.time.based.workflow']/>
            <@limitsView limit=organization.dashboard.limits.massEmail
                         label=labels['mass.email']/>
            <@limitsView limit=organization.dashboard.limits.monthlyPlatformEvents
                         label=labels['monthly.platform.events']/>
            <@limitsView limit=organization.dashboard.limits.package2VersionCreates
                         label=labels['package2.version.creates']/>
            <@limitsView limit=organization.dashboard.limits.singleEmail
                         label=labels['single.email']/>
            <@limitsView limit=organization.dashboard.limits.streamingApiConcurrentClients
                          label=labels['streaming.api.concurrent.clients']/>
            
        </div>
    </div>
</div>