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

<script language="JavaScript" type="text/javascript">
    function paginateOrderList(viewSize, viewIndex) {
        document.paginationForm.viewSize.value = viewSize;
        documentas.paginationForm.viewIndex.value = viewIndex;
        document.paginationForm.submit();
    }
</script>

<div class="widget-box transparent no-bottom-border">
  <div class="widget-header">
      <h3>${uiLabelMap.OrderOrderFound}</h3>
      <span class="widget-toolbar none-content">
    	<a href="<@ofbizUrl>EditProduct</@ofbizUrl>" class="icon-plus-sign open-sans">${uiLabelMap.ProductNewProduct}</a>
    	</span>
      <span class="widget-toolbar">
      <#if (orderList?has_content && 0 < orderList?size)>
        <#if (orderListSize > highIndex)>
          <a class="btn btn-mini btn-info" href="javascript:paginateOrderList('${viewSize}', '${viewIndex+1}')">${uiLabelMap.CommonNext}</a>
        <#else>
          <span class="disabled">${uiLabelMap.CommonNext}</span>
        </#if>
        <#if (orderListSize > 0)>
          <span>${lowIndex} - ${highIndex} ${uiLabelMap.CommonOf} ${orderListSize}</span>
        </#if>
        <#if (viewIndex > 1)>
          <a class="btn btn-mini btn-info" href="javascript:paginateOrderList('${viewSize}', '${viewIndex-1}')">${uiLabelMap.CommonPrevious}</a>
        <#else>
          <span class="disabled">${uiLabelMap.CommonPrevious}</span>
        </#if>
      </#if>
      </span>
  </div>
  <div class="widget-body">
    <form name="paginationForm" method="post" style="margin:0px;" action="<@ofbizUrl>viewProductOrder</@ofbizUrl>">
      <input type="hidden" name="viewSize"/>
      <input type="hidden" name="viewIndex"/>
      <#if paramIdList?exists && paramIdList?has_content>
        <#list paramIdList as paramIds>
          <#assign paramId = paramIds.split("=")/>
          <#if "productId" == paramId[0]>
            <#assign productId = paramId[1]/>
          </#if>
          <input type="hidden" name="${paramId[0]}" value="${paramId[1]}"/>
        </#list>
      </#if>
    </form>
    <table class="table table-hover table-bordered table-striped dataTable" cellspacing='0'>
      <tr class="header-row">
        <td>${uiLabelMap.OrderOrderId}</td>
        <td>${uiLabelMap.FormFieldTitle_itemStatusId}</td>
        <td>${uiLabelMap.FormFieldTitle_orderItemSeqId}</td>
        <td>${uiLabelMap.OrderDate}</td>
        <td>${uiLabelMap.OrderUnitPrice}</td>
        <td>${uiLabelMap.OrderQuantity}</td>
        <td>${uiLabelMap.OrderOrderType}</td>
      </tr>
      <#if orderList?has_content && productId?exists>
        <#list orderList as order>
          <#assign orderItems = delegator.findByAnd("OrderItem", {"orderId" : order.orderId, "productId" : productId}, null, false)/>
          <#list orderItems as orderItem>
            <tr>
              <td><a href="/ordermgr/control/orderview?orderId=${orderItem.orderId}" class='buttontext'>${orderItem.orderId}</a></td>
              <#assign currentItemStatus = orderItem.getRelatedOne("StatusItem", false)/>
              <td>${currentItemStatus.get("description",locale)?default(currentItemStatus.statusId)}</td>
              <td>${orderItem.orderItemSeqId}</td>
              <td>${order.orderDate}</td>
              <td>${orderItem.unitPrice}</td>
              <td>${orderItem.quantity}</td>
              <#assign currentOrderType = order.getRelatedOne("OrderType", false)/>
              <td>${currentOrderType.get("description",locale)?default(currentOrderType.orderTypeId)}</td>
            </tr>
          </#list>
        </#list>
      <#else>
        <tr>
          <td colspan='7' style="padding: 0px;"><p class="alert alert-info">${uiLabelMap.OrderNoOrderFound}</p></td>
        </tr>
      </#if>
    </table>
  </div>
</div>
