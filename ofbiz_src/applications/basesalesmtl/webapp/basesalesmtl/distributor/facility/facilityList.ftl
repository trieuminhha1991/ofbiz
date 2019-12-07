<#if parameters.partyId?exists>
<#assign ownerPartyId = parameters.partyId />
<#else>
<#assign ownerPartyId = userLogin.partyId />
</#if>


<#include "component://basecrm/webapp/basecrm/callcenter/ftLlibraryVariable.ftl"/>
<#assign dataField="[{ name: 'facilityId', type: 'string'},
					{ name: 'facilityTypeId', type: 'string'},
					{ name: 'facilityName', type: 'string'},
					{ name: 'description', type: 'string'}]"/>
					
<#assign columnlist="{ text: '${StringUtil.wrapString(uiLabelMap.DAFacilityId)}', datafield: 'facilityId', width: 200,
						cellsrenderer: function(row, colum, value){
							return \"<span><a href='FacilityDetail?me=${parameters.me?if_exists}&sub=${parameters.sub?if_exists}&facilityId=\" + value + \"'>\" + value + \"</a></span>\";
						}
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.DAFacilityTypeId)}', datafield: 'facilityTypeId', filtertype: 'checkedlist', width: 200,
						cellsrenderer: function(row, colum, value){
							return '<span title=' + value + '>' + mapFacilityType[value] + '</span>';
						},
						createfilterwidget: function (column, htmlElement, editor) {
	    		        	editor.jqxDropDownList({ autoDropDownHeight: true, source: listFacilityType, displayMember: 'description', valueMember: 'facilityTypeId' ,
	                            renderer: function (index, label, value) {
	                            	if (index == 0) {
	                            		return value;
									}
								    return mapFacilityType[value];
				                }
	    		        	});
						}
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.DAName)}', datafield: 'facilityName', width: 300},
					{ text: '${StringUtil.wrapString(uiLabelMap.DADescription)}', datafield: 'description'}"/>
<@jqGrid filtersimplemode="false" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true" viewSize="15"
		showtoolbar="true" addrow="false" deleterow="false" alternativeAddPopup="alterpopupWindow" editable="false"
		url="jqxGeneralServicer?sname=JQGetListFacilityByOwnerParty&ownerPartyId=${ownerPartyId?if_exists}&type=owner"
	/>

<#assign listFacilityType = delegator.findList("FacilityType", null, null, null, null, false)>

<script>
var mapFacilityType = {<#if listFacilityType?exists><#list listFacilityType as item>
				"${item.facilityTypeId?if_exists}": "${StringUtil.wrapString(item.get("description", locale)?if_exists)}",
			</#list></#if>};
var listFacilityType = [<#if listFacilityType?exists><#list listFacilityType as item>{
	facilityTypeId: "${item.facilityTypeId?if_exists}", description: "${StringUtil.wrapString(item.get("description", locale)?if_exists)}"
},</#list></#if>];
</script>