<style>
	.customBt{
		width  : 130px;
		height : 36px;
	}
</style>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxnumberinput.js"></script>
<#assign dataField = "[
				{name : 'paymentId',type : 'string'},
				{name : 'amount',type : 'number'},
				{name : 'effectiveDate',type : 'date',other : 'Timestamp'},
				{name : 'amountApplied',type : 'number'},
				{name : 'amountToApply',type : 'number'}
		]"/>
		
<#assign columnlist = "
	{text : '${uiLabelMap.FormFieldTitle_paymentId}',datafield : 'paymentId',editable: false,cellsrenderer : function(row){
		var data = $(\"#listPayment\").jqxGrid('getrowdata',row);
		return '<span><a href=\"accAppaymentOverview?paymentId='+ data.paymentId +'\">'+ data.paymentId+'</a></span>';		
	}},
	{text : '${uiLabelMap.FormFieldTitle_effectiveDate}',datafield : 'effectiveDate',editable: false,cellsformat : 'dd/MM/yy hh:mm:ss'},
	{text : '${uiLabelMap.FormFieldTitle_amount}',datafield : 'amount',editable: false,cellsrenderer : function(row){
		var data = $('#listPayment').jqxGrid('getrowdata',row);
		if(data) return '<span>' + formatcurrency(data.amount,null)+ '</span>';
		return '<span></span>';
	}},
	{text : '${uiLabelMap.FormFieldTitle_amountApplied}',datafield : 'amountApplied',width : '20%',editable: false,cellsrenderer : function(row){
		var data = $('#listPayment').jqxGrid('getrowdata',row);
		if(data) return '<span>' + formatcurrency(data.amountApplied,null)+ '</span>';
		return '<span></span>';
	}},
	{text : '${uiLabelMap.FormFieldTitle_amountToApply}',datafield : 'amountToApply',columntype : 'numberinput',editable: true,createeditor : function(row,column,editor){
		var data = $(\"#listPayment\").jqxGrid('getrowdata',row);
		editor.jqxNumberInput({decimalDigits : 0,digits : 30,spinButtons : true});	
		editor.jqxNumberInput('val',data.amountToApply ? formatcurrency(data.amountToApply,null) : 0);
	},cellsrenderer : function(row){
		var data = $('#listPayment').jqxGrid('getrowdata',row);
		if(data) return '<span>' + formatcurrency(data.amountToApply,null)+ '</span>';
		return '<span></span>';
	}}
"/>			
	 <#assign partyfrom = dispatcher.runSync("getPartyNameForDate", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId",invoice.partyIdFrom, "compareDate", invoice.invoiceDate, "userLogin", userLogin))/>
	 <#assign partyto = dispatcher.runSync("getPartyNameForDate", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId",invoice.partyId, "compareDate", invoice.invoiceDate, "userLogin", userLogin))/>
	<#assign customPropertyTitleListPayment = "${uiLabelMap.AccountingListPaymentsNotYetApplied} <span class='custom'>[${partyfrom.groupName?if_exists}]</span> ${uiLabelMap.AccountingPaymentSentForm} <span class='custom'>[${partyto.groupName?if_exists}]</span>" />
<@jqGrid id="listPayment" customTitleProperties=customPropertyTitleListPayment updateoffline="true" functionAfterUpdate="objInvoices.reloadGrid" columnlist=columnlist dataField=dataField filtersimplemode="true" filterable="false" editable="true" addrefresh="true"  clearfilteringbutton="true"
	url="jqxGeneralServicer?sname=jqGetListPaymentNotApplied&invoiceId=${invoiceId?if_exists}"
	updateUrl="jqxGeneralServicer?sname=updatePaymentApplicationDefCustom&jqaction=U"
	editColumns="paymentId;amount(java.math.BigDecimal);invoiceId[${parameters.invoiceId}];effectiveDate(java.sql.Timestamp);amountApplied(java.math.BigDecimal);amountToApply(java.math.BigDecimal)"	
/>
    

