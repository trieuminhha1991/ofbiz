<@jqGridMinimumLib />
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqx.utils.js"></script>
<script type="text/javascript" src="/aceadmin/assets/js/bootbox.min.js"></script>
<script type="text/javascript" src="/logresources/js/logisticsCommon.js"></script>
<script type="text/javascript">	
	<#assign localeStr = "VI" />
	var localeStr = "VI";
	<#if locale = "en">
		<#assign localeStr = "EN" />
		localeStr = "EN";
	</#if>
	listInvChanged = [];
	var dataFieldInvItems = ${StringUtil.wrapString(dataFieldInvItems?default("[]"))};
	var columnlistInvItems = [${StringUtil.wrapString(columnlistInvItems?default("[]"))}];
	var listInvSelected = [];
	
	<#assign company = Static["com.olbius.basehr.util.MultiOrganizationUtil"].getCurrentOrganization(delegator, userLogin.get("userLoginId"))!/>;
	var company = '${company?if_exists}';
	<#assign facilities = delegator.findList("Facility", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("facilityTypeId", "WAREHOUSE", "ownerPartyId", company)), null, null, null, false)>
	var facilityData = [];
	<#list facilities as item>
		var row = {};
		<#assign facilityName = StringUtil.wrapString(item.get("facilityName", locale)) />
		row['facilityId'] = "${item.facilityId}";
		row['description'] = "${facilityName?if_exists}";
		facilityData.push(row);
	</#list>
	if (uiLabelMap == undefined) var uiLabelMap = {};
	uiLabelMap.AreYouSureCreate = "${uiLabelMap.AreYouSureCreate}";
	uiLabelMap.YouNotYetChooseProduct = "${uiLabelMap.YouNotYetChooseProduct}";
	
	
</script>
<script type="text/javascript" src="/logresources/js/shipment/newShipmentTemplate.js"></script>