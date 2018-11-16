<#import "template.html" as t>
    <@t.page>
        <div class="container-fluid">  
            <div class="card-columns pt-3 m-2">     
                <div class="card">
                    <div class="card-body">
                        <h4>${labels['salesforce.information']}</h4>
                        <dl class="dl-horizontal">
                            <dt>${labels['organization.name']}</dt>
                            <dd class="text-muted">${organization.name}</dd>
                            <dt>${labels['connected.user']}</dt>
                            <dd class="text-muted">${organization.connection.connectedAs}</dd>
                            <dt>${labels['connected.at']}</dt>
                            <dd class="text-muted"><#if organization.connection.connectedAt??>${organization.connection.connectedAt?date?string.long} ${organization.connection.connectedAt?time?string.medium}<#else>&nbsp;</#if></dd>
                            <dt>${labels['instance.url']}</dt>
                            <dd class="text-muted">${organization.connection.instanceUrl}</dd>
                        </dl>
                    </div>
                    <div class="card-footer bg-transparent"><a href="${CHANGE_CONNECTED_USER_URI}"><i class="fa fa-arrow-right fa-lg p-1"></i>&nbsp;${labels['change.connected.user']}</a></div>
                </div>
                <div class="card">
                    <div class="card-body">
                        <h4>${labels['event.listeners']}</h4>
                        <span class="text-muted">${labels['configured.event.listeners']?replace(':size', organization.eventListeners?size)}</span>
                    </div>
                    <div class="card-footer bg-transparent"><a href="${ORGANIZATION_EVENT_LISTENERS_URI}"><i class="fa fa-arrow-right fa-lg p-1"></i>&nbsp;${labels['configure.event.listeners']}</a></div>
                </div>
            </div>    
        </div>
    </@t.page>