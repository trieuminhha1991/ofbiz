var validatePhone = function(phone) {
	var rePhone = /^(?:(?:\(?(?:00|\+)([1-4]\d\d|[1-9]\d?)\)?)?[\-\.\ \\\/]?)?((?:\(?\d{1,}\)?[\-\.\ \\\/]?){0,})(?:[\-\.\ \\\/]?(?:#|ext\.?|extension|x)[\-\.\ \\\/]?(\d+))?$/i;
	if (rePhone.test(phone)) {
		return true;
	}
	return false;
};
var validateEmail = function(email){
	var reEmail = /^([\w-]+(?:\.[\w-]+)*)@((?:[\w-]+\.)*\w[\w-]{0,66})\.([a-z]{2,6}(?:\.[a-z]{2})?)$/i;
	if(reEmail.test(email)){
		return true;
	}
	return false;
};
function getPadding(obj){
	var stripPx = function(value){
		if(value){
			return parseInt(value.replace("px", ""));
		}
		return 0;
	};
	return {
		left: stripPx(obj.css('padding-top')),
		right: stripPx(obj.css('padding-bottom')),
		bottom: stripPx(obj.css('padding-left')),
		top: stripPx(obj.css('padding-right'))
	};
}

function setLastInsertId(key, id) {
	localStorage.setItem(key, id);
};
function getLastInsertId(key) {
	var id = 1;
	var lastInventoryId = $.parseJSON(localStorage.getItem(key));
	console.log("last insert id" + id);
	if (lastInventoryId) {
		id = parseInt(lastInventoryId) + 1;
	}
	localStorage.setItem(key, id);
	return id;
};
function log(msg, obj) {
	console.log("OLBIUS: " + msg + " ", obj);
}

function formatNumberBy3(num, decpoint, sep) {
	if (num) {
		// check for missing parameters and use defaults if so
		if (arguments.length == 2) {
			sep = ",";
		}
		if (arguments.length == 1) {
			sep = ",";
			decpoint = ".";
		}
		// need a string for operations
		var num = num.toString();
		// separate the whole number and the fraction if possible
		var a = num.split(decpoint);
		var x = a[0];
		// decimal
		var y = a[1];
		// fraction
		var z = "";

		if ( typeof (x) != "undefined") {
			// reverse the digits. regexp works from left to right.
			for (var i = x.length - 1; i >= 0; i--)
				z += x.charAt(i);
			// add seperators. but undo the trailing one, if there
			z = z.replace(/(\d{3})/g, "$1" + sep);
			if (z.slice(-sep.length) == sep)
				z = z.slice(0, -sep.length);
			x = "";
			// reverse again to get back the number
			for (var i = z.length - 1; i >= 0; i--)
				x += z.charAt(i);
			// add the fraction back in, if it was there
			if ( typeof (y) != "undefined" && y.length > 0)
				x += decpoint + y;
		}
		return x;

	}
};
function makeid(length)
{
	if(!length){
		length = 5;
	}
    var text = "";
    var possible = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    for( var i=0; i < length; i++ )
        text += possible.charAt(Math.floor(Math.random() * possible.length));

    return text;
}

function convertDistance(distance) {
	if (distance < 1) {
		var m = Math.round(distance * 1000);
		return m + "m";
	}
	var dis = Math.round(distance * 10) / 10;
	return dis + "km";
}