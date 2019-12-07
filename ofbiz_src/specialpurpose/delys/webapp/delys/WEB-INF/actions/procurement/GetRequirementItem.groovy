import javolution.util.FastList;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;

String requirementId = parameters.requirementId;
EntityCondition mainCond = EntityCondition.makeCondition("requirementId", EntityOperator.EQUALS, requirementId);
List<GenericValue> listRequirementItem = delegator.findList("RequirementItem", mainCond, null, null, null, false);
List<String> listProductId = FastList.newInstance();
if(UtilValidate.isNotEmpty(listRequirementItem)){
	for (requirementItem in listRequirementItem) {
		String productId = requirementItem.getString("productId");
		if(UtilValidate.isNotEmpty(productId)){
			listProductId.add(productId);
		}
	}
}
EntityCondition productCond = EntityCondition.makeCondition("productId", EntityOperator.IN, listProductId);
List<GenericValue> listProduct = delegator.findList("Product", productCond, null, null, null, false);


GenericValue shoppingProposalSelected = delegator.findOne("Requirement", UtilMisc.toMap("requirementId", requirementId), false);
context.shoppingProposalSelected  = shoppingProposalSelected;
context.listProduct = listProduct;


