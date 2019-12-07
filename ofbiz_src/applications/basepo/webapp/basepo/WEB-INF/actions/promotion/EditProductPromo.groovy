import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;

import java.util.List;
import java.util.ArrayList;

import javolution.util.FastMap;

productPromoId = parameters.productPromoId;
if (productPromoId) {
	GenericValue productPromo = delegator.findOne("ProductPromo", UtilMisc.toMap("productPromoId", productPromoId), false);
	if (productPromo != null) {
		context.productPromo = productPromo;
		context.productPromoId = productPromo.productPromoId;
		
		// Don't Understand - analysis after
		List<EntityCondition> listCond = new ArrayList<EntityCondition>();
		listCond.add(EntityCondition.makeCondition("productPromoId", productPromoId));
		List<EntityCondition> listCondOr = new ArrayList<EntityCondition>();
		listCondOr.add(EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN, nowTimestamp));
		listCondOr.add(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null));
		listCond.add(EntityCondition.makeCondition(listCondOr, EntityOperator.OR));
		
		// TODO: not use
		List<GenericValue> supplierPromoAppl = delegator.findList("SupplierPromoAppl", EntityCondition.makeCondition(listCond, EntityOperator.AND), null, null, null, false);
		context.supplierPromoAppl = supplierPromoAppl;
		
		List<String> supplierPromoApplIds = EntityUtil.getFieldListFromEntityList(supplierPromoAppl, "partyId", true);
		if (supplierPromoApplIds != null) {
			context.supplierPromoApplIso = delegator.findList("PartySupplier", EntityCondition.makeCondition("partyId", EntityOperator.IN, supplierPromoApplIds), null, null, null, false);
		}
		
		if (productPromo && productPromo.productPromoTypeId) {
			GenericValue productPromoType = delegator.findOne("ProductPromoType", UtilMisc.toMap("productPromoTypeId", productPromo.productPromoTypeId), false);
			if (productPromoType) context.channelId = productPromoType.productPromoTypeGroupId;
		}
		
		// Get list product promotion rule
		List<GenericValue> listProductPromoRule = delegator.findByAnd("ProductPromoRule", UtilMisc.toMap("productPromoId", productPromo.productPromoId), UtilMisc.toList("productPromoRuleId"), false);
		// Get list product promotion condition
		List<GenericValue> listProductPromoCond = delegator.findByAnd("ProductPromoCond", UtilMisc.toMap("productPromoId", productPromo.productPromoId), UtilMisc.toList("productPromoRuleId", "productPromoCondSeqId"), false);
		// Get list product promotion action
		List<GenericValue> listProductPromoAction = delegator.findByAnd("ProductPromoAction", UtilMisc.toMap("productPromoId", productPromo.productPromoId), UtilMisc.toList("productPromoRuleId", "productPromoActionSeqId"), false);
		// Get list product promo product
		List<GenericValue> listProductPromoProduct = delegator.findByAnd("ProductPromoProduct", UtilMisc.toMap("productPromoId", productPromo.productPromoId), null, false);
		// Get list product promo category
		List<GenericValue> listProductPromoCategory = delegator.findByAnd("ProductPromoCategory", UtilMisc.toMap("productPromoId", productPromo.productPromoId), null, false);
		
		// Build map list rule [action <product, category>, condition <product, category>]
		List<Map<String, Object>> listPromoRule = new ArrayList<Map<String, Object>>();
		for (GenericValue productPromoRule : listProductPromoRule) {
			Map<String, Object> promoRule = FastMap.newInstance();
			promoRule.put("productPromoId", productPromoRule.productPromoId);
			promoRule.put("productPromoRuleId", productPromoRule.productPromoRuleId);
			promoRule.put("ruleName", productPromoRule.ruleName);
			promoRule.put("ruleText", productPromoRule.ruleText);
			
			// filter list condition
			List<Map<String, Object>> listPromoCond = new ArrayList<Map<String, Object>>();
			Map<String, Object> filterCond = UtilMisc.<String, Object>toMap("productPromoId", productPromoRule.productPromoId, "productPromoRuleId", productPromoRule.productPromoRuleId);
			List<GenericValue> listProductPromoCondFiltered = EntityUtil.filterByAnd(listProductPromoCond, filterCond);
			for (GenericValue productPromoCond : listProductPromoCondFiltered) {
				Map<String, Object> promoCond = FastMap.newInstance();
				promoCond.put("productPromoId", productPromoCond.productPromoId);
				promoCond.put("productPromoRuleId", productPromoCond.productPromoRuleId);
				promoCond.put("productPromoCondSeqId", productPromoCond.productPromoCondSeqId);
				promoCond.put("inputParamEnumId", productPromoCond.inputParamEnumId);
				promoCond.put("operatorEnumId", productPromoCond.operatorEnumId);
				promoCond.put("condValue", productPromoCond.condValue);
				promoCond.put("otherValue", productPromoCond.otherValue);
				promoCond.put("condExhibited", productPromoCond.condExhibited);
				promoCond.put("notes", productPromoCond.notes);
				promoCond.put("usePriceWithTax", productPromoCond.usePriceWithTax);
				
				// filter list product
				List<Map<String, Object>> listPromoProd = new ArrayList<Map<String, Object>>();
				Map<String, Object> filterProd = UtilMisc.<String, Object>toMap("productPromoId", productPromoCond.productPromoId, 
					"productPromoRuleId", productPromoCond.productPromoRuleId, "productPromoCondSeqId", productPromoCond.productPromoCondSeqId);
				List<GenericValue> listProductPromoProdFiltered = EntityUtil.filterByAnd(listProductPromoProduct, filterProd);
				for (GenericValue productPromoProd : listProductPromoProdFiltered) {
					Map<String, Object> promoProd = FastMap.newInstance();
					promoProd.put("productId", productPromoProd.productId);
					promoProd.put("productPromoApplEnumId", productPromoProd.productPromoApplEnumId);
					
					listPromoProd.add(promoProd);
				}
				promoCond.put("listProd", listPromoProd);
				
				// filter list category
				List<Map<String, Object>> listPromoCate = new ArrayList<Map<String, Object>>();
				Map<String, Object> filterCate = UtilMisc.<String, Object>toMap("productPromoId", productPromoCond.productPromoId,
					"productPromoRuleId", productPromoCond.productPromoRuleId, "productPromoCondSeqId", productPromoCond.productPromoCondSeqId);
				List<GenericValue> listProductPromoCateFiltered = EntityUtil.filterByAnd(listProductPromoCategory, filterCate);
				for (GenericValue productPromoCate : listProductPromoCateFiltered) {
					Map<String, Object> promoCate = FastMap.newInstance();
					promoCate.put("productCategoryId", productPromoCate.productCategoryId);
					promoCate.put("andGroupId", productPromoCate.andGroupId);
					promoCate.put("productPromoApplEnumId", productPromoCate.productPromoApplEnumId);
					promoCate.put("includeSubCategories", productPromoCate.includeSubCategories);
					
					listPromoCate.add(promoCate);
				}
				promoCond.put("listCate", listPromoCate);
				
				listPromoCond.add(promoCond);
			}
			promoRule.put("listCond", listPromoCond);
			
			// filter list action
			List<Map<String, Object>> listPromoAction = new ArrayList<Map<String, Object>>();
			Map<String, Object> filterAction = UtilMisc.<String, Object>toMap("productPromoId", productPromoRule.productPromoId, "productPromoRuleId", productPromoRule.productPromoRuleId);
			List<GenericValue> listProductPromoActionFiltered = EntityUtil.filterByAnd(listProductPromoAction, filterAction);
			for (GenericValue productPromoAction : listProductPromoActionFiltered) {
				Map<String, Object> promoAction = FastMap.newInstance();
				promoAction.put("productPromoId", productPromoAction.productPromoId);
				promoAction.put("productPromoRuleId", productPromoAction.productPromoRuleId);
				promoAction.put("productPromoActionSeqId", productPromoAction.productPromoActionSeqId);
				promoAction.put("productPromoActionEnumId", productPromoAction.productPromoActionEnumId);
				promoAction.put("orderAdjustmentTypeId", productPromoAction.orderAdjustmentTypeId);
				promoAction.put("serviceName", productPromoAction.serviceName);
				promoAction.put("quantity", productPromoAction.quantity);
				promoAction.put("amount", productPromoAction.amount);
				promoAction.put("productId", productPromoAction.productId);
				promoAction.put("partyId", productPromoAction.partyId);
				promoAction.put("useCartQuantity", productPromoAction.useCartQuantity);
				promoAction.put("operatorEnumId", productPromoAction.operatorEnumId);
				promoAction.put("isCheckInv", productPromoAction.isCheckInv);
				
				// filter list product
				List<Map<String, Object>> listPromoProd = new ArrayList<Map<String, Object>>();
				Map<String, Object> filterProd = UtilMisc.<String, Object>toMap("productPromoId", productPromoAction.productPromoId,
					"productPromoRuleId", productPromoAction.productPromoRuleId, "productPromoActionSeqId", productPromoAction.productPromoActionSeqId);
				List<GenericValue> listProductPromoProdFiltered = EntityUtil.filterByAnd(listProductPromoProduct, filterProd);
				for (GenericValue productPromoProd : listProductPromoProdFiltered) {
					Map<String, Object> promoProd = FastMap.newInstance();
					promoProd.put("productId", productPromoProd.productId);
					promoProd.put("productPromoApplEnumId", productPromoProd.productPromoApplEnumId);
					
					listPromoProd.add(promoProd);
				}
				promoAction.put("listProd", listPromoProd);
				
				// filter list category
				List<Map<String, Object>> listPromoCate = new ArrayList<Map<String, Object>>();
				Map<String, Object> filterCate = UtilMisc.<String, Object>toMap("productPromoId", productPromoAction.productPromoId,
					"productPromoRuleId", productPromoAction.productPromoRuleId, "productPromoActionSeqId", productPromoAction.productPromoActionSeqId);
				List<GenericValue> listProductPromoCateFiltered = EntityUtil.filterByAnd(listProductPromoCategory, filterCate);
				for (GenericValue productPromoCate : listProductPromoCateFiltered) {
					Map<String, Object> promoCate = FastMap.newInstance();
					promoCate.put("productCategoryId", productPromoCate.productCategoryId);
					promoCate.put("andGroupId", productPromoCate.andGroupId);
					promoCate.put("productPromoApplEnumId", productPromoCate.productPromoApplEnumId);
					promoCate.put("includeSubCategories", productPromoCate.includeSubCategories);
					
					listPromoCate.add(promoCate);
				}
				promoAction.put("listCate", listPromoCate);
				
				listPromoAction.add(promoAction);
			}
			promoRule.put("listAction", listPromoAction);
			
			listPromoRule.add(promoRule);
		}
		context.listPromoRule = listPromoRule;
	}
}