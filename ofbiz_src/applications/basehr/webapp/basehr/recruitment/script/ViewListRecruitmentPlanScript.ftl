<@jqGridMinimumLib/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtree.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdropdownbutton.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxeditor.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxpopover.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtextarea.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxsplitter.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxpasswordinput.js"></script>
<script src="/hrresources/js/CommonFunction.js" type="text/javascript"></script>
<script src="/aceadmin/assets/js/bootbox.min.js" type="text/javascript"></script>
<script src="/aceadmin/assets/js/spin.min.js"></script>
<script type="text/javascript" src="/aceadmin/assets/js/fuelux/fuelux.wizard.min.js"></script>
<script type="text/javascript">
<#if !rootOrgList?exists>
	<#assign rootOrgList = Static["com.olbius.basehr.util.PartyUtil"].getListOrgManagedByParty(delegator, userLogin.userLoginId)/>
</#if>
<#assign nowTimestamp = Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp()/>
<#assign startDate = Static["org.ofbiz.base.util.UtilDateTime"].getDayStart(nowTimestamp)/>
var globalVar = {};
var uiLabelMap = {};
globalVar.startDate = ${startDate.getTime()};
<#if security.hasEntityPermission("HR_RECRUITMENT", "_ADMIN", session)>
	globalVar.hasPermissionAdmin = true;
<#else>	
globalVar.hasPermissionAdmin = false;
</#if>
<#if expandedList?has_content>
<#assign expandTreeId=expandedList[0]>
	if(typeof(globalVar.expandTreeId) == 'undefined'){
		globalVar.expandTreeId = "${expandTreeId}";		
	}
</#if>
<#if defaultCountry?exists>
	globalVar.defaultCountry = "${defaultCountry}";
</#if>
globalVar.emplPositionTypeArr = [
	<#if emplPositionTypeList?has_content>
		<#list emplPositionTypeList as emplPositionType>
		{
			emplPositionTypeId: '${emplPositionType.emplPositionTypeId}',
			description: '${StringUtil.wrapString(emplPositionType.description)}',
		},			
		</#list>
	</#if>
];

