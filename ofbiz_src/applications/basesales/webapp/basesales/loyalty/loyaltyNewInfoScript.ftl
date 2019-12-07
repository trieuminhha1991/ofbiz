<#assign listRoleType = delegator.findByAnd("RoleType", null, null, true)!/>
<#assign listLoyaltyType = delegator.findByAnd("Enumeration", {"enumTypeId" : "LOYALTY_TYPE"}, null, false)!/>
<script type="text/javascript">
	var roleTypeData = [
	<#if listRoleType?exists>
		<#list listRoleType as roleTypeItem>
		{	roleTypeId : "${roleTypeItem.roleTypeId}",
			description : "${StringUtil.wrapString(roleTypeItem.get("description", locale))}",
			descriptionSearch : "${StringUtil.wrapString(roleTypeItem.get("description", locale))} [${roleTypeItem.roleTypeId}]",
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
	
	var dataYesNoChoose = [
		{id : "N", description : "${StringUtil.wrapString(uiLabelMap.BSChNo)}"},
		{id : "Y", description : "${StringUtil.wrapString(uiLabelMap.BSChYes)}"}
	];
	
	if (uiLabelMap == undefined) var uiLabelMap = {};
	uiLabelMap.ClickToChoose = "${StringUtil.wrapString(uiLabelMap.ClickToChoose)}";
	
	if (typeof(dataLoyaltyNew) == "undefined") var dataLoyaltyNew = {};
	<#--dataLoyaltyNew.showToCustomer = "Y";-->
	<#--dataLoyaltyNew.requireCode = "N";-->
	<#if loyalty?exists>
		dataLoyaltyNew.loyaltyId = "${loyalty.loyaltyId?if_exists}";
		
		<#if loyalty.loyaltyName?exists>
			dataLoyaltyNew.loyaltyName = "${StringUtil.wrapString(loyalty.loyaltyName?if_exists)}";
		</#if>
		<#if loyalty.loyaltyText?exists>
			dataLoyaltyNew.loyaltyText = "${StringUtil.wrapString(loyalty.loyaltyText?if_exists)}";
		</#if>
		<#if loyalty.loyaltyTypeId?exists>
			dataLoyaltyNew.loyaltyTypeId = "${StringUtil.wrapString(loyalty.loyaltyTypeId?if_exists)}";
		</#if>
		<#if loyalty.fromDate?exists>
			dataLoyaltyNew.fromDate = "${loyalty.fromDate}";
		</#if>
		<#if loyalty.thruDate?exists>
			dataLoyaltyNew.thruDate = "${loyalty.thruDate}";
		</#if>
		<#--<#if loyalty.showToCustomer?exists>
			dataLoyaltyNew.showToCustomer = "${loyalty.showToCustomer}";
		</#if>
		<#if loyalty.useLimitPerOrder?exists>
			dataLoyaltyNew.useLimitPerOrder = "${loyalty.useLimitPerOrder}";
		</#if>
		<#if loyalty.useLimitPerCustomer?exists>
			dataLoyaltyNew.useLimitPerCustomer = "${loyalty.useLimitPerCustomer}";
		</#if>
		<#if loyalty.useLimitPerPromotion?exists>
			dataLoyaltyNew.useLimitPerPromotion = "${loyalty.useLimitPerPromotion}";
		</#if>
		<#if loyalty.requireCode?exists>
			dataLoyaltyNew.requireCode = "${loyalty.requireCode}";
		</#if>-->
	</#if>
	
	var listProductStoreSelected = [
		<#if loyalty?exists && productStoreLoyaltyApplIso?exists>
		    <#if productStoreLoyaltyApplIso?is_collection>
				<#list productStoreLoyaltyApplIso as productStoreIdApply>
					"${productStoreIdApply.productStoreId}", 
				</#list>
		    <#else>
				"${productStoreLoyaltyApplIso.productStoreId}", 
		    </#if>
	    </#if>
    ];
    var listRoleTypeSelected = [
		<#if loyalty?exists && loyaltyRoleTypeApplyIso?exists>
		    <#if loyaltyRoleTypeApplyIso?is_collection>
				<#list loyaltyRoleTypeApplyIso as partyRoleTypesApply>
					"${partyRoleTypesApply.roleTypeId}", 
				</#list>
		    <#else>
				"${loyaltyRoleTypeApplyIso.roleTypeId}", 
		    </#if>
	    </#if>
    ];
</script>
<script type="text/javascript" src="/salesresources/js/loyalty/loyaltyNewInfo.js"></script>