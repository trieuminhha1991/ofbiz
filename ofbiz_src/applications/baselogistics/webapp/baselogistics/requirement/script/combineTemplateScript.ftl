<@jqGridMinimumLib />
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
	
	<#if fromSales?if_exists == "Y">
	var fromSales = true;
	<#else>
	var fromSales = false;
	</#if>
	
	var listProductSelected = [];
	<#assign company = Static["com.olbius.basehr.util.PartyUtil"].getCurrentOrganization(delegator, userLogin.userLoginId)/>
	var company = '${company?if_exists}';
	
	var requirementId = '${parameters.requirementId?if_exists}';
	<#assign requirement = delegator.findOne("Requirement", {"requirementId" : parameters.requirementId?if_exists}, false)/>
	var statusId = "${requirement.statusId}"; 
	
	<#assign requirementTypes = delegator.findList("RequirementType", null, null, null, null, false) />
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
	uiLabelMap.AreYouSureCreate = "${uiLabelMap.AreYouSureCreate}";
	uiLabelMap.DAYouNotYetChooseProduct = "${uiLabelMap.DAYouNotYetChooseProduct}";
	uiLabelMap.DetectProductNotHaveExpiredDate = "${uiLabelMap.DetectProductNotHaveExpiredDate}";
	uiLabelMap.AreYouSureExecuted = "${uiLabelMap.AreYouSureExecuted}";
	
</script>
<script type="text/javascript" src="/logresources/js/requirement/combineTemplate.js"></script>