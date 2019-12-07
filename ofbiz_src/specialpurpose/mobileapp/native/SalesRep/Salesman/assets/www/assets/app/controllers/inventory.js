app.controller('InventoryController', function($rootScope, $scope, $controller, $ionicScrollDelegate, $timeout, InventoryService, ProductService, GPS, CartFactory) {
	var root = $rootScope;
	/* extends base controller */
	var self = $scope;
	$.extend(this, $controller('BaseController', {
		$scope : self
	}));
	/* init function */
	self.$on('$ionicView.enter', function() {
		self.header = self.getLabel('Inventory');
		self.currentCustomer = null;
		self.isValidDistance = false;
		self.products = null;
		self.previousInventory = null;
		self.currentInventory = null;
		self.ads = {
			posm : "true",
			marketing : "true"
		};
		self.customers = self.getLocalItem(config.storage.customers);
		if (!localStorage.currentCustomer) {
			self.changeState("tab.customer");
		} else {
			self.currentCustomer = self.getLocalItem(config.storage.currentCustomer);
			self.header += " - " + self.currentCustomer.groupName;
			self.isInventory = self.currentCustomer.isInventory;
			self.getLastInventory(true);
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
	self.getLastInventory = function(force) {
		if (self.currentCustomer && force) {
			InventoryService.getByCustomer(self.currentCustomer.partyIdTo, self.showLoading, self.hideLoading).then(function(res) {
				self.$broadcast('scroll.refreshComplete');
				self.hideLoading();
				if (res.inventoryCusInfo && res.inventoryCusInfo.length != 0) {
					var data = res.inventoryCusInfo;
					self.initInventoryDate(data);
				} else {
					self.isInventory = true;
				}

			}, function(res) {
				self.hideLoading();
				self.$broadcast('scroll.refreshComplete');
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
		if (self.currentCustomer) {
			var point = {
				latitude : self.currentCustomer.latitude,
				longitude : self.currentCustomer.longitude
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
			} else {
				checkinfo = false;
				self.isInventory = false;
				self.showError(self.getLabel('NotiInventory'), config.event.notificationDelay);
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
		if (self.currentCustomer && checkinfo) {
			InventoryService.check(inven, self.currentCustomer.partyIdTo, self.showLoading, self.hideLoading).then(function(data) {
				if (data.retMsg == "update_success") {
					self.isInventory = true;
					self.showNotification(self.getLabel('NotiInventorySuccess'), config.event.notificationDelay);
					self.updateCustomerInventory();
				} else {
					self.showError(self.getLabel('NotiInventoryError'), config.event.notificationDelay);
				}
			}, function() {
				self.showError(self.getLabel('NotiInventoryError'), config.event.notificationDelay);
				self.hideLoading();
			});
		}
	};
	self.updateCustomerInventory = function(){
		if(self.currentCustomer && self.currentCustomer.partyIdTo
			&& self.customers.content && self.customers.content.length){
			self.currentCustomer.isInventory = true;
			self.setLocalItem(config.storage.currentCustomer, self.currentCustomer);
			var obj = _.where(self.customers.content, {partyIdTo : self.currentCustomer.partyIdTo});
			if(obj && obj.length){
				obj[0].isInventory = true;
				self.setLocalItem(config.storage.customers, self.customers);
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
		if (self.currentCustomer) {
			var key = "inventory" + self.currentCustomer.partyIdTo;
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
		if (action && action == "copy" && self.currentCustomer) {
			var key = "creatingorder_" + self.currentCustomer.partyIdTo;
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
		self.changeState('tab.createorder');
	};
	self.setQuantity = CartFactory.setQuantity;
	self.handleHoldQuantity = CartFactory.handleHoldQuantity;
	self.handleReleaseQuantity = CartFactory.handleReleaseQuantity;
	self.changeQuantity = CartFactory.changeQuantity;
	self.editLocation = function() {
		self.changeState('tab.location');
	};
});
