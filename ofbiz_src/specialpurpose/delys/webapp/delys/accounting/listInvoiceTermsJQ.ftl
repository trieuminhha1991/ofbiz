 <#assign columnlist="{ text: '${uiLabelMap.termTypeName}', dataField: 'termTypeName', width: 150},
                     { text: '${uiLabelMap.invoiceItemSeqId}', dataField: 'invoiceItemSeqId', width: 200 },
                     { text: '${uiLabelMap.termValue}', dataField: 'termValue', width: 150 },
                     { text: '${uiLabelMap.termDays}', dataField: 'termDays', width: 200 },                     
					 { text: '${uiLabelMap.textValue}', datafield: 'textValue', width: 200 },                           
		     		 { text: '${uiLabelMap.description}', dataField: 'description', width: 200 },
		             { text: '${uiLabelMap.uomId}', dataField: 'uomId', width: 200 },	   		     
                      "/>
<#assign dataField="{ name: 'termTypeName', type: 'string' },
                 { name: 'invoiceItemSeqId', type: 'string' },
                 { name: 'termValue', type: 'number' },
                 { name: 'termDays', type: 'number' }, 
                 { name: 'textValue', type: 'string'},                                             
                 { name: 'description', type: 'string' },
 		 		 { name: 'uomId', type: 'string' }
		 		 "/>	
<@jqGrid url="/delys/control/listJQQuery"  primaryColumn="termTypeName" defaultSortColumn="termTypeId" columnlist=columnlist dataField=dataField  height="400"  currencySymbol="₫" id="jqxgridInvTerm"
		 entityName="InvoiceTermAndType" updateUrl="" createUrl="" conditionsFind ="${conditionsFind}" otherCondition="" updaterow="false" showtoolbar="false" editable="false" deleterow="false" addrow="false" doubleClick="false" noConditionFind="N"
		 addmultiplerows="false" excelExport="false" toPrint="false" filterbutton="false" clearfilteringbutton="false" updatemultiplerows="false" removeUrl="" updateMulUrl="" keyvalue="glAccountId;accountCode;accountName" filtersimplemode="false"
		 dictionaryColumns="termTypeId;invoiceItemSeqId;termValue;termDays;textValue;description;uomId" editColumns="" />                	


<#--<@jqTable pageable="true" url="/delys/control/listGlAccountOrganization2" columnlist=columnlist dataField=dataField entityName="GlAccount"/> -->