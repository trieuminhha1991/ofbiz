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
<#if shipment?exists>
<div class="widget-box transparent no-bottom-border">

    <div class="widget-header">
    <h3>${uiLabelMap.PageTitleAddItemsFromOrder}</h3>
        <span class="widget-toolbar none-content">
            	 ${screens.render("component://product/widget/facility/ShipmentScreens.xml#ProductGenerateShipmentManifestReportSubTabBar")}
        </span>
        <br class="clear"/>
    </div>
    <div class="widget-body">
        <form name="additemsfromorder" action="<@ofbizUrl>AddItemsFromOrder</@ofbizUrl>">
            <input type="hidden" name="shipmentId" value="${shipmentId}"/>
            <table>
            	<tr>
            	<td>
                	<span class="label">${uiLabelMap.ProductOrderId}</span>
                </td>
                <td>
                	<span>
                	  <@htmlTemplate.lookupField value="${orderId?if_exists}" formName="additemsfromorder" name="orderId" id="orderId" fieldFormName="LookupOrderHeaderAndShipInfo"/>
                	</span>
           		</td>
           		</tr>
           		<tr>
	           		<td>
	                	<span class="label">${uiLabelMap.ProductOrderShipGroupId}</span>
	                	
	                </td>
					<td>
						<input type="text" size="20" name="shipGroupSeqId" value="${shipGroupSeqId?if_exists}"/>
					</td>
                </tr>
                <tr>
                	<td></td>
	                <td>
		                <button type="submit" class="btn btn-primary btn-small">
		                <i class="icon-ok"></i>
		                ${uiLabelMap.CommonSelect} 
	                	</button>
                	</td>
            	</tr>
            </table>
        </form>
    </div>
