$(function(){
	QuotaDetailBegin.init();
});
var QuotaDetailBegin = (function() {
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
	
	function approveQuotaHeader(quotaId){
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
			    		url: "changeQuotaStatus",
			    		type: "POST",
			    		async: false,
			    		data: {
			    			quotaId: quotaId,
			    			statusId: "QUOTA_APPROVED",
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
	}
	
	var cancelQuotaHeader = function (quotaId){
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
			    		url: "changeQuotaStatus",
			    		type: "POST",
			    		async: false,
			    		data: {
			    			quotaId: quotaId,
			    			statusId: "QUOTA_CANCELLED",
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
	
	var exportQuotaHeader = function (quotaId){
		window.open("exportQuotaExcel?quotaId="+ quotaId, "_blank");
	};
	
	var createQuotaHeader = function(){
		if (ObjAddQuota != undefined){
			ObjAddQuota.openPopupAdd();
		}
	}
	
	var editQuotaHeader = function(){
		if (ObjEditQuota != undefined && quotaHeader != undefined){
			ObjEditQuota.openPopupEdit(quotaHeader);
		}
	}
	
	return {
		init: init,
		approveQuotaHeader: approveQuotaHeader,
		cancelQuotaHeader: cancelQuotaHeader,
		exportQuotaHeader: exportQuotaHeader,
		editQuotaHeader: editQuotaHeader,
		createQuotaHeader: createQuotaHeader,
	}
}());