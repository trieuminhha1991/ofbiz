<!-- <a class=" btn btn-primary btn-small  applInvQuick"><i  class="icon-hand-right" ></i>&nbsp;${StringUtil.wrapString(uiLabelMap.BACCInvApplQuick)}</a> -->

<div id="applInvQuick" class="hide">
	<div>${StringUtil.wrapString(uiLabelMap.BACCInvApplQuick)}</div>
	<div id="invoiceGridAdd"></div>
</div>
<#assign datafieldPaymenInv = "[{ name: 'paymentApplicationId', type: 'string' },
						     	{ name: 'paymentId', type: 'string' },
						        { name: 'invoiceId', type: 'string' },
						        { name: 'invoiceItemSeqId', type: 'string' },
						        { name: 'partyFromName', type: 'string'},
					 			{ name: 'partyName', type: 'string'},
					 			{ name: 'invoiceDate', type: 'date'},
					 			{ name: 'newStatusId', type: 'string'},
						        { name: 'amountApplied', type: 'number' },
						        { name: 'totalAmount', type: 'number' },
						        { name: 'currencyUomId', type: 'string' },
								]"/>
								
							
<script type="text/javascript">
<#if businessType == "AP">
	<#assign fullNameField = "partyFromName"/>
	<#assign fullNameLabel = uiLabelMap.BACCInvoiceFromParty/>
<#elseif businessType == "AR">
	<#assign fullNameField = "partyName"/>
	<#assign fullNameLabel = uiLabelMap.BACCInvoiceToParty />
<#else>
	<#assign fullNameField = ""/>
	<#assign fullNameLabel = ""/>
</#if>
<#assign columnPaymentInv = "{text: '${uiLabelMap.BACCInvoiceId}', datafield: 'invoiceId', width: '13%',
								cellsrenderer: function(row, column, value){
									var data = $('#jqxgridPaymentInv').jqxGrid('getrowdata', row);
									if(data != undefined && data){
										if('${businessType}' == 'AP'){
											return '<span><a href=ViewAPInvoice?invoiceId=' + data.invoiceId + ' style=\"color: blue !important;\">' + value + '</a></span>';
										}else{
											return '<span><a href=ViewARInvoice?invoiceId=' + data.invoiceId + ' style=\"color: blue !important;\">' + value + '</a></span>';
										}
									}
								},
							 },
							 {text: '${StringUtil.wrapString(fullNameLabel)}', datafield: '${fullNameField}', width: '21%'},
							 {text: '${StringUtil.wrapString(uiLabelMap.BACCInvoiceDate)}', datafield: 'invoiceDate', width: '19%', 
								 cellsformat:'dd/MM/yyyy HH:mm:ss', columntype: 'datetimeinput', filtertype: 'range'},
							 { text: '${uiLabelMap.CommonStatus}', dataField: 'newStatusId', width: '12%', filtertype: 'checkedlist',
			                	  cellsrenderer: function(row, column, value){
			                		  for(var i = 0; i < invStatusArr.length; i++){
											if(value == invStatusArr[i].statusId){
												return '<span title=' + value + '>' + invStatusArr[i].description + '</span>';
											}
										}
										return '<span>' + value + '</span>';
			                	  },
			                	  createfilterwidget: function (column, columnElement, widget) {
						   				var uniqueRecords2 = [] ;
						   				if(invStatusArr && invStatusArr.length > 0 ){
						   					var filterBoxAdapter2 = new $.jqx.dataAdapter(invStatusArr,
									                {
									                    autoBind: true
									                });
							                uniqueRecords2 = filterBoxAdapter2.records;
						   				}
						   				widget.jqxDropDownList({ filterable:true,source: uniqueRecords2, displayMember: 'description', valueMember : 'statusId'});			   				
		   						},
			                  }, 
							{text: '${uiLabelMap.BACCAmountApplied}', datafield: 'amountApplied', columntype: 'numberinput', width: '17%',
								 cellsrenderer: function(row, column, value){
										if(typeof(value) == 'number'){
											var data = $('#jqxgridPaymentInv').jqxGrid('getrowdata', row);
											return '<span style=\"text-align: right\">' + formatcurrency(value, data.currencyUomId) + '<span>';
										}
									}	 
							 },
							 {text: '${uiLabelMap.BACCInvoiceTotal}', datafield: 'totalAmount', columntype: 'numberinput', width: '18%',
								 cellsrenderer: function(row, column, value){
										if(typeof(value) == 'number'){
											var data = $('#jqxgridPaymentInv').jqxGrid('getrowdata', row);
											return '<span style=\"text-align: right\">' + formatcurrency(value, data.currencyUomId) + '<span>';
										}
									}	 
							 }
							 "/>
