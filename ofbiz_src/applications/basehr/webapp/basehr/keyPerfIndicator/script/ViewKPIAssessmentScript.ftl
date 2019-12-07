<@jqGridMinimumLib/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxeditor.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtree.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdropdownbutton.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxsplitter.js"></script>
<script src="/hrresources/js/CommonFunction.js" type="text/javascript"></script>
<script src="/aceadmin/assets/js/spin.min.js" type="text/javascript"></script>
<script src="/aceadmin/assets/js/bootbox.min.js" type="text/javascript"></script>
<script type="text/javascript">
var globalVar = {};
<#if !rootOrgList?exists>
<#assign rootOrgList = Static["com.olbius.basehr.util.PartyUtil"].getListOrgManagedByParty(delegator, userLogin.userLoginId)/>
</#if>
<#if security.hasEntityPermission("HR_KPIPERF", "_UPDATE", session)>
globalVar.updatePermission = true;
</#if>
<#if security.hasEntityPermission("HR_KPIPERF", "_DELETE", session)>
globalVar.deletePermission = true;
</#if>
globalVar.periodTypeArr = [
	<#if periodTypeList?has_content>
		<#list periodTypeList as periodType>
		{
			periodTypeId: '${periodType.periodTypeId}',
			description: '${StringUtil.wrapString(periodType.description)}',
			periodLength: ${periodType.periodLength},
			uomId: '${periodType.uomId}'
		},
		</#list>
	</#if>
];
globalVar.kpiPeriodTypeArr = [
	<#if kpiPeriodTypeList?has_content>
		<#list kpiPeriodTypeList as periodType>
		{
			periodTypeId: '${periodType.periodTypeId}',
			description: '${StringUtil.wrapString(periodType.description)}',
			periodLength: ${periodType.periodLength},
			uomId: '${periodType.uomId}'
		},
		</#list>
	</#if>
];
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
globalVar.uomArr = [
	<#if uomList?has_content>
		<#list uomList as uom>
		{
			uomId: '${uom.uomId}',
			abbreviation: '${StringUtil.wrapString(uom.get("abbreviation", locale))}',
			description: '${StringUtil.wrapString(uom.get("description", locale))}'
		},
		</#list>
	</#if>
];
globalVar.kpiAssessmentPeriodArr = [
	<#if kpiAssessmentPeriodUomList?has_content>
		<#list kpiAssessmentPeriodUomList as uom>
		{
			uomId: '${uom.uomId}',
			abbreviation: '${StringUtil.wrapString(uom.get("abbreviation", locale))}',
			description: '${StringUtil.wrapString(uom.get("description", locale))}'
		},
		</#list>
	</#if>
];
globalVar.perfCriteriaRateGradeArr = [
	<#if perfCriteriaRateGradeList?has_content>
		<#list perfCriteriaRateGradeList as perfCriteriaRateGrade>
		{
			perfCriteriaRateGradeId: "${perfCriteriaRateGrade.perfCriteriaRateGradeId}",
			perfCriteriaRateGradeName: "${perfCriteriaRateGrade.perfCriteriaRateGradeName}"
		},
		</#list>
	</#if>
];
globalVar.customTimePeriodArr = [
	<#if customTimePeriodList?has_content>
		<#list customTimePeriodList as customTimePeriod>
		{
			customTimePeriodId: '${customTimePeriod.customTimePeriodId}',
			periodName: '${StringUtil.wrapString(customTimePeriod.periodName)}',
			fromDate: ${customTimePeriod.fromDate.getTime()},
			thruDate: ${customTimePeriod.thruDate.getTime()}
		},
		</#list>
	</#if>
];
globalVar.statusArr = [
	<#if statusList?has_content>
		<#list statusList as status>
		{
			statusId: "${status.statusId}",
			description: "${StringUtil.wrapString(status.description)}"
		},
		</#list>
	</#if>
];
<#if selectYearCustomTimePeriodId?has_content>
	globalVar.selectYearCustomTimePeriodId = "${selectYearCustomTimePeriodId}";
