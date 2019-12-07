<@jqGridMinimumLib/>
<script src="/aceadmin/assets/js/bootbox.min.js" type="text/javascript"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtree.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxsplitter.js"></script>	
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxmaskedinput.js"></script>
<script src="/aceadmin/jqw/jqwidgets/jqxtooltip.js" type="text/javascript"></script>
<script src="/hrresources/js/CommonFunction.js" type="text/javascript"></script>
<script type="text/javascript">
<#assign nowTimestamp = Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp()/>
<#assign startYear = Static["org.ofbiz.base.util.UtilDateTime"].getYearStart(nowTimestamp)/>
<#assign endYear = Static["org.ofbiz.base.util.UtilDateTime"].getYearEnd(nowTimestamp, timeZone, locale)/>
<#if !rootOrgList?exists>
<#assign rootOrgList = Static["com.olbius.basehr.util.PartyUtil"].getListOrgManagedByParty(delegator, userLogin.userLoginId)/>
</#if>
var ONE_DAY = 1000 * 60 * 60 * 24;
var insuranceAllowanceBenefitTypeArr = [
	<#if insuranceAllowanceBenefitTypeList?has_content>
		<#list insuranceAllowanceBenefitTypeList as insuranceAllowanceBenefitType>
			{
				benefitTypeId: '${insuranceAllowanceBenefitType.benefitTypeId}',
				description: '${StringUtil.wrapString(insuranceAllowanceBenefitType.description?if_exists)}',
				code: '${insuranceAllowanceBenefitType.benefitTypeCode?if_exists}',
				name: '${StringUtil.wrapString(insuranceAllowanceBenefitType.description?if_exists)}',
				<#if insuranceAllowanceBenefitType.emplLeaveReasonTypeId?exists>
				emplLeaveReasonTypeId: '${insuranceAllowanceBenefitType.emplLeaveReasonTypeId}'	
				</#if>
			},
		</#list>
	</#if>
];

<#assign startDate = Static["org.ofbiz.base.util.UtilDateTime"].getDayStart(nowTimestamp) />
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

var globalVar = {
		nowTimestamp: ${nowTimestamp.getTime()},
		startDate: ${startDate.getTime()},
		startYear: ${startYear.getTime()},
		endYear: ${endYear.getTime()},
		<#if selectYearCustomTimePeriodId?exists>
		selectYearCustomTimePeriodId: "${selectYearCustomTimePeriodId}",
		</#if>
		<#if expandedList?has_content>
			<#assign expandTreeId=expandedList[0]>
			expandTreeId: "${expandTreeId}",
		</#if>
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
   		insuranceContentTypeArr: [
			<#if insuranceContentTypeList?has_content>
	   			<#list insuranceContentTypeList as insuranceContentType>
	   			{
	   				insuranceContentTypeId: "${insuranceContentType.insuranceContentTypeId}",
	   				description: "${StringUtil.wrapString(insuranceContentType.contentTypeName)}: ${StringUtil.wrapString(insuranceContentType.description)}"
	   			},
	   			</#list>
			</#if>
   		]
};

