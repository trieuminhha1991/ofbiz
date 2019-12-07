<script type="text/javascript">
	<#assign requirementId = parameters.requirementId?if_exists/>
	<#assign requirement = delegator.findOne("Requirement", {"requirementId" : requirementId}, false)!/>
	var facilitySelected = null;		
	<#assign managerRole = Static["org.ofbiz.base.util.UtilProperties"].getPropertyValue("baselogistics.properties", "roleType.manager")!>
	<#assign listRoleManages = []>
	<#assign listRoleManages = listRoleManages + [managerRole]>
	<#assign listFacilityManages = Static['com.olbius.baselogistics.util.LogisticsPartyUtil'].getFacilityByRolesAndFacilityType(delegator, userLogin.partyId?if_exists, listRoleManages, "WAREHOUSE")! />;
	<#assign accEmpl = Static['com.olbius.basehr.util.SecurityUtil'].hasRole("ACC_EMPLOYEE", userLogin.getString("partyId"), delegator)!/>
	<#assign checkRole = false>
	<#if accEmpl == true>
		<#assign checkRole = true>
	<#else>
		<#list listFacilityManages as fa>
			<#if fa.facilityId == requirement.facilityId?if_exists || fa.facilityId == requirement.destFacilityId?if_exists>
				<#assign checkRole = true>
				<#break>
			</#if>
		</#list>		
	</#if>
	
	<#if checkRole == false>
		<#if hasOlbPermission("MODULE", "LOG_REQUIREMENT", "ADMIN") && 'BORROW_REQUIREMENT' == requirement.requirementTypeId?if_exists>
			<#assign destFac = delegator.findOne("Facility", {"facilityId" : requirement.destFacilityId?if_exists}, false)/>
			<#if destFac.primaryFacilityGroupId?if_exists == 'FACILITY_CONSIGN'>
				<#assign checkRole = true>
			</#if>
		</#if>
		<#if hasOlbPermission("MODULE", "LOG_REQUIREMENT", "ADMIN") && 'PAY_REQUIREMENT' == requirement.requirementTypeId?if_exists>
			<#assign destFac = delegator.findOne("Facility", {"facilityId" : requirement.facilityId?if_exists}, false)/>
			<#if destFac.primaryFacilityGroupId?if_exists == 'FACILITY_CONSIGN'>
				<#assign checkRole = true>
			</#if>
		</#if>
	</#if>
	
	<#assign organization = Static["com.olbius.basehr.util.MultiOrganizationUtil"].getCurrentOrganization(delegator, userLogin.userLoginId)/>
	
	<#assign requirementAssocs1 = delegator.findList("RequirementItemAssoc", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("requirementId", requirementId)), null, null, null, false) />
	<#assign requirementAssocs2 = delegator.findList("RequirementItemAssoc", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("toRequirementId", requirementId)), null, null, null, false) />
	<#assign requirementIdTos = Static["org.ofbiz.entity.util.EntityUtil"].getFieldListFromEntityList(requirementAssocs1, "toRequirementId", true)>
	<#assign requirementIdFroms = Static["org.ofbiz.entity.util.EntityUtil"].getFieldListFromEntityList(requirementAssocs2, "requirementId", true)>
	
	<#assign doneBefore = true>
	<#if requirementIdFroms?has_content>
		<#list requirementIdFroms as reqId>
			<#assign req = delegator.findOne("Requirement", {"requirementId" : reqId}, false)/>
			<#if req.statusId != "REQ_COMPLETED">
				<#assign doneBefore = false>
				<#break>
			</#if>
		</#list>
	</#if>

	<#assign doneAfter = true>
	<#if requirementIdTos?has_content>
		<#list requirementIdTos as reqId>
			<#assign req = delegator.findOne("Requirement", {"requirementId" : reqId}, false)/>
			<#if req.statusId != "REQ_COMPLETED">
				<#assign doneAfter = false>
				<#break>
			</#if>
		</#list>
	</#if>
	
	<#assign reasonEnumId = requirement.reasonEnumId?if_exists>
	<#assign shipmentTypeEnums = delegator.findList("ShipmentTypeEnumDetail", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("enumId", reasonEnumId)), null, null, null, false) />
	<#assign shipmentTypeEnums = Static["org.ofbiz.entity.util.EntityUtil"].filterByDate(shipmentTypeEnums)>
	
	
	var organization = "${organization?if_exists}";
	var urlGetListFacility = "${urlGetListFacility?if_exists}";
	multiLang = _.extend(multiLang, {
		FacilityId: "${StringUtil.wrapString(uiLabelMap.FacilityId)}",
		FacilityName: "${StringUtil.wrapString(uiLabelMap.FacilityName)}",
		});
		
	if (uiLabelMap == undefined) var uiLabelMap = {};
	uiLabelMap.BLFacilityId = "${StringUtil.wrapString(uiLabelMap.BLFacilityId)}";
	uiLabelMap.BLFacilityName = "${StringUtil.wrapString(uiLabelMap.BLFacilityName)}";
	uiLabelMap.AreYouSureSave = "${StringUtil.wrapString(uiLabelMap.AreYouSureSave)}";
	uiLabelMap.CommonCancel = "${StringUtil.wrapString(uiLabelMap.CommonCancel)}";
	uiLabelMap.OK = "${StringUtil.wrapString(uiLabelMap.OK)}";
	uiLabelMap.SelectFacilityToReceive = "${StringUtil.wrapString(uiLabelMap.SelectFacilityToReceive)}";
	uiLabelMap.SelectFacilityToExport = "${StringUtil.wrapString(uiLabelMap.SelectFacilityToExport)}";
	uiLabelMap.FieldRequired = "${StringUtil.wrapString(uiLabelMap.FieldRequired)}";
	
	
</script>
<script type="text/javascript" src="/logresources/js/requirement/requirementDetailBegin.js?v=1.1.1"></script>