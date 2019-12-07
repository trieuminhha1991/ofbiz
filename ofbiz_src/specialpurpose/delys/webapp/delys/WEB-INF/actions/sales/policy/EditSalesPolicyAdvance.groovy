import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;

import java.util.List;
import java.util.ArrayList;

import javolution.util.FastMap;

salesPolicyId = parameters.salesPolicyId;
if (salesPolicyId) {
	GenericValue salesPolicy = delegator.findOne("SalesPolicy", UtilMisc.toMap("salesPolicyId", salesPolicyId), false);
	if (salesPolicy != null) {
		context.salesPolicy = salesPolicy;
		context.salesPolicyId = salesPolicy.salesPolicyId;
		
		// Don't Understand - analysis after
		List<EntityCondition> listCond = new ArrayList<EntityCondition>();
		listCond.add(EntityCondition.makeCondition("salesPolicyId", salesPolicyId));
		List<EntityCondition> listCondOr = new ArrayList<EntityCondition>();
		listCondOr.add(EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, nowTimestamp));
		listCondOr.add(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null));
		listCond.add(EntityCondition.makeCondition(listCondOr, EntityOperator.OR));
		//context.productStorePromoAppl = delegator.findList("ProductStorePromoAppl", EntityCondition.makeCondition(listCond, EntityOperator.AND), null, null, null, false);
		context.policyRoleTypeAppl = delegator.findList("SalesPolicyRoleTypeApply", EntityCondition.makeCondition(listCond, EntityOperator.AND), null, null, null, false);
		
		List<EntityCondition> listCondAnd = new ArrayList<EntityCondition>();
		listCondAnd.add(EntityCondition.makeCondition(listCond, EntityOperator.AND));
		listCondAnd.add(EntityCondition.makeCondition("salesPolicyGeoApplEnumId", "SPPA_INCLUDE"));
		context.salesPolicyGeoApplInclude = delegator.findList("SalesPolicyGeoAppl", EntityCondition.makeCondition(listCondAnd, EntityOperator.AND), null, null, null, false);
		
		List<EntityCondition> listCondAnd2 = new ArrayList<EntityCondition>();
		listCondAnd2.add(EntityCondition.makeCondition(listCond, EntityOperator.AND));
		listCondAnd2.add(EntityCondition.makeCondition("salesPolicyGeoApplEnumId", "SPPA_EXCLUDE"));
		context.salesPolicyGeoApplExclude = delegator.findList("SalesPolicyGeoAppl", EntityCondition.makeCondition(listCondAnd2, EntityOperator.AND), null, null, null, false);
		
		// Get list product promotion rule
		List<GenericValue> listSalesPolicyRuleGv = delegator.findByAnd("SalesPolicyRule", UtilMisc.toMap("salesPolicyId", salesPolicy.salesPolicyId), UtilMisc.toList("salesPolicyRuleId"), false);
		// Get list product promotion condition
		List<GenericValue> listSalesPolicyCond = delegator.findByAnd("SalesPolicyCond", UtilMisc.toMap("salesPolicyId", salesPolicy.salesPolicyId), null, false);
		// Get list product promotion action
		List<GenericValue> listSalesPolicyAction = delegator.findByAnd("SalesPolicyAction", UtilMisc.toMap("salesPolicyId", salesPolicy.salesPolicyId), null, false);
		// Get list product promo product
		List<GenericValue> listSalesPolicyProduct = delegator.findByAnd("SalesPolicyProduct", UtilMisc.toMap("salesPolicyId", salesPolicy.salesPolicyId), null, false);
		// Get list product promo category
		List<GenericValue> listSalesPolicyCategory = delegator.findByAnd("SalesPolicyCategory", UtilMisc.toMap("salesPolicyId", salesPolicy.salesPolicyId), null, false);
		
		// Build map list rule [action <product, category>, condition <product, category>]
		List<Map<String, Object>> listSalesPolicyRule = new ArrayList<Map<String, Object>>();
		for (GenericValue salesPolicyRule : listSalesPolicyRuleGv) {
			Map<String, Object> promoRule = FastMap.newInstance();
			promoRule.put("salesPolicyId", salesPolicyRule.salesPolicyId);
			promoRule.put("salesPolicyRuleId", salesPolicyRule.salesPolicyRuleId);
			promoRule.put("ruleName", salesPolicyRule.ruleName);
			promoRule.put("salesStatementTypeId", salesPolicyRule.salesStatementTypeId);
			promoRule.put("paymentParty", salesPolicyRule.paymentParty);
			
			// filter list condition
			List<Map<String, Object>> listPromoCond = new ArrayList<Map<String, Object>>();
			Map<String, Object> filterCond = UtilMisc.<String, Object>toMap("salesPolicyId", salesPolicyRule.salesPolicyId, "salesPolicyRuleId", salesPolicyRule.salesPolicyRuleId);
			List<GenericValue> listSalesPolicyCondFiltered = EntityUtil.filterByAnd(listSalesPolicyCond, filterCond);
			for (GenericValue salesPolicyCond : listSalesPolicyCondFiltered) {
				Map<String, Object> promoCond = FastMap.newInstance();
				promoCond.put("salesPolicyId", salesPolicyCond.salesPolicyId);
				promoCond.put("salesPolicyRuleId", salesPolicyCond.salesPolicyRuleId);
				promoCond.put("salesPolicyCondSeqId", salesPolicyCond.salesPolicyCondSeqId);
				promoCond.put("inputParamEnumId", salesPolicyCond.inputParamEnumId);
				promoCond.put("operatorEnumId", salesPolicyCond.operatorEnumId);
				promoCond.put("condValue", salesPolicyCond.condValue);
				promoCond.put("otherValue", salesPolicyCond.otherValue);
				promoCond.put("condExhibited", salesPolicyCond.condExhibited);
				promoCond.put("notes", salesPolicyCond.notes);
				
				// filter list product
				List<Map<String, Object>> listPromoProd = new ArrayList<Map<String, Object>>();
				Map<String, Object> filterProd = UtilMisc.<String, Object>toMap("salesPolicyId", salesPolicyCond.salesPolicyId, 
					"salesPolicyRuleId", salesPolicyCond.salesPolicyRuleId, "salesPolicyCondSeqId", salesPolicyCond.salesPolicyCondSeqId);
				List<GenericValue> listProductPromoProdFiltered = EntityUtil.filterByAnd(listSalesPolicyProduct, filterProd);
				for (GenericValue salesPolicyProd : listProductPromoProdFiltered) {
					Map<String, Object> promoProd = FastMap.newInstance();
					promoProd.put("productId", salesPolicyProd.productId);
					promoProd.put("salesPolicyApplEnumId", salesPolicyProd.salesPolicyApplEnumId);
					
					listPromoProd.add(promoProd);
				}
				promoCond.put("listProd", listPromoProd);
				
				// filter list category
				List<Map<String, Object>> listPromoCate = new ArrayList<Map<String, Object>>();
				Map<String, Object> filterCate = UtilMisc.<String, Object>toMap("salesPolicyId", salesPolicyCond.salesPolicyId,
					"salesPolicyRuleId", salesPolicyCond.salesPolicyRuleId, "salesPolicyCondSeqId", salesPolicyCond.salesPolicyCondSeqId);
				List<GenericValue> listProductPromoCateFiltered = EntityUtil.filterByAnd(listSalesPolicyCategory, filterCate);
				for (GenericValue salesPolicyCate : listProductPromoCateFiltered) {
					Map<String, Object> promoCate = FastMap.newInstance();
					promoCate.put("productCategoryId", salesPolicyCate.productCategoryId);
					promoCate.put("andGroupId", salesPolicyCate.andGroupId);
					promoCate.put("salesPolicyApplEnumId", salesPolicyCate.salesPolicyApplEnumId);
					promoCate.put("includeSubCategories", salesPolicyCate.includeSubCategories);
					
					listPromoCate.add(promoCate);
				}
				promoCond.put("listCate", listPromoCate);
				
				listPromoCond.add(promoCond);
			}
			promoRule.put("listCond", listPromoCond);
			
			// filter list action
			List<Map<String, Object>> listPromoAction = new ArrayList<Map<String, Object>>();
			Map<String, Object> filterAction = UtilMisc.<String, Object>toMap("salesPolicyId", salesPolicyRule.salesPolicyId, "salesPolicyRuleId", salesPolicyRule.salesPolicyRuleId);
			List<GenericValue> listSalesPolicyActionFiltered = EntityUtil.filterByAnd(listSalesPolicyAction, filterAction);
			for (GenericValue salesPolicyAction : listSalesPolicyActionFiltered) {
				Map<String, Object> promoAction = FastMap.newInstance();
				promoAction.put("salesPolicyId", salesPolicyAction.salesPolicyId);
				promoAction.put("salesPolicyRuleId", salesPolicyAction.salesPolicyRuleId);
				promoAction.put("salesPolicyActionSeqId", salesPolicyAction.salesPolicyActionSeqId);
				promoAction.put("salesPolicyActionEnumId", salesPolicyAction.salesPolicyActionEnumId);
				//promoAction.put("orderAdjustmentTypeId", salesPolicyAction.orderAdjustmentTypeId);
				promoAction.put("serviceName", salesPolicyAction.serviceName);
				promoAction.put("quantity", salesPolicyAction.quantity);
				promoAction.put("amount", salesPolicyAction.amount);
				promoAction.put("productId", salesPolicyAction.productId);
				promoAction.put("partyId", salesPolicyAction.partyId);
				promoAction.put("useCartQuantity", salesPolicyAction.useCartQuantity);
				
				// filter list product
				List<Map<String, Object>> listPromoProd = new ArrayList<Map<String, Object>>();
				Map<String, Object> filterProd = UtilMisc.<String, Object>toMap("salesPolicyId", salesPolicyAction.salesPolicyId,
					"salesPolicyRuleId", salesPolicyAction.salesPolicyRuleId, "salesPolicyActionSeqId", salesPolicyAction.salesPolicyActionSeqId);
				List<GenericValue> listProductPromoProdFiltered = EntityUtil.filterByAnd(listSalesPolicyProduct, filterProd);
				for (GenericValue salesPolicyProd : listProductPromoProdFiltered) {
					Map<String, Object> promoProd = FastMap.newInstance();
					promoProd.put("productId", salesPolicyProd.productId);
					promoProd.put("salesPolicyApplEnumId", salesPolicyProd.salesPolicyApplEnumId);
					
					listPromoProd.add(promoProd);
				}
				promoAction.put("listProd", listPromoProd);
				
				// filter list category
				List<Map<String, Object>> listPromoCate = new ArrayList<Map<String, Object>>();
				Map<String, Object> filterCate = UtilMisc.<String, Object>toMap("salesPolicyId", salesPolicyAction.salesPolicyId,
					"salesPolicyRuleId", salesPolicyAction.salesPolicyRuleId, "salesPolicyActionSeqId", salesPolicyAction.salesPolicyActionSeqId);
				List<GenericValue> listProductPromoCateFiltered = EntityUtil.filterByAnd(listSalesPolicyCategory, filterCate);
				for (GenericValue salesPolicyCate : listProductPromoCateFiltered) {
					Map<String, Object> promoCate = FastMap.newInstance();
					promoCate.put("productCategoryId", salesPolicyCate.productCategoryId);
					promoCate.put("andGroupId", salesPolicyCate.andGroupId);
					promoCate.put("salesPolicyApplEnumId", salesPolicyCate.salesPolicyApplEnumId);
					promoCate.put("includeSubCategories", salesPolicyCate.includeSubCategories);
					
					listPromoCate.add(promoCate);
				}
				promoAction.put("listCate", listPromoCate);
				
				listPromoAction.add(promoAction);
			}
			promoRule.put("listAction", listPromoAction);
			
			listSalesPolicyRule.add(promoRule);
		}
		context.listSalesPolicyRule = listSalesPolicyRule;
	}
}