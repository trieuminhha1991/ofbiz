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
   	<ul class="unstyled spaced">
		<li style="display: inline-block;"><a class="btn btn-mini btn-primary" href="<@ofbizUrl>emptyCart</@ofbizUrl>">${uiLabelMap.DAClearOrder}</a></li>
      	<#if (shoppingCart.size() > 0)>
      		<li style="display: inline-block;"><a class="btn btn-mini btn-primary" href="javascript:removeSelected();">${uiLabelMap.DARemoveSelected}</a></li>
	        <li style="display: inline-block;"><a class="btn btn-mini btn-primary" href="javascript:document.cartform.submit()">${uiLabelMap.DARecalculateOrder}</a></li>
      	<#else>
      		<li style="display: inline-block;" class="disabled"><a class="btn btn-mini dislink" href="#">${uiLabelMap.DARemoveSelected}</a></li>
	        <li style="display: inline-block;" class="disabled"><a class="btn btn-mini dislink" href="#">${uiLabelMap.DARecalculateOrder}</a></li>
      	</#if>
     	<#if shoppingCart.getOrderType() == "PURCHASE_ORDER">
	        <#if shoppingCart.getOrderPartyId() == "_NA_" || (shoppingCart.size() = 0)>
	          <li style="display: inline-block;"><a class="btn btn-mini dislink" href="#">${uiLabelMap.DAFinalizeOrder}</a></li>
	        <#else>
	           <li style="display: inline-block;"><a class="btn btn-mini btn-primary" href="<@ofbizUrl>finalizeOrder?finalizeMode=purchase&amp;finalizeReqCustInfo=false&amp;finalizeReqShipInfo=false&amp;finalizeReqOptions=false&amp;finalizeReqPayInfo=false</@ofbizUrl>">${uiLabelMap.OrderFinalizeOrder}</a></li>
	        </#if>
      	<#else>
	        <#if shoppingCart.size() = 0>
	        	<li style="display: inline-block;" class="disabled"><a class="btn btn-mini dislink" href="#">${uiLabelMap.DAFinalizeOrder}</a></li>
	          	<#--<li style="display: inline-block;" class="disabled"><a class="btn btn-mini dislink" href="#">${uiLabelMap.DAFinalizeOrderDefault}</a></li>
	          	<li style="display: inline-block;"><a class="btn btn-mini btn-primary" class="dislink btn btn-mini btn-primary" href="<@ofbizUrl>quickCheckout</@ofbizUrl>">${uiLabelMap.DAQuickFinalizeOrder}</a></li>
	          	-->
	        <#else>
	          	<li style="display: inline-block;"><a class="btn btn-mini btn-primary" href="<@ofbizUrl>quickCheckout</@ofbizUrl>">${uiLabelMap.DAQuickFinalizeOrder}</a></li>
	          	<#--<li style="display: inline-block;"><a class="btn btn-mini btn-primary" href="<@ofbizUrl>finalizeOrder?finalizeMode=default</@ofbizUrl>">${uiLabelMap.DAFinalizeOrderDefault}</a></li>
	          	<li style="display: inline-block;"><a class="btn btn-mini btn-primary" href="<@ofbizUrl>finalizeOrder?finalizeMode=init</@ofbizUrl>">${uiLabelMap.DAFinalizeOrder}</a></li>-->
	        </#if>
      	</#if>
	</ul>
</div>
