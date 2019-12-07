app.controller('PromotionDetailController', function($rootScope, $scope, $controller, $stateParams, PromotionFactory) {
	var self = $scope;
	var root = $rootScope;
	$.extend(this, $controller('BaseController', {
		$scope : self
	}));
	self.productPromoId = $stateParams.id;
	self.productPromoTypeId = $stateParams.type;
	self.promotion = {};
	self.initData = function(force){
		if(self.productPromoTypeId != 'ORDER_PROMOS'){
			self.showAddition = true;
			PromotionFactory.get(self, self.productPromoId, function(){
				self.$broadcast('scroll.refreshComplete');
			}, force);
		}else{
			self.showAddition = false;
			PromotionFactory.getOrderPromotion(self, self.productPromoTypeId, self.productPromoId, function(){
				self.$broadcast('scroll.refreshComplete');
			}, force);
		}
	};
	self.$on('$ionicView.enter', self.initData);

});
