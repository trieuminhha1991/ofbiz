import java.util.*;
import java.lang.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.*;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.service.ServiceUtil;
import java.text.NumberFormat;

result = ServiceUtil.returnSuccess();
result.outputValue =  (NumberFormat.getNumberInstance(context.get("locale")).format(org.ofbiz.accounting.invoice.InvoiceWorker.getInvoiceTotal(delegator,context.inputValue)));
return result;