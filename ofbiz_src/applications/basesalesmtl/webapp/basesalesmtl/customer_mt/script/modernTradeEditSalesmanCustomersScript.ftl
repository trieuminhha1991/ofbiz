<#assign listStatusItem = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", "PARTY_STATUS"), null, null, null, false) />
<script>
    var listStatusItem = [<#if listStatusItem?exists><#list listStatusItem as item>{
		statusId: '${item.statusId?if_exists}',
		description: "${StringUtil.wrapString(item.get("description", locale))}"
	   },</#list></#if>];
    var mapStatusItem = {<#if listStatusItem?exists><#list listStatusItem as item>
			"${item.statusId?if_exists}": "${StringUtil.wrapString(item.get("description", locale)?if_exists)}",
    </#list></#if>};
</script>
<script type="text/javascript">
    $(function () {
        OlbMTEditSalesmanCustomersObj.init();
    });
    var OlbMTEditSalesmanCustomersObj = (function () {
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
            $("#jqxgridCustomers").on("bindingcomplete", function (event) {
                $("#jqxgridCustomers").find('.jqx-grid-column-header:first').children().hide();  //hidden checkall
            });
            $("#jqxgridCustomers").on('rowselect', function (event) {
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
            $("#jqxgridCustomers").on('rowunselect', function (event) {
                var args = event.args;
                var rowBoundIndex = args.rowindex;
                var data = $("#jqxgridCustomers").jqxGrid("getrowdata", rowBoundIndex);
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
                var data = $("#jqxgridCustomers").jqxGrid("getrowdata", rowBoundIndex);
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

        var clearCustomersSelected = function () {
            customerIds = [];
            customerRows = [];
        };

        var getCustomers = function () {
            return customerRows;
        };
        return {
            init: init,
            getCustomers: getCustomers,
            clearCustomersSelected: clearCustomersSelected,
        }
    }());
</script>