import java.util.*;

import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.base.util.*;
import org.ofbiz.service.ServiceUtil;

productQuotationId = parameters.productQuotationId;
GenericValue productQuotation = null;
if (productQuotationId) {
	productQuotation = delegator.findOne("ProductQuotation", ["productQuotationId" : productQuotationId], false);
	if (productQuotation != null) {
		// get roleTypeId apply price to market
		List<EntityCondition> listRuleRoleTypeMarket = new ArrayList<EntityCondition>();
		listRuleRoleTypeMarket.add(EntityCondition.makeCondition("productQuotationId", EntityOperator.EQUALS, productQuotationId));
		listRuleRoleTypeMarket.add(EntityCondition.makeCondition("isExtra", EntityOperator.EQUALS, "Y"));
		List<GenericValue> listProductPriceRuleMarket = delegator.findList("ProductPriceRule", EntityCondition.makeCondition(listRuleRoleTypeMarket, EntityOperator.AND), null, null, null, false);
		if (listProductPriceRuleMarket != null && listProductPriceRuleMarket.size() > 0) {
			GenericValue productPriceRuleIdMarket = EntityUtil.getFirst(listProductPriceRuleMarket);
			
			List<EntityCondition> listCondRoleTypeMarket2 = new ArrayList<EntityCondition>();
			listCondRoleTypeMarket2.add(EntityCondition.makeCondition("productPriceRuleId", EntityOperator.EQUALS, productPriceRuleIdMarket.get("productPriceRuleId")));
			listCondRoleTypeMarket2.add(EntityCondition.makeCondition("inputParamEnumId", EntityOperator.EQUALS, "PRIP_ROLE_TYPE"));
			List<GenericValue> listProductPriceCondMarket = delegator.findList("ProductPriceCond", EntityCondition.makeCondition(listCondRoleTypeMarket2, EntityOperator.AND), null, null, null, false);
			if (listProductPriceCondMarket != null && listProductPriceCondMarket.size() > 0) {
				List<String> roleTypeMarketes = EntityUtil.getFieldListFromEntityList(listProductPriceCondMarket, "condValue", true);
				List<GenericValue> listRoleTypeMarket = delegator.findList("RoleType", EntityCondition.makeCondition("roleTypeId", EntityOperator.IN, roleTypeMarketes), null, null, null, false);
				context.listRoleTypeMarket = listRoleTypeMarket;
			}
		}
		
		// get partyId condition
		List<EntityCondition> listRuleRoleTypeMarket3 = new ArrayList<EntityCondition>();
		listRuleRoleTypeMarket3.add(EntityCondition.makeCondition("productQuotationId", EntityOperator.EQUALS, productQuotationId));
		List<EntityCondition> listRuleRoleTypeMarketOr3 = new ArrayList<EntityCondition>();
		listRuleRoleTypeMarketOr3.add(EntityCondition.makeCondition("isExtra", EntityOperator.EQUALS, "N"));
		listRuleRoleTypeMarketOr3.add(EntityCondition.makeCondition("isExtra", EntityOperator.EQUALS, null));
		listRuleRoleTypeMarket3.add(EntityCondition.makeCondition(listRuleRoleTypeMarketOr3, EntityOperator.OR));
		List<GenericValue> listProductPriceRuleMarket3 = delegator.findList("ProductPriceRule", EntityCondition.makeCondition(listRuleRoleTypeMarket3, EntityOperator.AND), null, null, null, false);
		if (listProductPriceRuleMarket3 != null && listProductPriceRuleMarket3.size() > 0) {
			GenericValue productPriceRuleIdMarket3 = EntityUtil.getFirst(listProductPriceRuleMarket3);
			
			List<EntityCondition> listCondRoleTypeMarket3 = new ArrayList<EntityCondition>();
			listCondRoleTypeMarket3.add(EntityCondition.makeCondition("productPriceRuleId", EntityOperator.EQUALS, productPriceRuleIdMarket3.get("productPriceRuleId")));
			listCondRoleTypeMarket3.add(EntityCondition.makeCondition("inputParamEnumId", EntityOperator.EQUALS, "PRIP_PARTY_ID"));
			List<GenericValue> listProductPriceCondMarket3 = delegator.findList("ProductPriceCond", EntityCondition.makeCondition(listCondRoleTypeMarket3, EntityOperator.AND), null, null, null, false);
			if (listProductPriceCondMarket3 != null && listProductPriceCondMarket3.size() > 0) {
				List<String> listPartyIdApply = EntityUtil.getFieldListFromEntityList(listProductPriceCondMarket3, "condValue", true);
				context.listPartyIdApply = listPartyIdApply;
			}
		}
	}
}