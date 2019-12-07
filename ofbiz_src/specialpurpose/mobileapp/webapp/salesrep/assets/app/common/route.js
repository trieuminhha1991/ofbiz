app.config(['$stateProvider', '$urlRouterProvider', '$httpProvider', '$ionicConfigProvider', 'uiGmapGoogleMapApiProvider',
function($stateProvider, $urlRouterProvider, $httpProvider, $ionicConfigProvider, GoogleMapApi) {
	$ionicConfigProvider.backButton.text('').previousTitleText(false);
	$ionicConfigProvider.scrolling.jsScrolling(false);

	$httpProvider.interceptors.push('interceptors');
	$httpProvider.defaults.timeout = config.timeout;
	GoogleMapApi.configure({
		v : '3.20',
		libraries : 'weather,geometry,visualization,places',
		key : 'AIzaSyCHu4vQUKFsMnqpjk_HHjIIAU_yejvT5cs'
	});

	$stateProvider.state('login', {
		url : '/login',
		templateUrl : 'templates/login.htm',
		controller : 'LoginController'
	}).state('tab', {
		url : '/tab',
		abstract : true,
		templateUrl : 'templates/item/tabs.htm'
	}).state('tab.home', {
		url : '/home',
		views : {
			'tab-home' : {
				templateUrl : 'templates/dashboard.htm',
				controller : 'DashboardController'
			}
		}
	}).state('tab.salesReport', {
		url : '/report/sales',
		views : {
			'tab-home' : {
				templateUrl : 'templates/salesReport.htm',
				controller : 'SalesReportController'
			}
		}
	}).state('tab.customerReport', {
		url : '/report/sales',
		views : {
			'tab-home' : {
				templateUrl : 'templates/customerReport.htm',
				controller : 'CustomerReportController'
			}
		}
	}).state('tab.customer', {
		url : '/customer?other&routeId',
		views : {
			'tab-customer' : {
				templateUrl : 'templates/customer.htm',
				controller : 'CustomerController'
			}
		}
	}).state('createCustomer', {
		url : '/customer/create?partyId',
		templateUrl : 'templates/createCustomer.htm',
		controller : 'CreateCustomerController'
	}).state('tab.inventory', {
		url : '/customer/inventory',
		views : {
			'tab-customer' : {
				templateUrl : 'templates/inventory.htm',
				controller : 'InventoryController'
			}
		}
	}).state('tab.location', {
		url : '/customer/location/:isCreated',
		views : {
			'tab-customer' : {
				templateUrl : 'templates/location.htm',
				controller : 'LocationController'
			}
		}
	}).state('tab.createorder', {
		url : '/customer/createorder?routeId&other',
		views : {
			'tab-customer' : {
				templateUrl : 'templates/createorder.htm',
				controller : 'CreateOrderController'
			}
		}
	}).state('orderhistory', {
		url : '/order/history',
		templateUrl : 'templates/orderhistory.htm',
		controller : 'OrderHistoryController'
	}).state('orderdetail', {
		url : '/order/detail?id',
		templateUrl : 'templates/orderdetail.htm',
		controller : 'OrderDetailController'
	}).state('tab.cart', {
		url : '/cart',
		views : {
			'tab-cart' : {
				templateUrl : 'templates/cart.htm',
				controller : 'CartController'
			}
		}
	}).state('promotionType', {
		url : '/promotion/type',
		templateUrl : 'templates/promotionType.htm',
		controller : 'PromotionTypeController'
	}).state('promotionEvent', {
		url : '/promotion/event/:type',
		templateUrl : 'templates/promotion.htm',
		controller : 'PromotionController'
	}).state('promotionDetail', {
		url : '/promotion/detail?id&type',
		templateUrl : 'templates/promotiondetail.htm',
		controller : 'PromotionDetailController'
	}).state('promotionregistration', {
		url : '/promotion/registration/:productPromoId',
		templateUrl : 'templates/promotionregistration.htm',
		controller : 'PromotionRegistrationController'
	}).state('promotiongrading', {
		url : '/promotion/grade',
		templateUrl : 'templates/promotiongrading.htm',
		controller : 'PromotionGradingController'
	}).state('pricingrule', {
		url : '/pricingrule',
		templateUrl : 'templates/pricingrule.htm',
		controller : 'PricingController'
	}).state('tab.route', {
		url : '/route?created',
		views : {
			'tab-customer' : {
				templateUrl : 'templates/routeinfo.htm',
				controller : 'RouteInfoController'
			}
		}
	}).state('route', {
		url : '/route?created',
		templateUrl : 'templates/routeinfo.htm',
		controller : 'RouteInfoController'
	}).state('tab.setting', {
		url : '/setting',
		views : {
			'tab-setting' : {
				templateUrl : 'templates/setting.htm',
				controller : 'SettingController'
			},
		}
	}).state('profile', {
		url : '/employee/profile',
		templateUrl : 'templates/profile.htm',
		controller : 'ProfileController'
	}).state('employeeleave', {
		url : '/employee/leave',
		templateUrl : 'templates/leave.htm',
		controller : 'LeaveController'
	}).state('employeeleavinghistory', {
		url : '/employee/leavinghistory',
		templateUrl : 'templates/leavingHistory.htm',
		controller : 'LeavingHistoryController'
	}).state('opinion', {
		url : '/customer/opinion',
		templateUrl : 'templates/opinion.htm',
		controller : 'OpinionController'
	}).state('opponentinfo', {
		url : '/opponentinfo',
		templateUrl : 'templates/opponentinfo.htm',
		controller : 'OpponentController'
	});

	$urlRouterProvider.otherwise('/login');

}]);
