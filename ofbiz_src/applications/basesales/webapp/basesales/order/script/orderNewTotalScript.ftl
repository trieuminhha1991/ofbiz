<#--<@jqGridMinimumLib />-->
<script type="text/javascript" src="/aceadmin/assets/js/fuelux/fuelux.wizard.min.js"></script>
<script type="text/javascript" src="/aceadmin/assets/js/bootbox.min.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.extend.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.extend.search.remote.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtooltip.js"></script>
<@jqOlbCoreLib hasGrid=true hasDropDownButton=true hasDropDownList=true hasComboBox=true hasValidator=true/>
<script type="text/javascript">
	var isFirstLoad = true;
	var loadedWindowAddShipping = false;
	var shippingAddressDDB;
	var shipToCustomerPartyDDB;
	var salesExecutiveDDB;
	<#if enabledFavorDelivery?exists && enabledFavorDelivery>
	var favorSupplierPartyDDB;
	var shipGroupFacilityDDB;
	</#if>
	var shippingMethodTypeDDL;
	var checkoutPaymentDDL;
	<#if defaultPartyId?exists>
	var customerIdMain = "${defaultPartyId}";
	var customerRowDataMain = {partyId : "${defaultPartyId}", fullName: "${defaultPartyFullName?if_exists}"};
	<#else>
	var customerIdMain = "";
	var customerRowDataMain = {};
	</#if>
	var processSalesOrderUrl = "<#if salesOrderWithoutAcc?exists && salesOrderWithoutAcc>processSalesOrderDisAjax<#else>processSalesOrderAjax</#if>";
	
	if (uiLabelMap == undefined) var uiLabelMap = {};
	uiLabelMap.BSAreYouSureYouWantToCreate = "${uiLabelMap.BSAreYouSureYouWantToCreate}";
	uiLabelMap.wgcancel = "${StringUtil.wrapString(uiLabelMap.wgcancel)}";
	uiLabelMap.wgok = "${StringUtil.wrapString(uiLabelMap.wgok)}";
	uiLabelMap.wgcreatesuccess = "${StringUtil.wrapString(uiLabelMap.wgcreatesuccess)}";
	uiLabelMap.wgupdatesuccess = "${StringUtil.wrapString(uiLabelMap.wgupdatesuccess)}";
	uiLabelMap.BSYouNotYetChooseProduct = "${uiLabelMap.BSYouNotYetChooseProduct}!";
	uiLabelMap.ATP = "${uiLabelMap.ATP}";
	uiLabelMap.QOH = "${uiLabelMap.QOH}";
	uiLabelMap.BSYouNotYetChooseFacility = "${uiLabelMap.BSYouNotYetChooseFacility}";
	
	jOlbUtil.setUiLabelMap("wgupdatesuccess", uiLabelMap.wgupdatesuccess);
	
	var enableFacilityConsign = <#if enableFacilityConsign?exists && enableFacilityConsign>true<#else>false</#if>;
</script>
<#if isPurchaseSelfie?exists && isPurchaseSelfie>
<script type="text/javascript">
	<#if userLogin?exists>
		<#assign defaultParty = delegator.findOne("Party", {"partyId" : userLogin.partyId}, false)!/>
		<#if defaultParty?exists>
			<#assign defaultPartyId = defaultParty.partyId/>
			<#assign defaultPartyCodeId = defaultParty.partyCode/>
		</#if>
	</#if>
	var defaultPartyId = <#if defaultPartyId?exists>'${defaultPartyId}'<#else>null</#if>;
	var defaultPartyCodeId = <#if defaultPartyCodeId?exists>'${defaultPartyCodeId}'<#else>null</#if>;
</script>
<script type="text/javascript" src="/salesresources/js/order/orderNewTotalSelfie.js"></script>
<#else>
<script type="text/javascript" src="/salesresources/js/order/orderNewTotal.js"></script>
</#if>