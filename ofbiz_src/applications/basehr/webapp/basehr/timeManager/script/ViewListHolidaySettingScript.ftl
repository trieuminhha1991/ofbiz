<@jqGridMinimumLib/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script src="/hrresources/js/CommonFunction.js" type="text/javascript"></script>
<script type="text/javascript">
<#assign nowTimestamp = Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp()/>
<#assign startDate = Static["org.ofbiz.base.util.UtilDateTime"].getDayStart(nowTimestamp) />
<#assign startYear = Static["org.ofbiz.base.util.UtilDateTime"].getYearStart(nowTimestamp)/>
<#assign endYear = Static["org.ofbiz.base.util.UtilDateTime"].getYearEnd(nowTimestamp, timeZone, locale)/>
var dayInWeekArr = ["${StringUtil.wrapString(uiLabelMap.wgsunday)}",
                    "${StringUtil.wrapString(uiLabelMap.wgmonday)}",
                    "${StringUtil.wrapString(uiLabelMap.wgtuesday)}",
                    "${StringUtil.wrapString(uiLabelMap.wgwednesday)}",
                    "${StringUtil.wrapString(uiLabelMap.wgthursday)}",
                    "${StringUtil.wrapString(uiLabelMap.wgfriday)}",
                    "${StringUtil.wrapString(uiLabelMap.wgsaturday)}"];
var emplTimekeepingSignArr = [
	<#if emplTimekeepingSignList?has_content>
		<#list emplTimekeepingSignList as emplTimekeepingSign>
			{
				emplTimekeepingSignId: '${emplTimekeepingSign.emplTimekeepingSignId}',
				description: '${StringUtil.wrapString(emplTimekeepingSign.description?if_exists)}',
				sign: '${emplTimekeepingSign.sign}'
			},
		</#list>
	</#if>
];   
<#assign cal = Static["java.util.Calendar"].getInstance()/>
var globalVar = {
		nowTimestamp: ${nowTimestamp.getTime()},
		startDate: ${startDate.getTime()},
		startYear: ${startYear.getTime()},
		endYear: ${endYear.getTime()},
		YEAR: ${cal.get(Static["java.util.Calendar"].YEAR)},
		 <#if security.hasEntityPermission("HR_TIMEMGR", "_ADMIN", session)>
		hasPermission: true
		<#else>
		hasPermission: false
		</#if>
};


var uiLabelMap = {};
uiLabelMap.HrCreateNewConfirm = "${StringUtil.wrapString(uiLabelMap.HrCreateNewConfirm)}?";
uiLabelMap.CommonSubmit = "${StringUtil.wrapString(uiLabelMap.CommonSubmit)}";
uiLabelMap.CommonCancel = "${StringUtil.wrapString(uiLabelMap.CommonCancel)}";
uiLabelMap.FieldRequired = "${StringUtil.wrapString(uiLabelMap.FieldRequired)}";
uiLabelMap.HolidayName = "${StringUtil.wrapString(uiLabelMap.HolidayName)}";
uiLabelMap.DateHoliday = "${StringUtil.wrapString(uiLabelMap.DateHoliday)}";
uiLabelMap.HRCommonEmplTimekeepingSignShort = "${StringUtil.wrapString(uiLabelMap.HRCommonEmplTimekeepingSignShort)}";
uiLabelMap.LunarCalendar = "${StringUtil.wrapString(uiLabelMap.LunarCalendar)}";
uiLabelMap.accAddNewRow = "${StringUtil.wrapString(uiLabelMap.accAddNewRow)}";
uiLabelMap.accDeleteSelectedRow = "${StringUtil.wrapString(uiLabelMap.accDeleteSelectedRow)}";
uiLabelMap.AddHolidayInYear = "${StringUtil.wrapString(uiLabelMap.AddHolidayInYear)}";
uiLabelMap.CreateHolidayConfirm = "${StringUtil.wrapString(uiLabelMap.CreateHolidayConfirm)}?";
uiLabelMap.NotifyDelete = "${StringUtil.wrapString(uiLabelMap.NotifyDelete)}?";
uiLabelMap.InvalidChar = "${StringUtil.wrapString(uiLabelMap.InvalidChar)}";
uiLabelMap.wgdelete = "${StringUtil.wrapString(uiLabelMap.wgdelete)}";
uiLabelMap.CannotDeleteRow = "${StringUtil.wrapString(uiLabelMap.CannotDeleteRow)}";
uiLabelMap.wgdeleteconfirm = "${StringUtil.wrapString(uiLabelMap.wgdeleteconfirm)}";
uiLabelMap.wgok = "${StringUtil.wrapString(uiLabelMap.wgok)}";
</script>