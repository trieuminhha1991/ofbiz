app.controller('InventoryController', function($rootScope, $scope, $controller, $ionicScrollDelegate, $timeout, InventoryService, ProductService, GPS, CartFactory) {
	var root = $rootScope;
	/* extends base controller */
	var self = $scope;
	_.extend(this, $controller('BaseController', {
		$scope : self
	}));
	_.extend(this, $controller('ProductController', {
		$scope : self
	}));
	_.extend(this, $controller('CustomerDetailController', {
		$scope : self
	}));
	self.previousInventory = [];
	self.currentInventory = [];
	self.deleted = [];
	self.globalEditable = false;
	/* init function */
	self.init = function(){
		self.header = self.getLabel('Inventory');
		self.currentCustomer = null;
		self.isValidDistance = true;
		self.expiredDate = config.inventory.expiredDate;
		self.products = null;
		self.ads = {
			posm : "true",
			marketing : "true"
		};
		self.customers = self.getLocalItem(config.storage.customers);
		if (!localStorage.currentCustomer) {
			self.changeState("tab.customer");
		} else {
			self.currentCustomer = self.getLocalItem(config.storage.currentCustomer);
			self.isInventory = self.currentCustomer.isInventory;
			self.initProductStore();
			self.getLastInventory(true);
			if(config.gps.required) self.reloadDistance();
		}
	};
	self.$watch('productStores', function() {
		if(self.productStores){
			self.initProduct(false, true);
		}
	});
	self.initProductAvailable = function(){
		var arr = [];
		for(var x in self.products){
			if(!_.findWhere(self.currentInventory, {productId : self.products[x].productId})){
				arr.push(self.products[x]);
			}
		}
		self.productAvailable = arr;
	};
	/* get latest check inventory of salesman */
	self.getLastInventory = function(force) {
		if (self.currentCustomer && force) {
			InventoryService.getByCustomer(self.currentCustomer.partyIdTo).then(function(res) {
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
	self.resetInventoryItem = function(){
		self.previousInventory = null;
		self.currentInventory = null;
	};
	self.initInventoryDate = function(data) {
		var res = Array();
		for (var x in data) {
			var wh = _.findWhere(res, {productId : data.productId});
			if(wh){
				wh.qtyInInventory += data.qtyInInventory;
			}else{
				var obj = {
					productId : data[x].productId,
					productName : data[x].productName,
					qtyInInventory : data[x].qtyInInventory,
					max : data[x].qtyInInventory ? data[x].qtyInInventory : 0
				};
				res.push(obj);
			}
		}
		self.previousInventory = _.clone(res);
		self.currentInventory = res;
	};
	self.updateProductInfo = function(productId){
		var obj;
		var tmp ;
		for(var x in self.currentInventory){
			obj = self.currentInventory[x];
			if(obj.productId && obj.editable && !obj.productName){
				tmp = _.findWhere(self.products, {productId : obj.productId});
				if(tmp){
					obj = _.extend(obj, tmp);
				}
			}
		}
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
					self.globalEditable = false;
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
			var obj = _.findWhere(self.customers.content, {partyIdTo : self.currentCustomer.partyIdTo});
			if(obj){
				obj.isInventory = true;
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
	self.getWarningLocation = function(){
		var dis = self.formatNumberDecimal(config.distance);
		var msg = self.processMessage(self.getLabel('ConditionViewStore'), [dis]);
		return msg;
	};
	/*check current inventory < last inventory*/
	self.addProductToInventoryList = function(product, editable) {
		self.showDeleteButton = false;
		var flag = true;
		for(var x in self.currentInventory){
			if(!self.currentInventory[x].productId) {
				flag = false;
				break;
			}
		}
		if(flag){
			if(editable){
				self.isInventory = false;
				self.globalEditable = true;
				self.initProductAvailable();
			}
			var obj = {
				productId : product.productId,
				qtyInInventory : 0,
				productName : product.productName,
				editable : editable
			};
			self.currentInventory.push(obj);
		}
	};
	self.showDeleteItem = function(){
		self.showDeleteButton = self.showDeleteButton ? false : true;
	};
	self.removeItem = function(index){
		if (self.currentInventory[index]) {
			var pr = self.currentInventory[index];
			if(pr.productId) self.deleted.push(pr);
			self.currentInventory.splice(index, 1);
		}
		if(!self.currentInventory.length) self.globalEditable = false;
	};
	self.undoItem = function(){
		if (self.deleted.length) {
			for (var x in self.deleted) {
				self.currentInventory.push(self.deleted[x])
			}
			self.deleted = [];
			self.showDeleteButton = false;
		}
	};
	self.$on('$ionicView.enter', self.init);
});
