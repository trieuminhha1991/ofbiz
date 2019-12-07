import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import java.util.List;
import org.ofbiz.entity.GenericValue;

productPromoId = parameters.productPromoId;
if (productPromoId) {
	context.productPromoId = productPromoId;
	productPromo = delegator.findOne("ProductPromoExt", UtilMisc.toMap("productPromoId", productPromoId), false);
	context.productPromo = productPromo;
	
	// Don't Understand - analysis after
	List<EntityCondition> listCond = new ArrayList<EntityCondition>();
	listCond.add(EntityCondition.makeCondition("productPromoId", productPromoId));
	List<EntityCondition> listCondOr = new ArrayList<EntityCondition>();
	listCondOr.add(EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN, nowTimestamp));
	listCondOr.add(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null));
	listCond.add(EntityCondition.makeCondition(listCondOr, EntityOperator.OR));
	
	List<GenericValue> productStorePromoAppl = delegator.findList("ProductStorePromoExtAppl", EntityCondition.makeCondition(listCond, EntityOperator.AND), null, UtilMisc.toList("sequenceNum", "productPromoId"), null, false);
	List<GenericValue> promoRoleTypeApply = delegator.findList("ProductPromoExtRoleTypeAppl", EntityCondition.makeCondition(listCond, EntityOperator.AND), null, null, null, false);
	
	List<GenericValue> productPromoRules = delegator.findByAnd("ProductPromoExtRule", ["productPromoId" : productPromoId], ["ruleName"], false);
	List<GenericValue> promoProductPromoCategories = delegator.findByAnd("ProductPromoExtCategory", 
		["productPromoId" : productPromoId, "productPromoRuleId" : "_NA_", "productPromoActionSeqId" : "_NA_", "productPromoCondSeqId" : "_NA_"], null, false);
	List<GenericValue> promoProductPromoProducts = delegator.findByAnd("ProductPromoExtProduct", 
		["productPromoId" : productPromoId, "productPromoRuleId" : "_NA_", "productPromoActionSeqId" : "_NA_", "productPromoCondSeqId" : "_NA_"], null, false);
	List<GenericValue> productStores = delegator.findByAnd("ProductStore", null, null, false);
	List<GenericValue> roleTypes = delegator.findByAnd("RoleType", null, null, false);
	
	context.productStorePromoAppl = productStorePromoAppl;
	context.promoRoleTypeApply = promoRoleTypeApply;
	context.productPromoRules = productPromoRules;
	context.promoProductPromoCategories = promoProductPromoCategories;
	context.promoProductPromoProducts = promoProductPromoProducts;
	context.productStores = productStores;
	context.roleTypes = roleTypes;
	
	/*
	List<EntityCondition> listCond = new ArrayList<EntityCondition>();
	listCond.add(EntityCondition.makeCondition("productPromoId", productPromoId));
	List<EntityCondition> listCondOr = new ArrayList<EntityCondition>();
	listCondOr.add(EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN, nowTimestamp));
	listCondOr.add(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null));
	listCond.add(EntityCondition.makeCondition(listCondOr, EntityOperator.OR));
	context.productStorePromoAppl = delegator.findList("ProductStorePromoExtAppl", EntityCondition.makeCondition(listCond, EntityOperator.AND), null, null, null, false);
	context.promoRoleTypeApply = delegator.findList("ProductPromoExtRoleTypeAppl", EntityCondition.makeCondition(listCond, EntityOperator.AND), null, null, null, false);
	*/
}