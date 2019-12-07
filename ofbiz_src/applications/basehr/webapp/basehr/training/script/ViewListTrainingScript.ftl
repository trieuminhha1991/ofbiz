<@jqGridMinimumLib/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxeditor.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdatatable.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdata2.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtreegrid2.full.js"></script>

<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtree.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxsplitter.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxradiobutton.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<script src="/aceadmin/assets/js/spin.min.js"></script>
<script src="/aceadmin/assets/js/bootbox.min.js" type="text/javascript"></script>
<script src="/hrresources/js/CommonFunction.js" type="text/javascript"></script>

<!-- <script src="/hrresources/js/jqxtreegrid.js" type="text/javascript"></script> -->

<script type="text/javascript" src="/aceadmin/assets/js/fuelux/fuelux.wizard.min.js"></script>
<script type="text/javascript">
var globalVar = {};
globalVar.statusArr = [
	<#if statusList?exists>
		<#list statusList as status>
		{
			statusId: '${status.statusId}',
			description: '${StringUtil.wrapString(status.description?if_exists)}'
		},
		</#list>
	</#if>
];

var cellClass = function (row, columnfield, value) {
	var data = $('#jqxgrid').jqxGrid('getrowdata', row);
	if (typeof(data) != 'undefined') {
		if ("TRAINING_PLANNED_REJ" == data.statusId) {
			return "background-cancel";
		} else if ("TRAINING_PLANNED" == data.statusId) {
			return "background-important-nd";
		} else if ("TRAINING_PLANNED_ACC" == data.statusId) {
			return "background-prepare";
		}
		<#-- back favor ... -->
	}
}

var uiLabelMap ={};
uiLabelMap.TrainingCourseIdContainInvalidChar = '${StringUtil.wrapString(uiLabelMap.TrainingCourseIdContainInvalidChar)}';
uiLabelMap.CommonAddNew = '${StringUtil.wrapString(uiLabelMap.CommonAddNew)}';
uiLabelMap.OnlyContainInvalidChar = "${StringUtil.wrapString(uiLabelMap.OnlyContainInvalidChar)}";
uiLabelMap.ParentSkillTypeId = '${StringUtil.wrapString(uiLabelMap.ParentSkillTypeId)}';
uiLabelMap.CommonDescription = "${StringUtil.wrapString(uiLabelMap.CommonDescription)}";
uiLabelMap.ParentSkillTypeList = "${StringUtil.wrapString(uiLabelMap.ParentSkillTypeList)}";
uiLabelMap.accRemoveFilter = "${StringUtil.wrapString(uiLabelMap.accRemoveFilter)}";
uiLabelMap.HRContainSpecialSymbol = "${StringUtil.wrapString(uiLabelMap.HRContainSpecialSymbol)}";
uiLabelMap.TrainingCourseSkillTypeList = "${StringUtil.wrapString(uiLabelMap.TrainingCourseSkillTypeList)}";
uiLabelMap.NotSkillTypeSelected = "${StringUtil.wrapString(uiLabelMap.NotSkillTypeSelected)}";
uiLabelMap.HRSkillTypeList = "${StringUtil.wrapString(uiLabelMap.HRSkillTypeList)}";
uiLabelMap.accAddNewRow = "${StringUtil.wrapString(uiLabelMap.accAddNewRow)}";

</script>