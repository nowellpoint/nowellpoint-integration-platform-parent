
/*-----------------------------------------------------------
 *
 *
 */

$(document).on('click', 'button#save-profile', function(e) {
    e.preventDefault();

    $form = $("#profile");

    $.ajax({
        type: $form.attr('method'),
        url: $form.attr('action'),
        dataType: "html",
        data: $form.serialize(),
        complete: function (response) {
            console.log(response);
            if (response.status == 200) {
                $form.hide();
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

$(document).on('click', 'button#save-address', function(e) {
    e.preventDefault();
    
    $('#error').remove();

    $form = $("#address");

    $.ajax({
        method: $form.attr('method'),
        url: $form.attr('action'),
        dataType: "html",
        data: $form.serialize(),
        complete: function (response) {
            if (response.status == 200) {
                $form.hide();
                $('#content').html(response.responseText);
            } else {
                $form.prepend(response.responseText);
            }
        }
    });
    
    return true;
});