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