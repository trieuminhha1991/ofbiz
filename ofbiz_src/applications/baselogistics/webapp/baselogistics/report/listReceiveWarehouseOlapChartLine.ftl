<script type="text/javascript" id="reportReceiveWarehouse">
	var dataFilterTop = ["5", "10", "15", "20"];
	var dataFilterSortQty = [
		{'text': '${StringUtil.wrapString(uiLabelMap.ReceiveHighest)}', 'value': 'RECEIVE_DESC'},
		{'text': '${StringUtil.wrapString(uiLabelMap.ReceiveLowest)}', 'value': 'RECEIVE_ASC'}
	];
	$(function () {
		var config =
		{
			service: "facilityInventory",
			service: "salesOrderNew",
            id: "reportReceiveWarehouse",
            olap: "logReportReceiveWarehouseChartLine",
			title: {
				text: "${StringUtil.wrapString(uiLabelMap.LogChartReceiveWarehousing)}",
				x: -20 //center
			},
			xAxis: {
				labels: {
					enabled: false
				},
				title : {
					text: null
				}
			},
			yAxis: {
				plotLines: [{
					value: 0,
					width: 1,
					color: "#808080"
				}],
				title : {
					text: null
				},
				min: 0
			},
			legend: {
				layout: "vertical",
				align: "right",
				verticalAlign: "middle",
				borderWidth: 0
			},
			tooltip: {
				formatter: function () {
					return "<b>" + this.x + "</b>" + "<br/>" + "<tspan style=\"fill:" + this.color + "\" x=\"8\" dy=\"15\">●</tspan>: <b>" + formatnumber(this.y) + "</b>";
				}
			},
			chartRender: OlbiusUtil.getChartRender('defaultLineFunc'),
		 	popup: [
            {
                group: "dateTime",
                id: "dateTime",
                params: {
                	index: 5,
                	dateTypeIndex: 2,
                	fromDate: past_date,
                	thruDate: cur_date
                }
            },
            {
                action: 'jqxDropDownList',
                params: {
                    id: 'filterTop',
                    label: "${StringUtil.wrapString(uiLabelMap.BSTop)}",
                    source: dataFilterTop,
                    selectedIndex: 0
                }
            },
            {
                action: 'jqxDropDownList',
                params: {
                    id: 'filterSort',
                    label: "${StringUtil.wrapString(uiLabelMap.olap_filter)}",
                    source: dataFilterSortQty,
                    selectedIndex: 0
                }
            },
            {
                action: 'jqxDropDownList',
                params: {
                    id: 'filter',
                    label: "${StringUtil.wrapString(uiLabelMap.olap_filter)}",
                    source: LocalData.array.facilities,
                    selectedIndex: 0
                }
            },
	        ],
	        
	        apply: function (grid, popup) {
	        	var dateTimeData = popup.group("dateTime").val();
	            return $.extend({
	            	'olapType': 'LINECHART',
	            	'filterTop': popup.element("filterTop").val(),
	            	'filterSort': popup.element("filterSort").val(),
	            	'filterTypeId': popup.element("filter").val(),
	            }, dateTimeData);
	        },

		};
		OlbiusUtil.chart(config);
	});
</script>