<hr>
<div class="row">
    <#if notifications?size==0>
        <div class="col-12">
            ${labels['empty.notification.list']}
        </div>    
    <#else>
        <#list notifications?sort_by( "receivedOn") as notification>
            <div class="col-2">
                <span class="text-muted">${notification.receivedFrom}</span>
            </div>
            <div class="col-2">
                <span class="text-muted">${notification.receivedOn?date?string.long} - ${notification.receivedOn?time?string.medium}</span>
            </div>
            <div class="col-2">
                <span class="text-muted">${notification.subject}</span>
            </div>
            <div class="col-4">
                <span class="text-muted">${notification.message}</span>
            </div>
            <div class="col-2">
                <#if notification.isUrgent>
                    <i class="fa fa-arrow-alt-circle-left text-danger"></i>
                <#else>
                    &nbsp;
                </#if>
            </div>
            <hr>    
        </#list>
    </#if>
</div>
<hr>