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
					{ name: 'forPullOnly', type: 'string' },
					{ name: 'forPushOnly', type: 'string' },
					{ name: 'runStatusId', type: 'string' },
					{ name: 'startDate', type: 'date', other: 'Timestamp', pattern:'HH:mm:ss dd/MM/yyyy' },
					{ name: 'beginningSynchTime', type: 'date', other: 'Timestamp', pattern:'HH:mm:ss dd/MM/yyyy' },
					{ name: 'lastSuccessfulSynchTime', type: 'date', other: 'Timestamp', pattern:'HH:mm:ss dd/MM/yyyy' },
					{ name: 'lastCandidateEndTime', type: 'date', other: 'Timestamp', pattern:'HH:mm:ss dd/MM/yyyy' },
					{ name: 'lastSplitStartTime', type: 'number', other: 'Long' },
					{ name: 'toCreateInserted', type: 'number', other: 'Long' },
					{ name: 'toCreateUpdated', type: 'number', other: 'Long' },
					{ name: 'toCreateNotUpdated', type: 'number', other: 'Long' },
					{ name: 'toStoreInserted', type: 'number', other: 'Long' },
					{ name: 'toStoreUpdated', type: 'number', other: 'Long' },
					{ name: 'toStoreNotUpdated', type: 'number', other: 'Long' },
					{ name: 'toRemoveDeleted', type: 'number', other: 'Long' },
					{ name: 'toRemoveAlreadyDeleted', type: 'number', other: 'Long' },
					{ name: 'totalRowsExported', type: 'number', other: 'Long' },
					{ name: 'totalRowsToCreate', type: 'number', other: 'Long' },
					{ name: 'totalRowsToStore', type: 'number', other: 'Long' },
					{ name: 'totalRowsToRemove', type: 'number', other: 'Long' },
					{ name: 'totalSplits', type: 'number', other: 'Long' },
					{ name: 'totalStoreCalls', type: 'number', other: 'Long' },
					{ name: 'runningTimeMillis', type: 'number', other: 'Long' },
					{ name: 'perSplitMinMillis', type: 'number', other: 'Long' },
					{ name: 'perSplitMaxMillis', type: 'number', other: 'Long' },
					{ name: 'perSplitMinItems', type: 'number', other: 'Long' },
					{ name: 'perSplitMaxItems', type: 'number', other: 'Long' }]"/>

