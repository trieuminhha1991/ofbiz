/*global todomvc, angular */
'use strict';

/**
 * The main controller for the app. The controller:
 * - retrieves and persists the model via the todoStorage service
 * - exposes the model to the template and provides event handlers
 */
olbius.controller('ProductController', function($rootScope, $scope, $controller,InventoryService, OrderService, ProductService) {
	var root = $rootScope;
	var self = $scope;
	$.extend(this, $controller('BaseController', {
		$scope : self
	}));
	self.inventories = [];
	self.orderItems = [];
	self.products = [];
	self.productStores = [];
	self.partyId = "";
	self.productStoreId = "";
	self.currentTab = self.getLocalItem("currentTab") ? self.getLocalItem("currentTab") : 'available';
	self.$on('$viewContentLoaded', function() {
		self.setHeader('ProductList', "/main", false);
		self.getDataType("listProductStore", self.initProductStoreData, OrderService.getProductStore);
		self.getStore({
			size : configPage.pageSize,
			page : 0
		});
		self.checkTab(self.currentTab);
	});
	self.$watch('customers.content', function() {
		var currentCustomer = self.getLocalItem('currentCustomer');
		if (self.customers && self.customers.content && currentCustomer) {
			self.partyId = currentCustomer.partyIdTo;
		}
		self.getProducts();
	});
	self.$watch('productStores', function() {
		if (self.productStores && self.productStores.length) {
			self.productStoreId = self.productStores[0].productStoreId;
		}
		self.getProducts();
	});
	self.checkTab = function(i) {
		if (self.currentTab != i) {
			self.currentTab = i;
			self.setLocalItem('currentTab', self.currentTab);
		}
	};
	self.initProductStoreData = function(data) {
		self.productStores = data;
	};
	self.getData = function() {
		switch(self.currentTab) {
		case 'available' :
			self.getProducts(true);
			break;
		case 'transit' :
			self.getOrderItems(true);
			break;
		}

	};
	self.getProducts = function(reload) {
		if(!self.productStoreId || !self.partyId){
			return;
		}
		var key = 'listProducts' + "-" + self.partyId + "-" + self.productStoreId;
		var data = self.getLocalItem(key);
		if (data && data.length && !reload) {
			self.products = data;
		} else if (self.partyId && self.productStoreId) {
			ProductService.getPriceTax(self.partyId, self.productStoreId, root.showLoading, root.hideLoading).then(function(res) {
				if (res.listProducts) {
					self.products = res.listProducts;
					self.setLocalItem(key, self.products);
				}
			});
		}
	};

	// self.getInventories = function() {
		// var key = 'StoreInventories';
		// var data = self.getLocalItem(key);
		// if (data && data.length) {
			// self.inventories = data;
		// } else if (self.productStoreId) {
			// InventoryService.getStoreInventories(self.productStoreId, root.showLoading, root.hideLoading).then(function(res) {
				// if (res.results) {
					// self.inventories = res.results;
					// self.setLocalItem(key, self.products);
				// }
			// });
		// }
	// };
	self.getOrderItems = function(reload) {
		if(!self.productStoreId){
			return;
		}
		var key = 'OrderItems' + self.productStoreId;
		var data = self.getLocalItem(key);
		if (data && data.length && !reload) {
			self.orderItems = data;
		} else if (self.productStoreId) {
			OrderService.getOrderTransit(self.productStoreId, root.showLoading, root.hideLoading).then(function(res) {
				if (res.results) {
					self.orderItems = res.results;
					self.setLocalItem(key, self.orderItems);
				}
			});
		}
	};
});
