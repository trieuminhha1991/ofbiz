import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.service.ServiceUtil;
import java.sql.Timestamp;
import java.util.Date;
import java.text.SimpleDateFormat;

result = ServiceUtil.returnSuccess();

SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss aa");

if (context.parameters.parameters.fromDate) {
	fromDate = context.parameters.parameters.fromDate;
	thruDate = context.parameters.parameters.thruDate;
	parameters = context.parameters.parameters;
	salesRepPartyList = parameters.get("salesRepPartyList[]");
    List invoiceCond = [];
    invoiceCond.add(EntityCondition.makeCondition("invoiceTypeId", EntityOperator.EQUALS, "SALES_INVOICE"));
    invoiceCond.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "INVOICE_PAID"));
    Date parsedDate = dateFormat.parse(fromDate[0]);
    invoiceCond.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.GREATER_THAN_EQUAL_TO, new java.sql.Timestamp(parsedDate.getTime())));
    invoiceCond.add(EntityCondition.makeCondition("invoiceRoleTypeId", EntityOperator.EQUALS, "SALES_REP"));
    if (thruDate) {
    	parsedDate = dateFormat.parse(thruDate[0]);
        invoiceCond.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.LESS_THAN_EQUAL_TO, new java.sql.Timestamp(parsedDate.getTime())));
    }
    if (salesRepPartyList) {
        invoiceCond.add(EntityCondition.makeCondition("invoiceRolePartyId", EntityOperator.IN, java.util.Arrays.asList(salesRepPartyList)));
    }
    invoiceList = delegator.findList("InvoiceAndRole", EntityCondition.makeCondition(invoiceCond, EntityOperator.AND), null, null, null, false);
    
    List invoices = [];
    if (invoiceList) {
        resultMap = dispatcher.runSync("getInvoicesFilterByAssocType", [invoiceItemAssocTypeId : "COMMISSION_INVOICE", invoiceList : invoiceList, userLogin : userLogin]);
        invoices = resultMap.filteredInvoiceList; 
        result.listIterator = invoices;
    }else{
    	java.util.LinkedList list = new java.util.LinkedList();
    	result.listIterator = list;
    }
}else{
	java.util.LinkedList list = new java.util.LinkedList();
	result.listIterator = list;
}
return result;

