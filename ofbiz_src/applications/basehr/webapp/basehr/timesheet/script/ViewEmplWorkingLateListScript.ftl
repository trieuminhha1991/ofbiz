<@jqGridMinimumLib/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script src="/hrresources/js/CommonFunction.js" type="text/javascript"></script>
<script type="text/javascript">
var statusWorkingLateArr = [
	<#if statusListWorkinglate?exists>
		<#list statusListWorkinglate as status>
			{
				statusId: "${status.statusId}",
				description: "${StringUtil.wrapString(status.description)}"
			},
		</#list>
	</#if>
];
<#assign nowTimestamp = Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp()>
<#assign nextDay = Static["org.ofbiz.base.util.UtilDateTime"].getNextDayStart(nowTimestamp)>
<#if parameters.fromDate?exists>
	<#assign fromDate = parameters.fromDate>
<#else>
	<#assign fromDate = Static["org.ofbiz.base.util.UtilDateTime"].getMonthStart(nowTimestamp)/> 
</#if>
<#if parameters.thruDate?exists>
	<#assign thruDate = parameters.thruDate>
<#else>
	<#assign thruDate = Static["org.ofbiz.base.util.UtilDateTime"].getMonthEnd(nowTimestamp, timeZone, locale)/> 
</#if>
var globalVar = {
	fromDate: ${fromDate.getTime()},
	thruDate: ${thruDate.getTime()},
	nowTimestamp: ${nowTimestamp.getTime()}
};

var uiLabelMap = {};
uiLabelMap.CommonRequired = '${uiLabelMap.CommonRequired}';
uiLabelMap.AmountValueGreaterThanZero = '${uiLabelMap.AmountValueGreaterThanZero}';
uiLabelMap.EmployeeId = '${uiLabelMap.EmployeeId}';
uiLabelMap.EmployeeName = '${uiLabelMap.EmployeeName}';

<#assign listEmplDirectMgr = Static["com.olbius.basehr.util.PartyUtil"].getListEmplMgrByParty(delegator, userLogin.userLoginId, nowTimestamp, nowTimestamp)/>
var emplDataArr = [
	<#if listEmplDirectMgr?has_content>
		<#list listEmplDirectMgr as partyId>
			<#assign partyName = Static["com.olbius.basehr.util.PartyUtil"].getPersonName(delegator, partyId)/>
			{
				partyId: "${partyId}",
				partyName: "${StringUtil.wrapString(partyName)}"
			}
			<#if partyId_has_next>
			,
			</#if>
		</#list>
	</#if>
];
var emplSource = {
		localdata: emplDataArr,
        datatype: "array"
}
var emplJqxComboboxAdapter = new $.jqx.dataAdapter(emplSource);
var contentDropDownButton = [];
var partyIdDropDown = [];

</script>