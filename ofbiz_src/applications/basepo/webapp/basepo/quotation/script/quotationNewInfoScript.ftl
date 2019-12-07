<#assign listProductQuotationType = delegator.findByAnd("ProductQuotationType", null, null, false)!/>
<#assign salesMethodChannelEnum = Static["com.olbius.basesales.util.SalesUtil"].getListSalesMethodChannelEnum(delegator)!/>
<#assign currentCurrencyUomId = Static["com.olbius.basesales.util.SalesUtil"].getCurrentCurrencyUom(delegator)/>
<#assign currencyUom = delegator.findByAnd("Uom", {"uomTypeId" : "CURRENCY_MEASURE"}, null, true)/>
<#assign listRoleType = delegator.findByAnd("RoleType", null, null, true)/>
<script type="text/javascript">
	var productQuotationTypeData = [
	<#if listProductQuotationType?exists>
		<#list listProductQuotationType as item>
		{	typeId: '${item.productQuotationTypeId}',
			description: '${StringUtil.wrapString(item.get("description", locale))}'
		},
		</#list>
	</#if>
	];
	var salesMethodChannelEnumData = [
	<#if salesMethodChannelEnum?exists>
		<#list salesMethodChannelEnum as enumItem>
		{	enumId: '${enumItem.enumId}',
			description: '${StringUtil.wrapString(enumItem.get("description", locale))}'
		},
		</#list>
	</#if>
	];
	var currencyUomData = [
	<#if currencyUom?exists>
		<#list currencyUom as uomItem>
		{	uomId : "${uomItem.uomId}",
			descriptionSearch : "${StringUtil.wrapString(uomItem.get("description", locale))} [${uomItem.abbreviation}]",
		},
		</#list>
	</#if>
	];
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
	var listRoleTypeSelected = [
	<#if productQuotation?exists && roleTypesSelected?exists>
	    <#if roleTypesSelected?is_collection>
			<#list roleTypesSelected as partyRoleTypesApply>
				"${partyRoleTypesApply.roleTypeId}", 
			</#list>
	    <#else>
			"${roleTypesSelected.roleTypeId}", 
	    </#if>
    </#if>
    ];
    var listProductStoreGroupSelected = [
    	<#if productQuotation?exists && productStoreGroupAppls?exists>
			<#list productStoreGroupAppls as productStoreGroupIdApply>
				"${productStoreGroupIdApply.productStoreGroupId}", 
			</#list>
	    </#if>
    ];
    var listProductStoreSelected = [
		<#if productQuotation?exists && productStoreAppls?exists>
			<#list productStoreAppls as productStoreIdApply>
				"${productStoreIdApply.productStoreId}", 
			</#list>
	    </#if>
    ];
    var listContactMechSelected = [
		<#if productQuotation?exists && productContactMechAppls?exists>
			<#list productContactMechAppls as contactMechIdApply>
				"${contactMechIdApply.contactMechId}", 
			</#list>
	    </#if>
    ];
    if (typeof(uiLabelMap) == "undefined") var uiLabelMap = {};
    uiLabelMap.BSClickToChoose = "${StringUtil.wrapString(uiLabelMap.BSClickToChoose)}";
    uiLabelMap.BSId = "${StringUtil.wrapString(uiLabelMap.BSId)}";
    uiLabelMap.BSFullName = "${StringUtil.wrapString(uiLabelMap.BSFullName)}";
    uiLabelMap.BSAddress = "${StringUtil.wrapString(uiLabelMap.BSAddress)}";
    uiLabelMap.BSReceiverName = "${StringUtil.wrapString(uiLabelMap.BSReceiverName)}";
    uiLabelMap.BSOtherInfo = "${StringUtil.wrapString(uiLabelMap.BSOtherInfo)}";
    var quotationSelected = {};
    <#if updateMode>
    	quotationSelected.productQuotationId = "${productQuotation.productQuotationId?if_exists}";
    	quotationSelected.productQuotationTypeId = "${productQuotation.productQuotationTypeId?if_exists}";
    	<#if productQuotation.quotationName?exists>
    		quotationSelected.quotationName = "${StringUtil.wrapString(productQuotation.quotationName?if_exists)}";
    	</#if>
    	<#if productQuotation.fromDate?exists>
			quotationSelected.fromDate = "${productQuotation.fromDate}";
		</#if>
		<#if productQuotation.thruDate?exists>
			quotationSelected.thruDate = "${productQuotation.thruDate}";
		</#if>
		<#if partyApply?exists>
			quotationSelected.partyIdApply = "${partyApply.partyId}";
			quotationSelected.partyIdApplyDefaultValue = "${partyApply.partyId}";
			quotationSelected.partyIdApplyDefaultCode = "${partyApply.partyCode}";
			quotationSelected.partyIdApplyDefaultLabel = "${partyApply.fullName}";
		</#if>
	</#if>
	var updateMode = <#if updateMode>true<#else>false</#if>;
	var copyMode = <#if copyMode>true<#else>false</#if>;
	var isQuotationEditSpecial = <#if isQuotationEditSpecial>true<#else>false</#if>;
    quotationSelected.currencyUomId = <#if productQuotation?if_exists.currencyUomId?exists>'${productQuotation.currencyUomId}'<#else>'${currentCurrencyUomId}'</#if>;
	quotationSelected.salesMethodChannelEnumId = <#if productQuotation?if_exists.salesMethodChannelEnumId?exists>"${productQuotation.salesMethodChannelEnumId}"<#else>null</#if>;
	
	<#if productQuotation?exists && productQuotation.productQuotationId?exists>
	var newUrlUpdateGridItems = "JQListProductAndTaxInQuotation&productQuotationId=${productQuotation.productQuotationId}";
	<#else>
	var newUrlUpdateGridItems = "JQListProductAndTaxByCatalog";<#--&channelEnumId&showAll=Y&prodCatalogId=${currentCatalogId?if_exists}-->
	</#if>
</script>
<script type="text/javascript" src="/poresources/js/quotation/quotationNewInfo.js?v=0.0.1"></script>