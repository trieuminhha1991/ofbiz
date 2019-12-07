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
<#--
    <div class="row-fluid margin-top-nav-14">
	    <ul class="unstyled spaced">
	      <#if isLastStep == "N">
	        <li style="display: inline-block"><a class="btn btn-mini btn-primary" href="javascript:document.checkoutsetupform.submit();">${uiLabelMap.CommonContinue}</a></li>
	      <#else>
	        <li style="display: inline-block"><a class="btn btn-mini btn-primary" href="<@ofbizUrl>processorder</@ofbizUrl>">${uiLabelMap.OrderCreateOrder}</a></li>
	      </#if>
	
	      <#list checkoutSteps?reverse as checkoutStep>
	        <#assign stepUiLabel = uiLabelMap.get(checkoutStep.label)>
	        <#if checkoutStep.enabled == "N">
	            <li style="display: inline-block" class="disabled">
	            	<a href="#" class="btn btn-mini dislink">${stepUiLabel}</a>
            	</li>
	        <#else>
	        	 <li style="display: inline-block">
	        	 	<a class="btn btn-mini btn-primary" href="<@ofbizUrl>${checkoutStep.uri}</@ofbizUrl>">${stepUiLabel}</a>
    	 		</li>
	        </#if>
	      </#list>
	    </ul>
  </div>
-->
<div class="widget-box transparent">
  	<div class="widget-header">
	  	<h3>
			<#if shoppingCart.getOrderType() == "PURCHASE_ORDER">
	            ${uiLabelMap.OrderPurchaseOrder}:
	   		<#else>
	            ${uiLabelMap.OrderSalesOrder}:
	    	</#if>
	        ${stepTitle?if_exists}
	    </h3>
	    <div class="widget-toolbar none-content pull-right">
	      	<#list checkoutSteps?reverse as checkoutStep>
	        	<#assign stepUiLabel = uiLabelMap.get(checkoutStep.label)>
	        	<#if checkoutStep.enabled == "N">
	            	<a href="#" class="dislink"><i class="${checkoutStep.icon} open-sans"></i>${stepUiLabel}</a>
	        	<#else>
	    	 		<a href="<@ofbizUrl>${checkoutStep.uri}</@ofbizUrl>">
	    	 			<i class="${checkoutStep.icon} open-sans" style="padding-right:5px; padding-left:3px"></i>
	    	 			${stepUiLabel}
    	 			</a>
	        	</#if>
	      	</#list>
	      	<#if isLastStep == "N">
	        	<a href="javascript:document.checkoutsetupform.submit();">${uiLabelMap.CommonContinue}</a>
	      	<#else>
	        	<a href="#" onClick="javascript:processOrder();"><i class="icon-ok open-sans" style="padding-right:0;width: auto;margin-right: 0;"></i>${uiLabelMap.OrderCreateOrder}</a>
	      	</#if>
	    </div>
	</div>
</div>
<script type="text/javascript" src="/delys/images/js/bootbox.min.js"></script>
<script type="text/javascript">
	function processOrder() {
		bootbox.confirm("${uiLabelMap.DAAreYouSureCreate}", function(result){
			if(result){
				window.location.href = "<@ofbizUrl>processOrder</@ofbizUrl>";
			}
		});
	}
</script>