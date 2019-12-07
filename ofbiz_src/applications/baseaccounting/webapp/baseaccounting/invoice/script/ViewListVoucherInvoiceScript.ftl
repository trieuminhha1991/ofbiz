<link rel="stylesheet" href="/aceadmin/assets/css/colorbox.css" />
<@jqGridMinimumLib/>
<script type="text/javascript" src="/accresources/js/acc.utils.js"></script>
<script type="text/javascript" src="/aceadmin/assets/js/fuelux/fuelux.wizard.min.js"></script>
<script type="text/javascript" src="/aceadmin/assets/js/jquery.colorbox-min.js"></script>
<script type="text/javascript">
<#assign listPaymentMethods = delegator.findByAnd("PaymentMethod",Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId","${userLogin.lastOrg}") , 
		Static["org.ofbiz.base.util.UtilMisc"].toList("paymentMethodId DESC"), false)>
<#assign invoiceTypeList = delegator.findList("InvoiceType", null, null, null, null, false) />
var globalVar = {};
var uiLabelMap = {};
globalVar.statusArr = [
	<#if statusList?has_content>
		<#list statusList as status>
		{
			statusId: '${status.statusId}',
			description: '${StringUtil.wrapString(status.get("description", locale))}'
		},
		</#list>
	</#if>
];
globalVar.paymentARTypeArr = [
   <#if paymentARTypeList?exists>
       	<#list paymentARTypeList as paymentType>
       		{
       			paymentTypeId : "${paymentType.paymentTypeId}",
       			description : "${StringUtil.wrapString(paymentType.get('description',locale))}"
			},
   		</#list>
	</#if>
];
globalVar.paymentAPTypeArr = [
   <#if paymentAPTypeList?exists>
       	<#list paymentAPTypeList as paymentType>
       		{
       			paymentTypeId : "${paymentType.paymentTypeId}",
       			description : "${StringUtil.wrapString(paymentType.get('description',locale))}"
			},
   		</#list>
	</#if>
];

globalVar.paymentMethodArr = [
   <#if listPaymentMethods?exists>
       	<#list listPaymentMethods as paymentMethod>
       		{
       			paymentMethodId : "${paymentMethod.paymentMethodId}",
       			description : "${StringUtil.wrapString(paymentMethod.get('description',locale))}"
			},
   		</#list>
	</#if>
];
globalVar.invoiceTypeArr = [
   <#if invoiceTypeList?exists>
       	<#list invoiceTypeList as invoiceType>
       		{
       			invoiceTypeId : "${invoiceType.invoiceTypeId}",
       			description : "${StringUtil.wrapString(invoiceType.get('description',locale))}"
			},
   		</#list>
	</#if>
];

globalVar.uomArr = [
     <#if uomList?exists>
     	<#list uomList as uom>
     		<#if uom?exists && uom.uomId == "USD" || uom.uomId == "EUR" || uom.uomId == "VND">
     		{
     			uomId : "${uom.uomId}",
     			description : "${StringUtil.wrapString(uom.get('description'))}",
     			abbreviation : "${StringUtil.wrapString(uom.get('abbreviation'))}",
 			},
 			</#if>
 		</#list>
 	  </#if>
];
<#assign organizationParty = delegator.findOne("PartyGroup", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", '${userLogin.lastOrg}'), false)>
<#assign party_preference = delegator.findByAnd("PartyAcctgPreference", {"partyId" : "${userLogin.lastOrg}"},null,false) !>
<#if party_preference?exists && party_preference?size != 0>
	globalVar.preferenceCurrencyUom = '${party_preference.get(0).baseCurrencyUomId?if_exists}';
<#else>
	globalVar.preferenceCurrencyUom = '${defaultCurrencyUomId?if_exists}';
