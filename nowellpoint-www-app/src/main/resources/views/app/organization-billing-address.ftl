<div class="row">
    <div class="col-3">
        <span class="grey-text">${labels["street"]}</span>
    </div>
    <div class="col-9">
        ${(organization.subscription.billingAddress.street)!}
    </div>
</div>
<hr>
<div class="row">
    <div class="col-3">
        <span class="grey-text">${labels["city"]}</span>
    </div>
    <div class="col-9">
        ${(organization.subscription.billingAddress.city)!}
    </div>
</div>
<hr>
<div class="row">
    <div class="col-3">
        <span class="grey-text">${labels["state"]}</span>
    </div>
    <div class="col-9">
        ${(organization.subscription.billingAddress.state)!}
    </div>
</div>
<hr>
<div class="row">
    <div class="col-3">
        <span class="grey-text">${labels["postal.code"]}</span>
    </div>
    <div class="col-9">
        ${(organization.subscription.billingAddress.postalCode)!}
    </div>
</div>
<hr>
<div class="row">
    <div class="col-3">
        <span class="grey-text">${labels["country"]}</span>
    </div>
    <div class="col-9">
        ${(organization.subscription.billingAddress.country)!}
    </div>
</div>
<hr>

<div class="card">
    <div class="card-body">
        <dl class="dl-vertical">
            <h5 class="card-title">${labels['billing.address']}</h5>
            <dt>${labels['street']}</dt>
            <dd>${(organization.subscription.billingAddress.street)!}&nbsp;</dd>
            <dt>${labels['city']}</dt>
            <dd>${(organization.subscription.billingAddress.city)!}&nbsp;</dd>
            <dt>${labels['state']}</dt>
            <dd>${(organization.subscription.billingAddress.state)!}&nbsp;</dd>
            <dt>${labels['postal.code']}</dt>
            <dd>${(organization.subscription.billingAddress.postalCode)!}&nbsp;</dd>
            <dt>${labels['country']}</dt>
            <dd>${(organization.subscription.billingAddress.country)!}&nbsp;</dd>
        </dl>
    </div>
    <div class="card-footer bg-transparent">
        &emsp;
    </div>    
</div>