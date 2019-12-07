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

<#if security.hasEntityPermission("ORDERMGR", "_CREATE", session) || security.hasEntityPermission("ORDERMGR", "_PURCHASE_CREATE", session)>

<#-- Purchase Orders -->

<#if facilityMaps?exists>
            <form method="post" action="<@ofbizUrl>finalizeOrder</@ofbizUrl>" name="checkoutsetupform">
            <div class="widget-box olbius-extra ">
        <div class="widget-header widget-header-small header-color-blue2">
            <h6>${uiLabelMap.OrderPurchaseOrder}</h6>
            <div class="widget-toolbar">
            	<a href="#" data-action="collapse">
            		<i class="icon-chevron-up"></i>
            	</a>
            </div>
        </div>
        <div class="widget-body">
        <div class="widget-body-inner">
    	<div class="widget-main">
            <input type="hidden" name="finalizeMode" value="ship"/>
            <#if (cart.getShipGroupSize() > 1)>
            <input type="hidden" name="finalizeReqShipGroups" value="true"/>
            </#if>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class="boxboutside">
        <tr>
          <td>
          <a href="<@ofbizUrl>setShipping?createNewShipGroup=Y</@ofbizUrl>" class="btn btn-mini btn-primary open-sans icon-plus-sign">${uiLabelMap.OrderCreateShipGroup}</a>
          

<#list 1..cart.getShipGroupSize() as currIndex>
<#assign shipGroupIndex = currIndex - 1>

            <table width="100%" border="0" cellpadding="1" cellspacing="0">
                <tr>
                  <td colspan="4">
                    <h3 class="header smaller lighter green">${uiLabelMap.OrderShipGroup} ${uiLabelMap.CommonNbr} ${currIndex}</h3>
                  </td>
                </tr>
                <#assign i = 0>
                <#list facilityMaps as facilityMap>
                <#assign facility = facilityMap.facility>
                <#assign facilityContactMechList = facilityMap.facilityContactMechList>
                <tr>
                  <td colspan="4">
                    <div>${uiLabelMap.FacilityFacility}: ${facility.facilityName?if_exists} [${facility.facilityId}]</div>
                  </td>
                </tr>
                <tr><td colspan="4"><hr /></td></tr>

                <#-- company postal addresses -->

                <#if facilityContactMechList?has_content>
                <#list facilityContactMechList as shippingContactMech>
                  <#if shippingContactMech.postalAddress?exists>
                  <#assign shippingAddress = shippingContactMech.postalAddress>
                  <tr>
                    <td valign="top" nowrap="nowrap">
                    	<label>
							<input type="radio" name="${shipGroupIndex?default("0")}_shipping_contact_mech_id" value="${shippingAddress.contactMechId}" <#if i == 0>checked</#if> /><span class="lbl"></span>
						</label>
                    </td>
                    <td nowrap="nowrap">&nbsp;&nbsp;&nbsp;&nbsp;</td>
                    <td valign="top" width="100%" nowrap="nowrap">
                      <div>
                        <#if shippingAddress.toName?has_content><b>${uiLabelMap.CommonTo}:</b>&nbsp;${shippingAddress.toName}<br /></#if>
                        <#if shippingAddress.attnName?has_content><b>${uiLabelMap.CommonAttn}:</b>&nbsp;${shippingAddress.attnName}<br /></#if>
                        <#if shippingAddress.address1?has_content>${shippingAddress.address1}<br /></#if>
                        <#if shippingAddress.address2?has_content>${shippingAddress.address2}<br /></#if>
                        <#if shippingAddress.city?has_content>${shippingAddress.city}</#if>
                        <#if shippingAddress.stateProvinceGeoId?has_content><br />${shippingAddress.stateProvinceGeoId}</#if>
                        <#if shippingAddress.postalCode?has_content><br />${shippingAddress.postalCode}</#if>
                        <#if shippingAddress.countryGeoId?has_content><br />${shippingAddress.countryGeoId}</#if>
                      </div>
                    </td>
                    <td>
                      <div><a href="/facility/control/EditContactMech?facilityId=${facility.facilityId}&amp;contactMechId=${shippingAddress.contactMechId}" target="_blank" class="btn btn-mini btn-primary open-sans"><i class="icon-ok"></i>&nbsp;${uiLabelMap.CommonUpdate}</a></div>
                    </td>
                  </tr>
                  <#if shippingContactMech_has_next>
                  <tr><td colspan="4"><hr /></td></tr>
                  </#if>
                  </#if>
                  <#assign i = i + 1>
                </#list>
                <#else>
                  <tr>
                    <td colspan="4">
                      <div>
                        ${uiLabelMap.CommonNoContactInformationOnFile}:
                        <a href="/facility/control/EditContactMech?facilityId=${facility.facilityId}&amp;preContactMechTypeId=POSTAL_ADDRESS" target="_blank" class="btn btn-mini btn-primary open-sans icon-plus-sign">${uiLabelMap.CommonNew}</a>
                      </div>
                    </td>
                </#if>
                </#list>
            </table>
