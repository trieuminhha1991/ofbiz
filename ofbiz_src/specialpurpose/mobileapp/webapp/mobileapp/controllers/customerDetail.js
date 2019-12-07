/*global todomvc, angular */
'use strict';

/**
 * The main controller for the app. The controller: - retrieves and persists the
 * model via the todoStorage service - exposes the model to the template and
 * provides event handlers
 */
olbius.controller('CustomerDetailController', function($rootScope, $scope, $controller, $routeParams, $location, CustomerService, LanguageFactory) {
	$.extend(this, $controller('BaseController', {
		$scope : $scope
	}));
	$scope.id = $routeParams.id;
	$scope.customer = {};
	$scope.marker = {
		id : "cr",
		coords : {
		},
		options : {
			draggable : true
		}
	};
	$scope.$on('$viewContentLoaded', function() {
		$scope.setHeader('CustomersDetail', "/customer", false);
		$scope.initCustomer();
		$scope.setCustomerMarker();
	});
	$scope.initCustomer = function(){
		$scope.getTotalOrderDetail();
		$scope.OrderDetailCustomer();
		var data = $.parseJSON(localStorage.getItem("customer_" + $scope.id));
		if(data){
			$scope.customer = data;
		}else{
			$scope.getInfo();
		}
	};
	/* get customer information */
	$scope.getInfo = function() {
		CustomerService.get($scope.id, $rootScope.showLoading).then(function(data) {
			$scope.customer = data.cusDetailsInfo;
			localStorage.setItem("customer_"+ $scope.id, JSON.stringify($scope.customer));
			$scope.setCustomerMarker();
			$rootScope.hideLoading();
		}, function() {
			$rootScope.hideLoading();
		});
	};
	/* get survey Order */
	$scope.reportOrder = {};
	$scope.getTotalOrderDetail = function(){
		var month = "thisWeek";
		CustomerService.getTotalOrderDetail($routeParams.id,month).then(function(res){
			$scope.reportOrder = {
					totalOrder : res.res.totalOrder,
					totalOrderNotPayment : res.res.totalOrderNotPayment,
					totalOrderPayment : res.res.totalOrderPayment,
					grandTotal : res.res.grandTotal,
					remainingSubTotal : res.res.remainingSubTotal,
					TotalAmountPaid : res.res.TotalAmountPaid
			};
		});
	};
	$scope.setCustomerMarker = function() {
		var data = [];
		var timeout;

		if ($scope.customer.latitude & $scope.customer.longitude) {
			$scope.store = {
				id : "fk",
				icon : 'assets/images/blue_marker.png',
				coords : {
					latitude : $scope.customer.latitude,
					longitude : $scope.customer.longitude
				},
				showWindow : true,
				title : $scope.customer.groupName + "-" + $scope.customer.address1,
				options : {
					draggable : false
				}
			};
		}
	};
	$scope.updateOrderInfo = function(month){
		CustomerService.getTotalOrderDetail($routeParams.id,month).then(function(res){
			$scope.reportOrder = {
					totalOrder : res.res.totalOrder,
					totalOrderNotPayment : res.res.totalOrderNotPayment,
					totalOrderPayment : res.res.totalOrderPayment,
					grandTotal : res.res.grandTotal,
					remainingSubTotal : res.res.remainingSubTotal,
					TotalAmountPaid : res.res.TotalAmountPaid
			};
		});
		CustomerService.getOrderProductDetailOfCustomer($routeParams.id,month).then(function(data){
			var arr = data.res;
			renderOrderDetailProductCustomer = "";
				for(var key in arr){
					renderOrderDetailProductCustomer += '<tr><td>'+key+'</td>';
					for(var k in arr[key]){
						renderOrderDetailProductCustomer += '<td>'+k +'</td><td>'+arr[key][k]+'</td></tr>';
					}
						$('#table_product').html(renderOrderDetailProductCustomer);
				}
		});
	};
	var renderOrderDetailProductCustomer = "";
	$scope.OrderDetailCustomer = function(){
		var month = 'thisWeek';
		CustomerService.getOrderProductDetailOfCustomer($routeParams.id,month).then(function(data){
			var arr = data.res;
			for(var key in arr){
				renderOrderDetailProductCustomer += '<tr><td>'+key+'</td>';
				for(var k in arr[key]){
					renderOrderDetailProductCustomer += '<td>'+k +'</td><td>'+arr[key][k]+'</td></tr>';
				}
				$('#table_product').html(renderOrderDetailProductCustomer);
			}
		});
	};
	$scope.updateLocationCustomer = function(){
		var point = $scope.getMarkerPoint();
			CustomerService.updateLocationCustomer($routeParams.id,point.latitude,point.longitude).then(function(data){
				if(data){
					$rootScope.openDialog(LanguageFactory.getLabel('UpdateinfoSuccess'));
				}
			});
	};
	$scope.isChange = false;
	$scope.changeCustomerName = function(){
		$scope.isChange = true;
	}
	$scope.groupName = "";
	$scope.submitCustomerName = function(){
	if($scope.groupName && $scope.groupName.length){
		CustomerService.updateCustomerName($routeParams.id,$scope.groupName).then(function(data){
			if(data.contact){
				$rootScope.openDialog(LanguageFactory.getLabel('UpdateinfoSuccess'));
				$scope.isChange = false;
				$('#customerName').trigger("reset");
			}
		});
	}else {
		$rootScope.openDialog(LanguageFactory.getLabel('ContentNotChange'));
	}
	};
	$scope.back = function() {
		$location.path('/customer');
	};
});
