app.controller('VisitingController', function($rootScope, $scope, $controller, $q, GPS, Request, CustomerService, NumberFactory) {
	var self = $scope;
	var root = $rootScope;
	_.extend(this, $controller('BaseController', {
		$scope : self
	}));
	self.checkLocationBeforeTrack = function(){
		if(config.gps.required){
			GPS.getCurrentLocation(self, false, function(location){
				self.checkVisitedCondition(null, location);
			});
		}
	};
	self.checkVisitedCondition = function(selectedPartyId, location) {
		var deferred = $q.defer();
		if(root.isActivated){
			return deferred.promise;
		}
		var currentCustomer = self.getLocalItem(config.storage.currentCustomer);
		if(!root.startVisitingTime){
			var tmp = self.getLocalItem(config.storage.startVisitingTime);
			self.setStartTime(tmp);
		}
		if(currentCustomer && selectedPartyId && currentCustomer.partyIdTo == selectedPartyId){
			deferred.resolve();
		}else if(currentCustomer){
			root.isActivated = true;
			Request.getServerTimestampAsLong().then(function(res){
				if(res.serverTimestamp){
					var cur = res.serverTimestamp;
					var duration = cur - root.startVisitingTime;
					if (duration >= config.minimumDuration) {
						var msg = self.getStoppingNotification();
						self.buildConfirm("", msg, function(){
							self.setStartTime();
							var flag = false;
							if(selectedPartyId){
								flag = self.trackVisiting(selectedPartyId, root.startVisitingTime, cur, location);
							}else{
								flag = self.trackVisiting(currentCustomer.partyIdTo, root.startVisitingTime, cur, location);
							}
							if(flag){
								self.removeLocalItem(config.storage.currentCustomer);
								root.visited = false;
							}
							root.isActivated = false;
							deferred.resolve();
						}, function(){root.isActivated = false;});
					} else {
						root.isActivated = false;
						if(currentCustomer){
							var msg = self.getWarningMessage(duration);
							self.showError(msg);
						}else{
							if(selectedPartyId){
								self.trackVisiting(selectedPartyId, root.startVisitingTime, cur);
							}else{
								self.trackVisiting(currentCustomer.partyIdTo, root.startVisitingTime, cur, location);
							}
							self.setStartTime();
							deferred.resolve();
						}
					}
				}
			}, function(){root.isActivated = false;});
		}else{
			deferred.resolve();
		}
		return deferred.promise;
	};
	self.getStoppingNotification = function() {
		var currentCustomer = self.getLocalItem(config.storage.currentCustomer);
		var label = self.getLabel('ConfirmStopVisiting');
		if (currentCustomer) {
			return msg = self.processMessage(label, [currentCustomer.groupName]);
		}
		return "";
	};
	self.setStartTime = function(time){
		var tmp = time ? $.parseJSON(time) : null;
		if(!tmp){
			Request.getServerTimestampAsLong().then(function(res){
				if(res.serverTimestamp){
					root.startVisitingTime = res.serverTimestamp;
					self.setLocalItem(config.storage.startVisitingTime, root.startVisitingTime);
				}
			});
		}else{
			root.startVisitingTime = tmp;
			self.setLocalItem(config.storage.startVisitingTime, root.startVisitingTime);
		}

	};
	self.getWarningMessage = function(duration) {
		var currentCustomer = self.getLocalItem(config.storage.currentCustomer);
		if (currentCustomer) {
			var fm = msToTime(duration);
			var label = self.getLabel("NotEnoughTimeVisiting");
			var cf = msToTime(config.minimumDuration);
			var msg = self.processMessage(label, [currentCustomer.groupName, fm.hour, fm.min, fm.sec, cf.min]);
			return msg;
		}
		return "";
	};
	/*
	 * Tracking location & time visiting action
	 */
	self.trackVisiting = function(partyId, fromDate, thruDate, location){
		if(location && partyId){
			CustomerService.createRouteHistory({
				partyIdTo : partyId,
				latitude : NumberFactory.processNumberLocale(location.latitude),
				longitude : NumberFactory.processNumberLocale(location.longitude),
				fromDateLong: fromDate,
				thruDateLong: thruDate
			}, self.showLoading, self.hideLoading).then(function(res){

			}, function(){
				self.showError(self.getLabel("PleaseCheckNetworkConnection"));
				self.hideLoading();
			});
			return true;
		}
		return false;
	};
});
