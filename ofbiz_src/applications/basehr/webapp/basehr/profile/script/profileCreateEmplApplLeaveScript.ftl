<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxeditor.js"></script>
<script type="text/javascript">
if(typeof(globalVar) == 'undefined'){
	globalVar = {};
}
globalVar.workingShiftArr = [
	<#if workingShiftList?has_content>
		<#list workingShiftList as workingShift>
		{
			workingShiftId: '${workingShift.workingShiftId}',
			workingShiftName: '${StringUtil.wrapString(workingShift.workingShiftName)}',
			shiftStartTime: new Date(${workingShift.shiftStartTime.getTime()}),
			shiftEndTime: new Date(${workingShift.shiftEndTime.getTime()}),
			<#if workingShift.shiftBreakStart?exists>
			shiftBreakStart: new Date(${workingShift.shiftBreakStart.getTime()}),
			<#else>
			shiftBreakStart: getDateBetween(new Date(${workingShift.shiftStartTime.getTime()}), new Date(${workingShift.shiftEndTime.getTime()})),
			</#if>
			<#if workingShift.shiftBreakEnd?exists>
			shiftBreakEnd: new Date(${workingShift.shiftBreakEnd.getTime()}),
			<#else>
			shiftBreakEnd: getDateBetween(new Date(${workingShift.shiftStartTime.getTime()}), new Date(${workingShift.shiftEndTime.getTime()})),
			</#if>
		},
		</#list>
	</#if>
];

<#if leaveFirstHalf?exists>
	globalVar.leaveFirstHalf = "${leaveFirstHalf.leaveTypeId}";
<#else>	
	globalVar.leaveFirstHalf = "";
</#if>
<#if leaveSecondHalf?exists>
	globalVar.leaveSecondHalf = "${leaveSecondHalf.leaveTypeId}";
<#else>
	globalVar.leaveSecondHalf = "";
</#if>

if(typeof(uiLabelMap) == 'undefined'){
	uiLabelMap = {};
}
uiLabelMap.DayOffCantBeBeforeApply = '${StringUtil.wrapString(uiLabelMap.DayOffCantBeBeforeApply?default(''))}';
uiLabelMap.FieldRequired = '${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}';
uiLabelMap.FromDateLessThanEqualThruDate = '${StringUtil.wrapString(uiLabelMap.FromDateLessThanEqualThruDate?default(''))}';
uiLabelMap.TimeChooseIsNotValid = '${StringUtil.wrapString(uiLabelMap.TimeChooseIsNotValid?default(''))}';
uiLabelMap.GTDateFieldRequired = '${StringUtil.wrapString(uiLabelMap.GTDateFieldRequired?default(''))}';
uiLabelMap.HrCreateNewConfirm = '${StringUtil.wrapString(uiLabelMap.HrCreateNewConfirm?default(''))}';
uiLabelMap.CommonSubmit = '${StringUtil.wrapString(uiLabelMap.CommonSubmit?default(''))}';
uiLabelMap.CommonClose = '${StringUtil.wrapString(uiLabelMap.CommonClose?default(''))}';
uiLabelMap.EmployeeId = '${StringUtil.wrapString(uiLabelMap.EmployeeId?default(''))}';
uiLabelMap.EmployeeName = '${StringUtil.wrapString(uiLabelMap.EmployeeName?default(''))}';
uiLabelMap.HrCommonPosition = '${StringUtil.wrapString(uiLabelMap.HrCommonPosition?default(''))}';
uiLabelMap.GTDateSelectExceeded = '${StringUtil.wrapString(uiLabelMap.GTDateSelectExceeded?default(''))}';
uiLabelMap.GTDateSelect = '${StringUtil.wrapString(uiLabelMap.GTDateSelect?default(''))}';
</script>