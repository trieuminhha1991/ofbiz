$(function(){
	TransferDlvObj.init();
});
var TransferDlvObj = (function(){
	var init = function(){
		initInputs();
		initElementComplex();
		initEvents();
		initValidateForm();
		if (inTransferDetail){
			checkAllTransferItemCreatedDone();
		}
	};
	var initInputs = function(){
		$("#popupDeliveryDetailWindow").jqxWindow({
			maxWidth: 1500, minWidth: 950, width: 1300, modalZIndex: 10000, zIndex:10000, minHeight: 500, height: 600, maxHeight: 670, resizable: false, cancelButton: $("#alterCancel2"), keyboardNavigation: true, keyboardCloseKey: 15,  isModal: true, autoOpen: false, modalOpacity: 0.7, theme:theme
		});
		$('#actualStartDate').jqxDateTimeInput({width: 200, formatString: 'dd/MM/yyyy HH:mm:ss', disabled: true});
		$('#actualStartDate').hide();
		$('#actualArrivalDate').jqxDateTimeInput({width: 200, formatString: 'dd/MM/yyyy HH:mm:ss', disabled: true});
		$('#actualArrivalDate').hide();
		
		$('#jqxgridDelivery').jqxGrid('selectrow', 0);
		$('#totalProductWeight').text('0');
		
		if ($("#DeliveryMenu").length > 0){
			$("#DeliveryMenu").jqxMenu({ width: 220, autoOpenPopup: false, mode: 'popup', theme: theme});
		}
	};
	var initElementComplex = function(){
	
	};
	var initValidateForm = function(){
		
	};
	var initEvents = function(){
		
		if ($("#DeliveryMenu").length > 0){
			$("#DeliveryMenu").on('itemclick', function (event) {
				var data = $('#jqxgridDelivery').jqxGrid('getRowData', $("#jqxgridDelivery").jqxGrid('selectedrowindexes'));
				var tmpStr = $.trim($(args).text());
				if(tmpStr == uiLabelMap.BLQuickView){
					showDetailPopup(data.deliveryId);
				} else if(tmpStr == uiLabelMap.BSViewDetail){
					showDetailDelivery(data.deliveryId);
				} else if (tmpStr == uiLabelMap.BSRefresh){
					$('#jqxgridDelivery').jqxGrid('updatebounddata');
				} else if (tmpStr == uiLabelMap.ExportPdf){
					window.open('transferDelivery.pdf?deliveryId='+data.deliveryId, '_blank');
				}
			});
		}
		
		$('#cancelDlv').on("click",function(){
			bootbox.dialog(uiLabelMap.AreYouSureCancel, 
				[{"label": uiLabelMap.CommonCancel, 
					"icon": 'fa fa-remove', "class": 'btn  btn-danger form-action-button pull-right',
		            "callback": function() {bootbox.hideAll();}
		        }, 
		        {"label": uiLabelMap.OK,
		            "icon": 'fa-check', "class": 'btn btn-primary form-action-button pull-right',
		            "callback": function() {
		            	$.ajax({
		        	    	url: "changeDeliveryStatus",
		        	    	type: "POST",
		        	    	data: {
		        	    		deliveryId: glDeliveryId,
		        	    		statusId: "DLV_CANCELLED",
		        	    	},
		        	    	async: false,
		        	    	success: function (res){
		        	    		showDetailPopup(res.deliveryId);
		        	    	}
		        	    });
		            }
				}]);
		});
		
		$("#alterpopupWindow").on('open', function (event) {
			btnClick = false;
		    var listTransferItems = [];
			$.ajax({
	            type: "POST",
	            url: "getTransferItemToDelivery",
	            data: {
	            	transferId: transferId,
	            },
	            dataType: "json",
	            async: false,
	            success: function(response){
	            	listTransferItems = response['listTransferItems'];
	            },
	            error: function(response){
	              alert("Error:" + response);
	            }
			});
			loadTransferItem(listTransferItems);
			if (!$('#alterpopupWindow').jqxValidator('validate')){
				return false;
			}
		});
		
		$('#uploadOkButton').click(function(){
			saveFileUpload();
		});
		$('#uploadCancelButton').click(function(){
			$('#jqxFileScanUpload').jqxWindow('close');
		});
		$('#jqxFileScanUpload').on('close', function(event){
			initAttachFile();
		});
		
		$("#addButtonSave").click(function () {
			var row;
			//Get List Order Item
			var selectedIndexs = [];
			if (maySplit != undefined && maySplit != null && maySplit == "N"){
		        var allRows = $('#jqxgridTransferItem').jqxGrid('getrows');
		        for (var id = 0; id < allRows.length; id ++){
		        	selectedIndexs.push(allRows[id].uid);
		        }
			} else {
				selectedIndexs = $('#jqxgridTransferItem').jqxGrid('getselectedrowindexes');
			}
			if(selectedIndexs.length == 0){
			    jOlbUtil.alert.error(uiLabelMap.YouNotYetChooseProduct);
			    return false;
			} else {
				bootbox.dialog(uiLabelMap.AreYouSureCreate, 
				[{"label": uiLabelMap.CommonCancel, 
					"icon": 'fa fa-remove', "class": 'btn  btn-danger form-action-button pull-right',
		            "callback": function() {bootbox.hideAll();}
		        }, 
		        {"label": uiLabelMap.OK,
		            "icon": 'fa-check', "class": 'btn btn-primary form-action-button pull-right',
		            "callback": function() {
		            	Loading.show('loadingMacro');
		            	setTimeout(function(){
		    				var listTransferItems = [];
		    				for(var i = 0; i < selectedIndexs.length; i++){
		    					var data = $('#jqxgridTransferItem').jqxGrid('getrowdata', selectedIndexs[i]);
		    					var map = {};
		    					map['transferItemSeqId'] = data.transferItemSeqId;
		    					map['transferId'] = data.transferId;
		    					map['inventoryItemId'] = data.inventoryItemId;
		    					map['shipGroupSeqId'] = data.shipGroupSeqId;
		    					map['quantity'] = data.quantityToDelivery;
		    					listTransferItems.push(map);
		    				}	
		    				var listTransferItems = JSON.stringify(listTransferItems);
		    				row = { 
		    					transferId:$('#transferId').val(),
		    					deliveryDate:$('#deliveryDate').jqxDateTimeInput('getDate'),
		    					listTransferItems:listTransferItems,
		    					estimatedStartDate: $('#estimatedStartDate').jqxDateTimeInput('getDate'),
		    					estimatedArrivalDate: $('#estimatedArrivalDate').jqxDateTimeInput('getDate'),
		    					defaultWeightUomId: 'WT_kg',
		    				};
		    				$("#jqxgridDelivery").jqxGrid('addRow', null, row, "first");
		    				Loading.hide('loadingMacro');
		            	}, 500);
	    				$("#jqxgridDelivery").jqxGrid('updatebounddata');      
	    				$("#alterpopupWindow").jqxWindow('close');
		            }
		        }]);
			}
		});
		$('#alterpopupWindow').on('close', function (event) {
			checkAllTransferItemCreatedDone();
			$('#jqxgridTransferItem').jqxGrid('clearSelection');
			btnClick = false;
		}); 
		$("#addButtonCancel").click(function () {
			$("#alterpopupWindow").jqxWindow('close');
		});
		
		$('#popupDeliveryDetailWindow').on('open', function (event) {
			checkOpenPoppup = true;
			btnClick = false;
		});
		
		$('#popupDeliveryDetailWindow').on('close', function (event) {
			$("#jqxgridDlvItem").jqxGrid('refreshdata');
			saveClick = 0;
			if($("#jqxgridDelivery").is('*[class^="jqx"]')){
				$("#jqxgridDelivery").jqxGrid('updatebounddata');
			}
			if (inTransferDetail){
				checkTransferStatus();
				checkAllTransferItemCreatedDone();
			}
		});
		
	    $("#alterCancel2").click(function () {
	       $("#popupDeliveryDetailWindow").jqxWindow('close'); 
	    });
	    
	    $("#approveBtn").click(function () {
	    	checkContinue = false;
	    	approveDelivery();
    	});
	    
	    $("#approveAndContinue").click(function () {
	    	checkContinue = true;
	    	approveDelivery();
    	});
	    
	    $("#alterSave2").click(function () {
	    	checkContinue = false;
	    	saveDelivery();
	    });
	    
    	$("#saveAndContinue").click(function () {
    		checkContinue = true;
    		saveDelivery();
    	});
	};
	
	function approveDelivery(){	
    	bootbox.dialog(uiLabelMap.AreYouSureApprove, 
		[{"label": uiLabelMap.CommonCancel, 
			"icon": 'fa fa-remove', "class": 'btn  btn-danger form-action-button pull-right',
            "callback": function() {bootbox.hideAll();}
        }, 
        {"label": uiLabelMap.OK,
            "icon": 'fa-check', "class": 'btn btn-primary form-action-button pull-right',
            "callback": function() {
		    	 $.ajax({
	                 type: "POST",
	                 url: "updateDeliveryStatus",
	                 data: {
	                	 setItemStatus: "Y",
	                	 deliveryId: glDeliveryId,
	                	 newStatusId: "DLV_APPROVED",
	                	 newItemStatus: "DELI_ITEM_APPROVED"
	                 },
	                 dataType: "json",
	                 async: false,
	                 success: function(data){
	                     $('#jqxgridDelivery').jqxGrid('updatebounddata');
	                 },
	                 error: function(response){
	                     $('#jqxgridDelivery').jqxGrid('hideloadelement');
	                 } 
	             }).done(function() {
	            	 if (checkContinue == false){
	            		 $("#popupDeliveryDetailWindow").jqxWindow('close');
	            	 } else {
	            		 showDetailPopup(glDeliveryId);
	            	 }
				 });
            }
        }]);
    }
	
	function saveDelivery(){
    	var row;
        //Get List Delivery Item
        var selectedItems = [];
        var allRows = $('#jqxgridDlvItem').jqxGrid('getrows');
        for (var id = 0; id < allRows.length; id ++){
        	if(checkGridDeliveryItemRequiredData(allRows[id].uid) == true){
                return false;
            }
        	if (allRows[id].statusId == "DELI_ITEM_APPROVED"){
        		if (allRows[id].actualExportedQuantity > 0) {
        			selectedItems.push(allRows[id]);
        		}
        	} else {
        		if (allRows[id].actualDeliveredQuantity > 0) selectedItems.push(allRows[id]);
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
            		for (var i = 0; i < selectedItems.length; i++){
            			var data1 = selectedItems[i];
                        var map1 = {};
                        if (data1 != undefined){
	                        map1["fromTransferId"] = data1.fromTransferId;
	                        map1["fromTransferItemSeqId"] = data1.fromTransferItemSeqId;
	                        map1["inventoryItemId"] = data1.inventoryItemId;
	                        map1["deliveryId"] = data1.deliveryId;
	                        map1["deliveryItemSeqId"] = data1.deliveryItemSeqId;
	                        map1["actualExportedQuantity"] = data1.actualExportedQuantity;
	                        map1["actualDeliveredQuantity"] = data1.actualDeliveredQuantity;
	                        map1["productId"] = data1.productId;
	                        map1["requireAmount"] = data1.requireAmount;
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
	            					if (listInvSelected[anh].inventoryItemId == listItemTmps[em].inventoryItemId && listInvSelected[anh].transferItemSeqId == listItemTmps[em].fromTransferItemSeqId){
	            						checkTmp = true;
	            						break;
	            					}
	            				}
	            				if (checkTmp == false){
	            					var mapInvItem = {
	            							transferItemSeqId: listItemTmps[em].fromTransferItemSeqId,
	            							inventoryItemId: listItemTmps[em].inventoryItemId,
	            					};
	            					listInvSelected.push(mapInvItem);
	            				}
	            			}
	            		}
	            		for (var iId = 0; iId < listInvSelected.length; iId ++){
	            			var mapInvItemTmp = listInvSelected[iId];
	            			var invIdTmp = mapInvItemTmp.inventoryItemId;
	            			var transferItemSeqIdTmp = mapInvItemTmp.transferItemSeqId;
	            			var listDuplicated = [];
	            			var mapTotal;
	            			for (var jId = 0; jId < listItemTmps.length; jId ++){
	            				if (listItemTmps[jId].inventoryItemId == invIdTmp && listItemTmps[jId].fromTransferItemSeqId == transferItemSeqIdTmp){
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
		            				if (listItemTmps[k].inventoryItemId == invIdTmp && listItemTmps[k].fromTransferItemSeqId == transferItemSeqIdTmp){
		            					if (listItemTmps[k].deliveryItemSeqId != null && listItemTmps[k].deliveryItemSeqId != undefined){
		            						mapTotal = listItemTmps[k];
		            					} 
		            				}
	            				}
	            				if (mapTotal === null){
	            					for (var k = 0; k < listItemTmps.length; k ++){
			            				if (listItemTmps[k].inventoryItemId == invIdTmp && listItemTmps[k].fromTransferItemSeqId == transferItemSeqIdTmp){
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
	            			
	            		}	            
	            		getInventory(glOriginFacilityId, curDeliveryId);
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
	            			var inv = listInv[l];
	            			var requireAmount = inv.requireAmount;
	            			var qoh = inv.quantityOnHandTotal;
	            			if (requireAmount && requireAmount == 'Y') {
	            				qoh = inv.amountOnHandTotal;
	            			}
	            			for (var m = 0; m < listInvExport.length; m ++){
	            				if (listInv[l].inventoryItemId == listInvExport[m].inventoryItemId){
		            				if (qoh < listInvExport[m].totalExptQty){
		            					checkEng = true;
		            					break;
		            				}
		            			}
	            			}
	            			if (checkEng == true) break;
	            		}
	            		if (checkEng == true){
	            			jOlbUtil.alert.error(uiLabelMap.NotEnoughDetail);
	            			Loading.hide('loadingMacro');
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
	                if ("DLV_APPROVED" == glDeliveryStatusId){
	                	var tmp = $('#actualStartDate').jqxDateTimeInput('getDate');
	                	if (tmp){
	                		actualStartDateTmp = tmp.getTime();
	                	}
	                }
	                if ("DLV_EXPORTED" == glDeliveryStatusId){
	                	var tmp = actualArrivalDateTmp = $('#actualArrivalDate').jqxDateTimeInput('getDate');
	                	if (tmp){
	                		actualArrivalDateTmp = tmp.getTime();
	                	}
	                }
	                row = { 
	                        listDeliveryItems:listDeliveryItems,
//	                        pathScanFile: pathScanFile,
	                        deliveryId: curDeliveryId,
	                        actualStartDate: actualStartDateTmp,
	                    	actualArrivalDate: actualArrivalDateTmp,
	                      };
	                // call Ajax request to Update Exported or Delivered value
	                if (saveClick == 0) {
		                $.ajax({
		                    type: "POST",
		                    url: "updateDeliveryItemList",
		                    data: row,
		                    dataType: "json",
		                    async: false,
		                    success: function(data){
		                    },
		                    error: function(response){
		                        $('#jqxgridDelivery').jqxGrid('hideloadelement');
		                    }
		                }).done(function() {
		    			});
		                saveClick = saveClick + 1;
	                }
	                if (checkContinue == true){
	                	saveClick = 0;
	                	showDetailPopup(curDeliveryId);
	                } else {
	                	$("#popupDeliveryDetailWindow").jqxWindow('close');
	                	displayEditSuccessMessage('jqxgridDelivery');
	                }
	                Loading.hide('loadingMacro');
            	}, 500);
            }
        }]);
    }
	
	var showDetailPopup = function showDetailPopup(deliveryId){
		$("#cancelDlv").hide();
		checkRoleByDelivery(deliveryId);
		selectedDlvId = deliveryId;
		glDeliveryId = deliveryId;
		//Create theme
		$.jqx.theme = 'olbius';
		theme = $.jqx.theme;
		
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
		//Cache delivery
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
                       url: "getInvByTransferAndDlv",
                       data: {'facilityId': deliveryDT.originFacilityId, 'deliveryId': deliveryDT.deliveryId},
                       dataType: "json",
                       async: false,
                       success: function(response){
                           listInv = response.listData
                       },
                       error: function(response){
                       }
                   });
	           },
	           error: function(response){
	           }
	    });
		if ("DLV_APPROVED" == deliveryDT.statusId){
			$("#cancelDlv").show();
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

				for (var m in arrDlvItem) {
					arrDlvItem[m]["actualExportedQuantity"] = arrDlvItem[m].quantity/arrDlvItem[m].convertNumber;
					var item = arrDlvItem[m];
					var requireAmount = item.requireAmount;
					if (requireAmount && requireAmount == 'Y'){
						arrDlvItem[m]["actualExportedQuantity"] = arrDlvItem[m].amount;
					}
					var expQuantity = item.actualExportedQuantity;
					
					var productId = arrDlvItem[m].productId
					var fst = true;
					var remainQty = expQuantity;
					var check = false;
					for (var i in listInv) {
						if (productId == listInv[i].productId) {
							var qoh = listInv[i].quantityOnHandTotal;
							if (requireAmount && requireAmount == 'Y'){
								qoh = listInv[i].amountOnHandTotal;
							}
							if (qoh > 0) {
								check = true;
							}
							if (qoh <= remainQty) {
								if (fst == true) {
									item["inventoryItemId"] = listInv[i].inventoryItemId;
									item["actualExportedQuantity"] = qoh;
									listDeliveryItems.push(item);
									fst = false;
								} else {
									var newItem = $.extend({}, item);
									delete newItem["deliveryItemSeqId"];
									newItem["inventoryItemId"] = listInv[i].inventoryItemId;
									newItem["actualExportedQuantity"] = qoh;
									listDeliveryItems.push(newItem);
								}
								remainQty = remainQty - qoh;
							} else {
								if (fst == true) {
									item["inventoryItemId"] = listInv[i].inventoryItemId;
									item["actualExportedQuantity"] = remainQty;
									listDeliveryItems.push(item);
									fst = false;
								} else {
									var newItem = $.extend({}, item);
									delete newItem["deliveryItemSeqId"];
									newItem["inventoryItemId"] = listInv[i].inventoryItemId;
									newItem["actualExportedQuantity"] = remainQty;
									listDeliveryItems.push(newItem);
								}
								remainQty = 0;
							}
							if (remainQty <= 0) break;
						}
					}
					if (check == false) {
						listDeliveryItems.push(item);
					}
				}
				for(i = 0; i < listInv.length; i++){
		 			if (listDeliveryItems[m].productId == listInv[i].productId && listInv[i].quantityOnHandTotal >= listDeliveryItems[m].actualExportedQuantity){
		 				listDeliveryItems[m]["inventoryItemId"] = listInv[i].inventoryItemId;
		 			}
		 		}
			}
		}
		if ("DLV_EXPORTED" == deliveryDT.statusId){
			for (var m = 0; m < listDeliveryItems.length; m ++){
				var item = listDeliveryItems[m];
				if (item.requireAmount && item.requireAmount == 'Y') {
					var exp = listDeliveryItems[m].actualExportedAmount;
					listDeliveryItems[m]["actualExportedQuantity"] = exp;
					listDeliveryItems[m]["actualDeliveredQuantity"] = exp;
				} else {
					listDeliveryItems[m]["actualDeliveredQuantity"] = listDeliveryItems[m].actualExportedQuantity;
				}
			}
			
		}
		loadDeliveryItem(listDeliveryItems);
		//Set deliveryId for target print pdf
		var hrefDoc = "transferDelivery.pdf?deliveryId=";
		hrefDoc += deliveryDT.deliveryId;
        $('#printPDF').attr('href', hrefDoc);
		
		//Create deliveryIdDT
		$("#deliveryIdDT").text(deliveryDT.deliveryId);
		glOriginFacilityId = deliveryDT.originFacilityId;
        glDeliveryStatusId = deliveryDT.statusId;
		//Create statusIdDT
        var statusDT;
		if (deliveryDT.statusId == "DLV_DELIVERED") {
			statusDT = uiLabelMap.BLCompleted;
		} else {
			for(var i = 0; i < statusData.length; i++){
				if(deliveryDT.statusId == statusData[i].statusId){
					statusDT = 	statusData[i].description;
					break;
				}
			}
		}
		$("#statusDT").text(statusDT);
		if ("DLV_CREATED" == deliveryDT.statusId){
			$("#cancelDlv").show();
			$('#titleDetailId > div:first-child').html("");
			$('#titleDetailId > div:first-child').text(uiLabelMap.DeliveryTransferNote + " - " + uiLabelMap.ApprovedDelivery);
			$("#addRow").hide();
			$("#alterSave2").hide();
		} else if ("DLV_APPROVED" == deliveryDT.statusId){
			$('#titleDetailId > div:first-child').html("");
			$('#titleDetailId > div:first-child').text(uiLabelMap.DeliveryTransferNote + " - " + uiLabelMap.UpdateActualExportedQuantity);
			$("#addRow").show();
			$("#alterSave2").show();
		} else if ("DLV_EXPORTED" == deliveryDT.statusId){
			$('#titleDetailId > div:first-child').html("");
			$('#titleDetailId > div:first-child').text(uiLabelMap.DeliveryTransferNote + " - " + uiLabelMap.UpdateActualDeliveredQuantity);
			$("#addRow").hide();
			$("#alterSave2").show();
		} else if ("DLV_DELIVERED" == deliveryDT.statusId){
			$('#titleDetailId > div:first-child').html("");
			$('#titleDetailId > div:first-child').text(uiLabelMap.DeliveryTransferNote+ " - " + uiLabelMap.DeliveryDoc);
			$("#addRow").hide();
			$("#alterSave2").hide();
		}
		
		//Create transferIdDT 
		$("#transferIdDT").text(deliveryDT.transferId);
		
		//Create originFacilityIdDT
		$("#originFacilityDT").text(deliveryDT.originFacilityName);
		
		//Create destFacilityIdDT
		$("#destFacilityDT").text(deliveryDT.destFacilityName);
		
		//Create destContactMechIdDT
		$("#destContactMechIdDT").text(deliveryDT.destAddress);
		
		//Create originContactMechIdDT
		$("#originContactMechIdDT").text(deliveryDT.originAddress);
		
		//Create deliveryDateDT
		$("#deliveryDateDT").text(DatetimeUtilObj.formatFullDate(new Date(deliveryDT.deliveryDate)));
		
		$("#estimatedStartDateDT").text(DatetimeUtilObj.formatFullDate(new Date(deliveryDT.estimatedStartDate)));
		$("#estimatedArrivalDateDT").text(DatetimeUtilObj.formatFullDate(new Date(deliveryDT.estimatedArrivalDate)));
		
		//Create Grid
        var tmpS = $("#jqxgridDlvItem").jqxGrid('source');
        tmpS._source.url = "jqxGeneralServicer?sname=getListDeliveryItem&deliveryId=" + deliveryId;
        $("#jqxgridDlvItem").jqxGrid('source', tmpS);
        //Create pathScanfile
		var path = "";
		
		if ("DLV_CREATED" == deliveryDT.statusId){
			$('#actualStartDateDis').hide();
			$('#actualArrivalDateDis').hide();
			$('#actualStartDate').show();
			$('#actualArrivalDate').show();
			$('#actualStartDate').jqxDateTimeInput('disabled', true);
			$('#actualArrivalDate').jqxDateTimeInput('disabled', true);
			if (perAdmin == true){
				$("#approveBtn").show();
				$('#approveAndContinue').show();
			} else {
				$("#approveBtn").hide();
				$('#approveAndContinue').hide();
			}
			$("#alterSave2").hide();
		} else {
			$("#approveBtn").hide();
			$('#approveAndContinue').hide();
		}
		
		if ("DLV_APPROVED" == deliveryDT.statusId){
			$('#actualStartDateDis').hide();
			$('#actualStartDate').show();
			$('#actualArrivalDate').show();
			$('#actualArrivalDateDis').hide();
			$('#actualStartDate').jqxDateTimeInput('disabled', false);
			$('#actualArrivalDate').jqxDateTimeInput('disabled', true);
			$("#saveAndContinue").show();
		} else {
			$("#saveAndContinue").hide();
		}
		if ("DLV_EXPORTED" == deliveryDT.statusId){
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
			var temp = date.split(" ");
			var d = temp[0].split("-");
			var h = temp[1].split(":");
			$('#actualStartDateDis').append(d[2]+'/'+d[1]+'/'+d[0] + " " + h[0]+':'+h[1]+':'+h[2].split(".")[0]);
		}
		if ("DLV_DELIVERED" == deliveryDT.statusId){
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
		}
		
		if ("DLV_CANCELLED" == deliveryDT.statusId){
        	$("#printPDF").hide();
        	$("#scanfile").hide();
        	$('#actualStartDate').jqxDateTimeInput('disabled', true);
			$('#actualArrivalDate').jqxDateTimeInput('disabled', true);
        	$("#alterSave2").hide();
        	$("#cancelDlv").hide();
        } else {
        	$("#printPDF").show();
        	$("#scanfile").show();
        }
		
		if (checkUpdate == false){
			$("#approveAndContinue").hide();
			$("#approveBtn").hide();
			$("#saveAndContinue").hide();
			$("#alterSave2").hide();
			$("#cancelDlv").hide();
		}
		var path = "";
		if (deliveryDT.contentId){
			var x = "deliveryId: &#39;"+deliveryId + "&#39;";
			$('#scanfile').html("");
			$('#scanfile').append("<a style='font-size: 14px;' href='javascript:Viewer.open({"+x+"})' data-rel='tooltip' title="+ uiLabelMap.Scan +" data-placement='bottom' class='button-action'><i class='fa fa-file-image-o'></i></a>");
		} else {
			if ("DLV_EXPORTED" == deliveryDT.statusId || "DLV_DELIVERED" == deliveryDT.statusId){
				$('#scanfile').html("");
				var x = "deliveryId: &#39;"+deliveryId + "&#39;";
				$('#scanfile').append("<a style='font-size: 14px;' href='javascript:Uploader.open({"+x+"})' data-rel='tooltip' title="+uiLabelMap.Scan+" data-placement='bottom' class='button-action'><i class='fa fa-upload'></i></a>");
			} else {
				$('#scanLabel').html("");
				$('#scanfile').html("");
			}
		}
		var path = "";
		if (deliveryDT.contentId){
			var x = "deliveryId: &#39;"+deliveryId + "&#39;";
			$('#scanfile').html("");
			$('#scanfile').append("<a style='font-size: 14px;' href='javascript:Viewer.open({"+x+"})' data-rel='tooltip' title="+ uiLabelMap.Scan +" data-placement='bottom' class='button-action'><i class='fa fa-file-image-o'></i></a>");
		} else {
			if ("DLV_EXPORTED" == deliveryDT.statusId || "DLV_DELIVERED" == deliveryDT.statusId){
				$('#scanfile').html("");
				var x = "deliveryId: &#39;"+deliveryId + "&#39;";
				$('#scanfile').append("<a style='font-size: 14px;' href='javascript:Uploader.open({"+x+"})' data-rel='tooltip' title="+uiLabelMap.Scan+" data-placement='bottom' class='button-action'><i class='fa fa-upload'></i></a>");
			} else {
				$('#scanLabel').html("");
				$('#scanfile').html("");
			}
		}
		//Open Window
		$("#popupDeliveryDetailWindow").jqxWindow('open');
	}
	
	function checkRoleByDelivery(deliveryId){
		$.ajax({
               type: "POST",
               url: "checkRoleByDelivery",
               data: {'deliveryId': deliveryId},
               dataType: "json",
               async: false,
               success: function(response){
            	   isStorekeeperFrom = response.isStorekeeperFrom;
            	   isStorekeeperTo = response.isStorekeeperTo;
            	   isSpecialist = response.isSpecialist;
               },
               error: function(response){
               }
        });
	}
	
	function updateTotalWeight(){
		var totalProductWeight = 0;
		var selectedIndexs = $('#jqxgridTransferItem').jqxGrid('getselectedrowindexes');
		for(var i = 0; i < selectedIndexs.length; i++){
			var data = $('#jqxgridTransferItem').jqxGrid('getrowdata', selectedIndexs[i]);
			var baseWeightUomId = data.baseWeightUomId;
			var defaultWeightUomId = 'WT_kg';
			var itemWeight = 0;
			if (data.availableToPromiseTotal < 1){
				itemWeight = 0;
			} else {
				itemWeight = (data.quantityToDelivery)*(data.weight)*(data.convertNumber);
			}
			if (baseWeightUomId == defaultWeightUomId){
				totalProductWeight = totalProductWeight + itemWeight;
			} else {
				for (var j=0; j<uomConvertData.length; j++){
					if ((uomConvertData[j].uomId == baseWeightUomId && uomConvertData[j].uomIdTo == defaultWeightUomId) || (uomConvertData[j].uomId == defaultWeightUomId && uomConvertData[j].uomIdTo == baseWeightUomId)){
						totalProductWeight = totalProductWeight + (uomConvertData[j].conversionFactor)*itemWeight;
						break;
					}
				}
			}
		}
		var n = parseFloat(totalProductWeight)
		totalProductWeight = Math.round(n * 1000)/1000;
		$('#totalProductWeight').text(totalProductWeight);
	}
	function rowselectfunctionProduct(event){
	    if (typeof event.args.rowindex != 'number'){
	        var tmpArray = event.args.rowindex;
	        for(i = 0; i < tmpArray.length; i++){
	            if(checkRequiredData(tmpArray[i])){
	                $('#jqxgridTransferItem').jqxGrid('clearselection');
	                break; // Stop for first item
	            }
	        }
	    } else{
	        var test = checkRequiredData(event.args.rowindex);
	        if (!test){
	        }
	    }
	}
	function checkRequiredData(rowindex){
	    var data = $('#jqxgridTransferItem').jqxGrid('getrowdata', rowindex);
	    if(data == undefined){
	        return true; // to break the loop
	    } 
	    if (data.quantityOnHandTotal < 1){
	    	displayNotEnough(rowindex, uiLabelMap.FacilityNotEnoughProduct);
	    	return true;
	    }
	    if(data.quantityToDelivery == undefined){
	        displayAlert(rowindex, uiLabelMap.DLYItemMissingFieldsDlv);
	        return true;
	    } else if (data.quantityToDelivery < 1){
	    	displayAlert(rowindex, uiLabelMap.NumberGTZ);
	        return true;
	    }else if(data.quantityToDelivery > data.quantity){
	        displayAlert(rowindex, uiLabelMap.ExportValueLTZRequireValue);
	        return true;
	    }
	    return false;
	}
	
	function displayNotEnough(rowindex, message){
	    bootbox.dialog(message, [{
	        "label" : uiLabelMap.OK,
	        "class" : "btn btn-primary standard-bootbox-bt",
	        "icon" : "fa fa-check",
	        "callback": function() {
	        	 $("#jqxgridTransferItem").jqxGrid('unselectrow', rowindex);
	        }
	        }]
	    );
	}
	function displayAlert(rowindex, message){
	    bootbox.dialog(message, [{
	        "label" : uiLabelMap.OK,
	        "class" : "btn btn-primary standard-bootbox-bt",
	        "icon" : "fa fa-check",
	        "callback": function() {
	            $("#jqxgridTransferItem").jqxGrid('begincelledit', rowindex, "quantityToDelivery");
	        }
	        }]
	    );
	}
	
	function checkRequiredTranferProductByFacilityToFacility(rowindex){
		var data = $('#jqxgridTransferItem').jqxGrid('getrowdata', rowindex);
		if(data == undefined){
            bootbox.dialog(uiLabelMap.DLYItemMissingFieldsDlv, [{
                "label" : "OK",
                "class" : "btn btn-primary standard-bootbox-bt",
                "icon" : "fa fa-check",
                "callback": function() {
                        $("#jqxgridTransferItem").jqxGrid('begincelledit', rowindex, "quantity");
                    }
                }]
            );
            return true;
		}else{
			var quantity = data.quantity;
	    	var quantityToDelivery = data.quantityToDelivery;
	        if(quantityToDelivery == 0 || quantityToDelivery == undefined){
	            $('#jqxgridTransferItem').jqxGrid('unselectrow', rowindex);
	            bootbox.dialog(uiLabelMap.DLYItemMissingFieldsDlv, [{
	                "label" : "OK",
	                "class" : "btn btn-primary standard-bootbox-bt",
	                "icon" : "fa fa-check",
	                "callback": function() {
	                        $("#jqxgridTransferItem").jqxGrid('begincelledit', rowindex, "quantityToDelivery");
	                    }
	                }]
	            );
	            return true;
	        }else{
	        	if(quantityToDelivery > quantity){
	                $('#jqxgridTransferItem').jqxGrid('unselectrow', rowindex);
	                bootbox.dialog(uiLabelMap.QuantityCantNotGreateThanQuantityNeedTransfer, [{
	                    "label" : "OK",
	                    "class" : "btn btn-primary standard-bootbox-bt",
	                    "icon" : "fa fa-check",
	                    "callback": function() {
	                            $("#jqxgridTransferItem").jqxGrid('begincelledit', rowindex, "quantityToDelivery");
	                        }
	                    }]
	                );
	                return true;
	            }
	        }
		}
	}
	
	function functionAfterUpdate(){
		$.ajax({
	           type: "POST",
	           url: "getDeliveryById",
	           data: {'deliveryId': selectedDlvId},
	           dataType: "json",
	           async: false,
	           success: function(response){
	        	   deliveryDT = response;
	           },
	           error: function(response){
	           }
	    });
		//Create statusIdDT
		var statusDT;
		if (deliveryDT.statusId == "DLV_DELIVERED") {
			statusDT = uiLabelMap.BLCompleted;
		} else {
			for(var i = 0; i < statusData.length; i++){
				if(deliveryDT.statusId == statusData[i].statusId){
					statusDT = 	statusData[i].description;
					break;
				}
			}
		}
		$("#statusIdDT").text(statusDT);
		
		$("#jqxgridDlvItem").jqxGrid('updatebounddata');
	}
	function updatejqxgridTransferItem(){
		$("#jqxgridTransferItem").jqxGrid("updatebounddata");
	}
	
	function removeScanFile (){
		pathScanFile = null;
		$('#linkId').html("");
		$('#linkId').attr('onclick', null);
		$('#linkId').append("<a id='linkId' onclick='showAttachFilePopup()'><i class='icon-upload'></i>"+uiLabelMap.AttachFileScan+"</a>");
	}
	function showAttachFilePopup(){
		$('#jqxFileScanUpload').jqxWindow('open');
	}
	
	function saveFileUpload (){
		var folder = "/baseLogistics/delivery";
		for ( var d in listImage) {
			var file = listImage[d];
			var dataResourceName = file.name;
			var path = "";
			var form_data= new FormData();
			form_data.append("uploadedFile", file);
			form_data.append("folder", folder);
			jQuery.ajax({
				url: "uploadDemo",
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
					$('#linkId').append("<a href='"+path+"' onclick='' target='_blank'><i class='fa-file-text-o'></i>'"+dataResourceName+"'</a> <a onclick='removeScanFile()'><i class='fa-remove'></i></a>");
		        }
			}).done(function() {
			});
		}
		$('#jqxFileScanUpload').jqxWindow('close');
	}
	
	function initAttachFile(){
		$('#attachFile').html('');
		listImage = [];
		$('#attachFile').ace_file_input({
			style:'well',
			btn_choose:uiLabelMap.DropFileOrClickToChoose,
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
					if (extended == "JPG" || extended == "jpg" || extended == "jpeg" || extended == "gif" || extended == "png") {
						listImage.push(files[int]);
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
	
	function functionAfterUpdate2(){
	    var tmpS = $("#jqxgridDlvItem").jqxGrid('source');
	    tmpS._source.url = "jqxGeneralServicer?sname=getListDeliveryItem&deliveryId=" + glDeliveryId;
	    $("#jqxgridDlvItem").jqxGrid('source', tmpS);
	}
	
	function checkGridDeliveryItemRequiredData(rowindex){
	    var data = $('#jqxgridDlvItem').jqxGrid('getrowdata', rowindex);
	    var requireAmount = data.requireAmount;
	    if(data.statusId == 'DELI_ITEM_EXPORTED'){
	        if (data.actualDeliveredQuantity == 0){
	            $('#jqxgridDlvItem').jqxGrid('unselectrow', rowindex);
	            bootbox.dialog(uiLabelMap.DLYItemMissingFieldsDlv, [{
	                "label" : "OK",
	                "class" : "btn btn-primary standard-bootbox-bt",
	                "icon" : "fa fa-check",
	                "callback": function() {
	                        $("#jqxgridDlvItem").jqxGrid('begincelledit', rowindex, "actualDeliveredQuantity");
	                    }
	                }]
	            );
	            return true;
	        }
	        if (requireAmount && 'Y' == requireAmount) {
	        	if(data.actualDeliveredQuantity > data.actualExportedAmount){
		            $('#jqxgridDlvItem').jqxGrid('unselectrow', rowindex);
		            bootbox.dialog(uiLabelMap.LogCheckActuallyExportedGreaterRealCommunication, [{
		                "label" : "OK",
		                "class" : "btn btn-primary standard-bootbox-bt",
		                "icon" : "fa fa-check",
		                "callback": function() {
		                        $("#jqxgridDlvItem").jqxGrid('begincelledit', rowindex, "actualDeliveredQuantity");
		                    }
		                }]
		            );
		            return true;
		        }
	        } else {
	        	if(data.actualDeliveredQuantity > data.actualExportedQuantity){
		            $('#jqxgridDlvItem').jqxGrid('unselectrow', rowindex);
		            bootbox.dialog(uiLabelMap.LogCheckActuallyExportedGreaterRealCommunication, [{
		                "label" : "OK",
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
	    }
	    if(data.statusId == 'DELI_ITEM_DELIVERED'){
	        jOlbUtil.alert.error(uiLabelMap.DLYItemComplete);
	        return true;
	    }
	    if(data.statusId == 'DELI_ITEM_CREATED' && (data.inventoryItemId == null || data.actualExportedQuantity == 0)){
	        if(data.inventoryItemId == null){
	            bootbox.dialog(uiLabelMap.DItemMissingFieldsExp, [{
	                "label" : "OK",
	                "class" : "btn btn-primary standard-bootbox-bt",
	                "icon" : "fa fa-check",
	                "callback": function() {
	                    $("#jqxgridDlvItem").jqxGrid('begincelledit', rowindex, "inventoryItemId");
	                }
	                }]
	            );
	            return true;
	        }else{
	            bootbox.dialog(uiLabelMap.DItemMissingFieldsExp, [{
	                "label" : "OK",
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
	    if(data.statusId == 'DELI_ITEM_APPROVED'){
	    	var requireAmount = data.requireAmount;
	    	if (listInv.length > 0 && data.inventoryItemId != null && data.inventoryItemId != undefined && data.inventoryItemId != ''){
	    		for(i = 0; i < listInv.length; i++){
	    			var qoh = listInv[i].quantityOnHandTotal;
	    			if (requireAmount && 'Y' == requireAmount) {
	    				qoh = listInv[i].amountOnHandTotal;
	    			}
	    	        if(listInv[i].inventoryItemId == data.inventoryItemId){
	    	            if (qoh < data.actualExportedQuantity){
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
    		if(data.inventoryItemId === null || data.inventoryItemId === undefined || data.inventoryItemId === ''){
	            bootbox.dialog(uiLabelMap.InventoryItemNotChoose + " " + uiLabelMap.or + " " + uiLabelMap.NotEnoughDetail, [{
	                "label" : uiLabelMap.OK,
	                "class" : "btn btn-primary standard-bootbox-bt",
	                "icon" : "fa fa-check",
	                "callback": function() {
	                    $("#jqxgridDlvItem").jqxGrid('begincelledit', rowindex, "inventoryItemId");
	                }
	                }]
	            );
	            return true;
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
	
	function loadTransferItem(valueDataSoure){
		var sourceProduct = {
			datafields:[{ name: 'transferId', type: 'string' },
						{ name: 'transferItemSeqId', type: 'string' },
						{ name: 'transferItemTypeId', type: 'string' },
						{ name: 'productId', type: 'string' },
						{ name: 'productName', type: 'string' },
						{ name: 'requireAmount', type: 'string' },
						{ name: 'quantity', type: 'string' },
						{ name: 'amount', type: 'string' },
						{ name: 'quantityToDelivery', type: 'number' },
						{ name: 'quantityUomId', type: 'string' },
						{ name: 'weightUomId', type: 'string' },
						{ name: 'expireDate', type: 'date', other: 'Timestamp' },
						{ name: 'quantityOnHandTotal', type: 'number'},
						{ name: 'amountOnHandTotal', type: 'number'},
						{ name: 'availableToPromiseTotal', type: 'number'},
						{ name: 'originFacilityId', type: 'string' },
						{ name: 'destFacilityId', type: 'string' },
						{ name: 'baseQuantityUomId', type: 'string' },
						{ name: 'weight', type: 'number' },
						{ name: 'baseWeightUomId', type: 'string' },
						{ name: 'convertNumber', type: 'number' },
			 		 	],
 		 	localdata: valueDataSoure,
	        datatype: "array",
	    }
		var dataAdapterProduct = new $.jqx.dataAdapter(sourceProduct);
	    $("#jqxgridTransferItem").jqxGrid({
        source: dataAdapterProduct,
        filterable: false,
        showfilterrow: false,
        theme: 'olbius',
        rowsheight: 26,
        width: '100%',
        height: 345,
        enabletooltips: true,
        autoheight: false,
        pageable: true,
        pagesize: 10,
        editable: true,
        columnsresize: true,
        localization: getLocalization(),
        selectionmode: seletion,
	        columns:[
					{ text: uiLabelMap.SequenceId, sortable: false, filterable: false, editable: false, pinned: true, 
					    groupable: false, draggable: false, resizable: false,
					    datafield: '', columntype: 'number', width: 50,
					    cellsrenderer: function (row, column, value) {
					        return '<span style=margin:4px;>' + (value + 1) + '</span>';
					    }
					},
					{ text: uiLabelMap.ProductId, dataField: 'productId', width: 200, filtertype:'input', editable: false, pinned: true},
					{ text: uiLabelMap.ProductName, dataField: 'productName', minwidth: 150, filtertype:'input', editable: false,},
					{ text: uiLabelMap.QuantityNeedToTransfer, dataField: 'quantity', width: 200, editable: false, cellsalign: 'right',
						cellsrenderer: function (row, column, value){
							 var data = $('#jqxgridTransferItem').jqxGrid('getrowdata', row);
							 var requireAmount = data.requireAmount;
							 if (requireAmount && 'Y' == requireAmount) {
								 return '<span style="text-align: right">' + formatnumber(data.amount) +'</span>';
							 } else {
								 return '<span style="text-align: right">' + formatnumber(value) +'</span>';
							 }
						 }
					},
					{ text: uiLabelMap.Unit, dataField: 'quantityUomId', width: 120, editable: false,
						 cellsrenderer: function (row, column, value){
							 var data = $('#jqxgridTransferItem').jqxGrid('getrowdata', row);
							 var requireAmount = data.requireAmount;
							 if (requireAmount && 'Y' == requireAmount) {
								 return '<span style="text-align: right">' + getUomDescription(data.weightUomId) +'</span>';
							 } else {
								 return '<span style="text-align: right">' + getUomDescription(value) +'</span>';
							 }
						 }
					},
					{ text: uiLabelMap.QuantityToTransfer, dataField: 'quantityToDelivery', width: 200, align: 'center', cellsalign: 'right', columntype: 'numberinput', editable: true,
						 validation: function (cell, value) {
					    	 var data = $('#jqxgridTransferItem').jqxGrid('getrowdata', cell.row);
					    	 if (value <= 0) {
					             return { result: false, message: uiLabelMap.QuantityMustBeGreateThanZero};
					         }
					    	 var requireAmount = data.requireAmount;
					    	 var qty = data.quantity;
					    	 if (requireAmount && requireAmount == 'Y') {
					    		 qty = data.amount;
					    	 }
					         if (value > qty){
					        	 return { result: false, message: uiLabelMap.QuantityCantNotGreateThanQuantityNeedTransfer + ' ' + value + ' > ' + qty};
					         }
					         return true;
					     },
					     initeditor: function(row, value, editor){
					    	 var data = $('#jqxgridTransferItem').jqxGrid('getrowdata', row);
					    	 var requireAmount = data.requireAmount;
					    	 if (requireAmount && requireAmount == 'Y') {
					    		 editor.jqxNumberInput({decimalDigits: 2, digits: 10});
					    	 } else {
					    		 editor.jqxNumberInput({decimalDigits: 0, digits: 10});
					    	 }
					    	 if (data.quantityToDelivery != null && data.quantityToDelivery != undefined && data.quantityToDelivery != ''){
					    		 editor.jqxNumberInput('val', data.quantityToDelivery);
					    	 } else {
					    		 if (data.quantity){
						          	editor.jqxNumberInput('val', data.quantity);
						         }
					    	 }
					     },
					     cellsrenderer: function (row, column, value){
					    	 if (value){
					    		 return '<span style="text-align: right" class="focus-color" title=' + value.toLocaleString(localeStr) + '>' + value.toLocaleString(localeStr) + '</span>'
					    	 } else {
					    		 	var data = $('#jqxgridTransferItem').jqxGrid('getrowdata', row);
							 		var id = data.uid;
							 		var requiredQty = data.quantity;
							 		$('#jqxgridTransferItem').jqxGrid('setcellvaluebyid', id, 'quantityToDelivery', requiredQty);
							 		return '<span style="text-align: right;" class="focus-color" title=' + requiredQty.toLocaleString(localeStr) + '>' + requiredQty.toLocaleString(localeStr) + '</span>';
					    	 }
					     }
					 }, 
				 ] 
	    });
	}
	
	function loadDeliveryItem(valueDataSoure){
		var sourceProduct =
		    {
		        datafields:[{ name: 'deliveryId', type: 'string' },
		                 	{ name: 'deliveryItemSeqId', type: 'string' },
		                 	{ name: 'fromTransferItemSeqId', type: 'string' },
		                 	{ name: 'fromTransferId', type: 'string' },
		                 	{ name: 'productId', type: 'string' },
		                 	{ name: 'productCode', type: 'string' },
		                	{ name: 'productName', type: 'string' },
		                 	{ name: 'quantityUomId', type: 'string' },
		                 	{ name: 'transferQuantityUomId', type: 'string' },
		                 	{ name: 'comment', type: 'string' },
		                 	{ name: 'actualExportedQuantity', type: 'number' },
		                 	{ name: 'actualDeliveredQuantity', type: 'number' },
		                 	{ name: 'actualExportedAmount', type: 'number' },
		                 	{ name: 'actualDeliveredAmount', type: 'number' },
		                 	{ name: 'statusId', type: 'string' },
		                 	{ name: 'quantity', type: 'number' },
		                 	{ name: 'amount', type: 'number' },
		                 	{ name: 'inventoryItemId', type: 'string' },
							{ name: 'actualExpireDate', type: 'string', other: 'Timestamp'},
							{ name: 'expireDate', type: 'date', other: 'Timestamp'},
		                 	{ name: 'deliveryStatusId', type: 'string'},
							{ name: 'weight', type: 'number'},
							{ name: 'weightUomId', type: 'String'},
							{ name: 'requireAmount', type: 'String'},
							{ name: 'defaultWeightUomId', type: 'String'},
				 		 	],
		        localdata: valueDataSoure,
		        datatype: "array",
		    };
		    var dataAdapterProduct = new $.jqx.dataAdapter(sourceProduct);
		    $("#jqxgridDlvItem").jqxGrid({
	        source: dataAdapterProduct,
	        filterable: false,
	        showfilterrow: false,
	        theme: 'olbius',
	        rowsheight: 26,
	        width: '100%',
	        height: 345,
	        enabletooltips: true,
	        autoheight: false,
	        pageable: true,
	        pagesize: 10,
	        editable: true,
	        columnsresize: true,
	        localization: getLocalization(),
	        columns: [	
			{ text: uiLabelMap.SequenceId, sortable: false, filterable: false, editable: false, pinned: true, 
		        groupable: false, draggable: false, resizable: false,
		        datafield: '', columntype: 'number', width: 50,
		        cellsrenderer: function (row, column, value) {
		            return '<span style=margin:4px;>' + (value + 1) + '</span>';
		        }
		    },
		    { text: uiLabelMap.ProductId, dataField: 'productCode', width: 120, editable: true, columntype: 'dropdownlist', pinned: true,
		    	createeditor: function (row, cellvalue, editor) {
					var codeSourceData = [];
					for (var n = 0; n < valueDataSoure.length; n ++){
						var prCode = valueDataSoure[n].productCode;
						var kt = false;
						for (var m = 0; m < codeSourceData.length; m ++){
							if (codeSourceData[m].productCode == prCode){
								kt = true;
								break;
							}
						}
						if (kt == false){
							var map = {};
							map['productCode'] = prCode;
							codeSourceData.push(map);
						}
					}
					var sourcePrCode =
					{
		               localdata: codeSourceData,
		               datatype: 'array'
					};
					var dataAdapterPrCode = new $.jqx.dataAdapter(sourcePrCode);
					editor.off('change');
					editor.jqxDropDownList({source: dataAdapterPrCode, autoDropDownHeight: true, displayMember: 'productCode', valueMember: 'productCode', placeHolder: uiLabelMap.PleaseSelectTitle,
					});
					editor.on('change', function (event){
						var args = event.args;
			     	    if (args) {
		     	    		var item = args.item;
			     		    if (item){
			     		    	TransferDlvObj.updateRowData(item.value);
			     		    } 
			     	    }
			        });
				 },
				 cellbeginedit: function (row, datafield, columntype) {
					 var data = $('#jqxgridDlvItem').jqxGrid('getrowdata', row);
					 if (data.productCode){
						 return false;
					 }
					 return true;
				 },
				 cellsrenderer: function(row, column, value){
					 var data = $('#jqxgridDlvItem').jqxGrid('getrowdata', row);
					 if (!data.productCode){
						 return '<span>' + uiLabelMap.PleaseSelectTitle + '</span>';
					 }
				 }
			},
			{ text: uiLabelMap.ProductName, dataField: 'productName', minwidth: 150, editable: false,
				cellsrenderer: function(row, column, value){
					var data = $('#jqxgridDlvItem').jqxGrid('getrowdata', row);
					if (!data.productCode){
						return '<span style="text-align: right">...</span>';
					}
				}
			},
			{ text: uiLabelMap.Quantity, dataField: 'quantity', cellsalign: 'right', width: 100, editable: false,
				cellsrenderer: function(row, column, value){
					var data = $('#jqxgridDlvItem').jqxGrid('getrowdata', row);
					var requireAmount = data.requireAmount;
					if (value === null || value === undefined || value === ""){
						if (data.productCode){
							return '<span style="text-align: right;"></span>';
						} else {
							return '<span style="text-align: right;">...</span>';
						}
					}
					if (requireAmount && 'Y' == requireAmount) {
						return '<span style="text-align: right">' + formatnumber(data.amount) +'</span>';
					}
					return '<span style="text-align: right">' + formatnumber(value) +'</span>';
				 }
			},
			{ text: uiLabelMap.Unit, dataField: 'quantityUomId', width: 100, editable: false,
				 cellsrenderer: function (row, column, value){
					 var data = $('#jqxgridDlvItem').jqxGrid('getrowdata', row);
					 if (value === null || value === undefined || value === ""){
						 if (data.productCode){
							return '<span style="text-align: right;"></span>';
						 } else {
							return '<span style="text-align: right;">...</span>';
						 }
					 }
					 var requireAmount = data.requireAmount;
					 if (requireAmount && 'Y' == requireAmount) {
						 return '<span style="text-align: right">' + getUomDescription(data.weightUomId) +'</span>';
					 } else {
						 return '<span style="text-align: right">' + getUomDescription(value) +'</span>';
					 }
				 }
			},
			{ text: uiLabelMap.LogInventoryItem, dataField: 'inventoryItemId', columntype: 'dropdownlist', width: 200, editable: true, sortable: false,
			    cellsrenderer: function(row, column, value){
			        var data = $('#jqxgridDlvItem').jqxGrid('getrowdata', row);
			        if(data != null && data != undefined){
			        	if(value != null && value != '' && value != undefined){
			        		if (data.statusId == 'DELI_ITEM_APPROVED'){
			            		for(k = 0; k < listInv.length; k++){
				                    if(listInv[k].inventoryItemId == value){
				                    	if (listInv[k].expireDate != null && listInv[k].expireDate != undefined && listInv[k].expireDate != ''){
				                    		var tmpDate = new Date(listInv[k].expireDate);
					                        return '<span style="text-align: right" class="focus-color">' + jOlbUtil.dateTime.formatDate(tmpDate) + '</span>';
				                    	} else {
				                    		return '<span style="text-align: right" class="focus-color">' + uiLabelMap.AnInvItemMissExpiredDate + '</span>';
				                    	}
			                        }
				                }
			            		if (listInv.length > 0){
			            			$('#jqxgridDlvItem').jqxGrid('setcellvaluebyid', row, 'inventoryItemId', listInv[0].inventoryItemId);
				            		var tmpDate = new Date(listInv[0].expireDate);
			                        return '<span style="text-align: right" class="focus-color">' + jOlbUtil.dateTime.formatDate(tmpDate) + '</span>';
			            		} else {
			            			return '<span title="' + uiLabelMap.NotEnough + '" style="text-align: left" class="warning-color">' + uiLabelMap.NotEnough + '</span>';
			            		}
			            	} else {
			            		var check = false;
			            		for(k = 0; k < listInv.length; k++){
				                    if(listInv[k].inventoryItemId == value){
				                    	check = true;
				                    	if (listInv[k].expireDate != null && listInv[k].expireDate != undefined && listInv[k].expireDate != ''){
				                    		var tmpDate = new Date(listInv[k].expireDate);
					                        return '<span style="text-align: right">' + jOlbUtil.dateTime.formatDate(tmpDate) + '</span>';
				                    	} else {
				                    		return '<span style="text-align: right">' + uiLabelMap.AnInvItemMissExpiredDate + '</span>';
				                    	}
				                    }
				                }
			            		if (check == false){
			            			if (data.actualExpireDate != null && data.actualExpireDate != undefined && data.actualExpireDate != ''){
			            				var x = null;
			            				if (typeof (data.actualExpireDate) == 'string'){
			            					x = new Date(parseFloat(data.actualExpireDate));
			            				} else {
			            					x = new Date(data.actualExpireDate);
			            				}
			            				if (x != null){
			            					return '<span style="text-align: right">' + jOlbUtil.dateTime.formatDate(x) + '</span>';
			            				}
			            				return '<span style="text-align: right">' + uiLabelMap.AnInvItemMissExpiredDate + '</span>';
			            			} else {
			            				return '<span style="text-align: right">' + uiLabelMap.AnInvItemMissExpiredDate + '</span>';
			            			}
			            		}
			            	}
			            } else {
			            	if (data.productCode){
			            		if (data.statusId == 'DELI_ITEM_APPROVED'){
			            			if (data.actualExportedQuantity > 0){
			            				var id = data.uid;
			            				var requireAmount = data.requireAmount;
					            		var check = false;
								 		for(i = 0; i < listInv.length; i++){
								 			var qoh = listInv[i].quantityOnHandTotal;
								 			if (requireAmount && requireAmount == 'Y') {
								 				qoh = listInv[i].amountOnHandTotal;
								 			}
								 			if (data.productId == listInv[i].productId && qoh >= data.actualExportedQuantity){
								 				$('#jqxgridDlvItem').jqxGrid('setcellvaluebyid', id, 'inventoryItemId', listInv[i].inventoryItemId);
								 				check = true;
								 				if (listInv[i].expireDate) {
								 					var tmpDate = new Date(listInv[i].expireDate);
								 					return '<span style="text-align: right" class="focus-color">' + jOlbUtil.dateTime.formatDate(tmpDate) + '</span>';
								 				} else {
								 					return '<span style="text-align: right" class="focus-color">' + uiLabelMap.AnInvItemMissExpiredDate + '</span>';
								 				} 
								 			} else {
								 				check = false;
								 			}
								 		}
								 		if (check == false){
								 			return '<span title="' + uiLabelMap.NotEnoughDetail + ': ' + data.actualExportedQuantity + '" style="text-align: left" class="warning-color">' + uiLabelMap.NotEnough + '</span>';
								 		}
			            			} else {
			            				return '<span class="focus-color">' + uiLabelMap.PleaseSelectTitle + '</span>';
			            			}
				            	} else {
				            		return '<span style="text-align: right;"></span>';
				            	}
			            	} else {
								return '<span style="text-align: right;">...</span>';
							}
			            }
			            return '<span></span>';
			        }
			        var tmpDate = new Date(data.actualExpireDate);
			        return '<span style="text-align: right">' + jOlbUtil.dateTime.formatDate(tmpDate) + '</span>';
			    }, 
			    initeditor: function(row, value, editor){
				    var data = $('#jqxgridDlvItem').jqxGrid('getrowdata', row);
				    var uid = data.uid;
				    var invData = [];
				    var iIndex = 0;
				    var curInvId = null;
				    if (data.inventoryItemId != null && data.inventoryItemId != undefined){
				    	curInvId = data.inventoryItemId;
				    }
				    var invEnoughTmp = null;
				    
				    var requireAmount = data.requireAmount;
			 			
			        for(i = 0; i < listInv.length; i++){
			            if(listInv[i].productId == data.productId){
			            	var qoh = listInv[i].quantityOnHandTotal;
				 			if (requireAmount && requireAmount == 'Y') {
				 				qoh = listInv[i].amountOnHandTotal;
				 			}
			            	if (qoh >= data.actualExportedQuantity){
			            		invEnoughTmp = listInv[i].inventoryItemId;
			            	}
			                var tmpDate ;
			                var tmpValue = new Object();
			                
			                if(listInv[i].expireDate != null){
			                    tmpDate = new Date(listInv[i].expireDate);
			                    tmpValue.expireDate =  jOlbUtil.dateTime.formatDate(tmpDate);
			                }else{
			                    tmpValue.expireDate = uiLabelMap.ProductMissExpiredDate;
			                }
			                if(listInv[i].datetimeManufactured != null){
			                    tmpDate = new Date(listInv[i].datetimeManufactured);
			                    tmpValue.datetimeManufactured =  jOlbUtil.dateTime.formatDate(tmpDate);
			                }else{
			                    tmpValue.datetimeManufactured = uiLabelMap.ProductMissDatetimeManufactured;
			                }
			                
			                if(listInv[i].datetimeReceived != null){
			                    tmpDate = new Date(listInv[i].datetimeReceived);
			                    tmpValue.receivedDate =  jOlbUtil.dateTime.formatDate(tmpDate);
			                }else{
			                    tmpValue.receivedDate = uiLabelMap.ProductMissDatetimeReceived;
			                }
			                
			                tmpValue.inventoryItemId = listInv[i].inventoryItemId;
			                tmpValue.productId = listInv[i].productId;
			                tmpValue.quantityOnHandTotal = qoh;
			                tmpValue.availableToPromiseTotal = listInv[i].availableToPromiseTotal;
			                
			                tmpValue.quantityCurrent = qoh;
			                
			                var qtyUom = getUomDescription(listInv[i].quantityUomId);
			                
			                tmpValue.qtyUom = qtyUom;
			                invData[iIndex++] = tmpValue;
			            }
			        }
			        if (invData.length <= 0){
			        	editor.jqxDropDownList({ placeHolder: uiLabelMap.PleaseSelectTitle, source: invData, dropDownWidth: '660px', popupZIndex: 755, displayMember: 'expireDate', valueMember: 'inventoryItemId',
				            renderer: function(index, label, value) {
				                var item = editor.jqxDropDownList('getItem', index);
				                return '<span>[<span style="color:blue;">'+uiLabelMap.ExpiredDateSum+':</span>&nbsp;' + item.originalItem.expireDate + ']&nbsp; - &nbsp;[<span style="color:blue;">' + uiLabelMap.ManufacturedDateSum + ':</span>&nbsp' + item.originalItem.datetimeManufactured + ']&nbsp; - &nbsp;[<span style="color:blue;">' + uiLabelMap.ReceivedDateSum + ':</span>&nbsp' + item.originalItem.receivedDate + ']&nbsp; - &nbsp;[<span style="color:blue;">QOH:</span>&nbsp' + formatnumber(item.originalItem.quantityOnHandTotal)  + ']&nbsp; - &nbsp;[<span style="color:blue;">CUR:</span>&nbsp' + formatnumber(item.originalItem.quantityCurrent)  + ']</span>';
				            },
				        });
			        } else {
			        	if (curInvId != null){
			        		 var curInv = null;
			        		 var curQuantity = 0;
			        		 var allrowTmp = $('#jqxgridDlvItem').jqxGrid('getrows');
		        			 for(var j = 0; j < allrowTmp.length; j++){
		        				 if (allrowTmp[j].inventoryItemId == curInvId){
		        					 curQuantity = curQuantity + allrowTmp[j].actualExportedQuantity;
		        				 }
		        			 }
			        		 for(var i = 0; i < invData.length;i++){
			        			 if(invData[i].inventoryItemId == curInvId){
			        				 invData[i].quantityCurrent = invData[i].quantityCurrent - curQuantity;
			        			 } else {
			        				 var qtyOfOtherInv = 0;
			        				 for(var j = 0; j < allrowTmp.length; j++){
				        				 if (allrowTmp[j].inventoryItemId == invData[i].inventoryItemId){
				        					 qtyOfOtherInv = qtyOfOtherInv + allrowTmp[j].actualExportedQuantity;
				        				 }
				        			 }
			        				 invData[i].quantityCurrent = invData[i].quantityCurrent - qtyOfOtherInv;
			        			 }
			        		 }
			        		 editor.jqxDropDownList({ placeHolder: uiLabelMap.PleaseSelectTitle, source: invData, selectedIndex: 0, dropDownWidth: '660px', popupZIndex: 755, displayMember: 'expireDate', valueMember: 'inventoryItemId',
		        			 	renderer: function(index, label, value) {
					                var item = editor.jqxDropDownList('getItem', index);
					                return '<span>[<span style="color:blue;">'+uiLabelMap.ExpiredDateSum+':</span>&nbsp;' + item.originalItem.expireDate + ']&nbsp; - &nbsp;[<span style="color:blue;">' + uiLabelMap.ManufacturedDateSum + ':</span>&nbsp' + item.originalItem.datetimeManufactured + ']&nbsp; - &nbsp;[<span style="color:blue;">' + uiLabelMap.ReceivedDateSum + ':</span>&nbsp' + item.originalItem.receivedDate + ']&nbsp; - &nbsp;[<span style="color:blue;">QOH:</span>&nbsp' + formatnumber(item.originalItem.quantityOnHandTotal)  + ']&nbsp; - &nbsp;[<span style="color:blue;">CUR:</span>&nbsp' + formatnumber(item.originalItem.quantityCurrent)  + ']</span>';
					            },
			        		 });
			        		 editor.jqxDropDownList('selectItem', curInvId);
			        	} else {
			        		editor.jqxDropDownList({ placeHolder: uiLabelMap.PleaseSelectTitle, source: invData, selectedIndex: 0, dropDownWidth: '660px', popupZIndex: 755, displayMember: 'expireDate', valueMember: 'inventoryItemId',
		        			 	renderer: function(index, label, value) {
					                var item = editor.jqxDropDownList('getItem', index);
					                return '<span>[<span style="color:blue;">'+uiLabelMap.ExpiredDateSum+':</span>&nbsp;' + item.originalItem.expireDate + ']&nbsp; - &nbsp;[<span style="color:blue;">' + uiLabelMap.ManufacturedDateSum + ':</span>&nbsp' + item.originalItem.datetimeManufactured + ']&nbsp; - &nbsp;[<span style="color:blue;">' + uiLabelMap.ReceivedDateSum + ':</span>&nbsp' + item.originalItem.receivedDate + ']&nbsp; - &nbsp;[<span style="color:blue;">QOH:</span>&nbsp' + formatnumber(item.originalItem.quantityOnHandTotal)  + ']&nbsp; - &nbsp;[<span style="color:blue;">CUR:</span>&nbsp' + formatnumber(item.originalItem.quantityCurrent)  + ']</span>';
					            },
			        		});
			        		if (invEnoughTmp != null){
			        			editor.jqxDropDownList('selectItem', invEnoughTmp);
			        		} else {
			        			for(var i = 0; i < invData.length;i++){
						            var tmpDate = new Date(data.actualExpireDate);
						            var tmpStr = jOlbUtil.dateTime.formatDate(tmpDate);
						            if((invData[i].productId = data.productId) && (tmpStr = data.actualExpireDate)){
						                editor.jqxDropDownList('selectItem', invData[i].inventoryItemId);
						                break;
						            }
						        }
			        		}
			        	}
			        	
			        }
			    },
			    validation: function (cell, value) {
			    	if (listInv.length < 1){
			    		return { result: false, message: uiLabelMap.FacilityNotEnoughProduct};
				    }
			        if (value == null || value == undefined || value == '') {
			            return { result: false, message: uiLabelMap.DmsFieldRequired};
			        }
			        return true;
			    },
			    cellbeginedit: function (row, datafield, columntype) {
			    	var data = $('#jqxgridDlvItem').jqxGrid('getrowdata', row);
					if (data.statusId != 'DELI_ITEM_APPROVED' || listInv.length == 0){
						return false;
					}else{
			            return true;
					}
			    },
			},
			{ text: uiLabelMap.ActualExportedQuantity, dataField: 'actualExportedQuantity', columntype: 'numberinput', width: 150, cellsalign: 'right', editable: true, sortable: false,
				cellbeginedit: function (row, datafield, columntype) {
					var data = $('#jqxgridDlvItem').jqxGrid('getrowdata', row);
					if(data.statusId != 'DELI_ITEM_APPROVED'){
						return false;
					}else{
                        return true;
					}
			    },
			    initeditor: function(row, value, editor){
			        var data = $('#jqxgridDlvItem').jqxGrid('getrowdata', row);
			        if(data.statusId == 'DELI_ITEM_EXPORTED'){
                        editor.jqxNumberInput({disabled: true});
                    }else{
                        editor.jqxNumberInput({disabled: false});
                        if (data.requireAmount && data.requireAmount == 'Y') {
                        	editor.jqxNumberInput({decimalDigits: 2, digits: 9, spinMode: 'simple'});
						} else {
							editor.jqxNumberInput({decimalDigits: 0, digits: 9, spinMode: 'simple'});
						}
                        if (value != null && value != undefined){
                        	editor.jqxNumberInput('val', value);
                        } else {
                        	if (data.productCode != null && data.productCode != undefined){
                        		if (data.quantity){
                        			if (data.requireAmount && data.requireAmount == 'Y') {
                        				editor.jqxNumberInput('val', data.quantity);
                        			} else {
                        				editor.jqxNumberInput('val', data.amount);
                        			}
                                }
                        	} else {
                        		editor.jqxNumberInput('val', 0);
                        	}
                        }
                    }
			    },
			    validation: function (cell, value) {
			        if (value < 0) {
			            return { result: false, message: uiLabelMap.NumberGTZ};
			        }
			        var dataTmp = $('#jqxgridDlvItem').jqxGrid('getrowdata', cell.row);
			        var prCode = dataTmp.productCode;
			        var rows = $('#jqxgridDlvItem').jqxGrid('getrows');
					
			        var listByPr = [];
			        for (var i = 0; i < rows.length; i ++){
			        	if (prCode == rows[i].productCode){
							 listByPr.push(rows[i]);
			        	} 
			        }
			        
			        var allDlvQty = 0;
			        for (var i = 0; i < listDeliveryItemData.length; i ++){
			        	if (listDeliveryItemData[i].productCode == prCode){
			        		if (dataTmp.requireAmount && dataTmp.requireAmount == 'Y') {
			        			allDlvQty = allDlvQty + listDeliveryItemData[i].amount;
			        		} else {
			        			allDlvQty = allDlvQty + listDeliveryItemData[i].quantity;
			        		}
			        	}
					}
			        var curQty = 0;
			        for (var i = 0; i < listByPr.length; i ++){
			        	if (listByPr[i].productCode == prCode){
			        		curQty = curQty + listByPr[i].actualExportedQuantity;
			        	}	
			        }
			        var totalCreated = 0;
			        if (dataTmp.requireAmount && dataTmp.requireAmount == 'Y') {
			        	totalCreated = curQty + value - dataTmp.actualExportedQuantity;
			        	totalCreated = Math.round(totalCreated * 100) / 100;
			        } else {
			        	totalCreated = curQty + value - parseInt(dataTmp.actualExportedQuantity);
			        }
			        
			        if (totalCreated - allDlvQty > 0){
			        	return { result: false, message: uiLabelMap.QuantityGreateThanQuantityCreatedInSalesDelivery +': ' + totalCreated + ' > ' + allDlvQty};
			        }
			        return true;
			    },
			    cellsrenderer: function (row, column, value){
			    	var tmp = null;
				 	var data = $('#jqxgridDlvItem').jqxGrid('getrowdata', row);
				 	if (value === null || value === undefined || value === ''){
				 		if (data.productCode){
				 			if (data.statusId == 'DELI_ITEM_APPROVED'){
				 				var id = data.uid;
                        		var orderQty = data.quantity;
						 		$('#jqxgridDlvItem').jqxGrid('setcellvaluebyid', id, 'actualExportedQuantity', orderQty);
						 		return '<span style="text-align: right;" class="focus-color" title=' + formatnumber(orderQty) + '>' + formatnumber(orderQty) + '</span>';
				 				
				 			} else {
				 				return '<span style="text-align: right;" class="focus-color" title=' + 0 + '>' + 0 + '</span>';
				 			}
				 		} else {
							return '<span style="text-align: right;">...</span>';
						}
				 	}
				 	if (data.statusId == 'DELI_ITEM_APPROVED'){
				 		return '<span style="text-align: right;" class="focus-color" title=' + formatnumber(value) + '>' + formatnumber(value) + '</span>'
				 	} else {
				 		var data = $('#jqxgridDlvItem').jqxGrid('getrowdata', row);
				 		var requireAmount = data.requireAmount;
				 		if (requireAmount && requireAmount == 'Y') {
				 			return '<span style="text-align: right" title=' + formatnumber(data.actualExportedAmount) + '>' + formatnumber(data.actualExportedAmount) + '</span>';
				 		}
				 		return '<span style="text-align: right" title=' + formatnumber(value) + '>' + formatnumber(value) + '</span>'
				 	}
		    	}
		  	},
		  	{ text: uiLabelMap.ActualDeliveredQuantity, columntype: 'numberinput',  cellsalign: 'right', dataField: 'actualDeliveredQuantity', width: 150, editable: true,
		  		cellbeginedit: function (row, datafield, columntype) {
					var data = $('#jqxgridDlvItem').jqxGrid('getrowdata', row);
					if(data.statusId != 'DELI_ITEM_EXPORTED'){
						return false;
					} else{
                        return true;
                    }
				 }, 
				 initeditor: function(row, value, editor){
                    var data = $('#jqxgridDlvItem').jqxGrid('getrowdata', row);
                	if(data.statusId != 'DELI_ITEM_EXPORTED'){
                		editor.jqxNumberInput({disabled: true});
                    } else{
                        editor.jqxNumberInput({disabled: false});
                        if (null === value || value === undefined){
                        	if (data.actualExportedQuantity){
                        		if (data.actualExportedQuantity > 0){
                                	editor.jqxNumberInput('val', data.actualExportedQuantity);
                                } else {
                                	editor.jqxNumberInput({disabled: true});
                                }
                        	}
                        } else {
                        	if (data.actualExportedQuantity > 0){
                            	editor.jqxNumberInput('val', data.actualExportedQuantity);
                            } else {
                            	editor.jqxNumberInput({disabled: true});
                            }
                        }
                    }
                 },
                 validation: function (cell, value) {
			        if (value < 0) {
			            return { result: false, message: uiLabelMap.NumberGTZ};
			        }
			        return true;
				 },
				 cellsrenderer: function (row, column, value){
				 	var tmp = null;
				 	var data = $('#jqxgridDlvItem').jqxGrid('getrowdata', row);
				 	if (null === value || value === undefined || value === ''){
				 		if (data.productCode){
                    		if (data.statusId == 'DELI_ITEM_EXPORTED'){
                    			var id = data.uid;
                        		var actualExprt = data.actualExportedQuantity;
                        		if (actualExprt == 0){
                        			$('#jqxgridDlvItem').jqxGrid('setcellvaluebyid', id, 'actualDeliveredQuantity', actualExprt);
							 		return '<span style="text-align: right" title=' + formatnumber(actualExprt) + '>' + formatnumber(actualExprt) + '</span>';
                        		} else {
                        			$('#jqxgridDlvItem').jqxGrid('setcellvaluebyid', id, 'actualDeliveredQuantity', actualExprt);
							 		return '<span style="text-align: right" class="focus-color" title=' + formatnumber(actualExprt) + '>' + formatnumber(actualExprt) + '</span>';
                        		}
                    		} else {
                    			return '<span style="text-align: right" title=' + 0 + '>' + 0 + '</span>';
                    		}
				 		} else {
							return '<span style="text-align: right;">...</span>';
				 		}
				 	}
				 	if (data.statusId == 'DELI_ITEM_EXPORTED'){
				 		var actualExprt = data.actualExportedQuantity;
                		if (actualExprt == 0){
                			return '<span style="text-align: right;" title=' + formatnumber(value) + '>' + formatnumber(value) + '</span>';
                		} else {
                			return '<span style="text-align: right;" class="focus-color" title=' + formatnumber(value) + '>' + formatnumber(value) + '</span>';
                		}
				 	} else {
				 		var data = $('#jqxgridDlvItem').jqxGrid('getrowdata', row);
				 		var requireAmount = data.requireAmount;
				 		if (requireAmount && requireAmount == 'Y') {
				 			return '<span style="text-align: right" title=' + formatnumber(data.actualDeliveredAmount) + '>' + formatnumber(data.actualDeliveredAmount) + '</span>';
				 		}
				 		return '<span style="text-align: right" title=' + formatnumber(value) + '>' + formatnumber(value) + '</span>'
				 	}
				 	return '<span style="text-align: right" title=' + formatnumber(value) + '>' + formatnumber(value) + '</span>';
				 }
			 },
			 { text: uiLabelMap.RequiredExpireDate, dataField: 'expireDate', width: 150, cellsformat: 'dd/MM/yyyy', editable: false, cellsalign: 'right',
				 cellsrenderer: function(row, column, value){
					 var data = $('#jqxgridDlvItem').jqxGrid('getrowdata', row);
					 if (value === null || value === undefined || value === ''){
						 if (data.productCode){
							 return '<span style="text-align: right"></span>';
						 } else {
							 return '<span style="text-align: right;">...</span>';
						 }
					 } else {
						 return '<span style="text-align: right">'+ jOlbUtil.dateTime.formatDate(value)+'</span>';
					 }
				 }
			},
		 	{ text: uiLabelMap.Status, dataField: 'statusId', width: 150, editable: false,
				 cellsrenderer: function(row, column, value){
					 var data = $('#jqxgridDlvItem').jqxGrid('getrowdata', row);
					 if (value === null || value === undefined || value === ''){
						 if (data.productCode){
							 return '<span style="text-align: right"></span>';
						 } else {
							 return '<span style="text-align: right;">...</span>';
						 }
					 } else {
						 for(var i = 0; i < dlvItemStatusData.length; i++){
							 if(value == dlvItemStatusData[i].statusId){
								 return '<span title=' + value + '>' + dlvItemStatusData[i].description + '</span>';
							 }
						 }
					 }
				 }
	 		},
 		]
		});
	}
	
	var afterAddDelivery = function afterAddDelivery(){
		$("#jqxgridDelivery").jqxGrid('updatebounddata');
	}
	
	function checkAllTransferItemCreatedDone(){
		var createdDone = true;
		setTimeout(function(){
		$.ajax({
			type: 'POST',
			async: false,
			url: 'checkAllTransferItemCreatedDelivery',
			data: {
				transferId: transferId,
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
		}).done(function (){
			
		});
		}, 600);
	}
	
	function addZero(i) {
	    if (i < 10) {
	        i = "0" + i;
	    }
	    return i;
	}
	
	function checkTransferStatus(){
		var newStatusId = null;
		setTimeout(function(){
		$.ajax({
			type: 'POST',
			async: false,
			url: 'checkTransferStatus',
			data: {
				transferId: transferId,
			},
			success: function (res){
				newStatusId = res.statusId;
			}
		}).done(function (){
			if (newStatusId != curStatusId){
				window.location.replace("viewDetailTransfer?transferId="+transferId+"&activeTab=general-tab");
			}
		});
		}, 500);
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
				row["fromTransferId"] = dlvItem.fromTransferId;
				row["fromTransferItemSeqId"] = dlvItem.fromTransferItemSeqId;
				row["inventoryItemId"] = null;
				row["deliveryId"] = dlvItem.deliveryId;
				row["deliveryItemSeqId"] = null;
				row["actualExportedQuantity"] = 0;
				row["actualDeliveredQuantity"] = 0;
				row["actualExportedAmount"] = 0;
				row["actualDeliveredAmount"] = 0;
				row["quantity"] = dlvItem.quantity;
				row["weight"] = dlvItem.weight;
				row["amount"] = dlvItem.amount;
				row["requireAmount"] = dlvItem.requireAmount;
				row["quantityUomId"] = dlvItem.quantityUomId;
				row["statusId"] = "DELI_ITEM_APPROVED";
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
			row["fromTransferId"] = "";
			row["fromTransferItemSeqId"] = "";
			row["inventoryItemId"] = "";
			row["deliveryId"] = "";
			row["deliveryItemSeqId"] = "";
			row["actualExportedQuantity"] = "";
			row["actualDeliveredQuantity"] = "";
			row["actualExportedAmount"] = "";
			row["actualDeliveredAmount"] = "";
			row["quantity"] = "";
			row["amount"] = "";
			row["quantityUomId"] = "";
			row["statusId"] = "";
			row["batch"] = "";
			row["expireDate"] = "";
			row["requireAmount"] = "";
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
	
	function getInventory(facilityId, deliveryId){
    	$.ajax({
            type: "POST",
            url: "getInvByTransferAndDlv",
            data: {'facilityId': facilityId, 'deliveryId': deliveryId},
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
	
	var quickCreateTransferDelivery = function (transferId){
		bootbox.dialog(uiLabelMap.AreYouSureCreate, 
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
					        url: "quickCreateTransferDelivery",
					        type: "POST",
					        async: false,
					        data: {
					        	transferId: transferId,
					        },
					        success: function(res) {
					        	dlvId = res.deliveryId;
					        	$("#jqxgridDelivery").jqxGrid('updatebounddata'); 
					        	checkAllTransferItemCreatedDone();
					        },
					    });
					Loading.hide('loadingMacro');
		    	}, 500);
            }
        }]);
	};
	
	var showDetailDelivery = function (deliveryId){
		location.replace("deliveryTransferDeliveryDetail?deliveryId="+deliveryId);
	}
	
	return {
		init: init,
		initAttachFile: initAttachFile,
		showDetailPopup: showDetailPopup,
		loadTransferItem: loadTransferItem,
		afterAddDelivery: afterAddDelivery,
		addNewRow: addNewRow,
		updateRowData: updateRowData,
		showDetailDelivery:showDetailDelivery,
		quickCreateTransferDelivery: quickCreateTransferDelivery,
	};
}());