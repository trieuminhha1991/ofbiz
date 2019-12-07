 <#assign columnlist="{ text: '${uiLabelMap.termTypeId}', dataField: 'termTypeId', width: 150},
                     { text: '${uiLabelMap.dueDate}', dataField: 'dueDate', filtertype: 'date' , cellsformat: 'dd-MM-yyyy h:mm:ss' ,  width: 170 },
                     { text: '${uiLabelMap.amount}', dataField: 'amount', width: 150 },
                     { text: '${uiLabelMap.paidAmount}', dataField: 'paidAmount', width: 150 },
		             { text: '${uiLabelMap.outstandingAmount}', dataField: 'outstandingAmount', width: 200 }	   		     
                      "/>
<#assign dataField="{ name: 'termTypeId', type: 'string' },
                 { name: 'dueDate', type: 'date' },
                 { name: 'amount', type: 'number' },
                 { name: 'paidAmount', type: 'number' }, 
                 { name: 'outstandingAmount', type: 'number'}                                             
		 		 "/>	
<@jqGrid url="/delys/control/listInvoicePaymentInfoJQ"  primaryColumn="termTypeId" defaultSortColumn="termTypeId" columnlist=columnlist dataField=dataField  height="400"  currencySymbol="₫" id="jqxgridInvPmIf"
		 entityName="InvoiceTermAndType" updateUrl="" createUrl="" conditionsFind ="${conditionsFind}" otherCondition="" updaterow="false" showtoolbar="false" editable="false" deleterow="false" addrow="false" doubleClick="false" noConditionFind="N"
		 addmultiplerows="false" excelExport="false" toPrint="false" filterbutton="false" clearfilteringbutton="false" updatemultiplerows="false" removeUrl="" updateMulUrl="" keyvalue="glAccountId;accountCode;accountName" filtersimplemode="false"
		 dictionaryColumns="termTypeId;dueDate;amount;paidAmount;outstandingAmount" editColumns=""/>                	


<#--<@jqTable pageable="true" url="/delys/control/listGlAccountOrganization2" columnlist=columnlist dataField=dataField entityName="GlAccount"/> -->