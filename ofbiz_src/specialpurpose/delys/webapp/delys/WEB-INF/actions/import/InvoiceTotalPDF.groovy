import net.sf.json.JSONArray;

String listProductInfor = parameters.myList;
JSONArray listConvert = new JSONArray().fromObject(listProductInfor);
context.listProductInfor = listConvert;

String listInvoicePartyInfo = parameters.txtInvoicePartyInfo;
JSONArray listConvertInvoicePartyInfo = new JSONArray().fromObject(listInvoicePartyInfo);
context.txtInvoicePartyInfo = listConvertInvoicePartyInfo;


String listtxtInvoiceInfo = parameters.txtInvoiceInfo;
JSONArray listConverttxtInvoiceInfo = new JSONArray().fromObject(listtxtInvoiceInfo);
context.txtInvoiceInfo = listConverttxtInvoiceInfo;

String listDynamicField = parameters.txtDynamicField;
JSONArray listConvertDynamicField = new JSONArray().fromObject(listDynamicField);
context.txtDynamicField = listConvertDynamicField;

context.txtRecipientInvoice = parameters.txtRecipientInvoice;
context.txtClientNumber = parameters.txtClientNumber;
context.txtInvoiceNo = parameters.txtInvoiceNo;
context.txtInvoiceDate = parameters.txtInvoiceDate;
context.txtContactAddressPhone = parameters.txtContactAddressPhone;
context.txtContactAddressFax = parameters.txtContactAddressFax;
context.txtClientNo = parameters.txtClientNo;
context.txtOrderNo = parameters.txtOrderNo;
context.txtExternalOrderNo = parameters.txtExternalOrderNo;
context.txtDeliveryNo1 = parameters.txtDeliveryNo1;
context.txtDeliveryDate1 = parameters.txtDeliveryDate1;
context.txtDeliveryNo2 = parameters.txtDeliveryNo2;
context.txtDeliveryDate2 = parameters.txtDeliveryDate2;

context.lblTotal = parameters.lblTotal;
context.lblVAT = parameters.lblVAT;
context.lblVATValue = parameters.lblVATValue;
context.lblVATAmount = parameters.lblVATAmount;
context.lblFinalAmount = parameters.lblFinalAmount;
context.currencyUom = parameters.currencyUom;
context.txtNoOfPallet = parameters.txtNoOfPallet;
context.txtTotalNetWeight = parameters.txtTotalNetWeight;
context.weightUomId = parameters.weightUomId;
context.txtNoOfSaleUnit = parameters.txtNoOfSaleUnit;
context.txtTotalGrossWeightt = parameters.txtTotalGrossWeightt;
context.txtDBTragerpallette = parameters.txtDBTragerpallette;
context.txtX = parameters.txtX;
context.txtTotalNoPallet = parameters.txtTotalNoPallet;
context.lblTotal = parameters.lblTotal;
