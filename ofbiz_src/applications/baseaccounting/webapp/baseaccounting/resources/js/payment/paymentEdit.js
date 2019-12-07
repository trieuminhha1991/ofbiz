var paymentEditObj = (function(){
	var _normalWindowHeight = 410;
	var _expandWindowHeight = 540;
	var init = function(){
		initInput();
		initDropDown();
		initDropDownGrid();
		initWindow();
		initEvent();
		initValidator();
		$("#jqxNotificationjqxgrid").jqxNotification({ width: "100%", appendContainer: "#containerjqxgrid", opacity: 1, autoClose: true, template: "success" });
	};
	var initInput = function(){
		$("#updatePayPartyFrom").jqxInput({width: '90%', height: 20, disabled: true});
		$("#updatePayPartyTo").jqxInput({width: '90%', height: 20, disabled: true});
		$("#amount").jqxNumberInput({ width: '100%',  max : 999999999999999, digits: 12, decimalDigits:2, spinButtons: true, min: 0});
		$("#amountNotTaxInc").jqxNumberInput({ width: '92%',  max : 999999999999999, digits: 15, decimalDigits:2, spinButtons: true, min: 0, disabled: true});
        $("#taxAmount").jqxNumberInput({ width: '96%',  max : 9999999999999, digits: 12, decimalDigits:2, spinButtons: true, min: 0});
        $("#conversionFactorEdit").jqxNumberInput({ width: '92%',  max : 9999999999999, digits: 12, decimalDigits:2, spinButtons: true, min: 0});
        $("#taxRate").jqxNumberInput({ width: '92%',  max : 100, digits: 3, decimalDigits: 2, spinButtons: true, min: 0, disabled: true,  symbolPosition: 'right', symbol: '%'});
        $("#effectiveDate").jqxDateTimeInput({width: '92%', height: 25, formatString: "dd/MM/yyyy HH:mm:ss"});
        $("#paidDate").jqxDateTimeInput({width: '92%', height: 25, formatString: "dd/MM/yyyy HH:mm:ss"});
        $('#paidDate').val(null);

        $("#organizationName").jqxInput({width: '92%', height: 20});
		$("#identifyCard").jqxInput({width: '92%', height: 20});
		$("#issuedPlace").jqxInput({width: '94%', height: 20});
		$("#issuedDate").jqxDateTimeInput({width: '96%', height: 25, showFooter: true});
        $('#issuedDate').val(null);

		var voucherF = new Array("01GTKT", "02GTTT", "06HDXK", "07KPTQ", "03XKNB","04HGDL", "01BLP", "02BLP2", "HĐ-BACHHOA", "HDBACHHOA", "TNDN", "PLHD", "THUEMB", "C1-02/NS");
		var voucherS = new Array("AB/17P", "SC/17P", "AA/16P", "AB/16P", "BK/01","AA/17E", "TT/16P", "ND/16P", "AB/18P", "AB/19P", "SC/18P", "SC/19P", "AA/17P", "AA/18P", "AA/19P");
		$("#voucherForm").jqxInput({width: '90%', height: 20, source: voucherF, theme: 'energyblue'});
		$("#voucherSerial").jqxInput({width: '90%', height: 20, source: voucherS, theme: 'energyblue'});
		$("#voucherNumber").jqxFormattedInput({width: '94%', height: 20, value: ''});
		$("#issuedDateVoucher").jqxDateTimeInput({width: '96%', height: 25, showFooter: true});
		$('#issuedDateVoucher').val(null);
	};
	var initDropDown = function(){
		accutils.createJqxDropDownList($("#updateEnumPartyTypeId"), globalVar.enumPartyTypeArr, {valueMember: 'enumId', displayMember: 'description', width: '92%', height: 25});
		accutils.createJqxDropDownList($("#paymentTypeId"), dataPaymentType, {valueMember: 'paymentTypeId', displayMember: 'description', width: '96%', height: 25, 
			placeHolder: uiLabelMap.filterchoosestring});
		accutils.createJqxDropDownList($("#paymentMethodId"), paymentMethodData, {valueMember: 'paymentMethodId', displayMember: 'description', 
			width: '96%', height: 25, placeHolder: uiLabelMap.filterchoosestring});
		accutils.createJqxDropDownList($("#payCurrencyUomId"), uomData, {valueMember: 'uomId', displayMember: 'abbreviation', width: '80%', height: 25});
	};
	var initDropDownGrid = function(){
		var datafield = [
		                 {name: 'partyId', type: 'string'}, 
		                 {name: 'partyCode', type: 'string'}, 
		                 {name: 'fullName', type: 'string'}
		                 ];
		var columns = [
						{text: uiLabelMap.BACCOrganizationId, datafield: 'partyCode', width: '30%'},
						{text: uiLabelMap.BACCFullName, datafield: 'fullName'}
					];
		
		var config = {
		   		width: '100%', 
		   		virtualmode: true,
		   		showfilterrow: true,
		   		pageable: true,
		   		sortable: true,
		        filterable: true,
		        editable: false,
		        url: '', 
		        showtoolbar: false,
	        	source: {
	        		pagesize: 5,
	        	}
	   	};
		if(globalVar.businessType == "AR"){
			$("#payPartyFromDropDown").jqxDropDownButton({
				width: '92%', 
				height: 25,
				theme: 'olbius',
				dropDownHorizontalAlignment: 'right'
			});
			Grid.initGrid(config, datafield, columns, null, $("#payPartyFromGrid"));
		}else{
			$("#payPartyToDropDown").jqxDropDownButton({
				width: '92%', 
				height: 25,
				theme: 'olbius',
				dropDownHorizontalAlignment: 'right'
			});
			Grid.initGrid(config, datafield, columns, null, $("#payPartyToGrid"));
		}
		
		var configProduct = {
			useUrl: true,
			root: 'results',
			widthButton: '96%',
			showdefaultloadelement: false,
			autoshowloadelement: false,
			dropDownHorizontalAlignment: 'right',
			datafields: [{name: 'productId', type: 'string'}, {name: 'productName', type: 'string'},
			             {name: 'productCode', type: 'string'}, {name: 'taxPercentage', type: 'number'}],
			columns: [
				{text: uiLabelMap.BACCProductId, datafield: 'productCode', width: '30%'},
				{text: uiLabelMap.BACCProductName, datafield: 'productName'}
			],
			url: 'JqxGetTaxProducts',
			useUtilFunc: true,
			key: 'productId',
			pagesize: 10,
			description: ['productName'],
		};
		accutils.initDropDownButton($("#productTaxDropDownBtn"), $("#productIdTaxGrid"), null, configProduct, []);
	};
	var initWindow = function(){
		accutils.createJqxWindow($("#editPaymentPopup"), 900, _normalWindowHeight);
	};
	var initEvent = function(){

		$("#editPaymentBtn").click(function(e){
			accutils.openJqxWindow($("#editPaymentPopup"));
		});
		$("#updateEnumPartyTypeId").on('select', function(event){
			var args = event.args;
		    if (args) {
		    	var item = args.item;
		    	var value = item.value;
		    	var grid;
		    	if(globalVar.businessType == "AR"){
		    		grid = $("#payPartyFromGrid");
		    		$("#payPartyFromDropDown").val("");
		    	}else{
		    		grid = $("#payPartyToGrid");
		    		$("#payPartyToDropDown").val("");
		    	}
		    	var source = grid.jqxGrid('source');
		    	source._source.url = 'jqxGeneralServicer?sname=JqxGetParties&enumId=' + value;
		    	grid.jqxGrid('source', source);
		    }
		});
		if(globalVar.businessType == "AP"){
			$("#partyToEditBtn").click(function(e){
				$("#updatePartyToContainerOld").hide();
				$("#updatePartyToContainerNew").show();
				$("#updateEnumPartyTypeId").val("SUPPLIER_PTY_TYPE");
			});
			$("#cancelEditPartyToBtn").click(function(e){
				$("#updatePartyToContainerOld").show();
				$("#updatePartyToContainerNew").hide();
				$("#payPartyToDropDown").jqxDropDownButton("setContent", "");
				$("#payPartyToGrid").jqxGrid('clearselection');
				$("#updateEnumPartyTypeId").jqxDropDownList('clearSelection');
				$("#editPaymentPopup").jqxValidator('hide');
			});
			$("#payPartyToGrid").on('rowclick', function(event){
				var args = event.args;
				var row = $("#payPartyToGrid").jqxGrid('getrowdata', args.rowindex);
				var dropDownContent = '<div class="innerDropdownContent">' + row['fullName'] + ' [' + row.partyCode + '] ' + '</div>';
		        $("#payPartyToDropDown").jqxDropDownButton('setContent', dropDownContent);
		        $("#payPartyToDropDown").jqxDropDownButton('close');
		        accutils.setAttrDataValue('payPartyToDropDown', row.partyId);
			});
		}else{
			$("#partyFromEditBtn").click(function(e){
				$("#updatePartyFromContainerOld").hide();
				$("#updatePartyFromContainerNew").show();
				$("#updateEnumPartyTypeId").val("CUSTOMER_PTY_TYPE");
			});
			$("#cancelEditPartyFromBtn").click(function(e){
				$("#updatePartyFromContainerOld").show();
				$("#updatePartyFromContainerNew").hide();
				$("#payPartyFromDropDown").jqxDropDownButton("setContent", "");
				$("#payPartyFromGrid").jqxGrid('clearselection');
				$("#updateEnumPartyTypeId").jqxDropDownList('clearSelection');
				$("#editPaymentPopup").jqxValidator('hide');
			});
			$("#payPartyFromGrid").on('rowclick', function(event){
				var args = event.args;
		        var row = $("#payPartyFromGrid").jqxGrid('getrowdata', args.rowindex);
		        var dropDownContent = '<div class="innerDropdownContent">' + row['fullName'] + ' [' + row.partyCode + '] ' + '</div>';
		        $("#payPartyFromDropDown").jqxDropDownButton('setContent', dropDownContent);
		        $("#payPartyFromDropDown").jqxDropDownButton('close');
		        accutils.setAttrDataValue('payPartyFromDropDown', row.partyId);
			});
		}
		$("#paymentTypeId").on('select', function(event){
			var args = event.args;
			if(args){
				var value = args.item.value;
				if("FEE_TAX_BANK_PAYMENT" == value){
					showPaymentTaxInfo();
				}else{
					hidePaymentTaxInfo();
				}
			}
		});

		$("#editPaymentPopup").on('open', function(e){
			Loading.show('loadingMacro');
			$.ajax({	
				url: 'getPaymentDetailInfo',
				type: "POST",
				data: {paymentId: globalVar.paymentId},
				success: function(response) {
					if(response.responseMessage == "error"){
						  bootbox.dialog(response.errorMessage,
								  [
									{
										"label" : uiLabelMap.CommonClose,
										"class" : "btn-danger btn-small icon-remove open-sans",
									}]		
							);	
						  return;
					}
					var data = response.data;
					$("#updatePayPartyFrom").val(data.fullNameFrom);
					$("#updatePayPartyTo").val(data.fullNameTo);
                    $("#amount").val(data.amount);
                    $("#conversionFactorEdit").val(data.conversionFactor);
                    if(data.currencyUomId && 'VND'!== data.currencyUomId) {
                        $("#divExchangedRateEdit").removeClass('hide');
                    }
					$("#payCurrencyUomId").val(data.currencyUomId);
                    $("#effectiveDate").val(new Date(data.effectiveDate));
                    if(data.paidDate) {
                        $("#paidDate").val(new Date(data.paidDate));
                    }
					$("#paymentTypeId").val(data.paymentTypeId);
					$("#paymentMethodId").val(data.paymentMethodId);
					if(data.productIdTaxCode){
						var dropDownContent = '<div class="innerDropdownContent">' + data.productName + '</div>';
				        $("#productTaxDropDownBtn").jqxDropDownButton('setContent', dropDownContent);
				        accutils.setAttrDataValue('productTaxDropDownBtn', data.productIdTaxCode);
				        $("#taxAmount").val(data.taxAmount);
				        $("#taxRate").val(data.taxRate);
					}
					$("#comments").val(data.comments);
					$("#organizationName").val(data.organizationName);
					if(data.issuedDate){
						$("#issuedDate").val(new Date(data.issuedDate));
					}
					$("#identifyCard").val(data.identifyCard);
					$("#issuedPlace").val(data.issuedPlace);
					$("#voucherForm").val(data.voucherForm);
					$("#voucherSerial").val(data.voucherSerial);
					$("#voucherNumber").val(data.voucherNumber);
					$('#issuedDateVoucher').val(new Date(data.issuedDateVoucher));
				},
				complete: function(){
					Loading.hide('loadingMacro');
				}
			});
		});
		$("#editPaymentPopup").on('close', function(){
			if(globalVar.businessType == 'AR'){
				$('#payPartyFromDropDown').jqxDropDownButton('setContent', '');
				$("#payPartyFromDropDown").attr('data-value', '');
				$("#payPartyFromGrid").jqxGrid('clearselection');
				$("#updatePartyFromContainerOld").show();
				$("#updatePartyFromContainerNew").hide();
			}else{
				$('#payPartyToDropDown').jqxDropDownButton('setContent', '');
				$("#payPartyToDropDown").attr('data-value', '');
				$("#payPartyToGrid").jqxGrid('clearselection');
				$("#updatePartyToContainerOld").show();
				$("#updatePartyToContainerNew").hide();
			}
			$("#updateEnumPartyTypeId").jqxDropDownList('clearSelection');
			$('#paymentTypeId').jqxDropDownList('clearSelection');
			$('#paymentMethodId').jqxDropDownList('clearSelection');
            $('#amount').val('');
            $('#conversionFactorEdit').val('');
			$('#comments').val("");
			$('#organizationName').val("");
			$('#identifyCard').val("");
			$('#issuedPlace').val("");
            $('#issuedDate').val(null);
            $('#paidDate').val(null);
			$("#updatePayPartyFrom").val("");
			$("#updatePayPartyTo").val("");
			$("#effectiveDate").val(null);
			hidePaymentTaxInfo();
			$('#editPaymentPopup').jqxValidator('hide');
		});
		$("#productIdTaxGrid").on('rowselect', function(event){
			var args = event.args;
			var rowData = args.row;
			var productId = rowData.productId;
			var totalAmount = $("#amount").val();
			var taxRate = typeof(rowData.taxPercentage) != 'undefined'? rowData.taxPercentage: 0;
			$("#taxRate").val(taxRate);
			calculateTax(totalAmount, taxRate);
		});
		
		$("#taxAmount").on('valueChanged', function(event){
			var taxAmount = event.args.value;
			var totalAmount = $("#amount").val();
			var amountNotTaxInc = totalAmount - taxAmount;
			$("#amountNotTaxInc").val(amountNotTaxInc);
		});
		$("#amount").on('valueChanged', function(event){
			var amount = event.args.value;
			var rowindex = $("#productIdTaxGrid").jqxGrid('getselectedrowindex');
			if(rowindex > -1){
				var rowData = $("#productIdTaxGrid").jqxGrid('getrowdata', rowindex);
				var taxRate = typeof(rowData.taxPercentage) != 'undefined'? rowData.taxPercentage: 0;
				calculateTax(amount, taxRate);
			}
		});
		$("#saveUpdatePayment").click(function(e){
			var valid = $('#editPaymentPopup').jqxValidator('validate');
			if(!valid){
				return;
			}
            Loading.show('loadingMacro');
			var data = getData();
			$.ajax({
				url: 'updatePaymentAjax',
				type: "POST",
				data: data,
				success: function(response) {
					  if(response.responseMessage == "error"){
						  bootbox.dialog(response.errorMessage,
									[
									{
										"label" : uiLabelMap.CommonClose,
										"class" : "btn-danger btn-small icon-remove open-sans",
									}]		
							);		
					  }else{
						  Grid.renderMessage('jqxgrid', response.successMessage, {template : 'success', appendContainer : '#containerjqxgrid'});
						  refreshPaymentView();
						  $("#editPaymentPopup").jqxWindow('close');
					  }
				  },
				complete:  function(jqXHR, textStatus){
					Loading.hide('loadingMacro');	
				}
			});
		});
		$("#cancelUpdatePayment").click(function(e){
			$('#editPaymentPopup').jqxWindow('close');
		});
	};
	
	var initValidator = function(){
		$('#editPaymentPopup').jqxValidator({
	        rules: [
	    			{input: '#paymentTypeId', message: uiLabelMap.FieldRequired, action: 'keyup, change, close', 
	    				rule: function (input, commit) {
	                       if(input.val()){
	                    	   return true;
	                       }else{
	                    	   return false;
	                       }
	    				}
	    			},
	    			{input: '#paymentMethodId', message: uiLabelMap.FieldRequired, action: 'keyup, change, close', 
	    				rule: function (input, commit) {
	                       if(input.val()){
	                    	   return true;
	                       }else{
	                    	   return false;
	                       }
	    				}
	    			},
	    			{input: '#amount', message: uiLabelMap.FieldRequired, action: 'keyup, change', 
	    				rule: function (input, commit) {
	                       if(input.val()){
	                    	   return true;
	                       }else{
	                    	   return false;
	                       }
	    				}
	    			},
	    			{input: '#organizationName', message: uiLabelMap.FieldRequired, action: 'keyup, change', 
	    				rule: function (input, commit) {
	    					var identifyCard = $('#identifyCard').val();
	    					if(identifyCard.length > 0){
	    						if(!$(input).val()){
	    							return false;
	    						}
	    					}
	    					return true;
	    				}
	    			},
	    			{input: '#organizationName', message: uiLabelMap.FieldRequired, action: 'none', 
	    				rule: function (input, commit) {
	    					var issuedDate = $('#issuedDate').val();
	    					if(issuedDate.length > 0){
	    						if(!$(input).val()){
	    							return false;
	    						}
	    					}
	    					return true;
	    				}
	    			},
	    			{input: '#organizationName', message: uiLabelMap.FieldRequired, action: 'none', 
	    				rule: function (input, commit) {
	    					var issuedPlace = $('#issuedPlace').val();
	    					if(issuedPlace.length > 0){
	    						if(!$(input).val()){
	    							return false;
	    						}
	    					}
	    					return true;
	    				}
	    			},
	    			{input: '#productTaxDropDownBtn', message: uiLabelMap.FieldRequired, action: 'keyup, change', 
	    				rule: function (input, commit) {
	    					var paymentTypeId = $("#paymentTypeId").val();
	    					if(paymentTypeId == "FEE_TAX_BANK_PAYMENT"){
    							if(!$(input).val()){
    								return false;
    							}
	    					}
	    					return true;
	    				}
	    			},
	    			{input: '#effectiveDate', message: uiLabelMap.FieldRequired, action: 'keyup, change', 
	    				rule: function (input, commit) {
	                       if(input.val()){
	                    	   return true;
	                       }
	                       return false;
	    				}
	    			},
	    			{input: '#voucherForm', message: uiLabelMap.FieldRequired, action: 'keyup, change', 
	    				rule: function (input, commit) {
	    					var paymentTypeId = $("#paymentTypeId").val();
	    					if(paymentTypeId == "FEE_TAX_BANK_PAYMENT"){
    							if(!$(input).val()){
    								return false;
    							}
	    					}
	    					return true;
	    				}
	    			},
	    			{input: '#voucherSerial', message: uiLabelMap.FieldRequired, action: 'keyup, change', 
	    				rule: function (input, commit) {
	    					var paymentTypeId = $("#paymentTypeId").val();
	    					if(paymentTypeId == "FEE_TAX_BANK_PAYMENT"){
    							if(!$(input).val()){
    								return false;
    							}
	    					}
	    					return true;
	    				}
	    			},
	    			{input: '#voucherNumber', message: uiLabelMap.FieldRequired, action: 'keyup, change', 
	    				rule: function (input, commit) {
	    					var paymentTypeId = $("#paymentTypeId").val();
	    					if(paymentTypeId == "FEE_TAX_BANK_PAYMENT"){
    							if(!$(input).val()){
    								return false;
    							}
	    					}
	    					return true;
	    				}
	    			},
	    			{input: '#issuedDateVoucher', message: uiLabelMap.FieldRequired, action: 'keyup, change', 
	    				rule: function (input, commit) {
	    					var paymentTypeId = $("#paymentTypeId").val();
	    					if(paymentTypeId == "FEE_TAX_BANK_PAYMENT"){
    							if(!$(input).val()){
    								return false;
    							}
	    					}
	    					return true;
	    				}
	    			}
               ]
	    });
	};
	
	var showPaymentTaxInfo = function(){
		$('#editPaymentPopup').jqxWindow({height: _expandWindowHeight});
		$(".taxCodeContainer").show();
	};
	
	var refreshPaymentView = function(){
		var paymentTypeSelect = $("#paymentTypeId").jqxDropDownList('getSelectedItem');
		$("#viewPaymentTypeId").html(paymentTypeSelect.label);
		var paymentMethodSelect = $("#paymentMethodId").jqxDropDownList('getSelectedItem');
		$("#viewPaymentMethodId").html(paymentMethodSelect.label);
		$("#viewPaymentAmount").html(formatcurrency($("#amount").val(), $("#payCurrencyUomId").val()));
        $("#viewConversionFactor").html(formatcurrency($("#conversionFactorEdit").val(), 'VND'));
		var effectiveDate = $("#effectiveDate").jqxDateTimeInput('val', 'date');
		$("#viewEffectiveDatePayment").html(getDateTimeDescription(effectiveDate, true));
		if($("#updateEnumPartyTypeId").val()){
			if(globalVar.businessType == "AR"){
				var partyFromName = $("#payPartyFromDropDown").val();
				$("#viewPartyFromName").html(partyFromName);
			}else{
				var partyToName = $("#payPartyToDropDown").val();
				$("#viewPartyToName").html(partyToName);
			}
		}
		$("#viewPaymentComment").html($("#comments").val());
		$("#viewPaymentOrganizationName").html($("#organizationName").val());
		$("#viewPaymentIdentifyCard").html($("#identifyCard").val());
		var issuedDate = $("#issuedDate").jqxDateTimeInput('val', 'date');
		if(issuedDate){
			$("#viewPaymentIssuedDate").html(getDateTimeDescription(issuedDate, false));
		}else{
			$("#viewPaymentIssuedDate").html("");
		}
        var paidDate = $("#paidDate").jqxDateTimeInput('val', 'date');
        if(paidDate){
            $("#viewPaidDatePayment").html(getDateTimeDescription(paidDate, true));
        }else{
            $("#viewPaidDatePayment").html("");
        }
		$("#viewPaymentIssuedPlace").html($("#issuedPlace").val());
	};
	
	var hidePaymentTaxInfo = function(){
		$(".taxCodeContainer").hide();
		$('#editPaymentPopup').jqxWindow({height: _normalWindowHeight});
		$("#taxAmount").val(0);
		$("#amountNotTax").val(0);
		$("#taxRate").val(0);
		$("#productIdTaxGrid").jqxGrid('clearselection');
		$("#productTaxDropDownBtn").jqxDropDownButton('setContent', "");
		$("#voucherForm").val("");
		$("#voucherSerial").val("");
		$("#voucherNumber").val("");
		$('#issuedDateVoucher').val(null);
	};
	
	var getData = function(){
		var submitData = {};
		submitData.paymentId = globalVar.paymentId;
		var enumPartyTypeId = $("#updateEnumPartyTypeId").val();
		if(enumPartyTypeId && enumPartyTypeId.length > 0){
			if(globalVar.businessType == "AR"){
				submitData.partyIdFrom = $('#payPartyFromDropDown').attr('data-value');
			}else{
				submitData.partyIdTo = $('#payPartyToDropDown').attr('data-value');
			}
		}
		submitData.paymentTypeId = $('#paymentTypeId').val();
		submitData.paymentMethodId = $('#paymentMethodId').val();
        submitData.amount = $('#amount').val();
        submitData.conversionFactor = $('#conversionFactorEdit').val();
		submitData.currencyUomId = $('#payCurrencyUomId').val();
		submitData.comments = $('#comments').val();
		var effectiveDate = $("#effectiveDate").jqxDateTimeInput('val', 'date');
		submitData.effectiveDate = effectiveDate.getTime();
		var paymentTypeId = $("#paymentTypeId").val();
		if(paymentTypeId == "FEE_TAX_BANK_PAYMENT"){
			submitData.productIdTaxCode = $("#productTaxDropDownBtn").attr('data-value');
			submitData.taxAmount = $("#taxAmount").val();
			submitData.voucherForm = $("#voucherForm").val();
			submitData.voucherSerial = $("#voucherSerial").val();
			submitData.voucherNumber = $("#voucherNumber").val();
			var issuedDateVoucher = $("#issuedDateVoucher").jqxDateTimeInput('val', 'date');
			submitData.issuedDateVoucher = issuedDateVoucher.getTime();
		}
		if($("#organizationName").val()){
			submitData.organizationName = $('#organizationName').val();
			if($("#identifyCard").val()){
				submitData.identifyCard = $('#identifyCard').val();
			}
			if($("#issuedPlace").val()){
				submitData.issuedPlace = $('#issuedPlace').val();
			}
			if($("#issuedDate").jqxDateTimeInput('val', 'date')){
				submitData.issuedDate = $("#issuedDate").jqxDateTimeInput('val', 'date').getTime();
			}
		}

        var paidDate = $("#paidDate").jqxDateTimeInput('val', 'date');
        if($("#paidDate").jqxDateTimeInput('val', 'date')){
            submitData.paidDate = $("#paidDate").jqxDateTimeInput('val', 'date').getTime();
        }

		return submitData;
	};
	
	var calculateTax = function(totalAmount, taxRate){
		var divide = (100 + taxRate) / 100;
		var amountNotTax = totalAmount/divide;
		amountNotTax = (Math.floor(amountNotTax * 100))/100;
		var taxAmount = totalAmount - amountNotTax;
		taxAmount = (Math.round(taxAmount * 100))/100;
		$("#taxAmount").val(taxAmount);
	};
	
	var getDateTimeDescription = function(date, showTime){
		var str = date.getDate() > 9? date.getDate() : ("0" + date.getDate());
		str += "/";
		str += date.getMonth() >= 9? (date.getMonth() + 1) : ("0" + (date.getMonth() + 1));
		str += "/";
		str += date.getFullYear();
		if(showTime){
			str += " - " + date.getHours() > 9? date.getHours() : ("0" + date.getHours());
			str += ":";
			str += date.getMinutes() > 9? date.getMinutes() : ("0" + date.getMinutes());
			str += ":";
			str += date.getSeconds() > 9? date.getSeconds() : ("0" + date.getSeconds());
		}
		return str;
	};
	return{
		init: init
	}
}());
$(document).ready(function () {
	paymentEditObj.init()
});