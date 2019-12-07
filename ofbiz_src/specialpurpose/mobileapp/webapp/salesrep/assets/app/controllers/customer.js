app.controller('CustomerController', function($rootScope, $scope, $controller, $stateParams, $timeout, $ionicScrollDelegate, CustomerService, GPS, StoreFactory, CartFactory) {
	var root = $rootScope;
	var self = $scope;
	_.extend(this, $controller('BaseController', {
		$scope : self
	}));
	_.extend(this, $controller('CustomerDetailController', {
		$scope : self
	}));
	_.extend(this, $controller('VisitingController', {
		$scope : self
	}));
	self.customers = {};
	self.datarenderer = [];
	self.calculated = [];
	self.lastIndex = 0;
	self.init = function() {
		root.isActivated = false;
		self.validReload = true;
		self.other = $stateParams.other != undefined ? $.parseJSON($stateParams.other) : null;
		self.routeId = $stateParams.routeId;
		self.isProcessingDistance = false;
		self.reloadStore();
		if (!self.other && !self.routeId) {
			self.header = self.getLabel('Customers');
		} else if (self.other == true) {
			self.header = self.getLabel('CustomersOther');
			self.isCurrentRoute = false;
		} else if (self.routeId) {
			self.header = self.getLabel('Customers');
			StoreFactory.getList(self, {
				'routeId' : self.routeId
			});
			self.isCurrentRoute = !self.other;
		}
		var isUpdated = self.getLocalItem(config.storage.isUpdateLocation);
		if (GPS.requireRelocate() || isUpdated || !self.datarenderer.length) {
			self.showLoading();
		}
		if (config.gps.required) {
			GPS.getCurrentLocation(self, false);
		}
		if (isUpdated) {
			self.reloadDistance(isUpdated);
			self.removeLocalItem(config.storage.isUpdateLocation);
		}
		self.currentCustomer = self.getLocalItem(config.storage.currentCustomer)
	};

	self.$watch("currentLocation", function() {
		if (self.currentLocation && self.currentLocation.latitude && self.currentLocation.longitude) {
			self.hideLoading();
			self.reloadDistance();
		}
	});
	self.$watch('customers.content', function() {
		self.hideLoading();
		self.reloadDistance();
		self.$broadcast('scroll.refreshComplete');
	});
	self.$watch('customers.routes', function() {
		if (self.customers && self.customers.routes) {
			self.isCurrentRoute = StoreFactory.isCurrentRoute(self.customers.routes, self.routeId);
			if (!self.isCurrentRoute) {
				self.setHeader();
			}
		}
	});
	self.reloadStore = function(force) {
		var before, after;
		if (!self.other && !self.routeId) {
			StoreFactory.getList(self, null, before, after, null, force);
		} else if (self.other == true) {
			StoreFactory.getList(self, {
				'other' : 'Y'
			}, before, after, force);
		}
	};
	self.setHeader = function() {
		self.header = "<i class='fa fa-road'></i> " + self.routeId;
	};
	self.reloadDistance = function(isUpdated) {
		if (!config.gps.required && self.customers && self.customers.content) {
			var tmp = [];
			var obj = {};
			for(var x in self.customers.content){
				obj = _.clone(self.customers.content[x]);
				obj.distance = self.getUndefinedLocation();
				tmp[x] = obj;
			}
			self.datarenderer = tmp;
		} else {
			if (self.datarenderer.length && !isUpdated) {
				self.hideLoading();
				return;
			}
			if (self.isProcessingDistance || !self.customers || !self.customers.content || (config.gps.required && !self.currentLocation))
				return;
			var point = {};
			var distance = 0;
			var temp = [];
			var location = self.currentLocation;
			self.isProcessingDistance = true;
			if (self.datarenderer.length && isUpdated) {
				self.currentCustomer = self.getLocalItem(config.storage.currentCustomer);
				if (currentCustomer) {
					var obj = _.where(self.datarenderer, {
						partyIdTo : currentCustomer.partyIdTo
					});
					if (obj.length) {
						var tmp = _.clone(self.datarenderer);
						if (currentCustomer.latitude && currentCustomer.longitude && location) {
							tmp = _.reject(tmp, {
								partyIdTo : currentCustomer.partyIdTo
							})
							point = {
								latitude : currentCustomer.latitude,
								longitude : currentCustomer.longitude
							};
							distance = GPS.getDistance(location, point);
							currentCustomer.distance = {
								origin : distance,
								text : convertDistance(distance),
								valid : true
							};
							self.datarenderer = self.sortDistance(currentCustomer, tmp);
						} else {
							tmp = _.reject(tmp, {
								partyIdTo : currentCustomer.partyIdTo
							})
							tmp.distance = self.getUndefinedLocation();
							temp.push(data);
						}
					}
				}
			} else {
				for (var x in self.customers.content) {
					var data = self.customers.content[x];
					var obj = {
						escapeName : removeUnicodeVietnamese(data.groupName),
						escapeAddress1 : removeUnicodeVietnamese(data.address1),
						escapeCity : removeUnicodeVietnamese(data.city)
					};
					data = _.extend(data, obj);
					if (data.latitude && data.longitude && location) {
						point = {
							latitude : data.latitude,
							longitude : data.longitude
						};
						distance = GPS.getDistance(location, point);
						data.distance = {
							origin : distance,
							text : convertDistance(distance),
							valid : true
						};
						temp = self.sortDistance(data, temp);
					} else {
						data.distance = self.getUndefinedLocation();
						data.isBirthDateToday = self.checkBirthDate(data.birthDate);
						temp.push(data);
					}
				}
				self.datarenderer = [];
				self.lastIndex = 0;
				self.datarenderer = temp;
			}
			self.renderData();
			self.isCalculateDistance = true;
			$timeout(function() {
				self.isProcessingDistance = false;
				self.hideLoading();
			}, 500);
		};
	}
	self.getUndefinedLocation = function() {
		return {
			origin : config.maxdistance,
			text : self.getLabel('UndefinedLocation'),
			valid : false
		};
	};
	self.sortDistance = function(obj, arr) {
		if (arr.length == 0) {
			arr.push(obj);
			return arr;
		} else {
			for (var x = 0; x < arr.length; x++) {
				if (arr[x].distance.origin > obj.distance.origin) {
					arr.splice(x, 0, obj);
					return arr;
				}
			}
		}
		arr.push(obj);
		return arr;
	};
	self.checkBirthDate = function(time) {
		if (time) {
			var d = new Date(time);
			var cur = new Date();
			var month = d.getMonth();
			var date = d.getDate();
			var curMonth = cur.getMonth();
			var curDate = cur.getDate();
			if (!isNaN(month) && !isNaN(date) && month == curMonth && date == curDate)
				return true;
		}
		return false;
	};
	/*to inventory customer & create order screen*/

	self.toStore = function(customer) {
		var cur = self.getLocalItem(config.storage.cartOwner);
		if (cur != customer.partyIdTo) {
			CartFactory.removeCart();
		}
		if (self.isCurrentRoute) {
			if(!config.gps.required){
				self.removeLocalItem(config.storage.other);
				self.setLocalItem(config.storage.currentCustomer, customer);
				self.changeState("tab.inventory");
			}else{
				GPS.checkDistance(self, {
					latitude : customer.latitude,
					longitude : customer.longitude
				}).then(function(res) {
					if (res) {
						// self.checkVisitedCondition(customer.partyIdTo, self.currentLocation).then(function(){
						self.removeLocalItem(config.storage.other);
						self.setLocalItem(config.storage.currentCustomer, customer);
						self.changeState("tab.inventory");
						self.trackVisiting(customer.partyIdTo, null, null, self.currentLocation);
						// });
					} else {
						var msg = self.processMessage(self.getLabel("ConditionViewStore"), [config.distance]);
						self.showError(msg);
					}
				})
			}
		} else {
			self.setLocalItem(config.storage.other, self.isCurrentRoute);
			self.setLocalItem(config.storage.currentCustomerOut, customer);
			self.changeState("tab.createorder", {
				other : self.isCurrentRoute
			});
		}
	};
	self.updateLocation = function(customer) {
		if (self.isCurrentRoute) {
			self.setLocalItem(config.storage.currentCustomer, customer);
			self.changeState("tab.location");
		}
	};

	self.$on("$ionicView.enter", self.init);
});
