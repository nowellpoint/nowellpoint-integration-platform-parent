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
                <span class="text-muted">ConcurrentAsyncGetReportInstances</span>
            </div>
            <div class="col-3 text-right">
                ${(organization.limits.concurrentAsyncGetReportInstances.max!)}
            </div>
            <div class="col-3 text-right">
                ${(organization.limits.concurrentAsyncGetReportInstances.remaining!)}
            </div>
        </div>
        <hr>
        <div class="row">
            <div class="col-6">
                <span class="text-muted">ConcurrentSyncReportRuns</span>
            </div>
            <div class="col-3 text-right">
                ${(organization.limits.concurrentSyncReportRuns.max!)}
            </div>
            <div class="col-3 text-right">
                ${(organization.limits.concurrentSyncReportRuns.remaining!)}
            </div>
        </div>
        <hr>
        <div class="row">
            <div class="col-6">
                <span class="text-muted">DailyAnalyticsDataflowJobExecutions</span>
            </div>
            <div class="col-3 text-right">
                ${(organization.limits.dailyAnalyticsDataflowJobExecutions.max!)}
            </div>
            <div class="col-3 text-right">
                ${(organization.limits.dailyAnalyticsDataflowJobExecutions.remaining!)}
            </div>
        </div>
        <hr>
        <div class="row">
            <div class="col-6">
                <span class="text-muted">DailyAsyncApexExecutions</span>
            </div>
            <div class="col-3 text-right">
                ${(organization.limits.dailyAsyncApexExecutions.max!)}
            </div>
            <div class="col-3 text-right">
                ${(organization.limits.dailyAsyncApexExecutions.remaining!)}
            </div>
        </div>
        <hr>
        <div class="row">
            <div class="col-6">
                <span class="text-muted">DailyDurableGenericStreamingApiEvents</span>
            </div>
            <div class="col-3 text-right">
                ${(organization.limits.dailyDurableGenericStreamingApiEvents.max!)}
            </div>
            <div class="col-3 text-right">
                ${(organization.limits.dailyDurableGenericStreamingApiEvents.remaining!)}
            </div>
        </div>
        <hr>
        <div class="row">
            <div class="col-6">
                <span class="text-muted">DailyDurableStreamingApiEvents</span>
            </div>
            <div class="col-3 text-right">
                ${(organization.limits.dailyDurableStreamingApiEvents.max!)}
            </div>
            <div class="col-3 text-right">
                ${(organization.limits.dailyDurableStreamingApiEvents.remaining!)}
            </div>
        </div>
        <hr>
    </div>
</div>