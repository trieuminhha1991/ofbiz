 <#assign columnlist="{ text: '${uiLabelMap.acctgTransId}', dataField: 'acctgTransId', width: 150},
                     { text: '${uiLabelMap.acctgTransEntrySeqId}', dataField: 'acctgTransEntrySeqId', width: 200 },
                     { text: '${uiLabelMap.isPosted}', dataField: 'isPosted', width: 150 },
                     { text: '${uiLabelMap.glFiscalTypeId}', dataField: 'glFiscalTypeId', width: 200 },                     
					 { text: '${uiLabelMap.transTypeDescription}', datafield: 'transTypeDescription', width: 200 },                           
		     		 { text: '${uiLabelMap.transactionDate}', dataField: 'transactionDate', filtertype: 'date' , cellsformat: 'dd/MM/yyyy hh:mm:ss',  width: 170 },
		             { text: '${uiLabelMap.postedDate}', dataField: 'postedDate', filtertype: 'date',  cellsformat: 'dd/MM/yyyy hh:mm:ss', width: 170},	   		     
		             { text: '${uiLabelMap.glJournalName}', dataField: 'glJournalName', width: 250 },
                     { text: '${uiLabelMap.paymentId}', dataField: 'paymentId', width: 150, cellsrenderer:
	                 	function(row, colum, value)
	                    {
                    		return \"<span><a href='accAppaymentOverview?paymentId=\" + value + \"'>\" + value + \"</span>\";
	                    }},
		             { text: '${uiLabelMap.fixedAssetId}', dataField: 'fixedAssetId', width: 150 },
		             { text: '${uiLabelMap.AccountingProduct}', dataField: 'productId', width: 150 },
		             { text: '${uiLabelMap.debitCreditFlag}', dataField: 'debitCreditFlag', width: 150 },
		             { text: '${uiLabelMap.apAmount}', dataField: 'amount', width: 150, cellsformat: 'c2' },
		             { text: '${uiLabelMap.origAmount}', dataField: 'origAmount', width: 150, cellsformat: 'c2' },
		             { text: '${uiLabelMap.accountCode}', dataField: 'accountCode', width: 150 },
		             { text: '${uiLabelMap.accountName}', dataField: 'accountName', width: 150 },
		             { text: '${uiLabelMap.apPartyId}', dataField: 'partyId', width: 150},
		             { text: '${uiLabelMap.reconcileStatusName}', dataField: 'reconcileStatusName', width: 150 }
                      "/>
<#assign dataField="[{ name: 'acctgTransId', type: 'string' },
                 { name: 'acctgTransEntrySeqId', type: 'string' },
                 { name: 'isPosted', type: 'string' },
                 { name: 'glFiscalTypeId', type: 'string' }, 
                 { name: 'transTypeDescription', type: 'string'},                                             
                 { name: 'transactionDate', type: 'date' },
 		 { name: 'postedDate', type: 'date' },
		 { name: 'glJournalName', type: 'string' },
		 { name: 'paymentId', type: 'string' },
		 { name: 'fixedAssetId', type: 'string' },
		 { name: 'productId', type: 'string' },
		 { name: 'debitCreditFlag', type: 'string' },
		 { name: 'amount', type: 'number' },
		 { name: 'origAmount', type: 'number' },
		 { name: 'accountCode', type: 'string' },
		 { name: 'accountName', type: 'string' },
		 { name: 'partyId', type: 'string' },
		 { name: 'reconcileStatusName', type: 'string' }]
		 		 "/>	
<style type="text/css">
	#jqxgrid2 .jqx-grid-header-olbius{
		height:25px !important;
	}	
	#jqxgrid2 .jqx-grid-header-olbius{
		height:25px !important;
	}
	#jqxgrid2{
		width: calc(100% - 2px) !important;
	}
	#jqxgrid{
		width: calc(100% - 2px) !important;
	}
</style>			 		 
<@jqGrid url="jqxGeneralServicer?invoiceId=${parameters.invoiceId}&sname=JQListAPAcctgTransAndEntries" dataField=dataField columnlist=columnlist filterable="true" filtersimplemode="false"
		 id="jqxgrid5" customTitleProperties="${uiLabelMap.AccountingTransactions}"/>