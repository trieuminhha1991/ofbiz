var paymentApplObj = (function(){
	var _partyId = "";
	var init = function(){
		initDropDownBtn();
		initDropDown();
		initInput();
		initWindow();
		initValidator();
		initEvent();
		$('#jqxNotificationjqxgridInvAppl').jqxNotification({template : 'success', appendContainer : "#containerjqxgridInvAppl"});
	};
	var initWindow = function(){
		accutils.createJqxWindow($("#paymenApplPopup"), 540, 500);
	};
	var initDropDownBtn = function(){
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
		if(globalVar.businessType == 'AR'){
			$("#payPartyIdTo").jqxDropDownButton({
				width: '96%', 
				height: 25,
				theme: 'olbius',
				dropDownHorizontalAlignment: 'right'
			});
			Grid.initGrid(config, datafield, columns, null, $("#payPartyToGrid"));
		}else{
			$("#payPartyIdFrom").jqxDropDownButton({
				width: '96%', 
				height: 25,
				theme: 'olbius',
				dropDownHorizontalAlignment: 'right'
			});
			Grid.initGrid(config, datafield, columns, null, $("#payPartyFromGrid"));
		}
	};
	var initDropDown = function(){
		accutils.createJqxDropDownList($("#paymentTypeId"), dataPaymentType, {valueMember: 'paymentTypeId', displayMember: 'description', width: '96%', height: 25, 
			placeHolder: uiLabelMap.filterchoosestring});
		accutils.createJqxDropDownList($("#paymentMethodId"), paymentMethodData, {valueMember: 'paymentMethodId', displayMember: 'description', 
			width: '96%', height: 25, placeHolder: uiLabelMap.filterchoosestring});
		accutils.createJqxDropDownList($("#payCurrencyUomId"), uomData, {valueMember: 'uomId', displayMember: 'abbreviation', width: '90%', height: 25});
	};
	var initInput = function(){
        $("#amount").jqxNumberInput({ width: '100%',  max : 999999999999, digits: 12, decimalDigits: 2, spinButtons: true, min: 0});
        $("#conversionFactor").jqxNumberInput({ width: '96%',  max : 999999999999, digits: 12, decimalDigits: 2, spinButtons: true, min: 0});
		$("#organizationName").jqxInput({width: '94.5%', height: 20});
		$("#identifyCard").jqxInput({width: '94.5%', height: 20});
		$("#issuedPlace").jqxInput({width: '94.5%', height: 20});
		$("#issuedDatePayment").jqxDateTimeInput({width: '96%', height: 25, showFooter: true});
		$('#issuedDatePayment').val(null);
		if(globalVar.businessType == 'AR'){
			$("#payPartyIdFrom").jqxInput({width: '83.5%', height: 20, disabled: true});
		}else{
			$("#payPartyIdTo").jqxInput({width: '83.5%', height: 20, disabled: true});
		}
	};
	var initOpen = function(){
		if(globalVar.businessType == 'AR'){			
			accutils.setValueDropDownButtonOnly($("#payPartyIdTo"), globalVar.userLogin_lastOrg, globalVar.groupName + ' [' + globalVar.userLogin_lastOrg + ']');
			$("#payPartyIdTo").jqxDropDownButton('disabled',true);
			_partyId = globalVar.partyId;
			$("#payPartyIdFrom").val(globalVar.partyNameTo);
		}else{
			accutils.setValueDropDownButtonOnly($("#payPartyIdFrom"), globalVar.userLogin_lastOrg, globalVar.groupName + ' [' + globalVar.userLogin_lastOrg + ']');
			$("#payPartyIdFrom").jqxDropDownButton('disabled',true);
			_partyId = globalVar.partyIdFrom;
			$("#payPartyIdTo").val(globalVar.partyNameFrom);
		}
		$('#payCurrencyUomId').jqxDropDownList('val', globalVar.currencyUomId);
		$('#amount').val(globalVar.notAppliedAmount);
		if(globalVar.currencyUomId != 'VND') {
		    $('#divConversionFactor').removeClass('hide');
        } //TODO: fix me

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
	    			{input: '#amount', message: uiLabelMap.ValueMustBeGreateThanZero, action: 'keyup, change', 
	    				rule: function (input, commit) {
	                       if(input.val() <= 0){
	                    	   return false;
	                       }
	                       return true;
	    				}
	    			},
                    {input: '#amount', message: uiLabelMap.BACCValueAppliedMustLessThanAmountNotApplied, action: 'keyup, change',
                        rule: function (input, commit) {
                            if (input.val() > globalVar.notAppliedAmount){
                                return false;
                            }
                            return true;
                        }
                    },
                    {input: '#conversionFactor', message: uiLabelMap.ValueMustBeGreateThanZero, action: 'keyup, change',
                        rule: function (input, commit) {
                            if(input.val() <= 0 && globalVar.currencyUomId != 'VND'){
                                return false;
                            }
                            return true;
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
	    					var issuedDate = $('#issuedDatePayment').jqxDateTimeInput('val', 'date');
	    					if(issuedDate){
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
               ]
	    });
	};
	var initEvent = function(){
		$("#paymenApplPopup").on('open', function(event){
			initOpen();
		});
		$("#paymenApplPopup").on('close', function(){
			if(globalVar.businessType == 'AR'){
				$('#payPartyIdFrom').jqxDropDownButton('setContent', '');
				$("#payPartyIdFrom").attr('data-value', '');
			}else{
				$('#payPartyIdTo').jqxDropDownButton('setContent', '');
				$("#payPartyIdTo").attr('data-value', '');
			}
			$('#paymentTypeId').jqxDropDownList('clearSelection');
			$('#paymentMethodId').jqxDropDownList('clearSelection');
			$('#amount').val('');
			$('#conversionFactor').val('');
			$('#comments').val("");
			$('#organizationName').val("");
			$('#identifyCard').val("");
			$('#issuedPlace').val("");
			$('#issuedDatePayment').val(null);
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
		$('#saveCreatePaymentAppl').on('click', function(){
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
							createPaymentAppl();
						}
					},
					 {
						 "label" : uiLabelMap.CommonCancel,
						 "class" : "btn-danger btn-small icon-remove open-sans",
					 }
					 ]		
			);
		});
		$('#cancelCreatePaymentAppl').on('click', function(){
			$("#paymenApplPopup").jqxWindow('close');
		});
		$("#changePaymentPartyBtn").click(function(){
			paymentPartyEditObj.openWindow();
		});
	};

	var getData = function() {
        var submitData = {};
        if(globalVar.businessType == 'AR'){
            submitData['partyIdFrom'] = _partyId;
            submitData['partyIdTo'] = $('#payPartyIdTo').attr('data-value');
        }else{
            submitData['partyIdFrom'] = $('#payPartyIdFrom').attr('data-value');
            submitData['partyIdTo'] = _partyId;
        }
        submitData['paymentTypeId'] = $('#paymentTypeId').val();
        submitData['paymentMethodId'] = $('#paymentMethodId').val();
        submitData['amount'] = $('#amount').val();
        submitData['conversionFactor'] = $('#conversionFactor').val();
        submitData['currencyUomId'] = $('#payCurrencyUomId').val();
        submitData['statusId'] = 'PMNT_NOT_PAID';
        submitData['comments'] = $('#comments').val();
        submitData['invoiceId'] = globalVar.invoiceId;
        if($("#organizationName").val()){
            submitData['organizationName'] = $('#organizationName').val();
            if($("#identifyCard").val()){
                submitData['identifyCard'] = $('#identifyCard').val();
            }
            if($("#issuedPlace").val()){
                submitData['issuedPlace'] = $('#issuedPlace').val();
            }
            if($("#issuedDatePayment").jqxDateTimeInput('val', 'date')){
                submitData['issuedDate'] = $("#issuedDatePayment").jqxDateTimeInput('val', 'date').getTime();
            }
        }
        return submitData;
    };
	
	var createPaymentAppl = function(){
	    var data = getData();
		Loading.show('loadingMacro');
		$.ajax({
			url: "createPaymentAndAppl",
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
					  $("#paymenApplPopup").jqxWindow('close');
					  $("#jqxgridInvAppl").jqxGrid('updatebounddata');
					  Grid.renderMessage('jqxgridInvAppl', response.successMessage, {template : 'success', appendContainer : '#containerjqxgridInvAppl'});
					  updateInvoiceAmount();
				  }
			  },
			complete:  function(jqXHR, textStatus){
				Loading.hide('loadingMacro');	
			}
	  	});
	};
	var updateInvoiceAmount = function(){
		$.ajax({
			url: 'getInvoiceAppliedAndNotAppliedAmount',
			type: 'POST',
			data:{invoiceId: globalVar.invoiceId},
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
				$("#appliedAmountInv").html(response.appliedAmount);
				$("#notAppliedAmountInv").html(response.notAppliedAmount);
				globalVar.notAppliedAmount = response.notAppliedAmountNbr;
			},
			complete: function(jqXHR, textStatus){
				
			}
		});
	};
	var openWindow = function(){
		accutils.openJqxWindow($("#paymenApplPopup"));
	};
	var setPaymentPartyData = function(data){
		_partyId = data.partyId;
		if(globalVar.businessType == 'AR'){
			$("#payPartyIdFrom").val(data.fullName);
		}else{
			$("#payPartyIdTo").val(data.fullName);
		}
	};
	return {
		init: init,
		openWindow: openWindow,
		setPaymentPartyData: setPaymentPartyData
	}
}());

