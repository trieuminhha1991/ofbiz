<script type="text/javascript" src="/delys/images/js/import/miscUtil.js"></script>
<script type="text/javascript" src="/delys/images/js/import/progressing.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>

<#assign dataField="[{ name: 'facilityId', type: 'string'},
					{ name: 'facilityTypeId', type: 'string'},
					{ name: 'facilityName', type: 'string'},
					{ name: 'description', type: 'string'},
					]"/>
					
<#assign columnlist="
					{ text: '${StringUtil.wrapString(uiLabelMap.DAFacilityId)}', datafield: 'facilityId', align: 'center', width: 200,
						cellsrenderer: function(row, colum, value){
							return \"<span><a href='editFacilityDis?facilityId=\" + value + \"'>\" + value + \"</a></span>\";
						}
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.DAFacilityTypeId)}', datafield: 'facilityTypeId', align: 'center', width: 200,
						cellsrenderer: function(row, colum, value){
							return '<span title=' + value + '>' + mapFacilityType[value] + '</span>';
						}
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.DAName)}', datafield: 'facilityName', align: 'center', width: 300},
					{ text: '${StringUtil.wrapString(uiLabelMap.DADescription)}', datafield: 'description', align: 'center'}
					"/>
<@jqGrid filtersimplemode="false" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true" viewSize="15"
		showtoolbar="true" addrow="false" deleterow="false" alternativeAddPopup="alterpopupWindow" editable="false"
		url="jqxGeneralServicer?sname=JQGetListFacilityByOwnerParty&ownerPartyId=${userLogin.partyId?if_exists}"
	/>

<#assign listFacilityType = delegator.findList("FacilityType", null, null, null, null, false)>

<script>
var mapFacilityType = {
		<#if listFacilityType?exists>
			<#list listFacilityType as item>
				"${item.facilityTypeId?if_exists}": "${StringUtil.wrapString(item.get("description", locale)?if_exists)}",
			</#list>
		</#if>
};
</script>