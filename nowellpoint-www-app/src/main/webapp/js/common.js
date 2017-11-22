$("#menu-toggle").click(function (e) {
    e.preventDefault();
    $("#wrapper").toggleClass("toggled");
});

$(document).ready(function () {
    $('.dropdown-toggle').dropdown();
});

$(document).ready(function() {
    $('#overlay').hide();
});

$(document).ajaxStart(function () {
    $('#overlay').show();
});

$(document).ajaxStop(function () {
    console.log('ajax stop');
    $('#overlay').hide();
});

$( document ).ready(function(){
    $('[data-toggle="popover"]').popover(); 
});

$(document).ready(function () {
    var path = window.location.pathname;
    path = path.replace(/\/$/, "");
    path = decodeURIComponent(path);

    $(".sidebar-nav a").each(function () {
        var href = $(this).attr('href');
        if (path.substring(0, href.length) === href) {
            $(this).closest('li').addClass('active');
        }
    });
});