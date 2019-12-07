app.controller('ProductController', function($scope, $ionicModal, AppFactory, StorageFactory, LoadingFactory, ProductService, OrderService) {
	var self = $scope;
	self.selectedProductId = "";
	self.displayPrice = 'priceVAT';
	$ionicModal.fromTemplateUrl('templates/item/productdetail.htm', {
		scope : self,
		animation : 'slide-in-right'
	}).then(function(modal) {
		self.productdetail = modal;
	});
	self.openDetailPopup = function(detail){
		if(self.productdetail){
			self.productdetail.show();
		}
	};
	self.initProductStore = function(){
		self.getDataType("listProductStore", self.initProductStoreData, OrderService.getProductStore, null, config.storage.productStores);
	};
	self.initProductStoreData = function(data){
		self.productStores = data;
		if(self.productStores.length){
			self.store = self.productStores[0];
		}
	};
	/* check distance is valid to create order */
	self.initProduct = function(force, cartIgnore) {
		try{
			// var key = config.storage.products + '-' + self.currentCustomer.partyIdTo;
			var before, after;
			if(!force){
				before = self.showLoading;
				after = self.hideLoading;
			}
			var key = config.storage.products;
			var data = self.getLocalItem(key);
			if (data && data.length && !force) {
				self.initProductData(data, cartIgnore);
			}else if(!_.isEmpty(self.currentCustomer.partyIdTo)  && self.store.productStoreId){
				ProductService.getPriceTax(null, self.store.productStoreId, before, after).then(function(res){
					if(res.listProducts){
						self.setLocalItem(key, res.listProducts);
						self.initProductData(res.listProducts, cartIgnore);
					}
					self.$broadcast('scroll.refreshComplete');
				}, function(){self.hideLoading();self.$broadcast('scroll.refreshComplete')});
			}
		}catch(e){
			self.$broadcast('scroll.refreshComplete');
			console.log(e);
		}
	};
	self.initProductData = function(data, cartIgnore) {
		var arr = [];
		if(!cartIgnore){
			var cartItems = self.getCartItem();
			for (var x in data) {
				var obj = data[x];
				obj.productNameEscape = removeUnicodeVietnamese(obj.productName);
				if(cartItems && cartItems.length){
					var i = _.findIndex(cartItems, {productId : obj.productId});
					if(i != -1){
						obj.quantity = cartItems[i].quantity;
					}else{
						obj.quantity = null;
					}
				}else{
					obj.quantity = null;
				}
				obj.expiry_date = "";
				obj.isAdded = false;
				obj.productName = obj.productCode + " - " + obj.productName;
				arr.push(obj);
			}
			self.products = arr;
			self.renderData();
		}else{
			for (var x in data) {
				var obj = data[x];
				obj.productNameEscape = removeUnicodeVietnamese(obj.productName);
				obj.productName = obj.productCode + " - " + obj.productName;
				arr.push(obj);
			}
			self.products = arr;
		}
	};

	self.getProductDetail = function(productId, productStoreId, force){
		console.log(productStoreId);
		self.selectedProductId = productId;
		self.currentProductStoreId = productStoreId;
		var key = config.storage.product + "-" + productId;
		self.openDetailPopup();
		AppFactory.getDataType('results', function(data){
			var obj = data ? data : {};
			var products = StorageFactory.getLocalItem(config.storage.products);
			var product = _.findWhere(products, {productId:productId});
			obj = _.extend(obj, product);
			self.selectedProduct = obj;
			self.$broadcast('scroll.refreshComplete');
		}, ProductService.getProductDetail, {productId : productId, productStoreId : productStoreId}, key, LoadingFactory.showLoading, LoadingFactory.hideLoading, force);
	};

});
