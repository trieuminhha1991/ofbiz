$(function(){
	DetailReturnObj.init();
});
var DetailReturnObj = (function(){
	var init = function(){
		initInputs();
		initElementComplex();
		initEvents();
		initValidateForm();
	};
	var initInputs = function (){
		$("#entryDate").text(DatetimeUtilObj.formatFullDate(new Date(entryDate)));
		
		if (receivedCompleted != null){
			$("#receivedDate").text(DatetimeUtilObj.formatFullDate(new Date(receivedCompleted)));
		} else if (receivedDate != null){
			$("#receivedDate").text(DatetimeUtilObj.formatFullDate(new Date(receivedDate)));
		} 
	};
	var initElementComplex = function (){
	};
	var initEvents = function (){
		
	};
	var initValidateForm = function (){
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
	
	var changeReturnStatus = function changeReturnStatus(returnId, statusId){
		var href = "";
		if ("CUSTOMER_RETURN" == returnHeaderTypeId){
			if ("RETURN_RECEIVED" == statusId){
				window.location.replace("prepareReceiveReturn?returnId="+returnId+"&facilityId="+destinationFacilityId);
			} else if ("RETURN_ACCEPTED" == statusId){
				var mapAccept = {};
				if ($("#destinationFacilityId").length > 0){
					mapAccept["destinationFacilityId"] = $("#destinationFacilityId").jqxDropDownList('val');
				}
				mapAccept["returnId"] = returnId;
				mapAccept["statusId"] = statusId;
				mapAccept["needsInventoryReceive"] = "N";
				
				bootbox.dialog(uiLabelMap.AreYouSureAccept, 
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
				      			  data: mapAccept,
				      			  dataType: "json",
				      			  success: function(res) {
				      			  }    
				      		}).done(function(data) {
				      			location.reload();
				      		});
				    	Loading.hide('loadingMacro');
				    	}, 500);
				    }
				}]);
			}
		}
		if ("VENDOR_RETURN" == returnHeaderTypeId){
			if (statusId == "SUP_RETURN_SHIPPED"){
				var checkError = false;
				if (checkInventoryItemReserved(returnId) == true){
					if (hasExported == true || hasReserved == true){
						bootbox.dialog(uiLabelMap.ProductHasBeenExported + " " + uiLabelMap.or + " " + uiLabelMap.NotReceivedToFacilityRequired.toLowerCase() + ". " + uiLabelMap.Click + " \"" + uiLabelMap.OK + "\" " + uiLabelMap.orderTo + " " + uiLabelMap.chooseAnotherProductToExport + " " + uiLabelMap.or + " " + uiLabelMap.click + " \"" + uiLabelMap.CommonCancel + "\" " + uiLabelMap.orderTo + " " + uiLabelMap.backTo + " " + uiLabelMap.detailScreen + ".", 
						[{"label": uiLabelMap.CommonCancel, 
							"icon": 'fa fa-remove', "class": 'btn  btn-danger form-action-button pull-right',
						    "callback": function() {bootbox.hideAll();}
						}, 
						{"label": uiLabelMap.OK,
						    "icon": 'fa-check', "class": 'btn btn-primary form-action-button pull-right',
						    "callback": function() {
						    	window.location.replace("prepareExportReturn?returnId="+returnId);
						    }
						}]);
						return false;
					}
				} else {
					var mapAccept = {};
					mapAccept["returnId"] = returnId;
					mapAccept["statusId"] = statusId;
					bootbox.dialog(uiLabelMap.Click + " \"" + uiLabelMap.OK + "\" " + uiLabelMap.orderTo + " " + uiLabelMap.ExportExactlyWhatYouReceived.toLowerCase() + " " + uiLabelMap.or + " " + uiLabelMap.click + " \"" + uiLabelMap.ChooseInventoryItem + "\" " + uiLabelMap.orderTo + " " + uiLabelMap.ChooseInventoryYouWant.toLowerCase() + ".", 
					[{"label": uiLabelMap.CommonCancel, 
						"icon": 'fa fa-remove', "class": 'btn  btn-danger form-action-button pull-right',
					    "callback": function() {bootbox.hideAll();}
					}, 
					{"label": uiLabelMap.ChooseInventoryItem,
					    "icon": 'fa-random', "class": 'btn btn-success form-action-button pull-right',
					    "callback": function() {
					    	window.location.replace("prepareExportReturn?returnId="+returnId);
					    }
					},
					{"label": uiLabelMap.OK,
					    "icon": 'fa-check', "class": 'btn btn-primary form-action-button pull-right',
					    "callback": function() {
					    	Loading.show('loadingMacro');
					    	setTimeout(function(){		
					    		$.ajax({
					      			  url: "updateVendorReturn",
					      			  type: "POST",
					      			  data: mapAccept,
					      			  dataType: "json",
					      			  success: function(res) {
					      				  if (res._ERROR_MESSAGE_ != null && res._ERROR_MESSAGE_ != undefined){
					      					  if ((res._ERROR_MESSAGE_).indexOf("INVENTORY_EXPORTED") > -1){
						      					  $("#inventoryItemHasBeenExported").jqxNotification("open");
						      					  checkError = true;
						      				  }
						      				  if ((res._ERROR_MESSAGE_).indexOf("INVENTORY_RESERVED") > -1){
							            		   $("#inventoryItemHasBeenReservedForSalesOrder").jqxNotification("open");
							            		   checkError = true;
						      				  }
					      				  }
					      			  }    
					      		}).done(function(data) {
					      			if (checkError == false){
					      				location.reload();
					      				$("#notifyUpdateSuccess").jqxNotification("open");
					      			}
					      		});
					    	Loading.hide('loadingMacro');
					    	}, 500);
					    }
					},
					]);
				}
			} else {
				
			}
		}
	}
	
	var checkInventoryItemReserved = function checkInventoryItemReserved(returnId){
		$.ajax({
			  url: "checkInventoryItemReserved",
			  type: "POST",
			  async: false,
			  data: {
				  returnId: returnId,
			  },
			  dataType: "json",
			  success: function(res) {
				  hasReserved = res.hasReserved;
				  hasExported = res.hasExported;
			  }    
		}).done(function(data) {
		});
		return hasReserved;
	}
	
	return {
		init: init,
		getLocalization: getLocalization,
		changeReturnStatus: changeReturnStatus,
	};
}());