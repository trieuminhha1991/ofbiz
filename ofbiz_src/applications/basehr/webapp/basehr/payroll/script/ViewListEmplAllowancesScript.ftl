<@jqGridMinimumLib/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtree.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdropdownbutton.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxsplitter.js"></script>
<script src="/aceadmin/assets/js/bootbox.min.js" type="text/javascript"></script>
<script src="/aceadmin/assets/js/spin.min.js"></script>
<script src="/hrresources/js/CommonFunction.js" type="text/javascript"></script>
<script type="text/javascript">
<#assign allowanceParamPeriodList = Static["com.olbius.basehr.payroll.util.PayrollUtil"].getListPeriodTypeOfAllowance(delegator)/>

var globalVar = {};
var uiLabelMap = {};
uiLabelMap.CommonMonth = "${StringUtil.wrapString(uiLabelMap.CommonMonth)}";
globalVar.allowanceParamPeriodArr = [
	<#if allowanceParamPeriodList?has_content>
		<#list allowanceParamPeriodList as periodTypeId>
		"${periodTypeId}",
		</#list>
	</#if>
];
globalVar.periodTypeArr = [
	<#if periodTypeList?has_content>
		<#list periodTypeList as periodType>
		{
			periodTypeId: '${periodType.periodTypeId}',
			description: '${StringUtil.wrapString(periodType.description)}'
		},
		</#list>
	</#if>                 
];
globalVar.allowancesParamArr = [
	<#if allowancesParamList?has_content>
		<#list allowancesParamList as allowancesParam>
		{
			code: '${allowancesParam.code}',
			name: '${StringUtil.wrapString(allowancesParam.name)}',
			periodTypeId: '${allowancesParam.periodTypeId}'
		},
		</#list>
	</#if>
];
uiLabelMap.DateJoinCompany = "${StringUtil.wrapString(uiLabelMap.DateJoinCompany)}";
uiLabelMap.HrCommonPosition = "${StringUtil.wrapString(uiLabelMap.HrCommonPosition)}";
uiLabelMap.EmployeeId = "${StringUtil.wrapString(uiLabelMap.EmployeeId)}";
uiLabelMap.EmployeeName = "${StringUtil.wrapString(uiLabelMap.EmployeeName)}";
uiLabelMap.CommonDepartment = "${StringUtil.wrapString(uiLabelMap.CommonDepartment)}";
uiLabelMap.FieldRequired = "${StringUtil.wrapString(uiLabelMap.FieldRequired)}";
uiLabelMap.ThruDateMustBeAfterFromDate = "${StringUtil.wrapString(uiLabelMap.ThruDateMustBeAfterFromDate)}";
uiLabelMap.CommonSubmit = "${StringUtil.wrapString(uiLabelMap.CommonSubmit)}";
uiLabelMap.CommonCancel = "${StringUtil.wrapString(uiLabelMap.CommonCancel)}";
uiLabelMap.CommonClose = "${StringUtil.wrapString(uiLabelMap.CommonClose)}";
uiLabelMap.CreateAllowaneForEmplConfirm = "${StringUtil.wrapString(uiLabelMap.CreateAllowaneForEmplConfirm)}";
uiLabelMap.HREmplAllowances = "${StringUtil.wrapString(uiLabelMap.HREmplAllowances)}";
uiLabelMap.CommonPeriodType = "${StringUtil.wrapString(uiLabelMap.CommonPeriodType)}";
uiLabelMap.HRCommonAmount = "${StringUtil.wrapString(uiLabelMap.HRCommonAmount)}";
uiLabelMap.CommonThruDate = "${StringUtil.wrapString(uiLabelMap.CommonThruDate)}";
uiLabelMap.EffectiveFromDate = "${StringUtil.wrapString(uiLabelMap.EffectiveFromDate)}";
uiLabelMap.ViewListAllowanceOfEmpl = "${StringUtil.wrapString(uiLabelMap.ViewListAllowanceOfEmpl)}";
uiLabelMap.EmployeeListSelected = "${StringUtil.wrapString(uiLabelMap.EmployeeListSelected)}";
uiLabelMap.wgdelete = "${StringUtil.wrapString(uiLabelMap.wgdelete)}";
uiLabelMap.EmployeeSelected = "${StringUtil.wrapString(uiLabelMap.EmployeeSelected)}";
uiLabelMap.NoPartyChoose = "${StringUtil.wrapString(uiLabelMap.NoPartyChoose)}";
</script>
