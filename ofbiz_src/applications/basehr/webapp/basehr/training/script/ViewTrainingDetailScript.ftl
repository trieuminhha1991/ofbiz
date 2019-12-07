<@jqGridMinimumLib/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtree.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdropdownbutton.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxsplitter.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxeditor.js"></script>
<script src="/aceadmin/assets/js/spin.min.js"></script>
<script src="/aceadmin/assets/js/bootbox.min.js" type="text/javascript"></script>
<script src="/hrresources/js/CommonFunction.js" type="text/javascript"></script>
<script type="text/javascript">
var globalVar = {};
globalVar.trainingCourseId = "${trainingCourseId}";
globalVar.trainingFormTypeArr = [
	<#if trainingFormTypeList?exists>
		<#list trainingFormTypeList as trainingFormType>
		{
			trainingFormTypeId: "${trainingFormType.trainingFormTypeId}",
			description: "${StringUtil.wrapString(trainingFormType.description?if_exists)}"
		},
		</#list>
	</#if>                                 
];

globalVar.trainingPurposeTypeArr = [
	<#if trainingPurposeTypeList?exists>
		<#list trainingPurposeTypeList as trainingPurposeType>
		{
			trainingPurposeTypeId: "${trainingPurposeType.trainingPurposeTypeId}",
			description: "${StringUtil.wrapString(trainingPurposeType.description?if_exists)}"
		},
		</#list>
	</#if>
];
globalVar.trainingResultTypeArr = [
	<#if trainingResultTypeList?has_content>
		<#list trainingResultTypeList as trainingResultType>
		{
			resultTypeId: '${trainingResultType.resultTypeId}',
			description: '${StringUtil.wrapString(trainingResultType.description?if_exists)}'
		},
		</#list>
	</#if>                             
];
globalVar.statusArr = [
	<#if statusList?has_content>
	<#list statusList as status>
	{
		statusId: '${status.statusId}',
		description: '${StringUtil.wrapString(status.description?if_exists)}'
	},
	</#list>
	</#if>             
];
var uiLabelMap = {};
uiLabelMap.CommonSubmit = "${StringUtil.wrapString(uiLabelMap.CommonSubmit)}";
uiLabelMap.CommonClose = "${StringUtil.wrapString(uiLabelMap.CommonClose)}";
uiLabelMap.InvalidChar = "${StringUtil.wrapString(uiLabelMap.InvalidChar)}";
uiLabelMap.FieldRequired = "${StringUtil.wrapString(uiLabelMap.FieldRequired)}";
uiLabelMap.EstimatedThruDateMustGreaterEqualFromDate = "${StringUtil.wrapString(uiLabelMap.EstimatedThruDateMustGreaterEqualFromDate)}";
uiLabelMap.RegisterThruDateMustGreaterEqualFromDate = "${StringUtil.wrapString(uiLabelMap.RegisterThruDateMustGreaterEqualFromDate)}";
uiLabelMap.RegisterThruDateMustLessThanStartDateTraining = "${StringUtil.wrapString(uiLabelMap.RegisterThruDateMustLessThanStartDateTraining)}";
uiLabelMap.HRCommonNotSetting = "${StringUtil.wrapString(uiLabelMap.HRCommonNotSetting)}";
uiLabelMap.HRSkillType = "${StringUtil.wrapString(uiLabelMap.HRSkillType)}";
uiLabelMap.HRSkillTypeParent = "${StringUtil.wrapString(uiLabelMap.HRSkillTypeParent)}";
uiLabelMap.HRSkillTypeList = "${StringUtil.wrapString(uiLabelMap.HRSkillTypeList)}";
uiLabelMap.accAddNewRow = "${StringUtil.wrapString(uiLabelMap.accAddNewRow)}";
uiLabelMap.OnlyContainInvalidChar = "${StringUtil.wrapString(uiLabelMap.OnlyContainInvalidChar)}";
uiLabelMap.ParentSkillTypeId = "${StringUtil.wrapString(uiLabelMap.ParentSkillTypeId)}";
uiLabelMap.CommonDescription = "${StringUtil.wrapString(uiLabelMap.CommonDescription)}";
uiLabelMap.ParentSkillTypeList = "${StringUtil.wrapString(uiLabelMap.ParentSkillTypeList)}";
uiLabelMap.CommonAddNew = "${StringUtil.wrapString(uiLabelMap.CommonAddNew)}";
uiLabelMap.accRemoveFilter = "${StringUtil.wrapString(uiLabelMap.accRemoveFilter)}";
uiLabelMap.wgaddsuccess = "${StringUtil.wrapString(uiLabelMap.wgaddsuccess)}";
uiLabelMap.RequrimentLevelSkillTrainingCourse = "${StringUtil.wrapString(uiLabelMap.RequrimentLevelSkillTrainingCourse)}";
uiLabelMap.NotSkillTypeSelected = "${StringUtil.wrapString(uiLabelMap.NotSkillTypeSelected)}";
uiLabelMap.AllowAllEmployeeRegister = "${StringUtil.wrapString(uiLabelMap.AllowAllEmployeeRegister)}";
uiLabelMap.OnlyEmplInRegisterList = "${StringUtil.wrapString(uiLabelMap.OnlyEmplInRegisterList)}";
uiLabelMap.NotAllowCancelRegister = "${StringUtil.wrapString(uiLabelMap.NotAllowCancelRegister)}";
uiLabelMap.AllowCancelRegisterBefore = "${StringUtil.wrapString(uiLabelMap.AllowCancelRegisterBefore)}";
uiLabelMap.CommonDay = "${StringUtil.wrapString(uiLabelMap.CommonDay)}";
uiLabelMap.SendApprRequestConfirm = "${StringUtil.wrapString(uiLabelMap.SendApprRequestConfirm)}";
uiLabelMap.CommonCancel = "${StringUtil.wrapString(uiLabelMap.CommonCancel)}";
uiLabelMap.PleaseSelectOption = "${StringUtil.wrapString(uiLabelMap.PleaseSelectOption)}";
uiLabelMap.HRApprovalConfirm = "${StringUtil.wrapString(uiLabelMap.HRApprovalConfirm)}";
uiLabelMap.ApprovalDate = "${StringUtil.wrapString(uiLabelMap.ApprovalDate)}";
uiLabelMap.CommonStatus = "${StringUtil.wrapString(uiLabelMap.CommonStatus)}";
uiLabelMap.HRApprover = "${StringUtil.wrapString(uiLabelMap.HRApprover)}";
uiLabelMap.HRCommonReason = "${StringUtil.wrapString(uiLabelMap.HRCommonReason)}";
uiLabelMap.ViewDetails = "${StringUtil.wrapString(uiLabelMap.ViewDetails)}";
uiLabelMap.ApprovalHistory = "${StringUtil.wrapString(uiLabelMap.ApprovalHistory)}";
uiLabelMap.EmployeeId = "${StringUtil.wrapString(uiLabelMap.EmployeeId)}";
uiLabelMap.EmployeeName = "${StringUtil.wrapString(uiLabelMap.EmployeeName)}";
uiLabelMap.HrCommonPosition = "${StringUtil.wrapString(uiLabelMap.HrCommonPosition)}";
uiLabelMap.CommonDepartment = "${StringUtil.wrapString(uiLabelMap.CommonDepartment)}";
uiLabelMap.DateJoinCompany = "${StringUtil.wrapString(uiLabelMap.DateJoinCompany)}";
</script>