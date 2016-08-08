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
    console.log('here');
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