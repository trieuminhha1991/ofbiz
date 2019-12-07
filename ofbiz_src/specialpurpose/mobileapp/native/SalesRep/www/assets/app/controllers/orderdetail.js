app.controller('OrderDetailController', function($rootScope, $scope, $controller, $stateParams, OrderService, CalendarFactory, CartFactory) {
	var self = $scope;
	var root = $rootScope;
	$.extend(this, $controller('BaseController', {
		$scope : self
	}));
	self.displayPrice = 'price';
	self.$on('$ionicView.enter', function() {
		self.id = $stateParams.id;
		self.canEditItem = false;
		self.orderHeader = {};
		self.cart = {};
		self.productStore = {};
		self.orderShipment = {};
		self.products = [];
		self.getOrderDetail();
	});
	self.getOrderDetail = function() {
		OrderService.getOrderDetail(function() {
		}, self.id).then(function(res) {
			self.$broadcast('scroll.refreshComplete');
			try {
				if (res.data !== "error") {
					self.orderHeader = res.orderHeader;
					self.initCartInfo(self.orderHeader);
					if (self.orderHeader.orderDate && self.orderHeader.orderDate.time) {
						self.orderHeader.orderDate.formattedDate = CalendarFactory.formatDateDMY(self.orderHeader.orderDate.time);
					}
					self.productStore = res.productStore;
					self.processItem(res.orderDetail);
					self.party = res.party;
					if (res.orderShipment && res.orderShipment.length) {
						self.orderShipment = res.orderShipment[0];
					}
				}
			} catch(e) {
				console.log(e);
			}
		}, function(){self.$broadcast('scroll.refreshComplete')});
	};
	self.initCartInfo = function(header){
		if(header){
			self.cart = {
				totalAmount : header.totalAmount,
				subTotal : header.remainingSubTotal,
				taxAmount: header.taxAmount,
				discountAmount: header.discountAmount,
				grandTotal: header.grandTotal,
			};
			var promotions = header.promotions;
			self.cart.promotions = CartFactory.groupPromotions(promotions);
		}
	};
	self.processItem = function(data){
		for(var x in data){
			var obj = {
				productId : data[x].productId,
				productName : data[x].itemDescription,
				price : data[x].unitPrice,
				quantity : data[x].quantity,
				image : data[x].image,
				image_small : data[x].image_small,
			};
			self.products.push(obj);
		}
	};
});
