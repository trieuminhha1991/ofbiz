$(function(){
	PODlvObj.init();
});
var PODlvObj = (function(){
	// TODO var locationProductSelected = null;
	var addClick = false;
	var listProductAddGrid = [];
	var listProductEditGrid = [];
	var dlvSelected = null;
	var btnClick = false;
	var confirmClick = false;
	var init = function(){
		initInputs();
		initElementComplex();
		initEvents();
		if ($("#alterpopupWindow").length > 0) {
			initValidate($("#alterpopupWindow"));
		}
		initValidateDateDT($("#detailpopupWindow"));
		initAttachFile();
		initConfirmGrid();
		$("#jqxgrid2").show();
		$("#jqxgridConfirm").hide();
	};
	var initInputs = function(){	
		if ($("#DeliveryMenu").length > 0){
			$("#DeliveryMenu").jqxMenu({ width: 220, autoOpenPopup: false, mode: 'popup', theme: theme});
		}
		$("#detailpopupWindow").jqxWindow({
			maxWidth: 1500, width: 1300, height:630, maxHeight: 800, resizable: false, isModal: true, modalZIndex: 100000, zIndex: 100000, autoOpen: false, cancelButton: $("#detailCancel"), modalOpacity: 0.7, theme:theme
		});
		$('#actualArrivalDate').jqxDateTimeInput({width: 200, disabled: true, height: 25, formatString: 'dd/MM/yyyy HH:mm:ss'});
		$('#actualStartDate').jqxDateTimeInput({width: 200, disabled: true, height: 25, formatString: 'dd/MM/yyyy HH:mm:ss'});
		
		$("#editWindow").jqxWindow({
			maxWidth: 1300, minWidth: 500, width: 1200, height: 480, minHeight: 100, maxHeight: 700, resizable: false, isModal: true, modalZIndex: 100000, zIndex: 100000, autoOpen: false, cancelButton: $("#editCancel"), modalOpacity: 0.7, theme:theme           
		});
		$("#invoiceId").jqxInput({width: 195, height: 20});
		$("#editAddProductWindow").jqxWindow({
			maxWidth: 1300, minWidth: 500, width: 1000, height: 460, minHeight: 100, maxHeight: 700, resizable: false, isModal: true, modalZIndex: 100000, zIndex: 100000, autoOpen: false, cancelButton: $("#editAddProductCancel"), modalOpacity: 0.7, theme:theme           
		});
		
		// TODO $("#locationProduct").jqxDropDownButton({width: 200}); 
		// TODO $('#locationProduct').jqxDropDownButton('setContent', '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+uiLabelMap.PleaseSelectTitle+'</div>');
		if ($("#facility").length > 0){
			$("#facility").jqxDropDownButton({width: 350, theme: theme, popupZIndex: 100001});
			$('#facility').jqxDropDownButton('setContent', '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+uiLabelMap.PleaseSelectTitle+'</div>');
		}
		if ($("#facilityPopup").length > 0){
			$("#facilityPopup").jqxDropDownButton({width: 200, theme: theme, popupZIndex: 100001});
			$('#facilityPopup').jqxDropDownButton('setContent', '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+uiLabelMap.PleaseSelectTitle+'</div>');
		}
	};
	var initElementComplex = function(){
		/* TODO initGridLocationProduct($('#jqxgridLocationProduct')); using location*/
		if ($("#jqxgridFacility").length > 0){
			initFacilityGrid($("#jqxgridFacility"));
		}
		if ($("#jqxgridFacilityPopup").length > 0){
			initFacilityGrid($("#jqxgridFacilityPopup"));
		}
	};
	
	var initGridLocationProduct = function(grid){
		var datafield = [{ name: 'locationId', type: 'string' },
		                 { name: 'locationCode', type: 'string' },
		                 { name: 'locationFacilityTypeId', type: 'string' },
		                 { name: 'description', type: 'string' },
		                 { name: 'parentLocationCode', type: 'string' },
		                 { name: 'parentLocationId', type: 'string' },
		                 ];
		var columns = [
					{ text: uiLabelMap.SequenceId, sortable: false, filterable: false, editable: false, pinned: true,
					    groupable: false, draggable: false, resizable: false,
					    datafield: '', columntype: 'number', width: 50,
					    cellsrenderer: function (row, column, value) {
					        return '<div style=margin:4px;>' + (value + 1) + '</div>';
					    }
					},		
					{ text: uiLabelMap.BLLocationCode, dataField: 'locationCode', width: 150, editable: false, pinned: true},
					{ text: uiLabelMap.Description, dataField: 'description', minwidth: 150, editable: false,},
		               ];
		var config = {
				width: '90%', 
				virtualmode: true,
				showtoolbar: false,
				selectionmode: 'singlerow',
				pageable: true,
				sortable: true,
				filterable: true,	        
				editable: false,
				rowsheight: 26,
				rowdetails: false,
				useUrl: true,
				url: 'jqGetAllLocationLeafNodeDetail&locationFacilityTypeId=RECEIVE_AREA',                
				source: {pagesize: 10}
		};
		
		Grid.initGrid(config, datafield, columns, null, grid);
	};
	
	var initConfirmGrid = function(){
		var datafieldCf =  [
				{ name: 'deliveryId', type: 'string' },
				{ name: 'deliveryItemSeqId', type: 'string' },
				{ name: 'fromOrderItemSeqId', type: 'string' },
				{ name: 'fromTransferItemSeqId', type: 'string' },
				{ name: 'fromOrderId', type: 'string' },
				{ name: 'fromTransferId', type: 'string' },
				{ name: 'productId', type: 'string' },
				{ name: 'productCode', type: 'string' },
				{ name: 'productName', type: 'string' },
				{ name: 'quantityUomId', type: 'string' },
				{ name: 'batch', type: 'string' },
				{ name: 'isPromo', type: 'string' },
				{ name: 'comment', type: 'string' },
				{ name: 'actualExportedQuantity', type: 'number' },
				{ name: 'actualDeliveredQuantity', type: 'number' },
				{ name: 'actualDeliveredQuantityQC', type: 'number' },
				{ name: 'actualDeliveredQuantityEA', type: 'number' },
				{ name: 'actualExportedAmount', type: 'number' },
				{ name: 'actualDeliveredAmount', type: 'number' },
				{ name: 'statusId', type: 'string' },
				{ name: 'amount', type: 'number' },
				{ name: 'quantity', type: 'number' },
				{ name: 'inventoryItemId', type: 'string' },
				{ name: 'actualExpireDate', type: 'date', other: 'Timestamp'},
				{ name: 'expireDate', type: 'date', other: 'Timestamp'},
				{ name: 'actualManufacturedDate', type: 'date', other: 'Timestamp'},
				{ name: 'deliveryStatusId', type: 'string'},
				{ name: 'weight', type: 'number'},
				{ name: 'productWeight', type: 'number'},
				{ name: 'weightUomId', type: 'String'},
				{ name: 'expRequired', type: 'String'},
				{ name: 'mnfRequired', type: 'String'},
				{ name: 'lotRequired', type: 'String'},
				{ name: 'defaultWeightUomId', type: 'String'},
				{ name: 'UPCACode', type: 'String'},
				{ name: 'requireAmount', type: 'String'},
				{ name: 'quantityUomIds', type: 'String'},
				{ name: 'weightUomIds', type: 'String'},
				{ name: 'orderWeightUomId', type: 'String'},
				{ name: 'orderQuantityUomId', type: 'String'},
				{ name: 'locationCode', type: 'String'},
				{ name: 'convertNumber', type: 'number'},
      	];
      	var columnlistCf = [
                { text: uiLabelMap.SequenceId, sortable: false, filterable: false, editable: false, pinned: true,
				    groupable: false, draggable: false, resizable: false,
				    datafield: '', columntype: 'number', width: 50,
				    cellsrenderer: function (row, column, value) {
				        return '<div style=margin:4px;>' + (value + 1) + '</div>';
				    }
				},
				{ text: uiLabelMap.ProductId, dataField: 'productCode', width: 120, editable: true, pinned: true,},
				{ text: uiLabelMap.ProductName, dataField: 'productName', minwidth: 120, editable: true, pinned: true,},
				{ text: uiLabelMap.BLPackingForm, dataField: 'convertNumber', width: 100, editable: false,
					cellsrenderer: function(row, column, value){
						return '<span class="align-right" title=' + formatnumber(value) + '>' + formatnumber(value) + '</span>'
					}
				},
				{ text: uiLabelMap.BLQuantityByQCUom, columntype: 'numberinput',  cellsalign: 'right', dataField: 'actualDeliveredQuantityQC', width: 130, editable: true,
					cellsrenderer: function(row, column, value){
						return '<span class="align-right" title=' + formatnumber(value) + '>' + formatnumber(value) + '</span>'
					}
				},
				{ text: uiLabelMap.BLQuantityByEAUom, columntype: 'numberinput', hidden:true, cellsalign: 'right', dataField: 'actualDeliveredQuantityEA', width: 130, editable: true,
					cellsrenderer: function(row, column, value){
						return '<span class="align-right" title=' + formatnumber(value) + '>' + formatnumber(value) + '</span>'
					}
				},
				{ text: uiLabelMap.BLQuantityEATotal, columntype: 'numberinput',  cellsalign: 'right', dataField: 'actualDeliveredQuantity', width: 120, editable: false,
					 cellsrenderer: function (row, column, value){
						 var data = $("#jqxgridConfirm").jqxGrid('getrowdata', row);
						 if (data.requireAmount && data.requireAmount == 'Y'){
							 value = data.actualDeliveredQuantityEA;
						 }
						 return '<span class="align-right" title=' + formatnumber(value) + '>' + formatnumber(value) + '</span>'
				 	}
				},
				{ text: uiLabelMap.ManufacturedDateSum, dataField: 'actualManufacturedDate', columntype: 'datetimeinput', cellsformat: 'dd/MM/yyyy', width: 120, editable: true, sortable: false,
					cellsrenderer: function (row, column, value){
						if (value){
							return '<span class="align-right">' + DatetimeUtilObj.getFormattedDate(new Date(value)) + '</span>';
						} else {
							return '<span class="align-right"></span>';
						}
					},
				},
				{ text: uiLabelMap.ExpiredDateSum, dataField: 'actualExpireDate', columntype: 'datetimeinput', cellsformat: 'dd/MM/yyyy', width: 120, editable: true, sortable: false,
					cellsrenderer: function (row, column, value){
						if (value){
							return '<span class="align-right">' + DatetimeUtilObj.getFormattedDate(new Date(value)) + '</span>';
						} else {
							return '<span class="align-right"></span>';
						}
					}
				},
				{ text: uiLabelMap.Batch, dataField: 'batch', width: 120, editable: true,
					 cellsrenderer: function(row, column, value){
				 		return '<span class="align-right" title=' + value + '>' + value + '</span>'
					 }
				},
				{ text: uiLabelMap.BLLocationCode, dataField: 'locationCode', width: 120, editable: true, hidden: true, 
					cellsrenderer: function(row, column, value){
						return '<span class="align-right" title=' + value + '>' + value + '</span>'
					}
				},
				{ text: uiLabelMap.IsPromo, dataField: 'isPromo', width: 120, editable: true, columntype: 'dropdownlist', 
					cellsrenderer: function(row, column, value){
					if (value)
						if (value == 'Y'){
							return '<span style=\"text-align: left\">'+uiLabelMap.LogYes+'</span>';
						}
						if (value == 'N'){
							return '<span style=\"text-align: left\">'+uiLabelMap.LogNO+'</span>';
						}
					}
				},
      	];
      	
      	var configCf = {
  			width: '100%', 
	   		virtualmode: false,
	   		showtoolbar: false,
	   		selectionmode: 'singlerow',
	   		pageable: true,
	   		sortable: false,
	        filterable: false,	        
	        height: 345,
	        autoheight: false,
	        editable: false,
	        rowsheight: 26,
	        useUrl: false,
	        source: {pagesize: 10}
      	};
      	Grid.initGrid(configCf, datafieldCf, columnlistCf, null, $("#jqxgridConfirm"));
	};

	var initValidator = function() {
        $("#selectFacilityWindow").jqxValidator({
            rules: [
                {input : '#conversionFactor', message: uiLabelMap.ValueMustBeGreateThanZero, action : 'blur',
                    rule : function(input, commit){
                        if(input.val() <= 0){
                            return false;
                        }
                        return true;
                    }
                },
            ]});
        };
	
	var initEvents = function(){
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
			}, 'getFacilityContactMechs' , 'listFacilityContactMechs', 'contactMechId', 'address1', 'destContactMechId');
			
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
		
	/*	$("#jqxgridLocationProduct").on('rowselect', function (event) {
			var args = event.args;
	        var rowData = args.row;
	        var description = uiLabelMap.PleaseSelectTitle;
	        if (rowData){
	        	locationProductSelected = rowData;
	        	description = rowData.locationCode;
	        }
	        var dropDownContent = '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+ description +' </div>';
	        $('#locationProduct').jqxDropDownButton('setContent', dropDownContent);
	        $('#locationProduct').jqxDropDownButton('close');
	    });
	*/
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
					listProductEditGrid = listOrderItemTmps;
					loadEditGrid(listOrderItemTmps);
					$("#editWindow").jqxWindow('open');
				}
			});
		});
		
		$("#editAddProductWindow").on("close", function (event) {
			btnClick = false;
			confirmClick = false;
			$("#editAddProductGrid").jqxGrid("clear");
			$("#editAddProductGrid").jqxGrid("clearselection");
		});
		
		$("#editWindow").on("close", function (event) {
			listProductToAdd = [];
			$("#editGrid").jqxGrid('clear');
		});
		
		$("#editSave").click(function () {
			if (!btnClick){
				btnClick = true;
				bootbox.dialog(uiLabelMap.AreYouSureSave, 
				[{"label": uiLabelMap.CommonCancel, 
					"icon": "fa fa-remove", "class": "btn  btn-danger form-action-button pull-right",
					"callback": function () {bootbox.hideAll(); btnClick = false; confirmClick = false;}
				}, 
				{"label": uiLabelMap.OK,
					"icon": "fa-check", "class": "btn btn-primary form-action-button pull-right",
					"callback": function () {
						if (!confirmClick){
							Loading.show("loadingMacro");
							setTimeout(function () {
								saveEditDelivery();
								Loading.hide("loadingMacro");	
							}, 500);
							confirmClick = true;
						}
					}
				}]);
				listProductToAdd = [];
			}
		});
		
		$("#editGrid").on("cellendedit", function(event) {
			var args = event.args;
			var dataField = event.args.datafield;
			var rowBoundIndex = event.args.rowindex;
			var value = args.value;
			var oldValue = args.oldvalue;
			var rowData = args.row;
			if (rowData){
				var x = value - oldValue;
				for (var i in listProductToAdd) {
					if (listProductToAdd[i].fromOrderItemSeqId == rowData.fromOrderItemSeqId){
						listProductToAdd[i].quantity = listProductToAdd[i].quantity - x;
						listProductToAdd[i].newQuantity = listProductToAdd[i].newQuantity - x;
					}
				}
				
				if (rowData.newQuantity > 0){
			    	$.each(listProductEditGrid, function(i){
						var olb = listProductEditGrid[i];
						if (olb.productId == rowData.productId ){
							listProductEditGrid.splice(i,1);
							return false;
						}
					});
			    	listProductEditGrid.push(rowData);
			    }
			}
		});
		
		$("#editAddProductGrid").on("rowselect", function(event) {
			// event arguments.
		    var args = event.args;
		    // row's bound index.
		    var rowBoundIndex = args.rowindex;
		    var rowData = args.row;
		    if (rowData.newQuantity > 0){
		    	$.each(listProductAddGrid, function(i){
					var olb = listProductAddGrid[i];
					if (olb.productId == rowData.productId ){
						listProductAddGrid.splice(i,1);
						return false;
					}
				});
		    	listProductAddGrid.push(rowData);
		    }
		});
		
		$("#editAddProductSave").click(function () {
			if (!btnClick){
				btnClick = true;
				var newDataList = [];
				listProductToAdd = [];
				for (var x in listProductAddGrid){
					var data1 = listProductAddGrid[x];
					var obj = $.extend({}, data1);
					obj.newQuantity = obj.quantity - data1.newQuantity;
					obj.quantity = data1.quantity - data1.newQuantity;
					var quantity1 = data1.newQuantity;
					var orderItemSeqId1 = data1.fromOrderItemSeqId;
					var check = true;
					for (var y in listProductEditGrid){
						var data2 = listProductEditGrid[y];
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
				for (var x in listProductEditGrid){
					var obj = listProductEditGrid[x];
					for (var y in newDataList){
						if (listProductEditGrid[x].fromOrderItemSeqId == newDataList[y].fromOrderItemSeqId){
							obj = listProductEditGrid[x];
							break;
						}
					}
					listData.push(obj);
				}
				for (var x in newDataList){
					var obj = newDataList[x];
					var check = false;
					for (var y in listProductEditGrid){
						if (listProductEditGrid[y].fromOrderItemSeqId == newDataList[x].fromOrderItemSeqId){
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
			}
		});
		
		$("#jqxgridDelivery").on("rowclick", function (event) {
		    var args = event.args;
		    dlvSelected = args.row.bounddata;
		});
		
		if ($("#DeliveryMenu").length > 0){
			$("#DeliveryMenu").on('itemclick', function (event) {
				if (dlvSelected != null){
					var data = dlvSelected;
					var tmpStr = $.trim($(args).text());
					if(tmpStr == uiLabelMap.BSViewDetail){
						showDetailDelivery(data.deliveryId);
					} else if(tmpStr == uiLabelMap.BLQuickView){
						showDetailPopup(data.deliveryId, data.orderId);
					} else if (tmpStr == uiLabelMap.BSRefresh){
						$('#jqxgridDelivery').jqxGrid('updatebounddata');
					} else if (tmpStr == uiLabelMap.ReceiptNote){
						if (data.statusId == 'DLV_CANCELLED'){
							jOlbUtil.alert.error(uiLabelMap.ReceiptNote + " " + uiLabelMap.Canceled.toLowerCase());
							return false;
						}
						window.open('receipt.pdf?deliveryId='+data.deliveryId, '_blank');
					} else if (tmpStr == uiLabelMap.BLReceiptNoteWithPrice){
						if (data.statusId == 'DLV_CANCELLED'){
							jOlbUtil.alert.error(uiLabelMap.ReceiptNote + " " + uiLabelMap.Canceled.toLowerCase());
							return false;
						}
						window.open('receiptNoteWithPrice.pdf?deliveryId='+data.deliveryId, '_blank');
					}
				}
			});
		}
		if (inOrderDetail == true){
			$("#jqxgridDelivery").on('rendertoolbarcompleted', function(event){
				if (createdDone){
					$("#addrowbuttonjqxgridDelivery").hide();
					$("#customcontroljqxgridDelivery1").hide();
				} else {
					$("#addrowbuttonjqxgridDelivery").show();
					$("#customcontroljqxgridDelivery1").show();
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
		        	    		$("#addrowbuttonjqxgridDelivery").show();
		    					$("#customcontroljqxgridDelivery1").show();
		        	    	}
		        	    });
		            }
				}]);
		});
			
		$('#sendRequestApprove').click(function(){
			if (!btnClick){
				btnClick = true;
				bootbox.dialog(uiLabelMap.AreYouSureSend, 
				[{"label": uiLabelMap.CommonCancel, 
					"icon": 'fa fa-remove', "class": 'btn  btn-danger form-action-button pull-right',
				    "callback": function() {bootbox.hideAll(); btnClick = false; confirmClick = false;}
				}, 
				{"label": uiLabelMap.OK,
				    "icon": 'fa-check', "class": 'btn btn-primary form-action-button pull-right',
				    "callback": function() {
				    	if (!confirmClick){
				    		Loading.show('loadingMacro');
			            	setTimeout(function(){
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
						    		success: function (res){
						    			showDetailPopup(glDeliveryId);
						    		}
						    	});
							Loading.hide('loadingMacro');
			            	}, 500);
				    		confirmClick = true;
				    	}
				    }
				}]);
			}
		});
		
		$('#quickSave').click(function(){
			if (!btnClick){
				btnClick = true;
				var conversionFactor = $("#conversionFactor").val();
				if(currencyUom != 'VND') {
                    var valid = $("#selectFacilityWindow").jqxValidator('validate');
                    if(!valid){
                        btnClick = false; confirmClick = false;
                        return;
                    }
                }
				bootbox.dialog(uiLabelMap.AreYouSureCreate, 
				[{"label": uiLabelMap.CommonCancel, 
					"icon": 'fa fa-remove', "class": 'btn  btn-danger form-action-button pull-right',
				    "callback": function() {bootbox.hideAll(); btnClick = false; confirmClick = false;}
				}, 
				{"label": uiLabelMap.OK,
				    "icon": 'fa-check', "class": 'btn btn-primary form-action-button pull-right',
				    "callback": function() {
				    	if (!confirmClick){
				    		Loading.show('loadingMacro');
			            	setTimeout(function(){
						    	var facId = facilitySelected.facilityId;
								var cmtId = $("#defaultContactMechId").val();
								var orderIdTmp = $("#defaultOrderId").val();
								if (quickClick == 0){
									quickCreateDelivery(orderIdTmp, facId, cmtId, conversionFactor, false);
									quickClick = quickClick + 1;
								}
							Loading.hide('loadingMacro');
			            	}, 500);
				    		confirmClick = true;
				    	}
				    }
				}]);
			}
		});
		$('#quickSaveApprove').click(function(){
			if (!btnClick){
				btnClick = true;
                var conversionFactor = $("#conversionFactor").val();
                if(currencyUom != 'VND') {
                    var valid = $("#selectFacilityWindow").jqxValidator('validate');
                    if(!valid){
                        btnClick = false; confirmClick = false;
                        return;
                    }
                } else {
                    conversionFactor = 1;
                }
				bootbox.dialog(uiLabelMap.AreYouSureCreate, 
				[{"label": uiLabelMap.CommonCancel, 
					"icon": 'fa fa-remove', "class": 'btn  btn-danger form-action-button pull-right',
				    "callback": function() {bootbox.hideAll(); btnClick = false; confirnClick = false; $('#quickSaveApprove').focus()}
				}, 
				{"label": uiLabelMap.OK,
				    "icon": 'fa-check', "class": 'btn btn-primary form-action-button pull-right',
				    "callback": function() {
				    	if (!confirmClick){
					    	Loading.show('loadingMacro');
			            	setTimeout(function(){
						    	var facId = facilitySelected.facilityId;
								var cmtId = $("#defaultContactMechId").val();
								var orderIdTmp = $("#defaultOrderId").val();
								quickCreateDelivery(orderIdTmp, facId, cmtId, conversionFactor, true);
							Loading.hide('loadingMacro');
			            	}, 500);
			            	confirnClick = true;
				    	}
					}
				}]);
			}
		});
		
		
		$("#detailpopupWindow").on("close", function(event){
			saveClick = 0;
			if (typeof inOrderDetail != 'undefined' && inOrderDetail == true) {
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
				if (newStatusId != currentOrderStatus){
					window.location.replace("viewDetailPO?orderId="+orderId);
				}
				
				checkCreatedDone();
			}
			$('#jqxgridConfirm').jqxGrid('clear');
			$("#jqxgridConfirm").hide();
			$("#jqxgrid2").show();
			$('#jqxgrid2').jqxGrid('clear');
			$('#jqxgridDelivery').jqxGrid('updatebounddata');
			$("#invoiceId").jqxInput('clear');
			pathScanFile = null;
		});
		
		$("#detailpopupWindow").on("open", function(event){
			saveClick = 0;
			btnClick = false;
			confirmClick = false;
			if (!$('#detailpopupWindow').jqxValidator('validate')){
				return false;
			}
		});
		
		$("#actualArrivalDate").on("change", function(event){
			setTimeout(function(){
				if (!$('#detailpopupWindow').jqxValidator('validate')){
					return false;
				}
			}, 500);
		});
		
		$("#actualStartDate").on("change", function(event){
			setTimeout(function(){
				if (!$('#detailpopupWindow').jqxValidator('validate')){
					return false;
				}
			}, 500);
		});
		
		$("#estimatedStartDate").on("change", function(event){
			setTimeout(function(){
				if ($("#alterpopupWindow").length > 0) {
					if (!$('#alterpopupWindow').jqxValidator('validate')){
						return false;
					}
				}
			}, 500);
		});
		
		$("#estimatedArrivalDate").on("change", function(event){
			setTimeout(function(){
				if ($("#alterpopupWindow").length > 0) {
					if (!$('#alterpopupWindow').jqxValidator('validate')){
						return false;
					}
				}
			}, 500);
		});
		
		$("#alterSave").click(function () {
			if (!btnClick){
				btnClick = true;
				var row;
				// Get List Order Item
				$('#jqxgrid1').jqxGrid('clearfilters');
				var selectedIndexs = $('#jqxgrid1').jqxGrid('getselectedrowindexes');
				if(selectedIndexs.length <= 0){
					jOlbUtil.alert.error(uiLabelMap.YouNotYetChooseProduct);
					btnClick = false;
					confirmClick = false;
					return false;
				} 
				if ($("#alterpopupWindow").length > 0) {
					if (!$('#alterpopupWindow').jqxValidator('validate')){
						btnClick = false;
						confirmClick = false;
						return false;
					}
				}
				bootbox.dialog(uiLabelMap.AreYouSureCreate, 
				[{"label": uiLabelMap.CommonCancel, 
					"icon": 'fa fa-remove', "class": 'btn  btn-danger form-action-button pull-right',
				    "callback": function() {bootbox.hideAll(); btnClick = false; confirmClick = false; addClick = false;}
				}, 
				{"label": uiLabelMap.OK,
				    "icon": 'fa-check', "class": 'btn btn-primary form-action-button pull-right',
				    "callback": function() {
				    	if (!addClick){
				    		var listOrderItems = [];
			        		for(var x in selectedIndexs){
			        			var data = $('#jqxgrid1').jqxGrid('getrowdata', selectedIndexs[x]);
			        			if (data != undefined){
			        				var map = {};
				        			map['orderItemSeqId'] = data.orderItemSeqId;
				        			map['orderId'] = data.orderId;
				        			map['quantity'] = data.quantityCreate;
				        			map['quantityUomId'] = data.baseQuantityUomId;
				        			if (data.requireAmount && data.requireAmount == "Y") {
				        				map['amount'] = data.quantityCreate;
				        				map['quantity'] = 1;
				        			} else {
				        				map['quantity'] = data.quantityCreate;
				        			}
				        			var exp = data.expireDate;
				        			if (exp){
				        				map['expireDate'] = exp.getTime();
				        			}
				        			listOrderItems.push(map);
			        			}
			        		
			        		}
			        		var listOrderItems = JSON.stringify(listOrderItems);
			        		row = { 
			        				partyIdFrom: partyFromIdGl,
			        				partyIdTo: company,
			        				currencyUomId: currencyUom,
			        				orderId: orderId,
			        				statusId: 'DLV_CREATED',
			        				destFacilityId: facilitySelected.facilityId,
			        				destContactMechId:$('#destContactMechId').val(),
			        				deliveryDate:$('#deliveryDate').jqxDateTimeInput('getDate'),
			        				estimatedStartDate:$('#estimatedStartDate').jqxDateTimeInput('getDate'),
			        				estimatedArrivalDate:$('#estimatedArrivalDate').jqxDateTimeInput('getDate'),
			        				defaultWeightUomId : "WT_kg",
			        				deliveryTypeId: $('#deliveryTypeId').val(),
			        				deliveryId: $('#deliveryId').val(),
			        				listOrderItems:listOrderItems,
                                    conversionFactor: $("#conversionFactorCreate").val()
			            	};
			        		Loading.show('loadingMacro');
		            		$("#jqxgridDelivery").jqxGrid('addRow', null, row, "first");
		            		$("#jqxgridDelivery").jqxGrid('updatebounddata'); 
		            		Loading.hide('loadingMacro');
			        		var tmpUrl = window.location.href;
			        		if(tmpUrl.indexOf("orderId") < 0){
			        		    tmpUrl += "?orderId="+orderId;
			        		}
			        		if(tmpUrl.indexOf("deliveries-tab") < 0){
			        		    tmpUrl += "&activeTab=deliveries-tab";
			        		}
					    	if ($("#alterpopupWindow").length > 0) {
					    		$("#alterpopupWindow").jqxWindow('close');
					    	}
					    	addClick = true;
				    	}
				    }
				}]);
			}
		});
		
		$('#uploadOkButton').click(function(){
			saveFileUpload();
		});
		$('#uploadCancelButton').click(function(){
			$('#jqxFileScanUpload').jqxWindow('close');
		});
		$('#jqxFileScanUpload').on('close', function(event){
			$('.remove').trigger('click');
			initAttachFile();
		});
		
		$("#confirmAndContinue").on("click", function(event){
			checkContinue = true;
			confirmDelivery();
		});
		$("#detailConfirm").on("click", function(event){
			checkContinue = false;
			confirmDelivery();
		});
		
		$("#alterApproveAndContinue").on("click", function(event){
			checkContinue = true;
			approveDelivery();
		});
		$("#detailApprove").on("click", function(event){
			checkContinue = false;
			approveDelivery();
		});
		
		$("#detailSave").on("click", function(event){
			checkContinue = false;
			saveDelivery();
		});
		
		$("#btnNextWizard").on("click", function(event){
			$("#jqxgrid2").hide();
			$("#addRow").hide();
			$("#detailSave").show();
			var listDlvItems = generateData();
			var tmpS = $("#jqxgridConfirm").jqxGrid('source');
    		tmpS._source.localdata = listDlvItems;
    	    $("#jqxgridConfirm").jqxGrid('source', tmpS);
    	    $("#jqxgridConfirm").jqxGrid('updatebounddata');
			$("#jqxgridConfirm").show();
			$("#btnNextWizard").hide();
		});
		
		$("#btnPrevWizard").on("click", function(event){
			$("#jqxgrid2").show();
			$("#detailSave").hide();
			$("#addRow").show();
			$("#jqxgridConfirm").hide();
			$("#btnNextWizard").show();
		});
		
		$("#editAddProductWindow").on('open', function(event){
			btnClick = false;
			confirmClick = false;
		});
		
		$("#editWindow").on('open', function(event){
			btnClick = false;
			confirmClick = false;
		});
		
		$("#editAddProductWindow").on('open', function(event){
			btnClick = false;
			confirmClick = false;
		});
		
		$("#selectFacilityWindow").on('open', function(event){
			if ($('#quickSave').length > 0){
				$('#quickSave').focus();
			}
			if ($('#quickSaveApprove').length > 0){
				$('#quickSaveApprove').focus();
			}
			btnClick = false;
			confirmClick = false;
			if (facilitySelected != null){
				if (facilitySelected.facilityCode != null){
	        		description = '['+ facilitySelected.facilityCode +'] ' + facilitySelected.facilityName;
	        	} else {
	        		description = '['+ facilitySelected.facilityId +'] ' + facilitySelected.facilityName;
	        	}
	        
		        var dropDownContent = '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+ description +' </div>';
		        $('#facility').jqxDropDownButton('setContent', dropDownContent);
					
				update({
					facilityId: facilitySelected.facilityId,
					contactMechPurposeTypeId: "SHIPPING_LOCATION",
					}, 'getFacilityContactMechs' , 'listFacilityContactMechs', 'contactMechId', 'address1', 'defaultContactMechId');
			}
		});
		if ($("#alterpopupWindow").length > 0) {
			$("#alterpopupWindow").on('open', function(event){
				btnClick = false;
				confirmClick = false;
				addClick = false;
				$("#jqxgrid1").jqxGrid('clearselection');
				$("#deliveryId").jqxInput('val', '');
				if (facilitySelected != null){
					if (facilitySelected.facilityCode != null){
		        		description = '['+ facilitySelected.facilityCode +'] ' + facilitySelected.facilityName;
		        	} else {
		        		description = '['+ facilitySelected.facilityId +'] ' + facilitySelected.facilityName;
		        	}
		        
			        var dropDownContent = '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+ description +' </div>';
			        $('#facilityPopup').jqxDropDownButton('setContent', dropDownContent);
						
					update({
						facilityId: facilitySelected.facilityId,
						contactMechPurposeTypeId: "SHIPPING_LOCATION",
						}, 'getFacilityContactMechs' , 'listFacilityContactMechs', 'contactMechId', 'address1', 'destContactMechId');
				}
			    var listOrderItems = []; 
				$.ajax({
		            type: "POST",
		            url: "getListOrderItemDelivery",
		            data: {
		            	orderId: orderId,
		            },
		            dataType: "json",
		            async: false,
		            success: function(response){
		            	listOrderItems = response['listOrderItems'];
		            },
		            error: function(response){
		              alert("Error:" + response);
		            }
				});
				if (listOrderItems && listOrderItems.length > 0) {
					for (var i in listOrderItems) {
						var data = listOrderItems[i];
						var convert = data.convertNumber;
						
						var quantity = listOrderItems[i].requiredQuantityTmp;
						var quantityQC = Math.floor(quantity/convert);
						var quantityEA = quantity - quantityQC*convert;
						listOrderItems[i].quantityQC = quantityQC;
						listOrderItems[i].quantityEA = quantityEA;
						listOrderItems[i].quantityCreate = quantity;
					}
				}
				loadOrderItem(listOrderItems);
			});
		}
		
	};
	
	function confirmDelivery(){
	bootbox.dialog(uiLabelMap.AreYouSureConfirm, 
		[{"label": uiLabelMap.CommonCancel, 
			"icon": 'fa fa-remove', "class": 'btn  btn-danger form-action-button pull-right',
		    "callback": function() {bootbox.hideAll();}
		}, 
		{"label": uiLabelMap.OK,
		    "icon": 'fa-check', "class": 'btn btn-primary form-action-button pull-right',
		    "callback": function() {
		    	Loading.show('loadingMacro');
            	setTimeout(function(){
			    	var dlvId = glDeliveryId;
					var tmp = $('#actualStartDate').jqxDateTimeInput('getDate');
					if (dlvId){
						$.ajax({
				               type: "POST",
				               url: "updateDelivery",
				               data: {'deliveryId': dlvId,
				            	   	'statusId': "DLV_EXPORTED",
				            	   	'actualStartDate': tmp.getTime(),
				               		},
				               dataType: "json",
				               async: false,
				               success: function(res){
				            	   if (res._ERROR_MESSAGE_ == "STOREKEEPER_NOT_FOUND"){
				            		   $("#notifyIdNotHaveStorekeeper").jqxNotification("open");
				            		   $("#detailpopupWindow").jqxWindow('close');
				            	   } else {
				            		   if (checkContinue == true){
				            			   showDetailPopup(dlvId);
				            		   } else {
				            			   $("#detailpopupWindow").jqxWindow('close');
				            		   }
				            	   }
				               },
				               error: function(response){
				                 alert("Error:" + response);
				               }
				        });
					}
				Loading.hide('loadingMacro');
            	}, 500);
		    }
		}]);
	}
	
	function approveDelivery(){
	bootbox.dialog(uiLabelMap.AreYouSureApprove, 
		[{"label": uiLabelMap.CommonCancel, 
			"icon": 'fa fa-remove', "class": 'btn  btn-danger form-action-button pull-right',
		    "callback": function() {bootbox.hideAll();}
		}, 
		{"label": uiLabelMap.Approve,
		    "icon": 'fa-check', "class": 'btn btn-primary form-action-button pull-right',
		    "callback": function() {
		    	Loading.show('loadingMacro');
            	setTimeout(function(){
			    	var dlvId = glDeliveryId;
					if (dlvId){
						$.ajax({
				               type: "POST",
				               url: "updateDelivery",
				               data: {'deliveryId': dlvId,
				            	   	'statusId': "DLV_APPROVED",
				               		},
				               dataType: "json",
				               async: false,
				               success: function(res){
				            	   if (res._ERROR_MESSAGE_ == "STOREKEEPER_NOT_FOUND"){
				            		   $("#notifyIdNotHaveStorekeeper").jqxNotification("open");
				            		   $("#detailpopupWindow").jqxWindow('close');
				            	   } else {
				            		   if (checkContinue == true){
				            			   showDetailPopup(dlvId);
				            		   } else {
				            			   $("#detailpopupWindow").jqxWindow('close');
				            		   }
				            	   }
				               },
				               error: function(response){
				                 alert("Error:" + response);
				               }
				        });
					}
				Loading.hide('loadingMacro');
            	}, 500);
		    }
		}]);
	}
	
	function generateData(){
		var selectedIndexs = []; 
		$('#jqxgrid2').jqxGrid('clearfilters');
        var allRows = $('#jqxgrid2').jqxGrid('getrows');
        for (var id = 0; id < allRows.length; id ++){
        	if (allRows[id].productId != '' && allRows[id].productId != null && allRows[id].productId != undefined){
        		selectedIndexs.push($('#jqxgrid2').jqxGrid('getrowboundindexbyid', allRows[id].uid));
        	}
        }
        for(var id = 0; id < selectedIndexs.length; id++){
            if(checkGridDeliveryItemRequiredData(selectedIndexs[id]) == true){
            	saveClick = 0;
                return false;
            }
        }
        var listDeliveryItems = [];
    	for (var i = 0; i < selectedIndexs.length; i ++){
    		var data = $('#jqxgrid2').jqxGrid('getrowdata', selectedIndexs[i]);
    		if (data != undefined){
    			if (data.actualDeliveredQuantity > 0){
    				var map = {};
                    map.fromOrderId = data.fromOrderId;
                    map.fromOrderItemSeqId = data.fromOrderItemSeqId;
                    if (validateObject(data.actualExpireDate)){
                    	var tmp = new Date(data.actualExpireDate);
                        map.actualExpireDate = tmp.getTime();
                    }
                    if (validateObject(data.actualManufacturedDate)){
                    	var tmp = new Date(data.actualManufacturedDate);
                        map.actualManufacturedDate = tmp.getTime();
                    }
                    map.deliveryId = data.deliveryId;
                    map.productCode = data.productCode;
                    map.productId = data.productId;
                    map.productName = data.productName;
                    map.deliveryItemSeqId = data.deliveryItemSeqId;
                    map.actualExportedQuantity = data.actualExportedQuantity;
                    map.actualDeliveredQuantity = data.actualDeliveredQuantity;
                    map.actualDeliveredQuantityQC = data.actualDeliveredQuantityQC;
                    map.actualDeliveredQuantityEA = data.actualDeliveredQuantityEA;
                    map.isPromo = data.isPromo;
                    map.convertNumber = data.convertNumber;
                    map.quantity = data.quantity;
                    map.locationCode = data.locationCode;
                    map.requireAmount = data.requireAmount;
                    if (data.batch) {
                    	map.batch = data.batch.toUpperCase();
                    }
    				map.statusId = 'DELI_ITEM_DELIVERED';
                    curDeliveryId = data.deliveryId;
                    listDeliveryItems.push(map);
    			} else {
    				var map = {};
                    map.fromOrderId = data.fromOrderId;
                    map.fromOrderItemSeqId = data.fromOrderItemSeqId;
                    map.deliveryId = data.deliveryId;
                    map.deliveryItemSeqId = data.deliveryItemSeqId;
                    map.actualExportedQuantity = data.actualExportedQuantity;
                    map.actualDeliveredQuantity = data.actualDeliveredQuantity;
                    map.actualDeliveredQuantityQC = data.actualDeliveredQuantityQC;
                    map.actualDeliveredQuantityEA = data.actualDeliveredQuantityEA;
                    map.productCode = data.productCode;
                    map.productId = data.productId;
                    map.productName = data.productName;
                    map.isPromo = data.isPromo;
                    map.convertNumber = data.convertNumber;
                    map.requireAmount = data.requireAmount;
    				map.statusId = 'DELI_ITEM_DELIVERED';
    				if (data.batch) {
                    	map.batch = data.batch.toUpperCase();
                    }
                    curDeliveryId = data.deliveryId;
                    listDeliveryItems.push(map);
    			}
    		}
    	}
    	var listTmp = [];
    	if (listDeliveryItems.length < selectedIndexs.length){
    		for (var i = 0; i < dlvItemData.length; i ++){
    			if (dlvItemData[i].deliveryId == curDeliveryId){
    				var check = false;
        			for (var j = 0; j < listDeliveryItems.length; j ++){
        				if (dlvItemData[i].deliveryItemSeqId == listDeliveryItems[j].deliveryItemSeqId){
        					check = true;
        					break;
        				}
            		}
        			if (!check){
        				listTmp.push(dlvItemData[i]);
        			}
    			}
    		}
    	}
    	
    	if (listTmp.length > 0){
    		for (var j = 0; j < listTmp.length; j ++){
    			var data = listTmp[j];
    			var map = {};
                map.fromOrderId = data.orderId;
                map.fromOrderItemSeqId = data.orderItemSeqId;
                if (validateObject(data.expireDate)){
                	var tmp = new Date();
                    map.actualExpireDate = tmp.getTime();
                }
                if (validateObject(data.actualManufacturedDate)){
                	var tmpMft2 = new Date(data.actualManufacturedDate);
                    map.actualManufacturedDate = tmpMft2.getTime();
                }
                map.deliveryId = data.deliveryId;
                map.deliveryItemSeqId = data.deliveryItemSeqId;
                map.actualExportedQuantity = data.actualExportedQuantity;
                map.actualDeliveredQuantity = data.actualDeliveredQuantity;
                map.quantity = data.quantity;
                map.locationCode = data.locationCode
				map.statusId = 'DELI_ITEM_DELIVERED';
                listDeliveryItems.push(map);
    		}
    	}
    	var listAlls = listDeliveryItems;
    	listDeliveryItems = [];
    	var listDistincts = [];
    	for (var i = 0; i < listAlls.length; i ++){
    		var exist = false;
    		var orderItemSeqId1 = listAlls[i].fromOrderItemSeqId;
    		var exp1 = listAlls[i].actualExpireDate;
			var mnf1 = listAlls[i].actualManufacturedDate;
			var lot1 = listAlls[i].batch;
			var locCode1 = listAlls[i].locationCode;
    		for (var j = 0; j < listDistincts.length; j ++){
    			var orderItemSeqId2 = listDistincts[j].fromOrderItemSeqId;
        		var exp2 = listDistincts[j].actualExpireDate;
    			var mnf2 = listDistincts[j].actualManufacturedDate;
				var lot2 = listDistincts[j].batch;
				var locCode2 = listDistincts[j].locationCode;
    			if (orderItemSeqId2 == orderItemSeqId1 && exp2 == exp1 && mnf2 == mnf1 && lot2 == lot1 && locCode1 == locCode2){
    				exist = true;
    				break;
    			}
    		}
    		if (!exist){
    			var g = {
    					actualExpireDate: exp1,
    					actualManufacturedDate: mnf1,
    					batch: lot1,
    					fromOrderItemSeqId: orderItemSeqId1,
    					locationCode: locCode1,
    			}
    			listDistincts.push(g);
    		}
    	}
    	if (listDistincts.length < listAlls.length){
    		for (var i = 0; i < listDistincts.length; i ++){
    			var orderItemSeqId1 = listDistincts[i].fromOrderItemSeqId;
        		var exp1 = listDistincts[i].actualExpireDate;
    			var mnf1 = listDistincts[i].actualManufacturedDate;
				var lot1 = listDistincts[i].batch;
				var locCode1 = listDistincts[i].locationCode;
				var qtyTotal = 0;
				var item;
				for (var j = 0; j < listAlls.length; j ++){
					var orderItemSeqId2 = listAlls[j].fromOrderItemSeqId;
            		var exp2 = listAlls[j].actualExpireDate;
        			var mnf2 = listAlls[j].actualManufacturedDate;
    				var lot2 = listAlls[j].batch;
    				var locCode2 = listAlls[j].locationCode;
        			if (orderItemSeqId2 == orderItemSeqId1 && exp2 == exp1 && mnf2 == mnf1 && lot2 == lot1 && locCode1 == locCode2){
        				qtyTotal = qtyTotal + listAlls[j].actualDeliveredQuantity;
        				item = listAlls[j];
        			}
				}
				if (item != undefined){
					item['actualDeliveredQuantity'] = qtyTotal;
    				listDeliveryItems.push(item);
				}
    		}
    	} else {
    		listDeliveryItems = listAlls;
    	}
    	for (var s = 0; s < listDeliveryItems.length; s ++) {
    		if (listDeliveryItems[s].requireAmount && listDeliveryItems[s].requireAmount == "Y") {
    			listDeliveryItems[s].actualExportedAmount = listDeliveryItems[s].actualExportedQuantity;
    			listDeliveryItems[s].actualDeliveredAmount = listDeliveryItems[s].actualDeliveredQuantity;
    			listDeliveryItems[s].actualExportedQuantity = 1;
    			listDeliveryItems[s].actualDeliveredQuantity = 1;
    		}
    	}
    	return listDeliveryItems;
	}
	
	function saveDelivery(){
		if (saveClick == 0){
			saveClick = saveClick + 1;
		} else {
			return;
		}
		if("DLV_DELIVERED" != glDeliveryStatusId){
			if (!$('#detailpopupWindow').jqxValidator('validate')){
				saveClick = 0;
				return false;
			}
			var row;
	        // Get List Delivery Item
			
	        var firstRow = $('#jqxgrid2').jqxGrid('getrowdata', 0);
	        
			bootbox.dialog(uiLabelMap.AreYouSureSave, 
			[{"label": uiLabelMap.CommonCancel, 
				"icon": 'fa fa-remove', "class": 'btn  btn-danger form-action-button pull-right',
	            "callback": function() {
	            	saveClick = 0;
	            	bootbox.hideAll();
	            	}
	        }, 
	        {"label": uiLabelMap.OK,
	            "icon": 'fa-check', "class": 'btn btn-primary form-action-button pull-right',
	            "callback": function() {
	            	Loading.show('loadingMacro');
	            	setTimeout(function(){
	            		if (saveClick == 0){
	            			Loading.hide('loadingMacro');
	            			return;
	            		} 
		            	var listDeliveryItems = [];
		            	listDeliveryItems = generateData();
		                $('#jqxgrid2').jqxGrid('showloadelement');
		                for (var t in listDeliveryItems) {
		                	delete listDeliveryItems[t]['productName'];
		                /* TODO	if (locationProductSelected){
		                		listDeliveryItems[t]['locationId'] = locationProductSelected.locationId;
		                	}
	                	*/
		                }
		                var listDeliveryItems = JSON.stringify(listDeliveryItems);
		                var actualArrivalDateTmp;
		                if ("DLV_EXPORTED" == glDeliveryStatusId){
		                	var tmp = $('#actualArrivalDate').jqxDateTimeInput('getDate');
		                	if (tmp){
		                		actualArrivalDateTmp = tmp.getTime();
		                	}
		                }
		                var invoiceId = $("#invoiceId").jqxInput('val');
		                row = { 
		                    listDeliveryItems:listDeliveryItems,
		                    deliveryId: glDeliveryId,
		                    invoiceId: invoiceId,
		                	actualArrivalDate: actualArrivalDateTmp,
		                	statusId: "DLV_DELIVERED"
		                };
		                // call Ajax request to Update Exported or Delivered value
		                $.ajax({
		                    type: "POST",
		                    url: "updateDelivery",
		                    data: row,
		                    dataType: "json",
		                    async: false,
		                    success: function(data){
		                    },
		                    error: function(response){
		                        $('#jqxgridDelivery').jqxGrid('hideloadelement');
		                    },
		                });
		                if (checkContinue == true){
		                	saveClick = 0;
		                	showDetailPopup(curDeliveryId);
		                } else {
		                	$("#detailpopupWindow").jqxWindow('close');
		                }
	                    Loading.hide('loadingMacro');
	            	}, 500);
	            }
			}]);
		} 
	}
	
	var upScanFile = function (deliveryId, pathScanFile){
    	setTimeout(function(){
        	$.ajax({
   				 type: "POST",
   				 url: "updateDeliveryScanfile",
   				 data: {
   					 pathScanFile: pathScanFile,
   					 deliveryId: deliveryId,
   				 },
   				 dataType: "json",
   				 async: false,
   				 success: function(data){
   				 },
   				 error: function(response){
   				 }
	 		});
    	}, 500);
	}
	
	var checkCreatedDone = function checkCreatedDone (){
		var check = false;
		$.ajax({
			type: 'POST',
			async: false,
			url: 'checkPurchaseOrderReceipt',
			data: {
				orderId: orderId,
			},
			success: function (res){
				check = res['createdDone'];
				if (check == true){
					$("#addrowbuttonjqxgridDelivery").hide();
					$("#customcontroljqxgridDelivery1").hide();
				} else {
					$("#addrowbuttonjqxgridDelivery").show();
					$("#customcontroljqxgridDelivery1").show();
				}
			}
		});
	}
	var initAttachFile = function initAttachFile(){
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
			before_remove : function() {
				listImage = [];
				return true;
			}
		});
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
	
	var getTotalWeight = function getTotalWeight(){
		var totalProductWeight = 0;
		for(var i = 0; i < orderItemData.length; i++){
			var data = orderItemData[i];
			var baseWeightUomId = data.weightUomId;
			var defaultWeightUomId = "WT_kg";
			var itemWeight = 0;
			if (data.weight){
				itemWeight = (data.quantity)*(data.weight);
			} else {
				if (data.productWeight){
					itemWeight = (data.quantity)*(data.productWeight);
				}
			}
			if (baseWeightUomId == defaultWeightUomId){
				totalProductWeight = totalProductWeight + itemWeight;
			} else {
				for (var j=0; j<uomConvertData.length; j++){
					if ((uomConvertData[j].uomId == baseWeightUomId && uomConvertData[j].uomIdTo == defaultWeightUomId)){
						totalProductWeight = totalProductWeight + (uomConvertData[j].conversionFactor)*itemWeight;
						break;
					}
					if ((uomConvertData[j].uomId == defaultWeightUomId && uomConvertData[j].uomIdTo == baseWeightUomId)){
						totalProductWeight = totalProductWeight + itemWeight/(uomConvertData[j].conversionFactor);
						break;
					}
				}
			}
		}
		var n = parseFloat(totalProductWeight)
		totalProductWeight = Math.round(n * 1000)/1000;

		var desc = "";
		for(var i = 0; i < weightUomData.length; i++){
			if(weightUomData[i].uomId == defaultWeightUomId){
				desc = weightUomData[i].description;
			}
		}
		$('#totalProductWeight').text(totalProductWeight.toLocaleString(localeStr) + " (" + desc + ")");
		$('#totalProductWeightDT').text(totalProductWeight.toLocaleString(localeStr) + " (" + desc + ")");
	}
	
	function initValidateDateDT(element){
		element.jqxValidator({
			rules:[
				{
				input: '#actualArrivalDate', 
			    message: uiLabelMap.ActualReceivedDateMustAfterActualStartDeliveryDate, 
			    action: 'blur',
			    position: 'topcenter',
			    rule: function (input) {
			    	var actualStartDateTmp = $('#actualStartDate').jqxDateTimeInput('getDate');
			    	var actualArrivalDate = $('#actualArrivalDate').jqxDateTimeInput('getDate');
			    	var statusCurrent = $("#currentDlvStatusId").val();
				   	if ((typeof(actualStartDateTmp) != 'undefined' && actualStartDateTmp != null && !(/^\s*$/.test(actualStartDateTmp))) && (typeof(actualArrivalDate) != 'undefined' && actualArrivalDate != null && !(/^\s*$/.test(actualArrivalDate)))) {
			 		    if (actualArrivalDate < actualStartDateTmp && "DLV_EXPORTED" == statusCurrent) {
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
			    	var actualStartDateTmp = $('#actualStartDate').jqxDateTimeInput('getDate');
			    	var now = new Date();
				   	if (typeof(actualStartDateTmp) != 'undefined' && actualStartDateTmp != null && !(/^\s*$/.test(actualStartDateTmp))) {
			 		    if (actualStartDateTmp > now) {
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
			    	var actualArrivalDateTmp = $('#actualArrivalDate').jqxDateTimeInput('getDate');
			    	var now = new Date();
				   	if (typeof(actualArrivalDateTmp) != 'undefined' && actualArrivalDateTmp != null && !(/^\s*$/.test(actualArrivalDateTmp))) {
			 		    if (actualArrivalDateTmp > now) {
			 		    	return false;
			 		    }
				   	}
				   	return true;
			    }
			},
			],
		});
	}
	function initValidate(element){
		element.jqxValidator({
			rules:[{
				input: '#estimatedArrivalDate', 
	            message: uiLabelMap.ArrivalDateEstimatedMustBeAfterExportDateEstimated, 
	            action: 'blur',
	            position: 'topcenter',
	            rule: function (input) {	
	        		var estimatedArrivalDate = $('#estimatedArrivalDate').jqxDateTimeInput('getDate');
	            	var estimatedStartDate = $('#estimatedStartDate').jqxDateTimeInput('getDate');
	     		   	if ((typeof(estimatedArrivalDate) != 'undefined' && estimatedArrivalDate != null && !(/^\s*$/.test(estimatedArrivalDate))) && (typeof(estimatedStartDate) != 'undefined' && estimatedStartDate != null && !(/^\s*$/.test(estimatedStartDate)))) {
		     		    if (estimatedArrivalDate < estimatedStartDate) {
		     		    	return false;
		     		    }
	     		   	}
	     		   	return true;
	            }
			},
			{
				input: '#destContactMechId', 
	            message: uiLabelMap.FieldRequired, 
	            action: 'blur',
	            position: 'topcenter',
	            rule: function (input) {	
	         	   	var tmp = $('#destContactMechId').jqxDropDownList('getSelectedItem');
	                return tmp ? true : false;
	            }
			},
			{
                input: '#facilityPopup',
                message: uiLabelMap.FieldRequired,
                action: 'blur',
                position: 'topcenter',
                rule: function (input) {
                    var tmp = facilitySelected;
                    return tmp ? true : false;
                }
            },
            {
                input: '#conversionFactorCreate',
                message: uiLabelMap.ValueMustBeGreateThanZero,
                action: 'blur',
                position: 'topcenter',
                rule: function (input) {
                    return !(input.val() <= 0 && 'VND' !== currencyUom);
                }
            },
			{input: '#deliveryId', message: uiLabelMap.ThisFieldMustNotByContainSpecialCharacter, action: 'blur', rule: 
                function (input, commit) {
                	var value = $(input).val();
        			if(value && !(/^[a-zA-Z0-9_]+$/.test(value))){
        				return false;
        			}
        			return true;
            	}
            },
			],
		});
	};
	
	var getDeliveryDetail = function (deliveryId) {
		var deliveryDT = null;
		$.ajax({
            type: "POST",
            url: "getDeliveryById",
            data: {'deliveryId': deliveryId},
            dataType: "json",
            async: false,
            success: function(response){
                deliveryDT = response;
            },
            error: function(response){
              alert("Error:" + response);
            }
		});
		return deliveryDT;
	};
	
	var showDetailPopup = function showDetailPopup(deliveryId){
		$("#cancelDlv").hide();
		$("#editDlv").hide();
		$("#detailSave").hide();
		$("#btnScanUPCA").hide();
		checkContinue = false;
		var deliveryDT;
		glDeliveryId = deliveryId;
		// Cache delivery
		deliveryDT = getDeliveryDetail(deliveryId);
        glOriginFacilityId = deliveryDT.originFacilityId;
        glDestFacilityId = deliveryDT.destFacilityId;
        glDeliveryStatusId = deliveryDT.statusId;
		// Set deliveryId for target print pdf
		var href = "receipt.pdf?deliveryId=";
		href += deliveryId
		$("#printPDF").attr("href", href);

		var href = "receiptNoteWithPrice.pdf?deliveryId=";
		href += deliveryId
		$("#printPDFWithPrice").attr("href", href);
		
		$("#deliveryIdDT").text(deliveryDT.deliveryId);
		$("#invoiceIdDT").text(deliveryDT.invoiceId);
        $("#currentDlvStatusId").val(deliveryDT.statusId);
        var stName = null;
        if ("DLV_EXPORTED" == deliveryDT.statusId){
        	stName = uiLabelMap.Shipping;
        } else if ("DLV_DELIVERED" == deliveryDT.statusId){
        	stName = uiLabelMap.BLCompleted ;
        } else {
        	stName = getStatusDescription (deliveryDT.statusId);
        }
        $("#statusIdDT").text(stName);
        var listChangeStatus = deliveryDT.listChangeStatus;
        if (listChangeStatus){
        	var objStatus = JSON.parse(listChangeStatus);
        	if (objStatus.length > 0){
        		for (var stt in objStatus){
                	if (deliveryDT.statusId == objStatus[stt].statusId){
                		if (objStatus[stt].changeReason){
                			$("#statusIdDT").append("<div style=\"color: red\">" +objStatus[stt].changeReason+ "</div>");
                		}
                		break;
                	}
                }
        	}
        }
    	if ("DLV_CREATED" == deliveryDT.statusId || "DLV_PROPOSED" == deliveryDT.statusId || ("DLV_DELIVERED" == deliveryDT.statusId && deliveryDT.pathScanFile != null)){
//    		$("#detailSave").hide();
    		$('#titleDetailId > div:first-child').html("");
    		if (isAdmin){
    			$('#titleDetailId > div:first-child').text(uiLabelMap.ReceiveNote + " - " + uiLabelMap.ApproveDelivery);
    		} else {
    			$('#titleDetailId > div:first-child').text(uiLabelMap.ReceiveNote + " - " + uiLabelMap.WaitForApprove);
    		}
    		$("#detailConfirm").hide();
    		$("#confirmAndContinue").hide();
    		if (isAdmin){
    			if ("DLV_CREATED" == deliveryDT.statusId || "DLV_PROPOSED" == deliveryDT.statusId) {
        			$("#detailApprove").show();
        			$('#alterApproveAndContinue').show();
        		} else {
        			$("#detailApprove").hide();
        			$('#alterApproveAndContinue').hide();
        		}
    		}
    	} else {
    		if ("DLV_DELIVERED" == deliveryDT.statusId && deliveryDT.pathScanFile == null){
//    			$("#detailSave").show();
    			$("#detailConfirm").hide();
    			$("#confirmAndContinue").hide();
    			$('#alterApproveAndContinue').hide();
    		} else if ("DLV_APPROVED" == deliveryDT.statusId) {
//    			$("#detailSave").hide();
    			$("#detailConfirm").show();
    			$("#confirmAndContinue").show();
    			$('#alterApproveAndContinue').hide();
    		} else {
    			$("#detailConfirm").hide();
    			$('#alterApproveAndContinue').hide();
    			$("#confirmAndContinue").hide();
//    			$("#detailSave").show();
    		}
    		$("#detailApprove").hide();
    	}
		
    	$("#orderIdDT").text(deliveryDT.orderId);
		$("#partyIdFromDT").text(deliveryDT.partyFromName);
		$("#destContactMechIdDT").text(deliveryDT.destAddress);
		$("#noDT").text(deliveryDT.no);
		$("#destFacilityDT").text(deliveryDT.destFacilityName);
		
		$('#createDateDT').text(DatetimeUtilObj.formatFullDate(new Date(deliveryDT.createDate)));
		$('#deliveryDateDT').text(DatetimeUtilObj.formatFullDate(new Date(deliveryDT.deliveryDate)));
		$('#estimatedStartDateDT').text(DatetimeUtilObj.formatFullDate(new Date(deliveryDT.estimatedStartDate)));
		$('#estimatedArrivalDateDT').text(DatetimeUtilObj.formatFullDate(new Date(deliveryDT.estimatedArrivalDate)));
		
		// Create pathScanfile
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
		
		
		if ("DLV_CREATED" == deliveryDT.statusId){
			$("#cancelDlv").show();
			$("#editDlv").show();
			$('#actualStartDateDis').hide();
			$('#actualArrivalDateDis').hide();
			$('#actualStartDate').show();
			$('#actualArrivalDate').show();
			$('#actualStartDate').jqxDateTimeInput('disabled', true);
			$('#actualArrivalDate').jqxDateTimeInput('disabled', true);
			$('#sendRequestApprove').show();
		} else {
			$('#sendRequestApprove').hide();
		}
		if ("DLV_APPROVED" == deliveryDT.statusId){
			$("#cancelDlv").show();
			$("#editDlv").show();
			$('#titleDetailId > div:first-child').html("");
			$('#titleDetailId > div:first-child').text(uiLabelMap.ReceiveNote + " - " + uiLabelMap.ConfirmDatetimeProviderStartExport);
			$('#actualStartDateDis').hide();
			var dateTmp = new Date(deliveryDT.estimatedStartDate);
			var nowDate1 = new Date();
 		    if (nowDate1 < dateTmp) {
 		    	$('#actualStartDate').jqxDateTimeInput('val', nowDate1);
 		    	$('#actualArrivalDate').jqxDateTimeInput('val', nowDate1);
 		    } else {
 		    	$('#actualStartDate').jqxDateTimeInput('val', dateTmp);
 		    	$('#actualArrivalDate').jqxDateTimeInput('val', dateTmp);
 		    }
			$('#actualStartDate').show();
			
			$('#actualStartDate').jqxDateTimeInput('disabled', false);
			$('#actualArrivalDate').jqxDateTimeInput('disabled', true);
			
			var dateTmp = deliveryDT.estimatedStartDate;
			if ((new Date(dateTmp)) < (new Date())){
				$('#actualStartDate').jqxDateTimeInput('val', new Date(dateTmp));
			} else {
				$('#actualStartDate').jqxDateTimeInput('val', new Date());
			}
			$('#actualArrivalDate').jqxDateTimeInput('val', $('#actualStartDate').jqxDateTimeInput('getDate'));
			
		}
		if ("DLV_EXPORTED" == deliveryDT.statusId){
			$("#cancelDlv").show();
			$("#editDlv").show();
			$('#titleDetailId > div:first-child').html("");
			$('#titleDetailId > div:first-child').text(uiLabelMap.ReceiveNote + " - " + uiLabelMap.UpdateActualReceivedQuantity);
			
			var dateTmp = deliveryDT.estimatedArrivalDate;
			$('#actualArrivalDate').jqxDateTimeInput('val', new Date());
			$('#actualArrivalDate').show();
			
			$('#actualArrivalDate').jqxDateTimeInput('disabled', false);
			$('#actualStartDate').hide();
			$('#actualArrivalDateDis').hide();
			$('#actualStartDateDis').show();
			$('#actualStartDateDis').html("");
			$('#actualStartDateDis').text(DatetimeUtilObj.formatFullDate(new Date(deliveryDT.actualStartDate)));
//			$('#detailSaveAndContinue').show();
			$("#addRow").show();
			$("#btnNextWizard").show();
			$("#btnPrevWizard").show();
			$("#invoiceId").show();
		} else {
//			$('#detailSaveAndContinue').hide();
			$("#addRow").hide();
			$("#invoiceId").hide();
			$("#btnNextWizard").hide();
			$("#btnPrevWizard").hide();
		}
		if ("DLV_DELIVERED" == deliveryDT.statusId){
			$('#titleDetailId > div:first-child').html("");
			$('#titleDetailId > div:first-child').text(uiLabelMap.ReceiveNote);
			$('#actualStartDate').hide();
			$('#actualArrivalDate').hide();
			$('#actualStartDateDis').show();
			$("#invoiceIdDT").show();
//			$("#detailSave").hide();
			$('#actualStartDateDis').text(DatetimeUtilObj.formatFullDate(new Date(deliveryDT.actualStartDate)));
			$('#actualArrivalDateDis').show();
			var arrDate = deliveryDT.actualArrivalDate;
			$('#actualArrivalDateDis').text(DatetimeUtilObj.formatFullDate(new Date(deliveryDT.actualArrivalDate)));
		} else {
			$("#invoiceIdDT").hide();
		}
		if ("DLV_CANCELLED" == deliveryDT.statusId){
        	$("#printPDF").hide();
        	$("#printPDFWithPrice").hide();
        	$("#scanfile").hide();
        	$('#actualStartDate').jqxDateTimeInput('disabled', true);
			$('#actualArrivalDate').jqxDateTimeInput('disabled', true);
//        	$("#detailSave").hide();
        	$("#detailApprove").hide();
        	$("#cancelDlv").hide();
        	$("#editDlv").hide();
        }
		var listDeliveryItemTmps = getDeliveryItemDetail(deliveryDT.deliveryId);
		if ("DLV_DELIVERED" == deliveryDT.statusId){
			for (var m = 0; m < listDeliveryItemTmps.length; m ++){
				var item = listDeliveryItemTmps[m];
				var convertNumber = item.convertNumber;
				if (item.requireAmount && item.requireAmount == 'Y') {
					listDeliveryItemTmps[m]["actualDeliveredQuantity"] = listDeliveryItemTmps[m].actualDeliveredAmount;
					var qcQty = listDeliveryItemTmps[m].actualDeliveredAmount;
					if (convertNumber != 1) {
						qcQty = Math.floor(listDeliveryItemTmps[m].actualDeliveredAmount/convertNumber);
					} else {
						qcQty = 0;
					}
					listDeliveryItemTmps[m]["actualDeliveredQuantityQC"] = qcQty;
					if (qcQty*convertNumber < listDeliveryItemTmps[m].actualDeliveredAmount) {
						listDeliveryItemTmps[m]["actualDeliveredQuantityEA"] = listDeliveryItemTmps[m].actualDeliveredAmount - qcQty*convertNumber;
					} else {
						listDeliveryItemTmps[m]["actualDeliveredQuantityEA"] = 0;
					}	
					listDeliveryItemTmps[m]["quantity"] = listDeliveryItemTmps[m].amount;
				} else {
					var convertNumber = item.convertNumber;
					var qcQty = listDeliveryItemTmps[m].actualDeliveredQuantity;
					if (convertNumber != 1) {
						qcQty = Math.floor(listDeliveryItemTmps[m].actualDeliveredQuantity/convertNumber);
					} else {
						qcQty = 0;
					}
					listDeliveryItemTmps[m]["actualDeliveredQuantityQC"] = qcQty;
					if (qcQty < listDeliveryItemTmps[m].actualDeliveredQuantity) {
						listDeliveryItemTmps[m]["actualDeliveredQuantityEA"] = listDeliveryItemTmps[m].actualDeliveredQuantity - qcQty*convertNumber;
					} else {
						listDeliveryItemTmps[m]["actualDeliveredQuantityEA"] = 0;
					}	
				}
				
				if (listDeliveryItemTmps[m].actualExpireDate != null && listDeliveryItemTmps[m].actualExpireDate != undefined) {
					if (listDeliveryItemTmps[m].actualExpireDate.time) {
						listDeliveryItemTmps[m]["actualExpireDate"] = listDeliveryItemTmps[m].actualExpireDate.time;
					}
				}
				if (listDeliveryItemTmps[m].actualManufacturedDate != null && listDeliveryItemTmps[m].actualManufacturedDate != undefined) {
					if (listDeliveryItemTmps[m].actualManufacturedDate.time) {
						listDeliveryItemTmps[m]["actualManufacturedDate"] = listDeliveryItemTmps[m].actualManufacturedDate.time;
					}
				}
				
			}
		} else if ("DLV_EXPORTED" == deliveryDT.statusId){
			for (var m = 0; m < listDeliveryItemTmps.length; m ++){
				var item = listDeliveryItemTmps[m];
				var convertNumber = item.convertNumber;
				if (item.requireAmount && item.requireAmount == 'Y') {
					listDeliveryItemTmps[m]["actualDeliveredQuantity"] = listDeliveryItemTmps[m].actualExportedAmount;
					var qcQty = listDeliveryItemTmps[m].actualExportedAmount;
					if (convertNumber != 1) {
						qcQty = Math.floor(listDeliveryItemTmps[m].actualExportedAmount/convertNumber);
					} else {
						qcQty = 0;
					}
					listDeliveryItemTmps[m]["actualDeliveredQuantityQC"] = qcQty;
					if (qcQty*convertNumber < listDeliveryItemTmps[m].actualExportedAmount) {
						listDeliveryItemTmps[m]["actualDeliveredQuantityEA"] = listDeliveryItemTmps[m].actualExportedAmount - qcQty*convertNumber;
					} else {
						listDeliveryItemTmps[m]["actualDeliveredQuantityEA"] = 0;
					}	
					listDeliveryItemTmps[m]["quantity"] = listDeliveryItemTmps[m].amount;
				} else {
					listDeliveryItemTmps[m]["actualDeliveredQuantity"] = listDeliveryItemTmps[m].actualExportedQuantity;
					var convertNumber = item.convertNumber;
					var qcQty = listDeliveryItemTmps[m].actualExportedQuantity;
					if (convertNumber != 1) {
						qcQty = Math.floor(listDeliveryItemTmps[m].actualExportedQuantity/convertNumber);
					} else {
						qcQty = 0;
					}
					listDeliveryItemTmps[m]["actualDeliveredQuantityQC"] = qcQty;
					if (qcQty < listDeliveryItemTmps[m].actualExportedQuantity) {
						listDeliveryItemTmps[m]["actualDeliveredQuantityEA"] = listDeliveryItemTmps[m].actualExportedQuantity - qcQty*convertNumber;
					} else {
						listDeliveryItemTmps[m]["actualDeliveredQuantityEA"] = 0;
					}	
				}
				
			}
		} else if ("DLV_APPROVED" == deliveryDT.statusId){
			for (var m = 0; m < listDeliveryItemTmps.length; m ++){
				var item = listDeliveryItemTmps[m];
				var convertNumber = item.convertNumber;
				if (item.requireAmount && item.requireAmount == 'Y') {
					listDeliveryItemTmps[m]["actualExportedQuantity"] = listDeliveryItemTmps[m].amount;
					
					var qcQty = listDeliveryItemTmps[m].actualExportedAmount;
					if (convertNumber != 1) {
						qcQty = Math.floor(listDeliveryItemTmps[m].actualExportedAmount/convertNumber);
					} else {
						qcQty = 0;
					}
					listDeliveryItemTmps[m]["actualDeliveredQuantityQC"] = qcQty;
					if (qcQty*convertNumber < listDeliveryItemTmps[m].actualExportedAmount) {
						listDeliveryItemTmps[m]["actualDeliveredQuantityEA"] = listDeliveryItemTmps[m].actualExportedAmount - qcQty*convertNumber;
					} else {
						listDeliveryItemTmps[m]["actualDeliveredQuantityEA"] = 0;
					}	
					
					listDeliveryItemTmps[m]["actualExportedQuantityQC"] = listDeliveryItemTmps[m].amount;
					listDeliveryItemTmps[m]["quantity"] = listDeliveryItemTmps[m].amount;
				} else {
					listDeliveryItemTmps[m]["actualExportedQuantity"] = listDeliveryItemTmps[m].quantity;
					
					var qcQty = listDeliveryItemTmps[m].actualExportedQuantity;
					if (convertNumber != 1) {
						qcQty = Math.floor(listDeliveryItemTmps[m].actualExportedQuantity/convertNumber);
					} else {
						qcQty = 0;
					}
					listDeliveryItemTmps[m]["actualDeliveredQuantityQC"] = qcQty;
					listDeliveryItemTmps[m]["actualDeliveredQuantityEA"] = listDeliveryItemTmps[m].quantity;
					listDeliveryItemTmps[m]["actualDeliveredQuantity"] = listDeliveryItemTmps[m].quantity;
				}
			}
		}
		deliveryItemData = listDeliveryItemTmps;
		
		/* TODO listLocationData = getLocationData(glDestFacilityId);
		var listLocTmp = [];
		for (var x in listLocationData) {
			listLocTmp.push({
				value: listLocationData[x].locationCode, 
				label: listLocationData[x].locationCode, 
			});
		}
		var locationSource =
        {
             datatype: "array",
             datafields: [
                 { name: 'value', type: 'string' },
                 { name: 'label', type: 'string' },
             ],
             localdata: listLocTmp,
        };
        locationAdapter = new $.jqx.dataAdapter(locationSource, {
            autoBind: true
        }); */
		loadDeliveryItem(listDeliveryItemTmps);
		
		$("#detailpopupWindow").jqxWindow('open');
	};
	
	var getLocationData = function (facilityId) {
		var listLocationDataTmp = null;
		$.ajax({
            type: "POST",
            url: "getLocationFacilityLeafAjax",
            data: {'facilityId': facilityId},
            dataType: "json",
            async: false,
            success: function(response){
            	listLocationDataTmp = response['listlocationFacility'];
            },
            error: function(response){
              alert("Error:" + response);
            }
		});
		return listLocationDataTmp;
	};
	
	var getDeliveryItemDetail = function (deliveryId) {
		var listDeliveryItems = null;
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
		return listDeliveryItems;
	};
	
	function checkGridDeliveryItemRequiredData(rowindex){
	    var data = $('#jqxgrid2').jqxGrid('getrowdata', rowindex);
	    if (data.productId){
	    	if(data.statusId == 'DELI_ITEM_EXPORTED'){
		        if(data.actualDeliveredQuantity == null){
		            $('#jqxgrid2').jqxGrid('unselectrow', rowindex);
		            bootbox.dialog(uiLabelMap.MissingActualDeliveredQty + " " + uiLabelMap.For.toLowerCase() + " " + uiLabelMap.Product.toLowerCase() + ": " + data.productCode, [{
		                "label" : uiLabelMap.OK,
		                "class" : "btn btn-primary standard-bootbox-bt",
		                "icon" : "fa fa-check",
		                "callback": function() {
	                        	$("#jqxgrid2").jqxGrid('begincelledit', rowindex, "actualDeliveredQuantity");
		                	}
		                }]
		            );
		            return true;
		        }
		        if (data.actualDeliveredQuantity > 0){
		        	if(!data.actualManufacturedDate && data.mnfRequired == "Y"){
			            bootbox.dialog(uiLabelMap.MissingManufactureDate + " " + uiLabelMap.For.toLowerCase() + " " + uiLabelMap.Product.toLowerCase() + ": " + data.productCode, [{
			                "label" : uiLabelMap.OK,
			                "class" : "btn btn-primary standard-bootbox-bt",
			                "icon" : "fa fa-check",
			                "callback": function() {
			                    $("#jqxgrid2").jqxGrid('begincelledit', rowindex, "actualManufacturedDate");
			                }
			                }]
			            );
			            return true;
			        }
		        	if(!data.actualExpireDate && data.expRequired == "Y"){
			            bootbox.dialog(uiLabelMap.MissingExpireDate + " " + uiLabelMap.For.toLowerCase() + " " + uiLabelMap.Product.toLowerCase() + ": " + data.productCode, [{
			                "label" : uiLabelMap.OK,
			                "class" : "btn btn-primary standard-bootbox-bt",
			                "icon" : "fa fa-check",
			                "callback": function() {
			                    $("#jqxgrid2").jqxGrid('begincelledit', rowindex, "actualExpireDate");
			                }
			                }]
			            );
			            return true;
			        }
			        if(!data.batch && data.lotRequired == "Y"){
			            bootbox.dialog(uiLabelMap.MissingBacth  + " " + uiLabelMap.For.toLowerCase() + " " + uiLabelMap.Product.toLowerCase() + ": " + data.productCode, [{
			                "label" : uiLabelMap.OK,
			                "class" : "btn btn-primary standard-bootbox-bt",
			                "icon" : "fa fa-check",
			                "callback": function() {
			                    $("#jqxgrid2").jqxGrid('begincelledit', rowindex, "batch");
			                }
			                }]
			            );
			            return true;
			        }
		        }
		    }
	    } else {
	    	bootbox.dialog(uiLabelMap.PleaseChooseProductAndFulfillData, [{
                "label" : uiLabelMap.OK,
                "class" : "btn btn-primary standard-bootbox-bt",
                "icon" : "fa fa-check",
                "callback": function() {
                    $("#jqxgrid2").jqxGrid('begincelledit', 0, "productId");
                    $("#jqxgrid2").jqxGrid('unselectrow', 0);
                }
                }]
            );
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
	    return false;
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
	        },
	        error: function(res){
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
    
    var renderHtmlContainGrids = function renderHtmlContainGrids() {
		var htmlRenderTabs = "<div id='jqxTabsDlvItem' style='margin-left: 20px !important; margin-right: 20px !important; border: 1px solid #CCC !important;'><ul style='margin-left: 8px' id='tabDynamic'>";
		var htmlRenderGrids = "";
        htmlRenderTabs += "<li value=\"acas\" style=\"margin-top: 6px; height: 15px !important; border-bottom-width: 0px; border-color: white;\">accs</li>";
        htmlRenderGrids += "<div style='overflow: hidden;'><div style='border:none;' id=\"123\" ></div></div>";
		htmlRenderTabs += "</ul>" + htmlRenderGrids + "</div>";
		$("#jqxTabsContain").html(htmlRenderTabs);
	};
    
    var showPopupSelectFacility = function showPopupSelectFacility(orderId){
		$("#selectFacilityWindow").jqxWindow("open");
		$("#defaultOrderId").val(orderId);
		if(currencyUom !== 'VND') {
            initValidator();
            $("#divConversion").removeClass("hide");
            $("#selectFacilityWindow").jqxWindow({
                height: 250
            });
        }
	};
    
    function quickCreateDelivery(orderId, facilityId, contactMechId, conversionFactor, isApprove){
    	var dlvId;
    	$.ajax({
			type: "POST",
			url: "quickCreateDelivery",
			async: false,
			data: {
				facilityId: facilityId,
				contactMechId: contactMechId,
				orderId: orderId,
                conversionFactor: conversionFactor,
				approveNow: isApprove
			},	
			success: function (res){
				dlvId = res["deliveryId"];
				$('#jqxgridDelivery').jqxGrid('updatebounddata');
				checkCreatedDone();
			}
		});
		$("#selectFacilityWindow").jqxWindow("close");
	}
    
	var addNewRow = function addNewRow(){
		var firstRow = $('#jqxgrid2').jqxGrid('getrowdata', 0);
		if (firstRow.productId){
			$('#jqxgrid2').jqxGrid('clearselection');
			var datarow = generaterow();
	        $("#jqxgrid2").jqxGrid('addrow', null, datarow, "first");
	        $("#jqxgrid2").jqxGrid('unselectrow', 0);
	        $("#jqxgrid2").jqxGrid('begincelledit', 0, "productCode");
		} else {
			$("#jqxgrid2").jqxGrid('begincelledit', 0, "productCode");
		}
	}
	
	function generaterow(productCode){
		var row = {};
		if (productCode){
			for(var i = 0; i < deliveryItemData.length; i++){
				var dlvItem = deliveryItemData[i];
				if (dlvItem.productCode == productCode){
					row["productId"] = dlvItem.productId;
					row["productName"] = dlvItem.productName;
					row["productCode"] = dlvItem.productCode;
					row["fromOrderId"] = dlvItem.fromOrderId;
					row["fromOrderItemSeqId"] = dlvItem.fromOrderItemSeqId;
					row["actualExpireDate"] = null;
					row["isPromo"] = dlvItem.isPromo;
					row["actualManufacturedDate"] = null;
					row["deliveryId"] = dlvItem.deliveryId;
					row["deliveryItemSeqId"] = null;
					row["actualExportedQuantity"] = 0;
					row["actualDeliveredQuantity"] = 0;
					row["actualExportedAmount"] = 0;
					row["actualDeliveredAmount"] = 0;
					row["quantity"] = dlvItem.quantity;
					row["amount"] = dlvItem.amount;
					row["requireAmount"] = dlvItem.requireAmount;
					row["quantityUomId"] = dlvItem.quantityUomId;
					row["orderQuantityUomId"] = dlvItem.orderQuantityUomId;
					row["weightUomId"] = dlvItem.weightUomId;
					row["statusId"] = "DELI_ITEM_EXPORTED";
					row["expRequired"] = dlvItem.expRequired;
					row["mnfRequired"] = dlvItem.mnfRequired;
					row["lotRequired"] = dlvItem.lotRequired;
					row["actualDeliveredQuantityQC"] = 0;
					row["actualDeliveredQuantityEA"] = 0;
					row["convertNumber"] = dlvItem.convertNumber;
					break;
				}
			}
		} else {
			row["deliveryId"] = "";
			row["deliveryItemSeqId"] = "";
			row["fromOrderItemSeqId"] = "";
			row["fromTransferItemSeqId"] = "";
			row["productId"] = "";
			row["productCode"] = "";
			row["isPromo"] = "";
			row["productName"] = "";
			row["requireAmount"] = "";
			row["actualExportedQuantity"] = "";
			row["actualDeliveredQuantity"] = "";
			row["actualExportedAmount"] = "";
			row["actualDeliveredAmount"] = "";
			row["inventoryItemId"] = "";
			row["actualExpireDate"] = "";
			row["actualManufacturedDate"] = "";
			row["expRequired"] = "";
			row["mnfRequired"] = "";
			row["lotRequired"] = "";
			row["actualDeliveredQuantityQC"] = "";
			row["actualDeliveredQuantityEA"] = "";
			row["convertNumber"] = "";
		}
		return row;
	}
	
	var updateRowData = function updateRowData(productCode){
		var datarow = generaterow(productCode);
		var id = $("#jqxgrid2").jqxGrid('getrowid', 0);
        $("#jqxgrid2").jqxGrid('updaterow', id, datarow);
	}
	
	var validateObject = function (object){
		if (object === null || object === undefined || object == '' || object == 'null' || object == 'undefined'){
			return false;
		}
		return true;
	};
	
	var getStatusDescription = function (statusId) {
		for (var i = 0 ; i < statusData.length; i ++) {
			if (statusData[i].statusId == statusId) return statusData[i].description;
		}
		return statusId;
	};
	
	var checkDeliveryExisted = function (deliveryId){
		var check = false;
		$.ajax({
    		url: "checkDeliveryExisted",
    		type: "POST",
    		async: false,
    		data: {
    			deliveryId: $('#deliveryId').val(),
    		},
    		success: function (res){
    			if(res._ERROR_MESSAGE_ == "DELIVERY_ID_EXISTED"){
    				check = true;
	        	} else {
	        		check = false;
	        	}
    		}
    	});
		return check;
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
		var url = "jqGetFacilities";
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
		location.replace("deliveryPurchaseDeliveryDetail?deliveryId="+deliveryId);
	}
	
	return {
		init: init,
		getFormattedDate: getFormattedDate,
		getLocalization: getLocalization,
		getTotalWeight: getTotalWeight,
		showDetailPopup: showDetailPopup,
		renderHtmlContainGrids: renderHtmlContainGrids,
		showPopupSelectFacility: showPopupSelectFacility,
		addNewRow: addNewRow,
		updateRowData : updateRowData,
		initAttachFile: initAttachFile,
		checkCreatedDone: checkCreatedDone,
		generaterow: generaterow,
		editAddNewProduct: editAddNewProduct,
		showDetailDelivery: showDetailDelivery,
	};
}());