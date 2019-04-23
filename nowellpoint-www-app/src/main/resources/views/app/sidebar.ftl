<nav id="sidebar" class="border-right">  
    <ul class="nav nav-stacked nav-bordered flex-md-column pl-2 pt-4">
        <li class="nav-item">
            <a class="nav-link ${START?then('active','')}" href="${START_URI}">${messages["start"]}</a>
        </li>
        <li class="nav-item">
            <a class="nav-link ${ORGANIZATION?then('active','')}" href="${ORGANIZATION_URI}">${messages["organization"]}</a>
        </li>
        <li class="nav-item">
            <a class="nav-link ${EVENT_STREAMS?then('active','')}" href="${EVENT_STREAMS_URI}">${messages["event.streams"]}</a>
        </li>
        <li class="nav-item">
            <a class="nav-link ${NOTIFICATIONS?then('active','')}" href="${NOTIFICATIONS_URI}">${messages["notifications"]}</a>
        </li>
    </ul>
</nav>