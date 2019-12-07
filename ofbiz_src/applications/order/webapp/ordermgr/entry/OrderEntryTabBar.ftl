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
 	<#if shoppingCart.getOrderType() == "PURCHASE_ORDER">
        <#if shoppingCart.getOrderPartyId() == "_NA_" || (shoppingCart.size() = 0)>
          <li style="display: inline-block;"><a class="btn btn-mini dislink" href="#">${uiLabelMap.OrderFinalizeOrder}</a></li>
        <#else>
           <li style="display: inline-block;"><a id="finalize" class="btn btn-mini btn-primary" href="<@ofbizUrl>finalizeOrder?finalizeMode=purchase&amp;finalizeReqCustInfo=false&amp;finalizeReqShipInfo=false&amp;finalizeReqOptions=false&amp;finalizeReqPayInfo=false</@ofbizUrl>">${uiLabelMap.OrderFinalizeOrder}</a></li>
        </#if>
  	<#else>
        <#if shoppingCart.size() = 0>
        	<li style="display: inline-block;" class="disabled"><a class="btn btn-mini dislink" href="#">${uiLabelMap.OrderFinalizeOrder}</a></li>
          	<li style="display: inline-block;" class="disabled"><a class="btn btn-mini dislink" href="#">${uiLabelMap.OrderFinalizeOrderDefault}</a></li>
          	<li style="display: inline-block;"><a class="btn btn-mini btn-primary" class="dislink btn btn-mini btn-primary" href="<@ofbizUrl>quickcheckout</@ofbizUrl>">${uiLabelMap.OrderQuickFinalizeOrder}</a></li>
        <#else>
          <li style="display: inline-block;"><a class="btn btn-mini btn-primary" href="<@ofbizUrl>quickcheckout</@ofbizUrl>">${uiLabelMap.OrderQuickFinalizeOrder}</a></li>
          <li style="display: inline-block;"><a class="btn btn-mini btn-primary" href="<@ofbizUrl>finalizeOrder?finalizeMode=default</@ofbizUrl>">${uiLabelMap.OrderFinalizeOrderDefault}</a></li>
          <li style="display: inline-block;"><a class="btn btn-mini btn-primary" href="<@ofbizUrl>finalizeOrder?finalizeMode=init</@ofbizUrl>">${uiLabelMap.OrderFinalizeOrder}</a></li>
        </#if>
  	</#if>

  	<#if (shoppingCart.size() > 0)>
        <li style="display: inline-block;"><a class="btn btn-mini btn-primary" href="javascript:document.cartform.submit()">${uiLabelMap.OrderRecalculateOrder}</a></li>
        <li style="display: inline-block;"><a class="btn btn-mini btn-primary" href="javascript:removeSelected();">${uiLabelMap.OrderRemoveSelected}</a></li>
  	<#else>
        <li style="display: inline-block;" class="disabled"><a class="btn btn-mini dislink" href="#">${uiLabelMap.OrderRecalculateOrder}</a></li>
        <li style="display: inline-block;" class="disabled"><a class="btn btn-mini dislink" href="#">${uiLabelMap.OrderRemoveSelected}</a></li>
  	</#if>
     <li style="display: inline-block;"><a id="emptyCart" class="btn btn-mini btn-primary" href="<@ofbizUrl>emptycart</@ofbizUrl>">${uiLabelMap.OrderClearOrder}</a></li>
    </ul>
</div>
