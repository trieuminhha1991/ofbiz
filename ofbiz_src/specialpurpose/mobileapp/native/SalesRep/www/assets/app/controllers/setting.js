app.controller('SettingController', function($rootScope, $scope, $controller, $ionicHistory, AuthService, EmployeeService, CartFactory) {
	var self = $scope;
	var root = $rootScope;
	self.employee = {};
	$.extend(this, $controller('BaseController', {
		$scope : self
	}));
	$.extend(this, $controller('VisitingController', {
		$scope : self
	}));
	/* watch current salesman's location change */
	self.$on("$ionicView.enter", function() {
		self.getProfile();
	});
	self.init = function(){
		self.currentCustomer = self.getLocalItem(config.storage.currentCustomer);
		if(self.currentCustomer) {
			root.visited = true;
		}
	};
	self.getProfile = function() {
		self.employee = self.getLocalItem(config.storage.employee);
		if(!self.employee)
		EmployeeService.getProfile().then(function(res) {

			if (res.employee) {
				self.employee = res.employee;
				self.setLocalItem(config.storage.employee, res.employee);
			}
		});
	};
	self.logout = function() {
		var confirm = function(dialog){
			var location = self.getLocalItem(config.storage.currentLocation);
			AuthService.logout().then(function() {
				$ionicHistory.clearCache();
				localStorage.clear();
				CartFactory.removeCart();
				if(!_.isEmpty(location)) self.setLocalItem(config.storage.currentLocation, location);
				self.changeState("login");
			}, function() {
				$ionicHistory.clearCache();
				localStorage.clear();
				CartFactory.removeCart();
				self.changeState("login");
			});
			dialog.close();
		};
		self.buildConfirm(self.getLabel('WarningMessage'), self.getLabel('NotiConfirmLogout'), confirm);
	};
	self.$on("$ionicView.enter", self.init);
});
