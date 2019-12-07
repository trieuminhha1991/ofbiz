<#assign dataField="[{ name: 'partyId', type: 'string'},
						   { name: 'groupName', type: 'string'},
						   { name: 'personRepresent', type: 'string'},
						   { name: 'contactNumber', type: 'string'},
						   { name: 'emailAddress', type: 'string'},
						   { name: 'address1', type: 'string'}
						   ]"/>
<#assign columnlist="{text: '${StringUtil.wrapString(uiLabelMap.DmsSequenceId)}', datafield: '', sortable: false, filterable: false, editable: false, pinned: true, groupable: false, draggable: false, resizable: false, width: 50,
	    cellsrenderer: function (row, column, value) {
	        return '<div style=margin:4px;>' + (row + 1) + '</div>';
	    }
	},
	{ text: '${StringUtil.wrapString(uiLabelMap.DmsCorporationName)}' , datafield: 'groupName', align: 'left', cellsalign: 'left', minWidth: 250},
	{ text: '${StringUtil.wrapString(uiLabelMap.DmsTelecom)}', datafield: 'contactNumber', align: 'left', width: 200},
	{ text: '${StringUtil.wrapString(uiLabelMap.DmsEmail)}', datafield: 'emailAddress', align: 'left', width: 200},
	{ text: '${StringUtil.wrapString(uiLabelMap.DmsAddress)}', datafield: 'address1', align: 'left', width: 250},
	{ text: '${StringUtil.wrapString(uiLabelMap.DmsRepresent)}' , datafield: 'personRepresent', width: 200, align: 'left', cellsalign: 'left'},
"/>
<#if !customLoadFunction?exists>
	<#assign customLoadFunction="false"/>
</#if>
<#if !selectionmode?exists>
	<#assign selectionmode="multiplerows"/>
</#if>
<@jqGrid filtersimplemode="false" addType="popup" customLoadFunction=customLoadFunction dataField=dataField columnlist=columnlist clearfilteringbutton="true"
	showtoolbar="true" addrow="false" deleterow="false" alternativeAddPopup="alterpopupWindow" editable="false" sourceId="partyId"
	customcontrol1="icon-plus-sign open-sans@${uiLabelMap.DmsAddGroup}@Callcenter" selectionmode=selectionmode
	url="jqxGeneralServicer?sname=JQGetListContactBusinessesAndSchool" autoshowloadelement="false" showdefaultloadelement="false"
	customTitleProperties="${uiLabelMap.DmsListContactBusinesses}" id="ListContactBusiness"
/>
