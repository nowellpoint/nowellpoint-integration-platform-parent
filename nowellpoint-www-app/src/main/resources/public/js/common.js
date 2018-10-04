$( document ).ready(function() {
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

$(document).ready(function() {
    $("#toggleSidebar").click(function() {
        if ( $('#sidebar').width() == 0 ) {
            $('#sidebar').animate({width: "250px"}, 200)
            $('#content').animate({left: "250px"}, 200)
        } else {
            $('#sidebar').animate({width: "0"}, 200)
            $('#content').animate({left: "0"}, 200)
        }
    });
});