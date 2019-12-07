app.controller('ProfileController', function($rootScope, $scope, $controller, $timeout, EmployeeService, AddressFactory) {
	var self = $scope;
	var root = $rootScope;
	self.self = self;
	$.extend(this, $controller('BaseController', {
		$scope : self
	}));
	self.address = {};
	self.init = function() {
		self.enter();
		self.account.countryGeoId = config.countryGeoId;
		AddressFactory.getGeo(self, config.countryGeoId, "PROVINCE", self.showLoading, self.hideLoading);
	};
	self.enter = function(){
		self.editable = false;
		self.options = {
			editable : false,
			preview : true,
			createdContent : true,
		};
		self.account = self.getLocalItem(config.storage.employee);
		self.account = self.account ? self.account : {};
		if(!self.account.fullName){
			var first = self.account.firstName ? self.account.firstName : "";
			var middle = self.account.middleName ? self.account.middleName : "";
			var last = self.account.lastName ? self.account.lastName : "";
			self.account.fullName = last + ' ' + middle + ' ' + first;
			self.account.fullName = self.account.fullName.trim();
		}
	};
	self.$watch("account.stateProvinceGeoId", function(){
		if(self.account && self.account.stateProvinceGeoId){
			AddressFactory.getGeo(self, self.account.stateProvinceGeoId, "DISTRICT", self.showLoading, self.hideLoading);
		}else{
			self.address.district = [];
		}
	});
	self.$watch("address.province", function(){
		if(!_.isEmpty(self.address.province) && _.isEmpty(self.account.stateProvinceGeoId)){
			self.account.stateProvinceGeoId = self.address.province[0].geoId;
		}
	});
	self.$watch("address.district", function(){
		if(!_.isEmpty(self.address.district)){
			var obj = _.where(self.address.district, {geoId : self.account.districtGeoId});
			if(_.isEmpty(obj)){
				self.account.districtGeoId = self.address.district[0].geoId;
			}
		}
	});
	self.$watch('account.avatar', function(){
		self.setLocalItem(config.storage.employee, self.account);
	});
	self.checkEdit = function(){
		self.options.editable = self.editable = true;
	};
	self.checkBeforeSend = function(){
		if(!self.validate()) return;
		self.buildConfirm(self.getLabel('Notification'), self.getLabel('ConfirmUpdateInformation'), self.updateProfile);
	}
	self.updateProfile = function() {
		EmployeeService.updateProfile(self.account, self.showLoading, self.hideLoading).then(function(data) {
			if (data._ERROR_MESSAGE_) {
				$timeout(function(){
					self.showError(self.getLabel('UpdateInfoError'));
				}, 500);
			} else {
				self.account.city = AddressFactory.getGeoName(self.province, self.account.stateProvinceGeoId);
				self.account.district = AddressFactory.getGeoName(self.district, self.account.districtGeoId);
				self.setLocalItem(config.storage.employee, self.account);
				$timeout(function(){
					self.showNotification(self.getLabel('UpdateInfoSuccess'));
				}, 500);
				self.editable = false;
				self.ProfileForm.reset();
			}
		}, function(err) {
			$timeout(function(){
				self.showError(self.getLabel('UpdateInfoError'));
			}, 500);
		});
	};
	self.validate = function(){
		return self.ProfileForm.validate();
	};
	self.$on('$ionicView.loaded', self.init);
	self.$on('$ionicView.enter', self.enter);
});
