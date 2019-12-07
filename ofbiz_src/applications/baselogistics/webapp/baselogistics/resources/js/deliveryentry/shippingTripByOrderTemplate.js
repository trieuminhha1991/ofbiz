$(function () {
    DlvEntryTemplateObj.init();
});
var DlvEntryTemplateObj = (function () {
    var listShipmentItemFinish = new Array();
    var init = function () {
        initInputs();
        initElementComplex();
        initEvents();
        initValidateForm();
    };
    var initInputs = function () {
    };
    var initElementComplex = function () {
    };

    function updateOrderNotInGridData(shipperId) {
        var element = $("#jqxGridOrderNotIn");
        var facId = null;
        var tmpS = element.jqxGrid('source');
        if (tmpS) {
            var curUrl = tmpS._source.url;
            var newUrl = "jqxGeneralServicer?sname=JQGetListOrderInArea&isIn=N&shipperId=" + shipperId;
            if (newUrl != curUrl) {
                OlbOrder.updateGridOrderNotInSource(newUrl);
            }
        }
    }

    var initEvents = function () {
        $('#fuelux-wizard').ace_wizard().on('change', function (e, info) {
            if (info.step == 1 && (info.direction == "next")) {
                updateOrderNotInGridData($('#driverPartyId').jqxDropDownList('val'));
                // check form valid
                $('#containerNotify').empty();
                var resultValidate = !DlvEntryInfoObj.getValidator().validate();
                if (resultValidate) return false;
            } else if (info.step == 2 && (info.direction == "next")) {
                //updateOrderNotInGridData($('#driverPartyId').jqxDropDownList('val'));
                // check form valid
                $('#containerNotify').empty();
                var resultValidate = !DlvEntryInfoObj.getValidator().validate();
                if (resultValidate) return false;
                var selectedIndexs = $('#jqxGridOrder').jqxGrid('getselectedrowindexes');

                if (OlbOrder.getListSelectedOrder().length <= 0 && OlbOrder.getListSelectedOrderNotIn().length <= 0) {
                    jOlbUtil.alert.error(uiLabelMap.DAYouNotYetChooseOrder);
                    return false;
                }
                showConfirmPage(OlbOrder.getListSelectedOrder().concat(OlbOrder.getListSelectedOrderNotIn()));
            }
        }).on('finished', function (e) {
            jOlbUtil.confirm.dialog(uiLabelMap.AreYouSureCreate, function () {
                Loading.show('loadingMacro');
                setTimeout(function () {
                    finishCreateDeliveryEntry(OlbOrder.getListSelectedOrder().concat(OlbOrder.getListSelectedOrderNotIn()));
                    Loading.hide('loadingMacro');
                }, 500);
            });
        }).on('stepclick', function (e) {
            //prevent clicking on steps
        });
    };

    function showConfirmPage(listData) {
        for (var i = 0; i < faciData.length; i++) {
            if ($("#facilityId").jqxDropDownList('val') == faciData[i].facilityId) {
                $("#facilityIdDT").text(faciData[i].description);
            }
        }

        var contactMechDescription = $("#contactMechId").jqxDropDownList('val');
        $.ajax({
            type: 'POST',
            url: 'getDetailPostalAddress',
            async: false,
            data: {
                contactMechId: $('#contactMechId').jqxDropDownList('val'),
            },
            success: function (data) {
                contactMechDescription = data.fullName;
                $("#contactMechIdDT").text(contactMechDescription);
            },
        });

        $("#fromDateDT").text($("#fromDate").val());
        $("#thruDateDT").text($("#thruDate").val());
        $("#shipCostDT").text(formatnumber(parseFloat($("#shipCost").val())));
        $("#shipReturnCostDT").text(formatnumber(parseFloat($("#shipReturnCost").val())));
        $("#descriptionDT").text($("#description").val());


        if ($("#driverPartyId").jqxDropDownList('disabled') == true) {
            $("#driverPartyIdDT").text(carrierName);
        } else {
            for (var i = 0; i < driverPartyData.length; i++) {
                if (driverPartyData[i].partyId == $("#driverPartyId").jqxDropDownList('val')) {
                    $("#driverPartyIdDT").text(unescapeHTML(driverPartyData[i].description));
                }
            }
        }

        if ($("#tableOrder").length > 0) {
            var totalValue = 0;
            $('#tableOrder tbody').empty();
            var tableRef = document.getElementById('tableOrder').getElementsByTagName('tbody')[0];
            var sequenceNumber = 0;
            for (var i in listData) {
                sequenceNumber++;
                var data = listData[i];
                var newRow = tableRef.insertRow(tableRef.rows.length);
                var newCell0 = newRow.insertCell(0);
                
                var newText = document.createTextNode(sequenceNumber);
                newCell0.appendChild(newText);

                var newCell1 = newRow.insertCell(1);
                newText = document.createTextNode(data.orderId);
                newCell1.appendChild(newText);

                var newCell2 = newRow.insertCell(2);
                newText = document.createTextNode(data.customerId);
                newCell2.appendChild(newText);

                var newCell3 = newRow.insertCell(3);
                newText = document.createTextNode(data.deliveryClusterId);
                newCell3.appendChild(newText);
                var newCell4 = newRow.insertCell(4);
                newText = document.createTextNode(data.partyName);
                newCell4.appendChild(newText);
                var newCell5 = newRow.insertCell(5);
                newText = document.createTextNode(data.postalAddressName);
                newCell5.appendChild(newText);

                var newCell6 = newRow.insertCell(6);
                newText = document.createTextNode(formatnumber(data.totalGrandAmount));
                newCell6.appendChild(newText);
                totalValue += data.totalGrandAmount;
            }
            if (totalValue) {
                var newRowTotal = tableRef.insertRow(tableRef.rows.length);
                var newCellTotal0 = newRowTotal.insertCell(0);
                newCellTotal0.colSpan = 6;
                newCellTotal0.className = 'align-right';
                newCellTotal0.style.fontWeight = "bold";
                newCellTotal0.style.background = "#f2f2f2";
                var str = uiLabelMap.OrderItemsSubTotal.toUpperCase();
                var newTextTotal = document.createTextNode(str);
                newCellTotal0.appendChild(newTextTotal);

                var newCellTotal8 = newRowTotal.insertCell(1);
                newCellTotal8.className = 'align-right';
                newCellTotal8.style.background = "#f2f2f2";
                var newTextTotal = document.createTextNode(formatnumber(totalValue));
                newCellTotal8.appendChild(newTextTotal);
            }
        }
    }

    function finishCreateDeliveryEntry(listData) {
        var listOrders = [];
        for (var j = 0; j < listData.length; j++) {
            var data = listData[j];
            var map = {};
            map['orderId'] = data.orderId;
            listOrders.push(map);
        }
        listOrders = JSON.stringify(listOrders);
        var dataMap = {};
        dataMap = {
            facilityId: $('#facilityId').jqxDropDownList('val'),
            contactMechId: $('#contactMechId').jqxDropDownList('val'),
            fromDate: $('#fromDate').jqxDateTimeInput('getDate').getTime(),
            thruDate: $('#thruDate').jqxDateTimeInput('getDate').getTime(),
            driverPartyId :  $('#driverPartyId').jqxDropDownList('val'),
            tripReturnCost : $('#shipReturnCost').jqxNumberInput('val'),
            tripCost : $('#shipCost').jqxNumberInput('val'),
            listOrders: listOrders
        };
        if ($('#description').jqxInput('val')) {
            dataMap.description = $('#description').jqxInput('val');
        }
        $.ajax({
            type: 'POST',
            url: 'createNewShippingTripByOrder',
            async: false,
            data: dataMap,
            beforeSend: function () {
                $("#btnPrevWizard").addClass("disabled");
                $("#btnNextWizard").addClass("disabled");
                $("#loader_page_common").show();
            },
            success: function (data) {

                jOlbUtil.processResultDataAjax(data, function(data, errorMessage){
                    $("#btnPrevWizard").removeClass("disabled");
                    $("#btnNextWizard").removeClass("disabled");
                    $('#container').empty();
                    $('#jqxNotification').jqxNotification({ template: 'error'});
                    $("#jqxNotification").html(uiLabelMap.BLDErrorCreateShippingTrip);
                    $("#jqxNotification").jqxNotification("open");
                    return false;
                }, function(){
                    viewDeliveryEntryDetail(data.tripId);
                });
            },
            error: function (data) {
                alert("Send request is error");
            },
            complete: function (data) {
                $("#loader_page_common").hide();
                $("#btnPrevWizard").removeClass("disabled");
                $("#btnNextWizard").removeClass("disabled");
            },
        });
    }

    function viewDeliveryEntryDetail(tripId) {
        window.location.href = 'shippingTripDetail?shippingTripId=' + tripId;
    }

    var initValidateForm = function () {

    };
    var entityMap = {
        "&": "&amp;",
        "<": "&lt;",
        ">": "&gt;",
        '"': '&quot;',
        "'": '&#39;',
        "/": '&#x2F;'
    };

    function escapeHtml(string) {
        return String(string).replace(/[&<>"'\/]/g, function (s) {
            return entityMap[s];
        });
    }

    function unescapeHTML(escapedStr) {
        var div = document.createElement('div');
        div.innerHTML = escapedStr;
        var child = div.childNodes[0];
        return child ? child.nodeValue : '';
    };
    return {
        init: init,
    }
}());