<@jqGridMinimumLib/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxeditor.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtree.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxsplitter.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<script src="/hrresources/js/CommonFunction.js" type="text/javascript"></script>
<script src="/aceadmin/assets/js/spin.min.js"></script>
<script type="text/javascript">
<#assign nowDate = Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp()/>
<#assign startMonth = Static["org.ofbiz.base.util.UtilDateTime"].getMonthStart(nowDate)/>
<#assign endMonth = Static["org.ofbiz.base.util.UtilDateTime"].getMonthEnd(nowDate, timeZone, locale)/>
var globalVar = {};
<#if parameters.fromDate?exists>
	globalVar.fromDate = new Date(${parameters.fromDate});
<#else>
	globalVar.fromDate = new Date(${startMonth.getTime()});
</#if>
<#if parameters.thruDate?exists>
	globalVar.thruDate = new Date(${parameters.thruDate});
<#else>
	globalVar.thruDate = new Date(${endMonth.getTime()});
</#if>
var statusApprPopup = [
	<#if statusListWorkOvertime?exists>
		<#list statusListWorkOvertime as status>
		{
			statusId: "${status.statusId}",
			description: "${StringUtil.wrapString(status.description)}"
		},
		</#list>
	</#if>
];

globalVar.statusWorkOvertimeArr = [
	<#if statusListWorkOvertime?exists>
		<#list statusListWorkOvertime as status>
			{
				statusId: "${status.statusId}",
				description: "${StringUtil.wrapString(status.description)}"
			},
		</#list>
	</#if>
];
globalVar.weekdayEnumArr = [
    {enumId: "_NA_", description: "${StringUtil.wrapString(uiLabelMap.AllDayOfWeek)}"},                        
	<#if weekdayEnumList?has_content>
		<#list weekdayEnumList as weekdayEnum>
		{
			enumId: "${weekdayEnum.enumId}",
			description: '${StringUtil.wrapString(weekdayEnum.get("description", locale))}'
		},
		</#list>
	</#if>
];

var uiLabelMap = {};
uiLabelMap.ApproverWorkingOvertime = "${uiLabelMap.ApproverWorkingOvertime}";
uiLabelMap.CommonFor = "${uiLabelMap.CommonFor}";
uiLabelMap.HRCommonNotSetting = "${uiLabelMap.HRCommonNotSetting}";
uiLabelMap.ApproverWorkingOvertime = "${uiLabelMap.ApproverWorkingOvertime}";
uiLabelMap.PleaseSelectOption = "${uiLabelMap.PleaseSelectOption}";
uiLabelMap.CommonEmployee = '${StringUtil.wrapString(uiLabelMap.CommonEmployee)}';
uiLabelMap.DateJoinCompany = "${StringUtil.wrapString(uiLabelMap.DateJoinCompany)}";
uiLabelMap.HrCommonPosition = "${StringUtil.wrapString(uiLabelMap.HrCommonPosition)}";
uiLabelMap.EmployeeId = "${StringUtil.wrapString(uiLabelMap.EmployeeId)}";
uiLabelMap.EmployeeName = "${StringUtil.wrapString(uiLabelMap.EmployeeName)}";
uiLabelMap.CommonDepartment = "${StringUtil.wrapString(uiLabelMap.EmployeeName)}";
uiLabelMap.FieldRequired = "${StringUtil.wrapString(uiLabelMap.FieldRequired)}";
uiLabelMap.ThruDateMustBeAfterFromDate = "${StringUtil.wrapString(uiLabelMap.ThruDateMustBeAfterFromDate)}";
uiLabelMap.ConfirmWorkingOvertimeRegister = "${StringUtil.wrapString(uiLabelMap.ConfirmWorkingOvertimeRegister)}";
uiLabelMap.CommonClose = "${StringUtil.wrapString(uiLabelMap.CommonClose)}";
uiLabelMap.CommonSubmit = "${StringUtil.wrapString(uiLabelMap.CommonSubmit)}";
</script>