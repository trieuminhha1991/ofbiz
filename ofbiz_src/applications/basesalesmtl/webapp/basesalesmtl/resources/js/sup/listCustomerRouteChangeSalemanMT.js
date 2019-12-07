$(function(){
    OlbCustomerRouteChangeSalesman.init();
});
var OlbCustomerRouteChangeSalesman = (function ($) {
    var gridListCustomerAssignedChangeSaleman;
    var _routeId;
    var init = function () {
        initElement();
        initElementComplex();
        initEvent();
    };

    var initElement = function () {
        $("#jqxwindowListCustChangSalemanMT").jqxWindow({
            theme: "olbius",
            cancelButton: $("#cancelViewListCustChangeSalesman"),
            width: 950,
            maxWidth: 1845,
            minHeight: 310,
            height: 460,
            resizable: false,
            isModal: true,
            autoOpen: false,
            modalOpacity: 0.7
        });
        $("#jqxNotificationCust").jqxNotification({
            width: 250, position: "top-right",
            opacity: 0.9, autoClose: true, template: "info"
        });
    };

    var initElementComplex = function () {
        var datafields = [{name: "customerId", type: "string"},
            {name: "partyCode", type: "string"},
            {name: "fullName", type: "string"},
            {name: "distributorId", type: "string"},
            {name: "salesmanId", type: "string"},
            {name: "salesmanName", type: "string"},
            {name: "geoPointId", type: "string"},
            {name: "postalAddressName", type: "string"},
            {name: "latitude", type: "number"},
            {name: "longitude", type: "number"},
            {name: "supervisorId", type: "string"},
            {name: "sequenceNum", type: "number"},
            {name: "routeId", type: "string"}];
        var columns = [
            {text: uiLabelMap.BSCustomerId, datafield: "partyCode", editable: false, width: 110},
            {text: uiLabelMap.BSCustomerName, datafield: "fullName", editable: false, width: 250},
            {text: uiLabelMap.BSSalesmanName, datafield: "salesmanName", editable: false, width: 250},
            {text: uiLabelMap.BSAddress, datafield: "postalAddressName", editable: false}
            // {text: uiLabelMap.BSLocation, datafield: "geoPointId", width: 125, sortable: false, editable: false,
            //     cellsrenderer: function(row, column, value, a, b, data){
            //         var local = "", localNoFixed = "";
            //         if(!!value) {
            //             local = [data.latitude.toFixed(3), data.longitude.toFixed(3)].join(", ");
            //             localNoFixed = [data.latitude, data.longitude].join(", ");
            //             return '<div class=\"jqx-grid-cell-left-align\" style=\"margin-top: 4px;\" title=\"'+localNoFixed+'\">'+local+'</div>';
            //         } else {
            //             return '<div style="width: 100%;height: 100%">' +
            //                 '<a href="javascript:void(0)" onclick="OlbCustomerRouteChangeSalesman.openUpdateLocation(\''+ row +'\')" class="blue"><i class="fa-plus-circle open-sans"></i>'+uiLabelMap.BSUpdateLocationCustomer+'</a>' +
            //                 '</div>'
            //         }
            //     }
            // },
            // {text: uiLabelMap.BSSequenceIdCustomer, datafield: "sequenceNum",cellsalign: "right", columntype: "numberinput", editable: true, width: 80,
            //     validation: function (cell, value) {
            //         if(value < 0){
            //             return {result: false, message: uiLabelMap.ValueMustBeGreateThanZero};
            //         }
            //         return true;
            //     },
            //     createeditor: function (row, cellvalue, editor, celltext, cellwidth) {
            //         editor.jqxNumberInput({width: cellwidth, inputMode: 'simple', decimalDigits: 0});
            //     },
            //     cellsrenderer: function(row, column, value){
            //         if(typeof(value) == 'number'){
            //             return "<span style='text-align: right'>" + value + "</span>";
            //         }
            //     },
            //     initeditor: function (row, cellvalue, editor, celltext, pressedkey) {
            //         if(typeof(cellvalue) == 'number'){
            //             editor.val(cellvalue);
            //         }
            //     },
            //     cellendedit: function (row, datafield, columntype, oldvalue, newvalue) {
            //         updateSequenceNum(row, oldvalue, newvalue);
            //     }
            //}
        ];
        var config = {
            width: "100%",
            pagesize: 10,
            filterable: true,
            sortable: true,
            pageable: true,
            editable: false,
            showfilterrow: true,
            datafields: datafields,
            columns: columns,
            useUrl: true,
            url: '',
            useUtilFunc: true,
            showtoolbar: false,
            showdefaultloadelement: true,
            autoshowloadelement: true,
            enabletooltips: true,
            selectionmode: "singlerow",
        };
        gridListCustomerAssignedChangeSaleman = new OlbGrid($("#jqxgridViewListRouteCustChangeSalesman"), null, config, []);
    };


    var initEvent = function () {
        $("#okAndContinueBT").click(function () {
            AddRouteObj.updateRoute('Y');
            $("#jqxwindowListCustChangSalemanMT").jqxWindow('close');
        });

        $("#disagreeAndContinueBT").click(function () {
            AddRouteObj.updateRoute('N');
            $("#jqxwindowListCustChangSalemanMT").jqxWindow('close');
        });
    };

    var updateSource = function (routeId, salemanId) {
        gridListCustomerAssignedChangeSaleman.updateSource("jqxGeneralServicer?sname=JQGetListRouteCustomerChangeSaleman&routeId=" + routeId + "&salemanId=" + salemanId, null, null);
    };
    var open = function (row) {
        var grid = $('#ListRoute');
        var rowData = grid.jqxGrid('getrowdata', row);
        _routeId = rowData.routeId;
        //var salesmanId = rowData.employeeId;
        var salesmanId = rowData.salesmanId;
        //$(".customerInfo").text(rowData.routeName + " [" + rowData.routeCode + "]");
        $("#jqxwindowListCustChangSalemanMT").data("routeId", _routeId);
        $("#jqxwindowListCustChangSalemanMT").data("salesmanId", salesmanId);
        OlbCustomerRouteChangeSalesman.updateSource(_routeId);
        var wtmp = window;
        var tmpwidth = $("#jqxwindowListCustChangSalemanMT").jqxWindow("width");
        //$("#jqxwindowListCustChangSalemanMT").jqxWindow({ position: { x: (wtmp.outerWidth - tmpwidth)/2, y: pageYOffset + 70 }});
        $("#jqxwindowListCustChangSalemanMT").jqxWindow("open");
    };
    var notify = function (res) {
        $("#jqxNotificationCust").jqxNotification("closeLast");
        if (res["_ERROR_MESSAGE_"] || res["_ERROR_MESSAGE_LIST_"]) {
            $("#jqxNotificationCust").jqxNotification({template: "error"});
            $("#notificationContentCust").text(uiLabelMap.updateError);
            $("#jqxNotificationCust").jqxNotification("open");
        } else {
            $("#jqxNotificationCust").jqxNotification({template: "info"});
            $("#notificationContentCust").text(uiLabelMap.updateSuccess);
            $("#jqxNotificationCust").jqxNotification("open");
        }
    };
    // var _delete = function() {
    //    var data = OlbCustomerRouteChangeSalesman.getValue();
    //    if (!_.isEmpty(data)) {
    //        bootbox.dialog(uiLabelMap.ConfirmDelete,
    //            [
    //                {"label": uiLabelMap.wgcancel,
    //                    "icon": 'fa fa-remove', "class": 'btn  btn-danger form-action-button pull-right',
    //                    "callback": function() {bootbox.hideAll();}
    //                },
    //                {"label": uiLabelMap.wgok,
    //                    "icon": 'fa-check', "class": 'btn btn-primary form-action-button pull-right',
    //                    "callback": function() {
    //                        var customers = [];
    //                        for ( var x in data) {
    //                            customers.push(data[x].customerId);
    //                        }
    //                        var dataMap = {
    //                            routeId: _routeId,
    //                            parties: JSON.stringify(customers)
    //                        };
    //
    //                        $.ajax({
    //                            type: 'POST',
    //                            url: 'removeRouteStores',
    //                            data: dataMap,
    //                            beforeSend: function(){
    //                                $("#loader_page_common").show();
    //                            },
    //                            success: function(data){
    //                                jOlbUtil.processResultDataAjax(data, function(data, errorMessage){
    //                                        $('#container').empty();
    //                                        $('#jqxNotification').jqxNotification({ template: 'error'});
    //                                        $("#jqxNotification").html(errorMessage);
    //                                        $("#jqxNotification").jqxNotification("open");
    //                                        return false;
    //                                    }, function(){
    //                                        $('#container').empty();
    //                                        $('#jqxNotification').jqxNotification({ template: 'info'});
    //                                        $("#jqxNotification").html(uiLabelMap.wgdeletesuccess);
    //                                        $("#jqxNotification").jqxNotification("open");
    //                                        gridListCustomerAssignedChangeSaleman.updateBoundData();
    //                                    }
    //                                );
    //                            },
    //                            error: function(data){
    //                                alert("Send request is error");
    //                            },
    //                            complete: function(data){
    //                                $("#loader_page_common").hide();
    //                            },
    //                        });
    //                    }
    //                }
    //            ]
    //        );
    // 	} else {
    // 		bootbox.alert(uiLabelMap.BSNotCustomerSelected);
    // 	}
    // };
    var getValue = function () {
        var value = [];
        var result = [];
        var rowindexes = $('#jqxgridViewListRouteCustChangeSalesman').jqxGrid('getselectedrowindexes');
        for (var x in rowindexes) {
            value.push($('#jqxgridViewListRouteCustChangeSalesman').jqxGrid('getrowdata', rowindexes[x]));
        }
        $.each(value, function (i, v) {
            if (OlbCore.isNotEmpty(v)) {
                result.push(v);
            }
        });
        return result;
    };

    // var openUpdateLocation = function(row) {
    //     var data = $("#jqxgridViewListRouteCustChangeSalesman").jqxGrid("getrowdata",row);
    //     var dataInput = data;
    //     //dataInput["address1"] = data.postalAddressName;
    //     OlbCustomerOnMap.open(dataInput);
    // };
    return {
        init: init,
        open: open,
        //openUpdateLocation: openUpdateLocation,
        updateSource: updateSource,
        notify: notify,
        getValue: getValue//,
        //_delete: _delete
    };
})(jQuery);
