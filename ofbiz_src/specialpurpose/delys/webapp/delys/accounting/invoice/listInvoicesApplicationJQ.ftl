<#assign dataField="[
	{name : 'invoiceItemSeqId',type : 'string'},
	{name : 'productId',type : 'string'},
	{name : 'description',type : 'string'},
	{name : 'total',type : 'number'},
	{name : 'invoiceId',type : 'string'},
	{name : 'paymentId',type : 'string'},
	{name : 'billingAccountId',type : 'string'},
	{name : 'paymentApplicationId',type : 'string'},
	{name : 'amountApplied',type : 'string'}
]"/>

<#assign columnlist="
	{text : '${uiLabelMap.FormFieldTitle_invoiceItemSeqId}',datafield : 'invoiceItemSeqId',width : '20%'},
	{text : '${uiLabelMap.FormFieldTitle_productId}',datafield : 'productId',width : '20%'},
	{text : '${uiLabelMap.FormFieldTitle_description}',datafield : 'description',width : '20%'},
	{text : '${uiLabelMap.FormFieldTitle_total}',datafield : 'total',width : '20%'},
	{text : '${uiLabelMap.FormFieldTitle_invoiceId}',datafield : 'invoiceId',width : '20%',hidden: true},
	{text : '${uiLabelMap.FormFieldTitle_paymentId}',datafield : 'paymentId',width : '20%'},
	{text : '${uiLabelMap.FormFieldTitle_billingAccountId}',datafield : 'billingAccountId',width : '20%'},
	{text : '${uiLabelMap.FormFieldTitle_paymentApplicationId}',datafield : 'paymentApplicationId',width : '20%'},
	{text : '${uiLabelMap.FormFieldTitle_amountApplied}',datafield : 'amountApplied',width : '20%'}
"/>

<@jqGrid id="jqxgridListInvoiceApplication" width="540" clearfilteringbutton="true" dataField=dataField columnlist=columnlist url="jqxGeneralServicer?sname=jqGetListInvoicesApplication&invoiceId=${parameters.invoiceId?if_exists}"/>
