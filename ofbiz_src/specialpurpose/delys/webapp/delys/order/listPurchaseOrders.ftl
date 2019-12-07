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

<script type="text/javascript">
    var checkBoxNameStart = "view";
    var formName = "findorder";
    function setCheckboxes() {
        // This would be clearer with camelCase variable names
        var allCheckbox = document.forms[formName].elements[checkBoxNameStart + "all"];
        for(i = 0;i < document.forms[formName].elements.length;i++) {
            var elem = document.forms[formName].elements[i];
            if (elem.name.indexOf(checkBoxNameStart) == 0 && elem.name.indexOf("_") < 0 && elem.type == "checkbox") {
                elem.checked = allCheckbox.checked;
            }
        }
    }

</script>

          <#assign viewIndex= state.getViewIndex() - 1/>
          <#assign viewSize= state.getViewSize()/>
          <#assign filterDate= filterDate?if_exists />


<#macro pagination>
	<form method="post" id="paging" style="margin: -12px !important" action="<@ofbizUrl>findPurcharseOrderDis</@ofbizUrl>">
    <table class="basic-table" cellspacing='0'>
         <tr>
        <td>
          <#if state.hasPrevious()>
          <#assign viewIndex= state.getViewIndex() - 1/>
          <#assign viewSize= state.getViewSize()/>
          <#assign filterDate= filterDate?if_exists />
            <a href="javascript:void(0)" onclick="paging.submit()" class="btn btn-primary btn-mini margin-left10"><i class="icon-arrow-left  icon-on-left"></i>&nbsp;${uiLabelMap.CommonPrevious}</a>
          </#if>
        </td>
        <td align="right">
          <#if state.hasNext()>
            <#assign viewIndex= state.getViewIndex() + 1/>
          	<#assign viewSize= state.getViewSize()/>
          	<#assign filterDate= filterDate?if_exists />                 
            <a href="javascript:void(0)" onclick="paging.submit()" class="btn btn-primary btn-mini margin-left10"><i class="icon-arrow-right  icon-on-right"></i>&nbsp;${uiLabelMap.CommonNext}</a>
          </#if>
        </td>
      </tr>
    </table>
    <input type="hidden" name="viewIndex" value="${viewIndex}" />
    <input type="hidden" name="viewSize" value="${viewSize}" />
    <input type="hidden" name="filterDate" value="${filterDate}" />
    </form>
</#macro>

