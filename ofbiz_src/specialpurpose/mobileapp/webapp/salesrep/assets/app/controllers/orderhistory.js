app.controller('OrderHistoryController', function($rootScope, $scope, $controller, OrderService, CustomerService, StoreFactory) {
	var self = $scope;
	var root = $rootScope;
	$.extend(this, $controller('BaseController', {
		$scope : self
	}));
	self.condition = {
		partyId : "",
		time : ""
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
	self.$watch('condition.routeId', function() {
		if (self.condition.routeId) {
			self.getCustomer();
			self.checkActiveRoute();
		}
	});

	self.$watch('condition.partyId', function() {
		self.loadData();
	});

	self.$watch('condition.time', function() {
		self.loadData();
	});
	self.init = function() {
		try {
			self.getDataType('results', self.initRoad, CustomerService.getAllRoute, null, config.storage.routes);
			self.condition.time = self.condition.timeViewOrder[1].value;
		} catch(e) {
			console.log(e);
		}
	};
	self.getCustomer = function(force) {
		StoreFactory.getList(self, {
			'routeId' : self.condition.routeId
		}, null, null, function(data) {
			var arr = data.content;
			if (arr) {
				arr.unshift({
					partyIdTo : 'ALL',
					groupName : self.getLabel('All')
				});
				self.customers = arr;
			}
			var cur = self.getLocalItem(config.storage.currentCustomer);
			if (cur) {
				self.condition.partyId = cur.partyIdTo;
			} else {
				self.condition.partyId = "ALL";
				self.loadData();
			}
		}, force);
	};

	self.initRoad = function(data) {
		self.routes = data;
		var current = self.getLocalItem(config.storage.routetoday);
		if (current && current.length) {
			var tmp = _.pluck(data, 'routeId');
			var inter = _.intersection(tmp, current);
			if (inter.length)
				self.condition.routeId = inter[0];
		} else if (data && data.length) {
			self.condition.routeId = data[0].routeId;
		}
	};
	self.loadData = function() {
		if (self.condition.partyId && self.condition.time) {
			var cus = [];
			if (self.condition.partyId != "ALL") {
				cus = [self.condition.partyId];
			} else if (self.customers) {
				var arr = [];
				for (var x in self.customers) {
					if(self.customers[x].partyIdTo != "ALL"){
						arr.push(self.customers[x].partyIdTo);
					}
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
		self.changeState('orderdetail', {
			id : id
		});
	};
	self.checkActiveRoute = function(){
		var current = self.getLocalItem(config.storage.routetoday);
		if(_.contains(current, self.condition.routeId)){
			self.activeRoute = true;
			return;
		}
		self.activeRoute = false;
	};
	self.$on('$ionicView.enter', self.init);
});