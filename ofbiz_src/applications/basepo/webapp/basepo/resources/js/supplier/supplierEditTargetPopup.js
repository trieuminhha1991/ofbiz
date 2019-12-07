$(function() {
    supplierEditTarget.init();
});

var supplierEditTarget = (function() {
    var dataEdit = {};
    var validatorVAL;
    var init = function() {
        initElement();
        initEvent();
        initValidateForm();
    };
    var initElement = function() {
        $("#alterpopupWindowEdit").jqxWindow({
            width : 700,
            height : 240,
            resizable : false,
            isModal : true,
            autoOpen : false,
            cancelButton : $("#alterCancel"),
            modalOpacity : 0.7,
            theme : theme
        });
    };

    var initEvent = function() {
        $("#alterSaveEdit").on(
            "click",
            function() {
                if (validatorVAL.validate()) {
                    var row = {
                        partyId : dataEdit.partyId,
                        productId : dataEdit.productId,
                        quantityUomId : dataEdit.quantityUomId,
                        quantity : $("#quantityEdit").val(),
                        fromDate : $("#fromDateDivEdit").jqxDateTimeInput('getDate').getTime(),
                        thruDate : $("#thruDateDivEdit").jqxDateTimeInput('getDate').getTime()
                    };
                    $.ajax({
                        type: 'POST',
                        url: 'updateSupplierTarget',
                        data: row,
                        success: function(data){
                            jOlbUtil.processResultDataAjax(data, function(data, errorMessage){
                                $("#container").empty();
                                $("#jqxNotification").jqxNotification({ template: "error"});
                                $("#jqxNotification").html(errorMessage);
                                $("#jqxNotification").jqxNotification("open");
                                return false;
                            },function () {
                                $("#jqxNotification").jqxNotification({ template: "info"});
                                $("#jqxNotification").text(uiLabelMap.updateSuccess);
                                $("#jqxNotification").jqxNotification("open");
                                $("#jqxgridSupplierTargets").jqxGrid("updatebounddata");
                            });
                        },
                        error: function () {
                            alert("Send request is error");
                        }
                    });
                    $("#alterpopupWindowEdit").jqxWindow("close");
                }
            });
        $("#alterCancelEdit").on(
            "click",
            function() {
                $("#alterpopupWindowEdit").jqxWindow("close");
            });

        $("#alterpopupWindowEdit").on("open", function() {
            $("#quantity").val(1);
        });

        $("#alterpopupWindowEdit").on("close", function() {
            $("#alterpopupWindowEdit").jqxValidator("hide");
        });
    };

    var openWindow = function (data) {
        $("#alterpopupWindowEdit").jqxWindow("open");
        dataEdit = data;
        setData(data);
    };

    var setData = function (data) {
        $("#supplierIdEdit").html(data.groupName);
        $("#productNameEdit").html(data.productName);
        var uom = data.quantityUomId;
        if(OlbCore.isNotEmpty(uomData)){
            for(var i =0; i <uomData.length; i++){
                if(uomData[i].uomId == data.quantityUomId){
                    uom = uomData[i].description;
                    break;
                }
            }
        }
        $("#quantityUomIdEdit").html(uom);
        $("#quantityEdit").jqxNumberInput({
            inputMode : "simple",
            spinMode : "simple",
            groupSeparator : ".",
            value: data.quantity,
            min : 1,
            width : 218,
            decimalDigits : 0
        });
        $("#fromDateDivEdit").jqxDateTimeInput({height: '25px',width: 218, formatString: 'dd-MM-yyyy HH:mm:ss', allowNullDate: false, value: data.fromDate, disabled:true});
        $("#thruDateDivEdit").jqxDateTimeInput({height: '25px',width: 218, formatString: 'dd-MM-yyyy HH:mm:ss', allowNullDate: true, value: data.thruDate});

    };

    var initValidateForm = function() {
        var extendRules = [
            {
                input : "#quantityEdit",
                message : uiLabelMap.DAQuantityMustBeGreaterThanZero,
                action : "valueChanged",
                rule : function(input, commit) {
                    var value = input.val();
                    if (value <= 0)
                        return false;
                    return true;
                }
            },

        ];
        var mapRules = [ {
            input : "#fromDateDivEdit",
            type : "validDateTimeInputNotNull"
        }, {
            input : "#thruDateDivEdit",
            type : "validDateCompareToday"
        }, {
            input : "#fromDateDivEdit, #thruDateDivEdit",
            type : "validCompareTwoDate",
            paramId1 : "fromDateDivEdit",
            paramId2 : "thruDateDivEdit"
        }, ];
        validatorVAL = new OlbValidator($("#alterpopupWindowEdit"), mapRules,
            extendRules, {
                position : "bottom"
            });
    };

    return {
        init : init,
        initValidateForm : initValidateForm,
        openWindow: openWindow,
    };
}());