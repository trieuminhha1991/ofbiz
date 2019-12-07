<style type="text/css">
	.contentTab {
		padding:10px
	}
</style>
<div class="tabDivInner">
	<ul style='margin-left: 30px;'>
		<li>${StringUtil.wrapString(uiLabelMap.DAQuantityDetail)}</li>
		<li class='titleTab2'>${StringUtil.wrapString(uiLabelMap.DAStatus)}</li>
		<#if isViewAtpQoh><li class='titleTab3'>${StringUtil.wrapString(uiLabelMap.DAFacility)}</li></#if>
		<li class='titleTab4'>${StringUtil.wrapString(uiLabelMap.DAShipping)}</li>
		<li class='titleTab5'>${StringUtil.wrapString(uiLabelMap.DAAdjustmentDetail)}</li>
	</ul>
	<div class='contentTab1 contentTab'>
		<#-- QUANTITY -->
		<#--
		<div class="row-fluid">
			<div class="span12">
				<span>${uiLabelMap.DAExpireDate}: </span>
        		<#if orderItem.expireDate?exists && orderItem.expireDate?has_content>${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(orderItem.expireDate, "dd/MM/yyyy", locale, timeZone)!}</#if>
			</div>
		</div>
		-->
		<div class="row-fluid">
			<div class="span6">
				<table width="100%" border="0" cellpadding="1" class="table table-striped table-hover table-bordered dataTable">
					<tr>
						<td colspan="4">
							${uiLabelMap.DACalculateByDerivedUnit}: 
							<#if orderItem.quantityUomId?exists>
								<#assign quantityUomOI = delegator.findOne("Uom", {"uomId" : orderItem.quantityUomId}, false)/>
								<#if quantityUomOI?exists>${quantityUomOI.get("description", locale)}</#if>
							</#if>
						</td>
					</tr>
					<tr valign="top">
						<td colspan="4">
							<span>${uiLabelMap.DAQuantityOrder}: ${orderItem.alternativeQuantity?if_exists}</span>
						</td>
					</tr>
				</table>
			</div>
			<div class="span6">
				<#if !product?exists>
            		<#assign product = orderItem.getRelatedOne("Product", true)>
            	</#if>
            	<#assign productId = orderItem.productId?if_exists>
				<table width="100%" border="0" cellpadding="1" class="table table-striped table-hover table-bordered dataTable">
					<tr>
						<td colspan="4">
							${uiLabelMap.DACalculateByBasicUnit}: 
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
                        <td><b>${uiLabelMap.OrderCancelled}</b></td>
                        <td>${orderItem.cancelQuantity?default(0)?string.number}</td>
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
                        <#assign returnQuantityMap = orderReadHelper.getOrderItemReturnedQuantities()/>
                        <td>${returnQuantityMap.get(orderItem.orderItemSeqId)?default(0)}</td>
                    </tr>
				</table>
			</div>
		</div>
	</div>
	<div class='contentTab2 contentTab'>
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
			</div>
		</div>
	</div><!--.contentTab2-->
	<#if isViewAtpQoh>
	<div class='contentTab3 contentTab'>
		<div class="row-fluid">
			<div class="span6">
				<#-- INVENTORY -->
                <#if (orderHeader.statusId != "ORDER_COMPLETED") && availableToPromiseMap?exists && quantityOnHandMap?exists && availableToPromiseMap.get(productId)?exists && quantityOnHandMap.get(productId)?exists>
                    <#assign quantityToProduce = 0>
                    <#assign atpQuantity = availableToPromiseMap.get(productId)?default(0)>
                    <#assign qohQuantity = quantityOnHandMap.get(productId)?default(0)>
                    <#assign mktgPkgATP = mktgPkgATPMap.get(productId)?default(0)>
                    <#assign mktgPkgQOH = mktgPkgQOHMap.get(productId)?default(0)>
                    <#assign requiredQuantity = requiredProductQuantityMap.get(productId)?default(0)>
                    <#assign onOrderQuantity = onOrderProductQuantityMap.get(productId)?default(0)>
                    <#assign inProductionQuantity = productionProductQuantityMap.get(productId)?default(0)>
                    <#assign unplannedQuantity = requiredQuantity - qohQuantity - inProductionQuantity - onOrderQuantity - mktgPkgQOH>
                    <#if unplannedQuantity < 0><#assign unplannedQuantity = 0></#if>
                        <table width="100%" border="0" cellpadding="1" class="table table-striped table-hover table-bordered dataTable">
                            <#--<tr>
                                <td style="text-align: left; padding-bottom: 10px;">
                                    <a class="btn btn-mini btn-primary"
                                       href="/catalog/control/EditProductInventoryItems?productId=${productId}&amp;showAllFacilities=Y${externalKeyParam}"
                                       target="_blank">${uiLabelMap.ProductInventory}</a>
                                </td>
                            </tr>-->
                            <tr>
                                <td style="padding:3px 8px;">${uiLabelMap.OrderRequiredForSO}</td>
                                <td style="padding:3px 8px; padding-left: 15px; text-align: left;">${requiredQuantity}</td>
                            </tr>
                            <#if availableToPromiseByFacilityMap?exists && quantityOnHandByFacilityMap?exists && quantityOnHandByFacilityMap.get(productId)?exists && availableToPromiseByFacilityMap.get(productId)?exists>
                                <#assign atpQuantityByFacility = availableToPromiseByFacilityMap.get(productId)?default(0)>
                                <#assign qohQuantityByFacility = quantityOnHandByFacilityMap.get(productId)?default(0)>
                                <tr>
                                    <td style="padding:3px 8px;">
                                        ${uiLabelMap.DAInventoryK} [${facility.facilityName?if_exists}]
                                    </td>
                                    <td style="padding:3px 8px; padding-left: 15px; text-align: left;">
                                        ${uiLabelMap.ProductQoh}: ${qohQuantityByFacility}<br />
                                        ${uiLabelMap.ProductAtp}: ${atpQuantityByFacility}
                                    </td>
                                </tr>
                            </#if>
                            <tr>
                                <td style="padding:3px 8px;">
                                    ${uiLabelMap.DAAllFacilities}
                                </td>
                                <td style="padding:3px 8px; padding-left: 15px; text-align: left;">
                                    ${uiLabelMap.ProductQoh}: ${qohQuantity}<br />
                                    ${uiLabelMap.ProductAtp}: ${atpQuantity}
                                </td>
                            </tr>
                            <#if (product?has_content) && (product.productTypeId?has_content) && Static["org.ofbiz.entity.util.EntityTypeUtil"].hasParentType(delegator, "ProductType", "productTypeId", product.productTypeId, "parentTypeId", "MARKETING_PKG")>
                                <tr>
                                    <td style="padding:3px 8px;">${uiLabelMap.ProductMarketingPackageQOH}</td>
                                    <td style="padding:3px 8px; padding-left: 15px; text-align: left;">
                                        ${mktgPkgQOH} (${uiLabelMap.ProductAtp}: ${mktgPkgATP})
                                    </td>
                                </tr>
                            </#if>
                            <tr>
                                <td style="padding:3px 8px;">${uiLabelMap.OrderOnOrder}</td>
                                <td style="padding:3px 8px; padding-left: 15px; text-align: left;">${onOrderQuantity}</td>
                            </tr>
                            <tr>
                                <td style="padding:3px 8px;">${uiLabelMap.OrderInProduction}</td>
                                <td style="padding:3px 8px; padding-left: 15px; text-align: left;">${inProductionQuantity}</td>
                            </tr>
                            <tr>
                                <td style="padding:3px 8px;">${uiLabelMap.OrderUnplanned}</td>
                                <td style="padding:3px 8px; padding-left: 15px; text-align: left;">${unplannedQuantity}</td>
                            </tr>
                        </table>
                <#else>
                	<span>${uiLabelMap.DAOrderCompletedStatus}</span>
                </#if>
			</div>
			<div class="span6">
			</div>
		</div>
	</div><!--.contentTab3-->
	</#if>
	<div class='contentTab4 contentTab'>
		<div class="row-fluid">
			<div class="span12">
				<table width="100%" border="0" cellpadding="1" class="table table-striped table-hover table-bordered dataTable">
					<#-- show info from workeffort -->
                    <#assign workOrderItemFulfillments = orderItem.getRelated("WorkOrderItemFulfillment", null, null, false)?if_exists>
                    <#if workOrderItemFulfillments?has_content>
                        <#list workOrderItemFulfillments as workOrderItemFulfillment>
                            <#assign workEffort = workOrderItemFulfillment.getRelatedOne("WorkEffort", true)>
                            <tr>
                                
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
                            <tr>
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
                            <tr>
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
                            <tr>
                                
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
                        <tr>
                            
                            <td colspan="6">
                                <span>${uiLabelMap.OrderLinkedToQuote}</span>&nbsp;
                                <a href="<@ofbizUrl>EditQuoteItem?quoteId=${linkedQuote.quoteId}&amp;quoteItemSeqId=${linkedQuote.quoteItemSeqId}</@ofbizUrl>"
                                   class="btn btn-mini btn-primary">${linkedQuote.quoteId}-${linkedQuote.quoteItemSeqId}</a>&nbsp;
                            </td>
                        </tr>
                    </#if>
                    
                    <#-- now show survey information per line item -->
                    <#assign orderItemSurveyResponses = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemSurveyResponse(orderItem)>
                    <#if orderItemSurveyResponses?exists && orderItemSurveyResponses?has_content>
                        <#list orderItemSurveyResponses as survey>
                            <tr>
                                <td align="right" class="align-right" colspan="2">
                                    <span>${uiLabelMap.CommonSurveys}</span>&nbsp;
                                    <a href="/content/control/ViewSurveyResponses?surveyResponseId=${survey.surveyResponseId}&amp;surveyId=${survey.surveyId}${externalKeyParam}"
                                       class="btn btn-mini btn-primary">${survey.surveyId}</a>
                                </td>
                                <td colspan="5">&nbsp;</td>
                            </tr>
                        </#list>
                    </#if>
                    <#-- display the ship estimated/before/after dates -->
                    <#if orderItem.estimatedShipDate?exists>
                        <tr>
                            <td align="right" class="align-right" colspan="2">
                                <span>${uiLabelMap.OrderEstimatedShipDate}</span>:&nbsp;${Static["org.ofbiz.base.util.UtilFormatOut"].formatDate(orderItem.estimatedShipDate, "", locale, timeZone)!}
                            </td>
                            <td colspan="5">&nbsp;</td>
                        </tr>
                    </#if>
                    <#if orderItem.estimatedDeliveryDate?exists>
                        <tr>
                            <td align="right" class="align-right" colspan="2">
                                <span>${uiLabelMap.OrderOrderQuoteEstimatedDeliveryDate}</span>:&nbsp;${Static["org.ofbiz.base.util.UtilFormatOut"].formatDate(orderItem.estimatedDeliveryDate, "", locale, timeZone)!}
                            </td>
                            <td colspan="5">&nbsp;</td>
                        </tr>
                    </#if>
                    <#if orderItem.shipAfterDate?exists>
                        <tr>
                            <td align="right" class="align-right" colspan="2">
                                <span>${uiLabelMap.OrderShipAfterDate}</span>:&nbsp;${Static["org.ofbiz.base.util.UtilFormatOut"].formatDate(orderItem.shipAfterDate, "", locale, timeZone)!}
                            </td>
                            <td colspan="5">&nbsp;</td>
                        </tr>
                    </#if>
                    <#if orderItem.shipBeforeDate?exists>
                        <tr>
                            <td align="right" class="align-right" colspan="2">
                                <span>${uiLabelMap.OrderShipBeforeDate}</span>:&nbsp;${Static["org.ofbiz.base.util.UtilFormatOut"].formatDate(orderItem.shipBeforeDate, "", locale, timeZone)!}
                            </td>
                            <td colspan="5">&nbsp;</td>
                        </tr>
                    </#if>
                    <#-- now show ship group info per line item -->
                    <#assign orderItemShipGroupAssocs = orderItem.getRelated("OrderItemShipGroupAssoc", null, null, false)?if_exists>
                    <#if orderItemShipGroupAssocs?has_content>
                        <#list orderItemShipGroupAssocs as shipGroupAssoc>
                            <#assign shipGroup = shipGroupAssoc.getRelatedOne("OrderItemShipGroup", false)>
                            <#assign shipGroupAddress = shipGroup.getRelatedOne("PostalAddress", false)?if_exists>
                            <tr>
                                <td align="right" class="align-right" colspan="2">
                                    <span>${uiLabelMap.DAShipGroup}</span>:&nbsp;<span class="green">${shipGroup.shipGroupSeqId}</span>
                                    ${shipGroupAddress.address1?default("${uiLabelMap.OrderNotShipped}")}
                                </td>
                                <td align="center">
                                    ${shipGroupAssoc.quantity?if_exists?string.number}&nbsp;
                                </td>
                                <td colspan="4">&nbsp;</td>
                            </tr>
                        </#list>
                    </#if>
                    <#-- now show inventory reservation info per line item -->
                    <#if orderItemShipGrpInvResList?exists && orderItemShipGrpInvResList?has_content>
                        <#list orderItemShipGrpInvResList as orderItemShipGrpInvRes>
                            <tr>
                                <td align="right" class="align-right" colspan="2">
                                    <span>${uiLabelMap.DAInventory}</span>:&nbsp;<span class="green">${orderItemShipGrpInvRes.inventoryItemId}</span>.
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
                            <tr>
                                <td align="right" class="align-right" colspan="2">
                                    <span>${uiLabelMap.OrderPlannedInShipment}</span>&nbsp;
                                	<a target="_blank" href="<@ofbizUrl>viewShipment</@ofbizUrl>?shipmentId=${orderShipment.shipmentId}${externalKeyParam}">${orderShipment.shipmentId}</a>: ${orderShipment.shipmentItemSeqId}
                                </td>
                                <td align="center">
                                    ${orderShipment.quantity?if_exists?string.number}&nbsp;
                                </td>
                                <td colspan="4">&nbsp;</td>
                            </tr>
                        </#list>
                    </#if>
                    <#-- now show item issuances (shipment) per line item -->
                    <#assign itemIssuances = itemIssuancesPerItem.get(orderItem.get("orderItemSeqId"))?if_exists>
                    <#if itemIssuances?has_content>
                        <#list itemIssuances as itemIssuance>
                        <tr>
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
                            <tr>
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
                            <tr>
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
				</table>
			</div>
		</div><!--.row-fluid-->
	</div><!--.contentTab4-->
	<div class='contentTab5 contentTab'>
		<div class="row-fluid">
			<div class="span12">
				<table width="100%" border="0" cellpadding="1" class="table table-striped table-hover table-bordered dataTable">
					 <#--TODOMOVE-->
                    <#-- now show adjustment details per line item -->
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
	</div><!--.contentTab5-->
</div>
<script type="text/javascript">
	$(function(){
		$(".tabDivInner").jqxTabs({ theme: 'energyblue', width: '100%', height: 280});
	});
</script>