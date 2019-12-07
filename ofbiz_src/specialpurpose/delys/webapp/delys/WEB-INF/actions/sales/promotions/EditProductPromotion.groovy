import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;

productPromoId = parameters.productPromoId;
if (productPromoId) {
	context.productPromoId = productPromoId;
	productPromo = delegator.findOne("ProductPromo", UtilMisc.toMap("productPromoId", productPromoId), false);
	context.productPromo = productPromo;
	List<EntityCondition> listCond = new ArrayList<EntityCondition>();
	listCond.add(EntityCondition.makeCondition("productPromoId", productPromoId));
	List<EntityCondition> listCondOr = new ArrayList<EntityCondition>();
	listCondOr.add(EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN, nowTimestamp));
	listCondOr.add(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null));
	listCond.add(EntityCondition.makeCondition(listCondOr, EntityOperator.OR));
	context.productStorePromoAppl = delegator.findList("ProductStorePromoAppl", EntityCondition.makeCondition(listCond, EntityOperator.AND), null, null, null, false);
	context.promoRoleTypeApply = delegator.findList("ProductPromoRoleTypeAppl", EntityCondition.makeCondition(listCond, EntityOperator.AND), null, null, null, false);
	if(productPromo && (productPromo.productPromoTypeId == "EXHIBITED" || productPromo.productPromoTypeId == "PROMOTION")){
		promoBudgetDist = delegator.findByAnd("ProductPromoBudget", UtilMisc.toMap("productPromoId", productPromoId, "budgetTypeId", "PROMO_BUDGET_DIS"), null, false);
		promoMiniRevenue = delegator.findByAnd("ProductPromoBudget", UtilMisc.toMap("productPromoId", productPromoId, "budgetTypeId", "PROMO_MINI_REVENUE"), null, false);
		if(promoBudgetDist){
			context.promoBudgetDist = promoBudgetDist[0];
		}else{
			context.promoBudgetDist = delegator.makeValue("ProductPromoBudget");
		}
		if(promoMiniRevenue){
			context.promoMiniRevenue = promoMiniRevenue[0];
		}else{
			context.promoMiniRevenue = delegator.makeValue("ProductPromoBudget");
		}
	}
}