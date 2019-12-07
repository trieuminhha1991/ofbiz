import net.sf.json.JSONArray;

String listProductInfo = parameters.myList;
JSONArray listConvertProductInfo = new JSONArray().fromObject(listProductInfo);
context.listConvertProductInfo = listConvertProductInfo;

context.txtContractNumber = parameters.txtContractNumber;
context.weightUnit = parameters.weightUnit;
context.currencyUnit = parameters.currencyUnit;

context.txtKARTotal = parameters.txtKARTotal;
context.txtquantityTotal = parameters.txtquantityTotal;
context.txttxtNetWeightTotal = parameters.txttxtNetWeightTotal;
context.txtShipmentTotalWeightTotal = parameters.txtShipmentTotalWeightTotal;
context.txttxtValueTotal = parameters.txttxtValueTotal;