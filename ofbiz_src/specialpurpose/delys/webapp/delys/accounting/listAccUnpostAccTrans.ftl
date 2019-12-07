<script>
	<#assign acctgTransTypes = delegator.findList("AcctgTransType", null, null, null, null, false) />
	var acctgTransTypesData =  new Array();
	<#list acctgTransTypes as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.description?if_exists)>
		row['acctgTransTypeId'] = "${item.acctgTransTypeId?if_exists}";
		row['description'] = "${description}";
		acctgTransTypesData[${item_index}] = row;
	</#list>
	
	<#assign glFiscalTypes = delegator.findList("GlFiscalType", null, null, null, null, false) />
	var glFiscalTypesData =  new Array();
	<#list glFiscalTypes as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.description?if_exists)>
		row['glFiscalTypeId'] = "${item.glFiscalTypeId?if_exists}";
		row['description'] = "${description}";
		glFiscalTypesData[${item_index}] = row;
	</#list>
	
	<#assign invoices = delegator.findList("InvoiceAndType", null, null, null, null, false) />
	var invoiceData = new Array();
	<#list invoices as item>
		<#assign description = StringUtil.wrapString(item.description?if_exists + "[" + item.invoiceTypeDesc?if_exists + "]") />
		var row = {};
		row['invoiceId'] = '${item.invoiceId?if_exists}';
		row['description'] = '${description}';
		invoiceData[${item_index}] = row;
	</#list>
	
	<#assign payments = delegator.findList("PaymentAndType", null, null, null, null, false) />
	var paymentData = new Array();
	<#list payments as item>
		<#assign description = StringUtil.wrapString(item.description?if_exists) />
		var row = {};
		row['paymentId'] = '${item.paymentId?if_exists}';
		row['description'] = '${description}';
		paymentData[${item_index}] = row;
	</#list>
	
	<#assign workEfforts = delegator.findList("WorkEffort", null, null, null, null, false) />
	var workEffortData = new Array();
	<#list workEfforts as item>
		<#assign description = StringUtil.wrapString(item.workEffortName?if_exists) />
		var row = {};
		row['workEffortId'] = '${item.workEffortId?if_exists}';
		row['description'] = '${description}';
		workEffortData[${item_index}] = row;
	</#list>
	
	<#assign shipments = delegator.findList("ShipmentAndType", null, null, null, null, false) />
	var shipmentData = new Array();
	<#list shipments as item>
		<#assign description = StringUtil.wrapString(item.description?if_exists) />
		var row = {};
		row['shipmentId'] = '${item.shipmentId?if_exists}';
		row['description'] = '${description}';
		shipmentData[${item_index}] = row;
	</#list>
