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
<div class="jm-position wrap clearfix">
	<div class="main">
		<div class="inner clearfix">
		  <div class="screenlet">
		    <h3>${uiLabelMap.OrderSalesHistory}</h3>
		    <div class='scroll-mobile'>
		    <table id="orderSalesHistory" summary="This table display order sales history." class="table1">
		      <thead>
		        <tr>
		          <th>${uiLabelMap.CommonDate}</th>
		          <th>${uiLabelMap.BEOrderNumber}</th>
		          <th>${uiLabelMap.BETotalValue}</th>
		          <th>${uiLabelMap.CommonStatus}</th>
		          <th>${uiLabelMap.OrderInvoices}</th>
		          <th>${uiLabelMap.CommonView}</th>
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
		  </div>
		  <div class="screenlet">
		    <h3>${uiLabelMap.OrderPurchaseHistory}</h3>
		    <div class='scroll-mobile'>
		    <table id="orderPurchaseHistory" summary="This table display order purchase history." class="table1">
		      <thead>
		        <tr>
		          <th>${uiLabelMap.CommonDate}</th>
		          <th>${uiLabelMap.BEOrderNumber}</th>
		          <th>${uiLabelMap.BETotalValue}</th>
		          <th>${uiLabelMap.CommonStatus}</th>
		          <th>${uiLabelMap.CommonView}</th>
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
		  </div>
		  <div class="screenlet">
		    <h3>${uiLabelMap.EcommerceDownloadsAvailableTitle}</h3>
		    <div class='scroll-mobile'>
		    <table id="availableTitleDownload" summary="This table display available title for download." class="table1">
		      <thead>
		        <tr>
		          <th>${uiLabelMap.BEOrderNumber}</th>
		          <th>${uiLabelMap.ProductProductName}</th>
		          <th>${uiLabelMap.CommonName}</th>
		          <th>${uiLabelMap.CommonDescription}</th>
		          <th>${uiLabelMap.CommonView}</th>
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
		</div>
	</div>
</div>
