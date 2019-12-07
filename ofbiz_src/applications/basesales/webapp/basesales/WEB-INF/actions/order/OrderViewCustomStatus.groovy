
import java.util.*;

import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.base.util.*;

import javolution.util.FastList;

import org.ofbiz.order.order.OrderReadHelper;

boolean isShipFromFacilityConsign = false;
String orderStatusMgs = "";
GenericValue favorDistributorDelivery = null;
boolean isFavorDisDeliveryNoShipping = false;
boolean isCustomer = false;

if (orderHeader) {
	boolean editLogAllow = true;
	
	orderStatus = orderHeader.getRelatedOne("StatusItem");
	if (orderStatus) {
		orderStatusMgs = orderStatus.get("description", locale);
	}
	/*orderStatus = orderHeader.getRelatedOne("StatusItem");
	if (orderStatus) {
		String orderStatusMgs = orderStatus.get("description", locale);
		if (UtilValidate.isEmpty(orderHeader.isFavorDelivery)) {
			if ("ORDER_CREATED".equals(orderHeader.statusId)) {
				orderStatusMgs += " (" + UtilProperties.getMessage("SalesUiLabels", "DAWaitAccConfirm", locale) + ")";
			} else {
				orderStatusMgs += " (" + UtilProperties.getMessage("SalesUiLabels", "DAWaitPOConfirm", locale) + ")";
			}
		} else {
			Map<String, Object> resultTmp1 = dispatcher.runSync("getDetailStatusOfOrder", UtilMisc.toMap("orderId", orderHeader.orderId, "userLogin", userLogin, "locale", locale));
			String extendStatus = (String)resultTmp1.get("statusDetail");
			if (UtilValidate.isNotEmpty(extendStatus)){
				if (!"NONE".equals(extendStatus)){
					editLogAllow = false;
					if ("SALES_ORDER".equals(orderHeader.orderTypeId)){
						if ("DLV_APPROVED_PARTIAL".equals(extendStatus)){
							extendStatus = UtilProperties.getMessage("LogisticsUiLabels", "SalesOrderApprovedApart", locale);
						} else if ("DLV_APPROVED_ALL".equals(extendStatus)){
							extendStatus = UtilProperties.getMessage("LogisticsUiLabels", "ExportDeliveryCreatedDone", locale);
						} else if ("EXPORTED_PARTIAL".equals(extendStatus)){
							extendStatus = UtilProperties.getMessage("LogisticsUiLabels", "SalesOrderExportedApart", locale);
						} else if ("DELIVERED_PARTIAL".equals(extendStatus)){
							extendStatus = UtilProperties.getMessage("LogisticsUiLabels", "SalesOrderDeliveredApart", locale);
						}
						if (extendStatus != ""){
							orderStatusMgs = orderStatusMgs + " (" + extendStatus + ")";
						}
					}
				}
			}
		}
		context.orderStatusMgs = orderStatusMgs;
	}*/
	context.editLogAllow = editLogAllow;
	
	String isEditing = null;
	GenericValue orderAttrIsEditing = delegator.findOne("OrderAttribute", UtilMisc.toMap("orderId", orderHeader.orderId, "attrName", "isEditing"), false);
	if (orderAttrIsEditing != null) isEditing = orderAttrIsEditing.getString("attrValue");
	context.isEditing = isEditing;
	
	if ("Y".equals(orderHeader.isFavorDelivery) && orderHeader.get("originFacilityId") != null) {
		GenericValue billFromVendorGV = EntityUtil.getFirst(delegator.findByAnd("OrderRole", UtilMisc.toMap("orderId", orderHeader.orderId, "roleTypeId", "BILL_FROM_VENDOR"), null, false));
		String billFromVendorId = billFromVendorGV != null ? billFromVendorGV.partyId : null;
		if (billFromVendorId) {
			GenericValue facilityGV = delegator.findOne("Facility", UtilMisc.toMap("facilityId", orderHeader.originFacilityId), false);
			if (facilityGV != null && facilityGV.ownerPartyId == billFromVendorId) {
				orderReadHelper = new OrderReadHelper(orderHeader);
		        displayParty = orderReadHelper.getPlacingParty();
		        
		        List<EntityCondition> listConds = FastList.newInstance();
		        listConds.add(EntityCondition.makeCondition("facilityId", orderHeader.originFacilityId));
		        listConds.add(EntityCondition.makeCondition("roleTypeId", "FACILITY_ADMIN"));
		        listConds.add(EntityUtil.getFilterByDateExpr());
		        GenericValue facilityPartyManager = EntityUtil.getFirst(delegator.findList("FacilityParty", EntityCondition.makeCondition(listConds), null, null, null, false));
		        if (facilityPartyManager) {
		        	String facilityAdminId = facilityPartyManager.partyId;
		        	favorDistributorDelivery = delegator.findOne("PartyFullNameDetailSimple", UtilMisc.toMap("partyId", facilityAdminId), false);
		        	if (facilityAdminId == displayParty.partyId) {
			        	isShipFromFacilityConsign = true;
			        	isFavorDisDeliveryNoShipping = true;
			        	orderStatusMgs += " (" + UtilProperties.getMessage("BaseSalesUiLabels", "BSShipFromFacilityConsign", locale) + ")";
			        } else {
			        	isShipFromFacilityConsign = true;
			        	orderStatusMgs += " (" + UtilProperties.getMessage("BaseSalesUiLabels", "BSFavorDistributorDelivery", locale) + ")";
			        }
		        }
			}
		}
	}
	
	// check userlogin is customer or not?
	GenericValue billToCustomerGV = EntityUtil.getFirst(delegator.findByAnd("OrderRole", UtilMisc.toMap("orderId", orderHeader.orderId, "roleTypeId", "BILL_TO_CUSTOMER", "partyId", userLogin.get("partyId")), null, false));
	if (UtilValidate.isNotEmpty(billToCustomerGV)) {
		isCustomer = true;
	}
}
context.orderStatusMgs = orderStatusMgs;
context.isShipFromFacilityConsign = isShipFromFacilityConsign;
context.favorDistributorDelivery = favorDistributorDelivery;
context.isFavorDisDeliveryNoShipping = isFavorDisDeliveryNoShipping;
context.isCustomer = isCustomer;
