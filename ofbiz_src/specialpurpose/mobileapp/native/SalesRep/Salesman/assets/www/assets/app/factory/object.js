app.factory('AppFactory', function($timeout, $state, StorageFactory, LoadingFactory, StoreFactory, OrderService, ProductService) {
	var self = this;
	self.getDataType = function(key, initData, remoteData, param, localkey, before, complete, force) {
		var tmp = localkey ? localkey : key;
		var data = tmp ? StorageFactory.getLocalItem(tmp) : null;
		if (data && _.isFunction(initData) && !force) {
			initData(data);
		} else if (_.isFunction(remoteData)) {
			if (param) {
				remoteData(param, before, complete).then(function(res) {
					if (tmp && res[key]) {
						StorageFactory.setLocalItem(tmp, res[key]);
					}
					initData(res[key]);
				}, complete);
			} else {
				remoteData(before, complete).then(function(res) {
					if (tmp && res[key]) {
						StorageFactory.setLocalItem(tmp, res[key]);
					}
					initData(res[key]);
				}, complete);
			}
		}
	};
	self.initApplicationData = function() {
		self.getDataType("listProductStore", function(stores) {
			if (stores.length) {
				var store = stores[0];
				ProductService.getPriceTax(null, store.productStoreId).then(function(res) {
					if (res.listProducts) {
						StorageFactory.setLocalItem(config.storage.products, res.listProducts);
					}
				});
			}
		}, OrderService.getProductStore, null, config.storage.productStores);
	};
	self.mergeData = function(arr1, arr2, property){
		arr1 = _.union(arr1, arr2);
		arr1 = _.uniq(arr1, false, function(item, key, a){
			return item[property];
		});
		return arr1;
	};
	self.changeState = function(state, params, options) {
		$state.go(state, params, options);
	};
	return self;
});
app.factory('PromotionFactory', function($rootScope, StorageFactory, PromotionService) {
	var self = this;
	self.get = function(scope, productPromoId, after, force) {
		if (productPromoId) {
			var key = config.storage.promotion + "-" + productPromoId;
			var data = StorageFactory.getLocalItem(key);
			if (data && !force) {
				scope.promotion = data;
			} else {
				PromotionService.getPromotionDetail(productPromoId).then(function(res) {
					if(res.rules || res.info || res.stores || res.roles){
						scope.promotion = {
							rules : res.rules,
							info : res.info,
							stores : res.stores,
							roles : res.roles
						};
						StorageFactory.setLocalItem(key, scope.promotion);
					}
					after();
				}, after);
			}
		}
	};
	self.getOrderPromotion = function(scope, type, productPromoId, after, force) {
		if (productPromoId) {
			var key = config.storage.promotion + "-" + type + "-" + productPromoId;
			var data = StorageFactory.getLocalItem(key);
			if (data && !force) {
				scope.promotion = data;
			} else {
				PromotionService.getOrderPromotion(productPromoId).then(function(res) {
					if(res.rules || res.info || res.stores || res.roles){
						scope.promotion = {
							rules : res.rules,
							info : res.info,
							stores : res.stores,
							roles : res.roles
						};
						StorageFactory.setLocalItem(key, scope.promotion);
					}
					after();
				}, after);
			}
		}
	};
	return self;
});
app.factory('StoreFactory', function($rootScope, StorageFactory, CustomerService) {
	var self = this;
	self.getList = function(scope, data, before, after, force) {
		if (!data) {
			data = {
				size : config.pageSize,
				page : 0
			};
		} else {
			if (!data.size) {
				data.size = config.pageSize;
			}
			if (data.page) {
				data.page = 0;
			}
		}
		var tmp = '';
		if(data.routeId) tmp = data.routeId;
		if(data.other == 'Y') tmp = 'other';
		var key = tmp ? (config.storage.customers + "-" + tmp) : config.storage.customers;
		scope.customers = StorageFactory.getLocalItem(key);
		if (!scope.customers|| force) {
			CustomerService.getStoreByRoad(data, before, after).then(function(res) {
				scope.customers = {
					content : res.customers,
					routes : res.routes
				};
				StorageFactory.setLocalItem(key, scope.customers);
			});
		}
	};
	self.isCurrentRoute = function(data, routeId) {
		return !_.isEmpty(_.where(data, routeId))
	};
	return self;
});
app.factory('AddressFactory', function($rootScope, EmployeeService) {
	var self = this;
	self.getGeo = function(scope, parentGeoId, geoTypeId, before, complete) {
		if (!geoTypeId)
			return;
		var tmp = geoTypeId.toLowerCase();
		if (!scope.address)
			scope.address = {};
		var key = config.storage[tmp] + "-" + parentGeoId;
		scope.getDataType('listGeo', function(data) {
			scope.address[tmp] = data;
		}, EmployeeService.getGeo, {
			geoId : parentGeoId,
			geoTypeId : geoTypeId
		}, key, before, complete);
	};
	self.getGeoName = function(data, id) {
		var obj = _.where(data, {
			geoId : id
		});
		if (!_.isEmpty(obj)) {
			return obj[0].geoName;
		}
		return "";
	};
	return self;
});
app.factory('CartFactory', function($rootScope, $timeout, $interval, StorageFactory, CartService) {
	var self = this;
	self.groupPromotions = function(promotions){
		if (promotions && promotions.length) {
			var groups = _.groupBy(promotions, 'productId');
			var promotions = _.map(groups, function(g, key) {
				if(g && g.length){
					var obj = _.reduce(g, function(memo, item){
						memo.productQuantity += item.productQuantity;
						return memo;
					});
					return obj;
				}
				return g;
			});
			return promotions;
		}
	};
	self.createCart = function(partyId, productStoreId, products, before, complete) {
		if (productStoreId && partyId) {
			if (!products || !products.length)
				products = [];
			CartService.createCart(partyId, products, productStoreId, before, complete).then(function(res) {
				if (res.order) {
					StorageFactory.setLocalItem(config.storage.cartOwner, partyId);
					StorageFactory.setLocalItem(config.storage.isCreateCart, true);
				} else {
					StorageFactory.setLocalItem(config.storage.isCreateCart, false);
				}
			});
		}
	};
	self.removeCart = function(before, complete) {
		CartService.emptycart(before, complete).then(function(res) {
			StorageFactory.removeLocalItem(config.storage.cartOwner);
			StorageFactory.setLocalItem(config.storage.isCreateCart, false);
		});
	};
	self.updateCartItem = function(partyId, products) {
		var key = config.storage.cartItem + '-' + partyId;
		StorageFactory.setLocalItem(key, products);
	};
	self.clearCartItem = function(partyId) {
		var key = config.storage.cartItem + '-' + partyId;
		StorageFactory.removeLocalItem(key);
	}
	self.setQuantity = function(product, key, flag, num) {
		if (!product) {
			return;
		}
		var value = product[key];
		value = isNaN(value) ? 0 : value;
		if (!flag) {
			var tmp = value - 1;
			if ((!isNaN(num) && tmp >= num) || isNaN(num)) {
				product[key] = tmp;
			}
		} else {
			var tmp = value + 1;
			if ((!isNaN(num) && tmp <= num) || isNaN(num)) {
				product[key] = tmp;
			}
		}
	};
	self.handleHoldQuantity = function(product, key, dir, num) {
		self.setQuantity(product, key, dir, num);
		var tmp = key + dir;
		if (!product[tmp]) {
			product[tmp] = $interval(function() {
				self.setQuantity(product, key, dir, num);
			}, config.event.holdRangeDelay);
		}
	};
	self.handleReleaseQuantity = function(product, key, dir, callback) {
		if (self.timeoutHandling)
			$timeout.cancel(self.timeoutHandling);
		var tmp = key + dir;
		if (product[tmp]) {
			$interval.cancel(product[tmp]);
			delete product[tmp];
		}
		self.timeoutHandling = $timeout(function() {
			if (_.isFunction(callback)) {
				callback();
			}
		}, config.event.leaveTimeout);
	};
	self.changeQuantity = function(product, key, callback) {
		var tmp = key + 'change';
		if (product[tmp]) {
			$timeout.cancel(product[tmp]);
			delete product[tmp];
		}
		product[tmp] = $timeout(function() {
			if (_.isFunction(callback)) {
				callback();
			}
			$timeout.cancel(product[tmp]);
		}, config.event.leaveTimeout);
	};
	// self.updateCart = function(partyId, productStoreId, products, before, complete) {
	// return CartService.updateCart(partyId, products, productStoreId, before, complete);
	// };
	return self;
});
app.factory('LoadingFactory', function($timeout, $rootScope, $ionicLoading) {
	var self = this;
	self.showLoading = function(options) {
		if (!_.isFinite($ionicLoading.pending) || $ionicLoading.pending <= 0) {
			$ionicLoading.pending = 1;
			if (!options) {
				options = {};
			}
			var tm = '<ion-spinner icon="ripple"></ion-spinner><br/>' + $rootScope.getLabel('Loading') + '...';
			$ionicLoading.show({
				template : options.template ? options.template : tm,
				duration : options.duration,
				delay : options.delay,
				hideOnStateChange : options.hideOnStateChange ? options.hideOnStateChange : true,
				noBackdrop : options.noBackdrop
			});
		} else {
			$ionicLoading.pending++;
		}
	};
	self.hideLoading = function() {
		if (_.isFinite($ionicLoading.pending) && $ionicLoading.pending) {
			$ionicLoading.pending--;
		}
		if ($ionicLoading.pending <= 0) {
			$timeout(function() {
				$ionicLoading.hide();
			}, 500)
			delete $ionicLoading.pending;
		}
	};
	self.resetLoading = function() {
		$ionicLoading.hide();
		delete $ionicLoading.pending;
	};
	return self;
});
app.factory('Popup', function($timeout, $rootScope, $ionicPopup) {
	var self = this;
	self.showError = function(err, timeout) {
		if (timeout) {
			$timeout(function() {
				self.buildAlert($rootScope.getLabel('WarningMessage'), err);
			}, config.event.notificationDelay);
		} else
			self.buildAlert($rootScope.getLabel('WarningMessage'), err);
	};
	self.showNotification = function(msg, timeout) {
		if (timeout) {
			$timeout(function() {
				self.buildAlert($rootScope.getLabel('Notification'), msg);
			}, config.event.notificationDelay);
		} else
			self.buildAlert($rootScope.getLabel('Notification'), msg);
	};
	self.buildAlert = function(title, msg) {
		var alertPopup = $ionicPopup.alert({
			title : title,
			template : msg
		});
	};
	self.buildConfirm = function(title, mes, ok, reject) {
		if (!title) {
			title = $rootScope.getLabel('Notification');
		}
		var confirmPopup = $ionicPopup.confirm({
			title : title,
			template : mes
		});
		confirmPopup.then(function(res) {
			if (res && typeof (ok) === 'function') {
				ok(confirmPopup);
			} else if (res && typeof (not) === 'function') {
				reject(confirmPopup);
			}
		});
	};
	return self;
});
app.factory('Chart', function() {
	var self = this;
	self.initConfig = function(options, series, title, loading, xAxis, yAxis, useHighStocks, size, callback) {
		var chartConfig = {
			options : options,
			series : series,
			title : title,
			loading : loading,
			xAxis : xAxis,
			yAxis : yAxis,
			useHighStocks : false,
			size : size,
			func : callback
		};
		return chartConfig;
	};
	return self;
});
