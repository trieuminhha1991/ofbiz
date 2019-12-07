$(function(){
	SupDetailOpenObj.init();
});
var SupDetailOpenObj = (function() {
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
	
	function approveReturn(returnId) {
		bootbox.dialog(uiLabelMap.AreYouSureApprove, [ {
			"label" : uiLabelMap.CommonCancel,
			"icon" : "fa fa-remove",
			"class" : "btn  btn-danger form-action-button pull-right",
			"callback" : function() {
				bootbox.hideAll();
			}
		}, {
			"label" : uiLabelMap.OK,
			"icon" : "fa-check",
			"class" : "btn btn-primary form-action-button pull-right",
			"callback" : function() {
				Loading.show("loadingMacro");
				setTimeout(function() {
					$.ajax({
						url : "updateReturnSupplier",
						type : "POST",
						data : {
							returnId : returnId,
							statusId : "SUP_RETURN_ACCEPTED",
							needsInventoryReceive : "N"
						},
						dataType : "json",
						success : function(data) {
							$.ajax({
								url : "sendNotifyFromPOToLogReturn",
								type : "POST",
								data : {
									returnId : returnId
								},
								success : function(data) {
								}
							});
							location.reload();
						}
					});
					Loading.hide("loadingMacro");
				}, 500);
			}
		} ]);
	}

	var initValidateForm = function() {
	};

	var cancelReturn = function(returnId) {
		bootbox.dialog(uiLabelMap.AreYouSureCancel, [ {
			"label" : uiLabelMap.CommonCancel,
			"icon" : "fa fa-remove",
			"class" : "btn  btn-danger form-action-button pull-right",
			"callback" : function() {
				bootbox.hideAll();
			}
		}, {
			"label" : uiLabelMap.OK,
			"icon" : "fa-check",
			"class" : "btn btn-primary form-action-button pull-right",
			"callback" : function() {
				Loading.show("loadingMacro");
				setTimeout(function() {
					$.ajax({
						url : "updateReturnSupplier",
						type : "POST",
						data : {
							returnId : returnId,
							statusId : "SUP_RETURN_CANCELLED"
						},
						dataType : "json",
						success : function(data) {
							location.reload();
						}
					});
					Loading.hide("loadingMacro");
				}, 500);
			}
		} ]);
	};
	
	var editReturn = function (returnId){
		window.location.href = "prepareEditReturnSupplier?returnId=" +  returnId;
	};
	
	var prepareExportReturn = function prepareExportReturn(returnId){
		window.location.href = "prepareExportProductFromVendorReturn?returnId="+returnId;
	}
	
	var exportPDFReturn = function exportPDFReturn(returnId){
		window.open("printSupplierReturn.pdf?returnId="+returnId, "_blank");
	}
	return {
		init : init,
		approveReturn : approveReturn,
		cancelReturn : cancelReturn,
		editReturn: editReturn,
		prepareExportReturn: prepareExportReturn,
		exportPDFReturn: exportPDFReturn,
	}
}());