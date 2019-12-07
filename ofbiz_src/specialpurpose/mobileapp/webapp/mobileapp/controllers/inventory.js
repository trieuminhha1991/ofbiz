/*global todomvc, angular */
'use strict';

/**
 * The main controller for the app. The controller: - retrieves and persists the
 * model via the todoStorage service - exposes the model to the template and
 * provides event handlers
 */
olbius.controller('InventoryController', function($rootScope, $scope, $controller, $location, $routeParams, InventoryService, ProductService, GPS, LanguageFactory) {
	var root = $rootScope;
	/* extends base controller */
	var self = $scope;
	$.extend(this, $controller('BaseController', {
		$scope : self
	}));
	self.isFinish = $routeParams.isFinish;
	self.currentStore = null;
	self.isValidDistance = false;
	self.isInventory = false;
	self.products = null;
	self.previousInventory = null;
	self.currentInventory = null;
	self.ads = {
		posm : "true",
		marketing : "true"
	};
	/* init function */
	self.$on('$viewContentLoaded', function() {
		self.setHeader("Inventory", "/store", false);
		self.customers = self.getLocalItem("customers");
		if (!localStorage.currentCustomer) {
			self.changeState("store");
		} else {
			self.currentStore = self.getLocalItem('currentCustomer');
			self.getLastInventory();
			self.reloadDistance();
			self.initProduct();
		}
	});
	/* init product */
	self.initProduct = function() {
		var data = [];
		if (localStorage.products) {
			data = JSON.parse(localStorage.getItem("products"));
		} else {
			self.getAllProduct();
			if (localStorage.products) {
				data = JSON.parse(localStorage.getItem("products"));
			}
		};
		if (data) {
			for (var x in data) {
				data[x].quantity = 0;
			}
			self.products = data;
		}
	};
	self.getAllProduct = function() {
		ProductService.getAll(function() {
		}).then(function(res) {
			if (res.listProduct) {
				self.products = res.listProduct;
				localStorage.setItem("products", JSON.stringify(res.listProduct));
			}
		}, function(res) {
			log("error" + JSON.stringify(res));
		});
	};
	/* get latest check inventory of salesman */
	self.getLastInventory = function() {
		if (self.currentStore) {
			InventoryService.getByCustomer(self.currentStore.partyIdTo, root.showLoading, root.hideLoading).then(function(res) {
				root.hideLoading();
				if (res.inventoryCusInfo && res.inventoryCusInfo.length != 0) {
					var data = res.inventoryCusInfo;
					self.initInventoryDate(data);
				} else {
					self.isInventory = true;
				}

			}, function(res) {
				root.hideLoading();
			});
		}
	};
	self.initInventoryDate = function(data) {
		var res = Array();
		for (var x in data) {
			var obj = {
				ivid : data[x].ivid,
				productId : data[x].productId,
				productName : data[x].productName,
				orderDate : data[x].orderDate,
				qtyInInventory : data[x].qtyInInventory,
				orderId : data[x].orderId,
				customerId : data[x].partyId,
				max : data[x].qtyInInventory
			};
			res.push(obj);
		}
		self.previousInventory = JSON.parse(JSON.stringify(res));
		self.currentInventory = res;
	};
	/*check current inventory < last inventory*/
	self.addProductToInventoryList = function(product) {
		var obj = {
			productId : product.productId,
			qtyInInventory : 0,
			productName : product.productName,
			expiryDate : ""
		};
		self.currentInventory.push(obj);
	};
	self.reloadDistance = function() {
		if (self.currentStore) {
			var point = {
				latitude : self.currentStore.latitude,
				longitude : self.currentStore.longitude
			};
			GPS.checkDistance(self, point);
		}
	};

	/* check inventory */
	var modifiedStep = false;
	self.checkInventory = function() {
		var inven = Array();
		// var input = new Array();
		var tmp = Array();
		var checkinfo = false;
		var lastCheck = new Date().getTime();
		for (var x in self.currentInventory) {
			if (!isNaN(self.currentInventory[x].qtyInInventory)) {
				checkinfo = true;
				// tmp = [self.currentInventory[x].productId, self.currentInventory[x].productName, self.currentInventory[x].qtyInInventory, self.currentInventory[x].orderId, lastCheck, self.currentStore.partyIdTo, self.currentInventory[x].orderDate, "modified"];
			} else {
				checkinfo = false;
				self.isInventory = false;
				self.buildAlert('',LanguageFactory.getLabel('NotiInventory'));
				return;
			};
			self.currentInventory[x].max = self.currentInventory[x].qtyInInventory;
			inven.push({
				productId : self.currentInventory[x].productId,
				productName : self.currentInventory[x].productName,
				qtyInInventory : self.currentInventory[x].qtyInInventory,
				orderId : self.currentInventory[x].orderId
			});
			// input.push(tmp);
		}
		if (self.currentStore && checkinfo) {
			InventoryService.check(inven, self.currentStore.partyIdTo, root.showLoading, root.hideLoading).then(function(data) {
				if (data.retMsg == "update_success") {
					self.isInventory = true;
					self.buildAlert('', LanguageFactory.getLabel('NotiInventorySuccess'));
					self.updateCustomerInventory();
				} else {
					self.buildAlert('', LanguageFactory.getLabel('NotiInventoryError'));
				}
			}, function() {
				self.buildAlert('', LanguageFactory.getLabel('NotiInventoryError'));
				root.hideLoading();
			});
		}
	};
	self.updateCustomerInventory = function(){
		if(self.currentStore && self.currentStore.partyIdTo
			&& self.customers.content && self.customers.content.length){
			for(var x in self.customers.content){
				var obj = self.customers.content[x];
				if(obj.partyIdTo = self.currentStore.partyIdTo){
					obj.isInventory = true;
					self.setLocalItem('customers', self.customers);
					return;
				}
			}
		}
	};
	self.calculateOrder = function(min, max) {
		var res = Array();
		var remain = 0;
		for (var x in max) {
			var obj = JSON.parse(JSON.stringify(max[x]));
			var old = self.findProduct(min, obj.productId);
			if (old) {
				remain = Math.abs(obj.qtyInInventory - old.qtyInInventory);
				obj.qtyInInventory = remain;
			}
			res.push(obj);
		}
		return res;
	};
	self.findProduct = function(data, key) {
		var res = _.where(data, {
			productId : key
		});
		if (res.length) {
			var obj = {};
			var total = 0;
			for (var x in res) {
				obj = res[x];
				total += res[x].qtyInInventory;
			}
			obj.qtyInInventory = total;
			return obj;
		}
	};
	self.isValueInventory = function() {
		if (self.currentStore) {
			var key = "inventory" + self.currentStore.partyIdTo;
			var current = localStorage.getItem(key);
			if (!current) {
				return true;
			} else if (JSON.stringify(self.currentInventory) == current) {
				return false;
			}
			return true;
		} else {
			bootbox.alert("Thông tin khách hàng không đúng");
			return false;
		}

	};
	self.createOrder = function(action) {
		if (action && action == "copy" && self.currentStore) {
			var key = "creatingorder_" + self.currentStore.partyIdTo;
			var data = Array();
			if (self.currentInventory && self.previousInventory) {
				if (self.currentInventory.length < self.previousInventory.length) {
					data = self.calculateOrder(self.currentInventory, self.previousInventory);
				} else {
					data = self.calculateOrder(self.previousInventory, self.currentInventory);
				}
				if (data.length) {
					localStorage.setItem(key, JSON.stringify(data));
				}
			}
		}
		self.changeState('order/create');
	};
	self.editLocation = function() {
		self.changeState('location');
	};
});
