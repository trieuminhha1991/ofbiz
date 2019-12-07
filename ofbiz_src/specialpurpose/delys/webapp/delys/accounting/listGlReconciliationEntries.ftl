<div class="widget-box transparent no-bottom-border">
	<div class="widget-header">
		<h4>${uiLabelMap.AccountingEditAcctRecon}</h4>
	</div>
</div>
<#assign dataField="[{ name: 'glReconciliationId', type: 'string' },
					 { name: 'acctgTransId', type: 'string'},
					 { name: 'acctgTransEntrySeqId', type: 'string'},
					 { name: 'reconciledAmount', type: 'string'},
					 { name: 'lastUpdatedStamp', type: 'date'}
					 ]
					 "/>
<#assign columnlist="{ text: '${uiLabelMap.glReconciliationId}', datafield: 'glReconciliationId'},
					 { text: '${uiLabelMap.acctgTransId}', datafield: 'acctgTransId'},
					 { text: '${uiLabelMap.acctgTransEntrySeqId}', datafield: 'acctgTransEntrySeqId'},
					 { text: '${uiLabelMap.reconciledAmount}', datafield: 'reconciledAmount'},
					 { text: '${uiLabelMap.lastUpdatedStamp}', datafield: 'lastUpdatedStamp', cellsformat: 'dd/MM/yyyy'}
					 "/>
<@jqGrid url="jqxGeneralServicer?sname=JQListGlReconciliationEntries&glReconciliationId=${parameters.glReconciliationId}" dataField=dataField columnlist=columnlist id="jqxgrid" jqGridMinimumLibEnable="true"/>