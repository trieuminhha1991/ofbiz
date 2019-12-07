<div id="general-tab" class="tab-pane<#if activeTab?exists && activeTab == "general-tab"> active</#if>">		
	<div class="title-status" id="statusTitle" style="margin-top: -10px;">
		<#assign statusAgr = delegator.findOne("StatusItem", {"statusId" : agreement.statusId?if_exists}, false)!>
		${statusAgr?if_exists.get("description", locale)}
		<#if agreement.hasOrdered?has_content && agreement.hasOrdered == 'Y'>
			(${uiLabelMap.BIEPOHasBeenCreated})
		</#if>
	</div>
	<input type="hidden" id="agrId" value="${agreementId}"/>
	<div class="widget-box transparent">
		<div class="row-fluid">
			 <div class="span12 widget-container-span">
			 	<div class="widget-box transparent">
			 		<div class="widget-body">
		 				<div class="widget-main span12 align-center no-left-margin">
			            	<h3><b>${(agreementName.attrValue)?if_exists}</b></h3>
		            		<h5>${uiLabelMap.No2}. ${agreement.agreementCode?if_exists}</h5>
		            		<h5>${uiLabelMap.ThisPurchaseOrderIsMadeOn2} ${(agreement.agreementDate)?if_exists?string('dd/MM/yyyy HH:mm')}</h5>
						</div>
						<div class="span6">
							<h5><b>${uiLabelMap.Between2}</b></h5>
						</div>
						
						<div class="span12" style="margin-left: 5%">
							<b>${purchaser.groupNameLocal?if_exists?upper_case}</b></br>
							${uiLabelMap.Address2}: ${(addressFrom.address2)?if_exists}</br>
						</div>
						
						<div style="margin-left: 5%">
							<div class="span3 begin-group-group">
								${uiLabelMap.fromContactMechPhone2}: ${(phoneFrom.contactNumber)?if_exists}</br>
								${uiLabelMap.Bank2}: <#if listFinAccountFroms?has_content>
									<#assign bank = delegator.findOne("PartyGroup", {"partyId" : listFinAccountFroms.get(0).get("bankId")?if_exists}, false)!>
								  	${bank.get("groupName")?if_exists}</#if></br>
							</div> 
							<div class="span3 end-group-group">
								${uiLabelMap.fromContactMechFax2}: ${(faxFrom.contactNumber)?if_exists}</br>
								${uiLabelMap.Account2}:<#if listFinAccountFroms?has_content> ${(listFinAccountFroms.get(0).get("finAccountCode"))?if_exists}</#if></br>
							</div>
						</div></br>
						
			<#--		<div style="margin-left: 5%" class="span12">
							${uiLabelMap.Represented2}: <#if representParty?has_content>${(representParty.firstName)?if_exists} ${(representParty.middleName)?if_exists} ${(representParty.lastName)?if_exists}</#if></br>
							<i>${uiLabelMap.HereInAfterCalled2} <b>"${uiLabelMap.ThePurchaser2}"</b></i>
						</div>
			 -->
						<div class="span6">
							<h5><b>${uiLabelMap.And2}</b></h5>
						</div>
						
						<div class="span12" style="margin-left: 5%">
							<b>${supplier.groupNameLocal?if_exists?upper_case}</b></br>
							${uiLabelMap.Address2}: ${(addressTo.address2)?if_exists}</br>
							<i>${uiLabelMap.HereInAfterCalled2} <b>"${uiLabelMap.TheSupplier2}"</b></i>
						</div>
						
						<div class="span6">
							<h5><b>1. ${uiLabelMap.ContractValue2?upper_case}</b></h5>
						</div>
						<div class="span12">
							<#assign totalAmount = 0>
							<#list listProducts as item>
								<#assign itemValue = item.quantity * item.unitPrice>
								<#assign totalAmount = totalAmount + itemValue>
							</#list>
							1.1 ${uiLabelMap.TheTotalPurchaseOrderPriceIs2} ${currencyUomId?if_exists} ${Static["org.ofbiz.base.util.UtilFormatOut"].formatCurrency(totalAmount, currencyUomId, locale)}</br>
							1.2 ${uiLabelMap.TheBreakdownOfThisAmountIdAsFollows2}:</br>
							<table id="sale-forecast-view" class="table table-striped table-bordered table-hover">
					            <thead>
					                <tr class="sf-product">
					                	<th width="1%">${StringUtil.wrapString(uiLabelMap.SequenceId)}</th>
					                	<th width="8%">${StringUtil.wrapString(uiLabelMap.ProductId2)}</th>
					                	<th width="12%">${StringUtil.wrapString(uiLabelMap.description2)}</th>
					                	<th width="6%">${StringUtil.wrapString(uiLabelMap.Unit2)}</th>
					                	<th width="8%">${StringUtil.wrapString(uiLabelMap.quantity2)}</th>
					                	<th width="8%">${StringUtil.wrapString(uiLabelMap.unitPrice2)} (${currencyUomId?if_exists})</th>
					                	<th width="8%">${StringUtil.wrapString(uiLabelMap.GoodValue2)} (${currencyUomId?if_exists})</th>
					                	<th width="8%">${StringUtil.wrapString(uiLabelMap.Remark2)}</th>
					                </tr>
					            </thead>
					            <tbody>
					            	<#assign totalValue = 0>
					            	<#assign totalRemark = 0>
					            	<#assign i = 0>
					            	<#list listProducts as item>
					            		<#assign i = i + 1>
					            		<#assign convertNumber = 1>
					            		<#assign productId = item.productId>
					            		<#assign product = delegator.findOne("Product", {"productId" : productId}, true)!>
					            		<#assign productCode = product.productCode?if_exists>
					            		<#assign baseUom = delegator.findOne("Uom", {"uomId" : product.quantityUomId}, true)!>
					            		<#assign goodValue = item.quantity * item.unitPrice>
					            		<#assign totalValue = totalValue + goodValue>
					            		<#assign quantity = item.quantity>
					            		<#assign listUomToConvert = Static["com.olbius.importsrc.AgreementServices"].getListUomToConvert(delegator, productId, productPackingUomId, product.quantityUomId)!>
					            		
					            		<#assign listConfigPacking = delegator.findList("ConfigPacking", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition({"productId" : productId, "uomFromId" : productPackingUomId?if_exists, "uomToId" : product.quantityUomId}), null, null, null, false) />
					            		<#if listConfigPacking?has_content>
					            		<#assign cfPacking = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(listConfigPacking) />
					            		</#if>
					            		
					            		<#assign convertNumber = 1 />
					            		<#if cfPacking?exists && cfPacking != "">
					            			<#assign convertNumber = cfPacking.quantityConvert !>
					            		</#if>
					            		<#-- <#assign convertNumber = Static["com.olbius.importsrc.AgreementServices"].getProductConvertNumber(delegator, convertNumber, productId, productPackingUomId?if_exists, product.quantityUomId, listUomToConvert)!> -->
						            	<#assign remark = quantity.divide(convertNumber, 1, Static["java.math.RoundingMode"].HALF_UP)/>
						            	<#assign totalRemark = totalRemark + remark>
						            	<#assign uomProductPacking = delegator.findOne("Uom", {"uomId" : productPackingUomId?if_exists}, true)!>
						            	<tr>
						            		<td class="align-left">${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(i?if_exists, "#,##0", locale)}</td>
						            		<td>${productCode?if_exists}</td>
						            		<td>${product.productName?if_exists}</td>
						            		<td>${baseUom.get("description", locale)?if_exists}</td>
						            		<td>${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(quantity?if_exists, "#,##0.00", locale)}</td>
						            		<td>
						            			${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(item.unitPrice?if_exists, "#,##0.00", locale)}
						            		</td>
						            		
						            		<td>
						            			${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(goodValue?if_exists, "#,##0.00", locale)}
						            		</td>
						            		<td>${remark?if_exists} ${uomProductPacking.description?if_exists} </td>
						            	</tr>
					            	</#list>
					            	<tr>
					            		<td colspan="6">${uiLabelMap.Total2}</td>
					            		<td>${Static["org.ofbiz.base.util.UtilFormatOut"].formatCurrency(totalValue, currencyUomId, locale)}</td>
					            		<td>${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(totalRemark?if_exists, "#,##0", locale)} ${(uomProductPacking.description)?if_exists} </td>
					            	</tr>
					            </tbody>
							</table>
							1.3 ${uiLabelMap.AllThePaymentCouldBeCombineRatioBetween2} ${currencyUomId?if_exists}
					    		<#list listOtherCurrencyTerms as otherUom>
					    			${uiLabelMap.CommonAnd2} ${(otherUom.textValue)?if_exists}
					    		</#list>
					    		. ${uiLabelMap.CFR} ${(currentPortTerm)?if_exists}
						</div>
						<div class="span6">
							<h5><b>2. ${uiLabelMap.TermOfDelivery2?upper_case}</b></h5>
						</div>
						<div class="span12">
							<#assign etd = delegator.findOne("CustomTimePeriod", {"customTimePeriodId" : currentETDTerm?if_exists}, true)!>
							<#assign eta = delegator.findOne("CustomTimePeriod", {"customTimePeriodId" : currentETATerm?if_exists}, true)!>
							2.1 ${uiLabelMap.TheDateOfShipment2}: </br>
								<div style="margin-left: 2%">
									${uiLabelMap.ETD2}: ${(etd.fromDate)?if_exists}</br>
									${uiLabelMap.ETA2}: ${(eta.fromDate)?if_exists}</br>
								</div>
							2.2 ${uiLabelMap.PortOfDischarge2}: ${(currentPortTerm)?if_exists} </br>
							2.3 ${uiLabelMap.Transshipment2} <#if transshipment?if_exists == "Y"> ${uiLabelMap.IsAllowed2}<#else> ${uiLabelMap.IsNotAllowed2}</#if>. ${uiLabelMap.PartialShipment2} <#if partialShipment?if_exists == "Y"> ${uiLabelMap.IsAllowed2}<#else> ${uiLabelMap.IsNotAllowed2}</#if></br>
						</div>
						<div class="span6">
							<h5><b>3. ${uiLabelMap.TermOfPayment2?upper_case}</b></h5>
						</div>
						<div class="span12">
							${uiLabelMap.AgreementPaymentTermDetail2}
							<div style="margin-left: 2%">
								<#list listFinAccountTos as finAcc>
									<#assign bic = delegator.findOne("FinAccountAttribute", {"finAccountId" : finAcc.finAccountId, "attrName" : "BIC"}, true)!>
									<#assign iban = delegator.findOne("FinAccountAttribute", {"finAccountId" : finAcc.finAccountId, "attrName" : "IBAN"}, true)!>
									<b>${uiLabelMap.SupplierBank2}</b>: ${finAcc.finAccountName} </br>
									<div style="margin-left: 2%">
										${uiLabelMap.Account2} (${(finAcc.currencyUomId)?if_exists}): ${(finAcc.finAccountCode)?if_exists} </br>
									 	<#if bic?has_content>
											${uiLabelMap.FinAccBIC}: ${(bic.attrValue)?if_exists} </br>
										</#if>
										<#if iban?has_content>
											${uiLabelMap.FinAccIBAN}: ${(iban.attrValue)?if_exists} </br>
										</#if>
									</div>
								</#list>
								${uiLabelMap.Beneficiary2}: ${supplier.groupNameLocal?if_exists?upper_case}</br>
							</div>
						</div>
						
						<div class="span6">
							<h5><b>4. ${uiLabelMap.Packing2?upper_case}</b></h5>
						</div>
						
						<div class="span12">
							${uiLabelMap.AgreementPackingTermDetail2}
						</div>
						
						<div class="span6">
							<h5><b>5. ${uiLabelMap.Documentation2?upper_case}</b></h5>
						</div>
						
						<div class="span12">
						According the requirement of product specification.</br>
						- ${uiLabelMap.Invoice2} </br>
						- ${uiLabelMap.PackingList2} (${uiLabelMap.DeliveryNote2}) </br>
						- ${uiLabelMap.BillOfLading2}</br>
						</div>
						
						<div class="span6">
							<h5><b>6. ${uiLabelMap.Transportation2?upper_case}</b></h5>
						</div>
						
						<div class="span12">
							<b>${supplier.groupNameLocal?if_exists?upper_case}</b> </br>
							${uiLabelMap.Receiver2}: </br>
							<b>${purchaser.groupNameLocal?if_exists?upper_case}</b></br>
							${uiLabelMap.Address2}: ${(addressFrom.address2)?if_exists}</br>
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
</div>