</#list>
          </td>
        </tr>
      </table>
      </div>
      </div>
      </div>
      </div>
            </form>

<#else>

<#-- Sales Orders -->

            <form method="post" action="<@ofbizUrl>finalizeOrder</@ofbizUrl>" name="checkoutsetupform">
            <div class="widget-box olbius-extra ">
        <div class="widget-header widget-header-small header-color-blue2">
            <h6>${uiLabelMap.OrderSalesOrder}</h6>
            <div class="widget-toolbar">
            	<a href="#" data-action="collapse">
            		<i class="icon-chevron-up"></i>
            	</a>
            </div>
        </div>
        <div class="widget-body">
        <div class="widget-body-inner">
    	<div class="widget-main">
            <input type="hidden" name="finalizeMode" value="ship"/>
            <#if (cart.getShipGroupSize() > 1)>
            <input type="hidden" name="finalizeReqShipGroups" value="true"/>
            </#if>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class="boxoutside">
        <tr>
          <td>
<a href="<@ofbizUrl>setShipping?createNewShipGroup=Y</@ofbizUrl>" class="btn btn-mini btn-primary open-sans icon-plus-sign">${uiLabelMap.CommonCreateNew} ${uiLabelMap.OrderShipGroup}</a>
<a href="<@ofbizUrl>EditShipAddress</@ofbizUrl>" class="btn btn-mini btn-primary open-sans icon-plus-sign">${uiLabelMap.OrderCreateShippingAddress}</a>
<#list 1..cart.getShipGroupSize() as currIndex>
<#assign shipGroupIndex = currIndex - 1>

<#assign currShipContactMechId = cart.getShippingContactMechId(shipGroupIndex)?if_exists>
<#assign supplierPartyId = cart.getSupplierPartyId(shipGroupIndex)?if_exists>
<#assign facilityId = cart.getShipGroupFacilityId(shipGroupIndex)?if_exists>
            <hr />
            <table width="100%" border="0" cellpadding="1" cellspacing="0">
              <tr>
                <td colspan="3">
                    <h3 class="header smaller lighter blue">${uiLabelMap.OrderShipGroup} ${uiLabelMap.CommonNbr} ${currIndex}</h3>
                </td>
              </tr>
              <tr>
                <td colspan="3">
                    <div>
                      ${uiLabelMap.PartySupplier}:
                      <select name="${shipGroupIndex?default("0")}_supplierPartyId">
                        <option value=""></option>
                        <#list suppliers as supplier>
                          <option value="${supplier.partyId}"<#if supplierPartyId?exists><#if supplier.partyId == supplierPartyId> selected="selected"</#if></#if>>${Static["org.ofbiz.party.party.PartyHelper"].getPartyName(supplier, true)}</option>
                        </#list>
                      </select>
                      ${uiLabelMap.ProductReserveInventoryFromFacility}:
                      <select name="${shipGroupIndex?default("0")}_shipGroupFacilityId">
                        <option value=""></option>
                        <#list productStoreFacilities as productStoreFacility>
                          <#assign facility = productStoreFacility.getRelatedOne("Facility", false)>
                          <option value="${productStoreFacility.facilityId}"<#if facilityId?exists><#if productStoreFacility.facilityId == facilityId> selected="selected"</#if></#if>>${facility.facilityName?if_exists} </option>
                        </#list>
                      </select>
                    </div>
                </td>
              </tr>
            <#if shippingContactMechList?has_content>
                <tr><td colspan="3"><hr /></td></tr>
                <#assign i = 0>
                <#list shippingContactMechList as shippingContactMech>
                  <#assign shippingAddress = shippingContactMech.getRelatedOne("PostalAddress", false)>
                  <#if currShipContactMechId?exists && currShipContactMechId?has_content>
                      <#if currShipContactMechId == shippingContactMech.contactMechId>
                        <#assign checkedValue = "checked='checked'">
                      <#else>
                        <#assign checkedValue = "">
                      </#if>
                  <#else>
                      <#if i == 0>
                          <#assign checkedValue = "checked='checked'">
                      <#else>
                          <#assign checkedValue = "">
                      </#if>
                  </#if>
                  <tr>
                    <td valign="top" width="10%" nowrap="nowrap">
                    	<label>
							<input type="radio" name="${shipGroupIndex?default("0")}_shipping_contact_mech_id" ${checkedValue} /><span class="lbl">${shippingAddress.contactMechId}</span>
						</label>
                    </td>
                    <td valign="top" width="30%" nowrap="nowrap">
                      <div>
                        <#if shippingAddress.toName?has_content><b>${uiLabelMap.CommonTo}:</b>&nbsp;${shippingAddress.toName}<br /></#if>
                        <#if shippingAddress.attnName?has_content><b>${uiLabelMap.CommonAttn}:</b>&nbsp;${shippingAddress.attnName}<br /></#if>
                        <#if shippingAddress.address1?has_content>${shippingAddress.address1}<br /></#if>
                        <#if shippingAddress.address2?has_content>${shippingAddress.address2}<br /></#if>
                        <#if shippingAddress.city?has_content>${shippingAddress.city}</#if>
                        <#if shippingAddress.stateProvinceGeoId?has_content><br />${shippingAddress.stateProvinceGeoId}</#if>
                        <#if shippingAddress.postalCode?has_content><br />${shippingAddress.postalCode}</#if>
                        <#if shippingAddress.countryGeoId?has_content><br />${shippingAddress.countryGeoId}</#if>
                      </div>
                    </td>
                    <td>
                      <div><a href="/partymgr/control/editcontactmech?partyId=${orderParty.partyId}&amp;contactMechId=${shippingContactMech.contactMechId}" target="_blank" class="btn btn-mini btn-primary"><i class="icon-ok"></i>&nbsp;${uiLabelMap.CommonUpdate}</a></div>
                    </td>
                  </tr>
                  <#if shippingContactMech_has_next>
                  <tr><td colspan="3"><hr /></td></tr>
                  </#if>
                  <#assign i = i + 1>
                </#list>
            </#if>
            <#if shipToPartyShippingContactMechList?has_content>
                <tr><td colspan="3"><hr /></td></tr>
                <tr><td colspan="3">${uiLabelMap.OrderShipToAnotherParty}: <b>${Static["org.ofbiz.party.party.PartyHelper"].getPartyName(shipToParty)}</b></td></tr>
                <tr><td colspan="3"><hr /></td></tr>
                <#list shipToPartyShippingContactMechList as shippingContactMech>
                  <#assign shippingAddress = shippingContactMech.getRelatedOne("PostalAddress", false)>
                  <tr>
                    <td valign="top" width="1%" nowrap="nowrap">
                    	<label>
							<input type="radio" name="${shipGroupIndex?default("0")}_shipping_contact_mech_id"/><span class="lbl">${shippingAddress.contactMechId}</span>
						</label>
                    </td>
                    <td valign="top" width="99%" nowrap="nowrap">
                      <div>
                        <#if shippingAddress.toName?has_content><b>${uiLabelMap.CommonTo}:</b>&nbsp;${shippingAddress.toName}<br /></#if>
                        <#if shippingAddress.attnName?has_content><b>${uiLabelMap.CommonAttn}:</b>&nbsp;${shippingAddress.attnName}<br /></#if>
                        <#if shippingAddress.address1?has_content>${shippingAddress.address1}<br /></#if>
                        <#if shippingAddress.address2?has_content>${shippingAddress.address2}<br /></#if>
                        <#if shippingAddress.city?has_content>${shippingAddress.city}</#if>
                        <#if shippingAddress.stateProvinceGeoId?has_content><br />${shippingAddress.stateProvinceGeoId}</#if>
                        <#if shippingAddress.postalCode?has_content><br />${shippingAddress.postalCode}</#if>
                        <#if shippingAddress.countryGeoId?has_content><br />${shippingAddress.countryGeoId}</#if>
                      </div>
                    </td>
                    <td>&nbsp;</td>
                  </tr>
                  <#if shippingContactMech_has_next>
                  <tr><td colspan="3"><hr /></td></tr>
                  </#if>
                </#list>
            </#if>
            </table>
	</#list>
          </td>
        </tr>
      </table>
