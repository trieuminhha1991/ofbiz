import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;

def orderIds = parameters.orderIds;

if (UtilValidate.isNotEmpty(orderIds)) {
	String[] dummy = orderIds.split(",");
	for (String s : dummy) {
		GenericValue orderHeader = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", s), false);
		if (UtilValidate.isNotEmpty(orderHeader)) {
			GenericValue facility = delegator.findOne("Facility", UtilMisc.toMap("facilityId", orderHeader.get("originFacilityId")), false);
			if (UtilValidate.isNotEmpty(facility)) {
				context.facility = facility;
				break;
			}
		}
	}
}