</script>
<#assign columnlist="{ text: '${uiLabelMap.acctgTransId}', dataField: 'acctgTransId', width: 150,
						cellsrenderer: function (row, column, value){
							return '<span> <a href=' + 'EditAccountingTransaction?acctgTransId='+ value + '&organizationPartyId=company' + '>' + value + '</a></span>'
						}
					 },
 					 { text: '${uiLabelMap.transactionDate}',filtertype: 'range', dataField: 'transactionDate', width: 150, cellsformat: 'dd/MM/yyyy HH:mm:ss'},
					 { text: '${uiLabelMap.acctgTransTypeId}', dataField: 'acctgTransTypeId', width: 150,
						cellsrenderer: function (row, column, value) {
							for(i = 0; i < acctgTransTypesData.length; i++){
									if(value == acctgTransTypesData[i].acctgTransTypeId){
										return '<span title=' + acctgTransTypesData[i].acctgTransTypeId + '>' + acctgTransTypesData[i].description + '</span>';
									}
							}
						}
					 },
					 { text: '${uiLabelMap.glFiscalTypeId}', dataField: 'glFiscalTypeId', width: 150,
						cellsrenderer: function (row, column, value) {
							for(i = 0; i < glFiscalTypesData.length; i++){
									if(value == glFiscalTypesData[i].glFiscalTypeId){
										return '<span title=' + glFiscalTypesData[i].glFiscalTypeId + '>' + glFiscalTypesData[i].description + '</span>';
								}
							}
						}
					 },
					 { text: '${uiLabelMap.invoiceId}', dataField: 'invoiceId', width: 150,
						cellsrenderer: function (row, column, value) {
							for(i = 0; i < invoiceData.length; i++){
									if(value == invoiceData[i].invoiceId){
										return '<span> <a title=' + invoiceData[i].invoiceId + ' href=' +'/accounting/control/invoiceOverview?invoiceId=' + invoiceData[i].invoiceId + '>' + invoiceData[i].description + '</a> </span>';
								}
							}
						}
					 },
					 { text: '${uiLabelMap.partyId}', dataField: 'partyId', width: 150},
					 { text: '${uiLabelMap.paymentId}', dataField: 'paymentId', width: 150,
							cellsrenderer: function (row, column, value) {
								for(i = 0; i < paymentData.length; i++){
										if(value == paymentData[i].paymentId){
											return '<span> <a title=' + paymentData[i].paymentId + ' href=' +'/accounting/control/editPayment?paymentId=' + paymentData[i].paymentId + '>' + paymentData[i].description + '</a> </span>';
									}
								}
							}
						 },
						 { text: '${uiLabelMap.workEffortId}', dataField: 'workEffortId', width: 150,
							cellsrenderer: function (row, column, value) {
								for(i = 0; i < workEffortData.length; i++){
										if(value == workEffortData[i].workEffortId){
											return '<span> <a title=' + workEffortData[i].workEffortId + ' href=' +'/workeffort/control/EditWorkEffort?workEffortId=' + workEffortData[i].workEffortId + '>' + workEffortData[i].description + '</a> </span>';
									}
								}
							}
						 },
						 { text: '${uiLabelMap.shipmentId}', dataField: 'shipmentId', width: 150,
							cellsrenderer: function (row, column, value) {
								for(i = 0; i < shipmentData.length; i++){
										if(value == shipmentData[i].shipmentId){
											return '<span> <a title=' + shipmentData[i].shipmentId + ' href=' +'/facility/control/EditShipment?shipmentId=' + shipmentData[i].shipmentId + '>' + shipmentData[i].description + '</a> </span>';
									}
								}
							}
						},
						{ text: '${uiLabelMap.Post}', width: 150, 
							 cellsrenderer: function (row, column, value) {
							 	var data = $('#jqxgrid').jqxGrid('getrowdata', row);
							 	if(data.isPosted == 'Y'){
							 		return ;
							 	}else{
							 		return '<span><a href=postAcctgTrans?acctgTransId=' + data.acctgTransId +'>' + '${uiLabelMap.Post}' + '</a></span>';
							 	}
						 	}
						 },
						 { text: '${uiLabelMap.PDF}', width: 150, 
							 cellsrenderer: function (row, column, value) {
							 	var data = $('#jqxgrid').jqxGrid('getrowdata', row);
							 	if(data.isPosted == 'N'){
							 		return ;
							 	}else{
							 		return '<span><a href=acctgTransDetailReportPdf?acctgTransId=' + data.acctgTransId +'>' + '${uiLabelMap.PDF}' + '</a></span>';
							 	}
						 	}
						 }
					 "/>
<#assign dataField="[{ name: 'acctgTransId', type: 'string' },
                 	{ name: 'transactionDate', type: 'date' },
                 	{ name: 'acctgTransTypeId', type: 'string' },
					{ name: 'glFiscalTypeId', type: 'string' },
					{ name: 'invoiceId', type: 'string' },
                 	{ name: 'paymentId', type: 'string' },
                 	{ name: 'partyId', type: 'string' },
                 	{ name: 'workEffortId', type: 'string' }, 
                 	{ name: 'shipmentId', type: 'string'}
		 		 	]"/>	
<@jqGrid filtersimplemode="true" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true" showtoolbar="true" addrow="false" filterable="true" alternativeAddPopup="alterpopupWindow" editable="false" 
		 url="jqxGeneralServicer?sname=JQListUnpostedTransaction"
	   />