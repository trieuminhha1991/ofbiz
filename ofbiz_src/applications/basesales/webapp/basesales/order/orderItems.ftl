<#--
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
-->
<#if orderHeader?has_content>
<div class="tab-pane<#if activeTab?exists && activeTab == "items-tab"> active</#if>" id="items-tab">
        <h4 class="smaller green">${uiLabelMap.ListProduct}</h4>
        <div class="widget-body">
        <div class="widget-body-inner">
    	<div class="widget-main" style="padding: 0px !important">
            <table class="table table-striped table-bordered table-hover dataTable" cellspacing='0' style="width: 100%">
                <tr valign="bottom" class="header-row">
                    <td width="15%">${uiLabelMap.ProductProduct}</td>
                    <td width="40%">${uiLabelMap.CommonStatus}</td>
                    <td width="5%">${uiLabelMap.OrderQuantity}</td>
                    <td width="15%" align="right">${uiLabelMap.OrderUnitList}</td>
                    <td width="15%" align="right">${uiLabelMap.OrderAdjustments}</td>
                    <td width="15%" align="right">${uiLabelMap.OrderSubTotal}</td>
                </tr>
                <#if !orderItemList?has_content>
                    <tr>
                        <td colspan="7">
                            <font color="red">${uiLabelMap.checkhelper_sales_order_lines_lookup_failed}</font>
                        </td>
                        <td>
                        </td>
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
                                <td colspan="7" valign="top" > &gt;&gt; ${orderItem.itemDescription}</td>
                            <#else>
                                <td colspan="7">
                                    <div class="order-item-description" style="color: green !important;">
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
                                     <#--   <a href="/catalog/control/EditProduct?productId=${productId}${externalKeyParam}" class="open-sans icon-book" target="_blank">${uiLabelMap.ProductCatalog}</a>
                                        <a href="/ecommerce/control/product?product_id=${productId}" class="open-sans" target="_blank">${uiLabelMap.OrderEcommerce}</a> -->
                                        <#if orderItemContentWrapper.get("IMAGE_URL")?has_content>
                                            <a href="<@ofbizUrl>viewimage?orderId=${orderId}&amp;orderItemSeqId=${orderItem.orderItemSeqId}&amp;orderContentTypeId=IMAGE_URL</@ofbizUrl>"
                                               target="_orderImage" class="btn btn-mini btn-primary">${uiLabelMap.OrderViewImage}</a>
                                        </#if>
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
                                            <div class="screenlet order-item-inventory">
                                                <div class="screenlet-body">
                                                    <table cellspacing="0" cellpadding="0" border="0">
                                                        <tr>
                                                            <td style="text-align: left; padding-bottom: 10px;">
                                                                <a class="open-sans"
                                                                   href="/catalog/control/EditProductInventoryItems?productId=${productId}&amp;showAllFacilities=Y${externalKeyParam}"
                                                                   target="_blank">${uiLabelMap.ProductInventory}</a>
                                                            </td>
                                                            
                                                        </tr>
                                                        <tr>
                                                            <td>${uiLabelMap.OrderRequiredForSO}</td>
                                                            <td style="padding-left: 15px; text-align: left;">${requiredQuantity}</td>
                                                        </tr>
                                                        <#if availableToPromiseByFacilityMap?exists && quantityOnHandByFacilityMap?exists && quantityOnHandByFacilityMap.get(productId)?exists && availableToPromiseByFacilityMap.get(productId)?exists>
                                                            <#assign atpQuantityByFacility = availableToPromiseByFacilityMap.get(productId)?default(0)>
                                                            <#assign qohQuantityByFacility = quantityOnHandByFacilityMap.get(productId)?default(0)>
                                                            <tr>
                                                                <td>
                                                                    ${uiLabelMap.ProductInInventory} [${facility.facilityName?if_exists}] ${uiLabelMap.ProductQoh}
                                                                </td>
                                                                <td style="padding-left: 15px; text-align: left;">
                                                                    ${qohQuantityByFacility} (${uiLabelMap.ProductAtp}: ${atpQuantityByFacility})
                                                                </td>
                                                            </tr>
                                                        </#if>
                                                        <tr>
                                                            <td>
                                                                ${uiLabelMap.ProductInInventory} [${uiLabelMap.CommonAll} ${uiLabelMap.ProductFacilities}] ${uiLabelMap.ProductQoh}
                                                            </td>
                                                            <td style="padding-left: 15px; text-align: left;">
                                                                ${qohQuantity} (${uiLabelMap.ProductAtp}: ${atpQuantity})
                                                            </td>
                                                        </tr>
                                                        <#if (product?has_content) && (product.productTypeId?has_content) && Static["org.ofbiz.entity.util.EntityTypeUtil"].hasParentType(delegator, "ProductType", "productTypeId", product.productTypeId, "parentTypeId", "MARKETING_PKG")>
                                                            <tr>
                                                                <td>${uiLabelMap.ProductMarketingPackageQOH}</td>
                                                                <td style="padding-left: 15px; text-align: left;">
                                                                    ${mktgPkgQOH} (${uiLabelMap.ProductAtp}: ${mktgPkgATP})
                                                                </td>
                                                            </tr>
                                                        </#if>
                                                        <tr>
                                                            <td>${uiLabelMap.OrderOnOrder}</td>
                                                            <td style="padding-left: 15px; text-align: left;">${onOrderQuantity}</td>
                                                        </tr>
                                                        <tr>
                                                            <td>${uiLabelMap.OrderInProduction}</td>
                                                            <td style="padding-left: 15px; text-align: left;">${inProductionQuantity}</td>
                                                        </tr>
                                                        <tr>
                                                            <td>${uiLabelMap.OrderUnplanned}</td>
                                                            <td style="padding-left: 15px; text-align: left;">${unplannedQuantity}</td>
                                                        </tr>
                                                    </table>
                                                </div>
                                            </div>
                                        </#if>
                                    </#if>
                                </td>
                                <#-- now show status details per line item -->
                                <#assign currentItemStatus = orderItem.getRelatedOne("StatusItem", false)>
                                <td colspan="1" valign="top">
                                    <div class="screenlet order-item-status-list<#if currentItemStatus.statusCode?has_content> ${currentItemStatus.statusCode}</#if>">
                                        <div class="screenlet-body">
                                            <div class="current-status">
                                                <span >${uiLabelMap.OrderCurrentStatus}:</span>&nbsp;<span style="color: green">${currentItemStatus.get("description",locale)?default(currentItemStatus.statusId)}</span>
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
                                                <#if orderItemStatus.statusDatetime?has_content>${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(orderItemStatus.statusDatetime, "", locale, timeZone)!}&nbsp;&nbsp;</#if>${loopStatusItem.get("description",locale)?default(orderItemStatus.statusId)}
                                            </#list>
                                        </div>
                                    </div>
                                    <#assign returns = orderItem.getRelated("ReturnItem", null, null, false)?if_exists>
                                    <#assign listIds = []>
                                    <#if returns?has_content>
                                        <#list returns as returnItem>
                                        	<#assign idNew = returnItem.returnId>
                                        	<#assign check = true>
                                        	<#list listIds as idTmp>
                                        		<#if idNew == idTmp>
                                        			<#assign check = false>
                                        			<#break>
                                        		<#else>
                                        			<#assign check = true>
                                        		</#if>
                                        	</#list>
                                        	<#if check == true>
                                        		<#assign listIds = listIds + [idNew]>
                                        	</#if>
                                        </#list>
                                        <#list listIds as returnId>
	                                        <#assign returnHeader = delegator.findOne("ReturnHeader", Static["org.ofbiz.base.util.UtilMisc"].toMap("returnId", returnId), true)!>
	                                        <#if returnHeader.statusId != "RETURN_CANCELLED">
	                                            <font color="red">${uiLabelMap.ReturnReceived}:</font>
	                                            <#--<#if hasOlbPermission("MODULE", "LOG_CUS_RETURN_VIEW", "VIEW")>-->
	                                            	<a href="viewReturnOrder?returnId=${returnId}">${returnId}</a>
	                                            <#--<#else>
	                                            	${returnId}
	                                            </#if> -->
												</br>
	                                        </#if>
                                        </#list>
                                    </#if>
                                </td>
                                <#-- QUANTITY -->
                                <td align="right" valign="top" nowrap="nowrap">
                                    <div class="screenlet order-item-quantity">
                                        <div class="screenlet-body">
                                            <table>
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
                                                            <#if shipmentReceipt.quantityQualityAssurance?? && shipmentReceipt.quantityQualityAssurance?has_content>
                                                                <#assign  quantityQualityAssurance = shipmentReceipt.quantityQualityAssurance>
                                                                <#assign totalReceived = quantityQualityAssurance + totalReceived>
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
                                        </div>
                                    </div>
                                </td>
                                <td align="right" valign="top" nowrap="nowrap">
                                    <@ofbizCurrency amount=orderItem.unitPrice isoCode=currencyUomId/>
                                    / <@ofbizCurrency amount=orderItem.unitListPrice isoCode=currencyUomId/>
                                </td>
                                <td align="right" valign="top" nowrap="nowrap">
                                    <@ofbizCurrency amount=Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemAdjustmentsTotal(orderItem, orderAdjustments, true, false, false) isoCode=currencyUomId/>
                                </td>
                                <td align="right" valign="top" nowrap="nowrap">
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
                                            <span >${uiLabelMap.ManufacturingProductionRun}</span>
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
                                        <span >${uiLabelMap.OrderLinkedToOrderItem}</span>&nbsp;(${description?if_exists})
                                        <a href="/ordermgr/control/orderview?orderId=${linkedOrderId}"
                                           class="btn btn-mini btn-primary">${linkedOrderId}/${linkedOrderItemSeqId}</a>&nbsp;${linkedOrderItemValueStatus.description?if_exists}
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
                                        <span >${uiLabelMap.OrderLinkedFromOrderItem}</span>&nbsp;(${description?if_exists})
                                        <a href="/ordermgr/control/orderview?orderId=${linkedOrderId}"
                                           class="btn btn-mini btn-primary">${linkedOrderId}/${linkedOrderItemSeqId}</a>&nbsp;${linkedOrderItemValueStatus.description?if_exists}
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
                                        <span >${uiLabelMap.OrderLinkedToRequirement}</span>&nbsp;
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
                                    <span >${uiLabelMap.OrderLinkedToQuote}</span>&nbsp;
                                    <a href="<@ofbizUrl>EditQuoteItem?quoteId=${linkedQuote.quoteId}&amp;quoteItemSeqId=${linkedQuote.quoteItemSeqId}</@ofbizUrl>"
                                       class="btn btn-mini btn-primary">${linkedQuote.quoteId}-${linkedQuote.quoteItemSeqId}</a>&nbsp;
                                </td>
                            </tr>
                        </#if>
                        <#-- now show adjustment details per line item -->
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
                            <td align="right" colspan="5">
                                <#if orderHeaderAdjustment.comments?has_content>${orderHeaderAdjustment.comments} - </#if>
                                <#if orderHeaderAdjustment.description?has_content>${orderHeaderAdjustment.description} - </#if>
                                <span >${adjustmentType.get("description", locale)}</span>
                            </td>
                            <td align="right" nowrap="nowrap">
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
                    <td align="right" colspan="5">
                        <span >${uiLabelMap.OrderItemsSubTotal}</span>
                    </td>
                    <td align="right" nowrap="nowrap">
                        <@ofbizCurrency amount=orderSubTotal isoCode=currencyUomId/>
                    </td>
                    
                </tr>
            </table>
        </div>
        </div>
    </div>
</div>
</#if>