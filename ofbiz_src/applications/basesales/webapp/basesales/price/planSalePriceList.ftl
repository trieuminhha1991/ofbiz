<#include "component://basecrm/webapp/basecrm/callcenter/ftLlibraryVariable.ftl"/>
<script src="/crmresources/js/generalUtils.js"></script>

<#assign dataField = "[
		{ name: 'jobId', type: 'string' },
		{ name: 'jobName', type: 'string' },
		{ name: 'runTime', type: 'date', other: 'Timestamp', pattern:'HH:mm:ss dd/MM/yyyy' },
		{ name: 'statusId', type: 'string' }
	]"/>
<#assign columnlist = "
		{ text: '${uiLabelMap.DmsSequenceId}', datafield: '', width: 50, sortable: false, filterable: false, pinned: true, groupable: false, draggable: false, resizable: false,
			cellsrenderer: function (row, column, value) {
				return '<div style=\"margin:4px;\">' + (row + 1) + '</div>';
			}
		},
		{ text: '${StringUtil.wrapString(uiLabelMap.PlanId)}', dataField: 'jobId', width: 120,
			cellsrenderer: function (row, column, value, a, b, data) {
				return \"<a style='margin:4px;' href='javascript:viewDetail(&#39;\" + JSON.stringify(data) + \"&#39;)'>\" + value + \"</a>\";
			}
		},
		{ text: '${StringUtil.wrapString(uiLabelMap.SGCExecutionDate)}', datafield: 'runTime', width: 160, filtertype: 'range', cellsformat: 'dd/MM/yyyy HH:mm:ss'},
		{ text: '${StringUtil.wrapString(uiLabelMap.BSStatus)}', datafield: 'statusId', filtertype: 'checkedlist', width: 220,
			cellsrenderer: function(row, colum, value) {
				value?value=mapStatusItem[value]:value;
				return '<span>' + value + '</span>';
			},
			createfilterwidget: function (column, htmlElement, editor) {
				editor.jqxDropDownList({ autoDropDownHeight: true, source: listStatusItem, displayMember: 'description', valueMember: 'statusId' });
			}
		},
		{ text: '${StringUtil.wrapString(uiLabelMap.BSNote)}', dataField: 'jobName', minwidth: 300 }
	"/>

<@jqGrid id="jqxgridPlansSalePrice" addrow="true" clearfilteringbutton="true" editable="false" alternativeAddPopup="popupPlansSalePriceAdd"
	columnlist=columnlist dataField=dataField contextMenuId="contextMenu" mouseRightMenu="true"
	showtoolbar="true" filtersimplemode="true" showstatusbar="false" isShowTitleProperty="true" addType="popup"
	url="jqxGeneralServicer?sname=JQGetListPlansSalePrice"/>

<div id="contextMenu" style="display:none;">
	<ul>
	<#if hasOlbPermission("MODULE", "PLANS_SALEPRICE", "UPDATE")>
		<li id="mnuEdit"><i class="fa fa-pencil"></i>&nbsp;${uiLabelMap.CommonEdit}</li>
	</#if>
		<li id="mnuListProduct"><i class="fa fa-list-ol"></i>&nbsp;${uiLabelMap.BSListProduct}</li>
		<li id="mnuCancel"><i class="fa fa-trash-o red"></i>&nbsp;${uiLabelMap.CommonCancel}</li>
		<li id="mnuRefresh"><i class="fa fa-refresh"></i>&nbsp;${uiLabelMap.BSRefresh}</li>
	</ul>
</div>

<#if hasOlbPermission("MODULE", "PLANS_SALEPRICE", "CREATE") || hasOlbPermission("MODULE", "PLANS_SALEPRICE", "UPDATE")>
	<#include "planSalePriceNewAddPopup.ftl"/>
</#if>

<#assign listStatusItem = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", "SERVICE_STATUS"), null, null, null, true) />
<#assign listQuantityUom = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "PRODUCT_PACKING"), null, null, null, true) />
<#assign listCurrencyUom = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "CURRENCY_MEASURE"), null, null, null, true) />

