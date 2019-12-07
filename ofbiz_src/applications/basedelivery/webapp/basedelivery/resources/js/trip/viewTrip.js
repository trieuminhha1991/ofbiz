var actionName = "";
var changeReasonCBB;
var validatorVAL;
$(function () {
    OlbOrderDetailPage.init();
});

var OlbOrderDetailPage = (function () {
    var init = function () {
        initElement();
        initElementComplex();
        initEvent();
        initValidateForm();
    };
    var initElement = function () {
        jOlbUtil.windowPopup.create($("#confirmOrderChangeStatus"), {
            width: 540,
            height: 220,
            cancelButton: $("#alterConfirmCancel")
        });

        var configChangeReason = {
            placeHolder: uiLabelMap.OtherReason,
            key: 'enumId',
            value: 'descriptionSearch',
            width: '98%',
            dropDownHeight: 260,
            dropDownWidth: 500,
            autoDropDownHeight: false,
            displayDetail: true,
            dropDownHorizontalAlignment: 'right',
            autoComplete: true,
            searchMode: 'containsignorecase',
            renderer: null,
            renderSelectedItem: null,
            selectedIndex: 0
        };
        changeReasonCBB = new OlbComboBox($("#wcos_changeReason"), reasonCancel, configChangeReason, []);
    };


    var initElementComplex = function () {
        var configPartyGroup = {
            useUrl: true,
            root: 'results',
            widthButton: '100%',
            showdefaultloadelement: false,
            autoshowloadelement: false,
            datafields: [{name: 'partyId', type: 'string'}, {name: 'groupName', type: 'string'}],
            columns: [
                {text: uiLabelMap.BDPartyId, datafield: 'partyId', width: '30%'},
                {text: uiLabelMap.BDGroupName, datafield: 'groupName', width: '70%'},
            ],
            url: 'JQGetListPartyGroup',
            useUtilFunc: true,

            key: 'partyId',
            description: function (rowData) {
                if (rowData) {
                    var descriptionValue = rowData['groupName'];
                    return descriptionValue;
                }
            },
            autoCloseDropDown: true,
            filterable: true,
            sortable: true,
        };
        // new OlbDropDownButton($("#driverId"), $("#driverGrid"), null, configPartyGroup, [typeof(driverId) != 'undefined' ? driverId : '0']);

        var vehicleConfig = {
            useUrl: true,
            root: 'results',
            widthButton: '100%',
            showdefaultloadelement: false,
            autoshowloadelement: false,
            datafields: [{name: 'vehicleId', type: 'string'}, {
                name: 'licensePlate',
                type: 'string'
            }],
            columns: [
                {text: uiLabelMap.BDVehicleId, datafield: 'vehicleId', width: '30%'},
                {text: uiLabelMap.BDLicensePlate, datafield: 'licensePlate', width: '70%'},
            ],
            url: 'JQGetVehicleBySupplierId&supplierId=' + supplierId,
            useUtilFunc: true,
            // defaultValue: '1',
            key: 'vehicleId',
            description: function (rowData) {
                if (rowData) {
                    var descriptionValue = rowData['licensePlate'];
                    return descriptionValue;
                }
            },
            autoCloseDropDown: true,
            filterable: true,
            sortable: true
        };
        new OlbDropDownButton($("#vehicleId"), $("#vehicleGrid"), null, vehicleConfig, [typeof(vehicleId) != 'undefined' ? vehicleId : '0']);
    };

    var initEvent = function () {
        $("#confirmOrderChangeStatus").on('open', function (event) {
            $("body").css('overflow', 'hidden');
        });
        $("#confirmOrderChangeStatus").on('close', function (event) {
            $("body").css('overflow', 'inherit');
        });
        $("#tripApprove").click(function () {
            bootbox.dialog(uiLabelMap.BSAreYouSureYouWantToApprove,
                [{
                    "label" : uiLabelMap.CommonSubmit,
                    "class" : "btn-primary btn-small icon-ok open-sans",
                    "callback": function() {
                        verifyTrip();
                    }
                },
                    {
                        "label" : uiLabelMap.CommonCancel,
                        "class" : "btn-danger btn-small icon-remove open-sans",
                    }]
            );
        });

        $("#alterConfirmSave").on('click', function () {
            if ("CANCEL" == actionName) {
                if (!$('#confirmOrderChangeStatus').jqxValidator('validate')) return false;
                var selectedIndex = $("#wcos_changeReason").jqxComboBox('getSelectedIndex');
                if (selectedIndex > 0) {
                    var resultInput = $("#wcos_changeReason").jqxComboBox('getSelectedItem');
                    document.OrderCancel.changeReason.value = "" + resultInput.value;
                    document.OrderCancel.submit();
                } else {
                    var resultValue = $("#wcos_changeDescription").val();
                    document.OrderCancel.changeReason.value = "" + resultValue;
                    document.OrderCancel.submit();
                }
            }
        });
        $("#confirmOrderChangeStatus").on('close', function (event) {
            $("#wcos_changeReason").jqxComboBox("close");
        });
        $("#wcos_changeReason").on("change", function (event) {
            var args = event.args;
            if (args) {
                var item = args.item;
                if (item) {
                    var parentObj = $("#wcos_changeDescription").closest(".row-fluid");
                    if (parentObj) {
                        if (!/^\s*$/.test(item.value)) {
                            $(parentObj).hide();
                        } else {
                            $(parentObj).show();
                        }
                    }
                }
            }
        });
    };
    var initValidateForm = function () {
        new OlbValidator($("#confirmOrderChangeStatus"), null,
            [{
                input: '#wcos_changeReason',
                message: uiLabelMap.validFieldRequire,
                action: 'change',
                rule: function (input, commit) {
                    var index = $(input).jqxComboBox('getSelectedIndex');
                    if (index > 0) {
                        if (OlbElementUtil.isNotEmpty($(input).val())) {
                            return true;
                        }
                        return false;
                    } else {
                        return true;
                    }
                }
            },
                {
                    input: '#wcos_changeDescription',
                    message: uiLabelMap.validFieldRequire,
                    action: 'key-up',
                    rule: function (input, commit) {
                        var index = $("#wcos_changeReason").jqxComboBox('getSelectedIndex');
                        if (index > 0) {
                            return true;
                        } else {
                            if (OlbElementUtil.isNotEmpty($(input).val())) {
                                return true;
                            }
                            return false;
                        }
                    }
                }]
        );

        var mapRules = [
            {input: '#vehicleId', type: 'validObjectNotNull', objType: 'dropDownButton'},
            // {input: '#driverId', type: 'validObjectNotNull', objType: 'dropDownButton'},
            // {input: '#vehicleName', type: 'validInputNotNull', objType: 'input'}
        ];
        validatorVAL = new OlbValidator($('#tripInfo'), mapRules, null, {scroll: true});
    };
    var getValidator = function () {
        return validatorVAL;
    };

    var verifyTrip = function () {
        // if (!getValidator().validate()) return false;
        var vehicleId = jOlbUtil.getAttrDataValue('vehicleId');
        // var driverId = jOlbUtil.getAttrDataValue('driverId');
        // var vehicleName = $("#vehicleName").val();
        var statusId = "TRIP_CONFIRMED";
        var dataMap = {
            tripId: tripId,
            vehicleId: vehicleId,
            // driverId: driverId,
            // vehicleName: vehicleName,
            statusId: statusId
        };
        var urlCreateUpdateQuotation = "verifyTripAjax";
        $.ajax({
            type: 'POST',
            url: urlCreateUpdateQuotation,
            data: dataMap,
            beforeSend: function(){
                $("#loader_page_common").show();
            },
            success: function(data){
                jOlbUtil.processResultDataAjax(data, "default", function(data){
                    if (OlbCore.isNotEmpty(data.tripId)) {
                        window.location.href = "viewTrip?tripId=" + data.tripId;
                    }
                });
            },
            error: function(data){
                alert("Send request is error");
            },
            complete: function(data){
                $("#loader_page_common").hide();
            }
        });
    };

    return {
        init: init,
    }
}());
var changeOrderStatus = function (action) {
    if ("CANCEL" == action) {
        actionName = "CANCEL";
        $("#confirmOrderChangeStatus").jqxWindow('open');
    }
};