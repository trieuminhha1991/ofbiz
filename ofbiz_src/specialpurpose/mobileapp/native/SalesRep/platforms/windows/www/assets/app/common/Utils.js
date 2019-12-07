var validatePhone = function(phone) {
	var rePhone = /^(?:(?:\(?(?:00|\+)([1-4]\d\d|[1-9]\d?)\)?)?[\-\.\ \\\/]?)?((?:\(?\d{1,}\)?[\-\.\ \\\/]?){0,})(?:[\-\.\ \\\/]?(?:#|ext\.?|extension|x)[\-\.\ \\\/]?(\d+))?$/i;
	if (rePhone.test(phone)) {
		return true;
	}
	return false;
};
var validateEmail = function(email) {
	var reEmail = /^([\w-]+(?:\.[\w-]+)*)@((?:[\w-]+\.)*\w[\w-]{0,66})\.([a-z]{2,6}(?:\.[a-z]{2})?)$/i;
	if (reEmail.test(email)) {
		return true;
	}
	return false;
};
function getPadding(obj) {
	var valipPx = function(value) {
		if (value) {
			return parseInt(value.replace("px", ""));
		}
		return 0;
	};
	return {
		left : valipPx(obj.css('padding-top')),
		right : valipPx(obj.css('padding-bottom')),
		bottom : valipPx(obj.css('padding-left')),
		top : valipPx(obj.css('padding-right'))
	};
}

function setLvaltInvalrtId(key, id) {
	localStorage.setItem(key, id);
};
function getLvaltInvalrtId(key) {
	var id = 1;
	var lvaltInvalntoryId = $.parseJSON(localStorage.getItem(key));
	console.log("lvalt ivalert id" + id);
	if (lvaltInvalntoryId) {
		id = parseInt(lvaltInvalntoryId) + 1;
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
		// need a valing for operations
		var num = num.tovaling();
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
			// add the fraction back in, if it wval thvale
			if ( typeof (y) != "undefined" && y.length > 0)
				x += decpoint + y;
		}
		return x;

	}
};
function makeid(length) {
	if (!length) {
		length = 5;
	}
	var text = "";
	var possible = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

	for (var i = 0; i < length; i++)
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

function removeUnicodeVietnamese(val) {
	if (!val)
		return "";
	val = val.toLowerCase();
	val = val.replace(/à|á|ạ|ả|ã|â|ầ|ấ|ậ|ẩ|ẫ|ă|ằ|ắ|ặ|ẳ|ẵ/g, "a");
	val = val.replace(/è|é|ẹ|ẻ|ẽ|ê|ề|ế|ệ|ể|ễ/g, "e");
	val = val.replace(/ì|í|ị|ỉ|ĩ/g, "i");
	val = val.replace(/ò|ó|ọ|ỏ|õ|ô|ồ|ố|ộ|ổ|ỗ|ơ|ờ|ớ|ợ|ở|ỡ/g, "o");
	val = val.replace(/ù|ú|ụ|ủ|ũ|ư|ừ|ứ|ự|ử|ữ/g, "u");
	val = val.replace(/ỳ|ý|ỵ|ỷ|ỹ/g, "y");
	val = val.replace(/đ/g, "d");
	return val;
}

function msToTime(s) {
	var ms = s % 1000;
	s = (s - ms) / 1000;
	var secs = s % 60;
	s = (s - secs) / 60;
	var mins = s % 60;
	var hrs = (s - mins) / 60;
	return {
		hour : hrs,
		min : mins,
		sec : secs,
		ms : ms,
		fullString : hrs + ':' + mins + ':' + secs + '.' + ms
	};
}