$('#confirm').click(function (e) {
    var id = $('#confirmDialog').data('id');
    var row = $('#'.concat(id));
    $.ajax({
        url: id,
        type: 'DELETE',
        success: function () {
            row.remove();
        }
    });
    $('#confirmDialog').modal('hide');
});