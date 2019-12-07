<script>
	$.jqx.theme = 'olbius';
	theme = $.jqx.theme;
	
	<#assign uoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "PRODUCT_PACKING"), null, null, null, false)>
	var quantityUomData = new Array();
	<#list uoms as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.abbreviation) />
		row['uomId'] = '${item.uomId}';
		row['description'] = '${description}';
		quantityUomData[${item_index}] = row;
	</#list>
	
	<#assign weightUoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "WEIGHT_MEASURE"), null, null, null, false)>
	var weightUomData = new Array();
	<#list weightUoms as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.abbreviation) />
		row['uomId'] = '${item.uomId}';
		row['description'] = '${description}';
		weightUomData[${item_index}] = row;
	</#list>
	
	//Create Window
	$("#alterpopupWindow").jqxWindow({
		maxWidth: 1500, minWidth: 950, minHeight: 500, maxHeight: 1200, resizable: true,  isModal: true, autoOpen: false, cancelButton: $("#alterCancel"), modalOpacity: 0.7, theme:theme           
	});
	$('#alterpopupWindow').on('open', function (event) {
		initGridjqxgridShipmentItemByDE();
	});
</script>
<h4 class="row header smaller lighter blue">
	${uiLabelMap.ProductListProduct}
</h4>
<#assign dataField2="[{ name: 'shipmentId', type: 'string' },
                 	{ name: 'shipmentItemSeqId', type: 'string' },
                 	{ name: 'productId', type:'string' },
                 	{ name: 'productName', type:'string' },
            	 	{ name: 'weight', type:'string' },
            	 	{ name: 'weightUomId', type:'string' },
                    { name: 'quantity', type: 'number' },
                    { name: 'quantityFree', type: 'number' },
                    { name: 'quantityUomId', type:'string' },
                    { name: 'quantityToVehicle', type: 'number' },
		 		 	]"/>
<#assign columnlist2="{ text: '${uiLabelMap.ShipmentId}', dataField: 'shipmentId', width: 150, editable: false},
					{ text: '${uiLabelMap.Product}', datafield: 'productId', width: 150 , editable: false, cellsalign: 'center'},
					{ text: '${uiLabelMap.Quantity}', dataField: 'quantity', width: 150, editable: false, cellsalign: 'center', 
							cellsrenderer: function(row, column, value){
								var data = $('#jqxgridShipmentItemByDE').jqxGrid('getrowdata', row);
								for(var i = 0; i < quantityUomData.length; i++){
									if(quantityUomData[i].uomId == data.quantityUomId){
										return '<div style=\"margin-left:5px;margin-top:5px\"><span>' + value +' ('+ quantityUomData[i].description + ')</span></div>'
									}
								}
							}	
					},
					{ text: '${uiLabelMap.QuantityFree}', dataField: 'quantityFree', minwidth: 150, editable: false, cellsalign: 'center', 
							cellsrenderer: function(row, column, value){
								var data = $('#jqxgridShipmentItemByDE').jqxGrid('getrowdata', row);
								for(var i = 0; i < quantityUomData.length; i++){
									if(quantityUomData[i].uomId == data.quantityUomId){
										return '<div style=\"margin-left:5px;margin-top:5px\"><span>' + value +' ('+ quantityUomData[i].description + ')</span></div>'
									}
								}
							}	
					},
					{ text: '${uiLabelMap.QuantityToVehicle}', dataField: 'quantityInVehicle', minwidth: 100, align: 'center', cellsalign: 'right', columntype: 'numberinput', filterable: false, editable: true,
					    validation: function (cell, value) {
					 	   var data = $('#jqxgridShipmentItemByDE').jqxGrid('getrowdata', cell.row);
					        if (value <= 0) {
					            return { result: false, message: '${uiLabelMap.QuantityMustBeGreateThanZero}'};
					        }
					        if (value > data.quantityFree){
					     	   return { result: false, message: '${uiLabelMap.QuantityCantNotGreateThanQuantityPlanned}'};
					        }
					        return true;
					    },
					    createeditor: function (row, cellvalue, editor) {
					        editor.jqxNumberInput({ decimalDigits: 0, digits: 10 });
					    }
						},
				 	"/>
<@jqGrid selectionmode="checkbox" filtersimplemode="true" width="1000" id="jqxgridShipmentItemByDE" usecurrencyfunction="true" addType="popup" dataField=dataField2 columnlist=columnlist2 clearfilteringbutton="true" showtoolbar="false" addrow="true" filterable="true" editmode="dblclick" editable="true" 
		url="jqxGeneralServicer?sname=getShipmentItemByDeliveryEntry&deliveryEntryId=${parameters.deliveryEntryId?if_exists}" bindresize="false" editrefresh="true" functionAfterUpdate=""  customLoadFunction="true"
		updateUrl="" editColumns="" otherParams="" 
	/>
