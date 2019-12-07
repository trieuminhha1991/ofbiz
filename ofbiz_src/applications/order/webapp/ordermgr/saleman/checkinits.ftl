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

<#assign shoppingCartOrderType = "">
<#assign shoppingCartProductStore = "NA">
<#if shoppingCart?exists>
  <#assign shoppingCartOrderType = shoppingCart.getOrderType()>
  <#assign shoppingCartProductStore = shoppingCart.getProductStoreId()?default("NA")>
<#else>
<#-- allow the order type to be set in parameter, so only the appropriate section (Sales or Purchase Order) shows up -->
  <#if parameters.orderTypeId?has_content>
    <#assign shoppingCartOrderType = parameters.orderTypeId>
  </#if>
</#if>
<#--get userName or Groupname -->
<#if (requestAttributes.person)?exists><#assign person = requestAttributes.person></#if>
<#if (requestAttributes.partyGroup)?exists><#assign partyGroup = requestAttributes.partyGroup></#if>
<#if person?has_content>
  <#assign userName = person.firstName?if_exists + " " + person.middleName?if_exists + " " + person.lastName?if_exists>
<#elseif partyGroup?has_content>
  <#assign userName = partyGroup.groupName?if_exists>
<#elseif userLogin?exists>
  <#assign userName = userLogin.userLoginId>
<#else>
  <#assign userName = "">
</#if>
<!-- Sales Order Entry -->
<#if security.hasEntityPermission("ORDERMGR", "_CREATE", session)>
<#if shoppingCartOrderType != "PURCHASE_ORDER">
	<div class="widget-box">
	  	<div class="widget-header widget-header-small header-color-blue2">
		  	<h5>${uiLabelMap.OrderSalesOrder}<#if shoppingCart?exists>&nbsp;${uiLabelMap.OrderInProgress}</#if></h5>
	  	</div>
		 <div class="widget-body">
			<form method="post" name="salesentryform" id="salesentryform" action="<@ofbizUrl>salesinitorderentry</@ofbizUrl>">
		 		<#if parameters.listProductStore?has_content>
			      <input type="hidden" name="originOrderId" value="${parameters.originOrderId?if_exists}"/>
			      <input type="hidden" name="finalizeMode" value="type"/>
			      <input type="hidden" name="orderMode" value="SALES_ORDER"/>
			      <input type="hidden" name="userLoginId" value="${parameters.userLogin.userLoginId}"/>
			      <table width="100%" border="0" cellspacing="0" cellpadding="0">
			      	<tr><td colspan="3">&nbsp;</td></tr>
			      	<tr>
			      		<td align='right' valign='middle' nowrap="nowrap"><div class='tableheadtext margin-top-nav-10'>${uiLabelMap.ProductProductStore}</div></td>
			      		<td>&nbsp;</td>
			      		<td>
			      			<#list parameters.listProductStore as store>
								<span class="label label-large label-success arrowed-right">
									<i class="icon-shopping-cart"></i>
									${store.storeName}
								</span>
								<input type="hidden" value="${store.productStoreId}" name="productStoreId" />
							</#list>
			      		</td>
			      	</tr>
			      	<tr><td colspan="3">&nbsp;</td></tr>
			      	<tr>
			      		<td align='right' valign='middle' nowrap="nowrap">
			      			${uiLabelMap.CommonUserLoginId}
			      		</td>
			      		<td>&nbsp;</td>
			      		<td valign="middle">
							<span class="label label-large label-success arrowed-right">
								<i class="icon-key"></i>
								${userName}
							</span>
				    	</td>
				    </tr>
				    <tr><td colspan="3">&nbsp;</td></tr>
				    <tr>
			       		<td align='right' valign='middle' nowrap="nowrap"><div class='tableheadtext'>Route</div></td>
				    	<td>&nbsp;</td>
				    	<td valign='middle'>
				    		<div style="margin-left:7px;">
					       		<select name="routeId" onchange="ajaxUpdateArea('customerid', 'getCustomers', jQuery('#salesentryform').serialize());">
					                <option value=""></option>
					                <#list parameters.listRoute as route>
					                  <option value="${route.partyIdFrom}">${route.groupName?if_exists}</option>
					                </#list>
					            </select>
				            </div>
			            </td>
		            <tr>
				    <tr><td colspan="3">&nbsp;</td></tr>
				    <tr>
				    	<td align='right' valign='middle' nowrap="nowrap"><div class='tableheadtext'>${uiLabelMap.OrderCustomer}</div></td>
				    	<td>&nbsp;</td>
				    	<td valign='middle'>
				      		<div style="margin-left:7px;" id="customerid">
				      			<select name="partyId">
				      			</select>
				      		</div>
				    	</td>
				    </tr>
				    <tr><td colspan="3">&nbsp;</td></tr>
				    <tr>
				    	<td></td>
				    	<td></td>
				    	<td>
					      <a style="margin-left:7px;" class="btn btn-primary btn-small margin-left3" href="javascript:document.salesentryform.submit();"> ${uiLabelMap.CommonContinue}
				       		<i class="icon-arrow-right icon-on-right"></i>
				       	  </a>
			       	    </td>
			       	</tr>
		       	  </table>
		      <#else>
		      <div class="widget-main"><p class="alert alert-success">
				${uiLabelMap.OrderSalesmanPermisson}
			  </p></div>
			</#if>
		</form>
		 </div>
	</div>	 
</#if>
</#if>
