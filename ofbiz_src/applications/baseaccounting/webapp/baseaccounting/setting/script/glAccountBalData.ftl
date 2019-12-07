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
</script>