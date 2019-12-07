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

<#if security.hasEntityPermission("ORDERMGR", "_VIEW", session)>
  <div class="widget-box olbius-extra">
    <div class="widget-box transparent no-bottom-border">
 
    <div class="widget-header">
     <h3>${uiLabelMap.OrderOrderStatisticsPage}</h3>
    </div>
    
    </div>
    <div >
    <div class="widget-body-inner">
    <div class="widget-main">
        <table class="table dataTable table-striped table-hover table-bordered" cellspacing='0'>
          <tr >
            <td>&nbsp;</td>
            <td align="right">${uiLabelMap.CommonToday}</td>
            <td align="right">${uiLabelMap.OrderWTD}</td>
            <td align="right">${uiLabelMap.OrderMTD}</td>
            <td align="right">${uiLabelMap.OrderYTD}</td>
          </tr>
          <tr>
            <td colspan="5" class="blue"><h5>${uiLabelMap.OrderOrdersTotals}</h5></td>
          </tr>
          <tr>
            <td>${uiLabelMap.OrderGrossDollarAmountsIncludesAdjustmentsAndPendingOrders}</td>
            <td align="right">${dayItemTotal}</td>
            <td align="right">${weekItemTotal}</td>
            <td align="right">${monthItemTotal}</td>
            <td align="right">${yearItemTotal}</td>
          </tr>
          <tr class="alternate-row">
            <td>${uiLabelMap.OrderPaidDollarAmountsIncludesAdjustments}</td>
            <td align="right">${dayItemTotalPaid}</td>
            <td align="right">${weekItemTotalPaid}</td>
            <td align="right">${monthItemTotalPaid}</td>
            <td align="right">${yearItemTotalPaid}</td>
          </tr>
          <tr>
            <td>${uiLabelMap.OrderPendingPaymentDollarAmountsIncludesAdjustments}</td>
            <td align="right">${dayItemTotalPending}</td>
            <td align="right">${weekItemTotalPending}</td>
            <td align="right">${monthItemTotalPending}</td>
            <td align="right">${yearItemTotalPending}</td>
          </tr>
          <tr>
            <td colspan="5" ></td>
          </tr>
          <tr>
            <td colspan="5" class="blue"><h5>${uiLabelMap.OrderOrdersItemCounts}</h5></td>
          </tr>
          <tr class="alternate-row">
            <td>${uiLabelMap.OrderGrossItemsSoldIncludesPromotionsAndPendingOrders}</td>
            <td align="right">${dayItemCount?string.number}</td>
            <td align="right">${weekItemCount?string.number}</td>
            <td align="right">${monthItemCount?string.number}</td>
            <td align="right">${yearItemCount?string.number}</td>
          </tr>
          <tr>
            <td>${uiLabelMap.OrderPaidItemsSoldIncludesPromotions}</td>
            <td align="right">${dayItemCountPaid?string.number}</td>
            <td align="right">${weekItemCountPaid?string.number}</td>
            <td align="right">${monthItemCountPaid?string.number}</td>
            <td align="right">${yearItemCountPaid?string.number}</td>
          </tr>
          <tr class="alternate-row">
            <td>${uiLabelMap.OrderPendingPaymentItemsSoldIncludesPromotions}</td>
            <td align="right">${dayItemCountPending?string.number}</td>
            <td align="right">${weekItemCountPending?string.number}</td>
            <td align="right">${monthItemCountPending?string.number}</td>
            <td align="right">${yearItemCountPending?string.number}</td>
          </tr>
          <tr>
            <td colspan="5" ></td>
          </tr>
          <tr>
            <td colspan="5" class="blue"><h5>${uiLabelMap.OrderOrdersPending}</h5></td>
          </tr>
          <tr>
            <td>${uiLabelMap.OrderWaitingPayment}</td>
            <td align="right">${waitingPayment?default(0)?string.number}</td>
            <td align="right">--</td>
            <td align="right">--</td>
            <td align="right">--</td>
          </tr>
          <tr class="alternate-row">
            <td>${uiLabelMap.OrderWaitingApproval}</td>
            <td align="right">${waitingApproval?default(0)?string.number}</td>
            <td align="right">--</td>
            <td align="right">--</td>
            <td align="right">--</td>
          </tr>
          <tr>
            <td>${uiLabelMap.OrderWaitingCompletion}</td>
            <td align="right">${waitingComplete?default(0)?string.number}</td>
            <td align="right">--</td>
            <td align="right">--</td>
            <td align="right">--</td>
          </tr>
          <tr>
            <td colspan="5" ></td>
          </tr>
          <tr>
            <td colspan="5" class="blue"><h5>${uiLabelMap.OrderStatusChanges}</h5></td>
          </tr>
          <tr class="alternate-row">
            <td>${uiLabelMap.OrderCreated}</td>
            <td align="right">${dayOrder?size?default(0)?string.number}</td>
            <td align="right">${weekOrder?size?default(0)?string.number}</td>
            <td align="right">${monthOrder?size?default(0)?string.number}</td>
            <td align="right">${yearOrder?size?default(0)?string.number}</td>
          </tr>
          <tr>
            <td>${uiLabelMap.OrderApproved}</td>
            <td align="right">${dayApprove?size?default(0)?string.number}</td>
            <td align="right">${weekApprove?size?default(0)?string.number}</td>
            <td align="right">${monthApprove?size?default(0)?string.number}</td>
            <td align="right">${yearApprove?size?default(0)?string.number}</td>
          </tr>
          <tr class="alternate-row">
            <td>${uiLabelMap.OrderCompleted}</td>
            <td align="right">${dayComplete?size?default(0)?string.number}</td>
            <td align="right">${weekComplete?size?default(0)?string.number}</td>
            <td align="right">${monthComplete?size?default(0)?string.number}</td>
            <td align="right">${yearComplete?size?default(0)?string.number}</td>
          </tr>
          <tr>
            <td>${uiLabelMap.OrderCancelled}</td>
            <td align="right">${dayCancelled?size?default(0)?string.number}</td>
            <td align="right">${weekCancelled?size?default(0)?string.number}</td>
            <td align="right">${monthCancelled?size?default(0)?string.number}</td>
            <td align="right">${yearCancelled?size?default(0)?string.number}</td>
          </tr>
          <tr class="alternate-row">
            <td>${uiLabelMap.OrderRejected}</td>
            <td align="right">${dayRejected?size?default(0)?string.number}</td>
            <td align="right">${weekRejected?size?default(0)?string.number}</td>
            <td align="right">${monthRejected?size?default(0)?string.number}</td>
            <td align="right">${yearRejected?size?default(0)?string.number}</td>
          </tr>
        </table>
    </div>
    </div>
    </div>
</div>
<#else>
  <h3>${uiLabelMap.OrderViewPermissionError}</h3>
</#if>
