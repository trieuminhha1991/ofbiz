<#include "component://basecrm/webapp/basecrm/callcenter/ftLlibraryVariable.ftl"/>

<#assign listStatusItem = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", "ENTSYNC_RUN"), null, null, null, true) />
<script>
	var listStatusItem = [<#if listStatusItem?exists><#list listStatusItem as item>{
		statusId: '${item.statusId?if_exists}',
		description: "${StringUtil.wrapString(item.get("description", locale))}"
	},</#list></#if>];
	var mapStatusItem = {<#if listStatusItem?exists><#list listStatusItem as item>
		"${item.statusId?if_exists}": "${StringUtil.wrapString(item.get("description", locale)?if_exists)}",
	</#list></#if>};
</script>
<#assign dataField = "[{ name: 'entitySyncId', type: 'string' },
					{ name: 'runStatusId', type: 'string' },
					{ name: 'lastSuccessfulSynchTime', type: 'date', other: 'Timestamp', pattern:'HH:mm:ss dd/MM/yyyy' },
					{ name: 'lastHistoryStartDate', type: 'date', other: 'Timestamp', pattern:'HH:mm:ss dd/MM/yyyy' },
					{ name: 'syncSplitMillis', type: 'number', other: 'Long' },
					{ name: 'syncEndBufferMillis', type: 'number', other: 'Long' },
					{ name: 'keepRemoveInfoHours', type: 'number', other: 'Double' },
					{ name: 'forPullOnly', type: 'string' },
					{ name: 'forPushOnly', type: 'string' },
					{ name: 'targetServiceName', type: 'string' }]"/>

<#assign columnlist = "{ text: '${StringUtil.wrapString(uiLabelMap.entitySyncId)}', dataField: 'entitySyncId', width: 150, editable: false },
					{ text: '${StringUtil.wrapString(uiLabelMap.ADStatus)}', datafield: 'runStatusId', filtertype: 'checkedlist', width: 150, editable: false,
						cellsrenderer: function(row, colum, value) {
							value = value?value=mapStatusItem[value]:value;
							return '<div style=margin:4px;>' + value + '</div>';
						},
						createfilterwidget: function (column, htmlElement, editor) {
							editor.jqxDropDownList({ autoDropDownHeight: true, source: listStatusItem, displayMember: 'description', valueMember: 'statusId' });
						}
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.lastSuccessfulSynchTime)}', datafield: 'lastSuccessfulSynchTime', width: 200, filtertype: 'range', cellsformat: 'HH:mm:ss dd/MM/yyyy', editable: false },
					{ text: '${StringUtil.wrapString(uiLabelMap.lastHistoryStartDate)}', datafield: 'lastHistoryStartDate', width: 200, filtertype: 'range', cellsformat: 'HH:mm:ss dd/MM/yyyy', editable: false },
					{ text: '${StringUtil.wrapString(uiLabelMap.syncSplitMillis)}', datafield: 'syncSplitMillis', columntype: 'numberinput', filtertype: 'number', width: 150,
						cellsrenderer: function(row, column, value, a, b, data) {
							return '<div class=\"text-right\">' + value.toLocaleString(locale) + '</div>';
						}, validation: function (cell, value) {
							if (value > 0) {
								return true;
							}
							return { result: false, message: multiLang.DmsQuantityNotValid };
						}
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.keepRemoveInfoHours)}', datafield: 'keepRemoveInfoHours', filtertype: 'number', width: 150, editable: false,
						cellsrenderer: function(row, column, value, a, b, data) {
							return '<div class=\"text-right\">' + value.toLocaleString(locale) + '</div>';
						}
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.targetServiceName)}', datafield: 'targetServiceName', width: 200, editable: false },
					{ text: '${StringUtil.wrapString(uiLabelMap.forPullOnly)}', datafield: 'forPullOnly', filtertype: 'checkedlist', width: 100, editable: false,
						createfilterwidget: function (column, htmlElement, editor) {
							editor.jqxDropDownList({ autoDropDownHeight: true, source: ['Y', 'N'] });
						}
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.forPushOnly)}', datafield: 'forPushOnly', filtertype: 'checkedlist', width: 100, editable: false,
						createfilterwidget: function (column, htmlElement, editor) {
							editor.jqxDropDownList({ autoDropDownHeight: true, source: ['Y', 'N'] });
						}
					}"/>

<#assign editable = "false" />
<#if security.hasEntityPermission("ENTITY_SYNC", "_UPDATE", session)>
<#assign editable = "true" />
</#if>

<@jqGrid id="jqxgridStatusSynchronization" addrow="false" clearfilteringbutton="true" editable=editable alternativeAddPopup="addUserGroup"
	columnlist=columnlist dataField=dataField customTitleProperties="ADStatusSynchronization"
	showtoolbar="true" filtersimplemode="true" showstatusbar="false" isShowTitleProperty="true" addType="popup"
	url="jqxGeneralServicer?sname=JQGetListEntitySync"
	updateUrl="jqxGeneralServicer?jqaction=U&sname=updateEntitySync" editColumns="entitySyncId;syncSplitMillis(java.lang.Long)"/>