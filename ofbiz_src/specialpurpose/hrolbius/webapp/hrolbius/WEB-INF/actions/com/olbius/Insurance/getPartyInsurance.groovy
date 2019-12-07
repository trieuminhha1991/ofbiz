import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityFunction;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;

partyId = parameters.partyId;
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
int partyListSize = 0;
int lowIndex = 0;
int highIndex = 0;
if (UtilValidate.isNotEmpty(partyId)) {
	paramList = paramList + "&partyId=" + partyId;
	//andExprs.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("partyId"), EntityOperator.LIKE, EntityFunction.UPPER("%"+partyId+"%")));
}
lowIndex = viewIndex * viewSize + 1;
highIndex = (viewIndex + 1) * viewSize;
EntityFindOptions findOpts = new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, -1, highIndex, true);
// using list iterator
EntityListIterator pli = delegator.find("PartyInsuranceAndParty", null, null, null, UtilMisc.toList("partyId"), findOpts);

// get the partial list for this page
partyList = pli.getPartialList(lowIndex, viewSize);

// attempt to get the full size
partyListSize = pli.getResultsSizeAfterPartialList();
if (highIndex > partyListSize) {
	highIndex = partyListSize;
}

// close the list iterator
pli.close();
context.listSize = partyListSize;
context.partyList = partyList;
context.highIndex = highIndex;
context.lowIndex = lowIndex;
context.viewSize = viewSize;
context.viewIndex = viewIndex;
context.paramList = paramList;