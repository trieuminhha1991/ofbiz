/*global todomvc, angular */
'use strict';

/**
 * The main controller for the app. The controller:
 * - retrieves and persists the model via the todoStorage service
 * - exposes the model to the template and provides event handlers
 */

olbius.controller('CustomerController', function($rootScope, $scope, $controller, $window, CustomerService) {
	$.extend(this, $controller('BaseController', {
		$scope : $scope
	}));
	$scope.customers = {
		content : [],
		total : 0,
		size : 0,
		road : []
	};
	$scope.color = ["road1", "road2"];
	$scope.currentPage = 0;
	$scope.loading = false;

	$scope.link = "#/customer/detail/";
	$scope.$on('$viewContentLoaded', function() {
		$scope.setHeader('Customers', "/main", false);
		$scope.init();
		$scope.scroll();
	});

	$scope.scroll = function() {
		var obj = angular.element($window);
		var bottom = angular.element($("#bottom"));
		obj.bind("scroll", function(res) {
			if ((this.pageYOffset + $(window).height()) == $(document).height()) {
				$scope.loadMore();
			}
		});
	};
	/*init list Order*/
	$scope.init = function() {
		if (localStorage.currentCustomer) {
			var key = "customers";
			if (localStorage.getItem(key)) {
				var list = JSON.parse(localStorage.getItem(key));
				if (list && list.content.length) {
					$scope.customers = list;
					var length = list.content.length;
					$scope.currentPage = Math.ceil(length / config.pageSize);
				}
			} else {
				$scope.loadMore(true);
			}
			localStorage.setItem("iscurrent", $scope.iscurrent);
		}
	};
	/*load more data when scroll to bottoms*/
	$scope.loadMore = function(init) {
		if ($scope.customers.total && $scope.customers.content.length) {
			if ($scope.currentPage < ($scope.customers.total - 1)) {
				$scope.currentPage++;
				$scope.getData(true);
			}
		} else if (init) {
			$scope.getData();
		}
	};
	/*get data from server*/
	$scope.getData = function(status) {
		if (!status) {
			var refresh = $rootScope.showLoading;
			var close = $rootScope.hideLoading;
		} else {
			// load more data
			var refresh = function() {
				$scope.loading = true;
				$("#refresh").addClass("rotate");
			};
			var close = function() {
				$scope.loading = false;
				$("#refresh").removeClass("rotate");
			};
		}
		$scope.getList(refresh, close);
	};
	$scope.getList = function(refresh, close) {
		var partyId = "";
		var key = "customers";
		CustomerServicgetStoreByRoad($scope.currentPage, 30,'N',refresh).then(function(data) {
			$scope.customers.total = data.total;
			$scope.customers.road = data.road;
			$scope.customers.size = data.size;
			if (data.customers) {
				if ($scope.customers.content.length) {
					for (var x in data.customers) {
						$scope.customers.content.push(data.customers[x]);
					}
				} else {
					$scope.customers.content = data.customers;
				}
			}
			localStorage.setItem(key, JSON.stringify($scope.customers));
			close();
		}, function() {
			close();
		});
	};
	// $scope.getColor = function(ip){
		// var val = $scope.getNumberFromString(ip);
		// var length = $scope.color.length;
		// var index = val%length;
//
//
		// return $scope.color[index];
	// };
	// $scope.getNumberFromString = function(ip) {
		// var total = 0;
		// for (var i = 0; i < ip.length; i++) {
			// total += ip.charCodeAt(i);
		// }
		// return total;
	// };
	$scope.onSelectRow = function(id) {
		$scope.changeState('customer/detail/' + id);
	};
	$scope.createCustomer = function() {
		$scope.changeState('customer/create');
	};
});
