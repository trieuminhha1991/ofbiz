<#if orderHeader?exists && orderHeader.salesMethodChannelEnumId?exists>
	<#assign salesMethodChannelEnumId = orderHeader.salesMethodChannelEnumId />
</#if>
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
		<div class="title-status" id="statusTitle">
			${orderStatusMgs?if_exists}
		</div>
		<div><!--class="widget-main"-->
			<#--<div style="position: absolute;color:#FFF;background: rgba(0,0,0,0.3);top: -15px;right: 0;padding: 5px 20px;">
			<#if salesMethodChannelEnumId?exists && salesMethodChannelEnumId == "SALES_GT_CHANNEL">
				<#if orderHeader.statusId == "ORDER_CREATED">... ${uiLabelMap.BSOrderWaitingSupApprove}
				<#elseif orderHeader.statusId == "ORDER_SUPAPPROVED">... ${uiLabelMap.BSOrderWaitingSalesAdminApprove}
				<#elseif orderHeader.statusId == "ORDER_SADAPPROVED">... ${uiLabelMap.BSOrderWaitingPayment}
				<#elseif orderHeader.statusId == "ORDER_NPPAPPROVED">... ${uiLabelMap.BSOrderWaitingAccountantApprove}
				<#elseif orderHeader.statusId == "ORDER_APPROVED">... ${uiLabelMap.BSOrderWaitingLogisticProcessAndShipping}
				<#elseif orderHeader.statusId == "ORDER_HOLD">... ${uiLabelMap.BSOrderHolding}
				<#elseif orderHeader.statusId == "ORDER_COMPLETED">${uiLabelMap.BSOrderCompleted}
				<#elseif orderHeader.statusId == "ORDER_CANCELLED">${uiLabelMap.BSOrderCancelled}</#if>
			<#else>
				<#if orderHeader.statusId == "ORDER_CREATED">... ${uiLabelMap.BSOrderWaitingSupApprove}
				<#elseif orderHeader.statusId == "ORDER_SUPAPPROVED">... ${uiLabelMap.BSOrderWaitingSalesAdminApprove}
				<#elseif orderHeader.statusId == "ORDER_SADAPPROVED">... ${uiLabelMap.BSOrderWaitingAccountantApprove}
				<#elseif orderHeader.statusId == "ORDER_APPROVED">... ${uiLabelMap.BSOrderWaitingLogisticProcessAndShipping}
				<#elseif orderHeader.statusId == "ORDER_HOLD">... ${uiLabelMap.BSOrderHolding}
				<#elseif orderHeader.statusId == "ORDER_COMPLETED">${uiLabelMap.BSOrderCompleted}
				<#elseif orderHeader.statusId == "ORDER_CANCELLED">${uiLabelMap.BSOrderCancelled}</#if>
			</#if>
			</div>-->
			<#if hasOlbPermission("MODULE", "DISTRIBUTOR", "ADMIN")>
				<h3 style="text-align:center;font-weight:bold; padding:0;line-height:20px">
					${uiLabelMap.BSPurchaseOrder}
				</h3>
			<#else>
				<h3 style="text-align:center;font-weight:bold; padding:0;line-height:20px">
				${uiLabelMap.BSSalesOrderFormTitle}
				</h3>
			</#if>
			<div class="row-fluid">
				<div class="form-horizontal form-window-content-custom label-text-left content-description" style="margin:10px">
					<div class="row-fluid">
						<div class="span6">
							<div class="row-fluid">
								<div class="div-inline-block">
									<label>${uiLabelMap.BSOrderId}:</label>
								</div>
								<div class="div-inline-block">
									<span><i>${orderHeader.orderId?if_exists}</i></span>
								</div>
							</div>
							<div class="row-fluid">
								<div class="div-inline-block">
									<label>${uiLabelMap.BSCustomerPOId}:</label>
								</div>
								<div class="div-inline-block">
									<span>${orderHeader.externalId?if_exists}</span>
								</div>
							</div>
							<#--<div class="row-fluid">
								<div class="div-inline-block">
									<label>${uiLabelMap.BSOrderName}:</label>
								</div>
								<div class="div-inline-block">
									<span>${orderHeader.orderName?if_exists}</span>
								</div>
							</div>-->
							<div class="row-fluid">
								<div class="div-inline-block">
									<label>${uiLabelMap.BSOrderDate}:</label>
								</div>
								<div class="div-inline-block">
									<span>
										<#if orderHeader.orderDate?exists>
											${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(orderHeader.orderDate, "dd/MM/yyyy HH:mm:ss", locale, timeZone)!}
										</#if>
									</span>
								</div>
							</div>
							<div class="row-fluid">
								<div class="div-inline-block">
									<label>${uiLabelMap.BSCustomer}:</label>
								</div>
								<div class="div-inline-block">
									<span>${displayPartyNameResult?if_exists}</span>
								</div>
							</div>
							<div class="row-fluid">
								<div class="div-inline-block">
									<label>${uiLabelMap.BSBillingCustomer}:</label>
								</div>
								<div class="div-inline-block">
									<span><#if billingCustomer?exists>${billingCustomer.fullName} [${billingCustomer.partyId}]</#if></span>
								</div>
							</div>
							<div class="row-fluid">
								<div class="div-inline-block">
									<label>${uiLabelMap.BSAgreementCode}:</label>
								</div>
								<div class="div-inline-block">
									<span>
										${agreementCode?if_exists}
									</span>
								</div>
							</div>
							<div class="row-fluid">
								<div class="div-inline-block">
									<label>${uiLabelMap.BSPaymentMethod}:</label>
								</div>
								<div class="div-inline-block">
									<span>
										${paymentMethodTypeId?if_exists}
									</span>
								</div>
							</div>
							<div class="row-fluid">
								<div class="div-inline-block">
									<label>${uiLabelMap.BSSalesChannel}:</label>
								</div>
								<div class="div-inline-block">
									<span>
										<#if productStore?exists>${productStore.storeName?if_exists} [${productStore.productStoreId?if_exists}]</#if>
									</span>
								</div>
							</div>
						</div><!--.span6-->
						<div class="span6">
							<#if favorDistributorDelivery?exists>
							<div class="row-fluid">
								<div class="div-inline-block">
									<label class="red"><b>${uiLabelMap.BSDistributorDeliveryId}:</b></label>
								</div>
								<div class="div-inline-block">
									<span class="red">
										<#if isFavorDisDeliveryNoShipping?exists && isFavorDisDeliveryNoShipping>
										${uiLabelMap.BSNoShipping}
										<#else>
										${favorDistributorDelivery.fullName?if_exists} [${favorDistributorDelivery.partyCode?default(favorDistributorDelivery.partyId)}]
										</#if>
									</span>
								</div>
							</div>
							</#if>
							<#if desiredDeliveryDate?exists>
							<div class="row-fluid">
								<div class="div-inline-block">
									<label>${uiLabelMap.BSDesiredDeliveryDate}:</label>
								</div>
								<div class="div-inline-block">
									<span>
										${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(desiredDeliveryDate, "dd/MM/yyyy HH:mm:ss", locale, timeZone)!}
									</span>
								</div>
							</div>
							</#if>
							<#if shipAfterDate?exists>
							<div class="row-fluid">
								<div class="div-inline-block">
									<label>${uiLabelMap.BSShipAfterDate}:</label>
								</div>
								<div class="div-inline-block">
									<span>
										${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(shipAfterDate, "dd/MM/yyyy HH:mm:ss", locale, timeZone)!}
									</span>
								</div>
							</div>
							</#if>
							<#if shipBeforeDate?exists>
							<div class="row-fluid">
								<div class="div-inline-block">
									<label>${uiLabelMap.BSShipBeforeDate}:</label>
								</div>
								<div class="div-inline-block">
									<span>
										${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(shipBeforeDate, "dd/MM/yyyy HH:mm:ss", locale, timeZone)!}
									</span>
								</div>
							</div>
							</#if>
							<div class="row-fluid" style="margin-top:5px">
								<div class="div-inline-block" style="width:65px; vertical-align: top">
									<label style="line-height: 20px;">${uiLabelMap.OrderDestination}:</label>
								</div>
								<div class="div-inline-block" style="width:calc(100% - 70px)">
									<span>
										<ul class="unstyled spaced" style="margin: 0 0 0 0">
										<#list shippingAddressList as shippingAddressItem>
						                	<li style="margin-bottom:0; margin-top:0">
										      	${shippingAddressItem?if_exists}
											</li>
						                </#list>
						                </ul>
									</span>
								</div>
							</div>
							<div class="row-fluid" style="margin-top:5px">
								<div class="div-inline-block">
									<label>${uiLabelMap.BSShippingMethod}:</label>
								</div>
								<div class="div-inline-block">
									<span>
									<#if shippingMethodTypeGv?exists && shippingMethodTypeGv.shipmentMethodTypeId?has_content>
										<#assign shippingMethodTypeTmp = delegator.findOne("ShipmentMethodType", {"shipmentMethodTypeId": shippingMethodTypeGv.shipmentMethodTypeId}, false)!>
										${shippingMethodTypeTmp?if_exists.get("description", locale)?if_exists} [${shippingMethodTypeGv.shipmentMethodTypeId}]
									</#if>
									</span>
								</div>
							</div>
							<div class="row-fluid" style="margin-top:5px">
								<div class="div-inline-block">
									<label>${uiLabelMap.BSSalesExecutive}:</label>
								</div>
								<div class="div-inline-block">
									<span>
										${salesExecutiveId?if_exists}
									</span>
								</div>
							</div>
							<#if callcenterId?exists>
							<div class="row-fluid">
								<div class="div-inline-block">
									<label>${uiLabelMap.BSCallcenter}:</label>
								</div>
								<div class="div-inline-block">
									<span>
										${callcenterId?if_exists}
									</span>
								</div>
							</div>
							</#if>
							<#if salesadminId?exists>
							<div class="row-fluid">
								<div class="div-inline-block">
									<label>${uiLabelMap.BSSalesAdmin}:</label>
								</div>
								<div class="div-inline-block">
									<span>
										${salesadminId?if_exists}
									</span>
								</div>
							</div>
							</#if>
							<#if totalLoyaltyPoint?exists>
							<div class="row-fluid">
								<div class="div-inline-block">
									<label>${uiLabelMap.BSLoyaltyPoint} <i class="fa fa-gift pink" aria-hidden="true"></i>:</label>
								</div>
								<div class="div-inline-block">
									<span>
										${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(totalLoyaltyPoint, "#,###.##", locale)} <span style="text-transform: lowercase;">(${uiLabelMap.BSPoint})</span>
									</span>
								</div>
							</div>
							</#if>
						</div><!--.span6-->
					</div>
				</div><!-- .form-horizontal -->
				<div class="form-horizontal basic-custom-form">
					<table cellspacing="0" cellpadding="1" border="0" class="table table-bordered">
						<thead>
							<tr style="font-weight: bold;">
								<td>${uiLabelMap.BSSTT}</td>
								<td class="align-left">${uiLabelMap.BSProduct} - ${uiLabelMap.BSProductName}</td>
								<td style="width:20px">${uiLabelMap.BSProdPromo}</td>
								<td style="width:30px">${uiLabelMap.BSUom}</td>
								<td align="left" class="align-left">${uiLabelMap.BSQuantity}</td>
							  	<td align="left" class="align-left" style="width:60px">${uiLabelMap.BSPriceBeforeVAT}</td>
							  	<td align="left" class="align-left">${uiLabelMap.BSAdjustment}</td>
								<td align="left" class="align-left">${uiLabelMap.BSItemTotal} <br />${uiLabelMap.BSParenthesisBeforeVAT}</td>
							</tr>
						</thead>
						<tbody>
						<#list listItemLine as itemLine>
							<#assign returnedQuantity = returnQuantityMap.get(itemLine.orderItem.orderItemSeqId)?default(0)/>
	            			<#assign itemType = itemLine.orderItem.getRelatedOne("OrderItemType", false)?if_exists>
	            			<tr>
	            				<#assign productId = itemLine.productId?if_exists/>
	            				<#assign product = itemLine.product?if_exists/>
								<td>${itemLine_index + 1}</td>
		                        <#if productId?exists && productId == "shoppingcart.CommentLine">
					                <td colspan="7" valign="top">
					                  	<div><b> &gt;&gt; ${itemLine.itemDescription?if_exists}</b></div>
					                </td>
		              			<#else>
		            				<td valign="top" <#if returnedQuantity &gt; 0>class="background-important-nd"</#if>>
					                  	<div>
				                  		<#if itemLine.supplierProductId?has_content>
	                                        ${itemLine.supplierProductId} - ${itemLine.itemDescription?if_exists}
	                                    <#elseif productId?exists>
	                                    	<#--<a href="<@ofbizUrl>editProduct?productId=${productId}</@ofbizUrl>">${productId}</a>-->
	                                         ${itemLine.productCode?default(productId)}  - ${itemLine.itemDescription?if_exists}
	                                        <#if (product.salesDiscontinuationDate)?exists && Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp().after(product.salesDiscontinuationDate)>
	                                            <br /><span style="color: red;">${uiLabelMap.BSProductSalesDiscontinued}: ${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(product.salesDiscontinuationDate, "", locale, timeZone)!}</span>
	                                        </#if>
	                                        <#-- <#if (product.purchaseDiscontinuationDate)?exists && Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp().after(product.purchaseDiscontinuationDate)>
	                                            <br /><span style="color: red;">${uiLabelMap.BSProductPurchaseDiscontinued}: ${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(product.purchaseDiscontinuationDate, "", locale, timeZone)!}</span>
	                                        </#if> -->
	                                    <#elseif itemLine.orderItemType?exists>
	                                        ${itemLine.orderItemType.description} - ${itemLine.itemDescription?if_exists}
	                                    <#else>
	                                        ${itemLine.itemDescription?if_exists}
	                                    </#if>
										<#if itemLine.comments?exists>
											 - <span class="text-error">(${itemLine.comments})</span>
										</#if>
										<#if "PRODPROMO_ORDER_ITEM" == itemLine.orderItemTypeId>
											 - <span class="text-error">(<b>${uiLabelMap.BSProductReturnPromo}</b>)</span>
										</#if>
										<#if itemLine.adjustmentOnlyPromo?has_content && (itemLine.adjustmentOnlyPromo != 0)>
											 <br/>- <span class="text-success">(<b>${uiLabelMap.BSAbbPromo}</b>: <@ofbizCurrency amount=itemLine.adjustmentOnlyPromo isoCode=currencyUomId/>)</span>
										</#if>
										<#if returnedQuantity &gt; 0> - <span class="text-error">(${uiLabelMap.OrderReturned} <b>${returnedQuantity}</b>)</span></#if>
					                  	</div>
		               				</td>
		               				<td align="right" class="align-center" valign="top">
		                				${itemLine.isPromo?if_exists}
					                </td>
		                			<td align="right" class="align-center" valign="top">
		                				${itemLine.quantityUomDescription?if_exists}
					                </td>
					                <td align="right" class="align-right" valign="top">
					                  	<#if itemLine.quantity?exists>${itemLine.quantity?string.number}</#if>
					                </td>
					                <td align="right" class="align-right" valign="top"><#-- Unit price -->
					                  	<@ofbizCurrency amount=itemLine.unitPriceBeVAT isoCode=currencyUomId/>
					                </td>
					                <td align="right" class="align-right" valign="top"><#-- Adjustment -->
										<@ofbizCurrency amount=itemLine.adjustment isoCode=currencyUomId/>
					                </td>					               
					                <td align="right" class="align-right" valign="top" nowrap="nowrap"><#-- Sub total before VAT -->
				                  		<@ofbizCurrency amount=itemLine.subTotalBeVAT isoCode=currencyUomId/>
					                </td>
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
						<#list listTaxTotal as taxTotalItem>
							<tr>
								<td align="right" class="align-right" colspan="7">
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
						
						<#list orderHeaderAdjustments as orderHeaderAdjustment>
			                <#assign adjustmentType = orderHeaderAdjustment.getRelatedOne("OrderAdjustmentType", false)>
			                <#assign adjustmentAmount = Static["org.ofbiz.order.order.OrderReadHelper"].calcOrderAdjustment(orderHeaderAdjustment, orderSubTotal)>
			                <#if adjustmentAmount != 0>
			                    <tr>
			                        <td align="right" class="align-right" colspan="7">
			                        	<#assign adjPrinted = false>
                                        <#if orderHeaderAdjustment.comments?has_content>
                                            ${orderHeaderAdjustment.comments}
                                            <#assign adjPrinted = true>
                                        </#if>
			                            <#if orderHeaderAdjustment.description?has_content>${orderHeaderAdjustment.description}
                                            <#assign adjPrinted = true>
                                        </#if>
			                            <#if !adjPrinted><span>${adjustmentType.get("description", locale)}</span></#if>
			                        </td>
			                        <td align="right" class="align-right" nowrap="nowrap">
			                        	<#if (adjustmentAmount &lt; 0)>
	                                		<#assign adjustmentAmountNegative = -adjustmentAmount>
			                            	(<@ofbizCurrency amount=adjustmentAmountNegative isoCode=currencyUomId/>)
			                            <#else>
			                            	<@ofbizCurrency amount=adjustmentAmount isoCode=currencyUomId/>
			                            </#if>
			                        </td>
			                    </tr>
			                </#if>
			            </#list>
						
						<#-- subtotal -->
	          			<tr>
	            			<td align="right" class="align-right" colspan="7"><div><b>${uiLabelMap.BSOrderItemsSubTotal}</b></div></td>
	            			<td align="right" class="align-right" nowrap="nowrap">
	            				<#if (orderSubTotal &lt; 0)>
                            		<#assign orderSubTotalNegative = -orderSubTotal>
                            		(<@ofbizCurrency amount=orderSubTotalNegative isoCode=currencyUomId/>)
                            	<#else>
                            		<@ofbizCurrency amount=orderSubTotal isoCode=currencyUomId/>
                        		</#if>
	        				</td>
	          			</tr>
	          			
	          			<#-- other adjustments -->
			            <tr>
			              	<td align="right" class="align-right" colspan="7"><div><b>${uiLabelMap.BSTotalOrderAdjustments}</b></div></td>
			              	<td align="right" class="align-right" nowrap="nowrap">
			              		<#if (otherAdjAmount &lt; 0)>
                            		<#assign otherAdjAmountNegative = -otherAdjAmount>
									(<@ofbizCurrency amount=otherAdjAmountNegative isoCode=currencyUomId/>)
								<#else>
									<@ofbizCurrency amount=otherAdjAmount isoCode=currencyUomId/>
								</#if>
							</td>
			            </tr>
	          			
	          			<#-- tax adjustments -->
			          	<tr>
				            <td align="right" class="align-right" colspan="7"><div><b>${uiLabelMap.OrderTotalSalesTax}</b></div></td>
				            <td align="right" class="align-right" nowrap="nowrap">
				            	<#if (taxAmount &lt; 0)>
                            		<#assign taxAmountNegative = -taxAmount>
				            		(<@ofbizCurrency amount=taxAmountNegative isoCode=currencyUomId/>)
				            	<#else>
				            		<@ofbizCurrency amount=taxAmount isoCode=currencyUomId/>
				            	</#if>
				            </td>
			          	</tr>
	          			
	          			<#-- shipping adjustments -->
			          	<tr>
				            <td align="right" class="align-right" colspan="7"><div><b>${uiLabelMap.OrderTotalShippingAndHandling}</b></div></td>
				            <td align="right" class="align-right" nowrap="nowrap"><div><@ofbizCurrency amount=shippingAmount isoCode=currencyUomId/></div></td>
			          	</tr>
			          	
			          	<#-- grand total -->
			          	<tr>
			          		<#assign accountOneValue = grandTotal/>
			          		<#assign accountTwoValue = currencyUomId />
				            <td align="right" class="align-right" colspan="7"><div style="font-size: 14px;text-transform:uppercase"><b>${uiLabelMap.BSTotalAmountPayment}</b></div></td><#--uiLabelMap.OrderTotalDue-->
				            <td align="right" class="align-right" nowrap="nowrap" style="font-size: 14px;">
				            	<b><#if (grandTotal &lt; 0)>
                            		<#assign grandTotalNegative = -grandTotal>
                            		<#assign accountOneValue = -grandTotal />
				            		(<@ofbizCurrency amount=grandTotalNegative isoCode=currencyUomId/>)
				            	<#else>
				            		<@ofbizCurrency amount=grandTotal isoCode=currencyUomId/>
				            	</#if></b>
				            </td>
			          	</tr>
						</tbody>
					</table>
				</div><!--.form-horizontal-->
				
				<input type="hidden" name="accountOneValue" id="accountOneValue" value="${accountOneValue?default(0)}"/>
				<input type="hidden" name="accountTwoValue" id="accountTwoValue" value="${accountTwoValue?default(VND)}"/>
				
				<#if orderAdjustmentsPromo?exists && orderAdjustmentsPromo?has_content>
				<div class="row-fluid" style="margin-bottom:20px">
					<div class="span12">
						<h4 class="smaller green" style="display:inline-block">
							${uiLabelMap.BSPromotionDetailApplyInOrder}
						</h4>
						<div>
							<ul>
							<#list orderAdjustmentsPromo as objAdj>
								<li>[<a href="<@ofbizUrl>viewPromotion?productPromoId=${objAdj.productPromoId}</@ofbizUrl>" target="_blank">${objAdj.productPromoId}</a>] ${objAdj.promoName}: <b><@ofbizCurrency amount=objAdj.amount isoCode=currencyUomId/></b>
								<#if objAdj.productPromoCodeIds?has_content>
									- ${uiLabelMap.OrderWithPromoCode} [<#list objAdj.productPromoCodeIds as productPromoCodeId>${productPromoCodeId}<#if productPromoCodeId_has_next>, </#if></#list>] 
								</#if></li>
							</#list>
							</ul>
						</div>
					</div>
				</div>
				</#if>
			</div><!--.row-fluid-->
		</div><!--.widget-main-->
	</div><!--.widget-body-->
	
	<div id="paymentOrderContainer">
		${screens.render("component://basesales/widget/OrderScreens.xml#OrderViewPaymentOrderAllAjax")}
	</div>
</div>