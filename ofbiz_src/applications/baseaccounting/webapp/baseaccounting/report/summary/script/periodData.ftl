<#--===================================Prepare Data=====================================================-->
<script>
	<#assign customTimePeriodList = delegator.findByAnd("CustomTimePeriodAndType", {"groupPeriodTypeId" : "FISCAL_ACCOUNT", "organizationPartyId" : "${parameters.organizationPartyId}"}, null, false)>
	<#assign glAccount = delegator.findOne("GlAccount", {"glAccountId" : "112"}, true)>
	<#assign cashGlAccount = delegator.findOne("GlAccount", {"glAccountId" : "111"}, true)>
	<#assign receipGlAccount = delegator.findOne("GlAccount", {"glAccountId" : "131"}, true)>
	<#assign liabilityGlAccount = delegator.findOne("GlAccount", {"glAccountId" : "331"}, true)>
	<#assign customTimePeriodDefault = customTimePeriodList.get(0)>
	<#assign listParties = delegator.findList("PartyDetail", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("partyTypeId", Static["org.ofbiz.entity.condition.EntityJoinOperator"].NOT_EQUAL, null), null, null, null, true) />
	<#assign party = listParties.get(0) />
	//Prepare for customTimePeriod
	var customTimePeriods = [
		<#list customTimePeriodList as item>
			{
				<#assign description = StringUtil.wrapString(item.periodName + " [" + item.fromDate + "-" +  item.thruDate + "]" ) />
				customTimePeriodId : '${item.customTimePeriodId}',
				description : '<span>${description}</span>',
			},
		</#list>
	]
	
	<#if (customTimePeriodDefault?has_content)>
		<#assign customTimePeriodDefaultId =  customTimePeriodDefault.customTimePeriodId />
		<#else>
		<#assign customTimePeriodDefaultId = 'null' />
	</#if>
</script>
<#--===================================/Prepare Data=====================================================-->