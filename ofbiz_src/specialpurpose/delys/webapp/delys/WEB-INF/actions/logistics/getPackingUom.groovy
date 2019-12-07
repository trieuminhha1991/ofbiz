import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.model.*;
import org.ofbiz.entity.util.EntityUtil;

userLogin = session.getAttribute("userLogin");
partyId = userLogin.get('partyId');
context.partyId = partyId;

exprList = [];
expr = EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId);
exprList.add(expr);
Cond = EntityCondition.makeCondition(exprList, EntityOperator.AND);

List<GenericValue> listConfigPacking = delegator.findList("ConfigPacking", Cond, null, null, null, false);
List<GenericValue> listProductQuantityUom = new ArrayList<GenericValue>();
if (!listConfigPacking.isEmpty()){
	for (GenericValue cf : listConfigPacking){
		GenericValue uom = delegator.findOne("Uom", false, UtilMisc.toMap("uomId", (String)cf.get("uomFromId")));
		if (!listProductQuantityUom.contains(uom)){
			listProductQuantityUom.add(uom);
		}
		uom = delegator.findOne("Uom", false, UtilMisc.toMap("uomId", (String)cf.get("uomToId")));
		if (!listProductQuantityUom.contains(uom)){
			listProductQuantityUom.add(uom);
		}
	}
}
context.listProductQuantityUom = listProductQuantityUom;