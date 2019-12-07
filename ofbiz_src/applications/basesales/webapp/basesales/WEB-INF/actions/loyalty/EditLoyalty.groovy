import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import java.util.List;
import java.util.ArrayList;
import javolution.util.FastMap;

loyaltyId = parameters.loyaltyId;
if (loyaltyId) {
	GenericValue loyalty = delegator.findOne("Loyalty", UtilMisc.toMap("loyaltyId", loyaltyId), false);
	if (loyalty != null) {
		context.loyalty = loyalty;
		context.loyaltyId = loyalty.loyaltyId;
		
		// Don't Understand - analysis after
		List<EntityCondition> listCond = new ArrayList<EntityCondition>();
		listCond.add(EntityCondition.makeCondition("loyaltyId", loyaltyId));
		List<EntityCondition> listCondOr = new ArrayList<EntityCondition>();
		listCondOr.add(EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN, nowTimestamp));
		listCondOr.add(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null));
		listCond.add(EntityCondition.makeCondition(listCondOr, EntityOperator.OR));
		
		// TODO: not use
		List<GenericValue> productStoreLoyaltyAppl = delegator.findList("ProductStoreLoyaltyAppl", EntityCondition.makeCondition(listCond, EntityOperator.AND), null, null, null, false);
		List<GenericValue> loyaltyRoleTypeApply = delegator.findList("LoyaltyRoleTypeAppl", EntityCondition.makeCondition(listCond, EntityOperator.AND), null, null, null, false);
		context.productStoreLoyaltyAppl = productStoreLoyaltyAppl;
		context.loyaltyRoleTypeApply = loyaltyRoleTypeApply;
		
		List<String> productStoreLoyaltyApplIds = EntityUtil.getFieldListFromEntityList(productStoreLoyaltyAppl, "productStoreId", true);
		List<String> loyaltyRoleTypeApplyIds = EntityUtil.getFieldListFromEntityList(loyaltyRoleTypeApply, "roleTypeId", true);
		if (productStoreLoyaltyApplIds != null) {
			context.productStoreLoyaltyApplIso = delegator.findList("ProductStore", EntityCondition.makeCondition("productStoreId", EntityOperator.IN, productStoreLoyaltyApplIds), null, null, null, false);
		}
		if (loyaltyRoleTypeApplyIds != null) {
			context.loyaltyRoleTypeApplyIso = delegator.findList("RoleType", EntityCondition.makeCondition("roleTypeId", EntityOperator.IN, loyaltyRoleTypeApplyIds), null, null, null, false);
		}
		
		// Get list product loyalty rule
		List<GenericValue> listLoyaltyPointRule = delegator.findByAnd("LoyaltyRule", UtilMisc.toMap("loyaltyId", loyalty.loyaltyId), UtilMisc.toList("loyaltyRuleId"), false);
		// Get list product loyalty condition
		List<GenericValue> listLoyaltyPointCond = delegator.findByAnd("LoyaltyCondition", UtilMisc.toMap("loyaltyId", loyalty.loyaltyId), UtilMisc.toList("loyaltyRuleId", "loyaltyCondSeqId"), false);
		// Get list product loyalty action
		List<GenericValue> listLoyaltyPointAction = delegator.findByAnd("LoyaltyAction", UtilMisc.toMap("loyaltyId", loyalty.loyaltyId), UtilMisc.toList("loyaltyRuleId", "loyaltyActionSeqId"), false);
		// Get list product loyalty product
		List<GenericValue> listLoyaltyPointProduct = delegator.findByAnd("LoyaltyProduct", UtilMisc.toMap("loyaltyId", loyalty.loyaltyId), null, false);
		// Get list product loyalty category
		List<GenericValue> listLoyaltyPointCategory = delegator.findByAnd("LoyaltyCategory", UtilMisc.toMap("loyaltyId", loyalty.loyaltyId), null, false);
		
		// Build map list rule [action <product, category>, condition <product, category>]
		List<Map<String, Object>> listLoyaltyRule = new ArrayList<Map<String, Object>>();
		for (GenericValue loyaltyPointRule : listLoyaltyPointRule) {
			Map<String, Object> loyaltyRule = FastMap.newInstance();
			loyaltyRule.put("loyaltyId", loyaltyPointRule.loyaltyId);
			loyaltyRule.put("loyaltyRuleId", loyaltyPointRule.loyaltyRuleId);
			loyaltyRule.put("ruleName", loyaltyPointRule.ruleName);
			
			// filter list condition
			List<Map<String, Object>> listLoyaltyCond = new ArrayList<Map<String, Object>>();
			Map<String, Object> filterCond = UtilMisc.<String, Object>toMap("loyaltyId", loyaltyPointRule.loyaltyId, "loyaltyRuleId", loyaltyPointRule.loyaltyRuleId);
			List<GenericValue> listLoyaltyPointCondFiltered = EntityUtil.filterByAnd(listLoyaltyPointCond, filterCond);
			for (GenericValue loyaltyPointCond : listLoyaltyPointCondFiltered) {
				Map<String, Object> loyaltyCond = FastMap.newInstance();
				loyaltyCond.put("loyaltyId", loyaltyPointCond.loyaltyId);
				loyaltyCond.put("loyaltyRuleId", loyaltyPointCond.loyaltyRuleId);
				loyaltyCond.put("loyaltyCondSeqId", loyaltyPointCond.loyaltyCondSeqId);
				loyaltyCond.put("inputParamEnumId", loyaltyPointCond.inputParamEnumId);
				loyaltyCond.put("operatorEnumId", loyaltyPointCond.operatorEnumId);
				loyaltyCond.put("condValue", loyaltyPointCond.condValue);
				loyaltyCond.put("otherValue", loyaltyPointCond.otherValue);
				loyaltyCond.put("condExhibited", loyaltyPointCond.condExhibited);
				loyaltyCond.put("notes", loyaltyPointCond.notes);
				loyaltyCond.put("usePriceWithTax", loyaltyPointCond.usePriceWithTax);
				loyaltyCond.put("isReturnOrder", loyaltyPointCond.isReturnOrder);
				
				// filter list product
				List<Map<String, Object>> listLoyaltyProd = new ArrayList<Map<String, Object>>();
				Map<String, Object> filterProd = UtilMisc.<String, Object>toMap("loyaltyId", loyaltyPointCond.loyaltyId, 
					"loyaltyRuleId", loyaltyPointCond.loyaltyRuleId, "loyaltyCondSeqId", loyaltyPointCond.loyaltyCondSeqId);
				List<GenericValue> listLoyaltyPointProdFiltered = EntityUtil.filterByAnd(listLoyaltyPointProduct, filterProd);
				for (GenericValue loyaltyPointProd : listLoyaltyPointProdFiltered) {
					Map<String, Object> loyaltyProd = FastMap.newInstance();
					loyaltyProd.put("productId", loyaltyPointProd.productId);
					loyaltyProd.put("loyaltyApplEnumId", loyaltyPointProd.loyaltyApplEnumId);
					
					listLoyaltyProd.add(loyaltyProd);
				}
				loyaltyCond.put("listProd", listLoyaltyProd);
				
				// filter list category
				List<Map<String, Object>> listLoyaltyCate = new ArrayList<Map<String, Object>>();
				Map<String, Object> filterCate = UtilMisc.<String, Object>toMap("loyaltyId", loyaltyPointCond.loyaltyId,
					"loyaltyRuleId", loyaltyPointCond.loyaltyRuleId, "loyaltyCondSeqId", loyaltyPointCond.loyaltyCondSeqId);
				List<GenericValue> listLoyaltyPointCateFiltered = EntityUtil.filterByAnd(listLoyaltyPointCategory, filterCate);
				for (GenericValue loyaltyPointCate : listLoyaltyPointCateFiltered) {
					Map<String, Object> loyaltyCate = FastMap.newInstance();
					loyaltyCate.put("productCategoryId", loyaltyPointCate.productCategoryId);
					loyaltyCate.put("andGroupId", loyaltyPointCate.andGroupId);
					loyaltyCate.put("loyaltyApplEnumId", loyaltyPointCate.loyaltyApplEnumId);
					loyaltyCate.put("includeSubCategories", loyaltyPointCate.includeSubCategories);
					
					listLoyaltyCate.add(loyaltyCate);
				}
				loyaltyCond.put("listCate", listLoyaltyCate);
				
				listLoyaltyCond.add(loyaltyCond);
			}
			loyaltyRule.put("listCond", listLoyaltyCond);
			
			// filter list action
			List<Map<String, Object>> listLoyaltyAction = new ArrayList<Map<String, Object>>();
			Map<String, Object> filterAction = UtilMisc.<String, Object>toMap("loyaltyId", loyaltyPointRule.loyaltyId, "loyaltyRuleId", loyaltyPointRule.loyaltyRuleId);
			List<GenericValue> listLoyaltyPointActionFiltered = EntityUtil.filterByAnd(listLoyaltyPointAction, filterAction);
			for (GenericValue loyaltyPointAction : listLoyaltyPointActionFiltered) {
				Map<String, Object> loyaltyAction = FastMap.newInstance();
				loyaltyAction.put("loyaltyId", loyaltyPointAction.loyaltyId);
				loyaltyAction.put("loyaltyRuleId", loyaltyPointAction.loyaltyRuleId);
				loyaltyAction.put("loyaltyActionSeqId", loyaltyPointAction.loyaltyActionSeqId);
				loyaltyAction.put("loyaltyActionEnumId", loyaltyPointAction.loyaltyActionEnumId);
				/*loyaltyAction.put("orderAdjustmentTypeId", loyaltyPointAction.orderAdjustmentTypeId);
				loyaltyAction.put("serviceName", loyaltyPointAction.serviceName);
				loyaltyAction.put("isCheckInv", loyaltyPointAction.isCheckInv);
				loyaltyAction.put("productId", loyaltyPointAction.productId);
				loyaltyAction.put("partyId", loyaltyPointAction.partyId);
				loyaltyAction.put("useCartQuantity", loyaltyPointAction.useCartQuantity);*/
				loyaltyAction.put("quantity", loyaltyPointAction.quantity);
				loyaltyAction.put("actionValue", loyaltyPointAction.actionValue);
				loyaltyAction.put("amount", loyaltyPointAction.amount);
				loyaltyAction.put("operatorEnumId", loyaltyPointAction.operatorEnumId);
				
				// filter list product
				/*List<Map<String, Object>> listLoyaltyProd = new ArrayList<Map<String, Object>>();
				Map<String, Object> filterProd = UtilMisc.<String, Object>toMap("loyaltyId", loyaltyPointAction.loyaltyId,
					"loyaltyRuleId", loyaltyPointAction.loyaltyRuleId, "loyaltyActionSeqId", loyaltyPointAction.loyaltyActionSeqId);
				List<GenericValue> listLoyaltyPointProdFiltered = EntityUtil.filterByAnd(listLoyaltyPointProduct, filterProd);
				for (GenericValue loyaltyPointProd : listLoyaltyPointProdFiltered) {
					Map<String, Object> loyaltyProd = FastMap.newInstance();
					loyaltyProd.put("productId", loyaltyPointProd.productId);
					loyaltyProd.put("loyaltyApplEnumId", loyaltyPointProd.loyaltyApplEnumId);
					
					listLoyaltyProd.add(loyaltyProd);
				}
				loyaltyAction.put("listProd", listLoyaltyProd);*/
				
				// filter list category
				/*List<Map<String, Object>> listLoyaltyCate = new ArrayList<Map<String, Object>>();
				Map<String, Object> filterCate = UtilMisc.<String, Object>toMap("loyaltyId", loyaltyPointAction.loyaltyId,
					"loyaltyRuleId", loyaltyPointAction.loyaltyRuleId, "loyaltyActionSeqId", loyaltyPointAction.loyaltyActionSeqId);
				List<GenericValue> listLoyaltyPointCateFiltered = EntityUtil.filterByAnd(listLoyaltyPointCategory, filterCate);
				for (GenericValue loyaltyPointCate : listLoyaltyPointCateFiltered) {
					Map<String, Object> loyaltyCate = FastMap.newInstance();
					loyaltyCate.put("productCategoryId", loyaltyPointCate.productCategoryId);
					loyaltyCate.put("andGroupId", loyaltyPointCate.andGroupId);
					loyaltyCate.put("loyaltyApplEnumId", loyaltyPointCate.loyaltyApplEnumId);
					loyaltyCate.put("includeSubCategories", loyaltyPointCate.includeSubCategories);
					
					listLoyaltyCate.add(loyaltyCate);
				}
				loyaltyAction.put("listCate", listLoyaltyCate);*/
				
				listLoyaltyAction.add(loyaltyAction);
			}
			loyaltyRule.put("listAction", listLoyaltyAction);
			
			listLoyaltyRule.add(loyaltyRule);
		}
		context.listLoyaltyRule = listLoyaltyRule;
	}
}