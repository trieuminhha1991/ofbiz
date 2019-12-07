<@jqGridMinimumLib/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script src="/aceadmin/assets/js/bootbox.min.js" type="text/javascript"></script>

<script src="/aceadmin/assets/js/spin.min.js"></script>
<script src="/hrresources/js/CommonFunction.js" type="text/javascript"></script>
<script src="/hrresources/js/shim.js" type="text/javascript"></script>
<script src="/hrresources/js/jszip.js" type="text/javascript"></script>
<script src="/hrresources/js/xlsx.js" type="text/javascript"></script>

<script type="text/javascript" src="/aceadmin/assets/js/fuelux/fuelux.wizard.min.js"></script>

<script type="text/javascript">
var globalVar = {};
var uiLabelMap = {};
globalVar.workingShiftArr = [
	<#if workingShiftList?has_content>
		<#list workingShiftList as workingShift>
		{
			workingShiftId: "${workingShift.workingShiftId}",
			workingShiftName: '${StringUtil.wrapString(workingShift.workingShiftName)}'
		},
		</#list>
	</#if>
];
globalVar.timekeepingDetailId = "${parameters.timekeepingDetailId}";
globalVar.fromDate = new Date(${fromDate.getTime()});
globalVar.thruDate = new Date(${thruDate.getTime()});
uiLabelMap.CommonClose = "${StringUtil.wrapString(uiLabelMap.CommonClose)}";
uiLabelMap.CommonDate = "${StringUtil.wrapString(uiLabelMap.CommonDate)}";
uiLabelMap.EmployeeId = "${StringUtil.wrapString(uiLabelMap.EmployeeId)}";
uiLabelMap.CommonSubmit = "${StringUtil.wrapString(uiLabelMap.CommonSubmit)}";
uiLabelMap.ColumnDataInImportFile = "${StringUtil.wrapString(uiLabelMap.ColumnDataInImportFile)}";
uiLabelMap.ColumnDataInSystem = "${StringUtil.wrapString(uiLabelMap.ColumnDataInSystem)}";
uiLabelMap.JoinColumnDataExcel = "${StringUtil.wrapString(uiLabelMap.JoinColumnDataExcel)}";
uiLabelMap.ColumnMapAuto = "${StringUtil.wrapString(uiLabelMap.ColumnMapAuto)}";
uiLabelMap.HRCommonReset = "${StringUtil.wrapString(uiLabelMap.HRCommonReset)}";
uiLabelMap.FieldRequired = "${StringUtil.wrapString(uiLabelMap.FieldRequired)}";
uiLabelMap.ConfirmReloadData = "${StringUtil.wrapString(uiLabelMap.ConfirmReloadData)}";
</script>