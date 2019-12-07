app.controller('ScrollingController', function($rootScope, $scope, $controller, $state, $timeout, $ionicPopup,
	$ionicLoading, $ionicHistory, $ionicScrollDelegate, AppFactory, StorageFactory, NumberFactory, CalendarFactory, LoadingFactory, Popup, GPS) {
	var self = $scope;
	var root = $rootScope;
	self.startPoint = 0;
	self.test = function(e){
		console.log(e);
	};
	self.checkScrollOverTop = function(){
		var position = $ionicScrollDelegate.getScrollPosition();
		if(position.top <= 0){
			self.startPoint = position.top;
		}
	};
	self.onReleaseScrollOver = function(){

	};
});
