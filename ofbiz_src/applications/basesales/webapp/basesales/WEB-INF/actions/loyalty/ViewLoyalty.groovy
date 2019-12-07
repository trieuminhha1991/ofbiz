import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import java.util.List;
import org.ofbiz.entity.GenericValue;

loyaltyId = parameters.loyaltyId;
if (loyaltyId) {
	context.loyaltyId = loyaltyId;
	loyalty = delegator.findOne("Loyalty", UtilMisc.toMap("loyaltyId", loyaltyId), false);
	context.loyalty = loyalty;
	
	List<EntityCondition> listCond = new ArrayList<EntityCondition>();
	listCond.add(EntityCondition.makeCondition("loyaltyId", loyaltyId));
	List<EntityCondition> listCondOr = new ArrayList<EntityCondition>();
	listCondOr.add(EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN, nowTimestamp));
	listCondOr.add(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null));
	listCond.add(EntityCondition.makeCondition(listCondOr, EntityOperator.OR));
	
	List<GenericValue> productStoreLoyaltyAppl = delegator.findByAnd("ProductStoreLoyaltyAppl", ["loyaltyId" : loyaltyId], ["sequenceNum", "loyaltyId"], false);
	List<GenericValue> loyaltyRoleTypeApply = delegator.findList("LoyaltyRoleTypeAppl", EntityCondition.makeCondition(listCond, EntityOperator.AND), null, null, null, false);
	List<GenericValue> loyaltyRules = delegator.findByAnd("LoyaltyRule", ["loyaltyId" : loyaltyId], ["ruleName"], false);
	List<GenericValue> loyaltyProductCategories = delegator.findByAnd("LoyaltyCategory", 
		["loyaltyId" : loyaltyId, "loyaltyRuleId" : "_NA_", "loyaltyCondSeqId" : "_NA_"], null, false);
	List<GenericValue> loyaltyProducts = delegator.findByAnd("LoyaltyProduct", 
		["loyaltyId" : loyaltyId, "loyaltyRuleId" : "_NA_", "loyaltyCondSeqId" : "_NA_"], null, false);
	List<GenericValue> productStores = delegator.findByAnd("ProductStore", null, null, false);
	List<GenericValue> roleTypes = delegator.findByAnd("RoleType", null, null, false);
	
	context.productStoreLoyaltyAppl = productStoreLoyaltyAppl;
	context.loyaltyRoleTypeApply = loyaltyRoleTypeApply;
	context.loyaltyRules = loyaltyRules;
	context.loyaltyProductCategories = loyaltyProductCategories;
	context.loyaltyProducts = loyaltyProducts;
	context.productStores = productStores;
	context.roleTypes = roleTypes;
}