</div>
</div>
</div>
</div>
            </form>
</#if>



    <#-- select a party id to ship to instead -->

    <form method="post" action="setShipping" name="partyshipform">
	<div class="widget-box olbius-extra ">
        <div class="widget-header widget-header-small header-color-blue2">
            <h6>${uiLabelMap.OrderShipToAnotherParty}</h6>
            <div class="widget-toolbar">
            	<a href="#" data-action="collapse">
            		<i class="icon-chevron-up"></i>
            	</a>
            </div>
        </div>
        <div class="widget-body">
        <div class="widget-body-inner">
    	<div class="widget-main">
          <table width="100%" border='0' cellspacing='0' cellpadding='0' class='boxbottom'>
            <tr><td colspan="4">&nbsp;</td></tr>
            <tr>
              <td>&nbsp;</td>
              <td align='right' valign='middle' nowrap="nowrap"><div class='tableheadtext'>${uiLabelMap.PartyPartyId}</div></td>
              <td>&nbsp;</td>
              <td valign='middle'>
                <div class='tabletext'>
                  <@htmlTemplate.lookupField value='${thisPartyId?if_exists}' formName="partyshipform" name="shipToPartyId" id="shipToPartyId" fieldFormName="LookupPartyName"/>
                  <button type="submit" class="btn btn-primary btn-small open-sans margin-top-nav-10" name="submitButton">Continue <i class="icon-arrow-right icon-on-right"></i></button>
                </div>
              </td>
            </tr>
          </table>
        </div>
        </div>
        </div>
      </div>
    </form>
<#else>
  <p class="alert alert-danger">${uiLabelMap.OrderViewPermissionError}</p>
</#if>
