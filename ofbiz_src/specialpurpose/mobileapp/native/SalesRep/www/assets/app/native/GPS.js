app.factory('GPS', ['$rootScope', '$q', '$cordovaGeolocation', '$state', 'StorageFactory', 'Popup', function($rootScope, $q, geolocation, $state, StorageFactory, Popup) {
	var self = this;
	self.options = config.map;
	// self.getLocation = geolocation.getLocation;
	self.getLocation = geolocation.getCurrentPosition;
	self.getDistance = function(first, second) {
		var R = 6371; // km
		var phi1 = self.toRadians(first.latitude);
		var phi2 = self.toRadians(second.latitude);
		var deltaphi = self.toRadians(second.latitude - first.latitude);
		var deltalamda = self.toRadians(second.longitude - first.longitude);
		var a = Math.sin(deltaphi / 2) * Math.sin(deltaphi / 2)
				+ Math.cos(phi1) * Math.cos(phi2) * Math.sin(deltalamda / 2)
				* Math.sin(deltalamda / 2);
		var c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		var d = R * c;
		return d;
	};
	self.getLocationCallback = function(){
		return	self.getLocation().then(function(position) {
			return {
				latitude : position.coords.latitude,
				longitude : position.coords.longitude
			};
		}, function(err){console.log(err);}, self.options);
	};
	self.getCurrentLocation = function(scope, isWatch, callback) {
		var position = StorageFactory.getLocalItem(config.storage.currentLocation);
		if(self.requireRelocate() || !position){
			var action = function(){
				self.getLocation().then(function(position) {
					scope.currentLocation = {
						latitude : position.coords.latitude,
						longitude : position.coords.longitude
					};
					if(scope.currentLocation){
						StorageFactory.setLocalItem(config.storage.currentLocation, scope.currentLocation);
						if(typeof(callback) == 'function'){
							callback(scope.currentLocation);
						}
					}else if(typeof(callback) == 'function') callback();
				}, function(err) {
					if(typeof(callback) == 'function'){
						callback();
					}
					Popup.showError($rootScope.getLabel('checkGPS'));
				}, self.options);
				if(isWatch){
					self.watch = geolocation.watchPosition(self.options);
					localStorage.setItem("watchid", self.watch.watchId);
					self.watch.promise.then(function() { /* Not used */
					}, function(err) {
						Popup.showError($rootScope.getLabel('checkGPS'));
					}, function(position) {
						scope.currentLocation = {
							latitude : position.coords.latitude,
							longitude : position.coords.longitude
						};
					});
				}
			};
			var diag = cordova && cordova.plugins ? cordova.plugins.diagnostic : null;
			if(diag){
				var func = diag.isLocationEnabled;
				var red = function(err){
					var errf = function(){
						callback();
						Popup.buildConfirm(null, $rootScope.getLabel('checkGPS'), err);
					};
					func(function(enabled){
						if(enabled){
							action();
						}else{
							errf();
						}
					}, errf);
				};
				// if(ionic.Platform.isIOS()){
					diag.requestLocationAuthorization(function(status){
						if(diag.switchToLocationSettings) red(diag.switchToLocationSettings);
						else red(diag.switchToSettings);
					}, "always");
			}else action();
		}else{
			scope.currentLocation = position;
			StorageFactory.setLocalItem(config.storage.currentLocation, scope.currentLocation);
			if(position && callback && typeof(callback) == 'function'){
				callback(position);
			}
		}
	};
	self.requireRelocate = function(){
		var key = config.storage.lastCheckLocation;
		var lastTime = StorageFactory.getLocalItem(key);
		var date = new Date().getTime();
		if(!lastTime){
			StorageFactory.setLocalItem(key, date);
			return true;
		}else {
			var re = date - parseInt(lastTime);
			if(re > config.gps.delayTime){
				StorageFactory.setLocalItem(key, date);
				return true;
			}
		}
		return false;
	};
	self.toRadians = function(num) {
		var phi = num * Math.PI / 180;
		return phi;
	};
	self.checkDistance = function(scope, store) {
		var def = $q.defer();
		var position = StorageFactory.getLocalItem(config.storage.currentLocation);
		if(self.requireRelocate() || !position){
			return self.getLocation().then(function(position) {
				var latitude = position.coords.latitude;
				var longitude = position.coords.longitude;
				var distance = self.getDistance({
					latitude : latitude,
					longitude : longitude
				}, store);
				if (distance <= config.distance) {
					scope.isValidDistance = true;
				}
				return scope.isValidDistance;
			}, function(err) {
				Popup.showError($rootScope.getLabel('checkGPS'));
			}, self.options);
		}else{
			var latitude = position.latitude;
			var longitude = position.longitude;
			var distance = self.getDistance({
				latitude : latitude,
				longitude : longitude
			}, store);
			if (distance <= config.distance) {
				scope.isValidDistance = true;
			}
			def.resolve(scope.isValidDistance);
			return def.promise;
		}
	};
	self.processDistance = function(scope, current, store) {
		var distance = self.getDistance(current, store);
		if (distance <= config.distance) {
			scope.isValidDistance = true;
		}else{
			scope.isValidDistance = false;
		}
		scope.isValidDistance = true;
	};
	return self;
}]);
