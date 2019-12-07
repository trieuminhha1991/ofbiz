import java.util.*;
import java.lang.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.*;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.service.ServiceUtil;


result = ServiceUtil.returnSuccess();
result.outputValue = (org.ofbiz.accounting.payment.PaymentWorker.getPaymentNotApplied(delegator,context.inputValue)).toString();
return result;        