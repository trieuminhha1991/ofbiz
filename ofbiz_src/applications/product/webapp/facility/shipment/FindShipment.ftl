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
<script language="JavaScript" type="text/javascript">
<!-- //
function lookupShipments() {
    shipmentIdValue = document.lookupShipmentForm.shipmentId.value;
    if (shipmentIdValue.length > 1) {
        document.lookupShipmentForm.action = "<@ofbizUrl>ViewShipment</@ofbizUrl>";
    } else {
        document.lookupShipmentForm.action = "<@ofbizUrl>FindShipment</@ofbizUrl>";
    }
    document.lookupShipmentForm.submit();
}
// -->
</script>
<#--
<a  class="btn btn-small btn-primary" href="<@ofbizUrl>EditShipment</@ofbizUrl>">${uiLabelMap.ProductNewShipment}</a>
<div id="findOrders" class="widget-box olbius-extra">
    <div class="widget-header widget-header-small header-color-blue2">
    <h6>${uiLabelMap.ProductFindShipmentTitle}</h6>
        <div class="widget-toolbar">
        	<a ref="#" data-action="collapse">
        		<i class="icon-chevron-up"></i>
        	</a>
		</div>
    </div>
    <div class="widget-body">
    <div class="widget-body-inner">
    <div class="widget-main">
            <#if requestParameters.facilityId?has_content>
                <li class="list_style_none margin-top5"><a class="btn btn-mini btn-primary" href="<@ofbizUrl>quickShipOrder?facilityId=${requestParameters.facilityId}</@ofbizUrl>">${uiLabelMap.ProductQuickShipOrder}</a></li>
            </#if>       
        <form method="post" name="lookupShipmentForm" action="<@ofbizUrl>FindShipment</@ofbizUrl>">
            <input type="hidden" name="lookupFlag" value="Y" />
            <table cellspacing="0" cellpadding="2" class="basic-table">
              <tr>
                <td  align="right" >${uiLabelMap.ProductShipmentId}</td>
                <td width="5%">&nbsp;</td>
                <td><input type="text" name="shipmentId" value="${shipmentId?if_exists}" /></td>
              </tr>
              <tr>
                <td  align="right" >${uiLabelMap.ProductShipmentType}</td>
                <td width="5%">&nbsp;</td>
                <td>
                  <select name="shipmentTypeId">
                    <#if currentShipmentType?has_content>
                    <option value="${currentShipmentType.shipmentTypeId}">${currentShipmentType.get("description",locale)}</option>
                    <option value="${currentShipmentType.shipmentTypeId}">---</option>
                    </#if>
                    <option value="">${uiLabelMap.ProductAnyShipmentType}</option>
                    <#list shipmentTypes as shipmentType>
                      <option value="${shipmentType.shipmentTypeId}">${shipmentType.get("description",locale)}</option>
                    </#list>
                  </select>
                </td>
              </tr>
              <tr>
                <td  align="right" >${uiLabelMap.ProductOriginFacility}</td>
                <td width="5%">&nbsp;</td>
                <td>
                  <select name="originFacilityId">
                    <#if currentOriginFacility?has_content>
                    <option value="${currentOriginFacility.facilityId}">${currentOriginFacility.facilityName} [${currentOriginFacility.facilityId}]</option>
                    <option value="${currentOriginFacility.facilityId}">---</option>
                    </#if>
                    <option value="">${uiLabelMap.ProductAnyFacility}</option>
                    <#list facilities as facility>
                      <option value="${facility.facilityId}">${facility.facilityName} [${facility.facilityId}]</option>
                    </#list>
                  </select>
                </td>
              </tr>
              <tr>
                <td  align="right" >${uiLabelMap.ProductDestinationFacility}</td>
                <td width="5%">&nbsp;</td>
                <td>
                  <select name="destinationFacilityId">
                    <#if currentDestinationFacility?has_content>
                    <option value="${currentDestinationFacility.facilityId}">${currentDestinationFacility.facilityName} [${currentDestinationFacility.facilityId}]</option>
                    <option value="${currentDestinationFacility.facilityId}">---</option>
                    </#if>
                    <option value="">${uiLabelMap.ProductAnyFacility}</option>
                    <#list facilities as facility>
                      <option value="${facility.facilityId}">${facility.facilityName} [${facility.facilityId}]</option>
                    </#list>
                  </select>
                </td>
              </tr>
              <tr>
                <td  align="right" >${uiLabelMap.CommonStatus}</td>
                <td width="5%">&nbsp;</td>
                <td>
                  <select name="statusId">
                    <#if currentStatus?has_content>
                    <option value="${currentStatus.statusId}">${currentStatus.get("description",locale)}</option>
                    <option value="${currentStatus.statusId}">---</option>
                    </#if>
                    <option value="">${uiLabelMap.ProductSalesShipmentStatus}</option>
                    <#list shipmentStatuses as shipmentStatus>
                      <option value="${shipmentStatus.statusId}">${shipmentStatus.get("description",locale)}</option>
                    </#list>
                    <option value="">---</option>
                    <option value="">${uiLabelMap.ProductPurchaseShipmentStatus}</option>
                    <#list purchaseShipmentStatuses as shipmentStatus>
                      <option value="${shipmentStatus.statusId}">${shipmentStatus.get("description",locale)}</option>
                    </#list>
                    <option value="">---</option>
                    <option value="">${uiLabelMap.ProductOrderReturnStatus}</option>
                    <#list returnStatuses as returnStatus>
                      <#if returnStatus.statusId != "RETURN_REQUESTED">
                        <option value="${returnStatus.statusId}">${returnStatus.get("description",locale)}</option>
                      </#if>
                    </#list>
                  </select>
                </td>
              </tr>
              <tr>
                <td  align="right" >${uiLabelMap.ProductDateFilter}</td>
                <td width="5%">&nbsp;</td>
                <td>
                  <table cellspacing="0" class="basic-table">
                    <tr>
                      <td>
                        <@htmlTemplate.renderDateTimeField name="minDate" event="" action="" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS" value="${requestParameters.minDate?if_exists}" size="25" maxlength="30" id="minDate1" dateType="date" shortDateInput=false timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
                        <span >${uiLabelMap.CommonFrom}</span>
                      </td>
                    </tr>
                    <tr>
                      <td>
                        <@htmlTemplate.renderDateTimeField name="maxDate" event="" action="" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS" value="${requestParameters.maxDate?if_exists}" size="25" maxlength="30" id="maxDate1" dateType="date" shortDateInput=false timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
                        <span >${uiLabelMap.CommonThru}</span>
                      </td>
                    </tr>
                  </table>
                </td>
              </tr>
              <tr>
              <td></td>
              <td></td>
              <td><a  class="btn btn-small btn-primary" href="javascript:lookupShipments();"><i class="icon-search"> ${uiLabelMap.ProductFindShipment}</a></td>
              </tr>
            </table>
            
        </form>
    </div>
    </div>
    </div>
