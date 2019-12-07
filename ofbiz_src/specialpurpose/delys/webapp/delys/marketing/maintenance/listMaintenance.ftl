<div id="allCooler">
	<#assign dataField="[{ name: 'maintHistSeqId', type: 'string' },
						 { name: 'fixedAssetId', type: 'string'},
						 { name: 'fixedAssetName', type: 'string'},
						 { name: 'serialNumber', type: 'string'},
						 { name: 'statusId', type: 'string'},
						 { name: 'estimatedCost', type: 'string'},
						 { name: 'actualCost', type: 'string'},
						 { name: 'startDate', type: 'date', cellsformat: 'dd.MM.yyyy' },
						 { name: 'endDate', type: 'date', cellsformat: 'dd.MM.yyyy' }]"/>
	<#assign columnlist="{ text: '${uiLabelMap.maintHistSeqId}', datafield: 'maintHistSeqId', width: '160px', filterable: true, editable: false},
						 { text: '${uiLabelMap.fixedAssetId}', datafield: 'fixedAssetId', width: '120px', filterable:true, editable: false},
						 { text: '${uiLabelMap.fixedAssetName}', datafield: 'fixedAssetName', width: '120px', filterable:true, editable: false},
						 { text: '${uiLabelMap.serialNumber}', datafield: 'serialNumber', width: '120px', filterable:true, editable: false},
						 { text: '${uiLabelMap.statusId}', datafield: 'statusId', width: '150px', filterable:true, editable: false},
						 { text: '${uiLabelMap.estimatedCost}', datafield: 'estimatedCost', width: '200px', editable: false},
						 { text: '${uiLabelMap.actualCost}', datafield: 'actualCost', width: '200px', editable: false},
						 { text: '${uiLabelMap.startDate}', datafield: 'startDate', filterable:true, editable: false},
						 { text: '${uiLabelMap.endDate}', datafield: 'endDate', filterable:true, editable: false}"/>
	<@jqGrid id="fam" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="false" showtoolbar="false" addrow="false" deleterow="false" url="jqxGeneralServicer?sname=JQGetAssetMaintenance"/>
</div>