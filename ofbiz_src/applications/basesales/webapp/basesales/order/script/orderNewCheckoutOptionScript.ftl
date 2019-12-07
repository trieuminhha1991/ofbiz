<script type="text/javascript">
	if (defaultPartyId == undefined) {
		var defaultPartyId = <#if defaultPartyId?exists>'${defaultPartyId}'<#else>null</#if>;
	}
	if (defaultProductStoreId == undefined) {
		var defaultProductStoreId = <#if defaultProductStoreId?exists>'${defaultProductStoreId}'<#else>null</#if>;
	}
	var chosenShippingMethod = <#if chosenShippingMethod?exists>'${StringUtil.wrapString(chosenShippingMethod)}'<#else>null</#if>;
	var checkOutPaymentId = <#if checkOutPaymentId?exists>'${checkOutPaymentId}'<#else>'EXT_COD'</#if>;
	var defaultPartyFullName = <#if defaultPartyFullName?exists>'${defaultPartyFullName}'<#else>''</#if>;
	var shipGroupFacilityId = <#if shipGroupFacilityId?exists>'${shipGroupFacilityId}'<#else>null</#if>;
	var favorSupplierPartyId = <#if favorSupplierPartyId?exists>'${favorSupplierPartyId}'<#else>null</#if>;
	var favorSupplierPartyFullName = <#if favorSupplierPartyFullName?exists>'${favorSupplierPartyFullName}'<#else>''</#if>;
	var currentOrganizationPartyId = <#if currentOrganizationPartyId?exists>'${currentOrganizationPartyId}'<#else>''</#if>;
	
	if (uiLabelMap == undefined) var uiLabelMap = {};
	uiLabelMap.BSCustomerId = '${StringUtil.wrapString(uiLabelMap.BSCustomerId)}';
	uiLabelMap.BSEmployeeId = '${StringUtil.wrapString(uiLabelMap.BSEmployeeId)}';
	uiLabelMap.BSFullName = '${StringUtil.wrapString(uiLabelMap.BSFullName)}';
	uiLabelMap.BSCustomerId = '${StringUtil.wrapString(uiLabelMap.BSCustomerId)}';
	uiLabelMap.BSContactMechId = '${StringUtil.wrapString(uiLabelMap.BSContactMechId)}';
	uiLabelMap.BSReceiverName = '${StringUtil.wrapString(uiLabelMap.BSReceiverName)}';
	uiLabelMap.BSOtherInfo = '${StringUtil.wrapString(uiLabelMap.BSOtherInfo)}';
	uiLabelMap.BSAddress = '${StringUtil.wrapString(uiLabelMap.BSAddress)}';
	uiLabelMap.BSCity = '${StringUtil.wrapString(uiLabelMap.BSCity)}';
	uiLabelMap.BSStateProvince = '${StringUtil.wrapString(uiLabelMap.BSStateProvince)}';
	uiLabelMap.BSCountry = '${StringUtil.wrapString(uiLabelMap.BSCountry)}';
	uiLabelMap.BSCounty = '${StringUtil.wrapString(uiLabelMap.BSCounty)}';
	uiLabelMap.BSWard = '${StringUtil.wrapString(uiLabelMap.BSWard)}';
	uiLabelMap.BSClickToChoose = '${StringUtil.wrapString(uiLabelMap.BSClickToChoose)}';
	uiLabelMap.BSFullName = '${StringUtil.wrapString(uiLabelMap.BSFullName)}';
	uiLabelMap.BSRequired = '${uiLabelMap.BSRequired}';
	uiLabelMap.BSYouNeedChooseCustomerIdBefore = '${uiLabelMap.BSYouNeedChooseCustomerIdBefore}!';
	uiLabelMap.InfoLiabilityOfCustomer = '${uiLabelMap.InfoLiabilityOfCustomer}';
	uiLabelMap.BSPaid = '${uiLabelMap.BSPaid}';
	uiLabelMap.BSMustPay = '${uiLabelMap.BSMustPay}';
	uiLabelMap.BSLiability = '${uiLabelMap.BSLiability}';
	uiLabelMap.BSRefresh = '${StringUtil.wrapString(uiLabelMap.BSRefresh)}';
	uiLabelMap.BSEdit = '${StringUtil.wrapString(uiLabelMap.BSEdit)}';
	uiLabelMap.BSDelete = '${StringUtil.wrapString(uiLabelMap.BSDelete)}';
	uiLabelMap.BSAreYouSureYouWantToDelete = '${StringUtil.wrapString(uiLabelMap.BSAreYouSureYouWantToDelete)}';
	
	var salesExecutiveUrl = "";
	var salesExecutiveIdObj;
	var salesExecutiveGridObj;
	<#if enableSalesExecutive?exists && enableSalesExecutive>
	salesExecutiveUrl = "JQGetSalesExecutiveOrderByCustomer";
	salesExecutiveIdObj = $("#salesExecutiveId");
	salesExecutiveGridObj = $("#salesExecutiveGrid");
	</#if>
	
	$(function(){
		$("#checkoutOrderEntry").on('keypress', function(event) {
	        if (event.keyCode == 13) {
	            event.preventDefault();
	        }
	    });
	});
</script>
<#if isPurchaseSelfie?exists && isPurchaseSelfie>
<script type="text/javascript" src="/salesresources/js/order/orderNewCheckoutOptionSelfie.js?v=0.0.1"></script>
<#else>
<script type="text/javascript" src="/salesresources/js/order/orderNewCheckoutOption.js?v=0.0.1"></script>
</#if>