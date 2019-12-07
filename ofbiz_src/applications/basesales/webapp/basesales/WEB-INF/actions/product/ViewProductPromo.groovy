import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.util.EntityUtil;

import com.olbius.basesales.product.ProductStoreWorker
import com.olbius.basesales.util.SalesPartyUtil

import javolution.util.FastList;

import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import java.util.List;
import org.ofbiz.entity.GenericValue;

productPromoId = parameters.productPromoId;
if (productPromoId) {
	context.productPromoId = productPromoId;
	productPromo = delegator.findOne("ProductPromo", UtilMisc.toMap("productPromoId", productPromoId), false);
	context.productPromo = productPromo;
	
	// condition get list product store by product date
	/*List<EntityCondition> listCond = new ArrayList<EntityCondition>();
	listCond.add(EntityCondition.makeCondition("productPromoId", productPromoId));
	List<EntityCondition> listCondOr = new ArrayList<EntityCondition>();
	listCondOr.add(EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN, nowTimestamp));
	listCondOr.add(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null));
	listCond.add(EntityCondition.makeCondition(listCondOr, EntityOperator.OR));*/
	List<EntityCondition> listCond = new ArrayList<EntityCondition>();
	listCond.add(EntityCondition.makeCondition("productPromoId", productPromoId));
	listCond.add(EntityCondition.makeCondition("fromDate", productPromo.fromDate));
	List<EntityCondition> listCondAnd1 = new ArrayList<EntityCondition>();
	listCondAnd1.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "PROMO_CANCELLED"));
	listCondAnd1.add(EntityCondition.makeCondition("thruDate", productPromo.thruDate));
	List<EntityCondition> listCondAnd2 = new ArrayList<EntityCondition>();
	listCondAnd2.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "PROMO_CANCELLED"));
	listCond.add(EntityCondition.makeCondition(EntityCondition.makeCondition(listCondAnd1), EntityOperator.OR, EntityCondition.makeCondition(listCondAnd2)));
	
	List<EntityCondition> listCondStore = FastList.newInstance();
	listCondStore.addAll(listCond);
	if (!SalesPartyUtil.isSalesManager(delegator, userLogin.partyId) && !SalesPartyUtil.isSalesAdminManager(delegator, userLogin.partyId) && !SalesPartyUtil.isSalesAdmin(delegator, userLogin.partyId)) {
		List<String> productStoreIds = EntityUtil.getFieldListFromEntityList(ProductStoreWorker.getListProductStoreView(delegator, userLogin, userLogin.partyId, false), "productStoreId", true);
		listCondStore.add(EntityCondition.makeCondition("productStoreId", EntityOperator.IN, productStoreIds));
	}
	
	//List<GenericValue> productStorePromoAppl = delegator.findList("ProductStorePromoAppl", EntityCondition.makeCondition(listCond, EntityOperator.AND), null, UtilMisc.toList("sequenceNum", "productPromoId"), null, false);
	List<GenericValue> productStorePromoAppl = delegator.findList("ProductPromoApplStore", EntityCondition.makeCondition(listCondStore, EntityOperator.AND), null, UtilMisc.toList("productStoreId"), null, false);
	//List<GenericValue> promoRoleTypeApply = delegator.findList("ProductPromoRoleTypeAppl", EntityCondition.makeCondition(listCond, EntityOperator.AND), null, null, null, false);
	List<GenericValue> promoRoleTypeApply = delegator.findList("ProductPromoApplRoleType", EntityCondition.makeCondition(listCond, EntityOperator.AND), null, null, null, false);
	
	List<GenericValue> productPromoRules = delegator.findByAnd("ProductPromoRule", ["productPromoId" : productPromoId], ["ruleName"], false);
	List<GenericValue> promoProductPromoCategories = delegator.findByAnd("ProductPromoCategory", 
		["productPromoId" : productPromoId, "productPromoRuleId" : "_NA_", "productPromoActionSeqId" : "_NA_", "productPromoCondSeqId" : "_NA_"], null, false);
	List<GenericValue> promoProductPromoProducts = delegator.findByAnd("ProductPromoProduct", 
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
	context.productStorePromoAppl = delegator.findList("ProductStorePromoAppl", EntityCondition.makeCondition(listCond, EntityOperator.AND), null, null, null, false);
	context.promoRoleTypeApply = delegator.findList("ProductPromoRoleTypeAppl", EntityCondition.makeCondition(listCond, EntityOperator.AND), null, null, null, false);
	*/
}