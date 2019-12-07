<style>
#pagerjqxgridProductAgreement{
	display: none;
}
</style>
<#assign lotId = parameters.lotId />
<#assign productPlanId = parameters.productPlanId />
<#assign localeStr = "VI" />
<#if locale = "en">
<#assign localeStr = "EN" />
</#if>



<#assign dataField="[{ name: 'productPlanId', type: 'string'},
{ name: 'lotId', type: 'string'},
{ name: 'orderId', type: 'string'},
{ name: 'orderItemSeqId', type: 'string'},
{ name: 'productId', type: 'string'},
{ name: 'lotQuantity', type: 'number'},
{ name: 'productName', type: 'string'},
{ name: 'supplierProductId', type: 'string'},
{ name: 'currencyUomId', type: 'string'},
{ name: 'unitPrice', type: 'number'},
{ name: 'goodValue', type: 'number'},
{ name: 'quantityUomId', type: 'string'},
{ name: 'quantityUomName', type: 'string'}
]"/>




<#assign columnlist="
{ text: 'No.', datafield: 'supplierProductId', editable: false, width: 80},
{ text: '${StringUtil.wrapString(uiLabelMap.orderId)}', datafield: 'orderId', editable: false, hidden: false},
{ text: '${StringUtil.wrapString(uiLabelMap.orderItemSeqId)}', datafield: 'orderItemSeqId', editable: false, hidden: true},
{ text: '${StringUtil.wrapString(uiLabelMap.productPlanId)}', datafield: 'productPlanId', editable: false, hidden: true},
{ text: '${StringUtil.wrapString(uiLabelMap.lotId)}', datafield: 'lotId', width: 120, editable: false, hidden: true},
{ text: '${StringUtil.wrapString(uiLabelMap.productId)}', datafield: 'productId', width: 150, editable: false, hidden: true },
{ text: '${StringUtil.wrapString(uiLabelMap.productName)}', datafield: 'productName', editable: false},
{ text: '${StringUtil.wrapString(uiLabelMap.uomId)}', dataField: 'quantityUomName', width: 150, editable: false},
{ text: '${StringUtil.wrapString(uiLabelMap.Quantity)}', datafield: 'lotQuantity', width: 120, editable: false, columntype:'numberinput', cellsalign: 'right',
	cellsrenderer: function(row, colum, value){
 		   return '<div style=\"text-align: right; margin-right:10px;margin-top: 5px;\">' +value.toLocaleString('${localeStr}')+'</div>';
	}
},
{ text: '${StringUtil.wrapString(uiLabelMap.unitPrice)}', dataField: 'unitPrice', width: 100, editable: true,columntype:'numberinput', cellsalign: 'right',
	cellsrenderer: function(row, colum, value){
		   return '<div style=\"text-align: right; margin-right:10px;margin-top: 5px;\">' +value.toLocaleString('${localeStr}')+'</div>';
	},
	cellendedit: function (row, datafield, columntype, oldvalue, newvalue) {
		var data = $('#jqxgridProductAgreement').jqxGrid('getRowData', row);
		var total = parseFloat(newvalue) * parseFloat(data.lotQuantity);
		$('#jqxgridProductAgreement').jqxGrid('setcellvalue', row, 'goodValue', total);
	},
	createeditor: function (row, cellvalue, editor) {
        editor.jqxNumberInput({inputMode: 'simple', spinMode: 'advanced', groupSeparator: '.', min:0, decimalDigits:2 });
    }
},
{ text: '${StringUtil.wrapString(uiLabelMap.Value)}', datafield: 'goodValue', width: 150, editable: false, cellsalign: 'right', 
	cellsrenderer: function (index, datafield, value, defaultvalue, column, rowdata) {
		var total = parseFloat(rowdata.unitPrice) * parseFloat(rowdata.lotQuantity);
		return '<div style=\"text-align: right; margin-right:10px;margin-top: 5px;\">' +value.toLocaleString('${localeStr}')+ '</div>';
	},
	aggregates: ['sum'],
    aggregatesrenderer: 
	 	function (aggregates, column, element, summaryData) 
	 	{
          var renderstring = \"\";
           $.each(aggregates, function (key, value) {
              renderstring += '<div style=\"color: ' + 'red' + '; position: relative; margin: 6px; text-align: right; overflow: hidden;\">' + '<b>${uiLabelMap.totalPrice}:<\\/b>' + '<br>' +  value.toLocaleString('VI')  + \"&nbsp;${defaultOrganizationPartyCurrencyUomId?if_exists}\";
              });                          
          	  renderstring += \"</div>\";
          return renderstring; 
	 	}
},

"/>


<@jqGrid filtersimplemode="true" id="jqxgridProductAgreement" filterable="false" addType="" dataField=dataField editmode="selectedcell" editable="true" columnlist=columnlist clearfilteringbutton="false" showtoolbar="false" addrow="false"
	bindresize="false" pageable="false" showlist="true" sortable="false" viewSize="15" columnsresize="false"
	url="jqxGeneralServicer?sname=jqxGetProductForAgreement&agreementId=${agreementId}&productPlanId=${productPlanId?if_exists}&lotId=${lotId?if_exists}" height="400" width="1160" statusbarheight="30"
	autoheight="true" showstatusbar="true" statusbarheight="50"
/>



















