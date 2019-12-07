/*
 * controller for create order
 * */
olbius.controller('CreateOrderController', function($rootScope, $scope, $routeParams, $controller, $timeout, $compile, OrderService, ProductService, CustomerService, GPS, LanguageFactory) {
	var self = $scope;
	var root = $rootScope;
	self.self = self;
	$.extend(this, $controller('BaseController', {
		$scope : self
	}));
	self.currentStore = null;
	self.pageSize = 10;
	self.total = 0;
	self.promotions = [];
	self.products = [];
	self.productStores = [];
	self.subTotal = 0;
	self.grandTotal = 0;
	self.dialogOrder = null;
	self.orderProcessing = false;
	self.store = "";
	self.routeId = "";
	self.outOfRoute = $routeParams.outofroute ? $routeParams.outofroute : false;
	self.isValidDistance = true;
	self.$on('$viewContentLoaded', function() {
		self.setHeader('CreateOrder', "/main", false);
		self.customers = self.getLocalItem("customers");
		if(self.outOfRoute){
			self.getDataType('results', self.initRoad, CustomerService.getAllRoute);	
		}else if (!localStorage.currentCustomer) {
			self.changeState("store");
		} else {
			self.currentStore = self.getLocalItem('currentCustomer');
		}
		self.reloadDistance();
		self.initProductStore();
		self.getLastInventory();
	});
	self.$watch('routeId', function(){
		if(self.routeId){
			CustomerService.getStoreByRoad({
				routeId : self.routeId
			}).then(function(res){
				if(res.customers){
					self.customers.content = res.customers;
					try{
						self.currentStore = self.customers.content[0];
						self.initProduct();
					}catch(e){
						console.log(e);
					}
				}else{
					self.customers.content = [];
				}
			});
		}
	});
	self.initProductStore = function(){
		var key = "listProductStore";
		var data = self.getLocalItem(key);
		if(data && data.length){
			self.initProductStoreData(data);
		}else{
			OrderService.getProductStore().then(function(res){
				self.initProductStoreData(res.listProductStore);
				self.setLocalItem(key, res.listProductStore);
			});
		}
	};
	self.initProductStoreData = function(data){
		self.productStores = data;
		if(self.productStores.length){
			self.store = self.productStores[0];
		}
	};
	self.$watch('currentStore', function(){
		self.initProduct();
	});
	self.$watch('store', function(){
		self.initProduct();
	});
	/* check distance is valid to create order */
	self.initProduct = function() {
		try{
			var key = 'listProducts';
			var data = self.getLocalItem(key);
			if (data && data.length) {
				self.initProductData(data);
			}else if(self.currentStore && self.currentStore.partyIdTo && self.store.productStoreId){
				ProductService.getPriceTax(self.currentStore.partyIdTo, self.store.productStoreId, root.showLoading, root.hideLoading).then(function(res){
					if(res.listProducts){
						self.initProductData(res.listProducts);
					}
				});
				// ProductService.getAll(self.store.productStoreId).then(function(res){
					// if(res.listProduct){
						// self.initProductData(res.listProduct);
						// self.setLocalItem(key, res);
					// }
				// });
			}
		}catch(e){
			console.log(e);
		}
	};
	self.initProductData = function(data) {
		var arr = [];
		for (var x in data) {
			var obj = data[x];
			obj.expiry_date = "";
			obj.quantity = null;
			obj.isAdded = false;
			obj.productName = obj.productCode + " - " + obj.productName;
			arr[x] = obj;
		}
		self.products = arr;
	};
	self.initRoad = function(data) {
		self.routes = data;
		if(data && data.length){
			self.routeId = data[0].routeId;
		}
	};
	/* get latest check inventory of salesman */
	self.getLastInventory = function() {
		if (self.currentStore) {
			var key = "creatingorder_" + self.currentStore.partyIdTo;
			if (localStorage[key]) {
				var data = JSON.parse(localStorage[key]);
				for (var x in self.products) {
					var obj = self.findProduct(data, self.products[x].productId);
					if (obj) {
						self.products[x].quantity = obj.qtyInInventory;
					}
				}
			}
		}
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
	self.getAllProduct = function() {
		if (localStorage.getItem('listProducts')) {
			self.products = $.parseJSON(localStorage.getItem('listProducts'));
		}
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
	self.btcreateOrder = function() {
		var products = self.getBought();
		self.isDisableButton = false;
		if (self.checkOrderCreated() && self.store.productStoreId && self.currentStore, products.length) {
			OrderService.createCart(self.currentStore.partyIdTo, products, self.store.productStoreId, root.showLoading, root.hideLoading).then(function(res){
				var data = res.order;
				if(data){
					self.showCartInfo(data);
					if(!self.dialogOrder){
						self.taxTotal = self.grandTotal - self.subTotal;
						var bodyOrder = '<div horizontal-scrollable>'
						+ '<table class="table table-striped table-bordered table-hover products"><thead><tr>'
						+ '<th class="center">' + LanguageFactory.getLabel('ProductName') + '</th>'
						+ '<th class="center">' + LanguageFactory.getLabel('Price') + '</th>'
						+ '<th class="center">' + LanguageFactory.getLabel('QuantityShort') + '</th>'
						+ '</tr></thead><tbody>'
						+ "<tr ng-repeat='product in products' ng-if='product.quantity > 0 || product.isAdded'><td>{{product.productName}}</td>"
						+ "<td>{{FormatNumberBy3(product.price)}}</td>"
						+ "<td><input type='number' min='0' ng-model='product.quantity' validate-integer ng-change='requireUpdateCart()'/></td></tr>"
						+ "<tr ng-repeat='product in promotions' class='promos-product'><td>{{product.productName}}</td>"
						+ "<td>0</td>"
						+ "<td>{{product.quantity}}</td></tr>"
						+ "</tbody></table>"
						+ '<div class="payment-info row"><div class="col-lg-3 col-md-3 col-xs-3 col-sm-3"><button class="btn btn-app btn-primary calculate-order" ng-click="updateCart()" ng-if="!isDisableButton"><i class="fa fa-refresh"></i></button></div>'
						+ '<div class="col-lg-9 col-md-9 col-xs-9 col-sm-9"><div class="sub-total">'
						+ '<div class="sub-total-label">' + LanguageFactory.getLabel('CartSubTotal') + ': </div>'
						+ '<div class="info" >{{FormatNumberBy3(subTotal)}}<sup>đ</sup></div></div>'
						+ '<div class="sub-total"><div class="sub-total-label">'+LanguageFactory.getLabel('TaxTotal')+': </div>'
						+ '<div class="info" >{{FormatNumberBy3(taxTotal)}}<sup>đ</sup></div></div>'
						+ '<div class="sub-total"><div class="sub-total-label">' + LanguageFactory.getLabel('TotalPayment') + ': </div>'
						+ '<div class="info" >{{FormatNumberBy3(grandTotal)}}<sup>đ</sup></div></div>'
						+ '</div></div></div>';
						self.dialogOrder = self.buildConfirm(LanguageFactory.getLabel('OrderDetail'), bodyOrder, function(dialog){
							self.submitOrder();
						}, null, true);
					}else{
						self.dialogOrder.realize();
						self.dialogOrder.open();
					}
				}else if(res._ERROR_MESSAGE_){
					self.showError();
				}
			}, function(){
				self.showError();
			});
		} else {
			self.buildAlert(LanguageFactory.getLabel('OrderDetail'), LanguageFactory.getLabel('NotiChooseProductsInOrder'));
		}
	};
	self.showError = function(error){
		if(!error){
			error = LanguageFactory.getLabel('CreateOrderError');
		}
		self.buildAlert(null, error);
	};
	self.checkOrderCreated = function(){
		var flag = false;
		for(var x in self.products){
			if(self.products[x].quantity){
				flag = true;
				self.products[x].isAdded = true;
			}else{
				self.products[x].isAdded = false;
			}
		}
		return flag;
	};
	self.updateCart = function(){
		var products = self.getBought();
		OrderService.updateCart(self.currentStore.partyIdTo, products, self.store.productStoreId, root.showLoading, root.hideLoading).then(function(res){
			var data = res.order;
			if(res._ERROR_MESSAGE_){
				self.showError(LanguageFactory.getLabel('UpdateCartError'));
			}else if(data){
				self.dialogOrder.enableButtons(true);
				self.showCartInfo(data);
			}
		}, function(){
			root.hideLoading();
		});
	};
	/* submit order to server
	 * input: list product id vs quantity */
	self.submitOrder = function() {
		if (!self.checkOrderCreated()) {
			if(self.dialogOrder){
				self.dialogOrder.close();
			}
			self.buildAlert(LanguageFactory.getLabel('OrderDetail'), LanguageFactory.getLabel('NotiChooseProductsInOrder'));
			return;
		}
		if(!self.orderProcessing){
			self.orderProcessing = true;
		}else return;
		try{
			root.showLoading();
			GPS.getLocationCallback().then(function(pos) {
				if (pos && pos.latitude && pos.longitude) {
					var positionCreateOrder = {
						latitude : pos.latitude,
						longitude : pos.longitude
					};
					var products = self.getBought();
					if(products.length){
						var data = {
							customerId : self.currentStore.partyIdTo,
							productStoreId : self.store.productStoreId,
							products : JSON.stringify(products),
							latitude : positionCreateOrder.latitude,
							longitude : positionCreateOrder.longitude,
							exception : self.outOfRoute,
							routeId : self.routeId
						};
						OrderService.submitOrder(data, null, root.hideLoading).then(function(res) {
							if(res._ERROR_MESSAGE_){
								self.showError();
							}else{
								var data = res.order;
								self.showCartInfo(data, true);
								self.orderProcessing = false;
								self.updateCustomerOrder();
							}
						}, function(err) {
							root.hideLoading();
							self.showError();
						});
					}
				}else{
					self.buildAlert(LanguageFactory.getLabel('Notification'), LanguageFactory.getLabel('checkGPS'));
				}
			}, function(err) {
				root.hideLoading();
				self.buildAlert(LanguageFactory.getLabel('Notification'), LanguageFactory.getLabel('checkGPS'));
			});
		}catch(e){
			root.showLoading();
			console.log(e);
		}
	};
	self.updateCustomerOrder = function(){
		if(self.currentStore && self.currentStore.partyIdTo
			&& self.customers.content && self.customers.content.length
			&& _.indexOf(self.customers.routes, self.routeId) != -1){
			for(var x in self.customers.content){
				var obj = self.customers.content[x];
				if(obj.partyIdTo == self.currentStore.partyIdTo){
					obj.isOrder = true;
					self.setLocalItem('customers', self.customers);
					return;
				}
			}
		}
	};
	self.requireUpdateCart = function(){
		if(self.dialogOrder){
			self.dialogOrder.enableButtons(false);
		}
	};
	self.getBought = function(){
		var products = [];
		if(self.products.length){
			for(var x in self.products){
				var product = self.products[x];
				if(product.quantity){
					products.push({
						quantity: product.quantity,
						productId: product.productId,
						uom: product.uom
					});
				}
			}
		}
		return products;
	};
	self.showCartInfo = function(data, isDisableButton){
		self.promotions = data.promotions;
		self.subTotal = data.subTotal;
		self.grandTotal = data.grandTotal;
		self.taxTotal = self.grandTotal - self.subTotal;
		self.isDisableButton = isDisableButton;
		if(isDisableButton){
			var cl = self.getButton(null, 'fa fa-times', LanguageFactory.getLabel('Cancel'), 'btn btn-app btn-danger', function(dialogRef) {
				dialogRef.close();
			});
			var sm = self.getButton(self.submitOrder);
			var bt = self.getButton(function(){
				if(self.dialogOrder){
					self.dialogOrder.close();
				}
				self.initProduct();
				$timeout(function(){
					self.dialogOrder.setButtons([sm, cl]);
				}, 200);
			});
			self.dialogOrder.setButtons([bt]);
		}
	};
	/*reset order*/
	self.viewOrder = function() {
		self.changeState('order/true');
	};
	var disableContainerScroll = function() {
		$("#container").css("overflow", "hidden");
	};
	var enableContainerScroll = function() {
		$("#container").css("overflow", "");
	};

});
