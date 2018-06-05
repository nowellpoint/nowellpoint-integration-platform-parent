    /*-----------------------------------------------------------
     *
     *
     */

    $(document).on('click', 'button#editPaymentMethod', function () {
        $('#cardholderName').focus();
    });

    /*-----------------------------------------------------------
     *
     *
     */

    $(document).on('click', 'button#editBillingContact', function () {
        $("#firstName").focus();
    });

    /*-----------------------------------------------------------
     *
     *
     */

    $(document).on('click', 'button#editBillingAddress', function () {
        $('#street').focus();
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