<#-- order list -->
<#if hasPermission>
  <div id="findOrdersList" class="widget-box transparent no-bottom-border">   
  <#--<div class="widget-header">
  		<#if productStoreId == "1">
	        <h4>${uiLabelMap.OrderOrderList} ${uiLabelMap.for} ${uiLabelMap.allStore}</h4>
        <#else>
        	<h6>${uiLabelMap.OrderOrderList} ${uiLabelMap.for} ${storeName?if_exists} - ${productStoreId?if_exists}</h6>
        </#if>-->
       <#-- <div class="widget-toolbar">
       	<a href="#" data-action="collapse"><i class="icon-chevron-up"></i></a>
		</div>
    </div>-->
     <div class=" transparent no-bottom-border">
    <div class="widget-body-inner">
    <div class="widget-main">
    
        <table class="table table-striped table-bordered table-hover dataTable" cellspacing='0' style="width: 100%; margin-bottom: 2%;">
          <tr class="header-row">
            <td width="15%">${uiLabelMap.CommonDate}</td>
            <td width="10%">${uiLabelMap.OrderOrder} ${uiLabelMap.CommonNbr}</td>
            <td width="10%">${uiLabelMap.OrderOrderName}</td>
            <td width="10%">${uiLabelMap.OrderOrderType}</td>
            <td width="10%">${uiLabelMap.OrderOrderBillFromParty}</td>
            <td width="10%">${uiLabelMap.OrderOrderBillToParty}</td>
            <td width="10%">${uiLabelMap.OrderProductStore}</td>
            <td width="10%">${uiLabelMap.CommonAmount}</td>
            <td width="10%">${uiLabelMap.OrderTrackingCode}</td>
            <#if state.hasFilter('filterInventoryProblems') || state.hasFilter('filterAuthProblems') || state.hasFilter('filterPOsOpenPastTheirETA') || state.hasFilter('filterPOsWithRejectedItems') || state.hasFilter('filterPartiallyReceivedPOs')>
                <td width="10%">${uiLabelMap.CommonStatus}</td>
                <td width="5%">${uiLabelMap.CommonFilter}</td>
            <#else>
                <td colspan="2" width="15%">${uiLabelMap.CommonStatus}</td>
            </#if>
          </tr>
          <#list orderHeaderList as orderHeader>
            <#assign status = orderHeader.getRelatedOne("StatusItem", true)>
            <#assign orh = Static["org.ofbiz.order.order.OrderReadHelper"].getHelper(orderHeader)>
            <#assign billToParty = orh.getBillToParty()?if_exists>
            <#assign billFromParty = orh.getBillFromParty()?if_exists>
            <#if billToParty?has_content>
                <#assign billToPartyNameResult = dispatcher.runSync("getPartyNameForDate", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", billToParty.partyId, "compareDate", orderHeader.orderDate, "userLogin", userLogin))/>
                <#assign billTo = billToPartyNameResult.fullName?default("[${uiLabelMap.OrderPartyNameNotFound}]")/>
                <#-- <#assign billTo = Static["org.ofbiz.party.party.PartyHelper"].getPartyName(billToParty, true)?if_exists> -->
            <#else>
              <#assign billTo = ''/>
            </#if>
            <#if billFromParty?has_content>
              <#assign billFrom = Static["org.ofbiz.party.party.PartyHelper"].getPartyName(billFromParty, true)?if_exists>
            <#else>
              <#assign billFrom = ''/>
            </#if>
            <#assign productStore = orderHeader.getRelatedOne("ProductStore", true)?if_exists />
            <tr>
              <td><#if orderHeader.orderDate?has_content>${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(orderHeader.orderDate, "", locale, timeZone)!}</#if></td>
              <td>
                <a href="<@ofbizUrl>orderview?orderId=${orderHeader.orderId}</@ofbizUrl>" class="btn btn-primary btn-mini">${orderHeader.orderId}</a>
              </td>
              <td>${orderHeader.orderName?if_exists}</td>
              <td>${orderHeader.getRelatedOne("OrderType", true).get("description",locale)}</td>
              <td>${billFrom?if_exists}</td>
              <td>${billTo?if_exists}</td>
              <td><#if productStore?has_content>${productStore.storeName?default(productStore.productStoreId)}</#if></td>
              <td><@ofbizCurrency amount=orderHeader.grandTotal isoCode=orderHeader.currencyUom/></td>
              <td>
                <#assign trackingCodes = orderHeader.getRelated("TrackingCodeOrder", null, null, false)>
                <#if trackingCodes?has_content>
                <#list trackingCodes as trackingCode>
                    <#if trackingCode?has_content>
                        <a class="btn btn-primary btn-mini" href="/marketing/control/FindTrackingCodeOrders?trackingCodeId=${trackingCode.trackingCodeId}&amp;externalLoginKey=${requestAttributes.externalLoginKey?if_exists}">${trackingCode.trackingCodeId}</a><br />
                    </#if>
                </#list>
                </#if>
              </td>
              <td>${orderHeader.getRelatedOne("StatusItem", true).get("description",locale)}</td>
              <#if state.hasFilter('filterInventoryProblems') || state.hasFilter('filterAuthProblems') || state.hasFilter('filterPOsOpenPastTheirETA') || state.hasFilter('filterPOsWithRejectedItems') || state.hasFilter('filterPartiallyReceivedPOs')>
              <td>
                  <#if filterInventoryProblems.contains(orderHeader.orderId)>
                    Inv&nbsp;
                  </#if>
                  <#if filterAuthProblems.contains(orderHeader.orderId)>
                   Aut&nbsp;
                  </#if>
                  <#if filterPOsOpenPastTheirETA.contains(orderHeader.orderId)>
                    ETA&nbsp;
                  </#if>
                  <#if filterPOsWithRejectedItems.contains(orderHeader.orderId)>
                    Rej&nbsp;
                  </#if>
                  <#if filterPartiallyReceivedPOs.contains(orderHeader.orderId)>
                    Part&nbsp;
                  </#if>
              </td>
              <#else>
              <td>&nbsp;</td>
              </#if>
            </tr>
          </#list>
          <#if !orderHeaderList?has_content>
            <tr><td colspan="10"><p class="alert alert-info">${uiLabelMap.OrderNoOrderFound}</p></td></tr>
          </#if>
        </table>
        <@pagination/>
    </div>
    </div>
    </div>
  </div>
<#else>
  <div><p class="alert alert-danger">${uiLabelMap.OrderViewPermissionError}</p></div>
</#if>

