<@jqGridMinimumLib/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtree.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdropdownbutton.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxsplitter.js"></script>

<script src="/aceadmin/assets/js/spin.min.js"></script>
<script src="/hrresources/js/CommonFunction.js" type="text/javascript"></script>
<script type="text/javascript">
var workingShiftArr = [
		<#if workingShiftList?has_content>
			<#list workingShiftList as workingShift>
			{
				workingShiftId: "${workingShift.workingShiftId}",
				workingShiftName: "${StringUtil.wrapString(workingShift.workingShiftName?if_exists)}"
			},	
			</#list>
		</#if>
];
var weekday = new Array(7);
weekday[0]=  "${uiLabelMap.CommonSundayShort}";
weekday[1] = "${uiLabelMap.CommonMondayShort}";
weekday[2] = "${uiLabelMap.CommonTuesdayShort}";
weekday[3] = "${uiLabelMap.CommonWednesdayShort}";
weekday[4] = "${uiLabelMap.CommonThursdayShort}";
weekday[5] = "${uiLabelMap.CommonFridayShort}";
weekday[6] = "${uiLabelMap.CommonSaturdayShort}";

var dayOfWeekKey = ["SUNDAY", "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY"];
var workingShiftWorkType = {};
<#if workingShiftList?has_content>
	<#list workingShiftList as workingShift>
		workingShiftWorkType["${workingShift.workingShiftId}"] = {};
		<#assign startTime = workingShift.shiftStartTime?string["HH:mm"]/>
		<#assign endTime = workingShift.shiftEndTime?string["HH:mm"]/>
		<#if workingShift.shiftBreakStart?exists>
			<#assign shiftBreakStart = workingShift.shiftBreakStart?string["HH:mm"]/>
		<#else>	
			<#assign shiftBreakStart = Static["com.olbius.basehr.util.DateUtil"].getMiddleTimeBetweenTwoTime(workingShift.shiftStartTime, workingShift.shiftEndTime)?string["HH:mm"]/>
		</#if>
		
		<#if workingShift.shiftBreakEnd?exists>
			<#assign shiftBreakEnd = workingShift.shiftBreakEnd?string["HH:mm"]/>
		<#else>	
			<#assign shiftBreakEnd = Static["com.olbius.basehr.util.DateUtil"].getMiddleTimeBetweenTwoTime(workingShift.shiftStartTime, workingShift.shiftEndTime)?string["HH:mm"]/>
		</#if>
		<#assign workingShiftTypeList = delegator.findByAnd("WorkingShiftDayWeek", Static["org.ofbiz.base.util.UtilMisc"].toMap("workingShiftId", workingShift.workingShiftId), null, false)/>
		<#list workingShiftTypeList as workingShiftType>
			<#if workingShiftType.workTypeId == "ALL_SHIFT">
				<#assign str = startTime + " - " + endTime/>
			<#elseif workingShiftType.workTypeId == "FIRST_HALF_SHIFT">
				<#assign str = startTime + " - " + shiftBreakStart/>
			<#elseif workingShiftType.workTypeId == "SECOND_HALF_SHIFT">
				<#assign str = shiftBreakEnd + " - " + endTime/>
			<#elseif workingShiftType.workTypeId == "DAY_OFF">
				<#assign str = StringUtil.wrapString(uiLabelMap.HRCommonDayOff)/>
			</#if>
			workingShiftWorkType["${workingShift.workingShiftId}"]["${workingShiftType.dayOfWeek}"] = "${str}";
		</#list>
	</#list>
</#if>

<#assign nowTimestamp = Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp()/>
<#assign monthStart = Static["org.ofbiz.base.util.UtilDateTime"].getMonthStart(nowTimestamp)/>
<#assign monthEnd = Static["org.ofbiz.base.util.UtilDateTime"].getMonthEnd(prevMonthStart, timeZone, locale)/>

<#assign dayOfWeekNameList = [uiLabelMap.CommonSundayShort, uiLabelMap.CommonMondayShort, uiLabelMap.CommonTuesdayShort, 
							  uiLabelMap.CommonWednesdayShort, uiLabelMap.CommonThursdayShort, 
							  uiLabelMap.CommonFridayShort, uiLabelMap.CommonSaturdayShort]/>
							  
<#assign cal = Static["java.util.Calendar"].getInstance()/>
<#assign CalendarDate =  Static["java.util.Calendar"].DATE/>   					
<#assign CalendarMonth =  Static["java.util.Calendar"].MONTH/>   					
<#assign CalendarYear =  Static["java.util.Calendar"].YEAR/>
<#assign dateOfMonthList = Static["com.olbius.basehr.util.DateUtil"].createDateList(monthStart, monthEnd) />							  

<#if !rootOrgList?exists>
<#assign rootOrgList = Static["com.olbius.basehr.util.PartyUtil"].getListOrgManagedByParty(delegator, userLogin.userLoginId)/>
</#if>

var globalVar = {
	nowTimestamp: ${nowTimestamp.getTime()},
	monthStart: ${monthStart.getTime()},
	monthEnd: ${monthEnd.getTime()},
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
 		]
};

var uiLabelMap = {
		HrCommonPosition: '${StringUtil.wrapString(uiLabelMap.HrCommonPosition)}',
		CommonDepartment: '${StringUtil.wrapString(uiLabelMap.CommonDepartment)}',
		EmployeeName: '${StringUtil.wrapString(uiLabelMap.EmployeeName)}',
		EmployeeName: '${StringUtil.wrapString(uiLabelMap.EmployeeName)}',
		EmployeeId: '${StringUtil.wrapString(uiLabelMap.EmployeeId)}',
		DateJoinCompany: '${StringUtil.wrapString(uiLabelMap.DateJoinCompany)}',
		wgdelete: '${StringUtil.wrapString(uiLabelMap.wgdelete)}',
		EmployeeSelected: '${StringUtil.wrapString(uiLabelMap.EmployeeSelected)}',
		EmployeeListSelected: '${StringUtil.wrapString(uiLabelMap.EmployeeListSelected)}',
		FieldRequired: '${StringUtil.wrapString(uiLabelMap.FieldRequired)}',
		NoPartyChoose: '${StringUtil.wrapString(uiLabelMap.NoPartyChoose)}',
		ThruDateMustBeAfterFromDate: '${StringUtil.wrapString(uiLabelMap.ThruDateMustBeAfterFromDate)}',
		CommonClose: '${StringUtil.wrapString(uiLabelMap.CommonClose)}',
		FromDateLessThanEqualThruDate : '${StringUtil.wrapString(uiLabelMap.FromDateLessThanEqualThruDate)}',
		GTDateFieldRequired : '${StringUtil.wrapString(uiLabelMap.GTDateFieldRequired)}',
};

var globalObject = (function(){
	<#assign defaultSuffix = ""/>
	${setContextField("defaultSuffix", defaultSuffix)}
	<#include "component://basehr/webapp/basehr/common/createJqxTree.ftl"/>
	return{
		createJqxTreeDropDownBtn: createJqxTreeDropDownBtn${defaultSuffix?if_exists}
	}
}());
</script>