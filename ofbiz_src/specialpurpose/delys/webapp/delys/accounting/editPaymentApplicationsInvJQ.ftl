<#assign dataField= "[
 	{name : 'paymentApplicationId',type : 'string'},
 	{name : 'paymentId',type : 'string'},
 	{name : 'invoiceId',type : 'string'},
 	{name : 'invoiceItemSeqId',type : 'string'},
 	{name : 'amountApplied',type : 'number'},
 	{name : 'currencyUomId',type : 'string'},
 ]"/>
 
<#assign columnlist="
 	{text : '${uiLabelMap.FormFieldTitle_invoiceId}',datafield : 'invoiceId',cellsrenderer : function(row){
 		var data = $(\"#editPayment\").jqxGrid('getrowdata',row);
 		return '<a href=\"accArinvoiceOverview?invoiceId='+ data.invoiceId +'\"> '+ data.invoiceId +' </a>';
 	}},
 	{text : '${uiLabelMap.FormFieldTitle_invoiceItemSeqId}',datafield : 'invoiceItemSeqId'},
 	{text : '${uiLabelMap.FormFieldTitle_amountApplied}',datafield : 'amountApplied',cellsformat : 'd',filtertype : 'number',
 		cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
  		  var data = $('#editPayment').jqxGrid('getrowdata', row);
		  return '<span>' + formatcurrency(data.amountApplied,data.currencyUomId) + '</span>';
  	  }  
 	}
 "/>
<#assign customTitleProperties = uiLabelMap.AccountingPaymentsApplied + " " +  Static["org.ofbiz.base.util.UtilFormatOut"].formatCurrency(appliedAmount?if_exists, payment.currencyUomId?if_exists, locale) + " " + uiLabelMap.AccountingOpenPayments + " " + Static["org.ofbiz.base.util.UtilFormatOut"].formatCurrency(notAppliedAmount?if_exists, payment.currencyUomId?if_exists, locale)>
<@jqGrid dataField=dataField columnlist=columnlist filtersimplemode="true" customTitleProperties="${customTitleProperties}" isShowTitleProperty="true" showtoolbar="true" url="jqxGeneralServicer?sname=jqgetListEditPaymentApps&paymentId=${paymentId?if_exists}"
 	id="editPayment" removeUrl="jqxGeneralServicer?sname=removePaymentApplication&jqaction=D" deleteColumn="paymentApplicationId;paymentId;invoiceId;invoiceItemSeqId;amountApplied"
  />