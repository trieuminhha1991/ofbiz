<div id="notifyId" style="display: none;">
	<div>
		${uiLabelMap.UpdateOrderStatusSuccessfully}
	</div>
</div>
<div id="containerNotify" style="width: 100%; overflow: auto;">
</div>
<script>
$(document).ready(function(){
	$("#notifyId").jqxNotification({ width: "100%", opacity: 0.9, appendContainer: "#containerNotify",
		autoOpen: false, animationOpenDelay: 800, autoClose: true, template: "success"
	});
});
</script>
<div id="orderoverview-tab" class="tab-pane<#if !activeTab?exists || activeTab == "" || activeTab == "orderoverview-tab"> active</#if>">
	<div style="position:relative"><!-- class="widget-body"-->
		<div class="title-status" id="statusTitle" style="margin-top: -10px;">
			<#assign orderStatus = orderHeader.getRelatedOne("StatusItem")!>
			${orderStatus?if_exists.get("description", locale)}
		</div>
		<div><!--class="widget-main"-->
			<h3 style="text-align:center;font-weight:bold; padding:0;line-height:20px">
				${uiLabelMap.DmsOrderPO}
			</h3>
			<div class="row-fluid margin-top20">
				<div class="form-horizontal label-text-left font-arial content-description">
					<div class="row-fluid">
						<div class="span6">
							<div class="row-fluid">
								<div class="span3">
									<div class="row-fluid">
										<div class="span3">
										</div>
										<div class="span9 bold-label">
											<span>${uiLabelMap.DAOrderId}<span>
										</div>
									</div>
								</div>
								<div class="span9 green-label bold-label">
									<span>${orderHeader.orderId?if_exists}</span>
								</div>
							</div>
							<div class="row-fluid">
								<div class="span3">
									<div class="row-fluid">
										<div class="span3">
										</div>
										<div class="span9 bold-label">
											<span>${uiLabelMap.CreatedDate}<span>
										</div>
									</div>
								</div>
								<div class="span9 green-label bold-label">
									<span>
										<#if orderHeader.orderDate?exists>
											${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(orderHeader.orderDate, "dd/MM/yyyy HH:mm:ss", locale, timeZone)!}
										</#if>
									</span>
								</div>
							</div>
							
							<div class="row-fluid">
								<div class="span3">
									<div class="row-fluid">
										<div class="span3">
										</div>
										<div class="span9 bold-label">
											<span>${uiLabelMap.POSupplier}<span>
										</div>
									</div>
								</div>
								<div class="span9 green-label bold-label">
									<span>
										<#if partyName?exists>
											${partyName}
										</#if>
									</span>
								</div>
							</div>
							<div class="row-fluid">
								<div class="span3">
									<div class="row-fluid">
										<div class="span3">
										</div>
										<div class="span9 bold-label">
											<span>${uiLabelMap.BPOCurrencyUomId}<span>
										</div>
									</div>
								</div>
								<div class="span9 green-label bold-label">
									<span>
										<#if orderHeader.currencyUom?exists>
											${orderHeader.currencyUom?if_exists}
										</#if>
									</span>
								</div>
							</div>
						</div><!--.span6-->
						<div class="span6">
							<div class="row-fluid">
								<div class="span4">
									<div class="row-fluid">
										<div class="bold-label">
											<span>${uiLabelMap.CreateBy}<span>
										</div>
									</div>
								</div>
								<div class="span8 green-label bold-label" >
									<span>
										<#if createdByPartyName?exists>
											${createdByPartyName!}
										</#if>
									</span>
								</div>
							</div>
							<div class="row-fluid">
								<div class="span4">
									<div class="row-fluid">
										<div class="bold-label">
											<span>${uiLabelMap.BLShippingDate}<span>
										</div>
									</div>
								</div>
								<div class="span8 green-label bold-label" >
									<span><#if shipAfterDate?exists>
										${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(shipAfterDate, "dd/MM/yyyy HH:mm", locale, timeZone)!}
									</#if> - 
									<#if shipBeforeDate?exists>
										${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(shipBeforeDate, "dd/MM/yyyy HH:mm", locale, timeZone)!}
									</#if></span>
								</div>
							</div>
							<div class="row-fluid">
								<div class="span4">
									<div class="row-fluid">
										<div class="bold-label">
											<span>${uiLabelMap.ReceiveToFacility}<span>
										</div>
									</div>
								</div>
								<div class="span8 green-label bold-label" >
									<span>
										<#assign facility = delegator.findOne("Facility", {"facilityId" : orderHeader.originFacilityId?if_exists}, true)/>
										<#if facility?exists>
											<#if facility.facilityCode?exists>
												[${facility.facilityCode?if_exists}] ${facility.facilityName?if_exists}								
											<#else>
												[${facility.facilityId?if_exists}] ${facility.facilityName?if_exists}
											</#if>
										</#if>
									</span>
								</div>
							</div>
							<div class="row-fluid">
								<div class="span4">
									<div class="row-fluid">
										<div class="bold-label">
											<span>${uiLabelMap.ShippingAddress}</span>
										</div>
									</div>
								</div>
								<div class="span8 bold-label green-label">
									<span>
										<#assign orderContactMechValueMaps = Static["org.ofbiz.party.contact.ContactMechWorker"].getOrderContactMechValueMaps(delegator, orderId) />
										<#list orderContactMechValueMaps as orderContactMechValueMap>
											<#assign contactMech = orderContactMechValueMap.contactMech>
											<#assign contactMechPurpose = orderContactMechValueMap.contactMechPurposeType>
											<#if contactMech.contactMechTypeId == "POSTAL_ADDRESS">
												<#assign postalAddress = orderContactMechValueMap.postalAddress>
												<#if postalAddress?has_content>
													<#if postalAddress.address1?has_content> ${postalAddress.address1}, </#if>
													<!-- <#if postalAddress.address2?has_content> ${postalAddress.address2}, </#if> -->
													<#if postalAddress.wardGeoId?has_content>
														<#if "_NA_" == postalAddress.wardGeoId>
															___, 
														<#else>
															<#assign wardGeo = delegator.findOne("Geo", {"geoId" : postalAddress.wardGeoId}, true)/>
															${wardGeo?default(postalAddress.wardGeoId).geoName?default(postalAddress.wardGeoId)}, 
														</#if>
													</#if>
													<#if postalAddress.districtGeoId?has_content>
														<#if "_NA_" == postalAddress.districtGeoId>
															___, 
														<#else>
															<#assign districtGeo = delegator.findOne("Geo", {"geoId" : postalAddress.districtGeoId}, true)/>
															${districtGeo?default(postalAddress.districtGeoId).geoName?default(postalAddress.districtGeoId)}, 
														</#if>
													</#if>
													<#if postalAddress.city?has_content> ${postalAddress.city}, </#if>
													<#if postalAddress.countryGeoId?has_content>
														<#assign country = postalAddress.getRelatedOne("CountryGeo", true)>
														${country.get("geoName", locale)?default(country.geoId)}
													</#if>
													<#-- 
														<#if postalAddress.toName?has_content> [${uiLabelMap.FormFieldTitle_toName}: ${postalAddress.toName}<#if postalAddress.attnName?has_content> (${postalAddress.attnName})</#if>].</#if>
												 	-->
												</#if>
											</#if>
										</#list>
									</span>
								</div>
							</div>
						</div><!--.span6-->
					</div>
				</div><!-- .form-horizontal -->
				<div class="form-horizontal basic-custom-form">
					<table cellspacing="0" cellpadding="1" border="0" class="table table-bordered">
						<thead>
							<tr style="font-weight: bold;">
								<td width="1%" class="align-left">${uiLabelMap.BPOSequenceId}</td>
								<td width="25%" class="align-left">${uiLabelMap.BPOProductId} - ${uiLabelMap.DAProductName}</td>
								<td width="15%" class="align-left">${uiLabelMap.BSNote}</td>
								<th width="8%" align="right" class="align-center"><span><b>${uiLabelMap.BLPackingForm}</b></span></th>
								<th width="10%" align="right" class="align-center"><span><b>${uiLabelMap.BSPurchaseUomId}</b></span></th>
								<th width="8%" align="right" class="align-center"><span><b>${uiLabelMap.BLPurchaseQtySum}</b></span></th>
								<th width="10%" align="right" class="align-center"><span><b>${uiLabelMap.BLQuantityByEAUom}</b></span></th>
								<#if hasOlbPermission("MODULE", "PO_PRICE", "VIEW")>
									<td width="10%" class="align-center">${uiLabelMap.UnitPrice} </br> ${uiLabelMap.DAParenthesisBeforeVAT}</td>
									<td width="8%" class="align-center">${uiLabelMap.DAAdjustment}</td>
									<td width="12%" class="align-center">${uiLabelMap.DAItemTotal} </br> ${uiLabelMap.DAParenthesisBeforeVAT}</td>
								</#if>
							</tr>
						</thead>
						<tbody>
						<#list listItemLine as itemLine>
							<#assign returnedQuantity = returnQuantityMap.get(itemLine.orderItem.orderItemSeqId)?default(0)/>
							<#assign itemType = orderItem.getRelatedOne("OrderItemType", false)?if_exists>
							<tr <#if itemLine.isPromo == "Y"> class="background-promo" </#if>>
								<#assign productId = itemLine.productId?if_exists/>
								<#assign productCode = itemLine.productCode?if_exists/>
								<#assign product = itemLine.product?if_exists/>
								<td>${itemLine_index + 1}</td>
								<#if productId?exists && productId == "shoppingcart.CommentLine">
									<td colspan="6" valign="top">
										<div><b> &gt;&gt; ${itemLine.itemDescription?if_exists}</b></div>
									</td>
								<#else>
									<td valign="top" <#if returnedQuantity &gt; 0>class="background-important-nd"</#if>>
										<div>
										<#if productId?exists>
											${itemLine.productCode?default(productId)}  - ${itemLine.itemDescription?if_exists}
											<#if (product.purchaseDiscontinuationDate)?exists && Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp().after(product.purchaseDiscontinuationDate)>
												<br /><span style="color: red;">${uiLabelMap.BSDiscountinuePurchase}: ${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(product.purchaseDiscontinuationDate, "dd/MM/yyyy hh:MM:ss", locale, timeZone)!}</span>
											</#if>
										<#elseif itemLine.orderItemType?exists>
											${itemLine.orderItemType.description} - ${itemLine.itemDescription?if_exists}
										<#else>
											${itemLine.itemDescription?if_exists}
										</#if>
										<#if returnedQuantity &gt; 0> - <span class="text-error">(${uiLabelMap.OrderReturned} <b>${returnedQuantity}</b>)</span></#if>
										<#if itemLine.isPromo == "Y"> <i class="fa-gift"></i> </#if>
										</div>
									</td>
									<td align="right" class="align-left" valign="top">
									 	<#if itemLine.comments?exists>
											 <span class="text-error">${itemLine.comments}</span>
										</#if>
									</td>
									<td align="right" class="align-right" valign="top">
									 	${itemLine.convertNumber?if_exists}
									</td>
									<td align="right" class="align-right" valign="top">
										<#if itemLine.requireAmount?exists && itemLine.requireAmount == 'Y'>
											${itemLine.weightUomDescription?if_exists}
										<#else>
											<#if itemLine.quantityUomId?exists>
												<#assign quantityUom = delegator.findOne("Uom", {"uomId" : itemLine.quantityUomId}, false) !/>
												${StringUtil.wrapString(quantityUom.getString("description")?if_exists)}
											<#else>
												<#assign productData = delegator.findOne("Product", {"productId" : itemLine.productId} , false)! />
												<#assign quantityUom = delegator.findOne("Uom", {"uomId" : productData.get("quantityUomId")}, false) !/>
												${StringUtil.wrapString(quantityUom.getString("description")?if_exists)}
											</#if>
										</#if>
									</td>
									<td align="right" class="align-right" valign="top">
										<#if itemLine.requireAmount?exists && itemLine.requireAmount == 'Y'>
											<#if itemLine.selectedAmount?exists>${itemLine.selectedAmount?string.number}</#if>
										<#else>
											<#if itemLine.cancelQuantity?exists && itemLine.convertNumber?exists && itemLine.quantity?exists>
												<#assign tmpQty = itemLine.quantity - itemLine.cancelQuantity/itemLine.convertNumber>
												${tmpQty?string.number}
											<#else>
												<#if itemLine.quantity?exists>
													${itemLine.quantity?string.number}
												</#if>
											</#if>
										</#if>
									</td>
									<td align="right" class="align-right" valign="top">
										<#if itemLine.requireAmount?exists && itemLine.requireAmount == 'Y'>
											<#if itemLine.convertQuantityByAmount?exists>${itemLine.convertQuantityByAmount?string.number}</#if>
										<#else>
											<#if itemLine.cancelQuantity?exists && itemLine.baseQuantity?exists>
												<#assign tmpQty = itemLine.baseQuantity - itemLine.cancelQuantity>
												${tmpQty?string.number}
											<#else>
												<#if itemLine.baseQuantity?exists>
													${itemLine.baseQuantity?string.number}
												</#if>
											</#if>
										</#if>
									</td>
									<#if hasOlbPermission("MODULE", "PO_PRICE", "VIEW")>
										<td align="right" class="align-right" valign="top"><#-- Unit price -->
											<#if itemLine.requireAmount?exists && itemLine.requireAmount == 'Y'>
												<#if itemLine.unitPriceBeVAT?exists>
													<#if parameters.orderId?exists>
														<@ofbizCurrency amount=itemLine.unitPriceBeVAT/itemLine.selectedAmount isoCode=currencyUomId rounding=2/>
													<#else>
														<@ofbizCurrency amount=itemLine.unitPriceBeVAT isoCode=currencyUomId rounding=2/>
													</#if>										
												</#if>
											<#else>
												<@ofbizCurrency amount=itemLine.unitPriceBeVAT isoCode=currencyUomId/>
											</#if>
										</td>
										<td align="right" class="align-right" valign="top"><#-- Adjustment -->
											<@ofbizCurrency amount=itemLine.adjustment isoCode=currencyUomId/>
										</td>
										<td align="right" class="align-right" valign="top" nowrap="nowrap"><#-- Sub total before VAT -->
											<@ofbizCurrency amount=itemLine.subTotalBeVAT isoCode=currencyUomId rounding=2/>
										</td>
									</#if>
									<#-- Unit price after VAT - new column-->
								</#if>
							</tr>
							<#-- show info from workeffort -->
							<#-- show linked order lines -->
							<#-- show linked requirements -->
							<#-- show linked quote -->
							<#-- now show adjustment details per line item -->
							<#-- now show price info per line item -->
							<#-- now show survey information per line item -->
							<#-- display the ship estimated/before/after dates -->
							<#-- now show ship group info per line item -->
							<#-- now show inventory reservation info per line item -->
							<#-- now show planned shipment info per line item -->
							<#-- now show item issuances (shipment) per line item -->
							<#-- now show item issuances (inventory item) per line item -->
							<#-- now show shipment receipts per line item -->
						</#list>
						<#-- display tax prices sum -->
						<#if hasOlbPermission("MODULE", "PO_PRICE", "VIEW")>
							<#list listTaxTotal as taxTotalItem>
								<tr>
									<td align="right" class="align-right" colspan="9">
										<#if taxTotalItem.description?exists>${StringUtil.wrapString(taxTotalItem.description)}</#if>
									</td>
									<td class="align-right">
										<#if taxTotalItem.amount?exists && taxTotalItem.amount &lt; 0>
											(<@ofbizCurrency amount=-taxTotalItem.amount isoCode=currencyUomId/>)
										<#elseif taxTotalItem.amount?exists>
											<@ofbizCurrency amount=taxTotalItem.amount isoCode=currencyUomId/>
										</#if>
									</td>
								</tr>
							</#list>
							<#if taxDiscountTotal?has_content>
								<tr>
									<td align="right" class="align-right" colspan="9">
										${StringUtil.wrapString(uiLabelMap.BPTaxDiscountTotal)}
									</td>
									<td class="align-right">
										<@ofbizCurrency amount=taxDiscountTotal isoCode=currencyUomId/>
									</td>
								</tr>
							</#if>
							
							<#list orderHeaderAdjustments as orderHeaderAdjustment>
								<#assign adjustmentType = orderHeaderAdjustment.getRelatedOne("OrderAdjustmentType", false)>
								<#assign productPromo = orderHeaderAdjustment.getRelatedOne("ProductPromo", false)>
								<#assign adjustmentAmount = Static["org.ofbiz.order.order.OrderReadHelper"].calcOrderAdjustment(orderHeaderAdjustment, orderSubTotal)>
								<#if adjustmentAmount != 0>
									<tr>
										<td align="right" class="align-right" colspan="9">
											<#if productPromo?has_content>${productPromo.promoName?if_exists}
											<#else>
												<#if orderHeaderAdjustment.comments?has_content><i>${orderHeaderAdjustment.comments}</i></#if>
												<#if orderHeaderAdjustment.description?has_content><i>${orderHeaderAdjustment.description}</i></#if>
											</#if>
											<#--<span >${adjustmentType.get("description", locale)}</span>-->
										</td>
										<td align="right" class="align-right" nowrap="nowrap">
											<@ofbizCurrency amount=adjustmentAmount isoCode=currencyUomId rounding=2/>
										</td>
									</tr>
								</#if>
							</#list>
							<#-- subtotal -->
							<tr>
								<td align="right" class="align-right" colspan="9"><div><b>${uiLabelMap.DAOrderItemsSubTotal}</b></div></td>
								<td align="right" class="align-right" nowrap="nowrap">
									<@ofbizCurrency amount=orderSubTotal isoCode=currencyUomId rounding=2/>
								</td>
							</tr>
							<#-- other adjustments -->
							<tr>
								<td align="right" class="align-right" colspan="9"><div><b>${uiLabelMap.DATotalOrderAdjustments}</b></div></td>
								<td align="right" class="align-right" nowrap="nowrap">
									<@ofbizCurrency amount=otherAdjAmount isoCode=currencyUomId rounding=2/>
								</td>
							</tr>
							<#-- tax adjustments -->
							<tr>
								<td align="right" class="align-right" colspan="9"><div><b>${uiLabelMap.BPValueAddedTaxTotal}</b></div></td>
								<td align="right" class="align-right" nowrap="nowrap">
									<@ofbizCurrency amount=taxAmount isoCode=currencyUomId rounding=2/>
								</td>
							</tr>
							<#-- shipping adjustments -->
							<#if shippingAmount?exists && shippingAmount?has_content && (shippingAmount != 0)>
								<tr>
									<td align="right" class="align-right" colspan="9"><div><b>${uiLabelMap.OrderTotalShippingAndHandling}</b></div></td>
									<td align="right" class="align-right" nowrap="nowrap"><div><@ofbizCurrency amount=shippingAmount isoCode=currencyUomId rounding=2/></div></td>
								</tr>
							</#if>
							<#-- grand total -->
							<tr>
								<#assign accountOneValue = grandTotal/>
								<#assign accountTwoValue = currencyUomId />
								<td align="right" class="align-right" colspan="9"><div style="font-size: 14px;text-transform:uppercase"><b>${uiLabelMap.DATotalAmountPayment}</b></div></td><#--uiLabelMap.OrderTotalDue-->
								<td align="right" class="align-right" nowrap="nowrap" style="font-size: 14px;">
									<b>
										<@ofbizCurrency amount=grandTotal isoCode=currencyUomId rounding=2/>
									</b>
								</td>
							</tr>
						</#if>
						</tbody>
					</table>
				</div><!--.form-horizontal-->
				
				<input type="hidden" name="accountOneValue" id="accountOneValue" value="${accountOneValue?if_exists?default(0)}"/>
				<input type="hidden" name="accountTwoValue" id="accountTwoValue" value="${accountTwoValue?if_exists?default(VND)}"/>
			</div><!--.row-fluid-->	
			
		</div><!--.widget-main-->
	</div><!--.widget-body-->
</div>