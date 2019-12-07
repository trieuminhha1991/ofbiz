$(function() {
    OlbReqProduct.init();
});

var OlbReqProduct = (function() {
    var grid = $('#jqxGridProduct');
    var product = null;
    var init = function() {
        initInput();
        initElementComplex();
        initEvents();
    };

    var initInput = function() {
    }

    var initElementComplex = function() {
        initGridProduct(grid);
    }
    var getColumns = function(grid){
        var columns =  [
            { text: uiLabelMap.SequenceId, sortable: false, filterable: false, editable: false, pinned: true,
                groupable: false, draggable: false, resizable: false,
                datafield: '', columntype: 'number', width: 50,
                cellsrenderer: function (row, column, value) {
                    return '<div style=margin:4px;>' + (value + 1) + '</div>';
                }
            },
            { text: uiLabelMap.DeliveryId, dataField: 'deliveryId', width: 300, editable: false, pinned: true,},
            { text: uiLabelMap.OrderId, dataField: 'orderId', width: 300, editable: false, pinned: true,},
            { text: uiLabelMap.partyIdTo, dataField: 'partyIdTo', width: 300, editable: false, pinned: true,},

            { text: uiLabelMap.Note, datafield: 'description', sortable: false, width: 400, editable: true, filterable: false, cellsalign: 'left', sortable: false, cellclassname: productGridCellclass,
                cellsrenderer: function(row, column, value) {
                    var rowData = grid.jqxGrid('getrowdata', row);
                    if (value) {
                        return '<span>' + value +'</span>';
                    }
                    return value;
                },
                initeditor: function (row, cellvalue, editor) {
                    editor.jqxInput();
                    if (!cellvalue) {
                        cellvalue = unescapeHTML(cellvalue);
                    }
                    editor.jqxInput('val', cellvalue);
                },
                validation: function (cell, value) {
                    if (checkSpecialCharacters(value)){
                        return { result: false, message: uiLabelMap.validContainSpecialCharacter };
                    }
                    return true;
                }
            },
        ];
        return columns;
    };

    var getDataField = function(){
        var datafield =  [
            { name: 'deliveryId', type: 'string' },
            { name: 'orderId', type: 'string' },
            { name: 'partyIdTo', type: 'string' },
            { name: 'description', type: 'string' },

        ];
        return datafield;
    };

    var initGridProduct = function(grid){
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
            url: 'jqxGeneralServicer?sname=JQGetListDeliveryByCustomer',
            groupable: false,
            showgroupsheader: false,
            showaggregates: false,
            showstatusbar: false,
            virtualmode:true,
            showdefaultloadelement:true,
            autoshowloadelement:true,
            showtoolbar:false,
            columnsresize: true,
            isSaveFormData: true,
            formData: "filterObjData",
            selectionmode: "checkbox",
            bindresize: true,
            pagesize: 10,
        };
        product = new OlbGrid(grid, null, configGrid, []);
    };


    var productGridCellclass = function (row, column, value, data) {
        var data = grid.jqxGrid('getrowdata',row);
        if (data['purchaseDiscontinuationDate'] != undefined && data['purchaseDiscontinuationDate'] != null) {
            var now = new Date();
            var ex = new Date(data['purchaseDiscontinuationDate']);
            if (ex <= now){
                return 'background-cancel';
            }
        } else {
            if (column == 'quantity' || column == 'description') {
                return 'background-prepare';
            }
        }
    }

    var updateGridProductSource = function (source){
        grid.jqxGrid("source")._source.url = source;
        grid.jqxGrid("updatebounddata");
    }

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

    var checkSpecialCharacters = function(value) {
        if (OlbCore.isNotEmpty(value) && !(/^[a-zA-Z0-9 ÀÁÂÃÈÉÊÌÍÒÓÔÕÙÚĂĐĨŨƠàáâãèéêìíòóôõùúăđĩũơƯĂẠẢẤẦẨẪẬẮẰẲẴẶẸẺẼỀỀỂưăạảấầẩẫậắằẳẵặẹẻẽềềểỄỆỈỊỌỎỐỒỔỖỘỚỜỞỠỢỤỦỨỪễệỉịọỏốồổỗộớờởỡợụủứừỬỮỰỲỴÝỶỸửữựỳỵỷỹ]+$/.test(value))) {
            return true;
        }
        return false;
    }
    return {
        init : init,
        updateGridProductSource: updateGridProductSource,
    }
}());