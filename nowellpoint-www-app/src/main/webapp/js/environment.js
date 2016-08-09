$('#confirm').click(function (e) {
    e.preventDefault();
    var href = $('#href').val();
    console.log('href: ' + href)
    $.ajax({
        type: "DELETE",
        url: href,
        success: function (data, textStatus, xhr) {
            $(location).attr("href", xhr.getResponseHeader('Location'));
        }
    });
    $('#confirmDialog').modal('hide');
});