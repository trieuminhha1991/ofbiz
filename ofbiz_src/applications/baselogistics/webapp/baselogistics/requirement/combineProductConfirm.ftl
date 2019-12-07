<#assign dataFieldConfirms="[
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
<#assign columnlistConfirms="
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
	{ text: '${uiLabelMap.Quantity}', dataField: 'quantity', columntype: 'numberinput', width: 150, editable: false,
		cellsrenderer: function(row, column, value){
			var data = $('#jqxgridInventoryItemSelected').jqxGrid('getrowdata', row);
			var descriptionUom = data.quantityUomId;
			for(var i = 0; i < quantityUomData.length; i++){
				if(data.quantityUomId == quantityUomData[i].quantityUomId){
					descriptionUom = quantityUomData[i].description;
			 	}
			}
			return '<span style=\"text-align: right;\">' + formatnumber(value)+'</span>';
		},
	},
	{ text: '${uiLabelMap.QOH}', datafield: 'quantityOnHandTotal', align: 'left', width: 120, cellsalign: 'right', filtertype: 'number', editable: false,
		cellsrenderer: function(row, colum, value){
			if(value){
				var data = $('#jqxgridInventoryItemSelected').jqxGrid('getrowdata', row);
				var des = null;
				for (var i = 0; i < quantityUomData.length; i ++){
					if (data.quantityUomId == quantityUomData[i].quantityUomId){
						des = quantityUomData[i].description;
					}
				}
				return '<span style=\"text-align: right;\">' + formatnumber(value)+'</span>';
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
				var data = $('#jqxgridInventoryItemSelected').jqxGrid('getrowdata', row);
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
<@jqGrid filtersimplemode="true" id="jqxgridInventoryItemSelected" filterable="false" dataField=dataFieldConfirms columnlist=columnlistConfirms editable="false" showtoolbar="false"
	url="" editmode='click' selectionmode='multiplecellsadvanced'
/>