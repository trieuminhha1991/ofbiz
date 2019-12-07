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

<div class="widget-box olbius-extra transparent no-border-bottom">
<div class="widget-header widget-header-small header-color-blue2">
	<h4>
	    <#if orderHeader?has_content>
	    	${uiLabelMap.PageTitleLookupBulkAddProduct}
	    <#else>
	        ${uiLabelMap.CommonCreate}&nbsp;
	        <#if shoppingCart.getOrderType() == "PURCHASE_ORDER">
	            ${uiLabelMap.OrderPurchaseOrder}
	        <#else>
	            ${uiLabelMap.OrderSalesOrder}
	        </#if>
	    </#if>
    </h4>
    <!-- <a class="btn btn-mini btn-info" href="<@ofbizUrl>orderentry</@ofbizUrl>">${uiLabelMap.OrderOrderItems}</a> -->
</div>
</div>
