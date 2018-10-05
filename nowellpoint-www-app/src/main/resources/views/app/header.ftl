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
                    <a class="nav-link" href="${DASHBOARD_URI}">${messages["dashboard"]}</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="${links['connectors']}">${messages["connectors"]}</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="${links['jobs']}">${messages["jobs"]}</a>
                </li>
            </ul>
            <!-- navbar menu --> 
            <ul class="nav navbar-nav navbar-right">    
                <li class="nav-item">
                    <a class="nav-link" href="${ORGANIZATION_URI}">${(identity.organization.name)!}</a>
                </li> 
                <li class="nav-item dropdown">
                    <a class="nav-link dropdown-toggle" id="navbarDropdownMenuLink" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                        ${identity.name}
                    </a>
                    <div class="dropdown-menu  dropdown-menu-right" aria-labelledby="navbarDropdownMenuLink">
                        <a class="dropdown-item" href="${IDENTITY_URI}">${messages["user.profile"]}</a>
                        <a class="dropdown-item" href="${LOGOUT_URI}">${messages["log.out"]}</a>
                    </div>
                </li>
            </ul>
            <!-- /navbar menu -->
        </div>
    </nav>
    <!-- /navbar -->
</header>
<!-- /header -->