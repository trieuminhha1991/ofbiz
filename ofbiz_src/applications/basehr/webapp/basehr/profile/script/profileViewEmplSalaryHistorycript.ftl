<@jqGridMinimumLib/>
<script src="/aceadmin/assets/js/spin.min.js"></script>
<script src="/aceadmin/assets/js/bootbox.min.js" type="text/javascript"></script>
<script src="/hrresources/js/CommonFunction.js" type="text/javascript"></script>
<script type="text/javascript">
var globalVar = {};
globalVar.periodTypeArr = [
	<#if periodTypeList?has_content>
		<#list periodTypeList as periodType>
		{
			periodTypeId: "${periodType.periodTypeId}",
			description: "${StringUtil.wrapString(periodType.description?if_exists)}",
		},
		</#list>
	</#if>
];
var uiLabelMap = {};
uiLabelMap.NotSetting = "${StringUtil.wrapString(uiLabelMap.NotSetting)}" ;
uiLabelMap.CommonMonth = "${StringUtil.wrapString(uiLabelMap.CommonMonth)}" ;
uiLabelMap.SalaryBaseFlat = "${StringUtil.wrapString(uiLabelMap.SalaryBaseFlat)}";
uiLabelMap.HrCommonFromLowercase = "${StringUtil.wrapString(uiLabelMap.HrCommonFromLowercase)}";
uiLabelMap.CommonToLowercase = "${StringUtil.wrapString(uiLabelMap.CommonToLowercase)}";
uiLabelMap.InsuranceSalaryShort = "${StringUtil.wrapString(uiLabelMap.InsuranceSalaryShort)}";
uiLabelMap.wgemptydatastring = "${StringUtil.wrapString(uiLabelMap.wgemptydatastring)}";
uiLabelMap.HRPayslip = "${StringUtil.wrapString(uiLabelMap.HRPayslip)}";
uiLabelMap.HRCommonNotSetting = "${StringUtil.wrapString(uiLabelMap.HRCommonNotSetting)}";
</script>