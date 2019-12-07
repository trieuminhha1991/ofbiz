/*global todomvc, angular */
'use strict';

/**
 * The main controller for the app. The controller:
 * - retrieves and persists the model via the todoStorage service
 * - exposes the model to the template and provides event handlers
 */
olbius.controller('SaleController', function($rootScope, $scope, $controller, LanguageFactory, PromotionService, OrderService) {
	var self = $scope;
	var root = $rootScope;
	self.self = self;
	$.extend(this, $controller('BaseController', {
		$scope : self
	}));
	/*tab registration action*/
	self.promotionTypes = [];
	self.customer = {};
	self.productStoreId = "";
	self.registration = {
		productPromoRuleId : "",
		productPromoTypeId : "",
		productPromoId: "",
		partyId : ""
	};

	self.currentTab = self.getLocalItem('currentTab') ? self.getLocalItem('currentTab') : 0;
	self.activeTab = [true, false, false];
	self.$on('$viewContentLoaded',function(){
		$scope.setHeader('Promotions','main',false);
		self.initTab(self.currentTab);
		self.getDataType("listProductStore", self.initProductStore, OrderService.getProductStore);
		self.getDataType("results", self.initPromotionType, PromotionService.getPromotionType);
		self.getStore({size : configPage.pageSize, page: 0});
		if(self.customers && self.customers.content && self.customers.content.length){
			self.customer = self.customers.content[0];
		}
		for(var x in self.activeTab){
			self.activeTab[self.currentTab] = true;
		}
	});

	self.$watch('productStoreId', function(){
		self.getPromotions(self.registration.productPromoTypeId, self.productStoreId);
	});
	self.$watch('registration.productPromoTypeId', function(){
		self.getPromotions(self.registration.productPromoTypeId, self.productStoreId);
	});
	self.$watch('registration.productPromoId', function(){
		self.getPromotion();
	});
	self.initProductStore = function(data){
		if(data && data.length){
			self.productStoreId = data[0].productStoreId;
		}
		self.productStores = data;
	};
	self.initPromotionType = function(data){
		if(data && data.length){
			self.registration.productPromoTypeId = data[0].productPromoTypeId;
			self.promotionTypes = data;
		}
	};
	self.loadPromotions = function(type){
		self.changeState('sale/promotion/' + type);
	};
	self.getPromotions = function(type, stores){
		if(type && stores){
			console.log(type,stores);
			var data = {productPromoTypeId: type, productStores : JSON.stringify([stores])};
			self.getDataType("results", self.initPromotions, PromotionService.getPromotions, data);
		}
	};
	self.initPromotions = function(data){
		if(data && data.length){
			self.registration.productPromoId = data[0].productPromoId;
			self.promotions = data;
		}else{
			self.registration.productPromoId = "";
			self.promotions = [];
		}
	};
	self.getPromotion = function(){
		if(self.registration.productPromoId){
			PromotionService.getPromotionDetail(self.registration.productPromoId, root.showLoading, root.hideLoading).then(function(res){
				self.promotion = {
					rules : res.rules,
					info: res.info,
					stores: res.stores,
					roles: res.roles
				};
			}, root.hideLoading);
		}else{
			self.promotion = {};
		}
	};

	self.choosePromotionRule = function(productPromoRuleId){
		self.registration.productPromoRuleId = productPromoRuleId;
	};
	self.sendRegistration = function(){
		if(self.customer){
			self.registration.partyId = self.customer.partyIdTo;
			PromotionService.register(self.registration, root.showLoading, root.hideLoading).then(function(res){
				if(res['_ERROR_MESSAGE_'] || res['_ERROR_MESSAGE_LIST_']){
					self.buildAlert("", LanguageFactory.getLabel('RegisterAccError'));
				}else self.buildAlert("", LanguageFactory.getLabel('RegisterAccSuccess'));
			}, root.hideLoading);
		}
	};
	self.cancelRegistration = function(){
		self.reloadState();
	};
	/*tab marking action*/
	self.marking = {
		partyId : "",
		productPromoId: "",
		productPromoRuleId: "",
		resultEnumId: "PROMO_REG_EVAL_PASS",
		url: "",
	};
	self.camera = {
		options: {
			preview : false,
			createdContent: false
		},
		avatar: ""
	};
	self.promoRegSelected = null;
	self.customerRegistered = [];
	self.promotionRegistered = [];
	self.$watch('marking.partyId', function(){
		if(self.marking.partyId){
			self.getPromotionRegistered(self.marking.partyId);
		}
	});
	self.initTab = function(i){
		self.currentTab = i;
		self.setLocalItem('currentTab', i);
	};
	self.chooseTab = function(i){
		self.initTab(i);
		switch(self.currentTab){
			case 2 :
				PromotionService.getCustomerRegistered(root.showLoading, root.hideLoading).then(function(res){
					self.customerRegistered = res.results;
					if(self.customerRegistered && self.customerRegistered.length){
						self.marking.partyId = self.customerRegistered[0].partyId;
					}
				}, root.hideLoading);
				break;
		}
	};
	self.getPromotionRegistered = function(partyId){
		if(partyId){
			PromotionService.getPromotionRegistered(partyId, root.showLoading, root.hideLoading).then(function(res){
				self.promotionRegistered = res.results;
				if(self.promotionRegistered && self.promotionRegistered.length){
					self.promoRegSelected = self.promotionRegistered[0];
				}
			}, root.hideLoading);
		}
	};
	self.createRegistrationEvaluation = function(){
		if(self.promoRegSelected && self.promoRegSelected.productPromoRuleId && self.promoRegSelected.productPromoId){
			self.marking.productPromoRuleId = self.promoRegSelected.productPromoRuleId;
			self.marking.productPromoId = self.promoRegSelected.productPromoId;
			PromotionService.createRegistrationEvaluation(self.marking, root.showLoading, root.hideLoading).then(function(res){
				if(res['_ERROR_MESSAGE_'] || res['_ERROR_MESSAGE_LIST_']){
					if(res['_ERROR_MESSAGE_'].indexOf('duplicate')){
						self.buildAlert("", LanguageFactory.getLabel('Duplicate'));
					}else{
						self.buildAlert("", LanguageFactory.getLabel('MarkingError'));
					}
				}else{
					self.clearEvaluationForm();
					self.buildAlert("", LanguageFactory.getLabel('MarkingSuccess'));
				}
			}, root.hideLoading);
		}
	};
	self.clearEvaluationForm = function(){
		self.marking = {
			partyId : self.marking.partyId,
			productPromoId: "",
			productPromoRuleId: "",
			resultEnumId: "PROMO_REG_EVAL_PASS",
			url: "",
		};
		self.camera.avatar = "";
	};
});
