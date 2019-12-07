app.factory("interceptors", function() {
	return {
		// if beforeSend is defined call it
		'request' : function(request) {
			if (typeof(request.beforeSend) == 'function'){
				request.beforeSend();
			}
			return request;
		},
		// if complete is defined call it
		'response' : function(response) {
			if (typeof(response.config.complete) == 'function')
				response.config.complete(response);
			return response;
		}
	};

});
app.factory('Request', function($http, $location) {
	var self = this;
	self.transformRequest = function(obj, callback) {
		if (callback) {
			callback();
		}
		var str = [];
		for (var p in obj)
		str.push(encodeURIComponent(p) + "=" + encodeURIComponent(obj[p]));
		return str.join("&");
	};
	self.send = function(url, form, beforesend, complete, dataType, method, headers, timeout) {
		var data = null;
		if (form && typeof (form.append) == "function") {
			data = form;
		} else if (form && typeof (form) == "object") {
			data = $.param(form);
		}
		return $http({
			url : baseUrl + url,
			dataType : dataType ? dataType : "json",
			method : method ? method : "POST",
			beforeSend : beforesend,
			complete : complete,
			data : data,
			timeout : timeout ? timeout : config.timeout,
			headers : headers ? headers : {
				'Content-Type' : 'application/x-www-form-urlencoded'
			}
		}).then(function(res) {
			self.checkLogin(res.data, $location);
			return res.data;
		});
	};
	self.checkLogin = function(obj, $location) {
		if (obj.login === "FALSE") {
			localStorage.setItem(config.storage.login, "false");
			$location.path('login');
		} else {
			localStorage.setItem(config.storage.login, "true");
		}
	};
	self.getTalbe = function(table) {
		return $http.get(table + '/' + table + '.json').then(function(res) {
			checkLogin(res.data, $location);
			return res.data;
		});
	};
	self.getServerTimestampAsLong = function(){
		return self.send("getServerTimestampAsLong");
	};
	return self;
});

