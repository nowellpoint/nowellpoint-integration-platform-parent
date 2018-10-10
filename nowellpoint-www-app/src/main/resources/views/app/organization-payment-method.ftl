<div class="row">
    <div class="col-3">
        <span class="grey-text">${labels["card.type"]}</span>
    </div>
    <div class="col-9">
        <#if organization.subscription.creditCard??><img src="${(organization.subscription.creditCard.imageUrl)!}" height="24" width="36" />&emsp;</#if>${(organization.subscription.creditCard.cardType)!} <#if organization.subscription.creditCard??>${labels["ending.in"]}</#if> ${(organization.subscription.creditCard.lastFour)!}
    </div>
</div>
<hr>
<div class="row">
    <div class="col-3">
        <span class="grey-text">${labels["name.on.card"]}</span>
    </div>
    <div class="col-9">
        ${(organization.subscription.creditCard.cardholderName)!}
    </div>
</div>
<hr>
<div class="row">
    <div class="col-3">
        <span class="grey-text">${labels["expires.on"]}</span>
    </div>
    <div class="col-9">
        ${(organization.subscription.creditCard.expirationMonth)!}<#if organization.subscription.creditCard??>/</#if>${(organization.subscription.creditCard.expirationYear)!}
    </div>
</div>
<hr>