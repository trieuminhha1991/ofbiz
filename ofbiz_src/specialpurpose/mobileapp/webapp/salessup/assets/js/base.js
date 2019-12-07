var app = angular.module('salessup', ['ionic','uiGmapgoogle-maps','geolocation'])

.run(function($ionicPlatform,$rootScope) {
  $ionicPlatform.ready(function() {
	  
    if (window.cordova && window.cordova.plugins.Keyboard) {
      cordova.plugins.Keyboard.hideKeyboardAccessoryBar(true);
      cordova.plugins.Keyboard.disableScroll(true);

    }
    if (window.StatusBar) {
      // org.apache.cordova.statusbar required
      StatusBar.styleDefault();
    }
    
    $rootScope.totalRoad = 0;
    $rootScope.totalSaler = 0;
    
  });
  
  $ionicPlatform.registerBackButtonAction(function(event) {
	    if (true) {
	      $ionicPopup.confirm({
	        title: 'System warning',
	        template: 'are you sure you want to exit?'
	      }).then(function(res) {
	        if (res) {
	          ionic.Platform.exitApp();
	        }
	      })
	    }
	  }, 100);
})

.config(function(uiGmapGoogleMapApiProvider) {
 uiGmapGoogleMapApiProvider.configure({
  key: 'AIzaSyDX5vhVfNu12hcIgPgVXz-NMpClz-vQleg',
  v: '3.17',
  libraries: 'weather,geometry,visualization'
 });
});


