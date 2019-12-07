<script type="text/javascript">
	var nowTimestamp = new Date();
	var nowYear = nowTimestamp.getFullYear();
	var nowMonth = nowTimestamp.getMonth();
	var selectedIndexYear = 10;
	var selectedIndexMonth = nowMonth;
	var dataCctpFilterYearArr = [];
	for (var i = (nowYear + 10); i > (nowYear - 10); i--) {
		dataCctpFilterYearArr.push({'text': '' + i, 'value': i});
	}
	var dataCctpFilterMonthArr = [
		{'text': '${StringUtil.wrapString(uiLabelMap.BSMonth)} 1', 'value': '1'},
		{'text': '${StringUtil.wrapString(uiLabelMap.BSMonth)} 2', 'value': '2'},
		{'text': '${StringUtil.wrapString(uiLabelMap.BSMonth)} 3', 'value': '3'},
		{'text': '${StringUtil.wrapString(uiLabelMap.BSMonth)} 4', 'value': '4'},
		{'text': '${StringUtil.wrapString(uiLabelMap.BSMonth)} 5', 'value': '5'},
		{'text': '${StringUtil.wrapString(uiLabelMap.BSMonth)} 6', 'value': '6'},
		{'text': '${StringUtil.wrapString(uiLabelMap.BSMonth)} 7', 'value': '7'},
		{'text': '${StringUtil.wrapString(uiLabelMap.BSMonth)} 8', 'value': '8'},
		{'text': '${StringUtil.wrapString(uiLabelMap.BSMonth)} 9', 'value': '9'},
		{'text': '${StringUtil.wrapString(uiLabelMap.BSMonth)} 10', 'value': '10'},
		{'text': '${StringUtil.wrapString(uiLabelMap.BSMonth)} 11', 'value': '11'},
		{'text': '${StringUtil.wrapString(uiLabelMap.BSMonth)} 12', 'value': '12'}
	];
