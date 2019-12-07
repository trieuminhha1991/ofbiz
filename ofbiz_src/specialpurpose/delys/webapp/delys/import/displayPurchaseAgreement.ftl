<input type="hidden" id="agrId" value="${agreementId}"/>
<div class="widget-box transparent">
<div class="row-fluid">
    <div class="span12 widget-container-span">
	    <div class="widget-box transparent">
	        <div class="widget-body">
	            <div class="widget-main span12 align-center no-left-margin">
	            	<h3><b>${agreementName.attrValue}</b></h3>
	            		<h5>${uiLabelMap.No}. ${agreementId}</h5>
	            		<h5>${uiLabelMap.ThisPurchaseOrderIsMadeOn} ${agreement.agreementDate}</h5>
				</div>
				<div class="span6">
					<h5><b>${uiLabelMap.Between}</b></h5>
				</div>
				
				<div class="span12" style="margin-left: 5%">
					<b>${purchaser.firstName!} ${purchaser.middleName!} ${purchaser.lastName!} ${purchaser.groupName!}</b></br>
					<b>${uiLabelMap.Address}: ${addressFrom.address1}</b></br>
				</div>
				<div style="margin-left: 5%">
					<div class="span3 begin-group-group">
						<b>${uiLabelMap.fromContactMechPhone}: ${phoneFrom.contactNumber}</b> </br>
						<b>${uiLabelMap.Bank}:</b> </br>
					</div> 
					<div class="span3 end-group-group">
						<b>${uiLabelMap.fromContactMechFax}: ${faxFrom.contactNumber}</b> </br>
						<b> ${uiLabelMap.Account}: ${listFinAccountFroms.get(0).finAccountCode}</b> </br>
					</div>
				</div></br>
				<div style="margin-left: 5%" class="span12">
					<i><b>${uiLabelMap.Represented}: ${representParty.firstName!} ${representParty.middleName!} ${representParty.lastName!} ${representParty.groupName!} - ${representParty.partyId}</b></i></br>
					<i>${uiLabelMap.HereInAfterCalled} <b>"${uiLabelMap.ThePurchaser}"</b></i>
				</div>
				<div class="span6">
					<h5><b>${uiLabelMap.And}</b></h5>
				</div>
				
				<div class="span12" style="margin-left: 5%">
					<b>${supplier.firstName!} ${supplier.middleName!} ${supplier.lastName!} ${supplier.groupName!}</b></br>
					<b>${uiLabelMap.Address}: ${addressTo.address1}</b></br>
					<i>${uiLabelMap.HereInAfterCalled} <b>"${uiLabelMap.TheSupplier}"</b></i>
				</div>
				
				<div class="span6">
					<h5><b>1. ${uiLabelMap.ContractValue}</b></h5>
				</div>
				<div class="span12">
					<#assign totalAmount = 0>
					<#list listProducts as item>
						<#assign itemValue = item.quantity * item.unitPrice>
						<#assign totalAmount = totalAmount + itemValue>
					</#list>
					1.1 ${uiLabelMap.TheTotalPurchaseOrderPriceIs} ${currencyUomId} ${Static["org.ofbiz.base.util.UtilFormatOut"].formatCurrency(totalAmount, currencyUomId, locale)}</br>
					1.2 ${uiLabelMap.TheBreakdownOfThisAmountIdAsFollows}:</br>
					<table id="sale-forecast-view" class="table table-striped table-bordered table-hover">
			            <thead>
			                <tr class="sf-product">
			                	<td>${uiLabelMap.ProductId}</td>
			                	<td>${uiLabelMap.description}</td>
			                	<td>${uiLabelMap.Unit}</td>
			                	<td>${uiLabelMap.quantity}</td>
			                	<td>${uiLabelMap.unitPrice} (${currencyUomId})</td>
			                	<td>${uiLabelMap.GoodValue} (${currencyUomId})</td>
			                	<td>${uiLabelMap.Remark}</td>
			                </tr>
			            </thead>
			            <tbody>
			            	<#assign totalValue = 0>
			            	<#assign totalRemark = 0>
			            	<#list listProducts as item>
			            		<#assign convertNumber = 1>
			            		<#assign productId = item.productId>
			            		<#assign product = delegator.findOne("Product", {"productId" : productId}, true)!>
			            		<#assign baseUom = delegator.findOne("Uom", {"uomId" : product.quantityUomId}, true)!>
			            		<#assign goodValue = item.quantity * item.unitPrice>
			            		<#assign totalValue = totalValue + goodValue>
			            		<#assign quantity = item.quantity>
			            		<#assign listUomToConvert = Static["com.olbius.services.DelysServices"].getListUomToConvert(delegator, productId, productPackingUomId, product.quantityUomId)!>
			            		<#assign convertNumber = Static["com.olbius.services.DelysServices"].getProductConvertNumber(delegator, convertNumber, productId, productPackingUomId, product.quantityUomId, listUomToConvert)!>
				            	<#assign remark = quantity.divide(convertNumber, 0, Static["java.math.RoundingMode"].HALF_UP)/>
				            	<#assign totalRemark = totalRemark + remark>
				            	<#assign uomProductPacking = delegator.findOne("Uom", {"uomId" : productPackingUomId}, true)!>
				            	<tr>
				            		<td>${productId?if_exists}</td>
				            		<td>${product.internalName?if_exists}</td>
				            		<td>${baseUom.description?if_exists}</td>
				            		<td>${quantity?if_exists}</td>
				            		<td>${Static["org.ofbiz.base.util.UtilFormatOut"].formatCurrency(item.unitPrice, currencyUomId, locale)}</td>
				            		<td>${Static["org.ofbiz.base.util.UtilFormatOut"].formatCurrency(goodValue, currencyUomId, locale)}</td>
				            		<td>${remark?if_exists} ${uomProductPacking.description?if_exists} </td>
				            	</tr>
			            	</#list>
			            	<tr>
			            		<td>${uiLabelMap.Total}</td>
			            		<td></td>
			            		<td></td>
			            		<td></td>
			            		<td></td>
			            		<td>${Static["org.ofbiz.base.util.UtilFormatOut"].formatCurrency(totalValue, currencyUomId, locale)}</td>
			            		<td>${totalRemark} ${uomProductPacking.description} </td>
			            	</tr>
			            </tbody>
					</table>
					<#assign port = delegator.findOne("Facility", {"facilityId" : currentPortTerm}, true)!>
					1.3 ${uiLabelMap.AllThePaymentCouldBeCombineRatioBetween} ${currencyUomId} 
			    		<#list listOtherCurrencyTerms as otherUom>
			    			${uiLabelMap.CommonAnd} ${otherUom.textValue}
			    		</#list>
			    		. ${uiLabelMap.CFR} ${port.facilityName}
				</div>
				<div class="span6">
					<h5><b>2. ${uiLabelMap.TermOfDelivery}</b></h5>
				</div>
				<div class="span12">
					<#assign etd = delegator.findOne("CustomTimePeriod", {"customTimePeriodId" : currentETDTerm}, true)!>
					<#assign eta = delegator.findOne("CustomTimePeriod", {"customTimePeriodId" : currentETATerm}, true)!>
					2.1 ${uiLabelMap.TheDateOfShipment}: </br>
						<div style="margin-left: 2%">
							• ${uiLabelMap.ETD}: ${etd.fromDate}</br>
							• ${uiLabelMap.ETA}: ${port.facilityName}: ${eta.fromDate}</br>
						</div>
					2.2 ${uiLabelMap.PortOfDischarge}: ${port.facilityName} </br>
					2.3 ${uiLabelMap.Transshipment} <#if transshipment == "Y"> ${uiLabelMap.IsAllowed}<#else> ${uiLabelMap.IsNotAllowed}</#if>. ${uiLabelMap.PartialShipment} <#if partialShipment == "Y"> ${uiLabelMap.IsAllowed}<#else> ${uiLabelMap.IsNotAllowed}</#if></br>
				</div>
				<div class="span6">
					<h5><b>3. ${uiLabelMap.TermOfPayment}</b></h5>
				</div>
				<div class="span12">
					${uiLabelMap.AgreementPaymentTermDetail}
					<div style="margin-left: 5%">
						<#list listFinAccountTos as finAcc>
							<#assign bic = delegator.findOne("FinAccountAttribute", {"finAccountId" : finAcc.finAccountId, "attrName" : "BIC"}, true)!>
							<#assign iban = delegator.findOne("FinAccountAttribute", {"finAccountId" : finAcc.finAccountId, "attrName" : "IBAN"}, true)!>
							<b>${uiLabelMap.SupplierBank}</b>: ${finAcc.finAccountName} </br>
							<div style="margin-left: 5%">
								${uiLabelMap.Account} (${finAcc.currencyUomId}): ${finAcc.finAccountCode} </br>
							 	<#if bic?has_content>
									${uiLabelMap.FinAccBIC}: ${bic.attrValue} </br>
								</#if>
								<#if iban?has_content>
									${uiLabelMap.FinAccIBAN}: ${iban.attrValue} </br>
								</#if>
							</div>
						</#list>
						<b>${uiLabelMap.Beneficiary}</b>: ${supplier.firstName!} ${supplier.middleName!} ${supplier.lastName!} ${supplier.groupName!}</br>
					</div>
				</div>
				
				<div class="span6">
					<h5><b>4. ${uiLabelMap.Packing}</b></h5>
				</div>
				
				<div class="span12">
					${uiLabelMap.AgreementPackingTermDetail}
				</div>
				
				<div class="span6">
					<h5><b>5. ${uiLabelMap.Documentation}</b></h5>
				</div>
				
				<div class="span12">
				${uiLabelMap.AgreementDocumentTermDetail}. </br>
				- ${uiLabelMap.Invoice} </br>
				- ${uiLabelMap.PackingList} (${uiLabelMap.DeliveryNote}) </br>
				- ${uiLabelMap.BillOfLading}</br>
				</div>
				
				<div class="span6">
					<h5><b>6. ${uiLabelMap.Transportation}</b></h5>
				</div>
				
				<div class="span12">
					${supplier.firstName!} ${supplier.middleName!} ${supplier.lastName!} ${supplier.groupName!} </br>
					${uiLabelMap.Receiver}: </br>
					<b>${purchaser.firstName!} ${purchaser.middleName!} ${purchaser.lastName!} ${purchaser.groupName!}</b></br>
					<b>${uiLabelMap.Address}: ${addressFrom.address1}</b></br>
					${uiLabelMap.AmendmentSupplementary}
				</div>
				<div style="text-align: center;">
					<div class="span6 begin-group-group">
						<h5><b>${uiLabelMap.ForTheSupplier}</b></h5>
					</div> 
					<div class="span6 end-group-group">
						<h5><b>${uiLabelMap.ForThePurchaser}</b></h5>
					</div>
				</div>
			</div>
		</div>        
	</div>
</div>
</div>