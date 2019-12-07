/*global todomvc, angular */
'use strict';

/**
 * The main controller for the app. The controller:
 * - retrieves and persists the model via the todoStorage service
 * - exposes the model to the template and provides event handlers
 */
olbius.controller('IndexController', function($scope, $location, AuthService) {
	$scope.$on("$viewContentLoaded", function(){
		var width = $(window).width();
		var height = $(window).height();
		$('.fullscreen').css("width", width+"px");
		$('.fullscreen').css("height", height+"px");
	});
    AuthService.checkLogin().then(function(res) {
        if (res !== "TRUE") {
            $location.path('/store');
        } else {
            $location.path('/store');
            localStorage.login = true;
        }
    }, function(res){
		console.log("login error"+ JSON.stringify(res));
	 $location.path('/store');
    });
});
