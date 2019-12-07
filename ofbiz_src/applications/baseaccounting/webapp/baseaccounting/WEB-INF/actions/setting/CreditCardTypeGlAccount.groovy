import org.ofbiz.entity.GenericValue;

import java.util.*;
import java.lang.*;

import javolution.util.FastList;

import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.*;


organizationPartyId = parameters.organizationPartyId;
EntityCondition cardTypeExitedCond = EntityCondition.makeCondition("organizationPartyId", EntityOperator.EQUALS, organizationPartyId);
List<GenericValue> listCardTypeExisted = delegator.findList("CreditCardTypeGlAccount", cardTypeExitedCond, null, null, null,false);
List<String> cardTypeIds = FastList.newInstance();
if(UtilValidate.isNotEmpty(listCardTypeExisted)){
	for ( cardType in listCardTypeExisted) {
		String cardTypeId = cardType.getString("cardType");
		cardTypeIds.add(cardTypeId);
	}
}
List<EntityCondition> listConds = FastList.newInstance();
listConds.add(EntityCondition.makeCondition("enumTypeId", EntityOperator.EQUALS, "CREDIT_CARD_TYPE"));
listConds.add(EntityCondition.makeCondition("enumCode", EntityOperator.NOT_IN, cardTypeIds));
EntityCondition cardTypeCond = EntityCondition.makeCondition(listConds, EntityOperator.AND);
List<GenericValue> listCardType = FastList.newInstance();
if(cardTypeIds.size() > 0){
	 listCardType = delegator.findList("Enumeration",cardTypeCond, null, null, null, false);
}
context.listCardType = listCardType;