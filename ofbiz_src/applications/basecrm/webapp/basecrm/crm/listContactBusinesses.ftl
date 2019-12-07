<#include "component://basecrm/webapp/basecrm/callcenter/ftLlibraryVariable.ftl"/>

<#assign dataField="[{ name: 'partyId', type: 'string' },
					{ name: 'partyCode', type: 'string' },
					{ name: 'groupName', type: 'string' },
					{ name: 'partyIdTo', type: 'string' },
					{ name: 'partyType', type: 'string' },
					{ name: 'representativeMember', type: 'string' },
					{ name: 'contactNumber', type: 'string' },
					{ name: 'emailAddress', type: 'string' },
					{ name: 'marketingCampaignId', type: 'string' },
					{ name: 'resultEnumTypeId', type: 'string' },
					{ name: 'entryDate', type: 'date', other:'Timestamp' },
					{ name: 'address1', type: 'string' }]"/>

<#assign columnlist="{text: '${StringUtil.wrapString(uiLabelMap.DmsSequenceId)}', datafield: '', filterable: false, editable: false, pinned: true, groupable: false, draggable: false, resizable: false, width: 50,
						cellsrenderer: function (row, column, value) {
							return '<div style=margin:4px;>' + (row + 1) + '</div>';
						}
					},"/>
				<#if !security.hasEntityPermission("CALLCAMPAIGN", "_ADMIN", session)>
<#assign columnlist=columnlist + "
					{ text: '${StringUtil.wrapString(uiLabelMap.CampaignId)}', datafield: 'marketingCampaignId', width: 200 },
					{ text: '${StringUtil.wrapString(uiLabelMap.ResultEnumId)}', datafield: 'resultEnumTypeId', width: 100, filtertype: 'checkedlist',
						cellsrenderer: function (row, column, value) {
							return '<div style=margin:4px;><b>' + value + '</b></div>';
						}, createfilterwidget: function (column, htmlElement, editor) {
							editor.jqxDropDownList({ autoDropDownHeight: false, source: reasons, displayMember: 'enumTypeId', valueMember: 'enumTypeId',
								renderer: function (index, label, value) {
									if (index == 0) {
										return value;
									}
									return resonsMap[value];
								}
							});
						}
					},
					{text: '${uiLabelMap.NextCallSchedule}', datafield: 'entryDate', width: 150, cellsformat: 'dd/MM/yyyy', filtertype: 'range'},
					{ text: '${StringUtil.wrapString(uiLabelMap.DmsPartyId)}', datafield: 'partyCode', width: 150},
					{ text: '${StringUtil.wrapString(uiLabelMap.DmsPartyType)}', datafield: 'partyType', filtertype: 'checkedlist', width: 150,
						createfilterwidget: function (column, htmlElement, editor) {
							editor.jqxDropDownList({ autoDropDownHeight: true, source: ['CONTACT', 'INDIVIDUAL_CUSTOMER', 'CUSTOMER'] });
						}
					},"/>
<#else>
<#assign columnlist=columnlist + "{ text: '${StringUtil.wrapString(uiLabelMap.DmsPartyId)}', datafield: 'partyCode', width: 150},"/>
					</#if>
<#assign columnlist=columnlist + "
					{ text: '${StringUtil.wrapString(uiLabelMap.DmsCorporationName)}' , datafield: 'groupName', minWidth: 250,
						cellsrenderer: function(row, column, value, a, b, data){
							var str = '<div class=\"cell-grid-custom\"><a target=\"_blank\" href=\"Callcenter?partyId=' + data.partyId + '\">' + value + '</a></div>';
							return str;
						}
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.DmsTelecom)}', datafield: 'contactNumber', width: 200},
					{ text: '${StringUtil.wrapString(uiLabelMap.DmsEmail)}', datafield: 'emailAddress', width: 200},
					{ text: '${StringUtil.wrapString(uiLabelMap.DmsAddress)}', datafield: 'address1', minWidth: 250},
					{ text: '${StringUtil.wrapString(uiLabelMap.DmsRepresent)}', datafield: 'representativeMember', width: 200}"/>
