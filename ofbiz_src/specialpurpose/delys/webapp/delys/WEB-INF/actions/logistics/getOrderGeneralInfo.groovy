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
GenericValue orderItem = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderId), false);

OrderReadHelper order = new OrderReadHelper(delegator, orderId);
BigDecimal orderTaxTotal = order.getTaxTotal();
BigDecimal orderShippingTotal = order.getShippingTotal();
BigDecimal orderGrandTotal = order.getOrderGrandTotal();
BigDecimal orderItemsSubTotal = order.getOrderItemsSubTotal();
List<GenericValue> adjustments = order.getAdjustments();
List<GenericValue> orderItems = delegator.findList("OrderItem", EntityCondition.makeCondition(UtilMisc.toMap("orderId", orderId)), null, null, null, false);
BigDecimal orderAdjustmentTotal = order.calcOrderAdjustments(order.getOrderHeaderAdjustments(adjustments, null), order.getOrderItemsSubTotal(orderItems, adjustments), true, true, true);
String currencyUomId = order.getCurrency();

context.orderTaxTotal = orderTaxTotal;
context.orderShippingTotal = orderShippingTotal;
context.orderGrandTotal = orderGrandTotal;
context.orderItemsSubTotal = orderItemsSubTotal;
context.orderAdjustmentTotal = orderAdjustmentTotal;
context.currencyUomId = currencyUomId;
