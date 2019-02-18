$("#contact-form").validate({    
    submitHandler: function (form) {
        $form = $("#contact-form");
        $.ajax({
            method: "POST",
            url: $form.attr('action'),
            dataType: "html",
            data: $form.serialize(),
            complete: function (response) {
                $("#response").html(response.responseText).show();
                $('#response').fadeOut(4000)
                $("#firstName").val("");
                $("#lastName").val("");
                $("#email").val("");
                $("#phone").val("");
                $("#company").val("");
                $("#message").val("");
            }
        });

        return false;
    }
});