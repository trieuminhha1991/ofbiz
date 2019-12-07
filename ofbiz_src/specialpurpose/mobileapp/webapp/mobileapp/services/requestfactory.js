olbius.factory("interceptors", function() {
	return {
		// if beforeSend is defined call it
		'request' : function(request) {
			if (request.beforeSend)
				request.beforeSend();
			return request;
		},
		// if complete is defined call it
		'response' : function(response) {
			if (response.config.complete)
				response.config.complete(response);
			return response;
		}
	};

});
olbius.factory('Request', function($http, $location) {
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
			timeout : timeout ? timeout : configPage.timeout,
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
			localStorage.setItem("login", "false");
			$location.path('login');
		} else {
			localStorage.setItem("login", "true");
		}
	};
	self.getTalbe = function(table) {
		return $http.get(table + '/' + table + '.json').then(function(res) {
			checkLogin(res.data, $location);
			return res.data;
		});
	};
	return self;
});

olbius.factory('AuthService', ['Request',
function(Request, $location) {
	var self = this;
	self.login = function(credentials) {
		return Request.send("checkLogin", credentials).then(function(res){
			if(res.locale){
				localStorage.setItem("locale", res.locale);
			}
			return res;
		});
	};
	self.checkLogin = function() {
		return Request.send("checkLogin").then(function(res) {
			if(res.locale){
				localStorage.setItem("locale", res.locale);
			}
			return res.login;
		});
	};
	self.logout = function() {
		return Request.send("logout").then(function(res) {
			if (res.statusText == "OK") {
				localStorage.setItem("login", "false");
				localStorage.removeItem("locale");
			}
		});
	};
	self.updatePassword = function(data, beforeSend, complete) {
		return Request.send("updatePassword", data, beforeSend, complete);
	};
	return self;
}]);

olbius.factory('CustomerService', function(Request, $location) {
	var self = this;

	self.getTable = function() {
		return Request.getTable('customer');
	};
	self.getInfo = function(beforeSend, complete) {
		return Request.send('getInfo', beforeSend, complete);
	};
	self.get = function(id, beforeSend) {
		return Request.send('customerInfoDetail', {
			customerId : id
		}, beforeSend);
	};
	// get store by road id
	self.getAll = function(page, size, beforeSend) {
		var data = {};
		if (page && size) {
			data = {
				viewIndex : page,
				viewSize : size
			};
		}
		return Request.send('getAllStore', data, beforeSend);
	};

	self.getAllRoute = function(beforeSend, complete) {
		return Request.send('getAllRoute', null, beforeSend, complete);
	};
	self.getStoreByRoad = function(data, beforeSend, complete) {
		return Request.send('getStoreByRoad', data, beforeSend, complete);
	};
	self.getStore = function(routeId, partyId, beforeSend, close) {
		return Request.send('getStore', {
			routeId : routeId,
			partyId : partyId
		}, beforeSend, close);
	};
	self.getTotalOrderDetail = function(customerId, month) {
		var data = {
			customerId : customerId,
			month : month
		};
		return Request.send('getDetailTotalOrder', data);
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

	return self;
});
olbius.factory('OrderService', function(Request, $location, $rootScope) {
	var self = this;
	/* get order (create new order) information table format */
	self.getTable = function() {
		return Request.getTable('order');
	};

	/* get orders (list ordered) information table format */
	self.getTableList = function() {// list all order
		return Request.getTable('orders');
	};
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

	self.submitOrder = function(data, beforeSend, complete) {
		return Request.send('submitOrder', data, beforeSend, complete);
	};
	return self;
});
olbius.factory('CategoryService', function($http, $location) {
	var srv = {};
	srv.getAll = function(beforeSend) {
		return $http.get(baseUrl + 'getAllCategories', {
			transformRequest : function(obj) {
				transformRequest(obj, beforeSend);
			}
		}).then(function(res) {
			checkLogin(res.data, $location);
			return res.data;
		});
	};
	return srv;
});

olbius.factory('ProductService', function(Request, $location) {
	var srv = this;
	srv.getTable = function() {
		return Request.getTable('product');
	};
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
	srv.getAll = function(productStoreId, beforeSend) {
		return Request.send("getAllProducts", {
			productStoreId : productStoreId
		}, beforeSend);
	};
	srv.getPriceTax = function(partyId, productStoreId, beforeSend, complete) {
		return Request.send("getPriceTax", {
			partyId : partyId,
			productStoreId : productStoreId,
			pagesize : configPage.pageSize,
			pagenum : 0
		}, beforeSend, complete);
	};
	return srv;
});
// all service for employee
olbius.factory('EmployeeService', function(Request, $location) {
	var srv = this;
	srv.getType = function(beforeSend, complete) {
		return Request.send('getEmployeeLeaveType', null, beforeSend, complete);
	};
	srv.getReason = function(beforeSend, complete) {
		return Request.send('getEmployeeLeaveReason', null, beforeSend, complete);
	};
	srv.getGeo = function(data, beforeSend, close) {
		return Request.send('geoGeo', data, beforeSend, close);
	};
	srv.getWorkingShift = function(beforeSend, complete) {
		return Request.send('getWorkingShift', null, beforeSend, complete);
	};
	srv.getHistory = function(beforeSend, complete) {
		return Request.send('getEmpLeaveStatus', null, beforeSend, complete);
	};
	srv.getProfile = function(beforeSend, complete) {
		return Request.send('getEmployeeInfo', null, beforeSend, complete);
	};
	srv.getAvatar = function(beforeSend, complete) {
		return Request.send('getImageAvatar', null, beforeSend, complete);
	};
	srv.getNotification = function(beforeSend, complete) {
		return Request.send('getNotification', null, beforeSend, complete);
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
olbius.factory('InventoryService', function(Request) {
	var srv = {};
	srv.getTable = function() {
		return $http.get('table/inventory.json').then(function(res) {
			checkLogin(res.data, $location);
			return res.data;
		});
	};
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
//DashBoard Services
olbius.factory('DashboardFactory', function(Request) {
	var self = this;
	self.evaluateTotal = function(obj, beforesend, complete) {
		obj.service = "evaluateSalesmanTGrid";
		return Request.send("getSalesReportDataWithTime", obj, beforesend, complete);
	};
	self.evaluateCustomer = function(obj, beforesend, complete) {
		obj.service = "evaluateSalesmanCGrid";
		return Request.send("getSalesReportDataWithTime", obj, beforesend, complete);
	};
	return self;
});

//
//CustomerOpinion Service
olbius.factory('CustomerOpinion', function(Request, $location) {
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
	return srv;
});

olbius.factory('UploadService', function(Request, $location) {
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
olbius.factory('PromotionService', function(Request) {
	var self = this;
	self.getPromotionType = function(before, complete) {
		return Request.send('getPromotionTypes', null, before, complete);
	};
	self.getPromotions = function(data, before, complete) {
		return Request.send('getPromotionsByType', data, before, complete);
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
	return self;
});
