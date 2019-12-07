import java.util.*;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.accounting.invoice.InvoiceWorker;

totalToApply = 0.0;
totalValue = 0.0;
if (result){
	listIt = result.listIt;
	while ((invoiceItem = listIt.next()) != null){
		invoiceId = invoiceItem.getString("invoiceId");
		BigDecimal tmpBD = InvoiceWorker.getInvoiceCurrencyConversionRate(delegator,invoiceId);
		applied = InvoiceWorker.getInvoiceApplied(delegator,invoiceId)*tmpBD;
		value = InvoiceWorker.getInvoiceTotal(delegator,invoiceId)*tmpBD;
		toApply = (value.subtract(applied))*tmpBD;
		totalToApply += toApply;
		totalValue += value;
	}
	listIt.close();
}
context.totalToApply = totalToApply;
context.totalValue = totalValue;