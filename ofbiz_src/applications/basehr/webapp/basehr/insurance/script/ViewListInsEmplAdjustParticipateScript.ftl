<@jqGridMinimumLib/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtree.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdropdownbutton.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxsplitter.js"></script>

<script src="/aceadmin/assets/js/spin.min.js"></script>
<script src="/aceadmin/assets/js/bootbox.min.js" type="text/javascript"></script>
<script src="/hrresources/js/CommonFunction.js" type="text/javascript"></script>
<script type="text/javascript">
<#assign nowTimestamp = Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp()/>
<#assign monthStart = Static["org.ofbiz.base.util.UtilDateTime"].getMonthStart(nowTimestamp)/>
<#assign monthEnd = Static["org.ofbiz.base.util.UtilDateTime"].getMonthEnd(prevMonthStart, timeZone, locale)/>

<#if !rootOrgList?exists>
<#assign rootOrgList = Static["com.olbius.basehr.util.PartyUtil"].getListOrgManagedByParty(delegator, userLogin.userLoginId)/>
</#if>
var uiLabelMap = {};

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
   		],
};
globalVar.statusArr = [
             	<#if statusInsuranceList?has_content>
             		<#list statusInsuranceList as status>
             			{
             				statusId: '${status.statusId}',
             				description: '${StringUtil.wrapString(status.description)}'
             			},
             		</#list>
             	</#if>
             ];