globalVar.emplLeaveReasonArr = [
	<#if emplLeaveReasonList?has_content>
		<#list emplLeaveReasonList as emplLeaveReason>
			{
				emplLeaveReasonTypeId: '${emplLeaveReason.emplLeaveReasonTypeId}',
				description: "${StringUtil.wrapString(emplLeaveReason.description)}",
				<#if emplLeaveReason.rateBenefit?exists>
					<#assign rateBenefit = emplLeaveReason.rateBenefit * 100/>  
					rateBenefit: '${rateBenefit}'
				</#if>
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

var uiLabelMap = {};
uiLabelMap.FieldRequired = "${StringUtil.wrapString(uiLabelMap.FieldRequired)}";
uiLabelMap.CommonSubmit = "${StringUtil.wrapString(uiLabelMap.CommonSubmit)}";
uiLabelMap.CommonClose = "${StringUtil.wrapString(uiLabelMap.CommonClose)}";
uiLabelMap.CommonCancel = "${StringUtil.wrapString(uiLabelMap.CommonCancel)}";
uiLabelMap.CommonAnd = "${StringUtil.wrapString(uiLabelMap.CommonAnd)}";
uiLabelMap.ValueNotLessThanZero = "${StringUtil.wrapString(uiLabelMap.ValueNotLessThanZero)}";
uiLabelMap.ThruDateMustGreaterThanFromDate = "${StringUtil.wrapString(uiLabelMap.ThruDateMustGreaterThanFromDate)}";
uiLabelMap.EmployeeId = "${StringUtil.wrapString(uiLabelMap.EmployeeId)}";
uiLabelMap.EmployeeName = "${StringUtil.wrapString(uiLabelMap.EmployeeName)}";
uiLabelMap.HrCommonPosition = "${StringUtil.wrapString(uiLabelMap.HrCommonPosition)}";
uiLabelMap.CommonDepartment = "${StringUtil.wrapString(uiLabelMap.CommonDepartment)}";
uiLabelMap.HRCommonEnter = "${StringUtil.wrapString(uiLabelMap.HRCommonEnter)}";
uiLabelMap.ValueIsInvalid = "${StringUtil.wrapString(uiLabelMap.ValueIsInvalid)}";
uiLabelMap.ErrorWhenRetrieveData = "${StringUtil.wrapString(uiLabelMap.ErrorWhenRetrieveData)}";
uiLabelMap.InsuranceConfirmAddEmpl = "${StringUtil.wrapString(uiLabelMap.InsuranceConfirmAddEmpl)}";
uiLabelMap.CreateNewInsuranceDeclaration = "${StringUtil.wrapString(uiLabelMap.CreateNewInsuranceDeclaration)}";
uiLabelMap.InsuranceParticipatePeriodNotes = "${StringUtil.wrapString(uiLabelMap.InsuranceParticipatePeriodNotes)}";
uiLabelMap.HRCommonYearMonth = "${StringUtil.wrapString(uiLabelMap.HRCommonYearMonth)}";
uiLabelMap.NoPartyChoose = "${StringUtil.wrapString(uiLabelMap.NoPartyChoose)}";
uiLabelMap.CommonReason = "${StringUtil.wrapString(uiLabelMap.CommonReason)}";
uiLabelMap.HRNumberDayLeave = "${StringUtil.wrapString(uiLabelMap.HRNumberDayLeave)}";
uiLabelMap.CommonFromDate = "${StringUtil.wrapString(uiLabelMap.CommonFromDate)}";
uiLabelMap.CommonThruDate = "${StringUtil.wrapString(uiLabelMap.CommonThruDate)}";
uiLabelMap.AddEmplLeaveToInsAllowancePaymentDecl = "${StringUtil.wrapString(uiLabelMap.AddEmplLeaveToInsAllowancePaymentDecl)}";
uiLabelMap.InsuranceStatusCondBenefitShort = "${StringUtil.wrapString(uiLabelMap.InsuranceStatusCondBenefitShort)}";
uiLabelMap.InsuranceTimeCondBenefitShort = "${StringUtil.wrapString(uiLabelMap.InsuranceTimeCondBenefitShort)}";
uiLabelMap.InsuranceBenefitCondition = "${StringUtil.wrapString(uiLabelMap.InsuranceBenefitCondition)}";
uiLabelMap.InsuranceBenefitType = "${StringUtil.wrapString(uiLabelMap.InsuranceBenefitType)}";
uiLabelMap.DateAccumulatedLeaveYTD = "${StringUtil.wrapString(uiLabelMap.DateAccumulatedLeaveYTD)}";
uiLabelMap.InsuranceBenefitTypeFull = "${StringUtil.wrapString(uiLabelMap.InsuranceBenefitTypeFull)}";
uiLabelMap.HRNoRowSelect = "${StringUtil.wrapString(uiLabelMap.HRNoRowSelect)}";
uiLabelMap.wgdeleteconfirm = "${StringUtil.wrapString(uiLabelMap.wgdeleteconfirm)}";
uiLabelMap.InsDayLeaveInPeriod = "${StringUtil.wrapString(uiLabelMap.InsDayLeaveInPeriod)}";
uiLabelMap.NumberOfProposal = "${StringUtil.wrapString(uiLabelMap.NumberOfProposal)}";
uiLabelMap.DayLeaveConcentrate = "${StringUtil.wrapString(uiLabelMap.DayLeaveConcentrate)}";
uiLabelMap.DayLeaveFamily = "${StringUtil.wrapString(uiLabelMap.DayLeaveFamily)}";
uiLabelMap.HRCommonAmount = "${StringUtil.wrapString(uiLabelMap.HRCommonAmount)}";
uiLabelMap.ParticipateFrom = "${StringUtil.wrapString(uiLabelMap.ParticipateFrom)}";
uiLabelMap.InsuranceParticipatePeriod = "${StringUtil.wrapString(uiLabelMap.InsuranceParticipatePeriod)}";
uiLabelMap.BenefitTypeIsNotSelected = "${StringUtil.wrapString(uiLabelMap.BenefitTypeIsNotSelected)}";
uiLabelMap.HRCommonYearLowercase = "${StringUtil.wrapString(uiLabelMap.HRCommonYearLowercase)}";
uiLabelMap.HRCommonMonthLowercase = "${StringUtil.wrapString(uiLabelMap.HRCommonMonthLowercase)}";
</script>