app.factory('AuthService', ['Request',
function(Request, $location) {
	var self = this;
	self.login = function(credentials, before, after) {
		return Request.send("dologin", credentials, before, after).then(function(res) {
			if (res.locale) {
				localStorage.setItem(config.storage.locale, res.locale);
			}
			return res;
		});
	};
	self.checkLogin = function() {
		return Request.send("checkLogin").then(function(res) {
			if (res.locale) {
				localStorage.setItem(config.storage.locale, res.locale);
			}
			return res.login;
		});
	};
	self.logout = function() {
		return Request.send("logout").then(function(res) {
			if (res.statusText == "OK") {
				localStorage.setItem(config.storage.login, "false");
				localStorage.removeItem(config.storage.locale);
			}
		});
	};
	self.updatePassword = function(data, beforeSend, complete) {
		return Request.send("updatePassword", data, beforeSend, complete);
	};
	return self;
}]);
app.factory('CustomerService', function(Request, $location) {
	var self = this;

	self.getAllRoute = function(beforeSend, complete) {
		return Request.send('getAllRoute', null, beforeSend, complete);
	};
	self.getRouteDetail = function(data, beforeSend, complete) {
		return Request.send('getRouteDetail', data, beforeSend, complete);
	};
	self.getCustomerInfo = function(data, beforeSend, complete) {
		return Request.send('getCustomerInfo', data, beforeSend, complete);
	};
	self.getStoreByRoad = function(data, beforeSend, complete) {
		return Request.send('getStoreByRoad', data, beforeSend, complete).then(function(res){
			for(var x in res.customers){
				if(res.customers[x].logoImageUrl){
					var image = new Image();
					image.src = res.customers[x].logoImageUrl;
				}
			}
			return res;
		});
	};
	self.getStore = function(routeId, partyId, beforeSend, close) {
		return Request.send('getStore', {
			routeId : routeId,
			partyId : partyId
		}, beforeSend, close);
	};
	self.getOrderProductDetailOfCustomer = function(customerId, month) {
		var data = {
			partyId : customerId,
			month : month
		};
		return Request.send('getOrderProductDetailOfCustomer', data);
	};
	self.updateLocationCustomer = function(customerId, location, address, before, complete) {
		var data = {
			customerId : customerId,
			latitude : location.latitude,
			longitude : location.longitude,
			address : address.name,
			stateProvinceGeoId : address.stateProvinceGeoId,
			districtGeoId : address.districtGeoId,
			countryGeoId : address.countryGeoId
		};
		return Request.send('updateLocationCustomer', data, before, complete);
	};
	self.createCustomerAgent = function(data, before, complete) {
		return Request.send('createCustomerAgent', data, before, complete);
	};
	self.createRouteHistory = function(data, before, complete){
		return Request.send('createRouteHistory', data, before, complete);
	};
	self.updateCustomerAgent = function(data, before, complete) {
		return Request.send('updateCustomerAgent', data, before, complete);
	};

	return self;
});
app.factory('EmployeeService', function(Request, $location) {
	var srv = this;
	srv.getType = function(beforeSend, complete) {
		return Request.send('getEmployeeLeaveType', null, beforeSend, complete);
	};
	srv.getReason = function(beforeSend, complete) {
		return Request.send('getEmployeeLeaveReason', null, beforeSend, complete);
	};
	srv.getWorkingShift = function(beforeSend, complete) {
		return Request.send('getWorkingShift', null, beforeSend, complete);
	};
	srv.getEmplLeaveInfo = function(data, beforeSend, complete) {
		return Request.send('getEmplLeaveInfo', data, beforeSend, complete);
	};
	srv.getHistory = function(data, beforeSend, complete) {
		return Request.send('getEmpLeaveStatus', data, beforeSend, complete);
	};

	srv.getGeo = function(data, beforeSend, close) {
		return Request.send('geoGeo', data, beforeSend, close);
	};
	srv.getProfile = function(beforeSend, complete) {
		return Request.send('getEmployeeInfo', null, beforeSend, complete);
	};
	srv.getAvatar = function(beforeSend, complete) {
		return Request.send('getImageAvatar', null, beforeSend, complete);
	};

	srv.getCurrentKpi = function(beforeSend, complete) {
		return Request.send('getCurrentKpi', null, beforeSend, complete);
	};

	srv.submit = function(data, beforeSend, complete) {
		return Request.send('createEmplLeaveMobile', data, beforeSend, complete);
	};
	srv.updateProfile = function(data, beforeSend, close) {
		return Request.send('updateProfileMobile', data, beforeSend, close);
	};
	return srv;
});
// all inventory service
app.factory('InventoryService', function(Request) {
	var srv = {};
	srv.check = function(inventory, customerId, beforeSend, complete) {
		return Request.send('updateInventoryCus', {
			inventory : JSON.stringify(inventory),
			party_id : customerId
		}, beforeSend, complete);
	};
	srv.getStoreInventories = function(productStoreId, beforeSend, complete) {
		return Request.send('getStoreInventories', {
			productStoreId : productStoreId
		}, beforeSend, complete);
	};
	srv.getByCustomer = function(customerId, beforeSend, complete) {
		return Request.send('getInventoryCusInfo', {
			partyId : customerId
		}, beforeSend, complete);
	};
	return srv;
});
app.factory('ProductService', function(Request, $location) {
	var srv = this;
	// get product by category
	srv.getByCategory = function(id, page, size, beforeSend) {
		var data = {
			productCategoryId : id,
			viewIndex : page,
			viewSize : size
		};
		return Request.send('getProductOfCat', data, beforeSend);
	};
	// get new product
	srv.getNewProduct = function(beforeSend) {
		return Request.send('getNewProducts', {}, beforeSend);
	};
	srv.getProductDetail = function(data, beforeSend, success) {
		return Request.send('getProductDetail', data, beforeSend, success);
	};
	srv.getAll = function(productStoreId, beforeSend) {
		return Request.send("getAllProducts", {
			productStoreId : productStoreId
		}, beforeSend);
	};
	srv.getPriceTax = function(partyId, productStoreId, beforeSend, complete) {
		return Request.send("getPriceTax", {
			partyId : partyId,
			productStoreId : productStoreId,
			pagesize : config.pageSize,
			pagenum : 0
		}, beforeSend, complete).then(function(res){
			for(var x in res.listProducts){
				if(res.listProducts[x].image){
					var image = new Image();
					image.src = res.listProducts[x].image;
				}else if(res.listProducts[x].image_small){
					var image = new Image();
					image.src = res.listProducts[x].image_small;
				}
			}
			return res;
		});
	};
	return srv;
});
app.factory('UploadService', function(Request, $location) {
	var srv = {};
	srv.uploadImage = function(data, before, complete) {
		return Request.send("uploadImage", data, before, complete, null, null, {
			'Content-Type' : undefined
		});
	};
	srv.uploadImageWithContent = function(data, before, complete) {
		return Request.send("uploadImageWithContent", data, before, complete, null, null, {
			'Content-Type' : undefined
		});
	};

	return srv;
});
app.factory('OrderService', function(Request, $location, $rootScope) {
	var self = this;
	self.getOrderTransit = function(productStoreId, before, complete) {
		return Request.send('getOrderItemsBeingTransfer', {
			productStoreId : productStoreId
		}, before, complete);
	};
	self.getProductStore = function(beforeSend) {
		return Request.send('getProductStore', {}, beforeSend);
	};
	/* get orders */
	self.getList = function(customers, month, pagesize, index, beforeSend, complete) {
		if (!beforeSend)
			beforeSend = $rootScope.showLoading;
		if (!complete)
			complete = $rootScope.hideLoading;
		if (!index) {
			index = 0;
		}
		var obj = {
			viewSize : pagesize,
			viewIndex : index,
			customers : JSON.stringify(customers),
			time : month
		};
		return Request.send('orderHeaderListView', obj, beforeSend, complete);
	};
	/* get order's detail information */
	self.getOrderDetail = function(beforeSend, id) {
		return Request.send('getOrderDetail', {
			id : id
		}, beforeSend);
	};
	return self;
});
app.factory("CartService", function(Request, $location, $rootScope){
	var self = this;
	self.createCart = function(customerId, products, productStoreId, beforeSend, complete) {
		var data = {
			customerId : customerId,
			products : JSON.stringify(products),
			productStoreId : productStoreId,
		};
		return Request.send('createCart', data, beforeSend, complete);
	};
	self.updateCart = function(customerId, products, productStoreId, beforeSend, complete) {
		var data = {
			customerId : customerId,
			productStoreId : productStoreId,
			products : JSON.stringify(products)
		};
		return Request.send('modifyCart', data, beforeSend, complete);
	};
	self.emptycart = function(beforeSend, complete) {
		return Request.send('emptycart', beforeSend, complete);
	};
	self.getCartInfo = function(beforeSend, complete){
		return Request.send('getCartInfo', null, beforeSend, complete);
	};
	self.checkout = function(data, beforeSend, complete) {
		return Request.send('submitOrder', data, beforeSend, complete);
	};
	return self;
});
app.factory('PromotionService', function(Request) {
	var self = this;
	self.getPromotionType = function(before, complete) {
		return Request.send('getPromotionTypes', null, before, complete);
	};
	self.getPromotions = function(data, before, complete) {
		return Request.send('getPromotionsByType', data, before, complete);
	};
	self.getOrderPromotions = function(data, before, complete) {
		return Request.send('getOrderPromotions', data, before, complete);
	};
	self.getOrderPromotion = function(productPromoId, before, complete) {
		return Request.send('getOrderPromotion', {productPromoId : productPromoId}, before, complete);
	};
	self.getPromotionDetail = function(productPromoId, before, complete) {
		return Request.send('getPromotionDetail', {
			productPromoId : productPromoId
		}, before, complete);
	};
	self.register = function(data, before, complete) {
		return Request.send('createRegistrationPromotion', data, before, complete);
	};
	self.getCustomerRegistered = function(before, complete) {
		return Request.send('getCustomerRegistered', before, complete);
	};
	self.getPromotionRegistered = function(partyId, before, complete) {
		return Request.send('getPromotionRegistered', {
			partyId : partyId
		}, before, complete);
	};
	self.createRegistrationEvaluation = function(data, before, complete) {
		return Request.send('createRegistrationEvaluation', data, before, complete);
	};
	self.getListPromosRuleByProduct = function(data, before, complete){
		return Request.send('getListPromosRuleByProduct', data, before, complete);
	};
	return self;
});
app.factory('DashboardService', function(Request) {
	var self = this;
	self.evaluateTotal = function(obj, beforesend, complete) {
		obj.service = "evaluateSalesmanTGrid";
		return Request.send("getSalesReportDataWithTime", obj, beforesend, complete);
	};
	self.evaluateCustomer = function(obj, beforesend, complete) {
		obj.service = "evaluateSalesmanCGrid";
		return Request.send("getSalesReportDataWithTime", obj, beforesend, complete);
	};
	self.getOrderTotal = function(obj, beforesend, complete) {
		obj.service = "salesmanOrderTotal";
		return Request.send("getOrderTotalReport", obj, beforesend, complete);
	};
	self.getSalesmanTop = function(obj, beforesend, complete) {
		obj.service = "evaluateTopSChart";
		return Request.send("getSalesmanTopReport", obj, beforesend, complete);
	};
	return self;
});

//CustomerOpinion Service
app.factory('CustomerOpinion', function(Request, $location) {
	var srv = self;
	srv.submitFbCustomer = function(infoFBfromCustomer, before, complete) {
		return Request.send("submitFbCustomer", infoFBfromCustomer, before, complete);
	};
	srv.submitInfoOpponent = function(opponentInfo, before, complete) {
		return Request.send("submitInfoOpponent", opponentInfo, before, complete);
	};
	srv.getListOpponent = function(before, complete) {
		return Request.send("getListOpponent", null, before, complete);
	};
	srv.createnewopponent = function(data, before, after){
		return Request.send("createOpponent", data, before, after);
	};
	return srv;
});