globalVar.genderArr = [
	<#if genderList?has_content>
		<#list genderList as gender>
		{
			genderId: '${gender.genderId}',
			description: '${StringUtil.wrapString(gender.get("description", locale))}'
		},
		</#list>
	</#if>
];
globalVar.insuranceTypeArr = [
	<#if insuranceTypeList?has_content>
		<#list insuranceTypeList as insuranceType>
		{
			insuranceTypeId: "${insuranceType.insuranceTypeId}",
			description: "${StringUtil.wrapString(insuranceType.description)}",
			isCompulsory: "${StringUtil.wrapString(insuranceType.isCompulsory)}",
			employeeRate: "${insuranceType.employeeRate * 100}".replace(/,/g,".")
		},
		</#list>
	</#if>
];
globalVar.emplPositionTypeArr = [
	<#if emplPositionTypeList?has_content>
		<#list emplPositionTypeList as emplPositionType>
		{
			emplPositionTypeId: "${emplPositionType.emplPositionTypeId}",
			description: "${StringUtil.wrapString(emplPositionType.description?if_exists)}",
		},
		</#list>
	</#if>                       
];
globalVar.suspendInsReasonTypeArr = [
	<#if suspendInsReasonTypeList?has_content>
		<#list suspendInsReasonTypeList as suspendInsReasonType>
		{
			suspendReasonId: "${suspendInsReasonType.suspendReasonId}",
			isRequestReturnCard: "${StringUtil.wrapString(suspendInsReasonType.isRequestReturnCard?if_exists)}",
			description: "${StringUtil.wrapString(suspendInsReasonType.description?if_exists)}",
		},
		</#list>
	</#if>                       
];
uiLabelMap.CommonMonth = "${StringUtil.wrapString(uiLabelMap.CommonMonth)}";
uiLabelMap.EmplParticipateInsurance = "${StringUtil.wrapString(uiLabelMap.EmplParticipateInsurance)}";
uiLabelMap.EmplSuspendInsurance = "${StringUtil.wrapString(uiLabelMap.EmplSuspendInsurance)}";
uiLabelMap.InsuranceNewlyParticipate = "${StringUtil.wrapString(uiLabelMap.InsuranceNewlyParticipate)}";
uiLabelMap.InsuranceReparticipate = "${StringUtil.wrapString(uiLabelMap.InsuranceReparticipate)}";
uiLabelMap.InsuranceAjustment = "${StringUtil.wrapString(uiLabelMap.InsuranceAjustment)}";
uiLabelMap.InsuranceUnemployment = "${StringUtil.wrapString(uiLabelMap.InsuranceUnemployment)}";
uiLabelMap.SalaryAndJobTitle = "${StringUtil.wrapString(uiLabelMap.SalaryAndJobTitle)}";
uiLabelMap.EmployeeId = "${StringUtil.wrapString(uiLabelMap.EmployeeId)}";
uiLabelMap.EmployeeName = "${StringUtil.wrapString(uiLabelMap.EmployeeName)}";
uiLabelMap.HrCommonPosition = "${StringUtil.wrapString(uiLabelMap.HrCommonPosition)}";
uiLabelMap.CommonDepartment = "${StringUtil.wrapString(uiLabelMap.CommonDepartment)}";
uiLabelMap.DateJoinCompany = "${StringUtil.wrapString(uiLabelMap.DateJoinCompany)}";
uiLabelMap.FieldRequired = "${StringUtil.wrapString(uiLabelMap.FieldRequired)}";
uiLabelMap.ValueMustBeGreateThanZero = "${StringUtil.wrapString(uiLabelMap.ValueMustBeGreateThanZero)}";
uiLabelMap.ParticipateThruGreateThanParticipateFrom = "${StringUtil.wrapString(uiLabelMap.ParticipateThruGreateThanParticipateFrom)}";
uiLabelMap.CreateNewlyParticipateConfirm = "${StringUtil.wrapString(uiLabelMap.CreateNewlyParticipateConfirm)}";
uiLabelMap.CommonSubmit = "${StringUtil.wrapString(uiLabelMap.CommonSubmit)}";
uiLabelMap.CommonCancel = "${StringUtil.wrapString(uiLabelMap.CommonCancel)}";
uiLabelMap.CommonClose = "${StringUtil.wrapString(uiLabelMap.CommonClose)}";
uiLabelMap.SocialInsuranceNbrIdentify = "${StringUtil.wrapString(uiLabelMap.SocialInsuranceNbrIdentify)}";
uiLabelMap.HealthInsuranceNbr = "${StringUtil.wrapString(uiLabelMap.HealthInsuranceNbr)}";
uiLabelMap.TotalInsuranceSocialSalary = "${StringUtil.wrapString(uiLabelMap.TotalInsuranceSocialSalary)}";
uiLabelMap.SuspendThruGreateThanSuspendFrom = "${StringUtil.wrapString(uiLabelMap.SuspendThruGreateThanSuspendFrom)}";
uiLabelMap.ReturnSHIThruGreateThanReturnSHIFrom = "${StringUtil.wrapString(uiLabelMap.ReturnSHIThruGreateThanReturnSHIFrom)}";
uiLabelMap.CreateSuspendOrStopParticipateConfirm = "${StringUtil.wrapString(uiLabelMap.CreateSuspendOrStopParticipateConfirm)}";
uiLabelMap.InsuranceAdjustSuspendStopParticipateEmpl = "${StringUtil.wrapString(uiLabelMap.InsuranceAdjustSuspendStopParticipateEmpl)}";
uiLabelMap.NoPartyChoose = "${StringUtil.wrapString(uiLabelMap.NoPartyChoose)}";
uiLabelMap.InsuranceReparticipateForEmpl = "${StringUtil.wrapString(uiLabelMap.InsuranceReparticipateForEmpl)}";
uiLabelMap.CreateReparticipateConfirm = "${StringUtil.wrapString(uiLabelMap.CreateReparticipateConfirm)}";
uiLabelMap.AdjustSalAndJobTitleForEmpl = "${StringUtil.wrapString(uiLabelMap.AdjustSalAndJobTitleForEmpl)}";
uiLabelMap.CreateAdjustEmplSalaryAndJobConfirm = "${StringUtil.wrapString(uiLabelMap.CreateAdjustEmplSalaryAndJobConfirm)}";
uiLabelMap.ValueMustGreaterThanFrom = "${StringUtil.wrapString(uiLabelMap.ValueMustGreaterThanFrom)}";
uiLabelMap.ValueMustBeGreaterThanOldInfoFromDate = "${StringUtil.wrapString(uiLabelMap.ValueMustBeGreaterThanOldInfoFromDate)}";
uiLabelMap.ValueMustBeGreaterThanOldInfoThruDate = "${StringUtil.wrapString(uiLabelMap.ValueMustBeGreaterThanOldInfoThruDate)}";
uiLabelMap.NotSetting = "${StringUtil.wrapString(uiLabelMap.NotSetting)}";
uiLabelMap.ThruDateMustBeAfterFromDate = "${StringUtil.wrapString(uiLabelMap.ThruDateMustBeAfterFromDate)}";
uiLabelMap.UpdatePartyHealthInsuranceConfirm = "${StringUtil.wrapString(uiLabelMap.UpdatePartyHealthInsuranceConfirm)}";
uiLabelMap.InsuranceHealthFromDateNewMustGreaterThanThruDateOld = "${StringUtil.wrapString(uiLabelMap.InsuranceHealthFromDateNewMustGreaterThanThruDateOld)}";

var globalObject = (function(){
	<#assign defaultSuffix = ""/>
	${setContextField("defaultSuffix", defaultSuffix)}
	<#include "component://basehr/webapp/basehr/common/createJqxTree.ftl"/>
	return{
		createJqxTreeDropDownBtn: createJqxTreeDropDownBtn${defaultSuffix?if_exists}
	}
}());

</script>