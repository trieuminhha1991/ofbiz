<@jqGridMinimumLib/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script src="/aceadmin/jqw/jqwidgets/jqxcheckbox.js" type="text/javascript"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdropdownbutton.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtree.js"></script>
<script src="/aceadmin/jqw/jqwidgets/jqxdatatable.js" type="text/javascript"></script>
<script src="/aceadmin/jqw/jqwidgets/jqxtreegrid.js" type="text/javascript"></script>
<script src="/aceadmin/jqw/jqwidgets/jqxmaskedinput.js" type="text/javascript"></script>
<script src="/hrresources/js/CommonFunction.js" type="text/javascript"></script>
<script src="/aceadmin/assets/js/bootbox.min.js" type="text/javascript"></script>

<script type="text/javascript">
<#if !rootOrgList?exists>
<#assign rootOrgList = Static["com.olbius.basehr.util.PartyUtil"].getListOrgManagedByParty(delegator, userLogin.userLoginId)/>
</#if>

<#assign rootOrgId = Static["com.olbius.basehr.util.PartyUtil"].getCurrentOrganization(delegator, userLogin.userLoginId)/>
<#assign orgRootParty = delegator.findOne("PartyGroup", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", rootOrgId), false)/>
<#assign nowTimestamp = Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp() />
<#assign startDate = Static["org.ofbiz.base.util.UtilDateTime"].getDayStart(nowTimestamp) />
var weekday = new Array(7);
weekday[0]=  "${uiLabelMap.CommonSundayShort}";
weekday[1] = "${uiLabelMap.CommonMondayShort}";
weekday[2] = "${uiLabelMap.CommonTuesdayShort}";
weekday[3] = "${uiLabelMap.CommonWednesdayShort}";
weekday[4] = "${uiLabelMap.CommonThursdayShort}";
weekday[5] = "${uiLabelMap.CommonFridayShort}";
weekday[6] = "${uiLabelMap.CommonSaturdayShort}";


var globalVar = {
		rootPartyArr: [
   			<#if rootOrgList?has_content>
   				<#list rootOrgList as rootOrgId>
   				<#assign rootOrg = delegator.findOne("PartyGroup", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", rootOrgId), false)/>
   				{
   					partyId: "${rootOrgId}",
   					partyName: "${StringUtil.wrapString(rootOrg.groupName)}"
   				},
   				</#list>
   			</#if>
   		],
   		rootOrgId: "${rootOrgId}",
		groupName: "${StringUtil.wrapString(orgRootParty.groupName)}",
		startDate: ${startDate.getTime()},
		<#if selectYearCustomTimePeriodId?exists>
		selectYearCustomTimePeriodId: "${selectYearCustomTimePeriodId}"
		</#if>
};

var globalObject = (function(){
	<#assign defaultSuffix = ""/>
	${setContextField("defaultSuffix", defaultSuffix)}
	<#include "component://basehr/webapp/basehr/common/createJqxTree.ftl"/>
	return{
		createJqxTreeDropDownBtn: createJqxTreeDropDownBtn${defaultSuffix?if_exists}
	}
}());
var statusArray = [
	<#if statusList?has_content>
		<#list statusList as status>
			{
				statusId:"${status.statusId}",
				description: "${StringUtil.wrapString(status.description)}"
			},
		</#list>
	</#if>	
];

var emplTimekeepingSignArr = [
	<#if listEmplTimekeepingSign?has_content>
		<#list listEmplTimekeepingSign as emplTimekeepingSign>
			{
				emplTimekeepingSignId: "${emplTimekeepingSign.emplTimekeepingSignId}",
				description: "${StringUtil.wrapString(emplTimekeepingSign.description?if_exists)}",
				sign: "${StringUtil.wrapString(emplTimekeepingSign.sign)}"
			},
		</#list>
	</#if>                           
];

var emplPosTypeTimeSheetOverview = [
		<#list emplPosType as posType>
			{
				emplPositionTypeId: "${posType.emplPositionTypeId}",
				description: "${posType.description?if_exists}"
			},
		</#list>		                                    
];

