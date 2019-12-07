import javolution.util.FastList;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;

public static List<GenericValue> getInvoiceItemType(org.ofbiz.entity.Delegator delegator, GenericValue parentType){
	List<GenericValue> tempInvoiceItemTypePayroll = delegator.findList("InvoiceItemType", EntityCondition.makeCondition("parentTypeId", EntityOperator.EQUALS, parentType.getString("invoiceItemTypeId")), null, null, null, false);
	List<GenericValue> retList = FastList.newInstance();
	if(UtilValidate.isNotEmpty(tempInvoiceItemTypePayroll)){
		retList.addAll(tempInvoiceItemTypePayroll);
		for(GenericValue tempGv: tempInvoiceItemTypePayroll){
			retList.addAll(getInvoiceItemType(delegator, tempGv));
		}
	}
	return retList;
}

List<GenericValue> invoiceItemTypePayroll = delegator.findList("InvoiceItemType", EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.LIKE, "PAYROL%"), null, null, null, false);
List<GenericValue> retList = FastList.newInstance();
if(UtilValidate.isNotEmpty(invoiceItemTypePayroll)){
	retList.addAll(invoiceItemTypePayroll);
	for(GenericValue invoiceItem: invoiceItemTypePayroll){
		List<GenericValue> tempList = getInvoiceItemType(delegator, invoiceItem);
		retList.addAll(tempList);
	}	
}
context.invoiceItemTypeList = retList; 