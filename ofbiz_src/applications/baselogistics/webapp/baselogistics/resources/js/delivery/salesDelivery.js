$(function () {
	SalesDlvObj.init();
});
var SalesDlvObj = (function () {
	var btnClick = false;
	var dlvSelected = null;
	var validateAdd = null;
	var init = function () {
		initInputs();
		initElementComplex();
		initEvents();
		initValidateForm();
		
		updateStateProvince();
		
		initValidateDatetimeDT($("#popupDeliveryDetailWindow"));
		
		initAttachFile();
		initAttachExptFile();
		
		if ($("#shipmentMethodTypeId").length > 0 && typeof needsCheckShipmentMethod != "undefined" && needsCheckShipmentMethod == true) {
			update({
				productStoreId: productStoreId,
				shipmentMethodTypeId: $("#shipmentMethodTypeId").val(),
				}, "getPartyCarrierByShipmentMethodAndStore" , "listParties", "partyId", "fullName", "partyId");
		}
		if ($("#quickShipmentMethodTypeId").length > 0 && typeof needsCheckShipmentMethod != "undefined" && needsCheckShipmentMethod == true) {
			update({
				productStoreId: productStoreId,
				shipmentMethodTypeId: $("#quickShipmentMethodTypeId").val(),
				}, "getPartyCarrierByShipmentMethodAndStore" , "listParties", "partyId", "fullName", "quickPartyId");
		}
	};
	var initInputs = function () {
		if ($("#DeliveryMenu").length > 0) {
			$("#DeliveryMenu").jqxMenu({ width: 220, autoOpenPopup: false, mode: "popup", theme: theme});
		}
		$("#popupDeliveryDetailWindow").jqxWindow({
			maxWidth: 1500, minWidth: 950, width: 1300, modalZIndex: 10000, zIndex:10000, minHeight: 500, height: 630, maxHeight: 800, maxHeight: 670, resizable: false, cancelButton: $("#alterCancel2"), keyboardNavigation: true, keyboardCloseKey: 15,  isModal: true, autoOpen: false, modalOpacity: 0.7, theme:theme           
		});
		
		$("#actualArrivalDate").jqxDateTimeInput({width: 200, disabled: true, formatString: "dd/MM/yyyy HH:mm"});
		$("#actualStartDate").jqxDateTimeInput({width: 200, disabled: true, formatString: "dd/MM/yyyy HH:mm"});
		
		$("#noteWindow").jqxWindow({
			maxWidth: 1300, minWidth: 500, width: 1200, height: 440, minHeight: 100, maxHeight: 700, resizable: false, isModal: true, modalZIndex: 100000, zIndex: 100000, autoOpen: false, cancelButton: $("#noteCancel"), modalOpacity: 0.7, theme:theme           
		});
		
		$("#editWindow").jqxWindow({
			maxWidth: 1300, minWidth: 500, width: 1200, height: 480, minHeight: 100, maxHeight: 700, resizable: false, isModal: true, modalZIndex: 100000, zIndex: 100000, autoOpen: false, cancelButton: $("#editCancel"), modalOpacity: 0.7, theme:theme           
		});
		
		$("#editAddProductWindow").jqxWindow({
			maxWidth: 1300, minWidth: 500, width: 1000, height: 460, minHeight: 100, maxHeight: 700, resizable: false, isModal: true, modalZIndex: 100000, zIndex: 100000, autoOpen: false, cancelButton: $("#editAddProductCancel"), modalOpacity: 0.7, theme:theme           
		});
		
		$("#debtWindow").jqxWindow({
			maxWidth: 1300, minWidth: 500, width: 900, height: 490, minHeight: 100, maxHeight: 700, resizable: false, isModal: true, modalZIndex: 100000, zIndex: 100000, autoOpen: false, cancelButton: $("#debtCancel"), modalOpacity: 0.7, theme:theme           
		});
		$("#facilityReturnId").jqxDropDownList({placeHolder: "${StringUtil.wrapString(uiLabelMap.PleaseSelectTitle)}", width: 200, selectedIndex: 0, source: facilityReturnData, theme: theme, displayMember: "description", valueMember: "facilityId",});
		$("#datetimeReceived").jqxDateTimeInput({width: 200, formatString: "dd/MM/yyyy HH:mm"});
		var deliveryDT;
		
		if ($("#facilityPopup").length > 0){
			$("#facilityPopup").jqxDropDownButton({width: 200, theme: theme, popupZIndex: 100001});
			$('#facilityPopup').jqxDropDownButton('setContent', '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+uiLabelMap.PleaseSelectTitle+'</div>');
		}
		
		if ($("#facility").length > 0){
			$("#facility").jqxDropDownButton({width: 350, theme: theme, popupZIndex: 100001});
			$('#facility').jqxDropDownButton('setContent', '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+uiLabelMap.PleaseSelectTitle+'</div>');
		}
	};
	var initElementComplex = function () {
		if ($("#jqxgridFacilityPopup").length > 0){
			initFacilityGrid($("#jqxgridFacilityPopup"));
		}
		if ($("#jqxgridFacility").length > 0){
			initFacilityGrid($("#jqxgridFacility"));
			
			if (facilitySelected != null){
				var description = uiLabelMap.PleaseSelectTitle; 
	        	if (facilitySelected.facilityCode != null){
	        		description = '['+ facilitySelected.facilityCode +'] ' + facilitySelected.facilityName;
	        	} else {
	        		description = '['+ facilitySelected.facilityId +'] ' + facilitySelected.facilityName;
	        	}
				update({
					facilityId: facilitySelected.facilityId,
					contactMechPurposeTypeId: "SHIPPING_LOCATION",
					}, 'getFacilityContactMechs' , 'listFacilityContactMechs', 'contactMechId', 'address1', 'defaultContactMechId');
				
		        var dropDownContent = '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+ description +' </div>';
		        $('#facility').jqxDropDownButton('setContent', dropDownContent);
			}
		}
	};
	
	var initEvents = function () {
		 
		$("#jqxgridFacility").on('rowselect', function (event) {
	        var args = event.args;
	        var rowData = args.row;
	        facilitySelected = $.extend({}, rowData);
	        var description = uiLabelMap.PleaseSelectTitle; 
	        if (facilitySelected) {
	        	if (facilitySelected.facilityCode != null){
	        		description = '['+ facilitySelected.facilityCode +'] ' + facilitySelected.facilityName;
	        	} else {
	        		description = '['+ facilitySelected.facilityId +'] ' + facilitySelected.facilityName;
	        	}
	        }
	        
	        update({
				facilityId: facilitySelected.facilityId,
				contactMechPurposeTypeId: "SHIPPING_LOCATION",
				}, 'getFacilityContactMechs' , 'listFacilityContactMechs', 'contactMechId', 'address1', 'defaultContactMechId');
			
	        var dropDownContent = '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+ description +' </div>';
	        $('#facility').jqxDropDownButton('setContent', dropDownContent);
	    });
		
		$('#jqxgridFacility').on('rowdoubleclick', function (event) { 
			$('#facility').jqxDropDownButton('close');
		});
		
		$("#jqxgridFacility").on('bindingcomplete', function (event) {
			if (facilitySelected != null){
				var rows = $('#jqxgridFacility').jqxGrid('getrows');
				if (rows.length > 0){
					for (var i in rows){
						var data1 = rows[i];
						if (data1.facilityId == facilitySelected.facilityId){
							var index = $('#jqxgridFacility').jqxGrid('getrowboundindexbyid', data1.uid);
							$('#jqxgridFacility').jqxGrid('selectrow', index);
						}
					}
				}
			}
		});
		
		$("#jqxgridFacilityPopup").on('rowselect', function (event) {
			var args = event.args;
			var rowData = args.row;
			facilitySelected = $.extend({}, rowData);
			var description = uiLabelMap.PleaseSelectTitle; 
			if (facilitySelected) {
				if (facilitySelected.facilityCode != null){
					description = '['+ facilitySelected.facilityCode +'] ' + facilitySelected.facilityName;
				} else {
					description = '['+ facilitySelected.facilityId +'] ' + facilitySelected.facilityName;
				}
			}
			
			update({
				facilityId: facilitySelected.facilityId,
				contactMechPurposeTypeId: "SHIPPING_LOCATION",
			}, 'getFacilityContactMechs' , 'listFacilityContactMechs', 'contactMechId', 'address1', 'originContactMechId');
			
			var dropDownContent = '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+ description +' </div>';
			$('#facilityPopup').jqxDropDownButton('setContent', dropDownContent);
		});
		
		$('#jqxgridFacilityPopup').on('rowdoubleclick', function (event) { 
			$('#facilityPopup').jqxDropDownButton('close');
		});
		
		$("#jqxgridFacilityPopup").on('bindingcomplete', function (event) {
			if (facilitySelected != null){
				var rows = $('#jqxgridFacilityPopup').jqxGrid('getrows');
				if (rows.length > 0){
					for (var i in rows){
						var data1 = rows[i];
						if (data1.facilityId == facilitySelected.facilityId){
							var index = $('#jqxgridFacilityPopup').jqxGrid('getrowboundindexbyid', data1.uid);
							$('#jqxgridFacilityPopup').jqxGrid('selectrow', index);
						}
					}
				}
			}
		});
		
		$("#jqxgridDelivery").on("rowclick", function (event) {
		    var args = event.args;
		    dlvSelected = args.row.bounddata;
		});
		
		if ($("#DeliveryMenu").length > 0) {
			$("#DeliveryMenu").on("itemclick", function (event) {
				if (dlvSelected != null){
					var data = dlvSelected;
					var tmpStr = $.trim($(args).text());
					if (tmpStr == uiLabelMap.BSViewDetail) {
						showDetailDelivery(data.deliveryId);
					} else if (tmpStr == uiLabelMap.BLQuickView) {
						showDetailPopup(data.deliveryId, data.orderId);
					} else if (tmpStr == uiLabelMap.BSRefresh) {
						$("#jqxgridDelivery").jqxGrid("updatebounddata");
					} else if (tmpStr == uiLabelMap.DeliveryDocPooledTax) {
						if (data.statusId == 'DLV_CANCELLED'){
							jOlbUtil.alert.error(uiLabelMap.DeliveryNote + " " + uiLabelMap.Canceled.toLowerCase());
							return false;
						}
						window.open("printDeliveryDocPooledTax.pdf?deliveryId="+data.deliveryId, "_blank");
					} else if (tmpStr == uiLabelMap.DeliveryNote) {
						if (data.statusId == 'DLV_CANCELLED'){
							jOlbUtil.alert.error(uiLabelMap.DeliveryNote + " " + uiLabelMap.Canceled.toLowerCase());
							return false;
						}
						window.open("deliveryUnitPrice.pdf?deliveryId="+data.deliveryId, "_blank");
					} else if (tmpStr == uiLabelMap.DeliveryDoc) {
						if (data.statusId == 'DLV_CANCELLED'){
							jOlbUtil.alert.error(uiLabelMap.DeliveryNote + " " + uiLabelMap.Canceled.toLowerCase());
							return false;
						}
						window.open("deliveryAndExport.pdf?deliveryId="+data.deliveryId, "_blank");
					} else if (tmpStr == uiLabelMap.BLSGCTicket) {
						if (data.statusId == 'DLV_CANCELLED'){
							jOlbUtil.alert.error(uiLabelMap.DeliveryNote + " " + uiLabelMap.Canceled.toLowerCase());
							return false;
						}
						if (data.deliveryId) {
							location.href = "ExportTicketFile?deliveryId=" + data.deliveryId;
						}
					}
				}
			});
		}
		$("#cancelDlv").on("click",function () {
			bootbox.dialog(uiLabelMap.AreYouSureCancel, 
				[{"label": uiLabelMap.CommonCancel, 
					"icon": "fa fa-remove", "class": "btn  btn-danger form-action-button pull-right",
					"callback": function () {bootbox.hideAll();}
				}, 
				{"label": uiLabelMap.OK,
					"icon": "fa-check", "class": "btn btn-primary form-action-button pull-right",
					"callback": function () {
						$.ajax({
							url: "changeDeliveryStatus",
							type: "POST",
							data: {
								deliveryId: glDeliveryId,
								statusId: "DLV_CANCELLED",
							},
							async: false,
							success: function (res) {
								if (!res._ERROR_MESSAGE_) {
									Grid.renderMessage("jqxNotification", uiLabelMap.UpdateSuccessfully, {
										autoClose : true,
										template : "info",
										appendContainer : "#container",
										opacity : 0.9
									});
								} else {
									Grid.renderMessage("jqxNotification", uiLabelMap.UpdateError, {
										autoClose : true,
										template : "error",
										appendContainer : "#container",
										opacity : 0.9
									});
								}
								showDetailPopup(res.deliveryId, glOrderId);
								$("#addrowbuttonjqxgridDelivery").show();
								$("#customcontroljqxgridDelivery1").show();
							}
						});
					}
				}]);
		});
		
		$("#editDlv").on("click",function () {
			$.ajax({
				url: "loadDeliveryItemToEdit",
				type: "POST",
				data: {
					deliveryId: glDeliveryId,
				},
				async: false,
				success: function (res) {
					var listOrderItemTmps = res.listDeliveryItems;
					
					for (var x in listOrderItemTmps) {
						if (listOrderItemTmps[x].convertNumber) {
							listOrderItemTmps[x].quantity = listOrderItemTmps[x].quantity/listOrderItemTmps[x].convertNumber;
							listOrderItemTmps[x].newQuantity = listOrderItemTmps[x].createdQuantity/listOrderItemTmps[x].convertNumber;
							listOrderItemTmps[x].createdQuantity = listOrderItemTmps[x].createdQuantity/listOrderItemTmps[x].convertNumber;
						}
					}
					
					loadEditGrid(listOrderItemTmps);
					$("#editWindow").jqxWindow('open');
				}
			});
		});
		
		$("#debtRecord").on("click",function () {
			$("#debtWindow").jqxWindow("open");
		});
		
		$("#debtSave").click(function () {
			bootbox.dialog(uiLabelMap.AreYouSureSave, 
			[{"label": uiLabelMap.CommonCancel, 
				"icon": "fa fa-remove", "class": "btn  btn-danger form-action-button pull-right",
				"callback": function () {bootbox.hideAll();}
			}, 
			{"label": uiLabelMap.CommonSave,
				"icon": "fa-check", "class": "btn btn-primary form-action-button pull-right",
				"callback": function () {
					$("#debtWindow").jqxWindow("close");
				}
			}]);
		});
		
		$("#suggestCancel").on("click", function (event) {
			$("#facilityWindow").jqxWindow("close");	
		});
		$("#countryGeoId").on("change", function (event) {
			updateStateProvince();
		});
		
		if ($("#addPostalAddressWindow").length > 0) {
			$("#addPostalAddressWindow").on("close", function (event) {
				$("#address1").val("");
				$("#address2").val("");
				$("#postalCode").val("");
			});
		}
		if ($("#estimatedStartDate").length > 0) {
			$("#estimatedStartDate").on("change", function (event) {
				if ($("#alterpopupWindow").length > 0 && !$("#alterpopupWindow").jqxValidator("validate")) {
					return false;
				}
			});
		}
		$("#estimatedArrivalDate").on("change", function (event) {
			if ($("#alterpopupWindow").length > 0 && !$("#alterpopupWindow").jqxValidator("validate")) {
				return false;
			}
		});
		
		$("#actualArrivalDate").on("change", function (event) {
			if (!$("#popupDeliveryDetailWindow").jqxValidator("validate")) {
				return false;
			}
		});
		
		$("#actualStartDate").on("change", function (event) {
			if (!$("#popupDeliveryDetailWindow").jqxValidator("validate")) {
				return false;
			}
		});

		$("#newAddrOkButton").click(function (event) {
			if (!$("#countryGeoId").val()) {
				bootbox.dialog(uiLabelMap.PleaseChooseCountryBefore, [{
					"label" : uiLabelMap.OK,
					"class" : "btn btn-primary standard-bootbox-bt",
					"icon" : "fa fa-check",
				}]
				);
			} else if (!$("#countryGeoId").val()) {
				bootbox.dialog(uiLabelMap.PleaseChooseProvinceBefore, [{
					"label" : uiLabelMap.OK,
					"class" : "btn btn-primary standard-bootbox-bt",
					"icon" : "fa fa-check",
				}]
				);
			} else {
				var validate = $("#addPostalAddressWindow").jqxValidator("validate");
				if (validate) {
					var facilityIdTemp = null;
					if ("SHIP_ORIG_LOCATION" == contactMechPurposeTypeId) {
						facilityIdTemp = facilitySelected.facilityId;
					} else {
					}
					if (facilityIdTemp) {
						bootbox.dialog(uiLabelMap.AreYouSureSave, 
						[{"label": uiLabelMap.CommonCancel, 
							"icon": "fa fa-remove", "class": "btn  btn-danger form-action-button pull-right",
							"callback": function () {bootbox.hideAll();}
						},
						{"label": uiLabelMap.CommonSave,
							"icon": "fa-check", "class": "btn btn-primary form-action-button pull-right",
							"callback": function () {
								Loading.show("loadingMacro");
								setTimeout(function () {
									jQuery.ajax({
										url: "createFacilityContactMechPostalAddress",
										type: "POST",
										async: false,
										data: {
											facilityId: facilityIdTemp,
											contactMechTypeId: "POSTAL_ADDRESS", 
											contactMechPurposeTypeId : contactMechPurposeTypeId, 
											address1: $("#address1").val(), 
											address2: $("#address2").val(),
											countryGeoId: $("#countryGeoId").val(),
											stateProvinceGeoId: $("#stateProvinceGeoId").val(),
											postalCode: $("#postalCode").val(),
										},
										success: function (res) {
											$("#addPostalAddressWindow").jqxWindow("close");
											if ("SHIP_ORIG_LOCATION" == contactMechPurposeTypeId) {
												update({
													facilityId: facilitySelected.facilityId,
													contactMechPurposeTypeId: "SHIP_ORIG_LOCATION",
													}, "getFacilityContactMechs" , "listFacilityContactMechs", "contactMechId", "address1", "originContactMechId");
											} else {
											}
										}
									});
									Loading.hide("loadingMacro");
								}, 500);
							}
						}]);
					} else {
						bootbox.dialog(uiLabelMap.PleaseChooseFacilityBefore, [{
							"label" : uiLabelMap.OK,
							"class" : "btn btn-primary standard-bootbox-bt",
							"icon" : "fa fa-check",
						}]
						);
					}
				}
			}
		});
		
		$("#selectFacilityWindow").on("open", function (event) {
			quickClick = 0;
			$("#quickSave").focus();
			update({
				facilityId: facilitySelected.facilityId,
				contactMechPurposeTypeId: "SHIPPING_LOCATION",
				}, "getFacilityContactMechs" , "listFacilityContactMechs", "contactMechId", "address1", "defaultContactMechId");
		});
		
		$("#facilityReturnId").on("change", function (event) {
			if ($("#noteGrid").length > 0) {
				var listProductFacilitys = [];
				var listProductMaps = [];
				var listNoteRows = $("#noteGrid").jqxGrid("getrows");
				if (listNoteRows != undefined && listNoteRows.length > 0) {
					for (var i in listNoteRows) {
						var h = {
							productId: listNoteRows[i].productId,
						}
						listProductMaps.push(h);
					}
					var listProductMaps = [];
					var listFacilityMaps = [];
					var k = {
						facilityId: $("#facilityReturnId").jqxDropDownList("val"),
					}
					listFacilityMaps.push(k);
					var listProductFacilitys = [];
					$.ajax({
						type: "POST",
						url: "getProductFacilitys",
						data: {
							listProductIds: JSON.stringify(listProductMaps),
							listFacilityIds: JSON.stringify(listFacilityMaps),
						},
						dataType: "json",
						async: false,
						success: function (data) {
							listProductFacilitys = data.listProductFacilitys
						}
					});
					if (listProductFacilitys.length > 0) {
						for (var c in listNoteRows) {
							for (var v in listProductFacilitys) {
								if (listNoteRows[c].productId == listProductFacilitys[v].productId) {
									listNoteRows[c]["expRequired"] = listProductFacilitys[v].expRequired;
									listNoteRows[c]["mnfRequired"] = listProductFacilitys[v].mnfRequired;
									listNoteRows[c]["topRequired"] = listProductFacilitys[v].topRequired;
								}
							}
						}
					} else {
						for (var c in listNoteRows) {
							listNoteRows[c]["expRequired"] = null;
							listNoteRows[c]["mnfRequired"] = null;
							listNoteRows[c]["topRequired"] = null;
						}
					}
					var tmpS = $("#noteGrid").jqxGrid("source");
					tmpS._source.localdata = listNoteRows;
					$("#noteGrid").jqxGrid("source", tmpS);
					$("#noteGrid").jqxGrid("updatebounddata");
				}
			}
		});
		
		$("#shipmentMethodTypeId").on("change", function (event) {
			update({
				productStoreId: productStoreId,
				shipmentMethodTypeId: $("#shipmentMethodTypeId").val(),
				}, "getPartyCarrierByShipmentMethodAndStore" , "listParties", "partyId", "fullName", "partyId");
		});
		
		$("#quickShipmentMethodTypeId").on("change", function (event) {
			update({
				productStoreId: productStoreId,
				shipmentMethodTypeId: $("#quickShipmentMethodTypeId").val(),
				}, "getPartyCarrierByShipmentMethodAndStore" , "listParties", "partyId", "fullName", "quickPartyId");
		});
		
		$("#quickSave").click(function () {
			bootbox.dialog(uiLabelMap.AreYouSureCreate, 
			[{"label": uiLabelMap.CommonCancel, 
				"icon": "fa fa-remove", "class": "btn  btn-danger form-action-button pull-right",
				"callback": function () {bootbox.hideAll();}
			}, 
			{"label": uiLabelMap.OK,
				"icon": "fa-check", "class": "btn btn-primary form-action-button pull-right",
				"callback": function () {
					Loading.show("loadingMacro");
					setTimeout(function () {
						var facId = facilitySelected.facilityId;
						var cmtId = $("#defaultContactMechId").val();
						var orderIdTmp = $("#defaultOrderId").val();
						var shipmentMethodTypeId = $("#quickShipmentMethodTypeId").val();
						var carrierPartyId = $("#quickPartyId").val();

						if (quickClick == 0) {
							quickCreateDelivery(orderIdTmp, facId, cmtId, shipmentMethodTypeId, carrierPartyId);
							quickClick = quickClick + 1;
						}
					Loading.hide("loadingMacro");
					}, 500);
				}
			}]);
		});
		
		$("#sendRequestApprove").click(function () {
			bootbox.dialog(uiLabelMap.AreYouSureSend, 
			[{"label": uiLabelMap.CommonCancel, 
				"icon": "fa fa-remove", "class": "btn  btn-danger form-action-button pull-right",
				"callback": function () {bootbox.hideAll();}
			}, 
			{"label": uiLabelMap.OK,
				"icon": "fa-check", "class": "btn btn-primary form-action-button pull-right",
				"callback": function () {
					Loading.show("loadingMacro");
					setTimeout(function () {
						$.ajax({
							url: "updateDeliveryStatus",
							type: "POST",
							async: false,
							data: {
								deliveryId: glDeliveryId,
								newStatusId: "DLV_PROPOSED",
								setItemStatus: "Y",
								newItemStatus: "DELI_ITEM_PROPOSED",
							},
							success: function (res) {
								showDetailPopup(glDeliveryId, glOrderId);
							}
						});
						Loading.hide("loadingMacro");
					}, 500);
				}
			}]);
		});
		
		$("#uploadOkButton").click(function () {
			saveFileUpload();
		});
		$("#uploadExptOkButton").click(function () {
			saveFileExptUpload();
		});
		$("#uploadCancelButton").click(function () {
			$("#jqxFileScanUpload").jqxWindow("close");
		});
		$("#uploadExptCancelButton").click(function () {
			$("#jqxFileScanExptUpload").jqxWindow("close");
		});
		$("#jqxFileScanUpload").on("close", function (event) {
			$(".remove").trigger("click");
			initAttachFile();
		});
		$("#jqxFileScanExptUpload").on("close", function (event) {
			$(".remove").trigger("click");
			initAttachExptFile();
		});
		
		$("#alterSave2").click(function () {
			checkContinue = false;
			btnClick = false;
			saveDelivery();
		});
		
		$("#editSave").click(function () {
			bootbox.dialog(uiLabelMap.AreYouSureSave, 
				[{"label": uiLabelMap.CommonCancel, 
					"icon": "fa fa-remove", "class": "btn  btn-danger form-action-button pull-right",
					"callback": function () {bootbox.hideAll();}
				}, 
				{"label": uiLabelMap.OK,
					"icon": "fa-check", "class": "btn btn-primary form-action-button pull-right",
					"callback": function () {
						Loading.show("loadingMacro");
						setTimeout(function () {
							saveEditDelivery();
							Loading.hide("loadingMacro");	
						}, 500);
					}
				}]);
			listProductToAdd = [];
		});
		
		$("#editGrid").on("cellendedit", function(event) {
			var args = event.args;
			var dataField = event.args.datafield;
			var rowBoundIndex = event.args.rowindex;
			var value = args.value;
			var oldValue = args.oldvalue;
			var rowData = args.row;
			var x = value - oldValue;
			for (var i in listProductToAdd) {
				if (listProductToAdd[i].fromOrderItemSeqId == rowData.fromOrderItemSeqId){
					listProductToAdd[i].quantity = listProductToAdd[i].quantity - x;
					listProductToAdd[i].newQuantity = listProductToAdd[i].newQuantity - x;
				}
			}
		});
		
		$("#editAddProductSave").click(function () {
			var selectedIndexs = $("#editAddProductGrid").jqxGrid("getselectedrowindexes");
			if (selectedIndexs.length <= 0) {
				jOlbUtil.alert.error(uiLabelMap.YouNotYetChooseProduct);
				return false;
			}
			var allRowNews = [];
			for (var t in selectedIndexs) {
				allRowNews.push($("#editAddProductGrid").jqxGrid('getrowdata', selectedIndexs[t]));
			}
			var allRows = $("#editGrid").jqxGrid("getrows");
			var newDataList = [];
			listProductToAdd = [];
			for (var x in allRowNews){
				var data1 = allRowNews[x];
				var obj = $.extend({}, data1);
				obj.newQuantity = obj.quantity - data1.newQuantity;
				obj.quantity = data1.quantity - data1.newQuantity;
				var quantity1 = data1.newQuantity;
				var orderItemSeqId1 = data1.fromOrderItemSeqId;
				var check = true;
				for (var y in allRows){
					var data2 = allRows[y];
					var orderItemSeqId2 = data2.fromOrderItemSeqId;
					if (orderItemSeqId1 == orderItemSeqId2){
						quantity1 = quantity1 + data2.newQuantity;
						data2.newQuantity = quantity1;
						check = false;
						newDataList.push(data2);
					}
				}
				if (check){
					newDataList.push(data1);
				} 
				listProductToAdd.push(obj);
			}
			var listData = [];
			for (var x in allRows){
				var obj = allRows[x];
				for (var y in newDataList){
					if (allRows[x].fromOrderItemSeqId == newDataList[y].fromOrderItemSeqId){
						obj = allRows[x];
						break;
					}
				}
				listData.push(obj);
			}
			for (var x in newDataList){
				var obj = newDataList[x];
				var check = false;
				for (var y in allRows){
					if (allRows[y].fromOrderItemSeqId == newDataList[x].fromOrderItemSeqId){
						check = true;
						break;
					}
				}
				if (check == false){
					listData.push(obj);
				}
			}
			loadEditGrid(listData);
			$("#editAddProductWindow").jqxWindow('close');
		});
		
		$("#alterSaveAndContinue").click(function () {
			checkContinue = true;
			saveDelivery();
		});
		
		$("#alterCancel").click(function () {
			$("#alterpopupWindow").jqxWindow("close");
		});
		$("#alterCancel2").click(function () {
			$("#popupDeliveryDetailWindow").jqxWindow("close");
		});
		
		$("#alterSave").click(function () {
			validatorAdd
			var resultValidate = !validatorAdd.validate();
			if (resultValidate) {
				return false;
			}
			var row;
			// Get List Order Item
			$("#jqxgridOrderItem").jqxGrid("clearfilters");
			var selectedIndexs = $("#jqxgridOrderItem").jqxGrid("getselectedrowindexes");
			if (selectedIndexs.length == 0) {
				jOlbUtil.alert.error(uiLabelMap.YouNotYetChooseProduct);
				return false;
			} 
			var ctm = $("#destContactMechId").val();
			if (ctm == null || ctm == undefined || ctm == "" || ctm == "null") {
				jOlbUtil.alert.error(uiLabelMap.DestAddress + " " + uiLabelMap.BLNotFound);
				return false;
			} 
			if (!$("#alterpopupWindow").jqxValidator("validate")) {
				return false;
			}
			bootbox.dialog(uiLabelMap.AreYouSureCreate, 
			[{"label": uiLabelMap.CommonCancel, 
				"icon": "fa fa-remove", "class": "btn  btn-danger form-action-button pull-right",
				"callback": function () {bootbox.hideAll();}
			}, 
			{"label": uiLabelMap.OK,
				"icon": "fa-check", "class": "btn btn-primary form-action-button pull-right",
				"callback": function () {
					Loading.show("loadingMacro");
					setTimeout(function () {
//						if ($("#deliveryId").val()) {
//							$.ajax({
//								url: "checkDeliveryExisted",
//								type: "POST",
//								async: false,
//								data: {
//									deliveryId: $("#deliveryId").val(),
//								},
//								success: function (res) {
//									if (res._ERROR_MESSAGE_ == "DELIVERY_ID_EXISTED") {
//										checkDeliveryExists = true;
//									} else {
//										checkDeliveryExists = false;
//									}
//								}
//							});
//						}
						var listOrderItems = [];
						var orderIdTmp;
						for (var i in selectedIndexs) {
							var data = $("#jqxgridOrderItem").jqxGrid("getrowdata", selectedIndexs[i]);
							var map = {};
							map["orderItemSeqId"] = data.orderItemSeqId;
							map["orderId"] = data.orderId;
							orderIdTmp = data.orderId;

							var quantityUomId = data.quantityUomId;
							var packingUomIdArray = JSON.parse(data.quantityUomIds);
							var convert = 1;
							for (var j in  packingUomIdArray) {
								var obj = packingUomIdArray[j];
								var quantityUomIdTmp = obj.quantityUomId;
								if (quantityUomId == quantityUomIdTmp) convert = obj.convertNumber;
							}

							map["quantity"] = (data.requiredQuantityTmp)*convert;
							if (data.requireAmount && data.requireAmount == 'Y'){
								map["quantity"] = 1;
								map["amount"] = data.requiredQuantityTmp * data.selectedAmount;
							}
							var exp = data.expireDate;
							if (exp) {
								map["expireDate"] = exp.getTime();
							}
							listOrderItems[i] = map;
						}
						var listOrderItems = JSON.stringify(listOrderItems);
						row = { 
								orderId: orderIdTmp,
								currencyUomId:$("#alterpopupWindow input[name=currencyUomId]").val(),
								statusId:$("#alterpopupWindow input[name=statusId]").val(),
								originContactMechId:$("#originContactMechId").val(),
								originProductStoreId:$("#originProductStoreId").val(),
								partyIdTo:$("#partyIdTo").val(),
								partyIdFrom:$("#partyIdFrom").val(), 
								destContactMechId:$("#destContactMechId").val(),
								originFacilityId: facilitySelected.facilityId,
								deliveryDate:$("#deliveryDate").jqxDateTimeInput("getDate"),
								estimatedStartDate:$("#estimatedStartDate").jqxDateTimeInput("getDate"),
								estimatedArrivalDate:$("#estimatedArrivalDate").jqxDateTimeInput("getDate"),
								deliveryId:$("#deliveryId").val(),
//								no:$("#noNumber").val(),
								defaultWeightUomId : "WT_kg",
								shipmentMethodTypeId:$("#shipmentMethodTypeId").val(),
								carrierPartyId:$("#partyId").val(), 
								listOrderItems:listOrderItems
						};
						$("#jqxgridDelivery").jqxGrid("addRow", null, row, "first");
						$("#jqxgridDelivery").jqxGrid("updatebounddata"); 
						Loading.hide("loadingMacro");
					}, 500);
				}
			}]);
			$("#jqxgridDelivery").on("bindingcomplete", function (event) {
				$("#alterpopupWindow").jqxWindow("close");
			});
		});

		$("#editAddProductWindow").on("close", function (event) {
			$("#editAddProductGrid").jqxGrid("clear");
		});
		
		$("#editWindow").on("close", function (event) {
			listProductToAdd = [];
		});
		
		$("#popupDeliveryDetailWindow").on("close", function (event) {
			$("#jqxgridDlvItem").jqxGrid("refreshdata");
			$("#jqxgridDelivery").jqxGrid("updatebounddata");

			$("#jqxgridDelivery").jqxGrid("clearselection");

			$("#jqxgridDlvItem").jqxGrid("clearselection");
			$("#actualArrivalDateDis").hide();
			$("#actualStartDateDis").hide();
			$("#orderNote").hide();
			pathScanFile = null;
			pathScanExptFile = null;
			$("#popupDeliveryDetailWindow").jqxValidator("hide");
			if (typeof inOrderDetail != "undefined" && inOrderDetail == true) {
				var newStatusId;
				$.ajax({
					type: "POST",
					url: "checkOrderStatus",
					data: {
						orderId: glOrderId,
					},
					async: false,
					success: function (res) {
						newStatusId = res.statusId;
					}
				});
				if (newStatusId != orderStatus || hasReturn == true) {
					window.location.replace("viewOrder?orderId="+orderId+"&activeTab=deliveries-tab");
				}
				if ("ORDER_COMPLETED" == newStatusId && $("#returnOrderId").length > 0) {
					var check = false;
					$.ajax({
						type: "POST",
						url: "checkAllDeliveryInSpecificStatus",
						data: {
							orderId: glOrderId,
							statusId: "DLV_DELIVERED",
						},
						async: false,
						success: function (res) {
							check = res.check;
							if (check) {
								$("#returnOrderId").hide();
							} else {
								$("#returnOrderId").show();
							}
						}
					});
				}

				if (typeof orderId != "undefined") {
					checkAllOrderItemCreatedDone(orderId);
				}
			}
			listNoteItems = [];
			listProductToAdd = [];
		});
		$("#popupDeliveryDetailWindow").on("open", function (event) {
			btnClick = false;
		});
		
		$("#alterApproveAndContinue").on("click", function (event) {
			checkContinue = true;
			approveDelivey()
		});
		$("#alterApprove").on("click", function (event) {
			checkContinue = false;
			approveDelivey();
		});

		if ($("#alterpopupWindow").length > 0) {
			$("#alterpopupWindow").on("open", function (event) {
				var listOrderItems = [];
				if (facilitySelected != null){
					$.ajax({
						type: "POST",
						url: "getListOrderItemDelivery",
						data: {
							orderId: orderId,
							facilityId: facilitySelected.facilityId,
						},
						dataType: "json",
						async: false,
						success: function (response) {
							listOrderItems = response["listOrderItems"];
						},
						error: function (response) {
							alert("Error:" + response);
						}
					});
				}
				
				if (listOrderItems && listOrderItems.length > 0) {
					for (var i in listOrderItems) {
						var data = listOrderItems[i];
						var reqAmount = false;
						if (data.requireAmount && data.requireAmount == 'Y'){
							reqAmount = true;
						}
						var quantityUomId = data.quantityUomId;
						if (reqAmount == true){
							quantityUomId = data.weightUomId;
						}
						if (reqAmount == true){
							listOrderItems[i].requiredQuantityTmp = (listOrderItems[i].requiredQuantityTmp);
							listOrderItems[i].requiredQuantity = (listOrderItems[i].requiredQuantity);
							listOrderItems[i].createdQuantity = (listOrderItems[i].createdQuantity);
							listOrderItems[i].quantityConvert = 1;
						} else {
							var convert = 1;
							var packingUomIdArray = JSON.parse(data.quantityUomIds);
							for (var j in packingUomIdArray) {
								var obj = packingUomIdArray[j];
								var quantityUomIdTmp = obj.quantityUomId;
								if (quantityUomId == quantityUomIdTmp) {
									convert = obj.convertNumber;
								}
							}
							listOrderItems[i].requiredQuantityTmp = (listOrderItems[i].requiredQuantityTmp)/convert;
							listOrderItems[i].requiredQuantity = (listOrderItems[i].requiredQuantity/convert);
							listOrderItems[i].createdQuantity = (listOrderItems[i].createdQuantity)/convert;
							listOrderItems[i].quantityConvert = convert;
						}
					}
				}
				loadOrderItem(listOrderItems);
				$("#deliveryId").jqxInput("clear");
				
				if (facilitySelected) {
					var description = null;
		        	if (facilitySelected.facilityCode != null){
		        		description = '['+ facilitySelected.facilityCode +'] ' + facilitySelected.facilityName;
		        	} else {
		        		description = '['+ facilitySelected.facilityId +'] ' + facilitySelected.facilityName;
		        	}
		        	update({
						facilityId: facilitySelected.facilityId,
						contactMechPurposeTypeId: "SHIPPING_LOCATION",
						}, 'getFacilityContactMechs' , 'listFacilityContactMechs', 'contactMechId', 'address1', 'originContactMechId');
		        	
		        	var dropDownContent = '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+ description +' </div>';
			        $('#facilityPopup').jqxDropDownButton('setContent', dropDownContent);
		        }
				if (!$("#alterpopupWindow").jqxValidator("validate")) {
					return false;
				}
			});

			$("#alterpopupWindow").on("close", function (event) {
				var selectedIndexs = $("#jqxgridOrderItem").jqxGrid("getselectedrowindexes");
				if (selectedIndexs) {
					if (selectedIndexs.length != 0) {
						$("#jqxgridOrderItem").jqxGrid("clearselection");
					}
				}
				if (typeof orderId != "undefined") {
					checkAllOrderItemCreatedDone(orderId);
				}
			});
		}
		
//		$("#partyIdTo").on("change", function (event) {
//			$("#partyIdTo").jqxValidator("validate");
//		});
//		$("#partyIdFrom").on("change", function (event) {
//			$("#partyIdFrom").jqxValidator("validate");
//		});
		$("#destContactMechId").on("change", function (event) {
			$("#destContactMechId").jqxValidator("validate");
		});
		$("#originContactMechId").on("change", function (event) {
			$("#originContactMechId").jqxValidator("validate");
		});
		
		$("#jqxgridOrderItem").on("rowselect", function (event) {
			if (facilitySelected == null) {
				bootbox.dialog(uiLabelMap.PleaseChooseFacilityBefore, [{
					"label" : uiLabelMap.OK,
					"class" : "btn btn-primary standard-bootbox-bt",
					"icon" : "fa fa-check",
				}]
				);
				$("#jqxgridOrderItem").jqxGrid("clearselection");
			} else {
				if (typeof event.args.rowindex != "number") {
					var tmpArray = event.args.rowindex;
					for (var i in tmpArray) {
						if (checkRequiredData(tmpArray[i])) {
							$("#jqxgridOrderItem").jqxGrid("clearselection");
							break; // Stop for first item
						}
					}
				} else {
					checkRequiredData(event.args.rowindex);
				}
			}
		});
		
		$("#noteSave").click(function () {
			var allRows = $("#noteGrid").jqxGrid("getrows");
			for (var id in allRows) {
				var data = allRows[id];
				var rowindex = $("#noteGrid").jqxGrid("getrowboundindexbyid", data.uid);
				if (!data.manufacturedDate && data.mnfRequired == "Y") {
					bootbox.dialog(uiLabelMap.MissingManufactureDate + " " + uiLabelMap.For.toLowerCase() + " " + uiLabelMap.Product.toLowerCase() + ": " + data.productCode, [{
						"label" : uiLabelMap.OK,
						"class" : "btn btn-primary standard-bootbox-bt",
						"icon" : "fa fa-check",
						"callback": function () {
							$("#noteGrid").jqxGrid("begincelledit", rowindex, "manufacturedDate");
						}
					}]
					);
					return false;
				}
				if (!data.expiredDate && data.expRequired == "Y") {
					bootbox.dialog(uiLabelMap.TheExpiredDateFieldNotYetBeEntered + " " + uiLabelMap.For.toLowerCase() + " " + uiLabelMap.Product.toLowerCase() + ": " + data.productCode, [{
						"label" : uiLabelMap.OK,
						"class" : "btn btn-primary standard-bootbox-bt",
						"icon" : "fa fa-check",
						"callback": function () {
							$("#noteGrid").jqxGrid("begincelledit", rowindex, "expiredDate");
						}
					}]
					);
					return false;
				}
			}

			bootbox.dialog(uiLabelMap.AreYouSureSave, 
					[{"label": uiLabelMap.CommonCancel, 
						"icon": "fa fa-remove", "class": "btn  btn-danger form-action-button pull-right",
						"callback": function () {bootbox.hideAll();}
					}, 
					{"label": uiLabelMap.CommonSave,
						"icon": "fa-check", "class": "btn btn-primary form-action-button pull-right",
						"callback": function () {
							listNoteItems = [];
							var row;
							var allRows = $("#noteGrid").jqxGrid("getrows");
							for (var i in allRows) {
								listNoteItems.push(allRows[i]);
							}
							$("#noteWindow").jqxWindow("close");
//							$("#updateOrderNoteComplete").jqxNotification("open");
						}
					}]);
		});
		
		$("#noteWindow").on("close", function (event) {
			if (listNoteItems.length > 0) {
				$("#orderNote").show();
				$("#orderNote").html("");
				$("#orderNote").append("<a id=\"noteId\" href=\"javascript:SalesDlvObj.showOrderNotePopup()\"><i class=\"fa-edit\"></i>"+ uiLabelMap.Return +"</a>");
			}
		});
	};
	
	function saveDelivery() {
		if ("DLV_DELIVERED" == glDeliveryStatusId) {
			// check missing scan file
			if (!pathScanFile) {
				jOlbUtil.alert.error(uiLabelMap.MustUploadScanFile);
				return false;
			}
			bootbox.dialog(uiLabelMap.AreYouSureSave, 
					[{"label": uiLabelMap.CommonCancel, 
						"icon": "fa fa-remove", "class": "btn  btn-danger form-action-button pull-right",
						"callback": function () {bootbox.hideAll();}
					}, 
					{"label": uiLabelMap.OK,
						"icon": "fa-check", "class": "btn btn-primary form-action-button pull-right",
						"callback": function () {
							$.ajax({
								type: "POST",
								url: "updateDeliveryScanfile",
								data: {
									pathScanFile: pathScanFile,
									deliveryId: glDeliveryId,
								},
								dataType: "json",
								async: false,
								success: function (data) {
									showDetailPopup(data.deliveryId, glOrderId);
								},
								error: function (response) {
								}
							});
						}
					}]);
		} else {
			if (perAdmin == false) {
				if ("DLV_EXPORTED" == glDeliveryStatusId) {
					if (!pathScanFile) {
						jOlbUtil.alert.error(uiLabelMap.MustUploadScanFileDelivery);
						return false;
					}
				} else if ("DLV_APPROVED" == glDeliveryStatusId) {
					if (!pathScanFileExpt) {
						jOlbUtil.alert.error(uiLabelMap.MustUploadScanFileExpt);
						return false;
					}
				}
			}
		
			if (!$("#popupDeliveryDetailWindow").jqxValidator("validate")) {
				return false;
			}
			var row;
			// Get List Delivery Item
			var selectedItems = [];
			var listItemSeqs = [];
			$("#jqxgridDlvItem").jqxGrid("clearfilters");
			var allRows = $("#jqxgridDlvItem").jqxGrid("getrows");
			for (var id in allRows) {
				if (checkGridDeliveryItemRequiredData(allRows[id].uid, allRows) == true) {
					return false;
				}
				if (allRows[id].statusId == "DELI_ITEM_APPROVED") {
					selectedItems.push(allRows[id]);
				} else {
					if (allRows[id].actualDeliveredQuantity >= 0) {
						selectedItems.push(allRows[id]);
					} 
				}
			}
			if ("DLV_EXPORTED" == glDeliveryStatusId) {
				var listProductMaps = [];
				var listFacilityMaps = [];
				var h = {
						facilityId: deliveryDT.originFacilityId,
				}
				listFacilityMaps.push(h);

				var listMissing = [];
				for (var j in selectedItems) {
					var data = selectedItems[j];
					if (data.actualExportedQuantity != data.actualDeliveredQuantity) {
						listMissing.push(data);
						var k = {
     						productId: data.productId,
						}
						listProductMaps.push(k);
					}
				}
				var show = false;
				if (listNoteItems.length <= 0) {
					show = true;
				} else {
					for (var i in listMissing) {
						for (var j in listNoteItems) {
							if (listMissing[i].deliveryItemSeqId == listNoteItems[j].deliveryItemSeqId) {
								if (listMissing[i].actualDeliveredQuantity != listNoteItems[j].actualDeliveredQuantity) {
									show = true;
									break;
								}
							}
						}
					}
				}
				
				/*// check required expired, manufactured date, ..
				var listProductFacilitys = [];
				$.ajax({
					type: "POST",
					url: "getProductFacilitys",
					data: {
						listProductIds: JSON.stringify(listProductMaps),
						listFacilityIds: JSON.stringify(listFacilityMaps),
					},
					dataType: "json",
					async: false,
					success: function (data) {
						listProductFacilitys = data.listProductFacilitys
					}
				});

				for (var c in listMissing) {
					for (var v in listProductFacilitys) {
						if (listMissing[c].productId == listProductFacilitys[v].productId) {
							listMissing[c]["expRequired"] = listProductFacilitys[v].expRequired;
							listMissing[c]["mnfRequired"] = listProductFacilitys[v].mnfRequired;
							listMissing[c]["topRequired"] = listProductFacilitys[v].topRequired;
						}
					}
				}
				if (listMissing.length > 0 && show == true) {
					bootbox.dialog(uiLabelMap.SomeProductHasMissingQuantity + ". " + uiLabelMap.Click + " \"" + uiLabelMap.OK + "\" " + uiLabelMap.orderTo + " " + uiLabelMap.updateReasonNoteForOrder + " " + uiLabelMap.or + " " + uiLabelMap.click + " \"" + uiLabelMap.CommonCancel + "\" " + uiLabelMap.orderTo + " " + uiLabelMap.backToEditDelivery + ".", 
							[{"label": uiLabelMap.CommonCancel, 
								"icon": "fa fa-remove", "class": "btn  btn-danger form-action-button pull-right",
								"callback": function () {bootbox.hideAll();}
							}, 
							{"label": uiLabelMap.OK,
								"icon": "fa-check", "class": "btn btn-primary form-action-button pull-right",
								"callback": function () {
									for (var m in listMissing) {
										listMissing[m]["quantity"] = listMissing[m].actualExportedQuantity - listMissing[m].actualDeliveredQuantity; 
									}
									loadNoteGrid(listMissing);
									$("#noteWindow").jqxWindow("open");
								}
							}]);
					return false;
				}*/
			}
			var descError = null;
			var listDeliveryItems = [];
			var listItemTmps = [];
			var curDeliveryId = null;
			for (var i in selectedItems) {
				var data1 = selectedItems[i];
				var convert = data1.convertNumber;
				var map1 = {};
				if (data1.statusId == "DELI_ITEM_APPROVED") {
					if (data1.inventoryItemId == null && data1.actualExportedQuantity > 0) {
						jOlbUtil.alert.error(uiLabelMap.DItemMissingFieldsExp);
						return false;
					}
				}
				if (data1 != undefined) {
					map1["fromOrderId"] = data1.fromOrderId;
					map1["fromOrderItemSeqId"] = data1.fromOrderItemSeqId;
					map1["inventoryItemId"] = data1.inventoryItemId;
					map1["deliveryId"] = data1.deliveryId;
					map1["deliveryItemSeqId"] = data1.deliveryItemSeqId;
					map1["actualExportedQuantity"] = data1.actualExportedQuantity*convert;
					map1["actualDeliveredQuantity"] = data1.actualDeliveredQuantity*convert;
					map1["productId"] = data1.productId;
					map1["productCode"] = data1.productCode;
					map1["isPromo"] = data1.isPromo;
					map1["requireAmount"] = data1.requireAmount;
					map1["selectedAmount"] = data1.selectedAmount;
					map1["locationId"] = data1.locationId;
					curDeliveryId = data1.deliveryId;
					listItemTmps.push(map1);
					listDeliveryItems.push(map1);
				}
			}
			var listCancel = [];
			if ("DLV_APPROVED" == glDeliveryStatusId) {
				listDeliveryItems = [];
				var listDuplicate = [];
				var listInvSelected = [];
				var listProductCodes = [];
				if (listItemTmps.length > 0) {
					for (var em in listItemTmps) {
						var checkTmp = false;
						for (var anh in listInvSelected) {
							if (listInvSelected[anh].inventoryItemId == listItemTmps[em].inventoryItemId && listInvSelected[anh].locationId == listItemTmps[em].locationId && listInvSelected[anh].orderItemSeqId == listItemTmps[em].fromOrderItemSeqId) {
								checkTmp = true;
								break;
							}
						}
						if (checkTmp == false) {
							var mapInvItem = {
								orderItemSeqId: listItemTmps[em].fromOrderItemSeqId,
								inventoryItemId: listItemTmps[em].inventoryItemId,
								requireAmount: listItemTmps[em].requireAmount,
								selectedAmount: listItemTmps[em].selectedAmount,
								locationId: listItemTmps[em].locationId,
							};
							listInvSelected.push(mapInvItem);
						}
						var checkTmp2 = false;
						for (var q in listProductCodes) {
        					if (listProductCodes[q] == listItemTmps[em].productCode) {
        						checkTmp2 = true;
        						break;
        					}
						}
						if (checkTmp2== false) {
							listProductCodes.push(listItemTmps[em].productCode);
						}
					}
				}
				for (var iId in listInvSelected) {
					var mapInvItemTmp = listInvSelected[iId];
					var invIdTmp = mapInvItemTmp.inventoryItemId;
					var locIdTmp = mapInvItemTmp.locationId;
					var orderItemSeqIdTmp = mapInvItemTmp.orderItemSeqId;
					var listDuplicated = [];
					var mapTotal;
					for (var jId in listItemTmps) {
						if (listItemTmps[jId].inventoryItemId == invIdTmp && listItemTmps[jId].locationId == locIdTmp && listItemTmps[jId].isPromo == "N" && listItemTmps[jId].fromOrderItemSeqId == orderItemSeqIdTmp) {
							mapTotal = listItemTmps[jId];
							listDuplicated.push(listItemTmps[jId]);
						}
					}
					if (listDuplicated.length > 1) {
						var totalExported = 0;
						for (var u in listDuplicated) {
							totalExported = totalExported + listDuplicated[u].actualExportedQuantity;
						}
						mapTotal = null;
						for (var k in listItemTmps) {
							if (listItemTmps[k].inventoryItemId == invIdTmp && listItemTmps[k].locationId == locIdTmp && listItemTmps[k].isPromo == "N" && listItemTmps[k].fromOrderItemSeqId == orderItemSeqIdTmp) {
								if (listItemTmps[k].deliveryItemSeqId != null && listItemTmps[k].deliveryItemSeqId != undefined) {
									mapTotal = listItemTmps[k];
								} 
							}
						}
						if (mapTotal === null) {
							for (var k in listItemTmps) {
								if (listItemTmps[k].inventoryItemId == invIdTmp && listItemTmps[k].locationId == locIdTmp && listItemTmps[k].isPromo == "N" && listItemTmps[k].fromOrderItemSeqId == orderItemSeqIdTmp) {
									mapTotal = listItemTmps[k];
									break;
								}
							}
						}
						mapTotal["actualExportedQuantity"] = totalExported;
						listDeliveryItems.push(mapTotal);
					} else if (listDuplicated.length == 1) {
						listDeliveryItems.push(mapTotal);
					}

					var listDuplicated2 = [];
					var mapTotal2;
					for (var jId in listItemTmps) {
						if (listItemTmps[jId].inventoryItemId == invIdTmp && listItemTmps[jId].locationId == locIdTmp && listItemTmps[jId].isPromo == "Y" && listItemTmps[jId].fromOrderItemSeqId == orderItemSeqIdTmp) {
							mapTotal2 = listItemTmps[jId];
							listDuplicated2.push(listItemTmps[jId]);
						}
					}
					if (listDuplicated2.length > 1) {
						var totalExported2 = 0;
						for (var u in listDuplicated2) {
							totalExported2 = totalExported2 + listDuplicated2[u].actualExportedQuantity;
						}
						mapTotal2 = null;
						for (var k in listItemTmps) {
							if (listItemTmps[k].inventoryItemId == invIdTmp && listItemTmps[k].locationId == locIdTmp && listItemTmps[k].isPromo == "Y" && listItemTmps[k].fromOrderItemSeqId == orderItemSeqIdTmp) {
								if (listItemTmps[k].deliveryItemSeqId != null && listItemTmps[k].deliveryItemSeqId != undefined) {
									mapTotal2 = listItemTmps[k];
								}
							}
						}
						if (mapTotal2 === null) {
							for (var k in listItemTmps) {
								if (listItemTmps[k].inventoryItemId == invIdTmp && listItemTmps[k].locationId == locIdTmp && listItemTmps[k].isPromo == "Y" && listItemTmps[k].fromOrderItemSeqId == orderItemSeqIdTmp) {
									mapTotal2 = listItemTmps[k];
									break;
								}
							}
						}
						mapTotal2["actualExportedQuantity"] = totalExported2;
						listDeliveryItems.push(mapTotal2);
					} else if (listDuplicated2.length == 1) {
						listDeliveryItems.push(mapTotal2);
					}
				}
				getInventory(glOrderId, glOriginFacilityId, curDeliveryId);
				var listInvExport = [];
				for (var v in listInvSelected) {
					var invTmp = {};
					invTmp["inventoryItemId"] = listInvSelected[v].inventoryItemId;

					var requireAmount = listInvSelected[v].requireAmount;
					var selectedAmount = listInvSelected[v].selectedAmount;
					var locationId = listInvSelected[v].locationId;

					var totalExptQty = 0;
					for (var t in listDeliveryItems) {
						if (listDeliveryItems[t].inventoryItemId == listInvSelected[v].inventoryItemId && listDeliveryItems[t].selectedAmount == selectedAmount && listDeliveryItems[t].locationId == locationId) {
							totalExptQty = totalExptQty + listDeliveryItems[t].actualExportedQuantity;
						}
					}
					invTmp["totalExptQty"] = totalExptQty;
					invTmp["requireAmount"] = requireAmount;
					invTmp["selectedAmount"] = selectedAmount;
					invTmp["locationId"] = locationId;
					listInvExport.push(invTmp);
				}
				var checkEng = false;
				for (var l in listInv) {
					for (var m in listInvExport) {
						if (listInv[l].inventoryItemId == listInvExport[m].inventoryItemId && listInv[l].locationId == listInvExport[m].locationId) {
							if (listInvExport[m].requireAmount == "Y") {
								if (listInv[l].amountOnHandTotal < listInvExport[m].totalExptQty) {
									checkEng = true;
									break;
								}
							} else {
								if (listInv[l].quantityOnHandTotal < listInvExport[m].totalExptQty) {
									checkEng = true;
									break;
								}
							}
						}
					}
					if (checkEng == true) break;
				}
				if (checkEng == true) {
					jOlbUtil.alert.error(uiLabelMap.NotEnoughDetail);
					Loading.hide("loadingMacro");
					return false;
				}
				var tmp = $("#actualStartDate").jqxDateTimeInput("getDate");
				if (tmp) {
					actualStartDateTmp = tmp.getTime();
				}

				var checkQty = true;
				for (b in listDeliveryItemData) {
					var init = listDeliveryItemData[b];
					var initQuantity = init.quantity;
					var expTotalQty = 0;
					for (c in listDeliveryItems) {
						var _item = listDeliveryItems[c];
						if (init.productCode == _item.productCode && init.isPromo == _item.isPromo) {
							expTotalQty = expTotalQty + _item.actualExportedQuantity;
						}
					}
					if (initQuantity > expTotalQty) {
						checkQty = false;
						if (expTotalQty <= 0){
							var cc = {
									deliveryId: init.deliveryId,
									deliveryItemSeqId: init.deliveryItemSeqId,
							}
							listCancel.push(cc);
						}
						var promo = "";
						if (init.isPromo == "Y"){
							promo = uiLabelMap.IsPromo;
						} 
						if (descError != null){
							if (promo != ""){
								descError = descError + uiLabelMap.ProductId + ": " + init.productCode + " - " + uiLabelMap.Quantity + ": " + formatnumber(expTotalQty) + " < " + formatnumber(init.quantity) + " (" + promo + ");</br>";
							} else {
								descError = descError + uiLabelMap.ProductId + ": " + init.productCode + " - " + uiLabelMap.Quantity + ": " + formatnumber(expTotalQty) + " < " + formatnumber(init.quantity) + ";</br>";
							}
						} else {
							if (promo != ""){
								descError = uiLabelMap.ProductId + ": " + init.productCode + " - " + uiLabelMap.Quantity + ": " + formatnumber(expTotalQty) + " < " + formatnumber(init.quantity) + " (" + promo + ");</br>";
							} else {
								descError = uiLabelMap.ProductId + ": " + init.productCode + " - " + uiLabelMap.Quantity + ": " + formatnumber(expTotalQty) + " < " + formatnumber(init.quantity) + ";</br>";
							}
						}
					}
				}
				if (checkQty == false) {
					descError = uiLabelMap.BLExportQuantityLessThanRequiredQuantity + "</br>" + descError;
				}
			}
			for (var ff in listDeliveryItems) {
				delete listDeliveryItems[ff]["productName"];
			}
			var listDeliveryItems = JSON.stringify(listDeliveryItems);
			var actualStartDateTmp;
			var actualArrivalDateTmp;
			if ("DLV_EXPORTED" == glDeliveryStatusId) {
				var tmp = actualArrivalDateTmp = $("#actualArrivalDate").jqxDateTimeInput("getDate");
				if (tmp) {
					actualArrivalDateTmp = tmp.getTime();
				}
			}
			var listNoteItemTmps = null;
			var datetimeReceivedReturn = null;
			var facilityReturnId = null;
			if (listNoteItems.length > 0) {
				hasReturn = true;
				for (var m in listNoteItems) {
					var convert = listNoteItems[m].convertNumber;
					listNoteItems[m]["quantity"] = (listNoteItems[m]["actualExportedQuantity"] - listNoteItems[m]["actualDeliveredQuantity"])*convert;
					if (listNoteItems[m]["expiredDate"]) {
						listNoteItems[m]["expiredDate"] = listNoteItems[m]["expiredDate"].getTime();
					} else {
						delete listNoteItems[m]["expiredDate"];
					}
					if (listNoteItems[m]["manufacturedDate"]) {
						listNoteItems[m]["manufacturedDate"] = listNoteItems[m]["manufacturedDate"].getTime();
					} else {
						delete listNoteItems[m]["manufacturedDate"];
					}
					if (listNoteItems[m].inventoryItemStatusId == "Good") {
						listNoteItems[m]["inventoryItemStatusId"] = null;
					}
				}
				facilityReturnId = $("#facilityReturnId").jqxDropDownList("val");
				var datetimeReceivedReturnTmp = $("#datetimeReceived").jqxDateTimeInput("getDate");
				datetimeReceivedReturn = datetimeReceivedReturnTmp.getTime();
				if (listNoteItems) {
					for (var nn in listNoteItems) {
						delete listNoteItems[nn]["productName"];
					}
				}
				listNoteItemTmps = JSON.stringify(listNoteItems);
			}

			var listProductDebtJson = null;
			if ("DLV_EXPORTED" == glDeliveryStatusId) {
				var listDebtProducts = $("#debtGrid").jqxGrid("getrows");
				if (listDebtProducts.length > 0) {
					for (h in listDebtProducts) {
						delete listDebtProducts[h]["productName"];
					}
					listProductDebtJson = JSON.stringify(listDebtProducts);
				}
			}
			var confirmMess = uiLabelMap.AreYouSureSave;
			if (descError != null) {
				confirmMess = descError + uiLabelMap.AreYouSureSave
			}
			var listCancelItems = null;
			if (listCancel.length > 0){
				listCancelItems = JSON.stringify(listCancel);
			}
			bootbox.dialog(confirmMess, 
			[{"label": uiLabelMap.CommonCancel, 
				"icon": "fa fa-remove", "class": "btn  btn-danger form-action-button pull-right",
				"callback": function () {
					btnClick = false;
					bootbox.hideAll();}
			}, 
			{"label": uiLabelMap.OK,
				"icon": "fa-check", "class": "btn btn-primary form-action-button pull-right",
				"callback": function () {
					if (!btnClick) {
						Loading.show("loadingMacro");
						setTimeout(function () {
							$.ajax({
								type: "POST",
								url: "updateDeliveryTotal",
								data: {
									listDeliveryItems:listDeliveryItems,
									deliveryId: curDeliveryId,
									actualStartDate: actualStartDateTmp,
									actualArrivalDate: actualArrivalDateTmp,
									orderId: glOrderId,
									facilityId: facilityReturnId,
									datetimeReceived: datetimeReceivedReturn,
									listReturnItems: listNoteItemTmps,
									listNoteItems: listNoteItemTmps,
									listProductDebts: listProductDebtJson,
									listCancelItems: listCancelItems,
								},
								dataType: "json",
								async: false,
								success: function (data) {
									if (data._ERROR_MESSAGE_ != undefined && data._ERROR_MESSAGE_ != null) {
										jOlbUtil.alert.error(uiLabelMap.UpdateError + ". " + data._ERROR_MESSAGE_);
										Loading.hide("loadingMacro");
										return false;
									}
									Grid.renderMessage("jqxNotification", uiLabelMap.UpdateSuccessfully, {
										autoClose : true,
										template : "info",
										appendContainer : "#container",
										opacity : 0.9
									});
									$("#jqxgridDelivery").jqxGrid("updatebounddata");
									if (typeof orderId != "undefined") {
										checkAllOrderItemCreatedDone(orderId);
									}
								},
								error: function (response) {
									$("#jqxgridDelivery").jqxGrid("hideloadelement");
								}
							});
							if (inOrderDetail == true) {
								// create return and note to order if have
								$.ajax({
									type: "POST",
									url: "checkOrderStatus",
									data: {
										orderId: glOrderId,
									},
									async: false,
									success: function (res) {
										statusId = res.statusId;
										var desc = "";
										for (var i in orderStatusData) {
											if (statusId == orderStatusData[i].statusId) {
												desc = orderStatusData[i].description;
											}
										}
										$("#statusTitle").text(desc);
									}
								});
							}
							if (checkContinue == true) {
								showDetailPopup(curDeliveryId, glOrderId);
							} else {
								$("#popupDeliveryDetailWindow").jqxWindow("close");
							}
							Loading.hide("loadingMacro");
						}, 500);
						btnClick = true;
					}
				}
			}]);
		}
	}
	
	function checkRequiredDataNote(rowindex) {
		var data = $("#noteGrid").jqxGrid("getrowdata", rowindex);
		if (!data.manufacturedDate) {
			$("#noteGrid").jqxGrid("unselectrow", rowindex);
			bootbox.dialog(uiLabelMap.TheManufacturedDateFieldNotYetBeEntered, [{
				"label" : uiLabelMap.OK,
				"class" : "btn btn-primary standard-bootbox-bt",
				"icon" : "fa fa-check",
				"callback": function () {
					$("#noteGrid").jqxGrid("begincelledit", rowindex, "manufacturedDate");
				}
			}]
			);
			return true;
		}
		if (!data.expiredDate) {
			$("#noteGrid").jqxGrid("unselectrow", rowindex);
			bootbox.dialog(uiLabelMap.TheExpiredDateFieldNotYetBeEntered, [{
				"label" : uiLabelMap.OK,
				"class" : "btn btn-primary standard-bootbox-bt",
				"icon" : "fa fa-check",
				"callback": function () {
					$("#noteGrid").jqxGrid("begincelledit", rowindex, "expiredDate");
				}
			}]
			);
			return true;
		}
		if (!data.returnReasonId) {
			$("#noteGrid").jqxGrid("unselectrow", rowindex);
			bootbox.dialog(uiLabelMap.PleaseSelectAReason, [{
				"label" : uiLabelMap.OK,
				"class" : "btn btn-primary standard-bootbox-bt",
				"icon" : "fa fa-check",
				"callback": function () {
					$("#noteGrid").jqxGrid("begincelledit", rowindex, "returnReasonId");
				}
			}]
			);
			return true;
		}
		if (!data.inventoryItemStatusId) {
			$("#noteGrid").jqxGrid("unselectrow", rowindex);
			bootbox.dialog(uiLabelMap.PleaseSelectAStatusOfProduct, [{
				"label" : uiLabelMap.OK,
				"class" : "btn btn-primary standard-bootbox-bt",
				"icon" : "fa fa-check",
				"callback": function () {
					$("#noteGrid").jqxGrid("begincelledit", rowindex, "inventoryItemStatusId");
				}
			}]
			);
			return true;
		}
	}
	
	getFormattedDate = function getFormattedDate(date) {
		var year = date.getFullYear();
		var month = (1 + date.getMonth()).toString();
		month = month.length > 1 ? month : "0" + month;
		var day = date.getDate().toString();
		day = day.length > 1 ? day : "0" + day;
		return day + "/" + month + "/" + year;
	}

	function addZero(i) {
		if (i < 10) {
			i = "0" + i;
		}
		return i;
	}

	showOrderNotePopup = function showOrderNotePopup() {
		loadNoteGrid(listNoteItems);
		$("#noteWindow").jqxWindow("open");
	}
	
	function checkRequiredData(rowindex) {
		var data = $("#jqxgridOrderItem").jqxGrid("getrowdata", rowindex);
		var requiredQuantityTmp = parseInt(data.requiredQuantityTmp);
		if (data == undefined) {
			return true; // to break the loop
		} 
//	    if (data.quantityOnHandTotal < 1) {
//	    	displayNotEnough(rowindex, uiLabelMap.FacilityNotEnoughProduct);
//	    	return true;
//	    }
		
		var selectedAmount = 1;
        if (data.requireAmount && data.requireAmount == "Y")
        {
        	selectedAmount = data.selectedAmount;
        }
		if (requiredQuantityTmp < 1) {
			displayAlert(rowindex, uiLabelMap.NumberGTZ);
			return true;
		} else if (requiredQuantityTmp*selectedAmount > (data.requiredQuantity - data.createdQuantity)) {
			displayAlert(rowindex, uiLabelMap.ExportValueLTZRequireValue);
			$("#jqxgridOrderItem").jqxGrid("unselectrow", rowindex);
			$("#jqxgridOrderItem").jqxGrid("begincelledit", rowindex, "requiredQuantityTmp");
			return true;
		}
//	    if (requiredQuantityTmp > data.quantityOnHandTotal) {
//	    	displayAlert(rowindex, uiLabelMap.FacilityNotEnoughProduct);
//	    	$("#jqxgridOrderItem").jqxGrid("unselectrow", rowindex);
//	    	$("#jqxgridOrderItem").jqxGrid("begincelledit", rowindex, "requiredQuantityTmp");
//	        return true;
//	    }
		return false;
	}
	function displayNotEnough(rowindex, message) {
		bootbox.dialog(message, [{
			"label" : uiLabelMap.OK,
			"class" : "btn btn-primary standard-bootbox-bt",
			"icon" : "fa fa-check",
			"callback": function () {
				$("#jqxgridOrderItem").jqxGrid("unselectrow", rowindex);
			}
		}]
		);
	}
	function displayAlert(rowindex, message) {
		bootbox.dialog(message, [{
			"label" : uiLabelMap.OK,
			"class" : "btn btn-primary standard-bootbox-bt",
			"icon" : "fa fa-check",
			"callback": function () {
				$("#jqxgridOrderItem").jqxGrid("begincelledit", rowindex, "requiredQuantityTmp");
			}
		}]
		);
	}
	
	function updateDeliveryTotalWeight() {
		var totalProductWeight = 0;
		var rows = $("#jqxgridDlvItem").jqxGrid("getselectedrowindexes");
		if (rows.length > 0) {
			for (var i in rows) {
				var data = $("#jqxgridDlvItem").jqxGrid("getrowdata", rows[i]);
				var baseWeightUomId = data.weightUomId;
				var defaultWeightUomId = data.defaultWeightUomId;
				var itemWeight = 0;
				if (data.weight) {
					itemWeight = (data.actualExportedQuantity)*(data.weight);
				} else {
					if (data.productWeight) {
						itemWeight = (data.actualExportedQuantity)*(data.productWeight);
					}
				}
				if (baseWeightUomId == defaultWeightUomId) {
					totalProductWeight = totalProductWeight + itemWeight;
				} else {
					for (var j in uomConvertData) {
						if ((uomConvertData[j].uomId == baseWeightUomId && uomConvertData[j].uomIdTo == defaultWeightUomId)) {
							totalProductWeight = totalProductWeight + (uomConvertData[j].conversionFactor)*itemWeight;
							break;
						}
						if ((uomConvertData[j].uomId == defaultWeightUomId && uomConvertData[j].uomIdTo == baseWeightUomId)) {
							totalProductWeight = totalProductWeight + itemWeight/(uomConvertData[j].conversionFactor);
							break;
						}
					}
				}
			}

			var n = parseFloat(totalProductWeight);
			totalProductWeight = Math.round(n * 1000)/1000;
			var desc = "";
			for (var i in weightUomData) {
				if (weightUomData[i].uomId == defaultWeightUomId) {
					desc = weightUomData[i].description;
				}
			}
			// $("#totalWeight").text(totalProductWeight.toLocaleString(localeStr) + " (" + desc + ")"); 
		} else {
			getTotalWeight();
		}
	}
	
	var updateTotalWeight = function updateTotalWeight() {
		var totalProductWeight = 0;
		var selectedIndexs = $("#jqxgridOrderItem").jqxGrid("getselectedrowindexes");
		for (var i in selectedIndexs) {
			var data = $("#jqxgridOrderItem").jqxGrid("getrowdata", selectedIndexs[i]);
			var baseWeightUomId = data.baseWeightUomId;
			if (!baseWeightUomId) {
				baseWeightUomId = "WT_Kg";
			}
			var defaultWeightUomId = "WT_kg";
			var itemWeight = 0;
			if (data.availableToPromiseTotal < 1) {
				itemWeight = 0;
			} else {
				itemWeight = (data.requiredQuantityTmp)*(data.weight);
			}
			if (baseWeightUomId == defaultWeightUomId) {
				totalProductWeight = totalProductWeight + itemWeight;
			} else {
				for (var j in uomConvertData) {
					if ((uomConvertData[j].uomId == baseWeightUomId && uomConvertData[j].uomIdTo == defaultWeightUomId)) {
						totalProductWeight = totalProductWeight + (uomConvertData[j].conversionFactor)*itemWeight;
						break;
					}
					if ((uomConvertData[j].uomId == defaultWeightUomId && uomConvertData[j].uomIdTo == baseWeightUomId)) {
						totalProductWeight = totalProductWeight + itemWeight/(uomConvertData[j].conversionFactor);
						break;
					}
				}
			}
		}
		var n = parseFloat(totalProductWeight)
		totalProductWeight = Math.round(n * 1000)/1000;
//		$("#totalProductWeight").text(totalProductWeight.toLocaleString(localeStr)); 
	}
	
	function getTotalWeight() {
		var totalProductWeight = 0;
		var defaultUomId = null;
		var rows = $("#jqxgridDlvItem").jqxGrid("getrows");
		for (var i in rows) {
			var itemWeight = 0;
			var data = rows[i];
			$.ajax({
				url: "getProductDeliveryWeight",
				type: "POST",
				async: false,
				data: {
					deliveryId : data.deliveryId,
					deliveryItemSeqId : data.deliveryItemSeqId,
				},
				success: function (res) {
					itemWeight = res["totalWeight"];
					defaultUomId = res["weightUomId"];
				}
			});
			totalProductWeight = totalProductWeight + itemWeight;
		}
		var n = parseFloat(totalProductWeight)
		totalProductWeight = Math.round(n * 1000)/1000;
		var desc = "";
		for (var i in weightUomData) {
			if (weightUomData[i].uomId == defaultUomId) {
				desc = weightUomData[i].description;
			}
		}
		// $("#totalWeight").text(totalProductWeight.toLocaleString(localeStr) + " (" + desc + ")"); 
	}
	
	var afterAddDelivery = function afterAddDelivery() {
		$("#jqxgridDelivery").jqxGrid("updatebounddata");
		if (typeof orderId != "undefined") {
			checkAllOrderItemCreatedDone(orderId);
		}
	}
	
	function checkAllOrderItemCreatedDone(orderId) {
		var createdDone = true;
		$.ajax({
			type: "POST",
			async: false,
			url: "checkAllSalesOrderItemCreatedDelivery",
			data: {
				orderId: orderId,
			},
			success: function (res) {
				createdDone = res["createdDone"];
				if (createdDone == true) {
					$("#addrowbuttonjqxgridDelivery").hide();
					$("#customcontroljqxgridDelivery1").hide();
				} else {
					$("#addrowbuttonjqxgridDelivery").show();
					$("#customcontroljqxgridDelivery1").show();
				}
			}
		});
	}
	
	function renderHtml(data, key, value, id) {
		var y = "";
		var source = new Array();
		var index = 0;
		for (var x in data) {
			index = source.length;
			var row = {};
			row[key] = data[x][key];
			row["description"] = data[x][value];
			source[index] = row;
		}
		if ($("#"+id).length) {
			$("#"+id).jqxDropDownList("clear");
			$("#"+id).jqxDropDownList({source: source, selectedIndex: 0});
		}
	}
	function update(jsonObject, url, data, key, value, id) {
		jQuery.ajax({
			url: url,
			type: "POST",
			data: jsonObject,
			async: false,
			success: function (res) {
				var json = res[data];
				renderHtml(json, key, value, id);
			}
		});
	}
	function updateStateProvince() {
		var request = $.ajax({
			url: "loadGeoAssocListByGeoId",
			type: "POST",
			data: {geoId : $("#countryGeoId").val(),
			},
			dataType: "json",
			success: function (data) {
				var listcontactMechPurposeTypeMap = data["listGeoAssocMap"];
				var contactMechPurposeTypeId = new Array();
				var description = new Array();
				var array_keys = new Array();
				var array_values = new Array();
				for (var i in listcontactMechPurposeTypeMap) {
					for (var key in listcontactMechPurposeTypeMap[i]) {
						array_keys.push(key);
						array_values.push(listcontactMechPurposeTypeMap[i][key]);
					}
				}
				var dataTest = new Array();
				for (var j in array_keys) {
					var row = {};
					row["id"] = array_keys[j];
					row["value"] = array_values[j];
					dataTest[j] = row;
				}
				if (dataTest.length == 0) {
					var dataEmpty = new Array();
					if ($("#stateProvinceGeoId").length > 0) {
						$("#stateProvinceGeoId").jqxDropDownList({placeHolder: uiLabelMap.PleaseSelectTitle, source: dataEmpty, autoDropDownHeight: true});
						$("#stateProvinceGeoId").jqxDropDownList("setContent", uiLabelMap.CommonNoStatesProvincesExists);
					}
				} else { 
					if ($("#stateProvinceGeoId").length > 0) {
						$("#stateProvinceGeoId").jqxDropDownList({placeHolder: uiLabelMap.PleaseSelectTitle,source: dataEmpty, autoDropDownHeight: false});
						$("#stateProvinceGeoId").jqxDropDownList({placeHolder: uiLabelMap.PleaseSelectTitle, selectedIndex: 0,  source: dataTest, displayMember: "value", valueMember: "id"});
						if ("VNM" == $("#countryGeoId").val()) {
							$("#stateProvinceGeoId").jqxDropDownList("val", "VNM-HN2");
						}
					}
				}
			}
		}); 
	}
	var initValidateForm = function () {
		if ($("#addPostalAddressWindow").length > 0) {
			$("#addPostalAddressWindow").jqxValidator({
				rules: [
					{ input: "#address1", message: uiLabelMap.FieldRequired, action: "keyup, valueChanged", rule: "required" },
					{ input: "#postalCode", message: uiLabelMap.FieldRequired, action: "keyup, valueChanged", rule: "required" },
				]
			});
		}
		
		if ($("#alterpopupWindow").length > 0) {
			initValidateAdd();
		}
	};
	var addOriginFacilityAddress = function addOriginFacilityAddress() {
		var originFacilityId = facilitySelected.facilityId;
		if (originFacilityId) {
			if (facilitySelected.facilityCode){
				$("#seletedFacilityId").text(facilitySelected.facilityCode);
			} else {
				$("#seletedFacilityId").text(facilitySelected.facilityCode);
			}
			contactMechPurposeTypeId = "SHIP_ORIG_LOCATION";
			$("#addPostalAddressWindow").jqxWindow("open");
		} else {
			bootbox.dialog(uiLabelMap.PleaseChooseFacilityBefore, [{
				"label" : uiLabelMap.OK,
				"class" : "btn btn-primary standard-bootbox-bt",
				"icon" : "fa fa-check",
			}]
			);
		}
	}
	var showPopupSelectFacility = function showPopupSelectFacility(orderId) {
		$("#selectFacilityWindow").jqxWindow("open");
		$("#defaultOrderId").val(orderId);
	}
	function quickCreateDelivery(orderId, facilityId, contactMechId, shipmentMethodTypeId, carrierPartyId) {
		var dlvId = null;
		jQuery.ajax({
			url: "quickCreateDelivery",
			type: "POST",
			async: false,
			data: {
				orderId: orderId,
				facilityId: facilityId,
				contactMechId: contactMechId,
				shipmentMethodTypeId: shipmentMethodTypeId,
				carrierPartyId: carrierPartyId,
			},
			success: function (res) {
				dlvId = res.deliveryId;
				$("#jqxgridDelivery").jqxGrid("updatebounddata"); 
				if (res._ERROR_MESSAGE_ == "NO_FACILITY_ENOUGH_PRODUCT") {
					$("#notifyIdQuickCreateError").jqxNotification("open");
				} else {
//					$("#notifyCreateSuccessful").jqxNotification("open");
				}
			}
		});
		$("#selectFacilityWindow").jqxWindow("close");
		if (typeof orderId != "undefined") {
			checkAllOrderItemCreatedDone(orderId);
		}
//		showDetailPopup(dlvId, orderId);
//		showDetailDelivery(dlvId); 
	}
	
	function initAttachExptFile() {
		$("#attachFileExpt").html("");
		listImage = [];
		$("#attachFileExpt").ace_file_input({
			style:"well",
			btn_choose: uiLabelMap.DropFileOrClickToChoose,
			btn_change:null,
			no_icon:"icon-cloud-upload",
			droppable:true,
			onchange:null,
			thumbnail:"small",
			before_change:function (files, dropped) {
				listImage = [];
				var count = files.length;
				for (var int in files) {
					var imageName = files[int].name;
					var hashName = imageName.split(".");
					var extended = hashName.pop();
					if (imageName.length > 50) {
						bootbox.dialog(uiLabelMap.NameOfImagesMustBeLessThan50Character, [{
							"label" : uiLabelMap.OK,
							"class" : "btn btn-primary standard-bootbox-bt",
							"icon" : "fa fa-check",
						}]
						);
						return false;
					} else {
						if (extended == "JPG" || extended == "jpg" || extended == "jpeg" || extended == "gif" || extended == "png") {
							listImage.push(files[int]);
						} else {
							bootbox.dialog(uiLabelMap.OnlySupportFile + " .JPG, .jpg, .jpeg, .gif, .png.", [{
								"label" : uiLabelMap.OK,
								"class" : "btn btn-primary standard-bootbox-bt",
								"icon" : "fa fa-check",
							}]
							);
							return false;
						}
					}
				}
				return true;
			},
			before_remove : function () {
				listImage = [];
				return true;
			}
		});
	}
	
	function initAttachFile() {
		$("#attachFile").html("");
		listImage = [];
		$("#attachFile").ace_file_input({
			style:"well",
			btn_choose: uiLabelMap.DropFileOrClickToChoose,
			btn_change:null,
			no_icon:"icon-cloud-upload",
			droppable:true,
			onchange:null,
			thumbnail:"small",
			before_change:function (files, dropped) {
				listImage = [];
				var count = files.length;
				for (var int in files) {
					var imageName = files[int].name;
					var hashName = imageName.split(".");
					var extended = hashName.pop();
					if (imageName.length > 50) {
						bootbox.dialog(uiLabelMap.NameOfImagesMustBeLessThan50Character, [{
							"label" : uiLabelMap.OK,
							"class" : "btn btn-primary standard-bootbox-bt",
							"icon" : "fa fa-check",
						}]
						);
						return false;
					} else {
						if (extended == "JPG" || extended == "jpg" || extended == "jpeg" || extended == "gif" || extended == "png") {
							listImage.push(files[int]);
						} else {
							bootbox.dialog(uiLabelMap.OnlySupportFile + " .JPG, .jpg, .jpeg, .gif, .png.", [{
								"label" : uiLabelMap.OK,
								"class" : "btn btn-primary standard-bootbox-bt",
								"icon" : "fa fa-check",
							}]
							);
							return false;
						}
					} 
				}
				return true;
			},
			before_remove : function () {
				listImage = [];
				return true;
			}
		});
	}
	
	function saveFileExptUpload () {
		Loading.show("loadingMacro");
		setTimeout(function () {
			var folder = "/baseLogistics/delivery";
			for ( var d in listImage) {
				var file = listImage[d];
				var dataResourceName = file.name;
				var path = "";
				var form_data= new FormData();
				form_data.append("uploadedFile", file);
				form_data.append("folder", folder);
				jQuery.ajax({
					url: "uploadImages",
					type: "POST",
					data: form_data,
					cache : false,
					contentType : false,
					processData : false,
					success: function (res) {
						path = res.path;
						pathScanFileExpt = path;
						$("#linkIdExpt").html("");
						$("#linkIdExpt").attr("onclick", null); 
						$("#linkIdExpt").append("<a href=\""+path+"\" onclick=\"\" target=\"_blank\"><i class=\"fa-file-image-o\"></i>"+uiLabelMap.AttachExportedScan+"</a> <a href=\"javascript:SalesDlvObj.removeExptScanFile()\"><i class=\"fa-remove\"></i></a>");
					}
				}).done(function () {
				});
			}
			$("#jqxFileScanExptUpload").jqxWindow("close");
			Loading.hide("loadingMacro");
		}, 500);
	}
	
	function saveFileUpload () {
		Loading.show("loadingMacro");
		setTimeout(function () {
			var folder = "/baseLogistics/delivery";
			for ( var d in listImage) {
				var file = listImage[d];
				var dataResourceName = file.name;
				var path = "";
				var form_data= new FormData();
				form_data.append("uploadedFile", file);
				form_data.append("folder", folder);
				jQuery.ajax({
					url: "uploadImages",
					type: "POST",
					data: form_data,
					cache : false,
					contentType : false,
					processData : false,
					success: function (res) {
						path = res.path;
						pathScanFile = path;
						$("#linkId").html("");
						$("#linkId").attr("onclick", null); 
						$("#linkId").append("<a href=\""+path+"\" onclick=\"\" target=\"_blank\"><i class=\"fa-file-image-o\"></i>"+uiLabelMap.AttachDeliveredScan+"</a> <a href=\"javascript:SalesDlvObj.removeScanFile()\"><i class=\"fa-remove\"></i></a>");
					}
				}).done(function () {
				});
			}
			$("#jqxFileScanUpload").jqxWindow("close");
			Loading.hide("loadingMacro");
		}, 500);
	}
	
	var removeExptScanFile = function removeExptScanFile () {
		pathScanExptFile = null;
		$("#linkIdExpt").html("");
		$("#linkIdExpt").attr("onclick", null);
		$("#linkIdExpt").append("<a id=\"linkId\" href=\"javascript:SalesDlvObj.showAttachExptFilePopup()\" onclick=\"\"><i class=\"fa-upload\"></i> "+uiLabelMap.AttachExportedScan+"</a>");
	}
	
	var removeScanFile = function removeScanFile () {
		pathScanFile = null;
		$("#linkId").html("");
		$("#linkId").attr("onclick", null);
		$("#linkId").append("<a id=\"linkId\" href=\"javascript:SalesDlvObj.showAttachFilePopup()\" onclick=\"\"><i class=\"fa-upload\"></i> "+uiLabelMap.AttachDeliveredScan+"</a>");
	}
	var showAttachFilePopup = function showAttachFilePopup() {
		$("#jqxFileScanUpload").jqxWindow("open");
	}
	
	var showAttachExptFilePopup = function showAttachExptFilePopup() {
		$("#jqxFileScanExptUpload").jqxWindow("open");
	}
	
	function formatFullDate(value) {
		if (value != undefined && value != null && !(/^\s*$/.test(value))) {
			var dateStr = "";
			dateStr += addZero(value.getFullYear()) + "-";
			dateStr += addZero(value.getMonth()+1) + "-";
			dateStr += addZero(value.getDate()) + " ";
			dateStr += addZero(value.getHours()) + ":";
			dateStr += addZero(value.getMinutes()) + ":";
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
	var selectFacility = function selectFacility(facilityId) {
		$("#facilityWindow").jqxWindow("close");
	}
	var getFacilityList = function getFacilityList() {
		$("#facilityWindow").jqxWindow("open");
	}
	function getInventoryItemTotalByFacility() {
		var facId = facilitySelected.facilityId;
		if (facId) {
			var rows = $("#jqxgridOrderItem").jqxGrid("getrows");
			if (rows && rows.length > 0) {
				for (var i in rows) {
					(function (i) {
						var data = $("#jqxgridOrderItem").jqxGrid("getrowdata", i);
						var exp = data.expireDate;
						var expireDate = formatFullDate(exp);
						jQuery.ajax({
							url: "getDetailQuantityInventory",
							type: "POST",
							async: false,
							data: {
								productId: data.productId,
								expireDate: expireDate,
								originFacilityId: facId,
							},
							success: function (res) {
								setTimeout(function () {
									if (parseInt(res.availableToPromiseTotal) <= 0) {
										var id = $("#jqxgridOrderItem").jqxGrid("getrowid", i);
										$("#jqxgridOrderItem").jqxGrid("setcellvaluebyid", id, "quantityOnHandTotal", res.quantityOnHandTotal);
									} else if (res != undefined) {
										var id = $("#jqxgridOrderItem").jqxGrid("getrowid", i);
										$("#jqxgridOrderItem").jqxGrid("setcellvaluebyid", id, "quantityOnHandTotal", res.quantityOnHandTotal);
									}
									Loading.hide("loadingMacro");
								}, 500);
							},
							error: function (response) {
							}
						});
					}(i));
				}
			}
		}
	}
	function initValidateDatetimeDT(element) {
		element.jqxValidator({
			rules:
			[
				{
					input: "#actualArrivalDate", 
					message: uiLabelMap.ActualDeliveredDateMustAfterActualExportedDate, 
					action: "valueChanged", 
					position: "topcenter",
					rule: function (input) {
						var actualArrivalDate = $("#actualArrivalDate").jqxDateTimeInput("getDate");
						if ((typeof(actualStartDategl) != "undefined" && actualStartDategl != null && !(/^\s*$/.test(actualStartDategl))) && (typeof(actualArrivalDate) != "undefined" && actualArrivalDate != null && !(/^\s*$/.test(actualArrivalDate)))) {
							if (actualArrivalDate < actualStartDategl && "DLV_EXPORTED" == glDeliveryStatusId) {
								return false;
							}
						}
						return true;
					}
				},
				{
					input: "#actualArrivalDate", 
					message: uiLabelMap.CannotAfterNow, 
					action: "valueChanged", 
					position: "topcenter",
					rule: function (input) {
						var actualArrivalDate = $("#actualArrivalDate").jqxDateTimeInput("getDate");
						var nowDate = new Date();
						if ((typeof(actualArrivalDate) != "undefined" && actualArrivalDate != null && !(/^\s*$/.test(actualArrivalDate)))) {
							if (actualArrivalDate > nowDate && "DLV_EXPORTED" == glDeliveryStatusId) {
								return false;
							}
						}
						return true;
					}
				},
				{
					input: "#actualStartDate", 
					message: uiLabelMap.CannotAfterNow, 
					action: "valueChanged", 
					position: "topcenter",
					rule: function (input) {
						var actualStartDate = $("#actualStartDate").jqxDateTimeInput("getDate");
						var nowDate = new Date();
						if ((typeof(actualStartDate) != "undefined" && actualStartDate != null && !(/^\s*$/.test(actualStartDate)))) {
							if (actualStartDate > nowDate && "DLV_APPROVED" == glDeliveryStatusId) {
								return false;
							}
						}
						return true;
					}
				}
			]
		});
	}
			
	function initValidateAdd() {
		var rules = [{
			input: "#estimatedArrivalDate", 
			message: uiLabelMap.ArrivalDateEstimatedMustBeAfterExportDateEstimated, 
			action: "valueChanged", 
			rule: function (input) {	
				var estimatedArrivalDate = $("#estimatedArrivalDate").jqxDateTimeInput("getDate");
				var estimatedStartDate = $("#estimatedStartDate").jqxDateTimeInput("getDate");
				if ((typeof(estimatedArrivalDate) != "undefined" && estimatedArrivalDate != null && !(/^\s*$/.test(estimatedArrivalDate))) && (typeof(estimatedStartDate) != "undefined" && estimatedStartDate != null && !(/^\s*$/.test(estimatedStartDate)))) {
					if (estimatedArrivalDate < estimatedStartDate) {
						return false;
					}
				}
				return true;
			}
		},
		{
			input: "#estimatedStartDate", 
			message: uiLabelMap.CannotBeforeNow, 
			action: "valueChanged", 
			rule: function (input) {
				var estimatedStartDate = $("#estimatedStartDate").jqxDateTimeInput("getDate");
				var nowDate = new Date();
				if ((typeof(estimatedStartDate) != "undefined" && estimatedStartDate != null && !(/^\s*$/.test(estimatedStartDate)))) {
					if (estimatedStartDate < nowDate) {
						return false;
					}
				}
				return true;
			}
		},
		{
			input: "#estimatedArrivalDate", 
			message: uiLabelMap.CannotBeforeNow, 
			action: "valueChanged", 
			rule: function (input) {
				var estimatedArrivalDate = $("#estimatedArrivalDate").jqxDateTimeInput("getDate");
				var nowDate = new Date();
				if ((typeof(estimatedArrivalDate) != "undefined" && estimatedArrivalDate != null && !(/^\s*$/.test(estimatedArrivalDate)))) {
					if (estimatedArrivalDate < nowDate) {
						return false;
					}
				}
				return true;
			}
		},
		{
			input: "#originContactMechId", 
			message: uiLabelMap.FieldRequired, 
			action: "valueChanged", 
			rule: function (input) {	
				var tmp = $("#originContactMechId").jqxDropDownList("getSelectedItem");
				return tmp ? true : false;
			}
		},
		{
			input: "#destContactMechId", 
			message: uiLabelMap.FieldRequired, 
			action: "valueChanged", 
			rule: function (input) {	
				var tmp = $("#destContactMechId").jqxDropDownList("getSelectedItem");
				return tmp ? true : false;
			}
		},
		{
			input: '#facilityPopup', 
            message: uiLabelMap.FieldRequired, 
            action: 'valueChanged',
            rule: function (input) {	
         	   	var tmp = facilitySelected;
                return tmp ? true : false;
            }
		},
		{input: "#deliveryId", message: uiLabelMap.ThisFieldMustNotByContainSpecialCharacter, action: "valueChanged", rule: 
			function (input, commit) {
				var value = $(input).val();
				if (value && !(/^[a-zA-Z0-9_]+$/.test(value))) {
					return false;
				}
				return true;
			}
		},
		];
		if (typeof needsCheckShipmentMethod != "undefined") {
			if (needsCheckShipmentMethod == true) {
				var objTmp1 = {input: "#shipmentMethodTypeId", message: uiLabelMap.FieldRequired, action: "valueChanged", rule: 
					function (input, commit) {
						var value = $(input).val();
						if (needsCheckShipmentMethod == true) {
							var value = $(input).val();
							if (value === null || value === undefined || value === "") {
								return false;
							}
							return true;
						}
						return true;
					}
				};
				var objTmp2 = {input: "#partyId", message: uiLabelMap.FieldRequired, action: "valueChanged", rule: 
					function (input, commit) {
						var value = $(input).val();
						if (needsCheckShipmentMethod == true) {
							if (value === null || value === undefined || value === "") {
								return false;
							}
							return true;
						}
						return true;
					}
				};
				rules.push(objTmp1);
				rules.push(objTmp2);
			}
		}
		
		validatorAdd = new OlbValidator($("#alterpopupWindow"), [], rules, { position : "right" });
	}
	
	function checkGridDeliveryItemRequiredData(rowindex, allRows) {
		var data = $("#jqxgridDlvItem").jqxGrid("getrowdata", rowindex);
		if (data.statusId == "DELI_ITEM_EXPORTED") {
			if (data.actualDeliveredQuantity > data.actualExportedQuantity) {
				$("#jqxgridDlvItem").jqxGrid("unselectrow", rowindex);
				bootbox.dialog(uiLabelMap.LogCheckActuallyExportedGreaterRealCommunication, [{
					"label" : uiLabelMap.OK,
					"class" : "btn btn-primary standard-bootbox-bt",
					"icon" : "fa fa-check",
					"callback": function () {
						$("#jqxgridDlvItem").jqxGrid("begincelledit", rowindex, "actualDeliveredQuantity");
					}
				}]
				);
				return true;
			}
		}
		if (data.statusId == "DELI_ITEM_DELIVERED") {
			jOlbUtil.alert.error(uiLabelMap.DLYItemComplete);
			return true;
		}
		if (data.statusId == "DELI_ITEM_APPROVED") {
			if (listInv.length > 0 && data.inventoryItemId != null && data.inventoryItemId != undefined && data.inventoryItemId != "") {
				var convert = data.convertNumber;
				var qtyExport = 0;
				for (var x in allRows){
					if (data.inventoryItemId == allRows[x].inventoryItemId && data.locationCode == allRows[x].locationCode){
						qtyExport = qtyExport + allRows[x].actualExportedQuantity;
					}
				}
				for (var i in listInv) {
					if (listInv[i].inventoryItemId == data.inventoryItemId && listInv[i].locationCode == data.locationCode) {
						var requireAmount = data.requireAmount;
						var selectedAmount = data.selectedAmount;
						if (requireAmount && requireAmount == "Y" && selectedAmount) {
							if (listInv[i].amountOnHandTotal < qtyExport) {
								bootbox.dialog(uiLabelMap.ExpiredDateSelectedNotEnoughSelectAnother, [{
									"label" : uiLabelMap.OK,
									"class" : "btn btn-primary standard-bootbox-bt",
									"icon" : "fa fa-check",
									"callback": function () {
										$("#jqxgridDlvItem").jqxGrid("begincelledit", rowindex, "inventoryItemId");
									}
								}]
								);
								return true;
							}
						} else {
							if (listInv[i].quantityOnHandTotal < qtyExport*convert) {
								bootbox.dialog(uiLabelMap.ExpiredDateSelectedNotEnoughSelectAnother, [{
									"label" : uiLabelMap.OK,
									"class" : "btn btn-primary standard-bootbox-bt",
									"icon" : "fa fa-check",
									"callback": function () {
										$("#jqxgridDlvItem").jqxGrid("begincelledit", rowindex, "inventoryItemId");
									}
								}]
								);
								return true;
							}
						}
					}
				}
			}
			if (data.inventoryItemId === null || data.inventoryItemId === undefined || data.inventoryItemId == "") {
				if (data.actualExportedQuantity > 0) {
					bootbox.dialog(uiLabelMap.ExpireDateNotEnter + " " + uiLabelMap.or + " " + uiLabelMap.NotEnoughDetail, [{
						"label" : uiLabelMap.OK,
						"class" : "btn btn-primary standard-bootbox-bt",
						"icon" : "fa fa-check",
						"callback": function () {
							$("#jqxgridDlvItem").jqxGrid("begincelledit", rowindex, "inventoryItemId");
						}
					}]
					);
					return true;
				}
			} else if (data.actualExportedQuantity === null || data.actualExportedQuantity === undefined) {
				bootbox.dialog(uiLabelMap.PleaseEnterQuantityExported, [{
					"label" : uiLabelMap.OK,
					"class" : "btn btn-primary standard-bootbox-bt",
					"icon" : "fa fa-check",
					"callback": function () {
						$("#jqxgridDlvItem").jqxGrid("begincelledit", rowindex, "actualExportedQuantity");
					}
				}]
				);
				return true;
			}
		}
		return false;
	}
	function functionAfterUpdate2() {
		var tmpS = $("#jqxgridDlvItem").jqxGrid("source");
		tmpS._source.url = "jqxGeneralServicer?sname=getListDeliveryItem&deliveryId=" + glDeliveryId;
		$("#jqxgridDlvItem").jqxGrid("source", tmpS);
	}

	function confirmExportNumber(rowid, rowdata) {
		var tmpRowData = new Object();
		tmpRowData.productId = rowdata.productId;
		tmpRowData.quantityUomId = rowdata.quantityUomId;
		tmpRowData.fromOrderId = rowdata.fromOrderId;
		tmpRowData.fromOrderItemSeqId = rowdata.fromOrderItemSeqId;
		tmpRowData.inventoryItemId = rowdata.inventoryItemId;
		tmpRowData.deliveryId = rowdata.deliveryId;
		tmpRowData.deliveryItemSeqId = rowdata.deliveryItemSeqId;
		tmpRowData.actualExportedQuantity = rowdata.actualExportedQuantity;
		tmpRowData.actualDeliveredQuantity = rowdata.actualDeliveredQuantity;
		tmpRowData.actualExpireDate = rowdata.actualExpireDate;
		tmpRowData.expireDate = rowdata.expireDate;
		for (var i in listInv) {
			if (listInv[i].productId == tmpRowData.productId) {
				var tmpDate = new Date(listInv[i].expireDate.time);
				var tmpValue = new Object();
				tmpRowData.expireDate =  $.datepicker.formatDate("dd/mm/yy", tmpDate);
				break;
			}
		}
		var strMsg;
		if (tmpRowData.actualDeliveredQuantity != null && tmpRowData.actualDeliveredQuantity > 0) {
			strMsg = uiLabelMap.ConfirmToDelivery + "#" +  tmpRowData.productId + " uiLabelMap.WithExpireDate " + tmpRowData.expireDate + " uiLabelMap.LogIs " +
			tmpRowData.actualDeliveredQuantity + " [" + tmpRowData.quantityUomId + "] ?";
		} else {
			strMsg = uiLabelMap.ConfirmToExport +"#" +  tmpRowData.productId + " uiLabelMap.WithExpireDate " + tmpRowData.expireDate + " uiLabelMap.LogIs " +
			tmpRowData.actualExportedQuantity + " [" + tmpRowData.quantityUomId + "] ?";
		}
		bootbox.dialog(strMsg, 
				[{"label": uiLabelMap.CommonCancel, 
					"icon": "fa fa-remove", "class": "btn  btn-danger form-action-button pull-right",
					"callback": function () {
						editPending = false;
						bootbox.hideAll();
					}
				}, 
				{"label": uiLabelMap.OK,
					"icon": "fa-check", "class": "btn btn-primary form-action-button pull-right",
					"callback": function () {
						editPending = true;
						$("#jqxgridDlvItem").jqxGrid("updaterow", rowid, tmpRowData);
					}
				}]);
	}
	function checkRoleByDelivery(deliveryId) {
		$.ajax({
			type: "POST",
			url: "checkRoleByDelivery",
			data: {"deliveryId": deliveryId},
			dataType: "json",
			async: false,
			success: function (response) {
				isStorekeeperFrom = response.isStorekeeperFrom;
				isStorekeeperTo = response.isStorekeeperTo;
				isSpecialist = response.isSpecialist;
			},
			error: function (response) {
				alert("Error:" + response);
			}
		});
	}
	var showDetailPopup = function showDetailPopup(deliveryId, orderId) {
		if (typeof glOrderId === "undefined") {
			glOrderId = orderId;
		}
		$("#cancelDlv").hide();
		$("#editDlv").hide();
		$("#debtRecord").hide();
		checkContinue = false;
		checkRoleByDelivery(deliveryId);
		$("#orderNote").hide();
		glDeliveryId = deliveryId;
		// Cache delivery
		$("#checkLabel").jqxCheckBox({ width: 120, height: 25, checked: false});
		var checkLabel = "N";
		if ($("#checkLabel").length > 0) {
			var val = $("#checkLabel").val();
			if (val == false) {
				checkLabel = "N";
			} else {
				checkLabel = "Y";
			}
		} else {
			checkLabel = "N";
		}

		$("#checkLabel").on("checked", function (event) {
			Loading.show("loadingMacro");
			setTimeout(function () {
			checkLabel = "Y";
			deliveryDT = getDeliveryAndInv(deliveryId, checkLabel, orderId);
			$("#jqxgridDlvItem").jqxGrid("refresh");
			$("#jqxgridDlvItem").jqxGrid("updatebounddata");
			Loading.hide("loadingMacro");	 
			}, 200);
		});
		
		$("#checkLabel").on("unchecked", function (event) {
			Loading.show("loadingMacro");
			setTimeout(function () {
			checkLabel = "N";
			deliveryDT = getDeliveryAndInv(deliveryId, checkLabel, orderId);
			$("#jqxgridDlvItem").jqxGrid("refresh");
			$("#jqxgridDlvItem").jqxGrid("updatebounddata");
			Loading.hide("loadingMacro");	
			}, 200);
		});
		
		deliveryDT = getDeliveryAndInv(deliveryId, checkLabel, orderId);
		var requireLocation = deliveryDT.requireLocation;
		if ("DLV_CANCELLED" == deliveryDT.statusId) {
			$("#printPDF").hide();
			$("#printDeliveryDoc").hide();
			$("#printDlvNotPrice").hide();
			$("#printDlvPooledTax").hide();
			$("#scanfile").hide();
			$("#orderNote").hide();
			$("#scanfileExpt").hide();
			$("#totalWeight").hide();
			$("#actualStartDate").jqxDateTimeInput("disabled", true);
			$("#actualArrivalDate").jqxDateTimeInput("disabled", true);
			$("#alterSave2").hide();
			$("#alterApprove").hide();
			$("#cancelDlv").hide();
			$("#editDlv").hide();
			$("#exportSample").hide();
		} else {
			$("#printPDF").show();
			$("#printDeliveryDoc").show();
			$("#printDlvNotPrice").show();
			$("#totalWeight").show();
			$("#scanfile").show();
			$("#scanfileExpt").show();
		}

		var hrefDoc = "deliveryAndExport.pdf?deliveryId=";
		hrefDoc += deliveryDT.deliveryId;
		$("#printDeliveryDoc").attr("href", hrefDoc);

		var hrefDoc = "printDeliveryDocPooledTax.pdf?deliveryId=";
		hrefDoc += deliveryDT.deliveryId;
		$("#printDlvPooledTax").attr("href", hrefDoc);

		var hrefSmt = "printShipmentInfo.pdf?deliveryId=";
		hrefSmt += deliveryDT.deliveryId;
		$("#printShipmentInfo").attr("href", hrefSmt);

		glDelivery = deliveryDT;
		glOriginFacilityId = deliveryDT.originFacilityId;
		glDeliveryStatusId = deliveryDT.statusId;
		// Set deliveryId for target print pdf
		var href = "deliveryUnitPrice.pdf?deliveryId=";
		href += deliveryId;
		$("#printPDF").attr("href", href);

		// Create deliveryIdDT
		$("#deliveryIdDT").text(deliveryDT.deliveryId);
		
		// Create statusIdDT
		var stName = null;
		if (deliveryDT.statusId == "DLV_DELIVERED") {
			stName = uiLabelMap.BLCompleted;
		} else {
			for (var i in statusData) {
				if (statusData[i].statusId == deliveryDT.statusId) {
					stName = statusData[i].description;
				}
			}
		}
		if (stName) {
			$("#statusIdDT").text(stName);
			if ("DLV_CANCELLED" == deliveryDT.statusId || "DLV_CREATED" == deliveryDT.statusId || "DLV_PROPOSED" == deliveryDT.statusId || ("DLV_DELIVERED" == deliveryDT.statusId && deliveryDT.pathScanFile != null)) {
				$("#alterSave2").hide();
				if (perAdmin) {
					if ("DLV_CREATED" == deliveryDT.statusId || "DLV_PROPOSED" == deliveryDT.statusId) {
						$("#alterApprove").show();
					} else {
						$("#alterApprove").hide();
					}
				}
			} else {
				$("#alterSave2").show();
				$("#alterApprove").hide();
			}
		} else {
			$("#statusIdDT").text("_NA_");
		}
		// Create orderIdDT
		if (deliveryDT.orderId) {
			$("#orderIdDT").text(orderId);
		} else {
			$("#orderIdDT").text("_NA_");
		}

		// Create originFacilityIdDT
		$("#originFacilityIdDT").text(deliveryDT.originFacilityName);

		// Create destFacilityIdDT
		$("#deliveryTypeDT").text(deliveryDT.deliveryTypeDesc);

		// Create createDateDT
		var createDate = new Date(deliveryDT.createDate);
		var createDateText = null;
		if (createDate.getMonth()+1 < 10) {
			if (createDate.getDate() < 10) {
				createDateText = "0" + createDate.getDate() + "/0" + (createDate.getMonth()+1) + "/" + createDate.getFullYear();
			} else {
				createDateText = createDate.getDate() + "/0" + (createDate.getMonth()+1) + "/" + createDate.getFullYear();
			}
		} else {
			if (createDate.getDate() < 10) {
				createDateText = "0" + createDate.getDate() + "/" + (createDate.getMonth()+1) + "/" + createDate.getFullYear();
			} else {
				createDateText = createDate.getDate() + "/" + (createDate.getMonth()+1) + "/" + createDate.getFullYear();
			}
		}
		$("#createDateDT").html("");
		$("#createDateDT").append(createDateText + " " + addZero(createDate.getHours())+":"+addZero(createDate.getMinutes())+":"+addZero(createDate.getSeconds()));

		// Create partyIdToDT
		if (typeof listParty == "undefined" || listParty == null || listParty.length <= 0) {
			var listParty = SalesDlvObj.getOrderRoleAndParty (orderId);
			var listPartyFrom = [];
			var listPartyTo = [];
			if (listParty.length > 0) {
				for (var i in listParty) {
					var party = listParty[i];
					if (party.roleTypeId == "BILL_FROM_VENDOR") {
						listPartyFrom.push(party);
					}
					if (party.roleTypeId == "BILL_TO_CUSTOMER") {
						listPartyTo.push(party);
					}
				}
			}
		}

		var partyIdTo = deliveryDT.partyIdTo;
		var partyNameTo = partyIdTo;
		for (var i in listPartyTo) {
			if (partyIdTo == listPartyTo[i].partyId) {
				if (listPartyTo[i].fullName) {
					partyNameTo = listPartyTo[i].fullName;
				} else {
					partyNameTo = listPartyTo[i].groupName;
				}
				break;
			}
		}
		$("#partyIdToDT").text(partyNameTo);

		// Create destContactMechIdDT
		$("#destContactMechIdDT").text(deliveryDT.destAddress);

		// Create originContactMechIdDT
		$("#originContactMechIdDT").text(deliveryDT.originAddress);

		// Create partyIdFromDT
		var partyIdFrom = deliveryDT.partyIdFrom;
		var partyNameFrom = partyIdFrom;
		for (var i in listPartyFrom) {
			if (partyIdFrom == listPartyFrom[i].partyId) {
				if (listPartyFrom[i].groupName) {
					partyNameFrom = listPartyFrom[i].groupName;
				} else {
					partyNameFrom = listPartyFrom[i].fullName;
				}
				break;
			}
		}
		$("#partyIdFromDT").text(partyNameFrom);

		// Create deliveryDateDT
		var deliveryDate = new Date(deliveryDT.deliveryDate);
		var textDlvDate = null;
		if (deliveryDate.getMonth()+1 < 10) {
			if (deliveryDate.getDate() < 10) {
				textDlvDate = "0"+ deliveryDate.getDate() + "/0" + (deliveryDate.getMonth()+1) + "/" + deliveryDate.getFullYear();
			} else {
				textDlvDate = deliveryDate.getDate() + "/0" + (deliveryDate.getMonth()+1) + "/" + deliveryDate.getFullYear();
			}
		} else {
			if (deliveryDate.getDate() < 10) {
				textDlvDate = "0" + deliveryDate.getDate() + "/" + (deliveryDate.getMonth()+1) + "/" + deliveryDate.getFullYear();
			} else {
				textDlvDate = deliveryDate.getDate() + "/" + (deliveryDate.getMonth()+1) + "/" + deliveryDate.getFullYear();
			}
		}
		$("#deliveryDateDT").html("");
		$("#deliveryDateDT").append(textDlvDate + " " + addZero(deliveryDate.getHours())+":"+addZero(deliveryDate.getMinutes())+":"+addZero(deliveryDate.getSeconds()));

		// Create noDT
		if (deliveryDT.no) {
			$("#noDT").text(deliveryDT.no);
		} else {
			$("#noDT").text("_NA_");
		}

		// Create pathScanfile
		var path = "";
		if (deliveryDT.contentId) {
			var x = "deliveryId: &#39;"+deliveryId + "&#39;";
			$("#scanfile").html("");
			$("#scanfile").append("<a style=\"font-size: 14px;\" href=\"javascript:Viewer.open({"+x+"})\" data-rel=\"tooltip\" title="+ uiLabelMap.Scan +" data-placement=\"bottom\" class=\"button-action\"><i class=\"fa fa-file-image-o\"></i></a>");
		} else {
			if ("DLV_EXPORTED" == deliveryDT.statusId || "DLV_DELIVERED" == deliveryDT.statusId) {
				$("#scanfile").html("");
				var x = "deliveryId: &#39;"+deliveryId + "&#39;";
				$("#scanfile").append("<a style=\"font-size: 14px;\" href=\"javascript:Uploader.open({"+x+"})\" data-rel=\"tooltip\" title="+uiLabelMap.Scan+" data-placement=\"bottom\" class=\"button-action\"><i class=\"fa fa-upload\"></i></a>");
			} else {
				$("#scanLabel").html("");
				$("#scanfile").html("");
			}
		}
		
		$("#estimatedStartDateDT").html("");
		var startDate = deliveryDT.estimatedStartDate;
		var tmp1 = new Date(deliveryDT.estimatedStartDate);
		var nowDate1 = new Date();
		if ((typeof(tmp1) != "undefined" && tmp1 != null && !(/^\s*$/.test(tmp1)))) {
			if (tmp1 > nowDate1) {
				$("#actualStartDate").val(nowDate1);
			} else {
				$("#actualStartDate").val(tmp1);
			}
		}

//		if ($("#actualStartDate").val()) {
//			var tmp2 = $("#actualStartDate").jqxDateTimeInput("getDate");
//			$("#actualArrivalDate").val(tmp2);
//		} else {
//			var tmp2 = new Date(deliveryDT.estimatedArrivalDate);
//			$("#actualArrivalDate").val(tmp2);
//		}
		
		var tmp2 = new Date();
		$("#actualArrivalDate").val(tmp2);

		$("#datetimeReceived").val(new Date(deliveryDT.estimatedArrivalDate));
		
		var startDateTemp = startDate.split(" ");
		var startD = startDateTemp[0].split("-");
		var startH = startDateTemp[1].split(":");
		$("#estimatedStartDateDT").append(startD[2]+"/"+startD[1]+"/"+startD[0] + " " + startH[0]+":"+startH[1]);
		
		$("#estimatedArrivalDateDT").html("");
		var arrivalDate = deliveryDT.estimatedArrivalDate;
		if ($("#estimatedArrivalDate").length > 0) {
			if ((new Date) > (new Date(deliveryDT.estimatedArrivalDate))) {
				$("#estimatedArrivalDate").jqxDateTimeInput("val", new Date());
			} else {
				$("#estimatedArrivalDate").jqxDateTimeInput("val", deliveryDT.estimatedArrivalDate);
			}
		}
		var arrivalTemp = arrivalDate.split(" ");
		var arrivalD = arrivalTemp[0].split("-");
		var arrivalH = arrivalTemp[1].split(":");
		$("#estimatedArrivalDateDT").append(arrivalD[2]+"/"+arrivalD[1]+"/"+arrivalD[0] + " " + arrivalH[0]+":"+arrivalH[1]);
		
		if ("DLV_CREATED" == deliveryDT.statusId) {
			$("#cancelDlv").show();
			$("#editDlv").show();
			$("#actualStartDateDis").hide();
			$("#actualArrivalDateDis").hide();
			$("#actualStartDate").show();
			$("#actualArrivalDate").show();
			$("#actualStartDate").jqxDateTimeInput("disabled", true);
			$("#actualArrivalDate").jqxDateTimeInput("disabled", true);
			$("#sendRequestApprove").show();
		} else {
			$("#sendRequestApprove").hide();
		}
		
		if ("DLV_PROPOSED" == deliveryDT.statusId) {
			$("#cancelDlv").show();
			$("#editDlv").show();
			$("#alterApproveAndContinue").show();
			if (perAdmin) {
				$("#titleDetailId > div:first-child").html("");
				$("#titleDetailId > div:first-child").text(uiLabelMap.DeliveryNote + " - " + uiLabelMap.ApproveDelivery);
			} else {
				$("#titleDetailId > div:first-child").html("");
				$("#titleDetailId > div:first-child").text(uiLabelMap.DeliveryNote + " - " + uiLabelMap.WaitForApprove);
			}
		} else {
			$("#alterApproveAndContinue").hide();
		}
		
		if ("DLV_APPROVED" == deliveryDT.statusId) {
			$("#cancelDlv").show();
			$("#editDlv").show();
			$("#titleDetailId > div:first-child").html("");
			$("#titleDetailId > div:first-child").text(uiLabelMap.DeliveryNote + " - " + uiLabelMap.UpdateActualExportedQuantity);
			$("#actualStartDateDis").hide();
			$("#actualStartDate").show();
			$("#actualStartDate").jqxDateTimeInput("disabled", false);
			$("#actualArrivalDate").jqxDateTimeInput("disabled", true);
			$("#addRow").show();
			$("#alterSaveAndContinue").show();
		} else {
			$("#addRow").hide();
			$("#alterSaveAndContinue").hide();
		}
		if ("DLV_EXPORTED" == deliveryDT.statusId) {
			$("#titleDetailId > div:first-child").html("");
			$("#titleDetailId > div:first-child").text(uiLabelMap.DeliveryNote + " - " + uiLabelMap.UpdateActualDeliveredQuantity);
			var date = deliveryDT.actualStartDate;
			actualStartDategl = new Date(date);
			$("#actualArrivalDate").show();
			$("#actualArrivalDate").jqxDateTimeInput("disabled", false);
			$("#actualStartDate").jqxDateTimeInput("val", deliveryDT.actualStartDate);
			$("#actualStartDate").hide();
			$("#actualArrivalDateDis").hide();
			$("#actualStartDateDis").show();
			$("#actualStartDateDis").html("");
			var temp = date.split(" ");
			var d = temp[0].split("-");
			var h = temp[1].split(":");
			$("#actualStartDateDis").append(d[2]+"/"+d[1]+"/"+d[0] + " " + h[0]+":"+h[1]);
			if (listNoteItems.length > 0) {
				$("#orderNote").show();
				$("#orderNote").html("");
				$("#orderNote").append("<a id=\"linkId\" href=\"javascript:SalesDlvObj.showAttachFilePopup()\" onclick=\"\"><i class=\"fa-edit\"></i>"+uiLabelMap.Record+"</a>");
			}
			$("#debtRecord").show();
		}
		if ("DLV_DELIVERED" == deliveryDT.statusId) {
			$("#titleDetailId > div:first-child").html("");
			$("#titleDetailId > div:first-child").text(uiLabelMap.DeliveryNote+ " - " + uiLabelMap.DeliveryDoc);
			$("#actualStartDate").hide();
			$("#actualArrivalDate").hide();
			$("#actualStartDateDis").show();
			$("#actualStartDateDis").html("");
			var date = deliveryDT.actualStartDate;
			var temp = date.split(" ");
			var d = temp[0].split("-");
			var h = temp[1].split(":");
			$("#actualStartDateDis").append(d[2]+"/"+d[1]+"/"+d[0] + " " + h[0]+":"+h[1]);
			$("#actualArrivalDateDis").show();
			$("#actualArrivalDateDis").html("");
			var arrDate = deliveryDT.actualArrivalDate;
			var temp2 = arrDate.split(" ");
			var d2 = temp2[0].split("-");
			var h2 = temp2[1].split(":");
			$("#alterSave2").hide();
			$("#actualArrivalDateDis").append(d2[2]+"/"+d2[1]+"/"+d2[0] + " " + h2[0]+":"+h2[1]);
		}

		var listDeliveryItems = [];
		$.ajax({
			type: "POST",
			url: "getDeliveryItemByDeliveryId",
			data: {"deliveryId": deliveryId},
			dataType: "json",
			async: false,
			success: function (response) {
				listDeliveryItems = response["listDeliveryItems"];
			},
			error: function (response) {
				alert("Error:" + response);
			}
		});
		listDeliveryItemData = $.extend({}, listDeliveryItems);
		if ("DLV_DELIVERED" == deliveryDT.statusId) {
			for (var m in listDeliveryItems) {
				if (listDeliveryItems[m].requireAmount && listDeliveryItems[m].requireAmount == 'Y'){
					listDeliveryItems[m]["actualDeliveredQuantity"] = listDeliveryItems[m].actualExportedAmount;
					listDeliveryItems[m]["actualExportedQuantity"] = listDeliveryItems[m].actualExportedAmount;
				} else {
					listDeliveryItems[m]["actualDeliveredQuantity"] = listDeliveryItems[m].actualExportedQuantity/listDeliveryItems[m].convertNumber;
					listDeliveryItems[m]["actualExportedQuantity"] = listDeliveryItems[m].actualExportedQuantity/listDeliveryItems[m].convertNumber;
					
				}
				if (listDeliveryItems[m].actualExpireDate != null && listDeliveryItems[m].actualExpireDate != undefined) {
					if (listDeliveryItems[m].actualExpireDate.time) {
						listDeliveryItems[m]["actualExpireDate"] = listDeliveryItems[m].actualExpireDate.time;
					}
				}
				if (listDeliveryItems[m].actualManufacturedDate != null && listDeliveryItems[m].actualManufacturedDate != undefined) {
					if (listDeliveryItems[m].actualManufacturedDate.time) {
						listDeliveryItems[m]["actualManufacturedDate"] = listDeliveryItems[m].actualManufacturedDate.time;
					}
				}
			}
		}
		if ("DLV_APPROVED" == deliveryDT.statusId) {
			if (requireLocation && requireLocation === 'Y'){
				if (listInv.length > 0) {
					var arrDlvItem = listDeliveryItems.slice();
					listDeliveryItems = [];
					var listInvTmp1 = [];
					var listInvTmp2 = [];
					for (var d in listInv) {
						if (listInv[d].expireDate === null || listInv[d].expireDate === undefined || listInv[d].expireDate === "" || listInv[d].expireDate === "null") {
							listInvTmp1.push(listInv[d]);
						} else {
							listInvTmp2.push(listInv[d]);
						}
					}
	
					listInvTmp2.sort(function (a,b) {
						return a.expireDate.time - b.expireDate.time;
					});
	
					listInv = [];
					for (var q in listInvTmp2) {
						listInv.push(listInvTmp2[q]);
					}
					for (var p in listInvTmp1) {
						listInv.push(listInvTmp1[p]);
					}
					var listInvOk = [];
					for (var m in arrDlvItem) {
						var convert = arrDlvItem[m].convertNumber;
						arrDlvItem[m]["actualExportedQuantity"] = arrDlvItem[m].quantity/convert;
						
						var item = arrDlvItem[m];
						var reqAmount = false;
						if (item.requireAmount && item.requireAmount == 'Y'){
							reqAmount = true;
						}
						var expQuantity = item.quantity/convert;
						
						if (reqAmount == true){
							expQuantity = item.amount;
							arrDlvItem[m]["actualExportedQuantity"] = item.amount;
						}
						
						var productId = arrDlvItem[m].productId;
						var fst = true;
						var remainQty = expQuantity;
						
						var check = false;
						var locationIdByPicklistItem = item.locationId;
						var inventoryItemIdByPicklistItem = item.inventoryItemId;
						var checkLoc = false;
						if (locationIdByPicklistItem){
							for (var i in listInv) {
								if (locationIdByPicklistItem == listInv[i].locationId && inventoryItemIdByPicklistItem == listInv[i].inventoryItemId){
									checkLoc = true;
								}
							}
						}
						for (var i in listInv) {
							if (checkLoc){
								if (locationIdByPicklistItem != listInv[i].locationId || inventoryItemIdByPicklistItem != listInv[i].inventoryItemId){
									continue;
								}
							}
							if (listInvOk.indexOf(listInv[i].inventoryItemId) > -1) {
								if (!checkLoc){
									continue;
								}
							}
							if (productId == listInv[i].productId) {
								var qoh = listInv[i].quantityOnHandTotal/convert;
								if (reqAmount){
									qoh = listInv[i].amountOnHandTotal;
								}
								if (qoh < 1) {
									check = false;
									continue;
								} else {
									check = true;
								}
								if (qoh <= remainQty) {
									if (fst == true) {
										item["inventoryItemId"] = listInv[i].inventoryItemId;
										item["actualExportedQuantity"] = qoh;
										item["locationCode"] = listInv[i].locationCode;
										item["locationId"] = listInv[i].locationId;
										listInvOk.push(listInv[i].inventoryItemId);
										listDeliveryItems.push(item);
										fst = false;
									} else {
										var newItem = $.extend({}, item);
										delete newItem["deliveryItemSeqId"];
										newItem["inventoryItemId"] = listInv[i].inventoryItemId;
										newItem["actualExportedQuantity"] = qoh;
										newItem["locationCode"] = listInv[i].locationCode;
										newItem["locationId"] = listInv[i].locationId;
										listDeliveryItems.push(newItem);
										listInvOk.push(listInv[i].inventoryItemId);
									}
									remainQty = remainQty - qoh;
								} else {
									if (fst == true) {
										item["inventoryItemId"] = listInv[i].inventoryItemId;
										item["actualExportedQuantity"] = remainQty;
										item["locationCode"] = listInv[i].locationCode;
										item["locationId"] = listInv[i].locationId;
										listDeliveryItems.push(item);
										listInvOk.push(listInv[i].inventoryItemId);
										fst = false;
									} else {
										var newItem = $.extend({}, item);
										delete newItem["deliveryItemSeqId"];
										newItem["inventoryItemId"] = listInv[i].inventoryItemId;
										newItem["actualExportedQuantity"] = remainQty;
										newItem["locationCode"] = listInv[i].locationCode;
										newItem["locationId"] = listInv[i].locationId;
										listDeliveryItems.push(newItem);
										listInvOk.push(listInv[i].inventoryItemId);
									}
									remainQty = 0;
								}
								if (remainQty <= 0) break;
							}
						}
						if (check == false) {
							if (convert != 1){
								expQuantity = expQuantity*convert;
								item["convertNumber"] = 1;
								item["orderQuantityUomId"] = item.quantityUomId;
								convert = 1;
								fst = true;
								remainQty = expQuantity;
								
								var check = true;
								for (var i in listInv) {
									if (listInvOk.indexOf(listInv[i].inventoryItemId) > -1) {
									    continue;
									}
									if (productId == listInv[i].productId) {
										var qoh = listInv[i].quantityOnHandTotal/convert;
										if (item.requireAmount && item.requireAmount == 'Y'){
											qoh = listInv[i].amountOnHandTotal;
										}
										if (qoh < 1) {
											check = false;
											continue;
										} else {
											check = true;
										}
										if (qoh <= remainQty) {
											if (fst == true) {
												item["inventoryItemId"] = listInv[i].inventoryItemId;
												item["actualExportedQuantity"] = qoh;
												item["locationCode"] = listInv[i].locationCode;
												item["locationId"] = listInv[i].locationId;
												listInvOk.push(listInv[i].inventoryItemId);
												listDeliveryItems.push(item);
												fst = false;
											} else {
												var newItem = $.extend({}, item);
												delete newItem["deliveryItemSeqId"];
												newItem["inventoryItemId"] = listInv[i].inventoryItemId;
												newItem["actualExportedQuantity"] = qoh;
												newItem["locationCode"] = listInv[i].locationCode;
												newItem["locationId"] = listInv[i].locationId;
												listDeliveryItems.push(newItem);
												listInvOk.push(listInv[i].inventoryItemId);
											}
											remainQty = remainQty - qoh;
										} else {
											if (fst == true) {
												item["inventoryItemId"] = listInv[i].inventoryItemId;
												item["actualExportedQuantity"] = remainQty;
												item["locationCode"] = listInv[i].locationCode;
												item["locationId"] = listInv[i].locationId;
												listDeliveryItems.push(item);
												listInvOk.push(listInv[i].inventoryItemId);
												fst = false;
											} else {
												var newItem = $.extend({}, item);
												delete newItem["deliveryItemSeqId"];
												newItem["inventoryItemId"] = listInv[i].inventoryItemId;
												newItem["actualExportedQuantity"] = remainQty;
												newItem["locationCode"] = listInv[i].locationCode;
												newItem["locationId"] = listInv[i].locationId;
												listDeliveryItems.push(newItem);
												listInvOk.push(listInv[i].inventoryItemId);
											}
											remainQty = 0;
										}
										if (remainQty <= 0) break;
									}
								}
								if (check == false) {
									listDeliveryItems.push(item);
									listInvOk.push(listInv[i].inventoryItemId);
								}
							}
						}
					}
				}
			} else {
				if (listInv.length > 0) {
					var arrDlvItem = listDeliveryItems.slice();
					listDeliveryItems = [];
					var listInvTmp1 = [];
					var listInvTmp2 = [];
					for (var d in listInv) {
						if (listInv[d].expireDate === null || listInv[d].expireDate === undefined || listInv[d].expireDate === "" || listInv[d].expireDate === "null") {
							listInvTmp1.push(listInv[d]);
						} else {
							listInvTmp2.push(listInv[d]);
						}
					}
	
					listInvTmp2.sort(function (a,b) {
						return a.expireDate.time - b.expireDate.time;
					});
	
					listInv = [];
					for (var q in listInvTmp2) {
						listInv.push(listInvTmp2[q]);
					}
					for (var p in listInvTmp1) {
						listInv.push(listInvTmp1[p]);
					}
					var listInvOk = [];
					for (var m in arrDlvItem) {
						var convert = arrDlvItem[m].convertNumber;
						arrDlvItem[m]["actualExportedQuantity"] = arrDlvItem[m].quantity/convert;
						
						var item = arrDlvItem[m];
						var reqAmount = false;
						if (item.requireAmount && item.requireAmount == 'Y'){
							reqAmount = true;
						}
						var expQuantity = item.quantity/convert;
						
						if (reqAmount == true){
							expQuantity = item.amount;
							arrDlvItem[m]["actualExportedQuantity"] = item.amount;
						}
						
						var productId = arrDlvItem[m].productId;
						var fst = true;
						var remainQty = expQuantity;
						
						var check = false;
						var inventoryItemIdByPicklistItem = item.inventoryItemId;
						var checkInv = false;
						for (var i in listInv) {
							if (inventoryItemIdByPicklistItem == listInv[i].inventoryItemId){
								var qohTmp = listInv[i].quantityOnHandTotal/convert;
								if (reqAmount){
									qohTmp = listInv[i].amountOnHandTotal;
								}
								if (qohTmp >= remainQty){
									checkInv = true;
								}
							}
						}
						for (var i in listInv) {
							if (checkInv){
								if (inventoryItemIdByPicklistItem != listInv[i].inventoryItemId){
									continue;
								}
							}
							if (listInvOk.indexOf(listInv[i].inventoryItemId) > -1) {
								if (!checkInv){
									for (var g in listDeliveryItems) {
										if (listDeliveryItems[g].inventoryItemId == listInv[i].inventoryItemId && listDeliveryItems[g].isPromo == item.isPromo){
											continue;
										}
									}
								}
							}
							if (productId == listInv[i].productId) {
								var qohTmp = 0;
								for (var g in listDeliveryItems) {
									if (listDeliveryItems[g].inventoryItemId == listInv[i].inventoryItemId){
										qohTmp = qohTmp + listDeliveryItems[g].actualExportedQuantity;
									}
								}
								var qoh = listInv[i].quantityOnHandTotal/convert - qohTmp;
								if (reqAmount){
									qoh = listInv[i].amountOnHandTotal;
								}
								if (qoh < 1) {
									check = false;
									continue;
								} else {
									check = true;
								}
								if (qoh <= remainQty) {
									if (fst == true) {
										item["inventoryItemId"] = listInv[i].inventoryItemId;
										item["actualExportedQuantity"] = qoh;
										listInvOk.push(listInv[i].inventoryItemId);
										listDeliveryItems.push(item);
										fst = false;
									} else {
										var newItem = $.extend({}, item);
										delete newItem["deliveryItemSeqId"];
										newItem["inventoryItemId"] = listInv[i].inventoryItemId;
										newItem["actualExportedQuantity"] = qoh;
										listDeliveryItems.push(newItem);
										listInvOk.push(listInv[i].inventoryItemId);
									}
									remainQty = remainQty - qoh;
								} else {
									if (fst == true) {
										item["inventoryItemId"] = listInv[i].inventoryItemId;
										item["actualExportedQuantity"] = remainQty;
										listDeliveryItems.push(item);
										listInvOk.push(listInv[i].inventoryItemId);
										fst = false;
									} else {
										var newItem = $.extend({}, item);
										delete newItem["deliveryItemSeqId"];
										newItem["inventoryItemId"] = listInv[i].inventoryItemId;
										newItem["actualExportedQuantity"] = remainQty;
										listDeliveryItems.push(newItem);
										listInvOk.push(listInv[i].inventoryItemId);
									}
									remainQty = 0;
								}
								if (remainQty <= 0) break;
							}
						}
						if (check == false) {
							if (convert != 1){
								expQuantity = expQuantity*convert;
								item["convertNumber"] = 1;
								item["orderQuantityUomId"] = item.quantityUomId;
								convert = 1;
								fst = true;
								remainQty = expQuantity;
								
								var check = true;
								for (var i in listInv) {
									if (listInvOk.indexOf(listInv[i].inventoryItemId) > -1) {
									    continue;
									}
									if (productId == listInv[i].productId) {
										var qoh = listInv[i].quantityOnHandTotal/convert;
										if (item.requireAmount && item.requireAmount == 'Y'){
											qoh = listInv[i].amountOnHandTotal;
										}
										if (qoh < 1) {
											check = false;
											continue;
										} else {
											check = true;
										}
										if (qoh <= remainQty) {
											if (fst == true) {
												item["inventoryItemId"] = listInv[i].inventoryItemId;
												item["actualExportedQuantity"] = qoh;
												listInvOk.push(listInv[i].inventoryItemId);
												listDeliveryItems.push(item);
												fst = false;
											} else {
												var newItem = $.extend({}, item);
												delete newItem["deliveryItemSeqId"];
												newItem["inventoryItemId"] = listInv[i].inventoryItemId;
												newItem["actualExportedQuantity"] = qoh;
												listDeliveryItems.push(newItem);
												listInvOk.push(listInv[i].inventoryItemId);
											}
											remainQty = remainQty - qoh;
										} else {
											if (fst == true) {
												item["inventoryItemId"] = listInv[i].inventoryItemId;
												item["actualExportedQuantity"] = remainQty;
												listDeliveryItems.push(item);
												listInvOk.push(listInv[i].inventoryItemId);
												fst = false;
											} else {
												var newItem = $.extend({}, item);
												delete newItem["deliveryItemSeqId"];
												newItem["inventoryItemId"] = listInv[i].inventoryItemId;
												newItem["actualExportedQuantity"] = remainQty;
												listDeliveryItems.push(newItem);
												listInvOk.push(listInv[i].inventoryItemId);
											}
											remainQty = 0;
										}
										if (remainQty <= 0) break;
									}
								}
								if (check == false) {
									listDeliveryItems.push(item);
									listInvOk.push(listInv[i].inventoryItemId);
								}
							}
						}
					}
				}
			}
		}
		if ("DLV_EXPORTED" == deliveryDT.statusId) {
			for (var m in listDeliveryItems) {
				if (listDeliveryItems[m].requireAmount && listDeliveryItems[m].requireAmount == 'Y'){
					listDeliveryItems[m]["actualDeliveredQuantity"] = listDeliveryItems[m].actualExportedAmount;
					listDeliveryItems[m]["actualExportedQuantity"] = listDeliveryItems[m].actualExportedAmount;
					listDeliveryItems[m]["debtQuantity"] = 0;
				} else {
					listDeliveryItems[m]["actualDeliveredQuantity"] = listDeliveryItems[m].actualExportedQuantity/listDeliveryItems[m].convertNumber;
					listDeliveryItems[m]["actualExportedQuantity"] = listDeliveryItems[m].actualExportedQuantity/listDeliveryItems[m].convertNumber;
					listDeliveryItems[m]["debtQuantity"] = 0;
				}
				if (listDeliveryItems[m].actualExpireDate != null && listDeliveryItems[m].actualExpireDate != undefined) {
					if (listDeliveryItems[m].actualExpireDate.time) {
						listDeliveryItems[m]["actualExpireDate"] = listDeliveryItems[m].actualExpireDate.time;
					}
				}
				if (listDeliveryItems[m].actualManufacturedDate != null && listDeliveryItems[m].actualManufacturedDate != undefined) {
					if (listDeliveryItems[m].actualManufacturedDate.time) {
						listDeliveryItems[m]["actualManufacturedDate"] = listDeliveryItems[m].actualManufacturedDate.time;
					}
				}
			}
		}
		// auto fill by date
		if (listDeliveryItems.length <= 0){
			listDeliveryItems = listDeliveryItemData;
		}
		loadDeliveryItem(listDeliveryItems);
		if ("DLV_EXPORTED" == deliveryDT.statusId) {
			loadDebtGrid(listDeliveryItems);
		}
		// Open Window
		$("#popupDeliveryDetailWindow").jqxWindow("open");
		
		$("#facilityReturnId").val(deliveryDT.originFacilityId);
	}
	
	function approveDelivey() {
		bootbox.dialog(uiLabelMap.AreYouSureApprove, 
		[{"label": uiLabelMap.CommonCancel, 
			"icon": "fa fa-remove", "class": "btn  btn-danger form-action-button pull-right",
			"callback": function () {bootbox.hideAll();}
		}, 
		{"label": uiLabelMap.Approve,
			"icon": "fa-check", "class": "btn btn-primary form-action-button pull-right",
			"callback": function () {
				Loading.show("loadingMacro");
				setTimeout(function () {
					var dlvId = glDeliveryId;
					if (dlvId) {
						$.ajax({
							type: "POST",
							url: "updateDelivery",
							data: {"deliveryId": dlvId,
								"statusId": "DLV_APPROVED",
							},
							dataType: "json",
							async: false,
							success: function (res) {
								if (res._ERROR_MESSAGE_ == "STOREKEEPER_NOT_FOUND") {
									$("#notifyIdNotHaveStorekeeper").jqxNotification("open");
									$("#popupDeliveryDetailWindow").jqxWindow("close");
								} else {
									if (checkContinue == true) {
										showDetailPopup(dlvId, glOrderId);
									} else {
										$("#popupDeliveryDetailWindow").jqxWindow("close");
									}
								}
							},
							error: function (response) {
								alert("Error:" + response);
							}
						});
					}
					Loading.hide("loadingMacro");	
				}, 500);
			}
		}]);
	}
	
	function addNewRow() {
		var firstRow = $("#jqxgridDlvItem").jqxGrid("getrowdata", 0);
		var selectedIndexs = $("#jqxgridDlvItem").jqxGrid("getselectedrowindexes");
		if (firstRow.productCode) {
			$("#jqxgridDlvItem").jqxGrid("clearselection");
			var datarow = generaterow();
			$("#jqxgridDlvItem").jqxGrid("addrow", null, datarow, "first");
			$("#jqxgridDlvItem").jqxGrid("unselectrow", 0);
			for (var i in selectedIndexs) {
				$("#jqxgridDlvItem").jqxGrid("selectrow", selectedIndexs[i] + 1);
			}
			$("#jqxgridDlvItem").jqxGrid("begincelledit", 0, "productCode");
		} else {
			$("#jqxgridDlvItem").jqxGrid("begincelledit", 0, "productCode");
		}
	}
	
	function generaterow(productCode, selectedAmount) {
		var row = {};
		if (productCode) {
			var listSames = [];
			if (selectedAmount) {
				for (var i in listDeliveryItemData) {
					var item = listDeliveryItemData[i];
					if (item.productCode == productCode && item.selectedAmount == selectedAmount) {
						listSames.push(item);
					}
				}
			} else {
				for (var x in listDeliveryItemData) {
					var item = listDeliveryItemData[x];
					if (item.productCode == productCode) {
						listSames.push(item);
					}
				}
			}

			if (listSames.length > 0) {
				var dlvItem = listSames[0];
				row["productId"] = dlvItem.productId;
				row["productCode"] = dlvItem.productCode;
				row["productName"] = dlvItem.productName;
				row["fromOrderId"] = dlvItem.fromOrderId;
				row["fromOrderItemSeqId"] = dlvItem.fromOrderItemSeqId;
				row["inventoryItemId"] = null;
				row["deliveryId"] = dlvItem.deliveryId;
				row["deliveryItemSeqId"] = null;
				row["actualExportedQuantity"] = 0;
				row["actualDeliveredQuantity"] = 0;
				row["quantity"] = dlvItem.quantity;
				row["quantityUomId"] = dlvItem.quantityUomId;
				row["statusId"] = "DELI_ITEM_APPROVED";
				row["isPromo"] = dlvItem.isPromo;
				row["batch"] = null;
				row["expireDate"] = null;
				row["deliveryStatusId"] = null;
				row["weight"] = null;
				row["productWeight"] = dlvItem.productWeight;
				row["weightUomId"] = dlvItem.weightUomId;
				row["defaultWeightUomId"] = dlvItem.defaultWeightUomId;
				row["orderQuantityUomId"] = dlvItem.orderQuantityUomId;
				row["convertNumber"] = dlvItem.convertNumber;
				row["selectedAmount"] = dlvItem.selectedAmount;
				row["requireAmount"] = dlvItem.requireAmount;
				row["locationCode"] = null;
				row["locationId"] = null;
			}
		} else {
			row["productId"] = "";
			row["productCode"] = "";
			row["productName"] = "";
			row["fromOrderId"] = "";
			row["fromOrderItemSeqId"] = "";
			row["inventoryItemId"] = "";
			row["deliveryId"] = "";
			row["deliveryItemSeqId"] = "";
			row["actualExportedQuantity"] = "";
			row["actualDeliveredQuantity"] = "";
			row["quantity"] = "";
			row["quantityUomId"] = "";
			row["statusId"] = "";
			row["isPromo"] = "";
			row["batch"] = "";
			row["expireDate"] = "";
			row["deliveryStatusId"] = "";
			row["weight"] = "";
			row["productWeight"] = "";
			row["weightUomId"] = "";
			row["defaultWeightUomId"] = "";
			row["orderQuantityUomId"] = "";
			row["convertNumber"] = "";
			row["selectedAmount"] = "";
			row["requireAmount"] = "";
			row["locationCode"] = "";
			row["locationId"] = "";
		}
		return row;
	}
	
	function updateRowData(productCode, selectedAmount) {
		var datarow = generaterow(productCode, selectedAmount);
		var id = $("#jqxgridDlvItem").jqxGrid("getrowid", 0);
		$("#jqxgridDlvItem").jqxGrid("updaterow", id, datarow);
	}
	
	var customMess = function () {
		return uiLabelMap.DeliveryNoteIdExisted;
	}
	
	function getInventory(orderId, facilityId, deliveryId) {
		$.ajax({
			type: "POST",
			url: "getINVByOrderAndDlv",
			data: {"orderId": orderId, "facilityId": facilityId, "deliveryId": deliveryId},
			dataType: "json",
			async: false,
			success: function (response) {
				listInv = response.listData;
			},
			error: function (response) {
				alert("Error:" + response);
			}
		});
	}
	
	var getDeliveryAndInv = function getDeliveryAndInv(deliveryId, checkLabel, orderId) {
		var deliveryTmp;
		$.ajax({
			type: "POST",
			url: "getDeliveryById",
			data: {"deliveryId": deliveryId},
			dataType: "json",
			async: false,
			success: function (response) {
				deliveryTmp = response;
				$.ajax({
					type: "POST",
					url: "getINVByOrderAndDlv",
					data: {"orderId": orderId, "facilityId":deliveryTmp.originFacilityId, "deliveryId": deliveryTmp.deliveryId, "checkLabel": checkLabel},
					dataType: "json",
					async: false,
					success: function (response) {
						listInv = response.listData;
					},
					error: function (response) {
						alert("Error:" + response);
					}
				});
			},
			error: function (response) {
				alert("Error:" + response);
			}
		});
		return deliveryTmp;
	};
	
	var getOrderRoleAndParty = function (orderId) {
		var listPartyTmp = []; 
		$.ajax({
			type: "POST",
			url: "getOrderRoleAndParty",
			data: {
				orderId: orderId,
			},
			async: false,
			success: function (res) {
				listPartyTmp = res["listParties"];
			}
		});
		return listPartyTmp;
	};
	
	var saveEditDelivery = function (){
		$("#editGrid").jqxGrid("clearfilters");
		var allRows = $("#editGrid").jqxGrid("getrows");
		var listDeliveryItemTmps = [];
		for (var i in allRows) {
			var data = allRows[i];
			delete data["productName"];
			if (data.convertNumber) {
				var quantity = data.newQuantity*data.convertNumber;
				data.quantity = quantity;
			} else {
				data.quantity = data.newQuantity;
			}
			data.deliveryId = glDeliveryId;
			listDeliveryItemTmps.push(data);
		}
		listDeliveryItemTmps = JSON.stringify(listDeliveryItemTmps);
		$.ajax({
			type: "POST",
			url: "updateDeliveryItemInfos",
			data: {
				listDeliveryItems: listDeliveryItemTmps,
			},
			async: false,
			success: function (res) {
				if (!res._ERROR_MESSAGE_) {
					Grid.renderMessage("jqxNotification", uiLabelMap.UpdateSuccessfully, {
						autoClose : true,
						template : "info",
						appendContainer : "#container",
						opacity : 0.9
					});
				} else {
					Grid.renderMessage("jqxNotification", uiLabelMap.UpdateError, {
						autoClose : true,
						template : "error",
						appendContainer : "#container",
						opacity : 0.9
					});
				}
				$("#editWindow").jqxWindow('close');
				showDetailPopup(glDeliveryId, glOrderId); 
			}
		});
	};
	
	var editAddNewProduct = function (){
		loadProductNotExportedYet();
		$("#editAddProductWindow").jqxWindow('open');
	};
	
	var loadProductNotExportedYet = function (){
		if (listProductToAdd.length <= 0) {
			var listProducts = [];
			$.ajax({
				type: "POST",
				url: "getProductNotExportedYet",
				data: {
					deliveryId: glDeliveryId,
				},
				async: false,
				success: function (res) {
					listProducts = res.listProducts;
					listProductToAdd = listProducts;
					var listData = [];
					for (var x in listProducts) {
						var obj = $.extend({}, listProducts[x]);
						if (listProducts[x].convertNumber) {
							obj.quantity = listProducts[x].quantity/listProducts[x].convertNumber;
							obj.newQuantity = listProducts[x].quantity/listProducts[x].convertNumber;
							obj.createdQuantity = listProducts[x].createdQuantity/listProducts[x].convertNumber;
							listData.push(obj);
							listProductToAdd.push(obj);
						}
					}
					loadEditAddProductGrid(listData);
				}
			});
		} else {
			var listProducts = [];
			for (var x in listProductToAdd){
				if (listProductToAdd[x].quantity > 0){
					listProducts.push(listProductToAdd[x]);
				}
			}
			loadEditAddProductGrid(listProducts);
		}
	};
	
	var initFacilityGrid = function(grid){
		if (!productStoreId){
			var url = "jqGetFacilities";
		} else {
			var url = "jqGetFacilities&productStoreId=" + productStoreId + "&facilityGroupId=FACILITY_INTERNAL"; 
		}
		var datafield =  [
			{name: 'facilityId', type: 'string'},
			{name: 'facilityCode', type: 'string'},
			{name: 'facilityName', type: 'string'},
      	];
      	var columnlist = [
				{text: uiLabelMap.BLFacilityId, datafield: 'facilityCode', width: '20%', pinned: true, classes: 'pointer',
					cellsrenderer: function (row, column, value) {
						if (!value) {
							var data = grid.jqxGrid('getrowdata', row);
							value = data.facilityId;
						}
				        return '<div style="cursor:pointer;">' + (value) + '</div>';
				    }
				},
				{text: uiLabelMap.FacilityName, datafield: 'facilityName', width: '80%',
					cellsrenderer: function (row, column, value) {
				        return '<div style="cursor:pointer;">' + (value) + '</div>';
				    }
				},
      	];
      	
      	var config = {
  			width: 500, 
	   		virtualmode: true,
	   		showtoolbar: false,
	   		selectionmode: 'singlerow',
	   		pageable: true,
	   		sortable: true,
	        filterable: true,	        
	        editable: false,
	        rowsheight: 26,
	        useUrl: true,
	        url: url,                
	        source: {pagesize: 10}
      	};
      	Grid.initGrid(config, datafield, columnlist, null, grid);
	};
	
	var showDetailDelivery = function (deliveryId){
		location.replace("deliverySalesDeliveryDetail?deliveryId="+deliveryId);
	}
	
	var showDetailDeliveryDistributor = function (deliveryId){
		location.replace("deliverySalesDeliveryDetailDis?deliveryId="+deliveryId);
	}
	
	return {
		init: init,
		showDetailPopup: showDetailPopup,
		getOrderRoleAndParty: getOrderRoleAndParty,
		afterAddDelivery: afterAddDelivery,
		showPopupSelectFacility: showPopupSelectFacility,
		showAttachFilePopup: showAttachFilePopup,
		updateTotalWeight: updateTotalWeight,
		getFormattedDate: getFormattedDate,
		showOrderNotePopup: showOrderNotePopup,
		getFacilityList: getFacilityList,
		addOriginFacilityAddress: addOriginFacilityAddress,
		selectFacility: selectFacility,
		formatFullDate: formatFullDate,
		removeScanFile: removeScanFile,
		addNewRow: addNewRow,
		updateRowData: updateRowData,
		removeExptScanFile: removeExptScanFile,
		showAttachExptFilePopup: showAttachExptFilePopup,
		customMess: customMess,
		editAddNewProduct: editAddNewProduct,
		showDetailDelivery: showDetailDelivery,
		showDetailDeliveryDistributor: showDetailDeliveryDistributor,
	};
}());