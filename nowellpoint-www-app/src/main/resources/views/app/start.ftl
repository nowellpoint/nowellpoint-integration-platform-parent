<#import "template.html" as t>
    <@t.page>
        <div class="container-fluid">  
            <div class="card-columns pt-3 m-2">     
                <div class="card">
                    <div class="card-body">
                        <h4>${labels['salesforce.information']}</h4>
                        <hr>
                        <dl class="dl-horizontal">
                        <dt>${labels['organization.name']}</dt>
                        <dl class="text-muted">${organization.name}</dl>
                        <dt>${labels['connected.user']}</dt>
                        <dl class="text-muted">${organization.connection.connectedAs}</dl>
                        <dt>${labels['connected.at']}</dt>
                        <dl class="text-muted"><#if organization.connection.connectedAt??>${organization.connection.connectedAt?date?string.long} ${organization.connection.connectedAt?time?string.medium}<#else>&nbsp;</#if></dl>
                        <dt>${labels['instance.url']}</dt>
                        <dl class="text-muted">${organization.connection.instanceUrl}</dl>
                        </dl>
                    </div>
                    <div class="card-footer bg-transparent"><a href="${CHANGE_CONNECTED_USER_URI}"><i class="fa fa-arrow-right fa-lg p-1"></i>&nbsp;${labels['change.connected.user']}</a></div>
                </div>
            </div>    
        </div>
    </@t.page>