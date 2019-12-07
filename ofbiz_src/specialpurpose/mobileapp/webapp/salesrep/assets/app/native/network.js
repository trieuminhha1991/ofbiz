// app.factory('Network', ['$rootScope', '$cordovaNetwork', 'Popup', function($rootScope, network, Popup) {
app.factory('Network', ['$rootScope', 'Popup', function($rootScope, Popup) {
	var self = this;
	self.setting = function(){
		if(window.cordova && cordova.plugins && cordova.plugins.diagnostic){
			var diag = cordova.plugins.diagnostic;
			var act = diag.switchToMobileDataSettings ? diag.switchToMobileDataSettings : diag.switchToSettings;
			if(act){
				act();
			}
		}
	};
	self.isOnline = function(){
		if(typeof(network) != 'undefined'){
			return network.isOnline();
		}
		return navigator.onLine;
	};
	self.isOffline = function(){
		if(typeof(network) != 'undefined'){
			return network.isOffline();
		}
		return !navigator.onLine;
	};
	return self;
}]);