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
	} else {
		$('#cardholderName').val('');
        $("#firstName").val('');
        $("#lastName").val('');
		$("#street").val('');
		$("#city").val('');
		$("#state").val('');
		$("#postalCode").val('');
		$("#countryCode").val('');
	}
});



$('#confirm').click(function(e) {
    event.preventDefault();
    var id = $('#confirmDialog').data('id');
    var accountProfile = jQuery.data(document.body, "accountProfile");
    $.ajax({
        type: "DELETE",
        url: accountProfile.basePath + "/" + accountProfile.id + "/payment-methods/" + id,
        complete: function(data) {
            $("#main").html(data.responseText);
        }
    });
    $('#confirmDialog').modal('hide');
});