</div>
<div class="widget-box transparent no-bottom-border">
    <div class="widget-header">
    <h3>${uiLabelMap.ProductAddItemsShipment}: [${shipmentId?if_exists}]; ${uiLabelMap.ProductFromAnOrder}: [${orderId?if_exists}], ${uiLabelMap.ProductOrderShipGroupId}: [${shipGroupSeqId?if_exists}]</h3>
        <span calss="widget-toolbar>
            
        </ul>
        <br class="clear"/>
    </div>
    <div class="widget-body">
    <#if orderId?has_content && !orderHeader?exists>
        <h4 style="color: red;"><#assign uiLabelWithVar=uiLabelMap.ProductErrorOrderIdNotFound?interpret><@uiLabelWithVar/>.</h3>
    </#if>
    <#if orderHeader?exists>
        <#if orderHeader.orderTypeId == "SALES_ORDER" && shipment.shipmentTypeId?if_exists != "SALES_SHIPMENT">
            <h5 style="color: red;">${uiLabelMap.ProductWarningOrderType} ${(orderType.get("description",locale))?default(orderHeader.orderTypeId?if_exists)}, ${uiLabelMap.ProductNotSalesShipment}.</h3>
        <#elseif orderHeader.orderTypeId == "PURCHASE_ORDER" && shipment.shipmentTypeId?if_exists != "PURCHASE_SHIPMENT" && shipment.shipmentTypeId?if_exists != "DROP_SHIPMENT">
            <h5 style="color: red;">${uiLabelMap.ProductWarningOrderType} ${(orderType.get("description",locale))?default(orderHeader.orderTypeId?if_exists)}, ${uiLabelMap.ProductNotPurchaseShipment}.</h3>
        <#else>
            <h5 class="smaller blue lighter">${uiLabelMap.ProductNoteOrderType} ${(orderType.get("description",locale))?default(orderHeader.orderTypeId?if_exists)}.</h3>
            <h5 class="smaller blue lighter">${uiLabelMap.ProductShipmentType}: ${shipment.shipmentTypeId?if_exists}.</h3>
        </#if>
        <#if shipment.shipmentTypeId?if_exists == "SALES_SHIPMENT">
            <h5 class="smaller blue lighter">${uiLabelMap.ProductOriginFacilityIs}: <#if originFacility?exists>${originFacility.facilityName?if_exists} [${originFacility.facilityId}]<#else><span style="color: red;">${uiLabelMap.ProductNotSet}</span></#if></h3>
        <#elseif shipment.shipmentTypeId?if_exists == "PURCHASE_SHIPMENT">
            <h5 class="smaller blue lighter">${uiLabelMap.ProductDestinationFacilityIs}: <#if destinationFacility?exists>${destinationFacility.facilityName?if_exists} [${destinationFacility.facilityId}]<#else><span style="color: red;">${uiLabelMap.ProductNotSet}</span></#if></h3>
        </#if>
        <#if "ORDER_APPROVED" == orderHeader.statusId || "ORDER_BACKORDERED" == orderHeader.statusId>
            <h5 class="smaller blue lighter">${uiLabelMap.ProductNoteOrderStatus} ${(orderHeaderStatus.get("description",locale))?default(orderHeader.statusId?if_exists)}.</h3>
        <#elseif "ORDER_COMPLETED" == orderHeader.statusId>
            <h5 class="smaller blue lighter">${uiLabelMap.ProductNoteOrderStatus} ${(orderHeaderStatus.get("description",locale))?default(orderHeader.statusId?if_exists)}, ${uiLabelMap.ProductNoItemsLeft}.</h3>
        <#else>
            <h5 style="color: red;">${uiLabelMap.ProductWarningOrderStatus} ${(orderHeaderStatus.get("description",locale))?default(orderHeader.statusId?if_exists)}; ${uiLabelMap.ProductApprovedBeforeShipping}.</h3>
        </#if>
    </#if>
    <br />
    <#if orderItemDatas?exists>
        <#assign rowCount = 0>
        <#if isSalesOrder>
            <form action="<@ofbizUrl>issueOrderItemShipGrpInvResToShipment</@ofbizUrl>" method="post" name="selectAllForm" id="OrderItemShipGrpInvRes">
        <#else>
            <form action="<@ofbizUrl>issueOrderItemToShipment</@ofbizUrl>" method="post" name="selectAllForm" id="OrderItems">
        </#if>
        <input style="opacity: 1"type="hidden" name="shipmentId" value="${shipmentId}" />
        <input style="opacity: 1"type="hidden" name="_useRowSubmit" value="Y" />
        <table cellspacing="0" cellpadding="2" class="table-bordered table table-striped table-hover dataTable">
            <thead>
            <tr class="header-row">
                <td style="font-weight:bold">${uiLabelMap.ProductOrderId}</td>
                <td style="font-weight:bold">${uiLabelMap.ProductOrderShipGroupId}</td>
                <td style="font-weight:bold">${uiLabelMap.ProductOrderItem}</td>
                <td style="font-weight:bold">${uiLabelMap.ProductProduct}</td>
                <#if isSalesOrder>
                    <td style="font-weight:bold" >${uiLabelMap.ProductItemsIssuedReserved}</td>
                    <td style="font-weight:bold">${uiLabelMap.ProductIssuedReservedTotalOrdered}</td>
                    <td style="font-weight:bold">${uiLabelMap.ProductReserved}</td>
                    <td style="font-weight:bold">${uiLabelMap.ProductNotAvailable}</td>
                <#else>
                    <td style="font-weight:bold">${uiLabelMap.ProductItemsIssued}</td>
                    <td style="font-weight:bold">${uiLabelMap.ProductIssuedOrdered}</td>
                </#if>
                <td  style="font-weight:bold">${uiLabelMap.Quantity} ${uiLabelMap.ProductIssue}</td>
                <td style="font-weight:bold" align="right">
                    <div>${uiLabelMap.CommonAll}</div><input type="checkbox" name="selectAll" value="${uiLabelMap.CommonY}" onclick="javascript:toggleAll(this, 'selectAllForm');highlightAllRows(this, 'orderItemData_tableRow_', 'selectAllForm');" /><span class="lbl"></span>
                </td>
            </tr>
            </thead>
            <#assign alt_row = false>
            <#list orderItemDatas?if_exists as orderItemData>
                <#assign orderItemAndShipGroupAssoc = orderItemData.orderItemAndShipGroupAssoc>
                <#assign product = orderItemData.product?if_exists>
                <#assign itemIssuances = orderItemData.itemIssuances>
                <#assign totalQuantityIssued = orderItemData.totalQuantityIssued>
                <#assign orderItemShipGrpInvResDatas = orderItemData.orderItemShipGrpInvResDatas?if_exists>
                <#assign totalQuantityReserved = orderItemData.totalQuantityReserved?if_exists>
                <#assign totalQuantityIssuedAndReserved = orderItemData.totalQuantityIssuedAndReserved?if_exists>
                <tr id="orderItemData_tableRow_${rowCount}" valign="middle"<#if alt_row> class="alternate-row"</#if>>
                    <td>${orderItemAndShipGroupAssoc.orderId}</td>
					<td>${orderItemAndShipGroupAssoc.shipGroupSeqId}</td>
					<td>${orderItemAndShipGroupAssoc.orderItemSeqId}</td>
                    <td><div>${(product.internalName)?if_exists} [${orderItemAndShipGroupAssoc.productId?default("N/A")}]</div></td>
                    <td>
                        <#if itemIssuances?has_content>
                            <#list itemIssuances as itemIssuance>
                                <div><b>[${itemIssuance.quantity?if_exists}]</b>${itemIssuance.shipmentId?if_exists}:${itemIssuance.shipmentItemSeqId?if_exists} ${uiLabelMap.CommonOn} [${(itemIssuance.issuedDateTime.toString())?if_exists}] ${uiLabelMap.CommonBy} [${(itemIssuance.issuedByUserLoginId)?if_exists}]</div>
                            </#list>
                        <#else>
                            <div>&nbsp;</div>
                        </#if>
                    </td>
                    <td>
                        <div>
                            <#if isSalesOrder>
                                <#if (totalQuantityIssuedAndReserved != orderItemAndShipGroupAssoc.quantity)>
                                <span style="color: red;">
                                <#else>
                                <span>
                                </#if>
                                    [${totalQuantityIssued} + ${totalQuantityReserved} = ${totalQuantityIssuedAndReserved}]
                                    <b>
                                        <#if (totalQuantityIssuedAndReserved > orderItemAndShipGroupAssoc.quantity)>&gt;<#else><#if (totalQuantityIssuedAndReserved < orderItemAndShipGroupAssoc.quantity)>&lt;<#else>=</#if></#if>
                                        ${orderItemAndShipGroupAssoc.quantity}
                                    </b>
                                </span>
                            <#else>
                                <#if (totalQuantityIssued > orderItemAndShipGroupAssoc.quantity)>
                                <span style="color: red;">
                                <#else>
                                <span>
                                </#if>
                                    ${totalQuantityIssued}
                                    <b>
                                        <#if (totalQuantityIssued > orderItemAndShipGroupAssoc.quantity)>&gt;<#else><#if (totalQuantityIssued < orderItemAndShipGroupAssoc.quantity)>&lt;<#else>=</#if></#if>
                                        ${orderItemAndShipGroupAssoc.quantity}
                                    </b>
                                </span>
                            </#if>
                        </div>
                    </td>
                    <#if isSalesOrder>
                        <td>&nbsp;</td>
                        <td>&nbsp;</td>
                        <td>&nbsp;</td>
                        <td>&nbsp;</td>
                    <#else>
                        <#assign quantityNotIssued = orderItemAndShipGroupAssoc.quantity - totalQuantityIssued>
                        <#if (quantityNotIssued > 0)>
                            <td>
                                <input style="opacity: 1"type="hidden" name="shipmentId_o_${rowCount}" value="${shipmentId}"/>
                                <input style="opacity: 1"type="hidden" name="orderId_o_${rowCount}" value="${orderItemAndShipGroupAssoc.orderId}"/>
                                <input style="opacity: 1"type="hidden" name="shipGroupSeqId_o_${rowCount}" value="${orderItemAndShipGroupAssoc.shipGroupSeqId}"/>
                                <input style="opacity: 1"type="hidden" name="orderItemSeqId_o_${rowCount}" value="${orderItemAndShipGroupAssoc.orderItemSeqId}"/>
                                <input style="opacity: 1"type="text" size="5" name="quantity_o_${rowCount}" value="${quantityNotIssued}"/>
                            </td>
                            <td align="left">
                              <input type="checkbox" name="_rowSubmit_o_${rowCount}" value="Y" onclick="javascript:checkToggle(this, 'selectAllForm');highlightRow(this,'orderItemData_tableRow_${rowCount}');" /><span class="lbl"></span>
                            </td>
                            <#assign rowCount = rowCount + 1>
                        <#else>
                            <td>&nbsp;</td>
                            <td>&nbsp;</td>
                        </#if>
                    </#if>
                </tr>
                <#if isSalesOrder>
                    <#list orderItemShipGrpInvResDatas as orderItemShipGrpInvResData>
                        <#assign orderItemShipGrpInvRes = orderItemShipGrpInvResData.orderItemShipGrpInvRes>
                        <#assign inventoryItem = orderItemShipGrpInvResData.inventoryItem>
                        <#assign inventoryItemFacility = orderItemShipGrpInvResData.inventoryItemFacility>
                        <#assign availableQuantity = orderItemShipGrpInvRes.quantity - (orderItemShipGrpInvRes.quantityNotAvailable?default(0))>
                        <#if availableQuantity < 0>
                            <#assign availableQuantity = 0>
                        </#if>
                        <tr id="orderItemData_tableRow_${rowCount}">
                            <td>&nbsp;</td>
                            <td>&nbsp;</td>
                            <td>&nbsp;</td>
                        	<td>&nbsp;</td>
                            <td>
                                <div>
                                    ${orderItemShipGrpInvRes.inventoryItemId}
                                    <#if inventoryItem.facilityId?has_content>
                                        <span<#if originFacility?exists && originFacility.facilityId != inventoryItem.facilityId> style="color: red;"</#if>>[${(inventoryItemFacility.facilityName)?default(inventoryItem.facilityId)}]</span>
                                    <#else>
                                        <span style="color: red;">[${uiLabelMap.ProductNoFacility}]</span>
                                    </#if>
                                </div>
                            </td>
                            <td>&nbsp;</td>
                            <td>${orderItemShipGrpInvRes.quantity}</td>
                            <td>${orderItemShipGrpInvRes.quantityNotAvailable?default("&nbsp;")}</td>
                            <#if originFacility?exists && originFacility.facilityId == inventoryItem.facilityId?if_exists>
                                <td>
                                    <input style="opacity: 1"type="hidden" name="shipmentId_o_${rowCount}" value="${shipmentId}"/>
                                    <input style="opacity: 1"type="hidden" name="orderId_o_${rowCount}" value="${orderItemShipGrpInvRes.orderId}"/>
                                    <input style="opacity: 1"type="hidden" name="shipGroupSeqId_o_${rowCount}" value="${orderItemShipGrpInvRes.shipGroupSeqId}"/>
                                    <input style="opacity: 1"type="hidden" name="orderItemSeqId_o_${rowCount}" value="${orderItemShipGrpInvRes.orderItemSeqId}"/>
                                    <input style="opacity: 1"type="hidden" name="inventoryItemId_o_${rowCount}" value="${orderItemShipGrpInvRes.inventoryItemId}"/>
                                    <input style="opacity: 1"type="text" size="5" name="quantity_o_${rowCount}" value="${(orderItemShipGrpInvResData.shipmentPlanQuantity)?default(availableQuantity)}"/>
                                </td>
                                <td align="right">
                                  <input type="checkbox" name="_rowSubmit_o_${rowCount}" value="Y" onclick="javascript:checkToggle(this, 'selectAllForm');highlightRow(this,'orderItemData_tableRow_${rowCount}');" /><span class="lbl"></span>
                                </td>
                                <#assign rowCount = rowCount + 1>
                            <#else>
                                <td>${uiLabelMap.ProductNotOriginFacility}</td>
                                <td>&nbsp;</td>
                            </#if>
                        </tr>
                    </#list>
                </#if>
                <#-- toggle the row color -->
                <#assign alt_row = !alt_row>
            </#list>
        </table>
        <div align="left"><button type="submit" style="margin-top: 2px" class="btn btn-primary btn-small" >
        <i class="icon-ok"></i>
        ${uiLabelMap.ProductIssue}
        </button>
        </div>
        <input style="opacity: 1"type="hidden" name="_rowCount" value="${rowCount}" />
        </form>
        <script language="JavaScript" type="text/javascript">selectAll('selectAllForm');</script>
    </#if>
    </div>
</div>
<#else>
<div class="widget-box">
    <div class="widget-header">
    <h3>${uiLabelMap.ProductShipmentNotFoundId}: [${shipmentId?if_exists}]</h3>
        <span class="widget-toolbar">
            
        </span>
        <br class="clear"/>
    </div>
</div>
</#if>