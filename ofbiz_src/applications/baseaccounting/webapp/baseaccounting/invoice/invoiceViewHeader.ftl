<style>
	.control-label-desc{
		font-weight : 5em !important;
		font-size :14px !important;
	}
</style>
<#assign invoiceTaxInfoList = delegator.findByAnd("InvoiceTaxInfoAndGeo", Static["org.ofbiz.base.util.UtilMisc"].toMap("invoiceId", parameters.invoiceId), null, false)/>
<#if invoiceTaxInfoList?has_content>
	<#assign invoiceTaxInfo = invoiceTaxInfoList.get(0)/> 
</#if>
<#assign invoiceRootType = Static["com.olbius.acc.utils.accounts.AccountUtils"].getRootInvoiceType(delegator, invoice.invoiceTypeId)!/>
<#assign isEditbleDeliveryNote = (businessType == "AR" && Static["com.olbius.acc.utils.accounts.AccountUtils"].checkInvoiceHaveBilling(delegator, parameters.invoiceId))/>

<div class="row-fluid" style="position: relative;">
	<#if invoice.statusId == "INVOICE_IN_PROCESS">
		<div style="top: -10px; right: 5px; position: absolute; background: rgba(182, 189, 179, 0.3); padding: 5px 20px">
			<a href="javascript:void(0)" id="editInvoiceBtn"><i class="icon-edit"></i>${uiLabelMap.CommonEdit}</a>
		</div>
		<#if businessType == "AP" && Static["com.olbius.acc.utils.accounts.AccountUtils"].checkInvoiceHaveBilling(delegator, parameters.invoiceId)>
			<div style="top: -10px; right: 130px; position: absolute; background:  #fee188; padding: 5px 20px">
				<a href="javascript:void(0)" id="editReceiveNoteBtn"><i class="fa fa-file-pdf-o"></i>${uiLabelMap.ReceiveNote}</a>
			</div>
		</#if>
	</#if>
    <#if invoice.statusId == "INVOICE_READY">
        <#if isEditbleDeliveryNote>
            <div style="top: -10px; right: 5px; position: absolute; background:  #fee188; padding: 5px 20px">
                <a href="javascript:void(0)" id="editDeliveryNoteBtn"><i class="fa fa-file-pdf-o"></i>${uiLabelMap.DeliveryNote}</a>
            </div>
        </#if>
    </#if>
	<h3 style="text-align:center;font-weight:bold; padding:0;line-height:20px">
		${uiLabelMap.BACCINVOICE}
	</h3>
	<div class="row-fluid" style="text-align:center;">
		<span>
			<#if Static["com.olbius.acc.utils.accounts.AccountUtils"].isInvoiceTransPosted(delegator, invoice.invoiceId)>
				<i>(${StringUtil.wrapString(uiLabelMap.BACCTransactionPosted)})</i>
			</#if>
		</span>
	</div>
