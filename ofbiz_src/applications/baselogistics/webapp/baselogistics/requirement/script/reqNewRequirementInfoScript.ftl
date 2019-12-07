<#if security.hasEntityPermission("PARTYDIST", "_VIEW", session)>
	<#assign urlDistributor = "JQGetListDistributor&sD=N" />
</#if>
<script src="/crmresources/js/generalUtils.js"></script>
<style type="text/css">
.destFaContainer {
    display: none;
}
</style>
<script type="text/javascript">
	$.jqx.theme = 'olbius';
	theme = $.jqx.theme;
	
	var localeStr = "VI";
	<#assign localeStr = "VI" />
	<#if locale = "en">
		<#assign localeStr = "EN" />
		localeStr = "EN";
	</#if>
	var requirementTypeId = "${parameters.requirementTypeId?if_exists}";
	<#assign shipmentMethods = delegator.findList("ShipmentMethodType", null, null, null, null, false) />
	var shipmentMethodData = [
	   	<#if shipmentMethods?exists>
	   		<#list shipmentMethods as item>
	   			{
	   				shipmentMethodTypeId: "${item.shipmentMethodTypeId?if_exists}",
	   				description: "${StringUtil.wrapString(item.get('description', locale)?if_exists)}"
	   			},
	   		</#list>
	   	</#if>
	];
	
	<#assign requirementReasons = delegator.findList("RequirementEnumType", null, null, null, null, false) />
	
	<#assign enumTypeIds = []>
	
	<#list requirementReasons as reason>
		<#assign enumTypeIds = enumTypeIds + [reason.enumTypeId?if_exists]>
	</#list>
	
	<#assign reasonEnums = delegator.findList("Enumeration", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("enumTypeId", Static["org.ofbiz.entity.condition.EntityOperator"].IN, enumTypeIds), null, null, null, false)>
	var reasonEnumData = [
  	   	<#if reasonEnums?exists>
  	   		<#list reasonEnums as item>
  	   			{
  	   				enumId: "${item.enumId?if_exists}",
  	   				description: "${StringUtil.wrapString(item.get('description', locale)?if_exists)}"
  	   			},
  	   		</#list>
  	   	</#if>
  	];
	
	<#assign company = Static["com.olbius.basehr.util.PartyUtil"].getCurrentOrganization(delegator, userLogin.userLoginId)/>
	var company = '${company?if_exists}';
	
	<#assign facilities = delegator.findList("Facility", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("facilityTypeId", "WAREHOUSE", "ownerPartyId", company)), null, null, null, false)>
	var originFacilityData = [
	   	<#if facilities?exists>
	   		<#list facilities as item>
	   			{
	   				facilityId: "${item.facilityId?if_exists}",
	   				facilityName: "${StringUtil.wrapString(item.get('facilityName', locale)?if_exists)}"
	   			},
	   		</#list>
	   	</#if>
	];
	
	<#if fromSales?if_exists == "Y">
		<#assign conditions = 
		Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toList(
				Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("requirementTypeId", "RETURN_REQ")),
				Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("requirementTypeId", "TRANSFER_REQUIREMENT"))
		), Static["org.ofbiz.entity.condition.EntityJoinOperator"].OR)/>
		
		<#assign requirementTypes = delegator.findList("RequirementType", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(conditions), null, null, null, false) />
	<#else>
		<#assign conditions = 
		Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toList(
				Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("requirementTypeId", Static["org.ofbiz.entity.condition.EntityOperator"].NOT_EQUAL, "RETURN_REQ"),
				Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("requirementTypeId", Static["org.ofbiz.entity.condition.EntityOperator"].NOT_EQUAL, "PAY_REQUIREMENT")
				Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("requirementTypeId", Static["org.ofbiz.entity.condition.EntityOperator"].NOT_EQUAL, "BORROW_REQUIREMENT")
				Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("requirementTypeId", Static["org.ofbiz.entity.condition.EntityOperator"].NOT_EQUAL, "CHANGEDATE_REQUIREMENT")
				Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("requirementTypeId", Static["org.ofbiz.entity.condition.EntityOperator"].NOT_EQUAL, "TRANSFER_REQUIREMENT")
				Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("parentTypeId", "PRODUCT_REQUIREMENT")
		), Static["org.ofbiz.entity.condition.EntityJoinOperator"].AND)/>
		
		<#assign requirementTypes = delegator.findList("RequirementType", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(conditions), null, null, null, false) />
	</#if>
	
	var requirementTypeData = [
  	   	<#if requirementTypes?exists>
  	   		<#list requirementTypes as item>
  	   			{
  	   				requirementTypeId: "${item.requirementTypeId?if_exists}",
  	   				description: "${StringUtil.wrapString(item.get('description', locale)?if_exists)}"
  	   			},
  	   		</#list>
  	   	</#if>
  	];
	
	if (uiLabelMap == undefined) var uiLabelMap = {};
	uiLabelMap.FieldRequired = "${uiLabelMap.FieldRequired}";
	uiLabelMap.PleaseSelectTitle = "${StringUtil.wrapString(uiLabelMap.PleaseSelectTitle)}";
	uiLabelMap.CannotBeforeNow = "${StringUtil.wrapString(uiLabelMap.CannotBeforeNow)}";
	uiLabelMap.CannotAfterNow = "${StringUtil.wrapString(uiLabelMap.CannotAfterNow)}";
	uiLabelMap.FacilityRequired = "${StringUtil.wrapString(uiLabelMap.FacilityRequired)}";
	uiLabelMap.FacilityFrom = "${StringUtil.wrapString(uiLabelMap.FacilityFrom)}";
	uiLabelMap.FacilityTo = "${StringUtil.wrapString(uiLabelMap.FacilityTo)}";
	uiLabelMap.RequestFromFacility = "${StringUtil.wrapString(uiLabelMap.RequestFromFacility)}";
	uiLabelMap.BLFacilityId = "${StringUtil.wrapString(uiLabelMap.BLFacilityId)}";
	uiLabelMap.BLFacilityName = "${StringUtil.wrapString(uiLabelMap.BLFacilityName)}";
	uiLabelMap.CannotBeforeNow = "${StringUtil.wrapString(uiLabelMap.CannotBeforeNow)}";
	
</script>
<script type="text/javascript" src="/logresources/js/requirement/reqNewRequirementInfo.js?v=1.1.1"></script>
<@jqOlbCoreLib hasCore=false hasValidator=true/>