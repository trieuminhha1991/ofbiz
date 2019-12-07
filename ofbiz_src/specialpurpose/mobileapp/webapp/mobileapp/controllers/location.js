/*global todomvc, angular */
'use strict';

/**
 * The main controller for the app. The controller: - retrieves and persists the
 * model via the todoStorage service - exposes the model to the template and
 * provides event handlers
 */
olbius.controller('LocationController', ["$rootScope", "$scope", "$controller", '$timeout', 'uiGmapGoogleMapApi', "CustomerService", "GPS", 'SidebarFactory', 'LanguageFactory',
function(root, self, $controller,$timeout, GoogleMapApi, CustomerService, GPS, SidebarFactory, LanguageFactory) {
	$.extend(this, $controller('BaseController', {
		$scope : self
	}));
	self.roadid = '';
	self.marker = {
		id : 0,
		coords : {},
		options : {
			draggable : false
		}
	};
	self.currentCustomer = {};
	self.customers = [];
	self.currentCustomerId = "";
	self.isControl = false;
	self.isShowAddressForm = false;
	self.currentLocation = {};
	self.selected = {};
	self.$on('$viewContentLoaded', function() {
		self.setHeader('Maps', "/main", false);
		self.height = 'auto';
		self.disableScrollX();
		self.disableScrollY();
		var ct = $('#content');
		ct.bind('touchstart', function() {
			SidebarFactory.disable();
		});
		ct.bind('touchend', function() {
			SidebarFactory.enable();
		});
		ct.bind('touchcancel', function() {
			SidebarFactory.enable();
		});
		if (!self.isControl) {
			self.currentCustomer = self.getLocalItem('currentCustomer');
			self.customers = self.getLocalItem('customers');
			if (self.currentCustomer) {
				self.currentCustomerId = self.currentCustomer.partyIdTo;
			}
		}
		GPS.getCurrentLocation(self, false);
		$timeout(function(){
			if(self.gmap){
				self.gmap.updateCurrentLocation();
			}
		}, 1000);
	});
	self.$watch("currentLocation", function() {
		if (self.focus && self.currentLocation.latitude && self.currentLocation.longitude) {
			self.focus(self.currentLocation.latitude, self.currentLocation.longitude);
		}
	});

	self.saveLocation = function() {
		if (self.currentCustomerId) {
			var location = self.getSelectedPoint(true);
			var googlemap = self.getGoogleMap();
			var geocoder = new googlemap.Geocoder();
			var latlng = new google.maps.LatLng(location.latitude, location.longitude);
			geocoder.geocode({
				'latLng' : latlng
			}, function(results, status) {
				if (status == google.maps.GeocoderStatus.OK) {
					if (results.length) {
						var obj = results[0];
						var address = self.processAddress(obj.address_components);
						CustomerService.updateLocationCustomer(self.currentCustomerId, location, address, root.showLoading, root.hideLoading).then(function(res) {
							self.currentCustomer.latitude = location.latitude;
							self.currentCustomer.longitude = location.longitude;
							self.currentCustomer.address1 = address.name;
							self.currentCustomer.city = address.stateProvinceGeoId;
							self.updateListCustomer(self.currentCustomer);
							self.setLocalItem('currentCustomer', self.currentCustomer);
							if (res._ERROR_MESSAGE_ || res._ERROR_MESSAGE_LIST_) {
								self.buildAlert('', LanguageFactory.getLabel('SendError'));
							} else {
								self.buildAlert('', LanguageFactory.getLabel('UpdateSuccess'));
								self.setLocalItem('isUpdateLocation', true);
							}
						});
					}
				} else {
					self.buildAlert("", LanguageFactory.getLabel('PleaseTryAgain'));
				}
			});
		}
	};
}]);
