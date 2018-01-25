/*-----------------------------------------------------------
 *
 *
 */

$(document).on('click', 'button#editUserProfile', function () {
    $('#editUserProfile').hide();
    $('#userView').hide();
    $('#userProfileEdit').show();
    $('#firstName').focus();
});

/*-----------------------------------------------------------
 *
 *
 */

$(document).on('click', 'button#cancelUserProfile', function () {
    var userProfile = JSON.parse(sessionStorage.getItem("userProfile"));
    $('#userProfileEdit').hide();
    $('#firstName').val(userProfile.firstName);
    $('#lastName').val(userProfile.lastName);
    $('#email').val(userProfile.email);
    $('#phone').val(userProfile.phone);
    $('#title').val(userProfile.title);
    $('#locale').val(userProfile.locale);
    $('#timeZone').val(userProfile.timeZone);
    $('#error').remove();
    $('#userView').show();
    $('#editUserProfile').show();
});

/*-----------------------------------------------------------
 *
 *
 */

$(document).on('click', 'button#editAddress', function () {
    $('#editAddress').hide();
    $('#addressView').hide();
    $('#addressEdit').show();
    $('#street').focus();

});

/*-----------------------------------------------------------
 *
 *
 */

$(document).on('click', 'button#cancelAddress', function () {
    var userProfile = JSON.parse(sessionStorage.getItem("userProfile"));
    $('#addressEdit').hide();
    $('#street').val(userProfile.street);
    $('#city').val(userProfile.city);
    $('#state').val(userProfile.state);
    $('#postalCode').val(userProfile.postalCode);
    $('#countryCode').val(userProfile.countryCode);
    $('#error').remove();
    $('#addressView').show();
    $('#editAddress').show();
});

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
                $('#profile-content').html(response.responseText);
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