var yearCustomTimePeriod = [
	<#if customTimePeriodYear?has_content>
		<#list customTimePeriodYear as customTimePeriod>
			{
				customTimePeriodId: "${customTimePeriod.customTimePeriodId}",
				periodTypeId: "${customTimePeriod.periodTypeId}",
				periodName: "${StringUtil.wrapString(customTimePeriod.periodName)}",
				fromDate: ${customTimePeriod.fromDate.getTime()},
				thruDate: ${customTimePeriod.thruDate.getTime()}
			},
		</#list>
	</#if>
];

var uiLabelMap = {};
uiLabelMap.CreateEmpTimesheetRemind = "${StringUtil.wrapString(uiLabelMap.CreateEmpTimesheetRemind)}";
uiLabelMap.CommonSubmit = "${StringUtil.wrapString(uiLabelMap.CommonSubmit)}";
uiLabelMap.CommonCancel = "${StringUtil.wrapString(uiLabelMap.CommonCancel)}";
uiLabelMap.TimesheetNotImportData = "${StringUtil.wrapString(uiLabelMap.TimesheetNotImportData)}";
uiLabelMap.FieldRequired = "${StringUtil.wrapString(uiLabelMap.FieldRequired)}";
uiLabelMap.EmplTimesheetIsCalculating = "${StringUtil.wrapString(uiLabelMap.EmplTimesheetIsCalculating)}";
uiLabelMap.EmployeeId = "${StringUtil.wrapString(uiLabelMap.EmployeeId)}";
uiLabelMap.EmployeeName = "${StringUtil.wrapString(uiLabelMap.EmployeeName)}";
uiLabelMap.NotifyDelete = "${StringUtil.wrapString(uiLabelMap.NotifyDelete)}";
uiLabelMap.OvertimeRegister = "${StringUtil.wrapString(uiLabelMap.OvertimeRegister)}";
uiLabelMap.OvertimeActual = "${StringUtil.wrapString(uiLabelMap.OvertimeActual)}";
uiLabelMap.TotalWorkingLateMinutes = "${StringUtil.wrapString(uiLabelMap.TotalWorkingLateMinutes)}";
uiLabelMap.TotalDayLeave = "${StringUtil.wrapString(uiLabelMap.TotalDayLeave)}";
uiLabelMap.TotalDayPaidLeave = "${StringUtil.wrapString(uiLabelMap.TotalDayPaidLeave)}";
uiLabelMap.TotalTimeKeeping = "${StringUtil.wrapString(uiLabelMap.TotalTimeKeeping)}";
uiLabelMap.CommonHoursNumber = "${StringUtil.wrapString(uiLabelMap.CommonHoursNumber)}";
uiLabelMap.OnlyInputNumberGreaterThanZero = "${StringUtil.wrapString(uiLabelMap.OnlyInputNumberGreaterThanZero)}";
uiLabelMap.Workday = "${StringUtil.wrapString(uiLabelMap.Workday)}";
uiLabelMap.EmplTimekeepingSign = "${StringUtil.wrapString(uiLabelMap.EmplTimekeepingSign)}";
uiLabelMap.CommonDescription = "${StringUtil.wrapString(uiLabelMap.CommonDescription)}";
uiLabelMap.CommonUpdate = "${StringUtil.wrapString(uiLabelMap.CommonUpdate)}";
uiLabelMap.EmplTimesheetInDay = "${StringUtil.wrapString(uiLabelMap.EmplTimesheetInDay)}";
uiLabelMap.IllegalCharacters = "${StringUtil.wrapString(uiLabelMap.IllegalCharacters)}";
uiLabelMap.HREmplFromPositionType = "${StringUtil.wrapString(uiLabelMap.HREmplFromPositionType)}";

function functionAfterRowComplete(){
	$('#jqxgrid').jqxGrid({'disabled': false});
	$('#jqxgrid').jqxGrid('hideloadelement');
	$("#alterSave").removeAttr("disabled");
}

</script>