globalVar.recruitmentFormTypeArr = [
	<#if recruitmentFormTypeList?has_content>
		<#list recruitmentFormTypeList as recruitmentFormType>
		{
			recruitmentFormTypeId: '${recruitmentFormType.recruitmentFormTypeId}',
			description: '${StringUtil.wrapString(recruitmentFormType.description)}'
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
globalVar.statusCandidateRoundArr = [
	<#if statusCandidateRoundList?has_content>
		<#list statusCandidateRoundList as status>
		{
			statusId: '${status.statusId}',
			description: '${StringUtil.wrapString(status.description)}'
		},
		</#list>
	</#if>
];

globalVar.statusEvaluatedCandidateArr = [
	<#if statusEvaluatedCandidateList?has_content>
		<#list statusEvaluatedCandidateList as status>
		{
			statusId: '${status.statusId}',
			description: '${StringUtil.wrapString(status.description)}'
		},
		</#list>
	</#if>
];

globalVar.recruitmentCostItemArr = [
	<#if recruitmentCostItemList?has_content>
		<#list recruitmentCostItemList as costItem>
		{
			recruitCostItemTypeId: "${costItem.recruitCostItemTypeId}",
			recruitCostItemName: "${StringUtil.wrapString(costItem.recruitCostItemName)}",
			recruitCostCatName: "${StringUtil.wrapString(costItem.recruitCostCatName)}",
			recruitCostCatTypeId: "${StringUtil.wrapString(costItem.recruitCostCatTypeId)}"
		},
		</#list>
	</#if>
];

globalVar.recruitmentCostCatTypeArr = [
	<#if recruitmentCostCatTypeList?has_content>
		<#list recruitmentCostCatTypeList as costCatType>
		{
			recruitCostCatTypeId: "${costCatType.recruitCostCatTypeId}",
			recruitCostCatName: "${StringUtil.wrapString(costCatType.recruitCostCatName)}"
		},			
		</#list>
	</#if>
];

globalVar.recruitmentFormTypeArr = [
	<#if recruitmentFormTypeList?has_content>
		<#list recruitmentFormTypeList as recruitmentFormType>
		{
			recruitmentFormTypeId: '${recruitmentFormType.recruitmentFormTypeId}',
			description: '${StringUtil.wrapString(recruitmentFormType.description)}'
		},
		</#list>
	</#if>
];

<#--/* globalVar.recInputParamArr = [
	<#if recInputParamList?has_content>
		<#list recInputParamList as enumeration>
		{
			enumId: '${enumeration.enumId}',
			description: '${StringUtil.wrapString(enumeration.description)}'
		},
		</#list>
	</#if>
];

globalVar.recOpCondArr = [
	<#if recOpCondList?has_content>
		<#list recOpCondList as enumeration>
		{
			enumId: '${enumeration.enumId}',
			description: '${StringUtil.wrapString(enumeration.description)}'
		},
		</#list>
	</#if>
];

globalVar.recLogicCondArr = [
	<#if recLogicCondList?has_content>
		<#list recLogicCondList as enumeration>
		{
			enumId: '${enumeration.enumId}',
			description: '${StringUtil.wrapString(enumeration.description)}'
		},
		</#list>
	</#if>
];*/-->
globalVar.genderArr = [
	<#if genderList?has_content>
		<#list genderList as gender>
		{
			genderId: '${gender.genderId}',
			description: '${StringUtil.wrapString(gender.description)}'
		},
		</#list>
	</#if>
];

globalVar.educationSystemTypeArr = [
	<#if educationSystemTypeList?has_content>
		<#list educationSystemTypeList as educationSystemType>
		{
			educationSystemTypeId: '${educationSystemType.educationSystemTypeId}',
			description: '${StringUtil.wrapString(educationSystemType.description)}'
		},
		</#list>
	</#if>
];

globalVar.degreeClassTypeArr = [
	<#if degreeClassTypeList?has_content>
		<#list degreeClassTypeList as degreeClassType>
		{
			classificationTypeId: '${degreeClassType.classificationTypeId}',
			description: '${StringUtil.wrapString(degreeClassType.description)}'
		},
		</#list>
	</#if>
]; 
globalVar.recruitmentResultTypeArr = [
	<#if recruitmentResultTypeList?has_content>
		<#list recruitmentResultTypeList as recruitmentResultType>
		{
			resultTypeId: '${recruitmentResultType.resultTypeId}',
			description: '${StringUtil.wrapString(recruitmentResultType.description)}'
		},
		</#list>
	</#if>
];
globalVar.recruitReqEnumArr = [
	<#if recruitReqEnumList?has_content>
		<#list recruitReqEnumList as enumRecruitReq>
		{
			enumId: "${enumRecruitReq.enumId}",
			description: "${StringUtil.wrapString(enumRecruitReq.description)}"
		},
		</#list>
	</#if>                               
];
globalVar.roundTypeEnumArr = [
	<#if roundTypeEnumList?has_content>
		<#list roundTypeEnumList as roundTypeEnum>
		{
			enumId: "${roundTypeEnum.enumId}",
			description: "${StringUtil.wrapString(roundTypeEnum.description)}"
		},
		</#list>
	</#if>                               
];

globalVar.periodTypeArr = [
	<#if periodTypeList?has_content>
		<#list periodTypeList as periodType>
			{
				periodTypeId: '${periodType.periodTypeId}',
				description: '${StringUtil.wrapString(periodType.description)}'
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

uiLabelMap.EmployeeName = "${StringUtil.wrapString(uiLabelMap.EmployeeName)}";
uiLabelMap.EmployeeId = "${StringUtil.wrapString(uiLabelMap.EmployeeId)}";
uiLabelMap.HrCommonPosition = "${StringUtil.wrapString(uiLabelMap.HrCommonPosition)}";
uiLabelMap.CommonDepartment = "${StringUtil.wrapString(uiLabelMap.CommonDepartment)}";
uiLabelMap.DateJoinCompany = "${StringUtil.wrapString(uiLabelMap.DateJoinCompany)}";
uiLabelMap.CommonRole = "${StringUtil.wrapString(uiLabelMap.CommonRole)}";
uiLabelMap.wgcancel = "${StringUtil.wrapString(uiLabelMap.wgcancel)}";
uiLabelMap.wgok = "${StringUtil.wrapString(uiLabelMap.wgok)}";
uiLabelMap.wgdeleteconfirm = "${StringUtil.wrapString(uiLabelMap.wgdeleteconfirm)}";
uiLabelMap.CannotDeleteRow = "${StringUtil.wrapString(uiLabelMap.CannotDeleteRow)}";
uiLabelMap.wgdelete = "${StringUtil.wrapString(uiLabelMap.wgdelete)}";
uiLabelMap.CommonAddNew = "${StringUtil.wrapString(uiLabelMap.CommonAddNew)}";
uiLabelMap.RecruitmentBoardList = "${StringUtil.wrapString(uiLabelMap.RecruitmentBoardList)}";
uiLabelMap.HRFullName = "${StringUtil.wrapString(uiLabelMap.HRFullName)}";
uiLabelMap.RoundOrder = "${StringUtil.wrapString(uiLabelMap.RoundOrder)}";
uiLabelMap.RoundName = "${StringUtil.wrapString(uiLabelMap.RoundName)}";
uiLabelMap.HRNotes = "${StringUtil.wrapString(uiLabelMap.HRNotes)}";
uiLabelMap.RecruitmentRoundList = "${StringUtil.wrapString(uiLabelMap.RecruitmentRoundList)}";
uiLabelMap.HRCommonJobTitle = "${StringUtil.wrapString(uiLabelMap.HRCommonJobTitle)}";
uiLabelMap.FieldRequired = "${StringUtil.wrapString(uiLabelMap.FieldRequired)}";
uiLabelMap.EmployeeAddedToBoard = "${StringUtil.wrapString(uiLabelMap.EmployeeAddedToBoard)}";
uiLabelMap.CommonClose = "${StringUtil.wrapString(uiLabelMap.CommonClose)}";
uiLabelMap.RecruitmentSubjectName = "${StringUtil.wrapString(uiLabelMap.RecruitmentSubjectName)}";
uiLabelMap.HRCommonRatio = "${StringUtil.wrapString(uiLabelMap.HRCommonRatio)}";
uiLabelMap.RecruitmentSubject = "${StringUtil.wrapString(uiLabelMap.RecruitmentSubject)}";
uiLabelMap.CommonName = "${StringUtil.wrapString(uiLabelMap.CommonName)}";
uiLabelMap.accRemoveFilter = "${StringUtil.wrapString(uiLabelMap.accRemoveFilter)}";
uiLabelMap.RecruitmentSubjectList = "${StringUtil.wrapString(uiLabelMap.RecruitmentSubjectList)}";
uiLabelMap.RecruitmentPassedNote = "${StringUtil.wrapString(uiLabelMap.RecruitmentPassedNote)}";
uiLabelMap.RecruitmentPassed = "${StringUtil.wrapString(uiLabelMap.RecruitmentPassed)}";
uiLabelMap.CannotDeleteDefaultRecruitmentRound = "${StringUtil.wrapString(uiLabelMap.CannotDeleteDefaultRecruitmentRound)}";
uiLabelMap.RecruitmentRoundIsCreated = "${StringUtil.wrapString(uiLabelMap.RecruitmentRoundIsCreated)}";
uiLabelMap.HRCommonAmount = "${StringUtil.wrapString(uiLabelMap.HRCommonAmount)}";
uiLabelMap.RecruitmentCostItemName = "${StringUtil.wrapString(uiLabelMap.RecruitmentCostItemName)}";
uiLabelMap.RecruitmentCostCategory = "${StringUtil.wrapString(uiLabelMap.RecruitmentCostCategory)}";
uiLabelMap.RecruitmentCostList = "${StringUtil.wrapString(uiLabelMap.RecruitmentCostList)}";
uiLabelMap.RecruitmentCostCategoryTypeList = "${StringUtil.wrapString(uiLabelMap.RecruitmentCostCategoryTypeList)}";
uiLabelMap.RecruitmentCostReasonList = "${StringUtil.wrapString(uiLabelMap.RecruitmentCostReasonList)}";
uiLabelMap.RecruitmentCostCategoryType = "${StringUtil.wrapString(uiLabelMap.RecruitmentCostCategoryType)}";
uiLabelMap.RecruitmentCostItemIsAdded = "${StringUtil.wrapString(uiLabelMap.RecruitmentCostItemIsAdded)}";
uiLabelMap.AddNewRecruitmentRound = "${StringUtil.wrapString(uiLabelMap.AddNewRecruitmentRound)}";
uiLabelMap.EditRecruitmentRound = "${StringUtil.wrapString(uiLabelMap.EditRecruitmentRound)}";
uiLabelMap.AddRecruitmentCost = "${StringUtil.wrapString(uiLabelMap.AddRecruitmentCost)}";
uiLabelMap.EditRecruitmentCost = "${StringUtil.wrapString(uiLabelMap.EditRecruitmentCost)}";
uiLabelMap.RecruitmentCriteria = "${StringUtil.wrapString(uiLabelMap.RecruitmentCriteria)}"; 
uiLabelMap.RecruitmentRequirementPosition = "${StringUtil.wrapString(uiLabelMap.RecruitmentRequirementPosition)}"; 
uiLabelMap.HRCondition = "${StringUtil.wrapString(uiLabelMap.HRCondition)}"; 
uiLabelMap.ValueMustBeGreateThanZero = "${StringUtil.wrapString(uiLabelMap.ValueMustBeGreateThanZero)}"; 
uiLabelMap.ConfirmCreateRecruitmentPlan = "${StringUtil.wrapString(uiLabelMap.ConfirmCreateRecruitmentPlan)}"; 
uiLabelMap.CommonSubmit = "${StringUtil.wrapString(uiLabelMap.CommonSubmit)}"; 
uiLabelMap.RecruitmentCandidateId = "${StringUtil.wrapString(uiLabelMap.RecruitmentCandidateId)}"; 
uiLabelMap.PartyGender = "${StringUtil.wrapString(uiLabelMap.PartyGender)}"; 
uiLabelMap.PartyBirthDate = "${StringUtil.wrapString(uiLabelMap.PartyBirthDate)}"; 
uiLabelMap.DegreeTraining = "${StringUtil.wrapString(uiLabelMap.DegreeTraining)}"; 
uiLabelMap.HRSpecialization = "${StringUtil.wrapString(uiLabelMap.HRSpecialization)}"; 
uiLabelMap.HRCommonClassification = "${StringUtil.wrapString(uiLabelMap.HRCommonClassification)}"; 
uiLabelMap.CommonStatus = "${StringUtil.wrapString(uiLabelMap.CommonStatus)}"; 
uiLabelMap.RecruitmentCandidatesList = "${StringUtil.wrapString(uiLabelMap.RecruitmentCandidatesList)}"; 
uiLabelMap.RecruitmentRound = "${StringUtil.wrapString(uiLabelMap.RecruitmentRound)}"; 
uiLabelMap.CreateNewCandidateConfirm = "${StringUtil.wrapString(uiLabelMap.CreateNewCandidateConfirm)}"; 
uiLabelMap.HRCommonPoint = "${StringUtil.wrapString(uiLabelMap.HRCommonPoint)}"; 
uiLabelMap.InterviewOrderShort = "${StringUtil.wrapString(uiLabelMap.InterviewOrderShort)}"; 
uiLabelMap.RecruitmentTimeInterview = "${StringUtil.wrapString(uiLabelMap.RecruitmentTimeInterview)}"; 
uiLabelMap.HRCommonEmail = "${StringUtil.wrapString(uiLabelMap.HRCommonEmail)}"; 
uiLabelMap.PhoneNumber = "${StringUtil.wrapString(uiLabelMap.PhoneNumber)}"; 
uiLabelMap.CandidateInterviewList = "${StringUtil.wrapString(uiLabelMap.CandidateInterviewList)}"; 
uiLabelMap.HRCalendarScheduleInterview = "${StringUtil.wrapString(uiLabelMap.HRCalendarScheduleInterview)}"; 
uiLabelMap.InterviewOrder = "${StringUtil.wrapString(uiLabelMap.InterviewOrder)}"; 
uiLabelMap.ConfirmSchduleInterviewCandidate = "${StringUtil.wrapString(uiLabelMap.ConfirmSchduleInterviewCandidate)}"; 
uiLabelMap.ConfirmReceiveCandidateBecomeEmpl = "${StringUtil.wrapString(uiLabelMap.ConfirmReceiveCandidateBecomeEmpl)}"; 
uiLabelMap.HRCommonResults = "${StringUtil.wrapString(uiLabelMap.HRCommonResults)}"; 
uiLabelMap.HRCommonComment = "${StringUtil.wrapString(uiLabelMap.HRCommonComment)}"; 
uiLabelMap.RecruitmentProcess = "${StringUtil.wrapString(uiLabelMap.RecruitmentProcess)}"; 
uiLabelMap.TotalPoint = "${StringUtil.wrapString(uiLabelMap.TotalPoint)}"; 
uiLabelMap.MustntHaveSpaceChar = "${StringUtil.wrapString(uiLabelMap.MustntHaveSpaceChar)}"; 
uiLabelMap.CreateNewUserLoginConfirm = "${StringUtil.wrapString(uiLabelMap.CreateNewUserLoginConfirm)}"; 
uiLabelMap.NotEmplPositionTypeChoose = '${StringUtil.wrapString(uiLabelMap.NotEmplPositionTypeChoose)}'; 
uiLabelMap.ThisFieldMustNotByContainSpecialCharacter = '${StringUtil.wrapString(uiLabelMap.ThisFieldMustNotByContainSpecialCharacter)}';
uiLabelMap.OnlyInputNumberGreaterThanZero = '${StringUtil.wrapString(uiLabelMap.OnlyInputNumberGreaterThanZero)}';
uiLabelMap.IdentifyDayGreaterBirthDate = '${StringUtil.wrapString(uiLabelMap.IdentifyDayGreaterBirthDate)}';
uiLabelMap.BirthDateBefIdentifyCardDay = '${StringUtil.wrapString(uiLabelMap.BirthDateBefIdentifyCardDay)}';
uiLabelMap.IllegalCharactersAndSpace = '${StringUtil.wrapString(uiLabelMap.IllegalCharactersAndSpace)}';
uiLabelMap.IllegalCharacters = '${StringUtil.wrapString(uiLabelMap.IllegalCharacters)}';
uiLabelMap.BirthDateBeforeToDay = '${StringUtil.wrapString(uiLabelMap.BirthDateBeforeToDay)}';
uiLabelMap.InvalidChar = '${StringUtil.wrapString(uiLabelMap.InvalidChar)}';
uiLabelMap.OnlyContainInvalidChar = "${StringUtil.wrapString(uiLabelMap.OnlyContainInvalidChar)}";
uiLabelMap.RecruitmentRoundResultCandidateIsNotChange = "${StringUtil.wrapString(uiLabelMap.RecruitmentRoundResultCandidateIsNotChange)}";
uiLabelMap.HRSelectCommon = "${StringUtil.wrapString(uiLabelMap.HRSelectCommon)}";
uiLabelMap.RecruitingPosition = "${StringUtil.wrapString(uiLabelMap.RecruitingPosition)}";
uiLabelMap.TimeRecruitmentPlan = "${StringUtil.wrapString(uiLabelMap.TimeRecruitmentPlan)}";
uiLabelMap.RecruitmentEnumType = "${StringUtil.wrapString(uiLabelMap.RecruitmentEnumType)}";
uiLabelMap.RecruitmentFormType = "${StringUtil.wrapString(uiLabelMap.RecruitmentFormType)}";
uiLabelMap.QuantityUnplannedShort = "${StringUtil.wrapString(uiLabelMap.QuantityUnplannedShort)}";
uiLabelMap.PlannedRecruitmentShort = "${StringUtil.wrapString(uiLabelMap.PlannedRecruitmentShort)}";
uiLabelMap.RecruitmentRequireIsApproved = "${StringUtil.wrapString(uiLabelMap.RecruitmentRequireIsApproved)}";
uiLabelMap.RecruitPlanBoardIsNotEmpty = "${StringUtil.wrapString(uiLabelMap.RecruitPlanBoardIsNotEmpty)}";
uiLabelMap.RecruitmentInterviewerMarker = "${StringUtil.wrapString(uiLabelMap.RecruitmentInterviewerMarker)}";
uiLabelMap.HRCommonStandard = "${StringUtil.wrapString(uiLabelMap.HRCommonStandard)}";
uiLabelMap.InterviewerEvalUnsatisfied = "${StringUtil.wrapString(uiLabelMap.InterviewerEvalUnsatisfied)}";
uiLabelMap.InterviewerEvalLittleSatisfied = "${StringUtil.wrapString(uiLabelMap.InterviewerEvalLittleSatisfied)}";
uiLabelMap.InterviewerEvalSatisfied = "${StringUtil.wrapString(uiLabelMap.InterviewerEvalSatisfied)}";
uiLabelMap.InterviewerEvalGood = "${StringUtil.wrapString(uiLabelMap.InterviewerEvalGood)}";
uiLabelMap.InterviewerEvalVeryGood = "${StringUtil.wrapString(uiLabelMap.InterviewerEvalVeryGood)}";
uiLabelMap.NotChooseReasonYet = "${StringUtil.wrapString(uiLabelMap.NotChooseReasonYet)}";
uiLabelMap.CommonMonth  = "${StringUtil.wrapString(uiLabelMap.CommonMonth)}";
uiLabelMap.TimeRecruiting  = "${StringUtil.wrapString(uiLabelMap.TimeRecruiting)}";
uiLabelMap.RecruitmentRequireNotChoose  = "${StringUtil.wrapString(uiLabelMap.RecruitmentRequireNotChoose)}";
uiLabelMap.password_did_not_match_verify_password = '${StringUtil.wrapString(uiLabelMap["password_did_not_match_verify_password"])}';
</script>