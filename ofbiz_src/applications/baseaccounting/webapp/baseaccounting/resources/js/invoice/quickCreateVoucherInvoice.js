var quickCreateVoucherObj = (function(){
    var _reloadAfterCloseWindow = false;
    var init = function(){
        initInput();
        initWindow();
        initEvent();
        initValidator();
        $("#jqxNotificationjqxVoucherList").jqxNotification({ width: "100%",
            appendContainer: "#containerjqxVoucherList", opacity: 0.9, template: "info" });
    };
    var open = function() {
        $("#invoiceIdVoucherQuick").val(globalVar.invoiceId);
        var wtmp = window;
        var tmpwidth = $("#addNewVoucherWindowQuick").jqxWindow("width");
        $("#addNewVoucherWindowQuick").jqxWindow({ position: { x: (wtmp.outerWidth - tmpwidth)/2, y: pageYOffset + 70 }});
        $("#addNewVoucherWindowQuick").jqxWindow("open");
    };
    var initInput = function(){
        $("#invoiceIdVoucherQuick").jqxInput({width: '90%', height: 20, disabled: true});
        var voucherF = new Array("01GTKT", "02GTTT", "06HDXK", "07KPTQ", "03XKNB","04HGDL", "01BLP", "02BLP2", "HĐ-BACHHOA", "HDBACHHOA", "TNDN", "PLHD", "THUEMB", "C1-02/NS");
        $("#voucherFormQuick").jqxInput({width: '90%', height: 20, source: voucherF, theme:'energyblue'});

        var voucherS = new Array("AB/17P", "SC/17P", "AA/16P", "AB/16P", "BK/01","AA/17E", "TT/16P", "ND/16P", "AB/18P", "AB/19P", "SC/18P", "SC/19P", "AA/17P", "AA/18P", "AA/19P");
        $("#voucherSerialQuick").jqxInput({width: '90%', height: 20, source: voucherS, theme:'energyblue'});

        $("#voucherNumberQuick").jqxFormattedInput({width: '90%', height: 20, value: ''});
        $("#descriptionQuick").jqxInput({width: '90%', height: 20, value: ''});

    };
    var initWindow = function(){
        accutils.createJqxWindow($("#addNewVoucherWindowQuick"), 490, 300);
    };
    var initEvent = function(){
        $("#addNewVoucherWindowQuick").on('open', function(event){
            initData();
        });
        $("#addNewVoucherWindowQuick").on('close', function(event){
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
        $("#cancelAddVoucherQuick").click(function(event){
            $("#addNewVoucherWindowQuick").jqxWindow('close');
        });
        $("#saveAddVoucherQuick").click(function(event){
            var valid = $("#addNewVoucherWindowQuick").jqxValidator('validate');
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
    };

    var initValidator = function(){
        $("#addNewVoucherWindowQuick").jqxValidator({
            rules: [
                { input: '#voucherFormQuick', message: uiLabelMap.FieldRequired, action: 'keyup, change',
                    rule: function (input, commit) {
                        if(!input.val()){
                            return false
                        }
                        return true;

                    }
                },
                { input: '#voucherSerialQuick', message: uiLabelMap.FieldRequired, action: 'keyup, change',
                    rule: function (input, commit) {
                        if(!input.val()){
                            return false
                        }
                        return true;
                    }
                },
                {input: '#voucherNumberQuick', message: uiLabelMap.FieldRequired, action: 'keyup, change',
                    rule: function (input, commit) {
                        if(!input.val()){
                            return false
                        }
                        return true;
                    }
                }
            ]
        });
    };
    var getData = function(){
        var dataSubmit = new FormData();
        var description = $("#descriptionQuick").val();
        if(description){
            dataSubmit.append("description", description);
        }
        dataSubmit.append("description", description);
        dataSubmit.append("voucherForm", $("#voucherFormQuick").val());
        dataSubmit.append("voucherNumber", $("#voucherNumberQuick").val());
        dataSubmit.append("voucherSerial", $("#voucherSerialQuick").val());
        dataSubmit.append("invoiceId", globalVar.invoiceId);
        return dataSubmit;
    };

    var createVoucherInvoice = function(isCloseWindow){
        var data = getData();
        Loading.show('loadingMacro');
        $.ajax({
            url: "quickCreateVoucherInvoice",
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
    };
    var resetData = function(){
        Grid.clearForm($("#addNewVoucherWindowQuick"));
        $("#addNewVoucherWindowQuick").jqxValidator('hide');
    };
    return{
        init: init,
        open: open
    }
}());
$(document).ready(function () {
    quickCreateVoucherObj.init();
});