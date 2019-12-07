/*global todomvc, angular */
'use strict';

/**
 * The main controller for the app. The controller:
 * - retrieves and persists the model via the todoStorage service
 * - exposes the model to the template and provides event handlers
 */
olbius.controller('PromotionDetailController', function($rootScope, $scope, $controller, $routeParams, PromotionService) {
	var self = $scope;
	var root = $rootScope;
	$.extend(this, $controller('BaseController', {
		$scope : self
	}));
	self.productPromoId = $routeParams.productPromoId;
	self.promotion = {};
	self.$on('$viewContentLoaded',function(){
		$scope.setHeader('Promotions','',false);
		self.getPromotion();
	});
	self.getPromotion = function(){
		if(self.productPromoId){
			PromotionService.getPromotionDetail(self.productPromoId).then(function(res){
				self.promotion = {
					rules : res.rules,
					info: res.info,
					stores: res.stores,
					roles: res.roles
				};
			});
		}
	};
});
