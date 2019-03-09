<#import "template.html" as t>
    <@t.page>
        <#include "sidebar.ftl" />
        <div id="content">
            <div class="container-fluid mt-2 pt-3 pr-5 pl-5 mb-3">
                <div class="dashhead">
                    <div class="dashhead-titles">
                        <h6 class="dashhead-subtitle">${labels["organization"]}</h6>
                        <h3 class="dashhead-title">${organization.name}</h4>
                    </div>
                    <div class="dashhead-toolbar">
                        <div class="dashhead-toolbar-item">
                            <span class="navbar-text">
                                <strong>${labels['last.refreshed']}:&nbsp;</strong>
                                ${organization.dashboard.lastRefreshedOn?date?string.long} - ${organization.dashboard.lastRefreshedOn?time?string.medium}
                            </span>
                        </div>
                        <div class="dashhead-toolbar-item">
                            <button type="button" id="refresh" name="refresh" class="btn bg-transparent" data-toggle="tooltip" data-placement="top" title="${labels['refresh']}">
                                <i class="fa fa-sync"></i>
                            </button>
                        </div>
                    </div>
                </div>
            </div>
            <#include "organization-metadata.ftl" />
            <#include "organization-licenses.ftl" />
            <#include "organization-limits.ftl" />
        </div>
        <#macro usage percent>
            <div class="progress" style="height:20px">
            <#if percent lt 6>
                <div class="progress-bar bg-danger" role="progressbar" style="width: 100%; height=20px;" aria-valuenow="100" aria-valuemin="0" aria-valuemax="100">${percent}&#37;</div>
            <#elseif percent gt 10>
                <div class="progress-bar bg-success" role="progressbar" style="width: 100%; height=20px;" aria-valuenow="100" aria-valuemin="0" aria-valuemax="100">${percent}&#37;</div>
            <#else>
                <div class="progress-bar bg-warning" role="progressbar" style="width: 100%; height=20px;" aria-valuenow="100" aria-valuemin="0" aria-valuemax="100">${percent}&#37;</div>
            </#if>
            </div>                    
        </#macro>
        <script type="text/javascript" src="/js/organization.js"></script>
    </@t.page>