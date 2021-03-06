<!DOCTYPE HTML>
<html lang="en">

<head>

    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta content='width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0' name='viewport' />
    <meta name="viewport" content="width=device-width" />
    <meta name="description" content="">
    <meta name="author" content="">

    <title>${messages["application.title"]}</title>

    <!-- Bootstrap Core CSS -->
    <link rel="stylesheet" href="/css/bootstrap.min.css" type="text/css">

    <!-- Include the CSS -->
    <link rel="stylesheet" href="/dist/toolkit-light.min.css" type="text/css">
    <link rel="stylesheet" href="/css/custom.css" type="text/css" />

    <!-- Font Awesome Icons -->
    <link rel="stylesheet" href="/font-awesome/css/font-awesome.min.css" type="text/css">

</head>

<body>
    <div class="container p-t-lg">
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
</body>
</html>