<#include "script/listShipmentScript.ftl"/>
<script type="text/javascript">
	<#assign localeStr = "VI" />
	<#if locale = "en">
	<#assign localeStr = "EN" />
	</#if>
	function reponsiveRowDetails(grid, parentElement) {
	    $(window).bind('resize', function() {
	    	$(grid).jqxGrid({ width: "96%"});
	    });
	    $('#sidebar').bind('resize', function() {
	    	$(grid).jqxGrid({ width: "96%"});
	    });
	}
</script>
<div>
<#assign initrowdetails = "function (index, parentElement, gridElement, datarecord) {
	var grid = $($(parentElement).children()[0]);
	$(grid).attr('id','jqxgridDetail'+index);
	var gridDetailId = 'jqxgridDetail'+index;
	reponsiveRowDetails(grid);
	if(datarecord.rowDetail){
		var sourceGridDetail =
	    {
	        localdata: datarecord.rowDetail,
	        datatype: 'local',
	        datafields:
	        [
	            { name: 'productId', type: 'string' },
	            { name: 'shipmentId', type: 'string' },
	            { name: 'deliveryId', type: 'string' },
	            { name: 'shipmentItemSeqId', type: 'string' },
	            { name: 'productCode', type: 'string'},
	            { name: 'productName', type: 'string' },
				{ name: 'quantity', type: 'number' },
				{ name: 'quantityCreate', type: 'number' },
				{ name: 'quantityUomId', type: 'string' },
	        ]
	    };
	    var dataAdapterGridDetail = new $.jqx.dataAdapter(sourceGridDetail);
	    grid.jqxGrid({
	        width: '98%',
	        height: '95%',
	        theme: 'olbius',
	        localization: getLocalization(),
	        source: dataAdapterGridDetail,
	        sortable: true,
	        pagesize: 5,
	 		pageable: true,
	 		editable: true,
	 		editmode: 'click',
	        selectionmode: 'multiplecellsadvanced',
	        columns: [
					{ text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, editable: false, pinned: true,
					    groupable: false, draggable: false, resizable: false,
					    datafield: '', columntype: 'number', width: 50,
					    cellsrenderer: function (row, column, value) {
					        return '<div style=margin:4px;>' + (value + 1) + '</div>';
					    }
					},
					{ text: '${uiLabelMap.ProductCode}', datafield: 'productCode', align: 'left', width: 150, pinned: true, editable: false,},
					{ text: '${uiLabelMap.ProductName}', datafield: 'productName', align: 'left', minwidth: 250, editable: false,},
					{ text: '${uiLabelMap.Quantity}', datafield: 'quantity', align: 'left', width: 200, cellsalign: 'right', editable: false,
						cellsrenderer: function(row, colum, value){
							if(value != null && value != undefined){
								return '<span style=\"text-align: right;\">' + value.toLocaleString('${localeStr}') + '</span>';
							}
							return '<span style=\"text-align: right;\">' + 0 + '</span>';
					    },
					},
					{ text: '${uiLabelMap.QuantityCreate}', datafield: 'quantityCreate', align: 'left', width: 200, cellsalign: 'right', columntype: 'numberinput', editable: true,
						cellsrenderer: function(row, colum, value){
							if(value != null && value != undefined && value != ''){
								return '<span style=\"text-align: right; background-color: #deedf5\">' + value.toLocaleString('${localeStr}') + '</span>';
							} 
							return '<span style=\"text-align: right;\">' + 0 + '</span>';
					    },
					    initeditor: function(row, value, editor){
                            var data = $('#'+gridDetailId).jqxGrid('getrowdata', row);
	                        editor.jqxNumberInput({disabled: false});
	                        if (null === value || value === undefined){
                            	editor.jqxNumberInput('val', data.quantity);
	                        } 
					    },
					    validation: function (cell, value) {
					    	var data = $('#'+gridDetailId).jqxGrid('getrowdata', cell.row);
					        if (value < 0) {
					            return { result: false, message: '${uiLabelMap.NumberGTZ}'};
					        }
					        if (value > data.quantity){
					        	return { result: false, message: '${uiLabelMap.QuantityCantNotGreateThanQuantityPlanned}'};
					        }
					        return true;
					    },
					},
                 ]
		    });
		}else {
		}
	}"/>
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
	{ name: 'rowDetail', type: 'string'},
	{ name: 'primaryOrderId', type: 'string'},
	{ name: 'deliveryId', type: 'string'},
	]"/>
