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

<div class="widget-box olbius-extra ">
        <div class="widget-header widget-header-small header-color-blue2">
            <h6>
            <#if shoppingCart.getOrderType() == "PURCHASE_ORDER">
            ${uiLabelMap.OrderPurchaseOrder}
       		<#else>
            ${uiLabelMap.OrderSalesOrder}
        	</#if>
        	</h6>
            <div class="widget-toolbar">
           	<a href="#" data-action="collapse">
            		<i class="icon-chevron-up"></i>
            	</a>
            </div>
       </div>
        <div class="widget-body">
        <div class="widget-body-inner">
    	<div class="widget-main">
        <table width="100%" border="0" cellpadding="1" class="table table-striped table-hover table-bordered dataTable">
        <#-- order name -->
        <#if (orderName?has_content)>
            <tr>
                <td align="right" valign="top" width="15%">
                    <span>&nbsp;<b>${uiLabelMap.OrderOrderName}</b> </span>
                </td>
                <td valign="top" width="80%">
                    ${orderName}
                </td>
            </tr>
            
        </#if>
        <#-- order for party -->
        <#if (orderForParty?exists)>
            <tr>
                <td align="right" valign="top" width="15%">
                    <span>&nbsp;<b>${uiLabelMap.OrderOrderFor}</b> </span>
                </td>
                <td valign="top" width="80%">
                    ${Static["org.ofbiz.party.party.PartyHelper"].getPartyName(orderForParty, false)} [${orderForParty.partyId}]
                </td>
            </tr>
            
        </#if>
        <#if (cart.getPoNumber()?has_content)>
            <tr>
                <td align="right" valign="top" width="15%">
                    <span>&nbsp;<b>${uiLabelMap.OrderPONumber}</b> </span>
                </td>
                <td valign="top" width="80%">
                    ${cart.getPoNumber()}
                </td>
            </tr>
            
        </#if>
        <#if orderTerms?has_content>
            <tr>
                <td align="right" valign="top" width="15%">
                    <div>&nbsp;<b>${uiLabelMap.OrderOrderTerms}</b></div>
                </td>
                <td valign="top" width="80%">
                    <table class="table table-striped table-bordered table-hover dataTable">
                        <tr>
                            <td width="35%"><div><b>${uiLabelMap.OrderOrderTermType}</b></div></td>
                            <td width="10%"><div><b>${uiLabelMap.OrderOrderTermValue}</b></div></td>
                            <td width="10%"><div><b>${uiLabelMap.OrderOrderTermDays}</b></div></td>
                            <td width="45%"><div><b>${uiLabelMap.CommonDescription}</b></div></td>
                        </tr>
                        <#assign index=0/>
                        <#list orderTerms as orderTerm>
                        <tr>
                            <td width="35%"><div>${orderTerm.getRelatedOne("TermType", false).get("description",locale)}</div></td>
                            <td width="10%"><div>${orderTerm.termValue?default("")}</div></td>
                            <td width="10%"><div>${orderTerm.termDays?default("")}</div></td>
                            <td width="45%"><div>${orderTerm.textValue?default("")}</div></td>
                        </tr>
                            <#if orderTerms.size()&lt;index>
                        <tr><td colspan="4"><hr /></td></tr>
                            </#if>
                            <#assign index=index+1/>
                        </#list>
                    </table>
                </td>
            </tr>
            
        </#if>
        <#-- tracking number -->
        <#if trackingNumber?has_content>
            <tr>
                <td align="right" valign="top" width="15%">
                    <div>&nbsp;<b>${uiLabelMap.OrderTrackingNumber}</b></div>
                </td>
                <td valign="top" width="80%">
                    <#-- TODO: add links to UPS/FEDEX/etc based on carrier partyId  -->
                    <div>${trackingNumber}</div>
                </td>
            </tr>
            
        </#if>
        <#-- splitting preference -->
            <tr>
                <td align="right" valign="top" width="15%">
                    <div>&nbsp;<b>${uiLabelMap.OrderSplittingPreference}</b></div>
                </td>
                <td valign="top" width="80%">
                    <div>
                        <#if maySplit?default("N") == "N">${uiLabelMap.FacilityWaitEntireOrderReady}</#if>
                        <#if maySplit?default("Y") == "Y">${uiLabelMap.FacilityShipAvailable}</#if>
                    </div>
                </td>
            </tr>
        <#-- shipping instructions -->
        <#if shippingInstructions?has_content>
            
            <tr>
                <td align="right" valign="top" width="15%">
                    <div>&nbsp;<b>${uiLabelMap.OrderSpecialInstructions}</b></div>
                </td>
                <td valign="top" width="80%">
                    <div>${shippingInstructions}</div>
                </td>
            </tr>
        </#if>
            
        <#if orderType != "PURCHASE_ORDER" && (productStore.showCheckoutGiftOptions)?if_exists != "N">
        <#-- gift settings -->
            <tr>
                <td align="right" valign="top" width="15%">
                    <div>&nbsp;<b>${uiLabelMap.OrderGift}</b></div>
                </td>
                
                <td valign="top" width="80%">
                    <div>
                        <#if isGift?default("N") == "N">${uiLabelMap.OrderThisOrderNotGift}</#if>
                        <#if isGift?default("N") == "Y">${uiLabelMap.OrderThisOrderGift}</#if>
                    </div>
                </td>
            </tr>
            
            <#if giftMessage?has_content>
            <tr>
                <td align="right" valign="top" width="15%">
                    <div>&nbsp;<b>${uiLabelMap.OrderGiftMessage}</b></div>
                </td>
                
                <td valign="top" width="80%">
                    <div>${giftMessage}</div>
                </td>
            </tr>
            
            </#if>
        </#if>
        <#if shipAfterDate?has_content>
            <tr>
                <td align="right" valign="top" width="15%">
                    <div>&nbsp;<b>${uiLabelMap.OrderShipAfterDate}</b></div>
                </td>
                
                <td valign="top" width="80%">
                    <div>${shipAfterDate}</div>
                </td>
            </tr>
        </#if>
        <#if shipBeforeDate?has_content>
            <tr>
                <td align="right" valign="top" width="15%">
                    <div>&nbsp;<b>${uiLabelMap.OrderShipBeforeDate}</b></div>
                </td>
                
                <td valign="top" width="80%">
                  <div>${shipBeforeDate}</div>
                </td>
            </tr>
        </#if>
        </table>
    </div>
    </div>
    </div>
</div>
