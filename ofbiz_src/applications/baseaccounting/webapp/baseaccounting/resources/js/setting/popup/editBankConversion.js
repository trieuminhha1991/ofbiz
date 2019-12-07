$.jqx.theme = 'olbius';
theme = $.jqx.theme;
$(function () {
    EditBank.init();
});
var EditBank = (function () {
    var init = function () {
        initElement();
        bindEvent();
        initRules();
    };
    var initData = function(data) {
        $('#bankIdEdit').val(data.bankId);
        $('#bankNameEdit').val(data.bankName);
        $('#bankAddressEdit').val(data.bankAddress);
    };
    var initElement = function (data) {
        $('#bankIdEdit').jqxInput({width: '250px', height: '25px', disabled: true});
        $('#bankNameEdit').jqxInput({width: '250px', height: '25px'});
        $('#bankAddressEdit').jqxInput({width: '250px', height: '25px', disabled: true});
        initjqxWindow();
        $($('.span12.form-window-content-custom').children()[0]).hide();
        $($('.span12.form-window-content-custom').children()[1]).hide();
        $($('.span12.form-window-content-custom').children()[7]).hide();
        $($('.span12.form-window-content-custom').children()[8]).hide();
    };

    var initjqxWindow = function () {
        $("#editPopupWindow").jqxWindow({
            width: 500,
            height: 250,
            resizable: false,
            isModal: true,
            autoOpen: false,
            cancelButton: $("#cancelEdit"),
            modalOpacity: 0.7,
            theme: theme
        });
    };
    var initRules = function () {
        $('#formEdit').jqxValidator({
            rules: [
                {
                    input: '#bankIdEdit',
                    message: (uiLabelMap.FieldRequiredAccounting ? uiLabelMap.FieldRequiredAccounting : ''),
                    action: 'change,close,blur',
                    rule: function (input) {
                        var val = input.val();
                        if (!val) return false;
                        return true;
                    }
                },
                {
                    input: '#bankIdEdit',
                    message: (uiLabelMap.BACCFieldContainsSpecialChar ? uiLabelMap.BACCFieldContainsSpecialChar : ''),
                    action: 'change,close,blur',
                    rule: function (input) {
                        var val = input.val();
                        if (!/^[a-zA-Z0-9- ]*$/.test(val)) return false;
                        return true;
                    }
                },
                {
                    input: '#bankNameEdit',
                    message: (uiLabelMap.FieldRequiredAccounting ? uiLabelMap.FieldRequiredAccounting : ''),
                    action: 'change,close,blur',
                    rule: function (input) {
                        var val = input.val();
                        if (!val) return false;
                        return true;
                    }
                }, {
                    input: '#bankAddressEdit',
                    message: (uiLabelMap.FieldRequiredAccounting ? uiLabelMap.FieldRequiredAccounting : ''),
                    action: 'change,close,blur',
                    rule: function (input) {
                        var val = input.val();
                        if (!val) return false;
                        return true;
                    }
                }
            ]
        })
    };

    var clear = function () {
        Grid.clearForm($('#formEdit'));
        setTimeout(function () {
            $('#formEdit').jqxValidator('hide');
        }, 200)
    };

    var bindEvent = function () {
        $("#saveEdit").click(function () {
            if (!$('#formEdit').jqxValidator('validate')) {
                return false;
            } else {
                EditBankConversion();
                return true;
            }
        });

        $("#editPopupWindow").on('close', function () {
            clear();
        });

        $('#alterSave').unbind('click');

        $('#alterSave').on('click', function () {
            if (!$("#alterpopupWindowContactMechNew").jqxValidator('validate')) return false;
            var stateProvince = $('#wn_stateProvinceGeoId').jqxComboBox('getSelectedItem');
            var wn_countryGeoId = $('#wn_countryGeoId').jqxComboBox('getSelectedItem');
            var wn_countyGeoId = $('#wn_countyGeoId').jqxComboBox('getSelectedItem');
            var wn_wardGeoId = $('#wn_wardGeoId').jqxComboBox('getSelectedItem');
            var description = $('#wn_address1').val() + ' - ' + (wn_wardGeoId ? wn_wardGeoId.label : '') + '-' + (wn_countyGeoId ? wn_countyGeoId.label : '') + '-' + (stateProvince ? stateProvince.label : '') + ' - ' + (wn_countryGeoId ? wn_countryGeoId.label : '' );
            if (description)
                $('#bankAddressEdit').val(description);
            $("#alterpopupWindowContactMechNew").jqxWindow('close');
        });
    };

    var getData = function() {
        var stateProvince = $('#wn_stateProvinceGeoId').jqxComboBox('getSelectedItem');
        var wn_countryGeoId = $('#wn_countryGeoId').jqxComboBox('getSelectedItem');
        var wn_countyGeoId = $('#wn_countyGeoId').jqxComboBox('getSelectedItem');
        var wn_wardGeoId = $('#wn_wardGeoId').jqxComboBox('getSelectedItem');
        var description = $('#wn_address1').val() + ' - ' + (wn_wardGeoId ? wn_wardGeoId.label : '') + '-' + (wn_countyGeoId ? wn_countyGeoId.label : '') + '-' + (stateProvince ? stateProvince.label : '') + ' - ' + (wn_countryGeoId ? wn_countryGeoId.label : '' );
        if (description)
            $('#bankAddressEdit').val(description);
        var data = {
            bankId: $('#bankIdEdit').val(),
            bankName: $('#bankNameEdit').val(),
            bankAddress: $('#bankAddressEdit').val(),
            contactMechTypeId: 'POSTAL_ADDRESS',
            contactMechPurposeTypeId: 'PRIMARY_LOCATION',
            countryGeoId: wn_countryGeoId ? wn_countryGeoId.value : null,
            stateProvinceGeoId: stateProvince ? stateProvince.value : null,
            countyGeoId: wn_countyGeoId ? wn_countyGeoId.value : null,
            wardGeoId: wn_wardGeoId ? wn_wardGeoId.value : null,
            address1: $('#wn_address1').val() ? $('#wn_address1').val() : null
        };
        return data;
    };

    var EditBankConversion = function () {
        var data = getData();
        var request = $.ajax({
            url: 'updateBankConversion',
            data: {
                bankId: data.bankId,
                bankName: data.bankName,
                bankAddress: data.bankAddress
            },
            type: 'POST'
        });
        request.done(function (res) {
            if (res._ERROR_MESSAGE_ || res._ERROR_MESSAGE_LIST_) {
                Grid.renderMessage('jqxgrid', uiLabelMap.BACCBankExisted, {template: 'error'});
            }
            else {
                $("#editPopupWindow").jqxWindow('close');
                $("#jqxgrid").jqxGrid('updatebounddata');
            }
        })
    };

    var createAddress = function () {
        $("#alterpopupWindowContactMechNew").jqxWindow('open');
    };

    return {
        init: init,
        initData: initData,
        createAddress: createAddress
    }
}());