var paymentPartyEditObj = (function(){
	var init = function(){
		initDropDown();
		initWindow();
		initEvent();
		initValidator();
	};
	var initDropDown = function(){
		accutils.createJqxDropDownList($("#enumPartyTypeId"), globalVar.enumPartyTypeArr, 
				{valueMember: 'enumId', displayMember: 'description', width: '99%', height: 25});
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
		$("#paymentPartyDropDown").jqxDropDownButton({
			width: '99%', 
			height: 25,
			theme: 'olbius',
			//dropDownHorizontalAlignment: 'right'
		});
		Grid.initGrid(config, datafield, columns, null, $("#paymentPartyGrid"));
	};
	var initWindow = function(){
		accutils.createJqxWindow($("#PaymentPartyEditWindow"), 350, 170);
	};
	var initEvent = function(){
		$("#enumPartyTypeId").on('select', function(event){
			var args = event.args;
		    if (args) {
		    	var item = args.item;
		    	var value = item.value;
		    	var grid = $("#paymentPartyGrid");
		    	$("#paymentPartyDropDown").val("");
		    	var source = grid.jqxGrid('source');
		    	source._source.url = 'jqxGeneralServicer?sname=JqxGetParties&enumId=' + value;
		    	grid.jqxGrid('source', source);
		    }
		});
		$("#paymentPartyGrid").on('rowclick', function(event){
			var args = event.args;
	        var row = $("#paymentPartyGrid").jqxGrid('getrowdata', args.rowindex);
	        var dropDownContent = '<div class="innerDropdownContent">' + row['fullName'] + ' [' + row.partyCode + '] ' + '</div>';
	        $("#paymentPartyDropDown").jqxDropDownButton('setContent', dropDownContent);
	        $("#paymentPartyDropDown").jqxDropDownButton('close');
	        accutils.setAttrDataValue('paymentPartyDropDown', row.partyId);
		});
		$("#PaymentPartyEditWindow").on('open', function(event){
			if(globalVar.businessType == 'AR'){			
				$("#enumPartyTypeId").val("CUSTOMER_PTY_TYPE");
			}else{
				$("#enumPartyTypeId").val("SUPPLIER_PTY_TYPE");
			}
		});
		$("#PaymentPartyEditWindow").on('close', function(event){
			$("#paymentPartyGrid").jqxGrid('clearselection');
			$("#enumPartyTypeId").jqxDropDownList('clearSelection');
		});
		
		$("#cancelEditParty").click(function(){
			$("#PaymentPartyEditWindow").jqxWindow('close');
		});
		$("#saveEditParty").click(function(){
			var valid = $('#PaymentPartyEditWindow').jqxValidator('validate');
			if(!valid){
				return;
			}
			var index = $("#paymentPartyGrid").jqxGrid('getselectedrowindex');
			var data = $("#paymentPartyGrid").jqxGrid('getrowdata', index);
			paymentApplObj.setPaymentPartyData(data);
			$('#PaymentPartyEditWindow').jqxWindow('close');
		});
		
	};
	var initValidator = function(){
		$('#PaymentPartyEditWindow').jqxValidator({
			rules: [
				{input: '#paymentPartyDropDown', message: uiLabelMap.FieldRequired, action: 'keyup, change', 
					rule: function (input, commit) {
						var index = $("#paymentPartyGrid").jqxGrid('getselectedrowindex');
						if(index < 0){
							return false;
						}
						return true;
					}
				},    
			]
		});
	};
	var openWindow = function(){
		accutils.openJqxWindow($("#PaymentPartyEditWindow"));
	};
	return{
		init: init,
		openWindow: openWindow
	}
}());

$(document).ready(function(){
	$.jqx.theme = 'olbius';
	paymentApplObj.init();
	paymentPartyEditObj.init();
});