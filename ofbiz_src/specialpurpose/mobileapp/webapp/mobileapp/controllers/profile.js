/*global todomvc, angular */
'use strict';

/**
 * The main controller for the app. The controller:
 * - retrieves and persists the model via the todoStorage service
 * - exposes the model to the template and provides event handlers
 */
olbius.controller('ProfileController', function($rootScope, $scope, $timeout, $controller, EmployeeService, AuthService, Sidebar, LanguageFactory) {
	var self = $scope;
	var root = $rootScope;
	$.extend(this, $controller('BaseController', {
		$scope : self
	}));
	self.camera = {
		loading : "AvatarLoading",
		options : {
			preview : false,
			createdContent: true
		},
		image: "",
	};
	self.account = self.getLocalItem('employee');
	self.password = {
		currentPassword : "",
		newPassword : "",
		passwordVerify : ""
	};

	self.avatarloading = "CameraLoading";
	self.provinces = [];
	self.districts = [];
	self.editable = false;
	self.$on('$viewContentLoaded', function() {
		self.setHeader('ProfileSalesMan', "/main", false);
	});

	self.$watch(function() { return Sidebar.getEmployee();}, function(val){
		if(Sidebar.getEmployee()){
			self.account = Sidebar.getEmployee();
			self.init();
		}
	}, true);

	self.init = function() {
		self.account = self.account ? self.account : {};
		if(!self.account.fullName){
			var first = self.account.firstName ? self.account.firstName : "";
			var middle = self.account.middleName ? self.account.middleName : "";
			var last = self.account.lastName ? self.account.lastName : "";
			self.account.fullName = last + ' ' + middle + ' ' + first;
			self.account.fullName = self.account.fullName.trim();
		}
		self.account.countryGeoId = configPage.countryGeoId;
		self.getDataType('listGeo', self.getProvinces, EmployeeService.getGeo, {
			geoId : 'VNM',
			geoTypeId: 'PROVINCE'
		});
	};
	self.$watch("account.stateProvinceGeoId", function(){
		if(self.account && self.account.stateProvinceGeoId){
			self.getDataType('listGeo', self.getDistricts, EmployeeService.getGeo, {
				geoId : self.account.stateProvinceGeoId,
				geoTypeId: 'DISTRICT'
			});
		}else{
			self.getDistricts([]);
		}
	});
	self.getProvinces = function(data){
		var obj = {
			geoName : LanguageFactory.getLabel('ChooseProvince'),
			geoId : "",
		};
		if(data.length){
			if(data[0].geoId){
				data.unshift(obj);
				self.provinces = data;
			}
		}else{
			self.provinces = [obj];
		}
		if(!self.account.stateProvinceGeoId){
			self.account.stateProvinceGeoId = self.provinces[0].geoId;
		}
	};
	self.getDistricts = function(data){
		var obj = {
			geoName : LanguageFactory.getLabel('ChooseDistrict'),
			geoId : "",
		};
		if(data.length){
			if(data[0].geoId){
				data.unshift(obj);
				self.districts = data;
			}
		}else{
			self.districts = [obj];
		}
		if(!self.account.districtGeoId){
			self.account.districtGeoId = self.districts[0].geoId;
		}
	};

	self.checkEdit = function() {
		self.editable = true;
	};

	self.updateProfile = function() {
		// if(self.camera){
			// self.account.image = self.camera.path;
		// }
		if(!self.ProfileForm.validate())return;
		if(self.camera && self.camera.path){
			self.setLocalItem("avatar", self.account.avatar);
		}
		if(self.account.address1){
			self.account.address1 = self.account.address1.trim();
		}
		if(self.account.fullName){
			self.account.fullName = self.account.fullName.trim();
		}
		if(!validateEmail(self.account.email)){
			return;
		}
		EmployeeService.updateProfile(self.account, root.showLoading, root.hideLoading).then(function(data) {
			if (data._ERROR_MESSAGE_) {
				self.buildAlert("", LanguageFactory.getLabel('UpdateInfoError'));
			} else {
				self.account.city = self.getGeoName(self.provinces, self.account.stateProvinceGeoId);
				self.account.district = self.getGeoName(self.districts, self.account.districtGeoId);
				self.setLocalItem("employee", self.account);
				Sidebar.setEmployee(self.employee);
				self.buildAlert("", LanguageFactory.getLabel('UpdateInfoSuccess'));
				self.editable = false;
			}
		}, function(err) {
			self.buildAlert("", LanguageFactory.getLabel('UpdateInfoError'));
		});
	};
	self.getGeoName = function(data, id){
		for(var x in data){
			if(data[x].geoId == id){
				return data[x].geoName;
			}
		}
		return "";
	};
	self.updatePassword = function() {
		// if(!self.PasswordForm.validate()){
			// return;
		// }
		if (self.password.newPassword != self.password.passwordVerify) {
			self.buildAlert("", LanguageFactory.getLabel('PasswordNotMatch'));
		} else if (!self.password.newPassword || !self.password.passwordVerify || !self.password.currentPassword) {
			self.buildAlert("", LanguageFactory.getLabel('PasswordNotMatch'));
		} else if(self.password.newPassword == self.password.passwordVerify == self.password.currentPassword){
			self.buildAlert("", LanguageFactory.getLabel('PasswordNotChange'));
		} else if(self.password.newPassword.length < configPage.passwordLength){
			self.buildAlert("", LanguageFactory.getLabel('PasswordTooShort'));
		} else {
			AuthService.updatePassword(self.password, root.showLoading, root.hideLoading).then(function(data) {
				if (data._ERROR_MESSAGE_) {
					self.buildAlert("", LanguageFactory.getLabel('SendError'));
				} else {
					self.resetForm('updatePasswordForm');
					self.buildAlert("", LanguageFactory.getLabel('UpdateSuccess'));
				}
			}, function(err) {
				self.buildAlert("", LanguageFactory.getLabel('SendError'));
			});
		}
	};
});
