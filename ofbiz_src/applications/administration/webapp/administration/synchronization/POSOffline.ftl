<#include "component://basecrm/webapp/basecrm/callcenter/ftLlibraryVariable.ftl"/>
<script src="/crmresources/js/generalUtils.js"></script>

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
					{ name: 'productStoreId', type: 'string' },
					{ name: 'facilityId', type: 'string' }]"/>

<#assign columnlist = "{ text: '${StringUtil.wrapString(uiLabelMap.entitySyncId)}', dataField: 'entitySyncId', width: 200 },
					{ text: '${StringUtil.wrapString(uiLabelMap.ADStatus)}', datafield: 'runStatusId', filtertype: 'checkedlist', width: 200,
						cellsrenderer: function(row, colum, value) {
							value = value?value=mapStatusItem[value]:value;
							return '<div style=margin:4px;>' + value + '</div>';
						},
						createfilterwidget: function (column, htmlElement, editor) {
							editor.jqxDropDownList({ autoDropDownHeight: true, source: listStatusItem, displayMember: 'description', valueMember: 'statusId' });
						}
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.BSProductStore)}', datafield: 'facilityId', minWidth: 200 },
					{ text: '${StringUtil.wrapString(uiLabelMap.lastSuccessfulSynchTime)}', datafield: 'lastSuccessfulSynchTime', width: 200, filtertype: 'range', cellsformat: 'HH:mm:ss dd/MM/yyyy' },
					{ text: '${StringUtil.wrapString(uiLabelMap.lastHistoryStartDate)}', datafield: 'lastHistoryStartDate', width: 200, filtertype: 'range', cellsformat: 'HH:mm:ss dd/MM/yyyy' }
					"/>

<#assign addrow = "false"/>
<#if hasOlbPermission("MODULE", "SYS_SYNCHRONIZATION_POS_OFFLINE", "CREATE")>
	<#assign addrow = "true"/>
</#if>
<#assign mouseRightMenu = "false"/>
<#if hasOlbPermission("MODULE", "SYS_SYNCHRONIZATION_POS_OFFLINE", "UPDATE")>
	<#assign mouseRightMenu = "true"/>
</#if>

<@jqGrid id="jqxgridPOSOffline" addrow=addrow clearfilteringbutton="true" editable="false" alternativeAddPopup="addPOSOffline"
	columnlist=columnlist dataField=dataField contextMenuId="contextMenu" mouseRightMenu=mouseRightMenu
	showtoolbar="true" filtersimplemode="true" showstatusbar="false" isShowTitleProperty="true" addType="popup"
	url="jqxGeneralServicer?sname=JQGetListPOSOffline"
	createUrl="jqxGeneralServicer?sname=createPOSOffline&jqaction=C" addColumns="productStoreId"
	updateUrl="jqxGeneralServicer?jqaction=U&sname=reactiveSynchronization" editColumns="entitySyncId"/>

<div id="contextMenu" style="display:none;">
	<ul>
		<li id="reactiveSynchronization"><i class="fa fa-refresh"></i>&nbsp;${uiLabelMap.ADReactiveSynchronization}</li>
	</ul>
</div>

<#include "popup/addPOSOffline.ftl"/>

<script>
	var mainGrid = $("#jqxgridPOSOffline");
	multiLang = _.extend(multiLang, {
		BSStoreName: "${StringUtil.wrapString(uiLabelMap.BSStoreName)}",
	});
	$(document).ready(function() {
		AddPOSOffline.init();
		var contextmenu = $("#contextMenu").jqxMenu({ theme: theme, width: 180, autoOpenPopup: false, mode: "popup"});
		contextmenu.on("itemclick", function (event) {
			var args = event.args;
			var itemId = $(args).attr("id");
			switch (itemId) {
			case "reactiveSynchronization":
				var rowIndexSelected = mainGrid.jqxGrid("getSelectedRowindex");
				var data = mainGrid.jqxGrid("getrowdata", rowIndexSelected);
				mainGrid.jqxGrid("updaterow", data.uid, data);
				break;
			default:
				break;
			}
		});
		contextmenu.on("shown", function () {
			var rowIndexSelected = mainGrid.jqxGrid("getSelectedRowindex");
			if (mainGrid.jqxGrid("getcellvalue", rowIndexSelected, "runStatusId") == "ESR_RUNNING") {
				contextmenu.jqxMenu("disable", "reactiveSynchronization", false);
			}else {
				contextmenu.jqxMenu("disable", "reactiveSynchronization", true);
			}
		});
	});
</script>