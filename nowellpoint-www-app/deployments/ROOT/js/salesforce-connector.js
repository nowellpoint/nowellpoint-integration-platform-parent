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


$('#confirm-remove-instance').on('click', function (e) {
    var id = $(this).data('id');
    var message = $(this).data('message');
    var title = $(this).data('title');
    var href = $(this).data('href');

    $('#confirm-remove-instance-dialog').find('.modal-body p').text(message);
    $('#confirm-remove-instance-dialog').find('.modal-title').text(title);
    $('#confirm-remove-instance-dialog').find('#href').val(href);
    $('#confirm-remove-instance-dialog').data('id', id).modal('show');
});


$('#confirm-remove-instance-button').click(function (e) {
    var id = $('#confirm-remove-instance-dialog').data('id');
    var row = $('#'.concat(id));
    var href = $('#href').val();
    $.ajax({
        url: href,
        type: 'DELETE',
        success: function () {
            row.remove();
        }
    });
    $('#confirm-remove-instance-dialog').modal('hide');
});

$('.test-connection').on('click', function (e) {
    $('#test-connection-dialog').modal('show');
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
    $('#test-connection-dialog').modal('hide');
});

$('.build-instance').on('click', function (e) {
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