$("#use-contact-info").change(function () {
    if (this.checked) {
        var accountProfile = jQuery.data(document.body, "accountProfile");
        $("#cardholderName").val((accountProfile.firstName ? accountProfile.firstName + ' ' : '') + accountProfile.lastName);
        $("#firstName").val(accountProfile.firstName);
        $("#lastName").val(accountProfile.lastName);
        $("#street").val(accountProfile.street);
        $("#city").val(accountProfile.city);
        $("#state").val(accountProfile.state);
        $("#postalCode").val(accountProfile.postalCode);
        $("#countryCode").val(accountProfile.countryCode);
        $("#number").focus();
    } else {
        $('#cardholderName').val('');
        $("#firstName").val('');
        $("#lastName").val('');
        $("#street").val('');
        $("#city").val('');
        $("#state").val('');
        $("#postalCode").val('');
        $("#countryCode").val('');
        $("#cardholderName").focus();
    }
});



$('#confirm').click(function (e) {
    e.preventDefault();
    var token = $('#confirmDialog').data('id');
    var row = $('#'.concat(token));
    var accountProfile = jQuery.data(document.body, "accountProfile");
    $.ajax({
        type: "DELETE",
        url: accountProfile.basePath + "/" + accountProfile.id + "/payment-methods/" + token,
        success: function () {
            $(location).attr("href", accountProfile.basePath);
        }
    });
    $('#confirmDialog').modal('hide');
});



$('#profileDialog').on('show.bs.modal', function(e) {
    $title = $('.profile-dialog').data('title');
    $(this).find('.modal-title').text($title);
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