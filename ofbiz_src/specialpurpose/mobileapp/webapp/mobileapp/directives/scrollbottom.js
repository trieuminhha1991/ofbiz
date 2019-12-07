olbius.directive('whenScrolled', function() {
    return function(scope, elm, attr) {
        $(window).bind('scroll', function(res) {
		//console.log(res);
        });
    };
});
olbius.directive('focusMe', function($timeout) {
	return {
		link : function(scope, element, attrs) {
			scope.$watch(attrs.focusMe, function(value) {
				if (value === true) {
					$timeout(function() {
						element[0].focus();
						scope[attrs.focusMe] = false;
					}, 350);
				}
			});
		}
	};
});