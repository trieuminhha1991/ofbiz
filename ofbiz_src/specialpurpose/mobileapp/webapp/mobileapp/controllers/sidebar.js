/*global todomvc, angular */
'use strict';

/**
 * The main controller for the app. The controller: - retrieves and persists the
 * model via the todoStorage service - exposes the model to the template and
 * provides event handlers
 */
olbius.controller('SidebarController', function($rootScope, $scope, $location, $window, $controller, SidebarFactory, EmployeeService, Sidebar, AuthService, LanguageFactory) {
	var self = $scope;
	var root = $rootScope;
	self.employee = {};
	$.extend(this, $controller('BaseController', {
		$scope : self
	}));
	self.$watch(function() { return Sidebar.getEmployee(); }, function(val){
		if(Sidebar.employee){
			self.employee = Sidebar.employee;
		}
	}, true);
	self.logout = function() {
		self.buildAlert(null, LanguageFactory.getLabel('NotiConfirmLogout'), function(dialog){
			localStorage.clear();
			AuthService.logout().then(function() {
				self.setLocalItem('login', false);
				self.changeState("login");
			}, function() {
				self.setLocalItem('login', false);
				self.changeState("login");
			});
			dialog.close();
		});
	};
	self.url = localStorage.getItem("serverUrl");
	self.element = angular.element($('#left'));
	SidebarFactory.init(document.getElementById('wrapper'), self.element.width());
	self.$watch("url", function() {
		if (self.url) {
			self.setLocalItem("serverUrl", self.url);
		}
	});
	self.getInfo = function() {
		self.employee = self.getLocalItem('employee');
		Sidebar.setEmployee(self.employee);
		if(!self.employee)
		EmployeeService.getProfile().then(function(res) {
			if (res.employee) {
				self.employee = res.employee;
				Sidebar.setEmployee(res.employee);
				self.setLocalItem('employee', res.employee);
			}
		});
	};
	self.updateProfile = function() {
		self.changeState('profile');
	};
	self.home = function() {
		self.changeState("main");
	};
	self.inventory = function() {
		self.changeState("store");
	};
	self.leave = function() {
		self.changeState("employee-leave");
	};
	self.createCustomer = function() {
		self.changeState("customer/create");
	};
	self.checkStateChange = function(href){
		SidebarFactory.close();
		self.changeState(href);
	};
	self.urlConfig = function() {
		localStorage.clear();
		if (self.url) {
			self.setLocalItem("serverUrl", self.url);
		}
		self.clearDB();
		$window.location.reload();
	};
	self.getInfo();
});
