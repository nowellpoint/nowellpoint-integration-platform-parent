<#import "template.html" as t>
    <@t.page>
        <#include "sidebar.ftl" />
        <div id="content">
            <div class="container-fluid mt-2 pt-3 pr-5 pl-5 mb-2">
                <div class="dashhead">
                    <div class="dashhead-titles">
                        <h6 class="dashhead-subtitle">&nbsp;</h6>
                        <h4 class="dashhead-title font-weight-normal">${labels["organization"]}</h4>
                    </div>
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
            <div class="container-fluid pr-5 pl-5 mb-2">
                <div class="card-deck">
                    <#include "organization-information.ftl" />
                    <#include "organization-subscription.ftl" />
                    <#include "organization-billing-address.ftl" />
                </div>
            </div>
            <div class="container-fluid pr-5 pl-5 mb-2">
                <div class="card w-100">
                    <div class="card-body">
                        <div class="row">
                            <div class="col">
                                <div class="statcard text-center p-1">
                                    <h4 class="statcard-number">${organization.dashboard.customObjectCount}</h4>
                                    <span class="statcard-desc">${labels["custom.objects"]}</span>
                                </div>
                            </div>
                            <div class="col">
                                <div class="statcard text-center p-1">
                                    <h4 class="statcard-number">${organization.dashboard.apexClassCount}</h4>
                                    <span class="statcard-desc">${labels["apex.classes"]}</span>
                                </div>
                            </div>
                            <div class="col">
                                <div class="statcard text-center p-1">
                                    <h4 class="statcard-number">${organization.dashboard.apexTriggerCount}</h4>
                                    <span class="statcard-desc">${labels["apex.triggers"]}</span>
                                </div>
                            </div>
                            <div class="col">
                                <div class="statcard text-center p-1">
                                    <h4 class="statcard-number">${organization.dashboard.recordTypeCount}</h4>
                                    <span class="statcard-desc">${labels["record.types"]}</span>
                                </div>
                            </div>
                            <div class="col">
                                <div class="statcard text-center p-1">
                                    <h4 class="statcard-number">${organization.dashboard.userRoleCount}</h4>
                                    <span class="statcard-desc">${labels["user.roles"]}</span>
                                </div>
                            </div>
                            <div class="col">
                                <div class="statcard text-center p-1">
                                    <h4 class="statcard-number">${organization.dashboard.profileCount}</h4>
                                    <span class="statcard-desc">${labels["profiles"]}</span>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="container-fluid pr-5 pl-5 mb-2">
                <div class="card w-100">
                    <div class="card-body">
                        <h5 class="card-title">${labels['user.licenses']}</h5>
                        <table class="table table-sm">
                            <tbody>
                                <caption>${labels['active.licenses']}</caption>
                                <#list organization.dashboard.userLicenses?sort_by("name") as license>
                                    <#if license.status=="Active">
                                        <tr class="d-flex">
                                            <td class="col-6">${license.name}</td>
                                            <td class="col-4">
                                                <#if license.percentAvailable lt 6>
                                                    <div class="p2 bg-danger text-white text-center border border-danger rounded"><strong>${license.availableLicenses} of ${license.totalLicenses}</strong></div>
                                                <#elseif license.percentAvailable gt 10>
                                                    <div class="p2 bg-success text-white text-center border border-success rounded"><strong>${license.availableLicenses} of ${license.totalLicenses}</strong></div>
                                                <#else>
                                                    <div class="p2 bg-warning text-white text-center border border-warning rounded"><strong>${license.availableLicenses} of ${license.totalLicenses}</strong></div>
                                                </#if>
                                            </td>
                                            <td class="col-2 text-right">${license.percentAvailable}%</td>
                                        </tr>
                                    </#if>
                                </#list>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div> 
        <script type="text/javascript" src="/js/organization.js"></script>
    </@t.page>
