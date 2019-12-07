<#assign partyNameResultFrom = dispatcher.runSync("getPartyNameForDate",Static["org.ofbiz.base.util.UtilMisc"].toMap("userLogin",userLogin,"partyId",invoice.partyIdFrom,"compareDate",invoice.invoiceDate,"lastNameFirst" ,"Y" )) />
<#assign partyNameResultTo = dispatcher.runSync("getPartyNameForDate",Static["org.ofbiz.base.util.UtilMisc"].toMap("userLogin",userLogin,"partyId",invoice.partyId,"compareDate",invoice.invoiceDate,"lastNameFirst" ,"Y" )) />
<style>
	.custom{
	    font-weight: 400;
	    font-size: 14px;
	}
	.color{
		color: #037c07!important;
	}
	.content{
		white-space : pre-wrap !important;
	}
</style>
<div class="row-fluid">
	<div class="span12">
		<div class="row-fluid">
			<div class="span6">
				<div  class="row-fluid margin-bottom10">
					<div class="span5 align-right custom">
						${uiLabelMap.FormFieldTitle_invoiceTypeId}
					</div>
					<div class="span7 custom color custom">
						<#assign invoiceType = delegator.findOne("InvoiceType",{"invoiceTypeId" : "${invoice.invoiceTypeId?if_exists}"},false)!>
						${StringUtil.wrapString(invoiceType.get("description",locale)?if_exists)}
					</div>
				</div>
				<div  class="row-fluid margin-bottom10">	
					<div class="span5 align-right custom ">
						${uiLabelMap.CommonDescription}
					</div>
					<div class="span7">	
						${invoice.description?if_exists}
					</div>
				</div>
				<div  class="row-fluid margin-bottom10">	
					<div class="span5 align-right custom ">
						${uiLabelMap.FormFieldTitle_partyIdFrom}
					</div>
					<div class="span7">
						<a  class="custom content" href="/partymgr/control/viewprofile?partyId=${invoice.partyIdFrom?default("")}">${partyNameResultFrom.fullName?if_exists} [${invoice.partyIdFrom?if_exists}]</a>
					</div>
				</div>
				<div  class="row-fluid margin-bottom10">	
					<div class="span5 align-right custom ">
						${uiLabelMap.FormFieldTitle_roleTypeId}
					</div>
					<div class="span7 ">
						${invoice.roleTypeId?if_exists}
					</div>
				</div>
				<div  class="row-fluid margin-bottom10">	
					<div class="span5 align-right custom ">
						${uiLabelMap.FormFieldTitle_invoiceDate}
					</div>
					<div class="span7 custom color">
						<#if invoice.invoiceDate?has_content>
							${invoice.invoiceDate?string["dd/MM/yyyy"]}
						<#else>
							
						</#if>
					</div>
				</div>
				<div  class="row-fluid margin-bottom10">	
					<div class="span5 align-right custom">
						${uiLabelMap.FormFieldTitle_total}
					</div>
					<div class="span7 custom color">
						<@ofbizCurrency amount=invoiceAmount?if_exists/>${invoice.currencyUomId?if_exists}
					</div>
				</div>
				<div  class="row-fluid margin-bottom10">	
					<div class="span5 align-right custom ">
						${uiLabelMap.FormFieldTitle_referenceNum}
					</div>
					<div class="span7 ">
						${invoice.referenceNumber?if_exists}
					</div>
				</div>	
			</div>
			<div class="span6">
				<div  class="row-fluid margin-bottom10">
					<div class="span5 align-right custom ">
						${uiLabelMap.CommonStatus}
					</div>
					<div class="span7 color custom" >
						<#assign stt = delegator.findOne("StatusItem",{"statusId" : "${invoice.statusId?if_exists}"},false)/>
						${StringUtil.wrapString(stt.get("description",locale)?if_exists)}	
					</div>
				</div>
				<div  class="row-fluid margin-bottom10">	
					<div class="span5 align-right custom ">
						${uiLabelMap.FormFieldTitle_invoiceMessage}
					</div>
					<div class="span7 ">
						${invoice.invoiceMessage?if_exists}
					</div>
				</div>
				<div  class="row-fluid margin-bottom10">	
					<div class="span5 align-right custom ">
						${uiLabelMap.FormFieldTitle_partyIdTo}
					</div>
					<div class="span7 ">
						<a class="custom content" href="/partymgr/control/viewprofile?partyId=${invoice.partyId}">${partyNameResultTo.fullName?if_exists} [${invoice.partyId?if_exists}]</a>
					</div>
				</div>
				<div  class="row-fluid margin-bottom10">	
					<div class="span5 align-right custom ">
						${uiLabelMap.FormFieldTitle_billingAccountId}
					</div>
					<div class="span7 ">
						${invoice.billingAccountId?if_exists}
					</div>
				</div>
				<div  class="row-fluid margin-bottom10">	
					<div class="span5 align-right custom ">
						${uiLabelMap.FormFieldTitle_dueDate}
					</div>
					<div class="span7 ">
						<#if invoice.dueDate?has_content>
							${invoice.dueDate?substring(0,10)?if_exists}
						<#else>
							
						</#if>
					</div>
				</div>
				<div  class="row-fluid margin-bottom10">	
					<div class="span5 align-right custom">
						${uiLabelMap.FormFieldTitle_paidDate}
					</div>
					<div class="span7 ">
						<#if invoice.paidDate?has_content>
							${invoice.paidDate?substring(0,10)?if_exists}
						<#else>
							
						</#if>
					</div>
				</div>	
			</div>
		</div>
	</div>
</div>