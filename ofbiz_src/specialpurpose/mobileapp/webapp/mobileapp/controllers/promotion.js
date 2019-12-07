/*global todomvc, angular */
'use strict';

/**
 * The main controller for the app. The controller:
 * - retrieves and persists the model via the todoStorage service
 * - exposes the model to the template and provides event handlers
 */
olbius.controller('PromotionController', function($rootScope, $scope, $controller, $routeParams, PromotionService, OrderService) {
	var self = $scope;
	var root = $rootScope;
	$.extend(this, $controller('BaseController', {
		$scope : self
	}));
	self.productPromoTypeId = $routeParams.productPromoTypeId;
	self.promotions = {};
	self.productStores = [];
	self.store = "";
	self.$on('$viewContentLoaded',function(){
		$scope.setHeader($routeParams.productPromoTypeId,'',false);
		self.getDataType("listProductStore", self.initProductStore, OrderService.getProductStore);
	});
	self.$watch('store', function(){
		if(self.store){
			self.getPromotions(self.productPromoTypeId, self.store.productStoreId);
		}
	});
	self.initProductStore = function(data){
		if(data.length){
			self.store = data[0];
		}
		self.productStores = data;
	};
	self.getPromotions = function(type, stores){
		if(type && stores){
			var data = {productPromoTypeId: type, productStores : JSON.stringify([stores])};
			self.getDataType("results", self.initPromotions, PromotionService.getPromotions, data);
		}
	};
	self.initPromotions = function(data){
		self.promotions = data;
	};
	self.loadPromotion = function(productPromoId){
		self.changeState('sale/promotiondetail/' + productPromoId);
	};
});
