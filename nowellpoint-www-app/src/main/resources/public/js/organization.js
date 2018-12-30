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
                	$('#change-plan-success-popup').modal('toggle');
                	setTimeout(function() {
                		  $('#change-plan-success-popup').modal( 'toggle');
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
    	
    	$('#error').remove();

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

    $(document).on('click', 'button#saveBillingContact', function () {

        $('#error').remove();

        $form = $("#billing-contact-form");

        $.ajax({
            type: $form.attr('method'),
            url: $form.attr('action'),
            dataType: "html",
            data: $form.serialize(),
            complete: function (response) {
            	if (response.status == 200) {
                	$('#modal-billing-address').modal('toggle');
                    $('#organization-billing-contact').html(response.responseText); 
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

   $(document).on('click', 'button#save-streaming-event-listener', function () {

       $form = $("#streaming-event-listener-form");

       $.ajax({
    	   type: $form.attr('method'),
           url: $form.attr('action'),
           dataType: "html",
           data: $form.serialize(),
           contentType: "application/x-www-form-urlencoded",
           complete: function (response) {
               if (response.status == 200) {
            	   
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