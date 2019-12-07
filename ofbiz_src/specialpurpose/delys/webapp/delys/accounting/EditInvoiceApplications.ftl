<script src="/delys/images/js/generalUtils.js"></script>
<#include "component://delys/webapp/delys/accounting/payment/initGeneralDropdown.ftl"/>
<style>
	.custom{
		color : red;
		font-weight : bold;
		font-size : 15px;
		font-style : italic;
	}
</style>
<#assign dataField = "[
	{name : 'invoiceItemSeqId',type : 'string'},
	{name : 'paymentApplicationId',type : 'string'},
	{name : 'productId',type : 'string'},
	{name : 'description',type : 'string'},
	{name : 'total',type : 'string'},	
	{name : 'paymentId',type : 'string'},
	{name : 'amountToApply',type : 'string'}
]"/>
<#assign datafieldPayment = "[
	{name : 'paymentId',type : 'string'},
	{name : 'partyIdFrom',type : 'string'},
	{name : 'partyIdTo',type : 'string'},
	{name : 'effectiveDate',type : 'date',other: 'Timestamp'},
	{name : 'amount',type : 'number'},
	{name : 'currencyUomId',type : 'string'}
]"/>
<#assign columnlistPayment = "[
	{text : '${uiLabelMap.FormFieldTitle_paymentId}',datafield : 'paymentId',width : '15%'},
	{text : '${uiLabelMap.FormFieldTitle_partyIdFrom}',datafield : 'partyIdFrom',width : '15%'},
	{text : '${uiLabelMap.FormFieldTitle_partyIdTo}',datafield : 'partyIdTo',width : '15%'},
	{text : '${uiLabelMap.FormFieldTitle_effectiveDate}',datafield : 'effectiveDate',width : '15%'},
	{text : '${uiLabelMap.FormFieldTitle_amount}',datafield : 'amount',width : '15%'},
	{text : '${uiLabelMap.FormFieldTitle_currencyUomId}',datafield : 'currencyUomId',width : '15%'}
]"/>
<#assign columnlist = "
	{text : '${uiLabelMap.FormFieldTitle_invoiceItemSeqId}',datafield : 'invoiceItemSeqId',editable : false,width : '10%'},
	{text : '${uiLabelMap.FormFieldTitle_productId}',datafield : 'productId',editable : false,width : '10%'},
	{text : '${uiLabelMap.FormFieldTitle_description}',datafield : 'description',editable : false},
	{text : '${uiLabelMap.FormFieldTitle_total}',datafield : 'total',editable : false,cellsrenderer : function(row){
		var data = $('#jqxgridEditInvoice').jqxGrid('getrowdata',row);
		if(data) return '<span>' + formatcurrency(data.total,null)+ '</span>';
		return '<span></span>';
	}},
	{text : '${uiLabelMap.FormFieldTitle_paymentId}',datafield : 'paymentId',editable : true,cellsrenderer : function(row){
		var data = $('#jqxgridEditInvoice').jqxGrid('getrowdata',row);
		return '<span class=\"\">' + data.paymentId + '&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href=\"javascript:objInvoices.updatePayment()\"><i class=\"fa fa-edit\">${StringUtil.wrapString(uiLabelMap.CommonUpdate)}</i></a>' + '</span>';
	}},
	{text : '${uiLabelMap.FormFieldTitle_amountToApply}',datafield : 'amountToApply',editable : false,width  :'20%',cellsrenderer : function(row){
		var data = $('#jqxgridEditInvoice').jqxGrid('getrowdata',row);
		if(data) return '<span>' + formatcurrency(data.amountToApply,null)+ '</span>';
		return '<span></span>';
	}},
	{text : '',width  :'10%',cellsrenderer : function(row){
		var data = $(\"#jqxgridEditInvoice\").jqxGrid('getrowdata',row);
		if(data.paymentApplicationId != null){
			return '<span><a href=\"javascript:objInvoices.removeInvoiceApp('+ \"'\" + row + \"'\" +')\" ><i class=\"fa fa-trash\"></i>&nbsp;${uiLabelMap.CommonRemove}</a></span>';
		}	
		return '';
	}}
"/>
	<#assign appliedAmount = Static["org.ofbiz.base.util.UtilFormatOut"].formatCurrency(appliedAmount?if_exists, invoice.currencyUomId?if_exists, locale)>
	<#assign notAppliedAmount = Static["org.ofbiz.base.util.UtilFormatOut"].formatCurrency(notAppliedAmount?if_exists, invoice.currencyUomId?if_exists, locale)>
	 <#assign customTitleProperties="${uiLabelMap.AccountingPaymentsApplied} <span class='custom'>${appliedAmount?default('')}</span> ${uiLabelMap.AccountingOpenPayments} <span class='custom'>${notAppliedAmount?default('')}</span> "/>	
<@jqGrid id="jqxgridEditInvoice" addrow="true"  customTitleProperties=customTitleProperties addType="popup" alternativeAddPopup="popupAddPayment"  offlinerefreshbutton="true" dataField=dataField columnlist=columnlist editable="false" filtersimplemode="true" filterable="false" showtoolbar="true" clearfilteringbutton="true" 
	url="jqxGeneralServicer?sname=jqGetListApplicationList&invoiceId=${parameters.invoiceId?if_exists}"
	updateUrl="jqxGeneralServicer?sname=updatePaymentApplicationDef&jqaction=U" editColumns="invoiceId;paymentApplicationId;invoiceItemSeqId;billingAccountId;productId;description;paymentId;"
 />	
<script type="text/javascript">
	/*init object global*/
	var editInvoices = function(){
		var getInvoices = function(){
			
		}
		var setInvoices = function(){
			
		}
	}
	editInvoices.prototype.reloadGrid = function(){
		$('#jqxgridEditInvoice').jqxGrid('updatebounddata');
		$('#listPayment').jqxGrid('updatebounddata');
		objInvoices.checkAmountApplied();	
	}
	
	editInvoices.prototype.updatePayment = function(){
		var rowindex = $('#jqxgridEditInvoice').jqxGrid('getselectedrowindex');
		var data = $('#jqxgridEditInvoice').jqxGrid('getrowdata',rowindex);
		$('#dropdownPayment').jqxDropDownButton('val',data.paymentId ? data.paymentId : '');
		$('#amountToApply').jqxNumberInput('val',data.amountToApply ? data.amountToApply : 0);
		$('#renderAmount').css('display','none');
		$('#updatePm').attr('disabled',false);
		$('#save').attr('disabled',true);
		$('#popupAddPayment').jqxWindow('open');
	}
	
	editInvoices.prototype.checkAmountApplied = function(){
		$.ajax({
			url : 'getStatusAmountAppl',
			data : {
				invoiceId : '${parameters.invoiceId?if_exists}'
			},
			type  :'POST',
			async : false,
			cache : false,
			success : function(response){
				var titleProperty ;
				if(response.amountNotApplied && response.amountNotApplied == 0 ){
					$('#listPayment').css('display','none');
				}else $('#listPayment').css('display','block');
				titleProperty = "${StringUtil.wrapString(uiLabelMap.AccountingPaymentsApplied)} <span class='custom'>" +  formatcurrency(response.amountApplied,null)  + "</span> ${StringUtil.wrapString(uiLabelMap.AccountingOpenPayments)}  <span class='custom'>" + formatcurrency(response.amountNotApplied,null) + "</span>";
				$($('#toolbarcontainerjqxgridEditInvoice').children()[0]).html(titleProperty);
			},
			error : function(){}
		})
	}
	
	editInvoices.prototype.removeInvoiceApp = function(row){
		var data = $('#jqxgridEditInvoice').jqxGrid('getrowdata',row);	
		if(data){
			$.ajax({
				url : 'accApremoveInvoiceApplicationJS',
				datatype : 'json',
				type : 'POST',
				async : false,
				cache : false,
				data : {
					paymentApplicationId : data.paymentApplicationId,
					invoiceId : data.invoiceId
				},
				success : function(response){
						if(response._EVENT_MESSAGE_){
							GridUtils.renderMessage('jqxgridEditInvoice',response._EVENT_MESSAGE_,{
									autoClose : true,
									template : 'success',
									appendContainer : "#containerjqxgridEditInvoice",
									opacity : 0.9,
									icon : {
										width : 25,
										height : 25,
										url : '/aceadmin/assets/images/info.jpg'
									}
							});
							objInvoices.reloadGrid();
						}else if(response._EVENT_MESSAGE_LIST || response._ERROR_MESSAGE_){
							GridUtils.renderMessage('jqxgridEditInvoice',response._EVENT_MESSAGE_LIST ? response._EVENT_MESSAGE_LIST : response._ERROR_MESSAGE_ ,
									 {
										autoClose : true,
										template : 'error',
										appendContainer : "#containerjqxgridEditInvoice" ,
										opacity : 0.9
									 }
							);
						}
				},
				error : function(){
				}
			});
		}
	} 
	var objInvoices = new editInvoices();
</script>	
<#include "component://delys/webapp/delys/accounting/formJqxAccounting/ListPaymentsNotApplied.ftl"/>
<#include "component://delys/webapp/delys/accounting/formJqxAccounting/AddPayment.ftl"/>
