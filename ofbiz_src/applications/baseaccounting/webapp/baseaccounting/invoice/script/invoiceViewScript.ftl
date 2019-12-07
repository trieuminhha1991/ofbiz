<@jqGridMinimumLib />
<link rel="stylesheet" href="/aceadmin/assets/css/colorbox.css"/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxformattedinput.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxloader.js"></script>
<script type="text/javascript" src="/aceadmin/assets/js/jquery.colorbox-min.js"></script>
<script type="text/javascript" src="/accresources/js/acc.bootbox.js"></script>
<script type="text/javascript" src="/accresources/js/acc.utils.js"></script>

<script type="text/javascript">
    <#assign partyNameFrom = Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, invoice.partyIdFrom?if_exists, true, true)?if_exists>
    <#assign partyNameTo = Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, invoice.partyId?if_exists, true, true)?if_exists>
    <#assign invoiceTaxInfoList = delegator.findByAnd("InvoiceTaxInfoAndGeo", Static["org.ofbiz.base.util.UtilMisc"].toMap("invoiceId", parameters.invoiceId), null, false)/>
    <#if invoiceTaxInfoList?has_content>
        <#assign invoiceTaxInfo = invoiceTaxInfoList.get(0)/>
    </#if>
    <#assign invoiceRootType = Static["com.olbius.acc.utils.accounts.AccountUtils"].getRootInvoiceType(delegator, invoice.invoiceTypeId)!/>

    var globalVar = {};
    var uiLabelMap = {};
    globalVar.currencyUomId = "${invoice.currencyUomId?if_exists}";
    globalVar.invoiceAmount = "${invoiceAmount?if_exists}";
    globalVar.notAppliedAmount = "${notAppliedAmount?if_exists}";
    globalVar.invoiceId = "${parameters.invoiceId}";
    globalVar.conversionFactor = "${invoice.conversionFactor?if_exists}";
    <#if invoice.invoiceDate?exists>
    globalVar.issuedDate = ${invoice.invoiceDate.getTime()};
    globalVar.invoiceDate = ${invoice.invoiceDate.getTime()};
    </#if>
    <#if invoice.paidDate?exists>
    globalVar.paidDate = ${invoice.paidDate.getTime()};
    </#if>
    <#if invoice.dueDate?exists>
    globalVar.dueDate = ${invoice.dueDate.getTime()};
    </#if>
    globalVar.description = '${Static["com.olbius.acc.utils.accounts.AccountUtils"].replaceSpecialCharToHtml(StringUtil.wrapString(invoice.description?if_exists))}';

    globalVar.partyNameFrom = "${StringUtil.wrapString(partyNameFrom?if_exists)}";
    globalVar.partyIdFrom = "${StringUtil.wrapString(invoice.partyIdFrom?if_exists)}";
    globalVar.partyNameTo = "${StringUtil.wrapString(partyNameTo?if_exists)}";
    globalVar.partyId = "${StringUtil.wrapString(invoice.partyId?if_exists)}";
    globalVar.invoiceId = '${parameters.invoiceId}';
    globalVar.invoiceTypeId = '${invoice.invoiceTypeId}';

    <#if Static["com.olbius.acc.utils.accounts.AccountUtils"].checkInvoiceHaveBilling(delegator, parameters.invoiceId)>
    globalVar.isInvoiceHaveBilling = true;
    <#else>
    globalVar.isInvoiceHaveBilling = false;
    </#if>

    <#if invoiceTaxInfo?exists>
    globalVar.invoiceTaxPartyName = "${StringUtil.wrapString(invoiceTaxInfo.partyName?if_exists)}";
    globalVar.invoiceTaxTaxCode = "${StringUtil.wrapString(invoiceTaxInfo.taxCode?if_exists)}";
    globalVar.invoiceTaxCountryGeoId = "${StringUtil.wrapString(invoiceTaxInfo.countryGeoId?if_exists)}";
    globalVar.invoiceTaxStateGeoId = "${StringUtil.wrapString(invoiceTaxInfo.stateGeoId?if_exists)}";
    globalVar.invoiceTaxAddress = "${StringUtil.wrapString(invoiceTaxInfo.address?if_exists)}";
    globalVar.invoiceTaxPhoneNbr = "${StringUtil.wrapString(invoiceTaxInfo.phoneNbr?if_exists)}";
    </#if>

    globalVar.countryGeoArr = [
    <#if countryGeoList?has_content>
        <#list countryGeoList as geo>
            {
                geoId: '${geo.geoId}',
                geoName: "${StringUtil.wrapString(geo.geoName)}"
            },
        </#list>
    </#if>];
    <#if defaultCountryGeoId?exists>
    globalVar.defaultCountryGeoId = "${defaultCountryGeoId}";
    </#if>
    var notApply = "#{notAppliedAmount}".replace(",",".");
    globalVar.notAppliedAmount = Number(notApply);
    globalVar.locale = "${locale}";
    uiLabelMap.DropFilesHereOrClickToChoose = "${StringUtil.wrapString(uiLabelMap.DropFilesHereOrClickToChoose)}";
    uiLabelMap.CreatePaymentApplicationConfirm = "${StringUtil.wrapString(uiLabelMap.CreatePaymentApplicationConfirm)}";
    uiLabelMap.CommonChooseFile = "${StringUtil.wrapString(uiLabelMap.CommonChooseFile)}";
    uiLabelMap.wgeditonly = "${StringUtil.wrapString(uiLabelMap.wgeditonly)}";
    uiLabelMap.ValueMustBeGreateThanZero = "${StringUtil.wrapString(uiLabelMap.ValueMustBeGreateThanZero)}";
    uiLabelMap.CreateInvoiceVoucherConfirm = "${StringUtil.wrapString(uiLabelMap.CreateInvoiceVoucherConfirm)}";
    uiLabelMap.UpdateInvoiceVoucherConfirm = "${StringUtil.wrapString(uiLabelMap.UpdateInvoiceVoucherConfirm)}";
    uiLabelMap.BACCSeqId = "${StringUtil.wrapString(uiLabelMap.BACCSeqId)}";
    uiLabelMap.BACCInvoiceItemType = "${StringUtil.wrapString(uiLabelMap.BACCInvoiceItemType)}";
    uiLabelMap.BACCProduct = "${StringUtil.wrapString(uiLabelMap.BACCProduct)}";
    uiLabelMap.BACCQuantity = "${StringUtil.wrapString(uiLabelMap.BACCQuantity)}";
    uiLabelMap.BACCUnitPrice = "${StringUtil.wrapString(uiLabelMap.BACCUnitPrice)}";
    uiLabelMap.BACCTotal = "${StringUtil.wrapString(uiLabelMap.BACCTotal)}";
    uiLabelMap.InvoiceValueNotTaxInSystem = "${StringUtil.wrapString(uiLabelMap.InvoiceValueNotTaxInSystem)}";
    uiLabelMap.accTaxAuthorityRateTypeId = "${StringUtil.wrapString(uiLabelMap.accTaxAuthorityRateTypeId)}";
    uiLabelMap.InvoiceValueTaxInSystem = "${StringUtil.wrapString(uiLabelMap.InvoiceValueTaxInSystem)}";
    uiLabelMap.BACCAmountTotal = "${StringUtil.wrapString(uiLabelMap.BACCAmountTotal)}";
    uiLabelMap.BACCUpdateConfirm = "${StringUtil.wrapString(uiLabelMap.BACCUpdateConfirm)}";
    uiLabelMap.InvoiceAmountTaxActualAndSystemDiff = "${StringUtil.wrapString(uiLabelMap.InvoiceAmountTaxActualAndSystemDiff)}";
    uiLabelMap.InvoiceAmountActualAndSystemDiffNotExceed = "${StringUtil.wrapString(uiLabelMap.InvoiceAmountActualAndSystemDiffNotExceed)}";
    uiLabelMap.VerifyInvoiceConfirm = "${StringUtil.wrapString(uiLabelMap.VerifyInvoiceConfirm)}";
    uiLabelMap.BACCOrganizationId = "${StringUtil.wrapString(uiLabelMap.BACCOrganizationId)}";
    uiLabelMap.BACCFullName = "${StringUtil.wrapString(uiLabelMap.BACCFullName)}";
    uiLabelMap.CommonClose = "${StringUtil.wrapString(uiLabelMap.CommonClose)}";
    uiLabelMap.FieldRequired = "${StringUtil.wrapString(uiLabelMap.FieldRequired)}";
    uiLabelMap.CommonId = "${StringUtil.wrapString(uiLabelMap.CommonId)}";
    uiLabelMap.CommonDescription = "${StringUtil.wrapString(uiLabelMap.CommonDescription)}";
    uiLabelMap.BACCGlAccountId = "${StringUtil.wrapString(uiLabelMap.BACCGlAccountId)}";
    uiLabelMap.BACCValueMustGreaterOrEqualThanZero = "${StringUtil.wrapString(uiLabelMap.BACCValueMustGreaterOrEqualThanZero)}";
    uiLabelMap.BSAreYouSureYouWantToUpdate = "${StringUtil.wrapString(uiLabelMap.BSAreYouSureYouWantToUpdate)}";
    uiLabelMap.BACCCreateNewConfirm = "${StringUtil.wrapString(uiLabelMap.BACCCreateNewConfirm)}";
    uiLabelMap.BACCCreatePOInvItemAdjustmentConfirm = '${StringUtil.wrapString(uiLabelMap.BACCCreatePOInvItemAdjustmentConfirm)}';
    uiLabelMap.BACCConfirmCancelInvoiceTransaction = '${StringUtil.wrapString(uiLabelMap.BACCConfirmCancelInvoiceTransaction)}';
    uiLabelMap.ProductId = '${StringUtil.wrapString(uiLabelMap.ProductId)}';
    uiLabelMap.ProductName = '${StringUtil.wrapString(uiLabelMap.ProductName)}';
    uiLabelMap.ActualReceivedQuantitySum = '${StringUtil.wrapString(uiLabelMap.ActualReceivedQuantitySum)}';
    uiLabelMap.Unit = '${StringUtil.wrapString(uiLabelMap.Unit)}';
    uiLabelMap.ListProduct = '${StringUtil.wrapString(uiLabelMap.ListProduct)}';
    uiLabelMap.QuantityAfterEdited = '${StringUtil.wrapString(uiLabelMap.QuantityAfterEdited)}';
    uiLabelMap.BSAdjustment = '${StringUtil.wrapString(uiLabelMap.BSAdjustment)}';
    uiLabelMap.BACCNotChange = '${StringUtil.wrapString(uiLabelMap.BACCNotChange)}';
    uiLabelMap.ValueMustBeEqualOrLessThanInvoiceQty = '${StringUtil.wrapString(uiLabelMap.ValueMustBeEqualOrLessThanInvoiceQty)}';
    uiLabelMap.CannotAdjustExceedInvoiceQuantity = '${StringUtil.wrapString(uiLabelMap.CannotAdjustExceedInvoiceQuantity)}';
    uiLabelMap.AreYouSureWantToChangeReceiveNote = '${StringUtil.wrapString(uiLabelMap.AreYouSureWantToChangeReceiveNote)}';
    uiLabelMap.BACCInputPrice = '${StringUtil.wrapString(uiLabelMap.BACCInputPrice)}';
    uiLabelMap.BACCModifiedPrice = '${StringUtil.wrapString(uiLabelMap.BACCModifiedPrice)}';
    uiLabelMap.BACCUserLoginModified = '${StringUtil.wrapString(uiLabelMap.BACCUserLoginModified)}';
    uiLabelMap.BACCCancelDeliveryNoteWarning = '${StringUtil.wrapString(uiLabelMap.BACCCancelDeliveryNoteWarning)}';
    uiLabelMap.BACCQuantityOnInvoice = '${StringUtil.wrapString(uiLabelMap.BACCQuantityOnInvoice)}';
    uiLabelMap.LogExportQuantity = '${StringUtil.wrapString(uiLabelMap.LogExportQuantity)}';
    uiLabelMap.BSUnitPrice = "${StringUtil.wrapString(uiLabelMap.BSUnitPrice)}";
    uiLabelMap.BSListProduct = '${StringUtil.wrapString(uiLabelMap.BSListProduct)}';
    uiLabelMap.BACCPleaseInputConversionFactor = '${StringUtil.wrapString(uiLabelMap.BACCPleaseInputConversionFactor)}';
    uiLabelMap.BACCValueAppliedMustLessThanAmountNotApplied = '${StringUtil.wrapString(uiLabelMap.BACCValueAppliedMustLessThanAmountNotApplied)}';

</script>