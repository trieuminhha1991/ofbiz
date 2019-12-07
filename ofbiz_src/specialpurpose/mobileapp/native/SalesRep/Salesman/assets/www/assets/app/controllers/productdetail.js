app.controller('ProductController', function($scope, $ionicModal, AppFactory, StorageFactory, LoadingFactory, ProductService) {
	var self = $scope;
	self.selectedProductId = "";
	$ionicModal.fromTemplateUrl('templates/item/productdetail.htm', {
		scope : self,
		animation : 'slide-in-up'
	}).then(function(modal) {
		self.productdetail = modal;
	});
	self.openDetailPopup = function(detail){
		if(self.productdetail){
			self.productdetail.show();
		}
	};
	self.getProductDetail = function(productId, force){
		self.selectedProductId = productId;
		var key = config.storage.product + "-" + productId;
		self.openDetailPopup();
		AppFactory.getDataType('results', function(data){
			var obj = data ? data : {};
			var products = StorageFactory.getLocalItem(config.storage.products);
			var product = _.findWhere(products, {productId:productId});
			obj = _.extend(obj, product);
			self.selectedProduct = obj;
		}, ProductService.getProductDetail, {productId : productId}, key, LoadingFactory.showLoading, LoadingFactory.hideLoading, force);
	};
});
