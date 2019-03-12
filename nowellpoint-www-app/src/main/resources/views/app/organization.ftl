<#import "template.html" as t>
    <@t.page>
        <#include "sidebar.ftl" />
        <div id="content">
            <div class="container-fluid mt-2 pt-3 pr-5 pl-5 mb-3">
                <div class="dashhead">
                    <div class="dashhead-titles">
                        <h6 class="dashhead-subtitle">${labels["organization"]}</h6>
                        <h3 class="dashhead-title">${organization.name}</h3>
                    </div>
                    <div class="dashhead-toolbar">
                        <div class="dashhead-toolbar-item">
                            <button type="button" id="refresh" name="refresh" class="btn btn-secondary">
                                <i class="fa fa-sync"></i>&emsp;${labels['refresh']}
                            </button>
                        </div>
                    </div>
                </div>
                <br>
                <div class="alert alert-light text-center border" role="alert">
                    <strong>${labels['last.refreshed']}:&nbsp;</strong> ${organization.dashboard.lastRefreshedOn?date?string.long} - ${organization.dashboard.lastRefreshedOn?time?string.medium}
                </div>

            </div>

            <#include "organization-metadata.ftl" />
            <#include "organization-licenses.ftl" />
            <#include "organization-limits.ftl" />
        </div>
        <#macro usage percent max>
            <div class="progress" style="height:20px">
                <#if max == 0>
                    <div class="progress-bar bg-light" role="progressbar" style="width: 100%; height=20px;" aria-valuenow="100" aria-valuemin="0" aria-valuemax="100">${percent}&#37;</div>
                <#elseif percent lt 6>
                    <div class="progress-bar bg-danger" role="progressbar" style="width: 100%; height=20px;" aria-valuenow="100" aria-valuemin="0" aria-valuemax="100">${percent}&#37;</div>
                <#elseif percent gt 10>
                    <div class="progress-bar bg-success" role="progressbar" style="width: 100%; height=20px;" aria-valuenow="100" aria-valuemin="0" aria-valuemax="100">${percent}&#37;</div>
                <#else>
                    <div class="progress-bar bg-warning" role="progressbar" style="width: 100%; height=20px;" aria-valuenow="100" aria-valuemin="0" aria-valuemax="100">${percent}&#37;</div>
                </#if>
            </div>
        </#macro>
        <#macro limitsView label limit>
            <div class="row">
                <div class="col-4">
                    <span class="text-muted">${label}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${limit.used?string(",##0")}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${limit.available?string(",##0")}</span>
                </div>
                <div class="col-2 text-right">
                    <span class="text-muted">${limit.max?string(",##0")}</span>
                </div>
                <div class="col-1"></div>    
                <div class="col-1">
                    <@usage percent=limit.percentAvailable
                            max=limit.max/> 
                </div>
            </div>
            <hr>
        </#macro>    
        <script type="text/javascript" src="/js/organization.js"></script>
    </@t.page>