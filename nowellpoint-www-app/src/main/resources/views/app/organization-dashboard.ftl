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
                    <h4 class="statcard-number">${organization.dashboard.apexClass.absoluteValue}
                        <#if organization.dashboard.apexClass.delta gt 0>
                            <small class="delta-indicator delta-positive">${organization.dashboard.apexClass.delta}</small>
                        </#if>
                        <#if organization.dashboard.apexClass.delta lt 0>
                            <small class="delta-indicator delta-negative">${organization.dashboard.apexClass.delta}</small>
                        </#if>
                    </h4>
                    <span class="statcard-desc">${labels["apex.classes"]}</span>
                </div>
            </div>
            <div class="col">
                <div class="statcard text-center p-1">
                    <h4 class="statcard-number">${organization.dashboard.apexTrigger.absoluteValue}
                        <#if organization.dashboard.apexTrigger.delta gt 0>
                            <small class="delta-indicator delta-positive">${organization.dashboard.apexTrigger.delta}</small>
                        </#if>
                        <#if organization.dashboard.apexTrigger.delta lt 0>
                            <small class="delta-indicator delta-negative">${organization.dashboard.apexTrigger.delta}</small>
                        </#if>
                    </h4>
                    <span class="statcard-desc">${labels["apex.triggers"]}</span>
                </div>
            </div>
            <div class="col">
                <div class="statcard text-center p-1">
                    <h4 class="statcard-number">${organization.dashboard.recordType.absoluteValue}
                        <#if organization.dashboard.recordType.delta gt 0>
                            <small class="delta-indicator delta-positive">${organization.dashboard.recordType.delta}</small>
                        </#if>
                        <#if organization.dashboard.recordType.delta lt 0>
                            <small class="delta-indicator delta-negative">${organization.dashboard.recordType.delta}</small>
                        </#if>
                    </h4>
                    <span class="statcard-desc">${labels["record.types"]}</span>
                </div>
            </div>
            <div class="col">
                <div class="statcard text-center p-1">
                    <h4 class="statcard-number">${organization.dashboard.userRole.absoluteValue}
                        <#if organization.dashboard.userRole.delta gt 0>
                            <small class="delta-indicator delta-positive">${organization.dashboard.userRole.delta}</small>
                        </#if>
                        <#if organization.dashboard.userRole.delta lt 0>
                            <small class="delta-indicator delta-negative">${organization.dashboard.userRole.delta}</small>
                        </#if>
                    </h4>
                    <span class="statcard-desc">${labels["user.roles"]}</span>
                </div>
            </div>
            <div class="col">
                <div class="statcard text-center p-1">
                    <h4 class="statcard-number">${organization.dashboard.profile.absoluteValue}
                        <#if organization.dashboard.profile.delta gt 0>
                            <small class="delta-indicator delta-positive">${organization.dashboard.profile.delta}</small>
                        </#if>
                        <#if organization.dashboard.profile.delta lt 0>
                            <small class="delta-indicator delta-negative">${organization.dashboard.profile.delta}</small>
                        </#if>
                    </h4>
                    <span class="statcard-desc">${labels["profiles"]}</span>
                </div>
            </div>
        </div>
    </div>
</div>