<script>
	var viewDetail = function(data) {
		data = JSON.parse(data);
		OlbAddProductItems.open(data.jobId, data.jobName, data.runTime, false, false);
	};
	var listStatusItem = [<#if listStatusItem?exists><#list listStatusItem as item>{
		statusId: '${item.statusId?if_exists}',
		description: "${StringUtil.wrapString(item.get("description", locale))}"
	},</#list></#if>];
	var mapStatusItem = {<#if listStatusItem?exists><#list listStatusItem as item>
		"${item.statusId?if_exists}": "${StringUtil.wrapString(item.get("description", locale)?if_exists)}",
	</#list></#if>};
	var listQuantityUom = [<#if listQuantityUom?exists><#list listQuantityUom as item>{
		uomId: '${item.uomId?if_exists}',
		description: "${StringUtil.wrapString(item.get("description", locale))}"
	},</#list></#if>];
	var mapQuantityUom = {<#if listQuantityUom?exists><#list listQuantityUom as item>
	"${item.uomId?if_exists}": "${StringUtil.wrapString(item.get("description", locale)?if_exists)}",
	</#list></#if>};
	<#--
	var listCurrencyUom = [<#if listCurrencyUom?exists><#list listCurrencyUom as item>{
		uomId: '${item.uomId?if_exists}',
		description: "${StringUtil.wrapString(item.get("description", locale))}"
	},</#list></#if>];
	var mapCurrencyUom = {<#if listCurrencyUom?exists><#list listCurrencyUom as item>
	"${item.uomId?if_exists}": "${StringUtil.wrapString(item.get("description", locale)?if_exists)}",
	</#list></#if>};
	-->

	multiLang = _.extend(multiLang, {
		BSProduct: "${StringUtil.wrapString(uiLabelMap.BSProduct)}",
		BSUPC: "${StringUtil.wrapString(uiLabelMap.BSUPC)}",
		BSPrimaryProductCategory: "${StringUtil.wrapString(uiLabelMap.BSPrimaryProductCategory)}",
		BSProductStoreName: "${StringUtil.wrapString(uiLabelMap.BSProductStoreName)}",
		ProductProductName: "${StringUtil.wrapString(uiLabelMap.ProductProductName)}",
		DmsQuantityUomId: "${StringUtil.wrapString(uiLabelMap.DmsQuantityUomId)}",
		CommonAdd: "${StringUtil.wrapString(uiLabelMap.CommonAdd)}",
		BSDefaultPrice: "${StringUtil.wrapString(uiLabelMap.BSDefaultPrice)}",
		BSListPrice: "${StringUtil.wrapString(uiLabelMap.BSListPrice)}",
		BSCurrencyUomId: "${StringUtil.wrapString(uiLabelMap.BSCurrencyUomId)}",
		DAYouNotYetChooseProduct: "${StringUtil.wrapString(uiLabelMap.DAYouNotYetChooseProduct)}",
		BSCurrentDefaultPrice: "${StringUtil.wrapString(uiLabelMap.BSCurrentDefaultPrice)}",
		BSCurrentListPrice: "${StringUtil.wrapString(uiLabelMap.BSCurrentListPrice)}",
		BSListProduct: "${StringUtil.wrapString(uiLabelMap.BSListProduct)}",
	});
	var mainGrid;
	$(document).ready(function() {
		mainGrid = $("#jqxgridPlansSalePrice");
		var contextmenu = $("#contextMenu").jqxMenu({ theme: theme, width: 180, autoOpenPopup: false, mode: "popup" });
		contextmenu.on("itemclick", function (event) {
			var args = event.args;
			var itemId = $(args).attr("id");
			switch (itemId) {
			case "mnuEdit":
				var rowIndexSelected = mainGrid.jqxGrid("getSelectedRowindex");
				var rowData = mainGrid.jqxGrid("getrowdata", rowIndexSelected);
				if (rowData) {
					OlbAddProductItems.open(rowData.jobId, rowData.jobName, rowData.runTime, false, true);
				}
				break;
			case "mnuListProduct":
				var rowIndexSelected = mainGrid.jqxGrid("getSelectedRowindex");
				var rowData = mainGrid.jqxGrid("getrowdata", rowIndexSelected);
				if (rowData) {
					OlbAddProductItems.open(rowData.jobId, rowData.jobName, rowData.runTime, false, false);
				}
				break;
			case "mnuCancel":
				var rowIndexSelected = mainGrid.jqxGrid("getSelectedRowindex");
				var rowData = mainGrid.jqxGrid("getrowdata", rowIndexSelected);
				if (rowData) {
					DataAccess.executeAsync({
						url: "cancelJobAsSystem",
						data: { jobId: rowData.jobId }
					}, function(res) {
						mainGrid.jqxGrid("updatebounddata");
					});
				}
				break;
			case "mnuRefresh":
				mainGrid.jqxGrid("updatebounddata");
				break;
			default:
				break;
			}
		});
		contextmenu.on("shown", function () {
			var rowIndexSelected = mainGrid.jqxGrid("getSelectedRowindex");
			if (mainGrid.jqxGrid("getcellvalue", rowIndexSelected, "statusId") == "SERVICE_PENDING") {
				contextmenu.jqxMenu("disable", "mnuEdit", false);
				contextmenu.jqxMenu("disable", "mnuCancel", false);
			}else {
				contextmenu.jqxMenu("disable", "mnuEdit", true);
				contextmenu.jqxMenu("disable", "mnuCancel", true);
			}
		});
		OlbAddProductItems.init();
	});
</script>