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
uiLabelMap.CommonSubmit = '${StringUtil.wrapString(uiLabelMap.CommonSubmit?default(''))}';
uiLabelMap.CommonClose = '${StringUtil.wrapString(uiLabelMap.CommonClose?default(''))}';
uiLabelMap.ApprovalEmplLeaveConfirm = '${StringUtil.wrapString(uiLabelMap.ApprovalEmplLeaveConfirm?default(''))}';
uiLabelMap.ChangeStatusEmplLeaveAfterApporveConfirm = '${StringUtil.wrapString(uiLabelMap.ChangeStatusEmplLeaveAfterApporveConfirm?default(''))}';
uiLabelMap.PleaseSelectOption = '${StringUtil.wrapString(uiLabelMap.PleaseSelectOption?default(''))}';
</script>