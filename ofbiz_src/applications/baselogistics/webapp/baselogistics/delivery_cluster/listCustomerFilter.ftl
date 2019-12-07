<#include "script/listCustomerFilterScript.ftl"/>
<script type="text/javascript">

</script>
<div>
<#assign dataField="[
				{name: 'partyId', type: 'string'},
				{name: 'partyCode', type: 'string'},
				{name: 'statusId', type: 'string'},
				{name: 'partyName', type: 'string'},
				{name: 'distributorName', type: 'string'},
				{name: 'distributorId', type: 'string'},
				{name: 'distributorCode', type: 'string'},
				{name: 'fullName', type: 'string'},
				{name: 'salesmanName', type: 'string'},
				{name: 'salesmanId', type: 'string'},
				{name: 'postalAddressName', type: 'string'},
				{name: 'telecomName', type: 'string'},
				{name: 'emailAddress', type: 'string'},
				{name: 'officeSiteName', type: 'string'},
				{name: 'latitude', type: 'number'},
				{name: 'longitude', type: 'number'},
				{name: 'geoPointId', type: 'string'},
				{name: 'preferredCurrencyUomId', type: 'string'}]"/>
<#assign columnlist = "[
				{text: '${uiLabelMap.DmsSequenceId}', datafield: '', sortable: false, filterable: false, editable: false, pinned: true,
					groupable: false, draggable: false, resizable: false, width: '5%',
				    cellsrenderer: function (row, column, value) {
				        return '<div>' + (row + 1) + '</div>';
				    }
				},
				{text: '${StringUtil.wrapString(uiLabelMap.BSAgentId)}', datafield: 'partyCode', width: '10%'
				},
				{text: '${StringUtil.wrapString(uiLabelMap.BSAgentName)}', datafield: 'partyName',width: '15%'},
				{text: '${StringUtil.wrapString(uiLabelMap.DmsTelecom)}', datafield: 'telecomName', width: '10%', cellsalign: 'right', sortable: false},
				{text: '${StringUtil.wrapString(uiLabelMap.DmsAddress)}', datafield: 'postalAddressName', sortable: false},
			]"/>
<@jqGrid filtersimplemode="true" id="jqxgridfilterGrid" filterable="true" dataField=dataField columnlist=columnlist editable="false" showtoolbar="false" clearfilteringbutton="false"
url="jqxGeneralServicer?sname=JQGetListCustomerCluster&type=SMCHANNEL_MT" initrowdetails = "false" selectionmode="checkbox" rowdetailsheight="200" viewSize="10"
/>
</div>