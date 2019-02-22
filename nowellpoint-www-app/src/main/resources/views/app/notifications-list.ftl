<hr>
<div class="row">
    <#if organization.notifications?size==0>
        <div class="col-12">
            ${labels['empty.notification.list']}
        </div>    
    <#else>
        <#list organization.notifications?sort_by( "receivedDate") as notification>
            <div class="col-3">
                <span class="text-muted">${notification.receivedOn?date?string.long} - ${notification.receivedOn?time?string.medium}</span>
            </div>
            <div class="col-3">
                <span class="text-muted">${notification.subject}</span>
            </div>
            <div class="col-3">
                <span class="text-muted">${notification.body}</span>
            </div>
            <div class="col-3">
                <#if notification.urgent>
                    <i class="fa fa-arrow-alt-circle-left text-danger"></i>
                <#else>
                    &nbsp;
                </#if>
            </div>
        </#list>
    </#if>
</div>
<hr>