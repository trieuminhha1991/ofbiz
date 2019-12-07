$('body').ajaxStart(function() {
					disAll();
		            $(this).css({'cursor':'progress'});
		            $(".jqx-widget").css({'cursor':'progress'});
		            $(".btn").css({'cursor':'progress'});
		        }).ajaxStop(function() {
		        	openAll();
		        	clearTimeout(waitingTime);
		            $(this).css({'cursor':'default'});
		            $(".jqx-widget").css({'cursor':'default'});
		            $(".btn").css({'cursor':'default'});
		        }).ajaxError(function( event, jqxhr, settings, thrownError ) {
			        	console.log(thrownError);
			        	clearTimeout(waitingTime);
			        	openAll();
			            $(this).css({'cursor':'default'});
			            $(".jqx-widget").css({'cursor':'default'});
			            $(".btn").css({'cursor':'default'});
				});
var waitingTime;
function disAll() {
	var all = $('input[type="submit"], button');
	for ( var x in all) {
		all[x].disabled = true;
	}
	$("form a").prop("disabled", true);
//	waitingTime = setTimeout(function() {
//		var r=confirm("No connection!");
//		if (r==true)
//		{
//			location.reload();
//		}
//		else
//		{
//			location.reload();
//		}
//	}, 180000);
}
function openAll() {
	var all = $('input[type="submit"], button');
	for ( var x in all) {
		all[x].disabled = false;
	}
	$("form a").prop("disabled", false);
	clearTimeout(waitingTime);
}
function deleteCookie(name) {
	document.cookie = name + '; expires=Thu, 01 Jan 1970 00:00:01 GMT;';
}
function getCookie() {
	return document.cookie;
}
function setCookie(value) {
	document.cookie = value;
}