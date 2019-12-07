<@jqGridMinimumLib />
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqx.utils.js"></script>
<script type="text/javascript" src="/aceadmin/assets/js/bootbox.min.js"></script>
<script type="text/javascript" src="/logresources/js/logisticsCommon.js"></script>
<style type="text/css">
.step-pane {
    min-height: 20px !important;
}
</style>
<script type="text/javascript">	
	<#assign localeStr = "VI" />
	var localeStr = "VI";
	<#if locale = "en">
		<#assign localeStr = "EN" />
		localeStr = "EN";
	</#if>
	var listInventorySelected = [];
	var listProductSelected = [];
	var listProductUpdates = [];
	var listProductInitUpdates = [];
	<#assign company = Static["com.olbius.basehr.util.MultiOrganizationUtil"].getCurrentOrganization(delegator, userLogin.get("userLoginId"))!/>;
	var company = '${company?if_exists}';
	var facilityData = [];
	<#assign storekeeper = Static["org.ofbiz.base.util.UtilProperties"].getPropertyValue("baselogistics.properties", "role.storekeeper")>
	<#assign manager = Static["org.ofbiz.base.util.UtilProperties"].getPropertyValue("baselogistics.properties", "roleType.manager")>
	<#assign listRoles = []>
	<#assign listRoles = listRoles + [storekeeper]>
	<#assign listRoles = listRoles + [manager]>
	
	<#assign listFacilities = Static['com.olbius.baselogistics.util.LogisticsPartyUtil'].getFacilityByRoles(delegator, userLogin.partyId?if_exists, listRoles)! />;
	<#list listFacilities as item>
		var row = {};
		row['facilityId'] = "${item.facilityId?if_exists}";
		row['description'] = "${StringUtil.wrapString(item.get('facilityName', locale)?if_exists)}";
		facilityData.push(row);
	</#list>	
	
	var listInventoryItemData = [];
	
	if (uiLabelMap == undefined) var uiLabelMap = {};
	uiLabelMap.AreYouSureCreate = "${StringUtil.wrapString(uiLabelMap.AreYouSureCreate)}";
	uiLabelMap.YouNotYetChooseProduct = "${StringUtil.wrapString(uiLabelMap.YouNotYetChooseProduct)}";
	uiLabelMap.LogYes = "${StringUtil.wrapString(uiLabelMap.LogYes)}";
	uiLabelMap.LogNO = "${StringUtil.wrapString(uiLabelMap.LogNO)}";
	uiLabelMap.QuantityNotEnoghForLost = "${StringUtil.wrapString(uiLabelMap.QuantityNotEnoghForLost)}";
	uiLabelMap.QuantityNotEnoghForUpdate = "${StringUtil.wrapString(uiLabelMap.QuantityNotEnoghForUpdate)}";
	
	uiLabelMap.or = "${StringUtil.wrapString(uiLabelMap.or)}";
	uiLabelMap.NotYetChooseReasonAndQuantityCorresponding = "${StringUtil.wrapString(uiLabelMap.NotYetChooseReasonAndQuantityCorresponding)}";
	
</script>
<script type="text/javascript" src="/logresources/js/util/StringUtil.js"></script>
<script type="text/javascript" src="/logresources/js/util/DateUtil.js"></script>
<script type="text/javascript" src="/logresources/js/inventory/newPhysicalInventoryTemplate.js"></script>