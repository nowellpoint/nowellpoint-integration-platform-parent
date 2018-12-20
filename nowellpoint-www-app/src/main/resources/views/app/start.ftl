<#import "template.html" as t>
    <@t.page>
        <#include "sidebar.ftl" />
        <content>
            <div class="container-fluid mt-3 p-3">
                <div class="dashhead">
                    <div class="dashhead-titles">
                        <h6 class="dashhead-subtitle">${organization.name}</h6>
                        <h3 class="dashhead-title">${messages['start']}</h3>
                    </div>
                    <div class="dashhead-toolbar">

                    </div>
                </div>
                <br>
                <div class="card-columns">
                    <div class="card">
                        <div class="card-body">
                            <h4>${labels['salesforce.information']}</h4>
                            <dl class="dl-horizontal">
                                <dt>${labels['organization.name']}</dt>
                                <dd>${organization.name}</dd>
                                <dt>${labels['organization.type']}</dt>
                                <dd>${organization.organizationType}</dd>
                                <dt>${labels['connected.user']}</dt>
                                <dd>${organization.connection.connectedAs}</dd>
                                <dt>${labels['connected.at']}</dt>
                                <dd>
                                    <#if organization.connection.connectedAt??>${organization.connection.connectedAt?date?string.long} ${organization.connection.connectedAt?time?string.medium}<#else>&nbsp;</#if>
                                </dd>
                                <dt>${labels['instance.url']}</dt>
                                <dd>${organization.connection.instanceUrl}</dd>
                            </dl>
                        </div>
                        <div class="card-footer bg-transparent"><a href="${CHANGE_CONNECTED_USER_URI}" style="text-decoration: none"><i class="fa fa-arrow-right fa-lg p-1"></i>&nbsp;${labels['change.connected.user']}</a></div>
                    </div>
                    <div class="card">
                        <div class="card-body">
                            <h4>${labels['event.listeners']}</h4>
                            <span>${ORGANIZATION_EVENT_LISTENER_LABEL}</span>
                        </div>
                        <div class="card-footer bg-transparent"><a href="${ORGANIZATION_EVENT_LISTENERS_URI}" style="text-decoration: none"><i class="fa fa-arrow-right fa-lg p-1"></i>&nbsp;${labels['configure.event.listeners']}</a></div>
                    </div>
                </div>
                
            </div>
        </content>
    </@t.page>