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
<#assign shoppingCartChannelType = "">
<#if shoppingCart?exists>
  <#assign shoppingCartOrderType = shoppingCart.getOrderType()>
  <#assign shoppingCartProductStore = shoppingCart.getProductStoreId()?default("NA")>
  <#assign shoppingCartChannelType = shoppingCart.getChannelType()?default("")>
<#else>
<#-- allow the order type to be set in parameter, so only the appropriate section (Sales or Purchase Order) shows up -->
  <#if parameters.orderTypeId?has_content>
    <#assign shoppingCartOrderType = parameters.orderTypeId>
  </#if>
</#if>
<!-- Sales Order Entry -->
<#assign shoppingCartOrderType = "">
<#assign shoppingCartProductStore = "NA">
<#assign shoppingCartChannelType = "">
<#if shoppingCart?exists>
  <#assign shoppingCartOrderType = shoppingCart.getOrderType()>
  <#assign shoppingCartProductStore = shoppingCart.getProductStoreId()?default("NA")>
  <#assign shoppingCartChannelType = shoppingCart.getChannelType()?default("")>
<#else>
<#-- allow the order type to be set in parameter, so only the appropriate section (Sales or Purchase Order) shows up -->
  <#if parameters.orderTypeId?has_content>
    <#assign shoppingCartOrderType = parameters.orderTypeId>
  </#if>
</#if>

<#if security.hasEntityPermission("ORDERMGR", "_CREATE", session)>
<#if shoppingCartOrderType != "PURCHASE_ORDER">
<div class="row-fluid">
<div class="span12">
<div class="span6">
<div class="widget-box olbius-extra no-border-bottom transparent">
  	<div class="widget-header widget-header-small header-color-blue2">
  	<h6 style="font-size:20px">${uiLabelMap.OrderSalesOrder}<#if shoppingCart?exists>&nbsp;${uiLabelMap.OrderInProgress}</#if></h6>
   </div>
	 <div class="widget-body">
	 <div class="widget-body-inner">
	 <div class="widget-main" style="margin-left:-10px;margin-top:-6px">
      <form method="post" name="salesentryform" action="<@ofbizUrl>initorderentry</@ofbizUrl>">
      <input type="hidden" name="originOrderId" value="${parameters.originOrderId?if_exists}"/>
      <input type="hidden" name="finalizeMode" value="type"/>
      <input type="hidden" name="orderMode" value="SALES_ORDER"/>
      <table width="100%" border="0" cellspacing="0" cellpadding="0">
      <a class="open-sans btn btn-primary btn-small icon-search" href="/partymgr/control/findparty?${externalKeyParam}">  ${uiLabelMap.PartyFindParty}</a>
        <tr>
          <td >&nbsp;</td>
          <td width="300" align='right' valign='middle' nowrap="nowrap"><div class='tableheadtext margin-top-nav-10'>${uiLabelMap.ProductProductStore}</div></td>
          <td >&nbsp;</td>
          <td valign='middle'>
            <div class='tabletext'>
              <select name="productStoreId"<#if sessionAttributes.orderMode?exists> disabled</#if>>
                <#assign currentStore = shoppingCartProductStore>
                <#if defaultProductStore?has_content>
                   <option value="${defaultProductStore.productStoreId}">${defaultProductStore.storeName?if_exists}</option>
                   <option value="${defaultProductStore.productStoreId}">----</option>
                </#if>
                <#list productStores as productStore>
                  <option value="${productStore.productStoreId}"<#if productStore.productStoreId == currentStore> selected="selected"</#if>>${productStore.storeName?if_exists}</option>
                </#list>
              </select>
              <#if sessionAttributes.orderMode?exists>${uiLabelMap.OrderCannotBeChanged}</#if>
            </div>
          </td>
        </tr>
        <tr><td colspan="4">&nbsp;</td></tr>
        <tr>
          <td>&nbsp;</td>
          <td align='right' valign='middle' nowrap="nowrap"><div class='tableheadtext margin-top-nav-5'>${uiLabelMap.OrderSalesChannel}</div></td>
          <td>&nbsp;</td>
          <td valign='middle'>
            <div class='tabletext'>
              <select name="salesChannelEnumId">
                <#assign currentChannel = shoppingCartChannelType>
                <#if defaultSalesChannel?has_content>
                   <option value="${defaultSalesChannel.enumId}">${defaultSalesChannel.description?if_exists}</option>
                   <option value="${defaultSalesChannel.enumId}"> ---- </option>
                </#if>
                <option value="">${uiLabelMap.OrderNoChannel}</option>
                <#list salesChannels as salesChannel>
                  <option value="${salesChannel.enumId}" <#if (salesChannel.enumId == currentChannel)>selected="selected"</#if>>${salesChannel.get("description",locale)}</option>
                </#list>
              </select>
            </div>
          </td>
        </tr>
        <tr><td colspan="4">&nbsp;</td></tr>
        <#if partyId?exists>
          <#assign thisPartyId = partyId>
        <#else>
          <#assign thisPartyId = requestParameters.partyId?if_exists>
        </#if>
        <tr>
          <td>&nbsp;</td>
          <td align='right' valign='middle' nowrap="nowrap"><div  style="margin-top:-7px" class='tableheadtext margin-top-nav-10'>${uiLabelMap.CommonUserLoginId}</div></td>
          <td>&nbsp;</td>
          <td valign='middle'>
            <div class='tabletext'>
              <@htmlTemplate.lookupField value="${parameters.userLogin.userLoginId}" formName="salesentryform" name="userLoginId" id="userLoginId_sales" fieldFormName="LookupUserLoginAndPartyDetails"/>
            </div>
          </td>
        </tr>
        <tr>
          <td>&nbsp;</td>
          <td align='right' valign='middle' nowrap="nowrap"><div  style="margin-top:-7px" class='tableheadtext'>${uiLabelMap.OrderCustomer}</div></td>
          <td>&nbsp;</td>
          <td valign='middle'>
            <div class='tabletext'>
              <@htmlTemplate.lookupField value='${thisPartyId?if_exists}' formName="salesentryform" name="partyId" id="partyId" fieldFormName="LookupCustomerName"/>
            </div>
          </td>
          </tr>
       	<tr>
      	<td></td>
          <td></td>
          <td></td>
       	<td>
       	<a class="open-sans btn btn-primary btn-small " href="javascript:document.salesentryform.submit();"> ${uiLabelMap.CommonContinue}
       		<i class="icon-arrow-right icon-on-right"></i>
       	</a>
       	</td>
       	</tr>
      </table>
      </form>
  </div>
  </div>
