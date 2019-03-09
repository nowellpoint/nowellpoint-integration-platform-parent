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
            <div class="row">
                <div class="col-4">
                    <span class="text-muted">${labels['daily.analytics.dataflow.job.executions']}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.dashboard.limits.dailyAnalyticsDataflowJobExecutions.used}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.dashboard.limits.dailyAnalyticsDataflowJobExecutions.available}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.dashboard.limits.dailyAnalyticsDataflowJobExecutions.max}</span>
                </div>
                <div class="col-1"></div>    
                <div class="col-1">
                    <@usage percent=organization.dashboard.limits.dailyAnalyticsDataflowJobExecutions.percentAvailable/> 
                </div>
            </div>
            <hr>
            <div class="row">
                <div class="col-4">
                    <span class="text-muted">${labels['daily.api.requests']}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.dashboard.limits.dailyApiRequests.used}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.dashboard.limits.dailyApiRequests.available}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.dashboard.limits.dailyApiRequests.max}</span>
                </div>
                <div class="col-1"></div>    
                <div class="col-1">
                    <@usage percent=organization.dashboard.limits.dailyApiRequests.percentAvailable/> 
                </div>
            </div>
            <hr>
            <div class="row">
                <div class="col-4">
                    <span class="text-muted">${labels['daily.async.apex.executions']}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.dashboard.limits.dailyAsyncApexExecutions.used}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.dashboard.limits.dailyAsyncApexExecutions.available}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.dashboard.limits.dailyAsyncApexExecutions.max}</span>
                </div>
                <div class="col-1"></div>    
                <div class="col-1">
                    <@usage percent=organization.dashboard.limits.dailyAsyncApexExecutions.percentAvailable/> 
                </div>
            </div>
            <hr>
            <div class="row">
                <div class="col-4">
                    <span class="text-muted">${labels['daily.bulk.api.requests']}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.dashboard.limits.dailyBulkApiRequests.used}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.dashboard.limits.dailyBulkApiRequests.available}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.dashboard.limits.dailyBulkApiRequests.max}</span>
                </div>
                <div class="col-1"></div>    
                <div class="col-1">
                    <@usage percent=organization.dashboard.limits.dailyBulkApiRequests.percentAvailable/> 
                </div>
            </div>
            <hr>
            <div class="row">
                <div class="col-4">
                    <span class="text-muted">${labels['daily.generic.streaming.api.events']}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.dashboard.limits.dailyGenericStreamingApiEvents.used}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.dashboard.limits.dailyGenericStreamingApiEvents.available}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.dashboard.limits.dailyGenericStreamingApiEvents.max}</span>
                </div>
                <div class="col-1"></div>    
                <div class="col-1">
                    <@usage percent=organization.dashboard.limits.dailyGenericStreamingApiEvents.percentAvailable/> 
                </div>
            </div>
            <hr>
            <div class="row">
                <div class="col-4">
                    <span class="text-muted">${labels['daily.streaming.api.events']}</span>
                </div>

                <div class="col-2 text-right">
                    <span class="text-muted">${organization.dashboard.limits.dailyStreamingApiEvents.used}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.dashboard.limits.dailyStreamingApiEvents.available}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.dashboard.limits.dailyStreamingApiEvents.max}</span>
                </div>
                <div class="col-1"></div>    
                <div class="col-1">
                    <@usage percent=organization.dashboard.limits.dailyStreamingApiEvents.percentAvailable/> 
                </div>
            </div>
            <hr>
            <div class="row">
                <div class="col-4">
                    <span class="text-muted">${labels['concurrent.async.get.report.instances']}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.dashboard.limits.concurrentAsyncGetReportInstances.used}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.dashboard.limits.concurrentAsyncGetReportInstances.available}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.dashboard.limits.concurrentAsyncGetReportInstances.max}</span>
                </div>
                <div class="col-1"></div>    
                <div class="col-1">
                    <@usage percent=organization.dashboard.limits.concurrentAsyncGetReportInstances.percentAvailable/> 
                </div>
            </div>
            <hr>
            <div class="row">
                <div class="col-4">
                    <span class="text-muted">${labels['concurrent.sync.report.runs']}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.dashboard.limits.concurrentSyncReportRuns.used}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.dashboard.limits.concurrentSyncReportRuns.available}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.dashboard.limits.concurrentSyncReportRuns.max}</span>
                </div>
                <div class="col-1"></div>    
                <div class="col-1">
                    <@usage percent=organization.dashboard.limits.concurrentSyncReportRuns.percentAvailable/> 
                </div>
            </div>

            <hr>
            <div class="row">
                <div class="col-4">
                    <span class="text-muted">${labels['daily.durable.generic.streaming.api.events']}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.dashboard.limits.dailyDurableGenericStreamingApiEvents.used}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.dashboard.limits.dailyDurableGenericStreamingApiEvents.available}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.dashboard.limits.dailyDurableGenericStreamingApiEvents.max}</span>
                </div>
                <div class="col-1"></div>    
                <div class="col-1">
                    <@usage percent=organization.dashboard.limits.dailyDurableGenericStreamingApiEvents.percentAvailable/> 
                </div>
            </div>
            <hr>
            <div class="row">
                <div class="col-4">
                    <span class="text-muted">${labels['daily.durable.streaming.api.events']}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.dashboard.limits.dailyDurableStreamingApiEvents.used}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.dashboard.limits.dailyDurableStreamingApiEvents.available}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.dashboard.limits.dailyDurableStreamingApiEvents.max}</span>
                </div>
                <div class="col-1"></div>    
                <div class="col-1">
                    <@usage percent=organization.dashboard.limits.dailyDurableStreamingApiEvents.percentAvailable/> 
                </div>
            </div>
            <hr>
            <div class="row">
                <div class="col-4">
                    <span class="text-muted">${labels['daily.workflow.emails']}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.dashboard.limits.dailyWorkflowEmails.used}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.dashboard.limits.dailyWorkflowEmails.available}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.dashboard.limits.dailyWorkflowEmails.max}</span>
                </div>
                <div class="col-1"></div>    
                <div class="col-1">
                    <@usage percent=organization.dashboard.limits.dailyWorkflowEmails.percentAvailable/> 
                </div>
            </div>
            <hr>
            <div class="row">
                <div class="col-4">
                    <span class="text-muted">${labels['data.storage.mb']}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.dashboard.limits.dataStorageMB.used}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.dashboard.limits.dataStorageMB.available}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.dashboard.limits.dataStorageMB.max}</span>
                </div>
                <div class="col-1"></div>    
                <div class="col-1">
                    <@usage percent=organization.dashboard.limits.dataStorageMB.percentAvailable/> 
                </div>
            </div>
            <hr>

            <div class="row">
                <div class="col-4">
                    <span class="text-muted">${labels['durable.streaming.api.concurrent.clients']}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.dashboard.limits.durableStreamingApiConcurrentClients.used}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.dashboard.limits.durableStreamingApiConcurrentClients.available}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.dashboard.limits.durableStreamingApiConcurrentClients.max}</span>
                </div>
                <div class="col-1"></div>    
                <div class="col-1">
                    <@usage percent=organization.dashboard.limits.durableStreamingApiConcurrentClients.percentAvailable/> 
                </div>
            </div>
            <hr>

            <div class="row">
                <div class="col-4">
                    <span class="text-muted">${labels['file.storage.mb']}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.dashboard.limits.fileStorageMB.used}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.dashboard.limits.fileStorageMB.available}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.dashboard.limits.fileStorageMB.max}</span>
                </div>
                <div class="col-1"></div>    
                <div class="col-1">
                    <@usage percent=organization.dashboard.limits.fileStorageMB.percentAvailable/> 
                </div>
            </div>
            <hr>

            <div class="row">
                <div class="col-4">
                    <span class="text-muted">${labels['hourly.async.report.runs']}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.dashboard.limits.hourlyAsyncReportRuns.used}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.dashboard.limits.hourlyAsyncReportRuns.available}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.dashboard.limits.hourlyAsyncReportRuns.max}</span>
                </div>
                <div class="col-1"></div>    
                <div class="col-1">
                    <@usage percent=organization.dashboard.limits.hourlyAsyncReportRuns.percentAvailable/> 
                </div>
            </div>
            <hr>
            <div class="row">
                <div class="col-4">
                    <span class="text-muted">${labels['hourly.dashboard.refreshes']}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.dashboard.limits.hourlyDashboardRefreshes.used}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.dashboard.limits.hourlyDashboardRefreshes.available}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.dashboard.limits.hourlyDashboardRefreshes.max}</span>
                </div>
                <div class="col-1"></div>    
                <div class="col-1">
                    <@usage percent=organization.dashboard.limits.hourlyDashboardRefreshes.percentAvailable/> 
                </div>
            </div>
            <hr>
            <div class="row">
                <div class="col-4">
                    <span class="text-muted">${labels['hourly.dashboard.results']}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.dashboard.limits.hourlyDashboardResults.used}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.dashboard.limits.hourlyDashboardResults.available}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.dashboard.limits.hourlyDashboardResults.max}</span>
                </div>
                <div class="col-1"></div>    
                <div class="col-1">
                    <@usage percent=organization.dashboard.limits.hourlyDashboardResults.percentAvailable/> 
                </div>
            </div>
            <hr>
            <div class="row">
                <div class="col-4">
                    <span class="text-muted">${labels['hourly.dashboard.statuses']}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.dashboard.limits.hourlyDashboardStatuses.used}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.dashboard.limits.hourlyDashboardStatuses.available}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.dashboard.limits.hourlyDashboardStatuses.max}</span>
                </div>
                <div class="col-1"></div>    
                <div class="col-1">
                    <@usage percent=organization.dashboard.limits.hourlyDashboardStatuses.percentAvailable/> 
                </div>
            </div>
            <hr>
            <div class="row">
                <div class="col-4">
                    <span class="text-muted">${labels['hourly.odata.callout']}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.dashboard.limits.hourlyODataCallout.used}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.dashboard.limits.hourlyODataCallout.available}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.dashboard.limits.hourlyODataCallout.max}</span>
                </div>
                <div class="col-1"></div>    
                <div class="col-1">
                    <@usage percent=organization.dashboard.limits.hourlyODataCallout.percentAvailable/> 
                </div>
            </div>
            <hr>
            <div class="row">
                <div class="col-4">
                    <span class="text-muted">${labels['hourly.sync.report.runs']}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.dashboard.limits.hourlySyncReportRuns.used}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.dashboard.limits.hourlySyncReportRuns.available}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.dashboard.limits.hourlySyncReportRuns.max}</span>
                </div>
                <div class="col-1"></div>    
                <div class="col-1">
                    <@usage percent=organization.dashboard.limits.hourlySyncReportRuns.percentAvailable/> 
                </div>
            </div>
            <hr>
            <div class="row">
                <div class="col-4">
                    <span class="text-muted">${labels['hourly.time.based.workflow']}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.dashboard.limits.hourlyTimeBasedWorkflow.used}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.dashboard.limits.hourlyTimeBasedWorkflow.available}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.dashboard.limits.hourlyTimeBasedWorkflow.max}</span>
                </div>
                <div class="col-1"></div>    
                <div class="col-1">
                    <@usage percent=organization.dashboard.limits.hourlyTimeBasedWorkflow.percentAvailable/> 
                </div>
            </div>
            <hr>
            <div class="row">
                <div class="col-4">
                    <span class="text-muted">${labels['mass.email']}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.dashboard.limits.massEmail.used}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.dashboard.limits.massEmail.available}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.dashboard.limits.massEmail.max}</span>
                </div>
                <div class="col-1"></div>    
                <div class="col-1">
                    <@usage percent=organization.dashboard.limits.massEmail.percentAvailable/> 
                </div>
            </div>
            <hr>
            <div class="row">
                <div class="col-4">
                    <span class="text-muted">${labels['monthly.platform.events']}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.dashboard.limits.monthlyPlatformEvents.used}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.dashboard.limits.monthlyPlatformEvents.available}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.dashboard.limits.monthlyPlatformEvents.max}</span>
                </div>
                <div class="col-1"></div>    
                <div class="col-1">
                    <@usage percent=organization.dashboard.limits.monthlyPlatformEvents.percentAvailable/> 
                </div>
            </div>
            <hr>
            <div class="row">
                <div class="col-4">
                    <span class="text-muted">${labels['package2.version.creates']}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.dashboard.limits.package2VersionCreates.used}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.dashboard.limits.package2VersionCreates.available}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.dashboard.limits.package2VersionCreates.max}</span>
                </div>
                <div class="col-1"></div>    
                <div class="col-1">
                    <@usage percent=organization.dashboard.limits.package2VersionCreates.percentAvailable/> 
                </div>
            </div>
            <hr>
            <div class="row">
                <div class="col-4">
                    <span class="text-muted">${labels['single.email']}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.dashboard.limits.singleEmail.used}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.dashboard.limits.singleEmail.available}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.dashboard.limits.singleEmail.max}</span>
                </div>
                <div class="col-1"></div>    
                <div class="col-1">
                    <@usage percent=organization.dashboard.limits.singleEmail.percentAvailable/> 
                </div>
            </div>
            <hr>
            <div class="row">
                <div class="col-4">
                    <span class="text-muted">${labels['streaming.api.concurrent.clients']}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.dashboard.limits.streamingApiConcurrentClients.used}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.dashboard.limits.streamingApiConcurrentClients.available}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${organization.dashboard.limits.streamingApiConcurrentClients.max}</span>
                </div>
                <div class="col-1"></div>    
                <div class="col-1">
                    <@usage percent=organization.dashboard.limits.streamingApiConcurrentClients.percentAvailable/> 
                </div>
            </div>
            <hr>
        </div>
    </div>
</div>