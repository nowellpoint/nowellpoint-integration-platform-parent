$('#confirm').click(function (e) {
    var id = $('#confirmDialog').data('id');
    var row = $('#'.concat(id));
    var href = $('#href').val();
    $.ajax({
        url: href,
        type: 'DELETE',
        success: function () {
            row.remove();
        }
    });
    $('#confirmDialog').modal('hide');
});


$('#confirm-remove-environment').on('click', function (e) {
    var id = $(this).data('id');
    var message = $(this).data('message');
    var title = $(this).data('title');
    var href = $(this).data('href');

    $('#confirm-remove-environment-dialog').find('.modal-body p').text(message);
    $('#confirm-remove-environment-dialog').find('.modal-title').text(title);
    $('#confirm-remove-environment-dialog').find('#href').val(href);
    $('#confirm-remove-environment-dialog').data('id', id).modal('show');
});


$('#confirm-remove-environment-button').click(function (e) {
    var id = $('#confirm-remove-environment-dialog').data('id');
    var row = $('#'.concat(id));
    var href = $('#href').val();
    $.ajax({
        url: href,
        type: 'DELETE',
        success: function () {
            row.remove();
        }
    });
    $('#confirm-remove-environment-dialog').modal('hide');
});

$('.test-connection').on('click', function (e) {
    var id = $(this).data('id');
    var row = $('#'.concat(id));
    var href = $(this).data('href');
    $.ajax({
        type: "POST",
        dataType: "text",
        url: href,
        complete: function (response) {
            if (response.status == 200) {
                $('#success').show().delay(3000).fadeOut();
            } else {
                $('#error-message').text(response.responseText);
                $('#error').show();
            }
        }
    });
});

$('.build-environment').on('click', function (e) {
    var id = $(this).data('id');
    var row = $('#'.concat(id));
    var href = $(this).data('href');
    $.ajax({
        type: "POST",
        dataType: "json",
        url: href,
        complete: function (response) {
            if (response.status == 200) {
                $('#success').show().delay(3000).fadeOut();
            } else {
                $('#error-message').text(response.responseText);
                $('#error').show();
            }
        } 
    });
});