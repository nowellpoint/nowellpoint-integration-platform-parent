<hr>
<#if notifications?size gt 0>
    <#list notifications as notification>
        <div class="row">
            <div class="col-2">
                <span class="text-muted">${notification.receivedFrom}</span>
            </div>
            <div class="col-2">
                <span class="text-muted">${notification.receivedOn?date?string.long} - ${notification.receivedOn?time?string.medium}</span>
            </div>
            <div class="col-2">
                <span class="text-muted">${notification.subject}</span>
            </div>
            <div class="col-5">
                <span class="text-muted">${notification.message}</span>
            </div>
            <div class="col-1">
                ${notification.isUrgent?then( '<i class="fa fa-arrow-alt-circle-left text-danger"></i>', '')}
            </div>
        </div>    
        <hr>    
    </#list>
<#else>
    <div class="row">
        <div class="col-12">
            ${labels['empty.notification.list']}
        </div>    
    </div>  
    <hr>
</#if>