 <#assign columnlist="{ text: '${StringUtil.wrapString(uiLabelMap.acctgTransId)}', dataField: 'acctgTransId', width: '10%'},                     
					 { text: '${StringUtil.wrapString(uiLabelMap.transTypeDescription)}', datafield: 'transTypeDescription', width: '15%' },
					 { text: '${StringUtil.wrapString(uiLabelMap.transactionDate)}', width:'15%', datafield: 'transactionDate', cellsformat: 'dd/MM/yyyy hh:mm:ss', filtertype: 'range'},		     		 
		             { text: '${StringUtil.wrapString(uiLabelMap.accountCode)}', dataField: 'accountCode', width: '10%' },
		             { text: '${StringUtil.wrapString(uiLabelMap.accountName)}', dataField: 'accountName', width: '20%' },					 
		             { text: '${StringUtil.wrapString(uiLabelMap.apAmount)}', dataField: 'amount', width: '15%', cellsrenderer:
						 	function(row, colum, value){						 	
					 		var data = $('#jqxgridFatr').jqxGrid('getrowdata', row);
					 		return \"<span>\" + formatcurrency(data.amount,data.currencyUomId) + \"</span>\";
					 }},
		             { text: '${StringUtil.wrapString(uiLabelMap.debitCreditFlag)}', dataField: 'debitCreditFlag', width: '10%' },
                     { text: '${StringUtil.wrapString(uiLabelMap.isPosted)}', dataField: 'isPosted', width: 150 }
                      "/>
<#assign dataField="[{ name: 'acctgTransId', type: 'string' },
                 { name: 'transTypeDescription', type: 'string'},                                             
                 { name: 'transactionDate', type: 'date', other: 'Timestamp'},
                 { name: 'accountCode', type: 'string' },
                 { name: 'accountName', type: 'string' },
                 { name: 'amount', type: 'number' },
                 { name: 'debitCreditFlag', type: 'string' },                 
                 { name: 'isPosted', type: 'string' },
                 { name: 'currencyUomId', type: 'string' }]
		 		 "/>	
	
<@jqGrid  filtersimplemode="false" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true" showtoolbar="true"
		 addrow="false" deleterow="false" id="jqxgridFatr" customTitleProperties="${StringUtil.wrapString(uiLabelMap.AccountingTransactions)}"
		 url="jqxGeneralServicer?sname=listFixedAssetTransactionsJqx&fixedAssetId=${parameters.fixedAssetId}" 		 
		 />