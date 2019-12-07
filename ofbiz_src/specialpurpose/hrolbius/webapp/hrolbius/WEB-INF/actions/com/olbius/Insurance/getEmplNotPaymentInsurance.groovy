import javolution.util.FastList;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;


insurancePaymentId = parameters.insurancePaymentId;

int viewIndex = 0;
try {
	viewIndex = Integer.parseInt((String) parameters.get("VIEW_INDEX"));
} catch (Exception e) {
	viewIndex = 0;
}
int viewSize = 20;
try {
	viewSize = Integer.parseInt((String) parameters.get("VIEW_SIZE"));
} catch (Exception e) {
	viewSize = 20;
}

String paramList = "";
int listSize = 0;
int lowIndex = 0;
int highIndex = 0;

lowIndex = viewIndex * viewSize + 1;
highIndex = (viewIndex + 1) * viewSize;
EntityFindOptions findOpts = new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, -1, highIndex, true);
// using list iterator
List<EntityCondition> conditions = FastList.newInstance();
conditions.add(EntityCondition.makeCondition("insuranceTypeId", insurancePayment.getString("insuranceTypeId")));
conditions.add(EntityCondition.makeCondition("statusPaymentId", "INS_PAYMENT_CREATED"));
EntityListIterator pli = delegator.find("PartyInsuranceReport", EntityCondition.makeCondition(conditions, EntityOperator.AND), null, null, UtilMisc.toList("-fromDate"), findOpts);

// get the partial list for this page
partyInsuranceReportList = pli.getPartialList(lowIndex, viewSize);

// attempt to get the full size
listSize = pli.getResultsSizeAfterPartialList();
if (highIndex > listSize) {
	highIndex = listSize;
}
// close the list iterator
pli.close();
context.listSize = listSize;
context.partyInsuranceReportList = partyInsuranceReportList;
context.highIndex = highIndex;
context.lowIndex = lowIndex;
context.viewSize = viewSize;
context.viewIndex = viewIndex;
context.paramList = paramList;