<!-- navbar -->
<nav class="navbar navbar-expand-lg navbar-dark unique-color fixed-top scrolling-navbar">
    <button id="toggleSidebar" type="button" class="btn btn-link"><i class="fa fa-bars fa-1x text-white"></i></button>&emsp;
    <span class="navbar-brand">${messages["application.title"]}</span>
    <div class="collapse navbar-collapse" id="navbarContent">
        <ul class="navbar-nav mr-auto">
            
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