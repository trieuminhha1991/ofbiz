<@jqGridMinimumLib/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdropdownbutton.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script src="/aceadmin/assets/js/bootbox.min.js" type="text/javascript"></script>
<script src="/aceadmin/assets/js/spin.min.js"></script>
<script src="/hrresources/js/CommonFunction.js" type="text/javascript"></script>
<script type="text/javascript">
var globalVar = {};
globalVar.statuses = [
	<#if statuses?has_content>
		<#list statuses as status>
			{
				statusId : "${status.statusId}",
				description : "${StringUtil.wrapString(status.description)}",
			},
		</#list>
	</#if>
];
var uiLabelMap = {};
uiLabelMap.HRCommonMonthLowercase = "${StringUtil.wrapString(uiLabelMap.HRCommonMonthLowercase)}";
uiLabelMap.TimekeepingSummaryName = "${StringUtil.wrapString(uiLabelMap.TimekeepingSummaryName)}";
uiLabelMap.PayrollTable = "${StringUtil.wrapString(uiLabelMap.PayrollTable)}";
uiLabelMap.CalcByTimekeepingSum = "${StringUtil.wrapString(uiLabelMap.CalcByTimekeepingSum)}";
uiLabelMap.TimekeepingSummary = "${StringUtil.wrapString(uiLabelMap.TimekeepingSummary)}";
uiLabelMap.HRCommonInLowercase = "${StringUtil.wrapString(uiLabelMap.HRCommonInLowercase)}";
uiLabelMap.HRCommonNotCreated = "${StringUtil.wrapString(uiLabelMap.HRCommonNotCreated)}";
uiLabelMap.TimekeepingSummaryIsNotSelected = "${StringUtil.wrapString(uiLabelMap.TimekeepingSummaryIsNotSelected)}";
uiLabelMap.CommonClose = "${StringUtil.wrapString(uiLabelMap.CommonClose)}";
uiLabelMap.ConfirmCreatePayrollTable = "${StringUtil.wrapString(uiLabelMap.ConfirmCreatePayrollTable)}";
uiLabelMap.CommonSubmit = "${StringUtil.wrapString(uiLabelMap.CommonSubmit)}";
uiLabelMap.FieldRequired = "${StringUtil.wrapString(uiLabelMap.FieldRequired)}";
uiLabelMap.PayrollCalculated_Recalculated = "${StringUtil.wrapString(uiLabelMap.PayrollCalculated_Recalculated)}";
uiLabelMap.CommonCancel = "${StringUtil.wrapString(uiLabelMap.CommonCancel)}";
uiLabelMap.CreateInvoiceConfirm = "${StringUtil.wrapString(uiLabelMap.CreateInvoiceConfirm)}";
</script>