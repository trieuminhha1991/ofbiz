app.controller('OpinionController', function($rootScope, $scope, $controller, $timeout, $stateParams, CustomerOpinion, StoreFactory) {
	var self = $scope;
	var root = $rootScope;
	self.self = self;
	$.extend(this, $controller('BaseController', {
		$scope : self
	}));
	self.customer = {
		customerId : "",
		comment : ""
	};
	self.customers = [];
	self.promotions = [];
	self.currentCustomer = self.getLocalItem(config.storage.currentCustomer);
	self.initLoad = function(){
		StoreFactory.getList(self, null, self.showLoading, self.hideLoading);
	};
	self.$watch('customers.content', function(){
		if(self.currentCustomer && !_.isEmpty(self.currentCustomer.partyIdTo)){
			self.customer.customerId = self.currentCustomer.partyIdTo;
		}else if(!_.isEmpty(self.customers.content)){
			self.customer.customerId = self.customers.content[0].partyIdTo;
		}
	});
	self.checkConditionBefore = function(){
		if(self.isValid())
			self.buildConfirm(self.getLabel('Notification'), self.getLabel('ConfirmSendFeedBack'), self.submitCustomerFeedback);
	};
	self.isValid = function(){
		return self.feedback.validate();
	};
	self.submitCustomerFeedback = function(){
		if(self.isValid()){
			CustomerOpinion.submitFbCustomer(self.customer, self.showLoading, self.hideLoading).then(function(data) {
				if (data.communicationEventId) {
					$timeout(function(){
						self.showNotification(self.getLabel('NotificationSuccess'));
					}, 500);
					self.resetForm();
				} else {
					$timeout(function(){
						self.showError(self.getLabel('NotificationError'));
					}, 500);
				}
				self.resetForm();
			}, self.hideLoading);
		}
	};
	self.resetForm = function(){
		self.customer = {
			customerId : "",
			comment : ""
		};
		self.feedback.reset();
	};
	self.$on('$ionicView.enter', self.initLoad);
});
