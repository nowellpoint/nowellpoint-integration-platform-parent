$( document ).ready(function() {
    $('[data-toggle="popover"]').popover(); 
});

$( document ).ready(function () {
    var path = window.location.pathname;
    path = path.replace(/\/$/, "");
    path = decodeURIComponent(path).concat("/");

    $("#sidebar a").each(function () {
        var href = $(this).attr('href');
        if (path.substring(0, href.length) === href) {
            $(this).closest('li').addClass('active');
        }
    });
});

$( document ).ready(function () {
    $('#toggleSidebar').on('click', function () {
        $('#sidebar, #content').toggleClass('active');
        $('.collapse.in').toggleClass('in');
        $('a[aria-expanded=true]').attr('aria-expanded', 'false');
    });
});

$(document).ajaxStart(function(){
	$("#overlay").css("display", "block");
});

$(document).ajaxComplete(function(){
	$("#overlay").css("display", "none");
});