var app = angular.module('sales', ['ionic', "ngTouch", "ngCordova", "uiGmapgoogle-maps", "highcharts-ng"]);
var imageUrl = config.imageUrl;
var baseUrl = config.baseUrl;
// var showSlash = function() {
	// navigator.splashscreen.show();
// };
// var hideSlash = function() {
	// navigator.splashscreen.hide();
// };
// showSlash();
// var time = setTimeout(function() {
	// hideSlash();
	// clearTimeout(time);
// }, config.splashDelay);
app.run(function($ionicPlatform, $rootScope, $templateCache, $ionicLoading, $state, $timeout, LanguageFactory, StorageFactory, LoadingFactory, GPS) {
	$templateCache.put('searchbox.tpl.html', '<input id="pac-input" class="pac-controls" type="text" placeholder="Search" ng-model="self.searchValue">');
	$templateCache.put('window.tpl.html', '<div ng-controller="WindowCtrl" ng-init="showPlaceDetails(parameter)">{{place.name}}</div>');
	var locale = StorageFactory.getLocalItem(config.storage.locale);
	$rootScope.imageUrl = imageUrl;
	$rootScope.locale = locale ? locale : 'vi';
	$rootScope.language = language;
	var isLogin = StorageFactory.getLocalItem(config.storage.login) == "true" ? true : false;
	$ionicPlatform.ready(function() {
		if (window.cordova && window.cordova.plugins && window.cordova.plugins.Keyboard) {
			cordova.plugins.Keyboard.hideKeyboardAccessoryBar(true);
			cordova.plugins.Keyboard.disableScroll(true);
		}
		if (window.StatusBar) {
			StatusBar.styleDefault();
		}
	});
	ionic.Platform.ready(function() {
		LoadingFactory.showLoading();
		GPS.getCurrentLocation($rootScope, false, LoadingFactory.hideLoading);
		if (isLogin) {
			$state.go(config.defaultState);
		}
	});
	$ionicPlatform.on('resume', function() {
		LoadingFactory.showLoading();
		GPS.getCurrentLocation($rootScope, false, LoadingFactory.hideLoading);
		// if(isLogin){
		// $state.go(config.defaultState);
		// }
	});
	$rootScope.getLabel = function(label) {
		return LanguageFactory.getLabel(label);
	};
	window.oncontextmenu = function(event) {
		event.preventDefault();
		event.stopPropagation();
		return false;
	};
	$rootScope.$on('$stateChangeStart', function(event, toState, toParams, fromState, fromParams) {
		LoadingFactory.resetLoading();
		isLogin = StorageFactory.getLocalItem(config.storage.login) == "true" ? true : false;
		if (isLogin && toState.name == "login" && fromState.name) {
			$rootScope.back = false;
			event.preventDefault();
		} else {
			if (fromState && fromState.name) {
				$rootScope.back = true;
			}
		}
	});
});