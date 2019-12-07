<@jqGridMinimumLib />
<@jqOlbCoreLib hasGrid=true />
<script type="text/javascript">
    <#assign localeStr = "VI" />
    var localeStr = "VI";
	<#if locale = "en">
        <#assign localeStr = "EN" />
		localeStr = "EN";
    </#if>
    var listShipmentSelected = [];
    var listShipmentItemSelected = [];
	
	<#assign company = Static["com.olbius.basehr.util.MultiOrganizationUtil"].getCurrentOrganization(delegator, userLogin.get("userLoginId"))!/>;
    var company = '${company?if_exists}';

    if (uiLabelMap == undefined) var uiLabelMap = {};
    uiLabelMap.AreYouSureCreate = "${uiLabelMap.AreYouSureCreate}";
    uiLabelMap.DAYouNotYetChooseOrder = "${uiLabelMap.DAYouNotYetChooseOrder}";
    uiLabelMap.HasErrorWhenProcess = "${uiLabelMap.HasErrorWhenProcess}";
    uiLabelMap.AreYouSureCreate = "${StringUtil.wrapString(uiLabelMap.AreYouSureCreate)}";
    uiLabelMap.YouNotYetChooseProduct = "${StringUtil.wrapString(uiLabelMap.YouNotYetChooseProduct)}";
    uiLabelMap.LogYes = "${StringUtil.wrapString(uiLabelMap.LogYes)}";
    uiLabelMap.LogNO = "${StringUtil.wrapString(uiLabelMap.LogNO)}";
    uiLabelMap.BLHasProductNotEnoughInv = "${StringUtil.wrapString(uiLabelMap.BLHasProductNotEnoughInv)}";

    uiLabelMap.SequenceId = "${StringUtil.wrapString(uiLabelMap.SequenceId)}";
    uiLabelMap.BLFacilityId = "${StringUtil.wrapString(uiLabelMap.BLFacilityId)}";
    uiLabelMap.FacilityName = "${StringUtil.wrapString(uiLabelMap.FacilityName)}";

    uiLabelMap.ProductId = "${StringUtil.wrapString(uiLabelMap.ProductId)}";
    uiLabelMap.ProductName = "${StringUtil.wrapString(uiLabelMap.ProductName)}";
    uiLabelMap.QOH = "${StringUtil.wrapString(uiLabelMap.QOH)}";
    uiLabelMap.Unit = "${StringUtil.wrapString(uiLabelMap.Unit)}";
    uiLabelMap.Quantity = "${StringUtil.wrapString(uiLabelMap.Quantity)}";
    uiLabelMap.BSDiscountinuePurchase = "${StringUtil.wrapString(uiLabelMap.BSDiscountinuePurchase)}";
    uiLabelMap.BSDiscountinueSales = "${StringUtil.wrapString(uiLabelMap.BSDiscountinueSales)}";
    uiLabelMap.UnitPrice = "${StringUtil.wrapString(uiLabelMap.UnitPrice)}";
    uiLabelMap.BSCustomerId = '${StringUtil.wrapString(uiLabelMap.BSCustomerId)}';
    uiLabelMap.BSFullName = '${StringUtil.wrapString(uiLabelMap.BSFullName)}';
    uiLabelMap.BSAddress = '${StringUtil.wrapString(uiLabelMap.BSAddress)}';
    uiLabelMap.BSAverageCost = "${StringUtil.wrapString(uiLabelMap.BSAverageCost)}";
    uiLabelMap.BPOTotal = "${StringUtil.wrapString(uiLabelMap.BPOTotal)}";
    uiLabelMap.Note = "${StringUtil.wrapString(uiLabelMap.Note)}";
    uiLabelMap.OrderId = '${StringUtil.wrapString(uiLabelMap.OrderId)}';
    uiLabelMap.OrderItemsSubTotal = "${StringUtil.wrapString(uiLabelMap.OrderItemsSubTotal)}";
    uiLabelMap.BLTimeDistanceNotValid = "${StringUtil.wrapString(uiLabelMap.BLTimeDistanceNotValid)}";
    uiLabelMap.BLDeliveryClusterId="${StringUtil.wrapString(uiLabelMap.BLDeliveryClusterId)}";
    uiLabelMap.BLDErrorCreateShippingTrip="${StringUtil.wrapString(uiLabelMap.BLDErrorCreateShippingTrip)}";
    var facilityData = new Array();
	<#assign manager = Static["org.ofbiz.base.util.UtilProperties"].getPropertyValue("baselogistics.properties", "roleType.manager")>
	<#assign listRoles = []>
	<#assign listRoles = listRoles + [manager]>
	<#assign listTypes = []>
	<#assign listTypes = listTypes + ["WAREHOUSE"]>
	
	<#assign facis = Static['com.olbius.baselogistics.util.LogisticsPartyUtil'].getFacilityByRolesAndFacilityTypesAndOwner(delegator, userLogin.partyId?if_exists, listRoles, listTypes, company)! />;
	<#list facis as item>
		var row = {};
        <#assign descFac = StringUtil.wrapString(item.get("facilityName",locale)?if_exists)/>
		row['facilityId'] = '${item.facilityId?if_exists}';
		row['description'] = '${descFac?if_exists}';
		facilityData.push(row);
    </#list>
</script>
<script type="text/javascript" src="/aceadmin/assets/js/fuelux/fuelux.wizard.min.js"></script>
<script type="text/javascript" src="/logresources/js/deliveryentry/shippingTripByOrderTemplate.js"></script>