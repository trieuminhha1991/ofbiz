<#assign orderPriorityList = delegator.findByAnd("Enumeration", {"enumTypeId": "ORDER_PRIORITY"}, null, false)!/>
<script type="text/javascript">
	var desiredDeliveryDate = <#if defaultDesiredDeliveryDate?exists>'${defaultDesiredDeliveryDate}'<#else>null</#if>;
	var defaultShipAfterDate = <#if defaultShipAfterDate?exists>'${defaultShipAfterDate}'<#else>null</#if>;
	var defaultShipBeforeDate = <#if defaultShipBeforeDate?exists>'${defaultShipBeforeDate}'<#else>null</#if>;
	var defaultPartyId = <#if defaultPartyId?exists>'${defaultPartyId}'<#else>null</#if>;
	var defaultPartyCodeId = <#if defaultPartyCodeId?exists>'${defaultPartyCodeId}'<#else>null</#if>;
	var defaultPartyFullName = <#if defaultPartyFullName?exists>'${defaultPartyFullName}'<#else>null</#if>;
	var defaultProductStoreId = <#if defaultProductStoreId?exists>'${defaultProductStoreId}'<#else>null</#if>;
	var currentAgreementId = <#if currentAgreementId?exists>'${currentAgreementId}'<#else>null</#if>;
	var defaultProductStoreId = <#if defaultProductStoreId?exists>'${defaultProductStoreId}'<#else>null</#if>;
	
	var productStoreData = [
	<#if productStores?exists>
		<#list productStores as productStore>
			{"productStoreId" : "${StringUtil.wrapString(productStore.productStoreId)}", "storeName" : "${StringUtil.wrapString(productStore.storeName?if_exists?replace('U+0022', 'U+0027'))}"},
		</#list>
	</#if>
	];
	var orderPriorityData = [
	<#if orderPriorityList?exists>
		<#list orderPriorityList as priority>
			{"enumId" : "${StringUtil.wrapString(priority.enumId)}", "description" : "${StringUtil.wrapString(priority.description?if_exists)}"},
		</#list>
	</#if>
	];
	
	if (uiLabelMap == undefined) var uiLabelMap = {};
	uiLabelMap.BSCustomerId = '${StringUtil.wrapString(uiLabelMap.BSCustomerId)}';
	uiLabelMap.BSFullName = '${StringUtil.wrapString(uiLabelMap.BSFullName)}';
	uiLabelMap.BSAgreementId = '${StringUtil.wrapString(uiLabelMap.BSAgreementId)}';
	uiLabelMap.BSAgreementCode = '${StringUtil.wrapString(uiLabelMap.BSAgreementCode)}';
	uiLabelMap.BSCustomerId = '${StringUtil.wrapString(uiLabelMap.BSCustomerId)}';
	uiLabelMap.BSFromDate = '${StringUtil.wrapString(uiLabelMap.BSFromDate)}';
	uiLabelMap.BSThruDate = '${StringUtil.wrapString(uiLabelMap.BSThruDate)}';
	uiLabelMap.BSClickToChoose = '${StringUtil.wrapString(uiLabelMap.BSClickToChoose)}';
	uiLabelMap.BSThisFieldMustNotByContainSpecialCharacter = '${uiLabelMap.BSThisFieldMustNotByContainSpecialCharacter}';
	uiLabelMap.BSRequired = '${uiLabelMap.BSRequired}';
	uiLabelMap.BSRequiredValueGreatherOrEqualDateTimeToDay = '${uiLabelMap.BSRequiredValueGreatherOrEqualDateTimeToDay}';
	uiLabelMap.BSDesiredDeliveryDateMustBetweenStartDateAndFinishDate = '${uiLabelMap.BSDesiredDeliveryDateMustBetweenStartDateAndFinishDate}';
	uiLabelMap.BSRequiredValueGreatherOrEqualDateTimeToDay = '${uiLabelMap.BSRequiredValueGreatherOrEqualDateTimeToDay}';
	uiLabelMap.BSStartDateMustLessThanOrEqualFinishDate = '${uiLabelMap.BSStartDateMustLessThanOrEqualFinishDate}';
	uiLabelMap.BSPhone = '${uiLabelMap.BSPhone}';
	uiLabelMap.BSAddress = '${uiLabelMap.BSAddress}';
</script>
<#if isPurchaseSelfie?exists && isPurchaseSelfie>
<script type="text/javascript" src="/salesresources/js/order/orderNewInfoSelfie.js"></script>
<#else>
	<script type="text/javascript" src="/salesresources/js/order/orderNewInfo.js"></script>
	<#if !orderNewSearchProductSplit>
	<script type="text/javascript">
		$(function(){
			productStoreDDL.selectListener(function(itemData){
				var productStoreId = itemData.value;
				
				// load list product
				OlbGridUtil.updateSource($('#jqxgridSO'), "jqxGeneralServicer?sname=JQGetListProductByStoreOrCatalog&productStoreId=" + productStoreId + "&hasrequest=Y");
			});
		});
	</script>
	</#if>
</#if>
