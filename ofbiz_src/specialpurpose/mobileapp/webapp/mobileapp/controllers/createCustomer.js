/*global todomvc, angular */
'use strict';

/**
 * The main controller for the app. The controller: - retrieves and persists the
 * model via the todoStorage service - exposes the model to the template and
 * provides event handlers
 */

olbius.controller('CreateCustomerController', function($rootScope, $scope, $controller, CustomerService, GPS, LanguageFactory) {
	var self = $scope;
	var root = $rootScope;
	$.extend(this, $controller('BaseController', {
		$scope : self
	}));
	self.googlemap = {
		height : 400,
		options : {
			fixedLocationButton : false
		},
	};
	self.customer = {
		groupName : "",
		roadId : "",
		mobile : "",
		birthDay : "",
		sex : "",
		startDate : ""
	};
	self.address = {};
	self.routes = [];
	self.$on('$viewContentLoaded', function() {
		self.setHeader('CreateCustomer', "/customer", false);
		self.initData();
		self.getDataType('results', self.initRoad, CustomerService.getAllRoute);
		GPS.getCurrentLocation(self, false);
	});
	self.$watch('googlemap.element.markers', function(){
		if(self.googlemap.element){
			var markers = self.googlemap.element.markers;
			try{
				if(markers.length == 1){
					var obj = markers[0];
					var ev = obj.events;
					var loadAddress = function(ev){
						var ma = ev.formatted_address;
						var com = ev.address_components;
						self.address = self.processAddressComponent(com);
						self.customer.address = ma;
						self.$evalAsync();
					};
					if(ev){
						loadAddress(ev);
					}else{
						root.showLoading();
						self.googlemap.scope.getAddress({
							latitude : obj.latitude,
							longitude: obj.longitude
						}, function(res){
							root.hideLoading();
							if(res && res.length){
								loadAddress(res[0]);
							}
						});
					}
				}
			}catch(e){
				console.log(e);
			}

		}
	});
	self.initData = function(){
		self.customer = {
			customerName : "",
			officeSiteName : "",
			routeId : "",
			phone : "",
			birthDay : "",
			sex : "M",
			startDate : new Date(),
		};
		self.address = {};
	};
	self.$watch("currentLocation", function() {
		if (self.focus && self.currentLocation.latitude && self.currentLocation.longitude) {
			self.focus(self.currentLocation.latitude, self.currentLocation.longitude);
		}
	});
	self.initRoad = function(data) {
		self.routes = data;
		if(data && data.length){
			self.customer.routeId = data[0].routeId;
		}
	};
	self.sendRegistration = function() {
		var valid = self.customerInfo.validate();
		if(!valid) return;
		var point = self.getMarkerPoint();
		var obj = _.extend(_.clone(self.customer), _.clone(self.address));
		// var ppoint = {};
		// if(point && point.latitude && point.longitude){
			// var lat = self.processNumberLocale(point.latitude);
			// var lng = self.processNumberLocale(point.longitude);
			// ppoint = {
				// latitude : lat,
				// longitude : lng
			// };
		// }
		obj = _.extend(obj, point);
		obj.startDate = self.formatDateYMD(self.customer.startDate);
		obj.birthDay = self.formatDateYMD(self.customer.birthDay);
		CustomerService.createCustomerAgent(obj, root.showLoading, root.hideLoading).then(function(data) {
			if (data.customerId || (!data['_ERROR_MESSAGE_'] && !data['_ERROR_MESSAGE_LIST_'])) {
				self.buildAlert("", LanguageFactory.getLabel('CreateCustomerSuccess'));
				self.initData();
				self.initRoad(self.routes);
				self.googlemap.scope.clearSearch();
				self.customerInfo.reset();
			}else{
				self.buildAlert("", LanguageFactory.getLabel('CreateCustomerError'));
			}
		}, root.hideLoading);
	};
	self.validateAddress = function(){
		if(!self.customer.address || !self.address.countryGeoId || !self.address.stateProvinceGeoId){
			return false;
		}
		return true;
	};
});
