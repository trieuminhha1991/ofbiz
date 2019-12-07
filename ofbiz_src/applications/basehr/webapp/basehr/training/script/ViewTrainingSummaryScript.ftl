<@jqGridMinimumLib/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtree.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdropdownbutton.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxsplitter.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxeditor.js"></script>
<script src="/aceadmin/assets/js/spin.min.js"></script>
<script src="/aceadmin/assets/js/bootbox.min.js" type="text/javascript"></script>
<script src="/hrresources/js/CommonFunction.js" type="text/javascript"></script>
<script type="text/javascript">
var globalVar = {};
var uiLabelMap = {};
globalVar.trainingCourseId = "${trainingCourseId}";
globalVar.trainingResultTypeArr = [
	<#if trainingResultTypeList?has_content>
		<#list trainingResultTypeList as trainingResultType>
		{
			resultTypeId: '${trainingResultType.resultTypeId}',
			description: '${StringUtil.wrapString(trainingResultType.description)}'
		},
		</#list>
	</#if>
];
globalVar.estimatedFromDate = new Date(${trainingCourseDetail.fromDate.getTime()}); 
globalVar.estimatedThruDate = new Date(${trainingCourseDetail.thruDate.getTime()});
globalVar.amountCompanySupport = ${trainingCourseDetail.amountCompanySupport};
globalVar.estimatedEmplPaid = ${trainingCourseDetail.estimatedEmplPaid};
globalVar.estimatedNumber = ${trainingCourseDetail.estimatedNumber};

<#if trainingCourseDetail.actualFromDate?exists>
	globalVar.actualFromDate = new Date(${trainingCourseDetail.actualFromDate.getTime()}); 
</#if>
<#if trainingCourseDetail.actualThruDate?exists>
	globalVar.actualThruDate = new Date(${trainingCourseDetail.actualThruDate.getTime()});
</#if>
<#if trainingCourseDetail.actualEmplPaid?exists>
	globalVar.actualEmplPaid = ${trainingCourseDetail.actualEmplPaid};
</#if>
<#if trainingCourseDetail.actualAmountCompanySup?exists>
	globalVar.actualAmountCompanySup = ${trainingCourseDetail.actualAmountCompanySup};
</#if>
<#if totalEmplAtt?exists>
	globalVar.totalActualAtt = ${totalEmplAtt.get("totalPartyAtt")};
<#else>
globalVar.totalActualAtt = 0;
</#if>

uiLabelMap.HRExpectedAttend = "${StringUtil.wrapString(uiLabelMap.HRExpectedAttend)}";
uiLabelMap.HRCommonRegisted = "${StringUtil.wrapString(uiLabelMap.HRCommonRegisted)}";
uiLabelMap.HRCommonResults = "${StringUtil.wrapString(uiLabelMap.HRCommonResults)}";
uiLabelMap.HRCommonComment = "${StringUtil.wrapString(uiLabelMap.HRCommonComment)}";
uiLabelMap.TrainingAmountEmployeeMustPaid = "${StringUtil.wrapString(uiLabelMap.TrainingAmountEmployeeMustPaid)}";
uiLabelMap.AmountCompanySupport = "${StringUtil.wrapString(uiLabelMap.AmountCompanySupport)}";
uiLabelMap.TrainingAmountEmployeePaid = "${StringUtil.wrapString(uiLabelMap.TrainingAmountEmployeePaid)}";
uiLabelMap.HRCommonRemain = "${StringUtil.wrapString(uiLabelMap.HRCommonRemain)}";
uiLabelMap.ListEmplAttendanceTraining = "${StringUtil.wrapString(uiLabelMap.ListEmplAttendanceTraining)}";
uiLabelMap.CommonSubmit = "${StringUtil.wrapString(uiLabelMap.CommonSubmit)}";
uiLabelMap.CommonCancel = "${StringUtil.wrapString(uiLabelMap.CommonCancel)}";
uiLabelMap.EmployeeId = "${StringUtil.wrapString(uiLabelMap.EmployeeId)}";
uiLabelMap.EmployeeName = "${StringUtil.wrapString(uiLabelMap.EmployeeName)}";
uiLabelMap.CommonDepartment = "${StringUtil.wrapString(uiLabelMap.CommonDepartment)}";
uiLabelMap.HrCommonPosition = "${StringUtil.wrapString(uiLabelMap.HrCommonPosition)}";
uiLabelMap.EmployeeListSelected = "${StringUtil.wrapString(uiLabelMap.EmployeeListSelected)}";
uiLabelMap.accRemoveFilter = "${StringUtil.wrapString(uiLabelMap.accRemoveFilter)}";
uiLabelMap.wgdelete = "${StringUtil.wrapString(uiLabelMap.wgdelete)}";
uiLabelMap.CannotDeleteRow = "${StringUtil.wrapString(uiLabelMap.CannotDeleteRow)}";
uiLabelMap.wgdeleteconfirm = "${StringUtil.wrapString(uiLabelMap.wgdeleteconfirm)}";
uiLabelMap.wgok = "${StringUtil.wrapString(uiLabelMap.wgok)}";
uiLabelMap.wgcancel = "${StringUtil.wrapString(uiLabelMap.wgcancel)}";
uiLabelMap.CommonAddNew = "${StringUtil.wrapString(uiLabelMap.CommonAddNew)}";
uiLabelMap.DateRegistration = "${StringUtil.wrapString(uiLabelMap.DateRegistration)}";
uiLabelMap.ListEmplRegistedTraining = "${StringUtil.wrapString(uiLabelMap.ListEmplRegistedTraining)}";
uiLabelMap.EmplExpectedAttendance = "${StringUtil.wrapString(uiLabelMap.EmplExpectedAttendance)}";
uiLabelMap.EmployeeSelected = "${StringUtil.wrapString(uiLabelMap.EmployeeSelected)}";
uiLabelMap.NoPartyChoose = "${StringUtil.wrapString(uiLabelMap.NoPartyChoose)}";
uiLabelMap.CommonClose = "${StringUtil.wrapString(uiLabelMap.CommonClose)}";
uiLabelMap.AddNewRowConfirm = "${StringUtil.wrapString(uiLabelMap.AddNewRowConfirm)}";
uiLabelMap.DateJoinCompany = "${StringUtil.wrapString(uiLabelMap.DateJoinCompany)}";
uiLabelMap.CompleteTrainingCourseConfirm = "${StringUtil.wrapString(uiLabelMap.CompleteTrainingCourseConfirm)}";
</script>