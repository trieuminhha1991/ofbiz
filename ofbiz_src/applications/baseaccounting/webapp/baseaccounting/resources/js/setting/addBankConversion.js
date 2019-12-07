$.jqx.theme = 'olbius';
theme = $.jqx.theme;
var action = (function () {
    var shippingAddressDDB;

    var configShippingAddress = {
        widthButton: '100%',
        dropDownHorizontalAlignment: 'right',
        datafields: [
            {name: 'contactMechId', type: 'string'},
            {name: 'toName', type: 'string'},
            {name: 'attnName', type: 'string'},
            {name: 'address1', type: 'string'},
            {name: 'city', type: 'string'},
            {name: 'stateProvinceGeoId', type: 'string'},
            {name: 'stateProvinceGeoName', type: 'string'},
            {name: 'postalCode', type: 'string'},
            {name: 'countryGeoId', type: 'string'},
            {name: 'countryGeoName', type: 'string'},
            {name: 'districtGeoId', type: 'string'},
            {name: 'districtGeoName', type: 'string'},
            {name: 'wardGeoId', type: 'string'},
            {name: 'wardGeoName', type: 'string'},
        ],
        columns: [
            {text: uiLabelMap.BSContactMechId, datafield: 'contactMechId', width: '20%'},
            {text: uiLabelMap.BSReceiverName, datafield: 'toName', width: '20%'},
            {text: uiLabelMap.BSOtherInfo, datafield: 'attnName', width: '20%'},
            {text: uiLabelMap.BSAddress, datafield: 'address1', width: '25%'},
            {text: uiLabelMap.BSWard, datafield: 'wardGeoName', width: '20%'},
            {text: uiLabelMap.BSCounty, datafield: 'districtGeoName', width: '20%'},
            {text: uiLabelMap.BSStateProvince, datafield: 'stateProvinceGeoName', width: '20%'},
            {text: uiLabelMap.BSCountry, datafield: 'countryGeoName', width: '20%'},
        ],
        useUrl: true,
        root: 'results',
        url: defaultPartyId != null ? 'JQGetShippingAddressByPartyReceive&partyId=' + defaultPartyId : '',
        useUtilFunc: true,
        selectedIndex: 0,
        key: 'contactMechId',
        description: ['toName', 'attnName', 'address1', 'wardGeoName', 'districtGeoName', 'city', 'countryGeoId'],
        autoCloseDropDown: false,
        contextMenu: "contextMenushippingContactMechGrid",
        filterable: true
    };

    var initElement = function () {
        $('#bankId').jqxInput({width: '250px', height: '25px'});
        $('#bankName').jqxInput({width: '250px', height: '25px'});
        $('#bankAddress').jqxInput({width: '250px', height: '25px', disabled: true});
        initjqxWindow();
        if (typeof OlbShippingAdrNewPopup !== undefined) OlbShippingAdrNewPopup.init();
        $($('.span12.form-window-content-custom').children()[0]).hide();
        $($('.span12.form-window-content-custom').children()[1]).hide();
        $($('.span12.form-window-content-custom').children()[7]).hide();
        $($('.span12.form-window-content-custom').children()[8]).hide();
        $('#alterpopupWindowContactMechNew').jqxWindow({
            title: uiLabelMap.BACCNewAddress ? uiLabelMap.BACCNewAddress : 'New Address',
            height: 300,
            position: {x: windowWidth / 3, y: 100}
        });
//			shippingAddressDDB = new OlbDropDownButton($("#shippingContactMechId"), $("#shippingContactMechGrid"), null, configShippingAddress, []);
    }
    var initjqxWindow = function () {
        $("#alterpopupWindow").jqxWindow({
            width: 500,
            height: 250,
            resizable: false,
            isModal: true,
            autoOpen: false,
            cancelButton: $("#cancel"),
            modalOpacity: 0.7,
            theme: theme
        });
    }
    var initRules = function () {
        $('#formAdd').jqxValidator({
            rules: [
                {
                    input: '#bankId',
                    message: (uiLabelMap.FieldRequiredAccounting ? uiLabelMap.FieldRequiredAccounting : ''),
                    action: 'change,close,blur',
                    rule: function (input) {
                        var val = input.val();
                        if (!val) return false;
                        return true;
                    }
                },
                {
                    input: '#bankId',
                    message: (uiLabelMap.BACCFieldContainsSpecialChar ? uiLabelMap.BACCFieldContainsSpecialChar : ''),
                    action: 'change,close,blur',
                    rule: function (input) {
                        var val = input.val();
                        if (!/^[a-zA-Z0-9- ]*$/.test(val)) return false;
                        return true;
                    }
                },
                {
                    input: '#bankName',
                    message: (uiLabelMap.FieldRequiredAccounting ? uiLabelMap.FieldRequiredAccounting : ''),
                    action: 'change,close,blur',
                    rule: function (input) {
                        var val = input.val();
                        if (!val) return false;
                        return true;
                    }
                }, {
                    input: '#bankAddress',
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
        Grid.clearForm($('#formAdd'));
        setTimeout(function () {
            $('#formAdd').jqxValidator('hide');
        }, 200)
    };

    var bindEvent = function () {
        $("#save").click(function () {
            if (!$('#formAdd').jqxValidator('validate')) {
                return false;
            } else {
                createBankConversion();
                return true;
            }
        });

        $('#bankName').change(function () {
            if (localStorage.bankName) localStorage.removeItem('bankName');
            localStorage.bankName = $('#bankName').val();
        })

        $('#bankId').change(function () {
            if (localStorage.bankId) localStorage.removeItem('bankId');
            localStorage.bankId = $('#bankId').val();
        });


        $("#saveAndContinue").click(function () {
            createBankConversion();
        });
        $("#alterpopupWindow").on('close', function () {
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
                $('#bankAddress').val(description);
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
            $('#bankAddress').val(description);
        var data = {
            bankId: $('#bankId').val(),
            bankName: $('#bankName').val(),
            bankAddress: $('#bankAddress').val(),
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

    var createBankConversion = function () {
        var data = getData();
        var request = $.ajax({
            url: 'createBankConversion',
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
                $("#alterpopupWindow").jqxWindow('close');
                $("#jqxgrid").jqxGrid('updatebounddata');
            }
        })
    };

    var runAjaxCreatePostalAddress = function (data) {
        $.ajax({
            type: "POST",
            url: "createPostalAddressShippingForParty",
            data: data,
            beforeSend: function () {
                $("#loader_page_common").show();
            },
            success: function (data) {
                $('#jqxgrid').jqxGrid('updatebounddata');
                Grid.renderMessage('jqxgrid', uiLabelMap.wgaddsuccess, {template: 'success'})
            },
            error: function () {
                alert("Send to server is false!");
            },
            complete: function () {
                $("#loader_page_common").hide();
            }
        });
    }

    var createAddress = function () {
        $("#alterpopupWindowContactMechNew").jqxWindow('open');
    }

    var processWhenCreateBankConversion = function (response) {
//			if(typeof response !== undefined && response.hasOwnProperty('partyId')){
//				
//			}
    }

    return {
        init: function () {
            initElement();
            bindEvent();
            initRules();
        },
        createAddress: createAddress,
        processWhenCreateBankConversion: processWhenCreateBankConversion
    }
}())

$(document).ready(function () {
    action.init();
});
    
