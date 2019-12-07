var Loading = (function() {
	var self = {};
	self.show = function(id, options) {
		if (!id)
			id = "loadingMacro";
		$('#' + id).show(options);
	};
	self.hide = function(id, options) {
		if (!id)
			id = "loadingMacro";
		$('#' + id).hide(options);
	};
	self.hideAll = function(options) {
		$('.loading-container').hide(options);
	};
	self.changeBackground = function(id, bg) {
		if (!id)
			id = "loadingMacro";
		$('#' + id).css('background', bg);
	};
	self.setIndex = function(id, index) {
		if (!id)
			id = "loadingMacro";
		$('#' + id).css('z-index', index);
	};
	self.showLoadingCursor = function(obj) {
		if (obj)
			obj.addClass('loading-cursor');
	};
	self.hideLoadingCursor = function(obj) {
		if (obj)
			obj.removeClass('loading-cursor');
	};
	return self;
})();
var Request = (function() {
	var self = {};
	self.post = function(url, data, success, before, error, options) {
		if (!options) {
			options = {};
		}
		return $.ajax({
			url : url,
			type : "POST",
			data : data,
			dataType : typeof (options.dataType) != 'undefined' ? options.dataType : 'json',
			async : typeof (options.async) != 'undefined' ? options.async : true,
			cache : typeof (options.cache) != 'undefined' ? options.cache : false,
			crossDomain : typeof (options.crossDomain) != 'undefined' ? options.crossDomain : false,
			contentType : typeof (options.contentType) != 'undefined' ? options.contentType : 'application/x-www-form-urlencoded;charset=UTF-8',
			processData : typeof (options.processData) != 'undefined' ? options.processData : true,
			beforeSend : before,
			success : success,
			error : error
		});
	};
	self.uploadFile = function(file, callback, before) {
		var form_data = new FormData();
		form_data.append("uploadedFile", file);
		self.post("jackrabbitUploadFile", form_data, callback, before, null, {
			contentType : false,
			processData : false
		});
	};
	return self;
})();
var BasicUtils = (function() {
	var self = {};
	self.formatDateDMY = function(date) {
		if (!date)
			return;
		var today;
		if ( typeof date.getMonth === 'function') {
			today = date;
		} else if (date.time) {
			today = new Date(date.time);
		} else {
			today = new Date(date);
		}
		var dd = today.getDate();
		var mm = today.getMonth() + 1;
		var yyyy = today.getFullYear();
		if (dd < 10) {
			dd = '0' + dd;
		}
		if (mm < 10) {
			mm = '0' + mm;
		}
		today = dd + '-' + mm + '-' + yyyy;
		return today;
	};
	self.getFullDate = function(date) {
		if (!date)
			return;
		var today;
		if ( typeof date.getMonth === 'function') {
			today = date;
		} else if (date.time) {
			today = new Date(date.time);
		} else {
			today = new Date(date);
		}
		var dd = today.getDate();
		var mm = today.getMonth() + 1;
		var yyyy = today.getFullYear();
		var hh = today.getHours();
		var mi = today.getMinutes();
		var se = today.getSeconds();
		var mil = today.getMilliseconds();

		dd = dd < 10 ? ("0" + dd) : (dd + "");
		mm = mm < 10 ? ("0" + mm) : (mm + "");
		hh = hh < 10 ? ("0" + hh) : (hh + "");
		mi = mi < 10 ? ("0" + mi) : (mi + "");
		se = se < 10 ? ("0" + se) : (se + "");
		if (mil < 10) {
			mil = "00" + mil;
		} else if (mil < 100) {
			mil = "0" + mil;
		}
		var str = dd + '-' + mm + '-' + yyyy + " " + hh + ":" + mi + ":" + se;
		return str;
	};

	self.validatePhone = function(phone) {
		var rePhone = /^(?:(?:\(?(?:00|\+)([1-4]\d\d|[1-9]\d?)\)?)?[\-\.\ \\\/]?)?((?:\(?\d{1,}\)?[\-\.\ \\\/]?){0,})(?:[\-\.\ \\\/]?(?:#|ext\.?|extension|x)[\-\.\ \\\/]?(\d+))?$/i;
		if (rePhone.test(phone)) {
			return true;
		}
		return false;
	};
	self.initNoSpace = function(element) {
		var time = 0;
		(function(time) {
			element.keyup(function() {
				clearTimeout(time);
				var obj = $(this);
				var val = $(this).val();
				val = val.replace(/[^\w-]/gi, '');
				var res = '';
				for (var x = 0; x < val.length; x++) {
					res += val[x].toUpperCase();
				}
				time = setTimeout(function() {
					obj.val(res);
					obj.trigger('change');
				}, 300);
			});
		})(time);
	};
	self.processNumberLocale = function(num, lo) {
		if (!lo && typeof (locale) != 'undefined') {
			lo = locale;
		} else if (!lo && typeof (locale) == 'undefined') {
			lo = 'vi';
		}
		var res = "";
		if (!isNaN(num)) {
			res = num.toString();
			if (locale == "vi") {
				res = res.replace('.', ',');
			}
		}
		return res;
	};
	return self;
})();
var BootBoxFactory = (function() {
	var self = this;
	self.buildCancel = function(cancelLabel, cancel) {
		var can = cancel ? cancel : function() {
			bootbox.hideAll();
		};
		return self.buildButton(cancelLabel, 'fa fa-remove', 'btn-danger', can);
	};
	self.buildConfirm = function(warningMessage, cancelLabel, cancel, confirm) {
		var rev = self.buildCancel(cancelLabel, cancel);
		var conf = self.buildButton(confirmLabel, 'fa-check', '', confirm);
		bootbox.dialog(warningMessage, [rev, conf]);
	};
	self.buildButton = function(label, icon, classes, callback) {
		var basecls = "btn form-action-button pull-right";
		var obj = {
			"label" : label ? label : "",
			"icon" : icon ? icon : 'fa-check',
			"class" : classes ? basecls + " " + classes : basecls + " btn-primary",
			"callback" : callback
		};
		return obj;
	};
	return self;
})();
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
