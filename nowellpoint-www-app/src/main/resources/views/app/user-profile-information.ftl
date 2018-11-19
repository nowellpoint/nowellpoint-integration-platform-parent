<hr>
<div class="row">
    <div class="col-3">
        <span class="text-muted">${labels["name"]}</span>
    </div>
    <div class="col-9">
        ${identity.name!}
    </div>
</div>
<hr>
<div class="row">
    <div class="col-3">
        <span class="text-muted">${labels["email"]}</span>
    </div>
    <div class="col-9">
        ${identity.email!}
    </div>
</div>
<hr>
<div class="row">
    <div class="col-3">
        <span class="text-muted">${labels["locale.key"]}</span>
    </div>
    <div class="col-9">
        ${identity.localeDisplayName!}
    </div>
</div>
<hr>
<div class="row">
    <div class="col-3">
        <span class="text-muted">${labels["timezone"]}</span>
    </div>
    <div class="col-9">
        ${identity.timeZone!}
    </div>
</div>
<hr>