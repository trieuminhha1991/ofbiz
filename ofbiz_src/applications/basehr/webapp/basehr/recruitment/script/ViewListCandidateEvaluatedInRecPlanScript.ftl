<@jqGridMinimumLib/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxsplitter.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtextarea.js"></script>
<script src="/hrresources/js/CommonFunction.js" type="text/javascript"></script>
<script src="/aceadmin/assets/js/bootbox.min.js" type="text/javascript"></script>
<script src="/aceadmin/assets/js/spin.min.js"></script>
<script type="text/javascript">
var globalVar = {};
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
<#if selectYearCustomTimePeriodId?has_content>
	globalVar.selectYearCustomTimePeriodId = "${selectYearCustomTimePeriodId}";
</#if>
var uiLabelMap = {};
uiLabelMap.RecruitmentCandidateId = "${StringUtil.wrapString(uiLabelMap.RecruitmentCandidateId)}"; 
uiLabelMap.PartyGender = "${StringUtil.wrapString(uiLabelMap.PartyGender)}"; 
uiLabelMap.PartyBirthDate = "${StringUtil.wrapString(uiLabelMap.PartyBirthDate)}"; 
uiLabelMap.DegreeTraining = "${StringUtil.wrapString(uiLabelMap.DegreeTraining)}"; 
uiLabelMap.HRSpecialization = "${StringUtil.wrapString(uiLabelMap.HRSpecialization)}"; 
uiLabelMap.HRCommonClassification = "${StringUtil.wrapString(uiLabelMap.HRCommonClassification)}"; 
uiLabelMap.CommonStatus = "${StringUtil.wrapString(uiLabelMap.CommonStatus)}";
uiLabelMap.HRFullName = "${StringUtil.wrapString(uiLabelMap.HRFullName)}";
uiLabelMap.RecruitmentRound = "${StringUtil.wrapString(uiLabelMap.RecruitmentRound)}";
uiLabelMap.accRemoveFilter = "${StringUtil.wrapString(uiLabelMap.accRemoveFilter)}";
uiLabelMap.accRemoveFilter = "${StringUtil.wrapString(uiLabelMap.accRemoveFilter)}";
uiLabelMap.RecruitmentCandidatesList = "${StringUtil.wrapString(uiLabelMap.RecruitmentCandidatesList)}";
uiLabelMap.CurrentRecruitmentRound = "${StringUtil.wrapString(uiLabelMap.CurrentRecruitmentRound)}";
uiLabelMap.HRCommonPoint = "${StringUtil.wrapString(uiLabelMap.HRCommonPoint)}";
uiLabelMap.RecruitmentSubjectName = "${StringUtil.wrapString(uiLabelMap.RecruitmentSubjectName)}";
uiLabelMap.HRCommonRatio = "${StringUtil.wrapString(uiLabelMap.HRCommonRatio)}";
uiLabelMap.RecruitmentSubject = "${StringUtil.wrapString(uiLabelMap.RecruitmentSubject)}";

uiLabelMap.HRCommonStandard = "${StringUtil.wrapString(uiLabelMap.HRCommonStandard)}";
uiLabelMap.InterviewerEvalUnsatisfied = "${StringUtil.wrapString(uiLabelMap.InterviewerEvalUnsatisfied)}";
uiLabelMap.InterviewerEvalLittleSatisfied = "${StringUtil.wrapString(uiLabelMap.InterviewerEvalLittleSatisfied)}";
uiLabelMap.InterviewerEvalSatisfied = "${StringUtil.wrapString(uiLabelMap.InterviewerEvalSatisfied)}";
uiLabelMap.InterviewerEvalGood = "${StringUtil.wrapString(uiLabelMap.InterviewerEvalGood)}";
uiLabelMap.InterviewerEvalVeryGood = "${StringUtil.wrapString(uiLabelMap.InterviewerEvalVeryGood)}";
uiLabelMap.RecruitmentEvaluatedCandidateConfirm = "${StringUtil.wrapString(uiLabelMap.RecruitmentEvaluatedCandidateConfirm)}";
uiLabelMap.CommonSubmit = "${StringUtil.wrapString(uiLabelMap.CommonSubmit)}";
uiLabelMap.CommonClose = "${StringUtil.wrapString(uiLabelMap.CommonClose)}";
</script>