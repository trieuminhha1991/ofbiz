import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.model.*;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.order.order.OrderReadHelper;

userLogin = session.getAttribute("userLogin");
partyId = userLogin.get('partyId');

orderId = context.get("orderId");
orderItemSeqId = context.get("orderItemSeqId");
GenericValue orderItem = delegator.findOne("OrderItem", UtilMisc.toMap("orderId", orderId, "orderItemSeqId", orderItemSeqId), false);

OrderReadHelper order = new OrderReadHelper(delegator, orderId);
BigDecimal orderAdjustment = order.getOrderItemAdjustmentsTotal(orderItem);
BigDecimal orderItemsSubTotal = order.getOrderItemsSubTotal();
BigDecimal orderTaxTotal = order.getHeaderTaxTotal();
String currency = order.getCurrency();

context.orderAdjustment = orderAdjustment;
context.orderItemsSubTotal = orderItemsSubTotal;
context.currency = currency;
context.orderTaxTotal = orderTaxTotal; 