<#assign columnlist = "{ text: '${StringUtil.wrapString(uiLabelMap.entitySyncId)}', dataField: 'entitySyncId', width: 150, editable: false },
					{ text: '${StringUtil.wrapString(uiLabelMap.forPullOnly)}', datafield: 'forPullOnly', filtertype: 'checkedlist', width: 110, editable: false,
						createfilterwidget: function (column, htmlElement, editor) {
							editor.jqxDropDownList({ autoDropDownHeight: true, source: ['Y', 'N'] });
						}
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.forPushOnly)}', datafield: 'forPushOnly', filtertype: 'checkedlist', width: 110, editable: false,
						createfilterwidget: function (column, htmlElement, editor) {
							editor.jqxDropDownList({ autoDropDownHeight: true, source: ['Y', 'N'] });
						}
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.startDate)}', datafield: 'startDate', width: 200, filtertype: 'range', cellsformat: 'HH:mm:ss dd/MM/yyyy', editable: false },
					{ text: '${StringUtil.wrapString(uiLabelMap.ADStatus)}', datafield: 'runStatusId', filtertype: 'checkedlist', width: 150, editable: false,
						cellsrenderer: function(row, colum, value) {
							value = value?value=mapStatusItem[value]:value;
							return '<div style=margin:4px;>' + value + '</div>';
						},
						createfilterwidget: function (column, htmlElement, editor) {
							editor.jqxDropDownList({ autoDropDownHeight: true, source: listStatusItem, displayMember: 'description', valueMember: 'statusId' });
						}
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.beginningSynchTime)}', datafield: 'beginningSynchTime', width: 200, filtertype: 'range', cellsformat: 'HH:mm:ss dd/MM/yyyy', editable: false },
					{ text: '${StringUtil.wrapString(uiLabelMap.lastSuccessfulSynchTime)}', datafield: 'lastSuccessfulSynchTime', width: 200, filtertype: 'range', cellsformat: 'HH:mm:ss dd/MM/yyyy', editable: false },
					{ text: '${StringUtil.wrapString(uiLabelMap.lastCandidateEndTime)}', datafield: 'lastCandidateEndTime', width: 200, filtertype: 'range', cellsformat: 'HH:mm:ss dd/MM/yyyy', editable: false },
					{ text: '${StringUtil.wrapString(uiLabelMap.lastSplitStartTime)}', datafield: 'lastSplitStartTime', filtertype: 'number', width: 150, editable: false,
						cellsrenderer: function(row, column, value, a, b, data) {
							return '<div class=\"text-right\">' + value.toLocaleString(locale) + '</div>';
						}
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.toCreateInserted)}', datafield: 'toCreateInserted', filtertype: 'number', width: 150, editable: false,
						cellsrenderer: function(row, column, value, a, b, data) {
							return '<div class=\"text-right\">' + value.toLocaleString(locale) + '</div>';
						}
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.toCreateUpdated)}', datafield: 'toCreateUpdated', filtertype: 'number', width: 150, editable: false,
						cellsrenderer: function(row, column, value, a, b, data) {
							return '<div class=\"text-right\">' + value.toLocaleString(locale) + '</div>';
						}
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.toCreateNotUpdated)}', datafield: 'toCreateNotUpdated', filtertype: 'number', width: 150, editable: false,
						cellsrenderer: function(row, column, value, a, b, data) {
							return '<div class=\"text-right\">' + value.toLocaleString(locale) + '</div>';
						}
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.toStoreInserted)}', datafield: 'toStoreInserted', filtertype: 'number', width: 150, editable: false,
						cellsrenderer: function(row, column, value, a, b, data) {
							return '<div class=\"text-right\">' + value.toLocaleString(locale) + '</div>';
						}
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.toStoreUpdated)}', datafield: 'toStoreUpdated', filtertype: 'number', width: 150, editable: false,
						cellsrenderer: function(row, column, value, a, b, data) {
							return '<div class=\"text-right\">' + value.toLocaleString(locale) + '</div>';
						}
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.toStoreNotUpdated)}', datafield: 'toStoreNotUpdated', filtertype: 'number', width: 150, editable: false,
						cellsrenderer: function(row, column, value, a, b, data) {
							return '<div class=\"text-right\">' + value.toLocaleString(locale) + '</div>';
						}
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.toRemoveDeleted)}', datafield: 'toRemoveDeleted', filtertype: 'number', width: 150, editable: false,
						cellsrenderer: function(row, column, value, a, b, data) {
							return '<div class=\"text-right\">' + value.toLocaleString(locale) + '</div>';
						}
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.toRemoveAlreadyDeleted)}', datafield: 'toRemoveAlreadyDeleted', filtertype: 'number', width: 150, editable: false,
						cellsrenderer: function(row, column, value, a, b, data) {
							return '<div class=\"text-right\">' + value.toLocaleString(locale) + '</div>';
						}
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.totalRowsExported)}', datafield: 'totalRowsExported', filtertype: 'number', width: 150, editable: false,
						cellsrenderer: function(row, column, value, a, b, data) {
							return '<div class=\"text-right\">' + value.toLocaleString(locale) + '</div>';
						}
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.totalRowsToCreate)}', datafield: 'totalRowsToCreate', filtertype: 'number', width: 150, editable: false,
						cellsrenderer: function(row, column, value, a, b, data) {
							return '<div class=\"text-right\">' + value.toLocaleString(locale) + '</div>';
						}
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.totalRowsToStore)}', datafield: 'totalRowsToStore', filtertype: 'number', width: 150, editable: false,
						cellsrenderer: function(row, column, value, a, b, data) {
							return '<div class=\"text-right\">' + value.toLocaleString(locale) + '</div>';
						}
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.totalRowsToRemove)}', datafield: 'totalRowsToRemove', filtertype: 'number', width: 150, editable: false,
						cellsrenderer: function(row, column, value, a, b, data) {
							return '<div class=\"text-right\">' + value.toLocaleString(locale) + '</div>';
						}
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.totalSplits)}', datafield: 'totalSplits', filtertype: 'number', width: 150, editable: false,
						cellsrenderer: function(row, column, value, a, b, data) {
							return '<div class=\"text-right\">' + value.toLocaleString(locale) + '</div>';
						}
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.totalStoreCalls)}', datafield: 'totalStoreCalls', filtertype: 'number', width: 150, editable: false,
						cellsrenderer: function(row, column, value, a, b, data) {
							return '<div class=\"text-right\">' + value.toLocaleString(locale) + '</div>';
						}
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.runningTimeMillis)}', datafield: 'runningTimeMillis', filtertype: 'number', width: 150, editable: false,
						cellsrenderer: function(row, column, value, a, b, data) {
							return '<div class=\"text-right\">' + value.toLocaleString(locale) + '</div>';
						}
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.perSplitMinMillis)}', datafield: 'perSplitMinMillis', filtertype: 'number', width: 150, editable: false,
						cellsrenderer: function(row, column, value, a, b, data) {
							return '<div class=\"text-right\">' + value.toLocaleString(locale) + '</div>';
						}
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.perSplitMaxMillis)}', datafield: 'perSplitMaxMillis', filtertype: 'number', width: 150, editable: false,
						cellsrenderer: function(row, column, value, a, b, data) {
							return '<div class=\"text-right\">' + value.toLocaleString(locale) + '</div>';
						}
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.perSplitMinItems)}', datafield: 'perSplitMinItems', filtertype: 'number', width: 150, editable: false,
						cellsrenderer: function(row, column, value, a, b, data) {
							return '<div class=\"text-right\">' + value.toLocaleString(locale) + '</div>';
						}
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.perSplitMaxItems)}', datafield: 'perSplitMaxItems', filtertype: 'number', width: 150, editable: false,
						cellsrenderer: function(row, column, value, a, b, data) {
							return '<div class=\"text-right\">' + value.toLocaleString(locale) + '</div>';
						}
					}"/>

<@jqGrid id="jqxgridSynchronizationHistory" addrow="false" clearfilteringbutton="true" editable="false" alternativeAddPopup="addUserGroup"
	columnlist=columnlist dataField=dataField
	showtoolbar="true" filtersimplemode="true" showstatusbar="false" isShowTitleProperty="true" addType="popup"
	url="jqxGeneralServicer?sname=JQGetListEntitySyncHistory"/>