<@jqGridMinimumLib/>
<script src="/aceadmin/assets/js/spin.min.js"></script>
<script src="/aceadmin/assets/js/bootbox.min.js" type="text/javascript"></script>
<script src="/hrresources/js/CommonFunction.js" type="text/javascript"></script>
<script type="text/javascript">
var globalVar = {};
var uiLabelMap = {};
globalVar.trainingCourseId = "${trainingCourseId}";
globalVar.statusArr = [
	<#if statusRegisterList?exists>
		<#list statusRegisterList as status>
		{
			statusId: '${status.statusId}',
			description: '${StringUtil.wrapString(status.description?if_exists)}'
		},
		</#list>
	</#if>
];
uiLabelMap.TrainingAcceptRegisterConfirm = "${StringUtil.wrapString(uiLabelMap.TrainingAcceptRegisterConfirm)}";
uiLabelMap.TrainingRejectRegisterConfirm = "${StringUtil.wrapString(uiLabelMap.TrainingRejectRegisterConfirm)}";
uiLabelMap.CommonSubmit = "${StringUtil.wrapString(uiLabelMap.CommonSubmit)}";
uiLabelMap.CommonCancel = "${StringUtil.wrapString(uiLabelMap.CommonCancel)}";
uiLabelMap.CommonClose = "${StringUtil.wrapString(uiLabelMap.CommonClose)}";
</script>