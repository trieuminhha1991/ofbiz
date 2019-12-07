<#assign localeStr = "VI" />
	<#if locale = "en">
	<#assign localeStr = "EN" />
	</#if>
<#assign dataFieldShipment="[{ name: 'shipmentId', type: 'string'},
	{ name: 'shipmentTypeId', type: 'string'},
	{ name: 'statusId', type: 'string'},
	{ name: 'primaryOrderId', type: 'string'},
	{ name: 'primaryTransferId', type: 'string'},
	{ name: 'estimatedReadyDate', type: 'date', other: 'Timestamp'},
	{ name: 'estimatedShipDate', type: 'date', other: 'Timestamp'},
	{ name: 'estimatedArrivalDate', type: 'date', other: 'Timestamp'},
	{ name: 'estimatedShipCost', type: 'number'},
	{ name: 'currencyUomId', type: 'string'},
	{ name: 'originFacilityId', type: 'string'},
	{ name: 'originContactMechId', type: 'string'},
	{ name: 'destinationFacilityId', type: 'string'},
	{ name: 'destinationContactMechId', type: 'string'},
	{ name: 'destFacilityName', type: 'string'},
	{ name: 'destAddress', type: 'string'},
	{ name: 'originAddress', type: 'string'},
	{ name: 'totalWeight', type: 'number'},
	{ name: 'defaultWeightUomId', type: 'string'},
	]"/>
<#assign columnlistAlter="
						{ text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, editable: false, pinned: true, 
						    groupable: false, draggable: false, resizable: false,
						    datafield: '', columntype: 'number', width: 50,
						    cellsrenderer: function (row, column, value) {
						        return '<span style=margin:4px;>' + (value + 1) + '</span>';
						    }
						},					
						{ text: '${uiLabelMap.ShipmentId}', datafield: 'shipmentId', width: 120, editable: false,  pinned: true, 
								cellsrenderer: function(row, colum, value){
						        	var data = $('#jqxgridfilterGrid').jqxGrid('getrowdata', row);
						        	var shipmentId = data.shipmentId;
						        	var shipmentTypeId = data.shipmentTypeId;
						        	var link = 'editShipmentInfo?shipmentId=' + shipmentId +'&shipmentTypeId=' + shipmentTypeId;
						        	return '<span><a href=\"' + link + '\">' + shipmentId + '</a></span>';
						   		}
							},
						    { text: '${uiLabelMap.ShipmentType}', datafield: 'shipmentTypeId', minwidth: 150, editable: false,
						        cellsrenderer:function(row, colum, value){
						        	for(var i = 0; i < shipmentTypeData.length; i++){
										if(shipmentTypeData[i].shipmentTypeId == value){
											return '<span title=' + value + '>' + shipmentTypeData[i].description + '</span>'
										}
									}
					        	}
					        },
						    { text: '${uiLabelMap.Status}', datafield: 'statusId', minwidth: 120, editable: false,
						       cellsrenderer: function(row, colum, value){
						    	   for(var i = 0; i < statusData.length; i++){
										if(statusData[i].statusId == value){
											return '<span title=' + value + '>' + statusData[i].description + '</span>'
										}
									}
					        	}
					        },
						   { text: '${uiLabelMap.DestAddress}', sortable:false, datafield: 'destAddress', width: 180, editable: false, 
						   },
						   { text: '${uiLabelMap.TotalWeight}', sortable:false,  datafield: 'totalWeight', width: 180, editable: false, cellsrenderer:
						       	function(row, colum, value){
								   	var data = $('#jqxgridfilterGrid').jqxGrid('getrowdata', row);
							        var totalWeight = data.totalWeight;
					 			    var weightUomId = data.defaultWeightUomId;
					 			    var weightUomAbb = '';
					 			    for(var i = 0; i < weightUomData.length; i++){
										 if(weightUomId == weightUomData[i].uomId){
											 weightUomAbb = weightUomData[i].description;
										 }
									 }
					 			    if (totalWeight){
					 			    	return '<span style=\"text-align: right\">' + totalWeight.toLocaleString('${localeStr}') +' (' + weightUomAbb +  ')</span>';
					 			    } else {
					 			    	return '<span style=\"text-align: right\">' + 0 +' (' + weightUomAbb +  ')</span>';
					 			    }
						   		}
						   },
						   { text: '${uiLabelMap.EstimatedReadyDate}',  datafield: 'estimatedReadyDate', width: 180, editable: true, cellsalign: 'right', cellsformat: 'dd/MM/yyyy - hh:mm:ss'},
						   { text: '${uiLabelMap.EstimatedShipDate}',  datafield: 'estimatedShipDate', width: 180, editable: true, cellsalign: 'right', cellsformat: 'dd/MM/yyyy - hh:mm:ss'},
						   { text: '${uiLabelMap.EstimatedArrivalDate}',  datafield: 'estimatedArrivalDate', width: 180, editable: true, cellsalign: 'right', cellsformat: 'dd/MM/yyyy - hh:mm:ss'},
						   { text: '${uiLabelMap.EstimatedShipCost}',  datafield: 'estimatedShipCost', cellsalign: 'right', minwidth: 180, editable: true},
						   "/>
<@jqGrid filtersimplemode="false" viewSize="5" selectionmode="checkbox" id="jqxgridfilterGrid" width="100%" height="270" bindresize="false" autoheight="false" rowsheight="26" dataField=dataFieldShipment columnlist=columnlistAlter clearfilteringbutton="false"
		 showtoolbar="true" deleterow="false" editable="false" customLoadFunction="true" pagesizeoptions="['5']" showlist="false"
		 url="" customTitleProperties="ShipmenElement"
/>