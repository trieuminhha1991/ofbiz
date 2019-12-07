app.controller('PromotionRegistrationController', function($rootScope, $scope, $controller, $stateParams, PromotionService, PromotionFactory, StoreFactory) {
	var self = $scope;
	var root = $rootScope;
	self.self = self;
	$.extend(this, $controller('BaseController', {
		$scope : self
	}));
	self.productPromoId = $stateParams.productPromoId;
	self.currentCustomer = self.getLocalItem(config.storage.currentCustomer);
	self.promotions = self.getLocalItem(config.storage.promotions);
	self.registration = {
		productPromoId : self.productPromoId,
		partyId : self.currentCustomer ? self.currentCustomer.partyIdTo : ""
	};
	self.initLoad = function(){
		PromotionFactory.get(self, self.productPromoId);
		StoreFactory.getList(self, null);
	};
	self.$watch('customers', function(){
		if(!self.currentCustomer && self.customers
			&& self.customers.content && self.customers.content.length){
			self.currentCustomer = self.customers.content[0];
			self.registration.partyId = self.currentCustomer.partyIdTo;
		}
	});
	self.sendRegistration = function(){
		PromotionService.register(self.registration, self.showLoading, self.hideLoading).then(function(res){
			if(res['_ERROR_MESSAGE_'] || res['_ERROR_MESSAGE_LIST_']){
				self.showError(self.getLabel('RegisterAccError'));
			}else self.showNotification(self.getLabel('RegisterAccSuccess'));
		}, self.hideLoading);
	};
	self.$on('$ionicView.loaded', self.initLoad);
	// self.$on('$ionicView.enter', self.init);
});
