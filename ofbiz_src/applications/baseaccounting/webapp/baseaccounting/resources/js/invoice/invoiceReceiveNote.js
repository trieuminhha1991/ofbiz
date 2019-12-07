var invoiceReceiveNoteObj = (function(){
	var init = function(){
		initGrid();
		initWindow();
		initEvent();
	};
	var initGrid = function(){
		var grid = $("#invoiceReceiveNoteGrid");
		var updateDataFieldValue = function(row, datafield, value){
			grid.jqxGrid('setcellvalue', row, datafield, value);
		};
		var datafield = [{name: 'deliveryId', type: 'string' },
	                 	 {name: 'deliveryItemSeqId', type: 'string' },
	                 	 {name: 'fromOrderId', type: 'string' },
	                 	 {name: 'fromOrderItemSeqId', type: 'string' },
		                 {name: 'productId', type: 'string'},
		                 {name: 'productCode', type: 'string'},
		                 {name: 'productName', type: 'string'},
		                 {name: 'actualDeliveredQuantity', type: 'number'},
		                 {name: 'weightUomId', type: 'string'},
		                 {name: 'quantityUomId', type: 'string'},
		                 {name: 'requireAmount', type: 'number'},
		                 {name: 'adjustedQuantity', type: 'number'},
		                 {name: 'editQuantity', type: 'number'},
		                 {name: 'amount', type: 'number'},
		                 {name: 'amountEdit', type: 'number'},
		                 {name: 'invQuantityUomId', type: 'string'},
		                 {name: 'invQuantity', type: 'number'},
		                 {name: 'modifiedUserLoginId', type: 'string'},
		                 {name: 'invoiceId', type: 'string'},
		                 {name: 'invoiceItemSeqId', type: 'string'},
		                 {name: 'shipmentReceiptId', type: 'string'}
		                 ];
		var columns = [{text: uiLabelMap.ProductId, datafield: 'productCode', width: '11%', editable: false},
		               {text: uiLabelMap.ProductName, datafield: 'productName', width: '26%', editable: false},
		               {text: uiLabelMap.ActualReceivedQuantitySum, datafield: 'actualDeliveredQuantity', width: '11%', columntype: 'numberinput', editable: false,
		            	   cellsrenderer: function (row, column, value){
		            		   if(typeof(value) == 'number'){
		            			   return '<span class="align-right">' + value + '</span>'
		            		   }
		            	   }
		               },
		               {text: uiLabelMap.Unit, datafield: 'quantityUomId', width: '6%', editable: false,
		            	   cellsrenderer: function (row, column, value){
		            		   var data = grid.jqxGrid('getrowdata', row);
		            		   if (data.productId){
		            			   if (data.requireAmount && data.requireAmount == 'Y') {
		            				   return '<span style="text-align: right">' + data.weightUomId +'</span>';
		            			   }else{
		            				   return '<span style="text-align: right">' + data.quantityUomId +'</span>';
		            			   }
		            		   }else{
		            			   return '<span style="text-align: right">...</span>';
		            		   }
		            	   }
		               },
		               {text: uiLabelMap.BACCQuantityOnInvoice, datafield: 'invQuantity', width: '10%', columntype: 'numberinput', editable: false,
		            	   cellsrenderer: function (row, column, value){
		            		   if(typeof(value) == 'number'){
		            			   return '<span class="align-right">' + value + '</span>'
		            		   }
		            	   }
		               },
		               {text: uiLabelMap.Unit, datafield: 'invQuantityUomId', width: '8%', editable: false},
		               {text: uiLabelMap.BSAdjustment, datafield: 'adjustedQuantity', width: '9%', columntype: 'numberinput',
		            	   cellsrenderer: function (row, column, value){
		            		   if(typeof(value) == 'number' && value > 0){
		            			   return '<span class="align-right">-' + value + '</span>'
		            		   } else {
		            			   return '<span class="align-right">0</span>'
		            		   }
		            	   },
		            	   createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight){
		            		   var rowData = grid.jqxGrid('getrowdata', row);
		            		   if (rowData) {
		            			   if (rowData.requireAmount == 'Y') {
		            				   editor.jqxNumberInput({width: cellwidth, height: cellheight, inputMode: 'simple', decimalDigits: 2});
		            			   } else {
		            				   editor.jqxNumberInput({width: cellwidth, height: cellheight, inputMode: 'simple', decimalDigits: 0});
		            			   }
		            		   }
		            	   },
		            	   initeditor: function(row, cellvalue, editor, celltext, pressedChar){
		            		   if(typeof(cellvalue) == 'number'){
		            			   editor.val(cellvalue);
		            		   }
		            	   },
		            	   geteditorvalue: function(row, cellvalue, editor){
		            		   return editor.val();
		            	   },
		            	   cellendedit: function (row, datafield, columntype, oldvalue, newvalue) {
		            		   var rowData = grid.jqxGrid('getrowdata', row);
		            		   updateDataFieldValue(row, 'editQuantity', (rowData.invQuantity - newvalue).toFixed(2));
		            		   return true;
		            	   },
		            	   validation: function (cell, value) {
		            		   var row = cell.row;
		            		   var rowData = grid.jqxGrid('getrowdata', row);
		            		   if(value > rowData.invQuantity){
		            			   return {result: false, message: uiLabelMap.CannotAdjustExceedInvoiceQuantity};
		            		   }
		            		   if(value < 0){
		            			   return {result: false, message: uiLabelMap.ValueMustBeGreateThanZero};
		            		   }
		            		   return true;
		            	   },
		            	   cellclassname: function (row, columnfield, value) {
								return 'background-prepare';
							} 
		               },
		               {text: uiLabelMap.QuantityAfterEdited, datafield: 'editQuantity', width: '12%', columntype: 'numberinput',
		            	   cellsrenderer: function (row, column, value){
		            		   var rowData = grid.jqxGrid('getrowdata', row);
		            		   var adjustValue = typeof(rowData.adjustedQuantity) == 'number'? rowData.adjustedQuantity: 0; 
		            		   if(typeof(value) == 'number' && adjustValue >= 0){
		            			   return '<span class="align-right">' + value + '</span>';
		            		   }
		            		   return '<span class="align-right"> ' + uiLabelMap.BACCNotChange + '</span>';
		            	   },
		            	   createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight){
		            		   var rowData = grid.jqxGrid('getrowdata', row);
		            		   if (rowData) {
		            			   if (rowData.requireAmount == 'Y') {
		            				   editor.jqxNumberInput({width: cellwidth, height: cellheight, inputMode: 'simple', decimalDigits: 2});
		            			   } else {
		            				   editor.jqxNumberInput({width: cellwidth, height: cellheight, inputMode: 'simple', decimalDigits: 0});
		            			   }
		            		   }
		            	   },
		            	   initeditor: function(row, cellvalue, editor, celltext, pressedChar){
		            		   if(typeof(cellvalue) == 'number'){
		            			   editor.val(cellvalue);
		            		   }
		            	   },
		            	   geteditorvalue: function(row, cellvalue, editor){
		            		   return editor.val();
		            	   },
		            	   cellendedit: function (row, datafield, columntype, oldvalue, newvalue) {
		            		   var rowData = grid.jqxGrid('getrowdata', row);
		            		   updateDataFieldValue(row, 'adjustedQuantity', (rowData.invQuantity - newvalue).toFixed(2));
		            		   return true;
		            	   },
		            	   validation: function (cell, value) {
		            		   var row = cell.row;
		            		   var rowData = grid.jqxGrid('getrowdata', row);
		            		   if(value < 0){
		            			   return {result: false, message: uiLabelMap.ValueMustBeGreateThanZero};
		            		   }
		            		   if(value > rowData.invQuantity){
		            			   return {result: false, message: uiLabelMap.ValueMustBeEqualOrLessThanInvoiceQty};
		            		   }
		            		   return true;
		            	   },
		            	   cellclassname: function (row, columnfield, value) {
								return 'background-prepare';
		            	   } 
		               },
		               {text: uiLabelMap.BACCInputPrice, datafield: 'amount', width: '12.5%', columntype: 'numberinput', editable: false,
		            	   cellsrenderer: function (row, column, value){
		            		   if(typeof(value) == 'number'){
		            			   return '<span class="align-right">' + formatcurrency(value, globalVar.currencyUomId) + '</span>';
		            		   }
		            	   }
		               },
		               {text: uiLabelMap.BACCModifiedPrice, datafield: 'amountEdit', width: '12,5%', columntype: 'numberinput',
		            	   cellsrenderer: function (row, column, value){
		            		   var rowData = grid.jqxGrid('getrowdata', row);
		            		   if(typeof(value) == 'number' && value >= 0 && value != rowData.amount){
		            			   return '<span class="align-right">' + formatcurrency(value, globalVar.currencyUomId) + '</span>';
		            		   }
		            		   return '<span class="align-right"> ' + uiLabelMap.BACCNotChange + '</span>';
		            	   },
		            	   cellclassname: function (row, columnfield, value) {
								return 'background-prepare';
		            	   },
		            	   createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight){
		            		   editor.jqxNumberInput({width: cellwidth, height: cellheight, inputMode: 'advanced', decimalDigits: 2, });
		            	   },
		            	   initeditor: function(row, cellvalue, editor, celltext, pressedChar){
		            		   var rowData = grid.jqxGrid('getrowdata', row);
		            		   var amount = rowData.amount;
		            		   if(typeof(cellvalue) == 'number' && cellvalue != amount){
		            			   editor.val(cellvalue);
		            		   } else {
		            			   editor.val(0);
		            		   }
		            	   },
		            	   geteditorvalue: function(row, cellvalue, editor){
		            		   return editor.val();
		            	   },
		            	   validation: function (cell, value) {
		            		   if(value < 0){
		            			   return {result: false, message: uiLabelMap.ValueMustBeGreateThanZero};
		            		   }
		            		   return true;
		            	   }
		               },
		               {text: uiLabelMap.BACCUserLoginModified, datafield: 'modifiedUserLoginId', width: '13%', editable: false}
		               ];
		var rendertoolbar = function(toolbar){
			toolbar.html("");
			var id = "invoiceReceiveNoteGrid";
			var jqxheader = $("<div id='toolbarcontainer" + id +"' class='widget-header'><h4>" + uiLabelMap.ListProduct + "</h4><div id='toolbarButtonContainer" + id + "' class='pull-right'></div></div>");
			toolbar.append(jqxheader);
	     	var container = $('#toolbarButtonContainer' + id);
	        var maincontainer = $("#toolbarcontainer" + id);
		};
		var config = {
				url: '',
				showtoolbar : true,
				rendertoolbar: rendertoolbar,
				width : '100%',
				virtualmode: false,
				editable: true,
				selectionmode: 'singlecell',
				editmode: 'selectedcell',
				localization: getLocalization(),
				pageable: true,
				source: {
					localdata: [],
					pagesize: 10
				}
		};
		Grid.initGrid(config, datafield, columns, null, grid);
	};
	var initWindow = function(){
		accutils.createJqxWindow($("#invoiceReceiveNoteWindow"), 950, 500);
	};
	var initEvent = function(){
		$("#invoiceReceiveNoteWindow").on('open', function(e){
			Loading.show('loadingMacro');
			$.ajax({
				url: 'getDeliveryItemDetailByInvoice',
				data: {invoiceId: globalVar.invoiceId},
				type: 'POST',
				success: function(response){
					if(response._ERROR_MESSAGE_){
						bootbox.dialog(response._ERROR_MESSAGE_,
								[
								{
									"label" : uiLabelMap.CommonClose,
									"class" : "btn-danger btn-small icon-remove open-sans",
								}]		
						);
						return;
					}
					updateLocaldataGrid($("#invoiceReceiveNoteGrid"), response.deliveryItemList);
				},
				complete: function(){
					Loading.hide('loadingMacro');
				}
			});
		});
		
		$("#editReceiveNoteBtn").click(function(e){
			accutils.openJqxWindow($("#invoiceReceiveNoteWindow"));
		});
		$("#invoiceReceiveNoteWindow").on('close', function(e){
			updateLocaldataGrid($("#invoiceReceiveNoteGrid"), []);
		});
		$("#cancelEditReceiveNote").click(function(e){
			$("#invoiceReceiveNoteWindow").jqxWindow('close');
		});
		$("#saveEditReceiveNote").click(function(e){
			bootbox.dialog(uiLabelMap.AreYouSureWantToChangeReceiveNote,
					[{
						"label" : uiLabelMap.CommonSubmit,
						"class" : "btn-primary btn-small icon-ok open-sans",
						"callback": function() {
							editReceiveNote();
						}
					},
					{
						"label" : uiLabelMap.CommonCancel,
						"class" : "btn-danger btn-small icon-remove open-sans",
					}]	
			);
		});
	};
	var editReceiveNote = function(){
		Loading.show('loadingMacro');
		var deliveryItemChangeArr = [];
		var rows = $("#invoiceReceiveNoteGrid").jqxGrid('getrows');
		rows.forEach(function(row){
			if(typeof(row.adjustedQuantity) == 'number' || typeof(row.amountEdit) == 'number'){
				var tempRow = {
						deliveryId: row.deliveryId, 
						deliveryItemSeqId: row.deliveryItemSeqId, 
						invoiceItemSeqId: row.invoiceItemSeqId,
						shipmentReceiptId: row.shipmentReceiptId,
						orderId: row.fromOrderId, 
						orderItemSeqId: row.fromOrderItemSeqId,
						productId: row.productId
				};
				if(typeof(row.editQuantity) == 'number'){
					tempRow.invQuantity = row.invQuantity;
					tempRow.editQuantity = row.editQuantity;
				}
				if(typeof(row.amountEdit) == 'number'){
					tempRow.amountEdit = row.amountEdit;
					tempRow.amount = row.amount;
				}
				deliveryItemChangeArr.push(tempRow);
			}
		});
		$.ajax({
			url: 'modifyDeliveryItem',
			data: {invoiceId: globalVar.invoiceId, deliveryItemChange: JSON.stringify(deliveryItemChangeArr)},
			type: 'POST',
			success: function(response){
				if(response._ERROR_MESSAGE_){
					bootbox.dialog(response._ERROR_MESSAGE_,
							[{
								"label" : uiLabelMap.CommonClose,
								"class" : "btn-danger btn-small icon-remove open-sans",
							}]		
					);
					return;
				}
				$("#invoiceReceiveNoteWindow").jqxWindow('close');
				Grid.renderMessage('jqxgrid', response._EVENT_MESSAGE_, {template : 'success', appendContainer : '#containerjqxgrid'});
				$("#jqxgridInvItem").jqxGrid('updatebounddata');
				location.reload();
			},
			complete: function(){
				Loading.hide('loadingMacro');
			}
		});
	};
	var updateLocaldataGrid = function(grid, localdata){
		var source = grid.jqxGrid('source');
		source._source.localdata = localdata;
		grid.jqxGrid('source', source);
	};
	return{
		init: init
	}
}());
$(document).ready(function () {
	invoiceReceiveNoteObj.init();
});