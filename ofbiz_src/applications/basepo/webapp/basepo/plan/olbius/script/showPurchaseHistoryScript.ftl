<script type="text/javascript">
	var orderStatusData = new Array();
	<#assign orderStatusList = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", "ORDER_STATUS"), null, null, null, false) /> 
	<#if orderStatusList?exists><#list orderStatusList as status >
			var row = {};
			row["statusId"] = "${status.statusId?if_exists}";
			row["description"] = "${status.get("description", locale)?if_exists}";
			orderStatusData[${status_index}] = row;
		</#list></#if>

	if(uiLabelMap == undefined) var uiLabelMap = {};
	uiLabelMap.SettingReturnOrder = "${StringUtil.wrapString(uiLabelMap.SettingReturnOrder)}";
	uiLabelMap.SettingPurchasesOrder = "${StringUtil.wrapString(uiLabelMap.SettingPurchasesOrder)}";
	var showPurchaseHistoryGlobalObject = {};
	showPurchaseHistoryGlobalObject.orderId = "${parameters.orderId?if_exists}";
	showPurchaseHistoryGlobalObject.flagSelectRow = true;
</script>