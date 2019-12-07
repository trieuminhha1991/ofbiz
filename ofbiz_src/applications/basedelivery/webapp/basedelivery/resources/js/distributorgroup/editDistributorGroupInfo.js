$(function () {
    OlbQuotationInfo.init();
});
var OlbQuotationInfo = (function () {

    var init = function () {
        initElement();
        initEvent();
    };
    var initElement = function () {
        $('#description').jqxTextArea({height: 40, width: '100%', minLength: 1});
    };
    var processDataRowSelect = function (rowBoundIndex) {
        var data = $("#jqxgridOrder").jqxGrid("getrowdata", rowBoundIndex);
        if (data) {
            var idStr = data.partyCode;
            if (typeof(productPricesMap[idStr]) != "undefined") {
                var itemValue = productPricesMap[idStr];
                itemValue.selected = true;
                productPricesMap[idStr] = itemValue;
            } else {
                var itemValue = {};
                itemValue.partyCode = data.partyCode;
                itemValue.groupName = data.groupName;
                itemValue.contactNumber = data.contactNumber;
                itemValue.address1 = data.address1;
                itemValue.emailAddress = data.emailAddress;
                itemValue.statusId = data.statusId;
                itemValue.selected = true;
                productPricesMap[idStr] = itemValue;
            }
        }
    };
    var selectableCheckBox = true;
    var initEvent = function () {
        $("#jqxgridOrder").on("bindingcomplete", function (event) {
            var dataRow = $("#jqxgridOrder").jqxGrid("getboundrows");
            if (typeof(dataRow) != 'undefined') {
                var icount = 0;
                $.each(dataRow, function (key, value) {
                    if (value) {
                        var isSelected = false;
                        var idStr = value.partyCode;
                        if (typeof(productPricesMap[idStr]) != "undefined") {
                            var itemValue = productPricesMap[idStr];
                            if (itemValue.selected) {
                                $('#jqxgridOrder').jqxGrid('selectrow', icount);
                                isSelected = true;
                            }
                        }
                        if (OlbElementUtil.isNotEmpty(value.partyCode) && !isSelected) {
                            $('#jqxgridOrder').jqxGrid('unselectrow', icount);
                        }
                    }
                    icount++;
                });
                selectableCheckBox = true;
            }
        });
        $('#jqxgridOrder').on('rowselect', function (event) {
            if (selectableCheckBox) {
                var args = event.args;
                var rowBoundIndex = args.rowindex;
                if (Object.prototype.toString.call(rowBoundIndex) === '[object Array]') {
                    for (var i = 0; i < rowBoundIndex.length; i++) {
                        processDataRowSelect(rowBoundIndex[i]);
                    }
                } else {
                    processDataRowSelect(rowBoundIndex);
                }
            }
        });
        $('#jqxgridOrder').on('rowunselect', function (event) {
            var rowindexes = $("#jqxgridOrder").jqxGrid("getselectedrowindexes");
            if (selectableCheckBox) {
                var args = event.args;
                var rowBoundIndex = args.rowindex;
                var data = $("#jqxgridOrder").jqxGrid("getrowdata", rowBoundIndex);
                if (typeof(data) != 'undefined') {
                    var idStr = data.partyCode;
                    if (typeof(productPricesMap[idStr]) != "undefined") {
                        var itemValue = productPricesMap[idStr];
                        itemValue.selected = false;
                        productPricesMap[idStr] = itemValue;
                    }
                }
            }
        });
    };
    return {
        init: init
    };
}());