 
 <#assign columnlist="
		    		 { text: '${uiLabelMap.PApTotal}', dataField: 'total', cellsformat: 'c2', width: 200 },	
		   		     { text: '${uiLabelMap.paymentId}', dataField: 'paymentId', cellsrenderer: linkpaymentrenderer,  width: 200 },
		    		 { text: '${uiLabelMap.amountApplied}', dataField: 'amountApplied', cellsformat: 'c2',  width: 200}                       
                     "/>
                     
<#assign dataField="{ name: 'total', type: 'number' },
                 { name: 'paymentId', type: 'string' },
                 { name: 'amountApplied', type: 'number' }
		 		 "/>      
		 		 
<@jqGrid url="/delys/control/listJQQueryInvApp" primaryColumn="paymentId" defaultSortColumn="invoiceItemSeqId" columnlist=columnlist dataField=dataField height="400"  currencySymbol="₫" id="jqxgridIA" 
		 entityName="PaymentApplicationTotal" updateUrl="" createUrl="" conditionsFind ="${conditionsFind}" otherCondition="${invoiceId}" updaterow="false" showtoolbar="false" editable="false" deleterow="false" addrow="false" doubleClick="false" noConditionFind="N"
		 addmultiplerows="false" excelExport="false" toPrint="false" filterbutton="false" clearfilteringbutton="false" updatemultiplerows="false" removeUrl="" updateMulUrl="" keyvalue="glAccountId;accountCode;accountName" filtersimplemode="false"
		 dictionaryColumns="paymentId;amountApplied"  editColumns=""/>                	


<#--<@jqTable pageable="true" url="/delys/control/listGlAccountOrganization2" columnlist=columnlist dataField=dataField entityName="GlAccount"/> -->