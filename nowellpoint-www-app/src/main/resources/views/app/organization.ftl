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
                <ul class="nav nav-bordered">
                    <li class="nav-item">
                        <a class="nav-link active" href="#details" data-toggle="tab">${labels["details"]}</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="#dashboard" data-toggle="tab">${labels["dashboard"]}</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="#limits" data-toggle="tab">${labels["limits"]}</a>
                    </li>
                </ul>
                <hr>
            </div>

            <div class="tab-content clearfix">
                <div class="tab-pane active" id="details">
                    <div class="container-fluid pr-5 pl-5 mb-3">
                        <div class="card-deck">
                            <#include "organization-information.ftl" />
                            <#include "organization-subscription.ftl" />
                            <#include "organization-billing-address.ftl" />
                        </div>
                    </div>
                </div>
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
                    <div class="container-fluid pr-5 pl-5 mb-3">
                        <#include "organization-dashboard.ftl" />
                    </div>
                    <div class="container-fluid pr-5 pl-5 mb-2">
                        <div class="card w-100">
                            <div class="card-body">
                                <h5 class="card-title">${labels['user.licenses']}</h5>
                                <table class="table table-sm">
                                    <tbody>
                                        <caption>${labels['active.licenses']}</caption>
                                        <#list organization.dashboard.userLicenses?sort_by( "name") as license>
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
                <#include "organization-limits.ftl" />
            </div>
        </div>
        <script type="text/javascript" src="/js/organization.js"></script>
    </@t.page>