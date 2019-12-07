$(function(){
	FastSalesDlvObj.init();
});
var FastSalesDlvObj = (function(){
	var init = function(){
		initInputs();
		initElementComplex();
		initEvents();
		initValidateForm();
		initValidateDatetimeDT($("#popupDeliveryDetailWindow"));
	};
	var initInputs = function(){
		$("#popupDeliveryDetailWindow").jqxWindow({
		    maxWidth: 1500, minWidth: 950, width: 1300, modalZIndex: 10000, zIndex:10000, minHeight: 500, height: 580, maxHeight: 670, resizable: false, cancelButton: $("#fastCancel"), keyboardNavigation: true, keyboardCloseKey: 15,  isModal: true, autoOpen: false, modalOpacity: 0.7, theme:theme           
		});
		
		$('#actualArrivalDate').jqxDateTimeInput({width: 200, disabled: true, formatString: 'dd/MM/yyyy HH:mm:ss'});
		$('#actualStartDate').jqxDateTimeInput({width: 200, disabled: true, formatString: 'dd/MM/yyyy HH:mm:ss'});
	};
	var initElementComplex = function(){
	};
	
	var initEvents = function(){
		$('#actualArrivalDate').on('change', function (event) 
		{  	
			if (!$('#popupDeliveryDetailWindow').jqxValidator('validate')){
				return false;
			}
		});
		
		$('#actualStartDate').on('change', function (event) 
		{  	
			if (!$('#popupDeliveryDetailWindow').jqxValidator('validate')){
				return false;
			}
		});
		
		$("#fastSave").click(function () {
			saveDelivery();
		});
		
		$("#fastCancel").click(function () {
		    $("#popupDeliveryDetailWindow").jqxWindow('close');
		});
		
		
		$('#popupDeliveryDetailWindow').on('close', function (event) {
			saveClick = 0;
			$('#actualArrivalDateDis').hide();
			$('#actualStartDateDis').hide();
			$('#popupDeliveryDetailWindow').jqxValidator('hide');
			var newStatusId;
			$.ajax({
				type: "POST",
				url: "checkOrderStatus",
				data: {
					orderId: orderId,
				},
				async: false,
				success: function (res){
					newStatusId = res.statusId;
				}
			});
			if (newStatusId != orderStatus){
				window.location.replace("viewOrder?orderId="+orderId);
			}
			$("#quickCreateDlv").attr("href", "javascript:FastSalesDlvObj.showDetailPopup("+glDeliveryId+")");
		});
		
		$('#popupDeliveryDetailWindow').on('open', function (event) {
			saveClick = 0;
			if (!$('#popupDeliveryDetailWindow').jqxValidator('validate')){
				return false;
			}
		});
	};
	
	function saveDelivery(){
		if("DLV_DELIVERED" == glDeliveryStatusId){
		} else {
			if (!$('#popupDeliveryDetailWindow').jqxValidator('validate')){
				return false;
			}
			var row;
	        // Get List Delivery Item
			var selectedIndexs = [];
	        var allRows = $('#jqxgridDlvItem').jqxGrid('getrows');
	        for (var id = 0; id < allRows.length; id ++){
	        	selectedIndexs.push(allRows[id].uid);
	        }
	        for(var id = 0; id < selectedIndexs.length; id++){
	            if(checkGridDeliveryItemRequiredData(selectedIndexs[id]) == true){
	                return false;
	            }
	        }
	        if("DLV_EXPORTED" == glDeliveryStatusId){
	        	var listMissing = [];
	        	for (var j = 0; j < selectedIndexs.length; j ++){
	     			var data = $('#jqxgridDlvItem').jqxGrid('getrowdata', selectedIndexs[j]);
	     			if (data.actualExportedQuantity != data.actualDeliveredQuantity){
	     				listMissing.push(data);
	     			}
	     		}
	    		var show = false;
	    		if (listNoteItems.length <= 0){
	    			show = true;
	    		} else {
		      		for (var i = 0; i < listMissing.length; i ++){
	        			for (var j = 0; j < listNoteItems.length; j ++){
	        				if (listMissing[i].deliveryItemSeqId == listNoteItems[j].deliveryItemSeqId){
	        					if (listMissing[i].actualDeliveredQuantity != listNoteItems[j].actualDeliveredQuantity){
	        						show = true;
	        						break;
	        					}
	        				}
	        			}
	            	}
	        	}
	        	
	        	if (listMissing.length > 0 && show == true){
					bootbox.dialog(uiLabelMap.SomeProductHasMissingQuantity + ". " + uiLabelMap.Click + " \"" + uiLabelMap.OK + "\" " + uiLabelMap.orderTo + " " + uiLabelMap.updateReasonNoteForOrder + " " + uiLabelMap.or + " " + uiLabelMap.click + " \"" + uiLabelMap.CommonCancel + "\" " + uiLabelMap.orderTo + " " + uiLabelMap.backToEditDelivery + ".", 
					[{"label": uiLabelMap.CommonCancel, 
						"icon": 'fa fa-remove', "class": 'btn  btn-danger form-action-button pull-right',
			            "callback": function() {bootbox.hideAll();}
			        }, 
			        {"label": uiLabelMap.OK,
			            "icon": 'fa-check', "class": 'btn btn-primary form-action-button pull-right',
			            "callback": function() {
			            	for (var m = 0; m < listMissing.length; m ++){
	                    		listMissing[m]["quantity"] = listMissing[m].actualExportedQuantity - listMissing[m].actualDeliveredQuantity; 
	                    	}
	                    	loadNoteGrid(listMissing);
	                    	$("#noteWindow").jqxWindow("open");
			            }
					}]);
	        		return false;
	        	}
	        }
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
	            		var listDeliveryItems = [];
	            		var listItemTmps = [];
	            	    var curDeliveryId = null;
	            		for (var i = 0; i < selectedIndexs.length; i++){
	            			var data1 = $('#jqxgridDlvItem').jqxGrid('getrowdata', selectedIndexs[i]);
	                        var map1 = {};
	                        if(data1.statusId == 'DELI_ITEM_APPROVED'){
	                            if(data1.inventoryItemId == null && data1.actualExportedQuantity > 0){
	                                bootbox.dialog(uiLabelMap.DItemMissingFieldsExp, [{
	                                    "label" : uiLabelMap.OK,
	                                    "class" : "btn btn-primary standard-bootbox-bt",
	                                    "icon" : "fa fa-check",
	                                    }]
	                                );
	                                return false;
	                            }
	                        }
	                        if (data1 != undefined){
		                        map1["fromOrderId"] = data1.fromOrderId;
		                        map1["fromOrderItemSeqId"] = data1.fromOrderItemSeqId;
		                        map1["inventoryItemId"] = data1.inventoryItemId;
		                        map1["deliveryId"] = data1.deliveryId;
		                        map1["deliveryItemSeqId"] = data1.deliveryItemSeqId;
		                        map1["actualExportedQuantity"] = data1.actualExportedQuantity;
		                        map1["actualDeliveredQuantity"] = data1.actualDeliveredQuantity;
		                        map1["productId"] = data1.productId;
		                        map1["isPromo"] = data1.isPromo;
		                        curDeliveryId = data1.deliveryId;
		                        listItemTmps.push(map1);
		                        listDeliveryItems.push(map1);
	                        }
	                    }
	            		if ("DLV_APPROVED" == glDeliveryStatusId){
	            			listDeliveryItems = [];
	            			var listDuplicate = [];
		            		var listInvSelected = [];
		            		if (listItemTmps.length > 0){
		            			for (var em = 0; em < listItemTmps.length; em ++){
		            				var checkTmp = false;
		            				for (var anh = 0; anh < listInvSelected.length; anh ++){
		            					if (listInvSelected[anh].inventoryItemId == listItemTmps[em].inventoryItemId && listInvSelected[anh].orderItemSeqId == listItemTmps[em].fromOrderItemSeqId){
		            						checkTmp = true;
		            						break;
		            					}
		            				}
		            				if (checkTmp == false){
		            					var mapInvItem = {
		            							orderItemSeqId: listItemTmps[em].fromOrderItemSeqId,
		            							inventoryItemId: listItemTmps[em].inventoryItemId,
		            					};
		            					listInvSelected.push(mapInvItem);
		            				}
		            			}
		            		}
		            		for (var iId = 0; iId < listInvSelected.length; iId ++){
		            			var mapInvItemTmp = listInvSelected[iId];
		            			var invIdTmp = mapInvItemTmp.inventoryItemId;
		            			var orderItemSeqIdTmp = mapInvItemTmp.orderItemSeqId;
		            			var listDuplicated = [];
		            			var mapTotal;
		            			for (var jId = 0; jId < listItemTmps.length; jId ++){
		            				if (listItemTmps[jId].inventoryItemId == invIdTmp && listItemTmps[jId].isPromo == "N" && listItemTmps[jId].fromOrderItemSeqId == orderItemSeqIdTmp){
		            					mapTotal = listItemTmps[jId];
		            					listDuplicated.push(listItemTmps[jId]);
		            				}	
		            			}
		            			if (listDuplicated.length > 1){
		            				var totalExported = 0;
		            				for (var u = 0; u < listDuplicated.length; u++){
		            					totalExported = totalExported + listDuplicated[u].actualExportedQuantity;
		            				}
		            				mapTotal["actualExportedQuantity"] = totalExported;
		            				listDeliveryItems.push(mapTotal);
		            			} else if (listDuplicated.length == 1){
		            				listDeliveryItems.push(mapTotal);
		            			}
		            			
		            			var listDuplicated2 = [];
		            			var mapTotal2;
		            			for (var jId = 0; jId < listItemTmps.length; jId ++){
		            				if (listItemTmps[jId].inventoryItemId == invIdTmp && listItemTmps[jId].isPromo == "Y" && listItemTmps[jId].fromOrderItemSeqId == orderItemSeqIdTmp){
		            					mapTotal2 = listItemTmps[jId];
		            					listDuplicated2.push(listItemTmps[jId]);
		            				}	
		            			}
		            			if (listDuplicated2.length > 1){
		            				var totalExported2 = 0;
		            				for (var u = 0; u < listDuplicated2.length; u++){
		            					totalExported2 = totalExported2 + listDuplicated2[u].actualExportedQuantity;
		            				}
		            				mapTotal2["actualExportedQuantity"] = totalExported2;
		            				listDeliveryItems.push(mapTotal2);
		            			} else if (listDuplicated2.length == 1){
		            				listDeliveryItems.push(mapTotal2);
		            			}
		            		}
		            		var tmp = $('#actualStartDate').jqxDateTimeInput('getDate');
	                    	if (tmp){
	                    		actualStartDateTmp = tmp.getTime();
	                    	}
	            		}
	                    $('#jqxgridDlvItem').jqxGrid('showloadelement');
	                    var listDeliveryItems = JSON.stringify(listDeliveryItems);
	                    var actualStartDateTmp;
	                    var actualArrivalDateTmp;
	                    if ("DLV_EXPORTED" == glDeliveryStatusId){
	                    	var tmp = actualArrivalDateTmp = $('#actualArrivalDate').jqxDateTimeInput('getDate');
	                    	if (tmp){
	                    		actualArrivalDateTmp = tmp.getTime();
	                    	}
	                    }
	                    row = { 
	                            listDeliveryItems:listDeliveryItems,
//	                            pathScanFile: pathScanFile,
//	                            pathScanFileExpt: pathScanFileExpt,
	                            deliveryId: curDeliveryId,
	                            actualStartDate: actualStartDateTmp,
	                        	actualArrivalDate: actualArrivalDateTmp,
	                          };
	                    // call Ajax request to Update Exported or Delivered value
	                    if (saveClick == 0){
                    		saveClick = saveClick + 1;
                    		$.ajax({
		    					 type: "POST",
		    					 url: "updateDeliveryItemList",
		    					 data: row,
		    					 dataType: "json",
		    					 async: false,
		    					 success: function(data){
		    					 },
		    					 error: function(response){
		    					 }
                    		});
                    		$("#popupDeliveryDetailWindow").jqxWindow('close');
	                    } else {
							return false;
						} 
	            	}, 500);
	            }
			}]);
		}
	}
	
	var getFormattedDate = function getFormattedDate(date) {
		  var year = date.getFullYear();
		  var month = (1 + date.getMonth()).toString();
		  month = month.length > 1 ? month : '0' + month;
		  var day = date.getDate().toString();
		  day = day.length > 1 ? day : '0' + day;
		  return day + '/' + month + '/' + year;
	}
	
	var getLocalization = function () {
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
	
	function renderHtml(data, key, value, id){
		var y = "";
		var source = new Array();
		var index = 0;
		for (var x in data){
			index = source.length;
			var row = {};
			row[key] = data[x][key];
			row['description'] = data[x][value];
			source[index] = row;
		}
		if($("#"+id).length){
			$("#"+id).jqxDropDownList('clear');
			$("#"+id).jqxDropDownList({source: source, selectedIndex: 0});
		}
	}
    function update(jsonObject, url, data, key, value, id) {
	    jQuery.ajax({
	        url: url,
	        type: "POST",
	        data: jsonObject,
	        async: false,
	        success: function(res) {
	        	var json = res[data];
	            renderHtml(json, key, value, id);
	        }
	    });
	}
	var initValidateForm = function(){
	};
	
	var quickCreateDelivery = function quickCreateDelivery(orderId){
		bootbox.dialog(uiLabelMap.QuickCreateDeliveryNoteFromConsignistaFacility + " " + uiLabelMap.AreYouSureCreate, 
		[{"label": uiLabelMap.CommonCancel, 
			"icon": 'fa fa-remove', "class": 'btn  btn-danger form-action-button pull-right',
            "callback": function() {bootbox.hideAll();}
        }, 
        {"label": uiLabelMap.OK,
            "icon": 'fa-check', "class": 'btn btn-primary form-action-button pull-right',
            "callback": function() {
            	Loading.show('loadingMacro');
            	setTimeout(function(){
			    	var dlvId = null;
					jQuery.ajax({
				        url: "quickCreateDelivery",
				        type: "POST",
				        async: false,
				        data: {
				        	orderId: orderId,
				        },
				        success: function(res) {
				        	dlvId = res.deliveryId;
				        },
				    });
					showDetailPopup(dlvId);
					Loading.hide('loadingMacro');
            	}, 500);
        	}
        }]);
	}
	
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
	var showDetailPopup = function showDetailPopup(deliveryId){
		var deliveryDT;
		glDeliveryId = deliveryId;
		// Cache delivery
        $.ajax({
               type: "POST",
               url: "getDeliveryById",
               data: {'deliveryId': deliveryId},
               dataType: "json",
               async: false,
               success: function(response){
                   deliveryDT = response;
                   $.ajax({
                       type: "POST",
                       url: "getINVByOrderAndDlv",
                       data: {'orderId': orderId, 'facilityId':deliveryDT.originFacilityId, 'deliveryId': deliveryDT.deliveryId},
                       dataType: "json",
                       async: false,
                       success: function(response){
                           listInv = response.listData;
                       },
                       error: function(response){
                         alert("Error:" + response);
                       }
                   });
               },
               error: function(response){
                 alert("Error:" + response);
               }
        });
		$("#deliveryIdDT").text(deliveryDT.deliveryId);
		glDeliveryStatusId = deliveryDT.statusId;
		var stName = null;
        for( i = 0; i < statusData.length; i++){
            if(statusData[i].statusId == deliveryDT.statusId){
                stName = statusData[i].description;
            }
        }
        if (stName){
        	$("#statusIdDT").text(stName);
        } else {
        	$("#statusIdDT").text("_NA_");
        }
		// Create orderIdDT
        if (deliveryDT.orderId){
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
		$('#createDateDT').text(formatFullDate(createDate));
		
		// Create partyIdToDT
		var partyIdTo = deliveryDT.partyIdTo;
		var partyNameTo = partyIdTo;
		for(var i = 0; i < listPartyTo.length; i++){
			if(partyIdTo == listPartyTo[i].partyId){
				if (listPartyTo[i].fullName){
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
		for(var i = 0; i < listPartyFrom.length; i++){
			if(partyIdFrom == listPartyFrom[i].partyId){
				if (listPartyFrom[i].groupName){
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
		$('#deliveryDateDT').text(formatFullDate(deliveryDate));
		
		// Create noDT
		if (deliveryDT.no){
        	$("#noDT").text(deliveryDT.no);
        } else {
        	$("#noDT").text("_NA_");
        }
		
		var startDate = deliveryDT.estimatedStartDate;
		$('#estimatedStartDateDT').text(formatFullDate(new Date(startDate)));
		
		var arrivalDate = deliveryDT.estimatedArrivalDate;
		$('#estimatedArrivalDateDT').text(formatFullDate(new Date(arrivalDate)));
		
		if ("DLV_CREATED" == deliveryDT.statusId){
			$('#actualStartDateDis').hide();
			$('#actualArrivalDateDis').hide();
			$('#actualStartDate').show();
			$('#actualArrivalDate').show();
			$('#actualStartDate').jqxDateTimeInput('disabled', true);
			$('#actualArrivalDate').jqxDateTimeInput('disabled', true);
		}
		if ("DLV_APPROVED" == deliveryDT.statusId){
			$('#titleDetailId > div:first-child').html("");
			$('#titleDetailId > div:first-child').text(uiLabelMap.DeliveryNote + " - " + uiLabelMap.UpdateActualExportedQuantity);
			$('#actualStartDateDis').hide();
			$('#actualStartDate').show();
			$('#actualStartDate').jqxDateTimeInput('disabled', false);
			$('#actualArrivalDate').jqxDateTimeInput('disabled', true);
			$('#addRow').show();
		} else {
			$('#addRow').hide();
		}
		if ("DLV_EXPORTED" == deliveryDT.statusId){
			$('#titleDetailId > div:first-child').html("");
			$('#titleDetailId > div:first-child').text(uiLabelMap.DeliveryNote + " - " + uiLabelMap.UpdateActualDeliveredQuantity);
			var date = deliveryDT.actualStartDate;
			actualStartDategl = new Date(date);
			$('#actualArrivalDate').show();
			$('#actualArrivalDate').jqxDateTimeInput('val', deliveryDT.actualStartDate);
			$('#actualArrivalDate').jqxDateTimeInput('disabled', false);
			$('#actualStartDate').jqxDateTimeInput('val', deliveryDT.actualStartDate);
			$('#actualStartDate').hide();
			$('#actualArrivalDateDis').hide();
			$('#actualStartDateDis').show();
			$('#actualStartDateDis').html("");
			$('#actualStartDateDis').text(formatFullDate(new Date(date)));
		}
		if ("DLV_DELIVERED" == deliveryDT.statusId){
			$('#titleDetailId > div:first-child').html("");
			$('#titleDetailId > div:first-child').text(uiLabelMap.DeliveryNote+ " - " + uiLabelMap.DeliveryDoc);
			$('#actualStartDate').hide();
			$('#actualArrivalDate').hide();
			$('#actualStartDateDis').show();
			$('#actualStartDateDis').html("");
			var date = deliveryDT.actualStartDate;
			$('#actualStartDateDis').text(formatFullDate(new Date(date)));
			$('#actualArrivalDateDis').show();
			$('#actualArrivalDateDis').html("");
			var arrDate = deliveryDT.actualArrivalDate;
			$('#actualArrivalDateDis').text(formatFullDate(new Date(arrDate)));
		}
		
		var listDeliveryItems = [];
		$.ajax({
            type: "POST",
            url: "getDeliveryItemByDeliveryId",
            data: {'deliveryId': deliveryId},
            dataType: "json",
            async: false,
            success: function(response){
                listDeliveryItems = response['listDeliveryItems'];
            },
            error: function(response){
              alert("Error:" + response);
            }
		});
		listDeliveryItemData = listDeliveryItems;
		if ("DLV_EXPORTED" == deliveryDT.statusId){
			for (var m = 0; m < listDeliveryItems.length; m ++){
				listDeliveryItems[m]["actualDeliveredQuantity"] = listDeliveryItems[m].actualExportedQuantity;
			}
		}
		if ("DLV_APPROVED" == deliveryDT.statusId){
			for (var m = 0; m < listDeliveryItems.length; m ++){
				listDeliveryItems[m]["actualExportedQuantity"] = listDeliveryItems[m].quantity;
				for(i = 0; i < listInv.length; i++){
		 			if (listDeliveryItems[m].productId == listInv[i].productId && listInv[i].quantityOnHandTotal >= listDeliveryItems[m].actualExportedQuantity){
		 				listDeliveryItems[m]["inventoryItemId"] = listInv[i].inventoryItemId;
		 			}
		 		}
			}
		}
		loadDeliveryItem(listDeliveryItems);
		// Open Window
		$("#popupDeliveryDetailWindow").jqxWindow('open');
	}
	
	function initValidateDatetimeDT(element){
		element.jqxValidator({
		rules:[
			{
				input: '#actualArrivalDate', 
			    message: uiLabelMap.ActualDeliveredDateMustAfterActualExportedDate, 
			    action: 'blur', 
			    position: 'topcenter',
			    rule: function (input) {
			    	var actualArrivalDate = $('#actualArrivalDate').jqxDateTimeInput('getDate');
				   	if ((typeof(actualStartDategl) != 'undefined' && actualStartDategl != null && !(/^\s*$/.test(actualStartDategl))) && (typeof(actualArrivalDate) != 'undefined' && actualArrivalDate != null && !(/^\s*$/.test(actualArrivalDate)))) {
			 		    if (actualArrivalDate < actualStartDategl && "DLV_EXPORTED" == glDeliveryStatusId) {
			 		    	return false;
			 		    }
				   	}
				   	return true;
			    }
			},
			{
				input: '#actualArrivalDate', 
			    message: uiLabelMap.CannotAfterNow, 
			    action: 'blur', 
			    position: 'topcenter',
			    rule: function (input) {
			    	var actualArrivalDate = $('#actualArrivalDate').jqxDateTimeInput('getDate');
			    	var nowDate = new Date();
				   	if ((typeof(actualArrivalDate) != 'undefined' && actualArrivalDate != null && !(/^\s*$/.test(actualArrivalDate)))) {
					   	if (actualArrivalDate > nowDate && "DLV_EXPORTED" == glDeliveryStatusId) {
			 		    	return false;
			 		    }
				   	}
				   	return true;
			    }
			},
			{	
				input: '#actualStartDate', 
			    message: uiLabelMap.CannotAfterNow, 
			    action: 'blur', 
			    position: 'topcenter',
			    rule: function (input) {
			    	var actualStartDate = $('#actualStartDate').jqxDateTimeInput('getDate');
			    	var nowDate = new Date();
				   	if ((typeof(actualStartDate) != 'undefined' && actualStartDate != null && !(/^\s*$/.test(actualStartDate)))) {
			 		    if (actualStartDate > nowDate && "DLV_APPROVED" == glDeliveryStatusId) {
			 		    	return false;
			 		    }
				   	}
				   	return true;
			    }
			},
			],
		});
	}
	
	var addNewRow = function addNewRow(){
		var firstRow = $('#jqxgridDlvItem').jqxGrid('getrowdata', 0);
		var selectedIndexs = $('#jqxgridDlvItem').jqxGrid('getselectedrowindexes');
		if (firstRow.productCode){
			$('#jqxgridDlvItem').jqxGrid('clearselection');
			var datarow = generaterow();
	        $("#jqxgridDlvItem").jqxGrid('addrow', null, datarow, "first");
	        $("#jqxgridDlvItem").jqxGrid('unselectrow', 0);
	        for (var i = 0; i < selectedIndexs.length; i ++){
				$("#jqxgridDlvItem").jqxGrid('selectrow', selectedIndexs[i] + 1);
			}
	        $("#jqxgridDlvItem").jqxGrid('begincelledit', 0, "productCode");
		} else {
			$("#jqxgridDlvItem").jqxGrid('begincelledit', 0, "productCode");
		}
	}
	
	function generaterow(productCode){
		var row = {};
		if (productCode){
			var listSames = [];
			for(var i = 0; i < listDeliveryItemData.length; i++){
				var item = listDeliveryItemData[i];
				if (item.productCode == productCode){
					listSames.push(item);
				}
			}
			if (listSames.length > 0){
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
		}
		return row;
	}
	
	var updateRowData = function updateRowData(productCode){
		var datarow = generaterow(productCode);
		var id = $("#jqxgridDlvItem").jqxGrid('getrowid', 0);
        $("#jqxgridDlvItem").jqxGrid('updaterow', id, datarow);
	}
	
	function checkGridDeliveryItemRequiredData(rowindex){
	    var data = $('#jqxgridDlvItem').jqxGrid('getrowdata', rowindex);
	    if(data.statusId == 'DELI_ITEM_EXPORTED'){
	        if(data.actualDeliveredQuantity > data.actualExportedQuantity){
	            $('#jqxgridDlvItem').jqxGrid('unselectrow', rowindex);
	            bootbox.dialog(uiLabelMap.LogCheckActuallyExportedGreaterRealCommunication, [{
	                "label" : uiLabelMap.OK,
	                "class" : "btn btn-primary standard-bootbox-bt",
	                "icon" : "fa fa-check",
	                "callback": function() {
	                        $("#jqxgridDlvItem").jqxGrid('begincelledit', rowindex, "actualDeliveredQuantity");
	                    }
	                }]
	            );
	            return true;
	        }
	    }
	    if(data.statusId == 'DELI_ITEM_DELIVERED'){
	        bootbox.dialog(uiLabelMap.DLYItemComplete, [{
	            "label" : uiLabelMap.OK,
	            "class" : "btn btn-primary standard-bootbox-bt",
	            "icon" : "fa fa-check",
	            }]
	        );
	        return true;
	    }
	    if(data.statusId == 'DELI_ITEM_APPROVED'){
	    	if (listInv.length > 0 && data.inventoryItemId != null && data.inventoryItemId != undefined && data.inventoryItemId != ''){
	    		for(i = 0; i < listInv.length; i++){
	    	        if(listInv[i].inventoryItemId == data.inventoryItemId){
	    	            if (listInv[i].quantityOnHandTotal < data.actualExportedQuantity){
	    	            	bootbox.dialog(uiLabelMap.ExpiredDateSelectedNotEnoughSelectAnother, [{
	    		                "label" : uiLabelMap.OK,
	    		                "class" : "btn btn-primary standard-bootbox-bt",
	    		                "icon" : "fa fa-check",
	    		                "callback": function() {
	    		                    $("#jqxgridDlvItem").jqxGrid('begincelledit', rowindex, "inventoryItemId");
	    		                }
	    		                }]
	    		            );
	    	            	return true;
	    	            }
	    	        }
	    	    }
	    	}
    		if(data.inventoryItemId === null || data.inventoryItemId === undefined || data.inventoryItemId == ''){
    			if (data.actualExportedQuantity > 0){
    				bootbox.dialog(uiLabelMap.ExpireDateNotEnter + " " + uiLabelMap.or + " " + uiLabelMap.NotEnoughDetail, [{
    	                "label" : uiLabelMap.OK,
    	                "class" : "btn btn-primary standard-bootbox-bt",
    	                "icon" : "fa fa-check",
    	                "callback": function() {
    	                    $("#jqxgridDlvItem").jqxGrid('begincelledit', rowindex, "inventoryItemId");
    	                }
    	                }]
    	            );
    				return true;
    			}
	        } else if (data.actualExportedQuantity === null || data.actualExportedQuantity === undefined){
	            bootbox.dialog(uiLabelMap.PleaseEnterQuantityExported, [{
	                "label" : uiLabelMap.OK,
	                "class" : "btn btn-primary standard-bootbox-bt",
	                "icon" : "fa fa-check",
	                "callback": function() {
	                    $("#jqxgridDlvItem").jqxGrid('begincelledit', rowindex, "actualExportedQuantity");
	                }
	            }]
	            );
	            return true;
	        }
	    }
	    return false;
	}
	
	return {
		init: init,
		showDetailPopup: showDetailPopup,
		getFormattedDate: getFormattedDate,
		formatFullDate: formatFullDate,
		updateRowData: updateRowData,
		quickCreateDelivery: quickCreateDelivery,
		addNewRow: addNewRow,
	};
}());