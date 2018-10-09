<#macro page>

    <!DOCTYPE html>
    <html lang="en">

    <head>
        <title>${messages["application.title"]}</title>
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">

        <!-- Font Awesome -->
        <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/font-awesome/4.7.0/css/font-awesome.min.css">
        <!-- Bootstrap core CSS -->
        <link href="/css/bootstrap.min.css" rel="stylesheet">
        <!-- Material Design Bootstrap -->
        <link href="/css/mdb.min.css" rel="stylesheet">
        <!-- Custom styles -->
        <link href="/css/custom.css" rel="stylesheet">
    </head>

    <body>

        <header>
            <div class="container-fluid">
                <a href="/"><h3>${messages["application.title"]}</h3></a>
            </div>
        </header>
        <main>
        <#nested/>
        </main>    
        <footer>    
            <div class="container-fluid">
                <div class="text-center">
                    ${messages["copyright"]}
                </div>
            </div>
        </footer>

        <!-- JQuery -->
        <script type="text/javascript" src="/js/jquery-3.2.1.min.js"></script>
        <!-- Bootstrap tooltips -->
        <script type="text/javascript" src="/js/popper.min.js"></script>
        <!-- Bootstrap core JavaScript -->
        <script type="text/javascript" src="/js/bootstrap.min.js"></script>
        <!-- MDB core JavaScript -->
        <script type="text/javascript" src="/js/mdb.min.js"></script>

    </body>

    </html>
</#macro>