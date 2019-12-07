olbius.factory('LanguageFactory', function($rootScope, StorageFactory) {
	var root = $rootScope;
	var self = this;
	self.change = function(lo){
		StorageFactory.setLocalItem('currentLanguage', lo);
	};
	self.getLabel = function(key){
		var label = "";
		try{
			label = root.Map[key][root.locale];
		}catch(e){
			label = key;
		}
		return label;
	};
	return self;
});
olbius.factory('NumberFactory', function($rootScope) {
	var root = $rootScope;
	var self = this;
	self.processNumberLocale = function(num){
		var locale = localStorage.getItem('locale');
		var res = "";
		if(!isNaN(num)){
			res = num.toString();
			if(locale == "vi"){
				res = res.replace('.', ',');
			}
		}
		return res;
	};
	self.FormatNumberBy3 = function(num, decpoint, sep) {
		if (num) {
			if (!sep) {
				sep = ",";
			}
			if (!decpoint) {
				decpoint = ".";
			}
			var num = num.toString();
			var a = num.split(decpoint);
			var x = a[0];
			var y = a[1];
			var z = "";
			if ( typeof (x) != "undefined") {
				for (var i = x.length - 1; i >= 0; i--)
					z += x.charAt(i);
				z = z.replace(/(\d{3})/g, "$1" + sep);
				if (z.slice(-sep.length) == sep)
					z = z.slice(0, -sep.length);
				x = "";
				for (var i = z.length - 1; i >= 0; i--)
					x += z.charAt(i);
				if (y && typeof (y) != "undefined" && y.length){
					var c = "";
					if(y.length > 2){
						for(var t = 0; t < 2; t++){
							c += y.charAt(t);
						}
					}else if(y.length < 2){
						c = y + "0";
					}
					x += decpoint + c;
				}else{
					x += decpoint + "00";
				}
			}
			return x;
		}
		return 0;
	};

	self.range = function(min, max, step) {
		step = step || 1;
		var input = [];
		for (var i = min; i <= max; i += step) {
			input.push(i);
		}
		return input;
	};
	return self;
});
olbius.factory('CalendarFactory', function($rootScope) {
	var root = $rootScope;
	var self = this;
	self.formatDateDMY = function(date) {
		if(!date)return;
		var today;
		if(typeof date.getMonth === 'function'){
			today = date;
		}else if(date.time){
			today = new Date(date.time);
		}else{
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
	self.getStartDate = function(date){
		if(!date)return;
		var today;
		if(typeof date.getMonth === 'function'){
			today = date;
		}else{
			today = new Date(date);
		}
		today.setHours(0);
		today.setMinutes(0);
		today.setSeconds(0);
		today.setMilliseconds(0);
		return self.formatDateDMY(today, true, true, true, true);
	};
	self.getEndDate = function(date){
		if(!date)return;
		var today;
		if(typeof date.getMonth === 'function'){
			today = date;
		}else{
			today = new Date(date);
		}
		today.setHours(23);
		today.setMinutes(59);
		today.setSeconds(59);
		today.setMilliseconds(999);
		return self.formatDateDMY(today, true, true, true, true);
	};

	self.formatDateYMD = function(date, h, m, s, mili) {
		if(!date)return;
		var today;
		if(typeof date.getMonth === 'function'){
			today = date;
		}else{
			today = new Date(date);
		}
		var dd = today.getDate();
		var mm = today.getMonth() + 1;
		var yyyy = today.getFullYear();
		var hh = today.getHours();
		var mi = today.getMinutes();
		var se = today.getSeconds();
		var mil = today.getMilliseconds();

		dd = dd < 10 ?("0" + dd) : (dd + "");
		mm = mm < 10 ?("0" + mm) : (mm + "");
		hh = hh < 10 ? ("0" + hh) : (hh + "");
		mi = mi < 10 ? ("0" + mi) : (mi + "");
		se = se < 10 ? ("0" + se) : (se + "");
		if(mil < 10){
			mil = "00" + mil;
		}else if(mil < 100){
			mil = "0" + mil;
		}
		var str = yyyy + '-' + mm + '-' + dd;
		if(h) str += " " + hh;
		if(m) str += ":" + mm;
		if(s) str += ":" + se;
		if(mili){
			str += "." + mil;
		}
		return str;
	};
	self.getCurrentDate = function() {
		var today = new Date();
		var dd = today.getDate();
		var mm = today.getMonth() + 1;
		// January is 0!
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

	self.getCurrentDateYMD = function() {
		var today = new Date();
		var dd = today.getDate();
		var mm = today.getMonth() + 1;
		// January is 0!
		var yyyy = today.getFullYear();
		if (dd < 10) {
			dd = '0' + dd;
		}
		if (mm < 10) {
			mm = '0' + mm;
		}
		today = yyyy + "-" + mm + '-' + dd;
		return today;
	};
	self.getTimeValue = function(time, isShowSecond){
		var date = new Date(time);
		if(!time || !date || isNaN(date.getMonth())){
			return "";
		}
		var min = date.getMinutes();
		var minStr = min < 10 ? "0" + min : min;
		var ho = date.getHours();
		var hoStr = ho < 10 ? "0" + ho : ho;
		var str = hoStr + ":" + minStr;
		if(isShowSecond){
			var se = date.getHours();
			var seStr = se < 10 ? "0" + se : se;
			str += ":" + date.getSeconds();
		}
		return str;
	};
	return self;
});
olbius.factory('StorageFactory', function($rootScope) {
	var root = $rootScope;
	var self = this;
	self.setLocalItem = function(key, item) {
		if ( typeof (item) == 'object') {
			localStorage.setItem(key, JSON.stringify(item));
		} else {
			localStorage.setItem(key, item);
		}
	};
	self.getLocalItem = function(key) {
		var x = localStorage.getItem(key);
		if(x && (x.indexOf("{")  != -1 || x.indexOf("[") != -1)){
			return $.parseJSON(localStorage.getItem(key));
		}
		return x;
	};
	self.removeLocalItem = function(key){
		localStorage.removeItem(key);
	};
	return self;
});
olbius.factory('DialogFactory', function($rootScope, $compile, LanguageFactory) {
	var root = $rootScope;
	var self = this;
	self.buildConfirm = function(scope, header, body, submit, close, compile, force) {
		var cl = self.getButton(scope, close, 'fa fa-times', LanguageFactory.getLabel('Cancel'), 'btn btn-app btn-danger', function(dialogRef) {
			if (close && typeof (close) == 'function') {
				close();
			}
			dialogRef.close();
		});
		return self.buildDialog(scope, header, body, {
			onshow : function(dialogRef) {
				if (compile) {
					$compile(dialogRef.getModalBody())(scope);
				}
			},
			closable : false,
			buttons : [self.getButton(scope, submit), cl]
		});
	};
	self.buildAlert = function(scope, title, body, close, force) {
		return self.buildDialog(scope, title, body, {
			closable : true,
			buttons : [self.getButton(scope, close)]
		});
	};
	self.buildDialog = function(scope, header, body, options, force) {
		if (!force) {
			self.clearAllDialog();
		}
		var dialog = BootstrapDialog.show({
			title : header,
			message : body,
			onshow : options ? options.onshow : null,
			type : BootstrapDialog.TYPE_SUCCESS,
			closable : options ? options.closable : false,
			spinicon : options ? options.spinicon : 'fa fa-spinner',
			buttons : options ? options.buttons : []
		});
		if (!header) {
			dialog.getModalHeader().hide();
		}
		root.listDialog.push(dialog);
		return dialog;
	};
	self.getButton = function(scope, callback, icon, label, css, action) {
		return {
			icon : icon ? icon : 'fa fa-check',
			label : label ? label : LanguageFactory.getLabel('Ok'),
			cssClass : css ? css : 'btn btn-app btn-primary',
			action : action ? action : function(dialogRef) {
				if (callback && typeof (callback) == "function") {
					callback(dialogRef);
				} else {
					dialogRef.enableButtons(false);
					dialogRef.setClosable(false);
					dialogRef.close();
				}
			}
		};
	};
	self.closeDialog = function(scope, dialog) {
		if (dialog) {
			dialog.close();
		}
	};
	self.clearAllDialog = function(scope) {
		for (var x in root.listDialog) {
			root.listDialog[x].close();
		}
		root.listDialog = [];
	};
	return self;
});
olbius.factory('Header', function($route) {
	var self = this;
	self.getHeight = function() {
		self.element = $('#header');
		return self.element.height();
	};
	self.setHeight = function(height) {
		self.element = $('#header');
		return self.element.height(height);
	};
	self.getWidth = function() {
		self.element = $('#header');
		return self.element.width();
	};
	self.setWidth = function(width) {
		self.element = $('#header');
		return self.element.width(width);
	};
	return self;
});
olbius.factory('SidebarFactory', function($http, $timeout) {
	var self = this;
	self.sidebar = null;
	self.init = function(wrapper, width) {
		self.sidebar = new Snap({
			element : wrapper,
			disable : 'right',
			maxPosition : width,
			easing : 'linear',
			transitionSpeed : 0.1
		});
	};
	self.close = function() {
		if (self.sidebar) {
			$timeout(function() {
				self.sidebar.close();
			});
		}
	};
	self.open = function() {
		if (self.sidebar) {
			$timeout(function() {
				self.sidebar.open('left');
			});
		}
	};
	self.disable = function() {
		if (self.sidebar) {
			self.sidebar.disable();
		}
	};
	self.enable = function() {
		if (self.sidebar) {
			self.sidebar.enable();
		}
	};
	return self;
});