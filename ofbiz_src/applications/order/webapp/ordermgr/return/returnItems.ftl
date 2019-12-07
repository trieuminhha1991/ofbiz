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

<#macro displayReturnAdjustment returnAdjustment adjEditable>
    <#assign returnHeader = returnAdjustment.getRelatedOne("ReturnHeader", false)>
    <#assign adjReturnType = returnAdjustment.getRelatedOne("ReturnType", false)?if_exists>
    <#if (adjEditable)>
        <input type="hidden" name="_rowSubmit_o_${rowCount}" value="Y" />
        <input type="hidden" name="returnAdjustmentId_o_${rowCount}" value="${returnAdjustment.returnAdjustmentId}" />
    </#if>
    <tr>
        <td colspan="2">&nbsp;</td>
        <td colspan="3" align="right"><span class="label">${returnAdjustment.get("description",locale)?default("N/A")}</span>
            <#if returnAdjustment.comments?has_content>: ${returnAdjustment.comments}</#if>
        </div></td>
        <#if (adjEditable)>
           <td align="right">
              <input type="text" size="8" name="amount_o_${rowCount}" value="${returnAdjustment.amount?default(0)?string("##0.00")}"/>
           </td>
        <#else>
           <td align="right"><@ofbizCurrency amount=returnAdjustment.amount?default(0) isoCode=returnHeader.currencyUomId/></td>
        </#if>
        <td colspan="2">&nbsp;</td>
        <td><div>
           <#if (!adjEditable)>
                <#if adjReturnType?has_content>
                  ${adjReturnType.get("description", locale)?default("${uiLabelMap.CommonNA}")}
                </#if>
           <#else>
               <select name="returnTypeId_o_${rowCount}">
                  <#if (adjReturnType?has_content)>
                    <option value="${adjReturnType.returnTypeId}">${adjReturnType.get("description",locale)?if_exists}</option>
                    <option value="${adjReturnType.returnTypeId}">--</option>
                  </#if>
                  <#list returnTypes as returnTypeItem>
                    <option value="${returnTypeItem.returnTypeId}">${returnTypeItem.get("description",locale)?if_exists}</option>
                  </#list>
                </select>
          </#if>
          </div>
       </td>
       <#if (adjEditable)>
         <td align='right'><a href='javascript:document.removeReturnAdjustment_${rowCountForAdjRemove}.submit()' class='buttontext'>${uiLabelMap.CommonRemove}</a></td>
       <#else>
       <td colspan="2">&nbsp;</td>
       </#if>
        <#if (adjEditable)>
          <#assign rowCount = rowCount + 1>
          <#assign rowCountForAdjRemove = rowCountForAdjRemove + 1>
       </#if>
       <#assign returnTotal = returnTotal + returnAdjustment.amount?default(0)>
    </tr>