</#if>
uiLabelMap.filterchoosestring = "${StringUtil.wrapString(uiLabelMap.filterchoosestring)}";
uiLabelMap.BACCPayeeName = "${StringUtil.wrapString(uiLabelMap.BACCPayeeName)}";
uiLabelMap.BACCPayers = "${StringUtil.wrapString(uiLabelMap.BACCPayers)}";
uiLabelMap.BACCProductId = "${StringUtil.wrapString(uiLabelMap.BACCProductId)}";
uiLabelMap.BACCProductName = "${StringUtil.wrapString(uiLabelMap.BACCProductName)}";
uiLabelMap.BACCCreateAPPayment = "${StringUtil.wrapString(uiLabelMap.BACCCreateAPPayment)}";
uiLabelMap.BACCCreateARPayment = "${StringUtil.wrapString(uiLabelMap.BACCCreateARPayment)}";
uiLabelMap.BACCInvoiceId = "${StringUtil.wrapString(uiLabelMap.BACCInvoiceId)}";
uiLabelMap.BACCInvoiceTypeId = "${StringUtil.wrapString(uiLabelMap.BACCInvoiceTypeId)}";
uiLabelMap.BACCInvoiceDateShort = "${StringUtil.wrapString(uiLabelMap.BACCInvoiceDateShort)}";
uiLabelMap.BACCInvoiceDateSystem = "${StringUtil.wrapString(uiLabelMap.BACCInvoiceDateSystem)}";
uiLabelMap.CommonStatus = "${StringUtil.wrapString(uiLabelMap.CommonStatus)}";
uiLabelMap.BACCTotal = "${StringUtil.wrapString(uiLabelMap.BACCTotal)}";
uiLabelMap.BACCPaymentId = "${StringUtil.wrapString(uiLabelMap.BACCPaymentId)}";
uiLabelMap.BACCListInvoices = "${StringUtil.wrapString(uiLabelMap.BACCListInvoices)}";
uiLabelMap.ValueMustBeGreateThanZero = "${StringUtil.wrapString(uiLabelMap.ValueMustBeGreateThanZero)}";
uiLabelMap.FieldRequired = "${StringUtil.wrapString(uiLabelMap.FieldRequired)}";
uiLabelMap.BACCInvoice = "${StringUtil.wrapString(uiLabelMap.BACCInvoice?lower_case?capitalize)}";
uiLabelMap.CommonIdCode = "${StringUtil.wrapString(uiLabelMap.CommonIdCode?lower_case)}";
uiLabelMap.BACCInvoiceIsCreatedPayment_PaymentCreateConfirm = "${StringUtil.wrapString(uiLabelMap.BACCInvoiceIsCreatedPayment_PaymentCreateConfirm)}";
uiLabelMap.BACCCreatePaymentForVoucherInvoiceConfirm = "${StringUtil.wrapString(uiLabelMap.BACCCreatePaymentForVoucherInvoiceConfirm)}";
uiLabelMap.CommonSubmit = "${StringUtil.wrapString(uiLabelMap.CommonSubmit)}";
uiLabelMap.CommonCancel = "${StringUtil.wrapString(uiLabelMap.CommonCancel)}";
uiLabelMap.CommonClose = "${StringUtil.wrapString(uiLabelMap.CommonClose)}";
uiLabelMap.CommonYes = "${StringUtil.wrapString(uiLabelMap.CommonYes)}";
uiLabelMap.CommonNo = "${StringUtil.wrapString(uiLabelMap.CommonNo)}";
uiLabelMap.BACCUpdatePaymentAmountAfterDelete = "${StringUtil.wrapString(uiLabelMap.BACCUpdatePaymentAmountAfterDelete)}";
uiLabelMap.BACCListInvoiceIsEmpty = "${StringUtil.wrapString(uiLabelMap.BACCListInvoiceIsEmpty)}";
uiLabelMap.VoucherNumber = "${StringUtil.wrapString(uiLabelMap.VoucherNumber)}";
uiLabelMap.BACCIssueDate = "${StringUtil.wrapString(uiLabelMap.BACCIssueDate)}";
uiLabelMap.PublicationReceivingDate = "${StringUtil.wrapString(uiLabelMap.PublicationReceivingDate)}";
uiLabelMap.BACCPaymentAmount = "${StringUtil.wrapString(uiLabelMap.BACCPaymentAmount)}";
uiLabelMap.BACCCheckPaymentAmount = "${StringUtil.wrapString(uiLabelMap.BACCCheckPaymentAmount)}";
uiLabelMap.BACCAmountApplied = "${StringUtil.wrapString(uiLabelMap.BACCAmountApplied)}";
uiLabelMap.BACCNotifyCreatePaymentFromVoucher = "${StringUtil.wrapString(uiLabelMap.BACCNotifyCreatePaymentFromVoucher)}";
</script>