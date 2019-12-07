<#assign dataField="[{ name: 'partyId', type: 'string'},
					 { name: 'partyCode', type: 'string'},
					 { name: 'fullName', type: 'string'},
					 { name: 'distributorId', type: 'string'},
					 { name: 'supervisorId', type: 'string'},
					 { name: 'visitFrequencyTypeId', type: 'string'},
					 { name: 'officeSiteName', type: 'string'}]"/>
<#assign columnlist = "{text: '${uiLabelMap.DmsSequenceId}', datafield: '', sortable: false, filterable: false, editable: false, pinned: true, groupable: false, draggable: false, resizable: false, width: 40,
						    cellsrenderer: function (row, column, value) {
						        return '<div style=margin:4px;>' + (row + 1) + '</div>';
						    }
						},
					 	{ text: '${StringUtil.wrapString(uiLabelMap.BSProductStoreId)}', datafield: 'partyCode', width: 150},
						{ text: '${StringUtil.wrapString(uiLabelMap.BSRetailOutletName)}', datafield: 'fullName', width: 200},
						{ text: '${StringUtil.wrapString(uiLabelMap.BSDistributorId)}', datafield: 'distributorId', width: 200},
						{ text: '${StringUtil.wrapString(uiLabelMap.BSSupervisorId)}', datafield: 'supervisorId', minwidth: 200}"/>
<#assign showtoolbar = "false"/>
<#if !urlStores?exists>
	<#assign urlStores = "jqxGeneralServicer?sname=JQGetListStores&partyId=${parameters.partyId?if_exists}"/>
	<#assign showtoolbar = "true"/>
	<#assign customLoadFunction = "false"/>
</#if>
<@jqGrid url=urlStores dataField=dataField columnlist=columnlist filterable="true" clearfilteringbutton="true"
	showtoolbar=showtoolbar alternativeAddPopup="alterpopupWindow" filtersimplemode="true" addType="popup" viewSize="10"
	height="300" id="jqxgridStores" addrow="false" customLoadFunction=customLoadFunction/>