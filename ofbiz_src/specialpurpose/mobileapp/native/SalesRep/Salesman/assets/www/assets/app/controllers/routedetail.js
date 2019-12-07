app.controller('RouteDetailController', function($rootScope, $scope, $controller, $stateParams, $ionicModal, CustomerService, StoreFactory) {
	var self = $scope;
	var root = $rootScope;
	self.selectedRouteId = "";
	$ionicModal.fromTemplateUrl('templates/item/routedetail.htm', {
		scope : self,
		animation : 'slide-in-up'
	}).then(function(modal) {
		self.routedetail = modal;
	});
	self.getRouteDetail = function(routeId, force){
		self.selectedRouteId = routeId;
		self.routedetail.show();
		self.getDataType('results', function(res){
			self.currentRoute = res
		}, CustomerService.getRouteDetail, {routeId : routeId}, config.storage.route, null, function(){
			self.$broadcast('scroll.refreshComplete');
		}, force);
	};
	self.notValidSchedule = function(day){
		try{
			if(self.currentRoute && _.contains(self.currentRoute.schedule, day))
				return true;
		}catch(e){
			console.log(e);
		}

	};
	self.$on('$ionicView.loaded', self.initLoad);
	// self.$on('$ionicView.enter', self.init);
});
