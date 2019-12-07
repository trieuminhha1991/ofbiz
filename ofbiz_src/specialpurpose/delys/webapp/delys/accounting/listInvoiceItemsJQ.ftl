 <#assign columnlist="{ text: '${uiLabelMap.invoiceItemSeqId}', dataField: 'invoiceItemSeqId', width: 200 },
                     { text: '${uiLabelMap.invoiceItemTypeName}', dataField: 'invoiceItemTypeName', width: 300 },
                     { text: '${uiLabelMap.inventoryItemId}', dataField: 'inventoryItemId', width: 150 },
                     { text: '${uiLabelMap.productId}', dataField: 'productId', width: 200, cellsrenderer : linkproductrenderer },
                     { text: '${uiLabelMap.uomId}', dataField: 'uomId', width: 100 },
		    		 { text: '${uiLabelMap.quantity}', dataField: 'quantity', cellsformat: 'D',  width: 150, cellsrenderer: quantityrender },
		    		 { text: '${uiLabelMap.amount}', dataField: 'amount', cellsformat: 'c2', width: 150 },	
		   		     { text: '${uiLabelMap.description}', dataField: 'description', width: 300 },
		    		 { text: '${uiLabelMap.orderId}', dataField: 'orderId', width: 150, cellsrenderer: linkorderrenderer },
                     { text: '${uiLabelMap.ApTotal}', dataField: 'total', cellsformat: 'c2', width: 200, cellsrenderer: totalrender}  
                     "/>
                     
<#assign dataField="{ name: 'invoiceItemSeqId', type: 'string' },
                 { name: 'invoiceItemTypeName', type: 'string' },
                 { name: 'inventoryItemId', type: 'string' },
                 { name: 'productId', type: 'string' },
                 { name: 'uomId', type: 'string' },
                 { name: 'quantity', type: 'number' },
		 		 { name: 'amount', type: 'number' },
				 { name: 'description', type: 'string' },
   				 { name: 'orderId', type: 'string' },
   				 { name: 'total', type: 'number' }
		 		 "/>      
    
<@jqGrid url="/delys/control/listJQQuery" primaryColumn="invoiceItemSeqId" defaultSortColumn="invoiceItemSeqId" columnlist=columnlist dataField=dataField height="400"  currencySymbol="₫" id="jqxgridIt" 
		 entityName="InvoiceItemAndOrderAndType" updateUrl="" createUrl="" conditionsFind ="${conditionsFind}" otherCondition=""  updaterow="false" showtoolbar="false" editable="false" deleterow="false" addrow="false" doubleClick="false" noConditionFind="N"
		 addmultiplerows="false" excelExport="false" toPrint="false" filterbutton="false" clearfilteringbutton="false" updatemultiplerows="false" removeUrl="" updateMulUrl="" keyvalue="glAccountId;accountCode;accountName" filtersimplemode="false"
		 dictionaryColumns="invoiceItemSeqId;invoiceItemTypeName;inventoryItemId;productId;uomId;quantity;amount;description;orderId"  editColumns=""/>                	


<#--<@jqTable pageable="true" url="/delys/control/listGlAccountOrganization2" columnlist=columnlist dataField=dataField entityName="GlAccount"/> -->