/*
 * controller for detail order screen
 */
olbius.controller('OrderDetailController', function($rootScope, $scope, $controller, $routeParams, $window, $location, OrderService) {
	var self = $scope;
	var root = $rootScope;
	$.extend(this, $controller('BaseController', {
		$scope : self
	}));
	self.id = $routeParams.id;
	self.orderHeader = {};
	self.productStore = {};
	self.orderShipment = {};
	self.orderDetail = [];
	self.$on('$viewContentLoaded', function() {
		self.setHeader('OrderDetail', "/order", false);
		OrderService.getOrderDetail(function() {
		}, self.id).then(function(res) {
			try{
				if (res.data !== "error") {
					self.orderHeader = res.orderHeader;
					var tmp = self.orderHeader.grandTotal - self.orderHeader.remainingSubTotal;
					self.orderHeader.taxTotal = tmp;
					self.productStore = res.productStore;
					self.orderDetail = res.orderDetail;
					self.party = res.party;
					if(res.orderShipment && res.orderShipment.length){
						self.orderShipment = res.orderShipment[0];	
					}
				}				
			}catch(e){
				console.log(e);
			}
		});
	});
	self.back = function() {
		$window.history.back();
	};

	/*copy current order to create a new order*/
	self.copy = function() {
		var data = Array();
		if (localStorage.currentCustomer) {
			var store = self.getLocalItem("currentCustomer"); 
			if (self.orderDetail.length) {
				for (var x in self.orderDetail) {
					var obj = {
						productId : self.orderDetail[x].productId,
						qtyInInventory : self.orderDetail[x].quantity
					};
					data.push(obj);
				}
				var key = "creatingorder_" + store.partyIdTo;
				self.setLocalItem(key, JSON.stringify(data));
			}
		}
		self.changeState("order/create");
	};
});