<#if !customLoadFunction?exists>
	<#assign customLoadFunction="false"/>
</#if>
<#if !selectionmode?exists>
	<#assign selectionmode="multiplerows"/>
</#if>

<#if security.hasEntityPermission("CALLCAMPAIGN", "_ADMIN", session)>
	<#assign url="jqxGeneralServicer?sname=JQGetListContactBusinesses" />
<#else>
	<#assign url="jqxGeneralServicer?sname=JQGetListContactBusinessesByCampaign&campaignId=any&partyTypeId=BUSINESSES" />
</#if>

<@jqGrid filtersimplemode="false" addType="popup" customLoadFunction=customLoadFunction dataField=dataField columnlist=columnlist clearfilteringbutton="true"
	showtoolbar="true" addrow="false" deleterow="false" alternativeAddPopup="alterpopupWindow" editable="false" sourceId="partyId"
	selectionmode=selectionmode sortable="false"
	url=url customTitleProperties="${uiLabelMap.DmsListContactBusinesses}" id="ListContactBusiness"/>

<div id="jqxNotificationNested">
	<div id="notificationContentNested">
	</div>
</div>

<#assign reasonContacted = delegator.findList("EnumerationType",
		Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("parentTypeId", "CONTACTED"), null, null, null, false) />
<#assign reasonUnContacted = delegator.findList("EnumerationType",
		Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("parentTypeId", "UNCONTACTED"), null, null, null, false) />
<script>
$(document).ready(function() {
	$("#ListContactBusiness").jqxGrid({ enabletooltips: true });
});
var reasonContacted =
	[<#if reasonContacted?exists>
		{enumTypeId: "47b56994cbc2b6d10aa1be30f70165adb305a41a", description: "${StringUtil.wrapString(uiLabelMap.NoResults)}"},
		<#list reasonContacted as item>{
			enumTypeId: "${item.enumTypeId?if_exists}", description: "${StringUtil.wrapString(item.description)}"
		},</#list>
   </#if>];
var mapReasonContacted =
{<#if reasonContacted?exists>
	"47b56994cbc2b6d10aa1be30f70165adb305a41a": "<b>${StringUtil.wrapString(uiLabelMap.NoResults)}</b>",
	<#list reasonContacted as item>
		"${item.enumTypeId?if_exists}": "<b>[${item.enumTypeId?if_exists}]</b>" +  "${StringUtil.wrapString(item.description?if_exists)}",
	</#list>
</#if>};
var reasonUnContacted =
	[<#if reasonUnContacted?exists>
	<#list reasonUnContacted as item>{
		enumTypeId: "${item.enumTypeId?if_exists}", description: "${StringUtil.wrapString(item.description)}"
	},</#list>
	</#if>];
var mapReasonUnContacted =
{<#if reasonUnContacted?exists>
	<#list reasonUnContacted as item>
		"${item.enumTypeId?if_exists}": "<b>[${item.enumTypeId?if_exists}]</b>" +  "${StringUtil.wrapString(item.description?if_exists)}",
	</#list>
</#if>};
var reasons = _.union(reasonContacted, reasonUnContacted);
var resonsMap = _.extend(mapReasonContacted, mapReasonUnContacted);

var listRoleType = [<#if listRoleType?exists><#list listRoleType as item>{
	  roleTypeId: "${item.roleTypeId?if_exists}",
	  description: "${StringUtil.wrapString(item.description?if_exists)}"
},</#list></#if>];
var mapRoleType = {<#if listRoleType?exists><#list listRoleType as item>
"${item.roleTypeId?if_exists}": "${StringUtil.wrapString(item.description?if_exists)}",
</#list></#if>};
</script>