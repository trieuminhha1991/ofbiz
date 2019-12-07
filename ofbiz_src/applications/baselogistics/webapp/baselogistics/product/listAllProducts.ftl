<#include "script/listAllProductsScript.ftl"/>
<div id="products">
<#assign dataFields = "[
		{name: 'productId', type: 'string'},
		{name: 'productCode', type: 'string'},
		{name: 'productType', type: 'string'},
		{name: 'parentProductId', type: 'string'},
   		{name: 'productName', type: 'string'},
   		{name: 'quantityUomId', type: 'string'},
   		{name: 'parentProductId', type: 'string'},
   		{name: 'quantityUomId', type: 'string'},
   		{name: 'weightUomId', type: 'string'},
   		{name: 'weight', type: 'number'},
   		{name: 'quantityOnHandTotal', type: 'number'},
	]"/>
<#assign columnlists = "
	{ text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, editable: false, pinned: true,
	    groupable: false, draggable: false, resizable: false,
	    datafield: '', columntype: 'number', width: 50,
	    cellsrenderer: function (row, column, value) {
	        return '<div style=margin:4px;>' + (value + 1) + '</div>';
	    }
	},
	{text: '${StringUtil.wrapString(uiLabelMap.ProductId)}', dataField: 'productCode', width: 150, editable:false},
	{text: '${StringUtil.wrapString(uiLabelMap.ProductName)}', dataField: 'productName', minwidth: 200, editable: false},
	{ text: '${uiLabelMap.QOH}', datafield: 'quantityOnHandTotal', align: 'left', width: 150, cellsalign: 'right', filtertype: 'number', filterable: false, sortable: false,
		cellsrenderer: function(row, colum, value){
			if(value){
				return '<span style=\"text-align: right;\">' + formatnumber(value) + '</span>';
			}
	    },
	},
	{ text: '${uiLabelMap.UnitPacking}', datafield: 'quantityUomId', align: 'left', width: 120, filtertype: 'checkedlist', filterable: false, filterable: false, sortable: false,
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
	{ text: '${uiLabelMap.WeightBase}', datafield: 'weight', align: 'left', width: 150, cellsalign: 'right', filtertype: 'number', filterable: false, sortable: false,
		cellsrenderer: function(row, colum, value){
			if(value){
				return '<span style=\"text-align: right;\">' + formatnumber(value) + '</span>';
			}
	    },
	},
	{ text: '${uiLabelMap.UnitWeight}', datafield: 'weightUomId', align: 'left', width: 125, filtertype: 'checkedlist', filterable: false, filterable: false, sortable: false,
		cellsrenderer: function (row, column, value){
			for (var i = 0; i < weightUomData.length; i ++){
				if (weightUomData[i].weightUomId == value){
					return '<span style=\"text-align: right\">' + weightUomData[i].description +'</span>';
				}
			}
		},
		createfilterwidget: function (column, columnElement, widget) {
			var filterDataAdapter = new $.jqx.dataAdapter(weightUomData, {
				autoBind: true
			});
			var records = filterDataAdapter.records;
			widget.jqxDropDownList({source: records, displayMember: 'weightUomId', valueMember: 'weightUomId', dropDownWidth: 'auto', autoDropDownHeight: 'auto',
				renderer: function(index, label, value){
		        	if (weightUomData.length > 0) {
						for(var i = 0; i < weightUomData.length; i++){
							if(weightUomData[i].weightUomId == value){
								return '<span>' + weightUomData[i].description + '</span>';
							}
						}
					}
					return value;
				}
			});
			widget.jqxDropDownList('checkAll');
		}
	},
"/>
<@jqGrid id="jqxgridProduct" clearfilteringbutton="false" editable="false" alternativeAddPopup="alterpopupWindow" columnlist=columnlists dataField=dataFields 
	viewSize="10" showtoolbar="true" editmode="click" selectionmode="multiplecellsadvanced" width="100%" bindresize="false" 
	url="jqxGeneralServicer?sname=JQGetListProductByOrganiztion&orgPartyId=${company}" 
	customcontrol1="fa-print@${uiLabelMap.PrintBarCode}@javascript:ListProductObj.preparePrintBarcodeProducts();"
/>
</div>