</div>-->
<#if shipmentList?exists>
<div id="findOrders" class="widget-box transparent no-bottom-border">
    <#--<div class="widget-header widget-header-small header-color-blue2">
    <h6>${uiLabelMap.ProductShipmentsFound}</h6>
        <div class="widget-toolbar">
        	<a ref="#" data-action="collapse">
        		<i class="icon-chevron-up"></i>
        	</a>
		</div>
    </div>-->
    <div class="widget-body">
    <div class="widget-body-inner">
    <div class="widget-main">
            
            <#if 0 < shipmentList?size>
            <p class="blue">${lowIndex} - ${highIndex} ${uiLabelMap.CommonOf} ${shipmentList?size}</p>
            <div>
            	<#if (viewIndex > 1)>
                    <a class="btn btn-mini btn-primary style-inline-block" href="<@ofbizUrl>FindShipment?VIEW_SIZE=${viewSize}&amp;VIEW_INDEX=${viewIndex-1}${paramList}&amp;lookupFlag=Y</@ofbizUrl>"><i class="icon-arrow-left icon-on-left"></i> ${uiLabelMap.CommonPrevious}</a>
                <#else>
                </#if>
                <#if (shipmentList?size > highIndex)>
                    <a class="btn btn-mini btn-primary style-inline-block float-right" href="<@ofbizUrl>FindShipment?VIEW_SIZE=${viewSize}&amp;VIEW_INDEX=${viewIndex+1}${paramList}&amp;lookupFlag=Y</@ofbizUrl>">${uiLabelMap.CommonNext} <i class="icon-arrow-right icon-on-right"></i></a></li>
                <#else>
                </#if>
       		</div>
            </#if>
        <table cellspacing="0" cellpadding="2" class="table table-striped table-hover table-bordered dataTable">
        <tr class="header-row">
          <td width="5%" style="font-weight:bold">${uiLabelMap.ProductShipmentId}</td>
          <td width="15%" style="font-weight:bold">${uiLabelMap.ProductShipmentType}</td>
          <td width="10%" style="font-weight:bold">${uiLabelMap.CommonStatus}</td>
          <td width="25%" style="font-weight:bold">${uiLabelMap.ProductOriginFacility}</td>
          <td width="25%" style="font-weight:bold">${uiLabelMap.ProductDestFacility}</td>
          <td width="15%" style="font-weight:bold">${uiLabelMap.ProductShipDate}</td>
          <td width="5%">&nbsp;</td>
        </tr>
        <#if shipmentList?has_content>
          <#assign alt_row = false>
          <#list shipmentList as shipment>
            <#assign originFacility = delegator.findOne("Facility", Static["org.ofbiz.base.util.UtilMisc"].toMap("facilityId", shipment.originFacilityId), true)?if_exists />
            <#assign destinationFacility = delegator.findOne("Facility", Static["org.ofbiz.base.util.UtilMisc"].toMap("facilityId", shipment.destinationFacilityId), true)?if_exists />
            <#assign statusItem = delegator.findOne("StatusItem", Static["org.ofbiz.base.util.UtilMisc"].toMap("statusId", shipment.statusId), true)?if_exists/>
            <#assign shipmentType = delegator.findOne("ShipmentType", Static["org.ofbiz.base.util.UtilMisc"].toMap("shipmentTypeId", shipment.shipmentTypeId), true)?if_exists/>
            <tr valign="middle"<#if alt_row> class="alternate-row"</#if>>
              <td><a href="<@ofbizUrl>ViewShipment?shipmentId=${shipment.shipmentId}</@ofbizUrl>" class="">${shipment.shipmentId}</a></td>
              <td style="font-weight:bold">${(shipmentType.get("description",locale))?default(shipmentType.shipmentTypeId?default(""))}</td>
              <td style="font-weight:bold">${(statusItem.get("description",locale))?default(statusItem.statusId?default("N/A"))}</td>
              <td style="font-weight:bold">${(originFacility.facilityName)?if_exists} [${shipment.originFacilityId?if_exists}]</td>
              <td style="font-weight:bold">${(destinationFacility.facilityName)?if_exists} [${shipment.destinationFacilityId?if_exists}]</td>
              <td><span style="white-space: nowrap;">${(shipment.estimatedShipDate.toString())?if_exists}</span></td>
              <td align="right">
                <a href="<@ofbizUrl>ViewShipment?shipmentId=${shipment.shipmentId}</@ofbizUrl>" class=" icon-eye-open open-sans btn btn-info btn-mini">${uiLabelMap.CommonView}</a>
              </td>
            </tr>
            <#-- toggle the row color -->
            <#assign alt_row = !alt_row>
          </#list>
        <#else>
          <tr>
            <td colspan="7"><p class="alert alert-info">${uiLabelMap.ProductNoShipmentsFound}.</p></td>
          </tr>
        </#if>
        </table>
        
    </div>
</div>
</div>
</div>
</#if>
