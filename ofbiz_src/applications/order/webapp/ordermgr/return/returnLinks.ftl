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
<#assign selected = tabButtonItem?default("void")>
<#if returnHeader?exists>
			  <div class="button-bar tab-bar" style="float:right">
			      	<a style="padding-right:20px;font-size:15px;text-decoration:none" class="icon-header open-sans" style="font-size:15px"<#if selected="OrderReturnHeader"> class="selected"</#if>  href="<@ofbizUrl>returnMain?returnId=${returnId?if_exists}</@ofbizUrl>">${uiLabelMap.OrderReturnHeader}</a>
				  	<a style="padding-right:20px;font-size:15px;text-decoration:none" class="icon-list open-sans"<#if selected="OrderReturnItems"> class="selected"</#if> href="<@ofbizUrl>returnItems?returnId=${returnId?if_exists}</@ofbizUrl>">${uiLabelMap.OrderReturnItems}</a>
				 	<a style="padding-right:20px;font-size:15px;text-decoration:none" class="icon-history open-sans"<#if selected="OrderReturnHistory"> class="selected"</#if>  href="<@ofbizUrl>ReturnHistory?returnId=${returnId?if_exists}</@ofbizUrl>">${uiLabelMap.OrderReturnHistory}</a>
				 	<a style="padding-right:20px;font-size:15px;text-decoration:none" class="btn-pdf open-sans"   href="<@ofbizUrl>return.pdf?returnId=${returnId?if_exists}</@ofbizUrl>" target="_BLANK" >PDF</a>
			  </div>

  <#if selected != "OrderReturnHistory">
    <div class="button-bar button-style-1">
    
      <#if returnId?exists>
        <#assign returnItems = delegator.findByAnd("ReturnItem", Static["org.ofbiz.base.util.UtilMisc"].toMap("returnId", returnId, "returnTypeId", "RTN_REFUND"), null, false)/>
        <#if returnItems?has_content>
          <#assign orderId = (Static["org.ofbiz.entity.util.EntityUtil"].getFirst(returnItems)).getString("orderId")/>
          <#assign partyId = "${(returnHeader.fromPartyId)?if_exists}"/>
          <a href="<@ofbizUrl>setOrderCurrencyAgreementShipDates?partyId=${partyId?if_exists}&amp;originOrderId=${orderId?if_exists}</@ofbizUrl>" class="btn btn-info btn-mini">${uiLabelMap.OrderCreateExchangeOrder} ${uiLabelMap.CommonFor} ${orderId?if_exists}</a>
        </#if>
        <#if "RETURN_ACCEPTED" == returnHeader.statusId>
          <#assign returnItems = delegator.findByAnd("ReturnItem", {"returnId" : returnId}, null, false)/>
          <#if returnItems?has_content>
            <#assign orderId = (Static["org.ofbiz.entity.util.EntityUtil"].getFirst(returnItems)).getString("orderId")/>
            <#assign shipGroupAssoc = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(delegator.findByAnd("OrderItemShipGroupAssoc", {"orderId" : orderId}, null, false))/>
            <#assign shipGroup = delegator.findOne("OrderItemShipGroup", {"orderId" : orderId, "shipGroupSeqId" : shipGroupAssoc.shipGroupSeqId}, false)>
            <#if shipGroup?exists && shipGroup.shipmentMethodTypeId?has_content && shipGroup.shipmentMethodTypeId != "NO_SHIPPING">
              <#assign shipGroupShipment = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(delegator.findByAnd("Shipment", {"primaryOrderId" : shipGroup.orderId, "primaryShipGroupSeqId" : shipGroup.shipGroupSeqId}, null, false))/>
              <#if shipGroupShipment?exists>
                <#assign shipmentRouteSegment = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(delegator.findByAnd("ShipmentRouteSegment", {"shipmentId" : shipGroupShipment.shipmentId}, null, false))>
                <#if shipmentRouteSegment?exists>
                  <#if shipmentRouteSegment.carrierPartyId?has_content && "UPS" == shipmentRouteSegment.carrierPartyId>
                    <li><a href="javascript:document.upsEmailReturnLabel.submit();" class="buttontext">${uiLabelMap.ProductEmailReturnShippingLabelUPS}</a></li>
                    <li><form name="upsEmailReturnLabel" method="post" action="<@ofbizUrl>upsEmailReturnLabelReturn</@ofbizUrl>">
                      <input type="hidden" name="returnId" value="${returnId}"/>
                      <input type="hidden" name="shipmentId" value="${shipGroupShipment.shipmentId}"/>
                      <input type="hidden" name="shipmentRouteSegmentId" value="${shipmentRouteSegment.shipmentRouteSegmentId}" />
                    </form></li>
                  </#if>
                </#if>
              </#if>
            </#if>
          </#if>
        </#if>
      </#if>
    </div>
  </#if>
<#else>
  <!--<h3 class="header smaller lighter blue">${uiLabelMap.OrderCreateNewReturn}</h3>-->
  <#if requestParameters.returnId?has_content>
    <h3 class="header smaller lighter blue">${uiLabelMap.OrderNoReturnFoundWithId} : ${requestParameters.returnId}</h3>
  </#if>
  <br />
</#if>