</div> 
</div>
</div>
</#if>
</#if>

<br />
<!-- Purchase Order Entry -->
<div class="span6" style="margin-top:-20px">
<#if security.hasEntityPermission("ORDERMGR", "_PURCHASE_CREATE", session)>
  <#if shoppingCartOrderType != "SALES_ORDER">
    <div class="widget-box transparent no-border-bottom olbius-extra" > 
    <div class="widget-header widget-header-small header-color-blue2">
    	<h6 style="font-size:20px">${uiLabelMap.OrderPurchaseOrder}<#if shoppingCart?exists>&nbsp;${uiLabelMap.OrderInProgress}</#if></h6>
    	
	</div>
    <div style="margin-top:10px">
    <div >
    <div >
      <form method="post" name="poentryform" action="<@ofbizUrl>initorderentry</@ofbizUrl>">
      <input type='hidden' name='finalizeMode' value='type'/>
      <input type='hidden' name='orderMode' value='PURCHASE_ORDER'/>
      <table width="100%" border='0' cellspacing='0' cellpadding='0'>
      <a class="open-sans btn btn-primary btn-small margin-left3 icon-search" href="/partymgr/control/findparty?${externalKeyParam}">  ${uiLabelMap.PartyFindParty}</a>
        <#if partyId?exists>
          <#assign thisPartyId = partyId>
        <#else>
          <#assign thisPartyId = requestParameters.partyId?if_exists>
        </#if>
        <tr>
          <td>&nbsp;</td>
          <td width="300" align='right' valign='middle' nowrap="nowrap"><div style="margin-top:-7px" class='tableheadtext'>${uiLabelMap.OrderOrderEntryInternalOrganization}</div></td>
          <td>&nbsp;</td>
          <td valign='middle'>
            <div class='tabletext'>
              <select name="billToCustomerPartyId"<#if sessionAttributes.orderMode?default("") == "SALES_ORDER"> disabled</#if>>
                <#list organizations as organization>
                  <#assign organizationName = Static["org.ofbiz.party.party.PartyHelper"].getPartyName(organization, true)/>
                    <#if (organizationName.length() != 0)>
                      <option value="${organization.partyId}">${organization.partyId} - ${organizationName}</option>
                    </#if>
                </#list>
              </select>
            </div>
          </td>
        </tr>
        <tr><td colspan="4">&nbsp;</td></tr>
        <tr>
          <td>&nbsp;</td>
          <td align='right' valign='middle' nowrap="nowrap"><div  style="margin-top:-7px" class='tableheadtext'>${uiLabelMap.CommonUserLoginId}</div></td>
          <td>&nbsp;</td>
          <td valign='middle'>
            <div class='tabletext'>
              <@htmlTemplate.lookupField value='${parameters.userLogin.userLoginId}'formName="poentryform" name="userLoginId" id="userLoginId_purchase" fieldFormName="LookupUserLoginAndPartyDetails"/>
            </div>
          </td>
        </tr>
        <tr>
          <td>&nbsp;</td>
          <td align='right' valign='middle' nowrap="nowrap"><div  style="margin-top:-7px" class='tableheadtext'>${uiLabelMap.PartySupplier}</div></td>
          <td>&nbsp;</td>
          <td valign='middle'>
            <div class='tabletext'>
              <select name="supplierPartyId"<#if sessionAttributes.orderMode?default("") == "SALES_ORDER"> disabled</#if>>
                <option value="">${uiLabelMap.OrderSelectSupplier}</option>
                <#list suppliers as supplier>
                  <option value="${supplier.partyId}"<#if supplier.partyId == thisPartyId> selected="selected"</#if>>[${supplier.partyId}] - ${Static["org.ofbiz.party.party.PartyHelper"].getPartyName(supplier, true)}</option>
                </#list>
              </select>
            </div>
          </td>
        </tr>
        <tr>
          <td></td>
          <td></td>
          <td></td>
          <td>
          	<a class="btn btn-small btn-primary" href="javascript:document.poentryform.submit();">${uiLabelMap.CommonContinue}
         		<i class="icon-arrow-right icon-on-right"></i>
          	</a>
          </td>
        </tr>
      </table>
      </form>
    </div>
    </div>
    </div>
  </#if>
</#if>
