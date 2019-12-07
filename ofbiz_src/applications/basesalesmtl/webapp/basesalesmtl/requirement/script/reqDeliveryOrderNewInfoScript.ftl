<#assign salesMethodChannelEnum = Static["com.olbius.basesales.util.SalesUtil"].getListSalesMethodChannelEnum(delegator)!/>
<#assign currentCurrencyUomId = Static["com.olbius.basesales.util.SalesUtil"].getCurrentCurrencyUom(delegator)/>
<script type="text/javascript">
	<#assign priorityList = delegator.findByAnd("Enumeration", {"enumTypeId" : "ORDER_PRIORITY"}, ["sequenceId"], false)/>
	var priorityData = [
	<#if priorityList?exists>
		<#list priorityList as priority>
		{	enumId: "${priority.enumId}",
			description: "${StringUtil.wrapString(priority.description?default(""))}",
		},
		</#list>
	</#if>
	];
	<#assign orderStatuses = delegator.findByAnd("StatusItem", {"statusTypeId" : "ORDER_STATUS"}, null, false)/>
	var orderStatusData = [
	<#if orderStatuses?exists>
		<#list orderStatuses as statusItem>
		{	statusId: '${statusItem.statusId}',
			description: '${StringUtil.wrapString(statusItem.get("description", locale))}'
		},
		</#list>
	</#if>
	];
	
	var productStoreData = [];
	<#if !isOwnerDistributor?exists>
		<#assign isOwnerDistributor = false/>
	</#if>
	<#if isPurchaseSelfie?exists && isPurchaseSelfie>
		<#assign productStoresBySeller = Static['com.olbius.basesales.product.ProductStoreWorker'].getListProductStoreSellByCustomer(delegator, userLogin)!/>
	<#else>
		<#assign productStoresBySeller = Static['com.olbius.basesales.product.ProductStoreWorker'].getListProductStore(delegator, userLogin, isOwnerDistributor)!/>
	</#if>
	<#if productStoresBySeller?exists>
	productStoreData = [
		<#list productStoresBySeller as productStore>
			{	storeName : "${productStore.storeName?default('')}",
				productStoreId : "${productStore.productStoreId}"
			},
		</#list>
	];
	</#if>
	
    if (typeof(uiLabelMap) == "undefined") var uiLabelMap = {};
    uiLabelMap.BSClickToChoose = "${StringUtil.wrapString(uiLabelMap.BSClickToChoose)}";
    var requirementSelected = {};
    <#if requirement?exists>
    	requirementSelected.requirementId = "${requirement.requirementId?if_exists}";
    	<#if requirement.requiredByDate?exists>
			requirementSelected.requiredByDate = "${requirement.requiredByDate}";
		</#if>
		<#if requirement.requirementStartDate?exists>
			requirementSelected.requirementStartDate = "${requirement.requirementStartDate}";
		</#if>
	</#if>
</script>
<script type="text/javascript" src="/salesmtlresources/js/requirement/reqDeliveryOrderNewInfo.js"></script>
