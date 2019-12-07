<#assign promotionStatuses = delegator.findByAnd("StatusItem", {"statusTypeId" : "PROMO_STATUS"}, null, false)!/>
<script type="text/javascript">
	var promotionStatusData = [
	<#if promotionStatuses?exists>
		<#list promotionStatuses as statusItem>
		{	statusId: '${statusItem.statusId}',
			description: '${StringUtil.wrapString(statusItem.get("description", locale))}'
		},
		</#list>
	</#if>
	];
	
	if (uiLabelMap == undefined) var uiLabelMap = {};
	uiLabelMap.BSRefresh = '${StringUtil.wrapString(uiLabelMap.BSRefresh)}';
	uiLabelMap.BSViewDetail = '${StringUtil.wrapString(uiLabelMap.BSViewDetail)}';
	uiLabelMap.BSViewDetailInNewTab = '${StringUtil.wrapString(uiLabelMap.BSViewDetailInNewTab)}';
	uiLabelMap.BSDeleteSelectedRow = '${StringUtil.wrapString(uiLabelMap.BSDeleteSelectedRow)}';
	uiLabelMap.BSCreateNew = '${StringUtil.wrapString(uiLabelMap.BSCreateNew)}';
	var contextMenuItemId = "${contextMenuItemId}";
</script>
<@jqOlbCoreLib />
<script type="text/javascript" src="/poresources/js/promotion/promotionList.js"></script>
