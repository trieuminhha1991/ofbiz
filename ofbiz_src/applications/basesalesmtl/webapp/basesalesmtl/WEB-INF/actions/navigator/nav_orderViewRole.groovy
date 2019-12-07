import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil

import com.olbius.basesales.util.SalesPartyUtil;

import javolution.util.FastList;;

boolean isOwnerDistributor = false;
boolean isSalesExecutive = false;
boolean isSalesSupViewable = false;
if (security.hasEntityPermission("SALESORDER_DIS", "_VIEW", session)) {
	String orderId = parameters.orderId;
	if (orderId) {
		List<GenericValue> orderRoles = delegator.findByAnd("OrderRole", ["orderId": orderId], null, false);
		if (orderRoles) {
			GenericValue payToPartyRole = EntityUtil.getFirst(EntityUtil.filterByAnd(orderRoles, UtilMisc.toMap("roleTypeId": "BILL_FROM_VENDOR", "partyId": userLogin.partyId)));
			if (payToPartyRole) {
				isOwnerDistributor = true;
			}
			if (!isOwnerDistributor) {
				GenericValue customerRole = EntityUtil.getFirst(EntityUtil.filterByAnd(orderRoles, UtilMisc.toMap("roleTypeId": "PLACING_CUSTOMER")));
				/*if (customerRole) {
					List<EntityCondition> listConds = FastList.newInstance();
					listConds.add(EntityCondition.makeCondition("partyIdFrom", userLogin.partyId));
					listConds.add(EntityCondition.makeCondition("partyIdTo", customerRole.partyId));
					listConds.add(EntityCondition.makeCondition("roleTypeIdFrom", "SALES_EXECUTIVE"));
					listConds.add(EntityCondition.makeCondition("roleTypeIdTo", "CUSTOMER"));
					listConds.add(EntityCondition.makeCondition("partyRelationshipTypeId", "SALES_REP_REL"));
					listConds.add(EntityUtil.getFilterByDateExpr());
					List<GenericValue> salesToCustomerRels = delegator.findList("PartyRelationship", EntityCondition.makeCondition(listConds), null, null, null, false);
					if (salesToCustomerRels) {
						isSalesExecutive = true;
					}
				}*/
				if(customerRole){
					List<EntityCondition> listConds = FastList.newInstance();
					listConds.add(EntityCondition.makeCondition("salesmanId", userLogin.partyId));
					listConds.add(EntityCondition.makeCondition("partyId", customerRole.partyId));
					List<GenericValue> salesToCustomerRels = delegator.findList("PartyCustomer", EntityCondition.makeCondition(listConds), null, null, null, false);
					if (salesToCustomerRels) {
						isSalesExecutive = true;
					}
				}
				
				if (!isSalesExecutive) {
					if (SalesPartyUtil.isSalessup(delegator, userLogin.partyId)) {
						GenericValue orderHeader = delegator.findOne("OrderHeader", ["orderId": orderId], false);
						if (orderHeader != null) {
							List<EntityCondition> conds = FastList.newInstance();
							conds.add(EntityCondition.makeCondition("productStoreId", orderHeader.productStoreId));
							conds.add(EntityCondition.makeCondition("roleTypeId", "SELLER"));
							conds.add(EntityUtil.getFilterByDateExpr());
							List<GenericValue> productStoreRoles = delegator.findList("ProductStoreRole", EntityCondition.makeCondition(conds), null, null, null, false);
							if (UtilValidate.isNotEmpty(productStoreRoles)) {
								isSalesSupViewable = true;
							}
						}
					}
					/*if (orderHeader != null && (userLogin.userLoginId == orderHeader.createdBy)) {
						isCreatedBy = true;
					}*/
				}
			}
		}
	}
}
context.isOwnerDistributor = isOwnerDistributor;
context.isSalesExecutive = isSalesExecutive;
boolean hasOrderSalesDisView = false;
if (isOwnerDistributor || isSalesExecutive || isSalesSupViewable) {
	hasOrderSalesDisView = true;
}
context.hasOrderSalesDisView = hasOrderSalesDisView;
