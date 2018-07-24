
/*-----------------------------------------------------------
 *
 *
 */

$(document).on('click', 'button#save-user-information', function(e) {

    $form = $("#form-user-information");

    $.ajax({
        type: $form.attr('method'),
        url: $form.attr('action'),
        dataType: "html",
        data: $form.serialize(),
        complete: function (response) {
            if (response.status == 200) {
            	$('#modal-user-information').modal('toggle');
                $('#user-information').html(response.responseText);
                $('#success-modal').modal('toggle');
                setTimeout(function() {
                	$('#success-modal').modal('toggle');
                }, 2000); 
            } else {
                $form.prepend(response.responseText);
            }
        }
    });
    
    return false;
});

/*-----------------------------------------------------------
 *
 *
 */

$(document).on('click', 'button#save-address', function(e) {

    $form = $("#form-address");

    $.ajax({
        type: $form.attr('method'),
        url: $form.attr('action'),
        dataType: "html",
        data: $form.serialize(),
        complete: function (response) {
            if (response.status == 200) {
            	$('#modal-address').modal('toggle');
                $('#address').html(response.responseText);
                $('#success-modal').modal('toggle');
                setTimeout(function() {
                	$('#success-modal').modal('toggle');
                }, 2000);
            } else {
                $form.prepend(response.responseText);
            }
        }
    });
    
    return false;
});


/*-----------------------------------------------------------
 *
 *
 */

$(document).on('click', 'button#save-preferences', function(e) {
    e.preventDefault();

    $form = $("#form-preferences");

    $.ajax({
        type: $form.attr('method'),
        url: $form.attr('action'),
        dataType: "html",
        data: $form.serialize(),
        complete: function (response) {
            if (response.status == 200) {
            	$('#modal-preferences').modal('toggle');
                $('#preferences').html(response.responseText);
                $('#success-modal').modal('toggle');
                setTimeout(function() {
                	$('#success-modal').modal('toggle');
                }, 2000);
            } else {
                $form.prepend(response.responseText);
            }
        }
    });
    
    return true;
});