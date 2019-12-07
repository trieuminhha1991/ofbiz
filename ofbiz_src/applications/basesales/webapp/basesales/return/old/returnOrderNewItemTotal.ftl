
<#if returnHeader?has_content>
  	<#if returnHeader.destinationFacilityId?has_content && returnHeader.statusId == "RETURN_ACCEPTED" && returnHeader.returnHeaderTypeId?starts_with("CUSTOMER_")>
    	<#list returnShipmentIds as returnShipmentId>
    		<#if notOrderComponent?has_content>
	    		<#--<a href="<@ofbizUrl>ViewShipment?shipmentId=${returnShipmentId.shipmentId}${externalKeyParam}</@ofbizUrl>" class="btn btn-small btn-primary"><i class="fa-truck"></i> ${uiLabelMap.ProductShipmentId} ${returnShipmentId.shipmentId}</a>-->
	      		<a href="<@ofbizUrl>ReceiveReturn?facilityId=${returnHeader.destinationFacilityId}&amp;returnId=${returnHeader.returnId?if_exists}&amp;shipmentId=${returnShipmentId.shipmentId}${externalKeyParam}</@ofbizUrl>" class="btn btn-small btn-primary"><i class="icon-cloud-download"></i>${uiLabelMap.OrderReceiveReturn}</a>
    		<#else>
	    		<a href="/facility/control/ViewShipment?shipmentId=${returnShipmentId.shipmentId}${externalKeyParam}" class="btn btn-small btn-primary"><i class="fa-truck"></i> ${uiLabelMap.ProductShipmentId} ${returnShipmentId.shipmentId}</a>
	      		<a href="/facility/control/ReceiveReturn?facilityId=${returnHeader.destinationFacilityId}&amp;returnId=${returnHeader.returnId?if_exists}&amp;shipmentId=${returnShipmentId.shipmentId}${externalKeyParam}" class="btn btn-small btn-primary"><i class="icon-cloud-download"></i>${uiLabelMap.OrderReceiveReturn}</a>
    		</#if>
    	</#list>
  	<#elseif returnHeader.statusId == "SUP_RETURN_ACCEPTED" && returnHeader.returnHeaderTypeId == "VENDOR_RETURN">
     	<#if returnShipmentIds?has_content>
       	<#list returnShipmentIds as returnShipmentId>
         	<a href="/facility/control/ViewShipment?shipmentId=${returnShipmentId.shipmentId}${externalKeyParam}" class="buttontext">${uiLabelMap.ProductShipmentId} ${returnShipmentId.shipmentId}</a>
       	</#list>
     <#else>
       <a href="/facility/control/EditShipment?primaryReturnId=${returnHeader.returnId}&amp;partyIdTo=${toPartyId}&amp;statusId=SHIPMENT_INPUT&amp;shipmentTypeId=PURCHASE_RETURN" class="buttontext">${uiLabelMap.OrderCreateReturnShipment}</a>
     </#if>
  </#if>
</#if>

<#-- if we're called with loadOrderItems or createReturn, then orderId would exist -->
<#if !requestParameters.orderId?exists>
	<#include "returnOrderNewItemExisted.ftl"/>
<#-- if no requestParameters.orderId exists, then show list of items -->
<#else>
    <#assign selectAllFormName = "returnItems"/>
    <form name="returnItems" method="post" action="<@ofbizUrl>createReturnItemsCustomer</@ofbizUrl>">
		<input type="hidden" name="returnId" value="${returnId?if_exists}" />
      	<input type="hidden" name="orderId" value="${orderId?if_exists}" />
      	<input type="hidden" name="_useRowSubmit" value="Y" />
      	<#include "returnOrderNewItemInc.ftl"/>
    </form>
</#if>
