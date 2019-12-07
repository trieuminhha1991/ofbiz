app.controller('LocationController', ["$rootScope", "$scope", "$controller", '$stateParams', '$timeout', '$ionicHistory', 'uiGmapGoogleMapApi', "CustomerService", "GPS",
function(root, self, $controller, $stateParams, $timeout, $ionicHistory, GoogleMapApi, CustomerService, GPS, SidebarFactory) {
	$.extend(this, $controller('BaseController', {
		$scope : self
	}));
	self.self = self;
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
	self.isCreated = $stateParams.isCreated;
	self.$on('$ionicView.enter', function() {
		self.currentCustomer = self.getLocalItem(config.storage.currentCustomer);
		self.customers = self.getLocalItem(config.storage.customers);
		if (self.currentCustomer) {
			self.currentCustomerId = self.currentCustomer.partyIdTo;
		}
		// GPS.getCurrentLocation(self, false);
	});

	self.$watch("currentLocation", function() {
		if(!self.currentCustomer || !self.currentCustomer.latitude || self.currentCustomer.longitude){
			$timeout(function(){
				if(self.gmap){
					self.gmap.updateCurrentLocation();
				}
			}, 500)
		}else if (self.currentLocation.latitude && self.currentLocation.longitude) {
			// self.focus(self.currentLocation.latitude, self.currentLocation.longitude);
			var location = {
				latitude : self.currentCustomer.latitude,
				longitude : self.currentCustomer.longitude
			}
			$timeout(function(){
				if(self.gmap){
					self.gmap.updateCurrentLocation(location);
				}
			}, 500);

		}
	});

	self.saveLocation = function() {
		if (self.currentCustomerId || self.isCreated) {
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
						var fullAddress = obj.formatted_address;
						if(self.isCreated){
							address.fullAddress = fullAddress;
							address = _.extend(address, location);
							delete(address.marker);
							address.address = address.name;
							delete(address.name);
							console.log(address);
							self.saveLocalLocation(address);
						}else if(self.currentCustomerId){
							CustomerService.updateLocationCustomer(self.currentCustomerId, location, address, root.showLoading, root.hideLoading).then(function(res) {
								self.currentCustomer.latitude = location.latitude;
								self.currentCustomer.longitude = location.longitude;
								self.currentCustomer.address1 = address.name;
								self.currentCustomer.city = address.stateProvinceGeoId;
								self.updateListCustomer(self.currentCustomer);
								self.setLocalItem(config.storage.currentCustomer, self.currentCustomer);
								self.gmap.clearSearch();
								if (res._ERROR_MESSAGE_ || res._ERROR_MESSAGE_LIST_) {
									self.buildAlert('', self.getLabel('SendError'));
								} else {
									self.buildAlert(self.getLabel('Notification'), self.getLabel('UpdateSuccess'));
									self.setLocalItem(config.storage.isUpdateLocation, true);
								}
							});
						}
					}
				} else {
					self.buildAlert(self.getLabel('WarningMessage'), self.getLabel('PleaseTryAgain'));
				}
			});
		}
	};
	self.saveLocalLocation = function(address){
		self.setLocalItem(config.storage.customerCreatingLocation, address);
		$ionicHistory.goBack();
	};
	self.updateListCustomer = function(customer){
		var customers = self.getLocalItem(config.storage.customers);
		if(customer){
			for(var x in customers.content){
				var obj = customers.content[x];
				if(obj.partyIdTo == customer.partyIdTo){
					customers.content.splice(x, 0, customer);
					customers.content.splice(parseInt(x) + 1, 1);
					self.setLocalItem(config.storage.customers, customers);
					return;
				}
			}
		}
	};
}]);
