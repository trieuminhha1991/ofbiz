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

<#if stepTitleId?exists>
    <#assign stepTitle = uiLabelMap.get(stepTitleId)>
</#if>
    <div class="row-fluid margin-top-nav-14">
	    <ul class="unstyled spaced">
	      <#if isLastStep == "N">
	        <li style="display: inline-block"><a id="continue" class="btn btn-mini btn-primary" href="javascript:document.checkoutsetupform.submit();">${uiLabelMap.CommonContinue}</a></li>
	      <#else>
	        <li style="display: inline-block"><a id="createOrder" class="btn btn-mini btn-primary" href="<@ofbizUrl>processorder</@ofbizUrl>">${uiLabelMap.OrderCreateOrder}</a></li>
	      </#if>
	
	      <#list checkoutSteps?reverse as checkoutStep>
	        <#assign stepUiLabel = uiLabelMap.get(checkoutStep.label)>
	        <#if checkoutStep.enabled == "N">
	            <li style="display: inline-block" class="disabled"><a href="#" class="btn btn-mini dislink">${stepUiLabel}</a></li>
	        <#else>
	        	 <li style="display: inline-block"><a class="btn btn-mini btn-primary" href="<@ofbizUrl>${checkoutStep.uri}</@ofbizUrl>">${stepUiLabel}</a></li>
	        </#if>
	      </#list>
	    </ul>
  </div>
  <div class="screenlet">
  	<div class="screenlet-title-bar">
  	<h3 class="header smaller lighter blue">
  			<#if shoppingCart.getOrderType() == "PURCHASE_ORDER">
            ${uiLabelMap.OrderPurchaseOrder}:
       		<#else>
            ${uiLabelMap.OrderSalesOrder}:
        	</#if>
        ${stepTitle?if_exists}</h3>
  	</div>
</div>
