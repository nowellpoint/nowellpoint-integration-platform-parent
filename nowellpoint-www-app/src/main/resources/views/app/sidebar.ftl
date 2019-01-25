<div id="sidebar" class="sidebar">
    <div class="container-fluid pt-4 pb-2 pr-3 pl-3">
        <div class="dashhead">
                    <div class="dashhead-titles">
                        <h4 class="dashhead-title font-weight-normal">&nbsp;</h4>
                    </div>
                    <div class="dashhead-toolbar">

                    </div>
                </div>
    </div>    
    <ul class="nav nav-stacked flex-md-column nav-flex-icons border-top">
        <li class="nav-item">
                <a class="nav-link" href="${START_URI}">
                    &emsp;<i class="fa fa-star fa-1x"></i><span class="clearfix d-none d-sm-inline-block">&emsp;${messages["start"]}</span>
                </a>
        </li>
        <li class="nav-item">
            <a class="nav-link" href="${DASHBOARD_URI}">
                &emsp;<i class="fa fa-dashboard fa-1x"></i><span class="clearfix d-none d-sm-inline-block">&emsp;${messages["dashboard"]}</span></a>
        </li>
        <li class="nav-header">&emsp;${messages["organization"]}</li>
        <li class="nav-item">
            <a class="nav-link" href="${ORGANIZATION_STREAMING_EVENTS_URI}">
                &emsp;<i class="fa fa-exchange fa-1x"></i><span class="clearfix d-none d-sm-inline-block">&emsp;${messages["streaming.events"]}</span></a>
        </li>
    </ul>
</div>