</script>
<#assign customTitleProperties = StringUtil.wrapString(uiLabelMap.BACCListInvoicesOfPayments)/>
<#if payment.statusId == "PMNT_CONFIRMED">
    <@jqGrid filtersimplemode="false" filterable="false" showtoolbar="true" dataField=datafieldPaymenInv columnlist=columnPaymentInv
    clearfilteringbutton="false"  editable="false" selectionmode="singlerow"
    addrow="false"
    showlist="false" sortable="true" id="jqxgridPaymentInv"
    customTitleProperties=customTitleProperties
    url="jqxGeneralServicer?sname=JQGetListInvoiceOfPayment&paymentId=${parameters.paymentId}" jqGridMinimumLibEnable="false"
    customcontrol2="fa fa-file-excel-o open-sans@${StringUtil.wrapString(uiLabelMap.BSExportExcel)}@javascript:void(0);exportExcel()"/>
<#else>
    <@jqGrid filtersimplemode="false" filterable="false" showtoolbar="true" dataField=datafieldPaymenInv columnlist=columnPaymentInv
    clearfilteringbutton="false"  editable="false" deleterow="true" selectionmode="singlerow"
    addrow="false"
    showlist="false" sortable="true" id="jqxgridPaymentInv"
    customTitleProperties=customTitleProperties
    updateUrl="" editColumns=""
    removeUrl="jqxGeneralServicer?jqaction=D&sname=removePaymentApplication" deleteColumn="paymentApplicationId"
    url="jqxGeneralServicer?sname=JQGetListInvoiceOfPayment&paymentId=${parameters.paymentId}" jqGridMinimumLibEnable="false"
    customcontrol1="icon-hand-right open-sans@${uiLabelMap.BACCAddInvoiceForPayment}@javascript: void(0);@addInvToPaymentObj.openWindow()"
    customcontrol2="fa fa-file-excel-o open-sans@${StringUtil.wrapString(uiLabelMap.BSExportExcel)}@javascript:void(0);exportExcel()"/>
</#if>
<!-- removeUrl = removePaymentApplication -->

		
<div class="span12"></div>

<div id="addInvToPaymentWindow" class="hide">
	<div>${uiLabelMap.BACCCreateNew}</div>
	<div class='form-window-container' style="position: relative;">
		<div class='form-window-content'>
			<div class='row-fluid margin-bottom10'>
				<div class="span5 text-algin-right">
					<label class="asterisk">${StringUtil.wrapString(uiLabelMap.BACCInvoiceId)}</label>
				</div>
				<div class="span7">
					<div id="dropDownInvGridBtn">
						<div style="border-color: transparent;" id="jqxGridInvoice"></div>
					</div>
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class="span5 text-algin-right">
					<label class="">${StringUtil.wrapString(uiLabelMap.BACCInvoiceTotal)}</label>
				</div>
				<div class="span7">
					<div id="invoiceTotalAmount"></div>
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class="span5 text-algin-right">
					<label class="">${StringUtil.wrapString(uiLabelMap.BACCOpenPayments)}</label>
				</div>
				<div class="span7">
					<div id="invoiceNotApplydAmount"></div>
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class="span5 text-algin-right">
					<label class="asterisk">${StringUtil.wrapString(uiLabelMap.BACCAmountApply)}</label>
				</div>
				<div class="span7">
					<div id="invoiceAmountApply"></div>
				</div>
			</div>
		</div>
		<div class="form-action">
			<button type="button" class='btn btn-danger form-action-button pull-right'id="cancelAddInvToPayment">
				<i class='icon-remove'></i>&nbsp;${uiLabelMap.CommonCancel}</button>	
			<button type="button" class='btn btn-success form-action-button pull-right'id="saveAndContinueAddInvToPayment">
				<i class='fa-plus'></i>&nbsp;${uiLabelMap.SaveAndContinue}</button>		
			<button type="button" class='btn btn-primary form-action-button pull-right' id="saveAddInvToPayment">
				<i class='fa fa-check'></i>&nbsp;${uiLabelMap.CommonSave}</button>
		</div>
	</div>	
</div>
<script type="text/javascript">
var exportExcel = function(){
	var winName='ExportExcel';
	var winURL = 'exportListPaymentApplExcel';
	var form = document.createElement("form");
	form.setAttribute("method", "post");
	form.setAttribute("action", winURL);
	form.setAttribute("target", "_blank");
	var params = {paymentId: globalVar.paymentId};
	for(var key in params){
		if (params.hasOwnProperty(key)) {
			var input = document.createElement('input');
			input.type = 'hidden';
			input.name = key;
			input.value = params[key];
			form.appendChild(input);
		}
	}
	document.body.appendChild(form);
	window.open(' ', winName);
	form.target = winName;
	form.submit();                 
	document.body.removeChild(form);
}
</script>
<script type="text/javascript" src="/accresources/js/payment/AddInvoiceToPaymet.js?v=001"></script>