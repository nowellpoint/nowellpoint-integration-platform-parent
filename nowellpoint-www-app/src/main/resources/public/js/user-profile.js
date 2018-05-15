
/*-----------------------------------------------------------
 *
 *
 */

$(document).on('click', 'button#save-user-information', function(e) {
    e.preventDefault();

    $form = $("#form-user-information");

    $.ajax({
        type: $form.attr('method'),
        url: $form.attr('action'),
        dataType: "html",
        data: $form.serialize(),
        complete: function (response) {
            console.log(response);
            if (response.status == 200) {
            	$('#modal-user-information').modal('toggle');
                $('#user-information').html(response.responseText);
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

    $form = $("#form-address");

    $.ajax({
        type: $form.attr('method'),
        url: $form.attr('action'),
        dataType: "html",
        data: $form.serialize(),
        complete: function (response) {
            if (response.status == 200) {
            	$('#modal-address-form').modal('toggle');
                $('#address').html(response.responseText);
            } else {
                $form.prepend(response.responseText);
            }
        }
    });
    
    return true;
});