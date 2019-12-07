<#include "script/listOrderByClusterScript.ftl"/>
<div>
	<div class="span11">
		<h4 class="smaller blue row">
			${uiLabelMap.ListOrderByCluster}
		</h4>
	</div>
	<div id="jqxGridListOrderByCluster" style="width: 100%"></div>
<#assign dataField="[
	{ name: 'shipperId', type: 'string'},
	{ name: 'shipperName', type: 'string'},
	{ name: 'deliveryClusterId', type: 'string'},
	{ name: 'amountOfOrder', type: 'string'},
	{ name: 'amountOfCompletedOrder', type: 'string'},
	{ name: 'amountOfOrderInProcessed', type: 'string'},
	{ name: 'amountOfRemainOrder', type: 'string'},
	]"/>
<#assign columnlist="
						
					{ text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, editable: false, pinned: true,
						groupable: false, draggable: false, resizable: false,
						datafield: '', columntype: 'number', width: 50,
						cellsrenderer: function (row, column, value) {
							return '<span style=margin:4px;>' + (value + 1) + '</span>';
						}
					},
					{ text: '${uiLabelMap.BLShipperCode}', datafield: 'shipperId',width:250, editable: false,  pinned: true,
						cellsrenderer: function(row, colum, value){
			
						}
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.LogShipper)}', dataField: 'shipperName', width: 200, editable:false, filterable: 'false',
					cellsrenderer: function(row, column, value){
					}
				},
				{ text: '${StringUtil.wrapString(uiLabelMap.BLDeliveryCluster)}', dataField: 'deliveryClusterId', minwidth: 170, editable:false, filterable: 'false',
				cellsrenderer: function(row, column, value){
					return '<span><a href=\"deliveryClusterDetail?deliveryClusterId=' + value +'\">' + value + '</a></span>';
				}
			},
				{ text: '${uiLabelMap.AmountOfCompletedOrder}', dataField: 'amountOfCompletedOrder',minwidth: 150, editable:false,
					cellsrenderer: function(row, column, value){
					}
				},
				{ text: '${uiLabelMap.AmountOfRemainOrder}', dataField: 'amountOfRemainOrder',minwidth:140, editable:false,
					cellsrenderer: function(row, column, value){
					}
				},
				{ text: '${uiLabelMap.AmountOfOrder}', dataField: 'amountOfOrder',minwidth:120, editable:false,
					cellsrenderer: function(row, column, value){
					}
				},
				"/>
<@jqGrid 
	filtersimplemode="true"
	id="jqxGridListOrderByCluster" 
	filterable="true" 
	dataField=dataField 
	columnlist=columnlist 
	editable="false" 
	showtoolbar="false"
	width= '100%'
	clearfilteringbutton="true"
	url="jqxGeneralServicer?sname=JQGetListOrderByCluster" 
	initrowdetails = "false" 
	
/>
</div>
