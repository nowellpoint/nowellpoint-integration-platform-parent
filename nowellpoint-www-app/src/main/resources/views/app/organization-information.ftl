<hr>
<div class="row">
    <div class="col-3">
        <span class="text-muted">${labels["name"]}</span>
    </div>
    <div class="col-9">
        ${organization.name}
    </div>
</div>
<hr>
<div class="row">
    <div class="col-3">
        <span class="text-muted">${labels["account.number"]}</span>
    </div>
    <div class="col-9">
        ${organization.number}
    </div>
</div>
<hr> 
<div class="row">
    <div class="col-3">
        <span class="text-muted">${labels["domain"]}</span>
    </div>
    <div class="col-9">
        ${organization.domain}
    </div>
</div>
<hr>
<div class="row">
    <div class="col-3">
        <span class="text-muted">${labels["created.on"]}</span>
    </div>
    <div class="col-9">
        ${organization.createdOn?date?string.long} - ${organization.createdOn?time?string.medium}
    </div>
</div>
<hr>