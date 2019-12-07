<script type="text/javascript" id="reportInventory">
	$(function () {
		var dataFilterTop = ["5", "10", "15", "20"];
		var dataFilterSortQty = [
			{'text': '${StringUtil.wrapString(uiLabelMap.ReceiveHighest)}', 'value': 'DESC'},
			{'text': '${StringUtil.wrapString(uiLabelMap.ReceiveLowest)}', 'value': 'ASC'}
		];
		var config = {
			service: "facilityInventory",
			title: {
				text: "${StringUtil.wrapString(uiLabelMap.LogChartInventory)}",
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
			tooltip: {
				formatter: function () {
					return "<b>" + this.x + "</b>" + "<br/>" + "<tspan style=\"fill:" + this.color + "\" x=\"8\" dy=\"15\">●</tspan><i> " + LocalData.object.mapProducsWithCode[this.series.name] + "</i>: <b>" + this.y.toLocaleString(locale) + "</b>";
				}
			},
			legend: {
				layout: "vertical",
				align: "right",
				verticalAlign: "middle",
				borderWidth: 0
			}
		};

		var configPopup =
		[
			{
				action : "addJqxGridMultil",
				params : [{
					id : "facilityId",
					title1: "${StringUtil.wrapString(uiLabelMap.FacilityId)}",
					title2: "${StringUtil.wrapString(uiLabelMap.LogFacilityName)}",
					label : "${StringUtil.wrapString(uiLabelMap.LogWarehouse)}",
					data : LocalData.array.facilities,
					value: []
				}]
			},
			{
				action : "addDropDownList",
				params : [{
					id : "dateType",
					label : "${StringUtil.wrapString(uiLabelMap.olap_dateType)}",
					data : date_type_source,
					index: 0
				}]
			},
			{
				action : "addDateTimeInput",
				params : [{
					id : "from_date",
					label : "${StringUtil.wrapString(uiLabelMap.olap_fromDate)}",
					value: OLBIUS.dateToString(LocalData.date.firstDayOfMonth)
				}],
				before: "thru_date"
			},
			{
				action : "addDateTimeInput",
				params : [{
					id : "thru_date",
					label : "${StringUtil.wrapString(uiLabelMap.olap_thruDate)}",
					value: OLBIUS.dateToString(LocalData.date.currentDate)
				}],
				after: "from_date"
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
		];

		var reportInventoryOLap = OLBIUS.oLapChart("reportInventory", config, configPopup, "logReportInventoryChartLine", true, true, OLBIUS.defaultLineFunc);

		reportInventoryOLap.funcUpdate(function(oLap) {
			oLap.update({
				dateType: oLap.val("dateType"),
				filterTop: oLap.val("filterTop"),
            	filterSort: oLap.val("filterSort"),
				fromDate: oLap.val("from_date"),
				thruDate: oLap.val("thru_date"),
				facilityId: oLap.val("facilityId")
			}, oLap.val("dateType"));
		});

		reportInventoryOLap.init(function () {
			reportInventoryOLap.runAjax();
		});
	});
</script>