import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityUtil;

GenericValue shipment = null;
if (shipmentId) {
    shipment = delegator.findOne("Shipment", [shipmentId : shipmentId], false);
}
if (shipment != null) { 
	String statusId = shipment.getString("statusId");
	
    GenericValue status = delegator.findOne("StatusItem", [statusId : statusId], false);
    GenericValue orderHeader = delegator.findOne("OrderHeader", [orderId : shipment.primaryOrderId], false);

	List<EntityCondition> conds = new ArrayList<EntityCondition>();
	conds.add(EntityCondition.makeCondition("shipmentId", EntityOperator.EQUALS, shipmentId));
	EntityFindOptions findOpts = new EntityFindOptions();
	findOpts.setDistinct(true);
	
	List<GenericValue> listItems = new ArrayList<GenericValue>();
	listItems = delegator.findList("OrderShipmentDetail", EntityCondition.makeCondition(conds, EntityOperator.AND), null, null, findOpts, false);
	
	String shippingAddress = null;
	List<GenericValue> orderContactMechs = delegator.findList("OrderContactMech", EntityCondition.makeCondition(UtilMisc.toMap("orderId", orderHeader.orderId, "contactMechPurposeTypeId", "SHIPPING_LOCATION")), null, null, null, false);
	if (!orderContactMechs.isEmpty()){
		String shippingContactMechId = orderContactMechs.get(0).getString("contactMechId");
		address3 = delegator.findOne("PostalAddressFullNameDetail", UtilMisc.toMap("contactMechId", shippingContactMechId), false);
		shippingAddress = address3.fullName;
	}

	context.listItems = listItems;
	context.statusId = statusId;
	context.status = status;
	context.shipment = shipment;
	context.orderHeader = orderHeader;
	context.shippingAddress = shippingAddress;
}
