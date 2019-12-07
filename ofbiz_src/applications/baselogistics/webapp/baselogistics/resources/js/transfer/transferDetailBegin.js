$(function(){
	TransferDetailBeginObj.init();
});
var TransferDetailBeginObj = (function() {
	var init = function() {
		var noteValidate;
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
	
	function approveTransfer(transferId){
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
			    		url: "changeTransferStatus",
			    		type: "POST",
			    		async: false,
			    		data: {
			    			transferId: transferId,
			    			statusId: "TRANSFER_APPROVED",
			    			setItemStatus: "Y",
			    			newItemStatus: "TRANS_ITEM_APPROVED",
			    		},
			    		success: function (res){
			    			location.reload();
			    		}
			    	});
				Loading.hide('loadingMacro');
            	}, 500);
		    }
		}]);
	}
	
	var initValidateForm = function(){
		var mapNoteRules = [
            {input: '#note', type: 'validInputNotNull'},
		];
		noteValidate = new OlbValidator($('#noteTransfer'), mapNoteRules, null, {position: 'right'});
	};
	
	var cancelTransfer = function (transferId){
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
			    		url: "changeTransferStatus",
			    		type: "POST",
			    		async: false,
			    		data: {
			    			transferId: transferId,
			    			statusId: "TRANSFER_CANCELLED",
			    			setItemStatus: "Y",
			    			newItemStatus: "TRANS_ITEM_CANCELLED",
			    		},
			    		success: function (res){
			    			location.reload();
			    		}
			    	});
				Loading.hide('loadingMacro');
            	}, 500);
		    }
		}]);
	};
	
	var completeTransfer = function (transferId){
		bootbox.dialog(uiLabelMap.AreYouSureSave, 
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
								url: "completeTransfer",
								type: "POST",
								async: false,
								data: {
									transferId: transferId,
								},
								success: function (res){
									location.reload();
								}
							});
							Loading.hide('loadingMacro');
						}, 500);
					}
				}]);
	};
	
	var prepareRejectTransfer = function (){
		$("#noteTransfer").jqxWindow("open");
	};
	
	var editTransfer = function(){
		window.location.href = "editTransfer?transferId="+transferId
	}
	
	return {
		init: init,
		approveTransfer: approveTransfer,
		cancelTransfer: cancelTransfer,
		prepareRejectTransfer: prepareRejectTransfer,
		editTransfer: editTransfer,
		completeTransfer: completeTransfer,
	}
}());