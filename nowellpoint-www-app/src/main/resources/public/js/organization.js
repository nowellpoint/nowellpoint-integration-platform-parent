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

    $(document).on('click', 'button#save-payment-information', function () {

        $form = $("#payment-method-form");

        $.ajax({
        	type: $form.attr('method'),
            url: $form.attr('action'),
            dataType: "html",
            data: $form.serialize(),
            complete: function (response) {
                if (response.status == 200) {
                	$('#modal-payment-method').modal('toggle');
                    $('#organization-payment-method').html(response.responseText);
                    $("#success-alert").fadeTo(2000, 500).slideUp(500, function() {
                        $("#success-alert").slideUp(500);
                    }); 
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
    
    $("#change-plan").click(function(e) {
        
        $('#error').remove();

        $form = $("#change-plan-form");

        $.ajax({
            type: $form.attr('method'),
            url: $form.attr('action'),
            dataType: "html",
            data: $form.serialize(),
            complete: function(response) {
                if (response.status == 200) {
                	$('#success-popup').modal('show');
                	setTimeout(function() {
                		  $('.success-popup').modal( 'hide');
                		  var organization = JSON.parse(sessionStorage.getItem("organization"));
                		  $(location).attr("href", organization.basePath + "/" + organization.id + "/");
                		}, 2000);                	
                } else {
                    $("#error").prepend(response.responseText);
                }
            }

        });
        
        return false;
    });
    
    /*-----------------------------------------------------------
     *
     *
     */

    $(document).on('click', 'button#save-billing-address', function () {

        $form = $("#billing-address-form");

        $.ajax({
            type: $form.attr('method'),
            url: $form.attr('action'),
            dataType: "html",
            data: $form.serialize(),
            complete: function (response) {
            	if (response.status == 200) {
                	$('#modal-billing-address').modal('toggle');
                    $('#organization-billing-address').html(response.responseText);
                    $("#success-alert").fadeTo(2000, 500).slideUp(500, function() {
                        $("#success-alert").slideUp(500);
                    }); 
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
            type: $form.attr('method'),
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
    
   /*-----------------------------------------------------------
    *
    *
    */
    
    $( document ).ajaxStart(function() {
    	$( "#wait" ).show();
    });
    
   /*-----------------------------------------------------------
    *
    *
    */
    
    $( document ).ajaxStop(function() {
    	$( "#wait" ).hide();
  	});