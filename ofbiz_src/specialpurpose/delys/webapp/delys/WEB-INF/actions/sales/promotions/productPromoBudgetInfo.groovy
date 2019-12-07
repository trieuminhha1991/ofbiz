import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericValue;

if(productPromoId){	
	List<GenericValue> budgetDists = delegator.findByAnd("ProductPromoBudgetAndItem", UtilMisc.toMap("productPromoId", productPromoId, "budgetTypeId", "PROMO_BUDGET_DIS"), null, false);
	List<GenericValue> budgetMiniRevenue = delegator.findByAnd("ProductPromoBudgetAndItem", UtilMisc.toMap("productPromoId", productPromoId, "budgetTypeId", "PROMO_MINI_REVENUE"),null, false)	
		
	if(budgetDists){
		context.budgetTotalId = budgetDists[0].budgetId;
	}
	if(budgetMiniRevenue){
		context.revenueMiniId = budgetMiniRevenue[0].budgetId;
	}
		   
}
