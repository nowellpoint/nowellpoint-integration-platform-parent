<#import "template.html" as t>
    <@t.page>
            <div class="container-fluid">  
            <div class="card-columns m-5">     
                <div class="card">
                    <div class="card-body">
                        <h4>${labels['salesforce.information']}</h4>
                        <hr>
                        <dl class="dl-horizontal">
                        <dt>${labels['organization.name']}</dt>
                        <dl class="text-muted">Nowellpoint</dl>
                        <dt>${labels['connected.user']}</dt>
                        <dl class="text-muted">service.user@nowellpoint.com</dl>
                        <dt>${labels['instance.url']}</dt>
                        <dl class="text-muted">https://na45.salesforce.com</dl>
                        </dl>
                    </div>
                    <div class="card-footer bg-transparent"><i class="fa fa-arrow-right fa-lg p-1"></i>&nbsp;${labels['change.connected.user']}</div>
                </div>
            </div>    
        </div>
    </@t.page>