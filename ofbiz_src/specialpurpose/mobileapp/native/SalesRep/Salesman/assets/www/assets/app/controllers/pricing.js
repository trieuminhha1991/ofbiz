app.controller('PricingController', function($rootScope, $scope, $stateParams, $controller, $timeout, $interval, $compile, CartService, OrderService, ProductService, CustomerService, GPS) {
	var self = $scope;
	var root = $rootScope;
	self.self = self;
	_.extend(this, $controller('BaseController', {
		$scope : self
	}));
	_.extend(this, $controller('ProductController', {
		$scope : self
	}));
	self.currentStore = null;
	self.products = null;
	self.productStores = [];
	self.store = "";
	self.routeId = "";
	self.$on('$ionicView.loaded', function() {
		self.initProductStore();
	});
	self.$on('$ionicView.enter', function(){
		if(self.products){
			self.initProduct();
		}
	});
	self.$watch('currentStore', function(){
		self.initProduct();
	});
	self.initProductStore = function(){
		var key = config.storage.productStores;
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
	/* check distance is valid to create order */
	self.initProduct = function(force) {
		try{
			var key = config.storage.products;
			var data = self.getLocalItem(key);
			if (data && data.length && !force) {
				self.initProductData(data);
			}else if(self.store.productStoreId){
				ProductService.getPriceTax(null, self.store.productStoreId).then(function(res){
					self.$broadcast('scroll.refreshComplete');
					if(res.listProducts){
						self.setLocalItem(key, res.listProducts);
						self.initProductData(res.listProducts);
					}
				}, function(){self.$broadcast('scroll.refreshComplete')});
			}
		}catch(e){
			console.log(e);
		}
	};
	self.initProductData = function(data) {
		var arr = [];
		for(var x in data){
			var obj = data[x];
			obj.productNameEscape = removeUnicodeVietnamese(obj.productName);
			arr.push(obj)
		}
		self.products = arr;
	};
	self.initRoad = function(data) {
		self.routes = data;
		if(data && data.length){
			self.routeId = data[0].routeId;
		}
	};
});
