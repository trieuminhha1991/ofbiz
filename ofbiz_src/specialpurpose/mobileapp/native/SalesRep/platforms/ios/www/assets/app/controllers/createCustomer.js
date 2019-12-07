app.controller('CreateCustomerController', function($rootScope, $scope, $controller, $stateParams, $ionicScrollDelegate, CustomerService, GPS, CalendarFactory, NumberFactory) {
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
		roadId : "",
		birthDay : "",
		sex : "M",
		startDate : "",
		contactPerson: []
	};
	self.startDate = {
		options : {
			date: self.customer.startDate,
	        mode: "date",
	        minDate: new Date(),
	        maxDate: config.date.max
		}
	};
	self.birthDay = {
		options : {
			date: self.customer.birthDay,
	        mode: "date",
	        minDate: config.date.min,
	        maxDate: new Date()
		}
	};
	self.routes = [];
	self.$on('$ionicView.loaded', function() {
		self.initData();
		self.updateLocation();
		self.getDataType('results', self.initRoad, CustomerService.getAllRoute);
	});
	self.$on('$ionicView.enter', function() {
		self.updateLocation();
		if(self.partyId){
			self.header = self.getLabel('UpdateCustomer');
		}else self.header = self.getLabel('CreateCustomer');
	});
	self.initData = function() {
		self.partyId = $stateParams.partyId;
		if(self.partyId){
			var key = config.storage.customer + "-" + self.partyId;
			self.selectedData = self.getLocalItem(key);
		}
		if(!self.selectedData){
			self.customer = {
				customerName : "",
				officeSiteName : "",
				routeId : "",
				phone : "",
				birthDay : "",
				gender : "M",
				contactPerson: [],
				startDate : new Date(),
				note: ""
			};
			self.address = {};
		}else{
			var rep = self.selectedData.representative;
			var routes = self.selectedData.routes;
			self.customer = {
				customerName : rep && rep.partyFullName ? rep.partyFullName : "",
				officeSiteName : self.selectedData.groupName,
				routeId : "",
				phone : self.selectedData.contactNumber,
				birthDay : rep && rep.birthDate ? new Date(rep.birthDate) : null,
				gender : rep && rep.gender ? rep.gender : "",
				contactPerson: self.selectedData.contacts,
				startDate : self.selectedData.startDate,
				partyId: self.selectedData.partyId,
				routeId: routes && routes.length ? routes[0] : "",
				note: self.selectedData.note
			};
		}
		if(self.camera){
			self.camera.image = "";
		}
	};

	self.initRoad = function(data) {
		self.routes = data;
		if (data && data.length) {
			self.customer.routeId = data[0].routeId;
		}
	};
	self.addContactPerson = function(){
		self.customer.contactPerson.push({
			name : "",
			phone: ""
		});
		$ionicScrollDelegate.scrollBottom();
	};
	self.updateLocation = function(){
		self.address = self.getLocalItem(config.storage.customerCreatingLocation);
		if(!self.address && self.selectedData){
			self.address = {
				fullAddress : self.selectedData.address
			}
		}
		if(self.address){
			self.customer = _.extend(self.customer, _.clone(self.address));
			delete(self.customer.fullAddress);
		}
	};
	self.removeContactPerson = function(i){
		self.customer.contactPerson.splice(i, 1);
		$ionicScrollDelegate.resize();
	};
	self.sendRegistration = function() {
		if(!self.customerform.validate()) return;
		self.buildConfirm("", self.getLabel('ConfirmCreateCustomer'), self.sendAction);
	};
	self.sendAction = function(){
		var obj = _.clone(self.customer);
		obj.contactPerson = typeof(self.customer.contactPerson) == "object" ? JSON.stringify(obj.contactPerson) : "";
		obj.startDate = CalendarFactory.formatDateYMD(self.customer.startDate);
		obj.birthDay = CalendarFactory.formatDateYMD(self.customer.birthDay);
		obj.latitude = NumberFactory.processNumberLocale(self.customer.latitude);
		obj.longitude = NumberFactory.processNumberLocale(self.customer.longitude);
		if(self.partyId){
			CustomerService.updateCustomerAgent(obj, self.showLoading, self.hideLoading).then(function(data) {
				if ((!data['_ERROR_MESSAGE_'] && !data['_ERROR_MESSAGE_LIST_'])) {
					self.showNotification(self.getLabel('UpdateCustomerSuccess'));
					self.removeLocalItem(config.storage.customerCreatingLocation);
					self.customerform.reset();
				} else {
					self.showError(self.getLabel('UpdateCustomerError'));
				}
			}, self.hideLoading);
		}else{
			CustomerService.createCustomerAgent(obj, self.showLoading, self.hideLoading).then(function(data) {
				if (data.customerId || (!data['_ERROR_MESSAGE_'] && !data['_ERROR_MESSAGE_LIST_'])) {
					self.showNotification(self.getLabel('CreateCustomerSuccess'));
					self.initData();
					self.initRoad(self.routes);
					self.removeLocalItem(config.storage.customerCreatingLocation);
					self.customerform.reset();
				} else {
					self.showError(self.getLabel('CreateCustomerError'));
				}
			}, self.hideLoading);
		}
	};
});
