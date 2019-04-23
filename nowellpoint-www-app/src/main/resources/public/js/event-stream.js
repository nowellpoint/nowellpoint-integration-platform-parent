/*-----------------------------------------------------------
*
*
*/

$(document).on('click', 'button#action', function () {

    $(".dropdown-toggle").dropdown('toggle');
   
    $.ajax({
        type: 'POST',
        url: $(this).data("href"),
        dataType: "html",
        complete: function (response) {
            if (response.status == 200) {
                $('#success-modal').modal('toggle');
                setTimeout(function () {
                    $('#success-modal').modal('toggle');
                    $(location).attr('href', response.getResponseHeader('location'));
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

$(document).on('click', 'button#save-streaming-event-listener', function () {

    $form = $("#streaming-event-listener-form");
   
    $.ajax({
        type: $form.attr('method'),
        url: $form.attr('action'),
        dataType: "html",
        data: $form.serialize(),
        complete: function (response) {
            if (response.status == 200) {
                $('#success-modal').modal('toggle');
                setTimeout(function () {
                    $('#success-modal').modal('toggle');
                    $(location).attr('href', response.getResponseHeader('location'));
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

$(document).ajaxStart(function () {
    $("#wait").show();
});

/*-----------------------------------------------------------
 *
 *
 */

$(document).ajaxStop(function () {
    $("#wait").hide();
});
