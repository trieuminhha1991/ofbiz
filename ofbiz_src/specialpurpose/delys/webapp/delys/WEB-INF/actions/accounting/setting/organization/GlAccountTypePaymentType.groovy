import org.ofbiz.entity.GenericValue;

import java.util.*;
import java.lang.*;

import javolution.util.FastList;

import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.*;


organizationPartyId = parameters.organizationPartyId;
EntityCondition paymentTypeExitedCond = EntityCondition.makeCondition("organizationPartyId", EntityOperator.EQUALS, organizationPartyId);
List<GenericValue> listPaymentTypeExisted = delegator.findList("PaymentGlAccountTypeMap", paymentTypeExitedCond, null, null, null,false);
List<String> paymentTypeIds = FastList.newInstance();
if(UtilValidate.isNotEmpty(listPaymentTypeExisted)){
	for ( paymentType in listPaymentTypeExisted) {
		String paymentTypeId = paymentType.getString("paymentTypeId");
		paymentTypeIds.add(paymentTypeId);
	}
}
EntityCondition paymentTypeCond = EntityCondition.makeCondition("paymentTypeId", EntityOperator.NOT_IN, paymentTypeIds);
List<GenericValue> listPaymentType = delegator.findList("PaymentType",paymentTypeCond, null, null, null, false);
context.listPaymentType = listPaymentType;