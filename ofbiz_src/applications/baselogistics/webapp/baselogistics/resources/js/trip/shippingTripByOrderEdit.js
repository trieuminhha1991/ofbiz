$(function () {
    TripEditObj.init();
});
var TripEditObj = (function () {
    var listOrderGrid;
    var packAddOLBG = null;
    //var listPackAdd = [];
    var init = function () {
        if (noteValidate === undefined) var noteValidate;
        initInputs();
        initElementComplex();
        initEvents();
    };
    var initInputs = function () {
        $("#addInsideClusterOrderPopup").jqxWindow({
            maxWidth: 1500,
            minWidth: 500,
            width: 1000,
            modalZIndex: 10000,
            zIndex: 10000,
            minHeight: 200,
            height: 470,
            maxHeight: 670,
            resizable: false,
            cancelButton: $("#addInsideOrderCancel"),
            keyboardNavigation: true,
            keyboardCloseKey: 15,
            isModal: true,
            autoOpen: false,
            modalOpacity: 0.7,
            theme: theme
        });
        $("#addOutsideClusterOrderPopup").jqxWindow({
            maxWidth: 1500,
            minWidth: 500,
            width: 1000,
            modalZIndex: 10000,
            zIndex: 10000,
            minHeight: 200,
            height: 470,
            maxHeight: 670,
            resizable: false,
            cancelButton: $("#addOutsideOrderCancel"),
            keyboardNavigation: true,
            keyboardCloseKey: 15,
            isModal: true,
            autoOpen: false,
            modalOpacity: 0.7,
            theme: theme
        });
    };
    var initElementComplex = function () {
        initGrid();
        initOrderGridAdd();
    };
    var initEvents = function () {
        var gridOrderOnTripTmp = $("#jqxgridOrdersOnTrip");
        var insideClusterOrderTmp = $('#jqxgridAddInsideClusterOrder');
        var outsideClusterOrderTmp = $('#jqxgridAddOutsideClusterOrder');
        $("#addInsideOrderSave").on("click", function (event) {
            var selectedIndexs = insideClusterOrderTmp.jqxGrid('getselectedrowindexes');
            var length = selectedIndexs.length;
            for(var i = 0; i < length; i++){
                var data = insideClusterOrderTmp.jqxGrid('getrowdata', selectedIndexs[i]);
                gridOrderOnTripTmp.jqxGrid('addrow', null, data);
            }
            updateDataGrid(gridOrderOnTripTmp);	
            if (selectedIndexs.length == 1) {
                insideClusterOrderTmp.jqxGrid('deleterow', selectedIndexs[0]);
            }else {
                insideClusterOrderTmp.jqxGrid('deleterow', selectedIndexs);
            }
            updateDataGrid(insideClusterOrderTmp);
            $("#addInsideClusterOrderPopup").jqxWindow('close');
        });
        
        $("#addOutsideOrderSave").on("click", function (event) {
            var selectedIndexs = outsideClusterOrderTmp.jqxGrid('getselectedrowindexes');
            var length = selectedIndexs.length;
            for(var i = 0; i < length; i++){
                var data = outsideClusterOrderTmp.jqxGrid('getrowdata', selectedIndexs[i]);
                gridOrderOnTripTmp.jqxGrid('addrow', null, data);
            }
            updateDataGrid(gridOrderOnTripTmp);	
            if (selectedIndexs.length == 1) {
                outsideClusterOrderTmp.jqxGrid('deleterow', selectedIndexs[0]);
            }else {
                outsideClusterOrderTmp.jqxGrid('deleterow', selectedIndexs);
            }
            updateDataGrid(outsideClusterOrderTmp);
            $("#addOutsideClusterOrderPopup").jqxWindow('close');
        });
        
    };
    var initGrid = function () {
        
        var gridOrderOnTrip = $("#jqxgridOrdersOnTrip");
        var rendertoolbar = function (toolbar) {
            toolbar.html("");
            var container = $("<div id='jqxgridOrdersOnTripToolbar' class='widget-header' style='height:33px !important;'><div class='pull-right' style='margin-left: -10px !important; margin-top: 4px; padding: 0px !important'></div></div>");
            toolbar.append(container);
            container.append('<div class="margin-top10">');
            container.append('<a href="javascript:TripEditObj.addInsideOrder()" data-rel="tooltip" data-placement="bottom" id="btnAddIn"  class="button-action"><i class="fa fa-plus-circle"></i></a>');
            container.append('<a href="javascript:TripEditObj.addOutsideOrder()" data-rel="tooltip" data-placement="bottom" id="btnAddOut" class="button-action"><i class="fa fa-plus"></i></a>');
            container.append('<a href="javascript:TripEditObj.deleteOrderOnTrip()" data-rel="tooltip" data-placement="bottom" id="btnDel" class="button-action"><i class="red fa fa-times"></i></a>');
            container.append('</div>');
            $("#btnAddIn").jqxTooltip({ content: uiLabelMap.BLAddInsideClusterOrder  });
            $("#btnAddOut").jqxTooltip({ content: uiLabelMap.BLAddOutsideClusterOrder });
            $("#btnDel").jqxTooltip({ content:  uiLabelMap.BLDeleteOrder});
        }
        var dataField = [
                {name: 'orderId', type: 'string'},
                {name: 'customerId', type: 'string'},
                {name: 'deliveryClusterId', type: 'string'},
                {name: 'partyName', type: 'string'},
                {name: 'postalAddressName', type: 'string'},
                {name: 'totalGrandAmount', type: 'number'},
                {name: 'executorId', type: 'string'},
                {name: 'packId', type: 'string'},
        ];
        var columnListOrder = [
            {
                text: uiLabelMap.SequenceId, sortable: false, filterable: false, editable: false, pinned: true,
                groupable: false, draggable: false, resizable: false,
                datafield: '', columntype: 'number', width: 50,
                cellsrenderer: function (row, column, value) {
                    return '<div style=margin:4px;>' + (value + 1) + '</div>';
                }
            },
            {text: uiLabelMap.OrderId, dataField: 'orderId', width: 150, editable: false, pinned: true,},
            {text: uiLabelMap.BSCustomerId, dataField: 'customerId', width: 150, editable: false,},
            {text: uiLabelMap.BLDeliveryClusterId, dataField: 'deliveryClusterId', width: 150, editable: false,},
            {text: uiLabelMap.BSFullName, dataField: 'partyName', width: "20%", editable: false,},
            {text: uiLabelMap.BSAddress, dataField: 'postalAddressName', width: "30%", editable: false,},
            {
                text: uiLabelMap.BPOTotal, dataField: 'totalGrandAmount', width: 150, editable: false,
                cellsrenderer: function (row, column, value) {
                    return '<span class="align-right">' + formatnumber(value) + '</span>';
                },
            },
        ];
        
        var configGridOrderOnTrip = {
            columns: columnListOrder,
            datafields: dataField,
            width: '100%',
            height: 'auto',
            sortable: true,
            editable: true,
            filterable: true,
            pageable: true,
            showfilterrow: true,
            useUtilFunc: false,
            useUrl: false,
            groupable: false,
            showgroupsheader: false,
            showaggregates: false,
            showstatusbar: false,
            virtualmode: false,
            showdefaultloadelement: true,
            autoshowloadelement: true,
            showtoolbar: true,
            columnsresize: true,
            isSaveFormData: true,
            toolbarheight: 38,
            formData: "filterObjData",
            selectionmode: "checkbox",
            bindresize: true,
            pagesize: 10,
            rendertoolbar: rendertoolbar,
            key: 'orderId',
        };
        listPackGrid = new OlbGrid(gridOrderOnTrip, null, configGridOrderOnTrip, []);
        initGridData("JQGetListPackByTripId", "jqxgridOrdersOnTrip");
        listOrderOld = $("#jqxgridOrdersOnTrip").jqxGrid('source')._source.localdata;
    }

    
    var initOrderGridAdd = function () {
        var dataFieldOrderAdd = [  
                {name: 'orderId', type: 'string'},
                {name: 'customerId', type: 'string'},
                {name: 'deliveryClusterId', type: 'string'},
                {name: 'partyName', type: 'string'},
                {name: 'postalAddressName', type: 'string'},
                {name: 'totalGrandAmount', type: 'number'},
                {name: 'executorId', type: 'string'},
                {name: 'packId', type: 'string'},
        ];
        var columnOrderAdd = [
            {
                text: uiLabelMap.SequenceId, sortable: false, filterable: false, editable: false, pinned: true,
                groupable: false, draggable: false, resizable: false,
                datafield: '', columntype: 'number', width: 50,
                cellsrenderer: function (row, column, value) {
                    return '<div style=margin:4px;>' + (value + 1) + '</div>';
                }
            },
            {text: uiLabelMap.OrderId, dataField: 'orderId', width: 150, editable: false, pinned: true,},
            //{text: uiLabelMap.BSFullName, dataField: 'executorId', width: 150,editable: false, pinned: false, },
            {text: uiLabelMap.BSCustomerId, dataField: 'customerId', width: 150, editable: false,},
            {text: uiLabelMap.BLDeliveryClusterId, dataField: 'deliveryClusterId', width: 150, editable: false,},
            {text: uiLabelMap.BSFullName, dataField: 'partyName', width: "20%", editable: false,},
            {text: uiLabelMap.BSAddress, dataField: 'postalAddressName', width: "30%", editable: false,},
            {
                text: uiLabelMap.BPOTotal, dataField: 'totalGrandAmount', width: 150, editable: false,
                cellsrenderer: function (row, column, value) {
                    return '<span class="align-right">' + formatnumber(value) + '</span>';
                },
            },
        ];
        var configOrderAdd = {
            datafields: dataFieldOrderAdd,
            columns: columnOrderAdd,
            width: '100%',
            height: 'auto',
            sortable: true,
            editable: true,
            filterable: true,
            pageable: true,
            showfilterrow: true,
            useUtilFunc: false,
            useUrl: false,
            groupable: false,
            showgroupsheader: false,
            showaggregates: false,
            showstatusbar: false,
            virtualmode: false,
            showdefaultloadelement: true,
            autoshowloadelement: true,
            showtoolbar: false,
            columnsresize: true,
            isSaveFormData: true,
            formData: "filterObjData",
            selectionmode: "checkbox",
            bindresize: true,
            pagesize: 10,
            key: 'orderId',
        };
        var insideOrderGrid = new OlbGrid($("#jqxgridAddInsideClusterOrder"), null, configOrderAdd, []);
        initGridData("JQGetListOrderInArea&isIn=Y&shipperId=" + shipperPartyData['shipperId'], "jqxgridAddInsideClusterOrder");
        var outsideOrderGrid = new OlbGrid($("#jqxgridAddOutsideClusterOrder"), null, configOrderAdd, []);
        initGridData("JQGetListOrderInArea&isIn=N&shipperId=" + shipperPartyData['shipperId'], "jqxgridAddOutsideClusterOrder");
    };

    function initGridData(serviceName, gridId) {
        var list;
        $.ajax({
            type: 'POST',
            url: 'jqxGeneralServicer?sname=' + serviceName + "&shippingTripId=" + shippingTripId ,
            async: false,
            data: {
                pagesize : 0,
            },
            success: function (data) {
                OlbGridUtil.updateSource($("#"+gridId ), null, data.results, false);
            }
        });
        
    }
    var deleteOrderOnTrip = function () {
        var gridOrderOnTripTmp2 = $("#jqxgridOrdersOnTrip");
        var insideClusterOrderTmp2 = $('#jqxgridAddInsideClusterOrder');
        var outsideClusterOrderTmp2 = $('#jqxgridAddOutsideClusterOrder');
        var deletedOrder = gridOrderOnTripTmp2.jqxGrid('getselectedrowindexes');
        var length = deletedOrder.length;
        if ( length <= 0) {
            jOlbUtil.alert.error(uiLabelMap.DAYouNotYetChooseOrder);
            return false;
        }
        for (var i = 0; i < length; i++) {
            var data = gridOrderOnTripTmp2.jqxGrid('getrowdata', deletedOrder[i]);
            if(isInsideOrder( data['deliveryClusterId'])){
                insideClusterOrderTmp2.jqxGrid('addrow', null, data);
                
            }else {
                outsideClusterOrderTmp2.jqxGrid('addrow', null, data);
                
            }
        }
        updateDataGrid(insideClusterOrderTmp2);
        updateDataGrid(outsideClusterOrderTmp2);
        if (deletedOrder.length == 1) {
            gridOrderOnTripTmp2.jqxGrid('deleterow', deletedOrder[0]);
        }else {
            gridOrderOnTripTmp2.jqxGrid('deleterow', deletedOrder);
        }
        updateDataGrid(gridOrderOnTripTmp2);
    }
    var isInsideOrder = function(clusterId) {
        if (listClusterByShipper.includes(clusterId)) {
            return true;
        }
        return false;
    }

    var addOutsideOrder = function () {
        $("#addOutsideClusterOrderPopup").jqxWindow('open');
    }
    var addInsideOrder = function () {
        $("#addInsideClusterOrderPopup").jqxWindow('open');
    }
    var loadProduct = function loadProduct(valueDataSoure) {
        for (var i = 0; i < valueDataSoure.length; i++) {
            valueDataSoure[i]["unitPriceTmp"] = valueDataSoure[i]["unitPrice"];
        }
        var tmpS = grid.jqxGrid("source");
        tmpS._source.localdata = valueDataSoure;
        grid.jqxGrid("source", tmpS);
    };

    function productGridCellclass(row, column, value, data) {
        if (column == 'quantity') {
            return 'background-prepare';
        }
    }

    var checkSpecialCharacters = function (value) {
        if (OlbCore.isNotEmpty(value) && !(/^[a-zA-Z0-9 ÀÁÂÃÈÉÊÌÍÒÓÔÕÙÚĂĐĨŨƠàáâãèéêìíòóôõùúăđĩũơƯĂẠẢẤẦẨẪẬẮẰẲẴẶẸẺẼỀỀỂưăạảấầẩẫậắằẳẵặẹẻẽềềểỄỆỈỊỌỎỐỒỔỖỘỚỜỞỠỢỤỦỨỪễệỉịọỏốồổỗộớờởỡợụủứừỬỮỰỲỴÝỶỸửữựỳỵỷỹ]+$/.test(value))) {
            return true;
        }
        return false;
    }

    function unescapeHTML(escapedStr) {
        var div = document.createElement('div');
        div.innerHTML = escapedStr;
        var child = div.childNodes[0];
        return child ? child.nodeValue : '';
    };
    var viewCurrentPackStatus = function(value) {
        for (var i = 0; i < statusAllStatusOrder.length; i++) {
            if (statusAllStatusOrder[i].statusId == value) {
                return statusAllStatusOrder[i].description;
            }
        }
    }
    var updateDataGrid = function( grid) {
      var currentData = grid.jqxGrid('getrows');
      var tmpS = grid.jqxGrid("source");
      tmpS._source.localdata = currentData;
      // if (tmpS._source.localdata.length > 0 ) {
      //     grid.jqxGrid('updatebounddata');
      // }else {
      //     grid.jqxGrid('refresh');
      // }
      grid.jqxGrid('updatebounddata');
      grid.jqxGrid('refreshdata');
      grid.jqxGrid('refresh');
    };
    var getListOrderSelected = function () {
        var tmpS = $("#jqxgridOrdersOnTrip").jqxGrid("source");
        return tmpS._source.localdata;
	};
    return {
        init: init,
        deleteOrderOnTrip: deleteOrderOnTrip,
        addOutsideOrder: addOutsideOrder,
        addInsideOrder: addInsideOrder,
        viewCurrentPackStatus: viewCurrentPackStatus,
        getListOrderSelected: getListOrderSelected,
    }
})();