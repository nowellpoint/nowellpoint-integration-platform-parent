$( document ).ready(function() {
    $('[data-toggle="popover"]').popover(); 
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