/*init function*/
olbius.run(function($window, $rootScope, $route, $templateCache, $location, SidebarFactory, StorageFactory) {
	var root = $rootScope;
	$templateCache.put('searchbox.tpl.html', '<input id="pac-input" class="pac-controls" type="text" placeholder="Search" ng-model="self.searchValue">');
	$templateCache.put('window.tpl.html', '<div ng-controller="WindowCtrl" ng-init="showPlaceDetails(parameter)">{{place.name}}</div>');
	root.listDialog = [];
	root.mainloading = "MainLoading";
	root.Map = language;
	var key = StorageFactory.getLocalItem('currentLanguage');
	root.locale = key ? key : "vi";
	root.$on("$locationChangeStart", function(e, toState, fromState){
		SidebarFactory.close();
		var login = localStorage.login;
		if(login == 'false' || !login){
			$location.path("/login");
		}
		if(toState.indexOf('login') != -1 && fromState.indexOf('login') == -1 && (login != 'false' && login)){
			e.preventDefault();
		}else if(login){
			// $location.path("/main");
		}else{
			$location.path("/login");
		}
	});
});


olbius.controller('WindowCtrl', function($scope) {
	$scope.place = {};
	$scope.showPlaceDetails = function(param) {
		$scope.place = param;
	};
});
