<@jqGridMinimumLib/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtree.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdropdownbutton.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script src="/aceadmin/assets/js/bootbox.min.js" type="text/javascript"></script>
<script src="/aceadmin/assets/js/spin.min.js"></script>
<script src="/hrresources/js/CommonFunction.js" type="text/javascript"></script>
<script src="/hrresources/js/shim.js" type="text/javascript"></script>
<script src="/hrresources/js/jszip.js" type="text/javascript"></script>
<script src="/hrresources/js/xlsx.js" type="text/javascript"></script>

<script type="text/javascript" src="/aceadmin/assets/js/fuelux/fuelux.wizard.min.js"></script>
<script type="text/javascript">
<#if !rootOrgList?exists>
	<#assign rootOrgList = Static["com.olbius.basehr.util.PartyUtil"].getListOrgManagedByParty(delegator, userLogin.userLoginId)/>
</#if>
var globalVar = {};
var uiLabelMap = {};
globalVar.rootPartyArr =  [
	<#if rootOrgList?has_content>
		<#list rootOrgList as rootOrgId>
		<#assign rootOrg = delegator.findOne("PartyGroup", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", rootOrgId), false)/>
		{
			partyId: "${rootOrgId}",
			partyName: "${rootOrg.groupName}"
		},
		</#list>
	</#if>
];

globalVar.workingShiftArr = [
	<#if workingShiftList?has_content>
		<#list workingShiftList as workingShift>
			{
				workingShiftId: '${workingShift.workingShiftId}',
				workingShiftName: '${workingShift.shiftStartTime?string["HH:mm"]} - ${workingShift.shiftEndTime?string["HH:mm"]} (${StringUtil.wrapString(workingShift.workingShiftName)})',
			},
		</#list>
	</#if>
];

globalVar.enumArr = [
	<#if enumList?has_content>
		<#list enumList as enumeration>
		{
			enumId: "${enumeration.enumId}",
			description: "${StringUtil.wrapString(enumeration.description)}"
		},
		</#list>
	</#if>
];

var globalObject = (function(){
	<#assign defaultSuffix = ""/>
	${setContextField("defaultSuffix", defaultSuffix)}
	<#include "component://basehr/webapp/basehr/common/createJqxTree.ftl"/>
	return{
		createJqxTreeDropDownBtn: createJqxTreeDropDownBtn${defaultSuffix?if_exists}
	}
}());

uiLabelMap.CommonChooseFile = "${StringUtil.wrapString(uiLabelMap.CommonChooseFile)}";
uiLabelMap.ColumnDataInSystem = "${StringUtil.wrapString(uiLabelMap.ColumnDataInSystem)}";
uiLabelMap.ColumnDataInImportFile = "${StringUtil.wrapString(uiLabelMap.ColumnDataInImportFile)}";
uiLabelMap.EmployeeId = "${StringUtil.wrapString(uiLabelMap.EmployeeId)}";
uiLabelMap.CommonDate = "${StringUtil.wrapString(uiLabelMap.CommonDate)}";
uiLabelMap.JoinColumnDataExcel = "${StringUtil.wrapString(uiLabelMap.JoinColumnDataExcel)}";
uiLabelMap.EmplTimesheetList = "${StringUtil.wrapString(uiLabelMap.EmplTimesheetList)}";
uiLabelMap.CommonFromLowercase = "${StringUtil.wrapString(uiLabelMap.CommonFromLowercase)}";
uiLabelMap.CommonToLowercase = "${StringUtil.wrapString(uiLabelMap.CommonToLowercase)}";
uiLabelMap.FieldRequired = "${StringUtil.wrapString(uiLabelMap.FieldRequired)}";
uiLabelMap.DateNotValid = "${StringUtil.wrapString(uiLabelMap.DateNotValid)}";
uiLabelMap.ValueMustGreaterOrEqualThanFromDate = "${StringUtil.wrapString(uiLabelMap.ValueMustGreaterOrEqualThanFromDate)}";
uiLabelMap.CommonSubmit = "${StringUtil.wrapString(uiLabelMap.CommonSubmit)}";
uiLabelMap.ConfirmCreateEmplTimesheetDetail = "${StringUtil.wrapString(uiLabelMap.ConfirmCreateEmplTimesheetDetail)}";
uiLabelMap.CommonClose = "${StringUtil.wrapString(uiLabelMap.CommonClose)}";
uiLabelMap.ColumnMapAuto = "${StringUtil.wrapString(uiLabelMap.ColumnMapAuto)}";
uiLabelMap.HRCommonReset = "${StringUtil.wrapString(uiLabelMap.HRCommonReset)}";
</script>