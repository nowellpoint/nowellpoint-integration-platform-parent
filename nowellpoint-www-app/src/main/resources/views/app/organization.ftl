<#import "template.html" as t>
    <@t.page>
        <#include "sidebar.ftl" />
        <div id="content">
            <div class="container-fluid mt-2 pt-3 pr-5 pl-5 mb-3">
                <div class="dashhead">
                    <div class="dashhead-titles">
                        <h6 class="dashhead-subtitle">&nbsp;</h6>
                        <h4 class="dashhead-title font-weight-normal">${labels["organization"]}</h4>
                    </div>
                </div>
            </div>

            <#include "organization-dashboard.ftl" />
            <#include "organization-licenses.ftl" />
            <#include "organization-limits.ftl" />

            <!--
            
            <div class="tab-pane" id="dashboard">
                    <div class="container-fluid pr-5 pl-5 mb-3">
                        <div class="dashhead">
                            <div class="dashhead-toolbar">
                                <div class="dashhead-toolbar-item">
                                    <span class="navbar-text">
                                <strong>${labels['last.refreshed']}:&nbsp;</strong>
                                ${organization.dashboard.lastRefreshedOn?date?string.long} - ${organization.dashboard.lastRefreshedOn?time?string.medium}
                            </span>
                                </div>
                                <div class="dashhead-toolbar-item">
                                    <button type="button" id="refresh" name="refresh" class="btn bg-transparent" data-toggle="tooltip" data-placement="top" title="${labels['refresh']}"><i class="fa fa-sync"></i></button>
                                </div>
                            </div>
                        </div>
                    </div>
                    

                </div>

            -->

        </div>
        <script type="text/javascript" src="/js/organization.js"></script>
    </@t.page>