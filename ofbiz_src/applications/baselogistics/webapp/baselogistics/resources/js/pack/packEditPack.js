$(function () {
    PackEditObj.init();
});
var PackEditObj = (function () {

    var listProductGrid=null;
    var grid = $('#jqxgridProductDetail');
    var productAddOLBG = null;
    var init = function () {
        if (noteValidate === undefined) var noteValidate;
        initInputs();
        initElementComplex();
        initEvents();
    };
    var initInputs = function () {
    };
    var initElementComplex = function () {
        //initGrid();
        //initProductGridAdd();
        initGridProduct(grid);
    };
    var initEvents = function () {
    };

    var getColumns = function(grid){
        var columns =  [
            { text: uiLabelMap.SequenceId, sortable: false, filterable: false, editable: false, pinned: true,
                groupable: false, draggable: false, resizable: false,
                datafield: '', columntype: 'number', width: 50,
                cellsrenderer: function (row, column, value) {
                    return '<div style=margin:4px;>' + (value + 1) + '</div>';
                }
            },
            { text: uiLabelMap.ProductId, dataField: 'productCode', width: 150, editable: false, pinned: true,},
            { text: uiLabelMap.DeliveryId, dataField: 'deliveryId', width: 150, editable: false, pinned: true,},
            { text: uiLabelMap.DeliveryItemSeqId, dataField: 'deliveryItemSeqId', width: 150, editable: false, pinned: true,},
            { text: uiLabelMap.ProductName, dataField: 'productName', minwidth: 150, editable:false,},
            { text: uiLabelMap.Quantity, datafield: 'quantityOnHand', sortable: true, width: 120, editable: false, filterable: false, cellsalign: 'right', cellclassname: productGridCellclass,
                cellsrenderer: function(row, column, value) {
                    var data = grid.jqxGrid('getrowdata', row);
                    value = data.quantity;
                    description = formatnumber(value) + ' (' + getUomDescription(data.quantityUomId)+')';
                    return '<span class="align-right">' + description +'</span>';
                },
            },
            {editable: false, text: uiLabelMap.Unit, sortable: false, dataField: 'uomId', width: 120, columntype: 'dropdownlist', filterable:false, cellclassname: productGridCellclass,
                cellsrenderer: function(row, column, value) {
                    var rowData = grid.jqxGrid('getrowdata', row);
                    value = rowData.quantityUomId;

                    if (value) {
                        var desc = getUomDescription(value);
                        return '<span class="align-right">' + desc +'</span>';
                    }
                    return value;
                },
            },

            { text: uiLabelMap.Note, datafield: 'description', sortable: false, width: 150, editable: true, filterable: false, cellsalign: 'left', sortable: false, cellclassname: productGridCellclass,
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
        var datafield =  [{ name: 'productId', type: 'string' },
            { name: 'deliveryId', type: 'string' },
            { name: 'deliveryItemSeqId', type: 'string' },
            { name: 'supplierProductId', type: 'string' },
            { name: 'productCode', type: 'string' },
            { name: 'productName', type: 'string' },
            { name: 'quantity', type: 'number' },
            { name: 'quantityUomId', type: 'string' },
            { name: 'quantityUomIds', type: 'string' },
            { name: 'quantityOnHand', type: 'number' }
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
            url: '',
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
            selectionmode: "singlerow",
            bindresize: true,
            pagesize: 100,
        };
        listProductGrid = new OlbGrid(grid, null, configGrid, []);
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
    var getProductGrid= function () {
        return listProductGrid;
    }
    return {
        init: init,
        //deleteRow: deleteRow,
        //addWithGrid: addWithGrid,
        getProductGrid:getProductGrid
    }
}());