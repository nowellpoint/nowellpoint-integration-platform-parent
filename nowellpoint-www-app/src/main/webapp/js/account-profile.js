


$('#deactivate-account-profile').click(function (e) {
    e.preventDefault();
    $.ajax({
        type: "DELETE",
        url: $(this).attr("href"),
        complete: function () {
            $(location).attr("href", "/");
        }
    });
});


$(document).on('show.bs.modal', '.modal', function (e) {
    var data = $(e.relatedTarget).data();

    $('.modal-title', this).text(data.title);
    $('.modal-body', this).text(data.message);

    $('#confirm-button').click(function (e) {
        e.preventDefault();
        var token = data.id;
        var row = $('#'.concat(token));
        var accountProfile = jQuery.data(document.body, "accountProfile");
        $.ajax({
            type: "DELETE",
            url: accountProfile.basePath + "/" + accountProfile.id + "/payment-methods/" + token,
            success: function () {
                row.hide();
            }
        });
        $('#confirm-dialog').modal('hide');
    });
});

$('.make-primary').click(function (e) {
    e.preventDefault();
    $.ajax({
        type: "POST",
        url: $(this).attr("href"),
        success: function () {
            $(location).attr("href", accountProfile.basePath + "/" + accountProfile.id);
        }
    });
});


$(document).ready(function (e) {
    $('#clickable').click(function (e) {
        e.preventDefault();
        $.ajax({
            type: "DELETE",
            url: "/app/account-profile/picture",
            success: function () {
                location.reload();
            }
        });
    });
});