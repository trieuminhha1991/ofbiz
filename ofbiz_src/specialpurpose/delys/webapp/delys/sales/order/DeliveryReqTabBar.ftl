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
    	<#if deliveryCart.size() = 0>
	    	<#--
	    	<li style="display: inline-block;" class="disabled"><a class="btn btn-mini dislink" href="#">${uiLabelMap.DAFinalizeOrder}</a></li>
	      	<li style="display: inline-block;" class="disabled"><a class="btn btn-mini dislink" href="#">${uiLabelMap.DAFinalizeOrderDefault}</a></li>
	    	-->
	      	<li style="display: inline-block;"><a class="btn btn-mini btn-primary" class="dislink btn btn-mini btn-primary" href="javascript:processOrder();">${uiLabelMap.DACreateProposal}</a></li>
    	<#else>
	      	<li style="display: inline-block;"><a class="btn btn-mini btn-primary" href="javascript:processOrder();">${uiLabelMap.DACreateProposal}</a></li>
      		<#--
      		<li style="display: inline-block;"><a class="btn btn-mini btn-primary" href="<@ofbizUrl>finalizeOrder?finalizeMode=default</@ofbizUrl>">${uiLabelMap.DAFinalizeOrderDefault}</a></li>
	      	<li style="display: inline-block;"><a class="btn btn-mini btn-primary" href="<@ofbizUrl>finalizeOrder?finalizeMode=init</@ofbizUrl>">${uiLabelMap.DAFinalizeOrder}</a></li>
    	
      		-->
      	</#if>

      	<#if (deliveryCart.size() > 0)>
        	<li style="display: inline-block;"><a class="btn btn-mini btn-primary" href="javascript:removeSelected();">${uiLabelMap.DARemoveSelected}</a></li>
      	<#else>
        	<li style="display: inline-block;" class="disabled"><a class="btn btn-mini dislink" href="#">${uiLabelMap.DARemoveSelected}</a></li>
      	</#if>
     	<li style="display: inline-block;"><a class="btn btn-mini btn-primary" href="<@ofbizUrl>emptyDeliveryCart</@ofbizUrl>">${uiLabelMap.DAClearDeliveryProposal}</a></li>
    </ul>
</div>
<div class="clear-all"></div>
<script type="text/javascript" src="/delys/images/js/bootbox.min.js"></script>
<script type="text/javascript">
	function processOrder() {
		bootbox.confirm("${uiLabelMap.DAAreYouSureCreate}", function(result){
			if(result){
				window.location.href = "<@ofbizUrl>processDeliveryReq</@ofbizUrl>";
			}
		});
	}
</script>