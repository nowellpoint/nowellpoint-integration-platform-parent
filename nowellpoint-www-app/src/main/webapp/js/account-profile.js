$(function () {
	$('.toggle').click(function (event) {
		event.preventDefault();
		var target = $(this).attr('href');
		$(target).toggleClass('hidden show');
		$('#add-card').toggleClass('hidden show');
		$('#firstName').val('');
		$("#lastName").val('');
		$("#street").val('');
		$("#city").val('');
		$("#state").val('');
		$("#postalCode").val('');
		$("#countryCode").val('');
		$('#use-contact-info').prop('checked', false);
	});
});

$("#use-contact-info").change(function () {
	if (this.checked) {
		var accountProfile = jQuery.data(document.body, "accountProfile");
		$("#firstName").val(accountProfile.firstName);
		$("#lastName").val(accountProfile.lastName);
		$("#street").val(accountProfile.street);
		$("#city").val(accountProfile.city);
		$("#state").val(accountProfile.state);
		$("#postalCode").val(accountProfile.postalCode);
		$("#countryCode").val(accountProfile.countryCode);
	} else {
		$('#firstName').val('');
		$("#lastName").val('');
		$("#street").val('');
		$("#city").val('');
		$("#state").val('');
		$("#postalCode").val('');
		$("#countryCode").val('');
	}
});

$("#save").click(function (event) {    
	event.preventDefault();
	var params = $("#form").serialize();
	$.ajax({
		type : "POST",
		dataType: "html",
		data: params,
		url : "/app/payment-methods",
		success : function() {
			location.reload();
		}
	});
});