<#include "invoiceViewHeader.ftl"/>
<#if invoice.statusId == "INVOICE_IN_PROCESS">
	<#include "invoiceEditHeader.ftl"/>
</#if>
<#if businessType == "AP" && Static["com.olbius.acc.utils.accounts.AccountUtils"].checkInvoiceHaveBilling(delegator, parameters.invoiceId)>
	<#include "invoiceReceiveNote.ftl"/>
</#if>
<#include "invoiceViewItems.ftl" />
