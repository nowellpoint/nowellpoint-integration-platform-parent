<div class="card w-100">
    <div class="card-body">
        <div class="row">
            <div class="col">
                <div class="statcard text-center p-1">
                    <h4 class="statcard-number">${organization.dashboard.customObject.absoluteValue}
                        <#if organization.dashboard.customObject.delta gt 0>
                            <small class="delta-indicator delta-positive">${organization.dashboard.customObject.delta}</small>
                        </#if>
                        <#if organization.dashboard.customObject.delta lt 0>
                            <small class="delta-indicator delta-negative">${organization.dashboard.customObject.delta}</small>
                        </#if>
                    </h4>
                    <span class="statcard-desc">${labels["custom.objects"]}</span>
                </div>
            </div>
            <div class="col">
                <div class="statcard text-center p-1">
                    <h4 class="statcard-number">${organization.dashboard.apexClass.absoluteValue}</h4>
                    <#if organization.dashboard.apexClass.delta gt 0>
                        <small class="delta-indicator delta-positive">${organization.dashboard.apexClass.delta}</small>
                    </#if>
                    <#if organization.dashboard.apexClass.delta lt 0>
                        <small class="delta-indicator delta-negative">${organization.dashboard.apexClass.delta}</small>
                    </#if>
                    <span class="statcard-desc">${labels["apex.classes"]}</span>
                </div>
            </div>
            <div class="col">
                <div class="statcard text-center p-1">
                    <h4 class="statcard-number">${organization.dashboard.apexTrigger.absoluteValue}</h4>
                    <#if organization.dashboard.apexClass.delta gt 0>
                        <small class="delta-indicator delta-positive">${organization.dashboard.apexTrigger.delta}</small>
                    </#if>
                    <#if organization.dashboard.apexClass.delta lt 0>
                        <small class="delta-indicator delta-negative">${organization.dashboard.apexTrigger.delta}</small>
                    </#if>
                    <span class="statcard-desc">${labels["apex.triggers"]}</span>
                </div>
            </div>
            <div class="col">
                <div class="statcard text-center p-1">
                    <h4 class="statcard-number">${organization.dashboard.recordType.absoluteValue}</h4>
                    <#if organization.dashboard.apexClass.delta gt 0>
                        <small class="delta-indicator delta-positive">${organization.dashboard.recordType.delta}</small>
                    </#if>
                    <#if organization.dashboard.apexClass.delta lt 0>
                        <small class="delta-indicator delta-negative">${organization.dashboard.recordType.delta}</small>
                    </#if>
                    <span class="statcard-desc">${labels["record.types"]}</span>
                </div>
            </div>
            <div class="col">
                <div class="statcard text-center p-1">
                    <h4 class="statcard-number">${organization.dashboard.userRole.absoluteValue}</h4>
                    <#if organization.dashboard.apexClass.delta gt 0>
                        <small class="delta-indicator delta-positive">${organization.dashboard.userRole.delta}</small>
                    </#if>
                    <#if organization.dashboard.apexClass.delta lt 0>
                        <small class="delta-indicator delta-negative">${organization.dashboard.userRole.delta}</small>
                    </#if>
                    <span class="statcard-desc">${labels["user.roles"]}</span>
                </div>
            </div>
            <div class="col">
                <div class="statcard text-center p-1">
                    <h4 class="statcard-number">${organization.dashboard.profile.absoluteValue}</h4>
                    <#if organization.dashboard.apexClass.delta gt 0>
                        <small class="delta-indicator delta-positive">${organization.dashboard.profile.delta}</small>
                    </#if>
                    <#if organization.dashboard.apexClass.delta lt 0>
                        <small class="delta-indicator delta-negative">${organization.dashboard.profile.delta}</small>
                    </#if>
                    <span class="statcard-desc">${labels["profiles"]}</span>
                </div>
            </div>
        </div>
    </div>
</div>