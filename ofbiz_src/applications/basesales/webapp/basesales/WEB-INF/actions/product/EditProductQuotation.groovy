import java.util.*;

import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.base.util.*;
import org.ofbiz.service.ServiceUtil;

productQuotationId = parameters.productQuotationId;
if (UtilValidate.isEmpty(productQuotationId)) {
	productQuotationId = parameters.productQuotationIdOrg;
}
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
		/*List<EntityCondition> listRuleRoleTypeMarket3 = new ArrayList<EntityCondition>();
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
		}*/
		List<EntityCondition> condsPartyApply = new ArrayList<EntityCondition>();
		condsPartyApply.add(EntityCondition.makeCondition("productQuotationId", EntityOperator.EQUALS, productQuotationId));
		List<EntityCondition> listRuleRoleTypeMarketOr3 = new ArrayList<EntityCondition>();
		listRuleRoleTypeMarketOr3.add(EntityCondition.makeCondition("isExtra", EntityOperator.EQUALS, "N"));
		listRuleRoleTypeMarketOr3.add(EntityCondition.makeCondition("isExtra", EntityOperator.EQUALS, null));
		condsPartyApply.add(EntityCondition.makeCondition(listRuleRoleTypeMarketOr3, EntityOperator.OR));
		GenericValue pprFirstRule = EntityUtil.getFirst(delegator.findList("ProductPriceRule", EntityCondition.makeCondition(condsPartyApply, EntityOperator.AND), null, null, null, false));
		if (pprFirstRule != null) {
			List<EntityCondition> condsPartyApplyCond = new ArrayList<EntityCondition>();
			condsPartyApplyCond.add(EntityCondition.makeCondition("productPriceRuleId", EntityOperator.EQUALS, pprFirstRule.get("productPriceRuleId")));
			condsPartyApplyCond.add(EntityCondition.makeCondition(EntityCondition.makeCondition("inputParamEnumId", "PRIP_PARTY_ID"), EntityOperator.OR, EntityCondition.makeCondition("inputParamEnumId", "PRIP_PARTY_GRP_MEM")));
			GenericValue ppcPartyApply = EntityUtil.getFirst(delegator.findList("ProductPriceCond", EntityCondition.makeCondition(condsPartyApplyCond, EntityOperator.AND), null, null, null, false));
			if (ppcPartyApply != null) {
				context.partyIdApply = ppcPartyApply.condValue;
				GenericValue partyApply = delegator.findOne("PartyFullNameDetailSimple", ["partyId" : ppcPartyApply.condValue], false);
				if (partyApply != null) {
					context.partyApply = partyApply;
				}
			}
		}
	}
	
	//List<GenericValue> roleTypesSelected = delegator.findByAnd("ProductQuotationRoleTypeAppl", ["productQuotationId" : productQuotationId], null, false);
	//context.roleTypesSelected = roleTypesSelected;
	
	List<EntityCondition> listCond = new ArrayList<EntityCondition>();
	listCond.add(EntityCondition.makeCondition("productQuotationId", productQuotationId));
	List<EntityCondition> listCondOr = new ArrayList<EntityCondition>();
	listCondOr.add(EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN, nowTimestamp));
	listCondOr.add(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null));
	listCond.add(EntityCondition.makeCondition(listCondOr, EntityOperator.OR));
	
	List<GenericValue> productStoreAppls = delegator.findList("ProductQuotationStoreAppl", EntityCondition.makeCondition(listCond, EntityOperator.AND), null, null, null, false);
	context.productStoreAppls = productStoreAppls;
	
	List<GenericValue> productStoreGroupAppls = delegator.findList("ProductQuotationStoreGroupAppl", EntityCondition.makeCondition(listCond, EntityOperator.AND), null, null, null, false);
	context.productStoreGroupAppls = productStoreGroupAppls;
	
	/*List<GenericValue> quotationItems = delegator.findList("ProductQuotationRulesAndTax", EntityCondition.makeCondition("productQuotationId", productQuotationId), null, null, null, false);
	context.quotationItems = quotationItems;*/
}