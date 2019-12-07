<#assign dataField="[
	{name : 'invoiceId',type : 'string'},
	{name : 'termTypeId',type : 'string'},
	{name : 'dueDate',type : 'date'},
	{name : 'amount',type : 'number'},
	{name : 'paidAmount',type : 'string'},
	{name : 'outstandingAmount',type : 'number'}
]"/>



<#assign columnlist="
	{text : '${uiLabelMap.FormFieldTitle_invoiceId}',datafield : 'invoiceId',hidden: true},
	{text : '${uiLabelMap.FormFieldTitle_termTypeId}',datafield : 'termTypeId',width : '20%'},
	{text : '${uiLabelMap.FormFieldTitle_dueDate}',datafield : 'dueDate',width : '20%',cellsformat : 'dd/MM/yyyy'},
	{text : '${uiLabelMap.FormFieldTitle_amount}',datafield : 'amount',width : '20%',cellsformat : 'd',cellsrenderer : function(row){
		var data = $('#jqxgridListPaymentInfo').jqxGrid('getrowdata',row);
		return '<span>'+ formatcurrency(data.amount,null) +'</span>';
	}},
	{text : '${uiLabelMap.FormFieldTitle_paidAmount}',datafield : 'paidAmount',width : '20%'},
	{text : '${uiLabelMap.FormFieldTitle_outstandingAmount}',datafield : 'outstandingAmount',cellsformat : 'd',cellsrenderer : function(row){
		var data = $('#jqxgridListPaymentInfo').jqxGrid('getrowdata',row);
		return '<span>'+ formatcurrency(data.outstandingAmount,null) +'</span>';
	}}
"/>


<@jqGrid id="jqxgridListPaymentInfo" clearfilteringbutton="true" filterable="false" dataField=dataField columnlist=columnlist url="jqxGeneralServicer?sname=jqGetListPaymentInfo&invoiceId=${parameters.invoiceId?if_exists}"/>

