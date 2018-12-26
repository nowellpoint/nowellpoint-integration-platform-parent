<div class="card">
    <div class="card-body">
        <dl class="dl-vertical">
            <h5 class="card-title">${labels['subscription']}</h5>
            <dt>${labels['plan.name']}</dt>
            <dd>${organization.subscription.planName}</dd>
            <dt>${labels['price']}</dt>
            <dd>${organization.subscription.currencySymbol}${organization.subscription.unitPrice?string["0.00"]}</dd>
            <dt>${labels['next.billing.date']}</dt>
            <dd>
                <#if organization.subscription.nextBillingDate??>
                    ${organization.subscription.nextBillingDate?date?string.long}
                    <#else>
                        ${labels["no.billdate"]}
                </#if>
            </dd>
        </dl>
    </div>
    <div class="card-footer bg-transparent">
        <a href="${ORGANIZATION_URI}/${organization.id}/plans/" style="text-decoration: none"><i class="fa fa-arrow-right fa-lg p-1"></i>&nbsp;${labels['change.plan']}</a>
    </div>    
</div>