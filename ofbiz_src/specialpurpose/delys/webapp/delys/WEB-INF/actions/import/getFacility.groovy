facilities = delegator.findList("Facility", null, null, null, null, false);
context.facilities = facilities;

fieldOrder = ["dimensionId"];
productDim = delegator.findList("ProductDimension", null, null, fieldOrder, null, false);
context.productDim = productDim;

System.out.println("pro:" +productDim);