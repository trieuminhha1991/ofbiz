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


<#if security.hasEntityPermission("ORDERMGR", "_VIEW", session)>
<form method="post" name="lookuporder" id="lookuporder" action="<@ofbizUrl>searchorders</@ofbizUrl>" onsubmit="javascript:lookupOrders();">
<input type="hidden" name="lookupFlag" value="Y"/>
<!--<input type="hidden" name="hideFields" value="Y"/>-->
<input type="hidden" name="viewSize" value="${viewSize}"/>
<input type="hidden" name="viewIndex" value="${viewIndex}"/>
<input type="hidden" name="orderTypeId" value="PURCHASE_ORDER"/>
<div>      
    </div>
	<div id="findOrders" class="widget-box">
  	<div class="widget-box transparent no-bottom-border">
  	</div>
  	<#if parameters.hideFields?default("N") != "Y">
    <div >
    <div class="widget-body-inner">
    <div class="widget-main">
        <tr>
          <td align='center' width='100%'>
          <div class="row-fluid">
          <div class="span12">
          <div class="span6">
            <table class="basic-table" cellspacing='0'>
              <tr>
                <td align='right' >${uiLabelMap.OrderOrderId}</td>
                <td>&nbsp;</td>
                <td align='left'><input type='text' name='orderId'/></td>
              </tr>
              <tr>
                <td align='right' >${uiLabelMap.ProductProductStore}</td>
                <td>&nbsp;</td>
                <td align='left'>
                  <select name='productStoreId'>
                    <#if currentProductStore?has_content>
                    <option value="${currentProductStore.productStoreId}">${currentProductStore.storeName?if_exists}</option>
                    <option value="${currentProductStore.productStoreId}">---</option>
                    </#if>
                    <option value="">${uiLabelMap.CommonAnyStore}</option>
                    <#list productStores as store>
                      <option value="${store.productStoreId}">${store.storeName?if_exists}</option>
                    </#list>
                  </select>
                </td>
              </tr>
              <tr>
                <td align='right' >${uiLabelMap.CommonStatus} ${uiLabelMap.OrderOrder}</td>
                <td>&nbsp;</td>
                <td align='left'>
                  <select name='orderStatusId'>
                    <#if currentStatus?has_content>
                    <option value="${currentStatus.statusId}">${currentStatus.get("description", locale)}</option>
                    <option value="${currentStatus.statusId}">---</option>
                    </#if>
                    <option value="">${uiLabelMap.CommonAll}</option>
                    <#list orderStatuses as orderStatus>
                      <option value="${orderStatus.statusId}">${orderStatus.get("description", locale)}</option>
                    </#list>
                  </select>
                </td>
              </tr>
              <tr>
                <td align='right' >${uiLabelMap.AccountingPaymentStatus}</td>
                <td>&nbsp;</td>
                <td>
                    <select name="paymentStatusId">
                        <option value="">${uiLabelMap.CommonAll}</option>
                        <#list paymentStatusList as paymentStatus>
                            <option value="${paymentStatus.statusId}">${paymentStatus.get("description", locale)}</option>
                        </#list>
                    </select>
                </td>
              </tr>
           <#--  <tr>
                <td align='right' >${uiLabelMap.OrderExternalId}</td>
                <td>&nbsp;</td>
                <td align='left'><input type='text' name='externalId'/></td>
              </tr>
          -->
          <#--    <tr>
                <td align='right' >${uiLabelMap.OrderCustomerPo}</td>
                <td>&nbsp;</td>
                <td align='left'><input type='text' name='correspondingPoId' value='${requestParameters.correspondingPoId?if_exists}'/></td>
              </tr>
          -->
          <#--    <tr>
                <td align='right' >${uiLabelMap.OrderInternalCode}</td>
                <td>&nbsp;</td>
                <td align='left'><input type='text' name='internalCode' value='${requestParameters.internalCode?if_exists}'/></td>
              </tr>
          -->
          <#--    <tr>
                <td align='right' >${uiLabelMap.ProductProductId}</td>
                <td>&nbsp;</td>
                <td align='left'><input type='text' name='productId' value='${requestParameters.productId?if_exists}'/></td>
              </tr>
          -->
          <#--    <#if goodIdentificationTypes?has_content>
              <tr>
                  <td align='right' >${uiLabelMap.ProductGoodIdentificationType}</td>
                  <td>&nbsp;</td>
                  <td align='left'>
                      <select name='goodIdentificationTypeId'>
                          <#if currentGoodIdentificationType?has_content>
                              <option value="${currentGoodIdentificationType.goodIdentificationTypeId}">${currentGoodIdentificationType.get("description", locale)}</option>
                              <option value="${currentGoodIdentificationType.goodIdentificationTypeId}">---</option>
                          </#if>
                          <option value="">${uiLabelMap.ProductAnyGoodIdentification}</option>
                          <#list goodIdentificationTypes as goodIdentificationType>
                              <option value="${goodIdentificationType.goodIdentificationTypeId}">${goodIdentificationType.get("description", locale)}</option>
                          </#list>
                      </select>
                  </td>
              </tr>
              <tr>
                  <td align='right' >${uiLabelMap.ProductGoodIdentification}</td>
                  <td>&nbsp;</td>
                  <td align='left'><input type='text' name='goodIdentificationIdValue' value='${requestParameters.goodIdentificationIdValue?if_exists}'/></td>
              </tr>
              </#if>
          -->
          <#--    <tr>
                <td align='right' >${uiLabelMap.ProductInventoryItemId}</td>
                <td>&nbsp;</td>
                <td align='left'><input type='text' name='inventoryItemId' value='${requestParameters.inventoryItemId?if_exists}'/></td>
              </tr>
         -->
         <#--     <tr>
                <td align='right' >${uiLabelMap.ProductSerialNumber}</td>
                <td>&nbsp;</td>
                <td align='left'><input type='text' name='serialNumber' value='${requestParameters.serialNumber?if_exists}'/></td>
              </tr>
         -->
         <#--     <tr>
                <td align='right' >${uiLabelMap.ProductSoftIdentifier}</td>
                <td>&nbsp;</td>
                <td align='left'><input type='text' name='softIdentifier' value='${requestParameters.softIdentifier?if_exists}'/></td>
              </tr>
         -->
             
         <#--     <tr>
                <td align='right' >${uiLabelMap.CommonUserLoginId}</td>
                <td>&nbsp;</td>
                <td align='left'><input type='text' name='userLoginId' value='${requestParameters.userLoginId?if_exists}'/></td>
              </tr>
        -->
              <tr>
                <td align='right' >${uiLabelMap.AccountingBillingAccount}</td>
                <td>&nbsp;</td>
                <td align='left'><input type='text' name='billingAccountId' value='${requestParameters.billingAccountId?if_exists}'/></td>
              </tr>
        <#--  <tr>
                <td align='right' >${uiLabelMap.CommonCreatedBy}</td>
                <td>&nbsp;</td>
                <td align='left'><input type='text' name='createdBy' value='${requestParameters.createdBy?if_exists}'/></td>
              </tr>
              -->
              <tr>
                <td align='right' >${uiLabelMap.OrderSalesChannel}</td>
                <td>&nbsp;</td>
                <td align='left'>
                  <select name='salesChannelEnumId'>
                    <#if currentSalesChannel?has_content>
                    <option value="${currentSalesChannel.enumId}">${currentSalesChannel.get("description", locale)}</option>
                    <option value="${currentSalesChannel.enumId}">---</option>
                    </#if>
                    <option value="">${uiLabelMap.CommonAnySalesChannel}</option>
                    <#list salesChannels as channel>
                      <option value="${channel.enumId}">${channel.get("description", locale)}</option>
                    </#list>
                  </select>
                </td>
              </tr>
             </table>
             </div>
             <div class="span6">
             <table> 
              
         <#--     <tr>
                <td align='right' >${uiLabelMap.ProductWebSite}</td>
                <td>&nbsp;</td>
                <td align='left'>
                  <select name='orderWebSiteId'>
                    <#if currentWebSite?has_content>
                    <option value="${currentWebSite.webSiteId}">${currentWebSite.siteName}</option>
                    <option value="${currentWebSite.webSiteId}">---</option>
                    </#if>
                    <option value="">${uiLabelMap.CommonAnyWebSite}</option>
                    <#list webSites as webSite>
                      <option value="${webSite.webSiteId}">${webSite.siteName?if_exists}</option>
                    </#list>
                  </select>
                </td>
              </tr>
          -->
              
           <#--   <tr>
                <td align='right' >${uiLabelMap.OrderContainsBackOrders}</td>
                <td>&nbsp;</td>
                <td align='left'>
                  <select name='hasBackOrders'>
                    <#if requestParameters.hasBackOrders?has_content>
                    <option value="Y">${uiLabelMap.OrderBackOrders}</option>
                    <option value="Y">---</option>
                    </#if>
                    <option value="">${uiLabelMap.CommonShowAll}</option>
                    <option value="Y">${uiLabelMap.CommonOnly}</option>
                  </select>
                </td>
              </tr>
           -->
           	<tr>
                <td align='right' >${uiLabelMap.PartyRoleType}</td>
                <td >&nbsp;</td>
                <td align='left'>
                  <select name='roleTypeId' id='roleTypeId' multiple="multiple">
                    <#if currentRole?has_content>
                    <option value="${currentRole.roleTypeId}">${currentRole.get("description", locale)}</option>
                    </#if>
                    <option value="">${uiLabelMap.CommonAnyRoleType}</option>
                    <#list roleTypes as roleType>
                      <option value="${roleType.roleTypeId}">${roleType.get("description", locale)}</option>
                    </#list>
                  </select>
                </td>
              </tr>
              <tr> 
                <td align='right' >${uiLabelMap.PartyPartyId}</td>
                <td>&nbsp;</td>
                <td align='left'>
                  <@htmlTemplate.lookupField value='${requestParameters.partyId?if_exists}' formName="lookuporder" name="partyId" id="partyId" fieldFormName="LookupPartyName"/>
                </td>
              </tr>
              <tr>
                <td align='right' >${uiLabelMap.OrderSelectShippingMethod}</td>
                <td>&nbsp;</td>
                <td align='left'>
                  <select name="shipmentMethod">
                    <#if currentCarrierShipmentMethod?has_content>
                      <#assign currentShipmentMethodType = currentCarrierShipmentMethod.getRelatedOne("ShipmentMethodType", false)>
                      <option value="${currentCarrierShipmentMethod.partyId}@${currentCarrierShipmentMethod.shipmentMethodTypeId}">${currentCarrierShipmentMethod.partyId?if_exists} ${currentShipmentMethodType.description?if_exists}</option>
                      <option value="${currentCarrierShipmentMethod.partyId}@${currentCarrierShipmentMethod.shipmentMethodTypeId}">---</option>
                    </#if>
                    <option value="">${uiLabelMap.OrderSelectShippingMethod}</option>
                    <#list carrierShipmentMethods as carrierShipmentMethod>
                      <#assign shipmentMethodType = carrierShipmentMethod.getRelatedOne("ShipmentMethodType", false)>
                      <option value="${carrierShipmentMethod.partyId}@${carrierShipmentMethod.shipmentMethodTypeId}">${carrierShipmentMethod.partyId?if_exists} ${shipmentMethodType.description?if_exists}</option>
                    </#list>
                  </select>
                </td>
              </tr>
        <#--      <tr>
                <td align='right' >${uiLabelMap.OrderViewed}</td>
                <td>&nbsp;</td>
                <td align='left'>
                  <select name="isViewed">
                    <#if requestParameters.isViewed?has_content>
                      <#assign isViewed = requestParameters.isViewed>
                      <option value="${isViewed}"><#if "Y" == isViewed>${uiLabelMap.CommonYes}<#elseif "N" == isViewed>${uiLabelMap.CommonNo}</#if></option>
                    </#if>
                    <option value=""></option>
                    <option value="Y">${uiLabelMap.CommonYes}</option>
                    <option value="N">${uiLabelMap.CommonNo}</option>
                  </select>
                </td>
              </tr>
           -->
          <#--    <tr>
                <td align='right' >${uiLabelMap.OrderAddressVerification}</td>
                <td>&nbsp;</td>
                <td align='left'><input type='text' name='gatewayAvsResult' value='${requestParameters.gatewayAvsResult?if_exists}'/></td>
              </tr>
              
              <tr>
                <td align='right' >${uiLabelMap.OrderScore}</td>
                <td>&nbsp;</td>
                <td align='left'><input type='text' name='gatewayScoreResult' value='${requestParameters.gatewayScoreResult?if_exists}'/></td>
              </tr>
          -->
              <tr>
                <td align='right' >${uiLabelMap.CommonDateFilter}</td>
                <td>&nbsp;</td>
                <td>&nbsp;</td>
              </tr>
              <tr>
              	<td align='right'><span >${uiLabelMap.CommonFrom}</span></td>
              	<td>&nbsp;</td>
                <td align='left'>
                        <@htmlTemplate.renderDateTimeField name="minDate" event="" action="" value="${requestParameters.minDate?if_exists}" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="25" maxlength="30" id="minDate1" dateType="date" shortDateInput=false timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
                 </td>
              </tr>
              <tr>
              	<td align='right'><span >${uiLabelMap.CommonThru}</span></td>
              	<td>&nbsp;</td>
                 <td align='left' nowrap="nowrap">
                    <@htmlTemplate.renderDateTimeField name="maxDate" event="" action="" value="${requestParameters.maxDate?if_exists}" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="25" maxlength="30" id="maxDate1" dateType="date" shortDateInput=false timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
                  </td>
              </tr>
      <#--        <tr>
                <td align='right' >${uiLabelMap.OrderFilterOn} ${uiLabelMap.OrderFilterInventoryProblems}</td>
                <td>&nbsp;</td>
                <td align='left'>
                  <table class="basic-table" cellspacing='0'>
                    <tr>
                      <td nowrap="nowrap">
                        <label>
							<input type="checkbox" name="filterInventoryProblems" value="Y"
                            <#if requestParameters.filterInventoryProblems?default("N") == "Y">checked="checked"</#if> /><span class="lbl"></span>
						</label>
                      </td>
                    </tr>
                  </table>
                </td>
              </tr>
        -->      
              <tr>
                <td align='right' >${uiLabelMap.OrderFilterPartiallyReceivedPOs}</td>
                <td>&nbsp;</td>
                <td align='left'>
                  <table class="basic-table" cellspacing='0'>
                    <tr>
                      <td nowrap="nowrap">
                        <label>
							<input type="checkbox" name="filterPartiallyReceivedPOs" value="Y"
                            <#if requestParameters.filterPartiallyReceivedPOs?default("N") == "Y">checked="checked"</#if> /><span class="lbl"></span>
						</label>
                      </td>
                    </tr>
                  </table>
                </td>
              </tr>
       <#--       <tr>
                <td align='right' >${uiLabelMap.OrderFilterPOsOpenPastTheirETA}</td>
                <td>&nbsp;</td>
                <td align='left'>
                  <table class="basic-table" cellspacing='0'>
                    <tr>
                      <td nowrap="nowrap">
                        <label>
							<input type="checkbox" name="filterPOsOpenPastTheirETA" value="Y"
                            <#if requestParameters.filterPOsOpenPastTheirETA?default("N") == "Y">checked="checked"</#if> /><span class="lbl"></span>
						</label>
                      </td>
                    </tr>
                  </table>
                </td>
              </tr>
         -->
              <tr>
                <td align='right' >${uiLabelMap.OrderFilterPOsWithRejectedItems}</td>
                <td>&nbsp;</td>
                <td align='left'>
                  <table class="basic-table" cellspacing='0'>
                    <tr>
                      <td nowrap="nowrap">
                        <label>
							<input type="checkbox" name="filterPOsWithRejectedItems" value="Y"
                            <#if requestParameters.filterPOsWithRejectedItems?default("N") == "Y">checked="checked"</#if> /><span class="lbl"></span>
						</label>
                      </td>
                    </tr>
                  </table>
                </td>
              </tr>
         <#-- <tr>
                <td align='right' >${uiLabelMap.OrderShipToCountry}</td>
                <td>&nbsp;</td>
                <td align='left'>
                  <select name="countryGeoId">
                    <#if requestParameters.countryGeoId?has_content>
                        <#assign countryGeoId = requestParameters.countryGeoId>
                        <#assign geo = delegator.findOne("Geo", Static["org.ofbiz.base.util.UtilMisc"].toMap("geoId", countryGeoId), true)>
                        <option value="${countryGeoId}">${geo.geoName?if_exists}</option>
                        <option value="${countryGeoId}">---</option>
                    <#else>
                        <option value="">---</option>
                    </#if>
                    ${screens.render("component://common/widget/CommonScreens.xml#countries")}
                  </select>
                  <select name="includeCountry">
                    <#if requestParameters.includeCountry?has_content>
                       <#assign includeCountry = requestParameters.includeCountry>
                       <option value="${includeCountry}"><#if "Y" == includeCountry>${uiLabelMap.OrderOnlyInclude}<#elseif "N" == includeCountry>${uiLabelMap.OrderDoNotInclude}</#if></option>
                       <option value="${includeCountry}">---</option>
                    </#if>
                    <option value="Y">${uiLabelMap.OrderOnlyInclude}</option>
                    <option value="N">${uiLabelMap.OrderDoNotInclude}</option>
                  </select>
                </td>
              </tr>
         -->
            </table>
          </td>
        </tr>
      </table>
    	</div>
      </div>
        <div class = "span12 align-center"> 
                    <input type="hidden" name="showAll" value="Y"/>
                    <button type="submit" class="btn btn-small btn-primary open-sans" name="submitButton"><i class="icon-search"></i>${uiLabelMap.CommonFind}</button>
      	</div>
      </div>
    </div>
    </div>
    </div>
      </#if>
</div>
<input type="image" src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" onclick="javascript:lookupOrders(true);"/>
</form>
<#if requestParameters.hideFields?default("N") != "Y">
<script language="JavaScript" type="text/javascript">
<!--//
document.lookuporder.orderId.focus();
//-->
</script>
</#if>
<#else>
  <div class="alert alert-danger">${uiLabelMap.OrderViewPermissionError}</div>
</#if>