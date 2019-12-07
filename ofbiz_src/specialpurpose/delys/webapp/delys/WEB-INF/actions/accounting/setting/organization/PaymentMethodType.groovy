import org.ofbiz.entity.GenericValue;

import java.util.*;
import java.lang.*;

import javolution.util.FastList;

import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.*;


organizationPartyId = parameters.organizationPartyId;
EntityCondition paymentMethodExitedCond = EntityCondition.makeCondition("organizationPartyId", EntityOperator.EQUALS, organizationPartyId);
List<GenericValue> listPaymentMethodExisted = delegator.findList("PaymentMethodTypeGlAccount", paymentMethodExitedCond, null, null, null,false);
List<String> paymentMethodTypeIds = FastList.newInstance();
if(UtilValidate.isNotEmpty(listPaymentMethodExisted)){
	for ( paymentMethodType in listPaymentMethodExisted) {
		String paymentMethodTypeId = paymentMethodType.getString("paymentMethodTypeId");
		paymentMethodTypeIds.add(paymentMethodTypeId);
	}
}
EntityCondition paymentMethodCond = EntityCondition.makeCondition("paymentMethodTypeId", EntityOperator.NOT_IN, paymentMethodTypeIds);
List<GenericValue> listPaymentMethodType = delegator.findList("PaymentMethodType",paymentMethodCond, null, null, null, false);
context.listPaymentMethodType = listPaymentMethodType;