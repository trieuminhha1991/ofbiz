<script type="text/javascript">
	if (uiLabelMap == undefined) var uiLabelMap = {};
	uiLabelMap.ClickToChoose = "${StringUtil.wrapString(uiLabelMap.ClickToChoose)}";
	uiLabelMap.POSupplierId = "${StringUtil.wrapString(uiLabelMap.POSupplierId)}";
	uiLabelMap.POSupplierName = "${StringUtil.wrapString(uiLabelMap.POSupplierName)}";
	uiLabelMap.BSDefaultCurrencyUomId = "${StringUtil.wrapString(uiLabelMap.BSDefaultCurrencyUomId)}";
	uiLabelMap.BSDescription = "${StringUtil.wrapString(uiLabelMap.BSDescription)}";
	
	if (typeof(dataPromoNew) == "undefined") var dataPromoNew = {};
	dataPromoNew.showToCustomer = "Y";
	dataPromoNew.requireCode = "N";
	<#if promotionPO?exists>
		dataPromoNew.productPromoId = "${promotionPO.productPromoId?if_exists}";
		
		<#if promotionPO.promoName?exists>
			dataPromoNew.productPromoName = "${StringUtil.wrapString(promotionPO.promoName?if_exists)}";
		</#if>
		<#if promotionPO.fromDate?exists>
			dataPromoNew.fromDate = "${promotionPO.fromDate}";
		</#if>
		<#if promotionPO.thruDate?exists>
			dataPromoNew.thruDate = "${promotionPO.thruDate}";
		</#if>
		<#--<#if promotionPO.useLimitPerOrder?exists>
			dataPromoNew.useLimitPerOrder = "${promotionPO.useLimitPerOrder}";
		</#if>
		<#if promotionPO.useLimitPerCustomer?exists>
			dataPromoNew.useLimitPerCustomer = "${promotionPO.useLimitPerCustomer}";
		</#if>
		<#if promotionPO.useLimitPerPromotion?exists>
			dataPromoNew.useLimitPerPromotion = "${promotionPO.useLimitPerPromotion}";
		</#if>-->
	</#if>
	
	var listSupplierSelected = [
		<#if promotionPO?exists && supplierPromoApplIso?exists>
		    <#if supplierPromoApplIso?is_collection>
				<#list supplierPromoApplIso as supplierIdApply>
					"${supplierIdApply.partyId}", 
				</#list>
		    <#else>
				"${supplierPromoApplIso.partyId}", 
		    </#if>
	    </#if>
    ];
</script>
<script type="text/javascript" src="/poresources/js/promotion/promotionNewInfo.js"></script>