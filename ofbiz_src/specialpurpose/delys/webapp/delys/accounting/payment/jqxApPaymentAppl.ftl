<div class="tab-pane" id="payment-appl">
	<button class="btn btn-primary btn-small" id="apply">${uiLabelMap.Apply}</button>
	<button class="btn btn-success btn-small" id="posible">${uiLabelMap.Posible}</button>
	<h4 style="color: #4383b4;" id="payment-appl-header">${uiLabelMap.CommonAmount} ${uiLabelMap.CommonTotal} ${Static["org.ofbiz.base.util.UtilFormatOut"].formatCurrency(payment.amount, payment.currencyUomId, locale)}  ${uiLabelMap.AccountingAmountNotApplied} ${Static["org.ofbiz.base.util.UtilFormatOut"].formatCurrency(notAppliedAmount, payment.currencyUomId, locale)}</h4>
	<div id="jqxgridPaymentInv"></div></hr>
	<div id="jqxgridPaymentPay"></div></hr>
	<div id="jqxgridPaymentBil"></div></hr>
	<div id="jqxgridPaymentTax"></div>
</div>
<div id="wdwPosibleApplication" style="display: none;">
	<div id="wdwHeader">
		<span>
		   ${uiLabelMap.CommonFrom} ${partyNameViewTo.groupName?if_exists}${partyNameViewTo.lastName?if_exists},${partyNameViewTo.firstName?if_exists} ${partyNameViewTo.middleName?if_exists}[${payment.partyIdTo?if_exists}] ${uiLabelMap.CommonTo?if_exists} ${partyNameViewFrom.groupName?if_exists}${partyNameViewFrom.lastName?if_exists},${partyNameViewFrom.firstName?if_exists} ${partyNameViewFrom.middleName?if_exists} [${payment.partyIdFrom?if_exists}]
		</span>
	</div>
	<div id="wdwContentNew">
		<div class="basic-form form-horizontal" style="margin-top: 10px">
			<form name="formNew" id="formNew">
				<div class="row-fluid">
					<div id="applyJqxNotification"></div>
					<div id="applyContainer" style="width : 100%"></div>
				</div>
				<div class="row-fluid" >
					<div id="jqxgridPosibleInv"></div>
				</div>
				<div class="row-fluid">
					<div id="jqxgridPosiblePay"></div>
				</div>
			</form>
		</div>
	</div>
</div>
<div id="wdwNewPayAppl" style="display: none;">
	<div id="wdwHeader">
		<span>
			${uiLabelMap.AccountingApplyPaymentoTo}
		</span>
	</div>
	<div id="wdwContentNew">
		<div class="basic-form form-horizontal" style="margin-top: 10px">
			<form name="formNew" id="formNew">	
				<div class="row-fluid" >
					<div class="span12">
						<div class="control-group no-left-margin">
							<label class="control-label">${uiLabelMap.FormFieldTitle_invoiceId}:</label>  
							<div class="controls">
								<div id="applInvoiceId">
									<div id="jqxgridApplInvoiceId"></div>
								</div>
							</div>
						</div>
						<div class="control-group no-left-margin">
							<label class="control-label">${uiLabelMap.FormFieldTitle_toPaymentId}:</label>  
							<div class="controls">
								<div id="applToPaymentId">
									<div id="jqxgridApplToPaymentId"></div>
								</div>
							</div>
						</div>
						<div class="control-group no-left-margin">
							<label class="control-label">${uiLabelMap.FormFieldTitle_billingAccountId}:</label>  
							<div class="controls">
								<div id="applBillingAccountId">
									<div id="jqxgridApplBillingAccountId"></div>
								</div>
							</div>
						</div>
						<div class="control-group no-left-margin">
							<label class="control-label">${uiLabelMap.FormFieldTitle_taxAuthGeoId}:</label>  
							<div class="controls">
								<div id="applTaxAuthGeoId">
									<div id="jqxgridApplTaxAuthGeoId"></div>
								</div>
							</div>
						</div>
						<div class="control-group no-left-margin">
							<label class="control-label">${uiLabelMap.FormFieldTitle_amountToApply}:</label>  
							<div class="controls">
								<input id="applAmountToApply" ></input>
							</div>
						</div>
					</div>
				</div>
			</form>
		</div>
		<div class="form-action">
			<div class='row-fluid'>
				<div class="span12 margin-top10">
					<button id="alterCancel" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.Cancel}</button>
					<button id="alterSave" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.Save}</button>
				</div>
			</div>
		</div>
	</div>
