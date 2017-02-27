


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