</#macro>
    <#if returnHeader?has_content>
      	<#if returnHeader.destinationFacilityId?has_content && returnHeader.statusId == "RETURN_ACCEPTED" && returnHeader.returnHeaderTypeId?starts_with("CUSTOMER_")>
        	<#list returnShipmentIds as returnShipmentId>
        		<#if notOrderComponent?has_content>
        		<!--
        			<a href="<@ofbizUrl>ViewShipment?shipmentId=${returnShipmentId.shipmentId}${externalKeyParam}</@ofbizUrl>" class="btn btn-small btn-primary"><i class="fa-truck"></i> ${uiLabelMap.ProductShipmentId} ${returnShipmentId.shipmentId}</a>
          		-->
          		<a href="<@ofbizUrl>ReceiveReturn?facilityId=${returnHeader.destinationFacilityId}&amp;returnId=${returnHeader.returnId?if_exists}&amp;shipmentId=${returnShipmentId.shipmentId}${externalKeyParam}</@ofbizUrl>" class="btn btn-small btn-primary"><i class="icon-cloud-download"></i>${uiLabelMap.OrderReceiveReturn}</a>
        		<#else>
        		<a href="/facility/control/ViewShipment?shipmentId=${returnShipmentId.shipmentId}${externalKeyParam}" class="btn btn-small btn-primary"><i class="fa-truck"></i> ${uiLabelMap.ProductShipmentId} ${returnShipmentId.shipmentId}</a>
          		<a href="/facility/control/ReceiveReturn?facilityId=${returnHeader.destinationFacilityId}&amp;returnId=${returnHeader.returnId?if_exists}&amp;shipmentId=${returnShipmentId.shipmentId}${externalKeyParam}" class="btn btn-small btn-primary"><i class="icon-cloud-download"></i>${uiLabelMap.OrderReceiveReturn}</a>
        		</#if>
        	</#list>
      	<#elseif returnHeader.statusId == "SUP_RETURN_ACCEPTED" && returnHeader.returnHeaderTypeId == "VENDOR_RETURN">
         	<#if returnShipmentIds?has_content>
           	<#list returnShipmentIds as returnShipmentId>
             	<a href="/facility/control/ViewShipment?shipmentId=${returnShipmentId.shipmentId}${externalKeyParam}" class="buttontext">${uiLabelMap.ProductShipmentId} ${returnShipmentId.shipmentId}</a>
           	</#list>
         <#else>
           <a href="/facility/control/EditShipment?primaryReturnId=${returnHeader.returnId}&amp;partyIdTo=${toPartyId}&amp;statusId=SHIPMENT_INPUT&amp;shipmentTypeId=PURCHASE_RETURN" class="buttontext">${uiLabelMap.OrderCreateReturnShipment}</a>
         </#if>
      </#if>
    </#if>

<!--<div class="widget-box transparent no-bottom-border">
<div class=" widget-header header smaller lighter blue">
	<h3>${uiLabelMap.PageTitleReturnItems}</h3> -->
<!--	<div style="float:right;margin-top:-30px">
	<a class="open-sans icon-header" style="padding-right:20px;font-size:15px;text-decoration:none"<#if selected="OrderReturnHeader"> class="selected"</#if> href="<@ofbizUrl>returnMain?returnId=${returnId?if_exists}</@ofbizUrl>">${uiLabelMap.OrderReturnHeader}</a>
	<a class="open-sans icon-list" style="padding-right:20px;font-size:15px;text-decoration:none"<#if selected="OrderReturnItems"> class="selected"</#if>  href="<@ofbizUrl>returnItems?returnId=${returnId?if_exists}</@ofbizUrl>">${uiLabelMap.OrderReturnItems}</a>
	<a class="open-sans icon-history" style="padding-right:20px;font-size:15px;text-decoration:none"<#if selected="OrderReturnHistory"> class="selected"</#if>  href="<@ofbizUrl>ReturnHistory?returnId=${returnId?if_exists}</@ofbizUrl>">${uiLabelMap.OrderReturnHistory}</a>
	<a  class="btn-pdf open-sans" style="padding-right:20px;font-size:15px;text-decoration:none" href="<@ofbizUrl>return.pdf?returnId=${returnId?if_exists}</@ofbizUrl>" target="_BLANK" >PDF</a>
    </div> 
-->
<div >
  <!-- <div class="screenlet-title-bar">
        <ul>
            <li class="h3">${uiLabelMap.PageTitleReturnItems}</li>
        </ul>
        <br class="clear"/>
    </div>-->
    <div class="screenlet-body">
