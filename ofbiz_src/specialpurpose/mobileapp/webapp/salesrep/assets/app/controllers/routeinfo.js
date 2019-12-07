app.controller('RouteInfoController', function($rootScope, $scope, $controller, $stateParams, $ionicModal, CustomerService, StoreFactory) {
	var self = $scope;
	var root = $rootScope;
	self.self = self;
	$.extend(this, $controller('BaseController', {
		$scope : self
	}));
	$.extend(this, $controller('RouteDetailController', {
		$scope : self
	}));
	self.created = $stateParams.created == 'true' ? true : false;
	self.initLoad = function(){
		self.reloadData(false);
	};
	self.reloadData = function(force){
		StoreFactory.getList(self, null);
		self.getDataType('results', self.initRoad, CustomerService.getAllRoute, null, config.storage.routes, null, function(){
			self.$broadcast('scroll.refreshComplete');
		}, force);	
	};
	self.initRoad = function(data) {
		self.routes = data;
		if(data && data.length){
			self.routeId = data[0].routeId;
		}
	};
	self.isCurrentRoute = function(routeId){
		if(self.customers && self.customers.routes){
			return StoreFactory.isCurrentRoute(self.customers.routes, routeId);
		}
	};
	self.$on('$ionicView.loaded', self.initLoad);
	// self.$on('$ionicView.enter', self.init);
});
