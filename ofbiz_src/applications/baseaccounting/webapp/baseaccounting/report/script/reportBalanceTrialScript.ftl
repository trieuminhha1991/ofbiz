<#--===================================Prepare Data=====================================================-->
<script>
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