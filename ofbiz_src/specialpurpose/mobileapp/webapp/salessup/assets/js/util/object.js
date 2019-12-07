app.factory('Popup', function($timeout, $rootScope, $ionicPopup) {
	var self = this;
	self.showError = function(err, timeout) {
		if (timeout) {
			$timeout(function() {
				self.buildAlert($rootScope.getLabel('WarningMessage'), err);
			}, config.event.notificationDelay);
		} else
			self.buildAlert($rootScope.getLabel('WarningMessage'), err);
	};
	self.showNotification = function(msg, timeout) {
		if (timeout) {
			$timeout(function() {
				self.buildAlert($rootScope.getLabel('Notification'), msg);
			}, config.event.notificationDelay);
		} else
			self.buildAlert($rootScope.getLabel('Notification'), msg);
	};
	self.buildAlert = function(title, msg) {
		var alertPopup = $ionicPopup.alert({
			title : title,
			template : msg
		});
	};
	self.buildConfirm = function(title, mes, ok, reject) {
		if (!title) {
			title = $rootScope.getLabel('Notification');
		}
		var confirmPopup = $ionicPopup.confirm({
			title : title,
			template : mes
		});
		confirmPopup.then(function(res) {
			if (res && typeof (ok) === 'function') {
				ok(confirmPopup);
			} else if(!res && typeof(reject) == "function"){
				reject(confirmPopup)
			}
		});
	};
	self.processMessage = function(msg, bind) {
		if (!msg)
			return "";
		if (!bind || !bind.length)
			return msg;
		var msLength = msg.length;
		var bLength = bind.length;
		var i = 0;
		var tmp = "";
		for (var x = 0; x < msLength; x++) {
			if (msg[x] == "$") {
				if (bind[i] != null && bind[i] != undefined) {
					tmp += bind[i];
				} else {
					tmp += msg[x];
				}
				i++;
			} else {
				tmp += msg[x];
			}
		}
		return tmp;
	};
	return self;
});