</#if>
var globalObject = (function(){
	<#assign defaultSuffix = ""/>
	${setContextField("defaultSuffix", defaultSuffix)}
	<#include "component://basehr/webapp/basehr/common/createJqxTree.ftl"/>
	return{
		createJqxTreeDropDownBtn: createJqxTreeDropDownBtn${defaultSuffix?if_exists}
	}
}());
var uiLabelMap = {};
uiLabelMap.FieldRequired = '${StringUtil.wrapString(uiLabelMap.FieldRequired)}';
uiLabelMap.CommonSubmit = '${StringUtil.wrapString(uiLabelMap.CommonSubmit)}';
uiLabelMap.CommonClose = '${StringUtil.wrapString(uiLabelMap.CommonClose)}';
uiLabelMap.ConfirmCreatePerfCriteriaAssessment = '${StringUtil.wrapString(uiLabelMap.ConfirmCreatePerfCriteriaAssessment)}';
uiLabelMap.EmployeeName = '${StringUtil.wrapString(uiLabelMap.EmployeeName)}';
uiLabelMap.EmployeeId = '${StringUtil.wrapString(uiLabelMap.EmployeeId)}';
uiLabelMap.KPIRating = '${StringUtil.wrapString(uiLabelMap.KPIRating)}';
uiLabelMap.SalaryRate = '${StringUtil.wrapString(uiLabelMap.SalaryRate)}';
uiLabelMap.AllowanceRate = '${StringUtil.wrapString(uiLabelMap.AllowanceRate)}';
uiLabelMap.HRCommonBonus = '${StringUtil.wrapString(uiLabelMap.HRCommonBonus)}';
uiLabelMap.HRPunishmentAmount = '${StringUtil.wrapString(uiLabelMap.HRPunishmentAmount)}';
uiLabelMap.PaymentPeriod = '${StringUtil.wrapString(uiLabelMap.PaymentPeriod)}';
uiLabelMap.CommonStatus = '${StringUtil.wrapString(uiLabelMap.CommonStatus)}';
uiLabelMap.CommonAddNew = '${StringUtil.wrapString(uiLabelMap.CommonAddNew)}';
uiLabelMap.accRemoveFilter = '${StringUtil.wrapString(uiLabelMap.accRemoveFilter)}';
uiLabelMap.EmplListInPerfCriteriaAssessment = '${StringUtil.wrapString(uiLabelMap.EmplListInPerfCriteriaAssessment)}';
uiLabelMap.HrCommonPosition = '${StringUtil.wrapString(uiLabelMap.HrCommonPosition)}';
uiLabelMap.CommonDepartment = '${StringUtil.wrapString(uiLabelMap.CommonDepartment)}';
uiLabelMap.DateJoinCompany = '${StringUtil.wrapString(uiLabelMap.DateJoinCompany)}';
uiLabelMap.HRCommonKPIName = '${StringUtil.wrapString(uiLabelMap.HRCommonKPIName)}';
uiLabelMap.KPIWeigth = '${StringUtil.wrapString(uiLabelMap.KPIWeigth)}';
uiLabelMap.HRCommonResults = '${StringUtil.wrapString(uiLabelMap.HRCommonResults)}';
uiLabelMap.HREffectiveDate = '${StringUtil.wrapString(uiLabelMap.HREffectiveDate)}';
uiLabelMap.CommonThruDate = '${StringUtil.wrapString(uiLabelMap.CommonThruDate)}';
uiLabelMap.DetailsOfReviewPoint = '${StringUtil.wrapString(uiLabelMap.DetailsOfReviewPoint)}';
uiLabelMap.HRCommonPoint = '${StringUtil.wrapString(uiLabelMap.HRCommonPoint)}';
uiLabelMap.GeneralOfReviewPoint = '${StringUtil.wrapString(uiLabelMap.GeneralOfReviewPoint)}';
uiLabelMap.HRTarget = '${StringUtil.wrapString(uiLabelMap.HRTarget)}';
uiLabelMap.HRCommonActual = '${StringUtil.wrapString(uiLabelMap.HRCommonActual)}';
uiLabelMap.HRFrequency = '${StringUtil.wrapString(uiLabelMap.HRFrequency)}';
uiLabelMap.ValueMustBeGreateThanZero = '${StringUtil.wrapString(uiLabelMap.ValueMustBeGreateThanZero)}';
uiLabelMap.HRContainSpecialSymbol = '${StringUtil.wrapString(uiLabelMap.HRContainSpecialSymbol)}';
uiLabelMap.wgdelete = '${StringUtil.wrapString(uiLabelMap.wgdelete)}';
uiLabelMap.CannotDeleteRow = '${StringUtil.wrapString(uiLabelMap.CannotDeleteRow)}';
uiLabelMap.wgdeleteconfirm = '${StringUtil.wrapString(uiLabelMap.wgdeleteconfirm)}';
uiLabelMap.wgok = '${StringUtil.wrapString(uiLabelMap.wgok)}';
uiLabelMap.wgcancel = '${StringUtil.wrapString(uiLabelMap.wgcancel)}';
</script>