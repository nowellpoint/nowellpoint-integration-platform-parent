<div class="container-fluid pr-5 pl-5 mb-3">
    <div class="card w-100">
        <div class="card-body">
            <h5 class="card-title">${labels['metadata']}</h5>
            <hr>
            <div class="row mb-1">
                <div class="col-2">
                    <span>${labels["custom.objects"]}</span>
                </div>
                <div class="col-1 text-right">
                    <span class="text-muted">${organization.dashboard.customObject.absoluteValue}</span>
                    <#if organization.dashboard.customObject.delta gt 0>
                        <small class="delta-indicator delta-positive">${organization.dashboard.customObject.delta}</small>
                    </#if>
                    <#if organization.dashboard.customObject.delta lt 0>
                        <small class="delta-indicator delta-negative">${organization.dashboard.customObject.delta}</small>
                    </#if>
                </div>
                <div class="col-3"></div>
                <div class="col-2">
                    <span>${labels["record.types"]}</span>
                </div>    
                <div class="col-1 text-right">
                    <span class="text-muted">${organization.dashboard.recordType.absoluteValue}</span>
                    <#if organization.dashboard.customObject.delta gt 0>
                        <small class="delta-indicator delta-positive">${organization.dashboard.recordType.delta}</small>
                    </#if>
                    <#if organization.dashboard.customObject.delta lt 0>
                        <small class="delta-indicator delta-negative">${organization.dashboard.recordType.delta}</small>
                    </#if>
                </div>
                <div class="col-3"></div>
            </div>    
            <div class="row mb-1">
                <div class="col-2">
                    <span>${labels["apex.classes"]}</span>
                </div>
                <div class="col-1 text-right">
                    <span class="text-muted">${organization.dashboard.apexClass.absoluteValue}</span>
                    <#if organization.dashboard.customObject.delta gt 0>
                        <small class="delta-indicator delta-positive">${organization.dashboard.apexClass.delta}</small>
                    </#if>
                    <#if organization.dashboard.customObject.delta lt 0>
                        <small class="delta-indicator delta-negative">${organization.dashboard.apexClass.delta}</small>
                    </#if>
                </div>
                <div class="col-3"></div>
                <div class="col-2">
                    <span>${labels["user.roles"]}</span>
                </div>    
                <div class="col-1 text-right">
                    <span class="text-muted">${organization.dashboard.userRole.absoluteValue}</span>
                    <#if organization.dashboard.customObject.delta gt 0>
                        <small class="delta-indicator delta-positive">${organization.dashboard.userRole.delta}</small>
                    </#if>
                    <#if organization.dashboard.customObject.delta lt 0>
                        <small class="delta-indicator delta-negative">${organization.dashboard.userRole.delta}</small>
                    </#if>
                </div>
                <div class="col-3"></div>
            </div>    
            <div class="row">
                <div class="col-2">
                    <span>${labels["apex.triggers"]}</span>
                </div>
                <div class="col-1 text-right">
                    <span class="text-muted">${organization.dashboard.apexTrigger.absoluteValue}</span>
                    <#if organization.dashboard.customObject.delta gt 0>
                        <small class="delta-indicator delta-positive">${organization.dashboard.apexTrigger.delta}</small>
                    </#if>
                    <#if organization.dashboard.customObject.delta lt 0>
                        <small class="delta-indicator delta-negative">${organization.dashboard.apexTrigger.delta}</small>
                    </#if>
                </div>
                <div class="col-3"></div>
                <div class="col-2">
                    <span>${labels["profiles"]}</span>
                </div>    
                <div class="col-1 text-right">
                    <span class="text-muted">${organization.dashboard.profile.absoluteValue}</span>
                    <#if organization.dashboard.customObject.delta gt 0>
                        <small class="delta-indicator delta-positive">${organization.dashboard.profile.delta}</small>
                    </#if>
                    <#if organization.dashboard.customObject.delta lt 0>
                        <small class="delta-indicator delta-negative">${organization.dashboard.profile.delta}</small>
                    </#if>
                </div>
                <div class="col-3"></div>
            </div>    
        </div>
    </div>    
</div>