<!-- if we're called with loadOrderItems or createReturn, then orderId would exist -->
<#if !requestParameters.orderId?exists>
	<#if returnHeader.statusId == "RETURN_REQUESTED" || returnHeader.statusId == "SUP_RETURN_REQUESTED">
    <form name="orderToReturns" method="post" action="<@ofbizUrl>returnItems</@ofbizUrl>">
      <input type="hidden" name="returnId" value="${returnId}" />
      <table class="basic-table" cellspacing="0" >
        <#if partyOrders?has_content>
          	<tr>
	            <td colspan="2" align='right' nowrap="nowrap" class="label">${uiLabelMap.CommonSelect} ${uiLabelMap.OrderOrderId}</td>
	            <td colspan="2" align='left'>
	              <select name="orderId">
	                <#list partyOrders as order>
	                  <option value="${order.orderId}">${order.orderId} - ${order.orderDate}</option>
	                </#list>
	              </select>
	            </td>
	            <td><div class="tooltip">${uiLabelMap.OrderReturnLoadItems}</div></td>
          	</tr>
          	<tr>
	          <td colspan="2">&nbsp;</td>
	          <td colspan="2" align='left'>
	            <a href="javascript:document.orderToReturns.submit();">${uiLabelMap.OrderReturnLoadItems}</a>
	          </td>
        	</tr>
        <#else>
          <tr>
          <!--<td colspan="4" nowrap="nowrap"><div>${uiLabelMap.OrderNoOrderFoundForParty}: <a href="${customerDetailLink}${partyId?default('_NA_')}" class="buttontext">${partyId?default('[null]')}</a></div></td>
          -->
            <td colspan="4" nowrap="nowrap"><div>${uiLabelMap.OrderNoOrderFoundForParty}: <a href="${customerDetailLink}${partyId?default('_NA_')}" class="buttontext">${partyId?default('[null]')}</a></div></td>
          </tr>
<!--          <tr>
            <td width='25%' align='right' nowrap="nowrap"><div>${uiLabelMap.OrderOrderId}</div></td>
            <td>&nbsp;</td>
            <td width='25%'>
              <input type='text' name='orderId' size='20' maxlength='20' />
            </td>
            <td><div class="tooltip">${uiLabelMap.OrderReturnLoadItems}</div></td>
          </tr>
