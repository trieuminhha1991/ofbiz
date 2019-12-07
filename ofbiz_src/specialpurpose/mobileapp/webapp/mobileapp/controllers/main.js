/*global todomvc, angular */
'use strict';

/**
 * The main controller for the app. The controller: - retrieves and persists the
 * model via the todoStorage service - exposes the model to the template and
 * provides event handlers
 */
olbius.controller('MainController', function($rootScope, $scope, $controller, AuthService) {
	$.extend(this, $controller('BaseController', {
		$scope : $scope
	}));
	$rootScope.isHeader = true;
	$rootScope.isMain = true;
	$scope.setHeader('Menu', "/main", true);
	$scope.$on('$viewContentLoaded', function() {
		$('.main-container').addClass('main-bg');
		$scope.enableScroll();
	});
});
