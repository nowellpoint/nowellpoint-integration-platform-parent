
/*-----------------------------------------------------------
 *
 *
 */

$(document).on('click', 'button#saveUserProfile', function(e) {
    e.preventDefault();
    
    $('#error').remove();
    
    $("#overlay").show();

    $form = $("#profile-details");

    $.ajax({
        method: $form.attr('method'),
        url: $form.attr('action'),
        dataType: "html",
        data: $form.serialize(),
        complete: function (response) {
            $("#overlay").hide();
            if (response.status == 200) {
                $('#content').html(response.responseText);
            } else {
                $form.prepend(response.responseText);
            }
        }
    });
    
    return true;
});

/*-----------------------------------------------------------
 *
 *
 */

$(document).on('click', 'button#saveAddress', function(e) {
    e.preventDefault();
    
    $('#error').remove();
    
    $("#overlay").show();

    $form = $("#profile-address");

    $.ajax({
        method: $form.attr('method'),
        url: $form.attr('action'),
        dataType: "html",
        data: $form.serialize(),
        complete: function (response) {
            $("#overlay").hide();
            if (response.status == 200) {
                $('#content').html(response.responseText);
            } else {
                $form.prepend(response.responseText);
            }
        }
    });
    
    return true;
});