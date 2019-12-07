var CommonUtils = (function() {
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
	var scorePassword = function(pass) {
		var score = 0;
		if (!pass){
			return score;
		}
		var letters = new Object();
		for (var i = 0; i < pass.length; i++) {
			letters[pass[i]] = (letters[pass[i]] || 0) + 1;
			score += 5.0 / letters[pass[i]];
		}
		var variations = {
			digits : /\d/.test(pass),
			lower : /[a-z]/.test(pass),
			upper : /[A-Z]/.test(pass),
			nonWords : /\W/.test(pass),
		};
		variationCount = 0;
		for (var check in variations) {
			variationCount += (variations[check] == true) ? 1 : 0;
		}
		score += (variationCount - 1) * 10;

		return parseInt(score);
	};
	var checkPassStrength = function(pass) {
	    var score = scorePassword(pass);
	    if (score > 80)
	        return "strong";
	    if (score > 60)
	        return "good";
	    if (score >= 30)
	        return "weak";

	    return "";
	};
	var renderMessage = function(obj, msg, theme, options){
		if(typeof(options) == 'undefined'){
			options = { content: msg, position: 'right', autoHide: true, trigger: "none", closeOnClick: true, theme: theme, autoHideDelay : 5000 };
		}
		obj.jqxTooltip('destroy');
		obj.jqxTooltip(options);
		obj.jqxTooltip('open');
	};
	var getWindowGUID = function() {
		var windowGUID = function() {
			//----------
			var S4 = function() {
				return (
					Math.floor(Math.random() * 0x10000).toString(16)
				);
			};

			return (S4() + S4() + "-" + S4() + "-" + S4() + "-" + S4() + "-" + S4() + S4() + S4()
			);
		};

		var topMostWindow = window;

		while (topMostWindow != topMostWindow.parent) {
			topMostWindow = topMostWindow.parent;
		}

		if (!topMostWindow.name.match(/^GUID-/)) {
			topMostWindow.name = "GUID-" + windowGUID();
		}

		return topMostWindow.name;
	};
	var executeAndReload = function(url) {
		$.ajax({
	        url: url,
	        type: "POST",
	        data: {},
			dataType: "json",
	    }).done(function(res) {
	    	location.reload();
		});
	};
	var removeCartItem = function(index) {
		CommonUtils.executeAndReload("removeCartByCartIndex?index=" + index);
	};
	return {
		validatePhone : validatePhone,
		validateEmail: validateEmail,
		checkPassStrength: checkPassStrength,
		renderMessage : renderMessage,
		getWindowGUID: getWindowGUID,
		executeAndReload: executeAndReload,
		removeCartItem: removeCartItem
	};
})();
var SessionStorage = (function(){
	var guid = CommonUtils.getWindowGUID();
	var setItem = function(key, value){
		try {
			key = key +'_'+ guid;
			sessionStorage.setItem(key, value);
		}
		catch(err) {
		    console.log(err.message);
		}
	};
	var getItem = function(key){
		try {
			key = key +'_'+ guid;
			return sessionStorage.getItem(key);
		}
		catch(err) {
		    console.log(err.message);
		}
	};
	var removeItem = function(key){
		try {
			key = key +'_'+ guid;
			sessionStorage.removeItem(key);
		}
		catch(err) {
		    console.log(err.message);
		}
	};
	// var init = function(){
		// window.onbeforeunload = function () {
		// };
	// };
	return {
		setItem : setItem,
		getItem : getItem,
		removeItem : removeItem
	};
})();
var LocalStorage = (function(){
	var setItem = function(userKey, key, value){
		try {
			key = userKey + "_" + key;
			localStorage.setItem(key, value);
		}
		catch(err) {
		    console.log(err.message);
		}
	};
	var getItem = function(userKey, key){
		try {
			key = userKey + "_" + key;
			return localStorage.getItem(key);
		}
		catch(err) {
		    console.log(err.message);
		}
	};
	var removeItem = function(userKey, key){
		try {
			key = userKey + "_" + key;
			localStorage.removeItem(key);
		}
		catch(err) {
		    console.log(err.message);
		}
	};
	// var init = function(){
		// window.onbeforeunload = function () {
		// };
	// };
	return {
		setItem : setItem,
		getItem : getItem,
		removeItem : removeItem
	};
})();
var setLocation = function(url){
    window.location.href = url;
};
