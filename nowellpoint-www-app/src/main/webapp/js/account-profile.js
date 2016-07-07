
            $(function() {
                $('.toggle').click(function(event) {
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

            $('#use-contact-info').change(function() {
                if (this.checked) {
                    $("#firstName").val('${accountProfile.firstName!}');
                    $("#lastName").val('${accountProfile.lastName!}');
                    $("#street").val('${(accountProfile.address.street)!}');
                    $("#city").val('${(accountProfile.address.city)!}');
                    $("#state").val('${(accountProfile.address.state)!}');
                    $("#postalCode").val('${(accountProfile.address.postalCode)!}');
                    $("#countryCode").val('${(accountProfile.address.countryCode)!}');
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

            $(document).ready(function(e) {
                $('#clickable').click(function(e) {
                    e.preventDefault();
                    $.ajax({
                        type: "DELETE",
                        url: "/app/account-profile/picture",
                        success: function() {
                            location.reload();
                        }
                    });
                });
            });
