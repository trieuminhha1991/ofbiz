/*global todomvc, angular */
'use strict';

/**
 * The main controller for the app. The controller: - retrieves and persists the
 * model via the todoStorage service - exposes the model to the template and
 * provides event handlers
 */
olbius.controller('EmployeeLeaveReportController', function($rootScope, $scope,
		$controller, $location, EmployeeService) {

	var self = $scope;
	var root = $rootScope;
	self.emlLeaveStatus = [];
	$.extend(this, $controller('BaseController', {
		$scope : self
	}));
	$scope.$on('$viewContentLoaded', function() {
		self.setHeader("HistoryLeave", "/employee-leave", false);
		self.getDataType('emlLeaveStatus', self.initData,  EmployeeService.getHistory);
	});
	self.initData = function(data){
		self.emlLeaveStatus = data;
	};
});
