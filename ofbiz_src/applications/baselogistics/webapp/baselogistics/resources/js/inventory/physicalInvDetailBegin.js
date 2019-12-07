$(function(){
	PhysicalInvDetailBeginObj.init();
});
var PhysicalInvDetailBeginObj = (function() {
	var init = function() {
		initInputs();
		initElementComplex();
		initEvents();
		initValidateForm();
	};
	var initInputs = function() {
	};
	var initElementComplex = function() {
	};
	var initEvents = function() {
		$('.nav.nav-tabs li').on('click', function(){
	    	// clear parameter
	    	var thisHref = location.href;
	    	var queryParam = thisHref.split("?");
	    	var newHref = "";
	    	if (queryParam != null && queryParam != undefined) {
	    		newHref = queryParam[0] + "?";
	    	}
	    	var isAdded = false;
	    	if (queryParam.length > 1) {
	    		var varsParam = queryParam[1].split("&");
			    for (var i = 0; i < varsParam.length; i++) {
			        var pairParam = varsParam[i].split("=");
			        if(pairParam[0] != 'activeTab'){
			        	if (isAdded) newHref += "&";
			        	newHref += varsParam[i];
			        	isAdded = true;
			        }
			    }
	    	}
	    	var tabObj = $(this).find("a[data-toggle=tab]");
	    	if (tabObj != null && tabObj != undefined) {
	    		var tabHref = tabObj.attr("href");
	    		if (tabHref.indexOf("#") == 0) {
	    			var tabId = tabHref.substring(1);
	    			window.history.pushState({}, "", newHref + '&activeTab=' + tabId);
	    		}
	    	}
	    });
	};
	
	var initValidateForm = function(){
	};
	return {
		init: init,
	}
}());