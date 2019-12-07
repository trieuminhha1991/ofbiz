app.controller('PromotionTypeController', function($rootScope, $scope, $controller, PromotionService) {
	var self = $scope;
	var root = $rootScope;
	self.self = self;
	$.extend(this, $controller('BaseController', {
		$scope : self
	}));
	self.promotionTypes = [];
	self.$on('$ionicView.enter', function(){
		self.getDataType("results", self.initPromotionType, PromotionService.getPromotionType, null, config.storage.promotionType, self.showLoading, self.hideLoading);
	});
	self.initPromotionType = function(data){
		if(data && data.length){
			self.promotionTypes = data;
		}
	};
});
