<div class="row">
    <div class="col-3">
        ${organization.subscription.planName}
    </div>
    <div class="col-3">
        ${organization.subscription.currencySymbol}${organization.subscription.unitPrice?string["0.00"]}
    </div>
    <div class="col-3">
        <#if organization.subscription.nextBillingDate??>
            ${organization.subscription.nextBillingDate?date?string.long}
        <#else>
            ${labels["no.billdate"]}
        </#if>
    </div>
     <div class="col-3">
        <a href="${ORGANIZATION_URI}/${organization.id}/plans/" class="pull-right">${labels["change.plan"]}</a>
    </div>    
</div>
<hr>