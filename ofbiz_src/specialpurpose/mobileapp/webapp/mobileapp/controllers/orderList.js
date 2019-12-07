/*global todomvc, angular */
'use strict';
/**
 * The order controller for the order & create order screen. The controller:
 * Retrieves and persists the model via the order service
 */

/* controller for orders screen */
olbius.controller('OrderController', function($rootScope, $routeParams, $scope, $controller, $location, $window, OrderService, LanguageFactory) {
	var self = $scope;
	var root = $rootScope;
	$.extend(this, $controller('BaseController', {
		$scope : self
	}));
	self.iscurrent = $.parseJSON($routeParams.iscurrent);
	self.listCustomer = {};
	self.listOrder = {
		content : Array(),
		totalOrder : 0,
		orderCreated : 0,
		orderApproved : 0,
		orderCompleted : 0,
		orderCancelled : 0,
	};
	self.timeViewOrder = [{
		text : LanguageFactory.getLabel('Thisweek'),
		value : 'week'
	}, {
		text : LanguageFactory.getLabel('Thismonth'),
		value : 'month'
	}];

	self.time = {};
	self.currentCustomer = null;
	self.customerId = "";
	self.customers = [];
	self.currentPage = 0;
	self.$on('$viewContentLoaded', function() {
		self.setHeader('OrderList', "/main", false);
		self.init();
		self.loadData();
	});
	self.init = function() {
		try{
			var arr = self.getLocalItem('customers');
			if(arr.content){
				arr.content.unshift({
					partyIdTo : 'ALL',
					groupName : LanguageFactory.getLabel('All')
				});
				self.listCustomer = arr;
			}
			self.currentCustomer = self.getLocalItem('currentCustomer');
			self.customerId = self.currentCustomer.partyIdTo;
			self.time = self.timeViewOrder[0].value;
		}catch(e){
			console.log(e);
		}
	};
	self.loadData = function(){
		if(self.customerId && self.time){
			console.log(self.customerId, self.time);
			if(self.customerId != 'ALL'){
				self.customers = [self.customerId];
			}else if(self.listCustomer.content){
				var arr = [];
				for(var x in self.listCustomer.content){
					arr.push(self.listCustomer.content[x].partyIdTo);
				}
				self.customers = arr;
			}
			self.getData();
		}
	};
	self.getData = function(index) {
		OrderService.getList(self.customers, self.time, configPage.pageSize, index).then(function(data) {
			self.listOrder = {
				content : data.listOrder,
				totalOrder : data.totalOrder,
				size : data.size,
				orderCreated : data.orderCreated,
				orderApproved : data.orderApproved,
				orderCompleted : data.orderCompleted,
				orderCancelled : data.orderCancelled
			};
		}, function() {
			root.hideLoading();
		});
	};
	self.onSelectRow = function(id) {
		self.changeState('order/detail/' + id);
	};
});