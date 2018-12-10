<div id="sidebar" class="sidebar">
    <ul class="nav nav-stacked flex-md-column pt-3">
        <li class="nav-header">${labels["standard.objects"]}</li>
        <#list organization.eventListeners?sort_by("name") as listener>
            <li class="nav-item">
                <a class="nav-link font-weight-normal" href="${listener.href}">
                    ${listener.name}
                </a>
            </li>
        </#list>
    </ul>
</div>