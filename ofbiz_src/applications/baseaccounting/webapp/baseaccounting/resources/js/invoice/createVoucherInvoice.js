var createVoucherObj = (function(){
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
        if(globalVar.currencyUomId == "USD"){
            currencysymbol = "$";
            decimalseparator = ".";
            thousandsseparator = ",";
        }else if(globalVar.currencyUomId == "EUR"){
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
        taxProductCategoryDDL = new OlbDropDownList($("#taxProductCategoryId"), taxCategoryData, configTaxCategory, []);


        $("#invoiceIdVoucher").jqxInput({width: '90%', height: 20, disabled: true});
        var voucherF = new Array("01GTKT", "02GTTT", "06HDXK", "07KPTQ", "03XKNB","04HGDL", "01BLP", "02BLP2", "HĐ-BACHHOA", "HDBACHHOA", "TNDN", "PLHD", "THUEMB", "C1-02/NS");
        $("#voucherForm").jqxInput({width: '90%', height: 20, source: voucherF, theme:'energyblue'});

        var voucherS = new Array("AB/17P", "SC/17P", "AA/16P", "AB/16P", "BK/01","AA/17E", "TT/16P", "ND/16P", "AB/18P", "AB/19P", "SC/18P", "SC/19P", "AA/17P", "AA/18P", "AA/19P");
        $("#voucherSerial").jqxInput({width: '90%', height: 20, source: voucherS, theme:'energyblue'});

        $("#voucherNumber").jqxFormattedInput({width: '90%', height: 20, value: ''});
        $("#issuedDate").jqxDateTimeInput({width: '92%', height: 25, formatString: 'dd/MM/yyyy HH:mm:ss'});
        $("#voucherCreatedDate").jqxDateTimeInput({width: '92%', height: 25, formatString: 'dd/MM/yyyy HH:mm:ss'});
        $("#amountVoucher").jqxNumberInput({width: '92%', height: 25, spinButtons: true, decimalDigits: 2,
            symbolPosition: 'right', symbol: ' ' + currencysymbol, max: 999999999999, digits: 12, groupSeparator: thousandsseparator, decimalSeparator: decimalseparator});
        $("#taxAmountVoucher").jqxNumberInput({width: '92%', height: 25, spinButtons: true, decimalDigits: 2,
            symbolPosition: 'right', symbol: ' ' + currencysymbol, max: 999999999999, digits: 12, groupSeparator: thousandsseparator, decimalSeparator: decimalseparator});

        $("#totalAmountVoucher").jqxNumberInput({width: '92%', height: 25, spinButtons: true, decimalDigits: 2,
            symbolPosition: 'right', symbol: ' ' + currencysymbol, max: 999999999999, digits: 12, groupSeparator: thousandsseparator, decimalSeparator: decimalseparator});

        $('#voucherImgUpload').ace_file_input({
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
        accutils.createJqxWindow($("#addNewVoucherWindow"), 490, 550);
    };
    var initEvent = function(){
        $("#addNewVoucherWindow").on('open', function(event){
            initData();
        });
        $("#addNewVoucherWindow").on('close', function(event){
            if(_reloadAfterCloseWindow){
                if(globalVar.businessType == "AR"){
                    window.location.href = 'ViewARInvoice?invoiceId=' + globalVar.invoiceId + '&active=voucher-appl';
                }else{
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
        $("#cancelAddVoucher").click(function(event){
            $("#addNewVoucherWindow").jqxWindow('close');
        });
        $("#saveAndContinueAddVoucher").click(function(event){
            var valid = $("#addNewVoucherWindow").jqxValidator('validate');
            if(!valid){
                return;
            }
            bootbox.dialog(uiLabelMap.CreateInvoiceVoucherConfirm,
                [
                    {
                        "label" : uiLabelMap.CommonSubmit,
                        "class" : "btn-primary btn-small icon-ok open-sans",
                        "callback": function() {
                            createVoucherInvoice(false);
                        }
                    },
                    {
                        "label" : uiLabelMap.CommonCancel,
                        "class" : "btn-danger btn-small icon-remove open-sans",
                    }
                ]
            );
        });
        $("#saveAddVoucher").click(function(event){
            var valid = $("#addNewVoucherWindow").jqxValidator('validate');
            if(!valid){
                return;
            }
            bootbox.dialog(uiLabelMap.CreateInvoiceVoucherConfirm,
                [
                    {
                        "label" : uiLabelMap.CommonSubmit,
                        "class" : "btn-primary btn-small icon-ok open-sans",
                        "callback": function() {
                            createVoucherInvoice(true);
                        }
                    },
                    {
                        "label" : uiLabelMap.CommonCancel,
                        "class" : "btn-danger btn-small icon-remove open-sans",
                    }
                ]
            );
        });
        $("#amountVoucher").on('valueChanged', function(event){
            var value = event.args.value;
            var taxSelectedIndex = $("#taxProductCategoryId").jqxDropDownList('getSelectedIndex');
            var taxRate = 10;
            if(taxSelectedIndex > -1){
                taxRate = taxCategoryData[taxSelectedIndex].taxPercentage;
            }
            calculateTaxAmountVoucher(value, taxRate);
        });
        $("#taxAmountVoucher").on('valueChanged', function(event) {
            var value = event.args.value;
            var amount = $("#amountVoucher").val();
            var total = Number(value) + Number(amount);
            total = total.toLocaleString(globalVar.locale);
            $("#totalAmountVoucher").val(total);
        });
        $("#taxProductCategoryId").on('select', function(event){
            var args = event.args;
            if (args) {
                var index = args.index;
                var taxRate = taxCategoryData[index].taxPercentage;
                var value = $("#amountVoucher").val();
                calculateTaxAmountVoucher(value, taxRate);
            }
        });
        $("#getInvTotalAmountBtn").click(function(e){
            $("#amountVoucher").val(globalVar.invoiceNoTaxTotal);
        });
    };

    var calculateTaxAmountVoucher = function(amount, taxRate){
        var tax = amount * (taxRate/100);
        tax = Math.round(tax * 100)/100;
        tax = tax.toLocaleString(globalVar.locale);
        if(globalVar.currencyUomId === 'USD' || globalVar.currencyUomId === 'EUR') {
            tax = tax.replace('.','');
            tax = tax.replace(',', '.');
        }
        $("#taxAmountVoucher").val(tax);
        var total = (amount*(taxRate/100)) + Number(amount);
        total = Math.round(total * 100)/100;
        total = total.toLocaleString(globalVar.locale);
        if(globalVar.currencyUomId === 'USD' || globalVar.currencyUomId === 'EUR') {
            total = total.replace('.','');
            total = total.replace(',', '.');
        }
        $("#totalAmountVoucher").val(total);
    };

    var initValidator = function(){
        $("#addNewVoucherWindow").jqxValidator({
            rules: [
                { input: '#voucherForm', message: uiLabelMap.FieldRequired, action: 'keyup, change',
                    rule: function (input, commit) {
                        if(!input.val()){
                            return false
                        }
                        return true;

                    }
                },
                { input: '#voucherSerial', message: uiLabelMap.FieldRequired, action: 'keyup, change',
                    rule: function (input, commit) {
                        if(!input.val()){
                            return false
                        }
                        return true;
                    }
                },
                {input: '#voucherNumber', message: uiLabelMap.FieldRequired, action: 'keyup, change',
                    rule: function (input, commit) {
                        if(!input.val()){
                            return false
                        }
                        return true;
                    }
                },
                {input: '#issuedDate', message: uiLabelMap.FieldRequired, action: 'keyup, change',
                    rule: function (input, commit) {
                        if(!input.val()){
                            return false
                        }
                        return true;
                    }
                },
                {input: '#voucherCreatedDate', message: uiLabelMap.FieldRequired, action: 'keyup, change',
                    rule: function (input, commit) {
                        if(!input.val()){
                            return false
                        }
                        return true;
                    }
                },
                {input: '#taxProductCategoryId', message: uiLabelMap.FieldRequired, action: 'keyup, change',
                    rule: function (input, commit) {
                        if(!input.val()){
                            return false
                        }
                        return true;
                    }
                },
                {input: '#taxAmountVoucher', message: uiLabelMap.ValueMustBeGreateThanZero, action: 'keyup, change',
                    rule: function (input, commit) {
                        var value = input.val();
                        if(value < 0){
                            return false;
                        }
                        return true;
                    }
                },
                {input: '#amountVoucher', message: uiLabelMap.ValueMustBeGreateThanZero, action: 'keyup, change',
                    rule: function (input, commit) {
                        var value = input.val();
                        if(value <= 0){
                            return false;
                        }
                        return true;
                    }
                },
                {input: '#totalAmountVoucher', message: uiLabelMap.ValueMustBeGreateThanZero, action: 'keyup, change',
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
        var form = jQuery("#upLoadFileForm");
        var file = form.find('input[type=file]').eq(0);
        var fileUpload = $('#voucherImgUpload')[0].files[0];
        if(fileUpload){
            $("#_uploadedFile_fileName").val(fileUpload.name);
            $("#_uploadedFile_contentType").val(fileUpload.type);
            var dataSubmit = new FormData(jQuery('#upLoadFileForm')[0]);
        }else{
            var dataSubmit = new FormData();
        }
        dataSubmit.append("voucherForm", $("#voucherForm").val());
        dataSubmit.append("voucherSerial", $("#voucherSerial").val());
        dataSubmit.append("voucherNumber", $("#voucherNumber").val());
        dataSubmit.append("amount", $("#amountVoucher").val());
        dataSubmit.append("taxAmount", $("#taxAmountVoucher").val());
        dataSubmit.append("totalAmountVoucher", $("#totalAmountVoucher").val());

        var issuedDate = $("#issuedDate").jqxDateTimeInput('val', 'date');
        var voucherCreatedDate = $("#voucherCreatedDate").jqxDateTimeInput('val', 'date');
        dataSubmit.append("issuedDate", issuedDate.getTime());
        dataSubmit.append("voucherCreatedDate", voucherCreatedDate.getTime());
        dataSubmit.append("invoiceId", globalVar.invoiceId);
        var taxProductCategoryId = taxProductCategoryDDL.getValue();
        if (taxProductCategoryId == null) taxProductCategoryId = "";
        dataSubmit.append("taxProductCategoryId", taxProductCategoryId);
        return dataSubmit;
    };

    var createVoucherInvoice = function(isCloseWindow){
        var data = getData();
        Loading.show('loadingMacro');
        $.ajax({
            url: "createVoucherInvoice",
            data: data,
            type: 'POST',
            cache: false,
            processData: false, // Don't process the files
            contentType: false, // Set content type to false as jQuery will tell the server its a query string request
            success: function(response){
                if(response.responseMessage == "success"){
                    Grid.renderMessage('jqxVoucherList', response.successMessage, {template : 'success', appendContainer : '#containerjqxVoucherList'});
                    if(isCloseWindow){
                        //$("#addNewVoucherWindow").jqxWindow('close');
                        if(globalVar.businessType == "AR"){
                            window.location.href = 'ViewARInvoice?invoiceId=' + globalVar.invoiceId + '&active=voucher-appl';
                        }else{
                            window.location.href = 'ViewAPInvoice?invoiceId=' + globalVar.invoiceId + '&active=voucher-appl';
                        }
                        return;
                    }else{
                        resetData();
                        initData();
                        _reloadAfterCloseWindow = true;
                        Loading.hide('loadingMacro');
                    }
                    $("#jqxVoucherList").jqxGrid('updatebounddata');
                }else{
                    Loading.hide('loadingMacro');
                    bootbox.dialog(response.errorMessage,
                        [
                            {
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
    var initData = function(){
        $("#invoiceIdVoucher").val(globalVar.invoiceId);
        if(typeof(globalVar.issuedDate) != "undefined"){
            $("#issuedDate").val(new Date(globalVar.issuedDate));
        }
        if(typeof(globalVar.invoiceDate) != "undefined"){
            $("#voucherCreatedDate").val(new Date(globalVar.invoiceDate));
        }
    };
    var resetData = function(){
        Grid.clearForm($("#addNewVoucherWindow"));
        $("#voucherImgUpload").parent().find('a.remove').trigger('click');
        $("#addNewVoucherWindow").jqxValidator('hide');
    };
    return{
        init: init
    }
}());
$(document).ready(function () {
    createVoucherObj.init();
});