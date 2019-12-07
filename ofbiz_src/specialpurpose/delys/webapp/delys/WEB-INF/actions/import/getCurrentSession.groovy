import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;

shoppingCart = request.getSession().getAttribute("shoppingCart");

String productPlanId = parameters.productPlanId;
GenericValue planHeader = delegator.findOne("ProductPlanHeader", false, UtilMisc.toMap("productPlanId", productPlanId));
String internalPartyId = (String)planHeader.get("internalPartyId");

exprOrList = [];
exprList = [];
expr = EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "DELYS_RSM_GT");
exprOrList.add(expr);
expr = EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "DELYS_RSM_MT");
exprOrList.add(expr);

exprList.add(EntityCondition.makeCondition(exprOrList, EntityOperator.OR));
expr = EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, internalPartyId);
exprList.add(expr);
Cond = EntityCondition.makeCondition(exprList, EntityOperator.AND);

List<GenericValue> listProductStores = delegator.findList("ProductStoreRole", Cond, null, null, null, false);

List<GenericValue> listFacilitiesStores = new ArrayList<GenericValue>();
if (!listProductStores.isEmpty()){
	for (GenericValue store : listProductStores){
		List<GenericValue> listFacilityTmp = delegator.findList("ProductStoreFacility", EntityCondition.makeCondition(UtilMisc.toMap("productStoreId", (String)store.get("productStoreId"))), null, null, null, false);
		listFacilityTmp = EntityUtil.filterByDate(listFacilityTmp);
		
		if (!listFacilityTmp.isEmpty()){
			for (GenericValue fa : listFacilityTmp){
				if (!listFacilitiesStores.contains(fa)){
					listFacilitiesStores.add(fa);
				}
			}
		}
	}
}
List<GenericValue> listFacilities = new ArrayList<GenericValue>();
if (!listFacilitiesStores.isEmpty()){
	for (GenericValue fac : listFacilitiesStores){
		GenericValue facility = delegator.findOne("Facility", false, UtilMisc.toMap("facilityId", fac.get("facilityId")));
		listFacilities.add(facility);
	}
}
String supplierPartyId = shoppingCart.getAttribute("supplierPartyId");
String originOrderId = shoppingCart.getAttribute("originOrderId");
context.listFacilities = listFacilities;
context.shoppingCart = shoppingCart;
context.originOrderId = originOrderId;
context.orderTypeId = shoppingCart.orderType;