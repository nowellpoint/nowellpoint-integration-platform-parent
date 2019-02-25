<nav id="sidebar" class="border-right">  
    <ul class="nav nav-stacked nav-bordered flex-md-column pt-4">
        <li class="nav-item">
                <a class="nav-link" href="${START_URI}">
                    <span class="clearfix d-none d-sm-inline-block">&emsp;${messages["start"]}</span>
                </a>
        </li>
        <li class="nav-item">
            <a class="nav-link" href="${ORGANIZATION_URI}">
                <span class="clearfix d-none d-sm-inline-block">&emsp;${messages["organization"]}</span></a>
        </li>
        <li class="nav-header">${messages["streaming.events"]}</li>
        <li class="nav-item">
            <a class="nav-link" href="${STREAMING_EVENTS_URI}">
                <span class="clearfix d-none d-sm-inline-block">&emsp;${messages["dashboard"]}</span></a>
        </li>
        <li class="nav-item">
            <a class="nav-link" href="${STREAMING_EVENTS_SOURCES_URI}">
                <span class="clearfix d-none d-sm-inline-block">&emsp;${messages["sources"]}</span></a>
        </li>
        <li class="nav-item">
            <a class="nav-link" href="${NOTIFICATIONS_URI}">
                <span class="clearfix d-none d-sm-inline-block">&emsp;${messages["notifications"]}</span></a>
        </li>
    </ul>
</nav>