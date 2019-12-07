$(function(){
	SalesDlvObj.init();
});
var SalesDlvObj = (function(){
	var init = function(){
		initInputs();
		initElementComplex();
		initEvents();
		initValidateDatetimeDT($("#popupDeliveryDetailWindow"));
		
		initAttachFile();
		initAttachExptFile();
	};
	var initInputs = function(){
		$("#popupDeliveryDetailWindow").jqxWindow({
		    maxWidth: 1500, minWidth: 950, width: 1300, modalZIndex: 10000, zIndex:10000, minHeight: 500, height: 600, maxHeight: 700, resizable: false, cancelButton: $("#alterCancel2"), keyboardNavigation: true, keyboardCloseKey: 15,  isModal: true, autoOpen: false, modalOpacity: 0.7, theme:theme           
		});
		
		$("#noteWindow").jqxWindow({
			maxWidth: 1300, minWidth: 500, width: 1200, height: 440, minHeight: 100, maxHeight: 700, resizable: false, isModal: true, modalZIndex: 100000, zIndex: 100000, autoOpen: false, cancelButton: $("#noteCancel"), modalOpacity: 0.7, theme:theme           
		});
		
		$('#actualArrivalDate').jqxDateTimeInput({width: 200, disabled: true, formatString: 'dd/MM/yyyy HH:mm:ss'});
		$('#actualStartDate').jqxDateTimeInput({width: 200, disabled: true, formatString: 'dd/MM/yyyy HH:mm:ss'});
		
		$('#facilityReturnId').jqxDropDownList({placeHolder: uiLabelMap.PleaseSelectTitle, width: 200, selectedIndex: 0, source: facilityData, theme: theme, displayMember: 'description', valueMember: 'facilityId',});
		$('#datetimeReceived').jqxDateTimeInput({width: 200, formatString: 'dd/MM/yyyy HH:mm:ss'});
		$('#datetimeReceived').val(new Date());
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
		
		$('#uploadOkButton').click(function(){
			saveFileUpload();
		});
		$('#uploadExptOkButton').click(function(){
			saveFileExptUpload();
		});
		$('#uploadCancelButton').click(function(){
			$('#jqxFileScanUpload').jqxWindow('close');
		});
		$('#jqxFileScanUpload').on('close', function(event){
			$('.remove').trigger('click');
			initAttachFile();
		});
		
		$("#alterSave2").click(function () {
			checkContinue = false;
			saveDelivery();
		});
		
		$("#alterCancel2").click(function () {
		    $("#popupDeliveryDetailWindow").jqxWindow('close');
		});
		
		$('#popupDeliveryDetailWindow').on('close', function (event) {
			$("#jqxgridDlvItem").jqxGrid('refreshdata');
			saveClick = 0;
			$("#jqxgridDelivery").jqxGrid('updatebounddata');
			
			$("#jqxgridDelivery").jqxGrid('clearselection');
			
			$('#jqxgridDlvItem').jqxGrid('clearselection');
			$('#actualArrivalDateDis').hide();
			$('#actualStartDateDis').hide();
			$('#orderNote').hide();
			pathScanFile = null;
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
			if (newStatusId != orderStatus || hasReturn == true){
				window.location.replace("viewOrder?orderId="+orderId+"&activeTab=deliverydis-tab");
			}
			if ("ORDER_COMPLETED" == newStatusId && $("#returnOrderId").length > 0){
				var check = false;
				$.ajax({
					type: "POST",
					url: "checkAllDeliveryInSpecificStatus",
					data: {
						orderId: orderId,
						statusId: "DLV_DELIVERED",
					},
					async: false,
					success: function (res){
						check = res.check;
						if (check){
							$("#returnOrderId").hide();
						} else {
							$("#returnOrderId").show();
						}
					}
				});
			}
			listNoteItems = [];		
			checkAllOrderItemCreatedDone();
		});
		$('#popupDeliveryDetailWindow').on('open', function (event) {
			saveClick = 0;
		});
		
		$("#noteSave").click(function () {
	        bootbox.dialog(uiLabelMap.AreYouSureSave, 
    		[{"label": uiLabelMap.CommonCancel, 
    			"icon": 'fa fa-remove', "class": 'btn  btn-danger form-action-button pull-right',
    		    "callback": function() {bootbox.hideAll();}
    		}, 
    		{"label": uiLabelMap.CommonSave,
    		    "icon": 'fa-check', "class": 'btn btn-primary form-action-button pull-right',
    		    "callback": function() {
    		    	listNoteItems = [];
				    var row;
			        var allRows = $('#noteGrid').jqxGrid('getrows');
			        for (var i = 0; i < allRows.length; i++){
						listNoteItems.push(allRows[i]);
			        }
			        $("#noteWindow").jqxWindow('close');
    		    }
    		}]);
		});
		
		$("#noteWindow").on('close', function (event) {
			if (listNoteItems.length > 0){
				$('#orderNote').show();
				$('#orderNote').html("");
				$('#orderNote').append("<a id='noteId' href='javascript:SalesDlvObj.showOrderNotePopup()'><i class='fa-edit'></i>"+ uiLabelMap.Record +"</a>");
			}
		});
	};
	
	function saveDelivery(){
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
	            				mapTotal = null;
	            				for (var k = 0; k < listItemTmps.length; k ++){
		            				if (listItemTmps[k].inventoryItemId == invIdTmp && listItemTmps[k].isPromo == "N" && listItemTmps[k].fromOrderItemSeqId == orderItemSeqIdTmp){
		            					if (listItemTmps[k].deliveryItemSeqId != null && listItemTmps[k].deliveryItemSeqId != undefined){
		            						mapTotal = listItemTmps[k];
		            					} 
		            				}
	            				}
	            				if (mapTotal === null){
	            					for (var k = 0; k < listItemTmps.length; k ++){
			            				if (listItemTmps[k].inventoryItemId == invIdTmp && listItemTmps[k].isPromo == "N" && listItemTmps[k].fromOrderItemSeqId == orderItemSeqIdTmp){
		            						mapTotal = listItemTmps[k];
		            						break;
			            				}
		            				}
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
	            				mapTotal2 = null;
	            				for (var k = 0; k < listItemTmps.length; k ++){
		            				if (listItemTmps[k].inventoryItemId == invIdTmp && listItemTmps[k].isPromo == "Y" && listItemTmps[k].fromOrderItemSeqId == orderItemSeqIdTmp){
		            					if (listItemTmps[k].deliveryItemSeqId != null && listItemTmps[k].deliveryItemSeqId != undefined){
		            						mapTotal2 = listItemTmps[k];
		            					} 
		            				}
	            				}
	            				if (mapTotal2 === null){
	            					for (var k = 0; k < listItemTmps.length; k ++){
			            				if (listItemTmps[k].inventoryItemId == invIdTmp && listItemTmps[k].isPromo == "Y" && listItemTmps[k].fromOrderItemSeqId == orderItemSeqIdTmp){
		            						mapTotal2 = listItemTmps[k];
		            						break;
			            				}
		            				}
	            				} 
	            				mapTotal2["actualExportedQuantity"] = totalExported2;
	            				listDeliveryItems.push(mapTotal2);
	            			} else if (listDuplicated2.length == 1){
	            				listDeliveryItems.push(mapTotal2);
	            			}
	            		}	            
	            		
	            		getInventory(orderId, glOriginFacilityId, curDeliveryId);
	            		var listInvExport = [];
	            		for (var v = 0; v < listInvSelected.length; v ++){
	            			var invTmp = {};
	            			invTmp['inventoryItemId'] = listInvSelected[v].inventoryItemId;
	            			var totalExptQty = 0;
	            			for (var t = 0; t < listDeliveryItems.length; t ++){
		            			if (listDeliveryItems[t].inventoryItemId == listInvSelected[v].inventoryItemId){
		            				totalExptQty = totalExptQty + listDeliveryItems[t].actualExportedQuantity;
		            			}
		            		}
	            			invTmp['totalExptQty'] = totalExptQty;
	            			listInvExport.push(invTmp);
	            		}
	            		var checkEng = false;
	            		for (var l = 0; l < listInv.length; l ++){
	            			for (var m = 0; m < listInvExport.length; m ++){
	            				if (listInv[l].inventoryItemId == listInvExport[m].inventoryItemId){
		            				if (listInv[l].quantityOnHandTotal < listInvExport[m].totalExptQty){
		            					checkEng = true;
		            					break;
		            				}
		            			}
	            			}
	            			if (checkEng == true) break;
	            		}
	            		if (checkEng == true){
	            			bootbox.dialog(uiLabelMap.NotEnoughDetail, [{
	                            "label" : uiLabelMap.OK,
	                            "class" : "btn btn-primary standard-bootbox-bt",
	                            "icon" : "fa fa-check",
	                            }]
	                        );
	                        return false;
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
                            pathScanFile: pathScanFile,
                            pathScanFileExpt: pathScanFileExpt,
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
	    					 $('#jqxgridDelivery').jqxGrid('updatebounddata');
	    					 checkAllOrderItemCreatedDone();
	    					 },
	    					 error: function(response){
	    					 $('#jqxgridDelivery').jqxGrid('hideloadelement');
	    					 }
                		});
	    				 // create return and note to order if have
                		if (listNoteItems.length > 0){
                			hasReturn = true;
	        				 for (var m = 0; m < listNoteItems.length; m ++){
	    						 listNoteItems[m]["quantity"] = listNoteItems[m]["actualExportedQuantity"] - listNoteItems[m]["actualDeliveredQuantity"];
	    						 listNoteItems[m]["expiredDate"] = listNoteItems[m]["expiredDate"].getTime();
	    						 listNoteItems[m]["manufacturedDate"] = listNoteItems[m]["manufacturedDate"].getTime();
	    						 if (listNoteItems[m].inventoryItemStatusId == "Good"){
	    							 listNoteItems[m]['inventoryItemStatusId'] = null;
	    						 }
	    						 listNoteItems[m]['productName'] = '';
	        				 }
	        				 var facilityReturnId = $("#facilityReturnId").val();
	        				 var datetimeReceivedReturn = $('#datetimeReceived').jqxDateTimeInput('getDate');
	        				 var listNoteItemTmps = JSON.stringify(listNoteItems);
	        				 
	        				 $.ajax({
	        					  url: "updateOrderNote",
	        					  type: "POST",
	        					  data: {
	        						  orderId : orderId,
	        						  listNoteItems: listNoteItemTmps,
	        						  },
	        					  dataType: "json",
	        					  async: false,
	        					  success: function(data) {
	        					  }
	        				 }).done(function() {
		        				 $.ajax({
		    	   					  url: "receiveReturnItems",
		    	   					  type: "POST",
		    	   					  data: {
		    	   						  orderId: orderId,
		    	   						  currencyUomId: currencyUom,
		    	   						  facilityId: facilityReturnId,
		    	   						  datetimeReceived: datetimeReceivedReturn.getTime(),
		    	   						  listReturnItems: listNoteItemTmps,
		    	   						  },
		    	   					  dataType: "json",
		    	   					  async: false,
		    	   					  success: function(data) {
		    	   					  }
		        				 });
	        				 });
	    				 }
	    				 $.ajax({
	    					 type: "POST",
	    					 url: "checkOrderStatus",
	    					 data: {
	    					 orderId: orderId,
	    					 },
	    					 async: false,
	    					 success: function (res){
	    						 statusId = res.statusId;
	    						 var desc = "";
	    						 for (var i = 0; i < orderStatusData.length; i ++){
	    							 if (statusId == orderStatusData[i].statusId){
	    								 desc = orderStatusData[i].description;
	    		 					}
	    						 }
	    						 $("#statusTitle").text(desc);
	    					 }
	    				 });
	    				 if (checkContinue == true){
	    					 saveClick = 0;
	    					 showDetailPopup(curDeliveryId);
	    				 } else {
	    					 $("#popupDeliveryDetailWindow").jqxWindow('close');
	    				 }
	                    Loading.hide('loadingMacro');
                    } else {
						return false;
					} 
            	}, 500);
            }
		}]);
	}
	
	function checkRequiredDataNote(rowindex){
		var data = $('#noteGrid').jqxGrid('getrowdata', rowindex);
		if(!data.manufacturedDate){
	        $('#noteGrid').jqxGrid('unselectrow', rowindex);
	        bootbox.dialog(uiLabelMap.TheManufacturedDateFieldNotYetBeEntered, [{
	            "label" : uiLabelMap.OK,
	            "class" : "btn btn-primary standard-bootbox-bt",
	            "icon" : "fa fa-check",
	            "callback": function() {
	                    $("#noteGrid").jqxGrid('begincelledit', rowindex, "manufacturedDate");
	                }
	            }]
	        );
	        return true;
	    }
		if(!data.expiredDate){
	        $('#noteGrid').jqxGrid('unselectrow', rowindex);
	        bootbox.dialog(uiLabelMap.TheExpiredDateFieldNotYetBeEntered, [{
	            "label" : uiLabelMap.OK,
	            "class" : "btn btn-primary standard-bootbox-bt",
	            "icon" : "fa fa-check",
	            "callback": function() {
	                    $("#noteGrid").jqxGrid('begincelledit', rowindex, "expiredDate");
	                }
	            }]
	        );
	        return true;
	    }
		if(!data.returnReasonId){
	        $('#noteGrid').jqxGrid('unselectrow', rowindex);
	        bootbox.dialog(uiLabelMap.PleaseSelectAReason, [{
	            "label" : uiLabelMap.OK,
	            "class" : "btn btn-primary standard-bootbox-bt",
	            "icon" : "fa fa-check",
	            "callback": function() {
	                    $("#noteGrid").jqxGrid('begincelledit', rowindex, "returnReasonId");
	                }
	            }]
	        );
	        return true;
	    }
		if(!data.inventoryItemStatusId){
	        $('#noteGrid').jqxGrid('unselectrow', rowindex);
	        bootbox.dialog(uiLabelMap.PleaseSelectAStatusOfProduct, [{
	            "label" : uiLabelMap.OK,
	            "class" : "btn btn-primary standard-bootbox-bt",
	            "icon" : "fa fa-check",
	            "callback": function() {
	                    $("#noteGrid").jqxGrid('begincelledit', rowindex, "inventoryItemStatusId");
	                }
	            }]
	        );
	        return true;
	    }
	}
	
	showOrderNotePopup = function showOrderNotePopup(){
		loadNoteGrid(listNoteItems);
		$("#noteWindow").jqxWindow('open');
	}
	
	function checkAllOrderItemCreatedDone(){
		var createdDone = true;
		$.ajax({
			type: 'POST',
			async: false,
			url: 'checkAllSalesOrderItemCreatedDelivery',
			data: {
				orderId: orderId,
			},
			success: function (res){
				createdDone = res['createdDone'];
				if (createdDone == true){
					$("#addrowbuttonjqxgridDelivery").hide();
					$("#customcontroljqxgridDelivery1").hide();
				} else {
					$("#addrowbuttonjqxgridDelivery").show();
					$("#customcontroljqxgridDelivery1").show();
				}
			}
		});
	}
	
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
    
	function initAttachExptFile(){
		$('#attachFileExpt').html('');
		listImage = [];
		$('#attachFileExpt').ace_file_input({
			style:'well',
			btn_choose: uiLabelMap.DropFileOrClickToChoose,
			btn_change:null,
			no_icon:'icon-cloud-upload',
			droppable:true,
			onchange:null,
			thumbnail:'small',
			before_change:function(files, dropped) {
				listImage = [];
				var count = files.length;
				for (var int = 0; int < files.length; int++) {
					var imageName = files[int].name;
					var hashName = imageName.split(".");
					var extended = hashName.pop();
					if (imageName.length > 50){
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
						}
					} 
				}
				return true;
			},
			before_remove : function() {
				listImage = [];
				return true;
			}
		});
	}
	
	function initAttachFile(){
		$('#attachFile').html('');
		listImage = [];
		$('#attachFile').ace_file_input({
			style:'well',
			btn_choose: uiLabelMap.DropFileOrClickToChoose,
			btn_change:null,
			no_icon:'icon-cloud-upload',
			droppable:true,
			onchange:null,
			thumbnail:'small',
			before_change:function(files, dropped) {
				listImage = [];
				var count = files.length;
				for (var int = 0; int < files.length; int++) {
					var imageName = files[int].name;
					var hashName = imageName.split(".");
					var extended = hashName.pop();
					if (imageName.length > 50){
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
						}
					} 
				}
				return true;
			},
			before_remove : function() {
				listImage = [];
				return true;
			}
		});
	}
	
	function saveFileExptUpload (){
		Loading.show('loadingMacro');
    	setTimeout(function(){
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
					success: function(res) {
						path = res.path;
						pathScanFileExpt = path;
						$('#linkIdExpt').html("");
						$('#linkIdExpt').attr('onclick', null); 
						$('#linkIdExpt').append("<a href='"+path+"' onclick='' target='_blank'><i class='fa-file-image-o'></i>"+uiLabelMap.AttachExportedScan+"</a> <a href='javascript:SalesDlvObj.removeExptScanFile()'><i class='fa-remove'></i></a>");
			        }
				}).done(function() {
				});
			}
			$('#jqxFileScanExptUpload').jqxWindow('close');
			Loading.hide('loadingMacro');
    	}, 500);
	}
	
	function saveFileUpload (){
		Loading.show('loadingMacro');
    	setTimeout(function(){
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
					success: function(res) {
						path = res.path;
						pathScanFile = path;
						$('#linkId').html("");
						$('#linkId').attr('onclick', null); 
						$('#linkId').append("<a href='"+path+"' onclick='' target='_blank'><i class='fa-file-image-o'></i>"+uiLabelMap.AttachDeliveredScan+"</a> <a href='javascript:SalesDlvObj.removeScanFile()'><i class='fa-remove'></i></a>");
			        }
				}).done(function() {
				});
			}
			$('#jqxFileScanUpload').jqxWindow('close');
			Loading.hide('loadingMacro');
    	}, 500);
	}
	
	var removeExptScanFile = function removeExptScanFile (){
		pathScanExptFile = null;
		$('#linkIdExpt').html("");
		$('#linkIdExpt').attr('onclick', null);
		$('#linkIdExpt').append("<a id='linkId' href='javascript:SalesDlvObj.showAttachExptFilePopup()' onclick=''><i class='fa-upload'></i> "+uiLabelMap.AttachExportedScan+"</a>");
	}
	
	var removeScanFile = function removeScanFile (){
		pathScanFile = null;
		$('#linkId').html("");
		$('#linkId').attr('onclick', null);
		$('#linkId').append("<a id='linkId' href='javascript:SalesDlvObj.showAttachFilePopup()' onclick=''><i class='fa-upload'></i> "+uiLabelMap.AttachDeliveredScan+"</a>");
	}
	var showAttachFilePopup = function showAttachFilePopup(){
		$('#jqxFileScanUpload').jqxWindow('open');
	}
	
	var showAttachExptFilePopup = function showAttachExptFilePopup(){
		$('#jqxFileScanExptUpload').jqxWindow('open');
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
	
	var showDetailPopup = function showDetailPopup(deliveryId){
		checkContinue = false;
		$('#orderNote').hide();
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
        console.log(121212, deliveryDT)
        glDelivery = deliveryDT;
        glOriginFacilityId = deliveryDT.originFacilityId;
        glDeliveryStatusId = deliveryDT.statusId;
		// Set deliveryId for target print pdf
		var href = "deliveryAndExport.pdf?deliveryId=";
		href += deliveryId;
		$('#printPDF').attr('href', href);
		// Create deliveryIdDT
		$("#deliveryIdDT").text(deliveryDT.deliveryId);
		
		// Create statusIdDT
		var stName = null;
        for(i=0; i < statusData.length; i++){
            if(statusData[i].statusId == deliveryDT.statusId){
                stName = statusData[i].description;
            }
        }
        if (stName){
        	$("#statusIdDT").text(stName);
        	if ("DLV_CREATED" == deliveryDT.statusId || "DLV_PROPOSED" == deliveryDT.statusId || ("DLV_DELIVERED" == deliveryDT.statusId && deliveryDT.pathScanFile != null)){
        		$("#alterSave2").hide();
        		if (perAdmin){
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
		var createDateText = null;
		if (createDate.getMonth()+1 < 10){
			if (createDate.getDate() < 10){
				createDateText = '0' + createDate.getDate() + '/0' + (createDate.getMonth()+1) + '/' + createDate.getFullYear();
			} else {
				createDateText = createDate.getDate() + '/0' + (createDate.getMonth()+1) + '/' + createDate.getFullYear();
			}
		} else {
			if (createDate.getDate() < 10){
				createDateText = '0' + createDate.getDate() + '/' + (createDate.getMonth()+1) + '/' + createDate.getFullYear();
			} else {
				createDateText = createDate.getDate() + '/' + (createDate.getMonth()+1) + '/' + createDate.getFullYear();
			}
		}
		$('#createDateDT').html("");
		$('#createDateDT').append(createDateText + " " + addZero(createDate.getHours())+':'+addZero(createDate.getMinutes())+':'+addZero(createDate.getSeconds()));
		
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
		var textDlvDate = null;
		if (deliveryDate.getMonth()+1 < 10){
			if (deliveryDate.getDate() < 10){
				textDlvDate = '0'+ deliveryDate.getDate() + '/0' + (deliveryDate.getMonth()+1) + '/' + deliveryDate.getFullYear();
			} else {
				textDlvDate = deliveryDate.getDate() + '/0' + (deliveryDate.getMonth()+1) + '/' + deliveryDate.getFullYear();
			}
		} else {
			if (deliveryDate.getDate() < 10){
				textDlvDate = '0' + deliveryDate.getDate() + '/' + (deliveryDate.getMonth()+1) + '/' + deliveryDate.getFullYear();
			} else {
				textDlvDate = deliveryDate.getDate() + '/' + (deliveryDate.getMonth()+1) + '/' + deliveryDate.getFullYear();
			}
		}
		$('#deliveryDateDT').html("");
		$('#deliveryDateDT').append(textDlvDate + " " + addZero(deliveryDate.getHours())+':'+addZero(deliveryDate.getMinutes())+':'+addZero(deliveryDate.getSeconds()));
		
		// Create noDT
		if (deliveryDT.no){
        	$("#noDT").text(deliveryDT.no);
        } else {
        	$("#noDT").text("_NA_");
        }
		
		// Create pathScanfile
		var path = "";
		if (deliveryDT.pathScanFile){
			path = deliveryDT.pathScanFile;
			$('#scanfile').html("");
			$('#scanfile').append("<a href="+path+" target='_blank'><i class='fa-file-image-o'></i>"+uiLabelMap.AttachDeliveredScan+"</a>");
		} else {
			if ("DLV_DELIVERED" == deliveryDT.statusId || "DLV_EXPORTED" == deliveryDT.statusId){
				$('#scanfile').html("");
				$('#scanfile').append("<a id='linkId' href='javascript:SalesDlvObj.showAttachFilePopup()' onclick=''><i class='fa-upload'></i> "+uiLabelMap.AttachDeliveredScan+"</a>");
			} else {
				$('#scanfile').html("");
			}
		}
		var pathExpt = "";
		if (deliveryDT.pathScanFileExpt){
			pathExpt = deliveryDT.pathScanFileExpt;
			$('#scanfileExpt').html("");
			$('#scanfileExpt').append("<a href="+pathExpt+" target='_blank'><i class='fa-file-image-o'></i>"+uiLabelMap.AttachExportedScan+"</a>");
		} else {
			if ("DLV_EXPORTED" == deliveryDT.statusId || "DLV_APPROVED" == deliveryDT.statusId){
				$('#scanfileExpt').html("");
				$('#scanfileExpt').append("<a id='linkIdExpt' href='javascript:SalesDlvObj.showAttachExptFilePopup()' onclick=''><i class='fa-upload'></i> "+uiLabelMap.AttachExportedScan+"</a>");
			} else {
				$('#scanfileExpt').html("");
			}
		}
		
		if ("DLV_EXPORTED" == deliveryDT.statusId){
			$('#titleDetailId > div:first-child').html("");
			$('#titleDetailId > div:first-child').text(uiLabelMap.DeliveryNote + " - " + uiLabelMap.UpdateActualDeliveredQuantity);
			if (listNoteItems.length > 0){
				$('#orderNote').show();
				$('#orderNote').html("");
				$('#orderNote').append("<a id='linkId' href='javascript:SalesDlvObj.showAttachFilePopup()' onclick=''><i class='fa-edit'></i>"+uiLabelMap.Record+"</a>");
			}
			var date = deliveryDT.actualStartDate;
			$('#actualArrivalDate').show();
			$('#actualArrivalDate').jqxDateTimeInput('val', deliveryDT.actualStartDate);
			$('#actualArrivalDate').jqxDateTimeInput('disabled', false);
			$('#actualStartDate').jqxDateTimeInput('val', deliveryDT.actualStartDate);
			$('#actualStartDate').hide();
			$('#actualArrivalDateDis').hide();
			$('#actualStartDateDis').show();
			$('#actualStartDateDis').html("");
			var temp = date.split(" ");
			var d = temp[0].split("-");
			var h = temp[1].split(":");
			$('#actualStartDateDis').append(d[2]+'/'+d[1]+'/'+d[0] + " " + h[0]+':'+h[1]+':'+h[2].split(".")[0]);
		} else if ("DLV_DELIVERED" == deliveryDT.statusId){
			$('#actualStartDate').hide();
			$('#actualArrivalDate').hide();
			$('#actualStartDateDis').show();
			$('#actualStartDateDis').html("");
			var date = deliveryDT.actualStartDate;
			var temp = date.split(" ");
			var d = temp[0].split("-");
			var h = temp[1].split(":");
			$('#actualStartDateDis').append(d[2]+'/'+d[1]+'/'+d[0] + " " + h[0]+':'+h[1]+':'+h[2].split(".")[0]);
			$('#actualArrivalDateDis').show();
			$('#actualArrivalDateDis').html("");
			var arrDate = deliveryDT.actualArrivalDate;
			var temp2 = arrDate.split(" ");
			var d2 = temp2[0].split("-");
			var h2 = temp2[1].split(":");
			$('#actualArrivalDateDis').append(d2[2]+'/'+d2[1]+'/'+d2[0] + " " + h2[0]+':'+h2[1]+':'+h2[2].split(".")[0]);
			$("#alterSave2").hide();
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
		loadDeliveryItem(listDeliveryItems);
		// Open Window
		$("#popupDeliveryDetailWindow").jqxWindow('open');
		
		$("#facilityReturnId").val(deliveryDT.originFacilityId);
	}
	
	function addNewRow(){
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
	
	function updateRowData(productCode){
		var datarow = generaterow(productCode);
		var id = $("#jqxgridDlvItem").jqxGrid('getrowid', 0);
        $("#jqxgridDlvItem").jqxGrid('updaterow', id, datarow);
	}
	
	function getInventory(orderId, facilityId, deliveryId){
    	$.ajax({
            type: "POST",
            url: "getINVByOrderAndDlv",
            data: {'orderId': orderId, 'facilityId': facilityId, 'deliveryId': deliveryId},
            dataType: "json",
            async: false,
            success: function(response){
                listInv = response.listData;
            },
            error: function(response){
              alert("Error:" + response);
            }
        });
    }
	
	getFormattedDate = function getFormattedDate(date) {
		  var year = date.getFullYear();
		  var month = (1 + date.getMonth()).toString();
		  month = month.length > 1 ? month : '0' + month;
		  var day = date.getDate().toString();
		  day = day.length > 1 ? day : '0' + day;
		  return day + '/' + month + '/' + year;
	}
	
	function addZero(i) {
	    if (i < 10) {
	        i = "0" + i;
	    }
	    return i;
	}

	return {
		init: init,
		showDetailPopup: showDetailPopup,
		showAttachFilePopup: showAttachFilePopup,
		removeScanFile: removeScanFile,
		addNewRow: addNewRow,
		updateRowData: updateRowData,
		removeExptScanFile: removeExptScanFile,
		getFormattedDate: getFormattedDate,
		showAttachExptFilePopup: showAttachExptFilePopup,
		showOrderNotePopup: showOrderNotePopup,
	};
}());