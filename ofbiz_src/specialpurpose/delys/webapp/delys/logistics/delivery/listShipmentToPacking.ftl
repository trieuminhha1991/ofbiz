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
	<#assign shipmentTypes = delegator.findList("ShipmentType", null, null, null, null, false) />
	var shipmentTypeData = new Array();
	<#list shipmentTypes as item>
		var row = {};
		row['shipmentTypeId'] = '${item.shipmentTypeId}';
		row['description'] = '${item.description}';
		shipmentTypeData[${item_index}] = row;
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
	
	<#assign facilitys = delegator.findList("Facility", null, null, null, null, false) />
	var facilityData = new Array();
	<#list facilitys as item>
		var row = {};
		row['facilityId'] = '${item.facilityId}';
		row['description'] = '${item.facilityName?if_exists}';
		facilityData[${item_index}] = row;
	</#list>
	<#assign postalAddress = delegator.findList("PostalAddress", null, null, null, null, false) />
	var postalAddressData = new Array();
	<#list postalAddress as item>
		var row = {};
		row['contactMechId'] = '${item.contactMechId}';
		row['description'] = '${item.address1?if_exists}';
		postalAddressData[${item_index}] = row;
	</#list>
	//Create Window
	$("#alterpopupWindow").jqxWindow({
		maxWidth: 1500, minWidth: 950, minHeight: 500, maxHeight: 1200, resizable: true,  isModal: true, autoOpen: false, cancelButton: $("#alterCancel"), modalOpacity: 0.7, theme:theme           
	});
	$('#alterpopupWindow').on('open', function (event) {
		initGridjqxgridShipmentByDE();
	});
</script>
<h4 class="row header smaller lighter blue">
	${uiLabelMap.ProductListProduct}
</h4>
<#assign shipmentRowDetail = "function (index, parentElement, gridElement, datarecord) {
 	var shipmentDataAdapter = new $.jqx.dataAdapter(datarecord.rowDetail, { autoBind: true });
    shipmentItems = shipmentDataAdapter.records;
	
	 var nestedGrids = new Array();
     var id = datarecord.uid.toString();
    
     var grid = $($(parentElement).children()[0]);
     $(grid).attr('id', 'jqxgridDetail_' + datarecord.shipmentId);
     nestedGrids[index] = grid;
   
     var shipmentItembyid = [];
    
     for (var m = 0; m < shipmentItems.length; m++) {
    	 shipmentItembyid.push(shipmentItems[m]);
     }
     var shipmentsource = { datafields: [
             { name: 'deliveryEntryId', type:'string' },
			 { name: 'shipmentId', type:'string' },
         	 { name: 'shipmentItemSeqId', type:'string' },
         	 { name: 'productId', type:'string' },
         	 { name: 'productName', type:'string' },
         	 { name: 'weight', type:'string' },
         	 { name: 'weightUomId', type:'string' },
             { name: 'quantity', type: 'number' },
             { name: 'quantityPackaged', type: 'number' },
             { name: 'quantityUomId', type:'string' },
             { name: 'totalWeight', type:'string' },
             { name: 'quantityToPacking', type: 'number' },
         ],
         localdata: shipmentItembyid,
     }
     var nestedGridAdapter = new $.jqx.dataAdapter(shipmentsource);
    
     if (grid != null) {
    	 var a ;
         grid.jqxGrid({
             source: nestedGridAdapter, width: '96%', autoheight:'true',
             showtoolbar:false,
             showstatusbar: false,
	 		 editable: true,
	 		 selectionmode:'checkbox',
	 		 editmode:'click',
	 		 showheader: true,
	 		 columnsresize: true,
	 		 rowsheight: 28,
	 		 pagesize: 5,
	 		 pageable: true,
	 		 pagesizeoptions: ['5', '10', '15'],
	 		 theme: 'olbius',
             columns: [
                   { text: '${uiLabelMap.Product}', datafield: 'productId', width: 150 , editable: false, cellsalign: 'center'},
                   { text: '${uiLabelMap.Quantity}', dataField: 'quantity', width: 150, editable: false, cellsalign: 'center', 
						cellsrenderer: function(row, column, value){
							var data = grid.jqxGrid('getrowdata', row);
							for(var i = 0; i < quantityUomData.length; i++){
								if(quantityUomData[i].uomId == data.quantityUomId){
									return '<div style=\"margin-left:5px;margin-top:5px\"><span>' + value +' ('+ quantityUomData[i].description + ')</span></div>'
								}
							}
						}	
                   },
                   { text: '${uiLabelMap.weight}', dataField: 'weight', width: 150, editable: false, cellsalign: 'center',
                	   cellsrenderer: function(row, column, value){
							var data = grid.jqxGrid('getrowdata', row);
							for(var i = 0; i < weightUomData.length; i++){
								if(weightUomData[i].uomId == data.weightUomId){
									return '<div style=\"margin-left:5px;margin-top:5px\"><span>' + value +' ('+ weightUomData[i].description + ')</span>'
								}
							}
						}	
                   },
                   { text: '${uiLabelMap.QuantityPackaged}', dataField: 'quantityPackaged', width: 150, editable: false, cellsalign: 'center', 
						cellsrenderer: function(row, column, value){
							var data = grid.jqxGrid('getrowdata', row);
							for(var i = 0; i < quantityUomData.length; i++){
								if(quantityUomData[i].uomId == data.quantityUomId){
									return '<div style=\"margin-left:5px;margin-top:5px\"><span>' + value +' ('+ quantityUomData[i].description + ')</span></div>'
								}
							}
						}	
                  },
                   { text: '${uiLabelMap.quantityPacking}', dataField: 'quantityToPacking', minwidth: 100, align: 'center', cellsalign: 'right', columntype: 'numberinput', filterable: false, editable: true,
                       validation: function (cell, value) {
                    	   var data = grid.jqxGrid('getrowdata', cell.row);
                           if (value <= 0) {
                               return { result: false, message: '${uiLabelMap.QuantityMustBeGreateThanZero}'};
                           }
                           if (value > (data.quantity - data.quantityPackaged)){
                        	   return { result: false, message: '${uiLabelMap.QuantityCantNotGreateThanQuantityPlanned}'};
                           }
                           return true;
                       },
                       createeditor: function (row, cellvalue, editor) {
                           editor.jqxNumberInput({ decimalDigits: 0, digits: 10 });
                       }
  				 	},
               ]
         });
     }
}"/>
<#assign dataField2="[{ name: 'shipmentId', type: 'string' },
                 	{ name: 'shipmentTypeId', type: 'string' },
                 	{ name: 'originFacilityId', type: 'string' },
                 	{ name: 'destinationFacilityId', type: 'string' },
                 	{ name: 'originContactMechId', type: 'string' },
                 	{ name: 'destinationContactMechId', type: 'number' },
                 	{ name: 'defaultWeightUomId', type: 'number' },
                 	{ name: 'currencyUomId', type: 'string' },
                 	{ name: 'rowDetail', type: 'string' },
		 		 	]"/>
