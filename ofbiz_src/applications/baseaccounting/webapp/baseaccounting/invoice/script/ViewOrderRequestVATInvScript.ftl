<@jqGridMinimumLib/>
<script type="text/javascript" src="/accresources/js/acc.utils.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxformattedinput.js"></script>
<script type="text/javascript">
var globalVar = {};
var uiLabelMap = {};
globalVar.enumPaymentMethodArr = [
	<#if enumPaymentMethodList?has_content>
		<#list enumPaymentMethodList as enumPaymentMethod>
		{
			enumId: '${enumPaymentMethod.enumId}',
			description: '${enumPaymentMethod.get("description", locale)}'
		},
		</#list>
	</#if>
];
uiLabelMap.CommonSubmit = "${StringUtil.wrapString(uiLabelMap.CommonSubmit)}";
uiLabelMap.CommonCancel = "${StringUtil.wrapString(uiLabelMap.CommonCancel)}";
uiLabelMap.CommonClose = "${StringUtil.wrapString(uiLabelMap.CommonClose)}";
uiLabelMap.SetOrderIsCreatedVATInvoiceConfirm = "${StringUtil.wrapString(uiLabelMap.SetOrderIsCreatedVATInvoiceConfirm)}";
uiLabelMap.BSOrderId = "${StringUtil.wrapString(uiLabelMap.BSOrderId)}";
uiLabelMap.FieldRequired = "${StringUtil.wrapString(uiLabelMap.FieldRequired)}";
uiLabelMap.BACCUpdateConfirm = "${StringUtil.wrapString(uiLabelMap.BACCUpdateConfirm)}";
</script>