<div class="container-fluid pr-5 pl-5 mb-3">
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
                    ${labels['percent']}
                </div>
            </div>
            <hr>

            <#list organization.dashboard.userLicenses?sort_by( "name") as license>
                <#if license.status=="Active">
                    <div class="row">
                        <div class="col-4">
                            <span class="text-muted">${license.name}</span>
                        </div>
                        <div class="col-2 text-right">
                            <span class="text-muted">${license.used}</span>
                        </div>
                        <div class="col-2 text-right">
                            <span class="text-muted">${license.available}</span>
                        </div>
                        <div class="col-2 text-right">
                            <span class="text-muted">${license.max}</span>
                        </div>
                        <div class="col-2 text-right">
                            <#if license.percentAvailable lt 6>
                            <span class="text-danger">${license.percentAvailable}&#37;</span>
                        <#elseif license.percentAvailable gt 10>
                            <span class="text-success">${license.percentAvailable}&#37;</span>
                        <#else>  
                            <span class="text-warning">${license.percentAvailable}&#37;</span>
                        </#if>
                            
                        </div>
                    </div>
                    <hr>
                </#if>
            </#list>
        </div>
    </div>
</div>