</div>
<script>
	var wgupdatesuccess = "${StringUtil.wrapString(uiLabelMap.wgupdatesuccess)}";
	//Set up data for posible invoice
	var getPosibleInvData = function(){
		var posibleInvData = new Array();
		$.ajax({
			  url: "getInvoices",
			  type: "POST",
			  data: {paymentId: '${parameters.paymentId}'},
			  async: false,
			  success: function(res) {
				  if(!res._ERROR_MESSAGE_LIST_ && !res._ERROR_MESSAGE_){
					  var listInvoices = res.listInvoices;
					  for(var i = 0; i < listInvoices.length; i++){
						  var row = {};
						  row['invoiceId'] = listInvoices[i].invoiceId;
						  row['currencyUomId'] = listInvoices[i].currencyUomId;
						  row['description'] = listInvoices[i].description;
						  var invoiceDateObj = listInvoices[i].invoiceDate;
						  var invoiceDate = new Date(invoiceDateObj.time);
						  row['invoiceDate'] = invoiceDate;
						  row['amount'] = listInvoices[i].amount;
						  row['amountApplied'] = listInvoices[i].amountApplied;
						  row['amountToApply'] = listInvoices[i].amountToApply;
						  posibleInvData[i] = row;
					  }
				  }
			  }
	  	})
	    return posibleInvData;
	}
	
	//Set up data for posible payment
	var getPosiblePayData = function(){
		var posiblePayData = new Array();
		$.ajax({
			  url: "getPayments",
			  type: "POST",
			  data: {paymentId: '${parameters.paymentId}'},
			  async: false,
			  success: function(res) {
				  if(!res._ERROR_MESSAGE_LIST_ && !res._ERROR_MESSAGE_){
					  var payments = res.payments;
					  for(var i = 0; i < payments.length; i++){
						  var row = {};
						  row['paymentId'] = '${parameters.paymentId}';
						  row['toPaymentId'] = payments[i].toPaymentId;
						  row['currencyUomId'] = payments[i].currencyUomId;
						  var effectiveDateObj = payments[i].effectiveDate;
						  var effectiveDate = new Date(effectiveDateObj.time);
						  row['effectiveDate'] = effectiveDate;
						  row['amount'] = payments[i].amount;
						  row['amountApplied'] = payments[i].amountApplied;
						  row['amountToApply'] = payments[i].amountToApply;
						  posiblePayData[i] = row;
					  }
				  }
			  }
	  	})
	    return posiblePayData;
	}
	
	var getPaymentPayData = function(){
		var paymentPayData = new Array();
		$.ajax({
			  url: "getPayApplPay",
			  type: "POST",
			  data: {paymentId: '${parameters.paymentId}'},
			  async: false,
			  success: function(res) {
				  if(!res._ERROR_MESSAGE_LIST_ && !res._ERROR_MESSAGE_){
					  var paymentApplicationsPay = res.paymentApplicationsPay;
					  for(var i = 0; i < paymentApplicationsPay.length; i++){
						  var row = {};
						  row['paymentApplicationId'] = paymentApplicationsPay[i].paymentApplicationId;
						  row['paymentId'] = paymentApplicationsPay[i].paymentId;
						  row['toPaymentId'] = paymentApplicationsPay[i].toPaymentId;
						  row['amountApplied'] = paymentApplicationsPay[i].amountApplied;
						  row['currencyUomId'] = paymentApplicationsPay[i].currencyUomId;
						  paymentPayData[i] = row;
					  }
				  }
			  }
	  	})
	  	return paymentPayData;
	}
	
	var getPaymentBilData = function(){
		var paymentBilData = new Array();
		$.ajax({
			  url: "getPayApplBil",
			  type: "POST",
			  data: {paymentId: '${parameters.paymentId}'},
			  async: false,
			  success: function(res) {
				  if(!res._ERROR_MESSAGE_LIST_ && !res._ERROR_MESSAGE_){
					  var paymentApplicationsBil = res.paymentApplicationsBil;
					  for(var i = 0; i < paymentApplicationsBil.length; i++){
						  var row = {};
						  row['paymentApplicationId'] = paymentApplicationsBil[i].paymentApplicationId;
						  row['paymentId'] = paymentApplicationsBil[i].paymentId;
						  row['billingAccountId'] = paymentApplicationsBil[i].billingAccountId;
						  row['amountApplied'] = paymentApplicationsBil[i].amountApplied;
						  row['currencyUomId'] = paymentApplicationsBil[i].currencyUomId;
						  paymentBilData[i] = row;
					  }
				  }
			  }
	  	})
	  	return paymentBilData;
	}
	
	var getPaymentTaxData = function(){
		var paymentTaxData = new Array();
		$.ajax({
			  url: "getPayApplTax",
			  type: "POST",
			  data: {paymentId: '${parameters.paymentId}'},
			  async: false,
			  success: function(res) {
				  if(!res._ERROR_MESSAGE_LIST_ && !res._ERROR_MESSAGE_){
					  var paymentApplicationsTax = res.paymentApplicationsTax;
					  for(var i = 0; i < paymentApplicationsTax.length; i++){
						  var row = {};
						  row['paymentApplicationId'] = paymentApplicationsTax[i].paymentApplicationId;
						  row['paymentId'] = paymentApplicationsTax[i].paymentId;
						  row['taxAuthGeoId'] = paymentApplicationsTax[i].taxAuthGeoId;
						  row['amountApplied'] = paymentApplicationsTax[i].amountApplied;
						  row['currencyUomId'] = paymentApplicationsTax[i].currencyUomId;
						  paymentTaxData[i] = row;
					  }
				  }
			  }
	  	})
	  	return paymentTaxData;
	}
	
	//Set up data for jqxgridPaymentInv
	var getPaymentInvData = function(){
		var payApplInvData = new Array();
		$.ajax({
			  url: "getPayApplInv",
			  type: "POST",
			  data: {paymentId: '${parameters.paymentId}'},
			  async: false,
			  success: function(res) {
				  if(!res._ERROR_MESSAGE_LIST_ && !res._ERROR_MESSAGE_){
					  var paymentApplicationsInv = res.paymentApplicationsInv;
					  for(var i = 0; i < paymentApplicationsInv.length; i++){
					  	var row = {};
					  	row['paymentApplicationId'] = paymentApplicationsInv[i].paymentApplicationId;
						row['paymentId'] = paymentApplicationsInv[i].paymentId;
						row['invoiceId'] = paymentApplicationsInv[i].invoiceId;
						row['invoiceItemSeqId'] = paymentApplicationsInv[i].invoiceItemSeqId;
						row['amountApplied'] = paymentApplicationsInv[i].amountApplied;
						row['currencyUomId'] = paymentApplicationsInv[i].currencyUomId;
						payApplInvData[i] = row;
					  }
				  }
			  }
		  	})
		  return payApplInvData;
	}
	
	//update inv grid title 
	var updateInvGridTitle = function(){
		$.ajax({
			  url: "getAppliedAmountAndNotAppliedAmount",
			  type: "POST",
			  data: {paymentId: '${parameters.paymentId}'},
			  async: false,
			  success: function(res) {
				  if(!res._ERROR_MESSAGE_LIST_ && !res._ERROR_MESSAGE_){
					  	$("#toolbarcontainereditPayment h4").text('${StringUtil.wrapString(uiLabelMap.AccountingPaymentsApplied)} ' +  res.appliedAmount + ' ${StringUtil.wrapString(payment.currencyUomId)} ${StringUtil.wrapString(uiLabelMap.AccountingOpenPayments)} ' + res.notAppliedAmount + ' ${StringUtil.wrapString(payment.currencyUomId)}');
					  	$("#payment-appl-header").text('${StringUtil.wrapString(uiLabelMap.CommonAmount)} ${StringUtil.wrapString(uiLabelMap.CommonTotal)} ${payment.amount} ${StringUtil.wrapString(payment.currencyUomId)} ${StringUtil.wrapString(uiLabelMap.AccountingAmountNotApplied)} ' + res.notAppliedAmount + ' ${StringUtil.wrapString(payment.currencyUomId)}');
				  }
			  }
	  	})
	}
	
	//Create Payment Application
	$('#wdwNewPayAppl').jqxWindow({showCollapseButton: false, autoOpen: false,width : 700,height : 350, isModal: true, modalZIndex: 10000,theme:this.theme, collapsed:false, cancelButton: '#alterCancel',
	    initContent: function () {
	    	//Create applAmountToApply
	    	$("#applAmountToApply").jqxInput({width: 195});
	    	
	    	//Create applTaxAuthGeoId
	    	var sourceGeo =
	    		{	
	    			datafields:
	    				[
	    				 { name: 'geoId', type: 'string' },
	    				 { name: 'geoTypeId', type: 'string' },
	    				 { name: 'geoName', type: 'string' },
	    				 { name: 'geoCode', type: 'string' },
	    				 { name: 'geoSecCode', type: 'string' },
	    				 { name: 'abbreviation', type: 'string'},
	    				 { name: 'wellKnownText', type: 'string'},
	    				],
	    			cache: false,
	    			root: 'results',
	    			datatype: "json",
	    			updaterow: function (rowid, rowdata) {
	    				// synchronize with the server - send update command   
	    			},
	    			beforeprocessing: function (data) {
	    				sourceGeo.totalrecords = data.TotalRows;
	    			},
	    			filter: function () {
	    				// update the grid and send a request to the server.
	    				$("#jqxgridApplTaxAuthGeoId").jqxGrid('updatebounddata');
	    			},
	    			pager: function (pagenum, pagesize, oldpagenum) {
	    				// callback called when a page or page size is changed.
	    			},
	    			sort: function () {
	    				$("#jqxgridApplTaxAuthGeoId").jqxGrid('updatebounddata');
	    			},
	    			sortcolumn: 'geoId',
	    			sortdirection: 'asc',
	    			type: 'POST',
	    			data: {
	    				noConditionFind: 'Y',
	    				conditionsFind: 'N',
	    			},
	    			pagesize:10,
	    			contentType: 'application/x-www-form-urlencoded',
	    			url: 'jqxGeneralServicer?sname=JQGetListGeos',
	    		};
	    	var dataAdapterGeo = new $.jqx.dataAdapter(sourceGeo,{
	    		autoBind: true,
	        	formatData: function (data) {
	        		if (data.filterscount) {
	                    var filterListFields = "";
	                    for (var i = 0; i < data.filterscount; i++) {
	                        var filterValue = data["filtervalue" + i];
	                        var filterCondition = data["filtercondition" + i];
	                        var filterDataField = data["filterdatafield" + i];
	                        var filterOperator = data["filteroperator" + i];
	                        filterListFields += "|OLBIUS|" + filterDataField;
	                        filterListFields += "|SUIBLO|" + filterValue;
	                        filterListFields += "|SUIBLO|" + filterCondition;
	                        filterListFields += "|SUIBLO|" + filterOperator;
	                    }
	                    data.filterListFields = filterListFields;
	                }else{
	                	data.filterListFields = null;
	                }
	                return data;
	            },
	            loadError: function (xhr, status, error) {
	                alert(error);
	            },
	            downloadComplete: function (data, status, xhr) {
                    if (!sourceGeo.totalRecords) {
                    	sourceGeo.totalRecords = parseInt(data['odata.count']);
                    }
	            }
	        });
		$("#applTaxAuthGeoId").jqxDropDownButton({height: 25});
			setTimeout(function(){
	    	$("#jqxgridApplTaxAuthGeoId").jqxGrid({
	    		source: dataAdapterGeo,
	    		autoshowloadelement : false,
	    		filterable: true,
				autoshowloadelement : false,
	    		width : 500,
	    		showfilterrow: true,
	    		virtualmode: true, 
	    		sortable:true,
	    		theme: theme,
	    		pagesize : 5,
	    		editable: false,
	    		autoheight:true,
	    		pageable: true,
	    		rendergridrows: function(obj)
	    		{
	    			return obj.data;
	    		},
	    		columns: [
	    		          { text: '${uiLabelMap.CommonGeoId}', datafield: 'geoId', width: 200},
	    				  { text: '${uiLabelMap.CommonGeoTypeId}', datafield: 'geoTypeId', width: 200},
	    				  { text: '${uiLabelMap.CommonGeoName}', datafield: 'geoName', width: 200},
	    				  { text: '${uiLabelMap.CommonGeoCode}', datafield: 'geoCode', width: 200},
	    				  { text: '${uiLabelMap.CommonGeoSecCode}', datafield: 'geoSecCode', width: 200},
	    				  { text: '${uiLabelMap.CommonGeoAbbr}', datafield: 'abbreviation', width: 200},
	    				  { text: '${uiLabelMap.CommonGeoWellKnownText}', datafield: 'wellKnownText'}
	    		         ]
			});					


			},500)
	    	
	    	$("#jqxgridApplTaxAuthGeoId").on('rowselect', function (event) {
	    		var args = event.args;
	    		var row = $("#jqxgridApplTaxAuthGeoId").jqxGrid('getrowdata', args.rowindex);
	    		var dropDownContent = '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+ row['geoId'] +'</div>';
	    		$("#applTaxAuthGeoId").jqxDropDownButton('close');
	    		$('#applTaxAuthGeoId').jqxDropDownButton('setContent', dropDownContent);
	    	});
	    	
	    	//Create To Payment
	    	var sourcePay =
	    		{	
	    			datafields:
	    				[
	    				 { name: 'paymentId', type: 'string' },
	    				 { name: 'partyIdFrom', type: 'string' },
	    				 { name: 'partyIdTo', type: 'string' },
	    				 { name: 'effectiveDate', type: 'string' },
	    				 { name: 'amount', type: 'string' },
	    				 { name: 'currencyUomId', type: 'string'}
	    				],
	    			cache: false,
	    			root: 'results',
	    			datatype: "json",
	    			updaterow: function (rowid, rowdata) {
	    				// synchronize with the server - send update command   
	    			},
	    			beforeprocessing: function (data) {
	    				sourcePay.totalrecords = data.TotalRows;
	    			},
	    			filter: function () {
	    				// update the grid and send a request to the server.
	    				$("#jqxgridApplToPaymentId").jqxGrid('updatebounddata');
	    			},
	    			pager: function (pagenum, pagesize, oldpagenum) {
	    				// callback called when a page or page size is changed.
	    			},
	    			sort: function () {
	    				$("#jqxgridApplToPaymentId").jqxGrid('updatebounddata');
	    			},
	    			sortcolumn: 'paymentId',
	    			sortdirection: 'asc',
	    			type: 'POST',
	    			data: {
	    				noConditionFind: 'Y',
	    				conditionsFind: 'N',
	    			},
	    			pagesize:5,
	    			contentType: 'application/x-www-form-urlencoded',
	    			url: 'jqxGeneralServicer?sname=getListPayment',
	    		};
	    	var dataAdapterPay = new $.jqx.dataAdapter(sourcePay,{
	        	autoBind: true,
	        	formatData: function (data) {
	        		if (data.filterscount) {
	                    var filterListFields = "";
	                    for (var i = 0; i < data.filterscount; i++) {
	                        var filterValue = data["filtervalue" + i];
	                        var filterCondition = data["filtercondition" + i];
	                        var filterDataField = data["filterdatafield" + i];
	                        var filterOperator = data["filteroperator" + i];
	                        filterListFields += "|OLBIUS|" + filterDataField;
	                        filterListFields += "|SUIBLO|" + filterValue;
	                        filterListFields += "|SUIBLO|" + filterCondition;
	                        filterListFields += "|SUIBLO|" + filterOperator;
	                    }
	                    data.filterListFields = filterListFields;
	                }else{
	                	data.filterListFields = null;
	                }
	                return data;
	            },
	            loadError: function (xhr, status, error) {
	                alert(error);
	            },
	            downloadComplete: function (data, status, xhr) {
                    if (!sourcePay.totalRecords) {
                    	sourcePay.totalRecords = parseInt(data['odata.count']);
                    }
	            }
	        });
	    	$("#applToPaymentId").jqxDropDownButton({height: 25});
	    	$("#jqxgridApplToPaymentId").jqxGrid({
	    		source: dataAdapterPay,
	    		filterable: true,
	    		showfilterrow: true,
	    		virtualmode: true, 
	    		sortable:true,
	    		theme: theme,
	    		editable: false,
	    		autoheight:true,
	    		pageable: true,
	    		rendergridrows: function(obj)
	    		{
	    			return obj.data;
	    		},
	    		columns: [
	    		          { text: '${uiLabelMap.paymentId}', datafield: 'paymentId', width: 100},
	    				  { text: '${uiLabelMap.partyIdFrom}', datafield: 'partyIdFrom', width: 100},
	    				  { text: '${uiLabelMap.partyIdTo}', datafield: 'partyIdTo', width: 100},
	    				  { text: '${uiLabelMap.effectiveDate}', datafield: 'effectiveDate', width: 100},
	    				  { text: '${uiLabelMap.amount}', datafield: 'amount', width: 100},
	    				  { text: '${uiLabelMap.currencyUomId}', datafield: 'currencyUomId', width: 100}
	    		         ]
			});
	    	$("#jqxgridApplToPaymentId").on('rowselect', function (event) {
	    		var args = event.args;
	    		var row = $("#jqxgridApplToPaymentId").jqxGrid('getrowdata', args.rowindex);
	    		var dropDownContent = '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+ row['paymentId'] +'</div>';
	    		$("#applToPaymentId").jqxDropDownButton('close');
	    		$('#applToPaymentId').jqxDropDownButton('setContent', dropDownContent);
	    	});
	    	
	    	//Create BillingAccount
	    	var sourceBilAcc =
    		{	
    			datafields:
    				[
    				 { name: 'billingAccountId', type: 'string' },
    				 { name: 'description', type: 'string' },
    				 { name: 'externalAccountId', type: 'string' }
    				],
    			cache: false,
    			root: 'results',
    			datatype: "json",
    			updaterow: function (rowid, rowdata) {
    				// synchronize with the server - send update command   
    			},
    			beforeprocessing: function (data) {
    				sourceBilAcc.totalrecords = data.TotalRows;
    			},
    			filter: function () {
    				// update the grid and send a request to the server.
    				$("#jqxgridApplBillingAccountId").jqxGrid('updatebounddata');
    			},
    			pager: function (pagenum, pagesize, oldpagenum) {
    				// callback called when a page or page size is changed.
    			},
    			sort: function () {
    				$("#jqxgridApplBillingAccountId").jqxGrid('updatebounddata');
    			},
    			sortcolumn: 'billingAccountId',
    			sortdirection: 'asc',
    			type: 'POST',
    			data: {
    				noConditionFind: 'Y',
    				conditionsFind: 'N',
    			},
    			pagesize:5,
    			contentType: 'application/x-www-form-urlencoded',
    			url: 'jqxGeneralServicer?sname=JQGetListBillingAccount',
    		};
	    	var dataAdapterBilAcc = new $.jqx.dataAdapter(sourceBilAcc,{
	        	autoBind: true,
	        	formatData: function (data) {
	        		if (data.filterscount) {
	                    var filterListFields = "";
	                    for (var i = 0; i < data.filterscount; i++) {
	                        var filterValue = data["filtervalue" + i];
	                        var filterCondition = data["filtercondition" + i];
	                        var filterDataField = data["filterdatafield" + i];
	                        var filterOperator = data["filteroperator" + i];
	                        filterListFields += "|OLBIUS|" + filterDataField;
	                        filterListFields += "|SUIBLO|" + filterValue;
	                        filterListFields += "|SUIBLO|" + filterCondition;
	                        filterListFields += "|SUIBLO|" + filterOperator;
	                    }
	                    data.filterListFields = filterListFields;
	                }else{
	                	data.filterListFields = null;
	                }
	                return data;
	            },
	            loadError: function (xhr, status, error) {
	                alert(error);
	            },
	            downloadComplete: function (data, status, xhr) {
                    if (!sourceBilAcc.totalRecords) {
                    	sourceBilAcc.totalRecords = parseInt(data['odata.count']);
                    }
	            }
	        });
	    	$("#applBillingAccountId").jqxDropDownButton({height: 25});
	    	$("#jqxgridApplBillingAccountId").jqxGrid({
	    		source: dataAdapterBilAcc,
	    		filterable: true,
	    		showfilterrow: true,
	    		virtualmode: true, 
	    		sortable:true,
	    		theme: theme,
	    		editable: false,
	    		autoheight:true,
	    		pageable: true,
	    		rendergridrows: function(obj)
	    		{
	    			return obj.data;
	    		},
	    		columns: [
	    		          { text: '${uiLabelMap.billingAccountId}', datafield: 'billingAccountId', width: 150},
	    				  { text: '${uiLabelMap.description}', datafield: 'description'},
	    				  { text: '${uiLabelMap.externalAccountId}', datafield: 'externalAccountId', width: 150},
	    		         ]
	    			});
	    	$("#jqxgridApplBillingAccountId").on('rowselect', function (event) {
	    		var args = event.args;
	    		var row = $("#jqxgridApplBillingAccountId").jqxGrid('getrowdata', args.rowindex);
	    		var dropDownContent = '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+ row['billingAccountId'] +'</div>';
	    		$("#applBillingAccountId").jqxDropDownButton('close');
	    		$('#applBillingAccountId').jqxDropDownButton('setContent', dropDownContent);
	    	});
	    	
	    	//Create Invoice
	    	var sourceInvoice = { 
				datafields: [
			      { name: 'invoiceId', type: 'string' },
			      { name: 'invoiceTypeId', type: 'string' },
			      { name: 'statusId', type: 'string' },
			      { name: 'description', type: 'string' },
			      { name: 'partyIdFrom', type: 'string' },
			      { name: 'partyId', type: 'string' },
			      { name: 'amount', type: 'number' },
			      { name: 'currencyUomId', type: 'string' }
			    ],
				cache: false,
				root: 'results',
				datatype: 'json',
				
				beforeprocessing: function (data) {
					sourceInvoice.totalrecords = data.TotalRows;
				},
				filter: function () {
	   				// update the grid and send a request to the server.
	   				$('#jqxgridApplInvoiceId').jqxGrid('updatebounddata');
				},
				sort: function () {
	  				$('#jqxgridApplInvoiceId').jqxGrid('updatebounddata');
				},
				sortcolumn: 'invoiceId',
				sortdirection: 'asc',
				type: 'POST',
				data: {
					noConditionFind: 'Y',
					conditionsFind: 'N',
				},
				pagesize:5,
				contentType: 'application/x-www-form-urlencoded',
				url: 'jqxGeneralServicer?sname=getListInvoice'
			};
		    var dataAdapterInvoice = new $.jqx.dataAdapter(sourceInvoice,
		    {
		    	formatData: function (data) {
			    	if (data.filterscount) {
	                    var filterListFields = '';
	                    for (var i = 0; i < data.filterscount; i++) {
	                        var filterValue = data['filtervalue' + i];
	                        var filterCondition = data['filtercondition' + i];
	                        var filterDataField = data['filterdatafield' + i];
	                        var filterOperator = data['filteroperator' + i];
	                        filterListFields += '|OLBIUS|' + filterDataField;
	                        filterListFields += '|SUIBLO|' + filterValue;
	                        filterListFields += '|SUIBLO|' + filterCondition;
	                        filterListFields += '|SUIBLO|' + filterOperator;
	                    }
	                    data.filterListFields = filterListFields;
	                }
	                 data.$skip = data.pagenum * data.pagesize;
	                 data.$top = data.pagesize;
	                 data.$inlinecount = 'allpages';
	                return data;
	            },
	            loadError: function (xhr, status, error) {
	                alert(error);
	            },
	            downloadComplete: function (data, status, xhr) {
	                    if (!sourceInvoice.totalRecords) {
	                    	sourceInvoice.totalRecords = parseInt(data['odata.count']);
	                    }
	            }, 
	            beforeLoadComplete: function (records) {
	            	for (var i = 0; i < records.length; i++) {
	            		if(typeof(records[i])=='object'){
	            			for(var key in records[i]) {
	            				var value = records[i][key];
	            				if(value != null && typeof(value) == 'object' && typeof(value) != null){
	            					var date = new Date(records[i][key]['time']);
	            					records[i][key] = date;
	            				}
	            			}
	            		}
	            	}
	            }
		    });
		    $("#applInvoiceId").jqxDropDownButton({ width: 200, height: 25});
	        $('#jqxgridApplInvoiceId').jqxGrid({
	            source: dataAdapterInvoice,
	            filterable: true,
	            virtualmode: true, 
	            sortable:true,
	            editable: false,
	            autoheight:true,
	            pageable: true,
	            rendergridrows: function(obj)
				{
					return obj.data;
				},
	            columns: [
	              { text: '${uiLabelMap.FormFieldTitle_invoiceId}', datafield: 'invoiceId', width: 150},
	              { text: '${uiLabelMap.FormFieldTitle_invoiceTypeId}', datafield: 'invoiceTypeId', width: 150},
	              { text: '${uiLabelMap.FormFieldTitle_statusId}', datafield: 'statusId', width: 150},
	              { text: '${uiLabelMap.FormFieldTitle_description}', datafield: 'description', width: 150},
	              { text: '${uiLabelMap.FormFieldTitle_partyIdFrom}', datafield: 'partyIdFrom', width: 150},
	              { text: '${uiLabelMap.FormFieldTitle_partyId}', datafield: 'partyId', width: 150},
	              { text: '${uiLabelMap.FormFieldTitle_amount}', datafield: 'amount', width: 150},
	              { text: '${uiLabelMap.FormFieldTitle_currencyUomId}', datafield: 'currencyUomId', width: 150}
	            ]
	        });
	        
	        $("#jqxgridApplInvoiceId").on('rowselect', function (event) {
        		var args = event.args;
        		var row = $("#jqxgridApplInvoiceId").jqxGrid('getrowdata', args.rowindex);
        		var dropDownContent = '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+ row['invoiceId'] +'</div>';
        		$('#applInvoiceId').jqxDropDownButton('setContent', dropDownContent);
        		$('#applInvoiceId').jqxDropDownButton('close');
        	});
	    }
	});
	
	$('#wdwPosibleApplication').jqxWindow({showCollapseButton: false, maxHeight: 1000, autoOpen: false, maxWidth: "75%", height: 550, minWidth: '40%', width: "90%", isModal: true, modalZIndex: 10000,theme:this.theme, collapsed:false,
	    initContent: function () {
	    	//Create posibleInv
	    	var posibleInv = getPosibleInvData();
	    	sourceInv =
	    	{
	    	    localdata: posibleInv,
	    	    datatype: "array",
	    	    datafields:
	    	    [
	    	        { name: 'invoiceId', type: 'string' },
	    	        { name: 'currencyUomId', type: 'string' },
	    	        { name: 'description', type: 'string' },
	    	        { name: 'invoiceDate', type: 'date' },
	    	        { name: 'amount', type: 'number' },
	    	        { name: 'amountApplied', type: 'number' },
	    	        { name: 'amountToApply', type: 'number' },
	    	    ],
	    	    updaterow: function (rowid, rowdata, commit) {
	    	    	var data = rowdata;
	    	    	var submitData = {};
	    	    	submitData['paymentId'] = '${parameters.paymentId}';
	    	    	submitData['invoiceId'] = data.invoiceId;
	    	    	submitData['description'] = data.description;
	    	    	submitData['invoiceDate'] = data.invoiceDate;
	    	    	submitData['amount'] = data.amount;
	    	    	submitData['dummy'] = data.amountApplied;
	    	    	submitData['amountApplied'] = data.amountToApply;
	    	    	$.ajax({
    				  url: "accApcreatePaymentApplication",
    				  type: "POST",
    				  data: submitData,
    				  async: false,
    				  success: function(res) {
    					  if(res._ERROR_MESSAGE_LIST_ || res._ERROR_MESSAGE_){
    						  $('#applyContainer').empty();
    						  $("#applyJqxNotification").jqxNotification({ width: "100%", appendContainer: "#applyContainer", opacity: 0.9, autoClose: true, template: "error" });
        					  $("#applyJqxNotification").text(res._ERROR_MESSAGE_LIST_ ? res._ERROR_MESSAGE_LIST_  :  res._ERROR_MESSAGE_);
        					  $("#applyJqxNotification").jqxNotification('open');
    					  }else{
    						  sourceInv.localdata = getPosibleInvData();
    						  sourcePayInv.localdata = getPaymentInvData();
    						  updateInvGridTitle();
    						  $("#jqxgridPosibleInv").jqxGrid('updatebounddata');
    						  $("#jqxgridPaymentInv").jqxGrid('updatebounddata');
    						  $("#editPayment").jqxGrid('updatebounddata');
    						  $('#applyContainer').empty();
    						  $("#applyJqxNotification").jqxNotification({ width: "100%", appendContainer: "#applyContainer", opacity: 0.9, autoClose: true, template: "info" });
        					  $("#applyJqxNotification").text(wgupdatesuccess);
        					  $("#applyJqxNotification").jqxNotification('open');
    					  }
    				  }
    			  	})
	    	        commit(true);
	    	    }
	    	};
	    	
	    	$("#jqxgridPosibleInv").jqxGrid(
	    	{
	    	    width: '100%',
	    	    source: sourceInv,
	    	    columnsresize: true,
	    	    pageable: true,
	    	    autoheight: true,
	    	    showtoolbar: true,
	    	    editable: true,
	    	    rendertoolbar: function (toolbar) {
	            	var container = $("<div id='toolbarcontainer' class='widget-header'></div>");
	                toolbar.append(container);
	                container.append('<h4>${uiLabelMap.AccountingListInvoicesNotYetApplied}</h4>');
	    	    },
	    	    columns: [
	    	      { text: '${uiLabelMap.FormFieldTitle_invoiceId}', datafield: 'invoiceId', width: 100, editable: false },
	    	      { text: '${uiLabelMap.FormFieldTitle_invoiceDate}', datafield: 'invoiceDate', width: 150, cellsformat: 'dd/MM/yyyy', editable: false},
	    	      { text: '${uiLabelMap.FormFieldTitle_amount}', datafield: 'amount', width: 100, editable: false,
	    	    	  cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
	    	    		  var data = $('#jqxgridPosibleInv').jqxGrid('getrowdata', row);
			 			  return "<span>" + formatcurrency(data.amount,data.currencyUomId) + "</span>";
	    	    	  }
	    	      },
	    	      { text: '${uiLabelMap.FormFieldTitle_amountApplied}', datafield: 'amountApplied', width: 100, editable: false,
	    	    	  cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
	    	    		  var data = $('#jqxgridPosibleInv').jqxGrid('getrowdata', row);
			 			  return "<span>" + formatcurrency(data.amountApplied,data.currencyUomId) + "</span>";
	    	    	  }  
	    	      },
	    	      { text: '${uiLabelMap.FormFieldTitle_amountToApply}', datafield: 'amountToApply', width: 100, editable: true,
	    	    	  cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
	    	    		  var data = $('#jqxgridPosibleInv').jqxGrid('getrowdata', row);
			 			  return "<span>" + formatcurrency(data.amountToApply,data.currencyUomId) + "</span>";
	    	    	  }  
	    	      },
	    	      { text: '${uiLabelMap.FormFieldTitle_description}', datafield: 'description' }
	    	    ]
	    	});
	    	
	    	//Create posiblePay
	    	var posiblePay = getPosiblePayData();
	    	var source =
	    	{
	    	    localdata: posiblePay,
	    	    datatype: "array",
	    	    datafields:
	    	    [
	    	        { name: 'toPaymentId', type: 'string' },
	    	        { name: 'effectiveDate', type: 'date' },
	    	        { name: 'amount', type: 'number' },
	    	        { name: 'amountApplied', type: 'number' },
	    	        { name: 'amountToApply', type: 'number' },
	    	        { name: 'currencyUomId', type: 'string' },
	    	    ]
	    	};
	    	var dataAdapter = new $.jqx.dataAdapter(source);
	    	
	    	$("#jqxgridPosiblePay").jqxGrid(
	    	{
	    	    width: '100%',
	    	    source: dataAdapter,
	    	    columnsresize: true,
	    	    pageable: true,
	    	    autoheight: true,
	    	    showtoolbar: true,
	    	    editable: true,
	    	    rendertoolbar: function (toolbar) {
	            	var container = $("<div id='toolbarcontainer' class='widget-header'></div>");
	                toolbar.append(container);
	                container.append('<h4>${uiLabelMap.AccountingListPaymentsNotYetApplied}</h4>');
	            },
	    	    columns: [
	    	      { text: '${uiLabelMap.FormFieldTitle_toPaymentId}', datafield: 'toPaymentId', width: 150, editable: false },
	    	      { text: '${uiLabelMap.FormFieldTitle_effectiveDate}', datafield: 'effectiveDate', cellsformat: 'dd/MM/yyyy', width: 150, editable: false },
	    	      { text: '${uiLabelMap.FormFieldTitle_amount}', datafield: 'amount', width: 150, editable: false,
	    	    	  cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
	    	    		  var data = $('#jqxgridPosiblePay').jqxGrid('getrowdata', row);
			 			  return "<span>" + formatcurrency(data.amount,data.currencyUomId) + "</span>";
	    	    	  }
	    	      },
	    	      { text: '${uiLabelMap.FormFieldTitle_amountApplied}', datafield: 'amountApplied', width: 150, editable: false,
	    	    	  cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
	    	    		  var data = $('#jqxgridPosiblePay').jqxGrid('getrowdata', row);
			 			  return "<span>" + formatcurrency(data.amountApplied,data.currencyUomId) + "</span>";
	    	    	  }  
	    	      },
	    	      { text: '${uiLabelMap.FormFieldTitle_amountToApply}', datafield: 'amountToApply', editable: true,
	    	    	  cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
	    	    		  var data = $('#jqxgridPosiblePay').jqxGrid('getrowdata', row);
			 			  return "<span>" + formatcurrency(data.amountToApply,data.currencyUomId) + "</span>";
	    	    	  }  
	    	      },
	    	    ]
	    	});
		}
	});
	
	//Create Payment Appl Invoice
	var paymentInv = getPaymentInvData();
	sourcePayInv =
	{
	    localdata: paymentInv,
	    datatype: "array",
	    datafields:
	    [
	     	{ name: 'paymentApplicationId', type: 'string' },
	     	{ name: 'paymentId', type: 'string' },
	        { name: 'invoiceId', type: 'string' },
	        { name: 'invoiceItemSeqId', type: 'string' },
	        { name: 'amountApplied', type: 'string' },
	        { name: 'currencyUomId', type: 'string' }
	    ],
	    deleterow: function (rowid, commit) {
	    	var data = $('#jqxgridPaymentInv').jqxGrid('getrowdata', rowid);
	    	var submitData = {};
	    	submitData['paymentApplicationId'] = data.paymentApplicationId;
	    	submitData['paymentId'] = data.paymentId;
	    	submitData['invoiceId'] = data.invoiceId;
	    	submitData['invoiceItemSeqId'] = data.invoiceItemSeqId;
	    	submitData['amountApplied'] = data.amountApplied;
	    	$.ajax({
			  url: "removePaymentApplication",
			  type: "POST",
			  data: submitData,
			  async: false,
			  dataType: "json",
			  success: function(res) {
				  if(res._ERROR_MESSAGE_LIST_ || res._ERROR_MESSAGE_){
				  	  $('#container').empty();
					  $('#notification').jqxNotification({ width: "100%", appendContainer: "#container", opacity: 0.9, autoClose: true, template: "error" });
					  if(res._ERROR_MESSAGE_LIST_){
						  $('#notification').text(res._ERROR_MESSAGE_LIST_);
					  }
					  if(res._ERROR_MESSAGE_){
						  $('#notification').text(res._ERROR_MESSAGE_);
					  }
					  $('#notification').jqxNotification('open');
				  }else{
					  if(typeof sourceInv != 'undefined'){
						  sourceInv.localdata = getPosibleInvData();
						  $("#jqxgridPosibleInv").jqxGrid('updatebounddata');
					  }
					  sourcePayInv.localdata = getPaymentInvData();
					  $("#jqxgridPaymentInv").jqxGrid('updatebounddata');
					  $("#editPayment").jqxGrid('updatebounddata');
					  updateInvGridTitle();
					  $('#container').empty();
					  $("#notification").jqxNotification({ width: "100%", appendContainer: "#container", opacity: 0.9, autoClose: true, template: "info" });
					  $("#notification").text(res._EVENT_MESSAGE_);
					  $("#notification").jqxNotification('open');
				  }
			  }
		  	})
	        commit(true);
	    }
	};
	
	$("#jqxgridPaymentInv").jqxGrid(
	{
	    width: '100%',
	    source: sourcePayInv,
	    columnsresize: true,
	    pageable: true,
	    autoheight: true,
	    showtoolbar: false,
	    columns: [
	      { text: '${uiLabelMap.FormFieldTitle_invoiceId}', datafield: 'invoiceId', width: 200 },
	      { text: '${uiLabelMap.FormFieldTitle_invoiceItemSeqId}', datafield: 'invoiceItemSeqId', width: 200 },
	      { text: '${uiLabelMap.FormFieldTitle_amountApplied}', datafield: 'amountApplied',
	    	  cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
	      		  var data = $('#jqxgridPaymentInv').jqxGrid('getrowdata', row);
	    		  return '<span>' + formatcurrency(data.amountApplied,data.currencyUomId) + '</span>';
	      	  }  
	      },
	      { text: '${uiLabelMap.CommonDelete}', 
	    	  cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
	    		  return '<span><a onclick=' + "$('#jqxgridPaymentInv').jqxGrid('deleterow'," + row + ')' + '><i class="fa fa-trash-o"></i></a></span>';
	      	  }  
	      }
	    ]
	});
	
	//Create Payment Appl Pay
	var paymentPay = getPaymentPayData();
	var sourcePaymentPay =
	{
	    localdata: paymentPay,
	    datatype: "array",
	    datafields:
	    [
	        { name: 'toPaymentId', type: 'string' },
	        { name: 'amountApplied', type: 'string' },
	        { name: 'paymentApplicationId', type: 'string' },
	        { name: 'paymentId', type: 'string' }
	    ],
	    deleterow: function (rowid, commit) {
	    	var data = $('#jqxgridPaymentPay').jqxGrid('getrowdata', rowid);
	    	var submitData = {};
	    	submitData['paymentApplicationId'] = data.paymentApplicationId;
	    	submitData['paymentId'] = data.paymentId;
	    	submitData['toPaymentId'] = data.toPaymentId;
	    	submitData['amountApplied'] = data.amountApplied;
	    	$.ajax({
			  url: "removePaymentApplication",
			  type: "POST",
			  data: submitData,
			  async: false,
			  dataType: "json",
			  success: function(res) {
				  if(res._ERROR_MESSAGE_LIST_ || res._ERROR_MESSAGE_){
				  	  $('#container').empty();
					  $('#notification').jqxNotification({ width: "100%", appendContainer: "#container", opacity: 0.9, autoClose: true, template: "error" });
					  if(res._ERROR_MESSAGE_LIST_){
						  $('#notification').text(res._ERROR_MESSAGE_LIST_);
					  }
					  if(res._ERROR_MESSAGE_){
						  $('#notification').text(res._ERROR_MESSAGE_);
					  }
					  $('#notification').jqxNotification('open');
				  }else{
					  if(typeof sourcePaymentPay != 'undefined'){
						  sourcePaymentPay.localdata = getPosibleInvData();
						  $("#jqxgridPaymentPay").jqxGrid('updatebounddata');
					  }
					  sourcePaymentPay.localdata = getPaymentInvData();
					  $("#jqxgridPaymentPay").jqxGrid('updatebounddata');
					  $("#editPayment").jqxGrid('updatebounddata');
					  updateInvGridTitle();
					  $('#container').empty();
					  $("#notification").jqxNotification({ width: "100%", appendContainer: "#container", opacity: 0.9, autoClose: true, template: "info" });
					  $("#notification").text(res._EVENT_MESSAGE_);
					  $("#notification").jqxNotification('open');
				  }
			  }
		  	})
	        commit(true);
	    }
	};
	var dataAdapterPaymentPay = new $.jqx.dataAdapter(sourcePaymentPay);
	
	$("#jqxgridPaymentPay").jqxGrid(
	{
	    width: '100%',
	    source: dataAdapterPaymentPay,
	    columnsresize: true,
	    pageable: true,
	    autoheight: true,
	    showtoolbar: false,
	    columns: [
	      { text: '${uiLabelMap.FormFieldTitle_toPaymentId}', datafield: 'toPaymentId', width: 200 },
	      { text: '${uiLabelMap.FormFieldTitle_amountApplied}', datafield: 'amountApplied',
	    	  cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
	      		  var data = $('#jqxgridPaymentPay').jqxGrid('getrowdata', row);
	    		  return '<span>' + formatcurrency(data.amountApplied,data.currencyUomId) + '</span>';
	      	  }  
	      },
	      { text: '${uiLabelMap.CommonDelete}', 
	    	  cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
	    		  return '<span><a onclick=' + "$('#jqxgridPaymentPay').jqxGrid('deleterow'," + row + ')' + '><i class="fa fa-trash-o"></i></a></span>';
	      	  }  
	      }
	    ]
	});
	
	//Create Payment Appl Bill
	var paymentBil = getPaymentBilData();
	var sourcePaymentBil =
	{
	    localdata: paymentBil,
	    datatype: "array",
	    datafields:
	    [
	        { name: 'billingAccountId', type: 'string' },
	        { name: 'amountApplied', type: 'string' },
	        { name: 'paymentApplicationId', type: 'string' },
	        { name: 'paymentId', type: 'string' }
	    ],
	    deleterow: function (rowid, commit) {
	    	var data = $('#jqxgridPaymentBil').jqxGrid('getrowdata', rowid);
	    	var submitData = {};
	    	submitData['paymentApplicationId'] = data.paymentApplicationId;
	    	submitData['paymentId'] = data.paymentId;
	    	submitData['toPaymentId'] = data.toPaymentId;
	    	submitData['amountApplied'] = data.amountApplied;
	    	$.ajax({
			  url: "removePaymentApplication",
			  type: "POST",
			  data: submitData,
			  async: false,
			  dataType: "json",
			  success: function(res) {
				  if(res._ERROR_MESSAGE_LIST_ || res._ERROR_MESSAGE_){
				  	  $('#container').empty();
					  $('#notification').jqxNotification({ width: "100%", appendContainer: "#container", opacity: 0.9, autoClose: true, template: "error" });
					  if(res._ERROR_MESSAGE_LIST_){
						  $('#notification').text(res._ERROR_MESSAGE_LIST_);
					  }
					  if(res._ERROR_MESSAGE_){
						  $('#notification').text(res._ERROR_MESSAGE_);
					  }
					  $('#notification').jqxNotification('open');
				  }else{
					  if(typeof sourcePaymentBil != 'undefined'){
						  sourcePaymentBil.localdata = getPosibleInvData();
						  $("#jqxgridPaymentBil").jqxGrid('updatebounddata');
					  }
					  sourcePaymentBil.localdata = getPaymentInvData();
					  $("#jqxgridPaymentBil").jqxGrid('updatebounddata');
					  $("#editPayment").jqxGrid('updatebounddata');
					  updateInvGridTitle();
					  $('#container').empty();
					  $("#notification").jqxNotification({ width: "100%", appendContainer: "#container", opacity: 0.9, autoClose: true, template: "info" });
					  $("#notification").text(res._EVENT_MESSAGE_);
					  $("#notification").jqxNotification('open');
				  }
			  	}
		  	})
	        commit(true);
	    }
	};
	var dataAdapterPaymentBil = new $.jqx.dataAdapter(sourcePaymentBil);
	
	$("#jqxgridPaymentBil").jqxGrid(
	{
	    width: '100%',
	    source: dataAdapterPaymentBil,
	    columnsresize: true,
	    pageable: true,
	    autoheight: true,
	    showtoolbar: false,
	    columns: [
	      { text: '${uiLabelMap.FormFieldTitle_billingAccountId}', datafield: 'billingAccountId', width: 200 },
	      { text: '${uiLabelMap.FormFieldTitle_amountApplied}', datafield: 'amountApplied',
	    	  cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
	      		  var data = $('#jqxgridPaymentBil').jqxGrid('getrowdata', row);
	    		  return '<span>' + formatcurrency(data.amountApplied,data.currencyUomId) + '</span>';
	      	  }
	      },
	      { text: '${uiLabelMap.CommonDelete}', 
	    	  cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
	    		  return '<span><a onclick=' + "$('#jqxgridPaymentBil').jqxGrid('deleterow'," + row + ')' + '><i class="fa fa-trash-o"></i></a></span>';
	      	  }  
	      }
	    ]
	});
	
	//Create Payment Appl Tax
	var paymentTax = getPaymentTaxData();
	var sourcePaymentTax =
	{
	    localdata: paymentTax,
	    datatype: "array",
	    datafields:
	    [
	        { name: 'taxAuthGeoId', type: 'string' },
	        { name: 'amountApplied', type: 'string' },
	        { name: 'paymentApplicationId', type: 'string' },
	        { name: 'paymentId', type: 'string' }
	    ],
	    deleterow: function (rowid, commit) {
	    	var data = $('#jqxgridPaymentTax').jqxGrid('getrowdata', rowid);
	    	var submitData = {};
	    	submitData['paymentApplicationId'] = data.paymentApplicationId;
	    	submitData['paymentId'] = data.paymentId;
	    	submitData['taxAuthGeoId'] = data.taxAuthGeoId;
	    	submitData['amountApplied'] = data.amountApplied;
	    	$.ajax({
			  url: "removePaymentApplication",
			  type: "POST",
			  data: submitData,
			  async: false,
			  dataType: "json",
			  success: function(res) {
				  if(res._ERROR_MESSAGE_LIST_ || res._ERROR_MESSAGE_){
				  	  $('#container').empty();
					  $('#notification').jqxNotification({ width: "100%", appendContainer: "#container", opacity: 0.9, autoClose: true, template: "error" });
					  if(res._ERROR_MESSAGE_LIST_){
						  $('#notification').text(res._ERROR_MESSAGE_LIST_);
					  }
					  if(res._ERROR_MESSAGE_){
						  $('#notification').text(res._ERROR_MESSAGE_);
					  }
					  $('#notification').jqxNotification('open');
				  }else{
					  if(typeof sourcePaymentTax != 'undefined'){
						  sourcePaymentTax.localdata = getPosibleInvData();
						  $("#jqxgridPaymentTax").jqxGrid('updatebounddata');
					  }
					  sourcePaymentTax.localdata = getPaymentTaxData();
					  $("#jqxgridPaymentTax").jqxGrid('updatebounddata');
					  $("#editPayment").jqxGrid('updatebounddata');
					  updateInvGridTitle();
					  $('#container').empty();
					  $("#notification").jqxNotification({ width: "100%", appendContainer: "#container", opacity: 0.9, autoClose: true, template: "info" });
					  $("#notification").text(res._EVENT_MESSAGE_);
					  $("#notification").jqxNotification('open');
				  }
			  	}
		  	})
	        commit(true);
	    }
	};
	var dataAdapterPaymentTax = new $.jqx.dataAdapter(sourcePaymentTax);
	
	$("#jqxgridPaymentTax").jqxGrid(
	{
	    width: '100%',
	    source: dataAdapterPaymentTax,
	    columnsresize: true,
	    pageable: true,
	    autoheight: true,
	    showtoolbar: false,
	    columns: [
	      { text: '${uiLabelMap.FormFieldTitle_taxAuthGeoId}', datafield: 'taxAuthGeoId', width: 200 },
	      { text: '${uiLabelMap.FormFieldTitle_amountApplied}', datafield: 'amountApplied',
	    	  cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
	      		  var data = $('#jqxgridPaymentInv').jqxGrid('getrowdata', row);
	    		  return '<span>' + formatcurrency(data.amountApplied,data.currencyUomId) + '</span>';
	      	  }  
	      },
	      { text: '${uiLabelMap.CommonDelete}', 
	    	  cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
	    		  return '<span><a onclick=' + "$('#jqxgridPaymentTax').jqxGrid('deleterow'," + row + ')' + '><i class="fa fa-trash-o"></i></a></span>';
	      	  }  
	      }
	    ]
	});
	
	//Bind Event
	$("#alterSave").on('click', function(){
    	var submitData = {};
    	submitData['paymentId'] = '${parameters.paymentId}';
    	submitData['invoiceId'] = $("#applInvoiceId").val();
    	submitData['toPaymentId'] = $("#applToPaymentId").val();
    	submitData['billingAccountId'] = $("#applBillingAccountId").val();
    	submitData['taxAuthGeoId'] = $("#applTaxAuthGeoId").val();
    	submitData['amountApplied'] = $("#applAmountApplied").val();
    	$.ajax({
		  url: "accApcreatePaymentApplication",
		  type: "POST",
		  data: submitData,
		  async: false,
		  
		  success: function(res) {
			  if(res._ERROR_MESSAGE_LIST_ || res._ERROR_MESSAGE_){
			  	  $('#container').empty();
				  $('#notification').jqxNotification({ width: "100%", appendContainer: "#container", opacity: 0.9, autoClose: true, template: "error" });
				  if(res._ERROR_MESSAGE_LIST_){
					  $('#notification').text(res._ERROR_MESSAGE_LIST_);
				  }
				  if(res._ERROR_MESSAGE_){
					  $('#notification').text(res._ERROR_MESSAGE_);
				  }
				  $('#notification').jqxNotification('open');
			  }else{
				  if(typeof sourceInv != 'undefined'){
					  sourceInv.localdata = getPosibleInvData();
					  $("#jqxgridPosibleInv").jqxGrid('updatebounddata');
				  }
				  sourcePayInv.localdata = getPaymentInvData();
				  $("#jqxgridPaymentInv").jqxGrid('updatebounddata');
				  $("#editPayment").jqxGrid('updatebounddata');
				  updateInvGridTitle();
				  $('#container').empty();
				  $("#notification").jqxNotification({ width: "100%", appendContainer: "#container", opacity: 0.9, autoClose: true, template: "info" });
				  $("#notification").text(res._EVENT_MESSAGE_);
				  $("#notification").jqxNotification('open');
			  }
		  }
	  	})
	});
	
	$("#posible").on('click', function(){
		$('#wdwPosibleApplication').jqxWindow('open');
	});
	
	$("#apply").on('click', function(){
		$('#wdwNewPayAppl').jqxWindow('open');
	});
</script>