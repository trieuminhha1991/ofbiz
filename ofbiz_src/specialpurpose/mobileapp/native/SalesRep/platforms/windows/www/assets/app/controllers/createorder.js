app.controller('CreateOrderController', function($rootScope, $scope, $stateParams, $controller, $timeout, $interval, $compile, CartService, OrderService, ProductService, CustomerService, GPS, CartFactory) {
	var self = $scope;
	var root = $rootScope;
	self.self = self;
	_.extend(this, $controller('BaseController', {
		$scope : self
	}));
	_.extend(this, $controller('ProductController', {
		$scope : self
	}));
	_.extend(this, $controller('CartItemController', {
		$scope : self
	}));
	_.extend(this, $controller('CustomerDetailController', {
		$scope : self
	}));
	self.products = null;
	self.productStores = [];
	self.store = {};
	self.enableSwipe = true;
	self.canEditItem = true;
	self.lastIndex = 0;

	self.init = function() {
		self.other = $stateParams.other;
		self.isValidDistance = false;
		self.timeoutHandling = 0;
		self.currentCustomer = self.other ? self.getLocalItem(config.storage.currentCustomerOut) : self.getLocalItem(config.storage.currentCustomer);
		self.header = self.currentCustomer && self.currentCustomer.groupName ? "<i class='fa fa-cart-plus'></i> " + self.currentCustomer.groupName : self.getLabel('CreateOrder');
		if (!self.currentCustomer) {
			self.changeState("tab.customer", {
				other : self.other
			});
			return;
		}
		if(config.gps.required) self.reloadDistance();
		self.initProductStore();
	};

	self.$watch('productStores', function() {
		if (self.currentCustomer) {
			self.initProduct();
			self.createCart();
		}
	});

	self.getCartItem = function() {
		var key = config.storage.cartItem + '-' + self.currentCustomer.partyIdTo;
		return self.getLocalItem(key);
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
	self.createCart = function() {
		var isCreateCart = self.getLocalItem(config.storage.isCreateCart) == "true" ? true : false;
		if (self.store.productStoreId && self.currentCustomer && !isCreateCart) {
			CartFactory.createCart(self.currentCustomer.partyIdTo, self.store.productStoreId, []);
		}
	};
	self.removeCart = function() {
		if (self.store.productStoreId && self.currentCustomer) {
			CartService.emptycart(self.showLoading, self.hideLoading);
		}
	};
	self.btcreateOrder = function() {
		var products = self.getBought();
		CartFactory.updateCartItem(self.currentCustomer.partyIdTo, products);
		if (products.length) {
			self.changeState('tab.cart');
		} else {
			self.showError(self.getLabel('CartEmpty'));
		}
	};

	self.getBought = function() {
		var products = [];
		if (self.products.length) {
			for (var x in self.products) {
				var product = self.products[x];
				if (product.quantity) {
					products.push({
						productName : product.productName,
						quantity : product.quantity,
						productId : product.productId,
						uom : product.uom,
						image : product.image,
						image_small : product.image_small,
						price : product.price,
						priceVAT : product.priceVAT
					});
				}
			}
		}
		return products;
	};
	self.setQuantity = CartFactory.setQuantity;
	self.handleHoldQuantity = CartFactory.handleHoldQuantity;
	self.handleReleaseQuantity = function(product, key, dir) {
		CartFactory.handleReleaseQuantity(product, key, dir, self.updateCartItem);
	};
	self.changeQuantity = function(product, key) {
		CartFactory.changeQuantity(product, key, self.updateCartItem);
	};
	self.updateCartItem = function() {
		var products = self.getBought();
		CartFactory.updateCartItem(self.currentCustomer.partyIdTo, products);
	};
	self.$on('$ionicView.enter', self.init);
});
