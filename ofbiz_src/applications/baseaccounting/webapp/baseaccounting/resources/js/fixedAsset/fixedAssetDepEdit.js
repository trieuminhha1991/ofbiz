/**
 * fixedAssetStep1 variable is defined in fixedAssetNewStep1.js file
 * fixedAssetStep2 variable is defined in fixedAssetNewStep2.js file
 * fixedAssetStep3 variable is defined in fixedAssetNewStep3.js file
 */

var fixedAssetDepObj = (function () {
    var init = function () {
        initInput();
        initElement();
        initDropDown();
        initValidator();
        initEvent();
        initDefaultValue();
    };
    var initInput = function(){
        var decimalSeparator = ',';
        var groupSeparator = '.';
        $('#lifeDepAmount').jqxNumberInput({digits: 12, max: 100000000000000, min: 0, decimalSeparator: decimalSeparator, groupSeparator: groupSeparator, width: '97%', spinButtons: true });
        $('#usedQuantity').jqxNumberInput({digits: 12, decimalSeparator: decimalSeparator, min: 0,  groupSeparator: groupSeparator, width: '65%', spinButtons: true });
        $('#yearlyDepRate').jqxNumberInput({digits: 12, disabled: true, min: 0,  inputMode: 'simple', decimalSeparator: decimalSeparator, groupSeparator: groupSeparator, width: '97%', spinButtons: true });
        $('#monthlyDepRate').jqxNumberInput({digits: 12, inputMode: 'simple', min: 0,  width: '97%', decimalSeparator: decimalSeparator, groupSeparator: groupSeparator, spinButtons: true });
        $('#annualDepAmount').jqxNumberInput({digits: 12, disabled: true, max: 100000000000000, min: 0, decimalSeparator: decimalSeparator, groupSeparator: groupSeparator, width: '97%', spinButtons: true });
        $('#monthlyDepAmount').jqxNumberInput({digits: 12, decimalSeparator: decimalSeparator, min: 0, max: 100000000000000, groupSeparator: groupSeparator, width: '97%', spinButtons: true });
        $('#accumulatedDep').jqxNumberInput({digits: 12, decimalSeparator: decimalSeparator, min: 0, max: 100000000000000, groupSeparator: groupSeparator, width: '97%', spinButtons: true });
        $('#remainingValue').jqxNumberInput({digits: 12, disabled: true, min: 0, max: 100000000000000, decimalSeparator: decimalSeparator, groupSeparator: groupSeparator, width: '97%',spinButtons: true });
        $('#purchaseCost').jqxNumberInput({digits: 12, max: 100000000000000, min: 0,
            decimalSeparator: ',', groupSeparator: '.', width: '97%', spinButtons: true });
    };

    var initDefaultValue = function() {
        if(lifeDepAmount) $('#lifeDepAmount').val(lifeDepAmount);
        if(yearlyDepRate) $('#yearlyDepRate').val(yearlyDepRate);
        if(usedQuantity) $('#usedQuantity').val(usedQuantity);
        if(monthlyDepRate) $('#monthlyDepRate').val(monthlyDepRate);
        if(annualDepAmount) $('#annualDepAmount').val(annualDepAmount);
        if(monthlyDepAmount) $('#monthlyDepAmount').val(monthlyDepAmount);
        if(accumulatedDep) $('#accumulatedDep').val(accumulatedDep);
        if(remainingValue) $('#remainingValue').val(remainingValue);
        if(purchaseCost) $('#purchaseCost').val(purchaseCost);
    };

    var initDropDown = function () {
        accutils.createJqxDropDownList($("#usedPeriod"), periodData, {placeHolder: uiLabelMap.filterchoosestring, width: '30%',
            height: '25px', valueMember: 'periodId', displayMember: 'description', selectedIndex: 0});
    };

    var initElement = function () {
        $("#fixedAssetDepEditPopupWindow").jqxWindow({
            maxWidth: 900,
            minWidth: 830,
            height: 350,
            minHeight: 300,
            maxHeight: 1200,
            resizable: true,
            isModal: true,
            autoOpen: false,
            cancelButton: $("#addButtonCancel2"),
            modalOpacity: 0.7,
            theme: 'olbius'
        });
    };

    var initEvent = function () {
        $('#fixedAssetDepEditPopupWindow').on('close', function () {
            $('#fixedAssetDepEditPopupWindow').jqxValidator('hide');
        });

        $('#usedQuantity').on('valueChanged', function (event) {
            var value = event.args.value;
            var depreciation = $('#lifeDepAmount').val();
            calculateDepreciation(depreciation, value, $("#usedPeriod").val());
        });
        $('#usedPeriod').on('change', function (event) {
            var args = event.args;
            if (args) {
                var item = args.item;
                var value = $('#usedQuantity').val();
                var depreciation = $('#lifeDepAmount').val();
                calculateDepreciation(depreciation, value, item.value);
            }
        });
        $('#lifeDepAmount').on('valueChanged', function (event) {
            var value = $('#usedQuantity').val();
            var depreciation = $('#lifeDepAmount').val();
            calculateDepreciation(depreciation, value, $("#usedPeriod").val());
        });
        $('#purchaseCost').on('valueChanged', function (event) {
            var value = event.args.value;
            $('#lifeDepAmount').jqxNumberInput('setDecimal', value);
        });

        $("#addButtonSave2").click(function () {
            var validate = $('#fixedAssetDepEditPopupWindow').jqxValidator('validate');
            if (validate != false) {
                bootbox.dialog(uiLabelMap.BPOAreYouSureYouWantSave,
                    [{
                        "label": uiLabelMap.wgcancel,
                        "icon": 'fa fa-remove',
                        "class": 'btn  btn-danger form-action-button pull-right',
                        "callback": function () {
                            bootbox.hideAll();
                        }
                    },
                        {
                            "label": uiLabelMap.wgok,
                            "icon": 'fa-check',
                            "class": 'btn btn-primary form-action-button pull-right',
                            "callback": function () {
                                updateFixedAssetDep();
                            }
                        }]);
            }
        });
    };

    var calculateDepreciation = function(depreciation, usedQuantity, period){
        $('#remainingValue').jqxNumberInput('setDecimal', depreciation);
        if(usedQuantity == 0){
            $('#monthlyDepRate').val(0);
            $('#yearlyDepRate').val(0);
            $('#annualDepAmount').val(0);
            $('#monthlyDepAmount').val(0);
            return;
        }
        if(period == 'MONTH'){
            $('#monthlyDepRate').jqxNumberInput('setDecimal', (100/usedQuantity).toFixed(2));
            $('#yearlyDepRate').jqxNumberInput('setDecimal', (100/usedQuantity*12).toFixed(2));
            $('#annualDepAmount').jqxNumberInput('setDecimal', (depreciation/usedQuantity*12).toFixed(2));
            $('#monthlyDepAmount').jqxNumberInput('setDecimal', (depreciation/usedQuantity).toFixed(2));
        }else if(period == 'YEAR'){
            $('#monthlyDepRate').jqxNumberInput('setDecimal', (100/usedQuantity/12).toFixed(2));
            $('#yearlyDepRate').jqxNumberInput('setDecimal', (100/usedQuantity).toFixed(2));
            $('#annualDepAmount').jqxNumberInput('setDecimal', (depreciation/usedQuantity).toFixed(2));
            $('#monthlyDepAmount').jqxNumberInput('setDecimal', (depreciation/usedQuantity/12).toFixed(2));
        }
    };

    var initValidator = function(){
        $('#fixedAssetDepEditPopupWindow').jqxValidator({
            position: 'bottom',
            rules: [
                { input: '#usedPeriod', message: uiLabelMap.FieldRequired, action: 'keyup, change, close',
                    rule: function (input, commit) {
                        if(input.val()){
                            return true;
                        }else{
                            return false;
                        }
                    }
                },
                { input: '#usedQuantity', message: uiLabelMap.ValueMustBeGreateThanZero, action: 'keyup, change',
                    rule: function (input, commit) {
                        if(input.val() <= 0){
                            return false;
                        }
                        return true;
                    }
                },
                { input: '#purchaseCost', message: uiLabelMap.ValueMustBeGreateThanZero, action: 'keyup, change, close',
                    rule: function (input, commit) {
                        if(input.val() <= 0){
                            return false;
                        }
                        return true;
                    }
                },
            ]
        });
    };

    var updateFixedAssetDep = function () {
        var lifeDepAmount = $('#lifeDepAmount').val();
        var usefulLives =  $('#usedQuantity').val();
        var yearlyDepRate = $('#yearlyDepRate').val();
        var monthlyDepRate = $('#monthlyDepRate').val();
        var monthlyDepAmount = $('#monthlyDepAmount').val();
        var accumulatedDep = $('#accumulatedDep').val();
        var remainingValue = $('#remainingValue').val();
        var purchaseCost = $('#purchaseCost').val();

        var usedPeriod = $('#usedPeriod').val();
        if(usedPeriod == "MONTH"){
            usefulLives =  $('#usedQuantity').val();
        }
        else {
            usefulLives =  $('#usedQuantity').val()*12;
        }
        var dataMap = {
            fixedAssetId: fixedAssetId,
            lifeDepAmount: lifeDepAmount,
            yearlyDepRate: yearlyDepRate,
            monthlyDepRate: monthlyDepRate,
            monthlyDepAmount: monthlyDepAmount,
            accumulatedDep: accumulatedDep,
            remainingValue: remainingValue,
            purchaseCost: purchaseCost,
            usefulLives : usefulLives
        };
        $.ajax({
            url: "updateFixedAssetAndDepInfo",
            type: "POST",
            data: dataMap,
            dataType: "json",
            success: function(data) {
                if(!data._ERROR_MESSAGE_ && !data._ERROR_MESSAGE_LIST_){
                    $("#notificationAddSuccess").text(uiLabelMap.NotifiUpdateSucess);
                    $('#fixedAssetDepEditPopupWindow').jqxWindow('close');
                    $("#jqxNotificationAddSuccess").jqxNotification('open');

                    window.location.href = 'ViewFixedAsset?fixedAssetId=' + fixedAssetId;
                    // $("#fixedAssetNameView").val(fixedAssetName);
                    // $('#fixedAssetTypeView').val(fixedAssetTypeView);
                    // $('#fixedAssetCostGlAccView').val(fixedAssetCostGlAccView);
                    // $('#fixedAssetDepGlAccView').val(fixedAssetDepGlAccView);
                    // $('#fixedAssetDateAcquiredView').val(accutils.getTimestamp(dateAcquiredTmp));
                    // $('#fixedAssetDatePurchaseView').val(accutils.getTimestamp(datePurchaseTmp));

                }else if(data._ERROR_MESSAGE_){
                    Loading.hide('loadingMacro');
                    bootbox.dialog(data._ERROR_MESSAGE_,
                        [{
                            "label" : uiLabelMap.CommonClose,
                            "class" : "btn-danger btn-small icon-remove open-sans",
                        }]
                    );
                }else if(data._ERROR_MESSAGE_LIST_){
                    Loading.hide('loadingMacro');
                    bootbox.dialog(data._ERROR_MESSAGE_LIST_[0],
                        [{
                            "label" : uiLabelMap.CommonClose,
                            "class" : "btn-danger btn-small icon-remove open-sans",
                        }]
                    );
                }
            }
        });
    };
    return {
        init: init
    }
}());
$(document).ready(function () {
    $.jqx.theme = 'olbius';
    fixedAssetDepObj.init()
});