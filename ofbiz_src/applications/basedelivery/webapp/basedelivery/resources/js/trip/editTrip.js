$(function () {
    OlbEditSO.init();
});
var OlbEditSO = (function () {
    var init = function () {
        initElement();
        initElementAdvance();
        initEvent();
    };
    var initElement = function () {
        jOlbUtil.notification.create("#containerEditSO", "#jqxNotificationEditSO");
        jOlbUtil.contextMenu.create($("#contextMenu"));
        jOlbUtil.windowPopup.create($("#windowEditContactMech"), {
            maxWidth: 1200,
            width: 1200,
            height: 600,
            showCloseButton: false
        });
    };
    var initElementAdvance = function () {
        var datafields = [
            {name: 'tripId', type: 'string'},
            {name: 'scLogId', type: 'string'},
            {name: 'contractorId', type: 'string'},
            {name: 'vehicleId', type: 'string'},
            {name: 'driverId', type: 'string'},
            {name: 'statusId', type: 'string'},
            {name: 'description', type: 'string'},
            {name: 'totalWeight', type: 'string'}
        ];
        var columns = [
            {
                text: uiLabelMap.BDTripId,
                dataField: 'tripId',
                width: '16%',
                editable: false,
                cellclassname: cellclass,
                pinned: true
            },

            {
                text: uiLabelMap.BDScLogId,
                dataField: 'scLogId',
                width: '16%',
                editable: false,
                cellclassname: cellclass,
                cellsrenderer: function (row, column, value) {
                    var partyName = value;
                    $.ajax({
                        url: 'getPartyName',
                        type: 'POST',
                        data: {partyId: value},
                        dataType: 'json',
                        async: false,
                        success: function (data) {
                            if (!data._ERROR_MESSAGE_) {
                                partyName = data.partyName;
                            }
                        }
                    });
                    return '<span title' + value + '>' + partyName + '</span>';
                }
            },

            {
                text: uiLabelMap.BDContractorId,
                dataField: 'contractorId',
                width: '16%',
                editable: false,
                cellclassname: cellclass,
                cellsrenderer: function (row, column, value) {
                    var partyName = value;
                    $.ajax({
                        url: 'getPartyName',
                        type: 'POST',
                        data: {partyId: value},
                        dataType: 'json',
                        async: false,
                        success: function (data) {
                            if (!data._ERROR_MESSAGE_) {
                                partyName = data.partyName;
                            }
                        }
                    });
                    return '<span title' + value + '>' + partyName + '</span>';
                }
            },

            {
                text: uiLabelMap.BDVehicleId,
                dataField: 'vehicleId',
                width: '16%',
                editable: true,
                cellclassname: cellclass,
                cellsrenderer: function (row, column, value) {
                    if (!value) return '_NA_';
                    if (vehicleData.length > 0) {
                        for (var i = 0; i < vehicleData.length; i++) {
                            if (value == vehicleData[i].vehicleId) {
                                return "<span><a href='editVehicle?vehicleId=" + value + "'>" + vehicleData[i].licensePlate + '[' + vehicleData[i].vehicleId + ']' + "</a></span>";
                            }
                        }
                    }
                    return '<span title=' + value + '>' + value + '</span>';
                },
                initeditor: function (row, cellvalue, editor) {
                    var packingUomData = new Array();
                    var data = $('#' + idGridJQ).jqxGrid('getrowdata', row);

                    var itemSelected = data['vehicleId'];
                    var packingUomIdArray = data['packingUomIds'];

                    for (var i = 0; i < packingUomIdArray.length; i++) {
                        var packingUomIdItem = packingUomIdArray[i];
                        var row = {};
                        if (packingUomIdItem.description == undefined || packingUomIdItem.description == '') {
                            row['description'] = '' + packingUomIdItem.uomId;
                        } else {
                            row['description'] = '' + packingUomIdItem.description;
                        }
                        row['uomId'] = '' + packingUomIdItem.uomId;
                        packingUomData[i] = row;
                    }
                    var sourceDataPacking = {
                        localdata: packingUomData,
                        datatype: 'array'
                    };
                    var dataAdapterPacking = new $.jqx.dataAdapter(sourceDataPacking);
                    editor.jqxDropDownList({ source: dataAdapterPacking, displayMember: 'description', valueMember: 'uomId'});
                    editor.jqxDropDownList('selectItem', itemSelected);
                }
            },

            {
                text: uiLabelMap.BDDriverId,
                dataField: 'driverId',
                width: '16%',
                editable: true,
                cellclassname: cellclass,
                cellsrenderer: function (row, column, value) {
                    if(!value) return '_NA_';
                    var partyName = value;
                    $.ajax({
                        url: 'getPartyName',
                        type: 'POST',
                        data: {partyId: value},
                        dataType: 'json',
                        async: false,
                        success: function (data) {
                            if (!data._ERROR_MESSAGE_) {
                                partyName = data.partyName;
                            }
                        }
                    });
                    return '<span title' + value + '>' + partyName + '</span>';
                }
            },

            {
                text: uiLabelMap.BDStatusId,
                dataField: 'statusId',
                width: '16%',
                editable: false,
                cellclassname: cellclass,
                cellsrenderer: function(row, column, value){
                    if (tripStatusData.length > 0) {
                        for(var i = 0 ; i < tripStatusData.length; i++){
                            if (value == tripStatusData[i].statusId){
                                return '<span title =\"' + tripStatusData[i].description +'\">' + tripStatusData[i].description + '</span>';
                            }
                        }
                    }
                    return '<span title=' + value +'>' + value + '</span>';
                }
            },
            {
                text: uiLabelMap.BDDescription,
                dataField: 'description',
                width: '16%',
                editable: true,
                cellclassname: cellclass
            },
            {
                text: uiLabelMap.BDTotalWeight,
                dataField: 'totalWeight',
                width: '16%',
                editable: false,
                cellclassname: cellclass
            },

        ];
        var configProductList = {
            showdefaultloadelement: false,
            autoshowloadelement: false,
            dropDownHorizontalAlignment: 'right',
            datafields: datafields,
            columns: columns,
            useUrl: false,
            clearfilteringbutton: false,
            editable: true,
            alternativeAddPopup: 'alterpopupWindow',
            pageable: true,
            pagesize: 15,
            showtoolbar: false,
            editmode: 'click',
            selectionmode: 'multiplecellsadvanced',
            width: '100%',
            bindresize: true,
            groupable: false,
            localization: getLocalization(),
            showtoolbar: true,
            showdefaultloadelement: true,
            autoshowloadelement: true,
            virtualmode: false,
            contextMenu: "contextMenu",
        };
        new OlbGrid($("#" + idGridJQ), localData, configProductList, []);
    };

    function simulateKeyPress(character) {
        jQuery.event.trigger({type: 'keypress', which: 50});
    }

    var initEvent = function () {
        $('#alterCancelEdit').on('click', function () {
            window.open('listTrip', '_self');
        });

        $('#confirmDialog').on('click', function () {
            jOlbUtil.confirm.dialog(uiLabelMap.AreYouSureEdit, function () {
                Loading.show("loadingMacro");
                setTimeout(function () {
                    var rowIndex = $('#' + idGridJQ).jqxGrid('getselectedrowindex');
                    if (OlbCore.isNotEmpty(rowIndex)) {
                        $("#" + idGridJQ).jqxGrid('endcelledit', rowIndex, "quantity", true, true);
                    }
                    var dataMap = {};
                    var data = $("#" + idGridJQ).jqxGrid("getrows")

                    if (typeof(data) == 'undefined') {
                        alert("Error check data");
                    }
                    dataMap.listVehicle = JSON.stringify(data);

                    $.ajax({
                        type: 'POST',
                        url: 'processEditVehicle',
                        data: dataMap
                    }).done(function () {
                        document.location.href = "viewTrip?tripId=" + data.tripId;
                    });
                    Loading.hide("loadingMacro");
                }, 500);
            });
        });
    };
    return {
        init: init
    };
}());