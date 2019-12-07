<script type="text/javascript">
<#assign nowTimestamp = Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp()/>
<#assign monthStart = Static["org.ofbiz.base.util.UtilDateTime"].getMonthStart(nowTimestamp, 0, -1)/>
<#assign monthEnd = Static["org.ofbiz.base.util.UtilDateTime"].getMonthEnd(monthStart, timeZone, locale)/>
<#assign listPayrollParaTypes = delegator.findList("PayrollEmplParameterType", null, null, null, null, false) />
<#assign paramCharacteristic = delegator.findList("PayrollParamCharacteristic", null, null, null, null, false) />
var paramCharacteristicArr = [
	<#if paramCharacteristic?has_content>
		<#list paramCharacteristic as characteristic>
		{
			paramCharacteristicId: "${characteristic.paramCharacteristicId}",
			description: "${StringUtil.wrapString(characteristic.description)}"
		},
		</#list>
	</#if>                              
]; 

//Prepare data for time period types
<#assign listTimePeriodTypes = delegator.findList("PeriodType", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("groupPeriodTypeId", Static["org.ofbiz.entity.condition.EntityOperator"].EQUALS , "BASIC_PERIOD"), null, null, null, false) />
var timePeriodTypeData = new Array();
<#list listTimePeriodTypes as item>
	var row = {};
	<#assign description = StringUtil.wrapString(item.description?if_exists) />
	row['periodTypeId'] = '${item.periodTypeId}';
	row['description'] = '${description}';
	timePeriodTypeData[${item_index}] = row;
</#list>

var globalVar = {
		monthStart: ${monthStart.getTime()},
		monthEnd: ${monthEnd.getTime()},
		userLogin_partyId: "${userLogin.partyId}"
}
</script>