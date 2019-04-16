/*-----------------------------------------------------------
*
*
*/

$(document).on('click', 'button#action-button', function () {
   
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
