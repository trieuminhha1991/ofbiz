import net.sf.json.JSONArray;

String listProductInfor = parameters.myList;

//String params = request.getParameter("myList");

System.out.println("AA:sds" +listProductInfor);JSONArray.fromObject(listProductInfor);
//JSONArray costCenters = JSONArray.fromObject(listProductInfor);
JSONArray costCenters = new JSONArray().fromObject(listProductInfor);
context.listProductInfor = costCenters;
context.totalPriceAll = parameters.totalPriceAll;
context.totalWeightAll = parameters.totalWeightAll;
context.currencyUomId = parameters.currencyUomId;
context.madeOn = parameters.madeOn;
context.txtNamePlan = parameters.txtNamePlan;
context.txtCompanyName1 = parameters.txtCompanyName1;
context.txtAddress1 = parameters.txtAddress1;
context.txtTel1 = parameters.txtTel1;
context.txtFax1 = parameters.txtFax1;
context.txtOther1 = parameters.txtOther1;
context.txtCompanyName2 = parameters.txtCompanyName2;
context.txtAddress2 = parameters.txtAddress2;
context.txtOther2 = parameters.txtOther2;
context.txtSupplierBank = parameters.txtSupplierBank;
context.txtBeneficiary = parameters.txtBeneficiary;
context.txtTheDateOfShipment = parameters.txtTheDateOfShipment;
context.txtPortOfDischarging = parameters.txtPortOfDischarging;
context.txtPacking = parameters.txtPacking;
context.txtDocumentation = parameters.txtDocumentation;
context.txtTransportation = parameters.txtTransportation;
