/*global todomvc, angular */
'use strict';

/**
 * The main controller for the app. The controller:
 * - retrieves and persists the model via the todoStorage service
 * - exposes the model to the template and provides event handlers
 */
olbius.controller('PortalController', function($rootScope, $scope, $controller, ProductService) {
    $.extend(this, $controller('BaseController', {
        $scope: $scope
    }));
    $scope.newProduct = Array();
    $scope.$on('$viewContentLoaded', function() {
	$scope.setHeader("Cổng thông tin", "/main", false);
    });
    $scope.getNewProduct = function() {
        ProductService.getNewProduct($rootScope.showLoading).then(function(res) {
            $scope.newProduct = res.newProduct;
            $rootScope.hideLoading();
        }, function(){
		$rootScope.hideLoading();
        });
    };
});
