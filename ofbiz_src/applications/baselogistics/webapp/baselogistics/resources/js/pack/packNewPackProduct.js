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
            { text: uiLabelMap.ProductId, dataField: 'productCode', width: 150, editable: false, pinned: true,},
            { text: uiLabelMap.DeliveryId, dataField: 'deliveryId', width: 150, editable: false, pinned: true,},
            { text: uiLabelMap.DeliveryItemSeqId, dataField: 'deliveryItemSeqId', width: 150, editable: false, pinned: true,},
            { text: uiLabelMap.ProductName, dataField: 'productName', minwidth: 150, editable:false,},
            { text: uiLabelMap.Quantity, datafield: 'quantityOnHand', sortable: true, width: 120, editable: false, filterable: false, cellsalign: 'right', cellclassname: productGridCellclass,
                cellsrenderer: function(row, column, value) {
                    var data = grid.jqxGrid('getrowdata', row);
                    if (data.requireAmount && data.requireAmount == 'Y') {
                        value = data.amountOnHandTotal;
                    }
                    console.log(data);
                    if(data.packedQuantity!=null)
                        value=value-data.packedQuantity;
                    description = formatnumber(value) + ' (' + getUomDesc(data.quantityUomId)+')';
                    return '<span class="align-right">' + description +'</span>';
                },
            },
            {editable: false, text: uiLabelMap.Unit, sortable: false, dataField: 'uomId', width: 120, columntype: 'dropdownlist', filterable:false, cellclassname: productGridCellclass,
                cellsrenderer: function(row, column, value) {
                    var rowData = grid.jqxGrid('getrowdata', row);
                    value = rowData.quantityUomId;

                    if (value) {
                        var desc = getUomDesc(value);
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
        product = new OlbGrid(grid, null, configGrid, []);
    };

    var initEvents = function() {
        /*$("#jqxGridProduct").on("cellendedit", function(event) {
            var args = event.args;
            var dataField = event.args.datafield;
            var rowBoundIndex = event.args.rowindex;
            var value = args.value;
            var oldvalue = args.oldvalue;
            var rowData = args.row;
            if (rowData){
                if (dataField == 'quantityDD'){
                    $.each(listProductSelected, function(i){
                        var olb = listProductSelected[i];
                        if (olb.productId == rowData.productId && olb.deliveryId== rowData.deliveryId &&olb.deliveryItemSeqId==rowData.deliveryItemSeqId ){
                            listProductSelected.splice(i,1);
                            return false;
                        }
                    });
                    if (value != 0){
                        var item = $.extend({}, rowData);
                        console.log(rowData);
                        console.log(item);
                        item.quantity = value;
                        listProductSelected.push(item);
                    }
                }
            }
        });*/

        // $("#jqxGridProduct").on("bindingcomplete", function(event) {
        //     var args = event.args;
        //     var rows = $("#jqxGridProduct").jqxGrid('getrows');
        //     if (listProductSelected.length > 0){
        //         for (var x in rows){
        //             var check = false;
        //             for (var y in listProductSelected){
        //                 if (rows[x].productId === listProductSelected[y].productId) {
        //                     check = true;
        //                     break;
        //                 }
        //             }
        //             var index = $('#jqxGridProduct').jqxGrid('getrowboundindexbyid',rows[x].uid);
        //             if (check){
        //                 $("#jqxGridProduct").jqxGrid('selectrow', index);
        //             } else {
        //                 $("#jqxGridProduct").jqxGrid('unselectrow', index);
        //             }
        //         }
        //     }
        //
        // });
    }

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
    function getProductGrid() {
        return product;

    }
    var checkSpecialCharacters = function(value) {
        if (OlbCore.isNotEmpty(value) && !(/^[a-zA-Z0-9 ÀÁÂÃÈÉÊÌÍÒÓÔÕÙÚĂĐĨŨƠàáâãèéêìíòóôõùúăđĩũơƯĂẠẢẤẦẨẪẬẮẰẲẴẶẸẺẼỀỀỂưăạảấầẩẫậắằẳẵặẹẻẽềềểỄỆỈỊỌỎỐỒỔỖỘỚỜỞỠỢỤỦỨỪễệỉịọỏốồổỗộớờởỡợụủứừỬỮỰỲỴÝỶỸửữựỳỵỷỹ]+$/.test(value))) {
            return true;
        }
        return false;
    }
    return {
        init : init,
        getProductGrid: getProductGrid,
        updateGridProductSource: updateGridProductSource,
    }
}());