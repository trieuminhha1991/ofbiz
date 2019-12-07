import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;

shoppingCart = request.getSession().getAttribute("shoppingCart");
supplierPartyId = null;
if (shoppingCart != null){
	supplierPartyId = shoppingCart.getAttribute("supplierPartyId");
} else {
	supplierPartyId = parameters.supplierPartyId;
}
List<String> orderBy = new ArrayList<String>();
orderBy.add("-availableFromDate");
productPlanId = null;
if (parameters.productPlanId != null){
	productPlanId = parameters.productPlanId; 
}
List<GenericValue> listAllProductOfPlans = delegator.findList("ProductPlanItem", EntityCondition.makeCondition(UtilMisc.toMap("productPlanId", productPlanId)), null, null, null, false);
List<GenericValue> listProductOfPlans = new ArrayList<GenericValue>();
if (!listAllProductOfPlans.isEmpty()){
	for (GenericValue item : listAllProductOfPlans){
		List<GenericValue> listSupplier = delegator.findList("SupplierProduct", EntityCondition.makeCondition(UtilMisc.toMap("productId", item.get("productId"), "partyId", supplierPartyId)), null, orderBy, null, false);
		listSupplier = EntityUtil.filterByDate(listSupplier, UtilDateTime.nowTimestamp(), "availableFromDate", "availableThruDate", true);
		if (!listSupplier.isEmpty()){
			listProductOfPlans.add(item);
		}
	}
}
context.listProductOfPlans = listProductOfPlans;
context.productPlanId = productPlanId;