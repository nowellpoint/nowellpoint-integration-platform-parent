<!DOCTYPE HTML>
<html lang="en">

<head>

    <title>${messages["application.title"]}</title>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
        
    <link rel="shortcut icon" type="image/x-icon" href="/favicon.ico" />

    <!-- Font Awesome -->
    <link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.7.1/css/all.css" integrity="sha384-fnmOCqbTlWIlj8LyTjo7mOUStjsKC4pOpQbqyi7RrhN7udi9RwhKkMHpvLbHG9Sr" crossorigin="anonymous">
    <!-- Bootstrap core CSS -->
    <link href="/css/bootstrap.min.css" rel="stylesheet">

    <!-- Bootstrap theme -->
    <link href="/css/toolkit-light.min.css" rel="stylesheet">
    <!-- Bootstrap toggle -->
    <link href="https://cdn.jsdelivr.net/gh/gitbrent/bootstrap4-toggle@3.4.0/css/bootstrap4-toggle.min.css" rel="stylesheet">
    <!-- Custom styles -->
    <link href="/css/custom.css" rel="stylesheet">
        
    <!-- JQuery -->
    <script type="text/javascript" src="/js/jquery-3.2.1.min.js"></script>
    <!-- Bootstrap tooltips -->
    <script type="text/javascript" src="/js/popper.min.js"></script>
    <!-- Bootstrap core JavaScript -->
    <script type="text/javascript" src="/js/bootstrap.min.js"></script>
    <!-- Bootstrap toggle JavaScript -->
    <script src="https://cdn.jsdelivr.net/gh/gitbrent/bootstrap4-toggle@3.4.0/js/bootstrap4-toggle.min.js"></script>
    <!-- Custom JavaScript -->
    <script type="text/javascript" src="/js/common.js"></script>

</head>

<body>
    <main>
        <nav class="navbar navbar-expand-lg navbar-dark unique-color-dark fixed-top scrolling-navbar">
            <button id="toggleSidebar" type="button" class="btn btn-link"><i class="fa fa-bars fa-1x text-white"></i></button>&emsp;
            <span class="navbar-brand">${messages["application.title"]}</span>
            <div class="collapse navbar-collapse" id="navbarContent"></div>
        </nav>
        <div id="content">
            <div class="row">
                <div>
                    <br>
                    <#if errorMessage??>
                        <div id="error" class="alert alert-danger">
                            <div class="text-center">${errorMessage}</div>
                        </div>
                    </#if>
                </div>
            </div>
        </div>
    </main>
</body>

</html>