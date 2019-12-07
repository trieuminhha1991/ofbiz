<script>
	<#assign boxTypes = delegator.findList("ShipmentBoxType", null, null, null, null, false) />
	var boxTypeData = new Array();
	<#list boxTypes as item>
		var row = {};
		row['boxTypeId'] = '${item.shipmentBoxTypeId?if_exists}';
		row['description'] = '${item.description?if_exists}';
		boxTypeData[${item_index}] = row;
	</#list>
	<#assign weightUoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "WEIGHT_MEASURE"), null, null, null, false) />
	var weightUomData = new Array();
	<#list weightUoms as item>
		var row = {};
		row['weightUomId'] = '${item.uomId?if_exists}';
		row['description'] = '${item.abbreviation?if_exists}';
		weightUomData[${item_index}] = row;
	</#list>
	
	<#assign currencyUoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "CURRENCY_MEASURE"), null, null, null, false) />
	var currencyUomData = new Array();
	<#list currencyUoms as item>
		var row = {};
		row['weightUomId'] = '${item.uomId?if_exists}';
		row['description'] = '${item.abbreviation?if_exists}';
		currencyUomData[${item_index}] = row;
	</#list>
	
	<#assign packingUoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "PRODUCT_PACKING"), null, null, null, false) />
	var packingUomData = new Array();
	<#list packingUoms as item>
		var row = {};
		row['quantityUomId'] = '${item.uomId?if_exists}';
		row['description'] = '${item.abbreviation?if_exists}';
		packingUomData[${item_index}] = row;
	</#list>
	
	$("#popupShipmentCosts").jqxWindow({
		maxWidth: 1500, minWidth: 800, minHeight: 500, maxHeight: 1200, resizable: true,  isModal: true, autoOpen: false, cancelButton: $("#alterCancel"), modalOpacity: 0.7, theme:theme           
	});
	$('#popupShipmentCosts').on('open', function (event) {
		initGridjqxgridShipmentCosts();
	});
</script>
<h4 class="row header smaller lighter blue">
	${uiLabelMap.TransportCost}
</h4>

<#assign dataField="[{ name: 'shipmentId', type: 'string'},
					{ name: 'shipmentPackageSeqId', type: 'string' }, 
					]"/>
<#assign columnlist="{ text: '${uiLabelMap.SequenceId}', datafield: 'shipmentPackageSeqId', width: '150px', editable: false, filterable: false},
					
					{ text: '${uiLabelMap.ShipmentBoxType}', datafield: 'shipmentBoxTypeId', width: '150px', editable: false, filterable: false,
						cellsrenderer: function(row, colum, value){
							    for(var i = 0; i < boxTypeData.length; i++){
									 if(value == boxTypeData[i].boxTypeId){
										 return '<span title=' + value + '>' + boxTypeData[i].description + '</span>';
									 }
							    }
							}
             		},{ text: '${uiLabelMap.weight}', datafield: 'weight', editable: false, width: '150px', filterable: false,
						cellsrenderer: function(row, colum, value){
							    var data = $('#jqxgridShipmentCosts').jqxGrid('getrowdata', row);
							    for(var i = 0; i < weightUomData.length; i++){
							    	if(data.weightUomId == weightUomData[i].weightUomId){
							    		return '<span>' + value +' (' + weightUomData[i].description +  ')</span>';
							    	}
							    }
							}
					},
					{ text: '${uiLabelMap.createDate}', datafield: 'dateCreated', editable: false, cellsformat: 'd', filterable: false},
				"/>
<@jqGrid id="jqxgridShipmentCosts" filtersimplemode="true" filterable="false" bindresize="false" width="700" addType="" rowsheight="28" dataField=dataField editmode="selectedrow" editable="true" columnlist=columnlist clearfilteringbutton="false" showtoolbar="true" addrow="true"
		 	url="jqxGeneralServicer?sname=getShipmentCosts" customLoadFunction="true" isShowTitleProperty="false"
		 />