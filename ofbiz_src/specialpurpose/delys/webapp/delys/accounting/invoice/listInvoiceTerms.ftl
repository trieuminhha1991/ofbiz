<#assign dataField="[
	{name : 'invoiceId',type : 'string'},
	{name : 'invoiceTermId',type : 'string'},
	{name : 'termTypeId',type : 'string'},
	{name : 'invoiceItemSeqId',type : 'string'},
	{name : 'termValue',type : 'string'},
	{name : 'termDays',type : 'string'},
	{name : 'textValue',type : 'string'},
	{name : 'description',type : 'string'},
	{name : 'uomId',type : 'string'}
]"/>


<#assign columnlist="
	{text : '${uiLabelMap.FormFieldTitle_invoiceId}',datafield : 'invoiceId',hidden: true},
	{text : '${uiLabelMap.FormFieldTitle_invoiceTermId}',datafield : 'invoiceTermId',hidden: true},
	{text : '${uiLabelMap.FormFieldTitle_termTypeId}',datafield : 'termTypeId',width : '20%'},
	{text : '${uiLabelMap.FormFieldTitle_invoiceItemSeqId}',datafield : 'invoiceItemSeqId',width : '20%'},
	{text : '${uiLabelMap.FormFieldTitle_termValue}',datafield : 'termValue',width : '20%'},
	{text : '${uiLabelMap.FormFieldTitle_termDays}',datafield : 'termDays',width : '20%'},
	{text : '${uiLabelMap.FormFieldTitle_textValue}',datafield : 'textValue',width : '20%'},
	{text : '${uiLabelMap.FormFieldTitle_description}',datafield : 'description',width : '20%'},
	{text : '${uiLabelMap.FormFieldTitle_uomId}',datafield : 'uomId'}
"/>

<@jqGrid id="jqxgridListInvoiceTerm" clearfilteringbutton="true" filterable="false"  dataField=dataField columnlist=columnlist url="jqxGeneralServicer?sname=jqGetListInvoiceTerms&invoiceId=${parameters.invoiceId?if_exists}"/>
