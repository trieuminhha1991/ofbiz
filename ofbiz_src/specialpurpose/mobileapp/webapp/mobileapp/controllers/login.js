/*global todomvc, angular */

'use strict';
/**
 * The main controller for the app. The controller:
 * - retrieves and persists the model via the todoStorage service
 * - exposes the model to the template and provides event handlers
 */
olbius.controller('LoginController', function($rootScope, $scope, $controller, $location, SidebarFactory, AuthService, LanguageFactory) {
	var root = $rootScope;
	var self = $scope;
	$.extend(this, $controller('BaseController', {
		$scope : self
	}));

	root.isHeader = false;
	self.credentials = {
		USERNAME : 'OLBSAGT0002',
		PASSWORD : 'oblofbiz'
	};
	root.login = true;
	self.$on("$viewContentLoaded", function() {
		setTimeout(function() {
			SidebarFactory.disable();
		}, 500);
	});
	self.login = function() {
		var username = self.credentials.USERNAME;
		var password = self.credentials.PASSWORD;
		self.credentials = {
			USERNAME : username,
			PASSWORD : password
		};
		AuthService.login(self.credentials).then(function(res) {
			if (res.login == "TRUE") {
				self.changeState("/store");
			} else {
				self.buildAlert("", LanguageFactory.getLabel('LoginFailed'));
			}
		}, function() {
			self.buildAlert("", LanguageFactory.getLabel('LoginFailed'));
		});
	};
});
olbius.directive('ngEnter', function() {
	return function(scope, element, attrs) {
		element.bind("keydown keypress", function(event) {
			if (event.which === 13) {
				scope.$apply(function() {
					scope.$eval(attrs.ngEnter);
				});
				event.preventDefault();
			}
		});
	};
});