var editVoucherObj = (function(){
	var _reloadAfterCloseWindow = false;
	var init = function(){
		initInput();
		initWindow();
		initEvent();
		initValidator();
		$("#jqxNotificationjqxVoucherList").jqxNotification({ width: "100%", 
			appendContainer: "#containerjqxVoucherList", opacity: 0.9, template: "info" });
	};
	var initInput = function(){
		var	decimalseparator = ",";
		var thousandsseparator = ".";
		var currencysymbol = "đ";
		if (globalVar.currencyUomId == "USD") {
	        currencysymbol = "$";
	        decimalseparator = ".";
	        thousandsseparator = ",";
	    } else if (globalVar.currencyUomId == "EUR") {
	        currencysymbol = "€";
	        decimalseparator = ".";
	        thousandsseparator = ",";
	    }
		
		var configTaxCategory = {
				width:'92%',
				height: 25,
				key: "productCategoryId",
	    		value: "categoryName",
	    		displayDetail: true,
				dropDownWidth: 400,
				autoDropDownHeight: 'auto',
				multiSelect: false,
				placeHolder: uiLabelMap.BSClickToChoose,
				useUrl: false,
				url: '',
			};
		taxProductCategoryDDLEdit = new OlbDropDownList($("#taxProductCategoryIdEdit"), taxCategoryData, configTaxCategory, []);

			
		$("#invoiceIdVoucherEdit").jqxInput({width: '90%', height: 20, disabled: true});
		var voucherF = new Array("01GTKT", "02GTTT", "06HDXK", "07KPTQ", "03XKNB","04HGDL", "01BLP", "02BLP2", "HĐ-BACHHOA", "HDBACHHOA", "TNDN", "PLHD", "THUEMB", "C1-02/NS");
		$("#voucherFormEdit").jqxInput({width: '90%', height: 20, source: voucherF, theme:'energyblue'});
		
		var voucherS = new Array("AB/17P", "SC/17P", "AA/16P", "AB/16P", "BK/01","AA/17E", "TT/16P", "ND/16P", "AB/18P", "AB/19P", "SC/18P", "SC/19P", "AA/17P", "AA/18P", "AA/19P");
		$("#voucherSerialEdit").jqxInput({width: '90%', height: 20, source: voucherS, theme:'energyblue'});
		
		$("#voucherNumberEdit").jqxFormattedInput({width: '90%', height: 20, value: ''});
        $("#issuedDateEdit").jqxDateTimeInput({width: '92%', height: 25, formatString: 'dd/MM/yyyy HH:mm:ss'});
        $("#voucherCreatedDateEdit").jqxDateTimeInput({width: '92%', height: 25, formatString: 'dd/MM/yyyy HH:mm:ss'});
		$("#amountVoucherEdit").jqxNumberInput({width: '92%', height: 25, spinButtons: true, decimalDigits: 2, 
			symbolPosition: 'right', symbol: ' ' + currencysymbol, max: 999999999999, digits: 12, groupSeparator: thousandsseparator, decimalSeparator: decimalseparator});
		$("#taxAmountVoucherEdit").jqxNumberInput({width: '92%', height: 25, spinButtons: true, decimalDigits: 2, 
			symbolPosition: 'right', symbol: ' ' + currencysymbol, max: 999999999999, digits: 12, groupSeparator: thousandsseparator, decimalSeparator: decimalseparator});

		$("#totalAmountVoucherEdit").jqxNumberInput({width: '92%', height: 25, spinButtons: true, decimalDigits: 2, 
			symbolPosition: 'right', symbol: ' ' + currencysymbol, max: 999999999999, digits: 12, groupSeparator: thousandsseparator, decimalSeparator: decimalseparator});		
		
		$('#voucherImgUploadEdit').ace_file_input({
			no_file:'No File ...',
			btn_choose: uiLabelMap.CommonChooseFile,
			btn_change: uiLabelMap.wgeditonly,
			droppable:false,
			thumbnail:false,	
			preview_error : function(filename, error_code) {
			},
		}).on('change', function(){
			var x = $('.ace-file-input');
			var y = x.children();
			$(y[1]).css('width', '90%');
			$(y[2]).css('margin-right', '10px');
		});
		var y = $('.ace-file-input').children();
		$(y[1]).css('width', '92%');
	};
	var initWindow = function(){
		accutils.createJqxWindow($("#editVoucherWindow"), 490, 550);
	};
	var initEvent = function(){
		$("#editVoucherWindow").on('close', function(event){
			if (_reloadAfterCloseWindow) {
				if (globalVar.businessType == "AR") {
					window.location.href = 'ViewARInvoice?invoiceId=' + globalVar.invoiceId + '&active=voucher-appl';
				} else{
					window.location.href = 'ViewAPInvoice?invoiceId=' + globalVar.invoiceId + '&active=voucher-appl';
				}
				return;
			}
			resetData();
		});
		$('.ace-file-input a.remove').click(function(){
			var x = $('.ace-file-input');
			var y = x.children();
			$(y[1]).css('width', '92%');
			$(y[2]).css('margin-right', '0px');
		});
		$("#cancelEditVoucher").click(function(event){
			$("#editVoucherWindow").jqxWindow('close');
		});
		$("#saveEditVoucher").click(function(event){
			var valid = $("#editVoucherWindow").jqxValidator('validate');
			if(!valid){
				return;
			}
			bootbox.dialog(uiLabelMap.UpdateInvoiceVoucherConfirm,
				[{
					"label" : uiLabelMap.CommonSubmit,
					"class" : "btn-primary btn-small icon-ok open-sans",
					"callback": function() {
						editVoucherInvoice();
					}
				},
				{
					 "label" : uiLabelMap.CommonCancel,
					 "class" : "btn-danger btn-small icon-remove open-sans",
				}]		
			);
		});
		$("#amountVoucherEdit").on('valueChanged', function(event){
			var value = event.args.value;
			var taxSelectedIndex = $("#taxProductCategoryIdEdit").jqxDropDownList('getSelectedIndex');
			var taxRate = 10;
			if(taxSelectedIndex > -1){
				taxRate = taxCategoryData[taxSelectedIndex].taxPercentage;
			}
			calculateTaxAmountVoucher(value, taxRate);
		});
		$("#taxProductCategoryIdEdit").on('select', function(event){
			var args = event.args;
			if (args) {
				var index = args.index;
				var taxRate = taxCategoryData[index].taxPercentage;
				var value = $("#amountVoucherEdit").val();
				calculateTaxAmountVoucher(value, taxRate);
			}
		});
		$("#getInvTotalAmountBtnEdit").click(function(e){
			$("#amountVoucherEdit").val(globalVar.invoiceNoTaxTotal);
		});
	};
	
	var calculateTaxAmountVoucher = function(amount, taxRate){
		// var tax = amount * (taxRate/100);
		// tax = tax.toLocaleString(globalVar.locale);
		// $("#taxAmountVoucherEdit").val(tax);

        var tax = amount * (taxRate/100);
        tax = Math.round(tax * 100)/100;
        tax = tax.toLocaleString(globalVar.locale);
        if(globalVar.currencyUomId === 'USD' || globalVar.currencyUomId === 'EUR') {
            tax = tax.replace('.','');
            tax = tax.replace(',', '.');
        }
        $("#taxAmountVoucherEdit").val(tax);
        var total = (amount*(taxRate/100)) + Number(amount);
        total = Math.round(total * 100)/100;
        total = total.toLocaleString(globalVar.locale);
        if(globalVar.currencyUomId === 'USD' || globalVar.currencyUomId === 'EUR') {
            total = total.replace('.','');
            total = total.replace(',', '.');
        }
        $("#totalAmountVoucherEdit").val(total);
	};
	
	var initValidator = function(){
		$("#editVoucherWindow").jqxValidator({
			rules: [
				{ input: '#voucherFormEdit', message: uiLabelMap.FieldRequired, action: 'keyup, change', 
					rule: function (input, commit) {
						if(!input.val()){
							return false
						}
						return true;
						
					}
				},
				{ input: '#voucherSerialEdit', message: uiLabelMap.FieldRequired, action: 'keyup, change', 
					rule: function (input, commit) {
						if(!input.val()){
							return false
						}
						return true;
					}
				},
				{input: '#voucherNumberEdit', message: uiLabelMap.FieldRequired, action: 'keyup, change', 
					rule: function (input, commit) {
						if(!input.val()){
							return false
						}
						return true;
					}
				},
				{input: '#issuedDateEdit', message: uiLabelMap.FieldRequired, action: 'keyup, change', 
					rule: function (input, commit) {
						if(!input.val()){
							return false
						}
						return true;
					}
				},
				{input: '#voucherCreatedDateEdit', message: uiLabelMap.FieldRequired, action: 'keyup, change', 
					rule: function (input, commit) {
						if(!input.val()){
							return false
						}
						return true;
					}
				},
				{input: '#taxProductCategoryIdEdit', message: uiLabelMap.FieldRequired, action: 'keyup, change', 
					rule: function (input, commit) {
						if(!input.val()){
							return false
						}
						return true;
					}
				},				
				{input: '#taxAmountVoucherEdit', message: uiLabelMap.ValueMustBeGreateThanZero, action: 'keyup, change', 
					rule: function (input, commit) {
						var value = input.val();
						if(value < 0){
							return false;
						}
						return true;
					}
				},
				{input: '#amountVoucherEdit', message: uiLabelMap.ValueMustBeGreateThanZero, action: 'keyup, change', 
					rule: function (input, commit) {
						var value = input.val();
						if(value <= 0){
							return false;
						}
						return true;
					}
				},
				{input: '#totalAmountVoucherEdit', message: uiLabelMap.ValueMustBeGreateThanZero, action: 'keyup, change', 
					rule: function (input, commit) {
						var value = input.val();
						if(value <= 0){
							return false;
						}
						return true;
					}
				},
			]
		});
	};
	var getData = function(){
		var form = jQuery("#upLoadFileFormEdit");
		var file = form.find('input[type=file]').eq(0);
		var fileUpload = $('#voucherImgUploadEdit')[0].files[0];
		if(fileUpload){
			$("#_uploadedFile_fileName").val(fileUpload.name);
			$("#_uploadedFile_contentType").val(fileUpload.type);
			var dataSubmit = new FormData(jQuery('#upLoadFileFormEdit')[0]);
		} else {
			var dataSubmit = new FormData();
		}
		dataSubmit.append("voucherId", $("#voucherIdEdit").val());
		dataSubmit.append("voucherForm", $("#voucherFormEdit").val());
		dataSubmit.append("voucherSerial", $("#voucherSerialEdit").val());
		dataSubmit.append("voucherNumber", $("#voucherNumberEdit").val());
		dataSubmit.append("amount", $("#amountVoucherEdit").val());
		dataSubmit.append("taxAmount", $("#taxAmountVoucherEdit").val());
		dataSubmit.append("totalAmountVoucher", $("#totalAmountVoucherEdit").val());
		
		var issuedDate = $("#issuedDateEdit").jqxDateTimeInput('val', 'date');
		var voucherCreatedDate = $("#voucherCreatedDateEdit").jqxDateTimeInput('val', 'date');
		dataSubmit.append("issuedDate", issuedDate.getTime());
		dataSubmit.append("voucherCreatedDate", voucherCreatedDate.getTime());
		dataSubmit.append("invoiceId", globalVar.invoiceId);
		var taxProductCategoryId = taxProductCategoryDDLEdit.getValue();
		if (taxProductCategoryId == null) taxProductCategoryId = "";
		dataSubmit.append("taxProductCategoryId", taxProductCategoryId);
		return dataSubmit;
	};
	
	var editVoucherInvoice = function(){
		var data = getData();
		Loading.show('loadingMacro');
		$.ajax({
			url: "updateVoucherInvoice",
			data: data,
			type: 'POST',
			cache: false,			        
	        processData: false, // Don't process the files
	        contentType: false, // Set content type to false as jQuery will tell the server its a query string request
			success: function(response){
				if(response.responseMessage == "success"){
					Grid.renderMessage('jqxVoucherList', response.successMessage, {template : 'success', appendContainer : '#containerjqxVoucherList'});
					if(globalVar.businessType == "AR"){
						window.location.href = 'ViewARInvoice?invoiceId=' + globalVar.invoiceId + '&active=voucher-appl';
					} else {
						window.location.href = 'ViewAPInvoice?invoiceId=' + globalVar.invoiceId + '&active=voucher-appl';
					}
					return;
				} else {
					Loading.hide('loadingMacro');
					bootbox.dialog(response.errorMessage,
						[{
							"label" : uiLabelMap.CommonClose,
							"class" : "btn-danger btn-small icon-remove open-sans",
						}]		
					);			
				}
			},
			complete: function(){
			}
		});
	};
	var openPopupEditVoucher = function(data){
		$("#editVoucherWindow").jqxWindow('open');
		$("#invoiceIdVoucherEdit").val(globalVar.invoiceId);
		$("#voucherIdEdit").val(data.voucherId);
		$("#voucherFormEdit").val(data.voucherForm);
		$("#voucherSerialEdit").val(data.voucherSerial);
		$("#voucherNumberEdit").val(data.voucherNumber);
		$("#amountVoucherEdit").val(data.amount.toLocaleString(globalVar.locale));
		$("#taxAmountVoucherEdit").val(data.taxAmount.toLocaleString(globalVar.locale));
		$("#totalAmountVoucherEdit").val(data.totalAmountVoucher.toLocaleString(globalVar.locale));
		$("#issuedDateEdit").val(data.issuedDate);
		$("#voucherCreatedDateEdit").val(data.voucherCreatedDate);
		$("#taxProductCategoryIdEdit").val(data.taxProductCategoryId);
		if (data.objectInfo) {
			$("#upLoadFileFormEdit").css("display", "none");
			$("#displayImg").html("");
			$("#displayImg").css("display", "block");
			$('#displayImg').append("<a href='" + data.objectInfo + "' target='_blank'><i class='fa-file-image-o'></i>" + data.dataResourceName + "</a><a onclick='editVoucherObj.removeFile()'><i class='fa-remove'></i></a>");
		}
	};
	var removeFile = function() {
		$("#upLoadFileFormEdit").css("display", "block");
		$("#displayImg").css("display", "none");
	};
	var resetData = function(){
		Grid.clearForm($("#editVoucherWindow"));
		$("#voucherImgUploadEdit").parent().find('a.remove').trigger('click');
		$("#editVoucherWindow").jqxValidator('hide');
	};
	return{
		init: init,
		openPopupEditVoucher: openPopupEditVoucher,
		removeFile: removeFile
	}
}());
$(document).ready(function () {
	editVoucherObj.init();
});