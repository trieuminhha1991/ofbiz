<#include "script/inventoryOriginalScript.ftl"/>
<div id="original-item">
	<#assign dataFieldFrom="[
					{ name: 'productId', type: 'string'},
					{ name: 'productCode', type: 'string' },
					{ name: 'productName', type: 'string' },
					{ name: 'facilityId', type: 'string'},
					{ name: 'facilityName', type: 'string'},
					{ name: 'inventoryItemId', type: 'string' },
	                { name: 'datetimeManufactured', type: 'date', other: 'Timestamp'},
	                { name: 'expireDate', type: 'date', other: 'Timestamp'},
	                { name: 'datetimeReceived', type: 'date', other: 'Timestamp'},
					{ name: 'quantityOnHandTotal', type: 'number' },
					{ name: 'availableToPromiseTotal', type: 'number' },
					{ name: 'quantityUomId', type: 'string' },
					{ name: 'statusId', type: 'string' },
					{ name: 'quantityTmp', type: 'number' },
					{ name: 'quantity', type: 'number' },
					{ name: 'quantityExecuted', type: 'number' },
					{ name: 'inventoryItemLabelId', type: 'string' },
					{ name: 'inventoryItemLabelDesc', type: 'string'},
					{ name: 'lotId', type: 'string' }]"/>
	<#assign columnlistFrom="
					{ text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, editable: false, pinned: true,
					    groupable: false, draggable: false, resizable: false,
					    datafield: '', columntype: 'number', width: 50,
					    cellsrenderer: function (row, column, value) {
					        return '<div style=margin:4px;>' + (value + 1) + '</div>';
					    }
					},
					{ text: '${uiLabelMap.ProductId}', datafield: 'productCode', align: 'left', width: 150, pinned: true, editable: false},
					{ text: '${uiLabelMap.ProductName}', datafield: 'productName', align: 'left', width: 250, pinned: true, editable: false},
					{ text: '${uiLabelMap.ProductLabel}', datafield: 'inventoryItemLabelDesc', align: 'left', minwidth: 150, editable: false,
						cellsrenderer: function(row, colum, value){
							if(value === null || value === undefined || value === ''){
								return '<span>_NA_</span>';
							}
					    }, 
					},
					{ text: '${uiLabelMap.RequiredNumberSum}', dataField: 'quantityTmp', cellsalign: 'right', width: 120, editable: false,
					},
					{ text: '${uiLabelMap.Quantity}', dataField: 'quantity', columntype: 'numberinput', width: 150, editable: true,
						cellsrenderer: function(row, column, value){
							var data = $('#jqxgridItemLabelFrom').jqxGrid('getrowdata', row);
							var descriptionUom = data.quantityUomId;
							for(var i = 0; i < quantityUomData.length; i++){
								if(data.quantityUomId == quantityUomData[i].quantityUomId){
									descriptionUom = quantityUomData[i].description;
							 	}
							}
							if (value === undefined || value === null || value === ''){
								if (value === null || value === undefined || value === ''){
									uid = data.uid;
									$('#jqxgridItemLabelFrom').jqxGrid('setcellvaluebyid', uid, 'quantity', data.quantityTmp);
									value = data.quantityTmp;
									return '<span style=\"text-align: right; background-color: #e6ffb8;\">' + formatnumber(value) +  '</span>';
								}
							} else {
								return '<span style=\"text-align: right; background-color: #e6ffb8;\">' + formatnumber(value) + '</span>';
							}
						},
						initeditor: function (row, cellvalue, editor, celltext, pressedChar) {
					        editor.jqxNumberInput({ decimalDigits: 0});
					    },
					    validation: function (cell, value) {
					    	var data = $('#jqxgridItemLabelFrom').jqxGrid('getrowdata', cell.row);
					        if (value < 0){
					        	return { result: false, message: '${uiLabelMap.ValueMustBeGreateThanZero}'};
					        } else if (!checkRequiredData(cell.row)){
					        	return { result: false, message: '${uiLabelMap.CannotGreaterRequiredNumber}'};
					        }
					        return true;
						 },
					},
					{ text: '${uiLabelMap.QOH}', datafield: 'quantityOnHandTotal', align: 'left', width: 120, cellsalign: 'right', filtertype: 'number', editable: false,
						cellsrenderer: function(row, colum, value){
							if(value){
								var data = $('#jqxgridItemLabelFrom').jqxGrid('getrowdata', row);
								var des = null;
								for (var i = 0; i < quantityUomData.length; i ++){
									if (data.quantityUomId == quantityUomData[i].quantityUomId){
										des = quantityUomData[i].description;
									}
								}
								return '<span style=\"text-align: right;\">' + formatnumber(value) +'</span>';
							} else {
								return '<span style=\"text-align: right;\">0</span>';
							}
					    }, 
					    rendered: function(element){
					    	$(element).jqxTooltip({content: '${StringUtil.wrapString(uiLabelMap.QuantityOnHandTotal)}', theme: 'orange' });
					    }, 
					},
					{ text: '${uiLabelMap.ATP}', datafield: 'availableToPromiseTotal', align: 'left', width: 120, cellsalign: 'right', filtertype: 'number', editable: false,
						cellsrenderer: function(row, colum, value){
							if(value){
								var data = $('#jqxgridItemLabelFrom').jqxGrid('getrowdata', row);
								var des = null;
								for (var i = 0; i < quantityUomData.length; i ++){
									if (data.quantityUomId == quantityUomData[i].quantityUomId){
										des = quantityUomData[i].description;
									}
								}
								return '<span style=\"text-align: right;\">' + formatnumber(value) +'</span>';
							} else {
								return '<span style=\"text-align: right;\">0</span>';
							}
					    },
					    rendered: function(element){
					    	$(element).jqxTooltip({content: '${StringUtil.wrapString(uiLabelMap.AvailableToPromiseTotal)}', theme: 'orange' });
					    }, 
					},
					{ text: '${uiLabelMap.Unit}', datafield: 'quantityUomId', align: 'left', width: 120, filtertype: 'checkedlist',
						cellsrenderer: function (row, column, value){
							for (var i = 0; i < quantityUomData.length; i ++){
								if (quantityUomData[i].quantityUomId == value){
									return '<span style=\"text-align: right\">' + quantityUomData[i].description +'</span>';
								}
							}
						},
						createfilterwidget: function (column, columnElement, widget) {
							var filterDataAdapter = new $.jqx.dataAdapter(quantityUomData, {
								autoBind: true
							});
							var records = filterDataAdapter.records;
							widget.jqxDropDownList({source: records, displayMember: 'quantityUomId', valueMember: 'quantityUomId', dropDownWidth: 'auto', autoDropDownHeight: 'auto',
								renderer: function(index, label, value){
						        	if (quantityUomData.length > 0) {
										for(var i = 0; i < quantityUomData.length; i++){
											if(quantityUomData[i].quantityUomId == value){
												return '<span>' + quantityUomData[i].description + '</span>';
											}
										}
									}
									return value;
								}
							});
							widget.jqxDropDownList('checkAll');
			   			}
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.ProductManufactureDate)}', dataField: 'datetimeManufactured', align: 'left', editable: false, width: 130, columntype: 'datetimeinput', filtertype: 'range', cellsformat: 'dd/MM/yyyy', cellsalign: 'right',
						cellsrenderer: function(row, colum, value){
							if(!value){
								return '<span style=\"text-align: right;\">_NA_</span>';
							}
					    },
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.ProductExpireDate)}', dataField: 'expireDate', align: 'left', editable: false, width: 130, columntype: 'datetimeinput', filtertype: 'range', cellsformat: 'dd/MM/yyyy', cellsalign: 'right',
						cellsrenderer: function(row, colum, value){
							if(!value){
								return '<span style=\"text-align: right;\">_NA_</span>';
							}
					    },
					},
					{ text: '${uiLabelMap.ReceivedDate}', datafield: 'datetimeReceived', align: 'left', width: 130, editable: false, columntype: 'datetimeinput', filtertype: 'range', cellsformat: 'dd/MM/yyyy', cellsalign: 'right' },
				"/>
	
	<@jqGrid filtersimplemode="true" filterable="true" showtoolbar="false" dataField=dataFieldFrom columnlist=columnlistFrom editable="true" editmode="click" 
		url="jqxGeneralServicer?sname=getInventoryLabelFromRequirement&requirementId=${parameters.requirementId?if_exists}" id="jqxgridItemLabelFrom"
			selectionmode="checkbox"
	/>
</div>
<script>
$("#jqxgridItemLabelFrom").on('rowselect', function (event) {
	var index = event.args.rowindex;
	checkRequiredData(index);
});
function checkRequiredData(index){
	var rows = $("#jqxgridItemLabelFrom").jqxGrid('getselectedrowindexes');
	for (var i = 0; i < requirementItemData.length; i ++){
		var productId = requirementItemData[i].productId;
		var requiredQty = requirementItemData[i].quantity;
		var exptQty = 0;
		for (var j = 0; j < rows.length; j ++){
			var data = $("#jqxgridItemLabelFrom").jqxGrid('getrowdata', rows[j]);
			if (data.productId == productId){
				exptQty = exptQty + data.quantity;
			}
		}
		if (requiredQty < exptQty){
			bootbox.dialog("${uiLabelMap.CannotGreaterRequiredNumber} " + exptQty + " > " + requiredQty, [{
                "label" : "${uiLabelMap.OK}",
                "class" : "btn btn-primary standard-bootbox-bt",
                "icon" : "fa fa-check",
                }]
            );
			$('#jqxgridItemLabelFrom').jqxGrid('unselectrow', index);
            return false;
		}
	}
	return true;
}
</script>