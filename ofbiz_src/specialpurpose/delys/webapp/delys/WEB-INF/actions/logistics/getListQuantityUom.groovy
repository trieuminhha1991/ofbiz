import java.util.List;

import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.model.*;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;

import com.olbius.services.DelysServices;

userLogin = session.getAttribute("userLogin");
partyId = userLogin.get('partyId');
context.partyId = partyId;

productId = product.productId;
quantityUomId = product.quantityUomId;

GenericValue uomBase = delegator.findOne("Uom", false, UtilMisc.toMap("uomId", quantityUomId));
DispatchContext ctx = dispatcher.getDispatchContext();
List<GenericValue> listConfigPackingProducts = new ArrayList<GenericValue>();
List<GenericValue> listUoms = new ArrayList<GenericValue>();
List<GenericValue> listUomPrs = new ArrayList<GenericValue>();

listConfigPackingProducts = delegator.findList("ConfigPacking", EntityCondition.makeCondition(UtilMisc.toMap("productId", productId, "uomToId", quantityUomId)), null, null, null, false);

listAllConfigPackingProducts = delegator.findList("ConfigPacking", EntityCondition.makeCondition(UtilMisc.toMap("productId", productId)), null, null, null, false);
listAllPackingUoms = delegator.findList("Uom", EntityCondition.makeCondition(UtilMisc.toMap("uomTypeId", "PRODUCT_PACKING")), null, null, null, false);
if (!listAllConfigPackingProducts.isEmpty()){
	for (GenericValue config : listAllConfigPackingProducts){
		GenericValue uomTmp = delegator.findOne("Uom", false, UtilMisc.toMap("uomId", config.get("uomFromId")));
		if (!listUoms.contains(uomTmp) && "PRODUCT_PACKING".equals(uomTmp.get("uomTypeId"))){
			listUoms.add(uomTmp);
		}
		if (!listUomPrs.contains(uomTmp) && "PRODUCT_PACKING".equals(uomTmp.get("uomTypeId"))){
			listUomPrs.add(uomTmp);
		}
		uomTmp = delegator.findOne("Uom", false, UtilMisc.toMap("uomId", config.get("uomToId")));
		if (!listUomPrs.contains(uomTmp) && "PRODUCT_PACKING".equals(uomTmp.get("uomTypeId"))){
			listUomPrs.add(uomTmp);
		}
		if (!listUoms.contains(uomTmp) && "PRODUCT_PACKING".equals(uomTmp.get("uomTypeId"))){
			listUoms.add(uomTmp);
		}
	}
}
listAllPackingUoms.remove(uomBase);
context.listUoms = listUoms;
context.listPrUoms = listAllPackingUoms;
