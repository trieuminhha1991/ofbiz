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
            {name: 'vehicleId', type: 'string'},
            {name: 'loading', type: 'string'},
            {name: 'licensePlate', type: 'string'},
            {name: 'volume', type: 'string'},
            {name: 'reqNo', type: 'string'},
            {name: 'vehicleTypeId', type: 'string'},
            {name: 'description', type: 'string'},
            {name: 'longitude', type: 'string'},
            {name: 'width', type: 'string'},
            {name: 'height', type: 'string'}
        ];
        var columns = [
            {
                text: uiLabelMap.BDVehicleId,
                dataField: 'vehicleId',
                width: '16%',
                editable: false,
                pinned: true,
                cellclassname: cellclass
            },
            {
                text: uiLabelMap.BDLicensePlate,
                dataField: 'licensePlate',
                width: '16%',
                pinned: true,
                editable: true,
                cellclassname: cellclass
            },
            {
                text: uiLabelMap.BDLoading + ' (' + uiLabelMap.Ton + ')',
                dataField: 'loading',
                width: '16%',
                editable: true,
                cellclassname: cellclass
            },
            {
                text: uiLabelMap.BDVolume + ' (' + uiLabelMap.M3 + ')',
                dataField: 'volume',
                width: '16%',
                editable: true,
                cellclassname: cellclass
            },
            {
                text: uiLabelMap.BDReqNo,
                dataField: 'reqNo',
                width: '16%',
                editable: true,
                cellclassname: cellclass
            },
            {
                text: uiLabelMap.BDVehicleType,
                datafield: 'vehicleTypeId',
                align: 'left',
                width: '13%',
                columntype: 'dropdownlist',
                cellsrenderer: function (row, column, value) {
                    if (!value) return '<span> _NA_ </span>';
                    var name = value;
                    $.ajax({
                        url: 'getVehicleTypeName',
                        type: 'POST',
                        data: {vehicleTypeId: value},
                        dataType: 'json',
                        async: false,
                        success: function (data) {
                            if (!data._ERROR_MESSAGE_) {
                                name = data.name;
                            }
                        }
                    });
                    return '<span title' + value + '>' + name + '</span>';
                },
                initeditor: function (row, cellvalue, editor) {
                    var vehicleTypeData = new Array();
                    var data = $('#' + idGridJQ).jqxGrid('getrowdata', row);
                    var itemSelected = data['vehicleTypeId'];

                    $.ajax({
                        url: 'getAllVehicleType',
                        type: 'POST',
                        data: {
                            listSortFields: [],
                            opts: [],
                            listAllConditions: [],
                            parameters: []
                        },
                        dataType: 'json',
                        async: false,
                        success: function (data) {
                            if (!data._ERROR_MESSAGE_) {
                                vehicleTypeData = data.listVehicleType;
                            }
                        }
                    });
                    var sourceData = {
                        localdata: vehicleTypeData,
                        datatype: 'array'
                    };
                    var dataAdapterPacking = new $.jqx.dataAdapter(sourceData);
                    editor.jqxDropDownList({
                        source: dataAdapterPacking,
                        displayMember: 'name',
                        valueMember: 'vehicleTypeId'
                    });
                    editor.jqxDropDownList('selectItem', itemSelected);
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
                text: uiLabelMap.BDLongitude + ' (m)',
                dataField: 'longitude',
                width: '16%',
                editable: true,
                cellclassname: cellclass
            },
            {
                text: uiLabelMap.BDWidth + ' (m)',
                dataField: 'width',
                width: '16%',
                editable: true,
                cellclassname: cellclass
            },
            {
                text: uiLabelMap.BDHeight + ' (m)',
                dataField: 'height',
                width: '16%',
                editable: true,
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
            // rendertoolbar: function (toolbar) {
            //     toolbar.html("");
            //     var grid = $('#' + idGridJQ);
            //     var me = this;
            //     var jqxheader = renderJqxTitle();
            //     toolbar.append(jqxheader);
            //     var container = $('#toolbarButtonContainer' + idGridJQ);
            //     var maincontainer = $("#toolbarcontainer" + idGridJQ);
            //     Grid.createAddRowButton(
            //         grid, container, uiLabelMap.accAddNewRow, {
            //             type: addType,
            //             container: $("#" + alternativeAddPopup),
            //             data: dataCreateAddRowButton,
            //         }
            //     );
            // },
            contextMenu: "contextMenu"
        };
        new OlbGrid($("#" + idGridJQ), localData, configProductList, []);
    };

    function simulateKeyPress(character) {
        jQuery.event.trigger({type: 'keypress', which: 50});
    }

    var initEvent = function () {
        $('#alterCancelEdit').on('click', function () {
            window.open('getAllVehicle', '_self');
        });
        // $('#alterSaveEdit').on('click', function () {
        //     var rowIndex = $('#' + idGridJQ).jqxGrid('getselectedrowindex');
        //     if (OlbCore.isNotEmpty(rowIndex)) {
        //         $("#" + idGridJQ).jqxGrid('endcelledit', rowIndex, "quantity", true, true);
        //     }
        //     var dataMap = {};
        //     var data = $("#" + idGridJQ).jqxGrid("getrows");
        //     if (typeof(data) == 'undefined') {
        //         alert("Error check data");
        //     }
        //
        //     var listVehilce = [];
        //     for (var i = 0; i < data.length; i++) {
        //         var dataItem = data[i];
        //         var prodItem = {
        //             vehicleId: dataItem.vehicleId,
        //             loading: dataItem.loading,
        //             licensePlate: dataItem.licensePlate,
        //             volume: dataItem.volume,
        //             reqNo: dataItem.reqNo,
        //             vehicleType: dataItem.vehicleType,
        //             description: dataItem.description
        //         };
        //         listVehilce.push(prodItem);
        //     }
        //     if (listVehilce.length > 0) {
        //         dataMap.listVehilce = JSON.stringify(listVehilce);
        //
        //         $.ajax({
        //             type: 'POST',
        //             url: 'processEditSalesOrderLoadToCart',
        //             data: dataMap,
        //             beforeSend: function () {
        //                 $("#loader_page_common").show();
        //             },
        //             success: function (data) {
        //                 jOlbUtil.processResultDataAjax(data, "default", function () {
        //                     $('#container').empty();
        //                     $('#jqxNotification').jqxNotification({template: 'info'});
        //                     $("#jqxNotification").html(uiLabelMap.wgupdatesuccess);
        //                     $("#jqxNotification").jqxNotification("open");
        //                     window.location.href = 'viewOrder?orderId=' + orderId;
        //                     return true;
        //                 }, function (data) {
        //                     $("#windowEditContactMech").jqxWindow("open");
        //                     $("#windowEditContactMechContainer").html(data);
        //                 });
        //             },
        //             error: function (data) {
        //                 alert("Send request is error");
        //             },
        //             complete: function (data) {
        //                 $("#loader_page_common").hide();
        //             },
        //         });
        //     } else {
        //         jOlbUtil.alert.error(uiLabelMap.BSYouNotYetChooseProduct);
        //         return false;
        //     }
        // });


        $('#confirmDialog').on('click', function () {
            jOlbUtil.confirm.dialog(uiLabelMap.AreYouSureEdit, function () {
                Loading.show("loadingMacro");
                setTimeout(function () {
                    var rowIndex = $('#' + idGridJQ).jqxGrid('getselectedrowindex');
                    if (OlbCore.isNotEmpty(rowIndex)) {
                        $("#" + idGridJQ).jqxGrid('endcelledit', rowIndex, "quantity", true, true);
                    }
                    var dataMap = {};
                    var data = $("#" + idGridJQ).jqxGrid("getrows");
                    if (typeof(data) == 'undefined') {
                        alert("Error check data");
                    }
                    dataMap.listVehicle = JSON.stringify(data);

                    $.ajax({
                        type: 'POST',
                        url: 'processEditVehicle',
                        data: dataMap
                    }).done(function () {
                        document.location.href = "getAllVehicle";
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