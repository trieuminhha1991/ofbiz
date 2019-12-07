<@jqGridMinimumLib/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtabs.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxeditor.js"></script>
<script src="/hrresources/js/CommonFunction.js" type="text/javascript"></script>
<script src="/aceadmin/assets/js/bootbox.min.js" type="text/javascript"></script>
<script src="/aceadmin/assets/js/spin.min.js"></script>
<script type="text/javascript">
var filterObjData = new Object();
var globalVar = {};
globalVar.payrollTableId = "${parameters.payrollTableId}";
globalVar.statusId = "${payrollTableRecordStatus.statusId}";
globalVar.payrollItemTypeArr = [
	<#if payrollItemTypeList?has_content>
		<#list payrollItemTypeList as payrollItemType>
		{
			payrollItemTypeId: "${payrollItemType.payrollItemTypeId}",
			description: '${StringUtil.wrapString(payrollItemType.description)}'
		},
		</#list>
	</#if>
];
globalVar.allPayrollItemTypeArr = [
	<#if allPayrollItemTypeList?has_content>
		<#list allPayrollItemTypeList as payrollItemType>
		{
			payrollItemTypeId: "${payrollItemType.payrollItemTypeId}",
			description: '${StringUtil.wrapString(payrollItemType.description)}'
		},
		</#list>
	</#if>
];
globalVar.taxableTypeArr = [
	<#if taxableTypeList?has_content>
		<#list taxableTypeList as taxableType>
		{
			taxableTypeId: "${taxableType.taxableTypeId}",
			description: '${StringUtil.wrapString(taxableType.description)}'
		},
		</#list>
	</#if>
];
globalVar.formulaIncomeArr = [
	<#if listFormulaIncome?has_content>
		<#list listFormulaIncome as formula>
		{
			code: "${formula.code}",
			name: '${StringUtil.wrapString(formula.name)}',
			taxableTypeId: "${formula.taxableTypeId?if_exists}",
		},
		</#list>
	</#if>
];
globalVar.formulaDeductionArr = [
	<#if listFormulaDeduction?has_content>
		<#list listFormulaDeduction as formula>
		{
			code: "${formula.code}",
			name: '${StringUtil.wrapString(formula.name)}',
			exempted: "${formula.exempted?if_exists}",
		},
		</#list>
	</#if>
];
var uiLabelMap = {};
uiLabelMap.PayrollCalculated_Recalculated = "${StringUtil.wrapString(uiLabelMap.PayrollCalculated_Recalculated)}";
uiLabelMap.CommonSubmit = "${StringUtil.wrapString(uiLabelMap.CommonSubmit)}";
uiLabelMap.CommonCancel = "${StringUtil.wrapString(uiLabelMap.CommonCancel)}";
uiLabelMap.CommonSearch = "${StringUtil.wrapString(uiLabelMap.CommonSearch)}";
uiLabelMap.HRIncome = "${StringUtil.wrapString(uiLabelMap.HRIncome)}";
uiLabelMap.PayrollItemType = "${StringUtil.wrapString(uiLabelMap.PayrollItemType)}";
uiLabelMap.HRCommonAmount = "${StringUtil.wrapString(uiLabelMap.HRCommonAmount)}";
uiLabelMap.TaxableType = "${StringUtil.wrapString(uiLabelMap.TaxableType)}";
uiLabelMap.HRDeduction = "${StringUtil.wrapString(uiLabelMap.HRDeduction)}";
uiLabelMap.IsExemptedTax = "${StringUtil.wrapString(uiLabelMap.IsExemptedTax)}";
uiLabelMap.CommonAddNew = "${StringUtil.wrapString(uiLabelMap.CommonAddNew)}";
uiLabelMap.wgdelete = "${StringUtil.wrapString(uiLabelMap.wgdelete)}";
uiLabelMap.FieldRequired = "${StringUtil.wrapString(uiLabelMap.FieldRequired)}";
uiLabelMap.CommonSubmit = "${StringUtil.wrapString(uiLabelMap.CommonSubmit)}";
uiLabelMap.CommonCancel = "${StringUtil.wrapString(uiLabelMap.CommonCancel)}";
uiLabelMap.HrCreateNewConfirm = "${StringUtil.wrapString(uiLabelMap.HrCreateNewConfirm)}";
uiLabelMap.ValueMustBeGreateThanZero = "${StringUtil.wrapString(uiLabelMap.ValueMustBeGreateThanZero)}";
uiLabelMap.NotifyDelete = "${StringUtil.wrapString(uiLabelMap.NotifyDelete)}";
uiLabelMap.CommonApprove = "${StringUtil.wrapString(uiLabelMap.CommonApprove)}";
uiLabelMap.CommonReject = "${StringUtil.wrapString(uiLabelMap.CommonReject)}";
uiLabelMap.AreYouSureWantToApproval = "${StringUtil.wrapString(uiLabelMap.AreYouSureWantToApproval)}";
uiLabelMap.CommonClose = "${StringUtil.wrapString(uiLabelMap.CommonClose)}";
uiLabelMap.EmployeeId = "${StringUtil.wrapString(uiLabelMap.EmployeeId)}";
uiLabelMap.EmployeeName = "${StringUtil.wrapString(uiLabelMap.EmployeeName)}";
uiLabelMap.RealSalaryPaid = "${StringUtil.wrapString(uiLabelMap.RealSalaryPaid)}";
uiLabelMap.InvoiceIsCreatedForOrg = "${StringUtil.wrapString(uiLabelMap.InvoiceIsCreatedForOrg)}";
uiLabelMap.EmployeeHaveNotCreateInvoice = "${StringUtil.wrapString(uiLabelMap.EmployeeHaveNotCreateInvoice)}";
uiLabelMap.accRemoveFilter = "${StringUtil.wrapString(uiLabelMap.accRemoveFilter)}";
uiLabelMap.CommonSelectAll = "${StringUtil.wrapString(uiLabelMap.CommonSelectAll)}";
uiLabelMap.CreateInvoiceConfirm = "${StringUtil.wrapString(uiLabelMap.CreateInvoiceConfirm)}";
uiLabelMap.PayrollInvoiceListEmployee = "${StringUtil.wrapString(uiLabelMap.PayrollInvoiceListEmployee)}";
uiLabelMap.HRInvoiceId = "${StringUtil.wrapString(uiLabelMap.HRInvoiceId)}";
uiLabelMap.RoundingThousand = "${StringUtil.wrapString(uiLabelMap.RoundingThousand)}";
uiLabelMap.RoundingHundreds = "${StringUtil.wrapString(uiLabelMap.RoundingHundreds)}";
uiLabelMap.RoundingTens = "${StringUtil.wrapString(uiLabelMap.RoundingTens)}";
uiLabelMap.RoundingUnit = "${StringUtil.wrapString(uiLabelMap.RoundingUnit)}";
</script>