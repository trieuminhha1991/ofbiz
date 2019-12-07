$(function(){
	DEDetailBeginObj.init();
});
var DEDetailBeginObj = (function() {
	var init = function() {
		initInputs();
		initElementComplex();
		initEvents();
		initValidateForm();
	};
	var initInputs = function() {
		$("#confirmWindow").jqxWindow({
			maxWidth: 1000, minWidth: 600, width: 680, minHeight: 100, height: 250, maxHeight: 800, resizable: false, cancelButton: $("#confirmCancel"), keyboardNavigation: true, keyboardCloseKey: 15,  isModal: true, autoOpen: false, modalOpacity: 0.7, theme:theme           
		});
		
		$("#shipCostConfirm").jqxNumberInput({ width: 300, height: 26, spinButtons: true });
		if (shipCost) {
			$("#shipCostConfirm").jqxNumberInput('val', shipCost);
		}
		$("#fromDateConfirm").jqxDateTimeInput({width: 300, theme: theme, height: '24px', formatString : 'dd/MM/yyyy HH:mm'});
		$("#fromDateConfirm").jqxDateTimeInput('clear');
		if (fromDate) {
			$("#fromDateConfirm").jqxDateTimeInput('val', fromDate);
		}
		
		//Create thruDate
		$("#thruDateConfirm").jqxDateTimeInput({width: 300, theme: theme, height: '24px', formatString : 'dd/MM/yyyy HH:mm'});
		$("#thruDateConfirm").jqxDateTimeInput('clear');
		if (thruDate) {
			$("#thruDateConfirm").jqxDateTimeInput('val', thruDate);
		}
		
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
		$('#confirmSave').on('click', function(){
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
				    		url: "updateDeliveryEntry",
				    		type: "POST",
				    		async: false,
				    		data: {
				    			deliveryEntryId: deliveryEntryId,
				    			statusId: "DELI_ENTRY_DELIVERED",
				    			fromDate: $("#fromDateConfirm").jqxDateTimeInput('getDate').getTime(),
				    			thruDate: $("#thruDateConfirm").jqxDateTimeInput('getDate').getTime(),
				    			shipCost: $("#shipCostConfirm").jqxNumberInput('val'),
				    		},
				    		success: function (res){
				    			if (res._ERROR_MESSAGE_) {
									jOlbUtil.alert.error(uiLabelMap.HasErrorWhenProcess);
									return;
								}
				    			location.reload();
				    		}
				    	});
					Loading.hide('loadingMacro');
	            	}, 500);
			    }
			}]);
	    });
	};
	
	var initValidateForm = function(){
	};
	
	var cancelDeliveryEntry = function(deliveryEntryId){
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
				    		url: "deleteDeliveryEntry",
				    		type: "POST",
				    		async: false,
				    		data: {
				    			deliveryEntryId: deliveryEntryId,
				    		},
				    		success: function (res){
				    			if (data._ERROR_MESSAGE_) {
									jOlbUtil.alert.error(uiLabelMap.HasErrorWhenProcess);
									return;
								}
				    			location.reload();
				    		}
				    	});
					Loading.hide('loadingMacro');
	            	}, 500);
			    }
			}]);
	};
	
	var completeDlvEntry = function(deliveryEntryId){
		$("#confirmWindow").jqxWindow('open');
	};
	
	return {
		init: init,
		cancelDeliveryEntry:cancelDeliveryEntry,
		completeDlvEntry: completeDlvEntry,
	}
}());