<#import "template.html" as t>
    <@t.page>
        <#include "sidebar.ftl" />
        <div id="content">
            <div class="container-fluid pt-4 pb-2 pr-3 pl-3">
                <div class="dashhead">
                    <div class="dashhead-titles">
                        <h4 class="dashhead-title font-weight-normal">${messages['start']}</h4>
                    </div>
                    <div class="dashhead-toolbar">

                    </div>
                </div>
            </div>
            <div class="container-fluid border-top p-3">
                <div class="card-columns">
                    <div class="card">
                        <div class="card-body">
                            <h5 class="card-title">${labels['salesforce.information']}</h5>
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
                            <h5 class="card-title">${labels['streaming.event.listeners']}</h5>
                            <span>${STREAMING_EVENT_LISTENER_LABEL}</span>
                        </div>
                        <div class="card-footer bg-transparent"><a href="${STREAMING_EVENTS_URI}" style="text-decoration: none"><i class="fa fa-arrow-right fa-lg p-1"></i>&nbsp;${labels['configure.streaming.event.listeners']}</a></div>
                    </div>
                </div>
                
            </div>
        </div>
    </@t.page>