<nav id="sidebar" class="border-right">  
    <ul class="nav nav-stacked nav-bordered flex-md-column pt-4">
        <li class="nav-item">
            <a class="nav-link ${START?then('active','')}" href="${START_URI}">${messages["start"]}</a>
        </li>
        <li class="nav-item">
            <a class="nav-link ${ORGANIZATION?then('active','')}" href="${ORGANIZATION_URI}">${messages["organization"]}</a>
        </li>
        <li class="nav-header">${messages["streaming.events"]}</li>
        <li class="nav-item">
            <a class="nav-link ${STREAMING_EVENTS?then('active','')}" href="${STREAMING_EVENTS_URI}">${messages["dashboard"]}</a>
        </li>
        <li class="nav-item">
            <a class="nav-link ${STREAMING_EVENTS_SOURCES?then('active','')}" href="${STREAMING_EVENTS_SOURCES_URI}">${messages["sources"]}</a>
        </li>
        <li class="nav-item">
            <a class="nav-link ${NOTIFICATIONS?then('active','')}" href="${NOTIFICATIONS_URI}">${messages["notifications"]}</a>
        </li>
    </ul>
</nav>