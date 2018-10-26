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
    
    <!-- JQuery -->
    <script type="text/javascript" src="/js/jquery-3.2.1.min.js"></script>
    <!-- Bootstrap tooltips -->
    <script type="text/javascript" src="/js/popper.min.js"></script>
    <!-- Bootstrap core JavaScript -->
    <script type="text/javascript" src="/js/bootstrap.min.js"></script>
    <!-- MDB core JavaScript -->
    <script type="text/javascript" src="/js/mdb.min.js"></script>

</head>

<body>

    <header>

    </header>

    <main>

        <br>
        <br>
        <br>
        <br>

        <div id="status">
            <div class="col-12 text-center">
                <div align="center-block"><i class="fa fa-4x fa-refresh fa-spin"></i></div>
                <h2>${labels["setting.up.connector"]}</h2>
            </div>

        </div>

    </main>

    <script type="text/javascript">
        //var hash = location.hash.replace("#", "");
        //var params = {};
        //hash.split('&').map(hk => {
            //let temp = hk.split('=');
            //params[temp[0]] = temp[1];
        //});

        $.ajax({
            type: "POST",
            url: "${TOKEN_URL}",
            complete: function(response) {
                var token = {
            "id": params.id,
            "access_token": params.access_token,
            "refresh_token": params.refresh_token,
            "signature": params.signature,
            "instance_url": params.instance_url,
            "token_type": params.token_type,
            "issued_at": params.issued_at
        };

        $.ajax({
            type: "POST",
            url: "${LINK_ACCOUNT_URI}".replace(":id", ${ORGANIZATION_ID}),
            dataType: "json",
            data: JSON.stringify(token),
            complete: function(response) {
                if (response.status == 200) {
                    window.opener.location = "${LINK_ACCOUNT_SUCCESS_URI}".replace(":id", state);
                    window.close();
                } else {
                    $("#status").html(response.responseText);
                }
            }
        });
            }
        });

        
    </script>

</body>

</html>