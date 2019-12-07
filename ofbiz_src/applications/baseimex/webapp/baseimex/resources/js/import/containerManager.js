$(function() {
	ContainerManager.init();
});

var ContainerManager = (function() {
	var mainWindow;
	var resultPackingListHeader;
	var listProductSelected = [];
	var gridProduct = $('#jqxgridPackingListDetail');
	var init = function() {
		mainWindow = $("#popupWindowContainer");
		initJqxElements();
		handleEvents();
		initValidator();
		initGridProduct(gridProduct);
	}
	
	var initGridProduct = function (grid) {
		var columns = [
			{ text: uiLabelMap.SequenceId, sortable: false, filterable: false, editable: false, pinned: true,
	        	  groupable: false, draggable: false, resizable: false,
	        	  datafield: '', columntype: 'number', width: 50,
	        	  cellsrenderer: function (row, column, value) {
	        		  return '<div style="cursor: pointer;">' + (value + 1) + '</div>';
	        	  }
	      	},
			{ text: uiLabelMap.ProductId, datafield: 'productCode', editable: false, width: 120,
				cellsrenderer: function(row, colum, value){
				},
			},
			{ text: uiLabelMap.ProductName, datafield: 'productName', editable: false, minwidth: 150,
				cellsrenderer: function(row, colum, value){
				},
			},
			{ text: uiLabelMap.globalTradeItemNumber, datafield: 'globalTradeItemNumber', width: 120, editable: true, cellclassname: productGridCellclass,},
			{ text: uiLabelMap.batchNumber, datafield: 'batchNumber', width: 120, editable: true , cellclassname: productGridCellclass,},
			{ text: uiLabelMap.packingUnits, datafield: 'packingUnit', filterable: false, width: 120, editable: true, columntype:'numberinput', cellsalign: 'right', cellclassname: productGridCellclass,
				cellsrenderer: function(row, colum, value){
	     		   return '<div class="align-right">' +formatnumber(value)+ '</div>';
				}
			},
			{ text: uiLabelMap.packingUomId, hidden: true, dataField: 'packingUomId', width: 80, editable: true},
			{ text: uiLabelMap.orderUnits, datafield: 'orderUnit', filterable: false, width: 120, editable: true, columntype:'numberinput', cellsalign: 'right', cellclassname: productGridCellclass,
				cellsrenderer: function(row, colum, value){
		     		   return '<div class="align-right">' +formatnumber(value)+ '</div>';
				}
			},
			{ text: uiLabelMap.orderUomId, hidden: true, dataField: 'orderUomId', width: 80, editable: true},
			{ text: uiLabelMap.originOrderUnit, filterable: false, datafield: 'originOrderUnit', width: 120, editable: false, cellsalign: 'right', 
				cellsrenderer: function(row, colum, value){
		     		   return '<div class="align-right">' +formatnumber(value)+ '</div>';
				}
			},
			{ text: uiLabelMap.dateOfManufacture, datafield: 'datetimeManufactured', width: 120, editable: true, columntype: 'datetimeinput', cellsformat: 'dd/MM/yyyy',  cellclassname: productGridCellclass,
				initeditor: function (row, cellvalue, editor, celltext, pressedChar) {
	        		var uid = gridProduct.jqxGrid('getrowid', row);
	        		editor.on('change', function(event){
	        			var jsDate = event.args.date;
	        		});
				}
			},
			{ text: uiLabelMap.ProductExpireDate, datafield: 'expireDate', width: 120, editable: true, columntype: 'datetimeinput', cellsformat: 'dd/MM/yyyy',  cellclassname: productGridCellclass,
				initeditor: function (row, cellvalue, editor, celltext, pressedChar) {
	        		var uid = gridProduct.jqxGrid('getrowid', row);
	        		var datemanu = gridProduct.jqxGrid('getcellvaluebyid', uid, 'datetimeManufactured');
	        		if(datemanu && datemanu != null && datemanu != ''){
	        			editor.jqxDateTimeInput('setMinDate', datemanu);
	        		}else{
	        			
	        		}
				},
				validation: function (cell, value) {
					var uid = gridProduct.jqxGrid('getrowid', cell.row);
	        		var datemanu = gridProduct.jqxGrid('getcellvaluebyid', uid, 'datetimeManufactured');
			        if ((!value || value == null || value == '') && (datemanu && datemanu != null && datemanu != '')) {
			        	return { result: false};
			        }
			        return true;
			    }
			}
        ];
		
		var datafield = [
		 	{ name: 'productId', type: 'string'},
		 	{ name: 'productCode', type: 'string'},
		 	{ name: 'productName', type: 'string'},
			{ name: 'packingListId', type: 'string'},
			{ name: 'packingListSeqId', type: 'string'},
			{ name: 'orderId', type: 'string'},
			{ name: 'orderItemSeqId', type: 'string'},
			{ name: 'datetimeManufactured', type: 'date', other: 'Timestamp'},
			{ name: 'expireDate', type: 'date', other: 'Timestamp'},
			{ name: 'batchNumber', type: 'string'},
			{ name: 'globalTradeItemNumber', type: 'string'},
			{ name: 'packingUnit', type: 'number'},
			{ name: 'packingUomId', type: 'string'},
			{ name: 'orderUnit', type: 'number'},
			{ name: 'originOrderUnit', type: 'number'},
			{ name: 'orderUomId', type: 'string'},
			{ name: 'quantity', type: 'number'},
			{ name: 'itemDescription', type: 'string'}
			]
		
		var rendertoolbar = function (toolbar){
			toolbar.html("");
			var id = "Container";
			var me = this;
			var jqxheader = $("<div id='toolbarcontainer" + id +"' class='widget-header'><h4>" + uiLabelMap.Product + "</h4><div id='toolbarButtonContainer" + id + "' class='pull-right'></div></div>");
			toolbar.append(jqxheader);
	     	var container = $('#toolbarButtonContainer' + id);
	        var maincontainer = $("#toolbarcontainer" + id);
	        var customcontrol1 = "icon-trash open-sans@" + uiLabelMap.CommonDelete + "@javascript:ContainerManager.btnRemoveRowDetail()";
//	        var customcontrol2 = "icon-plus open-sans@" + uiLabelMap.AddProduct + "@javascript:ContainerManager.btnAddNewRowDetail()";
	        Grid.createCustomControlButton(grid, container, customcontrol1);
//	        Grid.createCustomControlButton(grid, container, customcontrol2);
		}; 
		
		var config = {
			width: '100%', 
	   		virtualmode: true,
	   		showtoolbar: true,
	   		rendertoolbar: rendertoolbar,
	   		selectionmode: 'singlerow',
	   		editmode: 'click',
	   		pageable: true,
	   		sortable: true,
	        filterable: true,	        
	        editable: true,
	        rowsheight: 26,
	        rowdetails: false,
	        useUrl: true,
	        url: "",                
	        source: {pagesize: 5}
	  	};
	  	Grid.initGrid(config, datafield, columns, null, grid);
	}
	
	var productGridCellclass = function (row, column, value, data) {
		return 'background-prepare';
	}
	
	var initJqxElements = function() {
		$("#containerNumber").jqxInput({
			height : '22px',
			width : '195px',
			minLength : 1,
			theme : theme
		});
		$("#sealNumber").jqxInput({
			height : '22px',
			width : '195px',
			minLength : 1,
			theme : theme
		});
		$("#orderNumberSupp").jqxInput({
			height : '22px',
			width : '195px',
			minLength : 1,
			theme : theme
		});
		$("#invoiceNumber").jqxInput({
			height : '22px',
			width : '195px',
			minLength : 1,
			theme : theme
		});
		$("#packingListNumber").jqxInput({
			height : '22px',
			width : '195px',
			minLength : 1,
			theme : theme
		});

		$("#totalNetWeight").jqxNumberInput({
			width : '200px',
			height : '25px',
			spinButtons : true,
			theme : theme
		});
		$("#totalGrossWeight").jqxNumberInput({
			width : '200px',
			height : '25px',
			spinButtons : true,
			theme : theme
		});

		$("#packingListDate").jqxDateTimeInput({
			width : '200px',
			theme : theme
		});
		$("#invoiceDate").jqxDateTimeInput({
			width : '200px',
			theme : theme
		});
		$('#packingListDate').jqxDateTimeInput('clear');
		$('#invoiceDate').jqxDateTimeInput('clear');

		$("#orderTypeSupp").jqxComboBox({
			displayMember : 'externalOrderTypeName',
			valueMember : 'externalOrderTypeId',
			autoDropDownHeight : true,
			width : '200px',
			theme : theme,
			searchMode : 'containsignorecase',
			autoOpen : false,
			autoComplete : false
		});

		$("#orderPurchaseId").jqxComboBox({
			displayMember : 'agreementCode',
			valueMember : 'agreementId',
			width : '200px',
			searchMode : 'containsignorecase',
			theme : theme,
			autoOpen : false,
			autoComplete : false
		});

		mainWindow.jqxWindow({
			maxWidth : 1200,
			minWidth : 800,
			width : 1200,
			minHeight : 500,
			height : 920,
			resizable : false,
			cancelButton : $("#alterCancelContainer"),
			keyboardNavigation : true,
			keyboardCloseKey : 15,
			isModal : true,
			autoOpen : false,
			modalOpacity : 0.7,
			theme : theme
		});
		
		$('#jqxMenu').jqxMenu({ width: '300px', autoOpenPopup: false, mode: 'popup', theme: theme});
	};
	var handleEvents = function() {
		$(document).on('click', function (e) {
			$('#jqxMenu').jqxMenu('close');
		});
		$('#orderTypeSupp').on('select', function(event) {
			var args = event.args;
			if (args) {
				var index = args.index;
				var item = args.item;
				var label = item.label;
				var value = item.value;
				var packingListId = $('#packingListId').val();
				if (value == "ORIGINAL") {
					if (packingListId == null || packingListId == '') {
						loadOrderPurchase();
					}
				} else {
					if (packingListId == null || packingListId == '') {
					}
				}
			}
		});

		mainWindow.on('close', function() {
			$('#containerNumber').jqxInput('val', null);
			$('#sealNumber').jqxInput('val', null);
			$('#orderNumberSupp').jqxInput('val', null);
			$('#invoiceNumber').jqxInput('val', null);
			$('#totalNetWeight').jqxNumberInput('val', 0);
			$('#totalGrossWeight').jqxNumberInput('val', 0);
			
			$('#packingListId').jqxInput('val', null);
			$('#packingListDate').jqxDateTimeInput('val', null);
			$('#invoiceDate').jqxDateTimeInput('val', null);
			mainWindow.jqxValidator('hide');
		});
		
		$('#alterSaveContainer').on('click', function(){
			var validate = mainWindow.jqxValidator('validate');
			if (!validate){
				return false;
			}
			var dataJson = getFormPL();
			var dataRows = getPackingListDetail();
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
			    		alterSaveContainerAndPackingList(dataJson, dataRows);
			    		Loading.hide('loadingMacro');
			    	}, 500);
	            }
	        }]);
		});

		$('#saveAndContinueContainer').on('click', function(){
			var dataJson = getFormPL();
			var dataRows = getPackingListDetail();
			alterSaveContainerAndPackingList(dataJson, dataRows);
			$('#containerId').jqxInput('val', null);
			$('#sealNumber').jqxInput('val', null);
			$('#containerNumber').jqxInput('val', null);
			var item = $("#orderPurchaseId").jqxComboBox('getSelectedItem');
			$("#orderPurchaseId").jqxComboBox('removeItem', item.value);
			$("#orderPurchaseId").jqxComboBox('clearSelection');
			$('#orderTypeSupp').jqxComboBox('clearSelection');
			$('#orderNumberSupp').jqxInput('val', null);
			$('#invoiceNumber').jqxInput('val', null);
			$('#totalNetWeight').jqxNumberInput('val', 0);
			$('#totalGrossWeight').jqxNumberInput('val', 0);
			$('#packingListDate').jqxDateTimeInput('val', null);
			$('#invoiceDate').jqxDateTimeInput('val', null);
			$("#orderTypeSupp").jqxComboBox('clearSelection');
			$('#packingListId').jqxInput('val', null);
		});
		
		gridProduct.on("cellendedit", function(event) {
			var args = event.args;
			var dataField = event.args.datafield;
			var rowBoundIndex = event.args.rowindex;
			var value = args.value;
			var oldvalue = args.oldvalue;
			var rowData = args.row;
			if (rowData){
				if (dataField == 'orderUnit'){
					$.each(listProductSelected, function(i){
		   				var olb = listProductSelected[i];
		   				if (olb.productId == rowData.productId ){
		   					listProductSelected.splice(i,1);
		   					return false;
		   				}
		   			});
				
					var item = $.extend({}, rowData);
					item.orderUnit = value;
					listProductSelected.push(item);
				} 
			}
		});
		
		$('#viewDetailCont').on('click', function(){
	 		 AddAgreementToRow(indexParentGrid,'jqxgridDetail'+indexParentGrid);
	 		 var dataRow = $('#jqxgridDetail'+indexParentGrid).jqxGrid('getrowdata', indexChildGrid);
	 		 $('#indexGridDetail').val(indexChildGrid);
	 		 $('#containerId').val(indexChildGrid);
	 		 $('#containerNumber').jqxInput('val', dataRow.containerNumber);
	 		 $('#sealNumber').jqxInput('val', dataRow.sealNumber);
	 		 $('#packingListNumber').jqxInput('val', dataRow.packingListNumber);
	 		 $('#invoiceNumber').jqxInput('val', dataRow.invoiceNumber);
	 		 $('#orderNumberSupp').jqxInput('val', dataRow.externalOrderNumber);
	 		 $('#packingListDate').jqxDateTimeInput('val', dataRow.packingListDate);
	 		 $('#invoiceDate').jqxDateTimeInput('val', dataRow.invoiceDate);
	 		 $('#totalNetWeight').jqxNumberInput('val', dataRow.totalNetWeight);
	 		 $('#totalGrossWeight').jqxNumberInput('val', dataRow.totalGrossWeight);
	 		 jQuery.ajax({
	 	        url: 'getPackingListByContainer',
	 	        type: 'POST',
	 	        async: false,
	 	        data: {containerId: dataRow.containerId},
	 	        dataType: 'json',
	 	        success: function(res){
	 	        	var listPackingList = res.listPackingList;
	 	        	if(listPackingList.length > 0){
	 	        	}
	 	        	var listAgreement = res.listAgreement;
	 	        	if(listAgreement.length > 0){
	 	        	}
	 	        }
	 		 });
	 		 
	 	 });
		
		$('#viewTested').on('click', function(){
			 QADocumentation.open(uiLabelMap.testedDocument);
			 var dataRow = $('#jqxgridDetail'+indexParentGrid).jqxGrid('getrowdata', indexChildGrid);
			 var documentCustomsId="";
			 var registerNumber = "";
			 var registerDate = "";
			 var sampleSendDate = "";
			 jQuery.ajax({
			        url: "getDocumentCustomsByContainer",
			        type: "POST",
			        async: false,
			        data: {documentCustomsTypeId: "TESTED", containerId: dataRow.containerId},
			        dataType: 'json',
			        success: function(res){
			        	documentCustomsId = res.resultListDoc.documentCustomsId;
			        	registerNumber = res.resultListDoc.registerNumber;
			        	registerDate = res.resultListDoc.registerDate;
			        	sampleSendDate = res.resultListDoc.sampleSendDate;
			        }
			 });
			 $('#documentCustomsId').val(documentCustomsId);
			 $('#containerCustomsId').val(dataRow.containerId);
			 $('#documentCustomsTypeId').val("TESTED");
			 $('#registerNumber').val(registerNumber);
			 $('#registerDate').jqxDateTimeInput('val', registerDate);
			 $('#sampleSentDate').jqxDateTimeInput('val', sampleSendDate);
			 $('#customsTypeId').text(uiLabelMap.testedDocument);
		});
		
		$("#createInvoice").on("click", function() {
			 var dataRow = $('#jqxgridDetail'+indexParentGrid).jqxGrid('getrowdata', indexChildGrid);
			 var containerId = dataRow.containerId;
			 window.location.href = "CreateInvoice?containerId=" + containerId;
		});
		
		$('#viewQuarantine').on('click', function(){
			QADocumentation.open(uiLabelMap.quarantineDocument);
			var dataRow = $('#jqxgridDetail'+indexParentGrid).jqxGrid('getrowdata', indexChildGrid);
			var documentCustomsId="";
			var registerNumber = "";
			var registerDate = "";
			var sampleSendDate = "";
			jQuery.ajax({
			        url: "getDocumentCustomsByContainer",
			        type: "POST",
			        async: false,
			        data: {documentCustomsTypeId: "QUARANTINE", containerId: dataRow.containerId},
			        dataType: 'json',
			        success: function(res){
			        	documentCustomsId = res.resultListDoc.documentCustomsId;
			        	registerNumber = res.resultListDoc.registerNumber;
			        	registerDate = res.resultListDoc.registerDate;
			        	sampleSendDate = res.resultListDoc.sampleSendDate;
			        }
		     });
			 $('#documentCustomsId').val(documentCustomsId);
			 $('#containerCustomsId').val(dataRow.containerId);
			 $('#documentCustomsTypeId').val("QUARANTINE");
			 $('#registerNumber').val(registerNumber);
			 $('#registerDate').jqxDateTimeInput('val', registerDate);
			 $('#sampleSentDate').jqxDateTimeInput('val', sampleSendDate);
			 $('#customsTypeId').text(uiLabelMap.quarantineDocument);
		});
		
		$("#agreementToQuarantineChild").on("click", function() {
			var dataRow = $('#jqxgridDetail'+indexParentGrid).jqxGrid('getrowdata', indexChildGrid);
			var containerId = dataRow.containerId;
			window.location.href = "exportAgreementToQuarantine?containerId=" + containerId;
		});
		
		$("#agreementToValidationChild").on("click", function() {
			var dataRow = $('#jqxgridDetail'+indexParentGrid).jqxGrid('getrowdata', indexChildGrid);
			var containerId = dataRow.containerId;
			window.location.href = "exportAgreementToValidation?containerId=" + containerId;
		});
	};
	
	function loadOrderPurchase(){
		var valueCombo = $('#orderPurchaseId').jqxComboBox('getSelectedItem');
		if (valueCombo){
			var agreementId = valueCombo.value;
			var agreementName = valueCombo.label;
			var tmpS = gridProduct.jqxGrid('source');
			tmpS._source.url = "jqxGeneralServicer?sname=jqxGetListOrderItemsAjax&agreementId=" +agreementId;
			gridProduct.jqxGrid('source', tmpS);
			gridProduct.jqxGrid('updatebounddata');
		} else {
			bootbox.dialog(LoadFailPO, [{
				"label" : "error",
				"class" : "btn-small btn-danger",
				"callback": function(){
					$('#orderTypeSupp').jqxComboBox('clearSelection');
					$("#orderPurchaseId").jqxComboBox('focus');
				}
				}]
			);
		}
	}
	
	var initValidator = function() {

	};
	var getValue = function() {
		var value = new Object();

		return value;
	};
	var setValue = function(data) {
		if (!_.isEmpty(data)) {

		}
	};
	
	var open = function(data) {
		ContainerManager.setValue(data);
		mainWindow.jqxWindow("open");
	};
	
	var openPopupAdd = function() {
		mainWindow.jqxWindow("open");
	};

	function alterSaveContainerAndPackingList(dataJson, dataRows) {
		$.ajax({
			url : "createContainerAndPackingList",
			type : "POST",
			data : {
				packingList : JSON.stringify(dataJson),
				packingListDetail : JSON.stringify(dataRows)
			},
			async : false,
			success : function(res) {
				if (res._ERROR_MESSAGE_ || res._ERROR_MESSAGE_LIST_) {
					if (res._ERROR_MESSAGE_){
						jOlbUtil.alert.error(res._ERROR_MESSAGE_);
					}
					if (res._ERROR_MESSAGE_LIST_){
						jOlbUtil.alert.error(res._ERROR_MESSAGE_LIST_[0]);
					}
					return false;
				} else {
					$('#jqxgrid').jqxGrid('updatebounddata');
					mainWindow.jqxWindow('close');
				}
			}
		});
	}

	function getFormPL() {
		var billId = $('#billId').val();
		var containerId = $('#containerId').jqxInput('val');
		var containerNumber = $('#containerNumber').jqxInput('val');
		var sealNumber = $('#sealNumber').jqxInput('val');
		var purchaseOrderId = "";
		var labelPurchaseId = "";
		var valueComboOrder = $('#orderPurchaseId').jqxComboBox(
				'getSelectedItem');
		if (valueComboOrder) {
			purchaseOrderId = valueComboOrder.value;
			labelPurchaseId = valueComboOrder.label;
		}
		var packingListId = $('#packingListId').jqxInput('val');

		var packingListNumber = $('#packingListNumber').jqxInput('val');
		var orderNumberSupp = $('#orderNumberSupp').jqxInput('val');
		var invoiceNumber = $('#invoiceNumber').jqxInput('val');
		var totalNetWeight = $('#totalNetWeight').jqxNumberInput('val');
		totalNetWeight = totalNetWeight.toString();
		var totalGrossWeight = $('#totalGrossWeight').jqxNumberInput('val');
		totalGrossWeight = totalGrossWeight.toString();
		
		var packingListDate = $('#packingListDate').jqxDateTimeInput('getDate')
				.getTime();
		var invoiceDate = $('#invoiceDate').jqxDateTimeInput('getDate')
				.getTime();
		var orderTypeSuppId = "";
		var valueCombo = $('#orderTypeSupp').jqxComboBox('getSelectedItem');
		if (valueCombo) {
			orderTypeSuppId = valueCombo.value;
		}
		var gridDetailId = $('#gridDetailId').val();
		var dataJson = {
			packingListId : packingListId,
			agreementId : purchaseOrderId,
			sealNumber : sealNumber,
			containerNumber : containerNumber,
			containerId : containerId,
			packingListNumber : packingListNumber,
			orderNumberSupp : orderNumberSupp,
			invoiceNumber : invoiceNumber,
			totalNetWeight : totalNetWeight,
			totalGrossWeight : totalGrossWeight,
			packingListDate : packingListDate,
			invoiceDate : invoiceDate,
			orderTypeSuppId : orderTypeSuppId,
			billId : billId,
			gridDetailId : gridDetailId,
			agreementName : labelPurchaseId
		};
		return dataJson;
	}

	function getPackingListDetail() {
		var rows = gridProduct.jqxGrid('getboundrows');
		var rowsReturn = [];
		for (var i = 0; i < rows.length; i++) {
			if (rows[i].productId != "" && rows[i].productId != null) {
				rowsReturn.push(rows[i]);
			}
		}
		for (var i in rowsReturn) {
			var map = rowsReturn[i];
			if (map.datetimeManufactured){
				var x = new Date(map.datetimeManufactured);
				map.datetimeManufactured = x.getTime();
			} else {
				delete map["datetimeManufactured"];
			}
			if (map.expireDate){
				var x = new Date(map.expireDate);
				map.expireDate = x.getTime();
			} else {
				delete map["expireDate"];
			}
		}
		return rowsReturn;
	}

	function btnAddNewRowDetail() {
		var valueCombo = $('#orderTypeSupp').jqxComboBox('getSelectedItem');
		var orderTypeSuppId = null;
		if (valueCombo) {
			orderTypeSuppId = valueCombo.value;
		}
		if (orderTypeSuppId != "ORIGINAL") {
			var datarow = {
				originOrderUnit : 0,
				packingUnit : 0,
				orderUnit : 0
			};
			gridProduct.jqxGrid('addrow', null, datarow,
					'first');
		} else {
			var datarow = {
				originOrderUnit : 0,
				packingUnit : 0,
				orderUnit : 0
			};
			gridProduct.jqxGrid('addrow', null, datarow,
					'first');
		}
	}
	function btnRemoveRowDetail() {
		var selectedrowindex = gridProduct.jqxGrid(
				'getselectedrowindex');
		var rowscount = gridProduct.jqxGrid(
				'getdatainformation').rowscount;
		if (selectedrowindex >= 0 && selectedrowindex < rowscount) {
			var id = gridProduct.jqxGrid('getrowid',
					selectedrowindex);
			var commit = gridProduct
					.jqxGrid('deleterow', id);
		}
	}
	return {
		init : init,
		getValue : getValue,
		setValue : setValue,
		open : open,
		btnAddNewRowDetail : btnAddNewRowDetail,
		btnRemoveRowDetail : btnRemoveRowDetail,
		openPopupAdd: openPopupAdd,
	};
}());