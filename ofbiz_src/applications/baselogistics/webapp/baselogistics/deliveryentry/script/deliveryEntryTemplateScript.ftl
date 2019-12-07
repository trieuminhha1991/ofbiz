<@jqGridMinimumLib />
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
	uiLabelMap.DAYouNotYetChooseProduct = "${uiLabelMap.DAYouNotYetChooseProduct}";
	uiLabelMap.HasErrorWhenProcess = "${uiLabelMap.HasErrorWhenProcess}";
	
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
<script type="text/javascript" src="/logresources/js/deliveryentry/newDeliveryEntryTemplate.js"></script>