<!-- navbar -->
<nav class="navbar navbar-expand-lg navbar-dark unique-color fixed-top scrolling-navbar">
    <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarContent" aria-controls="navbarContent" aria-expanded="false" aria-label="${messages['toggle.navigation']}">
        <span class="navbar-toggler-icon"></span>
    </button>
    <span class="navbar-brand">${messages["application.title"]}</span>
    <div class="collapse navbar-collapse" id="navbarContent">
        <ul class="navbar-nav mr-auto">
            <li class="nav-item">
                <a class="nav-link" href="${START_URI}">
                    <i class="fa fa-star"></i><span class="clearfix d-none d-sm-inline-block">&nbsp;${messages["start"]}</span>
                </a>
            </li>
            <li class="nav-item">
                <a class="nav-link" href="${DASHBOARD_URI}">
                    <i class="fa fa-dashboard"></i><span class="clearfix d-none d-sm-inline-block">&nbsp;${messages["dashboard"]}</span></a>
            </li>
        </ul>
        <!-- navbar menu -->
        <ul class="navbar-nav navbar-right">
            <li class="nav-item">
                <a class="nav-link text-white" href="${ORGANIZATION_URI}">${(identity.organization.name)!}</a>
            </li>
            <li class="nav-item">
                <a class="nav-link text-white" href="${USER_PROFILE_URI}">${(identity.name)!}</a>
            </li>
            <li class="nav-item">
                <a class="nav-link text-white" href="${LOGOUT_URI}">${messages["log.out"]}</a>
            </li>
        </ul>
        <!-- /navbar menu -->
    </div>
</nav>
<!-- /navbar -->