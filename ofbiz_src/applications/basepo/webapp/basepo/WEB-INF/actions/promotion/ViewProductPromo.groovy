import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import java.util.List;
import org.ofbiz.entity.GenericValue;

productPromoId = parameters.productPromoId;
if (productPromoId) {
	context.productPromoId = productPromoId;
	productPromo = delegator.findOne("ProductPromo", UtilMisc.toMap("productPromoId", productPromoId), false);
	context.productPromo = productPromo;
	
	// Don't Understand - analysis after
	List<EntityCondition> listCond = new ArrayList<EntityCondition>();
	listCond.add(EntityCondition.makeCondition("productPromoId", productPromoId));
	List<EntityCondition> listCondOr = new ArrayList<EntityCondition>();
	listCondOr.add(EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN, nowTimestamp));
	listCondOr.add(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null));
	listCond.add(EntityCondition.makeCondition(listCondOr, EntityOperator.OR));
	
	List<GenericValue> supplierPromoAppl = delegator.findList("SupplierPromoAppl", EntityCondition.makeCondition(listCond, EntityOperator.AND), null, UtilMisc.toList("sequenceNum", "productPromoId"), null, false);
	
	List<GenericValue> productPromoRules = delegator.findByAnd("ProductPromoRule", ["productPromoId" : productPromoId], ["ruleName"], false);
	List<GenericValue> promoProductPromoCategories = delegator.findByAnd("ProductPromoCategory", 
		["productPromoId" : productPromoId, "productPromoRuleId" : "_NA_", "productPromoActionSeqId" : "_NA_", "productPromoCondSeqId" : "_NA_"], null, false);
	List<GenericValue> promoProductPromoProducts = delegator.findByAnd("ProductPromoProduct", 
		["productPromoId" : productPromoId, "productPromoRuleId" : "_NA_", "productPromoActionSeqId" : "_NA_", "productPromoCondSeqId" : "_NA_"], null, false);
	
	context.supplierPromoAppl = supplierPromoAppl;
	context.productPromoRules = productPromoRules;
	context.promoProductPromoCategories = promoProductPromoCategories;
	context.promoProductPromoProducts = promoProductPromoProducts;
}