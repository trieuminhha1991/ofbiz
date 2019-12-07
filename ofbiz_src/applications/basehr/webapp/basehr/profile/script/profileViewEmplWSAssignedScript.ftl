<script src="/aceadmin/assets/js/fullcalendar.min.js"></script>
<script src="/aceadmin/assets/js/bootbox.min.js"></script>
<link rel="stylesheet" href="/aceadmin/assets/css/fullcalendar.css" />
<#assign nowTimestamp = Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp()/>
<script type="text/javascript">
var globalVar = {
		nowTimestamp: ${nowTimestamp.getTime()}
};

var uiLabelMap = {
		CommonToday: "${StringUtil.wrapString(uiLabelMap.CommonToday)}",
		DateHoliday: '${StringUtil.wrapString(uiLabelMap.DateHoliday)}',
		CommonSunday: '${StringUtil.wrapString(uiLabelMap.CommonSunday)}',
		CommonMonday: '${StringUtil.wrapString(uiLabelMap.CommonMonday)}',
		CommonTuesday: '${StringUtil.wrapString(uiLabelMap.CommonTuesday)}',
		CommonWednesday: '${StringUtil.wrapString(uiLabelMap.CommonWednesday)}',
		CommonThursday: '${StringUtil.wrapString(uiLabelMap.CommonThursday)}',
		CommonFriday: '${StringUtil.wrapString(uiLabelMap.CommonFriday)}',
		CommonSaturday: '${StringUtil.wrapString(uiLabelMap.CommonSaturday)}',
		CommonSundayShort: '${StringUtil.wrapString(uiLabelMap.CommonSundayShort)}',
		CommonMondayShort: '${StringUtil.wrapString(uiLabelMap.CommonMondayShort)}',
		CommonWednesdayShort: '${StringUtil.wrapString(uiLabelMap.CommonWednesdayShort)}',
		CommonTuesdayShort: '${StringUtil.wrapString(uiLabelMap.CommonTuesdayShort)}',
		CommonThursdayShort: '${StringUtil.wrapString(uiLabelMap.CommonThursdayShort)}',
		CommonFridayShort: '${StringUtil.wrapString(uiLabelMap.CommonFridayShort)}',
		CommonSaturdayShort: '${StringUtil.wrapString(uiLabelMap.CommonSaturdayShort)}',
		wgjanuary: '${StringUtil.wrapString(uiLabelMap.wgjanuary)}',
		wgfebruary: '${StringUtil.wrapString(uiLabelMap.wgfebruary)}',
		wgmarch: '${StringUtil.wrapString(uiLabelMap.wgmarch)}',
		wgapril: '${StringUtil.wrapString(uiLabelMap.wgapril)}',
		wgmay: '${StringUtil.wrapString(uiLabelMap.wgmay)}',
		wgjune: '${StringUtil.wrapString(uiLabelMap.wgjune)}',
		wgjuly: '${StringUtil.wrapString(uiLabelMap.wgjuly)}',
		wgaugust: '${StringUtil.wrapString(uiLabelMap.wgaugust)}',
		wgseptember: '${StringUtil.wrapString(uiLabelMap.wgseptember)}',
		wgoctober: '${StringUtil.wrapString(uiLabelMap.wgoctober)}',
		wgnovember: '${StringUtil.wrapString(uiLabelMap.wgnovember)}',
		wgdecember: '${StringUtil.wrapString(uiLabelMap.wgdecember)}',
}
</script>