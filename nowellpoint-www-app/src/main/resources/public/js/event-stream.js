/*-----------------------------------------------------------
*
*
*/

$(document).on('click', 'button#action-button', function () {
	
	

    alert($(this).data("href"));

    return false;
});

/*-----------------------------------------------------------
 *
 *
 */

$(document).ajaxStart(function () {
    $("#wait").show();
});

/*-----------------------------------------------------------
 *
 *
 */

$(document).ajaxStop(function () {
    $("#wait").hide();
});
