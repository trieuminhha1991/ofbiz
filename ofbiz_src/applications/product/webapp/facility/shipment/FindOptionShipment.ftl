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
<#--<a  class="btn btn-small btn-primary" href="<@ofbizUrl>EditShipment</@ofbizUrl>">${uiLabelMap.ProductNewShipment}</a>-->
<div id="findOrders" class="widget-box transparent no-bottom-border">
    <#--<div class="widget-header widget-header-small header-color-blue2">
    <h6>${uiLabelMap.ProductFindShipmentTitle}</h6>
        <div class="widget-toolbar">
        	<a ref="#" data-action="collapse">
        		<i class="icon-chevron-up"></i>
        	</a>
		</div>
    </div>-->
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
              <td><a  class="btn btn-small btn-primary" href="javascript:lookupShipments();"><i class="open-sans icon-search"> ${uiLabelMap.ProductFindShipment}</a></td>
              </tr>
            </table>
            
        </form>
    </div>
    </div>
    </div>
</div>