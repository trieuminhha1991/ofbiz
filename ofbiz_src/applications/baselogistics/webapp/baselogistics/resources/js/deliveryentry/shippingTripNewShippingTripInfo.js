$(function () {
    DlvEntryInfoObj.init();
});
var DlvEntryInfoObj = (function () {
    var validatorVAL;

    var init = function () {
        initInputs();
        initElementComplex();
        initEvents();
        initValidateForm();
    };
    var initInputs = function () {
        $("#fromDate").jqxDateTimeInput({width: 300, theme: theme, height: '24px', formatString: 'dd/MM/yyyy HH:mm'});
        $("#fromDate").jqxDateTimeInput('clear');
        $("#thruDate").jqxDateTimeInput({width: 300, theme: theme, height: '24px', formatString: 'dd/MM/yyyy HH:mm'});
        $("#thruDate").jqxDateTimeInput('clear');
        $("#driverPartyId").jqxDropDownList({
            placeHolder: uiLabelMap.PleaseSelectTitle,
            width: 300,
            theme: theme,
            source: driverPartyData,
            valueMember: 'partyId',
            displayMember: 'description',
            height: '24px',
            dropDownHeight: 200
        });
        $("#facilityId").jqxDropDownList({
            placeHolder: uiLabelMap.PleaseSelectTitle,
            width: 300,
            selectedIndex: 0,
            theme: theme,
            source: faciData,
            valueMember: 'facilityId',
            displayMember: 'description',
            height: '24px',
            dropDownHeight: 200
        });

        var contactMechData = [];
        $("#contactMechId").jqxDropDownList({
            placeHolder: uiLabelMap.PleaseSelectTitle,
            width: 300,
            source: contactMechData,
            autoDropDownHeight: true,
            displayMember: "description",
            selectedIndex: 0,
            valueMember: "contactMechId"
        });
        $("#shipCost").jqxNumberInput({width: 295, height: 25, spinButtons: true});
        $("#shipReturnCost").jqxNumberInput({width: 295, height: 25, spinButtons: true});
        $("#description").jqxInput({height: 40,width: 300});
    };
    var initElementComplex = function () {
    };
    var initEvents = function () {
        if ($("#facilityId").val() != null && $("#facilityId").val() != undefined && $("#facilityId").val() != "") {
            update({
                facilityId: $("#facilityId").val(),
                contactMechPurposeTypeId: "SHIP_ORIG_LOCATION",
            }, 'getFacilityContactMechs', 'listFacilityContactMechs', 'contactMechId', 'address1', 'contactMechId');
        }
        $("#facilityId").on('change', function (event) {
            update({
                facilityId: $("#facilityId").val(),
                contactMechPurposeTypeId: "SHIP_ORIG_LOCATION",
            }, 'getFacilityContactMechs', 'listFacilityContactMechs', 'contactMechId', 'address1', 'contactMechId');
        });
        $("#driverPartyId").on('change', function (event) {
            updateOrderGridData($("#driverPartyId").val());
        });
    };

    var totalWeight = 0;

    var initValidateForm = function () {

        var extendRules = [
            {
                input: '#thruDate', message: uiLabelMap.CannotBeforeNow, action: 'change', position: 'topcenter',
                rule: function (input, commit) {
                    var value = $('#thruDate').jqxDateTimeInput('getDate');
                    var nowDate = new Date();
                    if (value < nowDate) {
                        return false;
                    }
                    return true;
                }
            },
            {
                input: '#thruDate',
                message: uiLabelMap.BLTimeDistanceNotValid,
                action: 'change',
                position: 'topcenter',
                rule: function (input, commit) {
                    var value1 = $('#fromDate').jqxDateTimeInput('getDate');
                    var value2 = $('#thruDate').jqxDateTimeInput('getDate');
                    if (value2 && value1 && value2 < value1) {
                        return false;
                    }
                    return true;
                }
            },
            {
                input: '#fromDate', message: uiLabelMap.CannotBeforeNow, action: 'change', position: 'topcenter',
                rule: function (input, commit) {
                    var value = $('#fromDate').jqxDateTimeInput('getDate');
                    var nowDate = new Date();
                    if (value < nowDate) {
                        return false;
                    }
                    return true;
                }
            },
            {
                input: '#shipCost',
                message: uiLabelMap.CostMustBePositiveNumbers,
                action: 'keyup, blur',
                position: 'topcenter',
                rule: function (input) {
                    var shipCostTmp = $("#shipCost").val();
                    if(shipCostTmp<0) return false;
                    return true;
                }
            },
            {
                input: '#shipReturnCost',
                message: uiLabelMap.CostMustBePositiveNumbers,
                action: 'keyup, blur',
                position: 'topcenter',
                rule: function (input) {
                    var shipCostTmp = $("#shipReturnCost").val();
                    if(shipCostTmp<0) return false;
                    return true;
                }
            },

         ];
        var mapRules = [
            {input: '#facilityId', type: 'validInputNotNull'},
            {input: '#contactMechId', type: 'validInputNotNull'},
            {input: '#shipCost', type: 'validInputNotNull'},
            {input: '#fromDate', type: 'validInputNotNull'},
            {input: '#thruDate', type: 'validInputNotNull'},
        ];
        validatorVAL = new OlbValidator($('#initDeliveryEntry'), mapRules, extendRules, {position: 'topcenter'});
    };


    function renderHtml(data, key, value, id) {
        var y = "";
        var source = new Array();
        var index = 0;
        for (var x in data) {
            index = source.length;
            var row = {};
            row[key] = data[x][key];
            row['description'] = data[x][value];
            source[index] = row;
        }
        if ($("#" + id).length) {
            $("#" + id).jqxDropDownList('clear');
            $("#" + id).jqxDropDownList({source: source, selectedIndex: 0});
        }
    }

    function update(jsonObject, url, data, key, value, id) {
        jQuery.ajax({
            url: url,
            type: "POST",
            data: jsonObject,
            async: false,
            success: function (res) {
                var json = res[data];
                renderHtml(json, key, value, id);
            }
        });
    }

    function updateOrderGridData(shipperId) {
        var element = $("#jqxGridOrder");
        var facId = null;
        var tmpS = element.jqxGrid('source');
        if (tmpS) {
            var curUrl = tmpS._source.url;
            var newUrl = "jqxGeneralServicer?sname=JQGetListOrderInArea&isIn=Y&shipperId=" + shipperId;
            if (newUrl != curUrl) {
                OlbOrder.updateGridOrderSource(newUrl);
                /*tmpS._source.url = newUrl;
                element.jqxGrid('source', tmpS);*/
            }
        }
    }

    var getValidator = function () {
        return validatorVAL;
    }
    return {
        init: init,
        getShipmentUrl: getShipmentUrl,
        getValidator: getValidator,
    }
}());