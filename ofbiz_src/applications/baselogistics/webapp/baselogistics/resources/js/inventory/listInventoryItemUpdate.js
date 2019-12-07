$(function(){
	InvUpdateObj.init();
});
var InvUpdateObj = (function() {
	var olbPageInvUpdate = new OlbPage();
	var init = function() {
		curIndexSelected = null;
		initInputs();
		initElementComplex();
		initEvents();
		initValidateForm();
	};
	var initInputs = function() {
		initGridInventory();
		$("#contextMenuUpdate").jqxMenu({ width: 220, autoOpenPopup: false, mode: 'popup', theme: theme});
	};
	var initElementComplex = function() {
		
	};
	var initEvents = function() {
		$('#jqxgridInventoryItemUpdate').on('rowselect', function (event) {
		    var args = event.args;
		    var rowBoundIndex = args.rowindex;
		    curIndexSelected = rowBoundIndex;
		});
		$("#contextMenuUpdate").on('itemclick', function (event) {
			var data = $('#jqxgridInventoryItemUpdate').jqxGrid('getRowData', curIndexSelected);
			var tmpStr = $.trim($(args).text());
			if(tmpStr == uiLabelMap.Copy){
				copyRow(data, curIndexSelected);
			}
		});
	};
	
	var initGridInventory = function(){
		var datafield =  [
      		{ name: 'productId', type: 'string'},
			{ name: 'productCode', type: 'string' },
			{ name: 'productName', type: 'string' },
			{ name: 'facilityId', type: 'string'},
			{ name: 'ownerPartyId', type: 'string'},
			{ name: 'requireAmount', type: 'string'},
			{ name: 'weightUomId', type: 'string'},
			{ name: 'facilityName', type: 'string'},
            { name: 'datetimeManufactured', type: 'date', other: 'Timestamp'},
            { name: 'expireDate', type: 'date', other: 'Timestamp'},
            { name: 'initDatetimeManufactured', type: 'date', other: 'Timestamp'},
            { name: 'initExpireDate', type: 'date', other: 'Timestamp'},
			{ name: 'quantityOnHandTotal', type: 'number' },
			{ name: 'amountOnHandTotal', type: 'number' },
			{ name: 'quantity', type: 'number' },
			{ name: 'availableToPromiseTotal', type: 'number' },
			{ name: 'quantityOnHandVar', type: 'number' },
			{ name: 'quantityUomId', type: 'string' },
			{ name: 'statusId', type: 'string' },
			{ name: 'initStatusId', type: 'string' },
			{ name: 'statusDesc', type: 'string' },
			{ name: 'varianceReasonId', type: 'string' },
			{ name: 'rowDetail', type: 'string'},
			{ name: 'lotId', type: 'string' },
			{ name: 'initLotId', type: 'string' },
			{ name: 'datetimeManufacturedOld', type: 'date', other: 'Timestamp'},
            { name: 'expireDateOld', type: 'date', other: 'Timestamp'},
			{ name: 'quantityOnHandTotalOld', type: 'number' },
			{ name: 'lotIdOld', type: 'string' },
			{ name: 'idParent', type: 'string' },
      	];
      	var columnlist = [
          { text: uiLabelMap.SequenceId, sortable: false, filterable: false, editable: false, pinned: true,
			    groupable: false, draggable: false, resizable: false,
			    datafield: '', columntype: 'number', width: 50,
			    cellsrenderer: function (row, column, value) {
			        return '<div style=margin:4px;>' + (value + 1) + '</div>';
			    }
			},
			{ text: uiLabelMap.ProductId, datafield: 'productCode', align: 'left', width: 130, pinned: true, editable: false,},
			{ text: uiLabelMap.ProductName, datafield: 'productName', align: 'left', minwidth: 250,editable: false, cellClassName: cellClass,},
			{ text: uiLabelMap.ProductManufactureDate, dataField: 'datetimeManufactured', editable: true, align: 'left', width: 110, filtertype: 'range',  columntype: 'datetimeinput', cellsformat: 'dd/MM/yyyy', cellsalign: 'right', cellClassName: cellClass,
				cellsrenderer: function(row, colum, value){
					if(value === null || value === undefined || value === ''){
						return '<span class="align-right"></span>';
					}
				},
				initeditor: function (row, column, editor) {
					editor.jqxDateTimeInput({ height: '25px',  formatString: 'dd/MM/yyyy' });
					var data = $('#jqxgridInventoryItemUpdate').jqxGrid('getrowdata', row);
		            if (data.datetimeManufactured){
		            	editor.jqxDateTimeInput('val', data.datetimeManufactured);
		            }
			 	},
			},
			{ text: uiLabelMap.ProductExpireDate, dataField: 'expireDate',editable: true, align: 'left', width: 110, filtertype: 'range',  columntype: 'datetimeinput', cellsformat: 'dd/MM/yyyy', cellsalign: 'right', cellClassName: cellClass,
				cellsrenderer: function(row, colum, value){
					if(value === null || value === undefined || value === ''){
						return '<span class="align-right"></span>';
					}
				},
				initeditor: function (row, column, editor) {
					editor.jqxDateTimeInput({ height: '25px',  formatString: 'dd/MM/yyyy' });
					var data = $('#jqxgridInventoryItemUpdate').jqxGrid('getrowdata', row);
		            if (data.expireDate){
		            	editor.jqxDateTimeInput('val', data.expireDate);
		            }
			 	},
			},
			{ text: uiLabelMap.Batch, datafield: 'lotId', width: 80, editable: true, cellsalign: 'right', cellClassName: cellClass,
				cellsrenderer: function(row, colum, value){
					if(value === null || value === undefined || value === ''){
						return '<span class="align-right"></span>';
					} else {
						return '<span class="align-right">'+value+'</span>';
					}
				},	
				validation: function (cell, value) {
               	 	if(value && !(/^[a-zA-Z0-9_-]+$/.test(value))){
               	 		return { result: false, message: uiLabelMap.ThisFieldMustNotByContainSpecialCharacter};
               	 	}
           	 		return true;
			 	},
			},
			{ text: uiLabelMap.Quantity, dataField: 'quantity', columntype: 'numberinput', width: 100, editable: true, cellClassName: cellClass,
				cellsrenderer: function(row, column, value){
					if (value != null && value != undefined && value != ''){
						return '<span style=\"text-align: right\">' + formatnumber(value) + '</span>';
					} else {
						return '<span style=\"text-align: right\">' + 0	 + '</span>';
					}
				},
				initeditor: function (row, cellvalue, editor, celltext, pressedChar) {
					var data = $('#jqxgridInventoryItemUpdate').jqxGrid('getrowdata', row);
					var requireAmount = data.requireAmount;
					editor.jqxNumberInput({decimalDigits: 0, disabled: false});
                	if (requireAmount && requireAmount == 'Y') {
                		editor.jqxNumberInput({decimalDigits: 2, disabled: false});
                	}
					if (!data.quantity){
						editor.jqxNumberInput('val', 0);
					} else {
						editor.jqxNumberInput('val', data.quantity);
					}
			    },
			    validation: function (cell, value) {
               	 	if(value != null && value != undefined && value != ''){
	               	 	if (value < 0){
	           	 			return { result: false, message: uiLabelMap.NumberGTZ};
	           	 		}
               	 		var data = $('#jqxgridInventoryItemUpdate').jqxGrid('getrowdata', cell.row);
               	 		var qoh = data.quantityOnHandTotal;
               	 		if (data.requireAmount && data.requireAmount == 'Y') {
               	 			qoh = data.amountOnHandTotal;
               	 		}
               	 		if (value > qoh){
               	 			return { result: false, message: uiLabelMap.CannotGreaterThanActualQuantityOnHand + ': ' + value + ' > ' + qoh};
               	 		} 
			    	}
           	 		return true;
			 	},
			},
			{ text: uiLabelMap.Status, datafield: 'statusId', align: 'left', width: 120, filtertype: 'checkedlist',editable: true, columntype: 'dropdownlist', cellClassName: cellClass,
				cellsrenderer: function(row, colum, value){
					for(i=0; i < statusData.length; i++){
			            if(statusData[i].statusId == value){
			            	return '<span title='+value+'>' + statusData[i].description + '</span>';
			            }
			        }
					if (!value || value === 'good'){
		            	return '<span title='+value+'>' + uiLabelMap.InventoryGood + '</span>';
					}
				},
				createfilterwidget: function (column, columnElement, widget) {
					var filterDataAdapter = new $.jqx.dataAdapter(statusData, {
						autoBind: true
					});
					var records = filterDataAdapter.records;
					widget.jqxDropDownList({source: records, displayMember: 'statusId', valueMember: 'statusId',
						renderer: function(index, label, value){
				        	if (statusData.length > 0) {
								for(var i = 0; i < statusData.length; i++){
									if(statusData[i].statusId == value){
										return '<span>' + statusData[i].description + '</span>';
									}
								}
							}
							return value;
						}
					});
					widget.jqxDropDownList('checkAll');
	   			},
	   			initeditor: function(row, value, editor){
			        var data = $('#jqxgridInventoryItemUpdate').jqxGrid('getrowdata', row);
			        editor.jqxDropDownList({ placeHolder: uiLabelMap.PleaseSelectTitle, source: statusData2, selectedIndex: 0, dropDownWidth: '120px', popupZIndex: 755, displayMember: 'description', valueMember: 'statusId',
			        });
			        if (data.statusId === null || data.statusId === undefined || data.statusId === ''){
			        	editor.jqxDropDownList('selectItem', 'good');
			        } else {
			        	editor.jqxDropDownList('selectItem', data.statusId);
			        }
	   			}
			},
			{ hidden: true, datafield: 'amountOnHandTotal', align: 'left',editable: false, width: 100, cellsalign: 'right', filtertype: 'number', columntype: 'numberinput', cellClassName: cellClass,},
			{ text: uiLabelMap.QOH, datafield: 'quantityOnHandTotal', align: 'left',editable: false, width: 100, cellsalign: 'right', filtertype: 'number', columntype: 'numberinput', cellClassName: cellClass,
				cellsrenderer: function(row, colum, value){
					var data = $('#jqxgridInventoryItemUpdate').jqxGrid('getrowdata', row);
					var prodId = data.productId;
					if(typeof(value) == 'number'){
						var uid = data.uid;
						var check = false;
						if (listInvToUpdates != undefined && listInvToUpdates.length > 0){
							var udData = null;
							for (var i = 0; i < listInvToUpdates.length; i ++){
								var data2 = listInvToUpdates[i];
								var check = false;
								if (data.productId != data2.productId) {
									break;
								}
								if (data.expireDate != data2.expireDate || data.datetimeManufactured != data2.datetimeManufactured){
									break;
								}
								if (data.expireDate != null && data.expireDate != undefined && data.expireDate != '') {
									var t1 = new Date(data.expireDate);
									var t2 = new Date(data2.expireDate);
									if (t1.getTime() != t2.getTime()) break;
								}
								if (data.datetimeManufactured != null && data.datetimeManufactured != undefined && data.datetimeManufactured != '') {
									var t1 = new Date(data.datetimeManufactured);
									var t2 = new Date(data2.datetimeManufactured);
									if (t1.getTime() != t2.getTime()) break;
								}
								if (data.lotId != data2.lotId) {
									break;
								}
								if (data.statusId != data2.statusId) {
									break;
								}
								if (data.serialNumber != data2.serialNumber) {
									break;
								}
					    		udData = data2;
							}
							if (udData != null){
								if (data.requireAmount && data.requireAmount == 'Y') {
									if (udData.amountOnHandTotal != value){
										$("#jqxgridInventoryItemUpdate").jqxGrid('setcellvaluebyid', uid, "quantityOnHandTotal", udData.quantityOnHandTotal);
										$("#jqxgridInventoryItemUpdate").jqxGrid('setcellvaluebyid', uid, "amountOnHandTotal", udData.amountOnHandTotal);
										value = udData.amountOnHandTotal;
										check = true;
									}
								} else {
									if (udData.quantityOnHandTotal != value){
										$("#jqxgridInventoryItemUpdate").jqxGrid('setcellvaluebyid', uid, "quantityOnHandTotal", udData.quantityOnHandTotal);
										$("#jqxgridInventoryItemUpdate").jqxGrid('setcellvaluebyid', uid, "amountOnHandTotal", udData.amountOnHandTotal);
										value = udData.quantityOnHandTotal;
									}
								}
							}
						}
						if (!check) {
							if (data.requireAmount && data.requireAmount == 'Y') {
								value = data.amountOnHandTotal;
							}
						}
						return '<span class="align-right">' + formatnumber(parseFloat(value)) + '</span>';
					} 
			    }, 
			    initeditor: function(row, value, editor){
                    var data = $('#jqxgridInventoryItemUpdate').jqxGrid('getrowdata', row);
                    editor.jqxNumberInput('val', data.quantityOnHandTotal);
                    if (data.requireAmount && data.requireAmount == 'Y') {
                    	editor.jqxNumberInput('val', data.amountOnHandTotal);
                    }
			    },
			    validation: function (cell, value) {
			    	var data = $('#jqxgridInventoryItemUpdate').jqxGrid('getrowdata', cell.row);
			    	var qoh = data.quantityOnHandTotalOld;
			    	if (value > qoh) return { result: false, message: uiLabelMap.CannotGreaterThanActualQuantityOnHand + ': ' + qoh};
			    	return true;
			    }
			},
			{ text: uiLabelMap.Unit, datafield: 'quantityUomId', align: 'left', width: 80,editable: false, filtertype: 'checkedlist', cellClassName: cellClass, filterable: false,
				cellsrenderer: function(row, colum, value){
					if(value){
						var data = $('#jqxgridInventoryItemUpdate').jqxGrid('getrowdata', row);
						if (data.requireAmount && data.requireAmount == 'Y') {
							value = data.weightUomId;
						}
						return '<span>' +  getUomDescription(value) + '</span>';
					}
			    }, 
			    createfilterwidget: function (column, columnElement, widget) {
					var filterDataAdapter = new $.jqx.dataAdapter(uomData, {
						autoBind: true
					});
					var records = filterDataAdapter.records;
					widget.jqxDropDownList({source: records, displayMember: 'uomId', valueMember: 'uomId', dropDownWidth: 'auto', autoDropDownHeight: 'auto',
						renderer: function(index, label, value){
				        	if (uomData.length > 0) {
								for(var i = 0; i < uomData.length; i++){
									if(uomData[i].uomId == value){
										return '<span>' + uomData[i].description + '</span>';
									}
								}
							}
							return value;
						}
					});
					widget.jqxDropDownList('checkAll');
	   			}
			},
      	];
      	
      	var config = {
  			width: '100%', 
	   		virtualmode: true,
	   		showtoolbar: false,
	   		selectionmode: 'checkbox',
	   		pageable: true,
	   		sortable: true,
	        filterable: true,	        
	        editable: true,
	        editmode: 'click',
	        rowsheight: 26,
	        rowdetails: false,
	        url: '',  
	        useUrl: true,
	        source: {pagesize: 10}
      	};
      	Grid.initGrid(config, datafield, columnlist, null, $("#jqxgridInventoryItemUpdate"));
      	Grid.createContextMenu($("#jqxgridInventoryItemUpdate"), $("#contextMenuUpdate"), false);
      	
	};
	var initValidateForm = function(){
	};
	
	function copyRow(originData, index){
		var data = jQuery.extend({}, originData);
		data["quantity"] = null;
		data["serialNumber"] = null;
		data["serialNumberNew"] = null;
		if (originData.idParent) {
			data["idParent"] = originData.idParent;
		} else {
			data["idParent"] = originData.uid;
		}
		$("#jqxgridInventoryItemUpdate").jqxGrid('addrow', null, data, index);
		
		$('#jqxgridInventoryItemUpdate').jqxGrid('unselectrow', index);
		if (originData.quantity != null && originData.quantity != undefined && originData.quantity != '' && originData.quantity > 0){
			$('#jqxgridInventoryItemUpdate').jqxGrid('selectrow', index+1);
		} else {
			$('#jqxgridInventoryItemUpdate').jqxGrid('unselectrow', index+1);
		}
	}
	
	return {
		init: init,
	}
}());