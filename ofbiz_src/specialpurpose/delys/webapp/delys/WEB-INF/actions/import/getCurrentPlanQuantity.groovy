BigDecimal value = new java.math.BigDecimal(planQuantity).subtract(new java.math.BigDecimal(recentPlanQuantity)).subtract(new java.math.BigDecimal(orderedQuantity));
BigDecimal quantityToOrder = 0;
if (value.compareTo(BigDecimal.ZERO) < 0){
	quantityToOrder = 0;
} else {
	quantityToOrder = value;
}
context.quantityToOrder = quantityToOrder;
