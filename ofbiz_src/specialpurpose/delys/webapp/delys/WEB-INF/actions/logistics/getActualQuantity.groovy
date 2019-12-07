BigDecimal actualQuantity = BigDecimal.ZERO;
if (curLackQuantity != null){
	BigDecimal lackQuantityTmp = new BigDecimal(curLackQuantity);
	if (curQuantityRejected != null){
		BigDecimal quantityRejectedTmp = new BigDecimal(curQuantityRejected); 
		actualQuantity = orderedQuantity.subtract(testQuantity.add(sampleQuantity.add(inspectQuantity.add(lackQuantityTmp.add(quantityRejectedTmp)))));
	} else {
		actualQuantity = orderedQuantity.subtract(testQuantity.add(sampleQuantity.add(inspectQuantity.add(lackQuantityTmp))));
	}
} else {
	if (curQuantityRejected != null){
		BigDecimal quantityRejectedTmp = new BigDecimal(curQuantityRejected); 
		actualQuantity = orderedQuantity.subtract(testQuantity.add(sampleQuantity.add(inspectQuantity.add(quantityRejectedTmp))));
	} else {
		actualQuantity = orderedQuantity.subtract(testQuantity.add(sampleQuantity.add(inspectQuantity)));
	}
} 
context.actualQuantity = actualQuantity;