app.controller('OrderHistoryController', function($rootScope, $scope, $controller, OrderService) {
	var self = $scope;
	var root = $rootScope;
	$.extend(this, $controller('BaseController', {
		$scope : self
	}));
	self.condition = {
		partyId : "",
		time: ""
	};
	self.listOrder = {
		content : Array(),
		totalOrder : 0,
		orderCreated : 0,
		orderApproved : 0,
		orderCompleted : 0,
		orderCancelled : 0,
	};
	self.condition.timeViewOrder = [{
		text : self.getLabel('Thisweek'),
		value : 'week'
	}, {
		text : self.getLabel('Thismonth'),
		value : 'month'
	}];

	self.currentPage = 0;
	self.processing = false;
	self.$watch('condition.partyId', function(){
		self.loadData();
	});
	self.$watch('condition.time', function(){
		self.loadData();
	});
	self.init = function() {
		try{
			var arr = self.getLocalItem(config.storage.customers);
			if(arr.content){
				arr.content.unshift({
					partyIdTo : 'ALL',
					groupName : self.getLabel('All')
				});
				self.customers = arr;
			}
			var cur = self.getLocalItem(config.storage.currentCustomer);
			if(cur){
				self.condition.partyId = cur.partyIdTo;
			}else{
				self.condition.partyId = "ALL";
			}
			self.condition.time = self.condition.timeViewOrder[1].value;
			self.loadData();
		}catch(e){
			console.log(e);
		}
	};
	self.loadData = function(){
		if(self.condition.partyId && self.condition.time){
			var cus = [];
			if(self.condition.partyId != "ALL"){
				cus = [self.condition.partyId];
			}else if(self.customers.content){
				var arr = [];
				for(var x in self.customers.content){
					arr.push(self.customers.content[x].partyIdTo);
				}
				cus = arr;
			}
			self.getData(cus);
		}
	};
	self.getData = function(cus, index) {
		self.processing = true;
		OrderService.getList(cus, self.condition.time, config.pageSize, index).then(function(data) {
			self.$broadcast('scroll.refreshComplete');
			self.processing = false;
			self.listOrder = {
				content : data.listOrder,
				totalOrder : data.totalOrder,
				size : data.size,
				orderCreated : data.orderCreated,
				orderApproved : data.orderApproved,
				orderCompleted : data.orderCompleted,
				orderCancelled : data.orderCancelled
			};
		}, function() {
			self.$broadcast('scroll.refreshComplete');
			self.processing = false;
		});
	};
	self.viewOrderDetail = function(id) {
		self.changeState('orderdetail', {id : id});
	};
	self.$on('$ionicView.enter', self.init);
});