<#assign columnlist2="{ text: '${uiLabelMap.ShipmentId}', dataField: 'shipmentId', width: 150, editable: false},
					{ text: '${uiLabelMap.ShipmentType}', datafield: 'shipmentTypeId', width: 150, filtertype:'input', editable: false, cellsrenderer:
					    function(row, colum, value){
						        for(var i = 0; i < shipmentTypeData.length; i++){
									if(shipmentTypeData[i].shipmentTypeId == value){
										return '<span title=' + value + '>' + shipmentTypeData[i].description + '</span>'
									}
								}
					 	}
					},
					{ text: '${uiLabelMap.OriginAddress}',  datafield: 'originFacilityId', minwidth: 160, editable: false, filtertype:'input', cellsrenderer:
					       function(row, colum, value){
								var data = $('#jqxgridShipmentByDE').jqxGrid('getrowdata', row);
						        var originFacilityId = data.originFacilityId;
						        var originFacility = '';
						        var originContactMech = '';
						        if (originFacilityId != null) {
						        	for (var i = 0; i < facilityData.length; i++){
						        		if(facilityData[i].facilityId == value){
						        			originFacility = facilityData[i].description;
										}
						        	}
						        	var originContactMechId = data.originContactMechId;
						        	if (originContactMechId != null){
						        		for (var i = 0; i < postalAddressData.length; i++){
							        		if(postalAddressData[i].contactMechId == originContactMechId){
							        			originContactMech = postalAddressData[i].description;
											}
							        	}
						        	}
							        return '<span>' + originFacility + ' (' + originContactMech + ')</span>';
								} else {
									return '';
								}
				        	}
			        },
				   { text: '${uiLabelMap.DestinationAddress}',  datafield: 'destinationFacilityId', minwidth: 160, editable: false, filtertype:'input', cellsrenderer:
					   function(row, colum, value){
					        var data = $('#jqxgridShipmentByDE').jqxGrid('getrowdata', row);
					        var destinationFacilityId = data.destinationFacilityId;
					        var destFacility = '';
					        var destContactMech = '';
					        if (destinationFacilityId != null) {
					        	for (var i = 0; i < facilityData.length; i++){
					        		if(facilityData[i].facilityId == value){
					        			destFacility = facilityData[i].description;
									}
					        	}
					        	var destContactMechId = data.destinationContactMechId;
					        	if (destContactMechId != null){
					        		for (var i = 0; i < postalAddressData.length; i++){
						        		if(postalAddressData[i].contactMechId == destContactMechId){
						        			destContactMech = postalAddressData[i].description;
										}
						        	}
					        	}
						        return '<span>' + destFacility + ' (' + destContactMech + ')</span>';
							} else {
								return '';
							}
		        		}
			        },
				 	"/>
<@jqGrid selectionmode="checkbox" filtersimplemode="true" width="850" id="jqxgridShipmentByDE" usecurrencyfunction="true" addType="popup" dataField=dataField2 columnlist=columnlist2 clearfilteringbutton="true" showtoolbar="false" addrow="true" filterable="true" editmode="dblclick" editable="true" 
		url="jqxGeneralServicer?sname=getShipmentByDeliveryEntry&deliveryEntryId=${parameters.deliveryEntryId?if_exists}" bindresize="false" editrefresh="true" functionAfterUpdate=""  customLoadFunction="true"
		updateUrl="" editColumns="shipmentId;shipmentItemSeqId;quantity(java.math.BigDecimal);" otherParams="" initrowdetailsDetail=shipmentRowDetail initrowdetails = "true"
	/>