</div>
<div class="row-fluid">
	<div class="form-horizontal form-window-content-custom label-text-left content-description" style="margin:10px">
		<div class="row-fluid">
			<div class="span6">
				<div class="row-fluid">
					<div class="div-inline-block">
						<label>${uiLabelMap.BACCInvoiceId}:</label>
					</div>
					<div class="div-inline-block">
						<span><i>${parameters.invoiceId?if_exists?default('')}</i></span>
					</div>
				</div>
				<div class="row-fluid">
					<div class="div-inline-block">
						<label>${uiLabelMap.BACCInvoiceTypeId}:</label>
					</div>
					<div class="div-inline-block">
						<#assign invoiceType = delegator.findOne("InvoiceType",{"invoiceTypeId" : "${invoice.invoiceTypeId?if_exists}"},false)!>
						<span><i>${StringUtil.wrapString(invoiceType.get("description",locale)?if_exists)}</i></span>
					</div>
				</div>
				<div class="row-fluid">
					<div class="div-inline-block">
						<label>${uiLabelMap.BACCStatusId}:</label>
					</div>
					<div class="div-inline-block">
						<#if invoice.newStatusId?exists>
							<#assign invoiceNewStatus = delegator.findOne("StatusItem",{"statusId" : "${invoice.newStatusId}"},false)!>
							<span><i>${StringUtil.wrapString(invoiceNewStatus.get("description", locale))}</i></span>
						</#if>
					</div>
				</div>
				<div class="row-fluid">
					<div class="div-inline-block">
						<label>${uiLabelMap.BACCTotal}:</label>
					</div>
					<div class="div-inline-block">
						<#if invoiceAmount?has_content && invoiceAmount?exists>
							<#if invoice.currencyUomId?has_content>
								<#assign currencyUomId = invoice.currencyUomId />
							<#else>
								<#assign currencyUomId = "VND" />
							</#if>
						   	<#assign total = Static["org.ofbiz.base.util.UtilFormatOut"].formatCurrency(invoiceAmount?if_exists?double, currencyUomId, locale, 2) />
						   	<#assign payrollTotal = Static["org.ofbiz.base.util.UtilFormatOut"].formatCurrency(payInvoiceAmount?if_exists?double, currencyUomId, locale, 2) />
						   	<span><i id="viewInvoiceTotal">${total?if_exists?default(0)}
							   	<#if invoice.invoiceTypeId="PAYROL_INVOICE">
							   		( ${uiLabelMap.BACCEmplPayroll} ${payrollTotal?if_exists?default(0)} )
							   	</#if>
						   	</i></span>
					   	</#if>
					</div>
				</div>
				<div class="row-fluid">
					<div class="div-inline-block">
						<label>${uiLabelMap.BACCInvoiceDate}:</label>
					</div>
					<div class="div-inline-block" >
						<span><i id="viewInvoiceDate">
						<#if invoice.invoiceDate?has_content>
						   	<#assign invoiceDate = Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(invoice.invoiceDate?if_exists, "dd/MM/yyyy - HH:mm:ss", locale, timeZone) />
					   		${invoiceDate?if_exists}
						</#if>
						</i></span>
					</div>
				</div>
				<div class="row-fluid">
					<div class="div-inline-block">
						<label>${uiLabelMap.BACCPaidDate}:</label>
					</div>
					<div class="div-inline-block">
						<span><i id="viewPaidDate">
						<#if invoice.paidDate?has_content>
						   	<#assign paidDate = Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(invoice.paidDate?if_exists, "dd/MM/yyyy - HH:mm:ss", locale, timeZone) />
					   		${paidDate?if_exists}
						</#if>
						</i></span>
					</div>
				</div>
				<div class="row-fluid">
					<div class="div-inline-block">
						<label>${uiLabelMap.BACCDueDate}:</label>
					</div>
					<div class="div-inline-block">
						<span><i id="viewDueDate">
						<#if invoice.dueDate?exists>
							<#assign dueDate = Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(invoice.dueDate?if_exists, "dd/MM/yyyy - HH:mm:ss", locale, timeZone) />
							${dueDate?if_exists?default('')}
						</#if>
						</i></span>
					</div>
				</div>

            <#if invoice.conversionFactor?exists && invoice.conversionFactor?has_content && invoice.currencyUomId?exists && invoice.currencyUomId != 'VND'>
                <div class="row-fluid" class="hide">
                    <div class="div-inline-block">
                        <label>${uiLabelMap.BACCExchangedRate}:</label>
                    </div>
                    <div class="div-inline-block">
                        <#assign currencyUomId = "VND" />
                        <#assign total = Static["org.ofbiz.base.util.UtilFormatOut"].formatCurrency(invoice.conversionFactor?if_exists?double, currencyUomId, locale, 2) />
                        <span><i id="viewConversionFactor">${total?if_exists?default(0)}
						   	</i></span>
                    </div>
                </div>
            </#if>
				<#--<!--
				<div class="row-fluid">
					<div class="div-inline-block">
						<label>${uiLabelMap.BACCRoleTypeId}:</label>
					</div>
					<div class="div-inline-block">
						<#assign listRoleType = delegator.findByAnd("RoleType",null,null,false) !>
						<#if listRoleType?has_content>
							<#list listRoleType as role>
								<#if role.roleTypeId == invoice.roleTypeId?if_exists?default('')>
									<span><i>${role.get('description',locale)?default('')}</i></span>
								</#if>
							</#list>
						</#if>
					</div>
				</div>
				-->
			</div><!--./span6-->
			<div class="span6">
				<#--<!-- 
				<div class="row-fluid">
					<div class="div-inline-block">
						<label>${uiLabelMap.BACCReferenceNum}:</label>
					</div>
					<div class="div-inline-block">
						<span><i>${invoice.referenceNumber?if_exists}</i></span>
					</div>
				</div>
				-->
				<div class="row-fluid">
					<div class="div-inline-block">
						<label>${uiLabelMap.BACCInvoiceFromParty}:</label>
					</div>
					<div class="div-inline-block">
						<span><i id="viewPartyFromName">${partyNameFrom?if_exists} [${invoice.partyIdFrom?if_exists}]</i></span>
					</div>
				</div>
				<#if invoiceRootType == "PURCHASE_INVOICE">
					<div class="row-fluid">
						<div class="div-inline-block">
							<label>${uiLabelMap.SellerName}:</label>
						</div>
						<div class="div-inline-block">
							<span>
								<i id="viewPartyName"><#if invoiceTaxInfo?exists>${invoiceTaxInfo.partyName?if_exists}</#if></i>
							</span>
						</div>
					</div>
					<div class="row-fluid">
						<div class="div-inline-block">
							<label>${uiLabelMap.BACCTaxCode}:</label>
						</div>
						<div class="div-inline-block">
							<span><i id="viewTaxCode"><#if invoiceTaxInfo?exists>${invoiceTaxInfo.taxCode?if_exists}</#if></i></span>
						</div>
					</div>
					<div class="row-fluid">
						<div class="div-inline-block">
							<label>${uiLabelMap.CommonAddress1}:</label>
						</div>
						<div class="div-inline-block">
							<span>
								<i id="viewTaxInfoAddr">
								<#if invoiceTaxInfo?exists>
									<#if invoiceTaxInfo.address?exists>${invoiceTaxInfo.address}</#if><#if invoiceTaxInfo.stateGeoName?exists>, ${invoiceTaxInfo.stateGeoName}</#if><#if invoiceTaxInfo.countryGeoName?exists>, ${invoiceTaxInfo.countryGeoName}</#if>
								</#if>	
								</i>
							</span>
						</div>
					</div>
					<div class="row-fluid">
						<div class="div-inline-block">
							<label>${uiLabelMap.PartyPhoneNumber}:</label>
						</div>
						<div class="div-inline-block">
							<span><i id="viewPhoneNbr"><#if invoiceTaxInfo?exists>${invoiceTaxInfo.phoneNbr?if_exists}</#if></i></span>
						</div>
					</div>
				</#if>
				<div class="row-fluid">
					<div class="div-inline-block">
						<label>${uiLabelMap.BACCInvoiceToParty}:</label>
					</div>
					<div class="div-inline-block">
						<span><i id="viewPartyToName">${partyNameTo?if_exists} [${invoice.partyId?if_exists}]</i></span>
					</div>
				</div>
				<#if invoiceRootType == "SALES_INVOICE">
					<div class="row-fluid">
						<div class="div-inline-block">
							<label>${uiLabelMap.BuyerName}:</label>
						</div>
						<div class="div-inline-block">
							<span><i id="viewPartyName"><#if invoiceTaxInfo?exists>${invoiceTaxInfo.partyName?if_exists}</#if></i></span>
						</div>
					</div>
					<div class="row-fluid">
						<div class="div-inline-block">
							<label>${uiLabelMap.BACCTaxCode}:</label>
						</div>
						<div class="div-inline-block">
							<span><i id="viewTaxCode"><#if invoiceTaxInfo?exists>${invoiceTaxInfo.taxCode?if_exists}</#if></i></span>
						</div>
					</div>
					<div class="row-fluid">
						<div class="div-inline-block">
							<label>${uiLabelMap.CommonAddress1}:</label>
						</div>
						<div class="div-inline-block">
							<span>
								<i id="viewTaxInfoAddr">
								<#if invoiceTaxInfo?exists>
									<#if invoiceTaxInfo.address?exists>${invoiceTaxInfo.address}</#if><#if invoiceTaxInfo.stateGeoName?exists>, ${invoiceTaxInfo.stateGeoName}</#if><#if invoiceTaxInfo.countryGeoName?exists>, ${invoiceTaxInfo.countryGeoName}</#if>
								</#if>
								</i>
							</span>
						</div>
					</div>
					<div class="row-fluid">
						<div class="div-inline-block">
							<label>${uiLabelMap.PartyPhoneNumber}:</label>
						</div>
						<div class="div-inline-block">
							<span><i id="viewPhoneNbr"><#if invoiceTaxInfo?exists>${invoiceTaxInfo.phoneNbr?if_exists}</#if></i></span>
						</div>
					</div>
				</#if>
				<div class="row-fluid">
					<div class="div-inline-block">
						<label>${uiLabelMap.BACCDescription}:</label>
					</div>
					<div class="div-inline-block">
						<span><i>${invoice.description?if_exists?default('')}</i></span>
					</div>
				</div>
			</div><!--./span6-->
		</div>
	</div>
</div>

<#if isEditbleDeliveryNote && invoice.statusId == "INVOICE_READY">
    <#include "invoiceDeliveryNote.ftl"/>
</#if>
