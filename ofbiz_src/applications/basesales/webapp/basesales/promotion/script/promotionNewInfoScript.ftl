<#--<#assign salesMethodChannelEnum = Static["com.olbius.basesales.util.SalesUtil"].getListSalesMethodChannelEnum(delegator)!/>-->
<#assign listRoleType = delegator.findByAnd("RoleType", null, null, true)!/>
<script type="text/javascript">
	<#--var salesMethodChannelEnumData = [
	<#if salesMethodChannelEnum?exists>
		<#list salesMethodChannelEnum as enumItem>
		{	enumId: '${enumItem.enumId}',
			description: '${StringUtil.wrapString(enumItem.get("description", locale))}'
		},
		</#list>
	</#if>
	];-->
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
	
	var dataYesNoChoose = [
		{id : "N", description : "${StringUtil.wrapString(uiLabelMap.BSChNo)}"},
		{id : "Y", description : "${StringUtil.wrapString(uiLabelMap.BSChYes)}"}
	];
	
	if (uiLabelMap == undefined) var uiLabelMap = {};
	uiLabelMap.ClickToChoose = "${StringUtil.wrapString(uiLabelMap.ClickToChoose)}";
	uiLabelMap.BSProductStoreId = "${StringUtil.wrapString(uiLabelMap.BSProductStoreId)}";
	uiLabelMap.BSStoreName = "${StringUtil.wrapString(uiLabelMap.BSStoreName)}";
	uiLabelMap.BSPayToParty = "${StringUtil.wrapString(uiLabelMap.BSPayToParty)}";
	uiLabelMap.BSDefaultCurrencyUomId = "${StringUtil.wrapString(uiLabelMap.BSDefaultCurrencyUomId)}";
	uiLabelMap.BSRoleTypeId = "${StringUtil.wrapString(uiLabelMap.BSRoleTypeId)}";
	uiLabelMap.BSDescription = "${StringUtil.wrapString(uiLabelMap.BSDescription)}";
	
	if (typeof(dataPromoNew) == "undefined") var dataPromoNew = {};
	dataPromoNew.showToCustomer = "Y";
	dataPromoNew.requireCode = "N";
	<#if productPromo?exists>
		<#if !copyMode>
			dataPromoNew.productPromoId = "${productPromo.productPromoId?if_exists}";
			<#if productPromo.fromDate?exists>
				dataPromoNew.fromDate = "${productPromo.fromDate}";
			</#if>
			<#if productPromo.thruDate?exists>
				dataPromoNew.thruDate = "${productPromo.thruDate}";
			</#if>
		</#if>
		
		<#if productPromo.promoName?exists>
			dataPromoNew.productPromoName = "${StringUtil.wrapString(productPromo.promoName?if_exists)}";
		</#if>
		<#if productPromo.useLimitPerOrder?exists>
			dataPromoNew.useLimitPerOrder = "${productPromo.useLimitPerOrder}";
		</#if>
		<#if productPromo.useLimitPerCustomer?exists>
			dataPromoNew.useLimitPerCustomer = "${productPromo.useLimitPerCustomer}";
		</#if>
		<#if productPromo.useLimitPerPromotion?exists>
			dataPromoNew.useLimitPerPromotion = "${productPromo.useLimitPerPromotion}";
		</#if>
		<#if productPromo.showToCustomer?exists>
			dataPromoNew.showToCustomer = "${productPromo.showToCustomer}";
		</#if>
		<#if productPromo.requireCode?exists>
			dataPromoNew.requireCode = "${productPromo.requireCode}";
		</#if>
	</#if>
	
	var listProductStoreSelected = [
		<#if productPromo?exists && productStorePromoApplIso?exists>
		    <#if productStorePromoApplIso?is_collection>
				<#list productStorePromoApplIso as productStoreIdApply>
					"${productStoreIdApply.productStoreId}", 
				</#list>
		    <#else>
				"${productStorePromoApplIso.productStoreId}", 
		    </#if>
	    </#if>
    ];
    var listRoleTypeSelected = [
		<#if productPromo?exists && promoRoleTypeApplyIso?exists>
		    <#if promoRoleTypeApply?is_collection>
				<#list promoRoleTypeApplyIso as partyRoleTypesApply>
					"${partyRoleTypesApply.roleTypeId}", 
				</#list>
		    <#else>
				"${promoRoleTypeApplyIso.roleTypeId}", 
		    </#if>
	    </#if>
    ];
</script>
<script type="text/javascript" src="/salesresources/js/promotion/promotionNewInfo.js"></script>

<#--
var configSalesChannel = {
	placeHolder: '${StringUtil.wrapString(uiLabelMap.BSClickToChoose)}',
	key: 'enumId',
	value: 'description',
	autoDropDownHeight: true,
}
new OlbDropDownList($("#salesMethodChannelEnumId"), salesMethodChannelEnumData, configSalesChannel, [<#if productPromo?if_exists.salesMethodChannelEnumId?exists>"${productPromo.salesMethodChannelEnumId}"</#if>]);
-->