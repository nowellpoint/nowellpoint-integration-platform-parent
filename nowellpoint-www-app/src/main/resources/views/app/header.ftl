<!-- header -->
<header>
    <!-- navbar -->
    <nav class="navbar navbar-default navbar-expand-lg navbar-light white fixed-top scrolling-navbar">
        <a href="#" id="toggleSidebar"><i class="fa fa-bars fa-2x p-1"></i></a>&emsp;
        <span class="navbar-brand mb-0 h1">${messages["application.title"]}</span>
        <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarContent" aria-controls="navbarContent" aria-expanded="false" aria-label="${messages['toggle.navigation']}">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarContent">
            <ul class="nav navbar-nav nav-flex-icons mr-auto">
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
            <ul class="nav navbar-nav navbar-right">    
                <li class="nav-item">
                    <a class="nav-link" href="${ORGANIZATION_URI}">${(identity.organization.name)!}</a>
                </li> 
                <li class="nav-item">
                    <a class="nav-link" href="${USER_PROFILE_URI}">${(identity.name)!}</a>
                </li> 
                <li class="nav-item">
                    <a class="nav-link" href="${LOGOUT_URI}">${messages["log.out"]}</a>
                </li> 
            </ul>
            <!-- /navbar menu -->
        </div>
    </nav>
    <!-- /navbar -->
</header>
<!-- /header -->