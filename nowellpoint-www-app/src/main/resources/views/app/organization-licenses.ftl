<div class="container-fluid pr-3 pl-3 mb-3">
    <div class="card w-100">
        <div class="card-body">
            <h5 class="card-title">${labels['user.licenses']}</h5>
            <div class="row">
                <div class="col-4">
                    <span></span>
                </div>
                <div class="col-2 text-right">
                    ${labels['used']}
                </div>
                <div class="col-2 text-right">
                    ${labels['available']}
                </div>
                <div class="col-2 text-right">
                    ${labels['max']}
                </div>
                <div class="col-2 text-right">
                    &nbsp;
                </div>
            </div>
            <#if organization.dashboard.userLicenses?size == 0>
                <hr>
            </#if>
            <#list organization.dashboard.userLicenses?sort_by( "name") as license>
                <#if license.status=="Active">
                    <hr>
                    <div class="row">
                        <div class="col-4">
                            <span class="text-muted">${license.name}</span>
                        </div>
                        <div class="col-2 text-right">
                            <span class="text-muted">${license.used?string(",##0")}</span>
                        </div>
                        <div class="col-2 text-right">
                            <span class="text-muted">${license.available?string(",##0")}</span>
                        </div>
                        <div class="col-2 text-right">
                            <span class="text-muted">${license.max?string(",##0")}</span>
                        </div>
                        <div class="col-1"></div>    
                        <div class="col-1 text-right">
                            <@usage percent=license.percentAvailable max=license.max/>   
                        </div>
                    </div>
                </#if>
            </#list>
        </div>
    </div>
</div>