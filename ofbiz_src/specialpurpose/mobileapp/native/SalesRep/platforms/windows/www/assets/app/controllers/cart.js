app.controller('CartController', function($rootScope, $scope, $controller, $interval, $timeout, OrderService, CartService, CartFactory, NumberFactory) {
	var self = $scope;
	var root = $rootScope;
	self.self = self;
	_.extend(this, $controller('BaseController', {
		$scope : self
	}));
	_.extend(this, $controller('ProductController', {
		$scope : self
	}));
	_.extend(this, $controller('CustomerDetailController', {
		$scope : self
	}));
	self.displayPrice = 'price';
	self.$watch('products', function(oldv, newv) {
		self.hasChanged = true;
		$timeout(function(){
			self.updateCart();
		}, 300);
	}, true);

	self.initEnter = function(){
		var products = self.getBought();
		self.other = self.getLocalItem(config.storage.other);
		self.currentCustomer = self.other ? self.getLocalItem(config.storage.currentCustomerOut) : self.getLocalItem(config.storage.currentCustomer);
		if(!products.length || self.orderId || !self.currentCustomer){
			self.cart = {};
		}
		self.isOrderExist = false;
		self.orderId = "";
		self.header = self.currentCustomer && self.currentCustomer.groupName ? self.currentCustomer.groupName : self.getLabel('CreateOrder');
		self.getCartItem();
		self.initProductStore();
		self.getCurrentLocation();
	};
	self.initLoad = function(){
		self.cart = {};
		self.products = [];
		self.deleted = [];
		self.productStores = [];
		self.store = null;
		self.hasChanged = false;
		self.canEditItem = true;
		self.orderProcessing = false;
		self.showDeleteButton = false;
		self.whileUpdateCart = false;
	};
	self.getCartItem = function() {
		if (self.currentCustomer) {
			var key = config.storage.cartItem + '-' + self.currentCustomer.partyIdTo;
			var cartItems = self.getLocalItem(key);
			self.products = cartItems;
		}
	};
	self.showDeleteCartItem = function() {
		if (self.showDeleteButton) {
			self.showDeleteButton = false;
			if (self.deleted.length) {
				self.updateCart();
			}
		} else
			self.showDeleteButton = true;
	};
	self.removeCartItem = function(index) {
		if (self.products[index]) {
			self.deleted.push(self.products[index]);
			self.products.splice(index, 1);
			CartFactory.updateCartItem(self.currentCustomer.partyIdTo, self.products);
		}
	};
	self.undoCartItem = function() {
		if (self.deleted.length) {
			for (var x in self.deleted) {
				self.products.push(self.deleted[x])
			}
			self.deleted = [];
		}
	};
	self.initProductStore = function() {
		var key = config.storage.productStores;
		var data = self.getLocalItem(key);
		if (data && data.length) {
			self.initProductStoreData(data);
		} else {
			OrderService.getProductStore().then(function(res) {
				self.initProductStoreData(res.listProductStore);
				self.setLocalItem(key, res.listProductStore);
			});
		}
	};
	self.initProductStoreData = function(data) {
		self.productStores = data;
		if (self.productStores.length) {
			self.store = self.productStores[0];
			self.updateCart();
		}
	};
	self.updateCart = function() {
		var products = self.getBought();
		if (!self.currentCustomer || !products.length || !self.hasChanged || self.whileUpdateCart) {
			self.$broadcast('scroll.refreshComplete');
			return;
		}
		self.whileUpdateCart = true;
		CartService.updateCart(self.currentCustomer.partyIdTo, products, self.store.productStoreId).then(function(res) {
			self.whileUpdateCart = false;
			self.hasChanged = false;
			var data = res.order;
			if (data) {
				self.deleted = [];
				self.showCartInfo(data);
			} else if (res._ERROR_MESSAGE_) {
				self.showError(self.getLabel('UpdateCartError'), config.event.notificationDelay);
			}
			self.$broadcast('scroll.refreshComplete');
		}, function() {
			self.$broadcast('scroll.refreshComplete');
			self.showError(self.getLabel('UpdateCartError'), config.event.notificationDelay);
		});
	};
	self.getBought = function() {
		var products = [];
		if (self.products && self.products.length) {
			for (var x in self.products) {
				var product = self.products[x];
				if (product.quantity) {
					products.push({
						quantity : product.quantity,
						productId : product.productId,
						uom : product.uom
					});
				}
			}
		}
		return products;
	};

	self.getCartInfo = function() {
		CartService.getCartInfo(self.showLoading, self.hideLoading).then(function(res) {
			self.showCartInfo(res.order);
		});
	};

	self.showCartInfo = function(data) {
		if(data){
			var promotions = data.promotions;
			data.promotions = CartFactory.groupPromotions(promotions);
			self.cart = data;
		}
	};

	self.checkoutOrder = function() {
		if (self.showDeleteButton) {
			self.showDeleteButton = false;
			self.updateCart();
			return;
		}
		if (self.orderProcessing)
			return;
		if (!self.currentLocation) {
			self.buildAlert(self.getLabel('WarningMessage'), self.getLabel('checkGPS'));
			return;
		}
		self.buildConfirm(self.getLabel('Notification'), self.getLabel('ConfirmCreateOrder'), self.checkoutAction);
	};
	self.checkoutAction = function() {
		try {
			self.orderProcessing = true;
			var products = self.getBought();
			if (products.length) {
				var data = {
					customerId : self.currentCustomer.partyIdTo,
					productStoreId : self.store.productStoreId,
					products : JSON.stringify(products),
					exception : self.other
				};
				var location = {};
				if(self.currentLocation){
					location = {
						latitude : NumberFactory.formatNumberDecimal(self.currentLocation.latitude),
						longitude : NumberFactory.formatNumberDecimal(self.currentLocation.longitude)
					};
				}
				data = angular.extend(data, location);
				CartService.checkout(data, self.showLoading, self.hideLoading).then(function(res) {
					if (res._ERROR_MESSAGE_) {
						self.showError(self.getLabel('CreateOrderError'));
					} else {
						self.showError(self.getLabel('CreateOrderSuccess'));
						self.orderProcessing = false;
						self.isOrderExist = true;
						self.orderId = res.orderId;
						self.showCartInfo(res.order);
						CartFactory.clearCartItem(self.currentCustomer.partyIdTo);
					}
				}, function(err) {
					root.hideLoading();
					self.showError(self.getLabel('CreateOrderError'));
				});
			}
		} catch(e) {
			self.showError(self.getLabel('CreateOrderError'));
			console.log(e);
		}
	};
	self.setQuantity = CartFactory.setQuantity;
	self.handleHoldQuantity = CartFactory.handleHoldQuantity;

	self.handleReleaseQuantity = function(product, key, dir, callback) {
		CartFactory.handleReleaseQuantity(product, key, dir, self.updateCartItem)
	};
	self.changeQuantity = function(product, key) {
		CartFactory.changeQuantity(product, key, self.updateCartItem);
	};

	self.updateCartItem = function() {
		self.updateCart();
		CartFactory.updateCartItem(self.currentCustomer.partyIdTo, self.products);
	};

	self.viewCurrentOrder = function() {
		if (self.orderId) {
			self.changeState('orderdetail', {
				id : self.orderId
			});
		}
	};
	self.$on('$ionicView.leave', function(){
		if(self.orderId) self.cart = {}
	});
	self.$on('$ionicView.loaded', self.initLoad);
	self.$on('$ionicView.enter', self.initEnter);
});
