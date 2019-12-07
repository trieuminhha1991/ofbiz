<#include "component://basesalesmtl/webapp/basesalesmtl/supervisor/inventoryAgent_rowDetail.ftl"/>

<#assign dataField="[{ name: 'partyId', type: 'string'},
					 { name: 'partyCode', type: 'string'},
					 { name: 'partyName', type: 'string'}
					 ]"/>
<#assign columnlist = "{text: '${uiLabelMap.DmsSequenceId}', datafield: '', sortable: false, filterable: false, editable: false, pinned: true, groupable: false, draggable: false, resizable: false, width: 50,
						    cellsrenderer: function (row, column, value) {
						        return '<div style=margin:4px;>' + (row + 1) + '</div>';
						    }
						},
						{ text: '${StringUtil.wrapString(uiLabelMap.BSCustomerId)}', datafield: 'partyCode', width: 300 },
						{ text: '${StringUtil.wrapString(uiLabelMap.BSCustomer)}', datafield: 'partyName'}"/>

<@jqGrid url="jqxGeneralServicer?sname=JQGetListCheckInventoryAgents" dataField=dataField columnlist=columnlist filterable="true" clearfilteringbutton="true"
		 showtoolbar="true" alternativeAddPopup="alterpopupWindow" filtersimplemode="true" addType="popup" 
		 initrowdetails="true" initrowdetailsDetail=initrowdetailsDetail rowdetailsheight="243"
		 addrow="false"/>