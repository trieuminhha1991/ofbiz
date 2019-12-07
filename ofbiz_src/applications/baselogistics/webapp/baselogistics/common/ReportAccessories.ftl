<link rel="stylesheet" type="text/css" href="../../logresources/css/reportStyle.css" />
<@jqGridMinimumLib/>
<script type="text/javascript" src="/aceadmin/assets/js/initDashboard.js"></script>

<script>
	<#assign transferTypes = delegator.findList("TransferType", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("parentTypeId", Static["org.ofbiz.entity.condition.EntityOperator"].NOT_EQUAL, null), null, null, null, false) />
	var transferTypeData = new Array();
	<#list transferTypes as item>
		var row = {};
		row['transferTypeId'] = "${item.transferTypeId}";
		row['description'] = "${StringUtil.wrapString(item.get('description', locale)?if_exists)}";
		transferTypeData.push(row);
	</#list>
	if (typeof (LocalData) == "undefined") {
		var LocalData = new Object();
		
		LocalData.array = Object.freeze({
			categories : Object.freeze(${StringUtil.wrapString(LocalData.categories!"[]")}),
			facilities : Object.freeze(${StringUtil.wrapString(LocalData.facilities!"[]")}),
			returnReasons : Object.freeze(${StringUtil.wrapString(LocalData.returnReasons!"[]")}),
			uoms_PRODUCT_PACKING : Object.freeze(${StringUtil.wrapString(LocalData.uoms_PRODUCT_PACKING!"[]")}),
			optionLimit: Object.freeze([
				{ text:"${StringUtil.wrapString(uiLabelMap.FullOptionFilter)}", value: "" },
				{ text: "5", value: 5 }, { text: "10", value: 10 }, { text: "15", value: 15 }, { text: "20", value: 20 }, { text: "25", value: 25 }
			]),
		});
		
		LocalData.object = Object.freeze({
			transferTypeData: transferTypeData,
			uoms_PRODUCT_PACKING : Object.freeze(${StringUtil.wrapString(LocalData.uoms_PRODUCT_PACKING!"[]")}),
			categories : Object.freeze(${StringUtil.wrapString(LocalData.categories!"[]")}),
			facilities : Object.freeze(${StringUtil.wrapString(LocalData.facilities!"[]")}),
			enumerations_SALES_METHOD_CHANNEL : Object.freeze(${StringUtil.wrapString(LocalData.enumerations_SALES_METHOD_CHANNEL!"[]")}),
			enumerations_ORDER_SALES_CHANNEL : Object.freeze(${StringUtil.wrapString(LocalData.enumerations_ORDER_SALES_CHANNEL!"[]")}),
			returnReasons : Object.freeze(${StringUtil.wrapString(LocalData.returnReasons!"[]")}),
			returnItemTypes : Object.freeze(${StringUtil.wrapString(LocalData.returnItemTypes!"[]")}),
			
			optionLimit: Object.freeze([
				{ text:"${StringUtil.wrapString(uiLabelMap.FullOptionFilter)}", value: "" },
				{ text: "5", value: 5 }, { text: "10", value: 10 }, { text: "15", value: 15 }, { text: "20", value: 20 }, { text: "25", value: 25 }
			]),
		});
		
		LocalData.date = Object.freeze({
			currentDate: Object.freeze(new Date()),
			currentYear: Object.freeze(new Date().getFullYear()),
			firstDayOfMonth: Object.freeze(new Date(new Date().getFullYear(), new Date().getMonth(), 1)),
			lastYear: Object.freeze(new Date(new Date().setFullYear(new Date().getFullYear() - 1))),
		});
		
		LocalData.config = Object.freeze({
			jqxwindow: Object.freeze({
				theme: theme,
				width: 700,
				height: 450,
				resizable: true,
				isModal: true,
				autoOpen: false,
				modalOpacity: 0.7
			}),
			jqxgrid: Object.freeze({
				theme: theme,
				localization: getLocalization(),
				filterable: true,
				showfilterrow: true,
				pageable: true,
				sortable: true,
				pagesize: 10,
				width: "100%",
				height: 340
			}),
		});
	}
</script>