import org.ofbiz.entity.GenericValue;

import java.util.*;
import java.lang.*;

import javolution.util.FastList;

import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.*;


organizationPartyId = parameters.organizationPartyId;
EntityCondition varianceReasonExitedCond = EntityCondition.makeCondition("organizationPartyId", EntityOperator.EQUALS, organizationPartyId);
List<GenericValue> listVarianceReasonExisted = delegator.findList("VarianceReasonGlAccount", varianceReasonExitedCond, null, null, null,false);
List<String> varianceReasonIds = FastList.newInstance();
if(UtilValidate.isNotEmpty(listVarianceReasonExisted)){
	for ( varianceReason in listVarianceReasonExisted) {
		String varianceReasonId = varianceReason.getString("varianceReasonId");
		varianceReasonIds.add(varianceReasonId);
	}
}
EntityCondition varianceReasonCond = null;
if(UtilValidate.isNotEmpty(varianceReasonIds)){
	varianceReasonCond = EntityCondition.makeCondition("varianceReasonId", EntityOperator.NOT_IN, varianceReasonIds);
}
	
List<GenericValue> varianceReasons = delegator.findList("VarianceReason",varianceReasonCond, null, null, null, false);
context.varianceReasons = varianceReasons;