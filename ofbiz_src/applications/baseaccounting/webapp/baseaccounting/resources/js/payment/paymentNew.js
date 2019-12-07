var paymentNewObj = (function(){
	var _locale = "vi";
	var THEME = 'olbius';
	var _normalWindowHeight = 380;
	var _expandWindowHeight = 510;
	var init = function(){
		initDropDownBtn();
		initDropDown();
		initInput();
		initWindow();
		initValidator();
		initEvent();
	};
	var initWindow = function(){
		accutils.createJqxWindow($("#newPaymentPopup"), 900, _normalWindowHeight);
	};
	var initDropDownBtn = function(){
		$("#payPartyIdTo").jqxDropDownButton({
			width: '96%', 
			height: 25,
			theme: 'olbius',
			dropDownHorizontalAlignment: 'right'
		});
		$("#payPartyIdFrom").jqxDropDownButton({
			width: '96%', 
			height: 25,
			theme: 'olbius',
			dropDownHorizontalAlignment: 'right'
		});
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
	   	Grid.initGrid(config, datafield, columns, null, $("#payPartyFromGrid"));
	   	Grid.initGrid(config, datafield, columns, null, $("#payPartyToGrid"));
	   	
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
	var initDropDown = function(){
		accutils.createJqxDropDownList($("#enumPartyTypeId"), globalVar.enumPartyTypeArr, {valueMember: 'enumId', displayMember: 'description', width: '96%', height: 25});
		accutils.createJqxDropDownList($("#paymentTypeId"), dataPaymentType, {valueMember: 'paymentTypeId', displayMember: 'description', width: '96%', height: 25, 
			placeHolder: uiLabelMap.filterchoosestring});
		accutils.createJqxDropDownList($("#paymentMethodId"), paymentMethodData, {valueMember: 'paymentMethodId', displayMember: 'description', 
			width: '96%', height: 25, placeHolder: uiLabelMap.filterchoosestring});
		accutils.createJqxDropDownList($("#payCurrencyUomId"), uomData, {valueMember: 'uomId', displayMember: 'abbreviation', width: '91%', height: 25});
	};
	var initInput = function(){
        $("#amount").jqxNumberInput({ width: '100%',  max : 999999999999, theme: THEME, digits: 12, decimalDigits:2, spinButtons: true, min: 0});
        $("#conversionFactor").jqxNumberInput({ width: '96%',  max : 999999999999, theme: THEME, digits: 12, decimalDigits:2, spinButtons: true, min: 0});
		$("#amountNotTaxInc").jqxNumberInput({ width: '96%',  max : 999999999999999, theme: THEME, digits: 15, decimalDigits:2, spinButtons: true, min: 0, disabled: true});
		$("#taxAmount").jqxNumberInput({ width: '96%',  max : 9999999999999, theme: THEME, digits: 12, decimalDigits:2, spinButtons: true, min: 0});
		$("#taxRate").jqxNumberInput({ width: '96%',  max : 100, theme: THEME, digits: 3, decimalDigits: 1, spinButtons: true, min: 0, disabled: true,  symbolPosition: 'right', symbol: '%'});
		$("#effectiveDate").jqxDateTimeInput({width: '96%', height: 25});
		
		$("#organizationName").jqxInput({width: '94%', height: 20});
		$("#identifyCard").jqxInput({width: '94%', height: 20});
		$("#issuedPlace").jqxInput({width: '94%', height: 20});
		$("#issuedDate").jqxDateTimeInput({width: '96%', height: 25, showFooter: true});
		$('#issuedDate').val(null);
		
		var voucherF = new Array("01GTKT", "02GTTT", "06HDXK", "07KPTQ", "03XKNB","04HGDL", "01BLP", "02BLP2", "HĐ-BACHHOA", "HDBACHHOA", "TNDN", "PLHD", "THUEMB", "C1-02/NS");
		var voucherS = new Array("AB/17P", "SC/17P", "AA/16P", "AB/16P", "BK/01","AA/17E", "TT/16P", "ND/16P", "AB/18P", "AB/19P", "SC/18P", "SC/19P", "AA/17P", "AA/18P", "AA/19P");
		$("#voucherForm").jqxInput({width: '94%', height: 20, source: voucherF, theme:'energyblue'});
		$("#voucherSerial").jqxInput({width: '94%', height: 20, source: voucherS, theme:'energyblue'});
		$("#voucherNumber").jqxFormattedInput({width: '94%', height: 20, value: ''});
		$("#issuedDateVoucher").jqxDateTimeInput({width: '96%', height: 25, showFooter: true});
		$('#issuedDateVoucher').val(null);
	};
	var initOpen = function(){
		var date = new Date();
		$("#effectiveDate").val(date);
		if(globalVar.businessType == 'AR'){			
			accutils.setValueDropDownButtonOnly($("#payPartyIdTo"), globalVar.userLogin_lastOrg, globalVar.groupName + ' [' + globalVar.userLogin_lastOrg + ']');
			$("#payPartyIdTo").jqxDropDownButton('disabled',true);
			$("#enumPartyTypeId").val("CUSTOMER_PTY_TYPE");
		}else{
			accutils.setValueDropDownButtonOnly($("#payPartyIdFrom"), globalVar.userLogin_lastOrg, globalVar.groupName + ' [' + globalVar.userLogin_lastOrg + ']');
			$("#payPartyIdFrom").jqxDropDownButton('disabled',true);
			$("#enumPartyTypeId").val("SUPPLIER_PTY_TYPE");
		}
		$('#payCurrencyUomId').jqxDropDownList('val', globalVar.preferenceCurrencyUom);
	};
	var initValidator = function(){
		$('#formNewPayment').jqxValidator({
	        rules: [
	       			{input: '#payPartyIdFrom', message: uiLabelMap.FieldRequired, action: 'keyup, change, close', 
	       				rule: function (input, commit) {
	                       if(input.val()){
	                    	   return true;
	                       }else{
	                    	   return false;
	                       }
	    				}
	    			},
	    			{input: '#payPartyIdTo', message: uiLabelMap.FieldRequired, action: 'keyup, change, close', 
	    				rule: function (input, commit) {
	                       if(input.val()){
	                    	   return true;
	                       }else{
	                    	   return false;
	                       }
	    				}
	    			},
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
                    {input: '#conversionFactor', message: uiLabelMap.ValueMustBeGreateThanZero, action: 'keyup, change',
                        rule: function (input) {
                        var currencyUomId = $("#payCurrencyUomId").val();
                        if('VND' === currencyUomId) return true;
                        return input.val() && input.val() > 0;

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
	    			{input: '#organizationName', message: uiLabelMap.FieldRequired, action: 'keyup, change', 
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
	    			{input: '#organizationName', message: uiLabelMap.FieldRequired, action: 'keyup, change', 
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
	    						var rowindex = $("#productIdTaxGrid").jqxGrid('getselectedrowindex');
    							if(rowindex < 0){
    								return false;
    							}
	    					}
	    					return true;
	    				}
	    			},
	    			{input: '#effectiveDate', message: uiLabelMap.FieldRequired, action: 'keyup, change, close', 
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
    							if(!input.val()){
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
	    						if(!input.val()){
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
	    						if(!input.val()){
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
	    						if(!input.val()){
    								return false;
    							}
	    					}
	    					return true;
	    				}
	    			}
               ]
	    });
	};
	var initEvent = function(){
		$("#enumPartyTypeId").on('select', function(event){
			var args = event.args;
		    if (args) {
		    	var item = args.item;
		    	var value = item.value;
		    	var grid;
		    	if(globalVar.businessType == "AR"){
		    		grid = $("#payPartyFromGrid");
		    		$("#payPartyIdFrom").val("");
		    	}else{
		    		grid = $("#payPartyToGrid");
		    		$("#payPartyIdTo").val("");
		    	}
		    	grid.jqxGrid('clearselection');
		    	grid.jqxGrid('clearfilters');
		    	var source = grid.jqxGrid('source');
		    	source._source.url = 'jqxGeneralServicer?sname=JqxGetParties&enumId=' + value;
		    	grid.jqxGrid('source', source);
		    }
		});

		$("#payCurrencyUomId").on('select', function(event) {
            var args = event.args;
            if (args) {
                var item = args.item;
                var value = item.value;
                if('VND' != value) {
                    $("#conversionFactorDiv").removeClass('hide');
                }
                else {
                    $("#conversionFactorDiv").addClass('hide');
                    $("#conversionFactor").val(1);
                }
            }
        });
		$("#payPartyFromGrid").on('rowclick', function(event){
			var args = event.args;
	        var row = $("#payPartyFromGrid").jqxGrid('getrowdata', args.rowindex);
	        var dropDownContent = '<div class="innerDropdownContent">' + row['fullName'] + ' [' + row.partyCode + '] ' + '</div>';
	        $("#payPartyIdFrom").jqxDropDownButton('setContent', dropDownContent);
	        $("#payPartyIdFrom").jqxDropDownButton('close');
	        accutils.setAttrDataValue('payPartyIdFrom', row.partyId);
		});
		$("#payPartyToGrid").on('rowclick', function(event){
			var args = event.args;
			var row = $("#payPartyToGrid").jqxGrid('getrowdata', args.rowindex);
			var dropDownContent = '<div class="innerDropdownContent">' + row['fullName'] + ' [' + row.partyCode + '] ' + '</div>';
	        $("#payPartyIdTo").jqxDropDownButton('setContent', dropDownContent);
	        $("#payPartyIdTo").jqxDropDownButton('close');
	        accutils.setAttrDataValue('payPartyIdTo', row.partyId);
		});
		$("#newPaymentPopup").on('open', function(event){
			initOpen();
		});
		$("#newPaymentPopup").on('close', function(){
			if(globalVar.businessType == 'AR'){
				$('#payPartyIdFrom').jqxDropDownButton('setContent', '');
				$("#payPartyIdFrom").attr('data-value', '');
				$("#payPartyFromGrid").jqxGrid('clearselection');
				$("#payPartyFromGrid").jqxGrid('clearfilters');
				$("#payPartyFromGrid").jqxGrid('gotopage', 0);
			}else{
				$('#payPartyIdTo').jqxDropDownButton('setContent', '');
				$("#payPartyIdTo").attr('data-value', '');
				$("#payPartyToGrid").jqxGrid('clearselection');
				$("#payPartyToGrid").jqxGrid('clearfilters');
		    	$("#payPartyToGrid").jqxGrid('gotopage', 0);
			}
			$('#paymentTypeId').jqxDropDownList('clearSelection');
			$('#paymentMethodId').jqxDropDownList('clearSelection');
            $('#amount').val('');
            $('#conversionFactor').val('');
			$('#comments').val("");
			$('#organizationName').val("");
			$('#identifyCard').val("");
			$('#issuedPlace').val("");
			$('#issuedDate').val(null);
			hidePaymentTaxInfo();
			$('#formNewPayment').jqxValidator('hide'); 
		});
		
		$('#payPartyIdTo').on('close',function(){
			var interval = setInterval(function(){
				$('#payPartyToGrid').jqxGrid('clearSelection');
				clearInterval(interval);
			},10);
		});
		
		$('#payPartyIdFrom').on('close',function(){
			var interval = setInterval(function(){
				$('#payPartyFromGrid').jqxGrid('clearSelection');
				clearInterval(interval);
			},10);
		});
		$("#paymentTypeId").on('select', function(event){
			var args = event.args;
			if(args){
				var value = args.item.value;
				if("FEE_TAX_BANK_PAYMENT" == value){
					showPaymentTaxInfo();
				} else {
					hidePaymentTaxInfo();
				}
			}
		});
		$('#alterSave').on('click', function(){
			var valid = $('#formNewPayment').jqxValidator('validate');
			if(!valid){
				return;
			}
			bootbox.dialog(uiLabelMap.CreatePaymentApplicationConfirm,
					[
					 {
						"label" : uiLabelMap.CommonSubmit,
						"class" : "btn-primary btn-small icon-ok open-sans",
						"callback": function() {
							createPayment();
						}
					},
					 {
						 "label" : uiLabelMap.CommonCancel,
						 "class" : "btn-danger btn-small icon-remove open-sans",
					 }
					 ]		
			);
		});
		$('#alterCancel').on('click', function(){
			$("#newPaymentPopup").jqxWindow('close');
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
	};
	
	var showPaymentTaxInfo = function(){
		$('#newPaymentPopup').jqxWindow({height: _expandWindowHeight});
		$(".taxCodeContainer").show();
	};
	
	var hidePaymentTaxInfo = function(){
		$(".taxCodeContainer").hide();
		$('#newPaymentPopup').jqxWindow({height: _normalWindowHeight});
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
	
	var calculateTax = function(totalAmount, taxRate){
		var divide = (100 + taxRate) / 100;
		var amountNotTax = totalAmount/divide;
		amountNotTax = (Math.floor(amountNotTax * 100))/100;
		var taxAmount = totalAmount - amountNotTax;
		taxAmount = (Math.round(taxAmount * 100))/100;
		$("#taxAmount").val(taxAmount);
	};
	
	var createPayment = function(){
		var submitData = {};
		submitData['partyIdFrom'] = $('#payPartyIdFrom').attr('data-value');
		submitData['partyIdTo'] = $('#payPartyIdTo').attr('data-value');
		submitData['paymentTypeId'] = $('#paymentTypeId').val();
		submitData['paymentMethodId'] = $('#paymentMethodId').val();
        submitData['amount'] = $('#amount').val();
        submitData['conversionFactor'] = ('' + $('#conversionFactor').val()).replace('.', ',');
		submitData['currencyUomId'] = $('#payCurrencyUomId').val();
		submitData['statusId'] = 'PMNT_NOT_PAID';
		submitData['comments'] = $('#comments').val();
		var effectiveDate = $("#effectiveDate").jqxDateTimeInput('val', 'date');
		submitData.effectiveDate = effectiveDate.getTime();
		var paymentTypeId = $("#paymentTypeId").val();
		if(paymentTypeId == "FEE_TAX_BANK_PAYMENT"){
			var productIndex = $("#productIdTaxGrid").jqxGrid('getselectedrowindex');
			var rowData = $("#productIdTaxGrid").jqxGrid('getrowdata', productIndex);
			submitData.productIdTaxCode = rowData.productId;
			submitData.taxAmount = $("#taxAmount").val();
			submitData.voucherForm = $("#voucherForm").val();
			submitData.voucherSerial = $("#voucherSerial").val();
			submitData.voucherNumber = $("#voucherNumber").val();
			var issuedDateVoucher = $("#issuedDateVoucher").jqxDateTimeInput('val', 'date');
			submitData.issuedDateVoucher = issuedDateVoucher.getTime();
		}
		if($("#organizationName").val()){
			submitData['organizationName'] = $('#organizationName').val();
			if($("#identifyCard").val()){
				submitData['identifyCard'] = $('#identifyCard').val();
			}
			if($("#issuedPlace").val()){
				submitData['issuedPlace'] = $('#issuedPlace').val();
			}
			if($("#issuedDate").jqxDateTimeInput('val', 'date')){
				submitData['issuedDate'] = $("#issuedDate").jqxDateTimeInput('val', 'date').getTime();
			}
		}		
		
		var paymentTaxInfo = paymentPartyInfo.getSubmitData();
		if(paymentTaxInfo.hasOwnProperty("partyName")){
			submitData.paymentTaxInfo = JSON.stringify(paymentTaxInfo);
		}
		
		//Send Request Create
		Loading.show('loadingMacro');
		$.ajax({
			url: "createPayment",
			type: "POST",
			data: submitData,
			success: function(data) {
				  if(data.responseMessage == "error"){
					  bootbox.dialog(data.errorMessage,
								[
								{
									"label" : uiLabelMap.CommonClose,
									"class" : "btn-danger btn-small icon-remove open-sans",
								}]		
						);		
				  }else{
					  $("#clearAddInfoPayment").trigger('click');
					 					 
					  $("#newPaymentPopup").jqxWindow('close');
					  if($("#jqxgridPayment").length > 0){
						  $("#jqxgridPayment").jqxGrid('updatebounddata');
						  $('#containerjqxgridPayment').empty();
						  $('#jqxNotificationjqxgridPayment').jqxNotification({ template: 'success'});
						  $("#notificationContentjqxgridPayment").text(wgaddsuccess);
						  $("#jqxNotificationjqxgridPayment").jqxNotification("open");
					  }
				  }
			  },
			complete:  function(jqXHR, textStatus){
				Loading.hide('loadingMacro');	
			}
	  	});
	};
	return {
		init: init
	}
}());	

$(document).ready(function(){
	paymentNewObj.init();
});