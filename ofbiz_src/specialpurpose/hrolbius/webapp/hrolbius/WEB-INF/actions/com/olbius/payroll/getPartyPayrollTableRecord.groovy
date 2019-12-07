import javolution.util.FastList;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;

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

if(payrollTableRecord){
	payrollTableId = payrollTableRecord.getString("payrollTableId");
	List<GenericValue> payrollTableCodeIIT = delegator.findList("PayrollTablePartyGroupAndInvoiceItemType", EntityCondition.makeCondition(
																											EntityCondition.makeCondition("payrollTableId", payrollTableId),
																											EntityOperator.AND,
																											EntityUtil.getFilterByDateExpr("fromDateInvoiceItemType", "thruDateInvoiceItemType")), null, null, null, false);
    List<String> codeList = EntityUtil.getFieldListFromEntityList(payrollTableCodeIIT, "code", true);																										
	//println ("codeList: " + codeList);
	String paramList = "";
	
	paramList = paramList + "&payrollTableId=" + payrollTableId;
	int partyListSize = 0;
	int lowIndex = 0;
	int highIndex = 0;
	lowIndex = viewIndex * viewSize + 1;
	highIndex = (viewIndex + 1) * viewSize;
	//println ("codeList: " + codeList);
	EntityFindOptions findOpts = new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, -1, highIndex, false);
	List<EntityCondition> conditions = FastList.newInstance();
	conditions.add(EntityCondition.makeCondition("payrollTableId", payrollTableId));
	conditions.add(EntityCondition.makeCondition(EntityCondition.makeCondition("statusId", "PAYR_APP"),EntityOperator.OR, EntityCondition.makeCondition("statusId", "PAYR_INIT")));
	conditions.add(EntityCondition.makeCondition("code", EntityOperator.IN,codeList));
	
	EntityListIterator pli = delegator.find("PayrollTable", EntityCondition.makeCondition(conditions), null, null, UtilMisc.toList("partyId"), findOpts);
	/*EntityListIterator pli = delegator.find("PayrollTable", EntityCondition.makeCondition("payrollTableId", payrollTableId), null, null, UtilMisc.toList("partyId"), findOpts);*/
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
	context.partyPayrollTableList = partyList;
	context.highIndex = highIndex;
	context.lowIndex = lowIndex;
	context.viewSize = viewSize;
	context.viewIndex = viewIndex;
	context.paramList = paramList;
}