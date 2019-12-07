'use strict';
if (!localStorage.serverUrl) {
	// localStorage.serverUrl = 'https://localhost:8443/mobileservices/control/';
	localStorage.serverUrl = '/mobileservices/control/';
	// localStorage.serverUrl = 'https://erp.delys.com.vn/mobileservices/control/';
}
var baseUrl = localStorage.serverUrl;
var config = {
	pageSize : 30,
	distance : 100
};
var olbius = angular.module('olbius', ['ngRoute', 'ngTouch', "geolocation", "uiGmapgoogle-maps", "ui.bootstrap"]);
olbius.config(['$routeProvider', '$httpProvider', 'uiGmapGoogleMapApiProvider', function olbiusConfig($routeProvider, $httpProvider, GoogleMapApi) {
	$httpProvider.interceptors.push('interceptors');
	$httpProvider.defaults.timeout = configPage.timeout;
	GoogleMapApi.configure({
        v: '3.20',
        libraries: 'weather,geometry,visualization,places',
        key: 'AIzaSyCHu4vQUKFsMnqpjk_HHjIIAU_yejvT5cs'
    });

	$routeProvider.when('/', {
		controller : 'IndexController',
		templateUrl : 'templates/index.htm',
	}).when('/login', {
		templateUrl : 'templates/login.htm',
		controller : 'LoginController'
	}).when('/store', {
		templateUrl : 'templates/store.htm',
		controller : 'StoreController'
	}).when('/main', {
		templateUrl : 'templates/main.htm',
		controller : 'MainController'
	}).when('/order/create/:outofroute?', {
		templateUrl : 'templates/createOrder.htm',
		controller : 'CreateOrderController'
	}).when('/customer', {
		templateUrl : 'templates/customer.htm',
		controller : 'CustomerController'
	}).when('/customer/create', {
		templateUrl : 'templates/createCustomer.htm',
		controller : 'CreateCustomerController'
	}).when('/customer/detail/:id', {
		templateUrl : 'templates/customerDetail.htm',
		controller : 'CustomerDetailController'
	}).when('/order/:iscurrent', {
		templateUrl : 'templates/order.htm',
		controller : 'OrderController'
	}).when('/order/detail/:id', {
		templateUrl : 'templates/orderDetail.htm',
		controller : 'OrderDetailController'
	}).when('/sale', {
		templateUrl : 'templates/sale.htm',
		controller : 'SaleController'
	}).when('/sale/promotion/:productPromoTypeId', {
		templateUrl : 'templates/promotion.htm',
		controller : 'PromotionController'
	}).when('/sale/promotiondetail/:productPromoId', {
		templateUrl : 'templates/promotiondetail.htm',
		controller : 'PromotionDetailController'
	}).when('/inventory', {
		templateUrl : 'templates/inventory.htm',
		controller : 'InventoryController'
	}).when('/inventory/:isFinish', {
		templateUrl : 'templates/inventory.htm',
		controller : 'InventoryController'
	}).when('/product', {
		templateUrl : 'templates/product.htm',
		controller : 'ProductController'
	}).when('/portal', {
		templateUrl : 'templates/portal.htm',
		controller : 'PortalController'
	}).when('/calendar', {
		templateUrl : 'templates/calendar.htm',
		controller : 'CalendarController'
	}).when('/customer-opinion', {
		templateUrl : 'templates/customerOpinion.htm',
		controller : 'CustomerOpinionController'
	}).when('/dashboard', {
		templateUrl : 'templates/dashboard.htm',
		controller : 'DashboardController'
	}).when('/sync', {
		templateUrl : 'templates/synchronize.htm',
		controller : 'SynchronizeController'
	}).when('/employee-leave', {
		templateUrl : 'templates/employeeLeave.htm',
		controller : 'EmployeeLeaveController'
	}).when('/employee-leave/report', {
		templateUrl : 'templates/employeeLeaveReport.htm',
		controller : 'EmployeeLeaveReportController'
	}).when('/profile', {
		templateUrl : 'templates/profile.htm',
		controller : 'ProfileController'
	}).when('/location', {
		templateUrl : 'templates/location.htm',
		controller : 'LocationController'
	}).otherwise({
		redirectTo : '/login'
	});
	// $locationProvider.html5Mode(true);
}]);
