<#assign loyaltyStatuses = delegator.findByAnd("StatusItem", {"statusTypeId" : "LOYALTY_STATUS"}, null, false)!/>
<#assign listLoyaltyType = delegator.findByAnd("Enumeration", {"enumTypeId" : "LOYALTY_TYPE"}, null, false)!/>
<script type="text/javascript">
	var loyaltyStatusData = [
	<#if loyaltyStatuses?exists>
		<#list loyaltyStatuses as statusItem>
		{	statusId: '${statusItem.statusId}',
			description: '${StringUtil.wrapString(statusItem.get("description", locale))}'
		},
		</#list>
	</#if>
	];
	
	var loyaltyTypeData = [
   	<#if listLoyaltyType?exists>
   		<#list listLoyaltyType as item>
   		{	enumId : "${item.enumId}",
   			description : "${StringUtil.wrapString(item.get("description", locale))}",
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

<script type="text/javascript" src="/salesresources/js/loyalty/loyaltyList.js"></script>