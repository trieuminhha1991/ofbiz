import java.util.*;
import java.lang.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.*;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.service.ServiceUtil;
import javolution.util.FastList;
import com.olbius.basesales.order.OrderReadHelper;
import java.math.BigDecimal;

//edited by DungPQ

result = ServiceUtil.returnSuccess();
//result.amountToApply = (org.ofbiz.accounting.payment.PaymentWorker.getPaymentNotApplied(delegator,context.paymentId)).toString();


orderId = orderHeader.get("orderId");
//System.println("orderId" + orderId);
BigDecimal amount = orderHeader.getBigDecimal("grandTotal");
BigDecimal amountNotApplied = BigDecimal.ZERO;
BigDecimal amountPaid = BigDecimal.ZERO;

if(orderHeader){
	//orderReadHelper = new OrderReadHelper(orderHeader);
	
	Set<String> invoiceIDs = new HashSet<String>();
	List<EntityCondition> conds = FastList.newInstance();
			conds.add(EntityCondition.makeCondition("orderId",
					EntityOperator.EQUALS, orderId));
			
	List<GenericValue> oib = delegator.findList("OrderItemBilling",
		EntityCondition.makeCondition(conds),
		null, null, null, false
		);
	for(GenericValue o: oib)
		invoiceIDs.add(o.get("invoiceId"));
	
	for(String invoiceId: invoiceIDs){
		conds.clear();
		conds.add(EntityCondition.makeCondition("invoiceId",
					EntityOperator.EQUALS, invoiceId));
			
		List<GenericValue> payment_applications = delegator.findList(
			"PaymentApplication",
			EntityCondition.makeCondition(conds),
			null, null, null, false
		);
		for(GenericValue pa: payment_applications){
			BigDecimal a = pa.getBigDecimal("amountApplied");
			amountPaid = amountPaid.add(a);
		}
	}
			
	amountNotApplied = amount.subtract(amountPaid);//oib.size();	
	//amountNotApplied = amountNotApplied.setScale(2,4);// lay 2 chu so sau dau phay
    BigDecimal amount2 = BigDecimal.ZERO;
    List<GenericValue> ordPayments = delegator.findList("OrderPaymentPreference", EntityCondition.makeCondition("orderId", orderId), null, null, null, false);
    for(GenericValue orderPaymentPre : ordPayments) {
        List<GenericValue> payments = orderPaymentPre.getRelated("Payment", null, null, false);
        for(GenericValue payment : payments) {
            amount2 = amount2.add(payment.getBigDecimal("amount"));
        }
    }

    amountNotApplied = amountNotApplied.subtract(amount2);
}



context.amountNotApplied = amountNotApplied;
context.orderId = orderId;

return result;        