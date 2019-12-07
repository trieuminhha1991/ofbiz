$(function () {
    OlbOrder.init();
});

var OlbOrder = (function () {
    var listOrderSelected = [];
    var listOrderNotInSelected = [];
    var grid = $('#jqxGridOrder');
    var gridNotIn = $('#jqxGridOrderNotIn');
    var order = null;
    var init = function () {
        initInput();
        initElementComplex();
        initEvents();
    };

    var initInput = function () {
    };

    var initElementComplex = function () {
        initGridOrder(grid);
    };
    var getColumns = function (grid) {
        var columns = [
            {
                text: uiLabelMap.SequenceId, sortable: false, filterable: false, editable: false, pinned: true,
                groupable: false, draggable: false, resizable: false,
                datafield: '', columntype: 'number', width: '4%',
                cellsrenderer: function (row, column, value) {
                    return '<div style=margin:4px;>' + (value + 1) + '</div>';
                }
            },
            {text: uiLabelMap.OrderId, dataField: 'orderId', width: '10%', editable: false, pinned: true,},
            {text: uiLabelMap.BSCustomerId, dataField: 'customerId', width: '16%', editable: false,},
            {text: uiLabelMap.BLDeliveryClusterId, dataField: 'deliveryClusterId', width: '13%', editable: false,},
            {text: uiLabelMap.BSFullName, dataField: 'partyName', width: '17%', editable: false,},
            {text: uiLabelMap.BSAddress, dataField: 'postalAddressName', width: "37%", editable: false,},
            {
                text: uiLabelMap.BPOTotal, dataField: 'totalGrandAmount', width: '14%', editable: false,
                cellsrenderer: function (row, column, value) {
                    return '<span class="align-right">' + formatnumber(value) + '</span>';
                },
            },

        ];
        return columns;
    };

    var getDataField = function () {
        var datafield = [{name: 'orderId', type: 'string'},
            {name: 'customerId', type: 'string'},
            {name: 'deliveryClusterId', type: 'string'},
            {name: 'partyName', type: 'string'},
            {name: 'postalAddressName', type: 'string'},
            {name: 'totalGrandAmount', type: 'string'},

        ];
        return datafield;
    };

    var initGridOrder = function (grid) {
        var configGrid = {
            datafields: getDataField(),
            columns: getColumns(grid),
            width: '100%',
            height: 'auto',
            sortable: true,
            editable: true,
            filterable: true,
            pageable: true,
            showfilterrow: true,
            useUtilFunc: false,
            useUrl: true,
            url: '',
            groupable: false,
            showgroupsheader: false,
            showaggregates: false,
            showstatusbar: false,
            virtualmode: true,
            showdefaultloadelement: true,
            autoshowloadelement: true,
            showtoolbar: false,
            columnsresize: true,
            isSaveFormData: true,
            formData: "filterObjData",
            selectionmode: "checkbox",
            bindresize: true,
            pagesize: 10,
            key: 'orderId'
        };
        order = new OlbGrid(grid, null, configGrid, []);

        var configGrid = {
            datafields: getDataField(),
            columns: getColumns(grid),
            width: '100%',
            height: 'auto',
            sortable: true,
            editable: true,
            filterable: true,
            pageable: true,
            showfilterrow: true,
            useUtilFunc: false,
            useUrl: true,
            url: '',
            groupable: false,
            showgroupsheader: false,
            showaggregates: false,
            showstatusbar: false,
            virtualmode: true,
            showdefaultloadelement: true,
            autoshowloadelement: true,
            showtoolbar: false,
            columnsresize: true,
            isSaveFormData: true,
            formData: "filterObjData",
            selectionmode: "checkbox",
            bindresize: true,
            pagesize: 10,
            key: 'orderId'
        };
        orderNotIn = new OlbGrid(gridNotIn, null, configGrid, []);
    };

    var initEvents = function () {
        order.rowSelectListener(function (rowData) {
            var tmp=true;
            for(var x in listOrderSelected) {
                if (rowData.orderId == listOrderSelected[x].orderId) tmp = false;
            }
            if(tmp) {
                listOrderSelected.push(rowData);
            }
        });
        order.rowUnSelectListener(function (rowData) {
            var tmp=-1;
            for(var x in listOrderSelected) {
                if (rowData.orderId == listOrderSelected[x].orderId) tmp = x;
            }
            if(tmp!=-1)

                listOrderSelected.splice(tmp,1);

        });
        order.bindingCompleteListener(function () {
            grid.find('.jqx-grid-column-header:first').find('div:first').hide();
            var listRowData = order.getAllRowData();
            for (var x in listOrderSelected) {
                for (var y in listRowData)
                    if (listRowData[y] != undefined && listRowData[y].orderId == listOrderSelected[x].orderId) {
                        grid.jqxGrid({selectedrowindex: grid.jqxGrid('getrowboundindex', y)});
                    }
            }
        });
        orderNotIn.rowSelectListener(function (rowData) {
            var tmp=true;
            for(var x in listOrderNotInSelected) {
                if (rowData.orderId == listOrderNotInSelected[x].orderId) tmp = false;
            }
            if(tmp) {
                listOrderNotInSelected.push(rowData);
            }
        });
        orderNotIn.rowUnSelectListener(function (rowData) {
            var tmp=-1;
            for(var x in listOrderNotInSelected) {
                if (rowData.orderId == listOrderNotInSelected[x].orderId) tmp = x;
            }
            if(tmp!=-1)
            listOrderNotInSelected.splice(listOrderNotInSelected.indexOf(rowData));

        });
        orderNotIn.bindingCompleteListener(function () {
            gridNotIn.find('.jqx-grid-column-header:first').find('div:first').hide();
            var listRowData = order.getAllRowData();
            for (var x in listOrderNotInSelected) {
                for (var y in listRowData)
                    if (listRowData[y] != undefined && listRowData[y].orderId == listOrderNotInSelected[x].orderId) {
                        grid.jqxGrid({selectedrowindex: grid.jqxGrid('getrowboundindex', y)});
                    }
            }
        });

    }

    var orderGridCellclass = function (row, column, value, data) {
        var data = grid.jqxGrid('getrowdata', row);
        if (data['purchaseDiscontinuationDate'] != undefined && data['purchaseDiscontinuationDate'] != null) {
            var now = new Date();
            var ex = new Date(data['purchaseDiscontinuationDate']);
            if (ex <= now) {
                return 'background-cancel';
            }
        } else {
            if (column == 'quantity' || column == 'description') {
                return 'background-prepare';
            }
        }
    };

    var updateGridOrderNotInSource = function (source) {
        listOrderNotInSelected = []
        gridNotIn.jqxGrid("source")._source.url = source;
        gridNotIn.jqxGrid("updatebounddata");
    };

    var updateGridOrderSource = function (source) {
        listOrderSelected = []
        grid.jqxGrid("source")._source.url = source;
        grid.jqxGrid("updatebounddata");
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
    }

    function getOrderGrid() {
        return order;

    }

    var getListSelectedOrder = function () {
        return listOrderSelected;
    };
    var getListSelectedOrderNotIn = function () {
        return listOrderNotInSelected;
    };
    var checkSpecialCharacters = function (value) {
        if (OlbCore.isNotEmpty(value) && !(/^[a-zA-Z0-9 ÀÁÂÃÈÉÊÌÍÒÓÔÕÙÚĂĐĨŨƠàáâãèéêìíòóôõùúăđĩũơƯĂẠẢẤẦẨẪẬẮẰẲẴẶẸẺẼỀỀỂưăạảấầẩẫậắằẳẵặẹẻẽềềểỄỆỈỊỌỎỐỒỔỖỘỚỜỞỠỢỤỦỨỪễệỉịọỏốồổỗộớờởỡợụủứừỬỮỰỲỴÝỶỸửữựỳỵỷỹ]+$/.test(value))) {
            return true;
        }
        return false;
    };
    return {
        init: init,
        getOrderGrid: getOrderGrid,
        updateGridOrderSource: updateGridOrderSource,
        updateGridOrderNotInSource: updateGridOrderNotInSource,
        getListSelectedOrderNotIn: getListSelectedOrderNotIn,
        getListSelectedOrder: getListSelectedOrder
    }
}());