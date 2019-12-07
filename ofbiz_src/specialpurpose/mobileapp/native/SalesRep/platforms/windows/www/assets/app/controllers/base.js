app.controller('BaseController', function($rootScope, $scope, $controller, $state, $timeout, $ionicPopup,
	$ionicLoading, $ionicHistory, AppFactory, StorageFactory, NumberFactory, CalendarFactory, LoadingFactory, Popup, GPS) {
	var self = $scope;
	var root = $rootScope;
	self.self = self;
	self.search = "";
	// _.extend(this, $controller('ScrollingController', {
		// $scope : self
	// }));
	self.getCurrentLocation = function(isWatch, callback) {
		GPS.getCurrentLocation(self, isWatch, callback);
	};
	self.showError = Popup.showError;
	self.showNotification = Popup.showNotification;
	self.buildAlert = Popup.buildAlert;
	self.buildConfirm = Popup.buildConfirm;
	self.processMessage = Popup.processMessage;
	self.getLabel = root.getLabel;
	self.showLoading = LoadingFactory.showLoading;
	self.hideLoading = LoadingFactory.hideLoading;
	self.changeState = function(state, params, options) {
		$state.go(state, params, options);
	};
	self.checkLastTimeCheckLocation = function() {
		var key = 'lastTimeCheckLocation';
		var lastTime = self.getLocalItem(key);
		var date = new Date().getTime();
		if (!lastTime) {
			self.setLocalItem(key, date);
			return true;
		} else {
			var re = date - lastTime;
			if (re > config.gps.delayTime) {
				self.setLocalItem(key, date);
				return true;
			}
		}
		return false;
	};
	self.setLocalItem = function(key, item) {
		return StorageFactory.setLocalItem(key, item);
	};
	self.getLocalItem = function(key) {
		return StorageFactory.getLocalItem(key);
	};
	self.removeLocalItem = function(key) {
		return StorageFactory.removeLocalItem(key);
	};

	self.getDataType = AppFactory.getDataType;
	self.formatNumberDecimal = function(num, decpoint, sep) {
		return NumberFactory.formatNumberDecimal(num, decpoint, sep);
	};
	self.formatDateDMY = CalendarFactory.formatDateDMY;
	self.formatDateYMD = CalendarFactory.formatDateYMD;
	self.getTimeValue = CalendarFactory.getTimeValue;
	self.getCurrentDateYMD = CalendarFactory.getCurrentDateYMD;
	self.resetForm = function(id) {
		var form = $('#' + id);
		form.find(':input').val('');
	};
	self.loadMoreData = function() {
		self.renderData();
		$scope.$broadcast('scroll.infiniteScrollComplete');
	};
	self.renderData = function() {
		if (isNaN(self.lastIndex))
			self.lastIndex = 0;
		self.lastIndex += config.listItem;
	};
});
