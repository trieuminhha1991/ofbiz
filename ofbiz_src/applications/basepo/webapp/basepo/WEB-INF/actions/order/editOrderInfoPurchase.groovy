import java.sql.Timestamp;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.base.util.UtilMisc;
import com.olbius.basehr.util.MultiOrganizationUtil;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.base.util.UtilValidate;
import javolution.util.FastList;
import com.olbius.basepo.utils.*;
// for edit
if (UtilValidate.isNotEmpty(orderId)){
	if (UtilValidate.isNotEmpty(orderHeader)){
		List<GenericValue> listSuppliers = delegator.findList("OrderRole", EntityCondition.makeCondition(UtilMisc.toMap("orderId", orderId, "roleTypeId", "BILL_FROM_VENDOR")), null, null, null, false);
		if (!listSuppliers.isEmpty()){
			String supplierId = listSuppliers.get(0).getString("partyId");
			context.supplierId = supplierId;
			if (supplierId) context.defaultSupplierId = supplierId;
			
			partyGroupSupplier = delegator.findOne("PartyGroup", false, UtilMisc.toMap("partyId", supplierId));
			
			if (partyGroupSupplier) { 
				if (partyGroupSupplier.partyCode){ 
					context.defaultSupplierName = "[" + partyGroupSupplier.partyCode + "] " + partyGroupSupplier.groupName;
				} else { 
					context.defaultSupplierName = "[" + partyGroupSupplier.partyId + "] " + partyGroupSupplier.groupName;
				}
			}
		}
		String currencyUomId = orderHeader.currencyUom;
		context.defaultCurrencyUomId = currencyUomId;
		Timestamp shipBeforeDate = orderHeader.shipBeforeDate;
		Timestamp shipAfterDate = orderHeader.shipAfterDate;
		
		List<String> listStatusCanBeEdit = FastList.newInstance();
		listStatusCanBeEdit.add("ITEM_CREATED");
		listStatusCanBeEdit.add("ITEM_APPROVED");
		
		List<EntityCondition> listConds = FastList.newInstance();
		
		listConds.add(EntityCondition.makeCondition("orderId", orderId));
		listConds.add(EntityCondition.makeCondition("statusId", EntityOperator.IN, listStatusCanBeEdit));
		
		// List<GenericValue> listOrderItems = delegator.findList("OrderItemAndProductDetail", EntityCondition.makeCondition(listConds), null, null, null, false);
		List<GenericValue> listOrderItems = POUtil.getOrderItemEditable(delegator, orderId);
		context.listOrderItemEdits = listOrderItems;
		
		if (shipBeforeDate) context.defaultShipBeforeDate = shipBeforeDate;
		if (shipAfterDate) context.defaultShipAfterDate = shipAfterDate;
		
		String originFacilityId = orderHeader.originFacilityId;
		context.originFacilityId = originFacilityId;
		if (originFacilityId) { 
			context.defaultFacilityId = originFacilityId;
			originFacility = delegator.findOne("Facility", false, UtilMisc.toMap("facilityId", originFacilityId));
			context.originFacility = originFacility;
		}
		context.orderId = orderId;
	}
}