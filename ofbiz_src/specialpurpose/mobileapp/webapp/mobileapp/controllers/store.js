/*global todomvc, angular */
'use strict';

/**
 * The main controller for the app. The controller:
 * - retrieves and persists the model via the todoStorage service
 * - exposes the model to the template and provides event handlers
 */
olbius.controller('StoreController', function($rootScope, $scope, $controller, $location, $compile, $timeout, CustomerService, GPS) {
	var self = $scope;
	var root = $rootScope;
	$.extend(this, $controller('BaseController', {
		$scope : self
	}));
	self.customers = {
		content : [],
		total: 0
	};
	self.customerRender = [];
	self.total = 0;
	self.current = 1;
	self.total = 0;
	self.currentLocation = null;
	self.isProcessingDistance = false;
	self.isCalculateDistance = false;
	self.itemHeight = 120;
	self.$on("$viewContentLoaded", function() {
		self.setHeader('ListCustomers', "/main", false);
		self.getStore({size: configPage.pageSize, page: 0}, root.showLoading, function(){
			if(self.currentLocation){
				root.hideLoading();
			}
		});
		GPS.getCurrentLocation(self, false, root.hideLoading);
		if(self.getLocalItem('isUpdateLocation')){
			self.reloadDistance();
			self.removeLocalItem('isUpdateLocation');
		}
	});
	/* watch current salesman's location change */
	self.$watch("currentLocation", function() {
		if (self.currentLocation && self.currentLocation.latitude && self.currentLocation.longitude) {
			self.reloadDistance();
		}
	});
	/*reload distance to current salesman'location*/
	self.reloadDistance = function() {
		if(self.isProcessingDistance || !self.currentLocation){
			return;
		}
		var point = {};
		var distance = 0;
		var temp = [];
		self.isProcessingDistance = true;
		for (var x in self.customers.content) {
			var data = self.customers.content[x];
			if (data.latitude && data.longitude) {
				point = {
					latitude : data.latitude,
					longitude : data.longitude
				};
				distance = GPS.getDistance(self.currentLocation, point);
				data.distance = {
					origin : distance,
					text : convertDistance(distance)
				};
				temp = self.sortDistance(data, temp);
			} else {
				data.distance = {
					origin : 1000000,
					text : "Không thể xác định"
				};
				data.isBirthDateToday = self.checkBirthDate(data.birthDate);
				temp.push(data);
			}
		}
		self.customers.content = temp;
		self.isCalculateDistance = true;
		$timeout(function(){
			self.isProcessingDistance = false;
		}, 500);
	};
	/*sort list customer by distance*/
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
	self.checkBirthDate = function(time){
		if(time){
			var d = new Date(time);
			var cur = new Date();
			var month = d.getMonth();
			var date = d.getDate();
			var curMonth = cur.getMonth();
			var curDate = cur.getDate();
			if(!isNaN(month) && !isNaN(date) && month == curMonth && date == curDate)
				return true;
		}
		return false;
	};
	/*to inventory customer & create order screen*/
	self.toStore = function(id) {
		var customer = self.customers.content[id];
		self.setLocalItem("currentCustomer", customer);
		$location.path("inventory");
	};
	self.toMap = function() {
		$location.path("location");
	};
});
