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
<#if requestAttributes.uiLabelMap?exists>
<#assign uiLabelMap = requestAttributes.uiLabelMap>
</#if>
<#assign selected = tabButtonItem?default("void")>
<#if shipmentId?has_content>
    <div class="tabbable">
        <ul class="nav nav-tabs">
            <li<#if selected="DelysViewShipment"> class="selected"</#if>><a  href="<@ofbizUrl>DelysViewShipment?shipmentId=${shipmentId}</@ofbizUrl>">${uiLabelMap.CommonView}</a></li>
            <li<#if selected="DelysEditShipment"> class="selected"</#if>><a  href="<@ofbizUrl>DelysEditShipment?shipmentId=${shipmentId}</@ofbizUrl>">${uiLabelMap.CommonEdit}</a></li>
        <#if (shipment.shipmentTypeId)?exists && shipment.shipmentTypeId = "PURCHASE_RETURN">
            <li<#if selected="DelysAddItemsFromInventory"> class="selected"</#if>><a  href="<@ofbizUrl>DelysAddItemsFromInventory?shipmentId=${shipmentId}</@ofbizUrl>">${uiLabelMap.ProductOrderItems}</a></li>
        </#if>
        <#if (shipment.shipmentTypeId)?exists && shipment.shipmentTypeId = "SALES_SHIPMENT">
            <li<#if selected="DelysEditShipmentPlan"> class="selected"</#if>><a  href="<@ofbizUrl>editShipmentPlan?shipmentId=${shipmentId}</@ofbizUrl>">${uiLabelMap.ProductShipmentPlan}</a></li>
            <li<#if selected="DelysAddItemsFromOrder"> class="selected"</#if>><a  href="<@ofbizUrl>DelysAddItemsFromOrder?shipmentId=${shipmentId}</@ofbizUrl>">${uiLabelMap.ProductOrderItems}</a></li>
            <li<#if selected="DelysEditShipmentItems"> class="selected"</#if>><a  href="<@ofbizUrl>DelysEditShipmentItems?shipmentId=${shipmentId}</@ofbizUrl>">${uiLabelMap.ProductItems}</a></li>
            <li<#if selected="DelysEditShipmentPackages"> class="selected"</#if>><a  href="<@ofbizUrl>DelysEditShipmentPackages?shipmentId=${shipmentId}</@ofbizUrl>">${uiLabelMap.ProductPackages}</a></li>
            <li<#if selected="DelysEditShipmentRouteSegments"> class="selected"</#if>><a  href="<@ofbizUrl>DelysEditShipmentRouteSegments?shipmentId=${shipmentId}</@ofbizUrl>">${uiLabelMap.ProductRouteSegments}</a></li>
        </#if>
        <#if (shipment.shipmentTypeId)?exists && shipment.shipmentTypeId='PURCHASE_SHIPMENT'>
            <li<#if selected="DelysEditShipmentPlan"> class="selected"</#if>><a  href="<@ofbizUrl>DelysEditShipmentPlan?shipmentId=${shipmentId}</@ofbizUrl>">${uiLabelMap.ProductOrderItems}</a></li>
            <li<#if selected="DelysViewShipmentReceipts"> class="selected"</#if>><a  href="<@ofbizUrl>DelysViewShipmentReceipts?shipmentId=${shipmentId}</@ofbizUrl>">${uiLabelMap.ProductShipmentReceipts}</a></li>
        </#if>
        </ul>
        <br />
    </div>
</#if>