<#assign columnlistAlter="
						{ text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, editable: false, pinned: true, 
						    groupable: false, draggable: false, resizable: false,
						    datafield: '', columntype: 'number', width: 50,
						    cellsrenderer: function (row, column, value) {
						        return '<span style=margin:4px;>' + (value + 1) + '</span>';
						    }
						},					
						{ text: '${uiLabelMap.DeliveryCode}', datafield: 'deliveryId', width: 120, editable: false,  pinned: true, 
							cellsrenderer: function(row, colum, value){
					   		}
						},
						{ text: '${uiLabelMap.OrderId}', datafield: 'primaryOrderId', width: 120, editable: false, 
							cellsrenderer: function(row, colum, value){
								if (value === null || value === undefined || value === ''){
									 return '<span title=\"_NA_\">_NA_</span>';
								}
					   		}
						},
						    { text: '${uiLabelMap.ShipmentType}', hidden: true, datafield: 'shipmentTypeId', minwidth: 150, editable: false,
						        cellsrenderer:function(row, colum, value){
						        	for(var i = 0; i < shipmentTypeData.length; i++){
										if(shipmentTypeData[i].shipmentTypeId == value){
											return '<span title=' + value + '>' + shipmentTypeData[i].description + '</span>'
										}
									}
					        	}
					        },
						    { text: '${uiLabelMap.Status}', datafield: 'statusId', minwidth: 120, editable: false, columntype: 'dropdownlist', filtertype: 'checkedlist',
						       cellsrenderer: function(row, colum, value){
						    	   for(var i = 0; i < statusData.length; i++){
										if(statusData[i].statusId == value){
											return '<span title=' + value + '>' + statusData[i].description + '</span>'
										}
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
					        },
						   { text: '${uiLabelMap.DestAddress}', sortable:false, datafield: 'destAddress', width: 180, editable: false, 
						   },
						   { text: '${uiLabelMap.TotalWeight}', sortable:false, filterable: false, datafield: 'totalWeight', width: 180, editable: false, cellsrenderer:
						       	function(row, colum, value){
								   	var data = $('#jqxgridfilterGrid').jqxGrid('getrowdata', row);
								   	if (data.totalWeight === null || data.totalWeight === undefined){
								   		var totalWeight = 0;
								   		return '<span style=\"text-align: right\">' + totalWeight.toLocaleString('${localeStr}') +' (' + weightUomAbb +  ')</span>';
								   	} else {
								   		var totalWeight = data.totalWeight;
						 			    var weightUomId = data.defaultWeightUomId;
						 			    var weightUomAbb = '';
								   		for(var i = 0; i < weightUomData.length; i++){
											 if(weightUomId == weightUomData[i].uomId){
												 weightUomAbb = weightUomData[i].description;
											 }
								   		}
						 			    return '<span style=\"text-align: right\">' + totalWeight.toLocaleString('${localeStr}') +' (' + weightUomAbb +  ')</span>';
								   	}
						   		}
						   },
//						   { text: '${uiLabelMap.EstimatedReadyDate}',  datafield: 'estimatedReadyDate', width: 180, editable: false , cellsalign: 'right', cellsformat: 'dd/MM/yyyy', filtertype: 'range',
//							   cellsrenderer: function(row, column, value){
//									 if (!value){
//										 return '<span style=\"text-align: right\" title=\"_NA_\">_NA_</span>';
//									 } else {
//										 return '<span style=\"text-align: right\">'+ShipmentObj.formatFullDate(value)+'</span>';
//									 }
//								 }, 
//						   },
						   { text: '${uiLabelMap.EstimatedShipDate}',  datafield: 'estimatedShipDate', width: 180, editable: false, cellsalign: 'right', cellsformat: 'dd/MM/yyyy', filtertype: 'range', 
							   cellsrenderer: function(row, column, value){
									 if (!value){
										 return '<span style=\"text-align: right\" title=\"_NA_\">_NA_</span>';
									 } else {
										 return '<span style=\"text-align: right\">'+ShipmentObj.formatFullDate(value)+'</span>';
									 }
							   }, 
						   },
						   { text: '${uiLabelMap.EstimatedArrivalDate}',  datafield: 'estimatedArrivalDate', width: 180, editable: false, cellsalign: 'right', cellsformat: 'dd/MM/yyyy', filtertype: 'range',
							   cellsrenderer: function(row, column, value){
									 if (!value){
										 return '<span style=\"text-align: right\" title=\"_NA_\">_NA_</span>';
									 } else {
										 return '<span style=\"text-align: right\">'+ShipmentObj.formatFullDate(value)+'</span>';
									 }
						   		}, 
						   },
						   { text: '${uiLabelMap.EstimatedShipCost}', hidden:true, datafield: 'estimatedShipCost', cellsalign: 'right', minwidth: 180, editable: false},
						   "/>
<@jqGrid filtersimplemode="true" id="jqxgridfilterGrid" filterable="true" dataField=dataFieldShipment columnlist=columnlistAlter editable="true" showtoolbar="false" clearfilteringbutton="false" initrowdetailsDetail=initrowdetails
	url="" editmode='click' initrowdetails = "false" selectionmode="checkbox" rowdetailsheight="200"
/>
</div>