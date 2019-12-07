import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.model.*;
import org.ofbiz.entity.util.EntityUtil;

uiLabelMap = UtilProperties.getResourceBundleMap("DelysLogisticsUiLabels", locale);

deliveryId = parameters.deliveryId;
String orderId = null;
String returnId = null;
if (deliveryId != null && !"".equals(deliveryId)){
	GenericValue delivery = delegator.findOne("Delivery", false, UtilMisc.toMap("deliveryId", deliveryId));
	orderId = (String)delivery.get("orderId");
	returnId = (String)delivery.get("returnId");
} else {
	orderId = parameters.orderId;
	returnId = parameters.returnId;
}
String receiveType = null;
String countryGeoId = null;
if (orderId != null){
	List<GenericValue> listOrderAndRoles = new ArrayList<GenericValue>();
	listOrderAndRoles = delegator.findList("PartyOrderAddressPurpose", EntityCondition.makeCondition(UtilMisc.toMap("orderId", orderId, "contactMechPurposeTypeId", "PRIMARY_LOCATION", "roleTypeId", "BILL_FROM_VENDOR")), null, null, null, false);
	if (!listOrderAndRoles.isEmpty()){
		String geoId = (String)listOrderAndRoles.get(0).countryGeoId;
		if (geoId != null){
			countryGeoId = geoId;
			if ("VNM".equals(geoId)){
				receiveType = "${uiLabelMap.ReceiveDomestics}";
			} else {
				receiveType = "${uiLabelMap.ReceiveImport}";
			}
		}
	}
} else if (returnId != null) {
	receiveType = "${uiLabelMap.ReceiveReturn}";
}
context.receiveType = receiveType;
context.countryGeoId = countryGeoId;
