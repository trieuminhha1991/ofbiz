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

<div class="screenlet">
    <div class="screenlet-body">
        <#-- DO NOT CHANGE THE NAME OF THIS FORM, it will break the some of the multi-service pattern features -->
        <#assign selectAllFormName = "selectAllForm"/>
        <form name="selectAllForm" method="post" action="<@ofbizUrl>makeQuickReturn</@ofbizUrl>">
          <input type="hidden" name="_checkGlobalScope" value="Y"/>
          <input type="hidden" name="_useRowSubmit" value="Y"/>
          <input type="hidden" name="fromPartyId" value="${partyId?if_exists}"/>
          <input type="hidden" name="toPartyId" value="${toPartyId?if_exists}"/>
          <input type="hidden" name="orderId" value="${orderId}"/>
          <input type="hidden" name="returnId" value="${returnId?if_exists}"/>
          <input type="hidden" name="needsInventoryReceive" value="${parameters.needsInventoryReceive?default("Y")}"/>
          <input type="hidden" name="destinationFacilityId" value="${destinationFacilityId?if_exists}"/>
          <input type="hidden" name="returnHeaderTypeId" value="${returnHeaderTypeId}"/>
          <#if (orderHeader?has_content) && (orderHeader.currencyUom?has_content)>
          <input type="hidden" name="currencyUomId" value="${orderHeader.currencyUom}"/>
          </#if>
          <#include "returnItemInc.ftl"/>
          
          <#if returnableItems?has_content>
          <#if "CUSTOMER_RETURN" == returnHeaderTypeId>
          <h3>${uiLabelMap.FormFieldTitle_paymentMethodId}:</h3>
          <table cellspacing="0" class="basic-table">
            <tr><td>
              <#if creditCardList?exists || eftAccountList?exists>
                <select name='paymentMethodId'>
                  <option value=""></option>
                  <#if creditCardList?has_content>
                    <#list creditCardList as creditCardPm>
                      <#assign creditCard = creditCardPm.getRelatedOne("CreditCard", false)>
                      <option value="${creditCard.paymentMethodId}">CC:&nbsp;${Static["org.ofbiz.party.contact.ContactHelper"].formatCreditCard(creditCard)}</option>
                    </#list>
                  </#if>
                  <#if eftAccountList?has_content>
                    <#list eftAccountList as eftAccount>
                      <option value="${eftAccount.paymentMethodId}">EFT:&nbsp;${eftAccount.nameOnAccount?if_exists}, ${eftAccount.accountNumber?if_exists}</option>
                    </#list>
                  </#if>
                </select>
              <#else>
                <input type='text' size='20' name='paymentMethodId' />
              </#if>
              <#if (party.partyId)?has_content>
                <a href="/partymgr/control/editcreditcard?partyId=${party.partyId}${externalKeyParam}" target="partymgr" class="smallSubmit">${uiLabelMap.AccountingCreateNewCreditCard}</a>
              </#if>
            </td></tr>
          </table>
          </#if>
            <h4><#if "CUSTOMER_RETURN" == returnHeaderTypeId>${uiLabelMap.OrderReturnShipFromAddress}<#else>${uiLabelMap["checkhelper.select_shipping_destination"]}</#if></h4>
            <table cellspacing="0" class="table table-striped table-bordered table-hover dataTable">
            	<tr class="header-row">
            		<td>${uiLabelMap.CommonTo}</td>
            		<td>${uiLabelMap.CommonAttn}</td>
            		<td>${uiLabelMap.CommonAddress1}</td>
            		<td>${uiLabelMap.CommonAddress2}</td>
            		<td>${uiLabelMap.CommonCity}</td>
            		<td>${uiLabelMap.StateProvinceGeoId}</td>
            		<td>${uiLabelMap.PostalCode}</td>
            		<td>${uiLabelMap.CountryGeoId}</td>
            		<td>${uiLabelMap.CommonSelect}</td>
            	</tr>
              <#list shippingContactMechList as shippingContactMech>
                <#assign shippingAddress = shippingContactMech.getRelatedOne("PostalAddress", false)>
                <tr>
                  <td valign="top" nowrap="nowrap">
                  	<#if shippingAddress.toName?has_content>${shippingAddress.toName}</#if>
                  </td>
                  <td valign="top" nowrap="nowrap">
                  	<#if shippingAddress.attnName?has_content>${shippingAddress.attnName}</#if>
                  </td>
                  <td valign="top" nowrap="nowrap">
                  	<#if shippingAddress.address1?has_content>${shippingAddress.address1}</#if>
                  </td>
                  <td valign="top" nowrap="nowrap">
                  	<#if shippingAddress.address2?has_content>${shippingAddress.address2}<br /></#if>
                  </td>
                  <td valign="top" nowrap="nowrap">
                  	<#if shippingAddress.city?has_content>${shippingAddress.city}</#if>
                  </td>
                  <td valign="top" nowrap="nowrap">
                  	<#if shippingAddress.stateProvinceGeoId?has_content>${shippingAddress.stateProvinceGeoId}</#if>
                  </td>
                  <td valign="top" nowrap="nowrap">
                  	<#if shippingAddress.postalCode?has_content>${shippingAddress.postalCode}</#if>
                  </td>
                  <td valign="top" nowrap="nowrap">
                  	<#if shippingAddress.countryGeoId?has_content>${shippingAddress.countryGeoId}</#if>
                  <#--<a href="<@ofbizUrl>editcontactmech?DONE_PAGE=checkoutoptions&amp;contactMechId=${shippingAddress.contactMechId}</@ofbizUrl>" class="buttontext">[${uiLabelMap.CommonUpdate}]</a>-->
                  </td>
                  <td width="1%" valign="top" nowrap="nowrap">
                    <input style="opacity: 1 !important; position: initial !important;" type="radio" name="originContactMechId" value="${shippingAddress.contactMechId}"  <#if (shippingContactMechList?size == 1)>checked="checked"</#if> />
                  </td>
                </tr>
              </#list>
            </table>
            </#if>
        </form>
    </div>
</div>