</script>
<div id="olbiusSalesForecast"></div>
<script type="text/javascript">
    $(function() {
        var config = {
            title: "${StringUtil.wrapString(uiLabelMap.BSReportSalesForecast)}<#if viewPartner?exists && viewPartner == "Y"> (NPP)<#elseif viewPartner?exists && viewPartner == "A"> (DTM)</#if>",
            service: "salesForecastTotal",
            button: <#if viewPartner?exists && viewPartner == "Y">false<#elseif viewPartner?exists && viewPartner == "A">false<#else>true</#if>,
            id: "olbiusSalesForecast",
            olap: "olapSalesForecast",
            sortable: true,
            filterable: true,
            showfilterrow: true,
            columns: [
                {text: "${StringUtil.wrapString(uiLabelMap.BSSalesForecastId)}",
                    datafield: {name: "sales_forecast_id", type: "string"},
                    width: 120
                },
                {text: "${StringUtil.wrapString(uiLabelMap.BSCustomTimePeriodId)}",
                    datafield: {name: "custom_time_period_id", type: "string"},
                    width: 140
                },
                {text: "${StringUtil.wrapString(uiLabelMap.BSNumDay)}",
                    datafield: {name: "num_day", type: "string"},
                    width: 80
                },
                {text: "${StringUtil.wrapString(uiLabelMap.BSProductCode)}",
                    datafield: {name: "product_code", type: "string"},
                    width: 120
                },
                {text: "${StringUtil.wrapString(uiLabelMap.BSProductName)}",
                    datafield: {name: "product_name", type: "string"},
                    minwidth: 160
                },
                {text: "${StringUtil.wrapString(uiLabelMap.BSQuantity)} ${StringUtil.wrapString(uiLabelMap.BSTarget)}",
                    datafield: {name: "quantity", type: "number"},
                    width: 120, filtertype: 'number', 
                    cellsrenderer: function(row, column, value) {
						return '<div class=\"innerGridCellContent align-right\">' + formatnumber(value) + '</div>';
				 	}
                },
                {text: "${StringUtil.wrapString(uiLabelMap.BSQuantity)} ${StringUtil.wrapString(uiLabelMap.BSActual)}",
                    datafield: {name: "report_quantity", type: "number"},
                    width: 120, filtertype: 'number', 
                    cellsrenderer: function(row, column, value) {
						return '<div class=\"innerGridCellContent align-right\">' + formatnumber(value) + '</div>';
				 	}
                },
                {text: "${StringUtil.wrapString(uiLabelMap.BSPercentComplete)}",
                    datafield: {name: "amount", type: "number"},
                    width: 120, filtertype: 'number', 
                    cellsrenderer: function(row, column, value, a, b, dataRow) {
						if (typeof(dataRow) != 'undefined') {
							var percentComplete = 0;
							if (typeof(dataRow.report_quantity) != 'undefined' && dataRow.quantity != 0) percentComplete = (dataRow.report_quantity / dataRow.quantity) * 100;
							return '<div class=\"innerGridCellContent align-right\">' + formatcurrency(percentComplete, null, true, 2) + ' %</div>';
						} else {
							return '<div class=\"innerGridCellContent align-right\">%</div>';
						}
				 	}
                },
                /*
                {text: "${StringUtil.wrapString(uiLabelMap.BSAmount)} ${StringUtil.wrapString(uiLabelMap.BSTarget)}",
                    datafield: {name: "amount", type: "number"},
                    width: 120, filtertype: 'number', 
                    cellsrenderer: function(row, column, value) {
						return '<div class=\"innerGridCellContent align-right\">' + formatnumber(value) + '</div>';
				 	}
                },
                {text: "${StringUtil.wrapString(uiLabelMap.BSAmount)} ${StringUtil.wrapString(uiLabelMap.BSActual)}",
                    datafield: {name: "report_amount", type: "number"},
                    width: 120, filtertype: 'number', 
                    cellsrenderer: function(row, column, value) {
						return '<div class=\"innerGridCellContent align-right\">' + formatnumber(value) + '</div>';
				 	}
                },
                */
            ],
            popup: [
                {
                    action: 'jqxDropDownList',
                    params: {
                        id: 'filterFromYear',
                        label: "${StringUtil.wrapString(uiLabelMap.BSFrom)} ${StringUtil.wrapString(uiLabelMap.BSYear)}",
                        source: dataCctpFilterYearArr,
                        selectedIndex: selectedIndexYear
                    },
                },
                {
                    action: 'jqxDropDownList',
                    params: {
                        id: 'filterToYear',
                        label: "${StringUtil.wrapString(uiLabelMap.BSTo)} ${StringUtil.wrapString(uiLabelMap.BSYear)}",
                        source: dataCctpFilterYearArr,
                        selectedIndex: selectedIndexYear
                    },
                },
                {
                    action: 'jqxDropDownList',
                    params: {
                        id: 'filterFromMonth',
                        label: "${StringUtil.wrapString(uiLabelMap.BSFrom)} ${StringUtil.wrapString(uiLabelMap.BSMonth)}",
                        source: dataCctpFilterMonthArr,
                        selectedIndex: selectedIndexMonth
                    },
                },
                {
                    action: 'jqxDropDownList',
                    params: {
                        id: 'filterToMonth',
                        label: "${StringUtil.wrapString(uiLabelMap.BSTo)} ${StringUtil.wrapString(uiLabelMap.BSMonth)}",
                        source: dataCctpFilterMonthArr,
                        selectedIndex: selectedIndexMonth
                    },
                },
            ],
            apply: function (grid, popup) {
            	var filterFromYear = popup.val("filterFromYear");
            	var filterFromMonth = popup.val("filterFromMonth");
            	var filterToYear = popup.val("filterToYear");
            	var filterToMonth = popup.val("filterToMonth");
            	var fromDate = new Date(filterFromYear, filterFromMonth - 1, 1);
            	var thruDate = new Date(filterToYear, filterToMonth, 0);
            	var dateTimeData = {
            		'fromDate': fromDate.formatDate("yyyy-MM-dd"),
            		'thruDate': thruDate.formatDate("yyyy-MM-dd")
            	}; //popup.group("dateTime").val();
            	var popupData = $.extend(dateTimeData, {}); //orderStatusId: popup.val("orderStatusId")
                return $.extend({
                	<#if viewPartner?exists>'viewPartner': '${viewPartner}',</#if>
                }, popupData);
            },
            excel: function(obj){
            	var isExistData = false;
				var dataRow = grid._grid.jqxGrid("getrows");
				if (typeof(dataRow) != 'undefined' && dataRow.length > 0) {
					isExistData = true;
				}
				if (!isExistData) {
					OlbCore.alert.error("${uiLabelMap.BSNoDataToExport}");
					return false;
				}
				
				var otherParam = "";
				if (obj._data) {
					$.each(obj._data, function(key, value){
						otherParam += "&" + key + "=" + value;
					});
				}
				var filterObject = grid.getFilter();
				if (filterObject && filterObject.filter) {
					var filterData = filterObject.filter;
					for (var i = 0; i < filterData.length; i++) {
						otherParam += "&filter=" + filterData[i];
					}
				}
				window.open("exportReportSalesForecastExcel?" + otherParam, "_blank");
            },
            exportFileName: 'BC_SALES_FORECAST'
        };
        
        var grid = OlbiusUtil.grid(config);
        
    });
</script>
