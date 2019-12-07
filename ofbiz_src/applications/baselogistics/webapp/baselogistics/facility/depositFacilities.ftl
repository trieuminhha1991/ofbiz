<#include "component://basecrm/webapp/basecrm/callcenter/ftLlibraryVariable.ftl"/>
<#assign dataField="[{ name: 'facilityId', type: 'string'},
					{ name: 'facilityCode', type: 'string'}
					{ name: 'facilityTypeId', type: 'string'},
					{ name: 'facilityName', type: 'string'},
					{ name: 'ownerPartyId', type: 'string'},
					{ name: 'ownerPartyName', type: 'string'},
					{ name: 'payToPartyId', type: 'string'},
					{ name: 'payToPartyName', type: 'string'},
					{ name: 'description', type: 'string'}]"/>
					
<#assign columnlist="{ text: '${StringUtil.wrapString(uiLabelMap.FacilityId)}', datafield: 'facilityCode', width: 200,
						cellsrenderer: function(row, colum, value){
							if (!value) {
								var data = $('#jqxgrid').jqxGrid('getrowdata', row);
								value = data.facilityId;
								return '<span title=' + value + '>' + value + '</span>';
							}
						}
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.ProductFacilityTypeId)}', datafield: 'facilityTypeId', filtertype: 'checkedlist', width: 150,
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
					{ text: '${StringUtil.wrapString(uiLabelMap.OwnerManager)}', datafield: 'ownerPartyName', width: 250},
					{ text: '${StringUtil.wrapString(uiLabelMap.FacilityName)}', datafield: 'facilityName', width: 300},
					{ text: '${StringUtil.wrapString(uiLabelMap.BSDescription)}', datafield: 'description', minwidth: 200}"/>
<@jqGrid filtersimplemode="false" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true" viewSize="15"
		showtoolbar="true" addrow="false" deleterow="false" alternativeAddPopup="alterpopupWindow" editable="false"
		url="jqxGeneralServicer?sname=JQGetListDepositFacilities"
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