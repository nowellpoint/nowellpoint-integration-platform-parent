<div class="container-fluid pr-3 pl-3 mb-3">
    <div class="card w-100">
        <div class="card-body">
            <div class="row">
                <div class="col-4 text-center">
                    <div class="statcard p-3 border-right">
                        <h3 class="statcard-number">${organization.dashboard.apexClass.absoluteValue?string(",##0")}
                            <#if organization.dashboard.customObject.delta gt 0>
                                <small class="delta-indicator delta-positive">${organization.dashboard.apexClass.delta}</small>
                            </#if>
                            <#if organization.dashboard.customObject.delta lt 0>
                                <small class="delta-indicator delta-negative">${organization.dashboard.apexClass.delta}</small>
                            </#if>
                        </h3>
                        <span class="statcard-desc">${labels["apex.classes"]}</span>
                    </div>
                </div>
                <div class="col-4 text-center">
                    <div class="statcard p-3 border-right">
                        <h3 class="statcard-number">${organization.dashboard.apexTrigger.absoluteValue?string(",##0")}
                            <#if organization.dashboard.customObject.delta gt 0>
                                <small class="delta-indicator delta-positive">${organization.dashboard.apexTrigger.delta}</small>
                            </#if>
                            <#if organization.dashboard.customObject.delta lt 0>
                                <small class="delta-indicator delta-negative">${organization.dashboard.apexTrigger.delta}</small>
                            </#if>    
                        </h3>
                        <span class="statcard-desc">${labels["apex.triggers"]}</span>
                    </div>
                </div>
                <div class="col-4 text-center">
                    <div class="statcard p-3">
                        <h3 class="statcard-number">${organization.dashboard.customObject.absoluteValue?string(",##0")}
                    <#if organization.dashboard.customObject.delta gt 0>
                        <small class="delta-indicator delta-positive">${organization.dashboard.customObject.delta}</small>
                    </#if>
                    <#if organization.dashboard.customObject.delta lt 0>
                        <small class="delta-indicator delta-negative">${organization.dashboard.customObject.delta}</small>
                    </#if>    
                </h3>
                        <span class="statcard-desc">${labels["custom.objects"]}</span>
                    </div>
                </div>
            </div>
            <hr>
            <div class="row">
                <div class="col-4 text-center">
                    <div class="statcard p-3 border-right">
                        <h3 class="statcard-number">${organization.dashboard.profile.absoluteValue?string(",##0")}
                    <#if organization.dashboard.customObject.delta gt 0>
                        <small class="delta-indicator delta-positive">${organization.dashboard.profile.delta}</small>
                    </#if>
                    <#if organization.dashboard.customObject.delta lt 0>
                        <small class="delta-indicator delta-negative">${organization.dashboard.profile.delta}</small>
                    </#if>  
                </h3>
                        <span class="statcard-desc">${labels["profiles"]}</span>
                    </div>
                </div>
                <div class="col-4 text-center">
                    <div class="statcard p-3 border-right">
                        <h3 class="statcard-number">${organization.dashboard.userRole.absoluteValue?string(",##0")}
                            <#if organization.dashboard.customObject.delta gt 0>
                        <small class="delta-indicator delta-positive">${organization.dashboard.userRole.delta}</small>
                    </#if>
                    <#if organization.dashboard.customObject.delta lt 0>
                        <small class="delta-indicator delta-negative">${organization.dashboard.userRole.delta}</small>
                    </#if>
                </h3>
                        <span class="statcard-desc">${labels["user.roles"]}</span>
                    </div>
                </div>
                <div class="col-4 text-center">
                    <div class="statcard p-3">
                        <h3 class="statcard-number">${organization.dashboard.recordType.absoluteValue?string(",##0")}
                            <#if organization.dashboard.customObject.delta gt 0>
                                <small class="delta-indicator delta-positive">${organization.dashboard.recordType.delta}</small>
                            </#if>
                            <#if organization.dashboard.customObject.delta lt 0>
                                <small class="delta-indicator delta-negative">${organization.dashboard.recordType.delta}</small>
                            </#if>
                        </h3>
                        <span class="statcard-desc">${labels["record.types"]}</span>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>