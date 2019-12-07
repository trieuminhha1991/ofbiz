app.controller('PromotionController', function($rootScope, $scope, $controller, $stateParams, PromotionService, OrderService) {
	var self = $scope;
	var root = $rootScope;
	$.extend(this, $controller('BaseController', {
		$scope : self
	}));
	self.productPromoTypeId = $stateParams.type;
	self.promotions = {};
	self.productStores = [];
	self.store = "";
	self.$on('$ionicView.enter',function(){
		self.getDataType("listProductStore", self.initProductStore, OrderService.getProductStore, {}, config.storage.productStores, self.showLoading, self.hideLoading);
	});
	self.$watch('store', function(){
		if(self.store){
			self.promotionByType(self.productPromoTypeId, self.store.productStoreId);
		}
	});
	self.initProductStore = function(data){
		if(data.length){
			self.store = data[0];
		}
		self.productStores = data;
	};
	self.promotionByType = function(type, stores, force){
		if(type != "ORDER_PROMOS"){
			self.getPromotions(type, self.store.productStoreId, force);
		}else{
			self.getOrderPromotions(self.store.productStoreId, force);
		}
	};
	self.getPromotions = function(type, stores, force){
		if(type && stores){
			var data = {productPromoTypeId: type, productStores : JSON.stringify([stores])};
			var key = config.storage.promotions + "-" + self.productPromoTypeId;
			self.getDataType("results", self.initPromotions, PromotionService.getPromotions, data, key, null, function(){
				self.$broadcast('scroll.refreshComplete');
			}, force);
		}
	};
	self.getOrderPromotions = function(stores, force){
		if(stores){
			var data = {productStoreId : stores};
			var key = config.storage.promotions + "-" + self.productPromoTypeId;
			self.getDataType("promotions", self.initPromotions, PromotionService.getOrderPromotions, data, key, null, function(){
				self.$broadcast('scroll.refreshComplete');
			}, force);
		}
	};
	self.initPromotions = function(data){
		self.promotions = data;
	};
	self.loadPromotion = function(productPromoId){
		self.changeState('sale/promotiondetail/' + productPromoId);
	};
});
