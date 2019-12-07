$(function () {
    OlbCustomerFilterObj.init();
});
var OlbCustomerFilterObj = (function () {
    if (!customerIds) var customerIds = [];
    if (!customerRows) var customerRows = [];
    var init = function () {
        initInputs();
        initElementComplex();
        initEvents();
    };
    var initInputs = function () {
    };
    var initElementComplex = function () {
    };
    var initEvents = function () {
        $("#jqxgridfilterGrid").on("bindingcomplete", function (event) {
            $("#jqxgridfilterGrid").find('.jqx-grid-column-header:first').children().hide();  //hidden checkall
        });
        $("#jqxgridfilterGrid").on('rowselect', function (event) {
            var args = event.args;
            var rowBoundIndex = args.rowindex;
            if (Object.prototype.toString.call(rowBoundIndex) === '[object Array]') {
                for (var i = 0; i < rowBoundIndex.length; i++) {
                    processDataRowSelect(rowBoundIndex[i]);
                }
            } else {
                processDataRowSelect(rowBoundIndex);
            }
        });
        $("#jqxgridfilterGrid").on('rowunselect', function (event) {
            var args = event.args;
            var rowBoundIndex = args.rowindex;
            var data = $("#jqxgridfilterGrid").jqxGrid("getrowdata", rowBoundIndex);
            if (typeof(data) != 'undefined') {
                var idStr = data.partyId;
                if (idStr) {
                    var index = customerIds.indexOf(idStr);
                    if (index > -1) {
                        customerIds.splice(index, 1);
                    }
                }

                var flagInsert = -1;
                $.each(customerRows, function (i, v) {
                    if (v.partyId === idStr) {
                        flagInsert = i;
                        return;
                    }
                });
                if (flagInsert < 0) {
                    customerRows.push(data);
                } else {
                    customerRows.splice(flagInsert, 1);
                }
            }
        });
        var processDataRowSelect = function (rowBoundIndex) {
            var data = $("#jqxgridfilterGrid").jqxGrid("getrowdata", rowBoundIndex);
            if (data) {
                var idStr = data.partyId;
                if (idStr) {
                    if (customerIds.indexOf(idStr) < 0) {
                        customerIds.push(idStr);
                    }
                }
                var flagInsert = -1;
                $.each(customerRows, function (i, v) {
                    if (v.partyId === idStr) {
                        flagInsert = i;
                        return;
                    }
                });
                if (flagInsert < 0) {
                    customerRows.push(data);
                } else {
                    customerRows.splice(flagInsert, 1);
                }
            }
        };
    };

    var getCustomers = function () {
        return customerRows;
    };
    return {
        init: init,
        getCustomers: getCustomers,
    }
}());