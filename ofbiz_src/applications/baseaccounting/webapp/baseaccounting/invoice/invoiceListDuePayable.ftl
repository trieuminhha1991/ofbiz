<@jqGridMinimumLib/>
<script type="text/javascript" src="/accresources/js/acc.utils.js"></script>
<link rel="stylesheet" href="/aceadmin/assets/css/colorbox.css" />
<script type="text/javascript" src="/aceadmin/assets/js/jquery.colorbox-min.js"></script>
<script type="text/javascript">
var globalVar = {};
var uiLabelMap = {};
globalVar.businessType = "${businessType}";
uiLabelMap.VoucherForm = "${StringUtil.wrapString(uiLabelMap.VoucherForm)}";
uiLabelMap.VoucherSerial = "${StringUtil.wrapString(uiLabelMap.VoucherSerial)}";
uiLabelMap.VoucherNumber = "${StringUtil.wrapString(uiLabelMap.VoucherNumber)}";
uiLabelMap.BACCIssueDate = "${StringUtil.wrapString(uiLabelMap.BACCIssueDate)}";
uiLabelMap.ReceivingVoucherDate = "${StringUtil.wrapString(uiLabelMap.ReceivingVoucherDate)}";
uiLabelMap.PublicationVoucherDate = "${StringUtil.wrapString(uiLabelMap.PublicationVoucherDate)}";
uiLabelMap.BACCInvoiceTypeId = "${StringUtil.wrapString(uiLabelMap.BACCInvoiceTypeId)}";
uiLabelMap.HRCommonAttactFile = "${StringUtil.wrapString(uiLabelMap.HRCommonAttactFile)}";
uiLabelMap.AmountNotIncludeTax = "${StringUtil.wrapString(uiLabelMap.AmountNotIncludeTax)}";
uiLabelMap.CommonTax = "${StringUtil.wrapString(uiLabelMap.CommonTax)}";
uiLabelMap.CommonTotal = "${StringUtil.wrapString(uiLabelMap.CommonTotal)}";
uiLabelMap.ListVouchers = "${StringUtil.wrapString(uiLabelMap.ListVouchers)}";
uiLabelMap.ListVoucherOfInvoice = "${StringUtil.wrapString(uiLabelMap.ListVoucherOfInvoice)}";
uiLabelMap.BSNoDataToExport = "${StringUtil.wrapString(uiLabelMap.BSNoDataToExport)}";

<#assign nowTimestamp = Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp()/>
<#assign monthStart = Static["org.ofbiz.base.util.UtilDateTime"].getMonthStart(nowTimestamp).getTime()/>
<#assign monthEnd = Static["org.ofbiz.base.util.UtilDateTime"].getMonthEnd(prevMonthStart, timeZone, locale).getTime()/>
</script>
<#include "invoiceListFilter.ftl"/>
<#include "invoiceGridDuePayable.ftl" />

<script>
    $( document ).ready(function(){
        filter.initFilter();
        filter.bindEvent();
    });
</script>