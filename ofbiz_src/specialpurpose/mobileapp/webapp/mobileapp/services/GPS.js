// olbius.factory('GPS', function($cordovaGeolocation, $route) {
olbius.factory('GPS', ['geolocation', '$route', function(geolocation, $route) {
	var self = this;
	self.options = configPage.map;
	self.getLocation = geolocation.getLocation;
	// self.getLocation = $cordovaGeolocation.getCurrentPosition;
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
		var position = scope.getLocalItem('currentLocation');
		if(scope.checkLastTimeCheckLocation() || !position){
			self.getLocation().then(function(position) {
				scope.currentLocation = {
					latitude : position.coords.latitude,
					longitude : position.coords.longitude
				};
				if(scope.currentLocation){
					scope.setLocalItem('currentLocation', scope.currentLocation);
					if(callback && typeof(callback) == 'function'){
						callback(scope.currentLocation);
					}
				}
			}, function(err) {
				if(callback && typeof(callback) == 'function'){
					callback();
				}
				if(!scope.confirmTurnOnDialog){
					scope.confirmTurnOnDialog = scope.buildConfirm("", "Bật kết nối GPS & thử lại", $route.reload);
				}else{
					scope.confirmTurnOnDialog.open();
				}
			}, self.options);
			if(isWatch){
				self.watch = $cordovaGeolocation.watchPosition(self.options);
				localStorage.setItem("watchid", self.watch.watchId);
				self.watch.promise.then(function() { /* Not used */
				}, function(err) {
					console.log("Cannot get current location " + JSON.stringify(err));
					bootbox.confirm("Bật kết nối GPS & thử lại", function(){
						$route.reload();
					});
				}, function(position) {
					scope.currentLocation = {
						latitude : position.coords.latitude,
						longitude : position.coords.longitude
					};
					console.log("current location watch" + JSON.stringify(scope.currentLocation));
				});
			}
		}else{
			scope.currentLocation = position;
			if(position && callback && typeof(callback) == 'function'){
				callback(position);
			}
		}
	};
	self.toRadians = function(num) {
		var phi = num * Math.PI / 180;
		return phi;
	};
	self.checkDistance = function(scope, store) {
		var position = scope.getLocalItem('currentLocation');
		if(scope.checkLastTimeCheckLocation() || !position){
			self.getLocation().then(function(position) {
				var latitude = position.coords.latitude;
				var longitude = position.coords.longitude;
				var distance = self.getDistance({
					latitude : latitude,
					longitude : longitude
				}, store);
				if (distance <= config.distance) {
					scope.isValidDistance = true;
				}
			}, function(err) {
				console.log("cannot get location : ", JSON.stringify(err));
				bootbox.confirm("Bật kết nối GPS & thử lại", function(){
					$route.reload();
				});
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
		}
	};
	self.processDistance = function(scope, current, store) {
		var distance = self.getDistance(current, store);
		if (distance <= config.distance) {
			console.log("valid");
			scope.isValidDistance = true;
		}else{
			console.log("invalid");
			scope.isValidDistance = false;
		}
		scope.isValidDistance = true;
	};
	return self;
}]);