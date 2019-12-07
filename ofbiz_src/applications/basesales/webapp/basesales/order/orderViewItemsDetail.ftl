<#-- copy from file .../orderItemsTabJQ.ftl -->
<style type="text/css">
	.contentTab {
		padding:10px
	}
</style>
<div class="tabDivInner">
	<ul style='margin-left: 30px;'>
		<li>${uiLabelMap.BSStatus} - ${uiLabelMap.BSQuantity}</li>
		<li class='titleTab2'>${uiLabelMap.BSAdjustment}</li>
	</ul>
	<div class='contentTab1 contentTab'>
		<#-- QUANTITY -->
		<div class="row-fluid">
			<div class="span4">
				<div class="row-fluid">
					<div class="span12">
						<#-- now show status details per line item -->
		                <#assign currentItemStatus = orderItem.getRelatedOne("StatusItem", false)>
		                <div class="screenlet order-item-status-list<#if currentItemStatus.statusCode?has_content> ${currentItemStatus.statusCode}</#if>">
		                    <div class="screenlet-body">
		                        <div class="current-status">
		                            <span>${uiLabelMap.CommonCurrent}</span>&nbsp;${currentItemStatus.get("description",locale)?default(currentItemStatus.statusId)}
		                        </div>
		                        <#if ("ITEM_CREATED" == (currentItemStatus.statusId) && "ORDER_APPROVED" == (orderHeader.statusId)) && security.hasEntityPermission("ORDERMGR", "_UPDATE", session)>
		                            <div>
		                                <a href="javascript:document.OrderApproveOrderItem_${orderItem.orderItemSeqId?default("")}.submit()" class="btn btn-mini btn-primary">${uiLabelMap.OrderApproveOrder}</a>
		                                <form name="OrderApproveOrderItem_${orderItem.orderItemSeqId?default("")}" method="post" action="<@ofbizUrl>changeOrderItemStatus</@ofbizUrl>">
		                                    <input type="hidden" name="statusId" value="ITEM_APPROVED"/>
		                                    <input type="hidden" name="orderId" value="${orderId?if_exists}"/>
		                                    <input type="hidden" name="orderItemSeqId" value="${orderItem.orderItemSeqId?if_exists}"/>
		                                </form>
		                            </div>
		                        </#if>
		                        <#assign orderItemStatuses = orderReadHelper.getOrderItemStatuses(orderItem)>
		                        <ul>
		                        <#list orderItemStatuses as orderItemStatus>
		                            <#assign loopStatusItem = orderItemStatus.getRelatedOne("StatusItem", false)>
		                            <li>
		                            <#if orderItemStatus.statusDatetime?has_content>
		                            	${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(orderItemStatus.statusDatetime, "", locale, timeZone)!}:&nbsp;
		                            </#if>
		                            ${loopStatusItem.get("description",locale)?default(orderItemStatus.statusId)}
		                            </li>
		                            <#--
		                            <#if orderItemStatus_has_next><br /></#if>
		                            -->
		                        </#list>
		                        </ul>
		                    </div>
		                </div>
		                
						<#if hasOlbPermission("MODULE", "RETURN_ORDER_VIEW", "")>
			                <#assign returns = orderItem.getRelated("ReturnItem", null, null, false)?if_exists>
			                <#if returns?has_content>
				                <div class="screenlet order-item-status-list">
				                	<div class="current-status">
			                            <span>${uiLabelMap.BSReturnOrder}</span>:&nbsp;(${returns?size})
			                        </div>
				                	<ul>
				                    <#list returns as returnItem>
				                        <#assign returnHeader = returnItem.getRelatedOne("ReturnHeader", false)>
				                        <#if returnHeader.statusId != "RETURN_CANCELLED">
				                            <li>
					                            <font color="red">${uiLabelMap.OrderReturned}</font>.
					                            ${uiLabelMap.CommonNbr}: <a href="<@ofbizUrl>viewReturnOrder?returnId=${returnItem.returnId}</@ofbizUrl>" target="_blank">${returnItem.returnId}</a>
				                        	</li>
										</#if>
				                    </#list>
									</ul>
								</div>
			                </#if>
						</#if>
					</div>
				</div>
			</div>
			<div class="span8">
				<table width="100%" border="0" cellpadding="1" class="table table-striped table-hover table-bordered dataTable">
					<tr>
						<td>
							${uiLabelMap.BSCalculateByDerivedUnit}: 
							<#if orderItem.quantityUomId?exists>
								<#assign quantityUomOI = delegator.findOne("Uom", {"uomId" : orderItem.quantityUomId}, false)/>
								<#if quantityUomOI?exists>${quantityUomOI.get("description", locale)}</#if>
							</#if>
						</td>
						<td>
							<span>${uiLabelMap.BSQuantityOrder}: ${orderItem.alternativeQuantity?if_exists}</span>
						</td>
					</tr>
				</table>
				
				<#if !product?exists>
            		<#assign product = orderItem.getRelatedOne("Product", true)>
            	</#if>
            	<#assign productId = orderItem.productId?if_exists>
				<table width="100%" border="0" cellpadding="1" class="table table-striped table-hover table-bordered dataTable">
					<tr>
						<td colspan="4">
							${uiLabelMap.BSCalculateByBasicUnit}: 
							<#if product?exists>
								<#if product.quantityUomId?exists>
									<#assign quantityUom = delegator.findOne("Uom", {"uomId" : product.quantityUomId}, false)/>
									<#if quantityUom?exists>${quantityUom.get("description", locale)}</#if>
								</#if>
							</#if>
						</td>
					</tr>
					<tr valign="top">
                        <#assign shippedQuantity = orderReadHelper.getItemShippedQuantity(orderItem)>
                        <#assign shipmentReceipts = delegator.findByAnd("ShipmentReceipt", {"orderId" : orderHeader.getString("orderId"), "orderItemSeqId" : orderItem.orderItemSeqId}, null, false)/>
                        <#assign totalReceived = 0.0>
                        <#if shipmentReceipts?exists && shipmentReceipts?has_content>
                            <#list shipmentReceipts as shipmentReceipt>
                                <#if shipmentReceipt.quantityAccepted?exists && shipmentReceipt.quantityAccepted?has_content>
                                    <#assign  quantityAccepted = shipmentReceipt.quantityAccepted>
                                    <#assign totalReceived = quantityAccepted + totalReceived>
                                </#if>
                                <#if shipmentReceipt.quantityRejected?exists && shipmentReceipt.quantityRejected?has_content>
                                    <#assign  quantityRejected = shipmentReceipt.quantityRejected>
                                    <#assign totalReceived = quantityRejected + totalReceived>
                                </#if>
                            </#list>
                        </#if>
                        <#if orderHeader.orderTypeId == "PURCHASE_ORDER">
                            <#assign remainingQuantity = ((orderItem.quantity?default(0) - orderItem.cancelQuantity?default(0)) - totalReceived?double)>
                        <#else>
                            <#assign remainingQuantity = ((orderItem.quantity?default(0) - orderItem.cancelQuantity?default(0)) - shippedQuantity?double)>
                        </#if>
                        <#-- to compute shortfall amount, sum up the orderItemShipGrpInvRes.quantityNotAvailable -->
                        <#assign shortfalledQuantity = 0/>
                        <#assign orderItemShipGrpInvResList = orderReadHelper.getOrderItemShipGrpInvResList(orderItem)/>
                        <#list orderItemShipGrpInvResList as orderItemShipGrpInvRes>
                            <#if (orderItemShipGrpInvRes.quantityNotAvailable?has_content && orderItemShipGrpInvRes.quantityNotAvailable > 0)>
                                <#assign shortfalledQuantity = shortfalledQuantity + orderItemShipGrpInvRes.quantityNotAvailable/>
                            </#if>
                        </#list>
                        <td><b>${uiLabelMap.OrderOrdered}</b></td>
                        <td>${orderItem.quantity?default(0)?string.number}</td>
                        <td><b>${uiLabelMap.OrderShipRequest}</b></td>
                        <td>${orderReadHelper.getItemReservedQuantity(orderItem)}</td>
                    </tr>
                    <tr valign="top">
                    	<#assign cancelQty = orderItem.cancelQuantity?default(0)/>
                    	<#if orderHeader.statusId == "ORDER_CANCELLED">
                    		<#assign cancelQty = orderItem.quantity?default(0)/>
                    		<#assign remainingQuantity = 0/>
                    	</#if>
                        <td><b>${uiLabelMap.OrderCancelled}</b></td>
                        <td>${cancelQty?string.number}</td>
                        <#if orderHeader.orderTypeId == "SALES_ORDER">
                        	<#assign pickedQty = orderReadHelper.getItemPickedQuantityBd(orderItem)>
                            <#if pickedQty gt 0 && orderHeader.statusId == "ORDER_APPROVED">
                                <td><font color="red"><b>${uiLabelMap.OrderQtyPicked}</b></font></td>
                                <td><font color="red">${pickedQty?default(0)?string.number}</font></td>
                            <#else>
                                <td><b>${uiLabelMap.OrderQtyPicked}</b></td>
                                <td>${pickedQty?default(0)?string.number}</td>
                            </#if>
                        <#else>
                        </#if>
                    </tr>
                    <tr valign="top">
                        <td><b>${uiLabelMap.OrderRemaining}</b></td>
                        <td>${remainingQuantity}</td>
                        <td><b>${uiLabelMap.OrderQtyShipped}</b></td>
                        <td>${shippedQuantity}</td>
                    </tr>
                    <tr valign="top">
                        <td><b>${uiLabelMap.OrderShortfalled}</b></td>
                        <td>${shortfalledQuantity}</td>
                        <td><b>${uiLabelMap.OrderOutstanding}</b></td>
                        <td>
                            <#-- Make sure digital goods without shipments don't always remainn "outstanding": if item is completed, it must have no outstanding quantity.  -->
							<#if (orderItem.statusId?has_content) && (orderItem.statusId == "ITEM_COMPLETED")>
                                <#assign outStandingQty = 0/>
                            <#elseif orderHeader.orderTypeId == "PURCHASE_ORDER">
                            	<#assign outStandingQty = (orderItem.quantity?default(0) - orderItem.cancelQuantity?default(0)) - totalReceived?double/>
                            <#elseif orderHeader.orderTypeId == "SALES_ORDER">
								<#assign outStandingQty = (orderItem.quantity?default(0) - orderItem.cancelQuantity?default(0)) - shippedQuantity?double/>
                            </#if>
	                    	<#if orderHeader.statusId == "ORDER_CANCELLED">
	                    		<#assign outStandingQty = 0/>
	                    	</#if>
							${outStandingQty?if_exists?string.number}
                        </td>
                    </tr>
                    <tr valign="top">
                        <td><b>${uiLabelMap.OrderInvoiced}</b></td>
                        <td>${orderReadHelper.getOrderItemInvoicedQuantity(orderItem)}</td>
                        <td><b>${uiLabelMap.OrderReturned}</b></td>
                        <#assign returnQuantityMap = orderReadHelper.getOrderItemReturnedQuantities()/>
                        <td>${returnQuantityMap.get(orderItem.orderItemSeqId)?default(0)}</td>
                    </tr>
				</table>
			</div>
		</div>
	</div>
	<#-- START SHIPPING
	... -->
	<#-- END SHIPPING -->
	<div class='contentTab2 contentTab'>
		<div class="row-fluid">
			<div class="span12">
				<table width="100%" border="0" cellpadding="1" class="table table-striped table-hover table-bordered dataTable">
					 <#--TODOMOVE-->
                    <#-- now show adjustment details per line item -->
					<#assign orderAdjustments = orderReadHelper.getAdjustments()!/>
                    <#assign orderItemAdjustments = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemAdjustmentList(orderItem, orderAdjustments)>
                    <#if orderItemAdjustments?exists && orderItemAdjustments?has_content>
                        <#list orderItemAdjustments as orderItemAdjustment>
                            <#assign adjustmentType = orderItemAdjustment.getRelatedOne("OrderAdjustmentType", true)>
                            <tr>
                                <td align="right" class="align-right" colspan="5">
                                    <span>${uiLabelMap.OrderAdjustment}</span>&nbsp;${adjustmentType.get("description",locale)}
                                    ${orderItemAdjustment.get("description",locale)?if_exists}
                                    <#if orderItemAdjustment.comments?has_content>
                                        (${orderItemAdjustment.comments?default("")})
                                    </#if>
                                    <#--
                                    <#if orderItemAdjustment.productPromoId?has_content>
                                        <a class="btn btn-mini btn-primary" href="<@ofbizUrl>viewProductPromo</@ofbizUrl>?productPromoId=${orderItemAdjustment.productPromoId}${externalKeyParam}" target="_blank">
                                        	${orderItemAdjustment.getRelatedOne("ProductPromo", false).getString("promoName")}</a>
                                    </#if>
                                    -->
                                    <#if orderItemAdjustment.orderAdjustmentTypeId == "SALES_TAX">
                                        <#if orderItemAdjustment.primaryGeoId?has_content>
                                            <#assign primaryGeo = orderItemAdjustment.getRelatedOne("PrimaryGeo", true)/>
                                            <#if primaryGeo.geoName?has_content>
                                                <span>${uiLabelMap.OrderJurisdiction}</span>&nbsp;${primaryGeo.geoName} [${primaryGeo.abbreviation?if_exists}]
                                            </#if>
                                            <#if orderItemAdjustment.secondaryGeoId?has_content>
                                                <#assign secondaryGeo = orderItemAdjustment.getRelatedOne("SecondaryGeo", true)/>
                                                <span>${uiLabelMap.CommonIn}</span>&nbsp;${secondaryGeo.geoName} [${secondaryGeo.abbreviation?if_exists}])
                                            </#if>
                                        </#if>
                                        <#if orderItemAdjustment.sourcePercentage?exists>
                                            <span>${uiLabelMap.OrderRate}</span>&nbsp;${orderItemAdjustment.sourcePercentage?string("0.######")}
                                        </#if>
                                        <#if orderItemAdjustment.customerReferenceId?has_content>
                                            <span>${uiLabelMap.OrderCustomerTaxId}</span>&nbsp;${orderItemAdjustment.customerReferenceId}
                                        </#if>
                                        <#if orderItemAdjustment.exemptAmount?exists>
                                            <span>${uiLabelMap.OrderExemptAmount}</span>&nbsp;${orderItemAdjustment.exemptAmount}
                                        </#if>
                                    </#if>
                                </td>
                                <#--<td colspan="3">&nbsp;</td>-->
                                <td align="right" class="align-right">
                                    <@ofbizCurrency amount=Static["org.ofbiz.order.order.OrderReadHelper"].calcItemAdjustment(orderItemAdjustment, orderItem) isoCode=currencyUomId/>
                                </td>
                            </tr>
                        </#list>
                    </#if>
                 	<#-- now show price info per line item -->
                    <#assign orderItemPriceInfos = orderReadHelper.getOrderItemPriceInfos(orderItem)>
                    <#if orderItemPriceInfos?exists && orderItemPriceInfos?has_content>
                        <#list orderItemPriceInfos as orderItemPriceInfo>
                            <tr>
                                <td align="right" class="align-right" colspan="5">
                                    <span>${uiLabelMap.ProductPriceRuleNameId}</span>&nbsp;
                                    [${orderItemPriceInfo.productPriceRuleId?if_exists}:${orderItemPriceInfo.productPriceActionSeqId?if_exists}]
                                    ${orderItemPriceInfo.description?if_exists}
                                </td>
                                <#--<td colspan="3">&nbsp;</td>-->
                                <td align="right" class="align-right">
                                    <@ofbizCurrency amount=orderItemPriceInfo.modifyAmount isoCode=currencyUomId/>
                                </td>
                            </tr>
                        </#list>
                    </#if>
				</table>
			</div><!--.span12-->
		</div><!--.row-fluid-->
	</div><!--.contentTab2-->
</div>
<script type="text/javascript">
	$(function(){
		$(".tabDivInner").jqxTabs({ theme: 'energyblue', width: '100%', height: 280});
	});
</script>