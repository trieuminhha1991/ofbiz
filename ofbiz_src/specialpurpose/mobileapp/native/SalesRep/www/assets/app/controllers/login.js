app.controller('LoginController', function($rootScope, $scope, $controller, AppFactory, AuthService, Network) {
	var root = $rootScope;
	var self = $scope;
	angular.extend(this, $controller('BaseController', {
		$scope : self
	}));

	self.$on('$ionicView.enter', function(){
		self.user = {
			USERNAME : '',
			PASSWORD : ''
		};
	});

	self.login = function() {
		if(!self.user.USERNAME || !self.user.PASSWORD){
			self.showError(self.getLabel('PleaseCheckUsernamePassword'));
			return;
		}
		console.log("FFFOLBIUS" + Network.isOffline());
		AuthService.login(self.user, self.showLoading, self.hideLoading).then(function(res) {
			if (res.login == "TRUE") {
				var customers = {};
				if(res.customers){
					customers.content = res.customers;
				}
				if(res.total){
					customers.total = res.total;
				}
				if(res.routes){
					customers.routes = res.routes;
				}
				if(!_.isEmpty(customers)){
					self.setLocalItem(config.storage.customers, customers);
				}
				if(res.routes){
					self.setLocalItem(config.storage.routetoday, res.routes);
				}
				if(res.others){
					var obj = {};
					if(res.others.customers){
						obj.content = res.others.customers;
						self.setLocalItem(config.storage.others, obj);
					}
				}
				self.changeState(config.defaultState);
				AppFactory.initApplicationData();
			} else {
				if(Network.isOffline()){
					self.buildConfirm(null, self.getLabel('PleaseCheckNetworkConnection'), Network.setting);
				}else{
					self.buildAlert(self.getLabel('LoginFailed'), self.getLabel('PleaseCheckUsernamePassword'));
				}
			}
		}, function() {
			self.hideLoading();
			if(Network.isOffline()){
				self.buildConfirm(null, self.getLabel('PleaseCheckNetworkConnection'), Network.setting);
			}else{
				self.buildAlert(self.getLabel('LoginFailed'), self.getLabel('PleaseCheckUsernamePassword'));
			}
		});
	};
});