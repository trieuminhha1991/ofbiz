<@jqGridMinimumLib/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdropdownbutton.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxpanel.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtree.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script src="/aceadmin/assets/js/bootbox.min.js" type="text/javascript"></script>
<script src="/hrresources/js/CommonFunction.js" type="text/javascript"></script>
<script type="text/javascript">
var weekday = new Array(7);
weekday[0]=  "${uiLabelMap.CommonSundayShort}";
weekday[1] = "${uiLabelMap.CommonMondayShort}";
weekday[2] = "${uiLabelMap.CommonTuesdayShort}";
weekday[3] = "${uiLabelMap.CommonWednesdayShort}";
weekday[4] = "${uiLabelMap.CommonThursdayShort}";
weekday[5] = "${uiLabelMap.CommonFridayShort}";
weekday[6] = "${uiLabelMap.CommonSaturdayShort}";

<#assign currentYear = currentDateTime.get(Static["java.util.Calendar"].YEAR)>
<#assign currentMonth = currentDateTime.get(Static["java.util.Calendar"].MONTH) + 1>
<#if !rootOrgList?exists>
<#assign rootOrgList = Static["com.olbius.basehr.util.PartyUtil"].getListOrgManagedByParty(delegator, userLogin.userLoginId)/>
</#if>
<#assign workingShiftList = Static["com.olbius.basehr.timekeeping.utils.TimekeepingUtils"].getAllWorkingShift(delegator)/>
<#assign sizeWorkingShift = workingShiftList.size()/> 
<#if (sizeWorkingShift > 0)>
	<#assign startTime = workingShiftList.get(0).get("shiftStartTime") /> 
	<#assign endTime = workingShiftList.get(0).get("shiftEndTime")/>
<#else>
	<#assign startTime = Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp() /> 
	<#assign endTime = Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp() />
</#if>
var globalVar = {
		rootPartyArr: [
		   			<#if rootOrgList?has_content>
		   				<#list rootOrgList as rootOrgId>
		   				<#assign rootOrg = delegator.findOne("PartyGroup", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", rootOrgId), false)/>
		   				{
		   					partyId: "${rootOrgId}",
		   					partyName: "${rootOrg.groupName}"
		   				},
		   				</#list>
		   			</#if>
		   		],
		currentYear: ${currentYear},
		currentMonth: ${currentMonth},
		startTime: ${startTime.getTime()},
		endTime: ${endTime.getTime()},
		<#if security.hasEntityPermission("HR_TIMESHEET", "_DELETE", session)>
		hasPermission: true,
		<#else>
		hasPermission: false,
		</#if>
};

var uiLabelMap = {};
uiLabelMap.SelectCellBeforeDelete = "${StringUtil.wrapString(uiLabelMap.SelectCellBeforeDelete)}";
uiLabelMap.CommonSubmit = "${StringUtil.wrapString(uiLabelMap.CommonSubmit)}";
uiLabelMap.CommonChooseFile = "${StringUtil.wrapString(uiLabelMap.CommonChooseFile)}";
uiLabelMap.FieldRequired = "${StringUtil.wrapString(uiLabelMap.FieldRequired)}";
uiLabelMap.FieldInDatabase = "${StringUtil.wrapString(uiLabelMap.FieldInDatabase)}";
uiLabelMap.FieldInExcelFile = "${StringUtil.wrapString(uiLabelMap.FieldInExcelFile)}";
uiLabelMap.FieldInExcelFile = "${StringUtil.wrapString(uiLabelMap.FieldInExcelFile)}";
uiLabelMap.ValueGreaterThanOne = "${StringUtil.wrapString(uiLabelMap.ValueGreaterThanOne)}";
uiLabelMap.EmployeeId = "${StringUtil.wrapString(uiLabelMap.EmployeeId)}";
uiLabelMap.EmployeeName = "${StringUtil.wrapString(uiLabelMap.EmployeeName)}";
uiLabelMap.DateAttendance = "${StringUtil.wrapString(uiLabelMap.DateAttendance)}";
uiLabelMap.TimesheetTimeIn = "${StringUtil.wrapString(uiLabelMap.TimesheetTimeIn)}";
uiLabelMap.TimesheetTimeOut = "${StringUtil.wrapString(uiLabelMap.TimesheetTimeOut)}";
uiLabelMap.HRCommonInTime = "${StringUtil.wrapString(uiLabelMap.HRCommonInTime)}";
uiLabelMap.HRCommonOutTime = "${StringUtil.wrapString(uiLabelMap.HRCommonOutTime)}";
uiLabelMap.DeleteAllDataExists = "${StringUtil.wrapString(uiLabelMap.DeleteAllDataExists)}";
uiLabelMap.OnlyDeleteDataCoincide = "${StringUtil.wrapString(uiLabelMap.OnlyDeleteDataCoincide)}";


var globalObject = (function(){
	<#assign defaultSuffix = ""/>
	${setContextField("defaultSuffix", defaultSuffix)}
	<#include "component://basehr/webapp/basehr/common/createJqxTree.ftl"/>
	return{
		createJqxTreeDropDownBtn: createJqxTreeDropDownBtn${defaultSuffix?if_exists}
	}
}());

function updateRowCustom(rowid, updateData, commit){
	
};
</script>