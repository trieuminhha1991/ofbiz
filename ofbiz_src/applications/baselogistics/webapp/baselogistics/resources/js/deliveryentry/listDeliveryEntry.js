$(function(){
	DlvEntryObj.init();
});
var DlvEntryObj = (function(){
	var init = function(){
		initInputs();
		initElementComplex();
		initEvents();
		var fromDate1 = null;
    	var fromDate2 = null;
    	
    	$("#jqxNotificationSuccess").jqxNotification({ width: "100%", opacity: 0.9, appendContainer: "#containerNotify",
	        autoOpen: false, animationOpenDelay: 800, autoClose: true, template: "success"
	    });
//		initValidateForm();
	};
	
	var initInputs = function initInputs() {
		$("#DeliveryEntryMenu").jqxMenu({ width: 220, autoOpenPopup: false, mode: 'popup', theme: theme});
		
		$("#editPopupWindow").jqxWindow({
			maxWidth: 1000, minWidth: 250, width: 500, minHeight: 200, maxHeight: 1000, height: 200, resizable: true,  isModal: true, modalZIndex: 100000, zIndex: 100000, autoOpen: false, cancelButton: $("#editCancel"), modalOpacity: 0.7, theme:theme           
		});
		
		$("#driverPartyId").jqxDropDownList({placeHolder: uiLabelMap.PleaseSelectTitle, width: 200, selectedIndex: 0, theme: theme, source: driverPartyData, valueMember:'partyId', displayMember:'description', height: '24px', dropDownHeight: 200});
		$("#delivererPartyId").jqxDropDownList({placeHolder: uiLabelMap.PleaseSelectTitle, width: 200, selectedIndex: 0, theme: theme, source: delivererPartyData, valueMember:'partyId', displayMember:'description', height: '24px', dropDownHeight: 200});
	
	};
	
	var initElementComplex = function initElementComplex() {
	}
	
	var initEvents = function initEvents() {
		$("#DeliveryEntryMenu").on('itemclick', function (event) {
	        var args = event.args;
	        var rowindex = $("#jqxgrid").jqxGrid('getselectedrowindex');
	        var dataRecord = $("#jqxgrid").jqxGrid('getrowdata', rowindex);
	        var deliveryEntryId = dataRecord.deliveryEntryId;
	        if ($.trim($(args).text()) == uiLabelMap.CommonCancel) {
	        	bootbox.dialog(uiLabelMap.AreYouSureDelete, 
    			[{"label": uiLabelMap.CommonCancel, 
    				"icon": 'fa fa-remove', "class": 'btn  btn-danger form-action-button pull-right',
    	            "callback": function() {
    	            	bootbox.hideAll();
    	            	$('#jqxgrid').jqxGrid('clearSelection');
    	            }
    	        }, 
    	        {"label": uiLabelMap.OK,
    	            "icon": 'fa-check', "class": 'btn btn-primary form-action-button pull-right',
    	            "callback": function() {
    	            	deleteDeliveryEntry(deliveryEntryId);
    	            }
    			}]);
	        } else if ($.trim($(args).text()) == uiLabelMap.CommonEdit) {
	        	var listDeliveryEntryRoles = [];
	        	$.ajax({
	        		url: "getDeliveryEntryRoles",
	    			type: "POST",
	    			data: {deliveryEntryId: deliveryEntryId},
	    			dataType: "json",
	    			async: false,
	    			success: function(data) {
	    				listDeliveryEntryRoles = data.listDeliveryEntryRoles;
	    			}
	        	});
	        	var curDriverId = null;
	        	var curDelivererId = null;
	        	
	        	for (var i = 0; i < listDeliveryEntryRoles.length; i ++){
	        		var data = listDeliveryEntryRoles[i];
	        		if (data.roleTypeId == "LOG_DRIVER") {
	        			curDriverId = data.partyId;
	        			fromDate1 = data.fromDate;
	        		}
	        		if (data.roleTypeId == "LOG_DELIVERER") {
	        			curDelivererId = data.partyId;
	        			fromDate2 = data.fromDate;
	        		}
	        	}
	        	if (curDriverId != null){
	        		$("#driverPartyId").jqxDropDownList('val', curDriverId);
	        	}
	        	if (curDelivererId != null){
	        		$("#delivererPartyId").jqxDropDownList('val', delivererPartyId);
	        	}
	        	$("#deliveryEntryIdEdit").val(deliveryEntryId);
	        	$("#editPopupWindow").jqxWindow('open');
	        } else if ($.trim($(args).text()) == uiLabelMap.ViewDetailInNewPage) {
	        	window.open("deliveryEntryDetail?deliveryEntryId=" + dataRecord.deliveryEntryId, '_blank');
	        } else if ($.trim($(args).text()) == uiLabelMap.BSViewDetail) {
	        	window.location.href = "deliveryEntryDetail?deliveryEntryId=" + dataRecord.deliveryEntryId;
	        } else if ($.trim($(args).text()) == uiLabelMap.BSRefresh) {
	        	$('#jqxgrid').jqxGrid('updatebounddata');
	        }
	    });
		
		$("#editSave").on('click', function (event) {
			var driverId = $("#driverPartyId").jqxDropDownList('val');
			var delivererId = $("#delivererPartyId").jqxDropDownList('val');
			var deliveryEntryId = $("#deliveryEntryIdEdit").val();
			bootbox.dialog(uiLabelMap.AreYouSureUpdate, 
			[{"label": uiLabelMap.CommonCancel, 
				"icon": 'fa fa-remove', "class": 'btn  btn-danger form-action-button pull-right',
	            "callback": function() {
	            	bootbox.hideAll();
	            	$('#jqxgrid').jqxGrid('clearSelection');
	            }
	        }, 
	        {"label": uiLabelMap.OK,
	            "icon": 'fa-check', "class": 'btn btn-primary form-action-button pull-right',
	            "callback": function() {
	            	Loading.show('loadingMacro');
	            	setTimeout(function(){
	            	$.ajax({
	        			url: "updateDeliveryEntryRole",
	        			type: "POST",
	        			async: false,
	        			data: {
	        				"deliveryEntryId": deliveryEntryId,
	        				"partyId": driverId,
	        				"roleTypeId": "LOG_DRIVER",
	        				"fromDate": fromDate1.time,
	        			},
	        			dataType: "json",
	        			success: function(data) {
	        				$.ajax({
	    	        			url: "updateDeliveryEntryRole",
	    	        			type: "POST",
	    	        			async: false,
	    	        			data: {
	    	        				"deliveryEntryId": deliveryEntryId,
	    	        				"partyId": delivererId,
	    	        				"roleTypeId": "LOG_DELIVERER",
	    	        				"fromDate": fromDate2.time,
	    	        			},
	    	        			dataType: "json",
	    	        			success: function(data) {
	    	        				
	    	        			}
	    	        		});
	        			}
	        		});
	            	$("#jqxgrid").jqxGrid('updatebounddata');
	    			$('#jqxgrid').jqxGrid('clearselection');
	    			$("#editPopupWindow").jqxWindow('close');
	    			Loading.hide('loadingMacro');	
	            	}, 500);
	            	$("#jqxNotificationSuccess").jqxNotification('open');
	            }
			}]);
		});
	}
	
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
	var createDeliveryEntry = function(){
		window.open('prepareCreateDeliveryEntry','_blank');
	};
	
	var addZero = function(i) {
	    if (i < 10) {i = "0" + i;}
	    return i;
	};
	var formatFullDate = function(value) {
		if (value) {
			var dateStr = "";
			dateStr += addZero(value.getDate()) + '/';
			dateStr += addZero(value.getMonth()+1) + '/';
			dateStr += addZero(value.getFullYear()) + ' ';
			dateStr += addZero(value.getHours()) + ':';
			dateStr += addZero(value.getMinutes()) + ':';
			dateStr += addZero(value.getSeconds());
			return dateStr;
		} else {
			return "";
		}
	};
	
	function deleteDeliveryEntry(deliveryEntryId){
		$.ajax({
			url: "deleteDeliveryEntry",
			type: "POST",
			data: {deliveryEntryId: deliveryEntryId},
			dataType: "json",
			success: function(data) {
			}
		}).done(function(data) {
			$("#jqxgrid").jqxGrid('updatebounddata');
			$('#jqxgrid').jqxGrid('clearselection');
		});
	}
	var getDetailShipment = function(shipmentId){
	};
	return {
		init: init,
//		getDetailShipment: getDetailShipment,
		getLocalization: getLocalization,
		createDeliveryEntry: createDeliveryEntry,
		formatFullDate: formatFullDate,
	};
}());