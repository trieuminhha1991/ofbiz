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
expr = EntityCondition.makeCondition("agreementId", EntityOperator.EQUALS, parameters.agreementId);
exprList.add(expr);
expr = EntityCondition.makeCondition("agreementItemTypeId", EntityOperator.EQUALS, "SUBAGREEMENT");
Cond = EntityCondition.makeCondition(exprList, EntityOperator.AND);

List<GenericValue> listAgreementItems = delegator.findList("AgreementItem", Cond, null, null, null, false);
List<GenericValue> listAgreementProducts = new ArrayList<GenericValue>();
if (!listAgreementItems.isEmpty()){
	for (GenericValue item : listAgreementItems){
		List<GenericValue> listProductsTmp = new ArrayList<GenericValue>();
		listProductsTmp = delegator.findList("SupplierProduct", EntityCondition.makeCondition(UtilMisc.toMap("agreementId", parameters.agreementId, "agreementItemSeqId", item.get("agreementItemSeqId"))), null, null, null, false);
		if (!listProductsTmp.isEmpty()){
			listAgreementProducts.addAll(listProductsTmp);
		}
	}
}
context.listAgreementProducts = listAgreementProducts;