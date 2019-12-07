<#include "script/listPackScript.ftl"/>
<script type="text/javascript">
	<#assign localeStr = "VI" />
	<#if locale = "en">
	<#assign localeStr = "EN" />
	</#if>
</script>
<div>
	<div class="span11">
		<h5 class="smaller green row header font-bold blue">
			${uiLabelMap.ListPacks}
		</h5>
	</div>
	
<#assign dataField="[
	{ name: 'packId', type: 'string'},
	{ name: 'statusId', type: 'string'},
	{ name: 'originContactMechId', type: 'string'},
	{ name: 'shipBeforeDate', type: 'date', other: 'Timestamp'},
	{ name: 'shipAfterDate', type: 'date', other: 'Timestamp'},
	{ name: 'partyIdTo', type: 'string'},
	]"/>
<#assign columnlistPack="
						{ text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, editable: false, pinned: true,
						    groupable: false, draggable: false, resizable: false,
						    datafield: '', columntype: 'number', width: 50,
						    cellsrenderer: function (row, column, value) {
						        return '<span style=margin:4px;>' + (value + 1) + '</span>';
						    }
						},
						{ text: '${uiLabelMap.PackCode}', datafield: 'packId',width:150, editable: false,  pinned: true,
							cellsrenderer: function(row, colum, value){
					   		}
						},
						{ text: '${uiLabelMap.Status}', dataField: 'statusId', width: 150, editable:false, filterable: 'false',
						cellsrenderer: function(row, column, value){
							for (var i = 0; i < statusData.length; i ++){
								if (value && value == statusData[i].statusId){
									return '<span>' + statusData[i].description + '<span>';
								}
							}
							return '<span>' + value + '<span>';
						}
					},
					{ text: '${uiLabelMap.BLProductStoreId}', dataField: 'partyIdTo', minwidth: 200, editable:false,
						cellsrenderer: function(row, column, value){
						}
					},
						   { text: '${uiLabelMap.ShipAfterDate}',  datafield: 'shipBeforeDate', editable: false, cellsalign: 'right', cellsformat: 'dd/MM/yyyy', filtertype: 'range',
							   cellsrenderer: function(row, column, value){
									 if (!value){
										 return '<span style=\"text-align: right\" title=\"_NA_\">_NA_</span>';
									 } else {
										 return '<span style=\"text-align: right\">'+ShipmentObj.formatFullDate(value)+'</span>';
									 }
							   },
						   },
						   { text: '${uiLabelMap.ShipBeforeDate}',  datafield: 'shipAfterDate', editable: false, cellsalign: 'right', cellsformat: 'dd/MM/yyyy', filtertype: 'range',
							   cellsrenderer: function(row, column, value){
									 if (!value){
										 return '<span style=\"text-align: right\" title=\"_NA_\">_NA_</span>';
									 } else {
										 return '<span style=\"text-align: right\">'+ShipmentObj.formatFullDate(value)+'</span>';
									 }
						   		},
						   },
						   "/>
<@jqGrid 
	filtersimplemode="true" 
	id="jqxgridfilterGrid" 
	filterable="true" 
	dataField=dataField 
	columnlist=columnlistPack 
	editable="true" 
	showtoolbar="true" 
	clearfilteringbutton="false"
	url="" 
	editmode='click' 
	initrowdetails = "false" 
	selectionmode="checkbox"
/>
</div>
