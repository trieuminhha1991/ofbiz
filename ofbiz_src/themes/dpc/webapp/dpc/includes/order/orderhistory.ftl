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

<div>
  <div class="cartdiv">
    <h2>${uiLabelMap.OrderSalesHistory}</h2>
    <table id="orderSalesHistory" summary="This table display order sales history.">
      <thead>
	        <tr>
	          <td>${uiLabelMap.CommonDate}</td>
	          <td>${uiLabelMap.OrderOrder} ${uiLabelMap.CommonNbr}</td>
	          <td>${uiLabelMap.CommonAmount}</td>
	          <td>${uiLabelMap.CommonStatus}</td>
	          <td>${uiLabelMap.OrderInvoices}</td>
	          <td></td>
	        </tr>
      </thead>
      <tbody>
        <#if orderHeaderList?has_content>
          <#list orderHeaderList as orderHeader>
            <#assign status = orderHeader.getRelatedOne("StatusItem", true) />
            <tr>
              <td>${orderHeader.orderDate.toString()}</td>
              <td>${orderHeader.orderId}</td>
              <td><@ofbizCurrency amount=orderHeader.grandTotal isoCode=orderHeader.currencyUom /></td>
              <td>${status.get("description",locale)}</td>
              <#-- invoices -->
              <#assign invoices = delegator.findByAnd("OrderItemBilling", Static["org.ofbiz.base.util.UtilMisc"].toMap("orderId", "${orderHeader.orderId}"), Static["org.ofbiz.base.util.UtilMisc"].toList("invoiceId"), false) />
              <#assign distinctInvoiceIds = Static["org.ofbiz.entity.util.EntityUtil"].getFieldListFromEntityList(invoices, "invoiceId", true)>
              <#if distinctInvoiceIds?has_content>
                <td>
                  <#list distinctInvoiceIds as invoiceId>
                     <a href="<@ofbizUrl>invoice.pdf?invoiceId=${invoiceId}</@ofbizUrl>" class="buttontext">(${invoiceId} PDF) </a>
                  </#list>
                </td>
              <#else>
                <td></td>
              </#if>
              <td><a href="<@ofbizUrl>orderstatus?orderId=${orderHeader.orderId}</@ofbizUrl>" class="button">${uiLabelMap.CommonView}</a></td>
            </tr>
          </#list>
        <#else>
          <tr><td colspan="6">${uiLabelMap.OrderNoOrderFound}</td></tr>
        </#if>
      </tbody>
    </table>
  </div>
  <div class="cartdiv">
    <h2>${uiLabelMap.OrderPurchaseHistory}</h2>
    <table id="orderPurchaseHistory" summary="This table display order purchase history.">
      <thead>
        <tr>
          <td>${uiLabelMap.CommonDate}</td>
          <td>${uiLabelMap.OrderOrder} ${uiLabelMap.CommonNbr}</td>
          <td>${uiLabelMap.CommonAmount}</td>
          <td>${uiLabelMap.CommonStatus}</td>
          <td></td>
        </tr>
      </thead>
      <tbody>
        <#if porderHeaderList?has_content>
          <#list porderHeaderList as porderHeader>
            <#assign pstatus = porderHeader.getRelatedOne("StatusItem", true) />
            <tr>
              <td>${porderHeader.orderDate.toString()}</td>
              <td>${porderHeader.orderId}</td>
              <td><@ofbizCurrency amount=porderHeader.grandTotal isoCode=porderHeader.currencyUom /></td>
              <td>${pstatus.get("description",locale)}</td>
              <td><a href="<@ofbizUrl>orderstatus?orderId=${porderHeader.orderId}</@ofbizUrl>" class="button">${uiLabelMap.CommonView}</a></td>
            </tr>
          </#list>
        <#else>
          <tr><td colspan="5">${uiLabelMap.OrderNoOrderFound}</td></tr>
        </#if>
      </tbody>
    </table>
  </div>
  <div class="cartdiv">
    <h2>${uiLabelMap.EcommerceDownloadsAvailableTitle}</h2>
    <table id="availableTitleDownload" summary="This table display available title for download.">
      <thead>
        <tr>
          <td>${uiLabelMap.OrderOrder} ${uiLabelMap.CommonNbr}</td>
          <td>${uiLabelMap.ProductProductName}</td>
          <td>${uiLabelMap.CommonName}</td>
          <td>${uiLabelMap.CommonDescription}</td>
          <td></td>
        </tr>
      </thead>
      <tbody>
        <#if downloadOrderRoleAndProductContentInfoList?has_content>
          <#list downloadOrderRoleAndProductContentInfoList as downloadOrderRoleAndProductContentInfo>
            <tr>
              <td>${downloadOrderRoleAndProductContentInfo.orderId}</td>
              <td>${downloadOrderRoleAndProductContentInfo.productName}</td>
              <td>${downloadOrderRoleAndProductContentInfo.contentName?if_exists}</td>
              <td>${downloadOrderRoleAndProductContentInfo.description?if_exists}</td>
              <td>
                <a href="<@ofbizUrl>downloadDigitalProduct?dataResourceId=${downloadOrderRoleAndProductContentInfo.dataResourceId}</@ofbizUrl>" class="button">Download</a>
              </td>
            </tr>
          </#list>
        <#else>
          <tr><td colspan="5">${uiLabelMap.EcommerceDownloadNotFound}</td></tr>
        </#if>
      </tbody>
    </table>
  </div>
</div>
