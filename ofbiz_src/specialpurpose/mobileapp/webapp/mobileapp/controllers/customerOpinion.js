/*global todomvc, angular */
'use strict';

/**
 * The main controller for the app. The controller: - retrieves and persists the
 * model via the todoStorage service - exposes the model to the template and
 * provides event handlers
 */
olbius.controller('CustomerOpinionController', function($rootScope, $scope, $location, $controller, $compile, CustomerOpinion, CustomerService, LanguageFactory) {
	var self = $scope;
	var root = $rootScope;
	self.self = self;
	$.extend(this, $controller('BaseController', {
		$scope : self
	}));
	//init Variable for get infomation FeedBack Customer
	self.customers = [];
	self.currentCustomer = self.getLocalItem('currentCustomer');
	self.customer = {
		customerId : "",
		comment : "",
	};
	self.processing = false;
	self.opponent = {
		partyId : "",
		comment : "",
		description : "",
		image : ""
	};
	self.dialogSubmitOpponent = null;
	self.dialogSubmitCustomer = null;
	self.$on('$viewContentLoaded', function() {
		self.setHeader('CommentCustomers', "/main", false);
		self.getDataType('listOpponent', self.initOpponents, CustomerOpinion.getListOpponent);
		self.getDataType('customers', self.initCustomers, CustomerService.getStoreByRoad);
	});
	self.initOpponents = function(data) {
		var obj = {
			groupName : LanguageFactory.getLabel('SelectOpponent'),
			partyId : "",
		};
		if(data.length){
			if(data[0].partyId){
				data.unshift(obj);
				self.opponents = data;
			}
		}else{
			self.opponents = [obj];
		}
		self.opponent.partyId = self.opponents[0].partyId;
	};
	self.initCustomers = function(data) {
		self.customers = data;
		if (data.length) {
			if(self.currentCustomer){
				for (var x in self.customers) {
					if (self.currentCustomer.partyIdTo == data[x].partyIdTo) {
						self.customer.customerId = self.currentCustomer.partyIdTo;
						return;
					}
				}
			}
		}else{
			self.customers = [obj];
		}
		self.customer.customerId = self.customers[0].partyIdTo;
	};

	self.submitFbCustomer = function() {
		if(!self.processing){
			if (self.customer.customerId && self.customer.comment) {
				self.dialogSubmitCustomer = self.buildConfirm("", LanguageFactory.getLabel('ConfirmInformation'), self.sendFbCustomer);
			} else{
				self.closeDialog(self.dialogSubmitCustomer);
				self.buildAlert("", LanguageFactory.getLabel('NotiInforCustomerMissing'));
			}

		}
	};
	self.sendFbCustomer = function(){
		self.processing = true;
		self.closeDialog(self.dialogSubmitCustomer);
		CustomerOpinion.submitFbCustomer(self.customer, root.showLoading, root.hideLoading).then(function(data) {
			if (data.communicationEventId) {
				self.buildAlert("", LanguageFactory.getLabel('NotificationSuccess'));
				self.resetForm('InfoCustomerForm');
			} else {
				self.buildAlert("", LanguageFactory.getLabel('NotificationError'));
			}
			self.processing = false;
			self.resetForm('InfoCustomerForm');
			self.initCustomers(self.customers);
		}, function(){
			self.processing = false;
		});
	};
	self.validateOpponent = function(){
		if(!self.opponent.partyId)
			return false;
		if(!self.opponent.comment || self.opponent.description)
			return true;
		return false;
	};
	self.submitInfoOpponent = function() {
		if(!self.processing){
			if(self.validateOpponent()){
				self.dialogSubmitOpponent = self.buildConfirm("", LanguageFactory.getLabel('ConfirmInformation'), self.sendInfoComponent);
			} else{
				self.closeDialog(self.dialogSubmitOpponent);
				self.buildAlert("", LanguageFactory.getLabel('NotiInforOppinionMissing'));
			}
		}
	};
	self.sendInfoComponent = function(){
		self.processing = true;
		self.closeDialog(self.dialogSubmitOpponent);
		CustomerOpinion.submitInfoOpponent(self.opponent, root.showLoading, root.hideLoading).then(function(data) {
			if (data.opponentEventId) {
				self.buildAlert("", LanguageFactory.getLabel('NotificationSuccess'));
			} else
				self.buildAlert("", LanguageFactory.getLabel('NotificationError'));
			self.processing = false;
			self.resetForm('FormOpponent');
			self.image = "";
			self.opponent.image = "";
			self.initOpponents(self.opponents);
		}, function(){
			self.processing = false;
		});
	};
});
