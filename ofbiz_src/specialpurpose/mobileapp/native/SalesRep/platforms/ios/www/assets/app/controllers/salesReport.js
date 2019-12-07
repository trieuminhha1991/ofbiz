app.controller('SalesReportController', function($rootScope, $scope, $location, $timeout, $controller) {
	var self = $scope;
	var root = $rootScope;
	$.extend(this, $controller('BaseController', {
		$scope : self
	}));

});
