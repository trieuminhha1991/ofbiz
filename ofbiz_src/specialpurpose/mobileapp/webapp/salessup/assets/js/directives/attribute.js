if(typeof app !== undefined)
	app.directive('showCustomLoading',function(){
		var init = function(scope, element, attrs){
			console.log(element);
			
			$(element).append('<ion-spinner icon="spiral"></ion-spinner>');
			
		}
		
		return {
			restrict: 'AEC',
			scope : {
				selector : "=?",
			},
			link : init
		}
	})
	
	app.directive('olbEnter',function(){
		var init = function(scope, element, attrs){
			console.log(scope,element);
			
		}
		
		return {
			restrict: 'AEC',
			scope : {
				target : "=?",
			},
			link : init
		}
	})
	
	app.directive('disableTap', function($timeout) {
	  return {
	    link: function() {
	      $timeout(function() {
	    	  var element = document.querySelector('#searchAddress');
	    	  if(element != null)
	    		  element.setAttribute('data-tap-disabled', 'true')
	      },500);
	    }
	  };
	});
	
