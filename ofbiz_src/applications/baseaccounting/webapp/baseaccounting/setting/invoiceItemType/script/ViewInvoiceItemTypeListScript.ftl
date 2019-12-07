<@jqGridMinimumLib/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdatatable.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtreegrid.js"></script>
<script type="text/javascript" src="/accresources/js/acc.utils.js"></script>
<script type="text/javascript">
var uiLabelMap = {};
var globalVar = {};
globalVar.invoiceTypeArr = [
	<#if invoiceTypeList?has_content>
		<#list invoiceTypeList as invoiceType>
		{
			invoiceTypeId: '${invoiceType.invoiceTypeId}',
			description: '${StringUtil.wrapString(invoiceType.get("description", locale))}'
		},
		</#list>
	</#if>
];
uiLabelMap.CommonId = "${StringUtil.wrapString(uiLabelMap.CommonId)}";
uiLabelMap.CommonDescription = "${StringUtil.wrapString(uiLabelMap.CommonDescription)}";
uiLabelMap.BACCInvoiceTypeId = "${StringUtil.wrapString(uiLabelMap.BACCInvoiceTypeId)}";
uiLabelMap.BACCGlAccountId = "${StringUtil.wrapString(uiLabelMap.BACCGlAccountId)}";
uiLabelMap.BACCAccountName = "${StringUtil.wrapString(uiLabelMap.BACCAccountName)}";
uiLabelMap.BACCInvoiceItemList = "${StringUtil.wrapString(uiLabelMap.BACCInvoiceItemList)}";
uiLabelMap.BACCAccountName = "${StringUtil.wrapString(uiLabelMap.BACCAccountName)}";
uiLabelMap.accAddNewRow = "${StringUtil.wrapString(uiLabelMap.accAddNewRow)}";
uiLabelMap.accRemoveFilter = "${StringUtil.wrapString(uiLabelMap.accRemoveFilter)}";
uiLabelMap.CommonSearch = "${StringUtil.wrapString(uiLabelMap.CommonSearch)}";
uiLabelMap.BACCGlAccountId = "${StringUtil.wrapString(uiLabelMap.BACCGlAccountId)}";
uiLabelMap.BACCAccountName = "${StringUtil.wrapString(uiLabelMap.BACCAccountName)}";
uiLabelMap.FieldRequired = "${StringUtil.wrapString(uiLabelMap.FieldRequired)}";
uiLabelMap.CreateInvoiceItemConfirm = "${StringUtil.wrapString(uiLabelMap.CreateInvoiceItemConfirm)}";
uiLabelMap.CommonSubmit = "${StringUtil.wrapString(uiLabelMap.CommonSubmit)}";
uiLabelMap.CommonCancel = "${StringUtil.wrapString(uiLabelMap.CommonCancel)}";
uiLabelMap.CommonClose = "${StringUtil.wrapString(uiLabelMap.CommonClose)}";
uiLabelMap.CommonEdit = "${StringUtil.wrapString(uiLabelMap.CommonEdit)}";
uiLabelMap.BACCCreateNew = "${StringUtil.wrapString(uiLabelMap.BACCCreateNew)}";
</script>