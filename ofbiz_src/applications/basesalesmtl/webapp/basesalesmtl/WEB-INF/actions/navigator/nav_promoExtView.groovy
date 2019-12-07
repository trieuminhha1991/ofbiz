import java.util.*;
import java.util.Map.Entry;

import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;

import com.olbius.basesales.product.ProductStoreWorker;
import com.olbius.basesales.util.SalesPartyUtil;

import javolution.util.FastList;

String module = UtilHttp.getModule(request);
if (module) {
	if (!"DISTRIBUTOR".equals(module)) {
		context.selectedMenuItem = "promotionExt";
		context.selectedSubMenuItem = "promotionListExt";
	} else {
		context.selectedMenuItem = "promotionExt";
		context.selectedSubMenuItem = "promotionExtRetailOutletList";
		/*List<EntityCondition> conds = FastList.newInstance();
		conds.add(EntityCondition.makeCondition("partyId", userLogin.partyId));
		conds.add(EntityCondition.makeCondition("roleTypeId", "CUSTOMER"));
		conds.add(EntityUtil.getFilterByDateExpr());
		List<String> productStoreIdCust = EntityUtil.getFieldListFromEntityList(delegator.findList("ProductStoreRole", EntityCondition.makeCondition(conds), null, null, null, false), "productStoreId", true);
		conds.clear();
		conds.add(EntityCondition.makeCondition("productPromoId", productPromoId));
		conds.add(EntityCondition.makeCondition("productStoreId", EntityOperator.IN, productStoreIdCust));
		conds.add(EntityUtil.getFilterByDateExpr());
		List<GenericValue> productStoreAppl = delegator.findList("ProductStorePromoAppl", EntityCondition.makeCondition(conds), null, null, null, false);
		if (UtilValidate.isNotEmpty(productStoreAppl)) {
			context.selectedMenuItem = "promotionExt";
			context.selectedSubMenuItem = "promotionExtRetailOutletList";
		} else {
			List<String> productStoreIds = EntityUtil.getFieldListFromEntityList(ProductStoreWorker.getListProductStoreSell(delegator, userLogin, userLogin.partyId, true, null, null, null, null), "productStoreId", true);
			conds.clear();
			conds.add(EntityCondition.makeCondition("productPromoId", productPromoId));
			conds.add(EntityCondition.makeCondition("productStoreId", EntityOperator.IN, productStoreIds));
			List<GenericValue> productStoreAppl2 = delegator.findList("ProductStorePromoAppl", EntityCondition.makeCondition(conds), null, null, null, false);
			if (UtilValidate.isNotEmpty(productStoreAppl2)) {
				context.selectedMenuItem = "promotionExt";
				context.selectedSubMenuItem = "promotionExtRetailOutletList";
			}
		}*/
	}
	//String focusMenu = parameters.fc;
	/*if ("dis".equals(focusMenu)) {
		context.selectedMenuItem = "promotionDis";
		context.selectedSubMenuItem = "promotionDistributorList";
	} else if ("reo".equals(focusMenu)) {
		context.selectedMenuItem = "promotionDis";
		context.selectedSubMenuItem = "promotionRetailOutletList";
	}*/
}