/*global todomvc, angular */
'use strict';

/**
 * The main controller for the app. The controller: - retrieves and persists the
 * model via the todoStorage service - exposes the model to the template and
 * provides event handlers
 */
olbius.controller('HeaderController', function($rootScope, $scope, $location, $controller, SidebarFactory, AuthService) {
	var self = $scope;
	var root = $rootScope;
	$.extend(this, $controller('BaseController', {
		$scope : self
	}));
	/*show sidebar left*/
	self.showLeftBar = function() {
		SidebarFactory.open();
	};
});
