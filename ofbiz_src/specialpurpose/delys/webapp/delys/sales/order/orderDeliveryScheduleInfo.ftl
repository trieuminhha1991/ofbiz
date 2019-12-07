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

<#if hasPermission>
	<div class="widget-box olbius-extra transparent no-border-bottom">
	    <div class="widget-header widget-header-small header-color-blue2">
	        <h6 style="font-size:20px">${uiLabelMap.OrderScheduleDelivery}</h6>
	        <#if orderId?exists>
	        <span class="widget-toolbar none-content">
				<a href="<@ofbizUrl>orderView?orderId=${orderId}</@ofbizUrl>">
					<i class="icon-zoom-in open-sans">${uiLabelMap.DABackOrder} (${orderId})</i>
				</a>
			</span>
			</#if>
	   	</div>        
    	<div class="widget-body">
		    <div class="widget-body-inner">
			    <div class="widget-main">
			        <#if orderId?has_content>
			          	${updatePODeliveryInfoWrapper.renderFormString(context)}
			        <#else>
			          	${uiLabelMap.OrderNoPurchaseSpecified}
			        </#if>
			    </div>
		    </div>
	    </div>
	</div>
<#else>
 	<div><p class="alert alert-danger">${uiLabelMap.OrderViewPermissionError}</p></div>
</#if>