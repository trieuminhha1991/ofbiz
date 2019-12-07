app.controller('PromotionGradingController', function($rootScope, $scope, $controller, $timeout, $stateParams, PromotionService, PromotionFactory, StoreFactory) {
	var self = $scope;
	var root = $rootScope;
	self.self = self;
	$.extend(this, $controller('BaseController', {
		$scope : self
	}));
	self.marking = {
		partyId : "",
		productPromoId : ""
	};
	self.customers = [];
	self.promotions = [];
	self.currentCustomer = self.getLocalItem(config.storage.currentCustomer);
	self.initLoad = function(){
		PromotionFactory.get(self, self.productPromoId);
		self.getCustomerRegistered();
	};

	self.$watch('customers', function(){
		if(self.customers && self.customers.length){
			if(self.currentCustomer){
				var arr = _.where(self.customers, {partyId : self.currentCustomer.partyIdTo});
				if(arr && arr.length){
					self.marking.partyId = self.currentCustomer.partyIdTo;
				}else{
					self.marking.partyId = self.customers[0].partyId;
				}
			}else{
				self.marking.partyId = self.customers[0].partyId;
			}
			self.getPromotionRegistered(self.marking.partyId);
		}
	});
	self.getCustomerRegistered = function(){
		PromotionService.getCustomerRegistered(self.showLoading, self.hideLoading).then(function(res){
			self.customers = res.results;
		}, self.hideLoading);
	};
	self.getPromotionRegistered = function(partyId){
		if(partyId){
			PromotionService.getPromotionRegistered(partyId, self.showLoading, self.hideLoading).then(function(res){
				self.promotions = res.results;
				if(self.promotions && self.promotions.length){
					self.marking.productPromoId = self.promotions[0].productPromoId;
				}
			}, self.hideLoading);
		}
	};
	self.takePicture = function(){
		if(self.camera){
			self.camera.chooseImage();
		}
	};
	self.confirm = function(){
		if(!self.form.validate()) return;
		self.buildConfirm(self.getLabel('Notification'), self.getLabel('ConfirmExhibitionEvaluation'), self.createRegistrationEvaluation)
	};
	self.createRegistrationEvaluation = function(){
		if(self.marking.productPromoId && self.marking.partyId && self.marking.resultEnumId){
			var obj = _.where(self.promotions , {productPromoId : self.marking.productPromoId})
			if(obj && obj.length){
				self.marking.productPromoRuleId = obj[0].productPromoRuleId;
			}else return;
			PromotionService.createRegistrationEvaluation(self.marking, self.showLoading, self.hideLoading).then(function(res){
				if(res['_ERROR_MESSAGE_'] || res['_ERROR_MESSAGE_LIST_']){
					self.showError(self.getLabel('MarkingError'));
				}else{
					self.clearEvaluationForm();
					self.showNotification(self.getLabel('MarkingSuccess'));
				}
			}, self.hideLoading);
		}
	};
	self.clearEvaluationForm = function(){
		self.marking = {
			partyId : self.marking.partyId,
			productPromoId: self.marking.productPromoId,
			productPromoRuleId: "",
			resultEnumId: "",
			url: "",
		};
		self.camera.image = "";
		self.form.reset();
	};
	self.$on('$ionicView.enter', self.initLoad);
});
