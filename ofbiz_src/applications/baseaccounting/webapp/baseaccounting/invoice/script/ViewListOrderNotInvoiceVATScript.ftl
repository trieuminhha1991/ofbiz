<@jqGridMinimumLib/>
<script type="text/javascript" src="/accresources/js/acc.utils.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxformattedinput.js"></script>
<script type="text/javascript">
<#assign productStoresList = Static['com.olbius.basesales.product.ProductStoreWorker'].getListProductStore(delegator, userLogin, isOwnerDistributor)!/>
<#assign orderStatuses = delegator.findByAnd("StatusItem", {"statusTypeId" : "ORDER_STATUS"}, null, false)/>
var globalVar = {};
var uiLabelMap = {};
globalVar.formData = {};
globalVar.productStoreData = [
	<#list productStoresList as productStore>
	{	storeName : "${productStore.storeName?default('')}",
		productStoreId : "${productStore.productStoreId}"
	},
	</#list>                              
];
globalVar.orderStatusData = [
	<#if orderStatuses?exists>
		<#list orderStatuses as statusItem>
		{	statusId: '${statusItem.statusId}',
			description: '${StringUtil.wrapString(statusItem.get("description", locale))}'
		},
		</#list>
	</#if>                             
];
globalVar.enumPaymentMethodArr = [
	<#if enumPaymentMethodList?exists>
		<#list enumPaymentMethodList as enumPaymentMethod>
		{	
			enumId: '${enumPaymentMethod.enumId}',
			description: '${StringUtil.wrapString(enumPaymentMethod.get("description", locale))}'
		},
		</#list>
	</#if>                             
];
uiLabelMap.BACCInvoiceItemType = "${StringUtil.wrapString(uiLabelMap.BACCInvoiceItemType)}";
uiLabelMap.BACCProductId = "${StringUtil.wrapString(uiLabelMap.BACCProductId)}";
uiLabelMap.BACCDescription = "${StringUtil.wrapString(uiLabelMap.BACCDescription)}";
uiLabelMap.BSQty = "${StringUtil.wrapString(uiLabelMap.BSQty)}";
uiLabelMap.BACCUnitPrice = "${StringUtil.wrapString(uiLabelMap.BACCUnitPrice)}";
uiLabelMap.BACCTotal = "${StringUtil.wrapString(uiLabelMap.BACCTotal)}";
uiLabelMap.BACCInvoiceItem = "${StringUtil.wrapString(uiLabelMap.BACCInvoiceItem)}";
uiLabelMap.CommonClose = "${StringUtil.wrapString(uiLabelMap.CommonClose)}";
uiLabelMap.BACCAmountTotal = "${StringUtil.wrapString(uiLabelMap.BACCAmountTotal)}";
uiLabelMap.CommonSubmit = "${StringUtil.wrapString(uiLabelMap.CommonSubmit)}";
uiLabelMap.CommonCancel = "${StringUtil.wrapString(uiLabelMap.CommonCancel)}";
uiLabelMap.FieldRequired = "${StringUtil.wrapString(uiLabelMap.FieldRequired)}";
uiLabelMap.CreateInvoiceVATForOrderConfirm = "${StringUtil.wrapString(uiLabelMap.CreateInvoiceVATForOrderConfirm)}";
uiLabelMap.BSOrderId = "${StringUtil.wrapString(uiLabelMap.BSOrderId)}";
uiLabelMap.BACCOrderIsSelected = "${StringUtil.wrapString(uiLabelMap.BACCOrderIsSelected)}";
uiLabelMap.CreateOrderInvoiceNoteConfirm = "${StringUtil.wrapString(uiLabelMap.CreateOrderInvoiceNoteConfirm)}";
</script>