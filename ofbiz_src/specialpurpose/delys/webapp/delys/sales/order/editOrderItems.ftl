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
	<#-- price change rules -->
	<#assign allowPriceChange = false/>
	<#if (orderHeader.orderTypeId == 'PURCHASE_ORDER' || security.hasEntityPermission("ORDERMGR", "_SALES_PRICEMOD", session))>
	    <#assign allowPriceChange = true/>
	</#if>
	<div class="widget-header widget-header-blue widget-header-flat">
		<#if orderHeader?exists && orderHeader.orderId?exists>
			<h4 class="lighter">${uiLabelMap.DAEditOrderItems}:
				(<a href="<@ofbizUrl>orderView?orderId=${orderHeader.orderId}</@ofbizUrl>">${orderHeader.orderId}</a>)
			</h4>
			<#if security.hasEntityPermission("ORDERMGR", "_UPDATE", session)>
              	<#if orderHeader?has_content && orderHeader.statusId != "ORDER_CANCELLED" && orderHeader.statusId != "ORDER_COMPLETED">
					<span class="widget-toolbar none-content">
						<a href="javascript:document.updateItemInfo.action='<@ofbizUrl>cancelSelectedOrderItems</@ofbizUrl>';document.updateItemInfo.submit()">
							<i class="icon-remove open-sans">${uiLabelMap.OrderCancelSelectedItems}</i>
						</a>
						<a href="javascript:document.updateItemInfo.action='<@ofbizUrl>cancelOrderItem</@ofbizUrl>';document.updateItemInfo.submit()">
							<i class="icon-remove open-sans">${uiLabelMap.OrderCancelAllItems}</i>
						</a>
						<a href="<@ofbizUrl>orderView?${paramString}</@ofbizUrl>">
							<i class="icon-reply open-sans">${uiLabelMap.CommonBack}</i>
						</a>
					</span>
					<#--<ul><li class="h3">&nbsp;${uiLabelMap.OrderOrderItems}</li></ul>-->
				</#if>
          	</#if>
		<#else>
			<h4 class="lighter">${uiLabelMap.DAEditOrderItems}</h4>
		</#if>
	</div>
	<div class="widget-body">	 
		<div class="widget-main">
			<div class="screenlet">
			    <div class="screenlet-body">
			        <#if !orderItemList?has_content>
			            <span class="alert">${uiLabelMap.checkhelper_sales_order_lines_lookup_failed}</span>
			        <#else>
			            <form name="updateItemInfo" class=""form-horizontal basic-custom-form" method="post" action="<@ofbizUrl>updateOrderItems</@ofbizUrl>">
				            <input type="hidden" name="orderId" value="${orderId}"/>
				            <input type="hidden" name="orderItemSeqId" value=""/>
				            <input type="hidden" name="shipGroupSeqId" value=""/>
			            	<#if (orderHeader.orderTypeId == 'PURCHASE_ORDER')>
				              	<input type="hidden" name="supplierPartyId" value="${partyId}"/>
				              	<input type="hidden" name="orderTypeId" value="PURCHASE_ORDER"/>
			            	</#if>
			            	<h5 class="smaller lighter green">${uiLabelMap.DAListOrderItems}</h5>
			            	<table class="table table-bordered no-bottom-border no-bottom-margin" cellspacing="0">
				                <thead>
				                	<tr class="header-row row-header-hover">
					                    <th style="border-bottom:none;" colspan="2">${uiLabelMap.ProductProduct}</th>
					                    <th style="border-bottom:none;">${uiLabelMap.CommonStatus}</th>
					                    <th style="border-bottom:none;" class="align-text">${uiLabelMap.OrderQuantity}</th>
					                    <th style="border-bottom:none;" class="align-text">${uiLabelMap.OrderUnitPrice}</th>
					                    <th style="border-bottom:none;" nowrap class="align-text">${uiLabelMap.OrderAdjustments}</th>
					                    <th style="border-bottom:none;" nowrap class="align-text" width="12%">${uiLabelMap.OrderSubTotal}</th>
					                    <th colspan="2" style="border-bottom:none;">&nbsp;</th>
					                </tr>
				                </thead>
				                <tbody>
				                <#assign rowColor = true/>
				                <#list orderItemList as orderItem>
				                	<#assign rowColor = !rowColor/>
				                    <#if orderItem.productId?exists> <#-- a null product may come from a quote -->
				                      	<#assign orderItemContentWrapper = Static["org.ofbiz.order.order.OrderContentWrapper"].makeOrderContentWrapper(orderItem, request)>
				                      	<#--row 1-->
				                      	<tr>
				                          	<#assign orderItemType = orderItem.getRelatedOne("OrderItemType", false)?if_exists>
				                          	<#assign productId = orderItem.productId?if_exists>
				                          	<#if productId?exists && productId == "shoppingcart.CommentLine">
				                              	<td colspan="9" valign="top" class="<#if rowColor>row-color-f9<#else>row-color-ff</#if> border-top-2px">
				                                  	<span class="label">&gt;&gt; ${orderItem.itemDescription}</span>
				                              	</td>
				                          	<#else>
				                              	<td colspan="2" valign="top" class="<#if rowColor>row-color-f9<#else>row-color-ff</#if> border-top-2px">
				                                  	<div>
				                                      	<#if orderHeader.statusId = "ORDER_CANCELLED" || orderHeader.statusId = "ORDER_COMPLETED">
					                                      	<#if productId?exists>
					                                      		${orderItem.productId?default("N/A")} - ${orderItem.itemDescription?if_exists}
			                                      			<#elseif orderItemType?exists>
				                                      			${orderItemType.description} - ${orderItem.itemDescription?if_exists}
				                                     	 	<#else>
				                                      			${orderItem.itemDescription?if_exists}
				                                      		</#if>
				                                      	<#else>
				                                      		<#if productId?exists>
				                                      			<#assign orderItemName = orderItem.productId?default("N/A")/>
				                                      		<#elseif orderItemType?exists>
				                                      			<#assign orderItemName = orderItemType.description/>
				                                      		</#if>
				                                      		<p>${uiLabelMap.ProductProduct}&nbsp;<a href="<@ofbizUrl>editProduct?productId=${productId}</@ofbizUrl>" class="buttontext" target="_blank">${orderItemName}</a></p>
				                                      		<#if productId?exists>
				                                          		<#assign product = orderItem.getRelatedOne("Product", true)>
				                                          		<#if product.salesDiscontinuationDate?exists && Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp().after(product.salesDiscontinuationDate)>
				                                              		<span class="alert">${uiLabelMap.OrderItemDiscontinued}: ${product.salesDiscontinuationDate}</span>
				                                          		</#if>
				                                      		</#if>
				                                      		${uiLabelMap.CommonDescription}<br />
				                                      		<input type="text" size="20" name="idm_${orderItem.orderItemSeqId}" value="${orderItem.itemDescription?if_exists}"/>
				                                      	</#if>
				                                  	</div>
				                                  	<#--<#if productId?exists>
				                                  		<div>
				                                      		<a href="<@ofbizUrl>editProduct?productId=${productId}</@ofbizUrl>" class="buttontext" target="_blank">${uiLabelMap.ProductProduct}</a>
				                                      		<a href="/ecommerce/control/product?product_id=${productId}" class="buttontext" target="_blank">${uiLabelMap.OrderEcommerce}</a>
				                                      		<#if orderItemContentWrapper.get("IMAGE_URL")?has_content>
				                                      			<a href="<@ofbizUrl>viewimage?orderId=${orderId}&amp;orderItemSeqId=${orderItem.orderItemSeqId}&amp;orderContentTypeId=IMAGE_URL</@ofbizUrl>" target="_orderImage" class="buttontext">${uiLabelMap.OrderViewImage}</a>
				                                      		</#if>
				                                  		</div>
				                                  	</#if>-->
				                              	</td>
				                              	
				                              	<#-- now show status details per line item -->
				                              	<#assign currentItemStatus = orderItem.getRelatedOne("StatusItem", false)>
				                              	<td nowrap class="<#if rowColor>row-color-f9<#else>row-color-ff</#if> border-top-2px">
				                                  	${uiLabelMap.CommonCurrent}: ${currentItemStatus.get("description",locale)?default(currentItemStatus.statusId)}<br />
				                                  	<#assign orderItemStatuses = orderReadHelper.getOrderItemStatuses(orderItem)>
				                                  	<#list orderItemStatuses as orderItemStatus>
				                                  		<#assign loopStatusItem = orderItemStatus.getRelatedOne("StatusItem", false)>
				                                  		<#if orderItemStatus.statusDatetime?has_content>
				                                  			${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(orderItemStatus.statusDatetime, "HH:mm:ss - dd/MM/yyyy", locale, timeZone)!}:
			                                  			</#if>
				                                  		&nbsp;${loopStatusItem.get("description",locale)?default(orderItemStatus.statusId)}<br />
				                                  	</#list>
				                                  	<#assign returns = orderItem.getRelated("ReturnItem", null, null, false)?if_exists>
				                                  	<#if returns?has_content>
					                                  	<#list returns as returnItem>
					                                  		<#assign returnHeader = returnItem.getRelatedOne("ReturnHeader", false)>
					                                  		<#if returnHeader.statusId != "RETURN_CANCELLED">
							                                  	<div class="alert">
							                                      	<span class="label">${uiLabelMap.OrderReturned}</span> ${uiLabelMap.CommonNbr}
							                                      	<a href="<@ofbizUrl>returnMain?returnId=${returnItem.returnId}</@ofbizUrl>" class="buttontext">${returnItem.returnId}</a>
							                                  	</div>
					                                  		</#if>
					                                  	</#list>
			                                  		</#if>
				                              	</td>
				                              	<td class="align-text <#if rowColor>row-color-f9<#else>row-color-ff</#if> border-top-2px" valign="top" nowrap="nowrap">
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
			                                  		${uiLabelMap.OrderOrdered}&nbsp;${orderItem.quantity?default(0)?string.number}&nbsp;&nbsp;<br />
			                                  		${uiLabelMap.OrderCancelled}:&nbsp;${orderItem.cancelQuantity?default(0)?string.number}&nbsp;&nbsp;<br />
			                                  		${uiLabelMap.OrderRemaining}:&nbsp;${remainingQuantity}&nbsp;&nbsp;<br />
			                              		</td>
				                              	<td class="align-text <#if rowColor>row-color-f9<#else>row-color-ff</#if> border-top-2px align-right" valign="top" nowrap="nowrap">
				                                  	<#-- check for permission to modify price -->
				                                  	<#if (allowPriceChange)>
				                                      	<input type="text" size="8" name="ipm_${orderItem.orderItemSeqId}" value="<@ofbizAmount amount=orderItem.unitPrice/>" class="width-cell-100px"/>
				                                      	&nbsp;<input type="checkbox" name="opm_${orderItem.orderItemSeqId}" value="Y"/>
				                                  	<#else>
				                                      	<div><@ofbizCurrency amount=orderItem.unitPrice isoCode=currencyUomId/> / <@ofbizCurrency amount=orderItem.unitListPrice isoCode=currencyUomId/></div>
				                                  	</#if>
				                              	</td>
				                              	<td class="align-text <#if rowColor>row-color-f9<#else>row-color-ff</#if> border-top-2px align-right" valign="top" nowrap="nowrap">
				                                  	<@ofbizCurrency amount=Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemAdjustmentsTotal(orderItem, orderAdjustments, true, false, false) isoCode=currencyUomId/>
				                              	</td>
				                              	<td class="align-text <#if rowColor>row-color-f9<#else>row-color-ff</#if> border-top-2px align-right" valign="top" nowrap="nowrap">
				                                  	<#if orderItem.statusId != "ITEM_CANCELLED">
				                                  		<@ofbizCurrency amount=Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemSubTotal(orderItem, orderAdjustments) isoCode=currencyUomId/>
				                                  	<#else>
				                                  		<@ofbizCurrency amount=0.00 isoCode=currencyUomId/>
				                                  	</#if>
				                              	</td>
				                              	<td colspan="2" class="<#if rowColor>row-color-f9<#else>row-color-ff</#if> border-top-2px">&nbsp;</td>
				                          	</#if>
				                      	</tr>
										<#--end row 1-->
										
										<#assign printBorderTop = false/>
				                      	<#-- now show adjustment details per line item -->
				                      	<#assign orderItemAdjustments = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemAdjustmentList(orderItem, orderAdjustments)>
				                      	<#if orderItemAdjustments?exists && orderItemAdjustments?has_content>
				                          	<#list orderItemAdjustments as orderItemAdjustment>
				                              	<#assign adjustmentType = orderItemAdjustment.getRelatedOne("OrderAdjustmentType", true)>
				                              	<tr>
				                              		<td class="<#if rowColor>row-color-f9<#else>row-color-ff</#if> width20">&nbsp;</td>
				                                  	<td class="align-text <#if rowColor>row-color-f9<#else>row-color-ff</#if>" colspan="2">
				                                      	${uiLabelMap.OrderAdjustment}:&nbsp;
				                                      	${adjustmentType.get("description",locale)}&nbsp;
				                                      	${orderItemAdjustment.get("description",locale)?if_exists} (${orderItemAdjustment.comments?default("")}).
				                                      	<#if orderItemAdjustment.orderAdjustmentTypeId == "SALES_TAX">
				                                      		<#if orderItemAdjustment.primaryGeoId?has_content>
				                                      			<#assign primaryGeo = orderItemAdjustment.getRelatedOne("PrimaryGeo", true)/>
			                                      				${uiLabelMap.OrderJurisdiction}:&nbsp;${primaryGeo.geoName} [${primaryGeo.abbreviation?if_exists}]
				                                      			<#if orderItemAdjustment.secondaryGeoId?has_content>
				                                      				<#assign secondaryGeo = orderItemAdjustment.getRelatedOne("SecondaryGeo", true)/>
				                                      				(${uiLabelMap.CommonIn}:&nbsp;${secondaryGeo.geoName} [${secondaryGeo.abbreviation?if_exists}])
				                                      			</#if>.
				                                      		</#if>
				                                      		<#if orderItemAdjustment.sourcePercentage?exists>
				                                      			${uiLabelMap.DARate}:&nbsp;${orderItemAdjustment.sourcePercentage}.
			                                      			</#if>
				                                      		<#if orderItemAdjustment.customerReferenceId?has_content>
				                                      			${uiLabelMap.DACustomerTaxId}:&nbsp;${orderItemAdjustment.customerReferenceId}.
			                                      			</#if>
				                                      		<#if orderItemAdjustment.exemptAmount?exists>
				                                      			${uiLabelMap.DAExemptAmount}:&nbsp;${orderItemAdjustment.exemptAmount}.
			                                      			</#if>
				                                      	</#if>
				                                  	</td>
				                                  	<td class="<#if rowColor>row-color-f9<#else>row-color-ff</#if>">&nbsp;</td>
				                                  	<td class="<#if rowColor>row-color-f9<#else>row-color-ff</#if>">&nbsp;</td>
				                                  	<td class="align-text <#if rowColor>row-color-f9<#else>row-color-ff</#if> align-right">
				                                      	<@ofbizCurrency amount=Static["org.ofbiz.order.order.OrderReadHelper"].calcItemAdjustment(orderItemAdjustment, orderItem) isoCode=currencyUomId/>
				                                  	</td>
				                                  	<td colspan="1" class="<#if rowColor>row-color-f9<#else>row-color-ff</#if>">&nbsp;</td>
				                                  	<td colspan="2" class="<#if rowColor>row-color-f9<#else>row-color-ff</#if>">&nbsp;</td>
				                              	</tr>
				                              	<#assign printBorderTop = true/>
				                          	</#list>
				                      	</#if>
				
				                      	<#-- now show ship group info per line item -->
				                      	<#assign orderItemShipGroupAssocs = orderItem.getRelated("OrderItemShipGroupAssoc", null, null, false)?if_exists>
				                      	<#if orderItemShipGroupAssocs?has_content>
				                          	<#list orderItemShipGroupAssocs as shipGroupAssoc>
			                             	 	<#assign shipGroup = shipGroupAssoc.getRelatedOne("OrderItemShipGroup", false)>
				                              	<#assign shipGroupAddress = shipGroup.getRelatedOne("PostalAddress", false)?if_exists>
				                              	<tr>
				                              		<td class="<#if rowColor>row-color-f9<#else>row-color-ff</#if> width20 <#if printBorderTop>no-border-top</#if>">&nbsp;</td>
				                                  	<td class="align-text <#if rowColor>row-color-f9<#else>row-color-ff</#if>" colspan="2">
				                                      	${uiLabelMap.OrderShipGroup}:&nbsp;[${shipGroup.shipGroupSeqId}] ${shipGroupAddress.address1?default("${uiLabelMap.OrderNotShipped}")}
				                                  	</td>
				                                  	<td align="center" class="<#if rowColor>row-color-f9<#else>row-color-ff</#if>">
				                                      	<input type="text" name="iqm_${shipGroupAssoc.orderItemSeqId}:${shipGroupAssoc.shipGroupSeqId}" size="6" value="${shipGroupAssoc.quantity?string.number}" class="width-cell-100px no-bottom-margin"/>
				                                  	</td>
				                                  	<td colspan="2" class="<#if rowColor>row-color-f9<#else>row-color-ff</#if>">&nbsp;</td>
				                                  	
			                                      	<#assign itemStatusOkay = (orderItem.statusId != "ITEM_CANCELLED" && orderItem.statusId != "ITEM_COMPLETED" && (shipGroupAssoc.cancelQuantity?default(0) < shipGroupAssoc.quantity?default(0)) && ("Y" != orderItem.isPromo?if_exists))>
			                                      	<#if (security.hasEntityPermission("ORDERMGR", "_ADMIN", session) && itemStatusOkay) || (security.hasEntityPermission("ORDERMGR", "_UPDATE", session) && itemStatusOkay && orderHeader.statusId != "ORDER_SENT")>
			                                          	<td class="<#if rowColor>row-color-f9<#else>row-color-ff</#if>">
			                                          	</td>
			                                          	<td nowrap style="width:5%" class="<#if rowColor>row-color-f9<#else>row-color-ff</#if>">
			                                          		<a href="javascript:document.updateItemInfo.action='<@ofbizUrl>cancelOrderItem</@ofbizUrl>';document.updateItemInfo.orderItemSeqId.value='${orderItem.orderItemSeqId}';document.updateItemInfo.shipGroupSeqId.value='${shipGroup.shipGroupSeqId}';document.updateItemInfo.submit()" 
			                                          			style="vertical-align:top"><i class="icon-remove open-sans">${uiLabelMap.CommonCancel}</i>
		                                          			</a>
			                                          	</td>
			                                          	<td style="width:3%" class="<#if rowColor>row-color-f9<#else>row-color-ff</#if>">
			                                          		<input type="checkbox" name="selectedItem" value="${orderItem.orderItemSeqId}" />
			                                          		<span class="lbl"></span>
			                                          	</td>
			                                      	<#else>
			                                          	<td colspan="2" class="<#if rowColor>row-color-f9<#else>row-color-ff</#if>">&nbsp;</td>
			                                      	</#if>
				                              	</tr>
				                          	</#list>
				                      	</#if>
				                      	
				                      	<#-- now update/cancel reason and comment field -->
				                      	<#if orderItem.statusId != "ITEM_CANCELLED" && orderItem.statusId != "ITEM_COMPLETED" && ("Y" != orderItem.isPromo?if_exists)>
					                        <tr>
					                        	<td class="<#if rowColor>row-color-f9<#else>row-color-ff</#if> width20 <#if printBorderTop>no-border-top</#if>">&nbsp;</td>
					                        	<td colspan="8" class="<#if rowColor>row-color-f9<#else>row-color-ff</#if>">
					                        		<div style="display:inline-block">
					                        			${uiLabelMap.OrderReturnReason}: 
						                            	<select name="irm_${orderItem.orderItemSeqId}" class="no-bottom-margin">
						                              		<option value="">&nbsp;</option>
						                              		<#list orderItemChangeReasons as reason>
						                                		<option value="${reason.enumId}">${reason.get("description",locale)?default(reason.enumId)}</option>
						                              		</#list>
					                            		</select>
					                        		</div>
					                        		<div style="display:inline-block; margin-left:10px">
					                        			${uiLabelMap.CommonComments}: 
						                            	<input type="text" name="icm_${orderItem.orderItemSeqId}" value="" size="30" maxlength="60" class="no-bottom-margin"/>
						                            	<#if (orderHeader.orderTypeId == 'PURCHASE_ORDER')>
						                              		<span class="label">${uiLabelMap.OrderEstimatedShipDate}</span>
						                              		<@htmlTemplate.renderDateTimeField name="isdm_${orderItem.orderItemSeqId}" value="${orderItem.estimatedShipDate?if_exists}" event="" action="" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="25" maxlength="30" id="isdm_${orderItem.orderItemSeqId}" dateType="date" shortDateInput=false timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
						                              		<span class="label">${uiLabelMap.OrderOrderQuoteEstimatedDeliveryDate}</span>
						                              		<@htmlTemplate.renderDateTimeField name="iddm_${orderItem.orderItemSeqId}" value="${orderItem.estimatedDeliveryDate?if_exists}" event="" action="" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="25" maxlength="30" id="iddm_${orderItem.orderItemSeqId}" dateType="date" shortDateInput=false timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
					                            		</#if>
					                        		</div>
				                            	</td>
					                        </tr>
			                      		</#if>
				                    </#if>
				                </#list>
			                <tr>
			                    <td colspan="10"><#-- style="background-color:#eff3f8" btn btn-mini btn-primary -->
			                    	<a href="javascript:document.updateItemInfo.submit();" class="pull-right no-bottom-margin">
			                    		<i class="icon-ok open-sans">${uiLabelMap.DAUpdateOrderItems}</i>
			                    	</a>
			                    </td>
			                </tr>
			                </tbody>
			            </table>
			            </form>
			        </#if>
			        <hr style="margin-top:0" />
			        <#list orderHeaderAdjustments as orderHeaderAdjustment>
			            <#assign adjustmentType = orderHeaderAdjustment.getRelatedOne("OrderAdjustmentType", false)>
			            <#assign adjustmentAmount = Static["org.ofbiz.order.order.OrderReadHelper"].calcOrderAdjustment(orderHeaderAdjustment, orderSubTotal)>
			            <#assign orderAdjustmentId = orderHeaderAdjustment.get("orderAdjustmentId")>
			            <#assign productPromoCodeId = ''>
			            <#if adjustmentType.get("orderAdjustmentTypeId") == "PROMOTION_ADJUSTMENT" && orderHeaderAdjustment.get("productPromoId")?has_content>
			                <#assign productPromo = orderHeaderAdjustment.getRelatedOne("ProductPromo", false)>
			                <#assign productPromoCodes = delegator.findByAnd("ProductPromoCode", {"productPromoId":productPromo.productPromoId}, null, false)>
			                <#assign orderProductPromoCode = ''>
			                <#list productPromoCodes as productPromoCode>
			                    <#if !(orderProductPromoCode?has_content)>
			                        <#assign orderProductPromoCode = delegator.findOne("OrderProductPromoCode", {"productPromoCodeId":productPromoCode.productPromoCodeId, "orderId":orderHeaderAdjustment.orderId}, false)?if_exists>
			                    </#if>
			                </#list>
			                <#if orderProductPromoCode?has_content>
			                    <#assign productPromoCodeId = orderProductPromoCode.get("productPromoCodeId")>
			                </#if>
			            </#if>
			            <#if adjustmentAmount != 0>
			                <form name="updateOrderAdjustmentForm${orderAdjustmentId}" method="post" action="<@ofbizUrl>updateOrderAdjustment</@ofbizUrl>">
			                    <input type="hidden" name="orderAdjustmentId" value="${orderAdjustmentId?if_exists}"/>
			                    <input type="hidden" name="orderId" value="${orderId?if_exists}"/>
			                    <table class="basic-table" cellspacing="0" width="100%">
			                        <tr>
			                            <td class="align-text" width="52%">
			                                <span class="label">${adjustmentType.get("description",locale)}:&nbsp;${orderHeaderAdjustment.comments?if_exists}&nbsp;&nbsp;</span>
			                            </td>
			                            <td nowrap="nowrap" width="28%">
			                            	${uiLabelMap.DADescription}: 
			                                <#if (allowPriceChange)>
			                                    <input type="text" name="description" value="${orderHeaderAdjustment.get("description")?if_exists}" size="30" maxlength="60"/>
			                                <#else>
			                                    ${orderHeaderAdjustment.get("description")?if_exists}
			                                </#if>
			                            </td>
			                            <td width="12%">
			                            	<input type="text" name="amount" size="6" value="<@ofbizAmount amount=adjustmentAmount/>" class="width-cell-100px"/>
			                            </td>
			                            <td nowrap="nowrap" width="8%">
			                                <#if (allowPriceChange)>
		                                  	 	<#--
		                                  	 	<button type="submit" class="btn btn-small btn-primary">
		                                  	 		<i class="icon-ok open-sans">${uiLabelMap.CommonUpdate}</i>
		                                  	 	</button>
		                                  	 	<a href="javascript:document.deleteOrderAdjustment${orderAdjustmentId}.submit();" class="btn btn-small btn-primary">
			                                    	<i class="icon-trash open-sans">${uiLabelMap.CommonDelete}</i></a>
		                                  	 	-->
		                                  	 	<button type="submit" class="btn btn-mini btn-primary">
													<i class="icon-ok bigger-120"></i>
												</button>
												<button type="button" class="btn btn-mini btn-danger" onclick="javascript:document.deleteOrderAdjustment${orderAdjustmentId}.submit();">
													<i class="icon-trash bigger-120"></i>
												</button>
			                                <#else>
			                                    <@ofbizAmount amount=adjustmentAmount/>
			                                </#if>
			                            </td>
			                        </tr>
			                    </table>
			                </form>
			                <form name="deleteOrderAdjustment${orderAdjustmentId}" method="post" action="<@ofbizUrl>deleteOrderAdjustment</@ofbizUrl>">
			                    <input type="hidden" name="orderAdjustmentId" value="${orderAdjustmentId?if_exists}"/>
			                    <input type="hidden" name="orderId" value="${orderId?if_exists}"/>
			                    <#if adjustmentType.get("orderAdjustmentTypeId") == "PROMOTION_ADJUSTMENT">
			                        <input type="hidden" name="productPromoCodeId" value="${productPromoCodeId?if_exists}"/>
			                    </#if>
			                </form>
			            </#if>
			        </#list>
			
			        <#-- add new adjustment -->
			        <#if security.hasEntityPermission("ORDERMGR", "_UPDATE", session) && orderHeader.statusId != "ORDER_COMPLETED" && orderHeader.statusId != "ORDER_CANCELLED" && orderHeader.statusId != "ORDER_REJECTED">
			            <form name="addAdjustmentForm" method="post" action="<@ofbizUrl>createOrderAdjustment</@ofbizUrl>">
			                <input type="hidden" name="comments" value="Added manually by [${userLogin.userLoginId}]"/>
			                <input type="hidden" name="orderId" value="${orderId?if_exists}"/>
			                <table class="basic-table" cellspacing="0" style="width:100%">
			                    <tr><td colspan="5"><hr /></td></tr>
			                    <tr>
			                        <td class="align-text">
			                            <span class="label">${uiLabelMap.OrderAdjustment}</span>&nbsp;
			                        </td>
			                        <td>
			                        	<select name="orderAdjustmentTypeId">
			                                <#list orderAdjustmentTypes as type>
			                                <option value="${type.orderAdjustmentTypeId}">${type.get("description",locale)?default(type.orderAdjustmentTypeId)}</option>
			                                </#list>
			                            </select>
			                            <select name="shipGroupSeqId" style="margin-right:5px">
			                                <option value="_NA_"></option>
			                                <#list shipGroups as shipGroup>
			                                <option value="${shipGroup.shipGroupSeqId}">${uiLabelMap.OrderShipGroup} ${shipGroup.shipGroupSeqId}</option>
			                                </#list>
			                            </select>
			                        </td>
			                        <td width="28%">
			                            ${uiLabelMap.DADescription}: <input type="text" name="description" value="" size="30" maxlength="60"/>
			                        </td>
			                        <td width="12%">
			                            <input type="text" name="amount" size="6" value="<@ofbizAmount amount=0.00/>" class="width-cell-100px"/>
			                        </td>
			                        <td width="8%">
			                        	<button class="btn btn-mini btn-primary" type="submit">
			                        		<i class="icon-plus open-sans">${uiLabelMap.CommonAdd}</i>
			                        	</button>
			                        </td>
			                    </tr>
			                </table>
			            </form>
			        </#if>
			
			        <#-- subtotal -->
			        <table class="basic-table" cellspacing="0" style="width:100%">
			            <tr><td colspan="4"><hr class="no-bottom-margin" /></td></tr>
			            <tr class="align-text">
			              <td width="80%"><span class="label" style="float:right">${uiLabelMap.OrderItemsSubTotal}: </span></td>
			              <td width="10%" nowrap="nowrap"><@ofbizCurrency amount=orderSubTotal isoCode=currencyUomId/></td>
			              <td width="10%" colspan="2">&nbsp;</td>
			            </tr>
			
			            <#-- other adjustments -->
			            <tr class="align-text">
			              <td><span class="label" style="float:right">${uiLabelMap.OrderTotalOtherOrderAdjustments}: </span></td>
			              <td nowrap="nowrap"><@ofbizCurrency amount=otherAdjAmount isoCode=currencyUomId/></td>
			              <td colspan="2">&nbsp;</td>
			            </tr>
			
			            <#-- shipping adjustments -->
			            <tr class="align-text">
			              <td><span class="label" style="float:right">${uiLabelMap.OrderTotalShippingAndHandling}: </span></td>
			              <td nowrap="nowrap"><@ofbizCurrency amount=shippingAmount isoCode=currencyUomId/></td>
			              <td colspan="2">&nbsp;</td>
			            </tr>
			
			            <#-- tax adjustments -->
			            <tr class="align-text">
			              <td><span class="label" style="float:right">${uiLabelMap.OrderTotalSalesTax}: </span></td>
			              <td nowrap="nowrap"><@ofbizCurrency amount=taxAmount isoCode=currencyUomId/></td>
			              <td colspan="2">&nbsp;</td>
			            </tr>
			
			            <#-- grand total -->
			            <tr class="align-text">
			              <td><span class="label" style="float:right">${uiLabelMap.OrderTotalDue}: </span></td>
			              <td nowrap="nowrap"><@ofbizCurrency amount=grandTotal isoCode=currencyUomId/></td>
			              <td colspan="2">&nbsp;</td>
			            </tr>
			        </table>
			    </div>
			</div>
			
		</div>
	</div>
</#if>