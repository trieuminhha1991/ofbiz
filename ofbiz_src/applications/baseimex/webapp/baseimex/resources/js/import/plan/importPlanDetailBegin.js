$(function(){
	PlanDetailBeginObj.init();
});
var PlanDetailBeginObj = (function() {
	var init = function() {
		var noteValidate;
		initInputs();
		initElementComplex();
		initEvents();
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
	
	function approvePlan(productPlanId){
		bootbox.dialog(uiLabelMap.AreYouSureApprove, 
		[{"label": uiLabelMap.CommonCancel, 
			"icon": 'fa fa-remove', "class": 'btn  btn-danger form-action-button pull-right',
		    "callback": function() {bootbox.hideAll();}
		}, 
		{"label": uiLabelMap.OK,
		    "icon": 'fa-check', "class": 'btn btn-primary form-action-button pull-right',
		    "callback": function() {
		    	Loading.show('loadingMacro');
            	setTimeout(function(){
            		$.ajax({
			    		url: "changeProductPlanStatus",
			    		type: "POST",
			    		async: false,
			    		data: {
			    			productPlanId: productPlanId,
			    			statusId: "IMPORT_PLAN_APPROVED",
			    		},
			    		success: function (res){
			    			if (res._ERROR_MESSAGE_ != undefined && res._ERROR_MESSAGE_ != null) {
								jOlbUtil.alert.error(res._ERROR_MESSAGE_);
								Loading.hide("loadingMacro");
								return false;
							}
			    			location.reload();
			    		}
			    	});
				Loading.hide('loadingMacro');
            	}, 500);
		    }
		}]);
	};
	
	var cancelPlan = function (productPlanId){
		bootbox.dialog(uiLabelMap.AreYouSureCancel, 
		[{"label": uiLabelMap.CommonCancel, 
			"icon": 'fa fa-remove', "class": 'btn  btn-danger form-action-button pull-right',
		    "callback": function() {bootbox.hideAll();}
		}, 
		{"label": uiLabelMap.OK,
		    "icon": 'fa-check', "class": 'btn btn-primary form-action-button pull-right',
		    "callback": function() {
		    	Loading.show('loadingMacro');
            	setTimeout(function(){
            		$.ajax({
			    		url: "changeProductPlanStatus",
			    		type: "POST",
			    		async: false,
			    		data: {
			    			productPlanId: productPlanId,
			    			statusId: "IMPORT_PLAN_CANCELLED",
			    		},
			    		success: function (res){
			    			if (res._ERROR_MESSAGE_ != undefined && res._ERROR_MESSAGE_ != null) {
								jOlbUtil.alert.error(res._ERROR_MESSAGE_);
								Loading.hide("loadingMacro");
								return false;
							}
			    			location.reload();
			    		}
			    	});
				Loading.hide('loadingMacro');
            	}, 500);
		    }
		}]);
	};
	
	var editPlan= function(productPlanId){
		window.location.href = "updateImExPlan?productPlanId="+productPlanId
	}
	
	return {
		init: init,
		approvePlan: approvePlan,
		cancelPlan: cancelPlan,
		editPlan: editPlan,
	}
}());