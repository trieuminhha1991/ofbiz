<#include "script/periodData.ftl">
<#include "paymentDetailBookFilter.ftl">

<#assign dataField="[{ name: 'transDate', type: 'date' },
					 { name: 'voucherDate', type: 'date', other:'Timestamp'},
					 { name: 'voucherID', type: 'string'},
					 { name: 'voucherDescription', type: 'string'},
					 { name: 'recipGlAccountCode', type: 'string'},
					 { name: 'discountDueDate', type: 'date'},
					 { name: 'creditAmount', type: 'number'},
					 { name: 'debitAmount', type: 'number'},
					 { name: 'creditBalAmount', type: 'number'},
					 { name: 'debitBalAmount', type: 'number'},
					 { name: 'currencyUomId', type: 'string'},
				 ]"/>

<#assign columnlist="{ text: '${uiLabelMap.BACCTransDate}', dataField: 'transDate', width: 150, cellsformat: 'dd/MM/yyyy',
						cellsrenderer: function(row, column, value){
						},
					 },
					 { text: '${uiLabelMap.BACCVoucherDate}', dataField: 'voucherDate', width: 150, cellsformat: 'dd/MM/yyyy',
					 },
					 { text: '${uiLabelMap.BACCVoucherID}', dataField: 'voucherID', width: 150, columngroup: 'cashVoucher',
					 },
					 { text: '${uiLabelMap.BACCDescription}', dataField: 'voucherDescription', width: 250,
					 },
					 { text: '${uiLabelMap.BACCRecipGlAccountId}', dataField: 'recipGlAccountCode', width: 150,
					 },
					 { text: '${uiLabelMap.BACCDiscountDueDate}', dataField: 'discountDueDate', width: 150,
					 },
					 { text: '${uiLabelMap.BACCCreditAmount}', dataField: 'creditAmount', width: 150, columngroup: 'arisingAmount',
						 cellsrenderer: function(row, column, value){
							 var data = $('#payDetailGrid').jqxGrid('getrowdata',row);
        		  			 if(data != undefined && data){
                		  		return '<span class=align-right>' + formatcurrency(value, data.currencyId) + '</span>';
        		  			 }
						 },
					 },
					 { text: '${uiLabelMap.BACCDebitAmount}', dataField: 'debitAmount', width: 150, columngroup: 'arisingAmount',
						 cellsrenderer: function(row, column, value){
							 var data = $('#payDetailGrid').jqxGrid('getrowdata',row);
        		  			 if(data != undefined && data){
        		  				return '<span class=align-right>' + formatcurrency(value, data.currencyId) + '</span>';
        		  			 }
						 },
					 },
					 { text: '${uiLabelMap.BACCCreditAmount}', dataField: 'creditBalAmount', width: 150, columngroup: 'balAmount',
						 cellsrenderer: function(row, column, value){
							 var data = $('#payDetailGrid').jqxGrid('getrowdata',row);
        		  			 if(data != undefined && data){
                		  		return '<span class=align-right>' + formatcurrency(value, data.currencyId) + '</span>';
        		  			 }
						 },
					 },
					 { text: '${uiLabelMap.BACCDebitAmount}', dataField: 'debitBalAmount', width: 150, columngroup: 'balAmount',
						 cellsrenderer: function(row, column, value){
							 var data = $('#payDetailGrid').jqxGrid('getrowdata',row);
        		  			 if(data != undefined && data){
        		  				return '<span class=align-right>' + formatcurrency(value, data.currencyId) + '</span>';
        		  			 }
						 },
					 },
					 "/>
 <#assign columngrouplist=" { text: '${uiLabelMap.BACCVoucher}', name: 'voucher', align: 'center'},
  							{ text: '${uiLabelMap.BACCArisingAmount}', name: 'arisingAmount', align: 'center'},
  							{ text: '${uiLabelMap.BACCBalanceAmount}', name: 'balAmount', align: 'center'},
						">
<@jqGrid id="payDetailGrid" filtersimplemode="true" filterable="false" addrefresh="true" editable="false" addType="popup" showtoolbar="true" clearfilteringbutton="true"
		 url="jqxGeneralServicer?sname=JqxGetPaymentDetailBook&glAccountId=${receipGlAccount.glAccountId}&customTimePeriodId=${customTimePeriodDefault.customTimePeriodId}&partyId=${party.partyId}" dataField=dataField columnlist=columnlist showstatusbar="false"
		 statusbarheight="30" columngrouplist=columngrouplist
	 />
 
<script>
 	$( document ).ready(function(){
 		filter.initFilter();
 		//filter.bindEvent();
 	});
</script>