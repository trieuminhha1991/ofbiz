<@jqGridMinimumLib/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtree.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdropdownbutton.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxeditor.js"></script>
<script src="/hrresources/js/CommonFunction.js" type="text/javascript"></script>
<script src="/aceadmin/assets/js/bootbox.min.js" type="text/javascript"></script>
<script src="/aceadmin/assets/js/spin.min.js"></script>
<script type="text/javascript">
<#if !rootOrgList?exists>
	<#assign rootOrgList = Static["com.olbius.basehr.util.PartyUtil"].getListOrgManagedByParty(delegator, userLogin.userLoginId)/>
</#if>
var uiLabelMap = {};
var globalVar = {};

<#if security.hasEntityPermission("HR_RECRUITMENT", "_UPDATE", session)>
	globalVar.createContextMenu = true;
<#else>
	globalVar.createContextMenu= false;
</#if>

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

globalVar.monthNames = ["${StringUtil.wrapString(uiLabelMap.CommonJanuary)}", 
                        "${StringUtil.wrapString(uiLabelMap.CommonFebruary)}", 
                        "${StringUtil.wrapString(uiLabelMap.CommonMarch)}", 
                        "${StringUtil.wrapString(uiLabelMap.CommonApril)}", 
                        "${StringUtil.wrapString(uiLabelMap.CommonMay)}", 
                        "${StringUtil.wrapString(uiLabelMap.CommonJune)}",
                  		"${StringUtil.wrapString(uiLabelMap.CommonJuly)}", 
                  		"${StringUtil.wrapString(uiLabelMap.CommonAugust)}", 
                  		"${StringUtil.wrapString(uiLabelMap.CommonSeptember)}", 
                  		"${StringUtil.wrapString(uiLabelMap.CommonOctober)}", 
                  		"${StringUtil.wrapString(uiLabelMap.CommonNovember)}", 
                  		"${StringUtil.wrapString(uiLabelMap.CommonDecember)}"];

globalVar.emplPositionTypeArr = [
	<#if emplPositionTypeList?has_content>
	<#list emplPositionTypeList as emplPositionType>
	{
		emplPositionTypeId: '${emplPositionType.emplPositionTypeId}',
		description: '${StringUtil.wrapString(emplPositionType.description)}'
	},
	</#list>
	</#if>
];             

<#assign existedTimeYears = delegator.findList("HRPlanningAndCustomTimePeriod", null, null, null, null, false) />
<#assign existedTimeYears = Static["org.ofbiz.entity.util.EntityUtil"].getFieldListFromEntityList(existedTimeYears, "parentPeriodId", true)>
globalVar.customTimePeriodArr = [
	<#if customTimePeriodList?has_content>
		<#list customTimePeriodList as customTimePeriod>
			{
				customTimePeriodId: '${customTimePeriod.customTimePeriodId}',
				periodName: '${customTimePeriod.periodName}'
			},
		</#list>
	</#if>
];
globalVar.statusArr = [
	<#if statusList?has_content>
		<#list statusList as status>
		{
			statusId: '${status.statusId}',
			description: '${StringUtil.wrapString(status.description)}'
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
<#if selectYearCustomTimePeriodId?has_content>
	globalVar.selectYearCustomTimePeriodId = "${selectYearCustomTimePeriodId}";
</#if>
uiLabelMap.ChooseEmplPositionType = "${StringUtil.wrapString(uiLabelMap.ChooseEmplPositionType)}";
uiLabelMap.EmployeeId = "${StringUtil.wrapString(uiLabelMap.EmployeeId)}";
uiLabelMap.EmployeeName = "${StringUtil.wrapString(uiLabelMap.EmployeeName)}";
uiLabelMap.HrCommonPosition = "${StringUtil.wrapString(uiLabelMap.HrCommonPosition)}";
uiLabelMap.FieldRequired = "${StringUtil.wrapString(uiLabelMap.FieldRequired)}";
uiLabelMap.ConfirmCreateHRPlanning = "${StringUtil.wrapString(uiLabelMap.ConfirmCreateHRPlanning)}";
uiLabelMap.NotInputHRPlanning = "${StringUtil.wrapString(uiLabelMap.NotInputHRPlanning)}";
uiLabelMap.CommonClose = "${StringUtil.wrapString(uiLabelMap.CommonClose)}";
uiLabelMap.CommonSubmit = "${StringUtil.wrapString(uiLabelMap.CommonSubmit)}";
uiLabelMap.PleaseSelectOption = "${StringUtil.wrapString(uiLabelMap.PleaseSelectOption)}";
uiLabelMap.ApproveHRPlanningConfirm = "${StringUtil.wrapString(uiLabelMap.ApproveHRPlanningConfirm)}";
uiLabelMap.HRNotCreated = "${StringUtil.wrapString(uiLabelMap.HRNotCreated)}";
uiLabelMap.ValueMustBeGreateThanZero = "${StringUtil.wrapString(uiLabelMap.ValueMustBeGreateThanZero)}";
uiLabelMap.InvalidChar = "${StringUtil.wrapString(uiLabelMap.InvalidChar)}";
uiLabelMap.HasErrorWhenProcess = "${StringUtil.wrapString(uiLabelMap.HasErrorWhenProcess)}";
</script>
