$(function(){
	CustomerDetailOpenObj.init();
});
var CustomerDetailOpenObj = (function() {
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
	
	function approveReturn(returnId){
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
            			url: "updateReturnHeader",
						type: "POST",
						data: {returnId: returnId, statusId: "RETURN_ACCEPTED", needsInventoryReceive: "N"},
						dataType: "json",
						success: function(data) {
						}
			    	}).done(function(data) {
			    		if (data._ERROR_MESSAGE_){
			    			jOlbUtil.alert.error(data._ERROR_MESSAGE_);
			    			return false;
			    		}
			    		if (data._ERROR_MESSAGE_LIST_){
			    			var x = data._ERROR_MESSAGE_LIST_;
			    			jOlbUtil.alert.error(x[0]);
			    			return false;
			    		}
		      			location.reload();
		      		});
				Loading.hide('loadingMacro');
            	}, 500);
		    }
		}]);
	}

    function approveReturnDistributor(returnId){
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
                                url: "updateReturnHeaderDist",
                                type: "POST",
                                data: {returnId: returnId, statusId: "RETURN_ACCEPTED", needsInventoryReceive: "N"},
                                dataType: "json",
                                success: function(data) {
                                }
                            }).done(function(data) {
                                location.reload();
                            });
                            Loading.hide('loadingMacro');
                        }, 500);
                    }
                }]);
    }
	
	var initValidateForm = function(){
	};
	
	var cancelReturn = function (returnId){
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
            			url: "updateReturnHeader",
						type: "POST",
						data: {returnId: returnId, statusId: "RETURN_CANCELLED"},
						dataType: "json",
						success: function(data) {
							location.reload();
						}
			    	});
				Loading.hide('loadingMacro');
            	}, 500);
		    }
		}]);
	};

    var cancelReturnDistributor = function (returnId){
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
                                url: "updateReturnHeaderDist",
                                type: "POST",
                                data: {returnId: returnId, statusId: "RETURN_CANCELLED"},
                                dataType: "json",
                                success: function(data) {
                                    location.reload();
                                }
                            });
                            Loading.hide('loadingMacro');
                        }, 500);
                    }
                }]);
    };
    
    var prepareReceiveCustomerReturn = function (){
    	window.location.href = "prepareReceiveProductFromCustomerReturn?returnId="+returnId;
    }

    var exportPDFReturn = function exportPDFReturn(returnId){
        window.open("printCustomerReturn.pdf?returnId="+returnId, "_blank");
    };
	
	return {
		init: init,
		approveReturn: approveReturn,
		cancelReturn: cancelReturn,
        approveReturnDistributor: approveReturnDistributor,
        cancelReturnDistributor: cancelReturnDistributor,
        prepareReceiveCustomerReturn: prepareReceiveCustomerReturn,
        exportPDFReturn: exportPDFReturn,
	}
}());