-->          
        </#if>

      </table>
      </div>
      </div>
    </form>
	</#if>
    <form method="post" action="<@ofbizUrl>updateReturnItems</@ofbizUrl>" name="ListReturnItems">
        <input type="hidden" name="_useRowSubmit" value="Y" />
          <div class="row-fluid">
          <div class="widget-body-inner">
          <h4>${uiLabelMap.CommonList} ${uiLabelMap.OrderReturnItems}</h4>
         <div id="table-container">
        <table class="table table-striped table-bordered table-hover dataTable" cellspacing="0" >
          <#assign readOnly = (returnHeader.statusId != "RETURN_REQUESTED" && returnHeader.statusId != "SUP_RETURN_REQUESTED")>
          <#-- information about orders and amount refunded/credited on past returns -->
          <#if orh?exists>
          <tr><td colspan="10">
              <table  class="table table-striped table-bordered table-hover dataTable" cellspacing="0" style=" margin: -12px; width: auto;">
                <tr>
                  <td class="label" width="25%">${uiLabelMap.OrderOrderTotal}</td>
                  <td><@ofbizCurrency amount=orh.getOrderGrandTotal() isoCode=orh.getCurrency()/></td>
                </tr>
                <tr>
                  <td class="label" width="25%">${uiLabelMap.OrderAmountAlreadyCredited}</td>
                  <td><@ofbizCurrency amount=orh.getReturnedCreditTotalWithBillingAccountBd() isoCode=orh.getCurrency()/></td>
                </tr>
                <tr>
                  <td class="label" width="25%">${uiLabelMap.OrderAmountAlreadyRefunded}</td>
                  <td><@ofbizCurrency amount=orh.getReturnedRefundTotalWithBillingAccountBd() isoCode=orh.getCurrency()/></td>
                </tr>
              </table>
          </td></tr>
          </#if>
          <tr class="header-row">
            <td>${uiLabelMap.OrderOrderItems}</td>
            <td>${uiLabelMap.ProductProduct}</td>
            <td>${uiLabelMap.CommonDescription}</td>
            <td>${uiLabelMap.OrderQuantity}</td>
            <td>${uiLabelMap.OrderPrice}</td>
            <td>${uiLabelMap.OrderSubTotal}</td>
            <td>${uiLabelMap.OrderReturnReason}</td>
            <td>${uiLabelMap.OrderItemStatus}</td>
            <td>${uiLabelMap.CommonType}</td>
            <#if (readOnly)>
            <td>${uiLabelMap.OrderReturnResponse}</td>
            </#if>
            <td>${uiLabelMap.CommonDelete}</td>
          </tr>
          <#assign returnTotal = 0.0>
          <#assign rowCount = 0>
          <#assign rowCountForAdjRemove = 0>
          <#if returnItems?has_content>
            <#assign alt_row = false>
            <#list returnItems as item>
              <#assign orderItem = item.getRelatedOne("OrderItem", false)?if_exists>
              <#assign orderHeader = item.getRelatedOne("OrderHeader", false)?if_exists>
              <#assign returnReason = item.getRelatedOne("ReturnReason", false)?if_exists>
              <#assign returnType = item.getRelatedOne("ReturnType", false)?if_exists>
              <#assign status = item.getRelatedOne("InventoryStatusItem", false)?if_exists>
              <#assign shipmentReceipts = item.getRelated("ShipmentReceipt", null, null, false)?if_exists>
              <#if (item.get("returnQuantity")?exists && item.get("returnPrice")?exists)>
                 <#assign returnTotal = returnTotal + item.get("returnQuantity") * item.get("returnPrice") >
                 <#assign returnItemSubTotal = item.get("returnQuantity") * item.get("returnPrice") >
              <#else>
                 <#assign returnItemSubTotal = null >  <#-- otherwise the last item's might carry over -->
              </#if>
              <tr valign="middle"<#if alt_row> class="alternate-row"</#if>>
                <td><a href="<@ofbizUrl>orderview?orderId=${item.orderId}</@ofbizUrl>" class="buttontext">${item.orderId}</a> - ${item.orderItemSeqId?default("N/A")}
                  <input name="orderId_o_${rowCount}" value="${item.orderId}" type="hidden" />
                  <input name="returnId_o_${rowCount}" value="${item.returnId}" type="hidden" />
                  <input name="returnItemTypeId_o_${rowCount}" value="${item.returnItemTypeId}" type="hidden" />
                  <input name="returnItemSeqId_o_${rowCount}" value="${item.returnItemSeqId}" type="hidden" />
                  <input type="hidden" name="_rowSubmit_o_${rowCount}" value="Y" />
                </td>
                <td><div>
                    <#if item.get("productId")?exists>
                        <a href="/catalog/control/EditProductInventoryItems?productId=${item.productId}" class="buttontext">${item.productId}</a>
                    <#else>
                        N/A
                    </#if></div></td>
                <td><div>
                    <#if readOnly>
                        ${item.description?default("N/A")}
                    <#else>
                        <input name="description_o_${rowCount}" value="${item.description?if_exists}" type="text" size="15" />
                    </#if>
                    </div></td>
                <td><div>
                    <#if readOnly>
                        ${item.returnQuantity?string.number}
                    <#else>
                        <input class="width100px" name="returnQuantity_o_${rowCount}" value="${item.returnQuantity}" type="text" size="8" align="right" />
                    </#if>
                    <#if item.receivedQuantity?exists>
                    <br />${uiLabelMap.OrderTotalQuantityReceive}: ${item.receivedQuantity}
                        <#list shipmentReceipts?if_exists as shipmentReceipt>
                            <br />${uiLabelMap.OrderQty}: ${shipmentReceipt.quantityAccepted}, ${shipmentReceipt.datetimeReceived}, <a href="/facility/control/EditInventoryItem?inventoryItemId=${shipmentReceipt.inventoryItemId}" class="buttontext">${shipmentReceipt.inventoryItemId}</a>
                        </#list>
                    </#if>
                    </div></td>
                <td><div>
                    <#if readOnly>
                        <@ofbizCurrency amount=item.returnPrice isoCode=orderHeader.currencyUom/>
                    <#else>
                        <input class="width100px" name="returnPrice_o_${rowCount}" value="<@ofbizCurrency amount=item.returnPrice isoCode=orderHeader.currencyUom/>" type="text" size="8" align="right" />
                    </#if>
                    </div></td>
                <td>
                    <#if returnItemSubTotal?exists><@ofbizCurrency amount=returnItemSubTotal isoCode=orderHeader.currencyUom/></#if>
                </td>
                <td><div>
                    <#if readOnly>
                        ${returnReason.get("description",locale)?default("N/A")}
                    <#else>
                        <select name="returnReasonId_o_${rowCount}">
                            <#if (returnReason?has_content)>
                                <option value="${returnReason.returnReasonId}">${returnReason.get("description",locale)?if_exists}</option>
                                <option value="${returnReason.returnReasonId}">--</option>
                            </#if>
                            <#list returnReasons as returnReasonItem>
                                <option value="${returnReasonItem.returnReasonId}">${returnReasonItem.get("description",locale)?if_exists}</option>
                            </#list>
                        </select>
                    </#if>
                    </div></td>
                <td><div>
                  <#if readOnly>
                      <#if status?has_content>
                      ${status.get("description",locale)}
                      <#else>
                      N/A
                      </#if>
                  <#else>
                      <select name="expectedItemStatus_o_${rowCount}">
                          <#if (status?has_content)>
                              <option value="${status.statusId}">${status.get("description",locale)?if_exists}</option>
                              <option value="${status.statusId}">--</option>
                          </#if>
                          <#list itemStatus as returnItemStatus>
                              <option value="${returnItemStatus.statusId}">${returnItemStatus.get("description",locale)?if_exists}</option>
                          </#list>
                      </select>
                  </#if>
                  </div></td>
                <td><div>
                    <#if (readOnly)>
                        ${returnType.get("description",locale)?default("N/A")}
                    <#else>
                        <select name="returnTypeId_o_${rowCount}">
                            <#if (returnType?has_content)>
                                <option value="${returnType.returnTypeId}">${returnType.get("description",locale)?if_exists}</option>
                                <option value="${returnType.returnTypeId}">--</option>
                            </#if>
                            <#list returnTypes as returnTypeItem>
                                <option value="${returnTypeItem.returnTypeId}">${returnTypeItem.get("description",locale)?if_exists}</option>
                            </#list>
                        </select>
                    </#if></div></td>
                <#if (readOnly)>
                  <td>
                  <#if returnHeader.statusId == "RETURN_COMPLETED" || returnHeader.statusId == "SUP_RETURN_COMPLETED">
                    <#assign itemResp = item.getRelatedOne("ReturnItemResponse", false)?if_exists>
                    <#if itemResp?has_content>
                      <#if itemResp.paymentId?has_content>
                        <div>${uiLabelMap.AccountingPayment} ${uiLabelMap.CommonNbr}<a href="/accounting/control/paymentOverview?paymentId=${itemResp.paymentId}${externalKeyParam}" class="buttontext">${itemResp.paymentId}</a></div>
                      <#elseif itemResp.replacementOrderId?has_content>
                        <div>${uiLabelMap.OrderOrder} ${uiLabelMap.CommonNbr}<a href="<@ofbizUrl>orderview?orderId=${itemResp.replacementOrderId}</@ofbizUrl>" class="buttontext">${itemResp.replacementOrderId}</a></div>
                      <#elseif itemResp.billingAccountId?has_content>
                        <div>${uiLabelMap.AccountingAccountId} ${uiLabelMap.CommonNbr}<a href="/accounting/control/EditBillingAccount?billingAccountId=${itemResp.billingAccountId}${externalKeyParam}" class="buttontext">${itemResp.billingAccountId}</a></div>
                      </#if>
                    <#else>
                      <div>${uiLabelMap.CommonNone}</div>
                    </#if>
                  <#else>
                    <div>${uiLabelMap.CommonNA}</div>
                  </#if>
                </td>
                </#if>
                <#if returnHeader.statusId == "RETURN_REQUESTED" || returnHeader.statusId == "SUP_RETURN_REQUESTED">
                  <td align='right'><a href='javascript:document.removeReturnItem_${item_index}.submit()' class='buttontext'>${uiLabelMap.CommonRemove}</a></td>
                <#else>
                  <td>&nbsp;</td>
                </#if>
              </tr>
              <#assign rowCount = rowCount + 1>
              <#assign returnItemAdjustments = item.getRelated("ReturnAdjustment", null, null, false)>
              <#if (returnItemAdjustments?has_content)>
                  <#list returnItemAdjustments as returnItemAdjustment>
                     <@displayReturnAdjustment returnAdjustment=returnItemAdjustment adjEditable=false/>  <#-- adjustments of return items should never be editable -->
                  </#list>
              </#if>
              <#-- toggle the row color -->
              <#assign alt_row = !alt_row>
            </#list>
        <#else>
            <tr>
              <td colspan="11"><div>${uiLabelMap.OrderNoReturnItemsFound}</div></td>
            </tr>
        </#if>
        <#-- these are general return adjustments not associated with a particular item (itemSeqId = "_NA_" -->
        <#if (returnAdjustments?has_content)>
            <#list returnAdjustments as returnAdjustment>
                <#assign adjEditable = !readOnly> <#-- they are editable if the rest of the return items are -->
                <@displayReturnAdjustment returnAdjustment=returnAdjustment adjEditable=adjEditable/>
            </#list>
            </#if>
            <#-- show the return total -->
           
            <tr>
              <td colspan="2">${uiLabelMap.OrderReturnTotal}</td>
              <td colspan="9"><@ofbizCurrency amount=returnTotal isoCode=returnHeader.currencyUomId/></td>
            </tr>
            <#if (!readOnly) && (rowCount > 0)>
               <tr>
                  <td colspan="11" align="right">
                  	<input name="returnId" value="${returnHeader.returnId}" type="hidden" />
                  	<input name="_rowCount" value="${rowCount}" type="hidden" />
                  	<input type="submit" class="btn btn-primary btn-small" value="${uiLabelMap.CommonUpdate}"></input>
                  </td>
              </tr>
           </#if>
        </table>
        </div>
        </form>
        <#if returnItems?has_content>
          <#list returnItems as item>
          	<#if formAction?has_content>
          		<#assign action = "deleteReturnItem">
          	<#else>
          		<#assign action = "removeReturnItem">
          	</#if>
            <form name="removeReturnItem_${item_index}" method="post" action="<@ofbizUrl>${action}</@ofbizUrl>">
              <input type="hidden" name="returnId" value="${item.returnId}"/>
              <input type="hidden" name="returnItemSeqId" value="${item.returnItemSeqId}"/>
            </form>
          </#list>
        </#if>
        <#if returnAdjustments?has_content>
          <#list returnAdjustments as returnAdjustment>
            <form name="removeReturnAdjustment_${returnAdjustment_index}" method="post" action="<@ofbizUrl>removeReturnAdjustment</@ofbizUrl>">
              <input type="hidden" name="returnId" value="${returnAdjustment.returnId}"/>
              <input type="hidden" name="returnAdjustmentId" value="${returnAdjustment.returnAdjustmentId}"/>
            </form>
          </#list>
        </#if>
        <#if (returnHeader.statusId == "RETURN_REQUESTED" || returnHeader.statusId == "SUP_RETURN_REQUESTED") && (rowCount > 0)>
        <br />
        <form name="acceptReturn" method="post" action="<@ofbizUrl>/updateReturn</@ofbizUrl>">
          <#if returnHeader.returnHeaderTypeId?starts_with("CUSTOMER_")>
            <#assign statusId = "RETURN_ACCEPTED">
          <#else>
            <#assign statusId = "SUP_RETURN_ACCEPTED">
          </#if>
          <input type="hidden" name="returnId" value="${returnId}" />
          <input type="hidden" name="statusId" value="${statusId}" />
          <input type="hidden" name="needsInventoryReceive" value="${returnHeader.needsInventoryReceive!"N"}" />
          <div align="right"><input type="submit" value="${uiLabelMap.OrderReturnAccept}" class="btn btn-primary btn-small"/></div>
        </form>
        </#if>
<!-- if no requestParameters.orderId exists, then show list of items -->
	<#else>
        <#assign selectAllFormName = "returnItems"/>
        <form name="returnItems" method="post" action="<@ofbizUrl>createReturnItems</@ofbizUrl>">
          <input type="hidden" name="returnId" value="${returnId}" />
          <input type="hidden" name="_useRowSubmit" value="Y" />
          <#include "returnItemInc.ftl"/>
        </form>
</#if>
    </div>
    </div>
</div>
