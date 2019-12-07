$(function(){
	ShipmentDetailObj.init();
});
var ShipmentDetailObj = (function(){
	var validatorVAL;
	
	var init = function(){
		initInputs();
		initElementComplex();
		initEvents();
		initValidateForm();
	};
	var initInputs = function (){
		getDetailShipment(shipmentId);
		$("#estimatedShipDate").text(formatFullDate(new Date(shipment['estimatedShipDate'])));
		$("#estimatedArrivalDate").text(formatFullDate(new Date(shipment['estimatedArrivalDate'])));
	};
	var initElementComplex = function (){
	};
	var initEvents = function (){
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
	var initValidateForm = function (){
		var extendRules = [
//		      				{input: '#actualShipDate', message: uiLabelMap.CannotAfterNow, action: 'valueChanged', 
//		      					rule: function(input, commit){
//		      						var actualShipDate = $('#actualShipDate').jqxDateTimeInput('getDate');
//		      		     		   	var nowDate = new Date();
//		      					   	if ((typeof(actualShipDate) != 'undefined' && actualShipDate != null && !(/^\s*$/.test(actualShipDate)))) {
//		      				 		    if (actualShipDate > nowDate) {
//		      				 		    	return false;
//		      				 		    } 
//		      				 		    return true;
//		      					   	}
//		      					}
//		      				},
//		      				{input: '#actualArrivalDate', message: uiLabelMap.CannotAfterNow, action: 'valueChanged', 
//		      					rule: function(input, commit){
//		      						var actualArrivalDate = $('#actualArrivalDate').jqxDateTimeInput('getDate');
//		      		     		   	var nowDate = new Date();
//		      					   	if ((typeof(actualArrivalDate) != 'undefined' && actualArrivalDate != null && !(/^\s*$/.test(actualArrivalDate)))) {
//		      				 		    if (actualArrivalDate > nowDate) {
//		      				 		    	return false;
//		      				 		    } 
//		      				 		    return true;
//		      					   	}
//		      					}
//		      				},
//		      				{input: '#actualShipDate', message: uiLabelMap.CanNotAfterArrivalDate, action: 'valueChanged', 
//		      					rule: function(input, commit){
//		      						var actualShipDate = $('#actualShipDate').jqxDateTimeInput('getDate');
//		      						var actualArrivalDate = $('#actualArrivalDate').jqxDateTimeInput('getDate');
//		      					   	if ((typeof(actualShipDate) != 'undefined' && actualShipDate != null && !(/^\s*$/.test(actualShipDate))) && (typeof(actualArrivalDate) != 'undefined' && actualArrivalDate != null && !(/^\s*$/.test(actualArrivalDate)))) {
//		      				 		    if (actualShipDate > actualArrivalDate) {
//		      				 		    	return false;
//		      				 		    }
//		      				 		    return true;
//		      					   	}
//		      					}
//		      				},
//		      				{input: '#actualArrivalDate', message: uiLabelMap.CanNotBeforeShipDate, action: 'valueChanged', 
//		      					rule: function(input, commit){
//		      						var actualShipDate = $('#actualShipDate').jqxDateTimeInput('getDate');
//		      						var actualArrivalDate = $('#actualArrivalDate').jqxDateTimeInput('getDate');
//		      					   	if ((typeof(actualShipDate) != 'undefined' && actualShipDate != null && !(/^\s*$/.test(actualShipDate))) && (typeof(actualArrivalDate) != 'undefined' && actualArrivalDate != null && !(/^\s*$/.test(actualArrivalDate)))) {
//		      				 		    if (actualShipDate > actualArrivalDate) {
//		      				 		    	return false;
//		      				 		    }
//		      				 		    return true;
//		      					   	}
//		      					}
//		      				},
		                 ];
		      		var mapRules = [
//		      				{input: '#actualShipDate', type: 'validInputNotNull'},
//		      				{input: '#actualArrivalDate', type: 'validInputNotNull'},
		                  ];
		      		validatorVAL = new OlbValidator($('#detailShipment'), mapRules, extendRules, {position: 'topcenter'});
	};
	var getLocalization = function getLocalization() {
	    var localizationobj = {};
	    localizationobj.pagergotopagestring = uiLabelMap.wgpagergotopagestring + ":";
	    localizationobj.pagershowrowsstring = uiLabelMap.wgpagershowrowsstring + ":";
	    localizationobj.pagerrangestring = uiLabelMap.wgpagerrangestring;
	    localizationobj.pagernextbuttonstring = uiLabelMap.wgpagernextbuttonstring;
	    localizationobj.pagerpreviousbuttonstring = uiLabelMap.wgpagerpreviousbuttonstring;
	    localizationobj.sortascendingstring = uiLabelMap.wgsortascendingstring;
	    localizationobj.sortdescendingstring = uiLabelMap.wgsortdescendingstring;
	    localizationobj.sortremovestring = uiLabelMap.wgsortremovestring;
	    localizationobj.emptydatastring = uiLabelMap.wgemptydatastring;
	    localizationobj.filterselectstring = uiLabelMap.wgfilterselectstring;
	    localizationobj.filterselectallstring = uiLabelMap.wgfilterselectallstring;
	    localizationobj.filterchoosestring = uiLabelMap.filterchoosestring;
	    localizationobj.groupsheaderstring = uiLabelMap.wgdragDropToGroupColumn;
	    localizationobj.todaystring = uiLabelMap.wgtodaystring;
	    localizationobj.clearstring = uiLabelMap.wgclearstring;
	    return localizationobj;
	};
	var getDetailShipment = function(shipmentId){
		var costEstimated = shipment.estimatedShipCost;
		$("#shipmentId").text(shipment.shipmentId);
		$("#originFacilityId").text(shipment.originFacilityName);
		$("#destinationFacilityId").text(shipment.destFacilityName);
		$("#originContactMechId").text(unescapeHTML(shipment.originAddress));
		$("#destinationContactMechId").text(unescapeHTML(shipment.destAddress));
		$("#estimatedShipDate").text(shipment.estimatedShipDate);
		$("#estimatedArrivalDate").text(shipment.estimatedArrivalDate);
		$("#estimatedShipCost").text(costEstimated.toLocaleString(localeStr) + " (" +shipment.currencyUomId+ ")");
//		if (!shipment.actualArrivalDate){
//			var now = new Date();
//			$("#actualArrivalDate").jqxDateTimeInput({width: 200, formatString: 'dd/MM/yyyy HH:mm:ss'});
//			$("#actualArrivalDate").val(now);
//			if (shipment.statusId == "SHIPMENT_INPUT"){
//				$("#actualArrivalDate").jqxDateTimeInput('disabled', true);
//			} else {
//				$("#actualArrivalDate").jqxDateTimeInput('disabled', false);
//			}
//		} else {
//			$("#actualArrivalDate").text(shipment.actualArrivalDate);
//		}
//		if (!shipment.actualShipDate){
//			var now = new Date();
//			$("#actualShipDate").jqxDateTimeInput({width: 200, formatString: 'dd/MM/yyyy HH:mm:ss'});
//			$("#actualShipDate").val(now);
//			if (shipment.statusId == "SHIPMENT_INPUT"){
//				$("#actualShipDate").jqxDateTimeInput('disabled', true);
//			} else {
//				$("#actualShipDate").jqxDateTimeInput('disabled', false);
//			}
//		} else {
//			$("#actualShipDate").text(shipment.actualShipDate);
//		}
		for (var i = 0; i < statusData.length; i ++){
			if (statusData[i].statusId == shipment.statusId){
				$("#statusId").text(statusData[i].description);
			}
		}
		
	};
	function approveShipmentTransfer(shipmentId){
		bootbox.dialog(uiLabelMap.AreYouSureSave, 
		[{"label": uiLabelMap.CommonCancel, 
			"icon": 'fa fa-remove', "class": 'btn  btn-danger form-action-button pull-right',
            "callback": function() {bootbox.hideAll();}
        }, 
        {"label": uiLabelMap.CommonSave,
            "icon": 'fa-check', "class": 'btn btn-primary form-action-button pull-right',
            "callback": function() {
            	Loading.show('loadingMacro');
            	setTimeout(function(){
					$.ajax({
						type: 'POST',
						url: 'updateShipmentTransfer',
						async: false,
						data: {
							shipmentId: shipmentId,
							statusId: "SHIPMENT_SCHEDULED",
						},
						success: function(data){
							location.reload();
						},
					});
					Loading.hide('loadingMacro');
            	}, 500);
            }
		}]);
	}
	function quickShipShipmentTransfer(shipmentId){
		bootbox.dialog(uiLabelMap.AreYouSureSave, 
		[{"label": uiLabelMap.CommonCancel, 
			"icon": 'fa fa-remove', "class": 'btn  btn-danger form-action-button pull-right',
            "callback": function() {bootbox.hideAll();}
        }, 
        {"label": uiLabelMap.CommonSave,
            "icon": 'fa-check', "class": 'btn btn-primary form-action-button pull-right',
            "callback": function() {
            	Loading.show('loadingMacro');
            	setTimeout(function(){
					$.ajax({
						type: 'POST',
						url: 'updateShipmentTransfer',
						async: false,
						data: {
							shipmentId: shipmentId,
							statusId: "SHIPMENT_PACKED",
						},
						success: function(data){
							$.ajax({
								type: 'POST',
								url: 'updateShipmentTransfer',
								async: false,
								data: {
									shipmentId: shipmentId,
									statusId: "SHIPMENT_SHIPPED",
								},
								success: function(data){
									$.ajax({
										type: 'POST',
										url: 'updateShipmentTransfer',
										async: false,
										data: {
											shipmentId: shipmentId,
											statusId: "SHIPMENT_DELIVERED",
										},
										success: function(data){
										/**	$.ajax({
												type: 'POST',
												url: 'updateDatetimeShipmentTransfer',
												async: false,
												data: {
													actualShipDate: $("#actualShipDate").jqxDateTimeInput('getDate').getTime(),
													actualArrivalDate: $("#actualArrivalDate").jqxDateTimeInput('getDate').getTime(),
													shipmentId: shipmentId,
												},
												success: function(data){
													location.reload();
												},
											});
										**/
										},
									});
								},
							});
						},
					});
					Loading.hide('loadingMacro');
            	}, 500);
            }
		}]);
	}
	
	var entityMap = {
	    "&": "&amp;",
	    "<": "&lt;",
	    ">": "&gt;",
	    '"': '&quot;',
	    "'": '&#39;',
	    "/": '&#x2F;'
	 };

	 function escapeHtml(string) {
	    return String(string).replace(/[&<>"'\/]/g, function (s) {
	      return entityMap[s];
	    });
	 }
	 function unescapeHTML(escapedStr) {
	     var div = document.createElement('div');
	     div.innerHTML = escapedStr;
	     var child = div.childNodes[0];
	     return child ? child.nodeValue : '';
	 };
	 
	 function formatFullDate(value) {
			if (value != undefined && value != null && !(/^\s*$/.test(value))) {
				var dateStr = "";
				dateStr += addZero(value.getFullYear()) + '-';
				dateStr += addZero(value.getMonth()+1) + '-';
				dateStr += addZero(value.getDate()) + ' ';
				dateStr += addZero(value.getHours()) + ':';
				dateStr += addZero(value.getMinutes()) + ':';
				dateStr += addZero(value.getSeconds());
				return dateStr;
			} else {
				return "";
			}
		}
		
	function addZero(i) {
	    if (i < 10) {i = "0" + i;}
	    return i;
	}
	var getValidator = function(){
		return validatorVAL;
	}
	return {
		init: init,
		getLocalization: getLocalization,
		approveShipmentTransfer: approveShipmentTransfer,
		quickShipShipmentTransfer: quickShipShipmentTransfer,
		getValidator: getValidator,
	};
}());