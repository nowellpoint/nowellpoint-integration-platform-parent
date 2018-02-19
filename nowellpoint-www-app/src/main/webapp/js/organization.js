    /*-----------------------------------------------------------
     *
     *
     */

    $(document).on('click', 'button#editPaymentMethod', function () {
        $('#editPaymentMethod').hide();
        $('#paymentMethodView').hide();
        $('#paymentMethodEdit').show();
        $('html, body').animate({
            scrollTop: ($('#payment-method').offset().top - 120)
        }, 500);
        $('#cardholderName').focus();
    });

    /*-----------------------------------------------------------
     *
     *
     */

    $(document).on('click', 'button#cancelPaymentMethod', function () {
        var organization = JSON.parse(sessionStorage.getItem("organization"));
        $('#paymentMethodEdit').hide();
        $('#number').val('');
        $('#cvv').val('');
        $('#cardholdername').val(organization.cardholdername);
        $('#expirationMonth').val(organization.expirationMonth);
        $('#error').remove();
        $('#paymentMethodView').show();
        $('#editPaymentMethod').show();
    });

    /*-----------------------------------------------------------
     *
     *
     */

    $(document).on('click', 'button#editBillingContact', function () {
        $('#editBillingContact').hide();
        $('#billingContactView').hide();
        $('#billingContactEdit').show();
        $('html, body').animate({
            scrollTop: ($('#billing-contact').offset().top - 120)
        }, 500);
        $("#firstName").focus();
    });

    /*-----------------------------------------------------------
     *
     *
     */

    $(document).on('click', 'button#cancelBillingContact', function () {
        var organization = JSON.parse(sessionStorage.getItem("organization"));
        $('#error').remove();
        $('#firstName').val(organization.billingContact.firstName);
        $('#lastName').val(organization.billingContact.lastName);
        $('#email').val(organization.billingContact.email);
        $('#phone').val(organization.billingContact.phone);
        $('#billingContactEdit').hide();
        $('#billingContactView').show();
        $('#editBillingContact').show();
    });


    /*-----------------------------------------------------------
     *
     *
     */

    $(document).on('click', 'button#editBillingAddress', function () {
        $('#editBillingAddress').hide();
        $('#billingAddressView').hide();
        $('#billingAddressEdit').show();
        $('html, body').animate({
            scrollTop: ($('#billing-address').offset().top - 120)
        }, 500);
        $('#street').focus();
    });


    /*-----------------------------------------------------------
     *
     *
     */

    $(document).on('click', 'button#cancelBillingAddress', function () {
        var organization = JSON.parse(sessionStorage.getItem("organization"));
        $('#error').remove();
        $('#street').val(organization.billingAddress.street);
        $('#city').val(organization.billingAddress.city);
        $('#state').val(organization.billingAddress.state);
        $('#postalCode').val(organization.billingAddress.postalCode);
        $('#countryCode').val(organization.billingAddress.countryCode);
        $('#billingAddressEdit').hide();
        $('#billingAddressView').show();
        $('#editBillingAddress').show();
    });

    /*-----------------------------------------------------------
     *
     *
     */

    $(document).on('click', 'button#savePaymentMethod', function () {

        $('#error').remove();

        $('#overlay').show();

        $form = $("#paymentMethodForm");

        $.ajax({
            method: $form.attr('method'),
            url: $form.attr('action'),
            dataType: "html",
            data: $form.serialize(),
            complete: function (response) {
                $('#overlay').hide();
                if (response.status == 200) {
                    $('#content').html(response.responseText);
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

    $(document).on('click', 'button#saveBillingAddress', function () {

        $('#error').remove();

        $('#overlay').show();

        $form = $("#billingAddressForm");

        $.ajax({
            method: $form.attr('method'),
            url: $form.attr('action'),
            dataType: "html",
            data: $form.serialize(),
            complete: function (response) {
                $('#overlay').hide();
                if (response.status == 200) {
                    $('#content').html(response.responseText);
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

    $(document).on('click', 'button#saveBillingContact', function (e) {
        e.preventDefault();

        $('#error').remove();

        $('#overlay').show();

        $form = $("#billingContactForm");

        $.ajax({
            method: $form.attr('method'),
            url: $form.attr('action'),
            dataType: "html",
            data: $form.serialize(),
            complete: function (response) {
                $('#overlay').hide();
                if (response.status == 200) {
                    $('#content').html(response.responseText);
                } else {
                    $form.prepend(response.responseText);
                }
            }
        });

        return true;
    });