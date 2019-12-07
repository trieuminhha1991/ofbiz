<script>
var cellclassname = function (row, columnfield, value) {
	  return "green1";
};
</script>
<style>
	#columntablejqxgridProduct .jqx-checkbox-default {
		display: none;
	}
</style>
<div class="row-fluid">
<div class="span12">
<#assign dataField="[
			{ name: 'orderId', type: 'string' },
			{ name: 'productId', type: 'string' },
			{ name: 'productCode', type: 'string' },
			{ name: 'itemDescription', type: 'string' },
			{ name: 'quantity', type: 'number' },
			{ name: 'unitPrice', type: 'number' },
			{ name: 'orderItemSeqId', type: 'string' },
			{ name: 'orderedQuantity', type: 'number' },
			{ name: 'returnableQuantity', type: 'number' },
			{ name: 'returnReasonId', type: 'string' }]"/>

<#assign columnlist = "
				{ text: '${uiLabelMap.POOrderId}', datafield: 'orderId', width: 100, editable: false, cellclassname: cellclassname, hidden: true },
				{ text: '${uiLabelMap.POProductId}', datafield: 'productId', width: 120, editable: false, cellclassname: cellclassname, hidden: true },
				{ text: '${uiLabelMap.POProductId}', datafield: 'productCode', width: 120, editable: false, cellclassname: cellclassname },
				{ text: '${uiLabelMap.BPOProductName}', dataField: 'itemDescription', editable: false , cellclassname: cellclassname },
				{ text: '${uiLabelMap.orderedQuantityLabel}', datafield: 'orderedQuantity', width: 130, editable: false, filterable: false, cellsalign: 'right', columntype: 'numberinput',
					cellsrenderer: function(row, column, value) {
						return '<span style=\"text-align: right\">' + value.toLocaleString(locale) +'</span>';
					}
				},
				{ text: '${uiLabelMap.POReturnQuantity}', datafield: 'returnableQuantity', width: 120, editable: false, filterable: false, cellsalign: 'right', columntype: 'numberinput' },
				{ text: '${uiLabelMap.BPOQuantity}', datafield: 'quantity', width: 140, editable: true, filterable: false, cellsalign: 'right', columntype: 'numberinput',
					cellsrenderer: function(row, column, value){
						if (value) {
							return '<span style=\"text-align: right\">' + value.toLocaleString(locale) +'</span>';
						}
					}, initeditor: function (row, cellvalue, editor) {
						var rowdata = $('#jqxgridProduct').jqxGrid('getrowdata', row);
						editor.jqxNumberInput({inputMode: 'simple', spinMode: 'simple', groupSeparator: '.', min:0, max: rowdata.returnableQuantity});
					}, cellendedit: function (row, datafield, columntype, oldvalue, newvalue) {
						var rowdata = $('#jqxgridProduct').jqxGrid('getrowdata', row);
						if (newvalue != oldvalue && newvalue <= rowdata.returnableQuantity) {
							$('#jqxgridProduct').jqxGrid('selectrow', row);
						}
						if(newvalue > rowdata.returnableQuantity || newvalue < 0) return false;
					}, validation: function (cell, value) {
						var data = $('#jqxgridProduct').jqxGrid('getrowdata', cell.row);
						if (value > data.returnableQuantity) {
							return { result: false, message: '${uiLabelMap.BPOQuantiyMustBeSmallerThanReturnableQuantity}' };
						}
						return true;
					}
				},
				{ text: '${uiLabelMap.unitPrice}', datafield: 'unitPrice', width: 90, editable: true,filterable: false, cellsalign: 'right', columntype: 'numberinput',
					cellsrenderer: function(row, column, value) {
						if (value) {
							return '<span style=\"text-align: right\">' + value.toLocaleString(locale) +'</span>';
						}
					}, createeditor: function (row, cellvalue, editor) {
						editor.jqxNumberInput({inputMode: 'simple', spinMode: 'simple', groupSeparator: '.', min:0 });
					}
				},
				{ text: '${uiLabelMap.POReturnReason}', datafield: 'returnReasonId', width: 200, editable: true, filterable: false, columntype: 'dropdownlist',
					cellsrenderer: function(row, column, value){
						if (value) {
							for(var i = 0; i < listReturnReasons.length; i++){
								if(value == listReturnReasons[i].returnReasonId){
									return '<span style=\"text-align: left\">' + listReturnReasons[i].description +'</span>';
								}
							}
						}
					}, createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
						editor.jqxDropDownList({ source: listReturnReasons, valueMember: 'returnReasonId', displayMember: 'description' });
					}
				}"/>
	
<@jqGrid id="jqxgridProduct" url="jqxGeneralServicer?sname=JQListProductByOrder" showtoolbar="false"
	filtersimplemode="true" filterable="true" 
	dataField=dataField columnlist=columnlist usecurrencyfunction="true"
	selectionmode="checkbox" editable="true" editmode="click"/>

</div>
</div>
<script type="text/javascript" src="/poresources/js/returnSupplier/returnSupplierListProduct.js"></script> 