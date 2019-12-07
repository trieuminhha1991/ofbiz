<input type="hidden" id="agrId" value="${agreementId}"/>
<div class="widget-box transparent">
<div class="row-fluid">
    <div class="span12 widget-container-span">
	    <div class="widget-box transparent">
	        <div class="widget-body">
	            <div class="widget-main span12 align-center no-left-margin">
	            	<h3><b>${agreementName.attrValue}</b></h3>
	            		<h5>${uiLabelMap.No2}. ${agreementId}</h5>
	            		<h5>${uiLabelMap.ThisPurchaseOrderIsMadeOn2} ${agreement.agreementDate}</h5>
				</div>
				<div class="span6">
					<h5><b>${uiLabelMap.Between2}</b></h5>
				</div>
				
				<div class="span12" style="margin-left: 5%">
					<b>${purchaser.firstName!} ${purchaser.middleName!} ${purchaser.lastName!} ${purchaser.groupName!}</b></br>
					<b>${uiLabelMap.Address2}: ${addressFrom.address1}</b></br>
				</div>
				<div style="margin-left: 5%">
					<div class="span3 begin-group-group">
						<b>${uiLabelMap.fromContactMechPhone2}: ${phoneFrom.contactNumber}</b> </br>
						<b>${uiLabelMap.Bank2}:</b> </br>
					</div> 
					<div class="span3 end-group-group">
						<b>${uiLabelMap.fromContactMechFax2}: ${faxFrom.contactNumber}</b> </br>
						<b> ${uiLabelMap.Account2}: ${listFinAccountFroms.get(0).get("finAccountCode")}</b> </br>
					</div>
				</div></br>
				<div style="margin-left: 5%" class="span12">
					<i><b>${uiLabelMap.Represented2}: ${representParty.firstName!} ${representParty.middleName!} ${representParty.lastName!} ${representParty.groupName!} - ${representParty.partyId}</b></i></br>
					<i>${uiLabelMap.HereInAfterCalled2} <b>"${uiLabelMap.ThePurchaser2}"</b></i>
				</div>
				<div class="span6">
					<h5><b>${uiLabelMap.And2}</b></h5>
				</div>
				
				<div class="span12" style="margin-left: 5%">
					<b>${supplier.get("groupName", locale)}</b></br>
					<b>${uiLabelMap.Address2}: ${addressTo.address1}</b></br>
					<i>${uiLabelMap.HereInAfterCalled2} <b>"${uiLabelMap.TheSupplier2}"</b></i>
				</div>
				
				<div class="span6">
					<h5><b>1. ${uiLabelMap.ContractValue2}</b></h5>
				</div>
				<div class="span12">
					<#assign totalAmount = 0>
					<#list listProducts as item>
						<#assign itemValue = item.quantity * item.unitPrice>
						<#assign totalAmount = totalAmount + itemValue>
					</#list>
					1.1 ${uiLabelMap.TheTotalPurchaseOrderPriceIs2} ${currencyUomId} ${Static["org.ofbiz.base.util.UtilFormatOut"].formatCurrency(totalAmount, currencyUomId, locale)}</br>
					1.2 ${uiLabelMap.TheBreakdownOfThisAmountIdAsFollows2}:</br>
					<table id="sale-forecast-view" class="table table-striped table-bordered table-hover">
			            <thead>
			                <tr class="sf-product">
			                	<th>${uiLabelMap.ProductId2}</th>
			                	<th>${uiLabelMap.description2}</th>
			                	<th>${uiLabelMap.Unit2}</th>
			                	<th>${uiLabelMap.quantity2}</th>
			                	<th>${uiLabelMap.unitPrice2} (${currencyUomId})</th>
			                	<th>${uiLabelMap.GoodValue2} (${currencyUomId})</th>
			                	<th>${uiLabelMap.Remark2}</th>
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
			            		<#assign cfPacking = delegator.findOne("ConfigPacking", {"productId" : productId, "uomFromId" : productPackingUomId, "uomToId" : product.quantityUomId}, false) !>
			            		<#assign convertNumber = 1 />
			            		<#if cfPacking != "">
			            			<#assign convertNumber = cfPacking.quantityConvert !>
			            		</#if>
			            		<#-- <#assign convertNumber = Static["com.olbius.services.DelysServices"].getProductConvertNumber(delegator, convertNumber, productId, productPackingUomId, product.quantityUomId, listUomToConvert)!> -->
				            	<#assign remark = quantity.divide(convertNumber, 1, Static["java.math.RoundingMode"].HALF_UP)/>
				            	<#assign totalRemark = totalRemark + remark>
				            	<#assign uomProductPacking = delegator.findOne("Uom", {"uomId" : productPackingUomId}, true)!>
				            	<tr>
				            		<td>${productId?if_exists}</td>
				            		<td>${product.internalName?if_exists}</td>
				            		<td>${baseUom.get("description", locale)?if_exists}</td>
				            		<td>${quantity?if_exists}</td>
				            		<td>${Static["org.ofbiz.base.util.UtilFormatOut"].formatCurrency(item.unitPrice, currencyUomId, locale)}</td>
				            		<td>${Static["org.ofbiz.base.util.UtilFormatOut"].formatCurrency(goodValue, currencyUomId, locale)}</td>
				            		<td>${remark?if_exists} ${uomProductPacking.description?if_exists} </td>
				            	</tr>
			            	</#list>
			            	<tr>
			            		<td>${uiLabelMap.Total2}</td>
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
					1.3 ${uiLabelMap.AllThePaymentCouldBeCombineRatioBetween2} ${currencyUomId}
			    		<#list listOtherCurrencyTerms as otherUom>
			    			${uiLabelMap.CommonAnd2} ${otherUom.textValue}
			    		</#list>
			    		. ${uiLabelMap.CFR} ${port.facilityName}
				</div>
				<div class="span6">
					<h5><b>2. ${uiLabelMap.TermOfDelivery2}</b></h5>
				</div>
				<div class="span12">
					<#assign etd = delegator.findOne("CustomTimePeriod", {"customTimePeriodId" : currentETDTerm}, true)!>
					<#assign eta = delegator.findOne("CustomTimePeriod", {"customTimePeriodId" : currentETATerm}, true)!>
					2.1 ${uiLabelMap.TheDateOfShipment2}: </br>
						<div style="margin-left: 2%">
							${uiLabelMap.ETD2}: ${etd.fromDate}</br>
							${uiLabelMap.ETA2}: ${port.facilityName}: ${eta.fromDate}</br>
						</div>
					2.2 ${uiLabelMap.PortOfDischarge2}: ${port.facilityName} </br>
					2.3 ${uiLabelMap.Transshipment2} <#if transshipment == "Y"> ${uiLabelMap.IsAllowed2}<#else> ${uiLabelMap.IsNotAllowed2}</#if>. ${uiLabelMap.PartialShipment2} <#if partialShipment == "Y"> ${uiLabelMap.IsAllowed2}<#else> ${uiLabelMap.IsNotAllowed2}</#if></br>
				</div>
				<div class="span6">
					<h5><b>3. ${uiLabelMap.TermOfPayment2}</b></h5>
				</div>
				<div class="span12">
					${uiLabelMap.AgreementPaymentTermDetail2}
					<div style="margin-left: 5%">
						<#list listFinAccountTos as finAcc>
							<#assign bic = delegator.findOne("FinAccountAttribute", {"finAccountId" : finAcc.finAccountId, "attrName" : "BIC"}, true)!>
							<#assign iban = delegator.findOne("FinAccountAttribute", {"finAccountId" : finAcc.finAccountId, "attrName" : "IBAN"}, true)!>
							<b>${uiLabelMap.SupplierBank2}</b>: ${finAcc.finAccountName} </br>
							<div style="margin-left: 5%">
								${uiLabelMap.Account2} (${finAcc.currencyUomId}): ${finAcc.finAccountCode} </br>
							 	<#if bic?has_content>
									${uiLabelMap.FinAccBIC}: ${bic.attrValue} </br>
								</#if>
								<#if iban?has_content>
									${uiLabelMap.FinAccIBAN}: ${iban.attrValue} </br>
								</#if>
							</div>
						</#list>
						<b>${uiLabelMap.Beneficiary2}</b>: ${supplier.firstName!} ${supplier.middleName!} ${supplier.lastName!} ${supplier.groupName!}</br>
					</div>
				</div>
				
				<div class="span6">
					<h5><b>4. ${uiLabelMap.Packing2}</b></h5>
				</div>
				
				<div class="span12">
					${uiLabelMap.AgreementPackingTermDetail2}
				</div>
				
				<div class="span6">
					<h5><b>5. ${uiLabelMap.Documentation2}</b></h5>
				</div>
				
				<div class="span12">
				According the requirement of product‘s specification.</br>
				- ${uiLabelMap.Invoice2} </br>
				- ${uiLabelMap.PackingList2} (${uiLabelMap.DeliveryNote2}) </br>
				- ${uiLabelMap.BillOfLading2}</br>
				</div>
				
				<div class="span6">
					<h5><b>6. ${uiLabelMap.Transportation2}</b></h5>
				</div>
				
				<div class="span12">
					${supplier.firstName!} ${supplier.middleName!} ${supplier.lastName!} ${supplier.groupName!} </br>
					${uiLabelMap.Receiver2}: </br>
					<b>${purchaser.firstName!} ${purchaser.middleName!} ${purchaser.lastName!} ${purchaser.groupName!}</b></br>
					<b>${uiLabelMap.Address2}: ${addressFrom.address1}</b></br>
					${uiLabelMap.AmendmentSupplementary2}
				</div>
				<div style="text-align: center;">
					<div class="span6 begin-group-group">
						<h5><b>${uiLabelMap.ForTheSupplier2}</b></h5>
					</div> 
					<div class="span6 end-group-group">
						<h5><b>${uiLabelMap.ForThePurchaser2}</b></h5>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
</div>