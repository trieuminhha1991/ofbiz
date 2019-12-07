<#if orderHeader?has_content>
	<div id="items-tab" class="tab-pane">
		<h4 class="smaller lighter green" style="display:inline-block">
			${uiLabelMap.OrderOrderItems}
		</h4>
        <table class="table table-striped table-bordered table-hover dataTable" cellspacing='0'>
            <thead>
            	<tr>
	                <th width="20%">${uiLabelMap.ProductProduct}</th>
	                <th width="30%">${uiLabelMap.CommonStatus}</th>
	                <th width="20%">${uiLabelMap.OrderQuantity}</th>
	                <th width="10%" align="right" class="align-right">${uiLabelMap.OrderUnitList}</th>
	                <th width="10%" align="right" class="align-right">${uiLabelMap.OrderAdjustments}</th>
	                <th width="10%" align="right" class="align-right">${uiLabelMap.OrderSubTotal}</th>
	            </tr>
            </thead>
            <tbody>
            <#if !orderItemList?has_content>
                <tr>
                    <td colspan="7">
                        <font color="red">${uiLabelMap.checkhelper_sales_order_lines_lookup_failed}</font>
                    </td>
                    <td></td>
                </tr>
            <#else>
                <#assign itemClass = "2">
                <#list orderItemList as orderItem>
                    <#assign orderItemContentWrapper = Static["org.ofbiz.order.order.OrderContentWrapper"].makeOrderContentWrapper(orderItem, request)>
                    <#assign orderItemShipGrpInvResList = orderReadHelper.getOrderItemShipGrpInvResList(orderItem)>
                    <#if orderHeader.orderTypeId == "SALES_ORDER"><#assign pickedQty = orderReadHelper.getItemPickedQuantityBd(orderItem)></#if>
                    <tr<#if itemClass == "1"> class="alternate-row"</#if>>
                        <#assign orderItemType = orderItem.getRelatedOne("OrderItemType", false)?if_exists>
                        <#assign productId = orderItem.productId?if_exists>
                        <#if productId?exists && productId == "shoppingcart.CommentLine">
                            <td colspan="7" valign="top"> &gt;&gt; ${orderItem.itemDescription}</td>
                        <#else>
                            <td colspan="7">
                                <div class="order-item-description" style="font-weight:bold">
                                    <#if orderItem.supplierProductId?has_content>
                                        ${orderItem.supplierProductId} - ${orderItem.itemDescription?if_exists}
                                    <#elseif productId?exists>
                                        ${orderItem.productId?default("N/A")} - ${orderItem.itemDescription?if_exists}
                                        <#if (product.salesDiscontinuationDate)?exists && Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp().after(product.salesDiscontinuationDate)>
                                            <br />
                                            <span style="color: red;">${uiLabelMap.OrderItemDiscontinued}: ${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(product.salesDiscontinuationDate, "", locale, timeZone)!}</span>
                                        </#if>
                                    <#elseif orderItemType?exists>
                                        ${orderItemType.description} - ${orderItem.itemDescription?if_exists}
                                    <#else>
                                        ${orderItem.itemDescription?if_exists}
                                    </#if>
                                    <#assign orderItemAttributes = orderItem.getRelated("OrderItemAttribute", null, null, false)/>
                                    <#if orderItemAttributes?has_content>
                                        <ul>
                                        <#list orderItemAttributes as orderItemAttribute>
                                            <li>
                                                ${orderItemAttribute.attrName} : ${orderItemAttribute.attrValue}
                                            </li>
                                        </#list>
                                        </ul>
                                    </#if>
                                </div>
                                <div style="float:left;">
                                    <#assign downloadContents = delegator.findByAnd("OrderItemAndProductContentInfo", {"orderId" : orderId, "orderItemSeqId" : orderItem.orderItemSeqId, "productContentTypeId" : "DIGITAL_DOWNLOAD", "statusId" : "ITEM_COMPLETED"})/>
                                    <#if downloadContents?has_content>
                                        <#list downloadContents as downloadContent>
                                            <a href="/content/control/ViewSimpleContent?contentId=${downloadContent.contentId}" class="btn btn-mini btn-primary" target="_blank">${uiLabelMap.ContentDownload}</a>&nbsp;
                                        </#list>
                                    </#if>
                                    <#--<a href="/ecommerce/control/product?product_id=${productId}" class="btn btn-mini btn-primary" target="_blank">${uiLabelMap.OrderEcommerce}</a>
                                    <#if orderItemContentWrapper.get("IMAGE_URL")?has_content>
                                        <a href="<@ofbizUrl>viewimage?orderId=${orderId}&amp;orderItemSeqId=${orderItem.orderItemSeqId}&amp;orderContentTypeId=IMAGE_URL</@ofbizUrl>"
                                           target="_orderImage" class="btn btn-mini btn-primary">${uiLabelMap.OrderViewImage}</a>
                                    </#if>-->
                                </div>
                            </td>
                        </#if>
                    </tr>
                    <tr<#if itemClass == "1"> class="alternate-row"</#if>>
                        <#if productId?exists && productId == "shoppingcart.CommentLine">
                            <td colspan="7" valign="top"> &gt;&gt; ${orderItem.itemDescription}</td>
                        <#else>
                            <td valign="top">
                                <#if productId?has_content>
                                    <#assign product = orderItem.getRelatedOne("Product", true)>
                                </#if>
                                <#if productId?exists>
                                    <#-- INVENTORY -->
                                    <#if (orderHeader.statusId != "ORDER_COMPLETED") && availableToPromiseMap?exists && quantityOnHandMap?exists && availableToPromiseMap.get(productId)?exists && quantityOnHandMap.get(productId)?exists>
                                        <#assign mktgPkgATP = mktgPkgATPMap.get(productId)?default(0)>
                                        <#assign mktgPkgQOH = mktgPkgQOHMap.get(productId)?default(0)>
                                        <#--
                                        <#assign quantityToProduce = 0>
                                        <#assign atpQuantity = availableToPromiseMap.get(productId)?default(0)>
                                        <#assign qohQuantity = quantityOnHandMap.get(productId)?default(0)>
                                        <#assign requiredQuantity = requiredProductQuantityMap.get(productId)?default(0)>
                                        <#assign onOrderQuantity = onOrderProductQuantityMap.get(productId)?default(0)>
                                        <#assign inProductionQuantity = productionProductQuantityMap.get(productId)?default(0)>
                                        <#assign unplannedQuantity = requiredQuantity - qohQuantity - inProductionQuantity - onOrderQuantity - mktgPkgQOH>
                                        <#if unplannedQuantity &lt; 0><#assign unplannedQuantity = 0></#if>
                                        -->
                                        <div>
                                        	<#if availableToPromiseByFacilityMap?exists && quantityOnHandByFacilityMap?exists && quantityOnHandByFacilityMap.get(productId)?exists && availableToPromiseByFacilityMap.get(productId)?exists>
                                                <#assign atpQuantityByFacility = availableToPromiseByFacilityMap.get(productId)?default(0)>
                                                <#assign qohQuantityByFacility = quantityOnHandByFacilityMap.get(productId)?default(0)>
                                                <div>
                                                    <div>${uiLabelMap.DAInventoryK} [${facility.facilityName?if_exists}]:</div>
                                                    <div style="margin-left:25px">
                                                        <span style="width:35px; display:inline-block">${uiLabelMap.ProductQoh}:</span> ${qohQuantityByFacility}<br />
                                                        <span style="width:35px; display:inline-block">${uiLabelMap.ProductAtp}:</span> ${atpQuantityByFacility}
                                                    </div>
                                                </div>
                                            </#if>
                                            <#if (product?has_content) && (product.productTypeId?has_content) && Static["org.ofbiz.entity.util.EntityTypeUtil"].hasParentType(delegator, "ProductType", "productTypeId", product.productTypeId, "parentTypeId", "MARKETING_PKG")>
                                                <div>
                                                    <div>${uiLabelMap.ProductMarketingPackageQOH}</div>
                                                    <div style="margin-left:25px">
                                                        ${mktgPkgQOH} (${uiLabelMap.ProductAtp}: ${mktgPkgATP})
                                                    </div>
                                                </div>
                                            </#if>
                                        </div>
                                	</#if>
                           	 	</#if>
                            </td>
                            <#-- now show status details per line item -->
                            <#assign currentItemStatus = orderItem.getRelatedOne("StatusItem", false)>
                            <td colspan="1" valign="top">
                                <div class="screenlet order-item-status-list<#if currentItemStatus.statusCode?has_content> ${currentItemStatus.statusCode}</#if>">
                                    <div class="screenlet-body" style="margin-top:0">
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
                                        <#list orderItemStatuses as orderItemStatus>
                                            <#assign loopStatusItem = orderItemStatus.getRelatedOne("StatusItem", false)>
                                            <#if orderItemStatus.statusDatetime?has_content>${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(orderItemStatus.statusDatetime, "", locale, timeZone)!}:&nbsp;</#if>${loopStatusItem.get("description",locale)?default(orderItemStatus.statusId)}
                                            <#if orderItemStatus_has_next><br /></#if>
                                        </#list>
                                    </div>
                                </div>
                                <#assign returns = orderItem.getRelated("ReturnItem", null, null, false)?if_exists>
                                <#if returns?has_content>
                                    <#list returns as returnItem>
                                        <#assign returnHeader = returnItem.getRelatedOne("ReturnHeader", false)>
                                        <#if returnHeader.statusId != "RETURN_CANCELLED">
                                            <font color="red">${uiLabelMap.OrderReturned}</font>.
                                            ${uiLabelMap.CommonNbr}: <a href="<@ofbizUrl>returnMain?returnId=${returnItem.returnId}</@ofbizUrl>">${returnItem.returnId}</a>
                                        </#if>
                                    </#list>
                                </#if>
                                <#-- display the ship estimated/before/after dates -->
                                <div style="margin-top:10px">
				                    <#if orderItem.estimatedShipDate?exists>
				                        <div>
				                       		<span>${uiLabelMap.OrderEstimatedShipDate}</span>:&nbsp;${Static["org.ofbiz.base.util.UtilFormatOut"].formatDate(orderItem.estimatedShipDate, "", locale, timeZone)!}
				                        </div>
				                    </#if>
				                    <#if orderItem.estimatedDeliveryDate?exists>
				                        <div>
			                                <span>${uiLabelMap.OrderOrderQuoteEstimatedDeliveryDate}</span>:&nbsp;${Static["org.ofbiz.base.util.UtilFormatOut"].formatDate(orderItem.estimatedDeliveryDate, "", locale, timeZone)!}
				                        </div>
				                    </#if>
				                    <#if orderItem.shipAfterDate?exists>
				                        <div>
		                                	<span>${uiLabelMap.OrderShipAfterDate}</span>:&nbsp;${Static["org.ofbiz.base.util.UtilFormatOut"].formatDate(orderItem.shipAfterDate, "", locale, timeZone)!}
				                        </div>
				                    </#if>
				                    <#if orderItem.shipBeforeDate?exists>
				                        <div>
			                                <span>${uiLabelMap.OrderShipBeforeDate}</span>:&nbsp;${Static["org.ofbiz.base.util.UtilFormatOut"].formatDate(orderItem.shipBeforeDate, "", locale, timeZone)!}
				                        </div>
				                    </#if>
                                </div>
                            </td>
                            <#-- QUANTITY -->
                            <td align="right" class="align-right no-padding" valign="top" nowrap="nowrap">
                                <table class="table-padding-small">
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
                                        <td><b>${uiLabelMap.OrderCancelled}</b></td>
                                        <td>${orderItem.cancelQuantity?default(0)?string.number}</td>
                                        <#if orderHeader.orderTypeId == "SALES_ORDER">
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
                                                0
                                            <#elseif orderHeader.orderTypeId == "PURCHASE_ORDER">
                                                ${(orderItem.quantity?default(0) - orderItem.cancelQuantity?default(0)) - totalReceived?double}
                                            <#elseif orderHeader.orderTypeId == "SALES_ORDER">
                                                ${(orderItem.quantity?default(0) - orderItem.cancelQuantity?default(0)) - shippedQuantity?double}
                                            </#if>
                                        </td>
                                    </tr>
                                    <tr valign="top">
                                        <td><b>${uiLabelMap.OrderInvoiced}</b></td>
                                        <td>${orderReadHelper.getOrderItemInvoicedQuantity(orderItem)}</td>
                                        <td><b>${uiLabelMap.OrderReturned}</b></td>
                                        <td>${returnQuantityMap.get(orderItem.orderItemSeqId)?default(0)}</td>
                                    </tr>
                                </table>
                            </td>
                            <td align="right" class="align-right" valign="top" nowrap="nowrap">
                                <@ofbizCurrency amount=orderItem.unitPrice isoCode=currencyUomId/>
                                / <@ofbizCurrency amount=orderItem.unitListPrice isoCode=currencyUomId/>
                            </td>
                            <td align="right" class="align-right" valign="top" nowrap="nowrap">
                                <@ofbizCurrency amount=Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemAdjustmentsTotal(orderItem, orderAdjustments, true, false, false) isoCode=currencyUomId/>
                            </td>
                            <td align="right" class="align-right" valign="top" nowrap="nowrap">
                                <#if orderItem.statusId != "ITEM_CANCELLED">
                                    <@ofbizCurrency amount=Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemSubTotal(orderItem, orderAdjustments) isoCode=currencyUomId/>
                                <#else>
                                    <@ofbizCurrency amount=0.00 isoCode=currencyUomId/>
                                </#if>
                            </td>
                            
                        </#if>
                    </tr>
                    <#-- show info from workeffort -->
                    <#assign workOrderItemFulfillments = orderItem.getRelated("WorkOrderItemFulfillment", null, null, false)?if_exists>
                    <#if workOrderItemFulfillments?has_content>
                        <#list workOrderItemFulfillments as workOrderItemFulfillment>
                            <#assign workEffort = workOrderItemFulfillment.getRelatedOne("WorkEffort", true)>
                            <tr<#if itemClass == "1"> class="alternate-row"</#if>>
                                
                                <td colspan="6">
                                    <#if orderItem.orderItemTypeId != "RENTAL_ORDER_ITEM">
                                        <span>${uiLabelMap.ManufacturingProductionRun}</span>
                                        <a href="/manufacturing/control/ShowProductionRun?productionRunId=${workEffort.workEffortId}${externalKeyParam}"
                                            class="btn btn-mini btn-primary">${workEffort.workEffortId}</a>
                                        ${uiLabelMap.OrderCurrentStatus}
                                        ${(delegator.findOne("StatusItem", Static["org.ofbiz.base.util.UtilMisc"].toMap("statusId", workEffort.getString("currentStatusId")), true).get("description",locale))?if_exists}
                                    <#else>
                                        ${uiLabelMap.CommonFrom}
                                        : <#if workEffort.estimatedStartDate?has_content>${Static["org.ofbiz.base.util.UtilFormatOut"].formatDate(workEffort.estimatedStartDate, "", locale, timeZone)!}</#if> ${uiLabelMap.CommonTo}
                                        : <#if workEffort.estimatedCompletionDate?has_content>${Static["org.ofbiz.base.util.UtilFormatOut"].formatDate(workEffort.estimatedCompletionDate, "", locale, timeZone)!}</#if> ${uiLabelMap.OrderNumberOfPersons}
                                        : ${workEffort.reservPersons?default("")}
                                    </#if>
                                </td>
                            </tr>
                            <#break><#-- need only the first one -->
                        </#list>
                    </#if>
                    <#-- show linked order lines -->
                    <#assign linkedOrderItemsTo = delegator.findByAnd("OrderItemAssoc", Static["org.ofbiz.base.util.UtilMisc"].toMap("orderId", orderItem.getString("orderId"), "orderItemSeqId", orderItem.getString("orderItemSeqId")), null, false)>
                    <#assign linkedOrderItemsFrom = delegator.findByAnd("OrderItemAssoc", Static["org.ofbiz.base.util.UtilMisc"].toMap("toOrderId", orderItem.getString("orderId"), "toOrderItemSeqId", orderItem.getString("orderItemSeqId")), null, false)>
                    <#if linkedOrderItemsTo?has_content>
                        <#list linkedOrderItemsTo as linkedOrderItem>
                            <#assign linkedOrderId = linkedOrderItem.toOrderId>
                            <#assign linkedOrderItemSeqId = linkedOrderItem.toOrderItemSeqId>
                            <#assign linkedOrderItemValue = linkedOrderItem.getRelatedOne("ToOrderItem", false)>
                            <#assign linkedOrderItemValueStatus = linkedOrderItemValue.getRelatedOne("StatusItem", false)>
                            <#assign description = linkedOrderItem.getRelatedOne("OrderItemAssocType", false).getString("description")/>
                            <tr<#if itemClass == "1"> class="alternate-row"</#if>>
                                <td colspan="6">
                                    <span>${uiLabelMap.OrderLinkedToOrderItem}</span>&nbsp;(${description?if_exists})
                                    <a href="<@ofbizUrl>orderView?orderId=${linkedOrderId}</@ofbizUrl>" class="btn btn-mini btn-primary" target="_blank">
                                    	${linkedOrderId}/${linkedOrderItemSeqId}</a>&nbsp;${linkedOrderItemValueStatus.description?if_exists}
                                </td>
                            </tr>
                        </#list>
                    </#if>
                    <#if linkedOrderItemsFrom?has_content>
                        <#list linkedOrderItemsFrom as linkedOrderItem>
                            <#assign linkedOrderId = linkedOrderItem.orderId>
                            <#assign linkedOrderItemSeqId = linkedOrderItem.orderItemSeqId>
                            <#assign linkedOrderItemValue = linkedOrderItem.getRelatedOne("FromOrderItem", false)>
                            <#assign linkedOrderItemValueStatus = linkedOrderItemValue.getRelatedOne("StatusItem", false)>
                            <#assign description = linkedOrderItem.getRelatedOne("OrderItemAssocType", false).getString("description")/>
                            <tr<#if itemClass == "1"> class="alternate-row"</#if>>
                                <td colspan="6">
                                    <span>${uiLabelMap.OrderLinkedFromOrderItem}</span>&nbsp;(${description?if_exists})
                                    <a href="<@ofbizUrl>orderView?orderId=${linkedOrderId}</@ofbizUrl>" class="btn btn-mini btn-primary" target="_blank">
                                    	${linkedOrderId}/${linkedOrderItemSeqId}</a>&nbsp;${linkedOrderItemValueStatus.description?if_exists}
                                </td>
                            </tr>
                        </#list>
                    </#if>
                    <#-- show linked requirements -->
                    <#assign linkedRequirements = orderItem.getRelated("OrderRequirementCommitment", null, null, false)?if_exists>
                    <#if linkedRequirements?has_content>
                        <#list linkedRequirements as linkedRequirement>
                            <tr<#if itemClass == "1"> class="alternate-row"</#if>>
                                
                                <td colspan="6">
                                    <span>${uiLabelMap.OrderLinkedToRequirement}</span>&nbsp;
                                    <a href="<@ofbizUrl>EditRequirement?requirementId=${linkedRequirement.requirementId}</@ofbizUrl>"
                                       class="btn btn-mini btn-primary">${linkedRequirement.requirementId}</a>&nbsp;
                                </td>
                            </tr>
                        </#list>
                    </#if>
                    <#-- show linked quote -->
                    <#assign linkedQuote = orderItem.getRelatedOne("QuoteItem", true)?if_exists>
                    <#if linkedQuote?has_content>
                        <tr<#if itemClass == "1"> class="alternate-row"</#if>>
                            
                            <td colspan="6">
                                <span>${uiLabelMap.OrderLinkedToQuote}</span>&nbsp;
                                <a href="<@ofbizUrl>EditQuoteItem?quoteId=${linkedQuote.quoteId}&amp;quoteItemSeqId=${linkedQuote.quoteItemSeqId}</@ofbizUrl>"
                                   class="btn btn-mini btn-primary">${linkedQuote.quoteId}-${linkedQuote.quoteItemSeqId}</a>&nbsp;
                            </td>
                        </tr>
                    </#if>
                    <#-- now show adjustment details per line item -->
                    <#assign orderItemAdjustments = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemAdjustmentList(orderItem, orderAdjustments)>
                    <#if orderItemAdjustments?exists && orderItemAdjustments?has_content>
                        <#list orderItemAdjustments as orderItemAdjustment>
                            <#assign adjustmentType = orderItemAdjustment.getRelatedOne("OrderAdjustmentType", true)>
                            <tr<#if itemClass == "1"> class="alternate-row"</#if>>
                                <td align="right" class="align-right" colspan="2" style="padding-left:2px">
                                	<#--<span>${uiLabelMap.OrderAdjustment}</span>&nbsp;-->
                                    ${adjustmentType.get("description",locale)}
                                    ${orderItemAdjustment.get("description",locale)?if_exists}
                                    <#if orderItemAdjustment.comments?has_content>
                                        (${orderItemAdjustment.comments?default("")})
                                    </#if>
                                    <#if orderItemAdjustment.productPromoId?has_content>
                                        <a class="btn btn-mini btn-primary" href="<@ofbizUrl>viewProductPromo</@ofbizUrl>?productPromoId=${orderItemAdjustment.productPromoId}${externalKeyParam}" target="_blank">
                                        	${orderItemAdjustment.getRelatedOne("ProductPromo", false).getString("promoName")}</a>
                                    </#if>
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
                                <td colspan="3">&nbsp;</td>
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
                            <tr<#if itemClass == "1"> class="alternate-row"</#if>>
                                <td align="right" class="align-right" colspan="2">
                                    <span>${uiLabelMap.ProductPriceRuleNameId}</span>&nbsp;
                                    [${orderItemPriceInfo.productPriceRuleId?if_exists}:${orderItemPriceInfo.productPriceActionSeqId?if_exists}]
                                    ${orderItemPriceInfo.description?if_exists}
                                </td>
                                <td colspan="3">&nbsp;</td>
                                <td align="right" class="align-right">
                                    <@ofbizCurrency amount=orderItemPriceInfo.modifyAmount isoCode=currencyUomId/>
                                </td>
                            </tr>
                        </#list>
                    </#if>
                    <#-- now show survey information per line item -->
                    <#assign orderItemSurveyResponses = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemSurveyResponse(orderItem)>
                    <#if orderItemSurveyResponses?exists && orderItemSurveyResponses?has_content>
                        <#list orderItemSurveyResponses as survey>
                            <tr<#if itemClass == "1"> class="alternate-row"</#if>>
                                <td align="right" class="align-right" colspan="2">
                                    <span>${uiLabelMap.CommonSurveys}</span>&nbsp;
                                    <a href="/content/control/ViewSurveyResponses?surveyResponseId=${survey.surveyResponseId}&amp;surveyId=${survey.surveyId}${externalKeyParam}"
                                       class="btn btn-mini btn-primary">${survey.surveyId}</a>
                                </td>
                                <td colspan="5">&nbsp;</td>
                            </tr>
                        </#list>
                    </#if>
                    <#-- now show ship group info per line item -->
                    <#assign orderItemShipGroupAssocs = orderItem.getRelated("OrderItemShipGroupAssoc", null, null, false)?if_exists>
                    <#if orderItemShipGroupAssocs?has_content>
                        <#list orderItemShipGroupAssocs as shipGroupAssoc>
                            <#assign shipGroup = shipGroupAssoc.getRelatedOne("OrderItemShipGroup", false)>
                            <#assign shipGroupAddress = shipGroup.getRelatedOne("PostalAddress", false)?if_exists>
                            <tr<#if itemClass == "1"> class="alternate-row"</#if>>
                                <td align="right" class="align-right" colspan="2">
                                    <span>${uiLabelMap.DAShipGroup}</span>:&nbsp;<span class="green">${shipGroup.shipGroupSeqId}</span>. 
                                    ${uiLabelMap.DATo}: ${shipGroupAddress.address1?default("${uiLabelMap.OrderNotShipped}")}
                                </td>
                                <td align="center">
                                    ${shipGroupAssoc.quantity?string.number}&nbsp;
                                </td>
                                <td colspan="4">&nbsp;</td>
                            </tr>
                        </#list>
                    </#if>
                    <#-- now show inventory reservation info per line item -->
                    <#if orderItemShipGrpInvResList?exists && orderItemShipGrpInvResList?has_content>
                        <#list orderItemShipGrpInvResList as orderItemShipGrpInvRes>
                            <tr<#if itemClass == "1"> class="alternate-row"</#if>>
                                <td align="right" class="align-right" colspan="2">
                                    <span>${uiLabelMap.DAInventoryItemId}</span>:&nbsp;<span class="green">${orderItemShipGrpInvRes.inventoryItemId}</span>.
                                    <#--<a class="btn btn-mini btn-primary" href="/facility/control/EditInventoryItem?inventoryItemId=${orderItemShipGrpInvRes.inventoryItemId}${externalKeyParam}"
                                       class="buttontext">${orderItemShipGrpInvRes.inventoryItemId}</a>-->
                                    <span>${uiLabelMap.DAShipGroup}</span>:&nbsp;<span class="green">${orderItemShipGrpInvRes.shipGroupSeqId}</span>
                                </td>
                                <td align="center">
                                    ${orderItemShipGrpInvRes.quantity?string.number}&nbsp;
                                </td>
                                <td colspan="2">
                                    <#if (orderItemShipGrpInvRes.quantityNotAvailable?has_content && orderItemShipGrpInvRes.quantityNotAvailable > 0)>
                                        <span style="color: red;">
                                            [${orderItemShipGrpInvRes.quantityNotAvailable?string.number}&nbsp;${uiLabelMap.OrderBackOrdered}]
                                        </span>
                                        <#--<a href="<@ofbizUrl>balanceInventoryItems?inventoryItemId=${orderItemShipGrpInvRes.inventoryItemId}&amp;orderId=${orderId}&amp;priorityOrderId=${orderId}&amp;priorityOrderItemSeqId=${orderItemShipGrpInvRes.orderItemSeqId}</@ofbizUrl>" class="buttontext" style="font-size: xx-small;">Raise Priority</a> -->
                                    </#if>
                                    &nbsp;
                                </td>
                                <td colspan="2">&nbsp;</td>
                            </tr>
                        </#list>
                    </#if>
                    <#-- now show planned shipment info per line item -->
                    <#assign orderShipments = orderItem.getRelated("OrderShipment", null, null, false)?if_exists>
                    <#if orderShipments?has_content>
                        <#list orderShipments as orderShipment>
                            <tr<#if itemClass == "1"> class="alternate-row"</#if>>
                                <td align="right" class="align-right" colspan="2">
                                    <span>${uiLabelMap.OrderPlannedInShipment}</span>&nbsp;
                                	<a target="_blank" href="<@ofbizUrl>viewShipment</@ofbizUrl>?shipmentId=${orderShipment.shipmentId}${externalKeyParam}">${orderShipment.shipmentId}</a>: ${orderShipment.shipmentItemSeqId}
                                </td>
                                <td align="center">
                                    ${orderShipment.quantity?string.number}&nbsp;
                                </td>
                                <td colspan="4">&nbsp;</td>
                            </tr>
                        </#list>
                    </#if>
                    <#-- now show item issuances (shipment) per line item -->
                    <#assign itemIssuances = itemIssuancesPerItem.get(orderItem.get("orderItemSeqId"))?if_exists>
                    <#if itemIssuances?has_content>
                        <#list itemIssuances as itemIssuance>
                        <tr<#if itemClass == "1"> class="alternate-row"</#if>>
                            <td align="right" class="align-right" colspan="2">
                                <#if itemIssuance.shipmentId?has_content>
                                    <span>${uiLabelMap.OrderIssuedToShipmentItem}</span>&nbsp;
                                    <a target="_blank" href="<@ofbizUrl>viewShipment</@ofbizUrl>?shipmentId=${itemIssuance.shipmentId}${externalKeyParam}">${itemIssuance.shipmentId}</a>: ${itemIssuance.shipmentItemSeqId?if_exists}
                                <#else>
                                    <span>${uiLabelMap.OrderIssuedWithoutShipment}</span>
                                </#if>
                            </td>
                            <td align="center">
                                ${itemIssuance.quantity?default(0) - itemIssuance.cancelQuantity?default(0)}&nbsp;
                            </td>
                            <td colspan="4">&nbsp;</td>
                        </tr>
                        </#list>
                    </#if>
                    <#-- now show item issuances (inventory item) per line item -->
                    <#if itemIssuances?has_content>
                        <#list itemIssuances as itemIssuance>
                            <tr<#if itemClass == "1"> class="alternate-row"</#if>>
                                <td align="right" class="align-right" colspan="2">
                                    <#if itemIssuance.inventoryItemId?has_content>
                                        <#assign inventoryItem = itemIssuance.getRelatedOne("InventoryItem", false)/>
                                        <span>${uiLabelMap.DAInventory}</span>
                                        <span class="green">${itemIssuance.inventoryItemId}</span>
                                        <#--<a href="/facility/control/EditInventoryItem?inventoryItemId=${itemIssuance.inventoryItemId}${externalKeyParam}"
                                           class="btn btn-mini btn-primary">${itemIssuance.inventoryItemId}</a>-->
                                        <span>${uiLabelMap.DAShipGroup}</span>&nbsp;${itemIssuance.shipGroupSeqId?if_exists}
                                        <#if (inventoryItem.serialNumber?has_content)>
                                            <br />
                                            <span>${uiLabelMap.ProductSerialNumber}</span>&nbsp;${inventoryItem.serialNumber}&nbsp;
                                        </#if>
                                    </#if>
                                </td>
                                <td align="center">
                                    ${itemIssuance.quantity?default(0) - itemIssuance.cancelQuantity?default(0)}
                                </td>
                                <td colspan="4">&nbsp;</td>
                            </tr>
                        </#list>
                    </#if>
                    <#-- now show shipment receipts per line item -->
                    <#assign shipmentReceipts = orderItem.getRelated("ShipmentReceipt", null, null, false)?if_exists>
                    <#if shipmentReceipts?has_content>
                        <#list shipmentReceipts as shipmentReceipt>
                            <tr<#if itemClass == "1"> class="alternate-row"</#if>>
                                <td align="right" class="align-right" colspan="2">
                                    <#if shipmentReceipt.shipmentId?has_content>
                                        <span>${uiLabelMap.OrderShipmentReceived}</span>&nbsp;
                                        <a target="_blank" href="<@ofbizUrl>viewShipment</@ofbizUrl>?shipmentId=${shipmentReceipt.shipmentId}${externalKeyParam}"
                                           class="btn btn-mini btn-primary">${shipmentReceipt.shipmentId}</a>:${shipmentReceipt.shipmentItemSeqId?if_exists}
                                    </#if>
                                    &nbsp;<#if shipmentReceipt.datetimeReceived?has_content>${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(shipmentReceipt.datetimeReceived, "", locale, timeZone)!}</#if>&nbsp;
                                    <span>${uiLabelMap.CommonInventory}</span>&nbsp;
                                    <span class="green">${itemIssuance.inventoryItemId}</span>
                                    <#--<a href="/facility/control/EditInventoryItem?inventoryItemId=${shipmentReceipt.inventoryItemId}${externalKeyParam}"
                                       class="btn btn-mini btn-primary">${shipmentReceipt.inventoryItemId}</a>-->
                                </td>
                                <td align="center">
                                    ${shipmentReceipt.quantityAccepted?string.number}&nbsp;/&nbsp;${shipmentReceipt.quantityRejected?default(0)?string.number}
                                </td>
                                <td colspan="4">&nbsp;</td>
                            </tr>
                        </#list>
                    </#if>
                    <#if itemClass == "2">
                        <#assign itemClass = "1">
                    <#else>
                        <#assign itemClass = "2">
                    </#if>
                </#list>
            </#if>
            <#list orderHeaderAdjustments as orderHeaderAdjustment>
                <#assign adjustmentType = orderHeaderAdjustment.getRelatedOne("OrderAdjustmentType", false)>
                <#assign adjustmentAmount = Static["org.ofbiz.order.order.OrderReadHelper"].calcOrderAdjustment(orderHeaderAdjustment, orderSubTotal)>
                <#if adjustmentAmount != 0>
                    <tr>
                        <td align="right" class="align-right" colspan="5">
                            <#if orderHeaderAdjustment.comments?has_content>${orderHeaderAdjustment.comments} - </#if>
                            <#if orderHeaderAdjustment.description?has_content>${orderHeaderAdjustment.description} - </#if>
                            <span>${adjustmentType.get("description", locale)}</span>
                        </td>
                        <td align="right" class="align-right" nowrap="nowrap">
                            <@ofbizCurrency amount=adjustmentAmount isoCode=currencyUomId/>
                        </td>
                    </tr>
                </#if>
            </#list>
            <#-- subtotal -->
            <tr>
                <td colspan="6"></td>
            </tr>
            <tr>
                <td align="right" class="align-right" colspan="5">
                    <span><b>${uiLabelMap.OrderItemsSubTotal}</b></span>
                </td>
                <td align="right" class="align-right" nowrap="nowrap">
                    <@ofbizCurrency amount=orderSubTotal isoCode=currencyUomId/>
                </td>
            </tr>
            <#-- other adjustments -->
            <tr>
                <td align="right" class="align-right" colspan="5">
                    <span><b>${uiLabelMap.DATotalOrderAdjustments}</b></span>
                </td>
                <td align="right" class="align-right" nowrap="nowrap">
                    <@ofbizCurrency amount=otherAdjAmount isoCode=currencyUomId/>
                </td>
            </tr>
            <#-- shipping adjustments -->
            <#--
            <tr>
                <td align="right" class="align-right" colspan="5">
                    <span><b>${uiLabelMap.OrderTotalShippingAndHandling}</b></span>
                </td>
                <td align="right" class="align-right" nowrap="nowrap">
                    <@ofbizCurrency amount=shippingAmount isoCode=currencyUomId/>
                </td>
            </tr>
            -->
            <#-- tax adjustments -->
            <tr>
                <td align="right" class="align-right" colspan="5">
                    <span><b>${uiLabelMap.OrderTotalSalesTax}</b></span>
                </td>
                <td align="right" class="align-right" nowrap="nowrap">
                    <@ofbizCurrency amount=taxAmount isoCode=currencyUomId/>
                </td>
            </tr>
            <#-- grand total -->
            <tr>
                <td align="right" class="align-right" colspan="5">
                    <span><b>${uiLabelMap.OrderTotalDue}</b></span>
                </td>
                <td align="right" class="align-right" nowrap="nowrap">
                    <@ofbizCurrency amount=grandTotal isoCode=currencyUomId/>
                </td>
            </tr>
            </tbody>
        </table>
	</div><!--#items-tab-->
	<div class="clear-all"></div>
</#if>